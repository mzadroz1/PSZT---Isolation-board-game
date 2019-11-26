import java.util.ArrayList;
import java.util.Collections;


public class Strategy {
    Node root;
    Movement predictedTurn;
    int sizeNodes;

    public Strategy(Board board) {
        root = new Node(board);
        predictedTurn = null;
        sizeNodes = 1;
    }

    public void thinkDumb() { // to sprawdza tylko jeden poziom, do testowania
        ArrayList<Movement> checkIt = possibleMoves(root, true);
        System.out.println(checkIt.size());
        for (Movement move: checkIt) {
            root.possibleMoves.add(root.genSon(move));
            ++sizeNodes;
        }
        Node best = root.possibleMoves.get(0);
        for(Node n: root.possibleMoves) {
            if(n.eval(true)<=best.eval(true))
                best = n;
        }
        predictedTurn = best.lastMove;
    }

    public void showStats() {
        root.show();
        System.out.println("Liczba rozpatrzonych nodów: " + sizeNodes);
        System.out.println("AI teritory " + root.territoryDFS(false));
        System.out.println("Player teritory " + root.territoryDFS(true));
        System.out.println("Player moves " + root.nOfPMoves(true));
        System.out.println("AI moves " + root.nOfPMoves(false));
    }

    public void minMax(int depth, boolean ter) {
        ArrayList<Movement> checkIt = possibleMoves(root,ter);
        Node best = null;
        for (Movement move: checkIt) {
            Node n = root.genSon(move);
            ++sizeNodes;
            root.possibleMoves.add(n);
            double val = alphaBeta(n,depth,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ter);
            if(best == null || val < best.eval(ter))
                best = n;
        }
        predictedTurn = best.lastMove;
//        System.out.println("Liczba rozpatrzonych nodów: " + sizeNodes);
    }

    private double alphaBeta(Node node, int depth, double alpha, double beta, boolean ter) {
        if(depth == 0 || node.isGameOver()) {
            return node.eval(ter);
        }

        ArrayList<Movement> checkIt = possibleMoves(node, ter);
        if(node.playerTurn) {
            for(Movement move: checkIt) {
                Node n = node.genSon(move);
                ++sizeNodes;
                node.possibleMoves.add(n);
                double val = alphaBeta(n,depth-1,alpha,beta,ter);
                alpha = Math.max(val,alpha);
                if(beta <= alpha)
                    break;
            }
            return alpha;
        }
        else {
            for(Movement move: checkIt) {
                Node n = node.genSon(move);
                ++sizeNodes;
                double val = alphaBeta(n,depth-1,alpha,beta,ter);
                beta = Math.min(val,beta);
                if(beta <= alpha)
                    break;
            }
            return beta;
        }
    }

    //zwraca listę dostępnych ruchów (krok + niszczenie kratki) na danej planszy w zaleznosci czyja kolej (sam sprawdza)
    // ale nie generyje nowych nodów
    public ArrayList<Movement> possibleMoves(Node node, boolean ter) {
        ArrayList<Movement> possibles = new ArrayList<>();
        ArrayList<Step> steps = new ArrayList<>();

        int r = node.getActiveY(), c = node.getActiveX();
        if(r>=1 && c>=1 && node.board[r-1][c-1]==1)
            steps.add(Step.NW);
        if(r>=1 && node.board[r-1][c]==1)
            steps.add(Step.N);
        if(r>=1 && c<6 && node.board[r-1][c+1]==1)
            steps.add(Step.NE);
        if(c>=1 && node.board[r][c-1]==1)
            steps.add(Step.W);
        if(c<6 && node.board[r][c+1]==1)
            steps.add(Step.E);
        if(r<6 && c>=1 && node.board[r+1][c-1]==1)
            steps.add(Step.SW);
        if(r<6 && node.board[r+1][c]==1)
            steps.add(Step.S);
        if(r<6 && c<6 && node.board[r+1][c+1]==1)
            steps.add(Step.SE);
// kiedy terytorium gracza jest odpowiednio male to możemy ograniczyć się tylko do ruchów redukujących je
        if(node.territoryDFS(true)<24) {
            for(Step st: steps) {
                int[] dYX = Movement.translateStep(st); //dYX[0] == dY dYX[1] == dX
                int newY = r+dYX[0], newX = c+dYX[1];
                for(int i = 0;i<7;++i)
                    for(int j = 0;j<7;++j) {
                        if(node.visited[i][j])
                            if ((node.board[i][j]==1 && !(i==newY && j==newX))
                                    || (i==r && j==c)) {
                                possibles.add(new Movement(st,i,j));
                            }
                    }
            }
        }
        //dla kazdego kroku dostepnego znajduje dostepne zniszczenia kratek
        else {
            for(Step st: steps) {
                int[] dYX = Movement.translateStep(st); //dYX[0] == dY dYX[1] == dX
                int newY = r+dYX[0], newX = c+dYX[1];
                int targetY = node.getStaticY(), targetX = node.getStaticX();
                for(int i = targetY-2;i<=targetY+2;++i)
                    for(int j = targetX-2;j<=targetX+2;++j) {
                        if(i>=0&&i<7 && j>=0&&j<7)
                            if ((node.board[i][j]==1 && !(i==newY && j==newX))
                                    || (i==r && j==c)) {
                                possibles.add(new Movement(st,i,j));
                            }
                    }
            }
        }
        Collections.shuffle(possibles);
        return possibles;
    }

}

