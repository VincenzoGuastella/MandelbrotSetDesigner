package mandelbrotsetdesigner.guicomponents

import mandelbrotsetdesigner.ColorsList
import mandelbrotsetdesigner.util.Config
import mandelbrotsetdesigner.datamodel.ColorItem
import mandelbrotsetdesigner.MandelbrotSetDesigner

import scala.swing._
import scala.swing.Panel
import scala.swing.Frame
import scala.swing.Button
import scala.swing.Dialog
import _root_.java.awt.Color._
import java.text.NumberFormat
import java.awt.BorderLayout
import javax.swing.SpinnerNumberModel
import javax.swing.JSpinner
import javax.swing.Icon
import javax.swing.ImageIcon
import javax.swing.JColorChooser
import scala.swing.event.MouseClicked
import scala.collection.mutable.Buffer
import scala.collection.mutable.ListBuffer


class ColorDialog(owner: Window) extends Dialog(owner) with GuiFramework with Config {

	log ("ColorDialog started", VERBOSE)	
	
//  Published events are not received when the listener is owner and the dialog is modal
//	modal = true	

	var isValid = true
	var repaintParent = false
	var colorDialog = this

	menuBar = new MenuBar {
    contents += new Menu("File") {
      contents += new MenuItem(Action("Import Configuration") { isNotImplemented(this) })
      contents += new MenuItem(Action("Export Configuration") { isNotImplemented(this) })
      contents += new Separator
      contents += new MenuItem(Action("Save and Exit") { tryCatch(this) { saveToConfig }  })  
      contents += new Separator
      contents += new MenuItem(Action("Exit without saving") { close() })  
    }
    contents += new Menu("Colors") {
      contents += new MenuItem(Action("Add Color") { tryCatch(this) { addColor } })
      contents += new MenuItem(Action("Validate") { tryCatch(this) { checkValidation } })
      contents += new MenuItem(Action("Sort colors list") { tryCatch(this) { sortColorItems } })
    }
  }
	
	log ("Created Dialog Menu Bar", VERBOSE)
  var mdbsColorPanel : MdbsColorPanel	= null
   
	contents = new BoxPanel(Orientation.Vertical) {
		colors.colors.foreach(colorItem => {
//			dialogHeight += 46			
			if (colorItem.startRange >= MAX_ITERATIONS) {
		  	mdbsColorPanel = new MdbsColorPanel(colorDialog, colorItem.getColorValue)
		  	contents += mdbsColorPanel				
			} else if (colorItem.isInstanceOf[mandelbrotsetdesigner.datamodel.SharpColorItem]) 
			  	contents += new BaseColorPanel(colorDialog, colorItem.startRange, 
			  														     colorItem.endRange, colorItem.getColorValue,
			  														     colorItem.getColorValue, true)
		  else if (colorItem.isInstanceOf[mandelbrotsetdesigner.datamodel.SmoothedColorItem]) 
		  	contents += new BaseColorPanel(colorDialog, colorItem.startRange, 
		  														     colorItem.endRange, colorItem.getColorValue, 
		  																 colorItem.getGradientValue, false)		  	

		})
  }

	log ("About to set visible", VERBOSE)
	pack
	visible = true
	
	override def repaint : Unit = {
   	super.repaint()
  }
	
	
	def removeColor(item: ColorPanel) {
		
		var newDialogPanel: BoxPanel = contents.head.asInstanceOf[BoxPanel] 
		newDialogPanel.contents -= item.asInstanceOf[BoxPanel]
		contents = newDialogPanel
		pack
		repaint
	}
	
	
	def sortColorItems {
  	log ("sortColorItems called", FINE)	
		contents = new BoxPanel(Orientation.Vertical) {
	  	contents ++= getSortedItems
  	}
	  log ("sortColorItems calling repaint", FINE)	
		repaint
	}
	

	def getSortedItems: Buffer[Component] = {
		var newDialogPanel: BoxPanel = contents.head.asInstanceOf[BoxPanel];
		newDialogPanel.contents.sortWith((item1, item2) => {
			if (item1.isInstanceOf[MdbsColorPanel]) false  
			else if (item2.isInstanceOf[MdbsColorPanel]) true
			else (item1.asInstanceOf[BaseColorPanel].getFrom() < item2.asInstanceOf[BaseColorPanel].getFrom())
		})
	}
	
	//To be valid from and to value of all the Color Items
	//must cover the whole range from 0 to MAX_ITERATIONS
	def checkValidation {
	  var param = -1
		getSortedItems.foreach(item => {
			if (item.isInstanceOf[BaseColorPanel])
				if (item.asInstanceOf[BaseColorPanel].getFrom == param + 1) 
					param = item.asInstanceOf[BaseColorPanel].getTo
				else setInvalid
		})
		
		if (isValid){			
			repaint		
			scala.swing.Dialog.showMessage(menuBar, "Validation successful")
		}
	}
	
	def setInvalid {
		if (isValid) { //execute actions only if there has been a change
			log ("ColorDialog. Validation failed", VERBOSE)	
			isValid = false
			if (mdbsColorPanel != null) mdbsColorPanel.showInvalid
			repaint
		}
	}
	
	
	def addColor {
		contents = new BoxPanel(Orientation.Vertical) {
	  	contents += new BaseColorPanel(colorDialog, 0, 0, java.awt.Color.WHITE.getRGB, 0, false)
	  	contents ++= getSortedItems
  	}
		pack
		repaint
	}

	
	def saveToConfig() {
		log("ColorDialog saveToConfig. Setting new colors list", VERBOSE)
		var colors = new ListBuffer[ColorItem]
		getSortedItems.foreach(item => colors += item.asInstanceOf[ColorPanel].toColorItem)
		setConfigColors(colors)
		log(" " + colors.toList, VERBOSE)
		publish(new RepaintAllEvent(this))
	}
	
}