package domain

package object model {
  private class Point(x: Int, y: Int) // get from front
  class Window(beginning: Point, end: Point) // send to front
  class Cardboard(lst: List[Window])//(TODO ???)
  class Text// to put under the cardboard
}
