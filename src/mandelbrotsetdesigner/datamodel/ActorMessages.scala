package mandelbrotsetdesigner

import scala.Array

sealed trait ActorMessages {}

/**
 * Message containing the calculated rgb
 *  value for one pixel   
 */
case class PixelColor(x: Int, y: Int, rgb: Int) extends ActorMessages {}

case class PixelsColors(pixels:Array[PixelColor]) extends ActorMessages {}

/**
 * Message sent to the function calculation thread 
 * to calculate the values for all the pixels for  
 * the line specified in the parameters.
 * 
 * @param p_y0 the starting line in the image for 
 *             which calculate the colors
 * @param p_y1 the last line in the image for 
 *             which calculate the colors
 */
case class CalculateLines(p_y0: Int, p_y1: Int) extends ActorMessages {}

case object Completed extends ActorMessages {}

case object Stop extends ActorMessages {}