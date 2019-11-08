import javax.swing.*;

public class Main {

    private static Board createModel() {
        return new Board();
    }

    private static Controller createController(Board b) {
        return new Controller(b);
    }

    private static SwingView createModelViewController() {
        Board b = createModel();
        Controller c = createController(b);
        SwingView v = new SwingView();
        v.setModel(b);
        v.setController(c);
        c.setView(v);
        return v;
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("HelloWorldSwing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        SwingView v = createModelViewController();
        frame.getContentPane().add(v);

        frame.setSize(v.getSize());
        frame.setVisible(true);

        v.requestFocus();
    }

    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
