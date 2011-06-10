package mandelbrotsetdesigner

import mandelbrotsetdesigner.util.Config
import mandelbrotsetdesigner.util.MyLoggable
import scala.actors.Actor._
import scala.actors.Actor

class FunctionIterator(p_y0: Int, p_y1: Int, imageDrawer: ImageDrawer, 
											 caller: Actor) extends MyLoggable with Config { 
	
	var status = "Initial"
	val maxIterations = MAX_ITERATIONS
	val increment = INCREMENT
	val startX = START_X
	val startY = START_Y
	val width = WIDTH
	
//	log("FunctionIterator constructor. Range: y0[" + p_y0 + "]" + " y1[" + p_y1 + "]", FINE)  
 	val calculator = actor { calculatePoint() }
  
  def calculatePoint(): Unit = { 
		react {

			try {				
//				log("Starting functionIterator thread. Range: y0[" + p_y0 + "]" + " y1[" + p_y1 + "]", FINER)  
				status = "Started"
					
		  	for(p_x <- 0 until width; p_y <- p_y0 to p_y1) {
		  		val x: Double = startX + (increment * p_x)
			  	val y: Double = startY + (increment * p_y)
			  	
				  val (it: Int, newX: Double, newY: Double) = getIterations(0, 0, x, y, 0)
//		      log("getIterations completed:" + it, FINEST)  
				  
					val rgb = colors.findColor(it, newX, newY, x, y)		  	
//					log("Color selected:" + rgb, FINEST)  
					
				  imageDrawer ! (new PixelColor(p_x, p_y, rgb))
		  	}
				caller ! new FICompleted(this)
				status = "Completed"
				exit()
			} catch {
					case e: Exception => {
					log("Caught exception calculating the iterations ", SEVERE)
					log(e.toString, SEVERE)
					log(" " + e.getStackTrace, SEVERE)
					//Better leave the rest of the pixels blank in these lines and go on
					status = "Completed"
					exit()
				}
			} 
      /* TO DO find out why the compiler want the return values
       * to be set in every block instead of accepting finally
			finally {	
				status = "Completed"
				exit()
			} */ 
		}
  }

  def getIterations(x:Double, 	y:Double, 
										x_0:Double, y_0:Double, iterations: Int):(Int, Double, Double) = {
	  val newX = x*x - y*y + x_0
	  val newY = 2*x*y + y_0
	  if (iterations < maxIterations && newX*newX + newY*newY <= 4)
			getIterations(newX, newY, x_0, y_0, iterations+1)
		else (iterations, newX, newY)
	}
  
  override def toString: String = "p_y0 [" + p_y0 + "]; p_y1 [" + p_y1 + "]"

  def y0: Int = p_y0
  def y1: Int = p_y1
  
  def equals(other: FunctionIterator): Boolean = {
		if (p_y0 == other.y0 && p_y1 == other.y1) true
		else false
  }
}

object FunctionIterator {
  
  def getZEscape (x:Double, 	y:Double, 
									x_0:Double, y_0:Double, counter: Int):(Double) = {
	  val newX = x*x - y*y + x_0
	  val newY = 2*x*y + y_0
	  if (counter > 2) getZEscape(newX, newY, x_0, y_0, counter + 1)
		else Math.sqrt(newX*newX + newY*newY)
	}

}