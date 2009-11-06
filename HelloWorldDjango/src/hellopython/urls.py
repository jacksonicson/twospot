from django.conf.urls.defaults import *
from django.contrib import admin
from django.http import HttpResponse

print "LOADING URLS"

urlpatterns = patterns('',
                       (r'test', 'hellopython.test.test.test'),
)
