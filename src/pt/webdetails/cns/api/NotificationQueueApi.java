package pt.webdetails.cns.api;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.notifications.Notification;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import java.util.LinkedList;
import java.util.Queue;

@Path( "/cns/api/queue" )
public class NotificationQueueApi {

  private static Queue<Notification> notifications = new LinkedList<Notification>();
  private Logger logger = LoggerFactory.getLogger( NotificationQueueApi.class );

  public static void push( Notification notification ) {
    if ( notification != null ) {
      notifications.add( notification );
    }
  }

  @GET
  @Path( "/subscribe" )
  public String subscribe() {
    return "OK";
  }

  @GET
  @Path( "/update" )
  @Produces( "application/json" )
  public String update() {

    while ( notifications.isEmpty() ) {

      try {
        logger.debug( "no notifications in queue; we'll check again in ~ 3 secs" );
        Thread.sleep( 3000 ); // sleep for 3 secs
      } catch ( InterruptedException e ) {
        // do nothing
      }
    }

    return toJsonString( pop() );
  }

  private Notification pop() {
    if ( !notifications.isEmpty() ) {
      return notifications.remove();
    }
    return null;
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
