package subway;



import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class subway_query {
	public static final int Infinity = 9999999;
	public static int[] dijkstra(int[][] weight,int start) {
		int n = weight.length;
		int[] visited  = new int[n];
		int[] distance = new int[n];
		int[] path = new int[n];
		
		int k=start;
		
		for(int i=0;i<n;i++) {
			distance[i] = Infinity;
		}
		distance[start] =0;
		path[start] = start;
		for(int i=0;i<n;i++) {
			visited[k] = 1;
			for(int j=0;j<n;j++) {
				if(visited[j]==0&&distance[k]+weight[k][j]<distance[j]) {
					distance[j] = distance[k]+weight[k][j];
					path[j] = k;
					
				}
			}
			int dmin = Infinity;
			for(int j=0;j<n;j++) {
				if(visited[j]==0&&distance[j]<dmin) {
					dmin = distance[j];
					k = j;
				}
			}
		}
		
		return path;
		
	}
	public Map<String,String[]> getData(String path) {
		String pathname = path;
		Map<String,String[]> line_station = new HashMap<String,String[]>();
		
		
		try (FileReader reader = new FileReader(pathname);BufferedReader br = new BufferedReader(reader)){
			
			String line;
			while((line= br.readLine())!=null) {
				String[] names = line.split(" ");
				line_station.put(names[0], new String[names.length-1]);
				for(int i=1;i<names.length;i++) {
					line_station.get(names[0])[i-1] = names[i];
				}
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
		return line_station;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		subway_query b = new subway_query();
		Map<String,String[]> line_station = null;//每条线路名称对应的各站点
		String target = null;//输出文件
		String[] line = new String[28];//查询的线路
		String sstation = null;//出发站点
		String terminal = null;//目的站点
		if(args[0].equals("-map")) { //获取各个信息
			line_station = b.getData(args[1]);
		}
		else if(args[0].equals("-a")){//获取各个信息
			int t=1;
			while(!args[t].equals("-map")) {
				line[t-1] = args[t];
				t++;
			}
			line_station = b.getData(args[t+1]);
			target = args[t+3];
		
		}
		else if(args[0].equals("-b")) {//获取各个信息
			line_station = b.getData(args[4]);
			target = args[6];
			sstation = args[1];
			terminal = args[2];
		}
		
		Map<String,Integer> station_num = new HashMap<String,Integer>();//站点对应的编号
		Map<Integer,String> num_station = new HashMap<Integer,String>();//编号对应的站点
		Map<String,String[]> station_line = new HashMap<String,String[]>();//站点所属的每条线路
		int count=0;
		Scanner in = new Scanner(System.in);
		
		for(String key : line_station.keySet()) { //生成各个所需字典信息
			for(String name : line_station.get(key)) {
				if(station_num.containsKey(name)) {
					String[] s = new String[station_line.get(name).length+1];
					int i=0;
					for(i=0;i<station_line.get(name).length;i++) {
						s[i] = station_line.get(name)[i];
					}
					s[i] = key;
					station_line.put(name, s);
				}
				else {
					station_num.put(name, count);
					num_station.put(count, name);
					station_line.put(name, new String[1]);
					station_line.get(name)[0] = key;
					count++;
				}
			}
			
		}
		
		int[][] matrix = new int[count][count];//生成站点连通信息的邻接矩阵
		for(int j=0;j<count;j++) {
			for(int k=0;k<count;k++) {
				if(j!=k)
					matrix[j][k] = Infinity;
				
			}
		}
		for(String key : line_station.keySet()) {
			for(int i=1;i<line_station.get(key).length;i++) {
				int m = station_num.get(line_station.get(key)[i-1]);
				int n = station_num.get(line_station.get(key)[i]);
				matrix[m][n] = matrix[n][m] = 1;
			}
			
		}
		matrix[station_num.get("T2航站楼")][station_num.get("三元桥")] = matrix[station_num.get("三元桥")][station_num.get("T2航站楼")] = 1;
		if(args[0].equals("-a")) { //查询线路
			FileWriter fileWriter = null;
			fileWriter = new FileWriter(target);
			for(String line1:line) {
				if(line1!=null) {
					System.out.print(line1+" ");
					fileWriter.write(line1+" ");
					System.out.print("\r\n");
					fileWriter.write("\r\n");
					for(int i=0;i<line_station.get(line1).length;i++) {
						System.out.print(line_station.get(line1)[i]+" ");
						fileWriter.write(line_station.get(line1)[i]+" ");
					}
					System.out.print("\r\n");
					fileWriter.write("\r\n");
				}
			}
			fileWriter.flush();
			fileWriter.close();
			
		}
		else if(args[0].equals("-b")) {//查询最短路径
			FileWriter fileWriter = null;
			fileWriter = new FileWriter(target);
			int s = station_num.get(sstation);int d = station_num.get(terminal);
			int[] path = dijkstra(matrix,s);
			int[] track = new int[count];
			int c=0;
			int t=path[d];
			int num=0;
			track[num] = d;
			while(t!=s) {
				num++;
				track[num] = t;
				t = path[t];
			}
			track[++num] = s;
			String str=null;
			if(station_line.get(num_station.get(track[num])).length!=1) {
				for(String line1 : station_line.get(num_station.get(track[num]))) {
					for(String line2 : station_line.get(num_station.get(track[num-1]))) {
						if(line1.equals(line2)) {
							str = line1;
							break;
						}
					}
				}
			}
			else {
				str = station_line.get(num_station.get(track[num]))[0];
			}
			if(s==d) { // 起点与目的地相同
				System.out.println("无需乘坐！");
				fileWriter.write("无需乘坐！");
				fileWriter.flush();
				fileWriter.close();
				
			}
			else {  //起点与目的地不同
				System.out.println(String.valueOf(num+1));
				fileWriter.write(String.valueOf(num+1)+"\r\n");
				System.out.println(str);
				fileWriter.write(str+"\r\n");
				for(int i=num;i>=0;i--) {
					if(i==0) {
					}
					else {
						if(station_line.get(num_station.get(track[i])).length!=1) {
							for(String line1 : station_line.get(num_station.get(track[i]))) {
								for(String line2 : station_line.get(num_station.get(track[i-1]))) {
									if(line1.equals(line2)) {
										if(!str.equals(line1)) {
											str=line1;
											System.out.println(str);
											fileWriter.write(str+"\r\n");
										}
										break;
									}
									
								}
							}
						}
						else {
						}
					}
					System.out.println(num_station.get(track[i]));
					fileWriter.write(num_station.get(track[i])+"\r\n");
						
				}
				fileWriter.flush();
				fileWriter.close();
			}
			
		}
		
		
		
		
		
		
	}

}