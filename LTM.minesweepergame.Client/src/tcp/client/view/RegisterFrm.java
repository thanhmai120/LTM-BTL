/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

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

/**
 *
 * @author Administrator
 */
public class RegisterFrm  extends JFrame implements ActionListener{
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtPasswordConfirm;
    private JTextField txtFullname;
    private JTextField txtEmail;
    private JButton btnRegister;
    private ClientCtr mySocket;

    public RegisterFrm(ClientCtr socket) {
        super("TCP Register MVC");     
        mySocket = socket;
         
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtPasswordConfirm = new JPasswordField(15);
        txtPassword.setEchoChar('*');
        txtPasswordConfirm.setEchoChar('*');
        txtFullname = new JTextField(15);
        txtEmail = new JTextField(15);
        btnRegister = new JButton("Register");
         
        JPanel content = new JPanel();
        content.setLayout(new FlowLayout());
        content.add(new JLabel("Username:"));
        content.add(txtUsername);
        content.add(new JLabel("Password:"));
        content.add(txtPassword);
        content.add(new JLabel("Retype Password:"));
        content.add(txtPasswordConfirm);
        content.add(new JLabel("Fullname:"));
        content.add(txtFullname);
        content.add(new JLabel("Email:"));
        content.add(txtEmail);
        content.add(btnRegister);
        
        btnRegister.addActionListener(this);
                 
        this.setContentPane(content);
        this.pack();        
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        mySocket.getActiveFunction().add(new ObjectWrapper(ObjectWrapper.REPLY_REGISTER_USER, this));
    }
    
    

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if((e.getSource() instanceof JButton) && (((JButton)e.getSource()).equals(btnRegister))) {
            //check password
            String password = txtPassword.getText();
            if(!password.equals(txtPasswordConfirm.getText())){
                JOptionPane.showMessageDialog(this, "Password confirm is wrong!");
                return;
            }
            //pack the entity
            User user = new User();
            user.setUsername(txtUsername.getText());
            user.setPassword(txtPassword.getText());
            user.setEmail(txtEmail.getText());
            user.setFullname(txtFullname.getText());
            user.setActive(true);
            user.setType("player");
            ObjectWrapper data = new ObjectWrapper();
            data.setData(user);
            data.setPerformative(ObjectWrapper.REGISTER_USER);
            //sending data
            mySocket.sendData(data);
             
            //receive data
            
        }
    }
    
    public void receivedDataProcessing(ObjectWrapper data) {
        if(data.getData().equals("ok")) {
            JOptionPane.showMessageDialog(this, "Register succesfully!");
            /*for(DataTO func: mySocket.getActiveFunction())
                if(func.getData().equals(this))
                    mySocket.getActiveFunction().remove(func);*/
            this.dispose();
        }
        else {
            JOptionPane.showMessageDialog(this, "Error when registering!");
        }
    }
}
