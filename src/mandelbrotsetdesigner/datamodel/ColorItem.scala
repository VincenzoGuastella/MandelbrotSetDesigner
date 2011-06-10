package mandelbrotsetdesigner.datamodel

import mandelbrotsetdesigner.ColorsList
import mandelbrotsetdesigner.FunctionIterator

import mandelbrotsetdesigner.util.Config
import mandelbrotsetdesigner.util.MyLoggable

import java.awt.Color

trait ColorItem  {
	var startRange: Int
  var endRange: Int
	
	
	def isInRange(iterations: Int): Boolean = {
	  return (iterations >= startRange && iterations <= endRange)
	}
	
	def getColor(iterations: Int, x: Double, y: Double, x_0: Double, y_0: Double): Int

	def getColorValue: Int
	def getGradientValue: Int
}	


case class SharpColorItem(color: Int, var startRange: Int, var endRange: Int) extends ColorItem {

	override def getColor(iterations: Int, x:Double, y:Double, x_0:Double, y_0:Double): Int = color
	
  override def getColorValue: Int = color
  override def getGradientValue: Int = color
}


/*
 * 
 */
case class SmoothedColorItem(val fromColor: Color, val toColor: Color, 
														 var startRange: Int, var endRange: Int) 
	extends ColorItem with Config with MyLoggable {

	val colorsPalette: List[Color] = ColorsList.getSmoothedColorArray(fromColor, toColor)
	
	val iterIncrement: Double = colorsPalette.size.toDouble / 50
	val smoothingFactor: Double = colorsPalette.size.toDouble / 300
	val paletteSizeMinus1: Int = colorsPalette.size - 1
	val lnEscapeRadius: Double = Math.log(ESCAPE_RADIUS)
	
  override def getColorValue: Int = fromColor.getRGB
  override def getGradientValue: Int = toColor.getRGB
	
	override def getColor(iterations: Int, x:Double, y:Double, x_0:Double, y_0:Double): Int = {
		
		val z = FunctionIterator.getZEscape(x, y, x_0, y_0, 0) //Calculates Z for 3 more iterations

		var ni: Double = iterations.toDouble + iterIncrement - Math.log(Math.log(z.abs)) / lnEscapeRadius

		var index = (ni * smoothingFactor).toInt 
  	if (index > paletteSizeMinus1) index = paletteSizeMinus1
		if (index < 0) index = 0
			
		return colorsPalette(index).getRGB
	}
}

case class MdbsColorItem(color: Int) extends ColorItem with Config {

	var startRange = MAX_ITERATIONS
	var endRange: Int = Int.MaxValue
	
	override def getColor(iterations: Int, x: Double, y: Double, x_0: Double, y_0: Double): Int = color
	
  override def getColorValue: Int = color
  override def getGradientValue: Int = color
}