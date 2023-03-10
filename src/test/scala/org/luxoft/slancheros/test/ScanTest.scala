package org.luxoft.slancheros.test

import org.junit._
import org.scalameter._
import Assert._
import org.luxoft.slancheros.test.Tree.{Leaf, Node}


@Test
class ScanTest {

  val scan:Scan = new Scan()

  @Test
  def testSequentialUpsweepWithAllElements() = {
    var myList: Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    var output = scan.sequentialUpsweep(myList, 0, myList.length)
    assertTrue(output == 3.8f)
  }

  @Test
  def testSequentialUpsweepWithHalfElements() = {
    var myList: Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    var output = scan.sequentialUpsweep(myList, 0, 2)
    assertFalse(output == 3.8f)
    assertTrue(output == 2.3f)
  }

  @Test
  def testSequentialDownsweepWithAllElements() = {
    var myList: Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    var output:Array[Float] = myList
    val startingValue:Float = 3f
    val time = measure {
      scan.sequentialDownsweep(myList, output, startingValue, 0, myList.length)
    }
    println("This is the time for sequential all elements")
    println(time)
    output.foreach(println)
    assertTrue(output.sameElements(Array(3.8f, 3.8f, 3.8f, 3.8f)))
  }

  @Test
  def testSequentialDownsweepWithHalfElements() = {
    var myList: Array[Float] = Array(3.4f, 2.3f, 3.1f, 3.8f)
    var output:Array[Float] = myList
    val startingValue:Float = 3f
    val time = measure {
        scan.sequentialDownsweep(myList, output, startingValue, 0, 2)
    }

    println("This is the time for sequential half elements")
    println(time)
    output.foreach(println)
    assertTrue(output.sameElements(Array(3.4f, 3f, 3.1f, 3.8f)))
  }

  @Test
  def testUpsweep() = {
    val myList: Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    val output = scan.upsweep(myList, 0, 2,1 )
    assertTrue(output.maxPrevious.floatValue() == 3.8f)
  }




  @Test
  def testPar2elements() = {
    val input: Array[Float] = Array(1.5f, 2.3f)
    val output = scan.upsweep(input, 0, 2, 1)
    assert(true)
  }

  @Test
  def testParDownsweepTree = {
    val input: Array[Float] = Array(8.5f, 6.3f,0.8f, 4.6f)
    val tree : Tree = scan.upsweep(input, 0, 4, 1)
    val output: Array[Float] = Array()
    scan.downsweep(input, output,startingValue = 6f,tree)
    assertTrue(output.sameElements(Array(8.5f,6.3f,6f,6f)))
  }
}
