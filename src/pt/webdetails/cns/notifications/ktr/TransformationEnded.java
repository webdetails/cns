package pt.webdetails.cns.notifications.ktr;

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.extension.ExtensionPoint;
import org.pentaho.di.core.extension.ExtensionPointInterface;
import org.pentaho.di.core.logging.LogChannelInterface;
import org.pentaho.di.trans.Trans;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.webdetails.cns.api.NotificationQueueApi;
import pt.webdetails.cns.notifications.Notification;

/**
 * @link http://wiki.pentaho.com/display/EAI/PDI+Extension+Point+Plugins
 */

@ExtensionPoint(
  id = "TransformationFinish",
  extensionPointId = "CNS_TransformationFinish",
  description = "A transformation finishes"
)
public class TransformationEnded implements ExtensionPointInterface {

  private Logger logger = LoggerFactory.getLogger( TransformationEnded.class );

  public void callExtensionPoint( LogChannelInterface logChannelInterface, Object o ) throws KettleException {

    if ( o == null || !( o instanceof Trans ) ) {
      return;
    }

    try {
      Notification notification = new Notification( "pentaho", "System", ( "[Finished] " + ( (Trans) o ).getName() ) );

      NotificationQueueApi.push( notification );
    } catch ( Exception e ) {
      logger.error( e.getLocalizedMessage(), e );
      logChannelInterface.logError( e.getLocalizedMessage(), e );
    }

  }
}
