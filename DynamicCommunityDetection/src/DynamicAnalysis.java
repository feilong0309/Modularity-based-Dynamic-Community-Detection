import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

/**
 * DynamicAnalysis
 *
 * @author Ludo Waltman
 * @author Nees Jan van Eck
 * @version 1.3.1, 11/23/14
 */

public class DynamicAnalysis {

	private Network network;
	private Clustering clustering;
	private Network newNetwork;
	private Clustering newCluster;
	private int sn;
	private int en;
	private double wt;
	
	public DynamicAnalysis(Network network, int sn, int en, double wt, Clustering clustering){
		this.network=network;
		this.clustering=clustering;
//		this.newNetwork=newNetwork;
		this.sn=sn;
		this.en=en;
		this.wt=wt;
	}
	
	public Clustering networkUpdate() throws IOException{
		int i, endNode;
		int startNode=-1;
		double EdgeWeight=0;
		int clusterStartNode,clusterEndNode;
//		HashSet<Integer> hs = new HashSet<Integer>();
		clustering.orderClustersByNNodes();//need test

		startNode=sn;
		endNode=en;
		EdgeWeight=wt;
		
//		startNode=newNetwork.neighbor[0]>newNetwork.neighbor[1]?newNetwork.neighbor[1]:newNetwork.neighbor[0];				
//		endNode=newNetwork.neighbor[1]>newNetwork.neighbor[0]?newNetwork.neighbor[1]:newNetwork.neighbor[0];
//		EdgeWeight=newNetwork.getEdgeWeights(startNode)[0];
//		System.out.println("EdgeWeight: "+EdgeWeight);
		
//		if(network.neighbor[startNode]==0 || endNode>network.nNodes) {//new node included, case 3 or 4
		if(endNode>=network.nNodes) {//new node included, case 3 or 4
			newCluster=(Clustering)clustering.clone();
			
			if(network.hs.contains(startNode)&& !network.hs.contains(endNode)){//case 3
				System.out.println("case 3");
				newCluster.nNodes=clustering.nNodes+1;
				newCluster.cluster=new int[newCluster.nNodes];
				newCluster.nClusters=clustering.nClusters;
				for(i=0;i<clustering.nNodes;i++){
					newCluster.cluster[i]=clustering.cluster[i];
				}
				newCluster.cluster[newCluster.nNodes-1]=clustering.cluster[startNode];
			}
			else if(!network.hs.contains(startNode)&& !network.hs.contains(endNode)){//case 4
				System.out.println("case 4");
				newCluster.nNodes=clustering.nNodes+2;
				newCluster.nClusters=clustering.nClusters+1;
				newCluster.cluster=new int[newCluster.nNodes];
				for(i=0;i<clustering.nNodes;i++){
					newCluster.cluster[i]=clustering.cluster[i];
				}
				newCluster.cluster[newCluster.nNodes-2]=clustering.nClusters;
				newCluster.cluster[newCluster.nNodes-1]=clustering.nClusters;
			}
				
		}else{//case 1 or 2
			clusterEndNode=clustering.cluster[endNode];
			clusterStartNode=clustering.cluster[startNode];	
			if(clustering.cluster[startNode]==clustering.cluster[endNode]){
				int[] vertex=Arrays.copyOfRange(network.neighbor, network.firstNeighborIndex[startNode], network.firstNeighborIndex[startNode + 1]);
				int endNodeIndex=-1;
				for(int k=0;k<vertex.length;k++)
				{
					if(vertex[k]==endNode)
					{
						endNodeIndex=k;
						break;
					}
				}
				if(endNodeIndex==-1)//case 1
				{
					System.out.println("case 1");
					newCluster=(Clustering)clustering.clone();
				}
				else//case 6
				{
					double OldEdgeWeight=network.getEdgeWeights(startNode)[endNodeIndex];
//					System.out.println("Old EdgeWeight: "+OldEdgeWeight);
					if(EdgeWeight>=OldEdgeWeight)//case 6
					{
						System.out.println("case 6-1");
						newCluster=(Clustering)clustering.clone();
					}
					else if(EdgeWeight<OldEdgeWeight)//case 6 re-calculate
					{
						System.out.println("case 6-2");
						Case6(startNode,endNode,EdgeWeight);
					}
				}
			}
			else{
				int[] vertex=Arrays.copyOfRange(network.neighbor, network.firstNeighborIndex[startNode], network.firstNeighborIndex[startNode + 1]);
				int endNodeIndex=-1;
				for(int k=0;k<vertex.length;k++)
				{
					if(vertex[k]==endNode)
					{
						endNodeIndex=k;
						break;
					}
				}
				if(endNodeIndex==-1)//case 2
				{
					System.out.println("case 2");
					if(mergeClusterCase2(startNode,endNode)==true){
						//merge two cluster
						newCluster=(Clustering)clustering.clone();
						for(i=0;i<network.nNodes;i++){
							if(newCluster.cluster[i]==clusterEndNode) 
								newCluster.cluster[i]=clusterStartNode;				
						}
						
					}else{
						newCluster=(Clustering)clustering.clone();
					}
				}
				else//case 7
				{
					double OldEdgeWeight=network.getEdgeWeights(startNode)[endNodeIndex];
//					System.out.println("Old EdgeWeight: "+OldEdgeWeight);
					if(EdgeWeight>OldEdgeWeight)//case 7 re-calculate
					{
						System.out.println("case 7-2");
						//TODO
						if(mergeClusterCase7(startNode,endNode,OldEdgeWeight)==true){
							//merge two cluster
							newCluster=(Clustering)clustering.clone();
							for(i=0;i<network.nNodes;i++){
								if(newCluster.cluster[i]==clusterEndNode) 
									newCluster.cluster[i]=clusterStartNode;				
							}
							
						}else{
							newCluster=(Clustering)clustering.clone();
						}
					}
					else if(EdgeWeight<=OldEdgeWeight)//case 7
					{
						System.out.println("case 7-1");
						newCluster=(Clustering)clustering.clone();
					}
				}
			}
		}
		
		
		newCluster.orderClustersByNNodes();
		return newCluster;
	}
	
