
public class Board {

    private Tile[][] tiles;
    private Player player;
    private int gameState;

    public Board() {
        initBoard();
    }

    public void initBoard() {
        gameState = 1;
        player = new Player();
        tiles = new Tile[7][7];
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j< 7; j++) {
                tiles[i][j] = new Tile();
                tiles[i][j].setType(1);
                tiles[i][j].setY(i*tiles[i][j].getImageWidth());
                tiles[i][j].setX(j*tiles[i][j].getImageHeight());
            }
        }
        Tile tile = tiles[player.getRow()][player.getColumn()];
        int playerX = tile.getX() + (tile.getImageWidth()-player.getImageWidth())/2;
        int playerY = tile.getY() + (tile.getImageHeight()-player.getImageHeight())/2;
        player.setX(playerX);
        player.setY(playerY);
        tile.setType(4);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public Player getPlayer() {
        return player;
    }

    public int getGameState() {return gameState;}

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }
}
