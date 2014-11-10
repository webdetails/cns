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

import org.apache.commons.lang.StringUtils;
import pt.webdetails.cns.notifications.simple.SimpleNotificationEvent;
import pt.webdetails.cns.service.NotificationService;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.util.Date;

@Path( "/cns/api" )
public class NotificationApi {

  @GET
  @Path( "/notify" )
  public Response doGetQueryNotify( @QueryParam( "style" ) @DefaultValue( "pentaho" ) String style,
                                    @QueryParam( "author" ) String author, @QueryParam( "message" ) String message ) {
    return notify( style, author, message );
  }

  @GET
  @Path( "/notify/{style: [^?]+ }/{author: [^?]+ }/{message: [^?]+ }" )
  public Response doGetPathNotify( @PathParam( "style" ) @DefaultValue("pentaho") String style,
                                   @PathParam( "author" ) String author, @PathParam( "message" ) String message ) {
    return notify( style, author, message );
  }

  @POST
  @Path( "/notify" )
  public Response doPostNotify( @FormParam( "style" ) @DefaultValue( "pentaho" ) String style,
                                @FormParam( "author" ) String author, @FormParam( "message" ) String message ) {
    return notify( style, author, message );
  }

  // useful for junit mock
  private NotificationService getService() {
    return NotificationService.getInstance();
  }

  private Response notify( String style, String author, String message ) {

    if ( StringUtils.isEmpty( author ) || StringUtils.isEmpty( message ) ) {
      return Response.status( Status.NOT_ACCEPTABLE ).build();
    }

    style = StringUtils.defaultIfBlank( style , "pentaho" );

    boolean successful = getService().notify( new SimpleNotificationEvent( style, author, message ) );

    return Response.status( successful ? Status.OK : Status.INTERNAL_SERVER_ERROR ).build();
  }
}
