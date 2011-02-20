package mandelbrotsetdesigner.datamodel

import mandelbrotsetdesigner.util.Config

trait ColorItem  {
	var startRange: Int
  var endRange: Int
	
	
	def isInRange(iterations: Int): Boolean = {
	  return (iterations >= startRange && iterations <= endRange)
	}
	
	def getColor(iterations: Int): Int

	def getColorValue: Int
}	


case class SharpColorItem(color: Int, var startRange: Int, var endRange: Int) extends ColorItem {

	override def getColor(iterations: Int): Int = color
	
  override def getColorValue: Int = color
}


/*
 * TO DO
 */
case class SmoothedColorItem(color: Int, var startRange: Int, var endRange: Int) extends ColorItem {

  override def getColorValue: Int = color


	def getColor(iterations: Int): Int = 0
	
}

case class MdbsColorItem(color: Int) extends ColorItem with Config {

	var startRange = MAX_ITERATIONS
	var endRange: Int = Int.MaxValue
	
	override def getColor(iterations: Int): Int = color
	
  override def getColorValue: Int = color
}