# 🚆 Efficiency comparison with sequential processing

## 1. Secuencial reduction

&#x20;Performs N operations in O(N) steps

1. Initializes the result as the identity value for the reduction operation

* Smallest possible value for the max reduction
* Largest possible value for the min reduction
* 0 for sum reduction
* 1 for product reduction

&#x20;2\. Scan through the input and performs the reduction operation between current result and the current input value from the initial set of elements to be processed

Parallelism is 0 because we are not using parallel computation

### 2. Parallel reduction tree algorithm&#x20;

Performs N-1 operations in Log(N) steps, since there is  a reduction every 2 elements and then this reduction is stored.

Average parallelism is (N-1) /Log(N)  which refers to the operations performed by number of steps.

