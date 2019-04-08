package SI;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.DoublePredicate;

import cern.jet.random.engine.RandomEngine;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class Main {
	
	static double kesei2=1;  //�û��Լ��������̬�ֲ�
	static int Time=50000;//ѭ������
	static int N=10000; //�����еĽڵ����
	//static double peer_threshold=0.1;//�������ھ�
	static double epsilon=0.001; //���汨��x0�����ʱ��ķֶ���ֵ
	static double kesei=0.5;  //���汨������̬�ֲ��ķ���
	//static double base=0.8;  //�û��仯���ұ���Ļ���  ���� �������������� ax^2ͼ�����ƵĹ�ϵ������������ʵֵ�ľ����sqrt(��ʵֵ��)�Ĺ�ϵ�� 
	
	public static void main(String[] args) {
		
		Agent[] agent = new Agent[N];
		
		for(int i=0; i<N; i++) {
			agent[i] = new Agent(i);
		}

		
		double[] trutharray = new double[N]; //��ȡ�ļ���ֵ��������
		double[] selffile=new double[N];     //�����ɵ���������ļ�
		try {
			trutharray=readFile("D:/eclipse/1807new/infection.xls",N);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(int i=0; i<N; i++) {
			agent[i].setTruth(trutharray[i]);
			RandomEngine re = RandomEngine.makeDefault();  //�����������
			agent[i].setSelf_report(Math.sqrt(kesei2)*(new Random().nextGaussian())+agent[i].getTruth());//��ʼ�����ұ������һ����������ʵ����Ϊ��ֵ����̬�ֲ�
			while(agent[i].getSelf_report()<0 || agent[i].getSelf_report()>1) {
				agent[i].setSelf_report(Math.sqrt(kesei2)*(new Random().nextGaussian())+agent[i].getTruth());
			}
			selffile[i] = agent[i].getSelf_report();
		}
		/*
		try {
			writeFile("D:/eclipse/1807new/selfreport.xls", selffile);
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		*/
		//��ȡ�ļ����ݴ�������
		
		double[] selfarray=new double[N];
		try {
			selfarray=readFile("D:/eclipse/1807new/selfreport.xls",N);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(int i=0; i<N; i++) {
			agent[i].setSelf_report(selfarray[i]);
		}
		
		
		//�����û���peer
		InputStream stream=null;
		Workbook rwb = null;
		try {
			stream = new FileInputStream("D:/eclipse/1807new/node_neighbor.xls");
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			rwb = Workbook.getWorkbook(stream);
		} catch (BiffException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Sheet sheet = rwb.getSheet(0);
		Cell cell=null;
		for (int i = 0; i <N; i++) {
			String str = new String();
			Set<Agent> kkk = new HashSet();
			for(int j=0; j<sheet.getRow(i).length; j++) {	
				cell = sheet.getCell(j, i);
				str = cell.getContents();
				kkk.add(agent[Integer.parseInt(str)]);
			}
			ArrayList<Agent> list1 = new ArrayList<Agent>();   
			list1.addAll(kkk);
			agent[i].setPeer(list1);
		}
		
		
		//System.out.println("-------------------------�����е�������------------------------");
		/*-------����������������ļ�-----------------------------------------------------------------------*/
		double[] tspearson = new double[Time+1];//truth ��self
		double[] agent1_report = new double[Time+1];
		double[] agent1_payoff = new double[Time+1];
		double[] agent2_report = new double[Time+1];
		double[] agent2_payoff = new double[Time+1];
		double[] diff = new double[N];
		/*-----------------------------------------------------------------------------------------------------*/
		/**
		 * ------------------------------------------------------------------------------------------------------------------
		 * ------- --ѭ����ʼ--------------------------------ѭ����ʼ--------------------------------------------------------
		 * ----------------------------ѭ����ʼ-------------------------------------ѭ����ʼ---------------------------------
		 * ------------------------------------------------------------------------------------------------------------------
		 */
		for(int time=0; time<Time; time++) {
			
			if(time==0) {
				//�����������ÿһ�ֵ�Ƥ��ѷ���ϵ��
				double[] real = new double[N];
				double[] self = new double[N];
				double[] ra = new double[N];
				for(int i=0; i<N; i++) {
					real[i]=agent[i].getTruth();
					self[i]=agent[i].getSelf_report();
					diff[i]=Math.abs(agent[i].getSelf_report()-agent[i].getTruth());
				}
				PearsonCorrelationScore score1 = new PearsonCorrelationScore(real, self);
				tspearson[0]=score1.getPearsonCorrelationScore();
				try {
					writeFile("D:/eclipse/1807new/0time_diff.xls",diff);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(time == 10 ) {
				for(int i=0; i<N; i++) {
					diff[i]=Math.abs(agent[i].getSelf_report()-agent[i].getTruth());
				}
				try {
					writeFile("D:/eclipse/1807new/10time_diff.xls",diff);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(time == 50 ) {
				for(int i=0; i<N; i++) {
					diff[i]=Math.abs(agent[i].getSelf_report()-agent[i].getTruth());
				}
				try {
					writeFile("D:/eclipse/1807new/50time_diff.xls",diff);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(time == 100 ) {
				for(int i=0; i<N; i++) {
					diff[i]=Math.abs(agent[i].getSelf_report()-agent[i].getTruth());
				}
				try {
					writeFile("D:/eclipse/1807new/100time_diff.xls",diff);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(time == 500 ) {
				for(int i=0; i<N; i++) {
					diff[i]=Math.abs(agent[i].getSelf_report()-agent[i].getTruth());
				}
				try {
					writeFile("D:/eclipse/1807new/500time_diff.xls",diff);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(time == 1000 ) {
				for(int i=0; i<N; i++) {
					diff[i]=Math.abs(agent[i].getSelf_report()-agent[i].getTruth());
				}
				try {
					writeFile("D:/eclipse/1807new/1000time_diff.xls",diff);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			if(time == 49999 ) {
				for(int i=0; i<N; i++) {
					diff[i]=Math.abs(agent[i].getSelf_report()-agent[i].getTruth());
				}
				try {
					writeFile("D:/eclipse/1807new/50000time_diff.xls",diff);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			agent1_report[time]=agent[105].getSelf_report();
			agent1_payoff[time]=agent[105].getPayoff();
			agent2_report[time]=agent[5050].getSelf_report();
			agent2_payoff[time]=agent[5050].getPayoff();
			
			for(int i=0; i<N; i++) {
				agent[i].setCross_report(kesei);
			}
			for(int i=0; i<N; i++) {
				agent[i].setRa(epsilon);
			}
			for(int i=0; i<N; i++) {
				agent[i].setPayoff();
			}
			//System.out.println("-------------------�ҿ�ʼ������--------------------");
			//����
			for(int i=0; i<N; i++) {
				Agent j = agent[new Random().nextInt(N)];
				//System.out.println(agent[0].getPayoff()+"......"+j.getPayoff());
				if(agent[i].getPayoff()<=j.getPayoff()) {
					//System.out.println(Math.abs(agent[i].getPayoff()-j.getPayoff())+" --- "+Math.abs(agent[i].getSelf_report()-agent[i].getTruth()));
					//�û�i�����Լ��ı���
					double payoffdiff=Math.abs(agent[i].getPayoff()-j.getPayoff());
					double reportdiff=Math.abs(agent[i].getSelf_report()-agent[i].getTruth());
					//double step = Math.pow(getNum(payoffdiff),2)+Math.sqrt(reportdiff);
					double step=0.1*getNum(payoffdiff)*Math.pow(reportdiff, 2);
					//System.out.println("----------------------------------------"+step);
					while(step>reportdiff) {
						System.out.println("����̫����");
					}
					if(agent[i].getSelf_report() < agent[i].getTruth()) {
						agent[i].setSelf_report(agent[i].getSelf_report()+step);
					}else if(agent[i].getSelf_report() > agent[i].getTruth()){
						agent[i].setSelf_report(agent[i].getSelf_report()-step);
					}
				}				
				
			}
			//System.out.println(agent[0].getSelf_report()+"  "+agent[0].getTruth());
			
			
			
			//�����������ÿһ�ֵ�Ƥ��ѷ���ϵ��
			double[] real = new double[N];
			double[] self = new double[N];
			double[] ra = new double[N];
			for(int i=0; i<N; i++) {
				real[i]=agent[i].getTruth();
				self[i]=agent[i].getSelf_report();
				ra[i]=agent[i].getRa();
			}
			PearsonCorrelationScore score1 = new PearsonCorrelationScore(real, self);
			PearsonCorrelationScore score2 = new PearsonCorrelationScore(real, ra);
			System.out.println(score1.getPearsonCorrelationScore());
			tspearson[time+1]=score1.getPearsonCorrelationScore();
			System.out.println(time);
			
			
		}
		
		
		/**
		 * ---------------------------------------------------------------------------------------------------------------------
		 * -------����ļ����-------------------����ļ����-----------------����ļ����-----------------����ļ����---------
		 * ---------------------------------------------------------------------------------------------------------------------
		 */
		try {
			writeFile("D:/eclipse/1807new/ts_pearson.xls", tspearson);
			writeFile("D:/eclipse/1807new/1_report.xls",agent1_report);
			writeFile("D:/eclipse/1807new/1_payoff.xls",agent1_payoff);
			writeFile("D:/eclipse/1807new/2_report.xls",agent2_report);
			writeFile("D:/eclipse/1807new/2_payoff.xls",agent2_payoff);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/*
	 * ----------------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------����main��������--------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------------
	 */
	
	
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
	
	/**
	 * ���ļ��ж��������ݴ���������
	 */
	public static double[] readFile(String filepath, int row) throws IOException {
		double[] zan = new double[row];
		Workbook rwb = null;
		Cell cell = null;
		InputStream stream = new FileInputStream(filepath);
		try {
			rwb = Workbook.getWorkbook(stream);
		} catch (BiffException e) {
			e.printStackTrace();
		}
		//��ȡָ��������Ĭ��Ϊ��һ��
		Sheet sheet = rwb.getSheet(0);
		for (int i = 0; i <row; i++) {
			String str = new String();
			//����ѡ��Ҫ��ȡ�����ݵ��������������磨3��4��˵��Ϊ�����е����У��������Ǵ�0��ʼ����	
			cell = sheet.getCell(0, i);
			str = cell.getContents();
			zan[i] = Double.parseDouble(str);
		}
		return zan;
	}
	
	/*�����ֱ仯Ϊ0.��ͷ*/
	public static double getNum(double number) {
		//payoffdiff/Math.abs(getNumCount(payoffdiff))
		int count=0;
		if (number < 1 && number>=0) {
			return number;
		}
		else {
			int k = (int) number;
			while (k > 0) {
				k = k / 10;
				count++;
			}
			return number/Math.pow(10,count);
			
		}
	}

}
