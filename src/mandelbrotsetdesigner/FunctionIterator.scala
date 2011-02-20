package mandelbrotsetdesigner

import mandelbrotsetdesigner.util.Config
import mandelbrotsetdesigner.util.MyLoggable

import scala.actors.Actor._

class FunctionIterator(p_y0: Int, p_y1: Int, 
											 imageDrawer: ImageDrawer) extends MyLoggable with Config { 
	
	var status = "Initial"
	log("FunctionIterator constructor. Range: y0[" + p_y0 + "]" + " y1[" + p_y1 + "]", FINE)  
 	val calculator = actor { calculatePoint() }
  
  def calculatePoint(): Unit = { 
		react {

			try {				
				log("Starting functionIterator thread. Range: y0[" + p_y0 + "]" + " y1[" + p_y1 + "]", FINER)  
				status = "Started"
					
		  	for(p_x <- 0 until WIDTH; p_y <- p_y0 to p_y1) {
		  		val x = START_X + (INCREMENT * p_x)
			  	val y = START_Y + (INCREMENT * p_y)
			  	
				  val it: Int = getIterations(0, 0, x, y, 0)
		      log("getIterations completed:" + it, FINEST)  
				  
					val rgb = colors.findColor(it, 0, 0)		  	
					log("Color selected:" + rgb, FINEST)  
					
				  imageDrawer ! (new PixelColor(p_x, p_y, rgb))
		  	}
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
										x_0:Double, y_0:Double, iterations: Int):Int = {
	  val newX = x*x - y*y + x_0
	  val newY = 2*x*y + y_0
	  if (iterations < MAX_ITERATIONS && newX*newX + newY*newY <= 4)
			getIterations(newX, newY, x_0, y_0, iterations+1)
		else iterations
	}

}