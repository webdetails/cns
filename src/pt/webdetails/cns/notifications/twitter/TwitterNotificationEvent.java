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

package pt.webdetails.cns.notifications.twitter;

import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.notifications.BasicEventObject;

public class TwitterNotificationEvent extends BasicEventObject implements INotificationEvent {

  private static final long serialVersionUID = -2988726587969222435L;

  public void populate( String notificationType, String sender, String recipient,
                        INotificationEvent.RecipientType recipientType, String title, String message,
                        String link ) {

    super.populate( notificationType, sender, recipient, recipientType, title, message, link );

    setSender( "twitter.com" );
    setLink( "https://twitter.com" );
  }
}
