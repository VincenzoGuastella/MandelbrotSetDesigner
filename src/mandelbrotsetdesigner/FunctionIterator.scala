package mandelbrotsetdesigner

import mandelbrotsetdesigner.datamodel._
import mandelbrotsetdesigner.util.Config
import mandelbrotsetdesigner.util.MyLoggable

import akka.actor.Actor

//import scala.actors.Actor._
//import scala.actors.Actor

class FunctionIterator extends Actor with MyLoggable with Config { 
	
	val maxIterations = MAX_ITERATIONS
	val increment = INCREMENT
	val startX = START_X
	val startY = START_Y
	val width = WIDTH
	
	private var zIterations:Int = 0
	private var zX:Double = 0.0
	private var zY:Double = 0.0
	
	log("FunctionIterator constructor called.", VERBOSE)  

	def receive = {
		case CalculateLines(p_y0, p_y1) => {
			calculatePixelsForLines(p_y0, p_y1) 
		}
	}


  private def calculatePixelsForLines(p_y0: Int, p_y1: Int): Unit = {
  	//Array size must be equal to the number of pixels calculated in the loop
		val pixels = new Array[PixelColor]((p_y1 - p_y0 + 1) * width)
		var i = 0
  	try {				
//				log("Starting functionIterator thread. Range: y0[" + p_y0 + "]" + " y1[" + p_y1 + "]", FINER)  
					
		  	for(p_x <- 0 until width; p_y <- p_y0 to p_y1) {
		  		val x: Double = startX + (increment * p_x.toDouble)
			  	val y: Double = startY + (increment * p_y.toDouble)
			  	
//			  	val(iterations:Int, newX:Double, newY:Double) = getIterations(x, y, x, y, 1)
			  	getIterations(x, y, x, y, 1)
//		      log("getIterations completed:" + it, FINEST)  
				  
//					val rgb = colors.findColor(iterations, x, y, x, y)		  	
					val rgb = colors.findColor(zIterations, zX, zY, x, y)		  	
//					log("Color selected:" + rgb, FINEST)  
					
					pixels(i) = new PixelColor(p_x, p_y, rgb)
		  		i += 1
		  	}
			  self reply (new PixelsColors(pixels)) //reply to the sender with the result
			} catch {
					case e: Exception => {
					log("Caught exception calculating the iterations ", SEVERE)
					log(e.toString, SEVERE)
					log(" " + e.getStackTrace, SEVERE)
					//Better leave the rest of the pixels blank in these lines and go on
				}
			} 
  }
  
  private def getIterations(x:Double, y:Double, x_0:Double, y_0:Double, 
  													iterations: Int) {
	  if (iterations < maxIterations && x*x + y*y <= 4)
			getIterations(x*x - y*y + x_0, x*y*2.0 + y_0, x_0, y_0, iterations+1)
		else {
			zIterations = iterations
			zX = x
			zY = y
		}
	}

/* Workaround for a small performance issue. When the return type is a
 * tuple of three parameters scala (v2.9.1) returns a generic tuple
 * and boxes the fields. To avoid boxing the method above returns unit
 * and sets some class parameters. Not nice, but works.
 */
//  private def getIterations(x:Double, y:Double, x_0:Double, y_0:Double, 
//  													iterations: Int):(Int, Double,Double) = {
//	  if (iterations < maxIterations && newX*newX + newY*newY <= 4)
//			getIterations(newX, newY, x_0, y_0, iterations+1)
//		else (iterations, x, y)
//	}

}

object FunctionIterator {
  
  def getZEscape (x:Double, 	y:Double, 
									x_0:Double, y_0:Double, iterations: Int):(Double) = {
	  if (iterations > 2)
			getZEscape(x*x - y*y + x_0, x*y*2.0 + y_0, x_0, y_0, iterations + 1)
		else scala.math.sqrt(x*x + y*y)
	}

}
