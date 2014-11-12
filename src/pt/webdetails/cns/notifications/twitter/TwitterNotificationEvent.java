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
