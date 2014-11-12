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
package pt.webdetails.cns.notifications.sparkl;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.api.INotificationEventHandler;
import pt.webdetails.cns.notifications.base.DefaultNotificationEvent;
import pt.webdetails.cns.notifications.twitter.TwitterNotificationEvent;

public class SparklEndpointEventHandler implements INotificationEventHandler {

  private Logger logger = LoggerFactory.getLogger( SparklEndpointEventHandler.class );
  private String sparklKtrEndpoint;

  public SparklEndpointEventHandler( String sparklKtrEndpoint ) {
    this.sparklKtrEndpoint = sparklKtrEndpoint;
  }

  @Subscribe
  public void handleDefaultEvent( DefaultNotificationEvent event ) {
    if ( event != null ) {
      sendToSparklKtrEndpoint( event );
    }
  }

  @Subscribe
  public void handleTwitterEvent( TwitterNotificationEvent event ) {
    if ( event != null ) {
      sendToSparklKtrEndpoint( event );
    }
  }


  private void sendToSparklKtrEndpoint( INotificationEvent event ) {

    // do something

  }

}