	private double[] innerEdgeWeight(){//sum(c,tot)
		int j,k;
		double[] clusterTotWeight = new double[clustering.nClusters];
		for(k=0;k<clustering.nClusters;k++){
			clusterTotWeight[k]=0;
		}
		for(k=0;k<clustering.nClusters;k++){
			for(j=0;j<network.nNodes;j++){
				if(clustering.cluster[j]==k){
	           	 clusterTotWeight[k]+=network.edgeWeight[k];
	            }
			}
		}
		return clusterTotWeight;
	}
	//TODO
	private void Case6(int startNode,int endNode,double EdgeWeight) throws IOException
	{
		HashSet<Integer> vertexSet=new HashSet<Integer>();
		HashSet<Integer> clusterSet=new HashSet<Integer>();
		int[] vt_st=Arrays.copyOfRange(network.neighbor, network.firstNeighborIndex[startNode], network.firstNeighborIndex[startNode + 1]);
		int[] vt_end=Arrays.copyOfRange(network.neighbor, network.firstNeighborIndex[endNode], network.firstNeighborIndex[endNode + 1]);

		for(int i:vt_st)
		{
			vertexSet.add(i);
		}
		
		for(int i:vt_end)
		{
			vertexSet.add(i);
		}

		for (int s : vertexSet) {
			clusterSet.add(clustering.cluster[s]);
		}
		
		for(int i=0;i<clustering.cluster.length;i++)
		{
			if(clusterSet.contains(clustering.cluster[i]))
			{
				vertexSet.add(i);
			}
		}
		
		System.out.println("clusterSet: "+clusterSet);
		System.out.println("vertexSet: "+vertexSet);
		
		ArrayList<Integer> vertexList=new ArrayList<Integer>(vertexSet);
		System.out.println("vertexList: "+vertexList);
		
		HashMap<Integer,Integer> vertexMapTempKey=new HashMap<Integer,Integer>();
		HashMap<Integer,Integer> vertexMapRealKey=new HashMap<Integer,Integer>();
		for(int i=0;i<vertexSet.size();i++)
		{
			vertexMapTempKey.put(i, vertexList.get(i));
			vertexMapRealKey.put(vertexList.get(i), i);
		}
		System.out.println("vertexMapTempKey: "+vertexMapTempKey);
		System.out.println("vertexMapRealKey: "+vertexMapRealKey);
		
		BufferedReader br = new BufferedReader(new FileReader("network"));
		String line="";
		PrintWriter pw=new PrintWriter("network2");
		while ((line=br.readLine()) != null)
		{
			String[] lines=line.split("\t");
			if(vertexList.contains(Integer.parseInt(lines[0]))
				&& vertexList.contains(Integer.parseInt(lines[1])))
			{
				if(lines[0].equals(startNode+"") && lines[1].equals(endNode+""))
				{					
					pw.println(vertexMapRealKey.get(startNode)+"\t"+vertexMapRealKey.get(endNode)+"\t"+EdgeWeight);
				}
				else
				{
					if(lines.length==3)
					{
						pw.println(vertexMapRealKey.get(Integer.parseInt(lines[0]))+"\t"+vertexMapRealKey.get(Integer.parseInt(lines[1]))+"\t"+lines[2]);
					}
					else
					{
						pw.println(vertexMapRealKey.get(Integer.parseInt(lines[0]))+"\t"+vertexMapRealKey.get(Integer.parseInt(lines[1])));
					}
					
				}
			}
		}		
		pw.close();
		br.close();
		/***********************************************/
		//TODO
		int maxClusterLabel=-1;
		for (int s : clustering.cluster) {
			if(maxClusterLabel<s)
				maxClusterLabel=s;
		}
		maxClusterLabel=maxClusterLabel+1;
		
		int[] ClusterChange=ModularityCalculate(maxClusterLabel);
		
		newCluster=(Clustering)clustering.clone();
		newCluster.nNodes=clustering.nNodes;
		newCluster.cluster=new int[newCluster.nNodes];
		System.out.println(clustering.nClusters+"\t"+clusterSet.size()+"\t"+ClusterChange.length);
		newCluster.nClusters=clustering.nClusters-clusterSet.size()+ClusterChange.length;
		
		for(int i=0;i<clustering.nNodes;i++){
			newCluster.cluster[i]=clustering.cluster[i];
		}		
		for(int i=0;i<ClusterChange.length;i++)
		{
			newCluster.cluster[vertexMapTempKey.get(i)]=ClusterChange[i];
//			System.out.println(ClusterChange[i]);
		}
		
		ModularityOptimizer.writeOutputFile("out_before_reorder", newCluster);
	}
	
