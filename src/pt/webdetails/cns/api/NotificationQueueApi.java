package pt.webdetails.cns.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.notifications.Notification;
import pt.webdetails.cns.service.NotificationService;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path( "/cns/api/queue" )
public class NotificationQueueApi {

  private Logger logger = LoggerFactory.getLogger( NotificationQueueApi.class );

  @GET
  @Path( "/subscribe" )
  public String subscribe() {
    return "OK";
  }

  @GET
  @Path( "/update" )
  @Produces( "application/json" )
  public String update() {
    return toJsonString( getService().pop() );
  }

  // useful for junit mock
  private NotificationService getService() {
    return NotificationService.getInstance();
  }

  private String toJsonString( Notification notification ) {
    if ( notification != null ) {

      try {

        JSONObject jsonNotification = new JSONObject();
        jsonNotification.put( "style", notification.getStyle() );
        jsonNotification.put( "author", notification.getAuthor() );
        jsonNotification.put( "message", notification.getMessage() );

        return jsonNotification.toString( 2 );

      } catch ( JSONException e ) {
        logger.error( e.getLocalizedMessage(), e );
      }
    }

    return "{}";
  }
}
