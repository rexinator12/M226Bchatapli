import javax.swing.*;


public class ClientGUI extends JFrame{
    private JPanel Main;
    private JButton sendButton;
    private JTextField msg;
    private JTextPane screen;


    public ClientGUI() {
        // TODO: place custom component creation code here
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600,600);
        frame.add(Main);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

    }


    public static void main(String[] args){
        new ClientGUI();}}