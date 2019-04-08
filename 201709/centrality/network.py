#-*- coding:UTF-8 -*-
import igraph, networkx, sys
import matplotlib.pyplot as plt

# This script generates an exponential (growing random) network with n
# vertices and mean degree k, and saves it in graphml format.
'''
生成一个随机增长的指数网络，
n个顶点，平均度为k(度表示一个顶点相关联的边数)
'''

def main():
    '''sys.argv将所有的输入参数装入list中返回给argv变量
    3的意思是3个变量，一个是gr_network.py,还有两个是n和k'''
    if len(sys.argv) != 3:
        sys.exit('Usage: python network.py <n> <k>')
    n = int(sys.argv[1])
    k = int(sys.argv[2])
    m = k / 2#每条边都多算了一次，因为一条边两个顶点
    g = igraph.Graph.Growing_Random(n, m, citation = True)
    g.write_graphml('gr%d.graphml' %(k))
    g = networkx.Graph(networkx.read_graphml('gr%d.graphml' %(k)))
    g = networkx.convert_node_labels_to_integers(g)
    networkx.write_graphml(g, 'gr%d.graphml' %(k))#生成图并保存在graphml文件中
    networkx.draw(g,width=0.5,node_size=20)
    plt.show()#可以将这个网络图形显示出来
    #print g.degree()[0]
   
'''
__name__是指示当前py文件调用方式的方法。
如果它等于"__main__"就表示是直接执行，如果不是，则用来被别的文件调用。
以下这段代码的意思：只有直接执行的时候才会执行main（），调用的时候不执行main（）
'''
if __name__ == '__main__':
    main()
