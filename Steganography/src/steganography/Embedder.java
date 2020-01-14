package steganography;

import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.ImageIO;

public class Embedder 
{
  private String toEmbed;
  private String vessel;
  private String trgtFile;
  private SecurityManager smgr;
  
  Embedder(String password, String toEmbed, String vessel, String trgtFile) throws Exception
  {
    //test for existence
    File f1 = new File(toEmbed);
    if(!f1.exists())
      throw new Exception(toEmbed + " doesnt exists");

    File f2 = new File(vessel);
    if(!f2.exists())
      throw new Exception(vessel + " doesnt exists");

    smgr = new SecurityManager(password);
    this.toEmbed = toEmbed;
    this.vessel = vessel;
    this.trgtFile = trgtFile;
  }//Embedder
  
  
  void embed() throws Exception
  {
    //load the vessel image in memory
    File fileVessel = new File(vessel);
    BufferedImage buffVessel = ImageIO.read(fileVessel);
    
    //capacity check
    int w, h, tot;
    w = buffVessel.getWidth();
    h = buffVessel.getHeight();
    tot = w*h;
    File fileToEmbed = new File(toEmbed);
    if( tot < fileToEmbed.length() + HeaderManager.HEADER_LENGTH)
      throw new Exception("Embedding capacity of " + vessel + " is less than the size of " + toEmbed);
    
    //Create the header
    String hdr = HeaderManager.formHeader(fileToEmbed.getName(), fileToEmbed.length());
    
    //get the raster (pixel matrix)
    WritableRaster wrstr = buffVessel.getRaster();
    //open the file to embed
    FileInputStream srcFile = new FileInputStream(fileToEmbed);

    int x, y;
    int r, g, b;
    int arr[], result[];
    int cnt = 0;
    int data;
    int flag = smgr.getPermutation();
    boolean keepEmbedding = true; 
    //embedding
    for(y =0 ; y < h && keepEmbedding; y++)
    {
      for(x =0 ; x < w ; x++)
      {
         //per pixel 
         r = wrstr.getSample(x, y, 0);//red band
         g = wrstr.getSample(x, y, 1);//green band
         b = wrstr.getSample(x, y, 2);//blue band
        
         if(cnt < HeaderManager.HEADER_LENGTH)
         {//embed header
           data = hdr.charAt(cnt);
         }
         else
         {//embed file content
           data = srcFile.read();
           if(data == -1)
           {//EOF
             keepEmbedding = false;
             srcFile.close();
             break;
           }
           data = smgr.primaryCrypto(data);
         }
         
         arr = ByteProcessor.slice(data, flag);
         result = ByteProcessor.merge(r,g,b,arr,flag);
         
         //update the raster
         wrstr.setSample(x, y, 0, result[0]);
         wrstr.setSample(x, y, 1, result[1]);
         wrstr.setSample(x, y, 2, result[2]);
         
         cnt++;
         flag = (flag+1)%3+1;
      }//for(x
      
    }//for(y
    
    //update the raster in buffered image
    ImageIO.write(buffVessel, "PNG", new File(trgtFile));
    
  }//embed
}
