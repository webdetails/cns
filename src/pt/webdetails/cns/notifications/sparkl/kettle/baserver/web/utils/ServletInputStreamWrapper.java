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


package pt.webdetails.cns.notifications.sparkl.kettle.baserver.web.utils;

import javax.servlet.ServletInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Marco Vala
 */
public final class ServletInputStreamWrapper extends ServletInputStream {

  private final InputStream inputStream;

  public InputStream getInputStream() {
    return this.inputStream;
  }

  public ServletInputStreamWrapper( InputStream inputStream ) {
    this.inputStream = inputStream;
  }

  @Override
  public int read() throws IOException {
    return this.inputStream.read();
  }

  @Override
  public void close() throws IOException {
    super.close();
    this.inputStream.close();
  }
}
