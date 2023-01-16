---
description: Method implementations, tests and changes on the original code
---

# 👑 Solution

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
