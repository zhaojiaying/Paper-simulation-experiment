#-*- coding:UTF-8 -*-
import igraph, networkx, pickle, operator, random, sys,json
import pandas,numpy,math
from networkx.classes.function import neighbors
#import pdb

IN=0
OUT=1

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
        self.ave_neighbor_invest=0.0
        self.state=IN
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
    
    def set_ave_neighbor_invest(self, ave_neighbor_invest):
        self.ave_neighbor_invest=ave_neighbor_invest

    def get_ave_neighbor_invest(self):
        return self.ave_neighbor_invest
    
    def set_state(self, state):
        self.state=state

    def get_state(self):
        return self.state
    
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
        sys.exit("Usage: python imitate_exit_mechanism.py <graphml file>")
    
    C=3
    L=6 #损失值
    select_strength=1 #选择强度
    fname = args[0]
    G = networkx.read_graphml(fname)
    G = networkx.convert_node_labels_to_integers(G)
    
    '''
        给每个参与者初始化投资额，利用之前initial_investment.npy文件
    '''
    a=numpy.load('initial_investment.npy')    
    defender = [Defender(i) for i in range(0, len(G))]
    for i in range(len(G)):
        defender[i].set_investment(a[i])
        defender[i].set_degree(G.degree()[i])
           

    for i in range(len(G)):
        sum_degree=0.0+defender[i].get_degree()
        for j in neighbors(G,i):
            sum_degree+=defender[j].get_degree()
        defender[i].set_weight((defender[i].get_degree())/sum_degree)



    #pdb.set_trace() 
    '''开始演化博弈'''  
    for i in range(300):    
        #计算每个参与者的风险和收益  
        for j in range(0,len(G)):
            sum=0
            neighbors_num=0
            sum_weight=0.0+defender[j].get_weight()
            for k in neighbors(G,j):
                neighbors_num+=1
                sum_weight+=defender[k].get_weight()
                sum = sum+defender[k].get_weight()*defender[k].get_investment()
            defender[j].set_risk(math.exp(-sum-defender[j].get_weight()*defender[j].get_investment()))
            defender[j].set_payoff(-C*defender[j].get_investment()-L*defender[j].get_risk())
            defender[j].set_ave_neighbor_invest((defender[j].get_weight()*defender[j].get_investment()+sum)/sum_weight)
        
        if i==0:
            pp=0
            for jo in range(0,len(G)):
                pp=pp+defender[jo].get_payoff()
            #print pp
          
        for jj in range(len(G)):      
                taxsum=0
                for kk in neighbors(G, jj):
                    ksum=0
                    for kkk in neighbors(G, kk):
                        if kkk!=jj:
                            ksum=ksum+defender[kkk].get_weight()*defender[kkk].get_investment()
                    defender[kk].set_tax(L*(defender[kk].get_risk()-math.exp(-defender[kk].get_weight()*defender[kk].get_investment()-ksum-defender[jj].get_weight()*defender[jj].get_ave_neighbor_invest())))
                    taxsum=taxsum+defender[kk].get_tax()
                    defender[jj].set_addtax(taxsum)
                    
                if defender[jj].get_state()==IN:
                    defender[jj].set_payoff(defender[jj].get_payoff()-taxsum)
           
        
        
        '''更新策略''' 
        for jjj in range(0,len(G)):
            
            if defender[jjj].get_addtax()>0:
                defender[jjj].set_state(OUT)
            #if defender[jjj].get_addtax()<=0 and defender[jjj].get_state()==OUT:
                #defender[jjj].set_state(IN)
                
            jjjj=random_neighbor(G, jjj)          
            if defender[jjj].get_payoff() < defender[jjjj].get_payoff():
                imitation_probabily=1.0/(1+math.exp((-select_strength)*(defender[jjjj].get_payoff()-defender[jjj].get_payoff())))
                if random.random() <= imitation_probabily:
                    '''
                    if defender[jjj].get_investment()>=defender[jjjj].get_investment():
                        
                        defender[jjj].set_investment_update(defender[jjj].get_investment()-(defender[jjj].get_degree()/defender[jjjj].get_degree())*defender[jjjj].get_investment())
                        if defender[jjj].get_investment_update()<0:
                            defender[jjj].set_investment_update(0)
                    else:
                        defender[jjj].set_investment_update(defender[jjj].get_investment()+(defender[jjj].get_degree()/defender[jjjj].get_degree())*defender[jjjj].get_investment())
                        #if defender[jjj].get_investment_update()>1:
                            #defender[jjj].set_investment_update(1)
                    '''
                    #defender[jjj].set_investment_update(defender[jjjj].get_investment())
                    defender[jjj].set_investment_update(((defender[jjj].get_degree()+0.1)/defender[jjjj].get_degree()+0.1)*defender[jjjj].get_investment())    
                    defender[jjj].set_state(defender[jjjj].get_state())
                else:
                    defender[jjj].set_investment_update((defender[jjj]).get_investment())
            else:
                (defender[jjj]).set_investment_update((defender[jjj]).get_investment())
                
            
       
        for jjjjj in range(0,len(G)):
            defender[jjjjj].set_investment(defender[jjjjj].get_investment_update())
            
        '''
        p=0
        for j in range(0,len(G)):
            #p=p+defender[j].get_investment()
            if defender[j].get_state()==IN:
                p=p+defender[j].get_payoff()+defender[j].get_addtax()
            else:
                p=p+defender[j].get_payoff()
            defender[j].set_tax(0.0)
            defender[j].set_addtax(0.0)
        print p  
        '''
          
        outp=0.0
        for j in range(0,len(G)):
            if defender[j].get_state()==OUT:
                outp=outp+1.0
            defender[j].set_tax(0.0)
            defender[j].set_addtax(0.0)
        outp=outp/len(G)
        #print outp 
        
     
        
    for j in range(0,len(G)): 
        print defender[j].get_investment()
     
    
    
       
if __name__ == "__main__":
    main(sys.argv[1:])
    
