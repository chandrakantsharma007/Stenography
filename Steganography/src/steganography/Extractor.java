package steganography;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class Extractor 
{
  private String vessel;
  private String extractedFile;
  private SecurityManager smgr;
  private String trgtFolder;
  
  Extractor(String password, String vessel, String trgtFolder) throws Exception
  {
    //test for existence
    File f1 = new File(vessel);
    if(!f1.exists())
      throw new Exception(vessel + " doesnt exists");

    smgr = new SecurityManager(password);
    
    this.vessel = vessel;
    this.trgtFolder = trgtFolder;
  }//Embedder
  
  
  void extract() throws Exception
  {
    //load the vessel image in memory
    File fileVessel = new File(vessel);
    BufferedImage buffVessel = ImageIO.read(fileVessel);
    
    //get the raster (pixel matrix)
    Raster rstr = buffVessel.getData();
    
    int w, h;
    w = buffVessel.getWidth();
    h = buffVessel.getHeight();
    
    int x, y;
    int r, g, b;
    int arr[], result[];
    int cnt = 0;
    int data;
    int flag = smgr.getPermutation();
    int fileSize = 0 ;
    boolean keepExtracting = true; 
    String hdr = "";
    
    //target File
    FileOutputStream fout = null;
    
    //extraction
    for(y =0 ; y < h && keepExtracting; y++)
    {
      for(x =0 ; x < w ; x++)
      {
         //per pixel 
         r = rstr.getSample(x, y, 0);//red band
         g = rstr.getSample(x, y, 1);//green band
         b = rstr.getSample(x, y, 2);//blue band
        
         //extract the bits
         arr = ByteProcessor.extract(r, g, b, flag);
         //combine to form a byte
         data = ByteProcessor.combine(arr, flag);
         
         if(cnt < HeaderManager.HEADER_LENGTH)
         {//embed header
           hdr= hdr+ (char)data;
           
           if(cnt == HeaderManager.HEADER_LENGTH-1)
           {
             //we have the header
             //extract the file name
             extractedFile = HeaderManager.getFileName(hdr);
             fileSize = HeaderManager.getFileSize(hdr);
             System.out.println("FILESIZE : " + fileSize);
             //open the file for writing
             fout = new FileOutputStream(trgtFolder+ "/"+ extractedFile);
           }
         }
         else
         {//extract 
           data = smgr.primaryCrypto(data);
           fout.write(data);
           
           if(cnt == fileSize+ HeaderManager.HEADER_LENGTH)
           {//EOF
             System.out.println("***"+ cnt);
             keepExtracting = false;
             fout.close();
             break;
           }
           
         }
         
         cnt++;
         flag = (flag+1)%3+1;
      }//for(x
      
    }//for(y
    
  }//extract
}
