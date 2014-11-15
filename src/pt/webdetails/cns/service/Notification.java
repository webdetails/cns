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
package pt.webdetails.cns.service;

import pt.webdetails.cns.api.INotificationEvent;

import java.io.Serializable;
import java.util.UUID;

public class Notification implements Comparable<Notification>, Serializable {

  private static final long serialVersionUID = -1517468225872804936L;

  private String id;
  private String notificationType;
  private String sender;
  private String recipient;
  private INotificationEvent.RecipientType recipientType;
  private String title;
  private String message;
  private long timestampInMillis;
  private boolean unread;
  private String link;

  public Notification( String id ) {
    setId( id );
  }

  public Notification( INotificationEvent e ) {
    this( e.getNotificationType(), e.getSender(), e.getRecipient(), e.getRecipientType(), e.getTitle(), e.getMessage(),
      e.getLink() );
  }

  public Notification( String notificationType, String sender, String recipient,
                       INotificationEvent.RecipientType recipientType, String title, String message, String link ) {

    setId( UUID.randomUUID().toString() );

    this.notificationType = notificationType;
    this.sender = sender;
    this.recipient = recipient;
    this.recipientType = recipientType;
    this.title = title;
    this.message = message;
    this.link = link;
  }

  public String getId() {
    return id;
  }

  public void setId( String id ) {
    this.id = id;
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

  public long getTimestampInMillis() {
    return timestampInMillis;
  }

  public void setTimestampInMillis( long timestampInMillis ) {
    this.timestampInMillis = timestampInMillis;
  }

  public boolean isUnread() {
    return unread;
  }

  public void setUnread( boolean unread ) {
    this.unread = unread;
  }

  public String getLink() {
    return link;
  }

  public void setLink( String link ) {
    this.link = link;
  }

  @Override public int compareTo( Notification o ) {

    long otherTimestampInMillis = o != null ? o.timestampInMillis : 0;
    return new Long( this.timestampInMillis - otherTimestampInMillis ).intValue();
  }

  @Override
  public boolean equals( Object o ) {
    return ( o != null && o instanceof Notification ) ? this.getId().equals( ( (Notification) o ).getId() ) : false;
  }

  @Override
  public int hashCode() {
    return this.id.hashCode();
  }
}
