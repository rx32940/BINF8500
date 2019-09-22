#!/bin/bash

path="/Users/rachel/Downloads/kmeans/data"

BA="/Users/rachel/Downloads/kmeans/data/Bacteria+Archaea.txt"
B="/Users/rachel/Downloads/kmeans/data/Bacteria.txt"
A="/Users/rachel/Downloads/kmeans/data/Archaea.txt"

BAO="/Users/rachel/Downloads/kmeans/kmeans_results/B+A/"
BO="/Users/rachel/Downloads/kmeans/kmeans_results/Bacteria/"
AO="/Users/rachel/Downloads/kmeans/kmeans_results/Archaea/"


for i in {1..15}; do

    java Kmeans $i $BA
    java Kmeans $i $B
    java Kmeans $i $A

done


mv $path/Bacteria_* $BO
mv $path/Archaea_* $AO
mv $path/Bacteria+Archaea_* $BAO