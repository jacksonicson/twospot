<html>

<head>
<title>Twospot</title>
</head>

<body onload="prettyPrint()">

<link href="prettify.css" type="text/css" rel="stylesheet" />
<link href="twospot.css" type="text/css" rel="stylesheet" />

<script type="text/javascript" src="prettify.js"></script>

<div id="page">
<h1>Twospot documentation</h1>

<h2>TwoSpot HelloWorld (Java)</h2>

<h3>Setup</h3>
<p>
Die TwoSpot-Tools sind eine Sammlung von Python Hilfs-Scripten die der vereinfachten Anwendungsentwicklung dienen. &Uuml;ber die Tools lassen sich Anwendungen deployen. Zus�tzlich helfen Sie auch beim Anlegen neuer Anwendungen. Die Tools lassen sich <a href="./dwls/tools.zip">hier</a> herunterladen. 
</p>

<p>
Das ZIP-Archiv <code>tools.zip</code> kann ein beliebiges Verzeichnis extrahiert werden. Das Verzeichnis muss in die <code>PATH</code>-Umgebungsvariabel aufgenommen werden, damit der <code>twospot.py</code>-Befehl verwendet werden kann.
</p>

<p>
Vor dem Deployment einer Anwendung muss f�r diese eine AppId registriert werden. Die Registrierung erfolgt �ber das TwoSpot-Portal, das <a href="../../start.htm">hier</a> erreichbar ist. Nach der Benutzerregistrierung lassen sich drei AppId's registrieren. Nachfolgend wird als AppId der Name <code>gogo</code> verwendet. Dieser muss durch die eigenen AppId ersetzt werden. 
</p>

<h3>Dateistruktur</h3>
<p>Die Dateistruktur von TwoSpot-Anwendungen weist sehr gro�e �hnlichkeit mit der Dateistruktur gew�hnlicher Java Web-Anwendungen auf. Die Dateistruktur der leeren <code>gogo</code>-Anwendungen hat die Form:</p>

<pre class="prettyprint lang-htm">
 /helloworld
    /WEB-INF
        /web.xml
        /classes
            /class files
            /application configuration files (e.g. the etc directory)
        /lib
            /jar files
    /app.yaml
    /index.html
    /*.jsp
    /...
</pre>

<p>
Die Verzeichnisstruktur l�sst sich einfach �ber die Tools erstellen. Dazu muss der folgende Befehl in dem Verzeichnis ausgef�hrt werden, in dem die Dateistruktur angelegt werden soll. 
</p>

<pre class="prettyprint bsh">
twospot.py --createProject gogo --type java
</pre>

<p>
Jede TwoSpot-Anwendung muss die Datei <code>app.yaml</code> enthalten. Dabei handelt es sich um eine Konfigurationsdatei die Informationen �ber die Anwendung (AppId, Typ der Runtime) und auch Konfigurationseinstellungen f�r den Application-Container beinhaltet. Beim Anlegen der Verzeichnisstruktur �ber die Tools wird die <code>app.yaml</code>-Datei automatisch erstellt. F�r die HelloWorld-Anwendung sind keine �nderungen an der Datei notwendig. 
</p>

<h3>Startseite</h3>

<p>Im Anwendungsverzeichnis kann nun eine einfache HTML-Seite als Startseite f�r die TwoSpot-Anwendung angelegt werden. F�r die <code>gogo</code>-Anwendung wurde die folgende <code>index.jsp</code>-Datei angelegt:</p>

<pre class="prettyprint lang-htm">
&lt;html&gt;
   &lt;body&gt;
      &lt;h1&gt;GoGo - HelloWorld&lt;/h1&gt;
   &lt;/body&gt;
&lt;/html&gt;
</pre>

<h3>Deployment</h3>
<p>Nach dem Anlegen der Startseite kann die TwoSpot-Anwendung in die Platform deployt werden. Das Deployment erfolgt ebenfalls �ber die Tools. Der folgende Befehl muss im Verzeichnis der <code>gogo</code>-Anwendung ausgef�hrt werden. Beim Deployment fragt das Script nach dem Benutzernamen und Passwort, um die Berechtigung des Deployments zu pr�fen. 
</p>

<pre class="prettyprint lang bsh">
twospot.py --deploy
</pre>

<p>Ein erfolgreiches Deployment wird mit einem <code>Status 200</code> angezeigt. Die Anwendung ist anschlie�end unter der http-Adresse <code>http://appId.twospot.informatik.fh-augsburg.de:6060/index.jsp</code> erreichbar.</p>
<p>
<b>Info: Beim ersten Anwendungsstart ist die Response-Time stark erh�ht!</b>
</p>

<p>
Die Quellocdes der gogo-Anwendung k�nnen <a href="dwls/gogo.zip">hier</a> heruntergeladen werden. 

<br/>

</body>
</div>

</html>