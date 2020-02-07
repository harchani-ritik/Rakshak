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


app.get("/", function(req, res) {
var registrationToken = 'c-e1XFgMDro:APA91bFuEg1tnwP8p66INxrhdDGOEK2KYvL5UK3IYpGMkM7Hl-LvWwrpJhBkZ2Fn8kzosHxN42bJ_DREYK4Yhkas4hEPO2r7JnEJNcj1rMsW51OLWqvKn0aMM6I2IfV6SSPqiShddzmw';
var message = {
  data: {
    date: 'Aastha',
    score: '850',
    time: '2:45'
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
});

app.post("/requests", function(req, res){
  // if(!isValid(req.body)) res.status(404).send("invalid request");
  // switch(req.body.type){
  //   case "F":
  // }
  admin.auth().listUsers(10)
    .then(function(listUsersResult) {
      listUsersResult.users.forEach(function(userRecord) {
        console.log('user', userRecord.toJSON());
      });
    }).catch(function(error) {
    console.log('Error fetching user data:', error);
  });
  res.status(200).send(req.body);
});
module.exports = app;
