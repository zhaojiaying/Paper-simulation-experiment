#-*- coding:UTF-8 -*-
import igraph, networkx, sys,pandas,numpy
import matplotlib.pyplot as plt

# This script generates an exponential (growing random) network with n
# vertices and mean degree k, and saves it in graphml format.

'''
def main(args):
    
    if len(args) != 1:
        sys.exit("Usage: python ?.py <graphml file>")
        
    net = args[0]
    G = networkx.read_graphml(net)
    G = networkx.convert_node_labels_to_integers(G)
    investment_dataframe=pandas.read_csv('investment.csv',header=None)
    investment_narray = numpy.array(investment_dataframe) #np.ndarray()
    investment_list=investment_narray.tolist() #list
    investment=[]
    degree=[]
    for i in range(len(investment_list)):
        [inv]=investment_list[i]
        investment.append(inv)
    for d in range(len(G)):
        degree.append(G.degree()[d])
    plt.title('degree&investment')
    plt.xlabel(r"degree")
    plt.ylabel(r"investment")
    plt.plot(degree,investment, "o")
    plt.show()
'''
class Node:
    def __init__(self,id):
        self.id=id
        self.degree=0.0
        self.weight=0.0
        
    def setdegree(self,degree):
        self.degree=degree
    
    def getdegree(self):
        return self.degree
    
    def setweight(self,weight):
        self.weight=weight
    
    def getweight(self):
        return self.weight

def neighbors(G, i):
    return G.neighbors(i)
        
def main(args):
    
    if len(args) != 1:
        sys.exit("Usage: python ?.py <graphml file>")
        
    net = args[0]
    G = networkx.read_graphml(net)
    G = networkx.convert_node_labels_to_integers(G)
    investment_dataframe=pandas.read_csv('investment.csv',header=None)
    investment_narray = numpy.array(investment_dataframe) #np.ndarray()
    investment_list=investment_narray.tolist() #list
    node = [Node(i) for i in range(len(G))]
    investment=[]
    centrality=[]
    for i in range(len(investment_list)):
        [inv]=investment_list[i]
        investment.append(inv)
    for d in range(len(G)):
        node[d].setdegree(G.degree()[d])
    
    for j in range(len(G)):
        sum=0.0+node[j].getdegree()
        for jj in neighbors(G,j):
          sum=sum+node[jj].getdegree()
        node[j].setweight(sum)
    
    for q in range(len(G)):
        centrality.append(node[q].getweight())
        
    plt.title('centrality&investment')
    plt.xlabel(r"centrality")
    plt.ylabel(r"investment")
    plt.plot(centrality,investment, "o")
    plt.show()




if __name__ == "__main__":
    main(sys.argv[1:])
