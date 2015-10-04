package rat_server;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 *
 * @author Daniele
 */
public class Read {
    private InputStreamReader input;
    private BufferedReader keybInput;
    private String str;
    private double temp;
    
    public Read()
    {
       input=new InputStreamReader(System.in);
       keybInput=new BufferedReader(input);
    }
    
    public int readInt()
    {
      try
      {
       temp=Integer.parseInt(keybInput.readLine());
      }
      catch(NumberFormatException exc){
       temp=0;
      }
      catch(IOException exc)
      {
          temp=0;
      }
      return (int)temp;
    }
    
    public float readFloat()
    {
      try
      {
       temp=Float.parseFloat(keybInput.readLine());
      }
      catch(NumberFormatException exc){
       temp=0;
      }
      catch(IOException exc)
      {
          temp=0;
      }
      return (float)temp;
    }
    
    public double readDouble()
    {
      try
      {
       temp=Double.parseDouble(keybInput.readLine());
      }
      catch(NumberFormatException e){
       temp=0;
      }
      catch(IOException e)
      {
          temp=0;
      }
      return temp;
    }
    
    public String readString()
    {
      try
      {
       str=keybInput.readLine();
      }
      catch(IOException e)
      {
          return "";
      }
      return str;
    }
    
    public char readChar()
    {
        try
        {
            str=keybInput.readLine();
        }
        catch(IOException e)
        {
            return ' ';
        }
        return str.charAt(0);
    }
}
