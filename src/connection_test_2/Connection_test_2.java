/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package connection_test_2;

import com.jtattoo.plaf.aero.AeroLookAndFeel;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author gayan.u
 */
public class Connection_test_2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            UIManager.setLookAndFeel(new AeroLookAndFeel());
            
          //  asdfghjk

            MainConnection m = new MainConnection();
            m.setLocationRelativeTo(null);
            m.setVisible(true);
            dfghjk

        } catch (UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Connection_test_2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
