import weight_network, json, networkx, numpy, operator, pickle, random, sys, os, pandas
import self7537

fp = open(r'params.json.sample', 'r')
params = json.load(fp)
f=open('a_tax.txt','w')
while params['a']<=6.5:
        m=os.popen(r'python weight_network.py params.json.sample')
        l=os.popen(r'python self7537.py weight.graphml')
        n=l.read()       
        f.write(str(n)) 
        params['a']=params['a']+0.5
        fp1=file('params.json.sample','w')
        json.dump(params,fp1)
        print params['a']
        
f.close()   
fp.close()


