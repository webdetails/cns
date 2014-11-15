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
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.api.INotificationEventHandler;
import pt.webdetails.cns.notifications.base.DefaultNotificationEvent;
import pt.webdetails.cns.notifications.sparkl.kettle.baserver.web.utils.HttpConnectionHelper;
import pt.webdetails.cns.notifications.twitter.TwitterNotificationEvent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SparklEndpointEventHandler implements INotificationEventHandler {

  private Logger logger = LoggerFactory.getLogger( SparklEndpointEventHandler.class );
  private String ktrEndpoint;

  public SparklEndpointEventHandler( String ktrEndpoint ) {
    this.ktrEndpoint = ktrEndpoint;
  }

  @Subscribe
  public void handleDefaultEvent( DefaultNotificationEvent event ) {
    if ( event != null ) {
      sendToKtrEndpoint( event );
    }
  }

  @Subscribe
  public void handleTwitterEvent( TwitterNotificationEvent event ) {
    if ( event != null ) {
      sendToKtrEndpoint( event );
    }
  }

  public String getKtrEndpoint() {
    return ktrEndpoint;
  }

  public void setKtrEndpoint( String ktrEndpoint ) {
    this.ktrEndpoint = ktrEndpoint;
  }

  protected void sendToKtrEndpoint( INotificationEvent event ) {

    if ( !StringUtils.isEmpty( ktrEndpoint ) && event != null ) {
      HttpConnectionHelper.invokeEndpoint( "cns", ktrEndpoint, "GET", toKtrParamMap( event ) );
    }
  }


  /**
   * DDL
   * <p/>
   * id INT PRIMARY KEY AUTO_INCREMENT <p/>
   * eventtype VARCHAR(64) NOT NULL <p/>
   * author VARCHAR(1024) NOT NULL <p/>
   * rcpt VARCHAR <p/>
   * title VARCHAR(2048) <p/>
   * message VARCHAR <p/>
   * style VARCHAR(64) NOT NULL <p/>
   * link VARCHAR <p/>
   */

  private Map<String, String> toKtrParamMap( INotificationEvent e ) {

    Map<String, String> map = new HashMap<String, String>();

    if ( e != null ) {

      if ( e.getRecipientType() != null ) {
        map.put( "eventtype", e.getRecipientType().toString() );
      }
      if ( !StringUtils.isEmpty( e.getSender() ) ) {
        map.put( "author", e.getSender() );
      }
      if ( !StringUtils.isEmpty( e.getRecipient() ) ) {
        map.put( "rcpt", e.getRecipient() );
      }
      if ( !StringUtils.isEmpty( e.getTitle() ) ) {
        map.put( "title", e.getTitle() );
      }
      if ( !StringUtils.isEmpty( e.getMessage() ) ) {
        map.put( "message", e.getMessage() );
      }
      if ( !StringUtils.isEmpty( e.getLink() ) ) {
        map.put( "link", e.getLink() );
      }
      if ( !StringUtils.isEmpty( e.getNotificationType() ) ) {
        map.put( "style", e.getNotificationType() );
      }
    }

    return map;
  }
}
