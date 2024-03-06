import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;
import java.util.ArrayList;

public class App {

    // local variables off app:

    private long score = 0;
    private long highscore = 0;

    //array for color creation:
    private final String[] colors = {"0xffffff", "0xff0000", "0x00ff00", "0x0000ff", "0xff8800", "0xaa0066"};
    
    //integer to determine the colors used:
    int colorCount = 5;

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
                area[row][col] = (int) (Math.random() * colorCount + 1);
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
                            Score.setText("Score: " + Long.toString(score));

                            // if there are no matches remaining, reset board:
                            if (!containsMatch) {
                                //do not reset score if board gets fully cleared (if bottom left block is white):
                                if (area[9][0] > 0) {

                                    //only set new highscore if score is greater:
                                        if (score > highscore) {
                                            Highscore.setText("Highscore: " + Long.toString(score));
                                            highscore = score;
                                        }
                                    score = 0;
                                    Score.setText("Score: 0");
                                }                                
                                fillPlayArea(RootFrame, RootPanel, area, PlayArea, Score, Highscore);
                            } else {

                            draw(RootFrame, RootPanel, area, PlayArea, Score, Highscore); }
                            RootFrame.revalidate();
                            //printMaxrix();
                        }
                    }
                });
                }
            }
        }
    }
    // for debug purposes:
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
    }

    //set the amount of colors used and reset cells and (high)score:
    void setColorCount( String sourceName) {
        colorCount = Integer.parseInt(sourceName);
    }

    public static void main(String[] args) {

        App app = new App();
            //initialize components:
        JFrame RootFrame = new JFrame("Same Game");
        JPanel Scoreboard = new JPanel();
            JLabel Score = new JLabel();
            JLabel Highscore = new JLabel();
        JPanel PlayAreaConstraints = new JPanel();
            JPanel PlayArea = new JPanel();
        JMenuBar GameBar = new JMenuBar();
                JMenu ColorCountSelect = new JMenu("Colors (Alt + C)");
                JMenuItem[] ColorCount = new JMenuItem[4];
                for (int i = 0; i < 4; ++i) {
                    ColorCount[i] = new JMenuItem();
                    ColorCount[i].setText(Integer.toString(i + 2));
                    ColorCount[i].setName(Integer.toString(i + 2));

                    //add action listeners:
                    ColorCount[i].addActionListener( new ActionListener() {
                        public void actionPerformed(ActionEvent ev) {
                            app.setColorCount(ev.getActionCommand());
                            app.score = 0;
                            Score.setText("Score: 0");
                            app.highscore = 0;
                            Highscore.setText("Highscore: 0");
                            app.containsMatch = false;
                            app.fillPlayArea(RootFrame, PlayArea, app.area, app.Area, Score, Highscore);
                            RootFrame.validate();
                        }
                    });
                }

        //set layouts of components:
        RootFrame.setLayout(new BorderLayout());
            PlayAreaConstraints.setLayout(new BorderLayout());
                Scoreboard.setLayout(new BoxLayout(Scoreboard, BoxLayout.X_AXIS));
                PlayArea.setLayout(new GridLayout(10,10));

        // intitial size settings of components:
        RootFrame.setSize(1000, 800);

        Score.setPreferredSize(new Dimension(app.screenSize.width/2, app.screenSize.height/10)); 
        Highscore.setPreferredSize(new Dimension(app.screenSize.width/2, app.screenSize.height/10));

        PlayAreaConstraints.setMaximumSize(new Dimension( app.screenSize.width, app.screenSize.height * 9 / 10 ) );
        PlayArea.setSize(new Dimension( app.screenSize.width, PlayArea.getHeight() * 9 / 10 ) );

        ColorCountSelect.setMinimumSize(new Dimension(app.screenSize.width, 50));

        //set text algnment of (high)score:
        Score.setVerticalAlignment(SwingConstants.CENTER);
        Score.setHorizontalAlignment(SwingConstants.CENTER);
        Highscore.setVerticalAlignment(SwingConstants.CENTER);
        Highscore.setHorizontalAlignment(SwingConstants.CENTER);
        
        //adding components to parent components:
        RootFrame.add(PlayAreaConstraints, BorderLayout.CENTER);
                PlayAreaConstraints.add(PlayArea, BorderLayout.CENTER);
                PlayAreaConstraints.add(Scoreboard, BorderLayout.NORTH);
                    Scoreboard.add(Score);
                    Scoreboard.add(Highscore);
        RootFrame.add(GameBar, BorderLayout.NORTH);
        GameBar.add(ColorCountSelect); 
                for (int i = 0; i < ColorCount.length; ++i) {
                    ColorCountSelect.add(ColorCount[i]);
                    if (i < ColorCount.length - 1) ColorCountSelect.addSeparator();
                }

        PlayArea.setBackground(Color.LIGHT_GRAY);
        

        //initially write score and highscore:
        Score.setText("Score: " + Long.toString(app.getScore()));
        Highscore.setText("Highscore: " + Long.toString(app.getHighscore()));

        //override default close operation so the window event listener can 
        //handle it and do some cleanup before closing:
        RootFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        RootFrame.setLocationRelativeTo(null);

        // add event listeners to components:

        // show / hide dropdown menu with shortcut alt + c:
        ColorCountSelect.setMnemonic(KeyEvent.VK_C);

        //set shortcuts for colors used for each menu item:
        ColorCount[0].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
        ColorCount[1].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK));
        ColorCount[2].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK));
        ColorCount[3].setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.CTRL_MASK));

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
        RootFrame.setVisible(true);
    }
}