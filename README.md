


# 🌳 Partial parallel reduction tree

Link to the GitBook https://sandras-organization-1.gitbook.io/luxoft-test-parallel-reduction-tree-for-max-func/

This is a widely used parallel computation pattern. Parallel reduction tree is the tree generated for processing a set of values using a function to provide a result for the set.

### Reduction

A reduction refers to a function that yields a scalar value for the set   Examples of such functions are sum for the elements of an array; or counting such elements.

The condition for a reduction function to work in a parallel processing is that:

* It's an associative function
* The function has an identity ( 0 for sum; 1 for product, and so on)

### Parallel processing

Parallel adjective refers to the way the calculation is performed. If we wanted to perform the sum of the elements of an array we could process all the elements sequentially and we could obtain the a correct result. However, we could use a parallel processing of the array by applying the sum of the array by every 2 elements and creating a new array with every execution until obtaining the final sum.

Parallel processing can be used to optimize the timing of the execution taking advantage of the processors available.

### Why "partial"?

The "partial"  word refers to  the fact that the reduction is done several times for partial sections of the array, list, or set of elements to be processed.

For instance, if there is a set of 10 elements , you can apply the reduction every 2 elements and then reduce again, until obtaining the overall reduction.

You can also choose to reduce only half of the elements of an array, so the reduction can be applied to any subset of elements while the rest is processed sequentially, for example. Since the reduction function is associative, the result should not change.

This algorithm uses intermediate steps in which results should be stored to combine both execution and memory complexity to optimize time performance.



### Why "tree"?

The tree is the structure used to generate partial results for the reduction. A tree is used since it can store intermediate results at every step, until having the final reduction.

Leaves of the tree are the original elements to be processed and Nodes are intermediate reductions. Root node should be the final reduction.



## Efficiency comparison with sequential processing

### 1. Secuencial reduction <a href="#1.-secuencial-reduction" id="1.-secuencial-reduction"></a>

Performs N operations in O(N) steps

1. 1.Initializes the result as the identity value for the reduction operation

* Smallest possible value for the max reduction
* Largest possible value for the min reduction
* 0 for sum reduction
* 1 for product reduction

2\. Scan through the input and performs the reduction operation between current result and the current input value from the initial set of elements to be processedParallelism is 0 because we are not using parallel computation

#### 2. Parallel reduction tree algorithm  <a href="#2.-parallel-reduction-tree-algorithm" id="2.-parallel-reduction-tree-algorithm"></a>

Performs N-1 operations in Log(N) steps, since there is a reduction every 2 elements and then this reduction is stored.Average parallelism is (N-1) /Log(N) which refers to the operations performed by number of steps.​

<figure><img src=".gitbook/assets/Screen Shot 2023-01-16 at 1.06.54 PM.png" alt=""><figcaption><p>Partial parallel reduction tree depiction Source: <a href="https://cs.wmich.edu/gupta/teaching/cs5260/5260Sp15web/lectureNotes/ece408-lecture11-reduction-tree-2011.pdf">Parallel Computation Patterns – Reduction Trees</a></p></figcaption></figure>

## Solution

_Method implementations, tests and changes on the original code. These are in the file_&#x20;

{% embed url="https://github.com/slancheros/luxoft/blob/master/src/main/scala/org/luxoft/slancheros/test/Scan.scala" %}

### 1. Sequential upsweep

It traverses an array from the beginning to a specified position to obtain the max reduction for the Array section.

```scala
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
```

### 2.Sequential down sweep&#x20;

it traverses array from the end to a specified position obtaining the max reduction for the Array section

```scala
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
  }scal
```

### 3. Parallel upsweep

This is a function that creates the eduction tree that can be used to get the max element for a given section of the array. The threshold value is used to determine a set of elements for which a sequential solution can be applied.&#x20;

```scala
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
  
```

### 4. Parallel down sweep

This function will use the reduction tree created in the parallel upsweep to evaluate and return the max element.

```scala
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
```

## Tests run

Test are in the file&#x20;

{% embed url="https://github.com/slancheros/luxoft/blob/master/src/test/scala/org/luxoft/slancheros/test/ScanTest.scala" %}

Only 2 methods are using the scalameter call. The tests for parallel execution are not using scalameter, since they are throwing a StackOverflow error.

### 1.Sequential Upsweep tests

* Tests that the reduction is correct

```scala
@Test
  def testSequentialUpsweepWithAllElements() = {
    var myList: Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    var output = scan.sequentialUpsweep(myList, 0, myList.length)
    assertTrue(output == 3.8f)
  }
```

<figure><img src=".gitbook/assets/Screen Shot 2023-01-16 at 11.05.34 AM.png" alt=""><figcaption><p>Sequential upsweep test running</p></figcaption></figure>

