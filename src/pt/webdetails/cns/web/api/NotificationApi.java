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
package pt.webdetails.cns.web.api;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.Constants;
import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.service.Notification;
import pt.webdetails.cns.service.NotificationEngine;
import pt.webdetails.cns.utils.SessionUtils;
import pt.webdetails.cns.utils.Utils;

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

@Path( "/cns/api/do" )
public class NotificationApi {

  private Logger logger = LoggerFactory.getLogger( NotificationApi.class );

  @GET
  @Path( "/count" )
  public String doGetPathCount() {
    return String
      .valueOf( getEngine().getTotalCount( SessionUtils.getUserInSession(), SessionUtils.getRolesForUserInSession(),
        false ) );
  }

  @GET
  @Path( "/count/unread" )
  public String doGetPathCountUnread() {
    return String
      .valueOf( getEngine().getTotalCount( SessionUtils.getUserInSession(), SessionUtils.getRolesForUserInSession(),
        true ) );
  }

  @GET
  @Path( "/get" )
  @Produces( "application/json" )
  public String doGetQuery( @QueryParam( Constants.PARAM_ID ) String id ) throws JSONException {
    Notification n = getEngine().getNotificationById( id );
    return Utils.toJsonObject( n ).toString( 2 );
  }

  @GET
  @Path( "/get/{id: [^?]+ }" )
  @Produces( "application/json" )
  public String doGetPath( @PathParam( Constants.PARAM_ID ) String id ) throws JSONException {
    Notification n = getEngine().getNotificationById( id );
    return Utils.toJsonObject( n ).toString( 2 );
  }

  @GET
  @Path( "/notify/user" )
  public Response doGetQueryNotifyUser(
    @QueryParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @QueryParam( Constants.PARAM_RECIPIENT ) String recipient,
    @QueryParam( Constants.PARAM_TITLE ) String title,
    @QueryParam( Constants.PARAM_MESSAGE ) String message,
    @QueryParam( Constants.PARAM_LINK ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.USER, recipient, title, message,
      link );
  }

  @GET
  @Path( "/notify/group" )
  public Response doGetQueryNotifyGroup(
    @QueryParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @QueryParam( Constants.PARAM_RECIPIENT ) String recipient,
    @QueryParam( Constants.PARAM_TITLE ) String title,
    @QueryParam( Constants.PARAM_MESSAGE ) String message,
    @QueryParam( Constants.PARAM_LINK ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.ROLE, recipient, title, message,
      link );
  }

  @GET
  @Path( "/notify/all" )
  public Response doGetQueryNotifyAll(
    @QueryParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @QueryParam( Constants.PARAM_TITLE ) String title,
    @QueryParam( Constants.PARAM_MESSAGE ) String message,
    @QueryParam( Constants.PARAM_LINK ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.ALL, null, title, message, link );
  }

  @GET
  @Path( "/notify" )
  public Response doGetQueryNotify(
    @QueryParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @QueryParam( Constants.PARAM_RECIPIENT_TYPE ) String recipientType,
    @QueryParam( Constants.PARAM_RECIPIENT ) String recipient,
    @QueryParam( Constants.PARAM_TITLE ) String title,
    @QueryParam( Constants.PARAM_MESSAGE ) String message,
    @QueryParam( Constants.PARAM_LINK ) String link ) {

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
    @PathParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @PathParam( Constants.PARAM_RECIPIENT ) String recipient,
    @PathParam( Constants.PARAM_TITLE ) String title,
    @PathParam( Constants.PARAM_MESSAGE ) String message ) {
    return notify( notificationType, INotificationEvent.RecipientType.USER, recipient, title, message,
      null );
  }

  @GET
  @Path( "/notify/group/{notificationType: [^?]+ }/{recipient: [^?]+ }/{title: [^?]+ }/{message: [^?]+ }" )
  public Response doGetPathNotifyGroup(
    @PathParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @PathParam( Constants.PARAM_RECIPIENT ) String recipient,
    @PathParam( Constants.PARAM_TITLE ) String title,
    @PathParam( Constants.PARAM_MESSAGE ) String message ) {
    return notify( notificationType, INotificationEvent.RecipientType.ROLE, recipient, title, message,
      null );
  }

  @GET
  @Path( "/notify/all/{notificationType: [^?]+ }/{title: [^?]+ }/{message: [^?]+ }" )
  public Response doGetPathNotifyAll(
    @PathParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @PathParam( Constants.PARAM_TITLE ) String title,
    @PathParam( Constants.PARAM_MESSAGE ) String message ) {
    return notify( notificationType, INotificationEvent.RecipientType.ALL, null, title, message, null );
  }

  @GET
  @Path(
    "/notify/{notificationType: [^?]+ }/{recipientType: [^?]+ }/{recipient: [^?]+ }/{title: [^?]+ }/{message: [^?]+ }" )
  public Response doGetPathNotify(
    @PathParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @PathParam( Constants.PARAM_RECIPIENT_TYPE ) String recipientType,
    @PathParam( Constants.PARAM_RECIPIENT ) String recipient,
    @PathParam( Constants.PARAM_TITLE ) String title,
    @PathParam( Constants.PARAM_MESSAGE ) String message ) {

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
    @FormParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @FormParam( Constants.PARAM_RECIPIENT ) String recipient,
    @FormParam( Constants.PARAM_TITLE ) String title,
    @FormParam( Constants.PARAM_MESSAGE ) String message,
    @FormParam( Constants.PARAM_LINK ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.USER, recipient, title, message,
      link );
  }

  @POST
  @Path( "/notify/group" )
  public Response doPostNotifyGroup(
    @FormParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @FormParam( Constants.PARAM_RECIPIENT ) String recipient,
    @FormParam( Constants.PARAM_TITLE ) String title,
    @FormParam( Constants.PARAM_MESSAGE ) String message,
    @FormParam( Constants.PARAM_LINK ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.ROLE, recipient, title, message,
      link );
  }

  @POST
  @Path( "/notify/all" )
  public Response doPostNotifyAll(
    @FormParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @FormParam( Constants.PARAM_TITLE ) String title,
    @FormParam( Constants.PARAM_MESSAGE ) String message,
    @FormParam( Constants.PARAM_LINK ) String link ) {
    return notify( notificationType, INotificationEvent.RecipientType.ALL, null, title, message, link );
  }

  @POST
  @Path( "/notify" )
  public Response doPostNotify(
    @FormParam( Constants.PARAM_NOTIFICATION_TYPE ) @DefaultValue( Constants.DEFAULT_NOTIFICATION_TYPE )
    String notificationType,
    @FormParam( Constants.PARAM_RECIPIENT_TYPE ) String recipientType,
    @FormParam( Constants.PARAM_RECIPIENT ) String recipient,
    @FormParam( Constants.PARAM_TITLE ) String title,
    @FormParam( Constants.PARAM_MESSAGE ) String message,
    @FormParam( Constants.PARAM_LINK ) String link ) {

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
