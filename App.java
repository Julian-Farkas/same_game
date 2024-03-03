import javax.swing.*;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.*;

public class App{

    public static void main(String[] args) {

        //create and customize main window:
        JFrame RootFrame = new JFrame("Same Game");
        JPanel Scoreboard = new JPanel();
        JPanel PlayArea = new JPanel();

        RootFrame.setSize(800, 600);
        RootFrame.setVisible(true);

        //set layouts of components:
        RootFrame.setLayout(new BorderLayout());
        PlayArea.setLayout(new GridLayout());
        
        //adding components to parent components:
        RootFrame.add(Scoreboard, BorderLayout.NORTH);
        RootFrame.add(PlayArea, BorderLayout.CENTER);

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