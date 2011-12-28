package mandelbrotsetdesigner

class ActorMessages {}

case class PixelColor(x: Int, y: Int, rgb: Int) {}

case class FICompleted(fi: FunctionIterator) {}  //Function Iterator Completed Execution

case object Stop {}