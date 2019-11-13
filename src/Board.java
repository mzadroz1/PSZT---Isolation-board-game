import java.util.*;

public class Board {

    private Tile[][] tiles;
    private Player player;
    private Player opponent;
    private boolean playerTurn; //false if it's AI turn, true if it's human player turn
    private int gameState;

    public Board() {
        initBoard();
    }

    public Board(Board other) {
        this.tiles = new Tile[7][7];
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j< 7; j++) {
                tiles[i][j] = new Tile();
                tiles[i][j].setType(other.getTiles()[i][j].getType());
                tiles[i][j].positionOnBoard(i,j);
                tiles[i][j].setY(i*tiles[i][j].getImageWidth());
                tiles[i][j].setX(j*tiles[i][j].getImageHeight());
            }
        }
        player = new Player(other.getPlayer().getRow(),other.getPlayer().getColumn(),1);
        opponent = new Player(other.getOpponent().getRow(),other.getOpponent().getColumn(),2);
        this.calculatePlayerCoordinates(player);
        this.calculatePlayerCoordinates(opponent);
        playerTurn = other.playerTurn;
        gameState = other.gameState;
    }

    public void initBoard() {
        gameState = 1;
        player = new Player(6,3, 1);
        opponent = new Player(0,3, 2);
        tiles = new Tile[7][7];
        playerTurn = true;
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j< 7; j++) {
                tiles[i][j] = new Tile();
                tiles[i][j].setType(1);
                tiles[i][j].positionOnBoard(i,j);
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

    public boolean isLooser(Player player) {
        return this.possibleMoves(player)==0;
    }

    public double evalGameState() { // opponent - MIN, player - MAX
        if(this.isLooser(player))
            return Double.NEGATIVE_INFINITY;
        if(this.isLooser(opponent))
            return Double.POSITIVE_INFINITY;
        double evaluation = 0.0;
        evaluation -= 3.0*teritory(opponent);
        evaluation += 3.0*teritory(player);
        evaluation -= 0.25*possibleMoves(opponent);
        evaluation += 0.25*possibleMoves(player);

        return evaluation;
    }

    public ArrayList<Board> generatePossibleBoards() {
        ArrayList<Board> result = new ArrayList<>();
        Player currPlayer = getOpponent();


        int x = currPlayer.getRow(), y = currPlayer.getColumn();
        for(int i = x - 1; i <= x + 1; i++) {
            for(int j = y - 1; j <= y + 1; j++) {
                if (i >= 0 && i <= 6 && j >= 0 && j <= 6) {
                    if (tiles[i][j].isNormal()) {
                        for (int k = 0; k < 7; k++) {
                            for (int l = 0; l < 7; l++) {
                                if (tiles[k][l].isNormal()) {
                                    Board child = new Board(this);

                                    child.movePlayer(child.getOpponent(), i, j);
                                    //child.getTiles()[x][y].setType(1);
                                    child.destroyTile(k, l);
                                    result.add(child);
                                }
                            }
                        }
                    }
                }
            }
        }

        return result;
    }

    public void movePlayer(Player player, int row, int col) {
        tiles[player.getRow()][player.getColumn()].setType(1);
        player.setRow(row);
        player.setColumn(col);
        calculatePlayerCoordinates(player);
        tiles[row][col].setType(4);
    }

    public void destroyTile(int row, int col) {
        tiles[row][col].setType(3);
    }


    //zliczamy na ile pol moze sie jeszcze przemiescic gracz AI
    private int teritory(Player p) {
        int x = p.getColumn(), y = p.getRow();
        ArrayDeque<Tile> tmp = new ArrayDeque<>();
        HashSet<Tile> field = new HashSet<>();
        tmp.add(tiles[y][x]);

        while(tmp.size()>0) {
            Tile t = tmp.pollLast();
            y = t.getRow();
            x = t.getCol();
            if(t.isNormal()|| y==p.getRow()&&x==p.getColumn()) {
                if(t.isNormal()) field.add(t);
                if(y>0) {
                    if(!field.contains(tiles[y-1][x])) tmp.add(tiles[y-1][x]);
                    if(x>0 && !field.contains(tiles[y-1][x-1])) tmp.add(tiles[y-1][x-1]);
                    if(x<6 && !field.contains(tiles[y-1][x+1])) tmp.add(tiles[y-1][x+1]);
                }
                if(x>0 && !field.contains(tiles[y][x-1])) tmp.add(tiles[y][x-1]);
                if(x<6 && !field.contains(tiles[y][x+1])) tmp.add(tiles[y][x+1]);
                if(y<6) {
                    if(!field.contains(tiles[y+1][x])) tmp.add(tiles[y+1][x]);
                    if(x>0 && !field.contains(tiles[y+1][x-1])) tmp.add(tiles[y+1][x-1]);
                    if(x<6 && !field.contains(tiles[y+1][x+1])) tmp.add(tiles[y+1][x+1]);
                }
            }
        }
        return field.size();
    }

    private int possibleMoves(Player p) {
        int pMoves =0, x, y;
        x = p.getColumn();
        if((y =p.getRow()) > 0) {
            if(tiles[y-1][x].isNormal()) ++pMoves;
            if(x>0 && tiles[y-1][x-1].isNormal()) ++pMoves;
            if(x<6 && tiles[y-1][x+1].isNormal()) ++pMoves;
        }
        if(x>0 && tiles[y][x-1].isNormal()) ++pMoves;
        if(x<6 && tiles[y][x+1].isNormal()) ++pMoves;
        if((y =p.getRow()) < 6) {
            if(tiles[y+1][x].isNormal()) ++pMoves;
            if(x>0 && tiles[y+1][x-1].isNormal()) ++pMoves;
            if(x<6 && tiles[y+1][x+1].isNormal()) ++pMoves;
        }
        return pMoves;
    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public Player getPlayer() {
        return player;
    }

    public Player getOpponent() {return opponent;}

    public boolean isPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(boolean playerTurn) {
        this.playerTurn = playerTurn;
    }

    public boolean getPlayerTurn() {
        return playerTurn;
    }

    public int getGameState() {return gameState;}

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }
}
