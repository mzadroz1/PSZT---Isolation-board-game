
public class Board {

    private Tile[][] tiles;
    private Player player;
    private Player opponent;
    private int gameState;

    public Board() {
        initBoard();
    }

    public void initBoard() {
        gameState = 1;
        player = new Player(6,3, 1);
        opponent = new Player(0,3, 2);
        tiles = new Tile[7][7];
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j< 7; j++) {
                tiles[i][j] = new Tile();
                tiles[i][j].setType(1);
                tiles[i][j].setY(i*tiles[i][j].getImageWidth());
                tiles[i][j].setX(j*tiles[i][j].getImageHeight());
            }
        }
        calculatePlayerCoordinates(player);
        calculatePlayerCoordinates(opponent);
        tiles[player.getRow()][player.getColumn()].setType(4);
        tiles[opponent.getRow()][opponent.getColumn()].setType(4);
    }

    public void calculatePlayerCoordinates(Player player) {
        Tile tile = tiles[player.getRow()][player.getColumn()];
        int playerX = tile.getX() + (tile.getImageWidth()-player.getImageWidth())/2;
        int playerY = tile.getY() + (tile.getImageHeight()-player.getImageHeight())/2;
        player.setX(playerX);
        player.setY(playerY);
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getOpponent() {return opponent;}

    public int getGameState() {return gameState;}

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }
}
