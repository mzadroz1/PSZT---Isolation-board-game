import javax.swing.*;

public class Player extends GameObject {

    private int i, j;

    public Player(int i, int j) {
        this.i = i;
        this.j = j;
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
        ImageIcon img = new ImageIcon("images/player.png");
        setImage(img.getImage());
    }

    public int getRow() {
        return i;
    }

    public int getColumn() {
        return j;
    }
}

