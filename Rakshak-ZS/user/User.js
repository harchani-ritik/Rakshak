var mongoose = require("mongoose");
var UserSchema = new mongoose.Schema({
  uid:{
    type: String,
    default:""
  },
  password: {
    type: String,
    default: ""
  },
  networkId:{
    type: String,
    default: ""
  },
  allowRing:{
    type: Boolean,
    default: false
  }
});
mongoose.model("User", UserSchema);

module.exports = mongoose.model("User");
