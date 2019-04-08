import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;

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
	
	static int Time=5000;//循环次数
	static int N=10000; //网络中的节点个数
	static double peer_threshold=0.1;//来区分邻居
	static double epsilon=0.001; //交叉报告x0计算的时候的分段阈值
	static double kesei=0.5;  //交叉报告是正态分布的方差
	//static double base=0.8;  //用户变化自我报告的基数  满足 【步长与收益差成 ax^2图象类似的关系】【步长与真实值的距离成sqrt(真实值差)的关系】 
	
	public static void main(String[] args) {
		
		Agent[] agent = new Agent[N];
		
		//将初始化的数值存入文件
		double[] truthfile = new double[N];
		double[] selffile = new double[N];
		double[] typefile = new double[N];
		for(int i=0; i<N; i++) {
			agent[i] = new Agent(i);
			truthfile[i] = agent[i].getTruth();
			selffile[i] = agent[i].getSelf_report();
			switch(agent[i].getType()) {
				case N1:
					typefile[i] = 1;
					break;
				case N2:
					typefile[i] = 2;
					break;
				case N3:
					typefile[i] = 3;
					break;
				case N4:
					typefile[i] = 4;
					break;
				case N5:
					typefile[i] = 5;
					break;
				case N6:
					typefile[i] = 6;
					break;
				default:
					System.out.println("Agent"+i+"的类型没有存储成功");
			}
			
		}
		//创建用户的peer
		for(int i=0; i<N; i++) {
			agent[i].setPeer(agent,peer_threshold);
			//System.out.println(agent[i].getPeer()+"\n");
		}
		
		
		/**
		 * -------------------------------------------------------------------------------------------------------------------
		 * -------------------------------存储初始化的文件，或导入初始化的文件数据--------------------------------------------
		 * -------------------------------------------------------------------------------------------------------------------
		 */
		
/*
		//将数据写入文件保存
		try {
			writeFile("D:/eclipse/1807/truth.xls", truthfile);
			writeFile("D:/eclipse/1807/selfreport.xls", selffile);
			writeFile("D:/eclipse/1807/type.xls", typefile);
		} catch (IOException e) {
			e.printStackTrace();
		}

*/
		//读取文件内容存入数组
		double[] trutharray = new double[N];
		double[] selfarray=new double[N];
		double[] typearray = new double[N];
		try {
			trutharray=readFile("D:/eclipse/1807/truth.xls",N);
			selfarray=readFile("D:/eclipse/1807/selfreport.xls",N);
			typearray=readFile("D:/eclipse/1807/type.xls",N);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		for(int i=0; i<N; i++) {
			agent[i].setTruth(trutharray[i]);
			agent[i].setSelf_report(selfarray[i]);
			switch((int)typearray[i]) {
				case 1:
					agent[i].setType(NetworkBlock.N1);
					break;
				case 2:
					agent[i].setType(NetworkBlock.N2);
					break;
				case 3:
					agent[i].setType(NetworkBlock.N3);
					break;
				case 4:
					agent[i].setType(NetworkBlock.N4);
					break;
				case 5:
					agent[i].setType(NetworkBlock.N5);
					break;
				case 6:
					agent[i].setType(NetworkBlock.N6);
					break;
				default:
					System.out.println("Agent"+i+"的类型没有获取成功");
			}
		}
		
		//System.out.println("-------------------------我运行到这里了------------------------");
		/*-------建数组存结果，存入文件-----------------------------------------------------------------------*/
		double[] tspearson = new double[Time+1];//truth 和self
		double[] trpearson = new double[Time+1];//truth 和ra综合评价
		double[] a1payoff = new double[Time+1]; //存储agent1的收益变化   N1区     agent[4]
		double[] a1report = new double[Time+1]; //agent[1]报告变化 
		double[] a2payoff = new double[Time+1]; //存储agent2的收益变化   N2区     agent[3]
		double[] a2report = new double[Time+1]; 
	/*	double[] a3payoff = new double[Time+1]; //存储agent3的收益变化   N3区     agent[0]
		double[] a3report = new double[Time+1]; 
		double[] a4payoff = new double[Time+1]; //存储agent4的收益变化   N4区     agent[1]
		double[] a4report = new double[Time+1]; 
		double[] a5payoff = new double[Time+1]; //存储agent5的收益变化   N5区     agent[2]
		double[] a5report = new double[Time+1]; 
		double[] a6payoff = new double[Time+1]; //存储agent6的收益变化   N6区     agent[9]
		double[] a6report = new double[Time+1]; */
		/*-----------------------------------------------------------------------------------------------------*/
		/**
		 * ------------------------------------------------------------------------------------------------------------------
		 * ------- --循环开始--------------------------------循环开始--------------------------------------------------------
		 * ----------------------------循环开始-------------------------------------循环开始---------------------------------
		 * ------------------------------------------------------------------------------------------------------------------
		 */
		for(int time=0; time<Time; time++) {
			
			if(time==0) {
				//结果输出，输出每一轮的皮尔逊相关系数
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
				tspearson[0]=score1.getPearsonCorrelationScore();
				trpearson[0]=score2.getPearsonCorrelationScore();
				a1report[0]=agent[4].getSelf_report();
				a1payoff[0]=agent[4].getPayoff();
				a2report[0]=agent[3].getSelf_report();
				a2payoff[0]=agent[3].getPayoff();
//				a3report[0]=agent[0].getSelf_report();
//				a3payoff[0]=agent[0].getPayoff();
//				a4report[0]=agent[1].getSelf_report();
//				a4payoff[0]=agent[1].getPayoff();
//				a5report[0]=agent[2].getSelf_report();
//				a5payoff[0]=agent[2].getPayoff();
//				a6report[0]=agent[9].getSelf_report();
//				a6payoff[0]=agent[9].getPayoff();
			}
			
			
			for(int i=0; i<N; i++) {
				agent[i].setCross_report(kesei);
			}
			for(int i=0; i<N; i++) {
				agent[i].setRa(epsilon);
			}
			for(int i=0; i<N; i++) {
				agent[i].setPayoff();
			}
			//System.out.println("-------------------我开始博弈了--------------------");
			//博弈
			for(int i=0; i<N; i++) {
				Agent j = agent[new Random().nextInt(N)];
				if(j==agent[i]) {
					j = agent[new Random().nextInt(N)];
				}
				if(agent[i].getPayoff()<j.getPayoff()) {
					//System.out.println(Math.abs(agent[i].getPayoff()-j.getPayoff())+" --- "+Math.abs(agent[i].getSelf_report()-agent[i].getTruth()));
					//用户i调整自己的报告
					double payoffdiff=Math.abs(agent[i].getPayoff()-j.getPayoff());
					double reportdiff=Math.abs(agent[i].getSelf_report()-agent[i].getTruth());
					//double step = Math.pow(getNum(payoffdiff),2)+Math.sqrt(reportdiff);
					double step=0.4*getNum(payoffdiff)*Math.pow(reportdiff, 2);
					while(step>reportdiff) {
						System.out.println("hah");
					}
					if(agent[i].getSelf_report() < agent[i].getTruth()) {
						agent[i].setSelf_report(agent[i].getSelf_report()+step);
					}else if(agent[i].getSelf_report() > agent[i].getTruth()){
						agent[i].setSelf_report(agent[i].getSelf_report()-step);
					}
				}				
				
			}
			
			//结果输出，输出每一轮的皮尔逊相关系数
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
			trpearson[time+1]=score2.getPearsonCorrelationScore();
			a1report[time+1]=agent[4].getSelf_report();
			a1payoff[time+1]=agent[4].getPayoff();
			a2report[time+1]=agent[3].getSelf_report();
			a2payoff[time+1]=agent[3].getPayoff();
//			a3report[time+1]=agent[0].getSelf_report();
//			a3payoff[time+1]=agent[0].getPayoff();
//			a4report[time+1]=agent[1].getSelf_report();
//			a4payoff[time+1]=agent[1].getPayoff();
//			a5report[time+1]=agent[2].getSelf_report();
//			a5payoff[time+1]=agent[2].getPayoff();
//			a6report[time+1]=agent[9].getSelf_report();
//			a6payoff[time+1]=agent[9].getPayoff();
			System.out.println(time);
			
		}
		
		
		/**
		 * ---------------------------------------------------------------------------------------------------------------------
		 * -------结果文件输出-------------------结果文件输出-----------------结果文件输出-----------------结果文件输出---------
		 * ---------------------------------------------------------------------------------------------------------------------
		 */
		try {
			writeFile("D:/eclipse/1807/ts_pearson.xls", tspearson);
			writeFile("D:/eclipse/1807/tr_preason.xls", trpearson);
			writeFile("D:/eclipse/1807/a1report.xls", a1report);
			writeFile("D:/eclipse/1807/a1payoff.xls", a1payoff);
			writeFile("D:/eclipse/1807/a2report.xls", a2report);
			writeFile("D:/eclipse/1807/a2payoff.xls", a2payoff);
//			writeFile("D:/eclipse/1807/a3report.xls", a3report);
//			writeFile("D:/eclipse/1807/a3payoff.xls", a3payoff);
//			writeFile("D:/eclipse/1807/a4report.xls", a4report);
//			writeFile("D:/eclipse/1807/a4payoff.xls", a4payoff);
//			writeFile("D:/eclipse/1807/a5report.xls", a5report);
//			writeFile("D:/eclipse/1807/a5payoff.xls", a5payoff);
//			writeFile("D:/eclipse/1807/a6report.xls", a6report);
//			writeFile("D:/eclipse/1807/a6payoff.xls", a6payoff);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/*
	 * ----------------------------------------------------------------------------------------------------------------------------
	 * ----------------------------------------------------以上main函数结束--------------------------------------------------------
	 * ----------------------------------------------------------------------------------------------------------------------------
	 */
	
	
	/**
	 * 数据写入文件
	 */
	public static void writeFile(String filepath, double[] writeobject) throws IOException{
		//获得输出流
		OutputStream os = new FileOutputStream(filepath);
		WritableWorkbook workbook = Workbook.createWorkbook(os);
		//在这里指定你存储数据表的名称
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
		//将输出流中数据写入Excel，关闭输出流
		workbook.write();
		try {
			workbook.close();
		} catch (WriteException e) {
			e.printStackTrace();
		}
		os.close();
	}
	
	/**
	 * 从文件中读出的数据存入数组中
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
		//获取指定工作表默认为第一个
		Sheet sheet = rwb.getSheet(0);
		for (int i = 0; i <row; i++) {
			String str = new String();
			//这里选择要读取的数据的列数和行数例如（3，4）说明为第三列第四行，行数都是从0开始计数	
			cell = sheet.getCell(0, i);
			str = cell.getContents();
			zan[i] = Double.parseDouble(str);
		}
		return zan;
	}
	
	/*将数字变化为0.开头*/
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
