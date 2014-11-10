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
package pt.webdetails.cns.notifications.simple;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.api.NotificationQueueApi;
import pt.webdetails.cns.notifications.Notification;
import pt.webdetails.cns.service.INotificationEventHandler;

public class SimpleNotificationEventHandler implements INotificationEventHandler {

  private Logger logger = LoggerFactory.getLogger( SimpleNotificationEventHandler.class );

  @Subscribe
  public void handleSimpleNotificationEvent( SimpleNotificationEvent event ) {

    if ( event != null ) {
      try {
        NotificationQueueApi.push( (Notification) event );
      } catch ( Exception e ) {
        logger.error( e.getLocalizedMessage(), e );
      }
    }

  }

}
