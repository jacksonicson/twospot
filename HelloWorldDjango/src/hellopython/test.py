import os
from wsgi_adapter import WsgiApp


# Tells django where to find the project settings
os.environ['DJANGO_SETTINGS_MODULE'] = 'hellopython.settings'

# Import django settings and reloads the application settings
from django.conf import settings
settings._target = None

# Import django libraries
import django.core.handlers.wsgi
import django.core.signals
import django.db
import django.dispatch.dispatcher

# Create a new wsgi handler from the django framework
app = django.core.handlers.wsgi.WSGIHandler()

# Start wsgi application
myApp = WsgiApp(wsgiChannel, headers, threadName)
myApp.run_wsgi_app(app)
