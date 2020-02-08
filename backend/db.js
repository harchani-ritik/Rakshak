var mongoose = require("mongoose");
mongoose.connect("mongodb+srv://test:test@cluster0-vi5m5.mongodb.net/test?retryWrites=true&w=majority", {
  useUnifiedTopology: true,
  useNewUrlParser: true
});
