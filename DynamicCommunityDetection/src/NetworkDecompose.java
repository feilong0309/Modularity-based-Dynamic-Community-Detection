import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class NetworkDecompose {
	public int[] startNode;
	public int[] endNode;
	public double[] weight;
	int nLine;
	
	public NetworkDecompose(String fileName) throws IOException{
		nLine=0;
		String[] splitLine;
		BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName));
		while (bufferedReader.readLine()!=null){
			nLine++;
		}
		bufferedReader.close();
        startNode=new int[nLine];
        endNode=new int[nLine];
        weight=new double[nLine];
		bufferedReader = new BufferedReader(new FileReader(fileName));
		for(int i=0;i<nLine;i++){
			splitLine=bufferedReader.readLine().split("\t");
			startNode[i]=Integer.parseInt(splitLine[0]);
			endNode[i]=Integer.parseInt(splitLine[1]);
			weight[i]=Double.parseDouble(splitLine[2]);
		}
	}
	

	
	
}
