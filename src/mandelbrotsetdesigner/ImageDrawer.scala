package mandelbrotsetdesigner

import mandelbrotsetdesigner.util._

import scala.actors.Actor
import scala.actors.Actor._
import scala.collection.mutable.LinkedList

import java.awt.image.BufferedImage
import java.awt.{Toolkit, Color, Graphics}
import java.util.concurrent.CountDownLatch

class ImageDrawer(img: BufferedImage, latch:CountDownLatch) 
	extends Actor with MyLoggable with Config {

	var stopFlag = false

	def act() {
		log("ImageDrawer thread running", FINE)
		while (true) {
			receive {
			  case pixel: PixelColor =>  {
			  	img.setRGB(pixel.x, pixel.y, pixel.rgb)
			  	
			  	if (stopFlag && mailboxSize <= 0) {
			  		latch.countDown()
			  		exit()
			  	}
			  }
				case Stop => {
					log("ImageDrawer Received Stop message", INFO)
			  	if (mailboxSize <= 0) {
			  		latch.countDown()
			  		exit()
			  	} else {
			  		stopFlag = true
			  	}	
				}
			}
		}
	}
}