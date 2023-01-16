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







