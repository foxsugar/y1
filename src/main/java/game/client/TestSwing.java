package game.client;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.util.Calendar;

/**
 * Created by SunXianping on 2016/6/15 0015.
 */
public class TestSwing extends JFrame{
    private JLabel jLabel = null;
    private JTextField jTextField = null;
    private JButton jButton = null;
    Client client = new Client();

    public TestSwing()
    {
        super();
        this.setSize(300, 200);
        this.getContentPane().setLayout(null);
        this.add(getJLabel(), null);
        this.add(getJTextField(), null);
        this.add(getJButton(), null);
        this.setTitle("HelloWorld");

        new Thread(client).start();

    }

    private javax.swing.JLabel getJLabel() {

        if(jLabel == null) {
            jLabel = new javax.swing.JLabel();
            jLabel.setBounds(34, 49, 53, 18);
            jLabel.setText("Name:");
        }
        return jLabel;
    }

    private javax.swing.JTextField getJTextField() {
        if(jTextField == null) {
            jTextField = new javax.swing.JTextField();
            jTextField.setBounds(96, 49, 160, 20);
        }
        return jTextField;
    }

    private javax.swing.JButton getJButton() {
        if(jButton == null) {
            jButton = new javax.swing.JButton();
            jButton.setBounds(103, 110, 71, 27);
            jButton.setText("OK");

            jButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    System.out.println(jTextField.getText());
                    client.sendMsg(jTextField.getText());
                }
            });
        }
        return jButton;
    }

    public static void main(String[] args)
    {

        TestSwing w = new TestSwing();
        w.setVisible(true);
        Calendar calendar = Calendar.getInstance();
        if (calendar.get(Calendar.DAY_OF_WEEK) == 0) {
            System.out.println("==============");
        }



    }
}
