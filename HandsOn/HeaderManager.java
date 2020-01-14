import java.io.File;

class HeaderManager
{
 final static int HEADER_LENGTH = 25;
 final static char SEPARATOR = '~';

 static String formHeader(String fname, int fsize) 
 {//Split : 16+1+8
   int forSize = 8;
   int forSeparator = 1;
   int forName = HEADER_LENGTH - forSize - forSeparator;

   //length process
   String fs= String.valueOf(fsize);//45309   
   while(fs.length() < forSize)
    fs = "#"+fs; //###45309

   //name process   
   File f = new File(fname);//f:/mydocs/qwerty.jpg
   fname= f.getName();//qwerty.jpg
   
   if(fname.length() > forName)
   {//trim (MapsOfArunachalPradesh.jpg)
     int start = fname.length() - forName;
     fname = fname.substring(start);
   }   
   else
   {//padding
     while(fname.length() < forName)
      fname = "#"+fname; //######qwerty.jpg
   }

   return fname + SEPARATOR + fs;

   /*

   String nameParts[] = fname.split(".");
   //nameParts[0] = "qwerty"
   //nameParts[1] = jpg

   int x, y;
   if(nameParts.length > 2)
   {//name had a dot 
     x = nameParts.length -2;
     y = nameParts.length -1;
   }
   else
   {
     x = 0; 
     y = 1;
   }
   
   //nameParts[x] : file name
   //nameParts[y]: file extension

   int remainingLength = forName - nameParts[y].length() -1;
    
   if(nameParts[x].length > remainingLength)
   {//trim
     nameParts[x] = nameParts[x].substring(0, remainingLength)
   }  
   else
   {//padding
     while(nameParts[x].length() < remainingLenght)
      nameParts[x] = "#"+nameParts[x]; //######qwerty

   }

  */
 }

 public static void main(String args[])
 {
   System.out.println(formHeader(args[0], Integer.parseInt(args[1])));   
 } 
}