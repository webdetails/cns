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

package pt.webdetails.cns.service.store;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.service.Notification;

import java.util.List;

public class NoStorage implements pt.webdetails.cns.api.INotificationStorage {

  private Logger logger = LoggerFactory.getLogger( NoStorage.class );

  public NoStorage() {
  }

  public int getTotalCount( String user, String[] roles, boolean unreadOnly ) {
    return 0;
  }

  public synchronized boolean store( INotificationEvent.RecipientType recipientType, Notification notification ) {
    logger.trace( "this class stores nothing" );
    return true;
  }

  @SuppressWarnings( "unchecked" )
  public Notification getNextUnread( String user, String[] roles ) {
    logger.trace( "this class stores nothing" );
    return null;
  }

  @SuppressWarnings( "unchecked" )
  public List<Notification> getAll( String user, String[] roles, boolean unreadOnly ) {
    logger.trace( "this class stores nothing" );
    return null;
  }

  public Notification getNotificationById( String id ) {
    logger.trace( "this class stores nothing" );
    return null;
  }

  public boolean deleteNotificationById( String id ) {
    logger.trace( "this class stores nothing" );
    return true;
  }

  public void markNotificationAsRead( String id ) {
    logger.trace( "this class stores nothing" );
  }
}


