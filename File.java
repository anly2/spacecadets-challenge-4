package challenge5;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

/* Helper Class */
class File
{
   protected String address;
   protected String _content;

   public File (String address)
   {
      this.address = address;
      this._content = null;
   }

   /** Alias for .content() */
   public String contents () {
      return this.content();
   }
   /** Get the contents of the file "selected" by this object as String*/
   public String content ()
   {
      //Use the "cached" value if available
      if (this._content != null)
         return this._content;


      //Initialize the filereader
      FileReader fl = null;

      //Load file
      try {
         fl = new FileReader(this.address); // NOT_FOUND Exception
      } catch (FileNotFoundException e) {
    	  System.err.println("File ("+this.address+") not found!");
    	  System.exit(1);
      }

      //Initialize
      BufferedReader bfr = new BufferedReader( fl );
      StringBuffer sb = new StringBuffer();
      String ln;

      //Load contents in the StringBuffer
      try {
         while ((ln = bfr.readLine()) != null)
            sb.append(ln+"\n");

         bfr.close();
      } catch (Exception e) {
    	  System.exit(2);
      }


      //Convert to String, store and return
      this._content = sb.toString();
      return this._content;
   }
}