enum Step {
    N, NE, E, SE, S, SW, W, NW;
}

class Node {

    int[][] board;
    int pY, oY, pX, oX;
    boolean playerTurn;
    Movement lastMove;
    ArrayList<Node> possibleMoves;

    boolean[][] visited;

    public Node(Board b) { // ten konstruktor tylko przy kopiowaniu boardu gry do roota strategii
        possibleMoves = new ArrayList<>();

        board = new int[7][7];
        for(int i=0; i<7; ++i)
            for (int j=0; j<7; ++j)
            {
                board[i][j] = b.getTiles()[i][j].getType();
            }
        visited = new boolean[7][7];

        Player p = b.getPlayer();
        pX = p.getColumn();
        pY = p.getRow();
        p = b.getOpponent();
        oY = p.getRow();
        oX = p.getColumn();

        playerTurn = b.getPlayerTurn();
        lastMove = null;
    }

    public Node(int[][] tab, int pY, int oY, int pX, int oX, boolean pTurn, Movement lMove) {
        this.board = tab;
        this.pY = pY;
        this.pX = pX;
        this.oY = oY;
        this.oX = oX;
        this.playerTurn = pTurn;
        this.lastMove = lMove;
        this.possibleMoves = new ArrayList<>();
        visited = new boolean[7][7];
    }

    public int getActiveY() {return playerTurn ? pY : oY;}
    public int getActiveX() {return playerTurn ? pX : oX;}
    public int getStaticY() {return playerTurn ? oY : pY;}
    public int getStaticX() {return playerTurn ? oX : pX;}

    public Node genSon(Movement movement) {
        int[][] afterTurn = new int[7][7];
        for (int i=0;i<7;++i)
            for (int j = 0; j < 7; ++j)
                afterTurn[i][j] = board[i][j];
        int[] dYX = Movement.translateStep(movement.step); //dYX[0] == dY dYX[1] == dX
        if(playerTurn) {
            int newY = pY+dYX[0], newX = pX+dYX[1];
            afterTurn[pY][pX] = 1;
            if(afterTurn[newY][newX] >= 3 || afterTurn[movement.destroyedY][movement.destroyedX] != 1) {
                throw new IllegalArgumentException("AI error, forbidden move(step)");
            }
            afterTurn[newY][newX] = 4;
            afterTurn[movement.destroyedY][movement.destroyedX] = 3;
            return new Node(afterTurn,newY,oY,newX,oX,false,movement);
        }
        int newY = oY+dYX[0], newX = oX+dYX[1];
        afterTurn[oY][oX] = 1;
        if(afterTurn[newY][newX] >= 3 || afterTurn[movement.destroyedY][movement.destroyedX] != 1) {
            throw new IllegalArgumentException("AI error, forbidden move(step)");
        }
        afterTurn[newY][newX] = 4;
        afterTurn[movement.destroyedY][movement.destroyedX] = 3;
        return new Node(afterTurn,pY,newY,pX,newX,true,movement);
    }

