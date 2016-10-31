import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class MainClass {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String fileName1="cluster1";
		String fileName2="cluster2";
		System.out.println("RandIndex:"+readInputFile(fileName1,fileName2));
		
		
	}

	 private static double readInputFile(String fileName1, String fileName2) throws IOException
	 {
	        BufferedReader bufferedReader;
	        int nLines,i,j;
	        int a=0,b=0,c=0,d=0;
	        HashMap<Integer,Integer> hm1=new HashMap<Integer,Integer>();
	        HashMap<Integer,Integer> hm2=new HashMap<Integer,Integer>();
//	        HashMap<Integer,HashSet> hm1Cluster=new HashMap<Integer,HashSet>();//key: cluster number; value node
	        bufferedReader = new BufferedReader(new FileReader(fileName1));
	        nLines = 0;
	        String line="";
	        while ((line=bufferedReader.readLine()) != null){	
	        	hm1.put(nLines, Integer.parseInt(line));	
	        	nLines++;
	        }   
	        bufferedReader.close();
	        nLines=0;
	        bufferedReader = new BufferedReader(new FileReader(fileName2));
	        while ((line=bufferedReader.readLine()) != null){	
	        	hm2.put(nLines, Integer.parseInt(line));	
	        	nLines++;
	        }    
	        System.out.println("nLines"+nLines);
	        bufferedReader.close();
//	        for(i=0;i<hm1.size();i++){	        	
//	        	if(hm1Cluster.containsKey(hm1.get(i)))
//	        	{
//	        		HashSet<Integer> hs = hm1Cluster.get(hm1.get(i));
//	        		hs.add(i);	        		
//	        	}
//	        	else
//	        	{
//	        		HashSet<Integer> hs=new HashSet<Integer>();
//	        		hs.add(i);
//	        		hm1Cluster.put(hm1.get(i), hs);
//	        	}
//	        }
	        System.out.println("hm1"+hm1);
	        System.out.println("hm2"+hm2);
	        for(i=0;i<nLines;i++){
	        	for(j=0;i<nLines;i++){
	        		if(hm1.get(i)==hm1.get(j))
	        			if(hm2.get(i)==hm2.get(j)){
	        				a++;
	        			}
	        			else{
	        				c++;
	        			}
	        		else{
	        			if(hm2.get(i)==hm2.get(j)){
	        				d++;
	        			}else{
	        				b++;
	        			}
	        		}
	        		
	        	}
	        }
	        System.out.println("a: "+a+", b: "+b+", c: "+c+", d: "+d);
	        double R=(double)(a+b)/(double)(a+b+c+d);
	        return R;
	        
	 }
	
}
