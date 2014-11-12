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
