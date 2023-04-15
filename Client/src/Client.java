import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Client {
    public static void main(String[] args){
         final File[] filetoSend = new File[1];
        JFrame jFrame = new JFrame("FileSharing Client");
        jFrame.setSize(450,450);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(),BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel jTitle = new JLabel("File Sender");
        jTitle.setFont(new Font("Arial",Font.BOLD,25));
        jTitle.setBorder(new EmptyBorder(20,0,10,0));
        jTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jFileName = new JLabel("Choose a file to send.");
        jFileName.setFont(new Font("Arial",Font.BOLD,20));
        jFileName.setBorder(new EmptyBorder(50,0,0,0));
        jFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButton = new JPanel();
        jpButton.setBorder(new EmptyBorder(75,0,10,0));

        JButton jbSendFile = new JButton("Send File");
        jbSendFile.setPreferredSize(new Dimension(150,75));
        jbSendFile.setFont(new Font("Arial",Font.BOLD,20));

        JButton jbChooseFile = new JButton("Choose File");
        jbChooseFile.setPreferredSize(new Dimension(150,75));
        jbChooseFile.setFont(new Font("Arial",Font.BOLD,20));

        jpButton.add(jbSendFile);
        jpButton.add(jbChooseFile);

        jbChooseFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser = new JFileChooser();
                jFileChooser.setDialogTitle("Choose a file to send");

                if(jFileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION){
                    filetoSend[0] = jFileChooser.getSelectedFile();
                    jFileName.setText("The File you want to send is: "+filetoSend[0].getName());
                }
            }
        });

        jbSendFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(filetoSend[0] == null){
                    jFileName.setText("Please choose a file first.");
                } else{
                    try {
                        FileInputStream fIs = new FileInputStream(filetoSend[0].getAbsolutePath());
                        Socket socket = new Socket("localhost", 1234);

                        DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

                        String FileName = filetoSend[0].getName();
                        byte[] fileNameBytes = FileName.getBytes();

                        byte[] fileContentBytes = new byte[(int) filetoSend[0].length()];
                        fIs.read(fileContentBytes);

                        dout.writeInt(fileNameBytes.length);
                        dout.write(fileNameBytes);

                        dout.writeInt(fileContentBytes.length);
                        dout.write(fileContentBytes);
                    } catch (IOException error){
                        error.printStackTrace();
                    }
                }
            }
        });

        jFrame.add(jTitle);
        jFrame.add(jFileName);
        jFrame.add(jpButton);
        jFrame.setVisible(true);
    }
}
