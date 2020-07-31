var mongoose = require("mongoose");
var ReportSchema = new mongoose.Schema({
  networkId:{
    type: String,
    default: ""
  },
  uid:{
      type:String,
      default: ""
  },
  name:{
    type:String,
    default: ""
  },
  date:{
    type: Date,
    default: Date.now
  },
  msg:{
      type : String,
      default: ""
  },
  type:{
    type: String,
    default: "general"
  }
});
mongoose.model("Report", ReportSchema);

module.exports = mongoose.model("Report");
