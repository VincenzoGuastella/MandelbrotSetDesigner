package mandelbrotsetdesigner

import mandelbrotsetdesigner.datamodel._

import mandelbrotsetdesigner.util.Config
import mandelbrotsetdesigner.util.MyLoggable
import scala.xml._
import scala.collection.mutable.ListBuffer
import java.awt.Color
import scala.collection.mutable.LinkedList


object ColorsList extends MyLoggable {

	//Default values
	def getInstance():ColorsList = {
		
		val colors = List(SharpColorItem(0x060620, 0, 1), //darkblue
				              SharpColorItem(0x060630, 2, 3), //darkblue
				              SharpColorItem(0x060640, 4, 6), //Navy 
				              SharpColorItem(0x191970, 7, 8), //midnightblue
											SharpColorItem(0xee0000, 9, 16), //Red
			                SharpColorItem(Color.YELLOW.getRGB, 17, 38), 
			                SharpColorItem(Color.WHITE.getRGB, 39, 999),
			                MdbsColorItem(Color.BLACK.getRGB)) 

		new ColorsList(colors)
	}
	
	
	def loadFromXMLFile(fileName: String): ColorsList = {
		val colors = new ListBuffer[ColorItem]
		val data = XML.loadFile(fileName)

		data \\ "ColorScheme" \ "ColorItems" \ "ColorItem" foreach {
			item => colors += getColorItemFromNode(item)
		}

		if (!validateColors(colors.toList)) throw new IllegalArgumentException("Error validating the color scheme")
		
		new ColorsList(colors.toList)
	}
	
	
	def getColorItemFromNode(node: Node):ColorItem = {
		node \ "@itemType" text match {
			case "sharp" => new SharpColorItem(Integer.parseInt((node \ "@colorRGB" text), 16),
																				 (node \ "@startRange" text).toInt, 
																				 (node \ "@endRange" text).toInt)
			
			case "mdbs" => new MdbsColorItem((node \ "@colorRGB" text).toInt)
			
			case _ => throw new IllegalArgumentException("Error parsing color scheme. itemType " 
																									+ (node \ "@itemType" text) + " unknown")
		}		
	}
	
	def validateColors(colors: List[ColorItem]): Boolean = {
		
		if ( colors.count(item => item.isInstanceOf[MdbsColorItem]) > 1 ) {
			log("Default Color scheme invalid. One and only one color of type mdbs must be defined", SEVERE)
			false
		}
		
		var param = -1
		colors.foreach(item => {
			if (item.startRange == param + 1) param = item.endRange
			else if (!item.isInstanceOf[MdbsColorItem]){
				log("Default Color scheme invalid. Start and end ranges of the colors contain a gap or an overlap", SEVERE)
				false 
			}
		})

		true
	}

	/**
	 * getSmoothedColorArray(from: Color, to: Color)
	 * Returns a list of colors varying the RGB  
	 * values between the two input colors  
	 */
	def getSmoothedColorArray(from: Color, to: Color): List[Color] = {
		
		var colorsPalette: ListBuffer[Color] = new ListBuffer() //Initialize the color list and add starting color
		colorsPalette += from
		
		val channels = List(new ChannelValues(from.getRed, to.getRed, 0, 0),
												new ChannelValues(from.getGreen, to.getGreen, 0, 0),
												new ChannelValues(from.getBlue, to.getBlue, 0, 0))
	  
		log("getSmoothedColorArray Channels values: " + channels.toList, VERBOSE)

		log(" " + colorsPalette.toList, VERBOSE)
		
		for (i <- 1 until 254) {
	  	if (channels.exists{_.nextStep == i}) {
	  		colorsPalette += new Color(channels(0).getNewChanneValue(i), 
	  															 channels(1).getNewChanneValue(i),
	  															 channels(2).getNewChanneValue(i))
	  	}
	  }
		
		colorsPalette += to 
		
		log("getSmoothedColorArray returning new color palette.", VERBOSE)
		log(" " + colorsPalette.toList, VERBOSE)

		colorsPalette.toList
	}

}

class ColorsList(var colors: List[ColorItem]) extends MyLoggable {
	
	def findColor(iterations: Int, x:Double, y:Double, x_0:Double, y_0:Double): Int = {		
	  for (colorItem: ColorItem <- colors) {
	  	if (colorItem.isInRange(iterations))
	  		return colorItem.getColor(iterations, x, y, x_0, y_0) 
	  }
	  //If it arrives here something went wrong
	  log("Did not find a color set for the iterations number: " + iterations, SEVERE)
	  log("Colors List: " + colors.toList, SEVERE)

	  0
	}
		
}