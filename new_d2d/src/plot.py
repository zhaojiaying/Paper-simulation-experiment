#-*- coding: UTF-8 -*-
import pandas, pylab, sys,numpy
import matplotlib.font_manager as font_manager


    
a_tax =pandas.read_csv('a_tax.csv',header=None)
  
font_prop = font_manager.FontProperties(size = 12)
pylab.figure(1, figsize = (12, 3), dpi = 500)
pylab.title('Budget in the Pivotal Mechanism')
pylab.xlabel(r"a", fontproperties = font_prop)
pylab.ylabel(r"Budget", fontproperties = font_prop)
 
a = list(a_tax[0])
budget=list(a_tax[1])
pylab.plot(a,budget, "b-", linewidth = 2, alpha = 0.6)
pylab.xticks([1,1.5,2,2.5,3,3.5,4,4.5,5,5.5,6],[r'$1$',r'$1.5$',r'$2$',r'$2.5$',r'$3$',r'$3.5$',r'$4$',r'$4.5$',r'$5$',r'$5.5$',r'$6$'])
pylab.yticks([-2,-1,0],[r'$-2$',r'$-1$',r'$0$'])
pylab.savefig("a_tax.pdf", format = "pdf", bbox_inches = "tight")
pylab.close(1)
print 'The image has been generated'
    