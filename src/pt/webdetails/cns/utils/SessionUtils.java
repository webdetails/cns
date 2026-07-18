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

package pt.webdetails.cns.utils;

import org.pentaho.platform.api.engine.IUserRoleListService;
import org.pentaho.platform.engine.core.system.PentahoSessionHolder;
import org.pentaho.platform.engine.core.system.PentahoSystem;
import org.pentaho.platform.engine.security.SecurityHelper;

import java.util.List;

public class SessionUtils {

  public static boolean isAdministrator() {
    return SecurityHelper.getInstance().isPentahoAdministrator( PentahoSessionHolder.getSession() );
  }

  public static String getUserInSession() {
    return PentahoSessionHolder.getSession().getName();
  }

  public static String[] getRolesForUserInSession() {
    return getRolesForUser( getUserInSession() );
  }

  public static boolean userExists( String user ) {

    if ( getUserRoleListService() != null ) {

      List<String> users = getUserRoleListService().getAllUsers();

      return users != null && users.contains( user );
    }
    return false;
  }

  public static boolean roleExists( String role ) {

    boolean roleExists = false;

    if ( getUserRoleListService() != null ) {

      List<String> roles = getUserRoleListService().getAllRoles();

      roleExists = roles != null && roles.contains( role );

      if( !roleExists ){

        roles = getUserRoleListService().getSystemRoles(); // last chance: check system ( hidden ) roles

        roleExists = roles != null && roles.contains( role );
      }
    }

    return roleExists;
  }

  public static String[] getRolesForUser( String user ) {

    if ( getUserRoleListService() != null ) {

      List<String> roles = getUserRoleListService().getRolesForUser( null, user );

      if ( roles != null ) {
        return roles.toArray( new String[] { } );
      }
    }
    return null;
  }

  public static String[] getUsersInRole( String role ) {

    if ( getUserRoleListService() != null ) {

      List<String> roles = getUserRoleListService().getUsersInRole( null, role );

      if ( roles != null ) {
        return roles.toArray( new String[] { } );
      }
    }
    return null;
  }

  public static String[] getAllUsers() {
    List<String> users =  getUserRoleListService().getAllUsers();
    if( users != null ){
      return users.toArray( new String[]{} );
    }
    return null;
  }

  protected static IUserRoleListService getUserRoleListService() {
    return PentahoSystem.get( IUserRoleListService.class );
  }
}
