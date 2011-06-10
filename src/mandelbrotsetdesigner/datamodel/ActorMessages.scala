package mandelbrotsetdesigner

class ActorMessages {}

case class PixelColor(x: Int, y: Int, rgb: Int) {}

case class Stop {}

case class FICompleted(fi: FunctionIterator) {}  //Function Iterator Completed Execution