import javax.swing.*;

public class Tile extends GameObject {

    private int type;
    private int col, row;

    public Tile() {
        this.type = 1;
        loadImage();
        getImageDimensions();
    }

    public Tile(int x, int y, int type) {

        initBrick(x, y, type);
    }

    public void positionOnBoard(int row, int col) {
        this.row = row;
        this.col = col;
    }

    private void initBrick(int x, int y, int type) {

        setX(x);
        setY(y);
        this.type = type;

        loadImage();
        getImageDimensions();
    }

    private void loadImage() {
        if (type == 1 || type == 4) {
            ImageIcon img = new ImageIcon("images/normal.png");
            setImage(img.getImage());
        }
        if (type == 2 ) {
            ImageIcon img = new ImageIcon("images/active.png");
            setImage(img.getImage());
        }
        if (type == 3) {
            ImageIcon img = new ImageIcon("images/destroyed.png");
            setImage(img.getImage());
        }

    }

    public boolean isDestroyed() {
        return type == 3;
    }

    public boolean isActive() {
        return type == 2;
    }

    public boolean isNormal() {
        return type == 1;
    }

    public boolean isPlayerOnTile() {return type == 4;}

    public int getType() {return type;}

    public void setType(int type) {
        this.type = type;
        loadImage();
    }

    public boolean isPointInsideTile(int x, int y) {
        int tx = this.getX();
        int ty = this.getY();
        if(x < tx || x > tx + this.getImageWidth() || y < ty || y > ty + this.getImageWidth())
            return false;
        else
            return true;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

}