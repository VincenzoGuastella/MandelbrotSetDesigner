package mandelbrotsetdesigner.guicomponents

import mandelbrotsetdesigner.util.Config

import javax.swing.JPopupMenu
import java.awt.Component
import scala.swing.event._
import scala.swing._

trait PopUpMenu extends GuiFramework {
	

	val menu = new JPopupMenu
	menu.add(new MenuItem(Action("Center Here"){ changeImageCenter(menu) }).peer)
	var x = 0
	var y = 0
	menu.add(new Separator().peer)
	menu.add(new Separator().peer)
	menu.add(new MenuItem(Action("Zoom +20% (Ctrl +)"){ menu.setVisible(false); zoom(0.833333) }).peer)
	menu.add(new MenuItem(Action("Zoom -20% (Ctrl -)"){ menu.setVisible(false); zoom(1.2)      }).peer)
	menu.add(new Separator().peer)
	menu.add(new Separator().peer)
	menu.add(new MenuItem(Action("Zoom +100% (Ctrl 1)"){ menu.setVisible(false); zoom(0.5) }).peer)
	menu.add(new MenuItem(Action("Zoom -100% (Ctrl 1)"){ menu.setVisible(false); zoom(2.0) }).peer)
	menu.add(new Separator().peer)
	menu.add(new Separator().peer)
	menu.add(new MenuItem(Action("Zoom +200% (Ctrl 2)"){ menu.setVisible(false); zoom(0.25) }).peer)
	menu.add(new MenuItem(Action("Zoom -200% (Ctrl 2)"){ menu.setVisible(false); zoom(4.0)  }).peer)
	menu.add(new Separator().peer)
	menu.add(new Separator().peer)
	menu.add(new MenuItem(Action("Zoom +400% (Ctrl 4)"){ menu.setVisible(false); zoom(0.125) }).peer)
	menu.add(new MenuItem(Action("Zoom -400% (Ctrl 4)"){ menu.setVisible(false); zoom(8.0)   }).peer)
	menu.add(new Separator().peer)
	menu.add(new Separator().peer)
	menu.add(new MenuItem(Action("Zoom +800% (Ctrl 8)"){ menu.setVisible(false); zoom(0.0625) }).peer)
	menu.add(new MenuItem(Action("Zoom -800% (Ctrl 8)"){ menu.setVisible(false); zoom(16.0)   }).peer)
	
	def handleMouseEvent(event: Event) {
		    
  	event match {
		case MouseClicked(src, point, evModifiers, mClicks, triggerSrc) => {
				if (evModifiers.equals(256)) { //Mouse right click
					menu.setLocation(point.getX.toInt + 4, point.getY.toInt + 50)
					x = point.getX.toInt + 4
					y = point.getY.toInt + 50
					menu.setVisible(true)
				} else {                      //Mouse left click evModifiers == 0
					menu.setVisible(false)
				}
			}
			case e : Event => { 
		  	log("handleMouseEvent unknown event:" + e.toString, INFO)
		  }		  

    }

	}
	
  def changeImageCenter(menu: JPopupMenu) {
  	Config.setCenter(x - 4, y - 50)
  	menu.setVisible(false)
  	repaintAll = true 
  	mainFrame.repaint 
  }

}