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
package pt.webdetails.cns.service.store;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.Constants;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.service.Notification;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotificationVolatileStorage implements INotificationStorage {

  final Predicate UNREAD_FILTER = new Predicate() {
    @Override public boolean evaluate( Object o ) {
      return o != null && o instanceof Notification && ( (Notification) o ).isUnread();
    }
  };

  private Logger logger = LoggerFactory.getLogger( NotificationVolatileStorage.class );
  private List<Notification> publicNotifications;
  private Map<String, List<Notification>> roleNotifications;
  private Map<String, List<Notification>> userNotifications;

  public NotificationVolatileStorage() {
    publicNotifications = new LinkedList<Notification>();
    roleNotifications = new HashMap<String, List<Notification>>();
    userNotifications = new HashMap<String, List<Notification>>();
  }

  public int getTotalCount( String user, String[] roles, boolean unreadOnly ) {

    int totalCount = 0;

    if ( StringUtils.isEmpty( user ) ) {
      return totalCount;

    } else if ( unreadOnly ) {

      totalCount = CollectionUtils.countMatches( publicNotifications, UNREAD_FILTER );
      totalCount += CollectionUtils.countMatches( userNotifications.get( user ), UNREAD_FILTER );

      if ( roles != null ) {

        for ( String role : roles ) {
          totalCount += CollectionUtils.countMatches( roleNotifications.get( role ), UNREAD_FILTER );
        }
      }

    } else {

      totalCount = publicNotifications != null ? publicNotifications.size() : 0;
      totalCount += userNotifications.get( user ) != null ? userNotifications.get( user ).size() : 0;

      if ( roles != null ) {

        for ( String role : roles ) {
          totalCount += roleNotifications.get( role ) != null ? roleNotifications.get( role ).size() : 0;
        }
      }
    }

    return totalCount;
  }

  public synchronized boolean store( INotificationEvent.RecipientType recipientType, Notification notification ) {

    if ( notification == null || recipientType == null ) {
      logger.error( "One of: 'notification' , 'recipientType' is null" );
      return false;

    } else if ( StringUtils.isEmpty( notification.getRecipient() ) ) {
      logger.error( "Null recipient for notification '" + notification.getMessage() + "'" );
      return false;
    }

    if ( INotificationEvent.RecipientType.ALL == recipientType ) {

      return storeInPublic( notification );

    } else if ( INotificationEvent.RecipientType.ROLE == recipientType ) {

      return storeInPrivate( roleNotifications, notification );

    } else if ( INotificationEvent.RecipientType.USER == recipientType ) {

      return storeInPrivate( userNotifications, notification );

    }

    // it should have never reached this point
    return false;
  }

  @SuppressWarnings( "unchecked" )
  public Notification getNextUnread( String user, String[] roles ) {

    if ( StringUtils.isEmpty( user ) ) {
      return null;

    } else {

      List<Notification> unreadList = new LinkedList<Notification>();

      List<Notification> publicList = (List) CollectionUtils.select( publicNotifications, UNREAD_FILTER );

      if ( publicList != null ) {
        unreadList.addAll( publicList );
      }

      List<Notification> userList = (List) CollectionUtils.select( userNotifications.get( user ), UNREAD_FILTER );

      if ( userList != null ) {
        unreadList.addAll( userList );
      }

      if ( roles != null ) {

        List<Notification> roleList = null;

        for ( String role : roles ) {

          roleList = (List) CollectionUtils.select( roleNotifications.get( role ), UNREAD_FILTER );

          if ( roleList != null ) {
            unreadList.addAll( roleList );
          }
        }
      }

      if ( unreadList.size() > 0 ) {

        Collections.sort( unreadList ); // recall: Notification overrides compareTo(), now sorting by timestamp
        return unreadList.get( 0 ); // the oldest unread entry ( read: lowest timestampMillis value )
      }

      return null;
    }
  }

  @SuppressWarnings( "unchecked" )
  public List<Notification> getAll( String user, String[] roles, boolean unreadOnly ) {

    if ( StringUtils.isEmpty( user ) ) {
      return null;

    } else {

      List<Notification> all = new LinkedList<Notification>();

      List<Notification> publicList =
        unreadOnly ? (List) CollectionUtils.select( publicNotifications, UNREAD_FILTER ) : publicNotifications;

      if ( publicList != null ) {
        all.addAll( publicList );
      }

      List<Notification> userList = unreadOnly ?
        (List) CollectionUtils.select( userNotifications.get( user ), UNREAD_FILTER ) : userNotifications.get( user );

      if ( userList != null ) {
        all.addAll( userList );
      }

      if ( roles != null ) {

        List<Notification> roleList = null;

        for ( String role : roles ) {

          roleList = userList = unreadOnly ?
            (List) CollectionUtils.select( roleNotifications.get( role ), UNREAD_FILTER ) :
            roleNotifications.get( role );

          if ( roleList != null ) {
            all.addAll( roleList );
          }
        }
      }

      if ( all.size() > 0 ) {

        Collections.sort( all ); // recall: Notification overrides compareTo(), now sorting by timestamp
      }

      return all;
    }
  }

  public Notification getNotificationById( String id ) {

    if ( StringUtils.isEmpty( id ) ) {
      return null;
    }

    Notification n = new Notification( id );

    if ( publicNotifications.contains( n ) ) {
      return publicNotifications.get( publicNotifications.indexOf( n ) );

    } else {

      Set<String> roles = roleNotifications.keySet();
      for ( String role : roles ) {
        if ( roleNotifications.get( role ).contains( n ) ) {
          return roleNotifications.get( role ).get( roleNotifications.get( role ).indexOf( n ) );
        }
      }


      Set<String> users = userNotifications.keySet();
      for ( String user : users ) {
        if ( roleNotifications.get( user ).contains( n ) ) {
          return roleNotifications.get( user ).get( roleNotifications.get( user ).indexOf( n ) );
        }
      }
    }

    return null;
  }

  public Notification deleteNotificationById( String id ) {

    if ( StringUtils.isEmpty( id ) ) {
      return null;
    }

    if ( containsNotification( publicNotifications, id ) ) {

      return publicNotifications.get( publicNotifications.indexOf( new Notification( id ) ) );

    } else if ( containsNotification( roleNotifications, id ) ) {

      String role = getKey( roleNotifications, id );
      int idx = roleNotifications.get( role ).indexOf( new Notification( id ) );
      return roleNotifications.get( role ).get( idx );

    } else if ( containsNotification( userNotifications, id ) ) {

      String user = getKey( userNotifications, id );
      int idx = userNotifications.get( user ).indexOf( new Notification( id ) );
      return userNotifications.get( user ).get( idx );

    }

    return null;
  }

  public void markNotificationAsRead( String id ) {

    if ( StringUtils.isEmpty( id ) ) {
      return;
    }

    if ( containsNotification( publicNotifications, id ) ) {

      publicNotifications.get( publicNotifications.indexOf( new Notification( id ) ) ).setUnread( false );

    } else if ( containsNotification( roleNotifications, id ) ) {

      String role = getKey( roleNotifications, id );
      int idx = roleNotifications.get( role ).indexOf( new Notification( id ) );
      roleNotifications.get( role ).get( idx ).setUnread( false );

    } else if ( containsNotification( userNotifications, id ) ) {

      String user = getKey( userNotifications, id );
      int idx = userNotifications.get( user ).indexOf( new Notification( id ) );
      userNotifications.get( user ).get( idx ).setUnread( false );

    }
  }

  private boolean containsNotification( Map<String, List<Notification>> notificationMap, String notificationId ) {

    if ( StringUtils.isEmpty( notificationId ) || notificationMap == null || notificationMap.size() == 0 ) {
      return false;
    }

    Notification n = new Notification( notificationId );
    Set<String> keys = notificationMap.keySet();

    for ( String key : keys ) {
      if ( notificationMap.get( key ).contains( n ) ) {
        return true;
      }
    }

    return false;
  }

  private boolean containsNotification( List<Notification> list, String notificationId ) {

    if ( StringUtils.isEmpty( notificationId ) ) {
      return false;
    }

    return list != null && list.contains( new Notification( notificationId ) );
  }

  private String getKey( Map<String, List<Notification>> notificationMap, String notificationId ) {

    if ( StringUtils.isEmpty( notificationId ) || notificationMap == null || notificationMap.size() == 0 ) {
      return null;
    }

    Notification n = new Notification( notificationId );
    Set<String> keys = notificationMap.keySet();

    for ( String key : keys ) {
      if ( notificationMap.get( key ).contains( n ) ) {
        return key;
      }
    }

    return null;
  }

  private synchronized boolean storeInPublic( Notification notification ) {

    while ( publicNotifications.size() > Constants.DEFAULT_MAX_LIST_SIZE ) {
      logger.warn( "there are too many public notifications, deleting older ones" );
      publicNotifications.remove( 0 ); // discard oldest one
    }

    publicNotifications.add( notification );
    Collections.sort( publicNotifications ); // recall: Notification overrides compareTo(), now sorting by timestamp
    return true;
  }

  private synchronized boolean storeInPrivate( Map<String, List<Notification>> list, Notification notification ) {

    if ( StringUtils.isEmpty( notification.getRecipient() ) ) {
      return false;
    }

    if ( !list.containsKey( notification.getRecipient() ) ) {
      // this recipient does not have a notification list yet
      list.put( notification.getRecipient(), new LinkedList<Notification>() );

    } else {

      while ( list.get( notification.getRecipient() ).size() > Constants.DEFAULT_MAX_LIST_SIZE ) {
        logger
          .warn( "recipient '" + notification.getRecipient() + "' has too many notifications, deleting older ones" );
        list.get( notification.getRecipient() ).remove( 0 ); // discard oldest one
      }
    }

    list.get( notification.getRecipient() ).add( notification );

    // recall: Notification overrides compareTo(), now sorting by timestamp
    Collections.sort( list.get( notification.getRecipient() ) );

    return true;
  }
}


