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
    - runAllK.sh
    - plot_kmeans.R 

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

Third Project:
- Alignment.java

```
javac Alignment.java
java Alignment /path/to/first/fasta /path/to/second/fasta match mismatch gap
```

Fourth Project:
- PSSM.java
```
javac PSSM.java
java PSSM /path/to/train/motifs/fasta/txt /path/to/sequence/file cutoffscore
```

Fifth project:
- GibbsSampler.java
- Sequence.java
```
javac GibbsSampler.java Sequence.java
java GibbsSampler /path/to/sequences/to/look/for/motifs initialEstimateMotifLength
