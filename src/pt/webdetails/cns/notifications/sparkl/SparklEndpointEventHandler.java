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
import pt.webdetails.cns.utils.SessionUtils;

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

    final String COMMA = ",";

    if ( StringUtils.isEmpty( getKtrEndpoint() ) ) {
      logger.error( "ktrEndpoint is null" );
      return false;
    } else if ( event == null ) {
      logger.error( "event is null" );
      return false;
    }

    try {

      String commaSeparatedRecipientList = null;
      String eventSubType = null;

      switch( event.getRecipientType() ) {

        case ALL:
          commaSeparatedRecipientList = StringUtils.join( SessionUtils.getAllUsers(), COMMA );
          break;

        case ROLE:
          eventSubType = event.getRecipient();
          commaSeparatedRecipientList = StringUtils.join( SessionUtils.getUsersInRole( event.getRecipient() ), COMMA );
          break;

        default:
          /* USER */
          commaSeparatedRecipientList = event.getRecipient();
      }

      Response r = HttpConnectionHelper
        .invokeEndpoint( "cns", ktrEndpoint, "GET", toKtrParamMap( event, commaSeparatedRecipientList, eventSubType ) );

      return r != null && ( HttpStatus.SC_OK == r.getStatusCode() || HttpStatus.SC_NO_CONTENT == r.getStatusCode() );

    } catch ( Exception e ) {
      logger.error( e.getLocalizedMessage(), e );
      return false;
    }
  }


  private Map<String, String> toKtrParamMap( INotificationEvent e, String commaSeparatedRecipientList,
                                             String eventSubType ) {

    Map<String, String> map = new HashMap<String, String>();

    if ( e != null && !StringUtils.isEmpty( commaSeparatedRecipientList ) ) {

      try {

        map.put( SPARKL_PARAM_PREFIX + "rcpts", URLEncoder.encode( commaSeparatedRecipientList, ENCODING ) );

        if ( e.getRecipientType() != null ) {

          String type = e.getRecipientType().toString();

          if( !StringUtils.isEmpty( eventSubType ) ){
            type += " (" + eventSubType + ")";
          }

          map.put( SPARKL_PARAM_PREFIX + "eventtype", URLEncoder.encode( type, ENCODING ) );
        }
        if ( !StringUtils.isEmpty( e.getSender() ) ) {
          map.put( SPARKL_PARAM_PREFIX + "author", URLEncoder.encode( e.getSender(), ENCODING ) );
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
