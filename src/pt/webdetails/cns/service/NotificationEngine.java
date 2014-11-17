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
import pt.webdetails.cns.api.INotificationEnvironment;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.api.INotificationEventHandler;
import pt.webdetails.cns.utils.SessionUtils;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

public class NotificationEngine {

  private static NotificationEngine instance;
  private Logger logger = LoggerFactory.getLogger( NotificationEngine.class );
  private INotificationEnvironment environment;

  private NotificationEngine() {

    CoreBeanFactory factory = new CoreBeanFactory( Constants.PLUGIN_NAME );

    this.environment = (INotificationEnvironment) factory.getBean( INotificationEnvironment.class.getSimpleName() );

    if ( environment == null ) {
      logger.error( "INotificationEnvironment has not been set; NotificationEngine will not function properly" );
    }

    if ( getAsyncEventBus() == null ) {
      initializeAsyncEventBus();
    }

    if ( getEnvironment().getEventHandlers() != null ) {

      for ( INotificationEventHandler eventHandler : getEnvironment().getEventHandlers() ) {
        registerEventHandler( eventHandler );
      }
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
      logger.error( "INotificationEvent cannot be null" );
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

  public boolean subscribeToPoll( String user, String eventType ) {
    return getEnvironment().getPoll().subscribe( user, eventType );
  }

  public boolean pushToPoll( INotificationEvent event ) {

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

        return getEnvironment().getPoll().pushToAll( notification );

      } else if ( INotificationEvent.RecipientType.USER == notification.getRecipientType() ) {

        if ( SessionUtils.userExists( notification.getRecipient() ) ) {
          return getEnvironment().getPoll().pushToUser( notification.getRecipient(), notification );
        } else {
          logger.error( "notification.getRecipient(): '" + notification.getRecipient() + "' is not a valid user" );
        }

      } else if ( INotificationEvent.RecipientType.ROLE == notification.getRecipientType() ) {

        if ( SessionUtils.roleExists( notification.getRecipient() ) ) {
          return getEnvironment().getPoll().pushToRole( notification.getRecipient(), notification );
        } else {
          logger.error( "notification.getRecipient(): '" + notification.getRecipient() + "' is not a valid role" );
        }
      }
    }

    return false;
  }

  public Notification popFromPoll( String user ) {

    while ( getEnvironment().getPoll().isPollEmpty( user ) ) {

      try {
        logger.trace( "no notifications in queue; we'll check again in ~ 3 secs" );
        Thread.sleep( 3000 ); // sleep for 3 secs
      } catch ( InterruptedException e ) {
        // do nothing
      }
    }

    return getEnvironment().getPoll().pop( user );
  }

  public boolean putInStorage( INotificationEvent event ) {

    Notification notification = new Notification( event );

    if ( notification != null ) {

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
      if ( INotificationEvent.RecipientType.ALL != notification.getRecipientType() ) {

        if ( INotificationEvent.RecipientType.USER == notification.getRecipientType()
          && !SessionUtils.userExists( notification.getRecipient() ) ) {
          logger.error( "notification.getRecipient(): '" + notification.getRecipient() + "' is not a valid user" );
          return false;

        } else if ( INotificationEvent.RecipientType.ROLE == notification.getRecipientType()
          && !SessionUtils.roleExists( notification.getRecipient() ) ) {
          logger.error( "notification.getRecipient(): '" + notification.getRecipient() + "' is not a valid role" );
          return false;
        }
      }

      // call INotificationStorage.store()
      getEnvironment().getStorage().store( notification.getRecipientType(), notification );

    }
    return false;
  }

  public int getTotalCount( String user, String[] roles, boolean unreadOnly ) {
    return getEnvironment().getStorage().getTotalCount( user, roles, unreadOnly );
  }

  public Notification getNextUnread( String user, String[] roles ) {
    return getEnvironment().getStorage().getNextUnread( user, roles );
  }

  public List<Notification> getAll( String user, String[] roles, boolean unreadOnly ) {
    return getEnvironment().getStorage().getAll( user, roles, unreadOnly );
  }

  public Notification getNotificationById( String id ) {
    return getEnvironment().getStorage().getNotificationById( id );
  }

  public boolean deleteNotificationById( String id ) {
    return getEnvironment().getStorage().deleteNotificationById( id );
  }

  public void markNotificationAsRead( String id ) {
    getEnvironment().getStorage().markNotificationAsRead( id );
  }

  public INotificationEvent getNotificationEvent( String notificationType ) {
    if ( !StringUtils.isEmpty( notificationType ) && getEnvironment().getEventObjects() != null
      && getEnvironment().getEventObjects().containsKey( notificationType ) ) {
      return getEnvironment().getEventObjects().get( notificationType );
    }
    return null;
  }

  public void registerEventHandler( INotificationEventHandler eventHandler ) {
    if ( eventHandler != null && getAsyncEventBus() != null ) {
      getAsyncEventBus().register( eventHandler );
    }
  }

  protected INotificationEnvironment getEnvironment() {
    return environment;
  }

  protected void initializeAsyncEventBus() {

    AsyncEventBus asyncEventBus = new AsyncEventBus( Executors.newCachedThreadPool() );

    // register the bus with PentahoSystem
    PentahoSystem.registerReference(
      new SingletonPentahoObjectReference.Builder<AsyncEventBus>( AsyncEventBus.class ).object( asyncEventBus )
        .attributes( Collections.<String, Object>singletonMap( "id", Constants.EVENT_BUS_ID ) ).build(),
      AsyncEventBus.class );
  }

  public AsyncEventBus getAsyncEventBus() {
    return PentahoSystem.get( AsyncEventBus.class, Constants.EVENT_BUS_ID, null );
  }
}
