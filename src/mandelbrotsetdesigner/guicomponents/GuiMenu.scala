package mandelbrotsetdesigner.guicomponents

import swing._

import mandelbrotsetdesigner.MandelbrotSetDesigner

trait GuiMenu extends GuiFramework {
	
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
		val parent = MandelbrotSetDesigner.parentWindow
		val dialog = new ColorDialog(parent)

		dialog.open()
		log("setColors method end", FINE)

	}

}