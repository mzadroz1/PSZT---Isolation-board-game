import javax.swing.*;

public class Player extends GameObject {

    private int i, j, type;

    public Player(int i, int j, int type) {
        this.i = i;
        this.j = j;
        this.type = type;
        loadImage();
        getImageDimensions();
    }

    public void setRow(int i) {
        this.i = i;
    }

    public void setColumn(int j) {
        this.j = j;
    }

    private void loadImage() {
        if(type == 1) {
            ImageIcon img = new ImageIcon("images/player.png");
            setImage(img.getImage()); }
        else {
            ImageIcon img = new ImageIcon("images/opponent.png");
            setImage(img.getImage());
        }

    }

    public int getRow() {
        return i;
    }

    public int getColumn() {
        return j;
    }
}