    public double evalSimple() {
        int pMoves = nOfPMoves(true), oMoves = nOfPMoves(false);
        if(pMoves==0)
            return Double.NEGATIVE_INFINITY;
        if(oMoves==0)
            return Double.POSITIVE_INFINITY;
        double playerPosition = 4*pMoves - distanceToCenter(true);
        double oppPosition = 4*oMoves - distanceToCenter(false);

        return playerPosition/2 - oppPosition/2;
    }

    public double eval(boolean ter) {
        if(!ter)
            return evalSimple();
        int pMoves = nOfPMoves(true), oMoves = nOfPMoves(false);
        if(pMoves==0)
            return Double.NEGATIVE_INFINITY;
        if(oMoves==0)
            return Double.POSITIVE_INFINITY;
        double playerPosition = territoryDFS(true)*5 + pMoves*1.5 - distanceToCenter(true);
        double oppPosition = territoryDFS(false)*5 + oMoves*1.5 - distanceToCenter(false);

        return playerPosition/2 - oppPosition/2;
    }
//liczy liczbę dostępnych posunięć pionka
    int nOfPMoves(boolean forPlayer) {
        int pMoves =0, x, y;
        x = forPlayer ? pX : oX;
        if((y = forPlayer ? pY : oY) > 0) {
            if(board[y-1][x]==1) ++pMoves;
            if(x>0 && board[y-1][x-1]==1) ++pMoves;
            if(x<6 && board[y-1][x+1]==1) ++pMoves;
        }
        if(x>0 && board[y][x-1]==1) ++pMoves;
        if(x<6 && board[y][x+1]==1) ++pMoves;
        if(y < 6) {
            if(board[y+1][x]==1) ++pMoves;
            if(x>0 && board[y+1][x-1]==1) ++pMoves;
            if(x<6 && board[y+1][x+1]==1) ++pMoves;
        }
        return pMoves;
    }

    private double distanceToCenter(boolean forPlayer) {
        int x,y;
        x = forPlayer ? pX : oX;
        y = forPlayer ? pY : oY;
        return Math.sqrt(Math.pow(3 - x, 2) + Math.pow(3 - y, 2));
    }
// liczymy obszar mapy, który jest wciąż dostępny dla pionka
    public int territoryDFS(boolean forPlayer) {
        int size = 0, x, y;
        for(int i = 0 ; i < 7; i++) {
            for(int j = 0; j < 7; j++) {
                visited[i][j] = false;
            }
        }
        x = forPlayer ? pX : oX;
        y = forPlayer ? pY : oY;

        size = visitTile(y,x,size);
        return size;
    }

    private int visitTile(int y, int x, int size) {
        visited[y][x] = true;
        size++;
        for (int i = y - 1; i <= y + 1; ++i)
            for (int j = x - 1; j <= x + 1; ++j) {
                if ((i >= 0 && i < 7) && (j >= 0 && j < 7))
                    if (board[i][j] == 1 && !visited[i][j]) {
                        size = visitTile(i, j, size);
                    }
            }
        return size;
    }

    boolean isGameOver() {
        return this.nOfPMoves(true)==0||this.nOfPMoves(false)==0;
    }

    void show() { //debug function
        for (int i=0;i<7;++i) {
            for (int j = 0; j < 7; ++j)
                System.out.print(board[i][j]+" ");
            System.out.println("");
        }
        System.out.println("");
    }

}

// klasa reprezentująca ruch gracza w turze tj. posunięcie pionka i usunięcie kafelka
class Movement {
    public Step step;
    public int destroyedX;
    public int destroyedY;


    public Movement(Step s, int y, int x) {
        step = s;
        destroyedX = x;
        destroyedY = y;
    }

    static int[] translateStep(Step step) {
        int[] ret = new int[2];
        int dY=8, dX=8;
        switch (step) {
            case N: {dY = -1; dX = 0; break;}
            case NE: {dY = -1; dX = 1; break;}
            case E: {dY = 0; dX = 1; break;}
            case SE: {dY = 1; dX = 1; break;}
            case S: {dY = 1; dX = 0; break;}
            case SW: {dY = 1; dX = -1; break;}
            case W: {dY = 0; dX = -1; break;}
            case NW: {dY = -1; dX = -1; break;}
        }
        ret[0] = dY;
        ret[1] = dX;
        return ret;
    }
}