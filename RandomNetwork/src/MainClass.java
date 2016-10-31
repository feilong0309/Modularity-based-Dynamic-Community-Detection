import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

public class MainClass {
	
	static int totGroupNum=10;
	static int vertexNum=500;
	static int maxConnectionPerVertex=10; 
	static int minConnectionPerVertex=2;
	static double rangeMin=1.2;
	static double rangeMax=2.5;
	static int randomCutChance=100;//out of 1000
	static int numOfNodeExtract=50;
 
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
		int runtime=10;
		int id;
		for(id=0;id<runtime;id++){
			DatasetGen(id);
		}
	}
	
	public static void DatasetGen(int id) throws FileNotFoundException, UnsupportedEncodingException{
		PrintWriter writer;
		HashMap<Integer, LinkedList> hm=new HashMap<Integer, LinkedList>();
		int[] groupInfo=new int[vertexNum];
		Random random=new Random();
		int max;
		int min;
		int rand;
		max=totGroupNum;
		for(int i=0;i<vertexNum;i++){
			rand=random.nextInt(max);
			groupInfo[i]=rand;
			if(hm.containsKey(rand)){
				hm.get(rand).add(i);
			}else{
				LinkedList ll=new LinkedList();
				ll.add(i);
				hm.put(rand,ll);
			}
			
		}
		
		writer = new PrintWriter(id+"_Network Info", "UTF-8");
		writer.println("Number of node: "+vertexNum);
		writer.println("Number of community: "+totGroupNum);
		writer.println("maxConnectionPerVertex: "+maxConnectionPerVertex);
		writer.println("minConnectionPerVertex: "+minConnectionPerVertex);
		writer.println("weight range: "+rangeMin+"~"+rangeMax);
		writer.println("Node Extract:"+numOfNodeExtract);
		writer.println("Add new edge chance:"+(double)randomCutChance/10.0+"%");
		writer.println(hm);
		
		writer.close();
			
//assign number of edges to each vertex		
		int[] connectionNum=new int[vertexNum];
		max=maxConnectionPerVertex;
		min= minConnectionPerVertex;
		for(int i=0;i<vertexNum;i++){
			connectionNum[i]=random.nextInt(max-min+1)+min;
		}
		
		
//construct new network		
		double[][] netlist=new double[vertexNum][vertexNum];
		for(int i=0;i<vertexNum;i++){//initiate netlist
			for(int j=0;j<vertexNum;j++) 
				netlist[i][j]=0;
		}
		
		
		int analyseVertex;
		int analyseGroup;
		int randomGroup;
		int randomVertexInTheGroup;
		double randomWeight;
		for(int i=0;i<totGroupNum;i++){
			analyseGroup=i;
			for(int j=0;j<hm.get(i).size();j++){
				max=1000;
				min=1;
				analyseVertex=(int) hm.get(i).get(j);
				for(int k=0;k<connectionNum[analyseVertex];k++){
					if(random.nextInt(max-min+1)+min>=800){//80% chance connect to its own group
						//connect with other group
						do{
							randomGroup=random.nextInt(totGroupNum);
						}while(analyseGroup==randomGroup);
						randomVertexInTheGroup=(int) hm.get(randomGroup).get(random.nextInt((int)hm.get(randomGroup).size()));				
					}else{
						//connect within it own group
						do{
							randomVertexInTheGroup=(int) hm.get(analyseGroup).get(random.nextInt((int)hm.get(analyseGroup).size()));
						}while(analyseVertex==randomVertexInTheGroup);
					}
					randomWeight=rangeMin + (rangeMax - rangeMin) * random.nextDouble();
					netlist[randomVertexInTheGroup][analyseVertex]+=randomWeight;
					netlist[analyseVertex][randomVertexInTheGroup]+=randomWeight;
				}
		
			}
		}
		
				
		writer = new PrintWriter(id+"_network", "UTF-8");
		for(int i=0;i<vertexNum;i++){
			for(int j=i+1;j<vertexNum;j++){
				if(netlist[i][j]!=0){
					writer.println(i+"\t"+j+"\t"+netlist[i][j]);
				}
			}
		}
		writer.close();
		
		
		
//chooseRandomType:1-new edge,2-weight increase,3-weight decrease.
		System.out.println("Extracting Node...");			
		int dynamicEdgeNum=0;

		int chooseRandomType;
		double[][] dynamicNetlist=new double[vertexNum][vertexNum];
		for(int i=0;i<vertexNum;i++){//initiate
			for(int j=0;j<vertexNum;j++){
				dynamicNetlist[i][j]=0;
			}
		}
		int[] extractNode = new int[numOfNodeExtract];
		boolean dataFeasibility;
		for(int i=0;i<numOfNodeExtract;i++){
			extractNode[i]=vertexNum-i-1;			
		}
		for(int i=0;i<numOfNodeExtract;i++){
			for(int j=0;j<vertexNum;j++){
				if(netlist[extractNode[i]][j]!=0){
					//remove node and check
					dynamicNetlist[extractNode[i]][j]=netlist[extractNode[i]][j];
//					System.out.println("dynamicNetlist["+extractNode[i]+"]["+j+"]="+dynamicNetlist[extractNode[i]][j]);
					dynamicNetlist[j][extractNode[i]]=netlist[extractNode[i]][j];
					netlist[extractNode[i]][j]=0;
					netlist[j][extractNode[i]]=0;
					
//					for(int ii=0;ii<vertexNum-numOfNodeExtract;ii++){
//						dataFeasibility=false;
//						for(int jj=0;jj<vertexNum-numOfNodeExtract;jj++){
//							if(netlist[ii][jj]!=0)
//								dataFeasibility=true;
//						}
//						if(dataFeasibility==false){
//							System.out.println("data error!");
//							System.out.println("error edge:["+ii+"]");
//							break;
//						}
//					}
				}				
			}
		}
				
		System.out.println("Running Edge Replacement...");	
				
		for(int i=0;i<vertexNum;i++){
			for(int j=i+1;j<vertexNum;j++){
				if(netlist[i][j]!=0){
					if(random.nextInt(1000)+1<randomCutChance){
						chooseRandomType=random.nextInt(3-1+1)+1;
						if(chooseRandomType==1){
							dynamicNetlist[i][j]=netlist[i][j];
							dynamicNetlist[j][i]=netlist[i][j];
							netlist[i][j]=0;
							netlist[j][i]=0;
						}else if(chooseRandomType==2){
							randomWeight=0.1 + (netlist[i][j]-0.1) * random.nextDouble();
							dynamicNetlist[i][j]=netlist[i][j];
							dynamicNetlist[j][i]=netlist[i][j];
							netlist[i][j]=randomWeight;
							netlist[j][i]=randomWeight;
						}else if(chooseRandomType==3){
							randomWeight=netlist[i][j]+(rangeMin+ (rangeMax-rangeMin) * random.nextDouble());
							dynamicNetlist[i][j]=netlist[i][j];
							dynamicNetlist[j][i]=netlist[i][j];
							netlist[i][j]=randomWeight;
							netlist[j][i]=randomWeight;
						}	
						dynamicEdgeNum++;
					}
				}
			}
		}
		
		
		writer = new PrintWriter(id+"_dynamic_network", "UTF-8");
		for(int i=0;i<vertexNum;i++){
			for(int j=i+1;j<vertexNum;j++){
				if(netlist[i][j]!=0){
					writer.println(i+"\t"+j+"\t"+netlist[i][j]);
				}
			}
		}
		writer.close();
		
		System.out.println("Checking Dataset...");	
		for(int i=0;i<vertexNum-numOfNodeExtract;i++){
			dataFeasibility=false;
			for(int j=0;j<vertexNum-numOfNodeExtract;j++){
				if(netlist[i][j]!=0)
					dataFeasibility=true;
			}
			if(dataFeasibility==false){
				System.out.println("data error!");
				break;
			}
		}

	
		writer = new PrintWriter(id+"_dynamic_newnetwork", "UTF-8");
		for(int j=0;j<vertexNum;j++){
			for(int i=0;i<j;i++){
				if(dynamicNetlist[i][j]!=0){
					writer.println(i+"\t"+j+"\t"+dynamicNetlist[i][j]);
				}
			}
		}
		writer.close();
		System.out.println("Complete:"+id);	
	}
	
	
}

