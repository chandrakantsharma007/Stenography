import java.io.*;

class FRead
{
 public static void main(String args[])
 {
  try
  {
    //open a file for reading
    //File must exist,otherwise open will fail
    //File will be opened in binary mode
    FileInputStream fin = new FileInputStream("f:/a.txt");  
 

    //read() 
    //* fetches one byte of data from the source.
    //* returns the ASCII value of the read byte.
    //* advances the file read pointer to the next position.
    //* returns -1 at EOF.

    int x;
    while((x = fin.read()) != -1)
     System.out.write(x);   
 
    System.out.flush();   
    //free the resource
    fin.close();
  }
  catch(Exception ex)
  {
    System.out.println(ex);
  }
 }//main
}//FRead