
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JPanel;

public class SwingView extends JPanel implements View {
    private static final long serialVersionUID = -7729510720848698723L;

    private Board board;
    private Controller controller;

    public SwingView() {
        //setSize(720, 730);
        addMouseListener(createMouseListener());
        setFocusable(true);
    }

    private MouseListener createMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                controller.tileClicked(x,y);
            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {

            }

            @Override
            public void mouseEntered(MouseEvent e) {

            }

            @Override
            public void mouseExited(MouseEvent e) {

            }
        };
    }

    @Override
    protected void paintComponent(Graphics g1) {
        Graphics2D g = (Graphics2D) g1;
        fillBackground(g);
        paintTiles(g);
    }

    private void paintTiles(Graphics2D g) {
        Tile[][] tiles = board.getTiles();
        for(int i = 0; i < 7; i++) {
            for(int j = 0; j< 7; j++) {
                Tile tile = tiles[i][j];
                g.drawImage(tile.getImage(),tile.getX(),tile.getY(),tile.getImageWidth(),tile.getImageHeight(),this);
            }
        }
    }

    private void fillBackground(Graphics2D g) {
        g.setColor(Color.blue);
        g.fillRect(0, 0, getWidth(), getHeight());
    }

    @Override
    public void updateView() {
        repaint();
    }

    @Override
    public void setModel(Board board) {
        this.board = board;
    }

    @Override
    public void setController(Controller c) {
        this.controller = c;
    }

}
