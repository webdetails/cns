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
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.Constants;
import pt.webdetails.cns.service.Notification;
import pt.webdetails.cns.service.NotificationEngine;
import pt.webdetails.cns.utils.SessionUtils;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path( "/cns/api/notification" )
public class NotificationApi {

  private Logger logger = LoggerFactory.getLogger( NotificationApi.class );

  @GET
  @Path( "/notifications/count" )
  public int doGetQueryCount( @QueryParam( "unread" ) @DefaultValue( "false" ) String unread ) {
    return getEngine().getTotalCount( SessionUtils.getUserInSession(), SessionUtils.getRolesForUserInSession(),
      "true".equalsIgnoreCase( unread ) );
  }

  @GET
  @Path( "/notifications/count/{unread: [^?]* }" )
  public int doGetPathCount( @PathParam( "unread" ) @DefaultValue( "false" ) String unread ) {
    return getEngine().getTotalCount( SessionUtils.getUserInSession(), SessionUtils.getRolesForUserInSession(),
      "true".equalsIgnoreCase( unread ) );
  }

  @GET
  @Path( "/notifications/get" )
  @Produces( "application/json" )
  public String doGetQuery( @QueryParam( "id" ) String id ) throws JSONException {
    Notification n = getEngine().getNotificationById( id );
    return n != null ? n.toJsonString() : "{}";
  }

  @GET
  @Path( "/notifications/get/{id: [^?]+ }" )
  @Produces( "application/json" )
  public String doGetPath( @PathParam( "id" ) String id ) throws JSONException {
    Notification n = getEngine().getNotificationById( id );
    return n != null ? n.toJsonString() : "{}";
  }

  @GET
  @Path( "/notify/user" )
  public Response doGetQueryNotifyUser(
    @QueryParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @QueryParam( "recipient" ) String recipient,
    @QueryParam( "title" ) String title,
    @QueryParam( "message" ) String message,
    @QueryParam( "link" ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.USER, recipient, title, message,
      link );
  }

  @GET
  @Path( "/notify/group" )
  public Response doGetQueryNotifyGroup(
    @QueryParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @QueryParam( "recipient" ) String recipient,
    @QueryParam( "title" ) String title,
    @QueryParam( "message" ) String message,
    @QueryParam( "link" ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.ROLE, recipient, title, message,
      link );
  }

  @GET
  @Path( "/notify/all" )
  public Response doGetQueryNotifyAll(
    @QueryParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @QueryParam( "title" ) String title,
    @QueryParam( "message" ) String message,
    @QueryParam( "link" ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.ALL, null, title, message, link );
  }

  @GET
  @Path( "/notify" )
  public Response doGetQueryNotify(
    @QueryParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @QueryParam( "recipientType" ) String recipientType,
    @QueryParam( "recipient" ) String recipient,
    @QueryParam( "title" ) String title,
    @QueryParam( "message" ) String message,
    @QueryParam( "link" ) String link ) {

    if ( !isValidRecipientType( recipientType ) ) {
      logger.error( "Invalid recipientType '" + recipientType + "'" );
      return Response.status( Status.INTERNAL_SERVER_ERROR ).build();
    }

    return notify( notificationType, INotificationEvent.RecipientType.valueOf( recipientType ), recipient, title,
      message, link );
  }

  @GET
  @Path( "/notify/user/{notificationType: [^?]+ }/{recipient: [^?]+ }/{title: [^?]+ }/{message: [^?]+ }" )
  public Response doGetPathNotifyUser(
    @PathParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @PathParam( "recipient" ) String recipient,
    @PathParam( "title" ) String title,
    @PathParam( "message" ) String message ) {
    return notify( notificationType, INotificationEvent.RecipientType.USER, recipient, title, message,
      null );
  }

  @GET
  @Path( "/notify/group/{notificationType: [^?]+ }/{recipient: [^?]+ }/{title: [^?]+ }/{message: [^?]+ }" )
  public Response doGetPathNotifyGroup(
    @PathParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @PathParam( "recipient" ) String recipient,
    @PathParam( "title" ) String title,
    @PathParam( "message" ) String message ) {
    return notify( notificationType, INotificationEvent.RecipientType.ROLE, recipient, title, message,
      null );
  }

