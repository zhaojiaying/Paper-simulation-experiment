#-*- coding: UTF-8 -*-
import pandas,sys,numpy
import matplotlib.font_manager as font_manager
import matplotlib.pyplot as plt


t=pandas.read_csv('t.csv',header=None)  
exit_rate =pandas.read_csv('rate.csv',header=None)

#font_prop = font_manager.FontProperties(size = 8)
plt.figure(1, figsize = (12, 3))
plt.title('Exit mechananism rate')
plt.xlabel(r"Time")
plt.ylabel(r"Exit mechananism rate")

time=list(t[0]) 
exit_rate = list(exit_rate[0])



plt.plot(time,exit_rate, "r-", markersize=1, alpha = 0.6,label="rate")

plt.legend(loc=1,prop={'size':6})
plt.savefig("exit_rate.pdf", bbox_inches = "tight")
plt.show()
plt.close(1)
print 'The image has been generated'
    