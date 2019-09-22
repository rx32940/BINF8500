# BINF8500

First Project:
  - QuickSort:
    - IndexedSeq.java
    - sortFastq.java
    - quickSortNum.java (not part of the assignment, just for algorithm)
 ```
javac sortFastq.java IndexedSeq.java
java sortFastq /absolute/path/to/fastq/file /absolute/path/to/output/file
 ```
Second Project:
 - Kmeans:
    - Kmeans.java (capital K)
    - DataPoint.java
    - Cluster.java

- output file will be in the same dir as input
```
javac Kmeans.java DataPoint.java Cluster.java
java k /absolute/path/to/input/file
```
- to run multiple k's for WCSS
```
bash runAllK.sh
```
- to plot runAllK.sh results for WCSS/AIC/BIC results
use R code:
plot_kmeans.R 

