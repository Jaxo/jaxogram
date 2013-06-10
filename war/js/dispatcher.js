/*
| Dispatcher is a singleton in the window object to broadcast "events"
| to registered listeners.
| Events are identified by their name ("somethingHappened" in the example
| below.)
|
| - on the "server side" (the guy who generates the event)
|      dispatcher.post("somethingHappened", "whatHappened", "how");
|   broadcasts the event "somethingHappened" to the registered listeners
|
|
| - the "client side" has registered a listener by doing:
|      dispatcher.on(
|         "somethingHappened",
|         function myAction(arg1, arg2) {
|            alert(arg1, arg2);
|         }
|      }
|   function(arg1, arg2) will be called after the "somethingHappened" event
|   has been posted.
|
| To remove a listener, use
|      dispatcher.off("somethingHappened", myAction);
|
*/
var dispatcher = (
   function () {
      var events = [];
      var dispatcher = {};
      dispatcher.post = function(name) {
         if (events[name]) {
            var args = Array.prototype.slice.call(arguments, 1);
            for (var i=0, l=events[name].length; i<l; ++i) {
               events[name][i].apply(this, args);
            }
         }
      };
      dispatcher.on = function(name, func) {
         events[name] = (events[name] || []).concat([func]);
      };
      dispatcher.off = function(name, func) {
         if (events[name]) {
            var newEvents = [];
            for (var i=0, l=events[name].length; i<l; ++i) {
               var f = events[name][i];
               if (f != func) newEvents.push();
            }
            events[name] = newEvents;
         }
      };
      dispatcher.clean = function() { events = []; };
      return dispatcher;
   }
)();
