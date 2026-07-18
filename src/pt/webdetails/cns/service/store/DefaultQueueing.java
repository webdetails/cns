/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2002 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

package pt.webdetails.cns.service.store;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.Constants;
import pt.webdetails.cns.api.INotificationPoll;
import pt.webdetails.cns.service.Notification;
import pt.webdetails.cns.utils.SessionUtils;

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

  public synchronized boolean pushToUser( String user, Notification notification ) {

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

  public synchronized boolean pushToRole( String role, Notification notification ) {

    if ( StringUtils.isEmpty( role ) ) {
      logger.error( "role cannot be null" );
      return false;
    } else if ( notification == null ) {
      logger.error( "Notification event cannot be null" );
      return false;
    }

    boolean success = false;

    String[] users = SessionUtils.getUsersInRole( notification.getRecipient() );

    if ( users != null && users.length > 0 ) {

      for ( String user : users ) {

        notification.setRecipient( user );
        success |= pushToUser( notification.getRecipient(), notification );
      }

    } else {
      logger.info( "No users found for role '" + notification.getRecipient() + "'" );
    }

    return success;
  }

  public synchronized boolean pushToAll( Notification notification ) {

    if ( notification == null ) {
      logger.error( "Notification event cannot be null" );
      return false;
    }

    Set<String> users = notificationsPollingQueue.keySet();

    if ( users != null ) {

      for ( String user : users ) {
        pushToUser( user, notification );
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
