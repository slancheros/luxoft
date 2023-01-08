package org.luxoft.slancheros.test

import scala.util.DynamicVariable
//import scala.concurrent.JavaConversions.{asExecutionContext,_}
//import scala.concurrent.forkjoin.{ForkJoinPool, ForkJoinTask, ForkJoinWorkerThread,
 // RecursiveTask}
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
  def sequentialUpsweep(input: Array[Float], from: Int, until: Int): Float = ???

  /** Traverses the part of the array starting at `from` and until `until`, and
   * returns the reduction tree for that part of the array.
   *
   * The reduction tree is a `Tree.Leaf` if the length of the specified part of the
   * array is smaller or equal to `threshold`, and a `Tree.Node` otherwise.
   * If the specified part of the array is longer than `threshold`, then the
   * work is divided and done recursively in parallel.
   */
  def upsweep(input: Array[Float], from: Int, until: Int, threshold: Int): Tree = ???

  /** Traverses the part of the `input` array starting at `from` and until
   * `until`, and computes the maximum value for each entry of the output array,
   * given the `startingValue`.
   */
  def sequentialDownsweep(input: Array[Float], output: Array[Float], startingValue: Float, from: Int, until:
  Int): Unit = ???

  /** Pushes the maximum value in the prefix of the array to each leaf of the
   * reduction `tree` in parallel, and then calls `downsweepSequential` to write
   * the `output` values.
   */
  def downsweep(input: Array[Float], output: Array[Float], startingValue: Float, tree: Tree): Unit = ???

  override def scan(input: Array[Float], output: Array[Float], threshold: Int): Unit = scan(input, output, threshold)


val forkJoinPool = new java.util.concurrent.ForkJoinPool()

  abstract class TaskScheduler {

    def schedule[T](body: => T): ForkJoinTask[T]
    def task[T](body: => T):
    ForkJoinTask[T] = this.schedule(body)

    def parallel[A, B](taskA: => A, taskB: => B):(A,B) ={
      val right = task {
        taskB
      }
      val left = taskA
      (left, right.join())
    }


    class DefaultTaskScheduler extends TaskScheduler {
      val scheduler = new DynamicVariable[TaskScheduler](new DefaultTaskScheduler())
      def schedule[T](body: => T): ForkJoinTask[T] = {
        val t = new RecursiveTask[T] {
          def compute = body
        }

        Thread.currentThread match {
          case wt: ForkJoinWorkerThread => t.fork()
          case _ => forkJoinPool.execute(t)
        }
        t
      }


      override def parallel[A, B](taskA: => A, taskB: => B):(A,B) =  scheduler.value.parallel(taskA,taskB)

      def parallel[A, B, C, D](taskA: => A, taskB: => B, taskC: => C, taskD: => D): (A, B, C, D) = {
        val ta = task {
          taskA
        }
        val tb = task {
          taskB
        }
        val tc = task {
          taskC
        }
        val td = taskD
        (ta.join(), tb.join(), tc.join(), td)
      }
    }
  }
}


