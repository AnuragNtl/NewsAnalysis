var fs=require("fs");
var express=require("express");
var session=require("express-session");
var bodyParser=require("body-parser");
var cookieParser=require("cookie-parser");
var newsDb=require("./NewsDB.js");
var app=express();
var bParser=bodyParser.urlencoded({extended:true});
app.use(bParser);
app.use(bodyParser.json());
app.use(cookieParser());
app.use(session({secret:"NWFServer!@#$%^&*"}));
app.use("/",express.static("NWFServer"));
app.set("view engine","pug");
app.set("views","views");
app.get("/",function(req,res)
{
	newsDb.getDetails(function(rs)
	{
		if(!rs)
			res.render("showEntryDetails",{error:true});
res.render("showEntryDetails",rs);
});
});

app.get("/export_news",function(req,res)
{

});

app.get("/export_news/filter",function(req,res)
{

});

app.listen(8084,function()
{
console.log("Started");
});
