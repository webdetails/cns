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

package pt.webdetails.cns.notifications;

import pt.webdetails.cns.api.INotificationEvent;

public class BasicEventObject {

  private String notificationType;
  private String sender;
  private String recipient;
  private INotificationEvent.RecipientType recipientType;
  private String title;
  private String message;
  private String link;

  public BasicEventObject() {
  }

  public String getNotificationType() {
    return notificationType;
  }

  public void setNotificationType( String notificationType ) {
    this.notificationType = notificationType;
  }

  public String getSender() {
    return sender;
  }

  public void setSender( String sender ) {
    this.sender = sender;
  }

  public String getRecipient() {
    return recipient;
  }

  public void setRecipient( String recipient ) {
    this.recipient = recipient;
  }

  public INotificationEvent.RecipientType getRecipientType() {
    return recipientType;
  }

  public void setRecipientType( INotificationEvent.RecipientType recipientType ) {
    this.recipientType = recipientType;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle( String title ) {
    this.title = title;
  }

  public String getMessage() {
    return message;
  }

  public void setMessage( String message ) {
    this.message = message;
  }

  public String getLink() {
    return link;
  }

  public void setLink( String link ) {
    this.link = link;
  }

  public void populate( String notificationType, String sender, String recipient,
                        INotificationEvent.RecipientType recipientType, String title, String message,
                        String link ) {

    setNotificationType( notificationType );
    setSender( sender );
    setRecipient( recipient );
    setRecipientType( recipientType );
    setTitle( title );
    setMessage( message );
    setLink( link );
  }
}
