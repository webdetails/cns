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

/**
 * @author Marco Vala
 */
public class Response {

  private long responseTime;
  private int statusCode;
  private String result;

  public long getResponseTime() {
    return this.responseTime;
  }

  public Response setResponseTime( long responseTime ) {
    this.responseTime = responseTime;
    return this;
  }

  public int getStatusCode() {
    return this.statusCode;
  }

  public Response setStatusCode( int statusCode ) {
    this.statusCode = statusCode;
    return this;
  }

  public String getResult() {
    return this.result;
  }

  public Response setResult( String result ) {
    this.result = result;
    return this;
  }

  public Response() {
    this.responseTime = -1;
    this.statusCode = -1;
    this.result = "";
  }
}
