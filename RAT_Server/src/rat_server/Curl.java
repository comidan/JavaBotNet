/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rat_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author s4mick
 */
public class Curl {
      URL url;
   Curl(String site){
          try {
              url=new URL(site);
          } catch (MalformedURLException ex) { System.out.println("sito non uniforme"); }
   }
   
  String toSite()
   {
   String result="";
   try {
            URLConnection conn = url.openConnection();
            
            // open the stream and put it into BufferedReader
            BufferedReader br = new BufferedReader(
                               new InputStreamReader(conn.getInputStream()));

            String inputLine;
            while ((inputLine = br.readLine()) != null) {
                result+=inputLine;    
                System.out.println(inputLine);
            }
            br.close();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
          return result;
   }
}
    

