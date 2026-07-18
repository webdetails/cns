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

import java.util.List;

public interface INotificationStorage {

  int getTotalCount( String user, String[] roles, boolean unreadOnly );

  Notification getNextUnread( String user, String[] roles );

  List<Notification> getAll( String user, String[] roles, boolean unreadOnly );

  Notification getNotificationById( String id );

  boolean store( INotificationEvent.RecipientType recipientType, Notification notification );

  boolean deleteNotificationById( String id );

  void markNotificationAsRead( String id );

}
