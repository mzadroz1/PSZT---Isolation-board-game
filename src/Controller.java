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
        Tile[][] tiles = board.getTiles();
        for(int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if(tiles[i][j].isPointInsideTile(x,y))
                    tiles[i][j].setType(3);
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
