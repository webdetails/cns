package pt.webdetails.cns.api;

import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.service.Notification;
import pt.webdetails.cns.service.NotificationEngine;
import pt.webdetails.cns.utils.SessionUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path( "/cns/api/queue" )
public class NotificationQueueApi {

  private Logger logger = LoggerFactory.getLogger( NotificationQueueApi.class );

  @GET
  @Path( "/subscribe" )
  public String subscribe() {
    boolean success = getEngine().subscribeToPoolingQueue( SessionUtils.getUserInSession() );
    return success ? "OK" : "ERROR";
  }

  @GET
  @Path( "/update" )
  @Produces( "application/json" )
  public String update() throws JSONException {
    Notification n = getEngine().popFromPollingQueue( SessionUtils.getUserInSession() );
    return n != null ? n.toJsonString() : "{}";
  }

  // useful for junit mock
  protected NotificationEngine getEngine() {
    return NotificationEngine.getInstance();
  }
}
