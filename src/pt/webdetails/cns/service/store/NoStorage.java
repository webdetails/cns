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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.service.Notification;

import java.util.List;

public class NoStorage implements pt.webdetails.cns.api.INotificationStorage {

  private Logger logger = LoggerFactory.getLogger( NoStorage.class );

  public NoStorage() {
  }

  public int getTotalCount( String user, String[] roles, boolean unreadOnly ) {
    return 0;
  }

  public synchronized boolean store( INotificationEvent.RecipientType recipientType, Notification notification ) {
    logger.trace( "this class stores nothing" );
    return true;
  }

  @SuppressWarnings( "unchecked" )
  public Notification getNextUnread( String user, String[] roles ) {
    logger.trace( "this class stores nothing" );
    return null;
  }

  @SuppressWarnings( "unchecked" )
  public List<Notification> getAll( String user, String[] roles, boolean unreadOnly ) {
    logger.trace( "this class stores nothing" );
    return null;
  }

  public Notification getNotificationById( String id ) {
    logger.trace( "this class stores nothing" );
    return null;
  }

  public boolean deleteNotificationById( String id ) {
    logger.trace( "this class stores nothing" );
    return true;
  }

  public void markNotificationAsRead( String id ) {
    logger.trace( "this class stores nothing" );
  }
}


