fromCategory("product")
   .foreachStream()
   .when({
        $init: function() {
           return { count: 0 };
        },
        "increase": function(state, event) {
          state.count += event.data.amount;
        },
        "decrease": function(state, event) {
          state.count -= event.data.amount;
        }
  });
