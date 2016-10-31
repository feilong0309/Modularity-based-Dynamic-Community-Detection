# Modularity-based-Dynamic-Community-Detection
## acknowledgement

The code is based on @Ludo Waltman and @Nees Jan van Eck 's Louvain Modularity Community Detection java code. 

## Code Example


## Motivation

In this project, we propose a dynamic modularity-based community detection method. It can solve the community detection problem in a heuristic way using the information from the previous computation. Our method uses almost the same computation time for each iteration while preserves the similar precision and derive new communities in a dynamic way.

## Tests

Run code under RandomNetwork folder to generate community-based network data and dynamicly added new network data.
Run code under DynamicCommunityDetection.
	1) We detect the community based on original network using Louvain Modularity Method.
	2) According to newly added network data, we update the community structure instead of running Louvain twice.
Run code under RandIndex folder to evaluate the result.

## Contributors

Pei Zhang & Di Zhuang


