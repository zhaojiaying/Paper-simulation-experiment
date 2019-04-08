#-*- coding:UTF-8 -*-
import igraph, pickle, operator, random, sys,json,math
import pandas
import numpy as np
import networkx as nx
from scipy.optimize import fmin
from sympy import *

'''
增强对角线上的权重a的值，其余位置为1的情况
'''

EXIT_MECHANISM=0
ENTER_MECHANISM=1

class Player:
    
    def __init__(self,id):
        self.id = id            #节点编号
        self.invest = 0.0  
        self.e_invest = 0.0  
        self.risk_f = 0.0    #节点风险
        self.cost_g=0.0
        self.tax=0.0
        self.state=ENTER_MECHANISM

    def setInvest(self, invest):#调用的时候，Offender.set_opopulation(v)这里的v用具体的策略替换
        self.invest = invest

    def getInvest(self):
        return self.invest
    
    def setEinvest(self, e_invest):#调用的时候，Offender.set_opopulation(v)这里的v用具体的策略替换
        self.e_invest = e_invest

    def getEinvest(self):
        return self.e_invest
    
    def setRisk(self, risk_f):#调用的时候，Offender.set_opopulation(v)这里的v用具体的策略替换
        self.risk_f = risk_f

    def getRisk(self):
        return self.risk_f

    def setState(self, state):
        self.state = state

    def getState(self):
        return self.state
    
    def setCost(self, cost_g):
        self.cost_g = cost_g

    def getCost(self):
        return self.cost_g
    
    def setTax(self, tax):
        self.tax = tax

    def getTax(self):
        return self.tax
    
    
def random_vertex(G):
    return random.choice(G.nodes())

def neighbors(G, i):
    return G.neighbors(i)


def exit_fun(G,player,i,j):
    C=1
    for m in range(len(G)):
        sum1=0
        for n in neighbors(G,m):
            weight_dict=G.get_edge_data(m,n)
            if player[n].getState()==EXIT_MECHANISM:
                sum1 = sum1+((weight_dict['weight'])*i)
            else:
                sum1 = sum1+((weight_dict['weight'])*j)    
        player[m].setRisk(exp(-1*sum1))

    for m in range(len(G)):
        if player[m].getState()==EXIT_MECHANISM:
            fi=player[m].getRisk()
        else:
            fj=player[m].getRisk()

    gI=fi+C*i
    gj=(len(G)-1)*(fj+C*j)
    if i!=0:
        gI1=diff(gI,i)
    else:
        gI1=0
    if j!=0:
        gj1=diff(gj,j)
    else:
        gj1=0
    ij_opt = solve((gI1,gj1),i,j)
    return ij_opt


def main(args):
    if len(args) != 1:
        sys.exit("Usage: python self_dependence.py <graphml file>")
    
    C=1
    ofname=args[0]
    G = nx.read_graphml(ofname)
    G = nx.convert_node_labels_to_integers(G)
    k = len(G)
    player = [Player(i) for i in range(0, k)]
    i=symbols('i')
    x=[symbols('x1'),symbols('x2'),symbols('x3'),symbols('x4'),symbols('x5'),symbols('x6')]
    
    '''（社会最优投资）求社会投资的投资成本的最小值'''
    
    for m in range(k):
        sum=0
        for n in neighbors(G,m):
            weight_dict=G.get_edge_data(m,n)
            sum = sum+((weight_dict['weight'])*i)
        player[m].setRisk(math.e**(-1*sum))
            
    #print sum
    f_sopt=player[1].getRisk()
    g=k*(f_sopt+C*i)
    [l_sopt]=solve(diff(g,i))#社会最优投资水平
    g_sopt=g.evalf(subs={i:l_sopt})#社会最优的时候的总成本  
       
    '''假设节点1退出机制'''
    for m in range(k):
        player[m].setEinvest(x[m])

    player[0].setState(EXIT_MECHANISM)
    for m in range(len(G)):
        sum1=0
        for n in neighbors(G,m):
            weight_dict=G.get_edge_data(m,n)
            sum1 = sum1 + ((weight_dict['weight'])*(player[n].getEinvest()))
        player[m].setRisk(exp(-1*sum1))
        player[m].setCost(player[m].getRisk()+C*(player[m].getEinvest()))
   
    enter_g=0
    exit_g=0
    for m in range(k):
        if player[m].getState()==ENTER_MECHANISM:
            enter_g = enter_g + player[m].getCost()
        else:
            exit_g+=player[m].getCost() 
    #       
    #print enter_g
    #print exit_g
    
    
    
    def myfunc1(xx):
        x1,x2,x3,x4,x5,x6 = xx#根据下面enter_g的包含的未知数
        return x2 + x3 + x4 + x5 + x6 + exp(-x1 - 1.5*x2 - x3 - x4 - x5 - x6) + exp(-x1 - x2 - 1.5*x3 - x4 - x5 - x6) + exp(-x1 - x2 - x3 - 1.5*x4 - x5 - x6) + exp(-x1 - x2 - x3 - x4 - 1.5*x5 - x6) + exp(-x1 - x2 - x3 - x4 - x5 - 1.5*x6)#enter_g上面输出的表达书具体带入
            
    kk=fmin(myfunc1,(1,1,1,1,1,1))
    
    def myfunc2(xxx):
        x1,x2,x3,x4,x5,x6 = xxx#根据下面exit_g的包含的未知数
        return x1 + exp(-1.5*x1 - x2 - x3 - x4 - x5 - x6)#exit_g上面输出的表达书具体带入
        
    kkk=fmin(myfunc2,(1,1,1,1,1,1))
    
    print kk
    print kkk
    
    
if __name__ == "__main__":
    main(sys.argv[1:])
