*------------------------------------------------------------------------------------------*
                             MandelbrotSetDesigner
*------------------------------------------------------------------------------------------*

MandelbrotSetDesigner is a GUI tool designed ot generate images from the Mandelbrot set.

This is a training project focused on Scala multithreading and the scala swing libraries.
Any comment, advice or code review sent to v.guast@mail.com will be highly appreciated.

Threading model: the calculation of the color of each pixels is based on immutable values 
                 and the lightweight actor method, therefore the calculating thread won't 
                 set any value, but it will sen a message to the ImageDrawer that is a full
                 implementation of a scala actor. 

*------------------------------------------------------------------------------------------*

Required libraries:

Scala Library 2.8.1 final
 - scala-library.jar
 - scala-dbc.jar
 - scala-swing.jar
  

Startup arguments '-p ./Resources/MSDConfig.xml' sets the path of the configuration file

*------------------------------------------------------------------------------------------*

Version 0.1 (first commit)

Colors, window size and threads can be configured through the startup configuration file
Colors can be configured through the dedicated dialog in the GUI

Known issues:

- repaint of the main window not working properly
- the color configuration dialog opens twice

