package mandelbrotsetdesigner

import swing._
import java.util.Calendar
import java.awt.image.BufferedImage
import java.awt.{Toolkit, Color, Graphics}


object SingleThreadedMandelbrotSetDesigner extends SimpleSwingApplication {

	val WIDTH = 1400
	val HEIGHT = 1024
	val START_X = -2.3 
	val START_y = -1.3
	val INCREMENT = 0.0026
	val MAX_ITERATIONS = 500
	val colors = List(ColorMatcher(0x060620, 0, 1), //darkblue
			              ColorMatcher(0x060630, 2, 3), //darkblue
			              ColorMatcher(0x060640, 4, 6), //Navy 
			              ColorMatcher(0x191970, 7, 8), //midnightblue
										ColorMatcher(0xee0000, 9, 16), //Red
	                  ColorMatcher(Color.YELLOW.getRGB, 17, 38), 
	                  ColorMatcher(Color.WHITE.getRGB, 39, MAX_ITERATIONS -1),
	                  ColorMatcher(Color.BLACK.getRGB, MAX_ITERATIONS, 9999)) 
	
	
	def top = new MainFrame {
    title = "A Mandelbrot Set"
    preferredSize_=(new Dimension(WIDTH, HEIGHT))

    contents = new Panel {
      var img = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB)
      this.peer.setDoubleBuffered(true)
 
      override def paintComponent(g : Graphics2D) : Unit = {
       	super.paintComponent(g)
       	var timeCheck = System.currentTimeMillis()
       	println("Calling drawImage")
        drawImage(img)
        timeCheck = System.currentTimeMillis() - timeCheck
       	println("drawImage completed")
        g.drawImage(img, null, 0, 0)        
        Toolkit.getDefaultToolkit().sync
//        g.dispose 
      }
      repaint()
    }
	}
	
	def drawImage(img: BufferedImage) {
	  for(i <- 0 until WIDTH; j <- 0 until HEIGHT) {
//	  	println("New loop i[" + i + "]  j[" + j + "]")
	  	var x= START_X + (INCREMENT*i) //is pixel 0,0 at the bottom ? To check
	  	var y= START_y + (INCREMENT*j) //is pixel 0,0 at the bottom ? To check
	  	
			var pixelColor = 0
		  var it: Int = getIterations(0, 0, x, y, 0)
        
		  for (colorMatcher <- colors)
		  	if (colorMatcher.matches(it)) pixelColor = colorMatcher.color 
	  	
	    img.setRGB(i, j, pixelColor)
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


case class ColorMatcher(color: Int, startRange: Int, endRange: Int)  {
	
	def matches(value: Int): Boolean = 
		if (startRange <= value && value <= endRange) true else false
}