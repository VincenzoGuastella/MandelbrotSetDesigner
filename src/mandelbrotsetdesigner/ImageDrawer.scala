package mandelbrotsetdesigner

import mandelbrotsetdesigner.util._

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.LinkedList

import java.awt.image.BufferedImage
import java.awt.{Toolkit, Color, Graphics}

class ImageDrawer(img: BufferedImage) extends Actor with MyLoggable with Config {

	def mboxSize = mailboxSize 

	def act() {
		log("ImageDrawer thread running", FINE)
		while (true) {
			receive {
			  case pixel: PixelColor =>  img.setRGB(pixel.x, pixel.y, pixel.rgb)
				case Stop => {
					log("ImageDrawer Received Stop message", INFO)
					exit()
				}
			}
		}
	}
}