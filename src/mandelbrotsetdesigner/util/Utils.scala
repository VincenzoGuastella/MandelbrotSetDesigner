package mandelbrotsetdesigner.util

object Utils {
	
	def substringBefore(str: String, refString: String): String = {
		str.indexOf(refString) match {
			case 0  => ""
			case -1 => str
			case _  => str.substring(0, str.indexOf(refString))	 
		}
	}
	
	def substringAfter(str: String, refString: String): String = {
		str.indexOf(refString) match {
			case -1 => str
			case _  => str.substring(str.indexOf(refString) + refString.length())	 
		}
	}
}