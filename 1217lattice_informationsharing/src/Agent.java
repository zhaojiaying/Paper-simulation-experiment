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
	NetworkBlock type; //假定一个网路分块中的用户都是相互关联的
	double truth; //真实值
	double self_report; //用户自己报告的值
	double[] cross_report;  //交叉报告,根据自身真实值评估对方,被人对他的
	Map<Agent,Double> tocross = new HashMap<>();  //i对Agent的交叉报告，值为Double
	ArrayList<Agent> peer;
	double ra; //表示其他人和用户自身报告的综合评估
	double payoff;
	double kesei2=1;  //用户自己报告的正态分布
	double ave;
	
	/**
	 * 构造方法
	 */
	public Agent(int id) {
		super();
		this.id = id;
		this.type=randomBlock();
		RandomEngine re = RandomEngine.makeDefault();  //随机数生成器
		switch(type){
	        case N1:
	        	//根据用户区域，产生一个符合特定区域特点的韦伯分布(double alpha,double beta,RandomEngine)的随机数[0,1]
	        	this.truth= Distributions.nextWeibull(1,0.5,re); //这里的a(比例参数)，b(形状参数)具体定什么值看情况
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
	        	System.out.println("Agent不属于此网络");
		}
		this.self_report=Math.sqrt(kesei2)*(new Random().nextGaussian())+this.truth;//初始的自我报告服从一个以自身真实收益为均值的正态分布
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
	
	public void setCross_report(double kesei) {//交叉报告，其他用户评估正态分布的方差
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

	public void setRa(double epsilon) { //计算交叉报告分段的阈值epsilon
		double rra; //表示评估的综合值
		double x0=0; //表示交叉评估的均值
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
	public void setPayoff() {//epsilon表示一个评估的差别阈值
		Iterator ne = this.peer.iterator();
		double sum=0;
		while(ne.hasNext()) {
			Agent j = (Agent) ne.next();
			sum+=Math.exp(Math.abs(this.getTocross().get(j)-j.getAve()));
		}
		this.payoff=-sum+Math.exp(this.getRa());
	}
	
}
