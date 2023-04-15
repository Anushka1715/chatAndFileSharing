import javax.imageio.IIOException;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Server {
    static ArrayList<MYFile> myFiles = new ArrayList<>();
    public static void main(String[] args) throws IOException {

        int fileId = 0;

        JFrame jFrame = new JFrame("Server");
        jFrame.setSize(400,400);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(),BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel jPanel = new JPanel();
        jPanel.setLayout(new BoxLayout(jPanel,BoxLayout.Y_AXIS));

        JScrollPane jScrollPane = new JScrollPane(jPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JLabel jlTitle = new JLabel("File Receiver");
        jlTitle.setFont(new Font("Arial",Font.BOLD,25));
        jlTitle.setBorder(new EmptyBorder(20,0,10,0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        jFrame.add(jlTitle);
        jFrame.add(jScrollPane);
        jFrame.setVisible(true);

        ServerSocket ss = new ServerSocket(1234);

        while(true){
            try{
                Socket clientSocket = ss.accept();

                DataInputStream DiS = new DataInputStream(clientSocket.getInputStream());

                int fileNameLength = DiS.readInt();

                if(fileNameLength > 0){
                    byte[] fileNameBytes = new byte[fileNameLength];
                    DiS.readFully(fileNameBytes,0,fileNameBytes.length);
                    String fileName = new String(fileNameBytes);

                    int fileContentLength = DiS.readInt();

                    if(fileContentLength > 0) {
                        byte[] fileContentBytes = new byte[fileContentLength];
                        DiS.readFully(fileContentBytes, 0, fileContentLength);

                        JPanel jpFileRow = new JPanel();
                        jpFileRow.setLayout(new BoxLayout(jpFileRow, BoxLayout.Y_AXIS));

                        JLabel jlFileName = new JLabel(fileName);
                        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
                        jlFileName.setBorder(new EmptyBorder(10, 0, 10, 0));
                        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

                        if (getFileExtension(fileName).equalsIgnoreCase("txt")) {
                            jpFileRow.setName(String.valueOf(fileId));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);
                            jFrame.validate();
                        } else{
                            jpFileRow.setName(String.valueOf(fileId));
                            jpFileRow.addMouseListener(getMyMouseListener());

                            jpFileRow.add(jlFileName);
                            jPanel.add(jpFileRow);

                            jFrame.validate();
                        }

                        myFiles.add(new MYFile(fileId,fileName,fileContentBytes,getFileExtension(fileName)));

                        fileId++;
                    }

                }
                } catch (IOException error){
                error.printStackTrace();
            }
            }
        }

        public static MouseListener getMyMouseListener(){
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JPanel jPanel = (JPanel) e.getSource();
                int fileId = Integer.parseInt(jPanel.getName());

                for(MYFile myFile: myFiles){
                    if(myFile.getId() == fileId){
                        JFrame jfPreview = createFrame(myFile.getName(),myFile.getData(),myFile.getFileExtension());
                        jfPreview.setVisible(true);
                    }
                }
            }
        };
        }

        public static JFrame createFrame(String fileName,byte[] fileData,String fileExtension) {
            JFrame jFrame = new JFrame("File Downloader");
            jFrame.setSize(400, 400);

            JPanel jPanel = new JPanel();
            jPanel.setLayout(new BoxLayout(jPanel, BoxLayout.Y_AXIS));

            JLabel jlTitle = new JLabel("File Downloader");
            jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
            jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
            jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));

            JLabel jPrompt = new JLabel("Are you sure you want to download" + fileName);
            jPrompt.setFont(new Font("Arial", Font.BOLD, 20));
            jPrompt.setBorder(new EmptyBorder(20, 0, 10, 0));
            jPrompt.setAlignmentX(Component.CENTER_ALIGNMENT);

            JButton jbYes = new JButton("Yes");
            jbYes.setPreferredSize(new Dimension(150, 75));
            jbYes.setFont(new Font("Arial", Font.BOLD, 20));

            JButton jbNo = new JButton("No");
            jbNo.setPreferredSize(new Dimension(150, 75));
            jbNo.setFont(new Font("Arial", Font.BOLD, 20));

            JLabel jlFileContent = new JLabel();
            jlFileContent.setAlignmentX(Component.CENTER_ALIGNMENT);

            JPanel jpButtons = new JPanel();
            jpButtons.setBorder(new EmptyBorder(20, 0, 10, 0));
            jpButtons.add(jbYes);
            jpButtons.add(jbNo);

            if (fileExtension.equalsIgnoreCase("txt")) {
                jlFileContent.setText("<html>" + new String(fileData) + "</html>");
            } else {
                jlFileContent.setIcon(new ImageIcon(fileData));
            }

            jbYes.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    File fileToDownload = new File(fileName);

                    try{
                        FileOutputStream Fout = new FileOutputStream(fileToDownload);

                        Fout.write(fileData);
                        Fout.close();

                        jFrame.dispose();
                    }catch (IOException error){
                        error.printStackTrace();
                    }
                }
            });

            jbNo.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    jFrame.dispose();
                }
            });

            jPanel.add(jlTitle);
            jPanel.add(jPrompt);
            jPanel.add(jlFileContent);
            jPanel.add(jpButtons);

            jFrame.add(jPanel);

            return jFrame;
        }


        public static String getFileExtension(String fileName){
            int i = fileName.lastIndexOf('.');

            if(i>0){
                return fileName.substring(i+1);
            }
            else{
                return "no extension found";
            }

    }
}
