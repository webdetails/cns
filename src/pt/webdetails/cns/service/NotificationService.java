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
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.notifications.Notification;
import pt.webdetails.cns.NotificationInitializer;

import java.util.LinkedList;
import java.util.Queue;

public class NotificationService {

  public static final String EVENT_BUS_ID = "notificationEventBus";
  private static NotificationService instance;
  private Logger logger = LoggerFactory.getLogger( NotificationService.class );
  private Queue<Notification> notifications;

  private NotificationService() {
    notifications = new LinkedList<Notification>();

    if( getAsyncEventBus() == null ){
      new NotificationInitializer( null ).initializeAsyncEventBus();
    }
  }

  public static NotificationService getInstance() {

    if ( instance == null ) {
      instance = new NotificationService();
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

  public void push( Notification notification ) {
    if ( notification != null ) {
      notifications.add( notification );
    }
  }

  public Notification pop() {
    while ( notifications.isEmpty() ) {

      try {
        logger.debug( "no notifications in queue; we'll check again in ~ 3 secs" );
        Thread.sleep( 3000 ); // sleep for 3 secs
      } catch ( InterruptedException e ) {
        // do nothing
      }
    }

    return notifications.remove();
  }


  protected AsyncEventBus getAsyncEventBus() {
    return PentahoSystem.get( AsyncEventBus.class, NotificationService.EVENT_BUS_ID, null );
  }

}
