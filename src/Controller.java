import java.util.Timer;
import java.util.TimerTask;

public class Controller {

    private Board board;
    private Timer timer;
    private View view;

    public Controller(Board board) {
        this.board = board;
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
        if(gameState == 1)
            showAvailableMoves(x,y);
        if(gameState == 2)
            movePlayer(x,y);
        if(gameState == 3)
            destroyTile(x,y);
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
        Player player = board.getPlayer();
        Tile[][] tiles = board.getTiles();
        int playerRow = player.getRow();
        int playerColumn = player.getColumn();
        boolean moved = false;
        for(int i = playerRow - 1; i <= playerRow + 1; i++) {
            for (int j = playerColumn - 1; j <= playerColumn + 1; j++) {
                if (i >= 0 && i <= 6 && j >= 0 && j <= 6) {
                    if(tiles[i][j].isActive()) {
                        if (tiles[i][j].isPointInsideTile(x, y)) {
                            tiles[playerRow][playerColumn].setType(1);
                            player.setRow(i);
                            player.setColumn(j);
                            board.calculatePlayerCoordinates();
                            tiles[i][j].setType(4);
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
                        tiles[i][j].setType(3);
                        board.setGameState(1);
                    }
                }
            }
        }
    }


    private class ScheduleTask extends TimerTask {

        @Override
        public void run() {

            view.updateView();
        }
    }

}
