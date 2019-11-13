import javax.swing.*;
import java.awt.*;

public class Main {

    static boolean isPlayerTurn;

    private static Board createModel(boolean isPlayerTurn) {
        return new Board(isPlayerTurn);
    }

    private static Controller createController(Board b) {
        return new Controller(b);
    }

    private static SwingView createModelViewController(boolean isPlayerTurn) {
        Board b = createModel(isPlayerTurn);
        Controller c = createController(b);
        SwingView v = new SwingView();
        v.setModel(b);
        v.setController(c);
        c.setView(v);
        return v;
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Isolation Game") {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(717, 739);
            }
        };
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


        frame.setSize(frame.getPreferredSize());
        frame.setResizable(false);
        //frame.setSize(v.getSize());
        frame.setVisible(true);
        SwingView v = createModelViewController(isPlayerTurn);
        frame.getContentPane().add(v);
        frame.pack();
        v.requestFocus();
    }

    public static void main(String[] args) {

        isPlayerTurn = args.length < 1 || !args[0].equals("-n");

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
