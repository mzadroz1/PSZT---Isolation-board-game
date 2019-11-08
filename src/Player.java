import javax.swing.*;

public class Player extends GameObject {

    private int i, j;

    public Player() {
        i = 6;
        j = 3;
        loadImage();
        getImageDimensions();
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

