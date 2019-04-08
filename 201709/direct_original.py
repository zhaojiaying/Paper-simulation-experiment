#-*- coding:UTF-8 -*-
import igraph, networkx, pickle, operator, random, sys,json
import pandas,numpy,math
from networkx.classes.function import neighbors
#import pdb


class Defender:
    
    def __init__(self,id):
        self.id = id
        self.investment = 0.0
        self.investment_update = 0.0
        self.risk = 0.0
        self.addtax=0.0
        self.payoff = 0.0
        self.imitation_probabily=0.0
        self.tax=0.0
        self.degree=0.0
        self.weight=0.0

    def set_investment(self, investment):#调用的时候，Offender.set_investment(v)这里的v用具体的策略替换
        self.investment = investment

    def get_investment(self):
        return self.investment
    
    def set_investment_update(self, investment_update):#调用的时候，Offender.set_investment(v)这里的v用具体的策略替换
        self.investment_update = investment_update

    def get_investment_update(self):
        return self.investment_update
    
    def set_risk(self, risk):#调用的时候，Offender.set_investment(v)这里的v用具体的策略替换
        self.risk = risk

    def get_risk(self):
        return self.risk
    
    def set_addtax(self, addtax):
        self.addtax = addtax

    def get_addtax(self):
        return self.addtax

    def set_payoff(self, payoff):
        self.payoff = payoff

    def get_payoff(self):
        return self.payoff
    
    
    def set_imitation_probabily(self, imitation_probabily):
        self.imitation_probabily=imitation_probabily

    def get_imitation_probabily(self):
        return self.imitation_probabily
    
    def set_tax(self, tax):
        self.tax=tax

    def get_tax(self):
        return self.tax
    
    def set_degree(self, degree):
        self.degree=degree

    def get_degree(self):
        return self.degree
    
    def set_weight(self, weight):
        self.weight=weight

    def get_weight(self):
        return self.weight

def random_vertex(G):
    return random.choice(G.nodes())

def random_vertex_s(G,num):#用于生产G中多个节点，num是要抽取的节点的数量
    G_s=random.sample(G.nodes(),num)
    return G_s

def neighbors(G, i):
    return G.neighbors(i)

def random_neighbor(G, i):
    l = neighbors(G, i)
    return random.choice(l) if len(l) > 0 else None
   



def main(args):

    if len(args) != 1:
        sys.exit("Usage: python origianl.py <graphml file>")
    
    C=3
    L=6 #损失值
    select_strength=1 #选择强度
    fname = args[0]
    G = networkx.read_graphml(fname)
    G = networkx.convert_node_labels_to_integers(G)
    
    '''
        给每个参与者初始化投资额，利用之前initial_investment.npy文件
    '''
    a=[]    
    defender = [Defender(i) for i in range(0, len(G))]
    for i in range(len(G)):
        defender[i].set_degree(G.degree()[i])
    for i in range(len(G)):
        if defender[i].get_degree()<2:
            defender[i].set_investment(random.uniform(0,0.2))
        elif defender[i].get_degree()<4 and defender[i].get_degree()>=2:
            defender[i].set_investment(random.uniform(0.2,0.4))
        elif defender[i].get_degree()<6 and defender[i].get_degree()>=4:
            defender[i].set_investment(random.uniform(0.4,0.6))
        elif defender[i].get_degree()<8 and defender[i].get_degree()>=6:
            defender[i].set_investment(random.uniform(0.6,0.8))
        elif defender[i].get_degree()>=8:
            defender[i].set_investment(random.uniform(0.8,1))
        a.append(defender[i].get_investment())
    numpy.save('initial_investment.npy',a)
    
    
    for i in range(len(G)):
        sum_weight=0.0+defender[i].get_degree()
        for j in neighbors(G,i):
            sum_weight+=defender[j].get_degree()
        defender[i].set_weight((defender[i].get_degree())/sum_weight)
    
    
    #pdb.set_trace() 
    '''开始演化博弈'''  
    for i in range(300):    
        #计算每个参与者的风险和收益  
        for j in range(0,len(G)):
            sum=0
            for k in neighbors(G,j):
                sum = sum+defender[k].get_weight()*defender[k].get_investment()
            defender[j].set_risk(math.exp(-sum-defender[j].get_weight()*defender[j].get_investment()))
            defender[j].set_payoff(-C*defender[j].get_investment()-L*defender[j].get_risk())
        
        if i==0:
            pp=0
            for jo in range(0,len(G)):
                pp=pp+defender[jo].get_payoff()
            print pp
        
        '''更新策略'''       
        for jj in range(0,len(G)):
            jjj=random_neighbor(G, jj)          
            if defender[jj].get_payoff() < defender[jjj].get_payoff():
                imitation_probabily=1.0/(1+math.exp((-select_strength)*(defender[jjj].get_payoff()-defender[jj].get_payoff())))
                if random.random() <= imitation_probabily:
                    defender[jj].set_investment_update(defender[jjj].get_investment())
                    #defender[jj].set_investment_update(((defender[jj].get_degree()+0.1)/defender[jjj].get_degree()+0.1)*defender[jjj].get_investment())    
                else:
                    defender[jj].set_investment_update((defender[jj]).get_investment())
            else:
                (defender[jj]).set_investment_update((defender[jj]).get_investment())
        
        
        for jjjj in range(0,len(G)):
            defender[jjjj].set_investment(defender[jjjj].get_investment_update())
            
        '''
    for j in range(0,len(G)): 
        print defender[j].get_investment()
        
        '''
        
        p=0
        for j in range(0,len(G)):
            p=p+defender[j].get_payoff()
        print p  
        
    
       
if __name__ == "__main__":
    main(sys.argv[1:])
    
