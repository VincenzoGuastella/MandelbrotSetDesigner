package mandelbrotsetdesigner

import mandelbrotsetdesigner.util._
import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.LinkedList
import java.awt.image.BufferedImage
import java.awt.{Toolkit, Color, Graphics}
import scala.collection.mutable.ListBuffer

class PixelsCalculation(imgDrawer: ImageDrawer) 
	extends Actor with MyLoggable with Config {

	def act() {
		log("PixelsCalculation thread running", FINE)  
		
	  var functionIterators = new ListBuffer[FunctionIterator]
		
		var p_y , p_y0 = 0;
	  while(p_y < HEIGHT) {
	  	p_y0 = p_y
	  	p_y += LINES_PER_THREAD
	  	if (p_y >= HEIGHT) p_y = HEIGHT - 1
	  	createIterationThread(p_y0, p_y, imgDrawer, functionIterators)
	  	p_y += 1
	  }
		
	  waitForAllThreadsCompleted(functionIterators)
	  MandelbrotSetDesigner.threadsStatusFlag = 1
		log("PixelsCalculation thread Completed", VERBOSE)  

		exit()
	}

		
	/**
	 * createIterationThread() creates a pixel calculation threads
	 * and adds it to the active threads list
	 * The number of threads will depend on the MAX_THREADS configuration
	 * 
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
	private def createIterationThread(p_y0: Int, p_y1: Int, imgDrawer: ImageDrawer, 
														functionIterators: ListBuffer[FunctionIterator]){
		
  	if (functionIterators.length < MAX_THREADS) {
  		if (isLogFine) //Int to string has a performance impact here
  			log("Creating new actor. Range: y0[" + p_y0 + "]" + " y1[" + p_y1 + "]", FINE)
  		var f = new FunctionIterator(p_y0, p_y1, imgDrawer, this)
  		functionIterators.append(f)
  	}	else {
			receive {  
			  case fiCompleted: FICompleted => {
			  	functionIterators.remove(functionIterators.indexOf(fiCompleted.fi))
			  	createIterationThread(p_y0, p_y1, imgDrawer, functionIterators)
			  }
			}
  	}		
	}

	
	private def waitForAllThreadsCompleted(functionIterators: ListBuffer[FunctionIterator]) {
		while (functionIterators.length > 0) {
			receive {
			  case fiCompleted: FICompleted => {
			  	functionIterators.remove(functionIterators.indexOf(fiCompleted.fi))
			  }
			}
		}	  			
	}

}