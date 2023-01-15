package org.luxoft.slancheros.test


import org.luxoft.slancheros.test.Tree.{Leaf, Node}



abstract class Tree(val maxPrevious: Float)

object Tree{



  case class Node(left: Tree, right: Tree) extends Tree(left.maxPrevious.max(right.maxPrevious))
  case class Leaf(from: Int, until: Int, override val maxPrevious: Float) extends Tree(maxPrevious)
  }


trait ScanInterface {
  def sequentialScan(input: Array[Float], output: Array[Float]): Unit
  def sequentialUpsweep(input: Array[Float], from: Int, until: Int): Float
  def upsweep(input: Array[Float], from: Int, until: Int, threshold: Int): Tree
  def sequentialDownsweep(input: Array[Float], output: Array[Float], startingValue: Float, from: Int, until:
  Int): Unit
  def downsweep(input: Array[Float], output: Array[Float], startingValue: Float, tree: Tree): Unit
 // def scan(input: Array[Float], output: Array[Float], threshold: Int): Unit
}

class Scan extends ScanInterface {


  def sequentialScan(input: Array[Float], output: Array[Float]): Unit = {
    //
    output(0) = 0
    var j = 1
    var max = 0f
    while (j < input.length) {
      val value = input(j) / j
      if (value > max) max = value else ()
      output(j) = max
      j += 1
    }
  }


  /** Input: the given part of the array and returns the maximum value.
   * from - inclusive
   * until - non-inclusive
   */
  def sequentialUpsweep(input: Array[Float], from: Int, until: Int): Float = {
    var output = 0f
    var j = from
    var max = 0f
    while (j < until) {
      val value = input(j)
      if (value > max) max = value else ()
      output = max
      j += 1
    }
    output
  }


  /** Traverses the part of the array starting at `from` and until `until`, and
   * returns the reduction tree for that part of the array.
   *
   * The reduction tree is a `Tree.Leaf` if the length of the specified part of the
   * array is smaller or equal to `threshold`, and a `Tree.Node` otherwise.
   * If the specified part of the array is longer than `threshold`, then the
   * work is divided and done recursively in parallel.
   */

  def upsweep(input: Array[Float], from: Int, until: Int, threshold: Int): Tree = {
    if ((until - from) < threshold) {
      Leaf(from, until, sequentialUpsweep(input, from, until))
    } else {
      val mid = (from + (until - from)) / 2
      val (a, b) = parallel(upsweep(input, from, mid, threshold),  upsweep(input, mid, until, threshold))
      Node(a, b)
    }
  }

  /** Traverses the part of the `input` array starting at `from` and until
   * `until`, and computes the maximum value for each entry of the output array,
   * given the `startingValue`.
   * Until -non-inclusive
   */
  def sequentialDownsweep(input: Array[Float], output: Array[Float],
                          startingValue: Float, from: Int, until: Int): Unit = {
    var j = until - 1
    var max = startingValue
    while (j >= from) {
      val value = input(j)
      if (value > max) max = value else ()
      output(j) = max
      j -= 1
    }
  }


  /** Pushes the maximum value in the prefix of the array to each leaf of the
   * reduction `tree` in parallel, and then calls `downsweepSequential` to write
   * the `output` values.
   */
  def downsweep(input: Array[Float], output: Array[Float], startingValue: Float, tree: Tree): Unit = {
    tree match {
      case Leaf(from, until, _) => sequentialDownsweep(input, output, startingValue, from, until)
      case Node(left, right) => {
        parallel(
          downsweep(input, output, startingValue, left),
          downsweep(input, output, left.maxPrevious max startingValue,right))
      }
    }
  }

  //override def scan(input: Array[Float], output: Array[Float], threshold: Int): Unit = scan(input, output, threshold)


}


