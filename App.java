import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;


public class App {

    // local variables off app:

    private int score = 0;
    private int highscore = 0;

    //array for color creation:
    private final String[] colors = {"0xffffff", "0xff0000", "0x00ff00", "0x0000ff", "0xff8800", "0xaa0066"}; 

    // variables for play area:
    protected int[][] area = new int[10][10];
    protected JPanel[][] Area = new JPanel[10][10];

    private Dimension screenSize = new Dimension(800,600);

    //getters and setters:

    public int getScore() {
        return this.score;
    }
    public int getHighscore() {
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
    public void fillPlayArea (JPanel RootPanel, int[][] area, JPanel[][] PlayArea) {

        for (int row = 0; row < 10; ++row) {
            for (int col = 0; col < 10; ++col) {
                area[row][col] = (int) (Math.random() * 5 + 1);
            }
        }
        draw(RootPanel, area, PlayArea);
    }

    //function to paint the play area:
    public void draw (JPanel RootPanel, int[][] area, JPanel[][] PlayArea) {

        Color cellColor = null;
        
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
                Area[row][col].setName( Integer.toString(row) + Integer.toString(col) );

                RootPanel.add(Area[row][col]);
            }
        }
    }

    public void checkNeighbors (String name) {
        System.out.println(name);
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
        RootFrame.setSize(800, 600);
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
        Score.setText(Integer.toString(app.getScore()));
        Highscore.setText(Integer.toString(app.getHighscore()));

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
        app.fillPlayArea(PlayArea, app.area, app.Area);
        RootFrame.repaint();

        for (int row = 0; row < 10; ++row) {
            for (int col = 0; col < 10; ++col) {
                app.Area[row][col].addMouseListener( new MouseAdapter() {
                    public void mousePressed( MouseEvent ev) {
                        if (ev.getSource() instanceof JPanel) {
                            app.checkNeighbors( ev.getComponent().getName() );
                        }
                    }
                });
            }
        }
    }
}