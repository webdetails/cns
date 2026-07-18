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

import java.io.Serializable;

public interface INotificationEvent extends Serializable {

  /**
   * The origin of the notification. This will define the course of this notification's event handling.
   * <p/>
   * Some notification type examples: 'pentaho', 'mail', 'twitter'.
   * <p/>
   * An empty value will be handled by default type 'pentaho'.
   * <p/>
   *
   * @return origin
   */
  String getNotificationType();

  /**
   * The user sending this notification.
   * <p/>
   *
   * @return the user sending this notification
   */
  String getSender();

  /**
   * The recipient of this notification.
   * <p/>
   *
   * @return the recipient of this notification.
   */
  String getRecipient();

  /**
   * The recipient type of this notification.
   * <p/>
   * One of
   * <p/>
   * RecipientType.ALL - this notification is intended for all users.
   * <p/>
   * RecipientType.USER - this notification is intended for a specific user.
   * <p/>
   * RecipientType.USER - this notification is intended for all users of specific role.
   * <p/>
   *
   * @return the recipient type of this notification.
   */
  RecipientType getRecipientType();

  /**
   * The title of this notification.
   * <p/>
   *
   * @return The title of this notification.
   */
  String getTitle();

  /**
   * The notification message.
   * <p/>
   *
   * @return the notification message.
   */
  String getMessage();

  /**
   * A URL to be added to the notification.
   * <p/>
   *
   * @return a URL to be added to the notification.
   */
  String getLink();

  /**
   * populates this INotificationEvent instance
   *
   * @param notificationType
   * @param sender
   * @param recipient
   * @param recipientType
   * @param title
   * @param message
   * @param link
   * @return this INotificationEvent instance
   */
  void populate( String notificationType, String sender, String recipient,
                 INotificationEvent.RecipientType recipientType, String title, String message,
                 String link );

  /**
   * The recipient type of this notification.
   * <p/>
   * One of
   * <p/>
   * RecipientType.ALL - this notification is intended for all users.
   * <p/>
   * RecipientType.USER - this notification is intended for a specific user.
   * <p/>
   * RecipientType.USER - this notification is intended for all users of specific role.
   */
  static enum RecipientType {
    ALL, USER, ROLE
  }
}
