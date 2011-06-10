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
import scala.actors.Actor._
import scala.collection.immutable.StringOps
import scala.collection.mutable.ListBuffer

import java.awt.image.BufferedImage
import java.awt.{Toolkit, Color, Graphics}
import javax.swing.JPopupMenu


object MandelbrotSetDesigner extends SimpleSwingApplication with GuiFramework 
														 with Config with GuiMenu with PopUpMenu {
	
	var threadsStatusFlag = 0;
	var waitTime = MAX_ITERATIONS / 4
	
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
       	
       	if (repaintAll)
       		tryCatch(this) { calculatePixels(img) } 
       	
       	g.drawImage(img, null, 0, 0)        
      }

    }
	}
	
	def calculatePixels(img: BufferedImage) {
   	var timeCheck = System.currentTimeMillis()
   	log("Calling drawImage", INFO)
		
    val imgDrawer = new ImageDrawer(img)   	    
    var pixelsCalculation = new PixelsCalculation(imgDrawer: ImageDrawer)

   	startDrawingThreads(imgDrawer, pixelsCalculation)
		  
		waitCalculationCompleted(pixelsCalculation)
    waitForDrawCompleted(imgDrawer)

    timeCheck = System.currentTimeMillis() - timeCheck
   	log("drawImage completed. Time taken: " + timeCheck, INFO)
   	
   	repaintAll = false
	}
		
	/**
	 * startDrawerThread(img: BufferedImage)
	 * Creates the  image drawer thread. The image drawer 
	 * thread collects the results from the calculation 
	 * threads and sets the color of each pixel
	 */
	def startDrawingThreads(imgDrawer: ImageDrawer, pixelsCalculation: PixelsCalculation) {
   	try {

   		imgDrawer.start()
   		pixelsCalculation.start()
	    log("Threads Started", VERBOSE)	
	
	    return imgDrawer			
		} catch {
			case e: Exception => {
				log("Caught exception starting the Drawing Threads", SEVERE)
				log(e.toString, SEVERE)
				log(" " + e.getStackTraceString, SEVERE)
				throw e
			}
		}
	}
		
	def waitCalculationCompleted(pixelsCalculation: PixelsCalculation) {
		log("Wait for Calculation to be Completed", VERBOSE)

		while (threadsStatusFlag == 0) {
			Thread.sleep(waitTime)
		}	 		
		log("pixels Calculation Completed", VERBOSE)
	}
	
	def waitForDrawCompleted(imgDrawer: ImageDrawer) {
		while(imgDrawer.mboxSize > 0){			
			Thread.sleep(waitTime)
		}
	  log("Stopping imgDrawer", VERBOSE)
		imgDrawer ! Stop
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
 
//    exit(0); Here the compiler went mad. Now I have to use the following
    exit(0.asInstanceOf[AnyRef]);
  }
	
}