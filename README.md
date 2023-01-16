---
description: This is an explanation of the algorithm for the implementation requested.
---

# 🌳 Partial parallel reduction tree

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

## Solution

_Method implementations, tests and changes on the original code_

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

