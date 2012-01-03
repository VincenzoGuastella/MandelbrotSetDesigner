package mandelbrotsetdesigner

import mandelbrotsetdesigner.util._
import mandelbrotsetdesigner.datamodel._

//import scala.actors.Actor
//import scala.actors.Actor._
import scala.collection.mutable.LinkedList
import scala.collection.mutable.ListBuffer

import akka.actor.Actor._
import akka.routing.{Routing, CyclicIterator}
import Routing._
import akka.event.EventHandler
import akka.actor.{Channel, Actor, PoisonPill}

import java.awt.image.BufferedImage
import java.awt.{Toolkit, Color, Graphics}
import java.util.concurrent.CountDownLatch

case class ImageDrawer(img: BufferedImage) 
	extends Actor with MyLoggable with Config {

	//create the threads that execute the calculation wrap them with a load-balancing router
  val workers = Vector.fill(MAX_THREADS)(actorOf[FunctionIterator].start())
  val router = Routing.loadBalancerActor(CyclicIterator(workers)).start()

  	
  var activeWorkers = MAX_THREADS.toInt
  var outstandingMsgs = 0
  
	// phase 1, can accept a Calculate message
	def scatter: Receive = {
  	case "CalcPixelsAndDraw" => {
  		log("Starting pixels calculation.", VERBOSE) 
  		callCalculationThreads
  		log("ImageDrawer sent " + outstandingMsgs + " the messages to the worker threads.", VERBOSE)  		

  		//Assume the gathering behavior
  		this become gather(self.channel)
		}
	}

	// phase 2, aggregate the results of the Calculation
	def gather(recipient: Channel[Any]): Receive = {
		case pc: PixelsColors => {			
			pc.pixels.foreach{ pixel:PixelColor => img.setRGB(pixel.x, pixel.y, pixel.rgb) }
			outstandingMsgs -= 1
			if (outstandingMsgs <= 0) {
				recipient ! "DrawingCompleted"
				self.stop()
			}
		}
	}

	// message handler starts at the scattering behavior
	def receive = scatter
	
  private def callCalculationThreads {
		//Loop to select the lines for each calculations and send
		//all the messages to the 
		var p_y , p_y0 = 0;
	  while(p_y < HEIGHT) {
	  	p_y0 = p_y
	  	p_y += LINES_PER_THREAD -1
	  	if (p_y >= HEIGHT) p_y = HEIGHT - 1
	  	router ! new CalculateLines(p_y0, p_y)
	  	p_y += 1
	  	outstandingMsgs += 1
	  }	  
	}
	
	override def preStart() {
		log("Starting master image drawing thread .", VERBOSE)  			
  }

	
  override def postStop {
		log("Master image drawing thread Completed.", VERBOSE)  			
    // Stop all the workers and the router
    router ! Broadcast(PoisonPill)
    router ! PoisonPill    
  }
	
}