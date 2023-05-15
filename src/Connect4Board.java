
package aiproject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import javafx.scene.paint.Color;

public class Connect4Board {
  
   public final static int COLS = 7; //Board dimensions
   public final static int ROWS = 6;

    //The piece
    static enum Piece {
        
        RED(+1, Color.RED), YELLOW(-1, Color.YELLOW), EMPTY(0, Color.WHITE);

        private final int fieldValue;
        private final Color color;

        private Piece(int fieldValue, Color color) {
            this.fieldValue = fieldValue;
            this.color = color;
        }

        int getFieldValue() {
            return fieldValue;
        }

        Color getColor() {
            return color;
        }

        static Piece ofFieldValue(int f) {
            switch (f) {
            case -1:
                return YELLOW;
            case +1:
                return RED;
            default: //zero
                return EMPTY; 
            }
        }

    } 
//a winning combination of 4 fields
    class Line {
        //field array element boxing class
        class Field {

            private final int row, col;

            Field(int col, int row) {
                this.row = row;
                this.col = col;
            }
            int getFieldValue() {
                return board[col][row];
            }
            Piece getPiece() {
                return Piece.ofFieldValue(board[col][row]);
            }
        }

        final private List<Field> fields = new ArrayList<Field>(4);

        
        private Line(int col, int row, int colo, int rowo) { // Create a winning line starting at (col,rol) in direction
                                                             // (colo,rowo)
            for (int i = 0; i < 4; i++) {
                fields.add(new Field(col + i * colo, row + i * rowo));
            }
        }

        int value() { //Count number of unique pieces in this line
            int s = 0;
            for (Field f : fields) {
                int sf = f.getFieldValue();
                if (s * sf < 0) return 0;
                s += sf;
            }
            return s;
        }

        int count() { // Count number of pieces in this line
            return (int)fields.stream().filter(f->(f.getFieldValue()!=0)).count();
        }

    }
    // Board data
    private int[][] board = new int[COLS][ROWS]; //Piece field values -1,0,+1
    private int[] colPieces = new int[COLS]; //Number of pieces in a column
    private int totPieces = 0; //Overall number pieces on the board
    private List<Line> lines; //Array list of all still possible line combinations

    Connect4Board() {
        for (int c = 0; c < COLS; c++) {
            colPieces[c] = 0;
            for (int r = 0; r < ROWS; r++) {
                board[c][r] = Piece.EMPTY.fieldValue;
            }
        }
        buildLines();
    }
    //Get a piece
    int get_(int col, int row) {
        return board[col][row];
    }
    Piece getPiece(int col, int row) {
        return Piece.ofFieldValue(get_(col, row));
    }
    //Put a piece
    void put_(int col, int p) {
        board[col][colPieces[col]++] = p;
        totPieces = getTotPieces() + 1;
    }
    boolean putPiece(int col, Piece piece) {
        if (colPieces[col]>=ROWS) return false;
        put_(col,piece.getFieldValue());
        updateLines();
        return true;
    }
//    //Remove a piece
//    void remove_(int col) {
//        board[col][--colPieces[col]] = 0;
//        totPieces = getTotPieces() - 1;
//    }
//    void removePiece(int col) {
//        remove_(col);
//        buildLines(); //Rebuild the line list
//        updateLines();
//    }
    int getColPieces(int col) {
        return colPieces[col];
    }

    int getTotPieces() {
        return totPieces;
    }
    List<Line> getLines() {
        return lines;
    }
    int getLineCount() {
        return lines.size();
    }
    //Create all winning line combinations of an empty field
    private void buildLines() {
        lines = new ArrayList<Line>();
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (r + 4 <= ROWS)
                    lines.add(new Line(c, r, 0, 1)); //Vertical
                if (c + 4 <= COLS)
                    lines.add(new Line(c, r, 1, 0)); //Horizontal
                if (r + 4 <= ROWS && c + 4 <= COLS)
                    lines.add(new Line(c, r, 1, 1)); //Diagonal
                if (r + 4 <= ROWS && c - 3 >= 0)
                    lines.add(new Line(c, r, -1, 1));
            }
        }
    }
    //Remove all lines which currently do not have any more impact on the game (speed optimization)
    private void updateLines() {
        Iterator<Line> i = lines.iterator();
        while (i.hasNext()) {
            Line l = i.next();
            if (l.count() != 0 && l.value() == 0) {
                i.remove();
            }
        }
    }
    //Find the first line completed with 4 pieces
    private Optional<Line> getWinningLine() {
        return lines.stream().filter(l->(Math.abs(l.value())==4)).findFirst(); 
    }
    //Call a BiConsumer on all fields of the winning line
    void processWinningLine( BiConsumer<Integer,Integer> c ) {   
        getWinningLine().ifPresent(l -> l.fields.forEach(f -> c.accept(f.col, f.row)));      
    }
    boolean gameOver() {
      return gameWon() || getTotPieces() >= ROWS * COLS;
    }
    boolean gameWon() {
        return getWinningLine().isPresent();
    }
}