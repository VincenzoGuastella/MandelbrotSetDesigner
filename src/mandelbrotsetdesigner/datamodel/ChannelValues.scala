package mandelbrotsetdesigner.datamodel

class ChannelValues(var value: Int, val valueTo: Int, 
										var step: Int, var nextStep: Int) {

	val delta = valueTo - value
	//If delta = 0 set step to 256 so the value will never increase 
	step = 256; if (delta != 0) step = 255/delta.abs
	nextStep = step
	
	def getNewChanneValue(i: Int): Int = {
		if (i == nextStep) {
			if (delta > 0 && value < valueTo) {
				value += 1
				nextStep += step
			}
			if (delta < 0  && value > valueTo) {
				value -= 1
				nextStep += step
			}
		}
		return value
	}
	
	override def toString: String =  "Value [" + value + "] Delta [" + delta  + "] Step [" + step + "] Nextstep [" + nextStep 
}
