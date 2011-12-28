package mandelbrotsetdesigner.guicomponents

import mandelbrotsetdesigner.util.MyLoggable
import scala.swing.Component
import scala.swing.Frame
import mandelbrotsetdesigner.util.Config

trait GuiFramework extends MyLoggable{
	
	var mainFrame : Frame = null
	var repaintAll = true

	def tryCatch(comp: Component)(wrappedMethod: => Unit) {
		try {
			log("ColorDialog tryCatch around method start", INFO)
			wrappedMethod			
			log("ColorDialog tryCatch around method end", INFO)
		} catch {
			case e: Exception => {
				var msg = "Caught Exception \n" + e.toString 
				if (isLogEnabled) {
					msg += "\n\n" + "Please check the log file"
					log(e.toString, SEVERE)
					log(" " + e.getStackTraceString, SEVERE)
				} 
				scala.swing.Dialog.showMessage(comp, msg)
			}
		}

	}

	def isNotImplemented(comp: Component) {
		try {
				scala.swing.Dialog.showMessage(comp, "Not implemented, yet")
		} catch {
			case e: Exception => {
				log("Caught exception showing isNotImplemented message. " + e.toString, SEVERE)
				log(e.getStackTraceString, SEVERE)
			}
		}
		
	}
	
	/**
	 * 
	 * @param zoomRate
	 * 
	 */
	def zoom(zoomRate: Double) {
	  val xCenter:Double = Config.START_X + Config.WIDTH / 2 * Config.INCREMENT
	  val yCenter:Double = Config.START_Y + Config.HEIGHT / 2 * Config.INCREMENT
		
		Config.INCREMENT = Config.INCREMENT * zoomRate

		Config.START_X = xCenter - Config.WIDTH / 2 * Config.INCREMENT
		Config.START_Y = yCenter - Config.HEIGHT / 2 * Config.INCREMENT

		repaintAll = true 
  	mainFrame.repaint 		
	}

}