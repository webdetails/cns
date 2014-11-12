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
package pt.webdetails.cns;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.api.INotificationEventHandler;
import pt.webdetails.cns.service.NotificationEngine;
import pt.webdetails.cns.service.store.INotificationStorage;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class NotificationRegistry {

  Map<String, INotificationEvent> eventObjectMap;
  List<INotificationEventHandler> eventHandlerList;
  INotificationStorage notificationStorage;
  private Logger logger = LoggerFactory.getLogger( NotificationRegistry.class );

  public NotificationRegistry( INotificationStorage notificationStorage, Map<String, INotificationEvent> eventObjectMap,
                               List<INotificationEventHandler> eventHandlerList ) {
    this.eventObjectMap = eventObjectMap;
    this.eventHandlerList = eventHandlerList;
    this.notificationStorage = notificationStorage;

    if ( getEngine() != null ) {
      registerObjects();
    }
  }

  protected void registerObjects() {

    if ( getEventObjectMap() != null && getEventObjectMap().size() > 0 ) {

      Set<String> eventTypes = getEventObjectMap().keySet();

      for ( String eventType : eventTypes ) {
        getEngine().registerNotificationEvent( eventType, getEventObjectMap().get( eventType ) );
      }
    }

    if ( getEventHandlerList() != null ) {

      for ( INotificationEventHandler eventHandler : getEventHandlerList() ) {
        getEngine().registerNotificationEventHandler( eventHandler );
      }
    }

    if ( getNotificationStorage() != null ) {
      getEngine().setNotificationStorage( getNotificationStorage() );
    }
  }

  public Map<String, INotificationEvent> getEventObjectMap() {
    return eventObjectMap;
  }

  public void setEventObjectMap( Map<String, INotificationEvent> eventObjectMap ) {
    this.eventObjectMap = eventObjectMap;
  }

  public List<INotificationEventHandler> getEventHandlerList() {
    return eventHandlerList;
  }

  public void setEventHandlerList( List<INotificationEventHandler> eventHandlerList ) {
    this.eventHandlerList = eventHandlerList;
  }

  public INotificationStorage getNotificationStorage() {
    return notificationStorage;
  }

  public void setNotificationStorage( INotificationStorage notificationStorage ) {
    this.notificationStorage = notificationStorage;
  }

  protected NotificationEngine getEngine() {
    return NotificationEngine.getInstance();
  }
}