	public int[] ModularityCalculate(int maxClusterLabel) throws IOException
	{
		String inputFileName ="network2";
		String outputFileName = "output2";
		double resolution = Double.parseDouble("1.0");
		int nRandomStarts = Integer.parseInt("10");
	    int nIterations = Integer.parseInt("10");
	    long randomSeed = Long.parseLong("0");
	    Network network_temp=ModularityOptimizer.readInputFile(inputFileName);
	    int modularityFunction = Integer.parseInt("1");
	    
	    /**********************************************************************************/
	    double resolution2 = ((modularityFunction == 1) ? (resolution / (2 * network_temp.getTotalEdgeWeight() + network_temp.totalEdgeWeightSelfLinks)) : resolution);

        long beginTime = System.currentTimeMillis();
        Clustering clustering2 = null;
        
        double maxModularity = Double.NEGATIVE_INFINITY;
        Random random = new Random(randomSeed);
        VOSClusteringTechnique VOSClusteringTechnique;
        boolean update;
        double modularity;
        for (int i = 0; i < nRandomStarts; i++)
        {
            VOSClusteringTechnique = new VOSClusteringTechnique(network_temp, resolution2);//initiate cluster

            int j = 0;
            update = true;
            do
            {
                update = VOSClusteringTechnique.runLouvainAlgorithm(random);
                j++;
                modularity = VOSClusteringTechnique.calcQualityFunction();
            }
            while ((j < nIterations) && update);

            if (modularity > maxModularity)
            {
            	clustering2 = VOSClusteringTechnique.getClustering();
                maxModularity = modularity;
            }
        }
        long endTime = System.currentTimeMillis();
        /**********************************************************************************/
//        System.out.println(clustering2.cluster.length);
        int[] clusterReturn=new int[clustering2.cluster.length];
        
        for(int i=0;i<clustering2.cluster.length;i++)
        {
        	clusterReturn[i]=clustering2.cluster[i]+maxClusterLabel;
//       	System.out.println(clusterReturn[i]);
        }
        ModularityOptimizer.writeOutputFile(outputFileName, clustering2);
        
        return clusterReturn;
	}
	
	private boolean mergeClusterCase7(int startNode,int endNode, double oldEdgeWeight)
	{
//		double wij=newNetwork.nodeWeight[startNode];
		double deltaWij=this.wt-oldEdgeWeight;
		double m=network.getTotalEdgeWeight();		
		double sumTotCi,sumTotCj;
		int clusterStartNode=clustering.cluster[startNode];
		int clusterEndNode=clustering.cluster[endNode];
		sumTotCi=innerEdgeWeight()[clusterStartNode];
		sumTotCj=innerEdgeWeight()[clusterEndNode];
		if(deltaWij*(m+deltaWij)>(sumTotCi+deltaWij)*(sumTotCj+deltaWij)) return true;
		return false;
	}
	private boolean mergeClusterCase2(int startNode,int endNode){
//		double wij=newNetwork.nodeWeight[startNode];
		double wij=this.wt;
		double m=network.getTotalEdgeWeight();
		double sumTotCi,sumTotCj;
		int clusterStartNode=clustering.cluster[startNode];
		int clusterEndNode=clustering.cluster[endNode];
		sumTotCi=innerEdgeWeight()[clusterStartNode];
		sumTotCj=innerEdgeWeight()[clusterEndNode];
		if(wij*(m+wij)>(sumTotCi+wij)*(sumTotCj+wij)) return true;
		return false;
	}
	
	
	
}

