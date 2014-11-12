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
