var fs=require("fs");
var express=require("express");
var session=require("express-session");
var bodyParser=require("body-parser");
var cookieParser=require("cookie-parser");
var app=express();
var bParser=bodyParser.urlEncoded({extended:true});
app.use(bParser);
app.use(bParser.json());
app.use(cookieParser());
app.use(session({secret:"NWFServer!@#$%^&*"}));
app.use("/",express.static("NWFServer"));
app.set("view engine","pug");
app.set("views","views");
app.get("/",function(req,res)
{
res.render("showEntryDetails");
});
