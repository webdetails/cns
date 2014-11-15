package pt.webdetails.cns.web.api;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.Constants;
import pt.webdetails.cns.service.Notification;
import pt.webdetails.cns.service.NotificationEngine;
import pt.webdetails.cns.utils.SessionUtils;
import pt.webdetails.cns.utils.Utils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path( "/cns/api/poll" )
public class NotificationPollApi {

  private Logger logger = LoggerFactory.getLogger( NotificationPollApi.class );

  @GET
  @Path( "/subscribe/all" )
  public String subscribeAll() {
    boolean success = getEngine().subscribeToPoll( SessionUtils.getUserInSession(), null );
    return success ? "OK" : "ERROR";
  }

  @GET
  @Path( "/subscribe/{notificationType: [^?]+ }" )
  public String subscribe( @PathParam( Constants.PARAM_NOTIFICATION_TYPE ) String notificationType ) {
    boolean success = getEngine().subscribeToPoll( SessionUtils.getUserInSession(), notificationType );
    return success ? "OK" : "ERROR";
  }

  @GET
  @Path( "/unsubscribe/{notificationType: [^?]+ }" )
  public String unsubscribe( @PathParam( Constants.PARAM_NOTIFICATION_TYPE ) String notificationType ) {
    boolean success = true; // TODO
    return success ? "OK" : "ERROR";
  }

  @GET
  @Path( "/get" )
  @Produces( "application/json" )
  public String update() throws JSONException {
    Notification n = getEngine().popFromPoll( SessionUtils.getUserInSession() );
    return Utils.toJsonObject( n ).toString( 2 );
  }

  // useful for junit mock
  protected NotificationEngine getEngine() {
    return NotificationEngine.getInstance();
  }
}
