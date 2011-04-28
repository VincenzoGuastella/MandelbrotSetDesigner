package mandelbrotsetdesigner.guicomponents

import scala.swing.event.Event
import scala.swing.RichWindow

case class RepaintAllEvent(source: ColorDialog) extends Event {}