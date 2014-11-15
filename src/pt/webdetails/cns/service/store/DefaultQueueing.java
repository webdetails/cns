/*!
* This program is free software; you can redistribute it and/or modify it under the
* terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
* Foundation.
*
* You should have received a copy of the GNU Lesser General Public License along with this
* program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
* or from the Free Software Foundation, Inc.,
* 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
*
* This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU Lesser General Public License for more details.
*
* Copyright (c) 2002-2014 Pentaho Corporation..  All rights reserved.
*/
package pt.webdetails.cns.service.store;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.Constants;
import pt.webdetails.cns.api.INotificationPoll;
import pt.webdetails.cns.service.Notification;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class DefaultQueueing implements INotificationPoll {

  private Logger logger = LoggerFactory.getLogger( DefaultQueueing.class );
  private Map<String, Queue<Notification>> notificationsPollingQueue;

  public DefaultQueueing() {
    notificationsPollingQueue = new HashMap<String, Queue<Notification>>();
  }

  public boolean isPollEmpty( String user ) {
    return StringUtils.isEmpty( user ) || !notificationsPollingQueue.containsKey( user )
      || notificationsPollingQueue.get( user ).size() == 0;
  }

  public synchronized boolean subscribe( String user, String eventType ) {
    return subscribeAll( user ); // TODO
  }

  public synchronized boolean subscribeAll( String user ) {

    if ( StringUtils.isEmpty( user ) ) {
      logger.error( "user cannot be null" );
      return false;
    }

    if ( !notificationsPollingQueue.containsKey( user ) ) {
      notificationsPollingQueue.put( user, new LinkedList<Notification>() );
    }
    return true;
  }

  public synchronized boolean push( String user, Notification notification ) {

    if ( StringUtils.isEmpty( user ) ) {
      logger.error( "user cannot be null" );
      return false;
    } else if ( notification == null ) {
      logger.error( "Notification event cannot be null" );
      return false;
    }

    if ( !notificationsPollingQueue.containsKey( user ) ) {
      notificationsPollingQueue.put( user, new LinkedList<Notification>() );

    } else if ( notificationsPollingQueue.get( user ).size() > Constants.DEFAULT_MAX_LIST_SIZE ) {
      logger.warn( "polling queue for user '" + user + "' has reached max size of " + Constants.DEFAULT_MAX_LIST_SIZE
        + "; new notifications will be discarded until size drops below " + Constants.DEFAULT_MAX_LIST_SIZE );
      return true;
    }

    notificationsPollingQueue.get( user ).add( notification );
    return true;
  }

  public synchronized boolean pushToAll( Notification notification ) {

    if ( notification == null ) {
      logger.error( "Notification event cannot be null" );
      return false;
    }

    Set<String> users = notificationsPollingQueue.keySet();

    if ( users != null ) {

      for ( String user : users ) {
        push( user, notification );
      }
    }

    return true;
  }

  public synchronized Notification pop( String user ) {
    return isPollEmpty( user ) ? null : notificationsPollingQueue.get( user ).remove(); // a.k.a. pop from stash
  }

  public synchronized void clearQueue( String user ) {
    notificationsPollingQueue.get( user ).clear();
  }

  public synchronized void clearAllQueues() {

    Set<String> users = notificationsPollingQueue.keySet();

    if ( users != null ) {

      for ( String user : users ) {
        clearQueue( user );
      }
    }
  }
}
