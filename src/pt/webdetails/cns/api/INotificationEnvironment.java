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

package pt.webdetails.cns.api;

import java.util.List;
import java.util.Map;

public interface INotificationEnvironment {

  INotificationStorage getStorage();

  INotificationPoll getPoll();

  Map<String, INotificationEvent> getEventObjects();

  List<INotificationEventHandler> getEventHandlers();

}
