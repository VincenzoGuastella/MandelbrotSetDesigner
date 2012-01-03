*------------------------------------------------------------------------------------------*
                             MandelbrotSetDesigner
*------------------------------------------------------------------------------------------*

MandelbrotSetDesigner is a GUI tool designed ot generate images from the Mandelbrot set.

This is a training project focused on Scala multithreading and the scala swing libraries.
Any comment, advice or code review sent to v.guast@mail.com will be highly appreciated.

*------------------------------------------------------------------------------------------*

Required libraries:

Scala Library 2.9.1 final
 - scala-library.jar
 - scala-swing.jar
Akka 1.3 RC4
 - akka-actor-1.3-RC4.jar 

Startup arguments '-p ./Resources/MSDConfig.xml' sets the path of the configuration file

*------------------------------------------------------------------------------------------*

Version 0.1 (first commit)

Colors, window size and threads can be configured through the startup configuration file
Colors can be configured through the dedicated dialog in the GUI

Version 0.1.1 

Moved pixels calculations out of repaint method, added swing event to trigger 
method call and complete repaint

Version 0.1.2

Added smoothed colors. Now the user can choose through the GUI every possible color
or color smoothing to be applied to the diagram.

Version 0.1.3

Added right click popup menu with "Center Here" and zooming at different levels.
Various performance improvements and added PixelsCalculation for better thread management.

Version 0.2.0

Some code cleanup and performance improvements. Added a java.util.concurrent.CountDownLatch
and removed all the wait instructions.

Version 0.3.0

Switched to Akka actors and simplified the threading model. Removed PixelsCalculation
class and implemented the master thread in ImageDrawer.