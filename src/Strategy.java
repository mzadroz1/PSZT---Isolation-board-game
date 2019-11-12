import java.util.ArrayList;

public class Strategy {
    Node root;
    Movement predictedTurn;

    public Strategy(Board board) {
        root = new Node(board);
        predictedTurn = null;
    }

    public ArrayList<Movement> possibleMoves(Node node) {
        ArrayList<Movement> possibles = new ArrayList<>();
        ArrayList<Step> steps = new ArrayList<>();
        Player mover;
        if(node.gameState.getPlayerTurn())
            mover = node.gameState.getPlayer();
        else
            mover = node.gameState.getOpponent();
        int r = mover.getRow(), c = mover.getColumn();
        if(node.gameState.getTiles()[r-1][c-1].isNormal())
            steps.add(Step.NW);
        if(node.gameState.getTiles()[r-1][c].isNormal())
            steps.add(Step.N);
        if(node.gameState.getTiles()[r-1][c+1].isNormal())
            steps.add(Step.NE);
        if(node.gameState.getTiles()[r][c-1].isNormal())
            steps.add(Step.W);
        if(node.gameState.getTiles()[r][c+1].isNormal())
            steps.add(Step.E);
        if(node.gameState.getTiles()[r+1][c-1].isNormal())
            steps.add(Step.SW);
        if(node.gameState.getTiles()[r+1][c].isNormal())
            steps.add(Step.S);
        if(node.gameState.getTiles()[r+1][c+1].isNormal())
            steps.add(Step.SE);


        for(Step st: steps) {
            int[] dYX = Movement.translateStep(st); //dYX[0] == dY dYX[1] == dX
            int newY = mover.getRow()+dYX[0], newX = mover.getColumn()+dYX[1];
            for (int i = 0; i < 7; ++i)
                for (int j = 0; j < 7; ++j) {
                    if ((node.gameState.getTiles()[i][j].isNormal() && i!=newY && j!=newX)
                            || (i==mover.getRow() && j==mover.getColumn())) {
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
    Board gameState;
    Movement lastMove;
    double points;
    ArrayList<Node> possibleMoves;

    public Node(Board b) {
        this.gameState = new Board(b);
        possibleMoves = new ArrayList<>();
        points = gameState.evalGameState();
    }

    public Node(Board b, Movement movement) {
        this(b);

        Player mover;
        if(gameState.getPlayerTurn())
            mover = gameState.getPlayer();
        else
            mover = gameState.getOpponent();
        lastMove = movement;
        int[] dYX = Movement.translateStep(movement.step);
        int dY=dYX[0], dX=dYX[1];
        int newX = mover.getColumn() + dX;
        int newY = mover.getRow() + dY;

        if(newX>6 || newY>6 || newX<0 || newY<0 || !gameState.getTiles()[newY][newX].isNormal()) {
            throw new IllegalArgumentException("AI has generated illegal move!");
        }
        gameState.movePlayer(mover, newY, newX);

        if(!gameState.getTiles()[movement.destroyedY][movement.destroyedX].isNormal()) {
            throw new IllegalArgumentException("AI has generated illegal tile destruction!");
        }
        gameState.destroyTile(movement.destroyedY,movement.destroyedX);

        points = gameState.evalGameState();

        //UWAGA WAŻNE TUTAJ ZMIENIAM CZYJ JEST KOLEJNY RUCH!!!
        gameState.setPlayerTurn(!gameState.getPlayerTurn());
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