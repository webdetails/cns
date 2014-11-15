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
package pt.webdetails.cns.utils;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import pt.webdetails.cns.Constants;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.service.Notification;

import java.util.HashMap;
import java.util.Map;

public class Utils {

  public static JSONObject toJsonObject( Notification n ) throws JSONException {

    JSONObject jsonObj = new JSONObject();

    if ( n != null ) {

      if ( !StringUtils.isEmpty( n.getId() ) ) {
        jsonObj.put( Constants.PARAM_ID, n.getId() );
      }
      if ( !StringUtils.isEmpty( n.getNotificationType() ) ) {
        jsonObj.put( Constants.PARAM_NOTIFICATION_TYPE, n.getNotificationType() );
      }
      if ( !StringUtils.isEmpty( n.getSender() ) ) {
        jsonObj.put( Constants.PARAM_SENDER, n.getSender() );
      }
      if ( !StringUtils.isEmpty( n.getRecipient() ) ) {
        jsonObj.put( Constants.PARAM_RECIPIENT, n.getRecipient() );
      }
      if ( !StringUtils.isEmpty( n.getTitle() ) ) {
        jsonObj.put( Constants.PARAM_TITLE, n.getTitle() );
      }
      if ( !StringUtils.isEmpty( n.getMessage() ) ) {
        jsonObj.put( Constants.PARAM_MESSAGE, n.getMessage() );
      }
      if ( !StringUtils.isEmpty( n.getLink() ) ) {
        jsonObj.put( Constants.PARAM_LINK, n.getLink() );
      }
      if ( n.getTimestampInMillis() > 0 ) {
        jsonObj.put( Constants.PARAM_TIMESTAMP_IN_MILLIS, n.getTimestampInMillis() );
      }
      if ( n.getNotificationType() != null ) {
        jsonObj.put( Constants.PARAM_RECIPIENT_TYPE, n.getRecipientType().toString() );
      }
      jsonObj.put( Constants.PARAM_UNREAD, n.isUnread() );
    }

    return jsonObj;
  }

  public static JSONObject toJsonObject( INotificationEvent e ) throws JSONException {

    if ( e == null ) {
      return new JSONObject();
    }

    Notification n = new Notification( e );
    n.setId( null ); // INotificationEvents don't have id's

    return toJsonObject( n );
  }

  public static Map<String, String> toParamMap( Notification n ) {

    Map<String, String> map = new HashMap<String, String>();

    if ( n != null ) {

      if ( !StringUtils.isEmpty( n.getId() ) ) {
        map.put( Constants.PARAM_ID, n.getId() );
      }
      if ( !StringUtils.isEmpty( n.getNotificationType() ) ) {
        map.put( Constants.PARAM_NOTIFICATION_TYPE, n.getNotificationType() );
      }
      if ( !StringUtils.isEmpty( n.getSender() ) ) {
        map.put( Constants.PARAM_SENDER, n.getSender() );
      }
      if ( !StringUtils.isEmpty( n.getRecipient() ) ) {
        map.put( Constants.PARAM_RECIPIENT, n.getRecipient() );
      }
      if ( !StringUtils.isEmpty( n.getTitle() ) ) {
        map.put( Constants.PARAM_TITLE, n.getTitle() );
      }
      if ( !StringUtils.isEmpty( n.getMessage() ) ) {
        map.put( Constants.PARAM_MESSAGE, n.getMessage() );
      }
      if ( !StringUtils.isEmpty( n.getLink() ) ) {
        map.put( Constants.PARAM_LINK, n.getLink() );
      }
      if ( n.getTimestampInMillis() > 0 ) {
        map.put( Constants.PARAM_TIMESTAMP_IN_MILLIS, String.valueOf( n.getTimestampInMillis() ) );
      }
      if ( n.getNotificationType() != null ) {
        map.put( Constants.PARAM_RECIPIENT_TYPE, n.getRecipientType().toString() );
      }
    }

    return map;
  }

  public static Map<String, String> toParamMap( INotificationEvent e ) {

    if ( e == null ) {
      return new HashMap<String, String>();
    }

    Notification n = new Notification( e );
    n.setId( null ); // INotificationEvents don't have id's

    return toParamMap( n );
  }
}
