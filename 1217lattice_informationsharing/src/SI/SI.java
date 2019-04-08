package SI;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class SI {
	
	static int initial_infection=1;
	static int N=10000;
	static int Time=800;
	static double r=0.8; //ÿ�ֵļ�����Ⱦ��
	static double des_rate=0.02; //��ʾûÿ����һ�֣���Ⱦ�����½�
	
	public static void main(String[] args) {
		Node[] node = new Node[N];
		for(int i=0; i<N; i++) {
			node[i] = new Node(i); //���ù��췽��
		}
		for (int i = 0; i <N; i++) {
			node[i].setNeighbor(node);
		}
		saveNeighbor(node);
		//-------------------------------------------------------------------------------------
		
		/*while(���нڵ��label����1��)*/
		//���ó�ʼ��Ⱦ�ڵ�
		/*for(int i=0; i<initial_infection; i++) {
			node[i].setInfection(1);
		}*/
		node[0].setInfection(1);
		
		//��ʼ��Ⱦ--------------------------------------------------------------------------------
		for(int time=0; time<Time; time++) {
			for(int i=0; i<N; i++) {
				node[i].setInfecrion_rate(node[i].getInfectnumber()*r);
			}
			
			double infectionratesum=0;
			for(int i=0; i<N; i++) {
				/*Iterator nn = node[i].getNeighbor().iterator();
				double infectionratesum=0;
				while(nn.hasNext()) {
					infectionratesum+=((Node) nn.next()).getInfection_rate();
				}
				node[i].setInfectionratesum(infectionratesum);*/
				infectionratesum+=node[i].getInfection_rate();
			}
			
			double rand = Math.random();
			for(int i=0; i<N; i++) {
				double sum=0;
				for(int j=0; j<i-1; j++) {
					//sum+=node[j].getInfection_rate()/node[i].getInfectionratesum();
					sum+=node[j].getInfection_rate()/infectionratesum;
				}
				
				//if(rand>sum && rand<sum+node[i].getInfection_rate()/node[i].getInfectionratesum() && node[i].getInfection()==0){
				if(rand<node[i].getInfection_rate() && node[i].getInfection()==0) {
				//if(rand>sum && rand<sum+node[i].getInfection_rate()/infectionratesum && node[i].getInfection()==0){
					node[i].setInfection(1-time*des_rate);
					if(node[i].getInfection()<0) {
						node[i].setInfection(0);
					}
				}
			}
			
			double insum=0;
			for(int i=0;i<N;i++) {
				if(node[i].getInfection()!=0) {
					insum++;
				}
			}
			System.out.println(insum/N);
			//System.out.println(time);
			//time=time-(int) (Math.log(1-Math.random())/infectionratesum);
			
		}
		double[] endinfection = new double[N];
		for(int i=0; i<N; i++) {
			endinfection[i] = node[i].getInfection();
		}
		try {
			writeFile("D:/eclipse/1807new/infection.xls", endinfection);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public static void saveNeighbor(Node[] node) {
		
		OutputStream os = null;
		try {
			os = new FileOutputStream("D:/eclipse/1807new/node_neighbor.xls");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(os);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//������ָ����洢���ݱ������
		WritableSheet sheet = workbook.createSheet("sheet1", 0);
		
		for(int i=0; i<N; i++) {
			Iterator ne = node[i].getNeighbor().iterator();
			int j=0;
			while(ne.hasNext()) {
				Label risk = new Label(j,i,String.valueOf(((Node) ne.next()).getId()));  //�У��У�ֵ
				try {
					sheet.addCell(risk);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
				j++;
			}
		}
		try {
			workbook.write();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			workbook.close();
		} catch (WriteException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * ����д���ļ�
	 */
	public static void writeFile(String filepath, double[] writeobject) throws IOException{
		//��������
		OutputStream os = new FileOutputStream(filepath);
		WritableWorkbook workbook = Workbook.createWorkbook(os);
		//������ָ����洢���ݱ������
		WritableSheet sheet = workbook.createSheet("sheet1", 0);
		for (int i = 0; i < writeobject.length; i++) {
				Label risk = new Label(0,i,String.valueOf(writeobject[i]));
				try {
					sheet.addCell(risk);
				} catch (RowsExceededException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
		}
		//�������������д��Excel���ر������
		workbook.write();
		try {
			workbook.close();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		os.close();
	}
	
	
}

