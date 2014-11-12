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
package pt.webdetails.cns.service;

import com.google.common.eventbus.AsyncEventBus;
import org.apache.commons.lang.StringUtils;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.objfac.references.SingletonPentahoObjectReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.Constants;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.api.INotificationEventHandler;
import pt.webdetails.cns.service.store.INotificationStorage;
import pt.webdetails.cns.service.store.NotificationPollingQueue;
import pt.webdetails.cns.utils.SessionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;

public class NotificationEngine {

  public static final String EVENT_BUS_ID = "notification-async-event-bus";

  private static NotificationEngine instance;
  private Logger logger = LoggerFactory.getLogger( NotificationEngine.class );

  private INotificationStorage notificationStorage;
  private NotificationPollingQueue notificationPollingQueue = new NotificationPollingQueue();
  ;
  private Map<String, INotificationEvent> eventMap = new HashMap<String, INotificationEvent>();

  private NotificationEngine() {
    if ( getAsyncEventBus() == null ) {
      initializeAsyncEventBus();
    }
  }

  public static NotificationEngine getInstance() {

    if ( instance == null ) {
      instance = new NotificationEngine();
    }

    return instance;
  }

  public boolean notify( INotificationEvent e ) {
    if ( e == null ) {
      logger.error( "Notification event cannot be null" );
      return false;
    }

    try {

      getAsyncEventBus().post( e );
      return true;

    } catch ( Throwable t ) {
      logger.error( t.getLocalizedMessage(), t );
    }
    return false;
  }

  public boolean subscribeToPoolingQueue( String user ) {
    return getNotificationPollingQueue().subscribe( user );
  }

  public boolean pushToPoolingQueue( INotificationEvent event ) {

    Notification notification = new Notification( event );

    if ( notification != null ) {

      notification.setUnread( true );
      notification.setTimestampInMillis( new Date().getTime() );

      if ( notification.getRecipientType() == null ) {
        logger.error( "notification from '" + notification.getSender() + "' with title '" + notification.getTitle()
          + "' has a null notification.getRecipientType() => discarding" );
        return false;

      } else if ( StringUtils.isEmpty( notification.getRecipient() )
        && INotificationEvent.RecipientType.ALL != notification.getRecipientType() ) {
        logger.error( "notification from '" + notification.getSender() + "' with title '" + notification.getTitle()
          + "' has a null notification.getRecipient() => discarding" );
        return false;
      }

      if ( StringUtils.isEmpty( notification.getNotificationType() ) ) {
        notification.setNotificationType( Constants.DEFAULT_NOTIFICATION_TYPE );
      }

      if ( INotificationEvent.RecipientType.ALL == notification.getRecipientType() ) {

        return getNotificationPollingQueue().pushToAll( notification );

      } else if ( INotificationEvent.RecipientType.USER == notification.getRecipientType() ) {

        if ( SessionUtils.userExists( notification.getRecipient() ) ) {
          return getNotificationPollingQueue().push( notification.getRecipient(), notification );

        } else {
          logger.error( "notification.getRecipient(): '" + notification.getRecipient() + "' is not a valid user" );
          return false;
        }

      } else if ( INotificationEvent.RecipientType.ROLE == notification.getRecipientType() ) {

        if ( SessionUtils.roleExists( notification.getRecipient() ) ) {

          String[] users = SessionUtils.getUsersInRole( notification.getRecipient() );

          if ( users != null && users.length > 0 ) {

            boolean success = false;

            for ( String user : users ) {

              notification.setRecipient( user );
              success |= getNotificationPollingQueue().push( notification.getRecipient(), notification );
            }

            return success;

          } else {
            logger.warn( "no users were found having the role: '" + notification.getRecipient() + "' => discading" );
            return true;
          }

        } else {
          logger.error( "notification.getRecipient(): '" + notification.getRecipient() + "' is not a valid role" );
          return false;
        }
      }
    }
    return false;
  }

  public Notification popFromPollingQueue( String user ) {

    Notification nextNotification = null;

    while ( getNotificationPollingQueue().isQueueEmpty( user ) ) {

      try {
        logger.debug( "no notifications in queue; we'll check again in ~ 3 secs" );
        Thread.sleep( 3000 ); // sleep for 3 secs
      } catch ( InterruptedException e ) {
        // do nothing
      }
    }

    return getNotificationPollingQueue().pop( user );
  }

