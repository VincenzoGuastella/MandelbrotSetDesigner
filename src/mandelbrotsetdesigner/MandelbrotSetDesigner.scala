package mandelbrotsetdesigner

import mandelbrotsetdesigner.util.Config
import mandelbrotsetdesigner.datamodel._
import mandelbrotsetdesigner.guicomponents.GuiMenu
import mandelbrotsetdesigner.guicomponents.PopUpMenu
import mandelbrotsetdesigner.guicomponents.GuiFramework
import mandelbrotsetdesigner.guicomponents.ColorDialog
import mandelbrotsetdesigner.guicomponents.RepaintAllEvent
import swing._

import scala.swing.event.Event

import scala.xml._
import scala.collection.immutable.StringOps
import scala.collection.mutable.ListBuffer

import akka.actor.Actor._

import java.awt.image.BufferedImage
import java.awt.{Toolkit, Color, Graphics}
import javax.swing.JPopupMenu
import java.util.concurrent.CountDownLatch;


object MandelbrotSetDesigner extends SimpleSwingApplication with GuiFramework 
														 with Config with GuiMenu with PopUpMenu {
		
	override def startup(args: Array[String]) {
		var configFilName = parseInputArgs(args)
		if (configFilName.equals("printUsage")) printUsage
		parseConfigFile(configFilName)
		
		super.startup(args) 
	}

	def top = new MainFrame {

		title = "A Mandelbrot Set"
		mainFrame = this
    preferredSize_=(new Dimension(WIDTH, HEIGHT + 25)) 
 
    menuBar = addGuiMenu(contents)
		
		contents = new Panel {
      var img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB)
      this.peer.setDoubleBuffered(true)
      
      listenTo(mouse clicks)   
	  	reactions += {
      	case e : Event => handleMouseEvent(e)
	    }
            
      override def paintComponent(g : Graphics2D) : Unit = {
       	super.paintComponent(g)
       	
       	if (repaintAll) {
       		tryCatch(this) { calculatePixels(img) } 
       		repaintAll = false
       	}
       	
       	g.drawImage(img, null, 0, 0)   
      }
    }
	}
	
	def calculatePixels(img: BufferedImage) {
   	var timeCheck = System.currentTimeMillis()
   	log("Calling image drawer", INFO)
   	try {
	    val pixelsCalculation = actorOf(new PixelsCalculation(img)).start()
	    
	  	(pixelsCalculation ? "CalcPixelsAndDraw").get
	  	
	  	timeCheck = System.currentTimeMillis() - timeCheck
	  	log("Image drawer completed. Time taken: " + timeCheck, INFO)   	
		} catch {
			case e: Exception => {
				log("Image drawer failed with exception", SEVERE)
				log(e.toString, SEVERE)
				log(" " + e.getStackTraceString, SEVERE)
				throw e
			}
		}
	}
				
	
	def parseInputArgs(args: Array[String]):String = {
		var returnval = "printUsage"
    
    for (i <- 0 until args.length){
      if((args(i).equalsIgnoreCase("--properties")) ||(args(i).equals("-p"))){
        if(args(i+1)endsWith(".xml")){
          returnval =  args(i+1)
        }
      }
    }
		returnval
  }


	def printUsage {
    println();
    println("Usage: JobProcessor -h -p propertiesFile");
    println("WHERE:");
    println("--properties|-p followed by a properties file, specifies the XML file containing the configuration");    
    println("-h (optional) Prints this help output then exits");    
 
    sys.exit();
  }
	
}