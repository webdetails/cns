Community Notification System
=============================

Objective
---------

The goal of this project is provide the ability to push notifications to BA-server.

Getting started
---------------

The first thing you should do is to confirm you have ant installed ( http://ant.apache.org/ ).

To prep the project, you first need to resolve/include the necessary dependencies.
Ivy.xml file contains a list of the dependencies needed to successfully compile this project.

### To fetch dependencies and populate /lib folder 

From the project root and using command-line simply type *ant resolve*


How to use
----------

Following this steps should get you going:

### Compile the project

Just run *ant* and you should be all set


### Deploying the plugin in your pentaho environment

Copy the zip folder in ./dist folder and unzip it at 

pentaho-solutions/system/


### Warning: the following is a temporary solution; further iterations of this plugin should have this fixed in some other proper manner.

### Initialize the notification's longpolling mechanism

Edit /tomcat/webapps/pentaho/mantle/Mantle.jsp and:

1 - find 

window.allFramesLoaded = true; 

( ~ line 210 ) and immediately below it place:

new Poll().start("/pentaho/plugin/cns/api/queue/subscribe","/pentaho/plugin/cns/api/queue/update");


2 - find 

<script type="text/javascript"> 

declared slightly above step 1 ( ~ line 187 ) and above it place:

<script type="text/javascript" src="/pentaho/api/repos/cns/resources/js/notify/notify-modified.js"></script>
<script type="text/javascript" src="/pentaho/api/repos/cns/resources/js/cns.js"></script>


And you should be all set


