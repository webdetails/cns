package pt.webdetails.cns.service.store;

import pt.webdetails.cns.api.INotificationEvent;
import pt.webdetails.cns.service.Notification;

import java.util.List;

public interface INotificationStorage {

  int getTotalCount( String user, String[] roles, boolean unreadOnly );

  boolean store( INotificationEvent.RecipientType recipientType, Notification notification );

  Notification getNextUnread( String user, String[] roles );

  List<Notification> getAll( String user, String[] roles, boolean unreadOnly );

  Notification getNotificationById( String id );

  Notification deleteNotificationById( String id );

  void markNotificationAsRead( String id );

}