  @GET
  @Path( "/notify/all/{notificationType: [^?]+ }/{title: [^?]+ }/{message: [^?]+ }" )
  public Response doGetPathNotifyAll(
    @PathParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @PathParam( "title" ) String title,
    @PathParam( "message" ) String message ) {
    return notify( notificationType, INotificationEvent.RecipientType.ALL, null, title, message, null );
  }

  @GET
  @Path(
    "/notify/{notificationType: [^?]+ }/{recipientType: [^?]+ }/{recipient: [^?]+ }/{title: [^?]+ }/{message: [^?]+ }" )
  public Response doGetPathNotify(
    @PathParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @PathParam( "recipientType" ) String recipientType,
    @PathParam( "recipient" ) String recipient,
    @PathParam( "title" ) String title,
    @PathParam( "message" ) String message ) {

    if ( !isValidRecipientType( recipientType ) ) {
      logger.error( "Invalid recipientType '" + recipientType + "'" );
      return Response.status( Status.INTERNAL_SERVER_ERROR ).build();
    }

    return notify( notificationType, INotificationEvent.RecipientType.valueOf( recipientType ), recipient, title,
      message, null );
  }

  @POST
  @Path( "/notify/user" )
  public Response doPostNotifyUser(
    @FormParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @FormParam( "recipient" ) String recipient,
    @FormParam( "title" ) String title,
    @FormParam( "message" ) String message,
    @FormParam( "link" ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.USER, recipient, title, message,
      link );
  }

  @POST
  @Path( "/notify/group" )
  public Response doPostNotifyGroup(
    @FormParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @FormParam( "recipient" ) String recipient,
    @FormParam( "title" ) String title,
    @FormParam( "message" ) String message,
    @FormParam( "link" ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.ROLE, recipient, title, message,
      link );
  }

  @POST
  @Path( "/notify/all" )
  public Response doPostNotifyAll(
    @FormParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @FormParam( "title" ) String title,
    @FormParam( "message" ) String message,
    @FormParam( "link" ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.ALL, null, title, message, link );
  }

  @POST
  @Path( "/notify" )
  public Response doPostNotify(
    @FormParam( "notificationType" ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE ) String notificationType,
    @FormParam( "recipientType" ) String recipientType,
    @FormParam( "recipient" ) String recipient,
    @FormParam( "title" ) String title,
    @FormParam( "message" ) String message,
    @FormParam( "link" ) String link ) {

    if ( !isValidRecipientType( recipientType ) ) {
      logger.error( "Invalid recipientType '" + recipientType + "'" );
      return Response.status( Status.INTERNAL_SERVER_ERROR ).build();
    }

    return notify( notificationType, INotificationEvent.RecipientType.valueOf( recipientType ), recipient, title,
      message, link );
  }

  // useful for junit mock
  protected NotificationEngine getEngine() {
    return NotificationEngine.getInstance();
  }

  protected Response notify( String notificationType, INotificationEvent.RecipientType recipientType, String recipient,
                             String title,
                             String message, String link ) {

    if ( recipientType == null ) {
      logger.error( "Empty recipientType not allowed" );
      return Response.status( Status.INTERNAL_SERVER_ERROR ).build();

    } else if ( StringUtils.isEmpty( recipient ) && INotificationEvent.RecipientType.ALL != recipientType ) {
      logger.error( "Empty recipient not allowed" );
      return Response.status( Status.INTERNAL_SERVER_ERROR ).build();

    } else if ( StringUtils.isEmpty( message ) ) {
      logger.error( "Empty message not allowed" );
      return Response.status( Status.INTERNAL_SERVER_ERROR ).build();
    }

    INotificationEvent appropriateEvent = NotificationEngine.getInstance().getNotificationEvent( notificationType );

    if ( appropriateEvent == null ) {
      logger.error( "No appropriate event object found for given '" + notificationType + "'" );
      return Response.status( Status.INTERNAL_SERVER_ERROR ).build();
    }

    appropriateEvent.populate( notificationType, SessionUtils.getUserInSession(), recipient, recipientType, title,
      message, link );

    boolean successful = getEngine().notify( appropriateEvent );

    return Response.status( successful ? Status.OK : Status.INTERNAL_SERVER_ERROR ).build();
  }

  private boolean isValidRecipientType( String recipientType ) {

    if ( StringUtils.isEmpty( recipientType ) ) {
      return false;
    }

    try {
      INotificationEvent.RecipientType.valueOf( recipientType.toUpperCase() );
      return true;
    } catch ( Exception e ) {
      /* do nothing */
    }
    return false;
  }
}
