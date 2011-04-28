package mandelbrotsetdesigner.guicomponents

import swing._
import scala.swing.event.Event
import scala.swing.event.WindowClosing
import scala.swing.event.WindowClosed

trait GuiMenu extends GuiFramework with Reactor {
	
	var mainFrame : Frame = null
	var repaintAll = true
	
	def addGuiMenu(contents: Seq[scala.swing.Component]): MenuBar = {
		
		 var menuBar = new MenuBar {
      contents += new Menu("File") {
        contents += new MenuItem(Action("Save Current Graph") { isNotImplemented(this) })
        contents += new Separator
        contents += new MenuItem(Action("Exit") { exit() })  
      }
      contents += new Menu("Graph") {
        contents += new MenuItem(Action("Show coordinates") { isNotImplemented(this) })
        contents += new MenuItem(Action("Set Colors") { setColors() })
        contents += new MenuItem(Action("Set Size and Position") { isNotImplemented(this) })
      }
    }

		return menuBar
	}

	
	def setColors() {
		
		log("setColors method start", FINE)
		val colorDialog = new ColorDialog(mainFrame.owner)

		log("setColors method end", FINE)
	  listenTo(colorDialog)
		
		reactions += {
		  case RepaintAllEvent(colorDialog) => {
		  	log("Reactions: caught RepaintAllEvent", FINE)
		  	repaintAll = true 
		    deafTo(colorDialog)
		  	colorDialog close;
		  	mainFrame repaint 
		  } 
		  // Is it necessary ?
		  case WindowClosing(colorDialog) => {
		  	log("Reactions: caught WindowClosing event", INFO)
		    deafTo(colorDialog)
		  }
//		  case e : Event => {
//		  	log("Case else selected", VERBOSE)
//		  	log(" " + e, INFO)
//		  }		  
		}
	}

}