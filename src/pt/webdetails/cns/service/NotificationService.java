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

public class NotificationService {

  public static final String EVENT_BUS_ID = "notificationEventBus";
  private Logger logger = LoggerFactory.getLogger( NotificationService.class );

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


  protected AsyncEventBus getAsyncEventBus() {
    return PentahoSystem.get( AsyncEventBus.class, NotificationService.EVENT_BUS_ID, null );
  }

}
