var express = require("express");
var app = express();
global.__root = __dirname + "/";

app.get("/api", function(req, res) {
  res.status(200).send("API works.");
});


module.exports = app;
