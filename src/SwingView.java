
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.*;

public class SwingView extends JPanel implements View {
    private static final long serialVersionUID = -7729510720848698723L;

    Board board;
    Controller controller;

    public SwingView() {
        //setSize(720, 730);
        addMouseListener(createMouseListener());
        setFocusable(true);
    }

    private MouseListener createMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {

            }

            @Override
            public void mousePressed(MouseEvent e) {

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if(controller.getEndGameState()!=0)
                    System.exit(0);
                if(e.getButton() == MouseEvent.BUTTON1)
                    controller.tileClicked(x,y);
                if(e.getButton() == MouseEvent.BUTTON3)
                    controller.showEval();

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
        paintPlayer(g);
        paintOpponent(g);
        if(controller.getEndGameState()!=0) {
            paintNotify(g);
        }
    }

    private void paintNotify(Graphics2D g) {
        Image win = new ImageIcon("images/win.png").getImage();
        Image loose = new ImageIcon("images/lose.png").getImage();
        if(controller.getEndGameState()==1)
            g.drawImage(win,0,0, null);
        else
            g.drawImage(loose,0,0,null);
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

    private void paintPlayer(Graphics2D g) {
        Player player = board.getPlayer();
        g.drawImage(player.getImage(),player.getX(),player.getY(),player.getImageWidth(),player.getImageHeight(),this);
    }

    private void paintOpponent(Graphics2D g) {
        Player opp = board.getOpponent();
        g.drawImage(opp.getImage(),opp.getX(),opp.getY(),opp.getImageWidth(),opp.getImageHeight(),this);
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
