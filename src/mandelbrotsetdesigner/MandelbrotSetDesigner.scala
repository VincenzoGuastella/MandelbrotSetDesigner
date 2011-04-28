package mandelbrotsetdesigner

import mandelbrotsetdesigner.util.Config
import mandelbrotsetdesigner.datamodel._
import mandelbrotsetdesigner.guicomponents.GuiMenu
import mandelbrotsetdesigner.guicomponents.GuiFramework
import mandelbrotsetdesigner.guicomponents.ColorDialog
import mandelbrotsetdesigner.guicomponents.RepaintAllEvent

import swing._
import java.awt.image.BufferedImage
import java.awt.{Toolkit, Color, Graphics}
import scala.xml._
import scala.collection.immutable.StringOps
import scala.collection.mutable.LinkedList



object MandelbrotSetDesigner extends SimpleSwingApplication with GuiFramework with Config with GuiMenu {
	
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
 
      override def paintComponent(g : Graphics2D) : Unit = {
       	super.paintComponent(g)
       	
       	if (repaintAll)
       		tryCatch(this) { calculatePixels(img) } 
       	
       	g.drawImage(img, null, 0, 0)        
//        Toolkit.getDefaultToolkit().sync
//        g.dispose 
      }
    }
	}
	
	def calculatePixels(img: BufferedImage) {
   	var timeCheck = System.currentTimeMillis()
   	log("Calling drawImage", INFO)
		
    val imgDrawer = startDrawerThread(img)
    var functionIterators = calcPixelColors(imgDrawer: ImageDrawer)
		  
		waitForAllThreadsCompleted(functionIterators)
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
	def startDrawerThread(img: BufferedImage):ImageDrawer = {
		try {
			val imgDrawer = new ImageDrawer(img)
			imgDrawer.start()
	    log("ImageDrawer thread Started", VERBOSE)	
	
	    return imgDrawer			
		} catch {
			case e: Exception => {
				log("Caught exception starting the ImageDrawer thread", SEVERE)
				log(e.toString, SEVERE)
				log(" " + e.getStackTraceString, SEVERE)
				throw e
			}
		}
	}

	/** 
	 * Creates the thread that calculates the color for each pixel.
	 * The number of threads will depend on the configured number
	 * lines (HEIGHT) of the image and the configured number of
	 * lines to be calculated by each thread
	 */
	def calcPixelColors(imgDrawer: ImageDrawer) :LinkedList[FunctionIterator] = {
		try {
	    var functionIterators = new LinkedList[FunctionIterator]
			
			var p_y , p_y0 = 0;
		  while(p_y < HEIGHT) {
		  	p_y0 = p_y
		  	p_y += LINES_PER_THREAD
		  	if (p_y >= HEIGHT) p_y = HEIGHT - 1
		  	functionIterators = createIterationThread(p_y0, p_y, imgDrawer, functionIterators)
		  	p_y += 1
		  }
		  return functionIterators
		  
		} catch {
			case e: Exception => {
				log("Caught exception starting the pixels calculation ", SEVERE)
				log(e.toString, SEVERE)
				log(" " + e.getStackTraceString, SEVERE)
				throw e
			}
		}
	}
	
	/**
	 * createIterationThread() creates a pixel calculation threads
	 * and adds it to the active threads list
	 * @param p_y0 the starting line in the image for 
	 *             which calculate the colors
	 * @param p_y1 the last line in the image for 
	 *             which calculate the colors
	 * @param imgDrawer the thread to which the 
	 *                  calculation result must be sent
	 * @param functionIterators the list holding the  
	 *                  				active threads
	 *             
	 * This method throttles the calculation threads.
	 * No more than MAX_THREADS are running concurrently.           
	 */
	def createIterationThread(p_y0: Int, p_y1: Int, imgDrawer: ImageDrawer,
														functionIterators: LinkedList[FunctionIterator])
				:LinkedList[FunctionIterator] = {
		
	  	if (functionIterators.length < MAX_THREADS) {
	  		log("Creating new actor. Range: y0[" + p_y0 + "]" + " y1[" + p_y1 + "]", FINE)
	  		var f = new FunctionIterator(p_y0, p_y1, imgDrawer)
	  		functionIterators :+ f
	  	}	else {
	  		Thread.sleep(60)
	  		createIterationThread(p_y0, p_y1, imgDrawer,
	  				functionIterators.filter{f: FunctionIterator => 
	  					!f.status.equalsIgnoreCase("Completed")})	
	  	}		
	}
	

	def waitForDrawCompleted(imgDrawer: ImageDrawer) {
		if (imgDrawer.mboxSize > 0){			
			Thread.sleep(100)
			waitForDrawCompleted(imgDrawer)
		}
	  log("Stopping imgDrawer", VERBOSE)
		imgDrawer ! Stop
	}
	
	
	def waitForAllThreadsCompleted(functionIterators: LinkedList[FunctionIterator]) {
		log("Wait for all threads to be completed", VERBOSE)
		if (functionIterators.length > 0) {
			Thread.sleep(1000)
			waitForAllThreadsCompleted(
				functionIterators.filter{f: FunctionIterator => 
	  			!f.status.equalsIgnoreCase("Completed")})
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

    exit(0);
  }
	
}