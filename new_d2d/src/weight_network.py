#-*- coding:UTF-8 -*-
import igraph, sys,json
import matplotlib.pyplot as plt
import networkx as nx

# This script generates an exponential (growing random) network with n
# vertices and mean degree k, and saves it in graphml format.
'''
生成一个相互联系有权重的weight，a为对角线
'''

def main(args):
    if len(args) != 1:
        sys.exit("Usage: python weight_network.py <params.json.sample>")
    
    
    params = json.load((open(args[0], "r")))
    a=params['a']    
    G=nx.Graph()
    for i in range(6):
        for j in range(6):
            if i==j:
                G.add_edge(i,j,weight=a)
            else:
                G.add_edge(i,j,weight=1) 

    elarge=[(u,v) for (u,v,d) in G.edges(data=True) if d['weight'] >1]  
    esmall=[(u,v) for (u,v,d) in G.edges(data=True) if d['weight'] ==1]  
    nx.draw_networkx_edges(G,nx.circular_layout(G),edgelist=elarge,width=2)
    nx.draw_networkx_edges(G,nx.circular_layout(G),edgelist=esmall,width=1,alpha=0.5,edge_color='b',style='dashed')#dashed表示虚线 

    #G = nx.convert_node_labels_to_integers(G)
    nx.write_graphml(G, 'weight.graphml')#生成图并保存在graphml文件中
   # plt.show()#可以将这个网络图形显示出来
'''
__name__是指示当前py文件调用方式的方法。
如果它等于"__main__"就表示是直接执行，如果不是，则用来被别的文件调用。
以下这段代码的意思：只有直接执行的时候才会执行main（），调用的时候不执行main（）
'''
if __name__ == "__main__":
    main(sys.argv[1:])
