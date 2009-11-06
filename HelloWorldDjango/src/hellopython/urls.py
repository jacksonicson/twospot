from django.conf.urls.defaults import *
from django.contrib import admin
from django.http import HttpResponse

# HelloWorld
def index(request):
    print "Hello World"
    return HttpResponse("Hello World from Django")


urlpatterns = patterns('',
    (r'^hellopython', 'hellopython.urls.index'),
)
