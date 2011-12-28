package mandelbrotsetdesigner.guicomponents

import mandelbrotsetdesigner.util.Config
import mandelbrotsetdesigner.util.MyLoggable
import mandelbrotsetdesigner.datamodel.ColorItem
import mandelbrotsetdesigner.datamodel.MdbsColorItem
import mandelbrotsetdesigner.datamodel.SharpColorItem
import mandelbrotsetdesigner.EmptySpace
import scala.swing._
import scala.swing.event.ButtonClicked
import scala.swing.event.MouseClicked
import _root_.java.awt.Color._
import java.text.NumberFormat
import java.awt.BorderLayout
import javax.swing.SpinnerNumberModel
import javax.swing.JSpinner
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JColorChooser
import javax.swing.border.EmptyBorder
import mandelbrotsetdesigner.datamodel.SmoothedColorItem



trait ColorPanel extends  Config {

  val lMand = new Label("Madelbrot set values ")
  val lColorType = new Label("  Type ")	
  val lColor = new Label("  Color ")	
  val lGrad = new Label("  Gradient ")	

	val removeIcon = new ImageIcon("./Resources/Remove-icon24.png")
  val removeBtn = new Button{ 
  	icon = removeIcon
  	tooltip = "Remove Color"	
  	border = null
  	contentAreaFilled = false
  	borderPainted = false
	}
  
  def toColorItem: ColorItem
}

trait ColorTypeRadioBtn {

	val colorType = new ButtonGroup
	val shrp = new RadioButton("Sharp")
	val smth = new RadioButton("Smoothed")
	val radios = List(shrp, smth)
  colorType.buttons ++= radios
  colorType.select(shrp)     
  val radiosPan = new BoxPanel(Orientation.Horizontal) {
		border = Swing.EmptyBorder(0, 1, 2, 1)
    contents ++= radios
  }
}

class BaseColorPanel(parent: ColorDialog, from: Int, to: Int, 
										 color: Int, gradient: Int, var isSharp: Boolean) 
	extends BoxPanel(Orientation.Horizontal) with ColorPanel with ColorTypeRadioBtn {

	border = Swing.EmptyBorder(4, 3, 4, 5)

	val spinPanel = new SpinnerPanel(from, to)
	val picker  = new ColorPicker(parent, color)
	val picker2 = new ColorPicker(parent, gradient)

	contents += spinPanel

	//Add radio button to choose sharp or smoothed ColorItem
	contents += lColorType
	contents += radiosPan
	import Dialog._
	if (isSharp){
		colorType.select(shrp)     
		setSharp
	} else {
		colorType.select(smth)     
		setSmoothed
	}

	contents += lColor
	contents += picker
	contents += new EmptySpace()
	contents += lGrad
	contents += picker2

	contents += new EmptySpace()
	contents += removeBtn
	listenTo(picker.mouse.clicks, picker2.mouse.clicks)
	listenTo(removeBtn)
	listenTo(shrp, smth) //Radio buttons

	reactions += {
		//ColorPicker Actions
		case MouseClicked(`picker`, point, mod, clicks, pops) => {
      picker.update
    }
		case MouseClicked(`picker2`, point, mod, clicks, pops) => {
      if (picker2.enabled) picker2.update
    }
		//removeBtn Actions
		case ButtonClicked(`removeBtn`) => {
			parent removeColor this
    }
		//colorType Actions
		case ButtonClicked(`shrp`) => {
			setSharp
    }
		case ButtonClicked(`smth`) => {
			setSmoothed
    }
	}
 	
	def setSharp {
		isSharp = true
		picker2.disable 
		lGrad enabled = false
		
		parent repaint
	}

	def setSmoothed {
		isSharp = false
		picker2.enable
		lGrad enabled = true

		parent repaint
	}
	
	def getFrom(): Int = spinPanel getFrom

	def getTo(): Int = spinPanel getTo

	def toColorItem: ColorItem = {
		if (isSharp) new SharpColorItem(picker getColorValue, getFrom, getTo)
		else new SmoothedColorItem(picker color, picker2 color, getFrom, getTo)
	}

}


class MdbsColorPanel(parent: ColorDialog, color: Int) 
	extends BoxPanel(Orientation.Horizontal) with ColorPanel {
 
	border = Swing.EmptyBorder(1, 2, 1, 2)
	val picker = new ColorPicker(parent, color)
	val validationLbl = new Label("                     ")
	
	contents += lMand		
	contents += lColor
	contents += picker
	listenTo(picker.mouse.clicks)
	reactions += {
		case MouseClicked(`picker`, point, mod, clicks, pops) => {
      picker.update
    }

	}
	contents += new EmptySpace()
	contents += validationLbl

	def showInvalid { 
		validationLbl text = "Invalid Configuration"
		validationLbl foreground = java.awt.Color.RED	
	}
	
	def showValid { 
		validationLbl text = "                     "
		validationLbl foreground = java.awt.Color.BLACK	
	}

	def toColorItem: ColorItem = new MdbsColorItem(picker getColorValue)
}

class SpinnerPanel(from: Int, to: Int) 
	extends BoxPanel(Orientation.Horizontal) with Config  {

	val lFrom = new Label("From ")
  val lTo = new Label("  To ")

	val min = 0
	val spIncrement = 1
	val spinnerFrom = new JSpinner(new SpinnerNumberModel(from, min, MAX_ITERATIONS -1, spIncrement))
	val spinnerTo = new JSpinner(new SpinnerNumberModel(to, min, MAX_ITERATIONS -1, spIncrement))
	spinnerFrom.setMaximumSize(new Dimension(60, 24))
	spinnerFrom.setMinimumSize(new Dimension(60, 24))
	spinnerTo.setMaximumSize(new Dimension(60, 24))
	spinnerTo.setMinimumSize(new Dimension(60, 24))
		
	border = Swing.EmptyBorder(2, 1, 2, 5)
			
	contents += lFrom
	contents += Component.wrap(spinnerFrom)
	contents += lTo
	contents += Component.wrap(spinnerTo)


	def getFrom(): Int = spinnerFrom.getValue().asInstanceOf[Int]

	def getTo(): Int = spinnerTo.getValue().asInstanceOf[Int]	
}