var allow = true;
var startUrl;
var pollUrl;

function NotificationsPoll() {

  this.start = function start(start, poll) {

    startUrl = start;
    pollUrl = poll;
    
    if (request) {
      request.abort(); // abort any pending request
    }

    // fire off the request to MatchUpdateController
    var request = $.ajax({
      url : startUrl,
      type : "get",
    });

    // This is jQuery 1.8+
    // callback handler that will be called on success
    request.done(function(reply) {

      setInterval(function() {
        if (allow === true) {
          allow = false;
          getUpdate();
        }
      }, 5000); // 5 seconds
    });
    
    // callback handler that will be called on failure
    request.fail(function(jqXHR, textStatus, errorThrown) {
      // log the error to the console
      console.log("[Polling] at start, the following error occured: " + textStatus, errorThrown);
    });
    
    
  };
  
  function getUpdate() {
                                  
    if (request) {
      request.abort();  // abort any pending request
    }
    
    // fire off the request to MatchUpdateController
    var request = $.ajax({
      url : pollUrl,
      type : "get",
    });

    // This is jQuery 1.8+
    // callback handler that will be called on success
    request.done(function( notification ) {
      //console.log("[Polling] Received a notification: " + notification );

      if( !$.notify ){
          //console.log("[Polling] $.notify undefined");
          require(['/pentaho/api/repos/cns/static/custom/js/notify/notify-modified.js']);
          return;
      }

      if( notification && notification.message ) {

        notification.notificationType = notification.notificationType || "default";

        if( !isStylingSet( notification.notificationType ) ){ 
          initStyling( notification.notificationType ); 
        }
        
        var msg = notification.message;
        if( 'mail' == notification.notificationType ){
          msg = 'From ' + notification.recipient + ': ' + notification.message;
        }

        $.notify( msg , { 
                              style: notification.notificationType , 
                              autoHide: true, 
                              autoHideDelay: 8000, 
                              showAnimation: 'slideDown',
                              showDuration: 500,
                              hideAnimation: 'fadeOut',
                              hideDuration: 500
                            });
      }
    });
    
    function isStylingSet( styling ){
      return $.notify.getStyle( styling ) ? true : false;
    }

    // TODO put styles in a css file, and only call the classes here

    function initStyling( style ){

      if( style && notificationSelectorExists( ".notifyjs-" + style + "-base") ){

        $.notify.addStyle( style , {
            html: "<div><div class='notifyjs-" + style + "-image'/><span data-notify-text/></div>",
            classes: {}
        });

      } else {
        console.log("No css style defined for '" + style + "'; applying a non-stylized one" );
      } 
    };

    function getAllNotificationSelectors() { 
        var ret = [];
        for( var i = 0; i < document.styleSheets.length; i++ ) {
            var rules = document.styleSheets[i].rules || document.styleSheets[i].cssRules;
            for( var x in rules ) {
                // must start with prefix '.notification-'
                if( typeof rules[x].selectorText == 'string' && rules[x].selectorText.indexOf('.notifyjs-') == 0 ){
                  ret.push( rules[x].selectorText );
                } 
            }
        }
        return ret;
    }

    function notificationSelectorExists( selector ) { 
        var selectors = getAllNotificationSelectors();
        for( var i = 0; i < selectors.length; i++ ) {
            if( selectors[i] == selector ) return true;
        }
        return false;
    }

    // callback handler that will be called on failure
    request.fail(function(jqXHR, textStatus, errorThrown) {
      // log the error to the console
      console.log("[Polling] The following error occured: " + textStatus, errorThrown);
    });

    // callback handler that will be called regardless if the request failed or succeeded
    request.always(function() {
      allow = true;
    });
  };  
};
