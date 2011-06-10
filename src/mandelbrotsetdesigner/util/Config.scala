package mandelbrotsetdesigner.util

import mandelbrotsetdesigner.ColorsList
import mandelbrotsetdesigner.datamodel.ColorItem

import scala.xml._
import scala.collection.immutable.StringOps
import scala.collection.mutable.ListBuffer


trait Config extends MyLoggable {
	
	def parseConfigFile(fileName: String) {
		
		val data = XML.loadFile(fileName)
		val logFolder = data \\ "Tracing" \ "@logFolder" text;
		val logLevel = data \\ "Tracing" \ "@logLevel" text;
		val traceOnConsole = new StringOps(data \\ "Tracing" \ "@traceOnConsole" text) toBoolean;
		initLogging(logFolder + "/MSDLog.log", logLevel, traceOnConsole)

		Config.MAX_THREADS = (data \\ "System" \ "@maxThreads" text) toInt;
		Config.LINES_PER_THREAD = (data \\ "System" \ "@linesPerThread" text) toInt;
		Config.MAX_ITERATIONS = (data \\ "System" \ "@maxIterations" text) toInt;
		
		Config.WIDTH = (data \\ "DefaultDiagram" \ "@width" text) toInt;
		Config.HEIGHT = (data \\ "DefaultDiagram" \ "@height" text) toInt;
		Config.START_X = (data \\ "DefaultDiagram" \ "@startX" text) toDouble;
		Config.START_Y = (data \\ "DefaultDiagram" \ "@startY" text) toDouble;
		Config.INCREMENT = (data \\ "DefaultDiagram" \ "@increment" text) toDouble;

		val  colorsFileName: String = data \\ "DefaultDiagram" \ "@defaultColorSchemeFile" text;
		try {
    	Config.colors = ColorsList.loadFromXMLFile(colorsFileName)
    }catch {
 			case e: Exception => {
					log("Got exception parsing the default Color Scheme File: "+ e.toString, SEVERE)
					//TO DO add message to let user know the default color scheme hasn't been used
					Config.colors = ColorsList.getInstance
			}  			
    }
	}
	
	def colors: ColorsList = Config.colors
	
	def setConfigColors(newColors: ListBuffer[ColorItem]) { 
		Config.colors = new ColorsList(newColors.toList) 
	}

	def MAX_THREADS: Int = Config.MAX_THREADS 
	def LINES_PER_THREAD: Int = Config.LINES_PER_THREAD
	def MAX_ITERATIONS: Int = Config.MAX_ITERATIONS
	def WIDTH: Int = Config.WIDTH
	def HEIGHT: Int = Config.HEIGHT
	def START_X: Double = Config.START_X 
	def START_Y: Double = Config.START_Y
	def INCREMENT: Double = Config.INCREMENT

	def ESCAPE_RADIUS: Double = Config.ESCAPE_RADIUS

}

object Config {

	var colors: ColorsList = null
	
	//System configurations
	var MAX_THREADS: Int = 0
	var LINES_PER_THREAD: Int = 0
	var MAX_ITERATIONS: Int = 0
	
	//Picture configurations
	var WIDTH: Int = 0
	var HEIGHT: Int = 0
	var START_X: Double = 0 
	var START_Y: Double = 0
	var INCREMENT: Double = 0
	
	val ESCAPE_RADIUS: Double = 2.0

	
	def setCenter (x: Double, y: Double) {
		println("setCenter x [" + x + "]     y [" + y + "]")
		START_X = START_X + ((x - WIDTH / 2) * INCREMENT)
		START_Y = START_Y + ((y - HEIGHT / 2) * INCREMENT)		
		println("New x [" + START_X + "]    New y [" + START_Y + "]")
	}
	
}