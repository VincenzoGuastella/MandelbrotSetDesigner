package mandelbrotsetdesigner.guicomponents

import scala.swing.Component
import scala.swing.Dimension
import scala.swing.Graphics2D

import java.awt.Color
import javax.swing.JColorChooser

class ColorPicker(parent: ColorDialog, initColor: Int) extends Component {
  var color = new Color(initColor)
  var enabledColor = new Color(initColor)

  preferredSize = new Dimension(24, 24)
  maximumSize = new Dimension(24, 24)

  def update() {
    val newColor = JColorChooser.showDialog(null, "Choose Color", color);
    if (newColor != null) color = newColor

    repaint
  }
  
  def enable { enabled = true; color = enabledColor  }

  def disable { enabled = false; enabledColor = color; color = Color.GRAY }

  def getColorValue: Int = color.getRGB
  	
  override def paintComponent(g: Graphics2D) = {
    g.setColor(color)
    g.fillRect(0, 0, size.width, size.height)
  }
}