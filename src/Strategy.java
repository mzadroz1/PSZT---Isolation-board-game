import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public class Strategy {
    Node root;
    Movement predictedTurn;
    int sizeNodes;

    public Strategy(Board board) {
        root = new Node(board);
        predictedTurn = null;
        sizeNodes = 1;
    }

    public void thinkDumb() { // to sprawdza tylko jeden poziom
        ArrayList<Movement> checkIt = possibleMoves(root);
        System.out.println(checkIt.size());
        for (Movement move: checkIt) {
            root.possibleMoves.add(root.genSon(move));
            ++sizeNodes;
//            root.show();
        } //System.out.println("line: 20");
        Node best = root.possibleMoves.get(0);
        //System.out.println(root.possibleMoves.size());
        for(Node n: root.possibleMoves) {
            if(n.eval(true)<=best.eval(true))
                best = n;
        }//System.out.println("line: 26");
        predictedTurn = best.lastMove;
        //System.out.println("Best move is: "+ best.lastMove.step.toString() +" Y: " + best.lastMove.destroyedY
         //       +" X: " + best.lastMove.destroyedX);
        System.out.println("Liczba rozpatrzonych nodów: " + sizeNodes);
    }

    public void minMax(int depth, boolean ter) {
        ArrayList<Movement> checkIt = possibleMoves(root);

        Node best = null; //root.possibleMoves.get(0);
//        for(Node n: root.possibleMoves) {
        for (Movement move: checkIt) {
            Node n = root.genSon(move);
            ++sizeNodes;
            root.possibleMoves.add(n);
            double val = alphaBeta(n,depth,Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, ter);
            if(best == null || val < best.eval(ter))
                best = n;
        }
        //System.out.println(best.territoryDFS(false));
        predictedTurn = best.lastMove;
        System.out.println("Liczba rozpatrzonych nodów: " + sizeNodes);
    }

    private double alphaBeta(Node node, int depth, double alpha, double beta, boolean ter) {
        if(depth == 0 || node.isGameOver()) {
            return node.eval(ter);
        }

        ArrayList<Movement> checkIt = possibleMoves(node);
//        for (Movement move: checkIt) {
//            node.possibleMoves.add(new Node(node.gameState,move));
//        }
        if(node.playerTurn) {
//            for(Node n : node.possibleMoves) {
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
//            for(Node n : node.possibleMoves) {
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
    // byc moze potem trzeba bedzie to polaczyc w minmaxie z obcinaniem ze sprawdzaniem stanu, zeby mniej mozliwych
    // ruchow generowac
    public ArrayList<Movement> possibleMoves(Node node) {
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

//        System.out.println(steps.size());

        //dla kazdego kroku dostepnego znajduje dostepne zniszczenia kratek
        for(Step st: steps) {
            int[] dYX = Movement.translateStep(st); //dYX[0] == dY dYX[1] == dX
            int newY = r+dYX[0], newX = c+dYX[1];
            int targetY = node.getStaticY(), targetX = node.getStaticX();
//            for (int i = targetY<2 ? 0 : targetY-2; i < 7 && i<=targetY+2; ++i)
//                for (int j = targetX<2 ? 0 : targetX-2; j < 7 && i<=targetX+2; ++j) {
            for(int i = targetY-2;i<=targetY+2;++i)
                for(int j = targetX-2;j<=targetX+2;++j) {
                    if(i>=0&&i<7 && j>=0&&j<7)
                        if ((node.board[i][j]==1 && !(i==newY && j==newX))
                                || (i==r && j==c)) {
                            possibles.add(new Movement(st,i,j));
                        }
                }
        }
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

//        points = Double.NaN;
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
//        int[][] afterTurn = this.board.clone();
//        System.out.println("line: 124");
        int[][] afterTurn = new int[7][7];
        for (int i=0;i<7;++i)
            for (int j = 0; j < 7; ++j)
                afterTurn[i][j] = board[i][j];
//        System.out.println("line: 129");
//        System.out.println(afterTurn + " " + board + afterTurn.equals(board));
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
//        System.out.println("line: 142");
        int newY = oY+dYX[0], newX = oX+dYX[1];
        afterTurn[oY][oX] = 1;
        if(afterTurn[newY][newX] >= 3 || afterTurn[movement.destroyedY][movement.destroyedX] != 1) {
//            for (int i=0;i<7;++i) {
//                for (int j = 0; j < 7; ++j)
//                    System.out.print(afterTurn[i][j]+" ");
//                System.out.println(" ");
//            }
//            System.out.println(afterTurn[newY][newX]);
//            System.out.println("Wrong move: "+ movement.step.toString() +" Y: " + movement.destroyedY
//                    +" X: " + movement.destroyedX);
            throw new IllegalArgumentException("AI error, forbidden move(step)");
        }
        afterTurn[newY][newX] = 4;
        afterTurn[movement.destroyedY][movement.destroyedX] = 3; //System.out.println("line: 157");
//        System.out.println("Good move: "+ movement.step.toString() +" Y: " + movement.destroyedY
//                +" X: " + movement.destroyedX);
        return new Node(afterTurn,pY,newY,pX,newX,true,movement);
    }

    public double eval2() {
        double pMoves = this.nOfPMoves(true), oMoves = this.nOfPMoves(false);
        if(this.playerTurn && pMoves==0) return Double.NEGATIVE_INFINITY;
        else if(oMoves==0) return Double.POSITIVE_INFINITY;

        return pMoves - oMoves; //+ teritory(true)*1.5 -teritory(false)*1.5;
    }

    public double eval3() {
        int pMoves = nOfPMoves(true), oMoves = nOfPMoves(false);
        if(pMoves==0)
            return Double.NEGATIVE_INFINITY;
        if(oMoves==0)
            return Double.POSITIVE_INFINITY;
        double playerPosition = pMoves - distanceToCenter(true);
        double oppPosition = oMoves - distanceToCenter(false);

        return playerPosition/1.95 - oppPosition/2;
    }

    public double eval(boolean ter) {
        if(!ter)
            return eval3();
        int pMoves = nOfPMoves(true), oMoves = nOfPMoves(false);
        if(pMoves==0)
            return Double.NEGATIVE_INFINITY;
        if(oMoves==0)
            return Double.POSITIVE_INFINITY;
        double playerPosition = territoryDFS(true)*1.2 + pMoves - distanceToCenter(true)*0.5;
        double oppPosition = territoryDFS(false)*1.2 + oMoves - distanceToCenter(false)*0.5;

        return playerPosition/2 - oppPosition/2;
    }

    private int nOfPMoves(boolean forPlayer) {
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

    private int territory(boolean forPlayer) {
        int x, y;
        class Position {
            int row;
            int col;
            Position(int r, int c) {row = r; col = c;}
            Position change(int r, int c) {this.row=r; this.col=c; return this;}
        }
        ArrayDeque<Position> tmp = new ArrayDeque<>();
        HashSet<Position> field = new HashSet<>();
        x = forPlayer ? pX : oX;
        y = forPlayer ? pY : oY;
        Position curr, t=new Position(-1,-1);
        tmp.add(new Position(y,x));
        while (tmp.size()>0) {
            curr = tmp.poll();
            if(board[curr.row][curr.col]!=3) {
                field.add(curr);
                if(curr.row>0) {
                    if(!field.contains(t.change(curr.row-1,curr.col)))
                        tmp.add(new Position(curr.row-1,curr.col));
                    if(curr.col>0 && !field.contains(t.change(curr.row-1,curr.col-1)))
                        tmp.add(new Position(curr.row-1,curr.col-1));
                    if(curr.col<6 && !field.contains(t.change(curr.row-1,curr.col+1)))
                        tmp.add(new Position(curr.row-1,curr.col+1));
                }
                if(curr.col>0 && !field.contains(t.change(curr.row,curr.col-1)))
                    tmp.add(new Position(curr.row,curr.col-1));
                if(curr.col<6 && !field.contains(t.change(curr.row,curr.col+1)))
                    tmp.add(new Position(curr.row,curr.col+1));
                if(curr.row<6) {
                    if(!field.contains(t.change(curr.row+1,curr.col)))
                        tmp.add(new Position(curr.row+1,curr.col));
                    if(curr.col>0 && !field.contains(t.change(curr.row+1,curr.col-1)))
                        tmp.add(new Position(curr.row+1,curr.col-1));
                    if(curr.col<6 && !field.contains(t.change(curr.row+1,curr.col+1)))
                        tmp.add(new Position(curr.row+1,curr.col+1));
                }
            }
        }
        return field.size();
    }

    public int territoryDFS(boolean forPlayer) {
        int size = 0, x, y;
        //boolean[][] visited = new boolean[7][7];
        for(int i = 0 ; i < 7; i++) {
            for(int j = 0; j < 7; j++) {
                visited[i][j] = false;
            }
        }
        x = forPlayer ? pX : oX;
        y = forPlayer ? pY : oY;

        size = visitTile(x,y,size);
        return size;
    }

    private int visitTile(int x, int y, int size) {
        visited[x][y] = true;
        size++;
        for (int i = x - 1; i <= x + 1; ++i)
            for (int j = y - 1; j <= y + 1; ++j) {
                if (i >= 0 && i < 7 && j >= 0 && j < 7)
                    if (board[i][j] == 1 && !visited[i][j]) {
                        size = visitTile(i, j, size);
                    }
            }
        return size;
    }

    boolean isGameOver() {
        return this.nOfPMoves(true)==0||this.nOfPMoves(false)==0;
    }

    void show() {
        for (int i=0;i<7;++i) {
            for (int j = 0; j < 7; ++j)
                System.out.print(board[i][j]+" ");
            System.out.println("");
        }
        System.out.println("");
    }

}

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