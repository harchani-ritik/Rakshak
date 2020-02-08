//calling express
var express = require("express");
app = express();

//handle bars with section
var handlebars = require("express-handlebars").create({
  defaultLayout: "main",
  helpers: {
    section: function(name, options) {
      if (!this._sections) this._sections = {};
      this._sections[name] = options.fn(this);
      return null;
    }
  }
});

app.engine("handlebars", handlebars.engine);
app.set("view engine", "handlebars");

// add body parser

const bodyParser = require("body-parser");

app.use(bodyParser.urlencoded({ extended: false }));
app.use(bodyParser.json());

// mongoose connect

const mongoose = require("mongoose");

// mongoose.connect(
//   "mongodb+srv://admin:admin_password@cluster0-vi5m5.mongodb.net/test?retryWrites=true&w=majority",
//   { useMongoClient: true }
// );

//add port to listen-default 3000
app.set("port", process.env.PORT || 3000);

// add public direct
app.use(express.static(__dirname + "/public"));

//MAIN code goes here

app.get("/login", (req, res) => {
  res.render("login");
});

app.post("/login", (req, res) => {
  var request = require("request");
  console.log(req.body);
  request.post(
    "http://localhost:3001/form-page",
    { json: { email: req.body.email, pass: req.body.pass } },
    function(error, response, body) {
      if (!error && response.statusCode == 200) {
        console.log(body);
      }
    }
  );
});

// Middlewares and errors
// 404 page
app.use(function(req, res) {
  res.status(404);

  res.send("404");
});

// 500 page
app.use(function(err, req, res, next) {
  console.error(err.stack);
  res.status(500);
  res.send("500");
});

//Listen port
app.listen(app.get("port"), function() {
  console.log("express started " + app.get("port"));
});
