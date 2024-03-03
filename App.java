import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.*;

public class App{

    // local variables off app:

    private int score = 0;
    private int highscore = 0;

    private Dimension screenSize = new Dimension(800,600);

    //getters and setters:

    public int getScore() {
        return this.score;
    }
    public int getHighscore() {
        return this.highscore;
    }



    //other methods:

    public static void main(String[] args) {

        App app = new App();
            //create and customize main window:
        JFrame RootFrame = new JFrame("Same Game");
        JPanel Scoreboard = new JPanel();
            JLabel Score = new JLabel();
            JLabel Highscore = new JLabel();
        JPanel PlayArea = new JPanel();

        RootFrame.setVisible(true);
        Scoreboard.setBackground(Color.ORANGE);
        Score.setBackground(Color.RED);
        Highscore.setBackground(Color.BLUE);
        PlayArea.setBackground(Color.GREEN);

        //set layouts of components:
        RootFrame.setLayout(new BorderLayout());
        Scoreboard.setLayout(new BoxLayout(Scoreboard, BoxLayout.X_AXIS));
        PlayArea.setLayout(new GridLayout());

        // intitial size settings of components:
        RootFrame.setSize(800, 600);
        Score.setPreferredSize(new Dimension(app.screenSize.width/2, app.screenSize.height/10));
        Highscore.setPreferredSize(new Dimension(app.screenSize.width/2, app.screenSize.height/10));
        System.out.println(app.screenSize.width/2 + " " + app.screenSize.height/10);

        //set text algnment of (high)score:
        Score.setVerticalAlignment(SwingConstants.CENTER);
        Score.setHorizontalAlignment(SwingConstants.CENTER);
        Highscore.setVerticalAlignment(SwingConstants.CENTER);
        Highscore.setHorizontalAlignment(SwingConstants.CENTER);
        
        //adding components to parent components:
        RootFrame.add(Scoreboard, BorderLayout.NORTH);
            Scoreboard.add(Score);
            Scoreboard.add(Highscore);
            Scoreboard.add(Box.createVerticalGlue());

        RootFrame.add(PlayArea, BorderLayout.CENTER);
        

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
    }
}