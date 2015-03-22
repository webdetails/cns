function NotificationsCount() {

  function reschedule() {
    setTimeout(function(){
      new NotificationsCount().check();
    }, 15000);
  };

  function enableUnreadMarker( unreadCount ) { 

      if( unreadMarker() ){
        $('div#pucUserDropDown > div#notificationUnreadCount').css( 'display' , 'inherit' );
        $('div#pucUserDropDown > div#notificationUnreadCount').html( unreadCount < 100 ? unreadCount : '99+' );
      }

      reschedule();
  };

  function disableUnreadMarker() { 

    if( unreadMarker() ){
      $('div#pucUserDropDown > div#notificationUnreadCount').css( 'display' , 'none' );
    }

    reschedule();
  }; 

  function unreadMarker() {
    return $('div#pucUserDropDown') && $('div#pucUserDropDown > div#notificationUnreadCount').length > 0;
  };

  function buildUnreadMarker() {
    var $notificationUnreadCount = $( "<div id='notificationUnreadCount' class='notification-unread-count' />" );
    $('div#pucUserDropDown').prepend( $notificationUnreadCount ); 
    disableUnreadMarker();
  };

  this.check = function() {

    if( !unreadMarker() ){ buildUnreadMarker(); }
    
    // fire off the request to MatchUpdateController
    var request = $.ajax({
      url : '/pentaho/plugin/cns/api/countnotifications?paramfilter=unread',
      type : 'get',
      success: function( unreadCount ){

        if( unreadCount && unreadCount > 0 ){
          enableUnreadMarker( unreadCount );
        } else {
          disableUnreadMarker();
        }
      },
      error: function( response ){
        disableUnreadMarker();
      }
    });
  };
};
