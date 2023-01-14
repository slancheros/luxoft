package org.luxoft.slancheros.test


import org.luxoft.slancheros.test.Tree.{Leaf, Node}

import scala.util.DynamicVariable
import java.util.concurrent._

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
  def scan(input: Array[Float], output: Array[Float], threshold: Int): Unit
}

class Scan extends ScanInterface {

  val scheduler: TaskScheduler = new TaskScheduler() {
    override def schedule[T](body: => T): ForkJoinTask[T] = ???
  }


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
  def sequentialUpsweep(input: Array[Float], from: Int, until: Int): Float  = {
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

  def upsweep(input: Array[Float], from: Int, mid: Int): Tree = ???

  /** Traverses the part of the array starting at `from` and until `until`, and
   * returns the reduction tree for that part of the array.
   *
   * The reduction tree is a `Tree.Leaf` if the length of the specified part of the
   * array is smaller or equal to `threshold`, and a `Tree.Node` otherwise.
   * If the specified part of the array is longer than `threshold`, then the
   * work is divided and done recursively in parallel.
   */
  def upsweep(input: Array[Float], from: Int, until: Int, threshold: Int): Tree = {
    if(until - from < threshold)
    {
      Leaf(from, until, input(from))
    }else {
      val mid = (from + (until - from)) / 2
     //Node(defaultTaskScheduler.parallel(  upsweep(input, from,mid),upsweep(input, mid, until)))
      Leaf(from, until, input(from))
    }
  }

  /** Traverses the part of the `input` array starting at `from` and until
   * `until`, and computes the maximum value for each entry of the output array,
   * given the `startingValue`.
   * Until -non-inclusive
   */
  def sequentialDownsweep(input: Array[Float], output: Array[Float],
                          startingValue: Float, from: Int, until: Int): Unit= {
    var j = until -1
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
  def downsweep(input: Array[Float], output: Array[Float], startingValue: Float, tree: Tree): Unit = ???

  override def scan(input: Array[Float], output: Array[Float], threshold: Int): Unit = scan(input, output, threshold)





}


