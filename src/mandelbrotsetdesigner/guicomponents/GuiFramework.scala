package mandelbrotsetdesigner.guicomponents

import mandelbrotsetdesigner.util.MyLoggable

import scala.swing.Component

trait GuiFramework extends MyLoggable{
	

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

}