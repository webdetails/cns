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

package pt.webdetails.cns.api;

import pt.webdetails.cns.service.Notification;

public interface INotificationPoll {

  boolean isPollEmpty( String user );

  boolean subscribe( String user, String eventType );

  boolean subscribeAll( String user );

  boolean pushToUser( String user, Notification notification );

  boolean pushToRole( String role, Notification notification );

  boolean pushToAll( Notification notification );

  Notification pop( String user );

  void clearQueue( String user );

  void clearAllQueues();

}
