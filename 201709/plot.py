#-*- coding: UTF-8 -*-
import pandas,sys,numpy
import matplotlib.font_manager as font_manager
import matplotlib.pyplot as plt


t=pandas.read_csv('t.csv',header=None)  
original =pandas.read_csv('ori.csv',header=None)
mechanism = pandas.read_csv('add.csv',header=None)
exit_mechanism=pandas.read_csv('rew.csv',header=None)
enter_exit_mechanism = pandas.read_csv('imi.csv',header=None)


  
#font_prop = font_manager.FontProperties(size = 8)
plt.figure(1, figsize = (12, 3))
plt.title('Evolution process')
plt.xlabel(r"time")
plt.ylabel(r"Total payoff")

wheel=list(t[0]) 
original_total_payoff = list(original[0])
mechanism_total_payoff=list(mechanism[0])
exit_total_payoff=list(exit_mechanism[0])
enter_total_payoff=list(enter_exit_mechanism[0])


plt.plot(wheel,original_total_payoff, "r-", linewidth = 1, alpha = 1,label="original")
plt.plot(wheel,mechanism_total_payoff, "k-", linewidth = 1, alpha = 1,label="add_mechanism")
plt.plot(wheel,exit_total_payoff, "m-", linewidth = 1, alpha = 1,label="reward_exit_mechanism")
plt.plot(wheel,enter_total_payoff, "c-", linewidth = 1, alpha = 1,label="imitate_exit_mechanism")
'''
plt.plot(wheel,original1_total_payoff, "r-", linewidth = 0.5, alpha = 0.4,label="original1")
plt.plot(wheel,mechanism1_total_payoff, "k-", linewidth = 0.5, alpha = 0.4,label="mechanism1")
plt.plot(wheel,exit1_total_payoff, "m-", linewidth = 0.5, alpha = 0.4,label="exit mechanism1")
plt.plot(wheel,enter1_total_payoff, "c-", linewidth = 0.5, alpha = 0.4,label="enter mechanism1")
'''
#plt.xticks([0,5,10,15,20,100],[r'$1$',r'$1.5$',r'$2$',r'$2.5$',r'$3$',r'$3.5$',r'$4$',r'$4.5$',r'$5$',r'$5.5$',r'$6$'])
#plt.yticks([-2,-1,0],[r'$-2$',r'$-1$',r'$0$'])

plt.legend(loc=0,prop={'size':8})
plt.show()
plt.savefig("image.pdf", format = "pdf", bbox_inches = "tight")
plt.close(1)
print 'The image has been generated'
    