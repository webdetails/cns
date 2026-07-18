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

package pt.webdetails.cns;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.api.INotificationEnvironment;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.api.INotificationEventHandler;
import pt.webdetails.cns.api.INotificationPoll;
import pt.webdetails.cns.api.INotificationStorage;

import java.util.List;
import java.util.Map;

public class DefaultNotificationEnvironment implements INotificationEnvironment {

  INotificationStorage storage;
  INotificationPoll poll;

  Map<String, INotificationEvent> eventObjects;
  List<INotificationEventHandler> eventHandlers;

  private Logger logger = LoggerFactory.getLogger( DefaultNotificationEnvironment.class );

  public DefaultNotificationEnvironment( INotificationStorage storage,
                                         INotificationPoll poll,
                                         Map<String, INotificationEvent> eventObjects,
                                         List<INotificationEventHandler> eventHandlers ) throws Exception {

    if ( storage == null ) {
      throw new Exception( "INotificationStorage cannot be null" );
    } else if ( poll == null ) {
      throw new Exception( "INotificationPoll cannot be null" );
    } else if ( eventObjects == null ) {
      throw new Exception( "eventObjects cannot be null" );
    } else if ( eventHandlers == null ) {
      throw new Exception( "eventHandlers cannot be null" );
    }

    setEventObjects( eventObjects );
    setEventHandlers( eventHandlers );
    setStorage( storage );
    setPolling( poll );
  }

  @Override
  public INotificationStorage getStorage() {
    return storage;
  }

  @Override
  public INotificationPoll getPoll() {
    return poll;
  }

  @Override
  public Map<String, INotificationEvent> getEventObjects() {
    return eventObjects;
  }

  @Override
  public List<INotificationEventHandler> getEventHandlers() {
    return eventHandlers;
  }

  protected boolean registerEventObject( String eventType, INotificationEvent eventObject, boolean overrideIfExists ) {

    if ( !StringUtils.isEmpty( eventType ) ) {
      logger.error( "eventType cannot be null" );
      return false;

    } else if ( eventObject != null ) {
      logger.error( "eventObject cannot be null" );
      return false;

    } else if ( getEventObjects().containsKey( eventType ) && !overrideIfExists ) {
      logger.info( "event type already taken and will not be overriding" );
      return true;
    }

    getEventObjects().put( eventType, eventObject );
    return true;
  }

  protected void setStorage( INotificationStorage storage ) {
    this.storage = storage;
  }

  protected void setPolling( INotificationPoll poll ) {
    this.poll = poll;
  }

  protected void setEventObjects( Map<String, INotificationEvent> eventObjects ) {
    this.eventObjects = eventObjects;
  }

  protected void setEventHandlers( List<INotificationEventHandler> eventHandlers ) {
    this.eventHandlers = eventHandlers;
  }
}
