package tcp.client.view;
 
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
 
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import model.ObjectWrapper;
 
import model.User;
import tcp.client.control.ClientCtr;
 
public class LoginFrm extends JFrame implements ActionListener{
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private ClientCtr mySocket;
     
    public LoginFrm(ClientCtr socket){
        super("TCP Login MVC");     
        mySocket = socket;
         
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtPassword.setEchoChar('*');
        btnLogin = new JButton("Login");
         
        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        content.add(new JLabel("Username:"));
        content.add(txtUsername);
        content.add(new JLabel("Password:"));
        content.add(txtPassword);
        content.add(btnLogin);
        btnLogin.addActionListener(this);
                 
        this.setContentPane(content);
        this.pack();        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_LOGIN_USER, this));
    }
     
 
    public void actionPerformed(ActionEvent e) {
        if((e.getSource() instanceof JButton) && (((JButton)e.getSource()).equals(btnLogin))) {
            //pack the entity
            User user = new User();
            user.setUsername(txtUsername.getText());
            user.setPassword(txtPassword.getText());
            ObjectWrapper data = new ObjectWrapper();
            data.setData(user);
            data.setPerformative(ObjectWrapper.LOGIN_USER);
            //sending data
            mySocket.sendData(data);
             
            //receive data
           
        }
    }
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getData() instanceof User || data.getData() == null) {
            User user = (User) data.getData();
            if(user != null){
                mySocket.switchLoginState(user);
                JOptionPane.showMessageDialog(this, "Login succesfully!");
//                for(ObjectWrapper func: mySocket.getActiveFunction())
//                    if(func.getData().equals(this))
//                        mySocket.getActiveFunction().remove(func);
                this.dispose();
            }else {
                JOptionPane.showMessageDialog(this, "Username / Password isn't correct, or Account have been loged in!");
            }
        }
    }
}