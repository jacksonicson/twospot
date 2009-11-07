# print "TODO: use the new wsgiAdapter to start django here!!!"

import os

from wsgiAdapter import run_wsgi_app
#from wsgiAdapter import set




# Must set this env var before importing any part of Django
# 'project' is the name of the project created with django-admin.py
os.environ['DJANGO_SETTINGS_MODULE'] = 'hellopython.settings'

# Force Django to reload its settings.
from django.conf import settings
settings._target = None

import django.core.handlers.wsgi
import django.core.signals
import django.db
import django.dispatch.dispatcher

app = django.core.handlers.wsgi.WSGIHandler()

#set(wsgio_in)

run_wsgi_app(app)