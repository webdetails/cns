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

import com.google.common.eventbus.AsyncEventBus;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.core.system.objfac.references.SingletonPentahoObjectReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.service.INotificationEventHandler;
import pt.webdetails.cns.service.NotificationService;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;

public class NotificationInitializer {

  private Logger logger = LoggerFactory.getLogger( NotificationInitializer.class );

  public NotificationInitializer( List<INotificationEventHandler> subscribers ) {

    initializeAsyncEventBus();

    if ( subscribers != null ) {

      for ( INotificationEventHandler subscriber : subscribers ) {
        try {
          getAsyncEventBus().register( subscriber );
        } catch ( Throwable t ) {
          logger.error( t.getLocalizedMessage(), t );
        }
      }
    }
  }

  protected AsyncEventBus getAsyncEventBus() {
    return PentahoSystem.get( AsyncEventBus.class, NotificationService.EVENT_BUS_ID, null );
  }

  protected void initializeAsyncEventBus(){

    AsyncEventBus asyncEventBus = new AsyncEventBus( Executors.newCachedThreadPool() );

    // register the bus with PentahoSystem
    PentahoSystem.registerReference(
      new SingletonPentahoObjectReference.Builder<AsyncEventBus>( AsyncEventBus.class ).object( asyncEventBus )
        .attributes(
          Collections.<String, Object>singletonMap( "id", NotificationService.EVENT_BUS_ID ) ).build(),
      AsyncEventBus.class );
  }

}