  public boolean putInStorage( INotificationEvent event ) {

    Notification notification = new Notification( event );

    if ( notification != null ) {

      notification.setUnread( true );
      notification.setTimestampInMillis( new Date().getTime() );

      if ( notification.getRecipientType() == null ) {
        logger.error( "notification from '" + notification.getSender() + "' with title '" + notification.getTitle()
          + "' has a null notification.getRecipientType() => discarding" );
        return false;

      } else if ( StringUtils.isEmpty( notification.getRecipient() )
        && INotificationEvent.RecipientType.ALL != notification.getRecipientType() ) {
        logger.error( "notification from '" + notification.getSender() + "' with title '" + notification.getTitle()
          + "' has a null notification.getRecipient() => discarding" );
        return false;
      }

      if ( StringUtils.isEmpty( notification.getNotificationType() ) ) {
        notification.setNotificationType( Constants.DEFAULT_NOTIFICATION_TYPE );
      }

      if ( INotificationEvent.RecipientType.ALL == notification.getRecipientType() ) {

        return getNotificationStorage().store( notification.getRecipientType(), notification );

      } else if ( INotificationEvent.RecipientType.USER == notification.getRecipientType() ) {

        if ( SessionUtils.userExists( notification.getRecipient() ) ) {
          return getNotificationStorage().store( notification.getRecipientType(), notification );

        } else {
          logger.error( "notification.getRecipient(): '" + notification.getRecipient() + "' is not a valid user" );
          return false;
        }

      } else if ( INotificationEvent.RecipientType.ROLE == notification.getRecipientType() ) {

        // INotificationEvent.RecipientType.ROLE: validate if recipient is a real role
        if ( SessionUtils.roleExists( notification.getRecipient() ) ) {
          return getNotificationStorage().store( notification.getRecipientType(), notification );

        } else {
          logger.error( "notification.getRecipient(): '" + notification.getRecipient() + "' is not a valid role" );
          return false;
        }
      }
    }
    return false;
  }

  public int getTotalCount( String user, String[] roles, boolean unreadOnly ) {
    return getNotificationStorage().getTotalCount( user, roles, unreadOnly );
  }

  public Notification getNextUnread( String user, String[] roles ) {
    return getNotificationStorage().getNextUnread( user, roles );
  }

  public List<Notification> getAll( String user, String[] roles, boolean unreadOnly ) {
    return getNotificationStorage().getAll( user, roles, unreadOnly );
  }

  public Notification getNotificationById( String id ) {
    return getNotificationStorage().getNotificationById( id );
  }

  public Notification deleteNotificationById( String id ) {
    return getNotificationStorage().deleteNotificationById( id );
  }

  public void markNotificationAsRead( String id ) {
    getNotificationStorage().markNotificationAsRead( id );
  }

  public AsyncEventBus getAsyncEventBus() {
    return PentahoSystem.get( AsyncEventBus.class, NotificationEngine.EVENT_BUS_ID, null );
  }

  public INotificationEvent getNotificationEvent( String notificationType ) {
    if ( !StringUtils.isEmpty( notificationType ) && getEventMap().containsKey( notificationType ) ) {
      return getEventMap().get( notificationType );
    }
    return null;
  }

  public void registerNotificationEvent( String eventType, INotificationEvent event ) {
    if ( !StringUtils.isEmpty( eventType ) && event != null ) {
      getEventMap().put( eventType, event );
    }
  }

  public void registerNotificationEventHandler( INotificationEventHandler eventHandler ) {
    if ( eventHandler != null && getAsyncEventBus() != null ) {
      getAsyncEventBus().register( eventHandler );
    }
  }

  protected Map<String, INotificationEvent> getEventMap() {
    return eventMap;
  }

  protected INotificationStorage getNotificationStorage() {
    return notificationStorage;
  }

  public void setNotificationStorage( INotificationStorage notificationStorage ) {
    this.notificationStorage = notificationStorage;
  }

  protected NotificationPollingQueue getNotificationPollingQueue() {
    return notificationPollingQueue;
  }

  protected void initializeAsyncEventBus() {

    AsyncEventBus asyncEventBus = new AsyncEventBus( Executors.newCachedThreadPool() );

    // register the bus with PentahoSystem
    PentahoSystem.registerReference(
      new SingletonPentahoObjectReference.Builder<AsyncEventBus>( AsyncEventBus.class ).object( asyncEventBus )
        .attributes(
          Collections.<String, Object>singletonMap( "id", NotificationEngine.EVENT_BUS_ID ) ).build(),
      AsyncEventBus.class );
  }
}
