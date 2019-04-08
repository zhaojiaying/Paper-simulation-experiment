import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import cern.jet.random.Distributions;
import cern.jet.random.engine.RandomEngine;
import sun.tools.jar.resources.jar;

public class Agent {
	int id;
	NetworkBlock type; //�ٶ�һ����·�ֿ��е��û������໥������
	double truth; //��ʵֵ
	double self_report; //�û��Լ������ֵ
	double[] cross_report;  //���汨��,����������ʵֵ�����Է�,���˶�����
	Map<Agent,Double> tocross = new HashMap<>();  //i��Agent�Ľ��汨�棬ֵΪDouble
	ArrayList<Agent> peer;
	double ra; //��ʾ�����˺��û���������ۺ�����
	double payoff;
	double kesei2=1;  //�û��Լ��������̬�ֲ�
	double ave;
	
	/**
	 * ���췽��
	 */
	public Agent(int id) {
		super();
		this.id = id;
		this.type=randomBlock();
		RandomEngine re = RandomEngine.makeDefault();  //�����������
		switch(type){
	        case N1:
	        	//�����û����򣬲���һ�������ض������ص��Τ���ֲ�(double alpha,double beta,RandomEngine)�������[0,1]
	        	this.truth= Distributions.nextWeibull(1,0.5,re); //�����a(��������)��b(��״����)���嶨ʲôֵ�����
	        	while(truth<0||truth>1) {
	        		this.truth=Distributions.nextWeibull(1,0.5,re);
	        	}
	        	break;
	        case N2: 
	        	this.truth= Distributions.nextWeibull(1,1,re);
	        	while(truth<0||truth>1) {
	        		this.truth=Distributions.nextWeibull(1,1,re);
	        	}
	        	break;
	        case N3:
	        	this.truth= Distributions.nextWeibull(1,3,re);
	        	while(truth<0||truth>1) {
	        		this.truth=Distributions.nextWeibull(1,3,re);
	        	}
	        	break;
	        case N4:
	        	this.truth= Distributions.nextWeibull(1,5,re);
	        	while(truth<0||truth>1) {
	        		this.truth=Distributions.nextWeibull(1,5,re);
	        	}
	        	break;
	        case N5:
	        	this.truth= Distributions.nextWeibull(1,7,re);
	        	while(truth<0||truth>1) {
	        		this.truth=Distributions.nextWeibull(1,7,re);
	        	}
	        	break;
	        case N6:
	        	this.truth= Distributions.nextWeibull(1,9,re);
	        	while(truth<0||truth>1) {
	        		this.truth=Distributions.nextWeibull(1,9,re);
	        	}
	        	break;
	        default:
	        	System.out.println("Agent�����ڴ�����");
		}
		this.self_report=Math.sqrt(kesei2)*(new Random().nextGaussian())+this.truth;//��ʼ�����ұ������һ����������ʵ����Ϊ��ֵ����̬�ֲ�
		while(this.self_report<0 || this.self_report>1) {
			this.self_report=Math.sqrt(kesei2)*(new Random().nextGaussian())+this.truth;
		}
	}
	
	private NetworkBlock randomBlock() {
		int pick = new Random().nextInt(NetworkBlock.values().length);
	    return NetworkBlock.values()[pick];
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public Map<Agent, Double> getTocross() {
		return tocross;
	}

	public void setTocross(Map<Agent, Double> tocross) {
		this.tocross = tocross;
	}

	public NetworkBlock getType() {
		return type;
	}
	public void setType(NetworkBlock type) {
		this.type = type;
	}
	public double getTruth() {
		return truth;
	}
	public void setTruth(double truth) {
		this.truth = truth;
	}
	public double getSelf_report() {
		return self_report;
	}
	public void setSelf_report(double self_report) {
		this.self_report = self_report;
	}
	
	public ArrayList getPeer() {
		return peer;
	}

	public void setPeer(Agent[] agent, double peer_threshold ) {
		ArrayList<Agent> a = new ArrayList<Agent>();
		for(int i=0; i<agent.length; i++) {
			if(this.type==agent[i].getType() && this.id!=i && Math.abs(this.truth-agent[i].getTruth())<=peer_threshold) {
				a.add(agent[i]);
			}
		}
		this.peer=a;
	}
	
	public double[] getCross_report() {
		return cross_report;
	}
	
	public void setCross_report(double kesei) {//���汨�棬�����û�������̬�ֲ��ķ���
		cross_report = new double[this.peer.size()];
		int i=0;
		for(Agent j : peer){
			double r=Math.sqrt(kesei)*(new Random().nextGaussian())+j.getTruth();
			while(r<0 || r>1) {
				r=Math.sqrt(kesei)*(new Random().nextGaussian())+j.getTruth();
			}
			j.getTocross().put(this, cross_report[i]);
			cross_report[i++]=r;
		}
	}
	
	public double getRa() {
		return ra;
	}

	
	public double getAve() {
		return ave;
	}

	public void setAve(double ave) {
		this.ave = ave;
	}

	public void setRa(double epsilon) { //���㽻�汨��ֶε���ֵepsilon
		double rra; //��ʾ�������ۺ�ֵ
		double x0=0; //��ʾ���������ľ�ֵ
		int l=this.cross_report.length;
		for(int i=0; i<l; i++) {
			x0+=this.cross_report[i];
		}
		x0=x0/l;
		this.setAve(x0);
		if(this.self_report>=(x0-epsilon) && this.self_report<=(x0+epsilon)) {
			rra=(x0+this.self_report)/2;
		}
		else {
			rra=x0-Math.abs(x0-this.self_report);
		}
		this.ra=rra;
	}
	
	public double getPayoff() {
		return payoff;
	}
	public void setPayoff() {//epsilon��ʾһ�������Ĳ����ֵ
		Iterator ne = this.peer.iterator();
		double sum=0;
		while(ne.hasNext()) {
			Agent j = (Agent) ne.next();
			sum+=Math.exp(Math.abs(this.getTocross().get(j)-j.getAve()));
		}
		this.payoff=-sum+Math.exp(this.getRa());
	}
	
}
