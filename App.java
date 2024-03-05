import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.ArrayList;

/*
 * TODO:
 * App.draw issue in containsMatch-check!
 */


public class App {

    // local variables off app:

    private long score = 0;
    private long highscore = 0;

    //array for color creation:
    private final String[] colors = {"0xffffff", "0xff0000", "0x00ff00", "0x0000ff", "0xff8800", "0xaa0066"}; 

    // variables for play area:
    protected int[][] area = new int[10][10];
    protected JPanel[][] Area = new JPanel[10][10];

    private Dimension screenSize = new Dimension(1000,800);

    //getters and setters:

    public long getScore() {
        return this.score;
    }
    public long getHighscore() {
        return this.highscore;
    }

    public void setScreenSize(Dimension newSize) {
        this.screenSize = newSize;
    }


    //other methods:


    /*
     * refills the area when game starts or the board is
     * empty. First the area-array gets filles with numbers in the range of 
     * the colors-array.
     */
    public void fillPlayArea (JFrame RootFrame, JPanel RootPanel, int[][] area, JPanel[][] PlayArea, JLabel Score, JLabel Highscore) {

        for (int row = 0; row < 10; ++row) {
            for (int col = 0; col < 10; ++col) {
                area[row][col] = (int) (Math.random() * 5 + 1);
            }
        }
        draw(RootFrame, RootPanel, area, PlayArea, Score, Highscore);
    }

    //function to paint the play area:
    public void draw (JFrame RootFrame, JPanel RootPanel, int[][] area, JPanel[][] PlayArea, JLabel Score, JLabel Highscore) {

        Color cellColor = null;

        //if PlayArea is already poulated, remove all children of RootPanel and repopulate later:
        RootPanel.removeAll();
        
        for (int row = 0; row < 10; ++row) {
            for (int col = 0; col < 10; ++col) {

                /*
                 * get the cell color by first get the string name of the color 
                 * determined by the area array, then the string gets decoded by the
                 * decode funcion of Color class:
                 */
                cellColor = Color.decode( colors[ area[row][col] ] );

                Area[row][col] = new JPanel();
                Area[row][col].setSize(new Dimension( screenSize.width/10, (screenSize.height * 9 / 100) ) );
                Area[row][col].setBackground(cellColor);

                //set unique id for each block based on location:
                Area[row][col].setName( Integer.toString(row) + Integer.toString(col) );

                RootPanel.add(Area[row][col]);

                //add mouse listener to colored blocks (excluding white):
                if (area[row][col] > 0) {
                Area[row][col].addMouseListener( new MouseAdapter() {
                    public void mousePressed( MouseEvent ev) {
                        if (ev.getSource() instanceof JPanel) {
                            int name = Integer.valueOf(ev.getComponent().getName());
    
                            //check neighbors for matches with out-of-bounds-check:               
                            if (name / 10 + 1 < 10) checkNeighbors(name + 10, name);
                            if (name / 10 - 1 > -1) checkNeighbors(name - 10, name);
                            if (name % 10 + 1 < 10) checkNeighbors(name + 1, name);
                            if (name % 10 - 1 > -1) checkNeighbors(name - 1, name);
    
                            removeCells(matches, Area, area);
                            Score.setText(Long.toString(score));

                            // if there are no matches remaining, set new highscore and reset board:
                            if (!containsMatch) {
                                if (score > highscore) {
                                     Highscore.setText(Long.toString(score));
                                     score = 0;
                                     Score.setText("0");
                                }
                                fillPlayArea(RootFrame, RootPanel, area, PlayArea, Score, Highscore); // draws 2 times when game over... fix later
                            }

                            draw(RootFrame, RootPanel, area, PlayArea, Score, Highscore);
                            RootFrame.revalidate();
                            //printMaxrix();
                        }
                    }
                });
                }
            }
        }
    }

    public void printMaxrix(){
        for (int i = 0; i < 10; ++i){
            for (int j = 0; j < 10; ++j){
                System.out.print(area[i][j] + " ");
            }
            System.out.print("\n");
        }
        System.out.print("\n");
        System.out.flush();
    }

    //temporary variables to cache every mached block:
    private ArrayList<Integer> matches = new ArrayList<Integer>();
    private boolean containsMatch = false;


    public void checkNeighbors (int current, int previous) {

        //get current row and check if out-of bounds:
        int currRow = current / 10;
        if (currRow  < 0 || currRow > 9) return;

        //get current column and check if out-of-bounds:
        int currCol = current % 10;
        if (currCol < 0 || currCol > 9) return;

        int prevRow = previous / 10;
        int prevCol = previous % 10;

        //return if current cell color doesn't match previous cell color:
        if (area[currRow][currCol] != area[prevRow][prevCol]) return;

        //check whether current cell is in matches or we have to add it:
        if (matches.contains(current)) {
            return;
        } else {
            matches.add(current);
        }

        //recursively check neighbors without wrapping:
        if (currRow + 1 < 10) checkNeighbors( current + 10, current);
        if (currRow - 1 > -1) checkNeighbors( current - 10, current);
        if (currCol + 1 < 10) checkNeighbors( current + 1, current);
        if (currCol - 1 > -1) checkNeighbors( current - 1, current);

        return;
    }

    //sets cells with id contained in matches to white:

