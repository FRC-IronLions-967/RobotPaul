import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
public class test2 {
    public static void main(String args[]) {
        String name = "735";
        String free = "735555";
        checkEquals(name, free);
        test2 test = new test2();
    }

    public test2() {
        JFrame frame = new JFrame("Test");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        JPanel panel = new JPanel();
        JTextField textField = new JTextField();
        JButton button = new JButton();
    }
}