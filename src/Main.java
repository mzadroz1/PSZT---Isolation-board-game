import javax.swing.*;
import java.awt.*;
import java.util.Scanner;

public class Main {

    static boolean isPlayerTurn;
    static int depth;

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

        //default
            isPlayerTurn = true;
            depth = 4;

        if(args.length == 2 || args.length == 4) {
            if(args[0].equals("-first")) {
                if(args[1].equals("player"))
                    isPlayerTurn = true;
                if(args[1].equals("ai")) {
                    isPlayerTurn = false;
                }
            }
            if(args[0].equals("-depth"))
                depth = Integer.parseInt(args[1]);

            if(args.length == 4) {
                if(args[2].equals("-first")) {
                    if(args[3].equals("player"))
                        isPlayerTurn = true;
                    if(args[3].equals("ai")) {
                        isPlayerTurn = false;
                    }
                }
                if(args[2].equals("-depth"))
                    depth = Integer.parseInt(args[3]);
            }
        }

        /*isPlayerTurn = (args.length < 1) || !args[0].equals("-n");
        if(args.length < 2)
            depth = 4;
        else
            depth = Integer.parseInt(args[1]);*/
        /*String input;
        while(true) {
            System.out.println("Kto rozpoczyna grę?");
            System.out.println("0 - Gracz");
            System.out.println("1 - Oponent sterowany przez AI");
            Scanner scan = new Scanner(System.in);
            input = scan.nextLine();
            if (input.equals("0") || input.equals("1"))
                break;
            else
                System.out.println("Podano nieprawidłowy znak, spróbuj ponownie: ");
        }
        isPlayerTurn = input.equals("0") ? true : false;
        */
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
