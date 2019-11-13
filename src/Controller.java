import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    private Board board;
    private Timer timer;
    private View view;
    private int endGameState; //0 -gra w trakcie, -1 - gracz przegrał 1 - gracz wygrał

    public Controller(Board board) {
        this.board = board;
        //this.board = new Board(board);
        endGameState = 0;
        timer = new Timer();
        final int INITIAL_DELAY = 50;
        final int PERIOD_INTERVAL = 6;
        timer.scheduleAtFixedRate(new ScheduleTask(),
                INITIAL_DELAY, PERIOD_INTERVAL);
    }

    public void setView(View v) {
        this.view = v;
    }

    public void tileClicked(int x, int y) {
        int gameState = board.getGameState();
        if(board.isPlayerTurn()) {
            if(gameState == 1)
                showAvailableMoves(x,y);
            if(gameState == 2)
                movePlayer(x,y);
            if(gameState == 3) {
                destroyTile(x, y);
                if(!board.isPlayerTurn() && !board.isLooser(board.getOpponent()))
                    AiTurn();
            }
        }
        else if(gameState == 1)
            AiTurn();
    }

    private void AiTurn() {
        Strategy strategy = new Strategy(this.board);
        strategy.thinkDumb();
        Movement move = strategy.predictedTurn;
        if (board.getGameState() == 1) {
            board.setGameState(2);
            int[] dYX = Movement.translateStep(move.step); //dYX[0] == dY dYX[1] == dX
            int newY = this.board.getOpponent().getRow() + dYX[0], newX = this.board.getOpponent().getColumn() + dYX[1];
            board.movePlayer(this.board.getOpponent(), newY, newX);
            board.setGameState(3);
            board.destroyTile(move.destroyedY, move.destroyedX);
            board.setPlayerTurn(true);
            board.setGameState(1);
        }
        if (board.isLooser(board.getPlayer())) {
            System.out.println("You Loose. GAME OVER");
            endGameState = -1;
        }
    }

    private void showAvailableMoves(int x, int y) {
        Player player = board.getPlayer();
        Tile[][] tiles = board.getTiles();
        if(tiles[player.getRow()][player.getColumn()].isPointInsideTile(x,y)) {
            for(int i = player.getRow() - 1; i <= player.getRow() + 1; i++) {
                for(int j = player.getColumn() - 1; j <= player.getColumn() + 1; j++) {
                    if(i >= 0 && i <= 6 && j >= 0 && j <= 6) {
                        if(tiles[i][j].isNormal())
                        tiles[i][j].setType(2);
                    }
                }
            }
            board.setGameState(2);
        }
    }

    private void movePlayer(int x, int y) {
        Tile[][] tiles = board.getTiles();
        Player player = board.getPlayer();
        int playerRow = player.getRow();
        int playerColumn = player.getColumn();
        boolean moved = false;
        for(int i = playerRow - 1; i <= playerRow + 1; i++) {
            for (int j = playerColumn - 1; j <= playerColumn + 1; j++) {
                if (i >= 0 && i <= 6 && j >= 0 && j <= 6) {
                    if(tiles[i][j].isActive()) {
                        if (tiles[i][j].isPointInsideTile(x, y)) {
                            board.movePlayer(player,i,j);
                            board.setGameState(3);
                            moved = true;
                        }
                        else {
                            tiles[i][j].setType(1);
                        }
                    }
                }
            }
            if(!moved) board.setGameState(1);
        }
    }

    private void destroyTile(int x, int y) {
        Tile[][] tiles = board.getTiles();
        for(int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if(tiles[i][j].isPointInsideTile(x,y)) {
                    if(tiles[i][j].isNormal()) {
                        board.destroyTile(i,j);

                        /*if(player == board.getPlayer())
                            player = board.getOpponent();
                        else
                            player = board.getPlayer();*/
                        board.setPlayerTurn(false);
                        board.setGameState(1);
                    }
                }
            }
        }
//        board.setGameState(1);
        if(board.isLooser(board.getOpponent())) {
            System.out.println("You Win. GAME OVER");
            endGameState = 1;
        }
    }

    public void showEval() {
        System.out.println("Stan gry: " + board.evalGameState());
    }

    private class ScheduleTask extends TimerTask {

        @Override
        public void run() {
            view.updateView();

        }
    }

    public int getEndGameState() {
        return endGameState;
    }


}
