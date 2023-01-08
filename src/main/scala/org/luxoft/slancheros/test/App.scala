package org.luxoft.slancheros.test

  object App extends App {
    val scan:Scan = new Scan()
    var myList:Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    var x:Array[Float] = myList
      scan.sequentialScan(myList,x)

    println(" This is the result of first sequential scan: ")
    x.foreach(println)

  }

