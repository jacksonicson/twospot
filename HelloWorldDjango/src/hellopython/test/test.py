'''
Created on Nov 7, 2009

@author: Andreas Wolke
'''
from django.http import HttpResponse

def test(request):
    # print "Hello World Django"
    response = HttpResponse("Hello World Django")
    return response 