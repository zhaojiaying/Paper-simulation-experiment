#-*- coding:UTF-8 -*-
import igraph, networkx, sys,pandas,numpy
import matplotlib.pyplot as plt


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
    nodes_color=[]
    nodes_size=[]
    for i in range(len(investment_list)):
        [inv]=investment_list[i]
        investment.append(inv)
    '''度的节点size大'''   
    for i in range(len(G)):
        d=G.degree()[i]
        if d>=0 and d<2:
            nodes_size.append(20)
        elif d>=2 and d<4:
            nodes_size.append(50)
        elif d>=4 and d<6:
            nodes_size.append(100)
        elif d>=6 and d<8:
            nodes_size.append(160)
        elif d>=8 and d<10:
            nodes_size.append(200)
        elif d>=10:
            nodes_size.append(1000)
            
            
    '''投资大的颜色偏红'''        
            
    for i in range(len(G)):
        invest=investment[i]
        if invest>=0 and invest<0.2:
            nodes_color.append('#FFFFB9')
        elif invest>=0.2 and invest<0.4:
            nodes_color.append('#FFFF37')
        elif invest>=0.4 and invest<0.6:
            nodes_color.append('#FF8000')
        elif invest>=0.6 and invest<0.8:
            nodes_color.append('#F75000')
        elif invest>=0.8 and invest<0.9:
            nodes_color.append('#F75000')
        elif invest>=1:
            nodes_color.append('#EA0000')
            
    networkx.draw_spring(G,width=0.5,with_labels=False,font_size =10,node_size =nodes_size,node_color = nodes_color,node_width=0)
    plt.show()#可以将这个网络图形显示出来
    plt.savefig('RELITU.png')


  
'''
__name__是指示当前py文件调用方式的方法。
如果它等于"__main__"就表示是直接执行，如果不是，则用来被别的文件调用。
以下这段代码的意思：只有直接执行的时候才会执行main（），调用的时候不执行main（）
'''
if __name__ == '__main__':
    main(sys.argv[1:])