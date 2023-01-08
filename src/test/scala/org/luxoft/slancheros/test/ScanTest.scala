package org.luxoft.slancheros.test

import org.junit._
import Assert._
import org.luxoft.slancheros.test.App.scan

@Test
class ScanTest {



  @Test
  def testSequentialUpsweepWithAllElements() = {
    var myList: Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    val scan:Scan = new Scan()
    var output = scan.sequentialUpsweep(myList, 0, myList.length)
    assertTrue(output == 3.8f)
  }

  @Test
  def testSequentialUpsweepWithHalfElements() = {
    var myList: Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    val scan:Scan = new Scan()
    var output = scan.sequentialUpsweep(myList, 0, 2)
    assertFalse(output == 3.8f)
    assertTrue(output == 2.3f)
  }

  @Test
  def testSequentialDownsweepWithAllElements() = {
    var myList: Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    var output:Array[Float] = myList
    val startingValue:Float = 3f
    scan.sequentialDownsweep(myList,output, startingValue, 0, myList.length)
    assertTrue(output == 3.8f)
  }

  @Test
  def testSequentialDownsweepWithHalfElements() = {
    var myList: Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    val scan:Scan = new Scan()
    var output = scan.sequentialUpsweep(myList, 0, myList.length)
    assertTrue(output == 3.8f)
  }


}
