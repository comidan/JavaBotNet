package rat_client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
/**
 *
 * @author daniele
 * @param <T>
 */
public class FileManagement<T>
{
    private String fileName;

    public FileManagement(String fileName)
    {
        this.fileName=fileName;
    }
    
    Path getCurrentPath()
    {
        try 
        {
            return Paths.get(new File(".").getCanonicalPath());
        } 
        catch (IOException ex) 
        {
            return Paths.get("");
        }
    }
    
    String getFileDataCreation()
    {
        try 
        {
            BasicFileAttributes bfa=Files.readAttributes(Paths.get(getCurrentPath().toString()+
                    "\\"+fileName),BasicFileAttributes.class);
            return bfa.creationTime().toString();
        } 
        catch (IOException ex) 
        {
            return "";
        }
    }
    
    String getLastFileAccess()
    {
        try 
        {
            BasicFileAttributes bfa=Files.readAttributes(Paths.get(getCurrentPath().toString()+
                    "\\"+fileName),BasicFileAttributes.class);
            return bfa.lastAccessTime().toString();
        } 
        catch (IOException ex) 
        {
            return "";
        }
    }
    
    String getLastFileModifiedTime()
    {
        try 
        {
            BasicFileAttributes bfa=Files.readAttributes(Paths.get(getCurrentPath().toString()+
                    "\\"+fileName),BasicFileAttributes.class);
            return bfa.lastModifiedTime().toString();
        } 
        catch (IOException ex) 
        {
            return "";
        }
    }
    
    int getFileSize()
    {
        try 
        {
            BasicFileAttributes bfa=Files.readAttributes(Paths.get(getCurrentPath().toString()+
                    "\\"+fileName),BasicFileAttributes.class);
            return (int)bfa.size();
        } 
        catch (IOException ex) 
        {
            return 0;
        }
    }
    
    int getFileSizeAbsolutePath()
    {
        try 
        {
            BasicFileAttributes bfa=Files.readAttributes(Paths.get(fileName),BasicFileAttributes.class);
            return (int)bfa.size();
        } 
        catch (IOException ex) 
        {
            return 0;
        }
    }
    
    T[] uploadTArray()
    {
        ArrayList<T> tmp=new ArrayList<>();
        int i=0;
        try(ObjectInputStream ois=new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName)))) 
        { 
            while(true)
            {
                tmp.add((T)ois.readObject());
                i++;
            }
        }
        catch(EOFException exc)
        {
            System.out.println("Read complete!");
            T type=tmp.get(0); //get a T example so I won't get a fucking NullPointerException
            return tmp.toArray((T[]) Array.newInstance(type.getClass(),i)); //creaating new array istance of T type
        }
        catch (IOException ex) 
        {
            System.out.println("Something went wrong while reading up object buffered bytes...");
            return null;
        } 
        catch (ClassNotFoundException ex) 
        {
            System.out.println("Something went wrong while reading up object buffered bytes...");
            return null;
        }
            
    }

    boolean upload(T[] al)
    {
        ArrayList<T> tmp=new ArrayList<>();
        int i=0;
        try(ObjectInputStream ois=new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName))))
        {   
            while(true)
            {
                tmp.add((T)ois.readObject());
                i++;
            }
        }
        catch(EOFException exc)
        {
            System.out.println("Read complete!");
            T type=tmp.get(0); //get a T example so I won't get a fucking NullPointerException
            al=tmp.toArray((T[]) Array.newInstance(type.getClass(),i)); //creaating new array istance of T type
            return true;
        } 
        catch (ClassNotFoundException exc) 
        {
            return false;
        }
        catch(IOException exc)
        {
            System.out.println("Something went wrong while reading up object buffered bytes...");
            return false;
        }
        catch(NullPointerException exc)
        {
            return false;
        }   
    }
    
    boolean upload(T al)
    {
        try(ObjectInputStream ois=new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName))))
        {   
            while(true)
                al=(T)ois.readObject();
        }
        catch(EOFException exc)
        {
            System.out.println("Read complete!");
            return true;
        } 
        catch (ClassNotFoundException exc) 
        {
            System.out.println("Something went wrong while reading up object buffered bytes...");
            return false;
        }
        catch(IOException exc)
        {
            System.out.println("Something went wrong while reading up object buffered bytes...");
            return false;
        }
        catch(NullPointerException exc)
        {
            System.out.println("Something went wrong while reading up object buffered bytes...");
            return false;
        }   
    }
    
    T upload()
    {
        T al=null;
        try(ObjectInputStream ois=new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName))))
        {   
            while(true)
                al=(T)ois.readObject();
        }
        catch(EOFException exc)
        {
            System.out.println("Read complete!");
            return al;
        } 
        catch (ClassNotFoundException exc) 
        {
            System.out.println("Something went wrong while reading up object buffered bytes...");
            return al;
        }
        catch(IOException exc)
        {
            System.out.println("Something went wrong while reading up object buffered bytes...");
            return al;
        }
        catch(NullPointerException exc)
        {
            System.out.println("Something went wrong while reading up object buffered bytes...");
            return al;
        }   
    }
    
    boolean upload(T[] al,int size)
    {
           try(ObjectInputStream ois=new ObjectInputStream(new BufferedInputStream(new FileInputStream(fileName))))
           {
                for(int i=0;i<size;i++)
                    al[i]=(T)ois.readObject();               
                return true;
            } 
           catch (IOException ex) 
           {
               return false;
           } 
           catch (ClassNotFoundException ex) 
           {
               return false;
           }
    }

    boolean download(T[] al)
    {
        try(ObjectOutputStream oos=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName))))
        {
            for(int i=0;i<al.length;i++)
                oos.writeObject(al[i]);
            return true;
        }
        catch(EOFException exc)
        {
            System.out.println("Write complete!");
            return true;
        } 
        catch(IOException exc)
        {
            System.out.println("Something went wrong while writing down object buffered bytes...");
            return false;
        }
            
    }
    
    boolean download(T al)
    {
        try(ObjectOutputStream oos=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName))))
        {
                oos.writeObject(al);
            return true;
        }
        catch(EOFException exc)
        {
            System.out.println("Write complete!");
            return true;
        } 
        catch(IOException exc)
        {
            System.out.println("Something went wrong while writing down object buffered bytes...");
            return false;
        }
            
    }
}
