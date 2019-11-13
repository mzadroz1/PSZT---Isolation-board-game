import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;

public class Strategy {
    Node root;
    Movement predictedTurn;

    public Strategy(Board board) {
        root = new Node(board);
        predictedTurn = null;
    }

    public void thinkDumb() { // to sprawdza tylko jeden poziom
        ArrayList<Movement> checkIt = possibleMoves(root);
        System.out.println(checkIt.size());
        for (Movement move: checkIt) {
            root.possibleMoves.add(new Node(root.gameState,move));
        }
        Node best = root.possibleMoves.get(0);
        for(Node n: root.possibleMoves) {
            if(n.points<best.points)
                best = n;
        }
        predictedTurn = best.lastMove;
        System.out.println("Best move is: "+ best.lastMove.step.toString() +" Y: " + best.lastMove.destroyedY
                +" X: " + best.lastMove.destroyedX);
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

        //dla kazdego kroku dostepnego znajduje dostepne zniszczenia kratek
        for(Step st: steps) {
            int[] dYX = Movement.translateStep(st); //dYX[0] == dY dYX[1] == dX
            int newY = r+dYX[0], newX = c+dYX[1];
            for (int i = 0; i < 7; ++i)
                for (int j = 0; j < 7; ++j) {
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

    public Node(Board b) { // ten konstruktor tylko przy kopiowaniu boardu gry do roota strategii
        possibleMoves = new ArrayList<>();

        board = new int[7][7];
        for(int i=0; i<7; ++i)
            for (int j=0; j<7; ++j)
            {
                board[i][j] = b.getTiles()[i][j].getType();
            }

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
    }

    public int getActiveY() {return playerTurn ? pY : oY;}
    public int getActiveX() {return playerTurn ? pX : oX;}

    public Node genSon(Movement movement) {
        int[][] afterTurn = this.board.clone();
        int[] dYX = Movement.translateStep(movement.step); //dYX[0] == dY dYX[1] == dX
        if(playerTurn) {
            int newY = pY+dYX[0], newX = pX+dYX[1];
            if(afterTurn[newY][newX] >= 3 || afterTurn[movement.destroyedY][movement.destroyedX] != 1) {
                throw new IllegalArgumentException("AI error, forbidden move(step)");
            }
            afterTurn[pY][pX] = 1;
            afterTurn[newY][newX] = 4;
            afterTurn[movement.destroyedY][movement.destroyedX] = 3;
            return new Node(afterTurn,newY,oY,newX,oX,false,movement);
        }

        int newY = oY+dYX[0], newX = oX+dYX[1];
        if(afterTurn[newY][newX] >= 3 || afterTurn[movement.destroyedY][movement.destroyedX] != 1) {
            throw new IllegalArgumentException("AI error, forbidden move(step)");
        }
        afterTurn[oY][oX] = 1;
        afterTurn[newY][newX] = 4;
        afterTurn[movement.destroyedY][movement.destroyedX] = 3;
        return new Node(afterTurn,pY,newY,pX,newX,true,movement);
    }

    public double eval() {

    }

    private int possibleMoves(boolean forPlayer) {
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

    private int teritory(boolean forPlayer) {
        int x, y;
        ArrayDeque<Tile> tmp = new ArrayDeque<>();
        HashSet<Tile> field = new HashSet<>();

        x = forPlayer ? pX : oX;
        y = forPlayer ? pY : oY;
//        tmp.add(tiles[y][x]);
//
//        while(tmp.size()>0) {
//            Tile t = tmp.pollLast();
//            y = t.getRow();
//            x = t.getCol();
//            if(t.isNormal()|| y==p.getRow()&&x==p.getColumn()) {
//                if(t.isNormal()) field.add(t);
//                if(y>0) {
//                    if(!field.contains(tiles[y-1][x])) tmp.add(tiles[y-1][x]);
//                    if(x>0 && !field.contains(tiles[y-1][x-1])) tmp.add(tiles[y-1][x-1]);
//                    if(x<6 && !field.contains(tiles[y-1][x+1])) tmp.add(tiles[y-1][x+1]);
//                }
//                if(x>0 && !field.contains(tiles[y][x-1])) tmp.add(tiles[y][x-1]);
//                if(x<6 && !field.contains(tiles[y][x+1])) tmp.add(tiles[y][x+1]);
//                if(y<6) {
//                    if(!field.contains(tiles[y+1][x])) tmp.add(tiles[y+1][x]);
//                    if(x>0 && !field.contains(tiles[y+1][x-1])) tmp.add(tiles[y+1][x-1]);
//                    if(x<6 && !field.contains(tiles[y+1][x+1])) tmp.add(tiles[y+1][x+1]);
//                }
//            }
//        }
//        return field.size();
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