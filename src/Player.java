import javax.swing.*;

public class Player extends GameObject {

    private int i, j;

    public Player() {
        i = 3;
        j = 3;
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

