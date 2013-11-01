import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Interpreter_old
{
   private String language;
   private HashMap  scope;


   /* Constructors */ 
      public Interpreter () {
         this("BareBones");
      }
      public Interpreter (String lang) {
         this.language = lang.toLowerCase();
         this.scope = new HashMap();
      }
   /* End of Constructors */


   /* Accessors */
      /** get:scope - Get the a HashMap of the variables */
      public HashMap getScope() {
         return this.scope;
      }
   /* End of Accessors */


   /** Evaluate the given code in the context of the current Interpreter Object */
   public void evaluate (String code)
   {
      //Lookup.nextStatement return the position where the next statement begins OR -1 on failure (no more statements)

      //for (int curr = 0, next = 0; (next = Lookup.nextStatement(code, curr)) > 0; curr = next)
         //this.evaluate(code.substring(curr, next));

      //if .nextStatement returned -1
      //execute the unhandled part of the code (curr->end)
      ///Lookup.word
      Pattern pattern = Pattern.compile(Command.incr_pattern);
      Matcher matcher = pattern.matcher(code);

      if(matcher.find())
      {
         Command.incr_action(matcher.group(1), this.getScope());
      }
   }


   public static void main (String[] args)
   {
      String filename = "barebone/sample 1.bbs";
      String file = (new File(filename)).content();

      Interpreter I = new Interpreter ("BareBones");
      I.evaluate (file);

      //Print the evaluated variables
      {
         Iterator i = I.getScope().entrySet().iterator();

         while(i.hasNext()) {
            Map.Entry me = (Map.Entry)i.next();

            System.out.print(me.getKey() + ": ");
            System.out.print(me.getValue() + "\n");
         }
      }
   }
}

/* Helper Class */

class Command {
   public static String incr_name = "increment";
   public static String incr_pattern = "incr ([a-zA-Z_].*);";
   public static void incr_action (String a, HashMap scope)
   {
      if (scope.containsKey(a))
      {
         scope.put(a, scope.get(a)++);
      }
      else
      {
         //System.err.print("Fatal error on line "+Supervisor.currentLine()+":");
         System.err.print("   Undefined variable "+a+".");
      }
   }


   public static String decr_name = "decrement";
   public static String decr_pattern = "decr ([a-zA-Z_].*);";
   public static int decr_action (Integer a)
   {
      return --a;
   }


   public static String clear_name = "clear";
   public static String clear_pattern = "clear ([a-zA-Z_].*);";
   public static int clear_action (Integer a)
   {
      return 0;
   }
}

/*
class Command {
   public static Object[] commands;
   commands.push(
      {
         name: "increment",
         pattern: "incr (.+);",
         action: method()
   )
   //incr, decr, while, clear, not
}
*/

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
      } catch (Exception e) { System.err.println("File ("+this.address+") not found!"); }

      //Initialize
      BufferedReader bfr = new BufferedReader( fl );
      StringBuffer sb = new StringBuffer();
      String ln;

      //Load contents in the StringBuffer
      try {
         while ((ln = bfr.readLine()) != null)
            sb.append(ln+"\n");

         bfr.close();
      } catch (Exception e) {}


      //Convert to String, store and return
      this._content = sb.toString();
      return this._content;
   }
}