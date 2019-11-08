import java.util.ArrayList;

public class Board {

    private Tile[][] tiles;

    public Board() {
        initBoard();
    }

    public void initBoard() {
        tiles = new Tile[7][7];
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j< 7; j++) {
                tiles[i][j] = new Tile();
                tiles[i][j].setType(1);
                tiles[i][j].setX(i*tiles[i][j].getImageWidth());
                tiles[i][j].setY(j*tiles[i][j].getImageHeight());
            }
        }
    }

    public Tile[][] getTiles() {
        return tiles;
    }
}
