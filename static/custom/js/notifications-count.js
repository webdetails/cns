function NotificationsCount( url ) {

  var _url = url;

  function enableUnreadMarker( unreadCount ) { 

    if( unreadMarker() ){
      $('div#pucUserDropDown > div#notificationUnreadCount').css( 'display' , 'inherit' );
      $('div#pucUserDropDown > div#notificationUnreadCount').html( unreadCount < 100 ? unreadCount : '99+' );
    }
  };

  function disableUnreadMarker() { 

    if( unreadMarker() ){
      $('div#pucUserDropDown > div#notificationUnreadCount').css( 'display' , 'none' );
    }
  }; 

  function unreadMarker() {
    return $('div#pucUserDropDown') && $('div#pucUserDropDown > div#notificationUnreadCount').length > 0;
  };

  function buildUnreadMarker() {
    var $notificationUnreadCount = $( "<div id='notificationUnreadCount' class='notification-unread-count' />" );
    $('div#pucUserDropDown').prepend( $notificationUnreadCount ); 
    disableUnreadMarker();
    $notificationUnreadCount.on( 'click' , function(){ 
      window.open('/pentaho/plugin/cns/api/notifications' , '_blank' );
    });
  };

  this.check = function() {

    if( !unreadMarker() ){ buildUnreadMarker(); }
    
    // fire off the request to MatchUpdateController
    $.ajax({
      url : _url,
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


