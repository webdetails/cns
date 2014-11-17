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


How to send out a notification
------------------------------

An extensive list of endpoints is offered:

[ GET | POST ] /pentaho/api/plugin/cns/api/do/notify
[ GET | POST ] /pentaho/api/plugin/cns/api/do/notify/all
[ GET | POST ] /pentaho/api/plugin/cns/api/do/notify/role
[ GET | POST ] /pentaho/api/plugin/cns/api/do/notify/user


A) sending the notification info in the path:

pentaho/plugin/cns/api/do/notify/all/{notificationType}/{title}/{message}
pentaho/plugin/cns/api/do/notify/role/{recipient}/{notificationType}/{title}/{message}
pentaho/plugin/cns/api/do/notify/user/{recipient}/{notificationType}/{title}/{message}

B) sending the notification info as url parameters:

pentaho/plugin/cns/api/do/notify/all?notificationType={notificationType}&title={title}&message={message}
pentaho/plugin/cns/api/do/notify/role?recipient={recipient}&notificationType={notificationType}&title={title}&message={message}
pentaho/plugin/cns/api/do/notify/user?recipient={recipient}&notificationType={notificationType}&title={title}&message={message}

C) sending the notification info as form parameters:

pentaho/plugin/cns/api/do/notify/all
	{
		notificationType={notificationType}
		title={title}
		message={message}
	}

pentaho/plugin/cns/api/do/notify/role
	{
		recipient={recipient}
		notificationType={notificationType}
		title={title}
		message={message}
	}

pentaho/plugin/cns/api/do/notify/user
	{
		recipient={recipient}
		notificationType={notificationType}
		title={title}
		message={message}
	}


Working examples:


A) sending the parameters in the path:


pentaho/plugin/cns/api/do/notify/user/admin/default/Marketplace/There are updates for your plugin 'CDE' in the markeplace


B) sending the notification info as url parameters:

pentaho/plugin/cns/api/do/notify/user?recipient=admin?notificationType=default?title=Marketplace&message=There are updates for your plugin 'CDE' in the markeplace&link=www.pentaho.com/marketplace

C) sending the notification info as form parameters:

pentaho/plugin/cns/api/do/notify/user
	{
		recipient=admin
		notificationType=default
		title=Marketplace
		message=There are updates for your plugin 'CDE' in the markeplace
		link=www.pentaho.com/marketplace
	}

