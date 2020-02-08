var express = require("express");
var admin = require('firebase-admin');
var app = express();
var bodyParser = require("body-parser");
global.__root = __dirname + "/";
var app = express()

app.use(bodyParser.urlencoded({ extended: false }))

app.use(bodyParser.json())
var serviceAccount = require("./serviceaccount.json");

admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://rakshak-53755.firebaseio.com"
});

var db = admin.database();
var ref = db.ref("users");

const messsage = (registrationToken, location, date, type, time) => {
  var message = {
    data: {
      loc : location,
      date: date,
      type: type,
      time: time
    },
    token: registrationToken
  };
  // Send a message to the device corresponding to the provided
  // registration token.
  admin.messaging().send(message)
    .then((response) => {
      // Response is a message ID string.
      console.log('Successfully sent message:', response);
    })
    .catch((error) => {
      console.log('Error sending message:', error);
    });
    res.status(200).send("Yes");
};

const isViable = (uid) => {
  return true;
};

const isValid = (body) => {
  return true;
};


app.get("/", function(req, res) {
  res.status(200).send("Yes");
});

app.post("/requests", function(req, res){
  //checking for a valid request
  if(!isValid(req.body)) res.status(404).send("invalid request");
  
  //getting the uid
  let uid = req.body.uid;
  
  //getting all the registrationTokens and sending the notification
  ref.on("value", function(snapshot) {
    let tokens = snapshot.val();
    Object.keys(tokens).forEach(function(key) {
      //Decide whether the current key is viable for sending the message
      if (key!==uid && isViable(key)) message(tokens[key], req.body.loc, req.body.date, req.body.type, req.body.time);
    });
  }, function (errorObject) {
    console.log("The read failed: " + errorObject.code);
  });

  //the response
  res.status(200).send(req.body);
});
module.exports = app;
