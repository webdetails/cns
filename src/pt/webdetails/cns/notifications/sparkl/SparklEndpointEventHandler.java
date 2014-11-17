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
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.notifications.base.AbstractNotificationPoolingEventHandler;
import pt.webdetails.cns.notifications.sparkl.kettle.baserver.web.utils.HttpConnectionHelper;
import pt.webdetails.cns.notifications.sparkl.kettle.baserver.web.utils.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SparklEndpointEventHandler extends AbstractNotificationPoolingEventHandler {

  private static final String ENCODING = "UTF-8";

  private static final String SPARKL_PARAM_PREFIX = "param";

  private Logger logger = LoggerFactory.getLogger( SparklEndpointEventHandler.class );
  private String ktrEndpoint;

  public SparklEndpointEventHandler( String ktrEndpoint ) {

    if ( StringUtils.isEmpty( ktrEndpoint ) ) {
      logger.error( "ktrEndpoint is null! no event dispatching will be made" );
    }

    this.ktrEndpoint = ktrEndpoint.startsWith( "/" ) ? ktrEndpoint : "/" + ktrEndpoint;
  }

  @Subscribe
  public void handleAllEvents( INotificationEvent event ) {
    if ( sendToKtrEndpoint( event ) ) {
      // send to poll is notification storing was successful
      super.doEventHandling( event );
    }
  }

/*

  @Subscribe
  public void handleDefaultEvent( DefaultNotificationEvent event ) {
    if ( sendToKtrEndpoint( event ) ) {
      // send to poll is notification storing was successful
      super.doEventHandling( event );
    }
  }

  @Subscribe
  public void handleTwitterEvent( TwitterNotificationEvent event ) {
    if ( sendToKtrEndpoint( event ) ) {
      // send to poll is notification storing was successful
      super.doEventHandling( event );
    }
  }

*/

  public String getKtrEndpoint() {
    return ktrEndpoint;
  }

  public void setKtrEndpoint( String ktrEndpoint ) {
    this.ktrEndpoint = ktrEndpoint;
  }

  protected boolean sendToKtrEndpoint( INotificationEvent event ) {

    if ( StringUtils.isEmpty( getKtrEndpoint() ) ) {
      logger.error( "ktrEndpoint is null" );
      return false;
    } else if ( event == null ) {
      logger.error( "event is null" );
      return false;
    }

    try {

      Response r = HttpConnectionHelper.invokeEndpoint( "cns", ktrEndpoint, "GET", toKtrParamMap( event ) );

      return r != null && ( HttpStatus.SC_OK == r.getStatusCode() || HttpStatus.SC_NO_CONTENT == r.getStatusCode() );

    } catch ( Exception e ) {
      logger.error( e.getLocalizedMessage(), e );
      return false;
    }
  }

  private Map<String, String> toKtrParamMap( INotificationEvent e ) {

    Map<String, String> map = new HashMap<String, String>();

    if ( e != null ) {

      try {

        if ( e.getRecipientType() != null ) {
          map.put( SPARKL_PARAM_PREFIX + "eventtype", URLEncoder.encode( e.getRecipientType().toString(), ENCODING ) );
        }
        if ( !StringUtils.isEmpty( e.getSender() ) ) {
          map.put( SPARKL_PARAM_PREFIX + "author", URLEncoder.encode( e.getSender(), ENCODING ) );
        }
        if ( !StringUtils.isEmpty( e.getRecipient() ) ) {
          map.put( SPARKL_PARAM_PREFIX + "rcpt", URLEncoder.encode( e.getRecipient(), ENCODING ) );
        }
        if ( !StringUtils.isEmpty( e.getTitle() ) ) {
          map.put( SPARKL_PARAM_PREFIX + "title", URLEncoder.encode( e.getTitle(), ENCODING ) );
        }
        if ( !StringUtils.isEmpty( e.getMessage() ) ) {
          map.put( SPARKL_PARAM_PREFIX + "message", URLEncoder.encode( e.getMessage(), ENCODING ) );
        }
        if ( !StringUtils.isEmpty( e.getLink() ) ) {
          map.put( SPARKL_PARAM_PREFIX + "link", URLEncoder.encode( e.getLink(), ENCODING ) );
        }
        if ( !StringUtils.isEmpty( e.getNotificationType() ) ) {
          map.put( SPARKL_PARAM_PREFIX + "style", URLEncoder.encode( e.getNotificationType(), ENCODING ) );
        }

      } catch ( UnsupportedEncodingException ex ) {
        logger.error( ex.getLocalizedMessage(), ex );
      }
    }

    return map;
  }
}
