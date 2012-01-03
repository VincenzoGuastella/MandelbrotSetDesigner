package mandelbrotsetdesigner.util

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


trait MyLoggable {
		
	def initLogging(logFileName: String, levelName: String, consoleTrace: Boolean) {
		MyLoggable.initLogging(logFileName, levelName, consoleTrace)
  }

	def log(message: String, level: Level) {
		if (MyLoggable isLoggingOff) return
		
		if (MyLoggable isLoggingInitialized) {
			var classname = Utils.substringBefore(this.getClass().getName(), "$")
			MyLoggable.logger.logp(level, classname, " ", message)
		} 
		else println(message)		
	}
	
	def isLogEnabled : Boolean = {
		!MyLoggable.logger.getLevel.equals(Level.OFF)
	}

	def isLogFine : Boolean = {
		MyLoggable.logger.getLevel.intValue <= Level.FINE.intValue
	}

	
	def SEVERE: Level = Level.SEVERE

	def ERROR: Level = MyLevel.ERROR

	def WARNING: Level = Level.WARNING

	def VERBOSE: Level = MyLevel.VERBOSE

	def INFO: Level = Level.INFO

	def FINE: Level = Level.FINE

	def FINER: Level = Level.FINER

	def FINEST: Level = Level.FINEST
}

object  MyLoggable {
	
	var isLoggingInitialized = false
	var isLoggingOff = false
	var traceOnConsole = false
  var logger:java.util.logging.Logger = null
  var fh: FileHandler = null
  var ch: ConsoleHandler = null
  
  
 	def initLogging(logFileName: String,
 									levelName: String, 
 									consoleTrace: Boolean) {
    	
		logger = Logger.getLogger("MyLog")
  	
    try {

      // This block configure the logger with handler and formatter
      fh = new FileHandler(logFileName, true)
      logger.addHandler(fh);
      
      
      // Get the Level from the Name and set the logging level
      val myLoggingLevel = MyLevel.parse(levelName)
      logger.setLevel(myLoggingLevel);
      val formatter = new SimpleFormatter();
      fh.setFormatter(formatter);
 
      if (consoleTrace) {
	      ch = new ConsoleHandler();
	      ch.setFormatter(formatter);
	      logger.addHandler(ch);
      }

      isLoggingInitialized = true
      if (MyLoggable.logger.getLevel.equals(Level.OFF)) isLoggingOff = true
      
    } catch {
      case ex: IOException => {
      	println ("Caught exception initializing the log file: ")
      	println (ex.getClass)
      	println (ex.getMessage)
        sys.exit(0)
      }
	    case ex: Exception => {
      	println ("Caught exception initializing the logging")
        ex.printStackTrace();
        sys.exit(0)
	    }
    }
  }

}

class MyLevel(levelName: String, levelValue: Int) 
	extends Level(levelName, levelValue) {
}


object MyLevel {
  // Create new logging levels
  val ERROR: Level = new MyLevel("ERROR", 950);

  val VERBOSE: Level = new MyLevel("VERBOSE", 550);
  
  def parse(levelName: String) : Level = {
  	levelName match {
  		case "SEVERE" => Level.SEVERE 
  		case "ERROR" => MyLevel.ERROR 
  		case "WARNING" => Level.WARNING 
  		case "INFO" => Level.INFO 
  		case "VERBOSE" => MyLevel.VERBOSE 
  		case "FINE" => Level.FINE 
  		case "FINER" => Level.FINER 
  		case "FINEST" => Level.FINEST 
  		case "ALL" => Level.ALL 
  		case "OFF" => Level.OFF
  		case _ => throw new IllegalArgumentException("Logging level " + levelName + " unknown")
  	}
  	
  }
}