    public void removeCells(ArrayList<Integer> matches, JPanel[][] PlayArea, int[][] area) {

        for (int match : matches) {
            PlayArea[ match / 10 ][ match % 10 ].setBackground(Color.decode(colors[0]));
            area[ match / 10 ][ match % 10 ] = 0;
        }

        // score multiplier based on count of removed cells:
        float scoreMultiplier = 1.0f;
        for (int match : matches) scoreMultiplier += 0.5f;
        
        // calculate new score and applies multiplier:
        score += matches.size() * scoreMultiplier;

        matches.removeAll(matches);

        joinDownwards(area);
    }

    //move all non-white cells upwards:
    public void joinDownwards (int[][] playArea) {

        /*
         * looks at each individual column and sets borderRow as a border. The row iterator checks
         * if the cell itself is white and the cell above is not. When that is the case, it "switches" both cells
         * and moves one cell up until it reaches the border; the border moves one cell down and each cell below gets
         * checked again unil the border reaches the bottom row. This ensures that in the worst case where every second
         * cell is white all cells moves down without checking all white cells above it, where we know that there is no 
         * colored cell remaining: 
         */

        //reset containsMatch for checks:
        containsMatch = false;

        int borderRow = 0;
        int row = 9;

        for (int col = 9; col > -1; --col){
            
            for (borderRow = 0; borderRow < 10; ++borderRow){
                for (row = 9; row > borderRow; --row) {
                    //check wether there are matching blocks in column:
                    if (area[row][col] > 0 && area[row][col] == area[row - 1][col]) containsMatch = true;

                    //switch cells:
                    if (area[row][col] == 0 && area[row - 1][col] > 0) {
                        
                        area[row][col] = area[row - 1][col];
                        area[row - 1][col] = 0;
                    }
                }
            }
        }
        joinLeft(playArea);
    }
    
    public void joinLeft (int[][] playArea) {    
    //same procedure as joinDownwards, just cells get moved to the left:

        int borderCol = 9;
        int col = 0;

        for (int row = 9; row > -1; --row) {
            
            for (borderCol = 9; borderCol > -1; --borderCol) {
                for (col = 0; col < borderCol; ++col) {
                    //check wether there are matching blocks in row:
                    if (area[row][col] > 0 && area[row][col] == area[row][col + 1]) containsMatch = true;

                    //switch cells:
                    if ( area[row][col] == 0 && area[row][col + 1] > 0) {
                        area[row][col] = area[row][col + 1];
                        area[row][col + 1] = 0;
                    }
                }
            }
        }
        System.out.println(containsMatch);
    }

    public static void main(String[] args) {

        App app = new App();
            //create and customize main window:
        JFrame RootFrame = new JFrame("Same Game");
        JPanel Scoreboard = new JPanel();
            JLabel Score = new JLabel();
            JLabel Highscore = new JLabel();
        JPanel PlayAreaConstraints = new JPanel();
        JPanel PlayArea = new JPanel();
    

        RootFrame.setVisible(true);

        //set layouts of components:
        RootFrame.setLayout(new BorderLayout());
        Scoreboard.setLayout(new BoxLayout(Scoreboard, BoxLayout.X_AXIS));
        PlayArea.setLayout(new GridLayout(10,10));

        // intitial size settings of components:
        RootFrame.setSize(1000, 800);
        Score.setPreferredSize(new Dimension(app.screenSize.width/2, app.screenSize.height/10));
        Highscore.setPreferredSize(new Dimension(app.screenSize.width/2, app.screenSize.height/10));
        PlayAreaConstraints.setSize(new Dimension( app.screenSize.width, app.screenSize.height * 8 / 10 ) );
        PlayArea.setSize(new Dimension( app.screenSize.width, app.screenSize.height * 8 / 10 ) );

        //set text algnment of (high)score:
        Score.setVerticalAlignment(SwingConstants.CENTER);
        Score.setHorizontalAlignment(SwingConstants.CENTER);
        Highscore.setVerticalAlignment(SwingConstants.CENTER);
        Highscore.setHorizontalAlignment(SwingConstants.CENTER);
        
        //adding components to parent components:
        RootFrame.add(Scoreboard, BorderLayout.NORTH);
            Scoreboard.add(Score);
            Scoreboard.add(Highscore);

        RootFrame.add(PlayArea, BorderLayout.CENTER);
            //PlayAreaConstraints.add(PlayArea);
        PlayArea.setBackground(Color.LIGHT_GRAY);
        

        //initially write score and highscore:
        Score.setText(Long.toString(app.getScore()));
        Highscore.setText(Long.toString(app.getHighscore()));

        //override default close operation so the window event listener can 
        //handle it and do some cleanup before closing:
        RootFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // add event listeners to components:
        RootFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing (WindowEvent ev) {
                RootFrame.dispose();
                return;
            }
        });

        //resize all childs of main window:
        RootFrame.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent ev) {
                //reset the screen size:
                app.setScreenSize(RootFrame.getSize());

                Score.setPreferredSize(new Dimension(app.screenSize.width/2, app.screenSize.height/10));
                Highscore.setPreferredSize(new Dimension(app.screenSize.width/2, app.screenSize.height/10));
            }
        });

        //initialize play area:
        app.fillPlayArea(RootFrame, PlayArea, app.area, app.Area, Score, Highscore);
        RootFrame.repaint();
    }
}