//Getting the essentials
const express = require("express");
const admin = require('firebase-admin');
const report = require("./Report");
const bodyParser = require("body-parser");
const request = require("request");
const serviceAccount = require("./serviceaccount.json");
var mongoose = require("mongoose");

//Setting the strings
const predictServer = "http://192.168.43.212:3005/"
global.__root = __dirname + "/";

//Initializing the App
const app = express();

//Adding middlewares
app.use(bodyParser.urlencoded({ extended: false }))
app.use(bodyParser.json())

//Adding router for Users related requests
const UserController = require(__root + "user/UserController");
app.use("/users", UserController);

//Adding router for Auth requests
const AuthController = require(__root + "auth/AuthController");
app.use("/auth", AuthController);

//Initializing Firebase
admin.initializeApp({
  credential: admin.credential.cert(serviceAccount),
  databaseURL: "https://rakshak-53755.firebaseio.com"
});

//Initializing mongoDB
mongoose.connect("mongodb+srv://test:test@cluster0-vi5m5.mongodb.net/test?retryWrites=true&w=majority", {
  useUnifiedTopology: true,
  useNewUrlParser: true
});

//Setting Users and Network firebase references
const db = admin.database();
const firebaseUsers = db.ref("users");
const firebaseNetworks = db.ref("network");

//Function for sending notification to the selected user
const message = (registrationToken, location, type, msg) => {
  var message = {
    data: {
      loc : location,
      type: type,
      msg: msg
    },
    token: registrationToken
  };
  admin.messaging().send(message)
    .then((response) => {
      console.log('Successfully sent message:' + registrationToken, response);
    })
    .catch((error) => {
      console.log('Error sending message:' + registrationToken, error);
    });
};

//Dummy function for checking whether to send the notification to a user or nit
const isViable = (uid) => {
  return true;
};

//?Dummy function that decodes the network id into firebase key
const decode = (key) => {
  return key;
}

//Checks if the rquest is valid
const isValid = (body) => {
  return true;
};

//Just a route to get the info of the firebase data
app.get("/", function(req, res) {

  firebaseUsers.on("value", function(snapshot) {
    console.log(snapshot.val());
  }, function (errorObject) {
    console.log("The read failed: " + errorObject.code);
  });
  firebaseNetworks.on("value", function(snapshot) {
    console.log(snapshot.val());
  }, function (errorObject) {
    console.log("The read failed: " + errorObject.code);
  });
  res.status(200).send("Something went wrong");
});


// ?Register a user on a network
app.post("/usenetwork",(req, res) => {
  console.log(req.body);
  let networkId = decode(req.body.key);
  res.send("hi");
});

// ?Network Server Raisng an alert, sends everyone in the network a notification
app.post("/raiseAlert", function (req, res){
  console.log(req.body);
  let userstoSend = [];
  firebaseNetworks.on("value", function(snapshot) {
    let ids = snapshot.val();
    Object.keys(ids).forEach(function(key) {
      //Decide whether the current key is viable for sending the message
       if (ids[key]==req.body.uid && isViable(key)){
        console.log(ids[key]);
        userstoSend.push(key);
       } 
    });
    console.log(userstoSend);
    firebaseUsers.on("value", function(snapshot) {
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


//Handling emergency requests from users
app.post("/requests", function(req, res){
  console.log(req.body);
  //checking for a valid request
  if(!isValid(req.body)) res.status(404).send("invalid request");
  //getting the uid
  let uid = req.body.uid;
  
  //getting all the registrationTokens and sending the notification
  firebaseUsers.on("value", function(snapshot) {
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
  //Creates the report on the network
  firebaseNetworks.on("value", function(snapshot) {
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


app.post("/predict", function(req, res){
  console.log(req.body);
  let url = predictServer + 
          "predict?military_time=" + req.body.military_time + 
          "&lat=" + req.body.lat +
          "&long=" + req.body.long +
          "&age=" + req.body.age + 
          "&gender=" + req.body.gender; 
          console.log(url); 
          request.get(
            url,
            function(error, response, body) {
              if (!error && response.statusCode == 200) {
                console.log(body);
              }
              res.send(response);
            }
          );
});


app.post("/form-page", function(req, res){
  res.send("Hi");
});

module.exports = app;
