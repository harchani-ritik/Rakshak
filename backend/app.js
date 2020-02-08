var express = require("express");
var admin = require('firebase-admin');
var app = express();
let report = require("./Report");
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

var dbm = require("./db");
var db = admin.database();
var ref = db.ref("users");
var refn = db.ref("network");

const message = (registrationToken, location, type, msg) => {
  var message = {
    data: {
      loc : location,
      type: type,
      msg: msg
    },
    token: registrationToken
  };
  // Send a message to the device corresponding to the provided
  // registration token.
  admin.messaging().send(message)
    .then((response) => {
      // Response is a message ID string.
      console.log('Successfully sent message:' + registrationToken, response);
    })
    .catch((error) => {
      console.log('Error sending message:' + registrationToken, error);
    });
};

var UserController = require(__root + "user/UserController");
app.use("/users", UserController);

var AuthController = require(__root + "auth/AuthController");
app.use("/auth", AuthController);

const isViable = (uid) => {
  return true;
};

const isValid = (body) => {
  return true;
};

app.get("/", function(req, res) {

  ref.on("value", function(snapshot) {
    console.log(snapshot.val());
  }, function (errorObject) {
    console.log("The read failed: " + errorObject.code);
  });
  refn.on("value", function(snapshot) {
    console.log(snapshot.val());
  }, function (errorObject) {
    console.log("The read failed: " + errorObject.code);
  });
  res.status(200).send("Something went wrong");
});


app.post("/raiseAlert", function (req, res){
  console.log(req.body);
  let userstoSend = [];
  refn.on("value", function(snapshot) {
    let ids = snapshot.val();
    Object.keys(ids).forEach(function(key) {
      //Decide whether the current key is viable for sending the message
       if (ids[key]==req.body.uid && isViable(key)){
        console.log(ids[key]);
        userstoSend.push(key);
       } 
    });
    console.log(userstoSend);
    ref.on("value", function(snapshot) {
      let tokens = snapshot.val();
      Object.keys(tokens).forEach(function(key) {
        //Decide whether the current key is viable for sending the message
         if (userstoSend.includes(key) && isViable(key) && tokens[key]!=='ABCD'){
          //console.log(tokens[key]);
          message(tokens[key], "18 71", "general", req.body.info);
         } 
      });
    }, function (errorObject) {
      console.log("The read failed: " + errorObject.code);
    });
    res.redirect("home?alert=true");
  }, function (errorObject) {
    console.log("The read failed: " + errorObject.code);
  });
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
       if (key!==uid && isViable(key) && tokens[key]!=='ABCD'){
        //console.log(tokens[key]);
        message(tokens[key], req.body.loc, req.body.type, req.body.msg);
       }
    });
  }, function (errorObject) {
    console.log("The read failed: " + errorObject.code);
  });
  refn.on("value", function(snapshot) {
    admin.auth().getUser(req.body.uid).then((userRecord)=>{
      report.create(
        {
          type : req.body.type,
          msg : req.body.msg,
          uid : req.body.uid,
          networkId : snapshot.val()[uid],
          name: userRecord.displayName
        },
        function(err, report){
          if (err) {
            console.log();
            return res
            .status(500)
            .send("There was a problem adding the information to the database.");
          }
          else {
            console.log(report);
            res.status(200).send(report);
          }
        }
      );
      console.log(snapshot.val()[uid]);
    }, function (errorObject) {
      console.log("The read failed: " + errorObject.code);
    });
    });
    
});

app.post("/form-page", function(req, res){
  res.send("Hi");
});

module.exports = app;
