
public interface View {
        void updateView();

        void setModel(Board board);
        void setController(Controller c);

        int getWidth();
    }

