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

package pt.webdetails.cns.notifications.base;

import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.service.NotificationEngine;

public class DefaultNotificationEventHandler extends AbstractNotificationPoolingEventHandler {

  private Logger logger = LoggerFactory.getLogger( DefaultNotificationEventHandler.class );

  @Subscribe
  public void handleEvent( DefaultNotificationEvent event ) {
    super.doEventHandling( event );

    if ( event != null ) {

      try {
        NotificationEngine.getInstance().putInStorage( event );
      } catch ( Exception e ) {
        logger.error( e.getLocalizedMessage(), e );
      }
    }
  }
}
