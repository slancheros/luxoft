package org.luxoft.slancheros.test

import java.util.concurrent.{ForkJoinTask, ForkJoinWorkerThread, RecursiveTask}
import scala.util.DynamicVariable


  abstract class TaskScheduler {

    def schedule[T](body: => T): ForkJoinTask[T]

    def task[T](body: => T):
    ForkJoinTask[T] = this.schedule(body)

    def parallel[A, B](taskA: => A, taskB: => B): (A, B) = {
      val right = task {
        taskB
      }
      val left = taskA
      (left, right.join())
    }
  }


class DefaultTaskScheduler extends TaskScheduler {
      val forkJoinPool = new java.util.concurrent.ForkJoinPool()
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


  override def parallel[A, B](taskA: => A, taskB: => B):(A,B) =  this.parallel(taskA,taskB)



  def parallel[A, B, C, D](taskA: => A, taskB: => B, taskC: => C, taskD: => D): (A, B, C, D) = {
        val ta =task {
          taskA
        }
        val tb = super.task {
          taskB
        }
        val tc = super.task {
          taskC
        }
        val td = taskD
        (ta.join(), tb.join(), tc.join(), td)
      }


}
