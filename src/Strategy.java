import java.util.ArrayList;

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
        Player mover;
        if(node.gameState.getPlayerTurn())
            mover = node.gameState.getPlayer();
        else
            mover = node.gameState.getOpponent();
        int r = mover.getRow(), c = mover.getColumn();
        if(r>=1 && c>=1 && node.gameState.getTiles()[r-1][c-1].isNormal())
            steps.add(Step.NW);
        if(r>=1 && node.gameState.getTiles()[r-1][c].isNormal())
            steps.add(Step.N);
        if(r>=1 && c<6 && node.gameState.getTiles()[r-1][c+1].isNormal())
            steps.add(Step.NE);
        if(c>=1 && node.gameState.getTiles()[r][c-1].isNormal())
            steps.add(Step.W);
        if(c<6 && node.gameState.getTiles()[r][c+1].isNormal())
            steps.add(Step.E);
        if(r<6 && c>=1 && node.gameState.getTiles()[r+1][c-1].isNormal())
            steps.add(Step.SW);
        if(r<6 && node.gameState.getTiles()[r+1][c].isNormal())
            steps.add(Step.S);
        if(r<6 && c<6 && node.gameState.getTiles()[r+1][c+1].isNormal())
            steps.add(Step.SE);

        //dla kazdego kroku dostepnego znajduje dostepne zniszczenia kratek
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

    public Node(Board b) { // ten konstruktor tylko przy kopiowaniu boardu gry do roota strategii
        this.gameState = new Board(b);
        possibleMoves = new ArrayList<>();
        points = gameState.evalGameState();
    }

    public Node(Board b, Movement movement) { // ten konstruktor do uzywania w minmaxie,
        this(b);                              // sam robi ruch i zmienia czyj ruch nastepny (reprezentuje boarda
                                              // po wykonanym ruchu przekazanym jako move
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