'''
Created on Nov 7, 2009

@author: Andreas Wolke
'''
from django.http import HttpResponse

def test(request):
    # print "Hello World Django"
    value = ""
    for i in range(0,100):
        value += "test \n"
    
    response = HttpResponse(value)
    return response 