* Tests that the reduction is correct for half of the elements of the array

```scala
@Test
  def testSequentialUpsweepWithHalfElements() = {
    var myList: Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    var output = scan.sequentialUpsweep(myList, 0, 2)
    assertFalse(output == 3.8f)
    assertTrue(output == 2.3f)
  }
```

<figure><img src=".gitbook/assets/Screen Shot 2023-01-16 at 11.13.27 AM.png" alt=""><figcaption><p>Sequential upsweep test for half elements running</p></figcaption></figure>

### 2.Sequential Down sweep tests

* All elements

```scala
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
  
```

<figure><img src=".gitbook/assets/Screen Shot 2023-01-16 at 11.48.17 AM.png" alt=""><figcaption></figcaption></figure>

* Half elements

```scala
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
```

<figure><img src=".gitbook/assets/Screen Shot 2023-01-16 at 11.24.29 AM.png" alt=""><figcaption><p>Sequential Down sweep half elements</p></figcaption></figure>

### 3. Parallel upsweep test

```scala
@Test
  def testUpsweep() = {
    val myList: Array[Float] = Array(1.5f, 2.3f, 3.1f, 3.8f)
    val output = scan.upsweep(myList, 0, 2,1 )
    assertTrue(output.maxPrevious.floatValue() == 3.8)
  }

```

The test takes a long time to run. This because I catch the error in the ForkJoinPool for the StackOverflow one that was appearing previously on the test.

<figure><img src=".gitbook/assets/Screen Shot 2023-01-16 at 11.56.56 AM.png" alt=""><figcaption><p>The test doesn't run because the Stack Overflow error happens</p></figcaption></figure>

###

I created a test with 2 elements to check if it happened in smaller arrays because of the many elements for processing.

```scala
 @Test
  def testPar2elements() = {
    val input: Array[Float] = Array(1.5f, 2.3f)
    val output = scan.upsweep(input, 0, 2, 1)
    assert(true)
  }
```

<figure><img src=".gitbook/assets/Screen Shot 2023-01-16 at 12.12.59 PM.png" alt=""><figcaption><p>This very trivial test also fails (with StackOverflow error)</p></figcaption></figure>

I temporarily created a simpler recursive count and discovered that the Stack Overflow happens in the forking for the task for the ForkJoinPool.

### 4. Parallel down sweep test

```scala
@Test
  def testParDownsweepTree = {
    val input: Array[Float] = Array(8.5f, 6.3f,0.8f, 4.6f)
    val tree : Tree = scan.upsweep(input, 0, 4, 1)
    val output: Array[Float] = Array()
    scan.downsweep(input, output,startingValue = 6f,tree)
    assertTrue(output.sameElements(Array(8.5f,6.3f,6f,6f)))
  }
```

The result output Array from the execution, should check that 6f is bigger of the last 2 elements, however for the other remaining 2, the current values are bigger so 6.3f and 8.5f should stay as the max values on the array in their respective positions.

<figure><img src=".gitbook/assets/Screen Shot 2023-01-16 at 11.59.52 AM.png" alt=""><figcaption></figcaption></figure>

As previous parallel implementation this one also gets a Stack Overflow error.

## Theory about the Stack Overflow error

I have tweaked the scheduler implementation after seeing that every parallel implementation got the same error.  My conclusion is that the forking of the task requires more work to use memory efficiently and being able to close the fork at some point. Honestly I don't what to improve to fix this error.

I refactored the scheduler implementation in the package code here:

{% embed url="https://github.com/slancheros/luxoft/blob/master/src/main/scala/org/luxoft/slancheros/test/package.scala" %}
package.scala
{% endembed %}

```scala
val forkJoinPool = new ForkJoinPool(2)
```

One of the tweaks was to adjust the parallelism parameter and this changed the original error, so I reckon this is something needed according the amount of processors  required.

Also I added try/catch to the execution of the thread, and in the one where the Exception is not specific, added the "exception occurred" message.

I also added a finally  to close the task, and I thought this could help avoid the memory expense, therefore solving the issue.

## Planned analysis for scalameter

Since the parallel methods didn't work for me, currently I don't have an analysis to compare sequential vs parallel execution.

If the implementations would work I anticipate that the parallel execution would run faster, specially for longer arrays. However there are many factors that affect the time measure for the executions even for the same sequential implementation: the processor can optimize for subsequent executions of the same method, however it can raise the time again. The time used for processing will tend to decrease, however we would find some outliers.

So the plan would be to use the same array and use the sequential method, measure time and check for the result, then compare with the parallel method, and checking the same reduction is obtained, then comparing several times the time measurement to find patterns.

Same tests but with different amount of elements in the array would give us the pattern in which it helps (or not) to have parallel executions.
