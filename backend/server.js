var app = require("./app");
var port = process.env.PORT || 3001;

var server = app.listen(3001, function() {
  console.log("Express server listening on port " + port);
});