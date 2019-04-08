package SI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import cern.jet.random.Distributions;
import cern.jet.random.engine.RandomEngine;

public class Agent {
	int id;
	double truth; //真实值
	double self_report; //用户自己报告的值
	double[] cross_report;  //别人对其的报告值，只有同一个分区中的用户可以报告，根据自身真实值评估对方
	ArrayList<Agent> peer;
	double ra; //表示其他人和用户自身报告的综合评估
	double payoff;
	static Map<Agent,Double> tocross;  //i对Agent的交叉报告，值为Double
	double ave;
	
	/**
	 * 构造方法
	 */
	public Agent(int id) {
		super();
		this.id = id;
		this.tocross = new HashMap<>();
	}
	
	
	public void setPeer(ArrayList<Agent> peer) {
		this.peer = peer;
	}

	public Map<Agent, Double> getTocross() {
		return tocross;
	}

	public void setTocross(Map<Agent, Double> tocross) {
		this.tocross = tocross;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
			if(this.id!=i && Math.abs(this.truth-agent[i].getTruth())<=peer_threshold) {
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
		Iterator ne = this.getPeer().iterator();
		int i=0;
		while(ne.hasNext()){
			Agent j = (Agent) ne.next();
			double r=Math.sqrt(kesei)*(new Random().nextGaussian())+j.getTruth();
			while(r<0 || r>1) {
				r=Math.sqrt(kesei)*(new Random().nextGaussian())+j.getTruth();
			}
			j.getTocross().put(this, r);
			//System.out.println("j的交叉报告"+this.getTocross());
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
		if(l==0) {
			l=1;
		}else {
			for(int i=0; i<l; i++) {
				x0+=this.cross_report[i];
			}
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
		Iterator ne = this.getPeer().iterator();
		double sum=0;
		while(ne.hasNext()) {
			Agent j = (Agent) ne.next();
			//System.out.println("对他人评价"+this.getTocross().get(j));
			if(this.getTocross().get(j)!=null) {
				sum+=Math.exp(Math.abs(this.getTocross().get(j)-j.getAve()));
			}
			//System.out.println(sum+"-----"+j.getRa()+"-----"+j.getTruth());
		}
		this.payoff=-sum+Math.exp(this.getRa());
	}


	@Override
	public int hashCode() {
		return this.getId()+"a".hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(this==obj)
		{
			return true;
		}
		if(obj instanceof Agent)
		{
			Agent i = (Agent)obj;
			if(this.getId()==i.getId())
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		else
		{
			return false;
		}
	}
	
	
	
}
