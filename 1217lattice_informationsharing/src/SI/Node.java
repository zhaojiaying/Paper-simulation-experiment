package SI;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

public class Node {
	int id;
	double infection=0.0; //感染值
	double infection_rate;
	Set<Node> neighbor;
	int delttime;
	double infectionratesum;
	
	public Node(int id) {
		super();
		this.id = id;
	}

	
	public double getInfectionratesum() {
		return infectionratesum;
	}

	public void setInfectionratesum(double infectionratesum) {
		this.infectionratesum = infectionratesum;
	}



	public int getDelttime() {
		return delttime;
	}

	public void setDelttime(int delttime) {
		this.delttime = delttime;
	}



	public void setNeighbor(Node[] node) { //传入的参数为系统中所有的节点
		int sumcount = 4; //一个节点有4个邻居节点
		Set<Node> s = new HashSet();
		if(this.id == 0) {  //左上角
			s.add(node[1]);
			s.add(node[100]);
		}else if(this.id == 99) {  //右上角
			s.add(node[98]);
			s.add(node[199]);
		}else if(this.id == 9900) { //左下角
			s.add(node[9800]);
			s.add(node[9901]);
		}else if(this.id == 9999){  //右下角
			s.add(node[9899]);
			s.add(node[9998]);
		}else if(this.id > 0 && this.id < 99) {   //上边
			s.add(node[this.id-1]);
			s.add(node[this.id+1]);
			s.add(node[this.id+100]);
		}else if(this.id%100 ==0 && this.id !=0 && this.id != 9900) {  //左边
			s.add(node[this.id-100]);
			s.add(node[this.id+100]);
			s.add(node[this.id+1]);
		}else if(this.id > 9900 && this.id < 9999) {  //下边
			s.add(node[this.id-100]);
			s.add(node[this.id-1]);
			s.add(node[this.id+1]);
		}else if((this.id+1)%100 == 0 && this.id!=99 && this.id != 9999) { //右边
			s.add(node[this.id-100]);
			s.add(node[this.id+100]);
			s.add(node[this.id-1]);
		}else {
			s.add(node[this.id-1]);
			s.add(node[this.id+1]);
			s.add(node[this.id-100]);
			s.add(node[this.id+100]);
		}
		this.neighbor=s;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public double getInfection() {
		return infection;
	}

	public void setInfection(double infection) {
		this.infection = infection;
	}

	public double getInfection_rate() {
		return infection_rate;
	}

	public void setInfecrion_rate(double infection_rate) {
		this.infection_rate = infection_rate;
	}

	public Set<Node> getNeighbor() {
		return neighbor;
	}

	public void setNeighbor(Set<Node> neighbor) {
		this.neighbor = neighbor;
	}

	public int getInfectnumber() {
		int sum=0;
		Iterator ne = this.neighbor.iterator();
		while(ne.hasNext()) {
			if(((Node) ne.next()).infection!=0) {
				sum++;
			}
		}
		return sum;
	}
	
	
}
