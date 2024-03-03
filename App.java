import javax.swing.*;
import java.awt.event.*;

public class App{

    public static void main(String[] args) {

        //create and customize main window:
        JFrame RootFrame = new JFrame("Same Game");

        RootFrame.setSize(800, 600);
        RootFrame.setLayout(null);
        RootFrame.setVisible(true);

        //override default close operation so the window event listener can 
        //handle it and do some cleanup before closing:
        RootFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // add event listeners to elements:
        RootFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing (WindowEvent ev) {
                RootFrame.dispose();
                return;
            }
        });
        
    }
}