from sympy import *
from scipy.optimize import fmin

x=[symbols('x1'),symbols('x2'),symbols('x3'),symbols('x4'),symbols('x5'),symbols('x6')]
def myfunc(x):
    x1,x2,x3,x4,x5,x6 = x
    return x1 + x2 + x3 + x4 + x5 + x6 + exp(-1.5*x1 - x2 - x3 - x4 - x5 - x6) + exp(-x1 - 1.5*x2 - x3 - x4 - x5 - x6) + exp(-x1 - x2 - 1.5*x3 - x4 - x5 - x6) + exp(-x1 - x2 - x3 - 1.5*x4 - x5 - x6) + exp(-x1 - x2 - x3 - x4 - 1.5*x5 - x6) + exp(-x1 - x2 - x3 - x4 - x5 - 1.5*x6)

k= fmin(myfunc,(1,1,1,1,1,1))[0]
print k