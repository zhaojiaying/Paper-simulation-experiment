#-*- coding:UTF-8 -*-
import igraph, pickle, operator, random, sys,json,math
import pandas
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
        self.invest_x = 0.0    #节点投资
        self.e_invest_x = 0.0   #有人退出后的投资水平   
        self.risk_f = 0.0    #节点风险
        self.cost_g=0.0
        self.diffcost=0.0
        self.tax=0.0
        self.state=ENTER_MECHANISM

    def setInvest(self, invest_x):#调用的时候，Offender.set_opopulation(v)这里的v用具体的策略替换
        self.invest_x = invest_x

    def getInvest(self):
        return self.invest_x
    
    def setEinvest(self, e_invest_x):
        self.e_invest_x = e_invest_x

    def getEinvest(self):
        return self.e_invest_x
    
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
    
    def setDiffcost(self, diffcost):
        self.diffcost =diffcost

    def getDiffcost(self):
        return self.diffcost
    
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
        sys.exit("Usage: python self.py <graphml file>")
    
    C=1
    ofname=args[0]
    G = nx.read_graphml(ofname)
    G = nx.convert_node_labels_to_integers(G)
    k = len(G)
    player = [Player(i) for i in range(0, k)]
    x=[symbols('x1'),symbols('x2'),symbols('x3'),symbols('x4'),symbols('x5'),symbols('x6')]
    
    for m in range(k):
        player[m].setInvest(x[m])

    '''
            社会最优投资）求社会投资的投资成本的最小值
    '''    
    for m in range(k):
        sum=0
        for n in neighbors(G,m):
            weight_dict=G.get_edge_data(m,n)
            sum = sum + ((weight_dict['weight'])*(player[n].getInvest()))
        player[m].setRisk(exp(-1*sum))
        player[m].setCost(player[m].getRisk()+C*(player[m].getInvest()))
        #player[m].setDiffcost(diff(player[m].getCost(),player[m].getInvest()))
        #player[m].setInvest(solve(player[m].getDiffcost(),player[m].getInvest()))
        
    social_g=0  
    for m in range(k):
        social_g = social_g + player[m].getCost()

    ##print social_g
    
    def myfunc(x):
        x1,x2,x3,x4,x5,x6 = x
        #social_g从上面输出的表达式输出
        return x1 + x2 + x3 + x4 + x5 + x6 + exp(-1.5*x1 - x2 - x3 - x4 - x5 - x6) + exp(-x1 - 1.5*x2 - x3 - x4 - x5 - x6) + exp(-x1 - x2 - 1.5*x3 - x4 - x5 - x6) + exp(-x1 - x2 - x3 - 1.5*x4 - x5 - x6) + exp(-x1 - x2 - x3 - x4 - 1.5*x5 - x6) + exp(-x1 - x2 - x3 - x4 - x5 - 1.5*x6)
        
    k=fmin(myfunc,(1,1,1,1,1,1))
    
    for m in range(k):
        player[m].setInvest(k[m])
    
    min_social_g=2.650894
    #min_social_g=social_g.evalf(subs={x1:player[0].getInvest(),x2:player[1].getInvest(),x3:player[2].getInvest(),x4:player[3].getInvest(),x5:player[4].getInvest(),x6:player[5].getInvest()})
    
    '''
    '''#假设某个节点退出了'''
    y=[symbols('y1'),symbols('y2'),symbols('y3'),symbols('y4'),symbols('y5'),symbols('y6')]
    for m in range(k):
        player[m].setInvest(y[m])
        
    I=random_vertex(G) #这个节点退出机制
    player[I].setState(EXIT_MECHANISM)
    for m in range(len(G)):
        e_sum=0
        for n in neighbors(G,m):
            weight_dict=G.get_edge_data(m,n)
            e_sum = e_sum + ((weight_dict['weight'])*(player[n].getEinvest()))
        player[m].setRisk(exp(-1*e_sum))
        player[m].setCost(player[m].getRisk()+C*(player[m].getEinvest()))
        
    
    enter_g=0
    exit_g=0
    for m in range(k):
        if player[m].getState()==ENTER_MECHANISM:
            enter_g = enter_g + player[m].getCost()
        else:
            exit_g+=player[m].getCost() 
    #       
    print enter_g
    print exit_g
    '''
    '''
    '''
    def myfunc1(xx):
        x1,x2,x3,x4,x5,x6 = xx根据下面enter_g的包含的未知数
        return enter_g上面输出的表达书具体带入
            
    kk=fmin(myfunc1,(1,1,1,1,1,1))
    
    def myfun2(xxx):
        x1,x2,x3,x4,x5,x6 = xxx根据下面exit_g的包含的未知数
        return exit_g上面输出的表达书具体带入
        
    kkk=fmin(myfunc1,(1,1,1,1,1,1))
    
    
    '''
    '''
    for m in range(k):
        for n in range(k):
            if kk[n]==kkk[n]
            player[n].setEinvest(kk[n])
    
    '''
    '''
    if ij_opt[i]<0:
        i_opt=0
        ij_opt=exit_fun(G,player,i_opt,j)
        j_opt=ij_opt[j]
    else:
        i_opt=ij_opt[i]
        j_opt=ij_opt[j]
    gj_opt=gj.evalf(subs={i:i_opt,j:j_opt})

    '''#计算税'''
    '''
    tax=(g_sopt*(k-1))/k-gj_opt
    tax_sum=(k-1)*tax+tax
    print tax_sum
    
    '''
    
if __name__ == "__main__":
    main(sys.argv[1:])
