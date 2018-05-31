var fs=require("fs");
var express=require("express");
var bodyParser=require("body-parser");
var cookieParser=require("cookie-parser");
var session=require("express-session");
var childProcess=require("child_process");
var stream=require("stream");
var fs=require("fs");
//var mongoDb=require("mongodb");
var db=require("./NewsDB.js");
var app=express();
var bParser=bodyParser.urlencoded({extended:true});
app.use(bParser);
app.use(bodyParser.json());
app.use(cookieParser());
app.use(session({secret:"NWFServer!@#%#$^%$&%^*"}));
app.use("/",express.static("NWFServer"));
app.set("view engine","pug");
app.set("views","views");
app.get("/",function(req,res)
{
	var date=req.query.date,srch=req.query.srch,i1=(parseInt(req.query.p)?parseInt(req.query.p):1);
db.fetchNews(date,i1,srch,function(err,r1)
{
	db.getRegisteredDates(function(err4,dates)
	{
	var groupedNews={};
	db.getTopNWords(20,date,i1,function(err2,res2)
	{
		if(err2)
		{
			res.end(err2.toString());
			return;
		}
	for(i in r1)
	{
		var news=r1[i];
		if(!groupedNews[news.orign])
			groupedNews[news.orign]=new Array();
			groupedNews[news.orign].push([news.cntt,news.url,news.freq?news.freq:0]);
	}
	//console.log(groupedNews);
res.render("nwpaper",{hlines:groupedNews,tags:res2,dates:dates,srch:(srch?srch:""),
	date:date?date:"",i1:(i1?i1:1)});
});
});
});
});
app.get("/words/:word",function(req,res)
{
var word=req.params.word;
res.render("words",{word:word});
});
app.post("/manage_word",function(req,res)
{
var word=req.body.t1;
if(!req.body.rd1)
{
res.send("__");
return;
}
if(req.body.rd1=="r1")
db.markWordAsCommon(word,function(err,res1)
{
if(!err)
	res.redirect("/");
else
	res.send("Error in Adding Common Word");
});
else
db.removeCommonWord(word,function(err,res2)
{
if(!err)
	res.redirect("/");
else
	res.send("Error in Removing Common Word");
});
});
app.get("/addTarget",function(req,res)
{
res.render("addTarget",{url:"",re:"",k:"{k,hList,urlList->\n}",tag:"daily_news"});
});
app.get("/addJSONTarget",function(req,res)
{
res.render("addJSONTarget",{url:"",k:"{response,hList,urlList->\n}",tag:"daily_news"});
});
app.post("/addJSONTarget",function(req,res)
{
var url=req.body.url,k=req.body.k,tag=req.body.tag;
var id=req.sessionID;
fs.writeFileSync(id,k);
execTestScript(false,url,null,id,function(data)
	{
		if(data==null)
			res.send("Error in Parsing Output");
		else if(data.error)
			res.send(data.message);
		else
		{
			data["url"]=url;
			data["k"]=k;
			data.tag=tag;
			res.render("listtestwords1",data);
		}
		fs.unlinkSync(id);
	});
});

app.post("/addJSONTargetConfirm",function(req,res)
{
	var url=req.body.url,k=req.body.k,tag=req.body.tag;
	db.addJSONTarget(url,k,tag,function(err)
	{
		if(err)
			res.send("Error: "+err);
		else
		{
			res.redirect("/");
		}
	})
});

app.post("/addTarget",function(req,res)
{
var url=req.body.url,re=req.body.re,k=req.body.k,tag=req.body.tag;
var id=req.sessionID;
fs.writeFileSync(id,k);
execTestScript(true,url,re,id,function(data)
	{
		if(data==null)
			res.send("Error in Parsing Output");
		else if(data.error)
			res.send(data.message);
		else
		{
			data["url"]=url;
			data.re=re;
			data["k"]=k;
			data.tag=tag;
			res.render("listtestwords",data);
		}
		fs.unlinkSync(id);
	});
});
app.post("/add_target_confirm",function(req,res)
{
var url=req.body.url,re=req.body.re,k=req.body.k,tag=req.body.tag;
db.addSource(url,re,k,tag,function(err)
{
if(err)
	res.send("Error "+err);
else
	res.redirect("/");
});
});
app.get("/viewCommonWords",function(req,res)
{
db.getCommonWordsList(function(err,data)
{
if(err)
	res.send("Error Occurred");
else
{
res.render("commonWords",{words:data});	
}
});
});
app.get("/viewTargets",function(req,res)
{
db.getAllTargets(function(err,data)
{
if(err)
	res.send("Error Occurred");
else
db.getAllJSONTargets(function(err,data1)
{
	data=data.concat(data1);
	res.render("viewTargets",{targets:data});
});
});
});
app.get("/target",(req,res)=>
{
var t=req.query.t;
if(!t)
{
	res.redirect("/");
	return;
}
var type=req.query.type;
if(type=="simple_source")
{
db.getTarget(t,function(err,data)
{
if(err || !data || !data[0])
	res.send("Error Occurred");
else
	res.render("addTarget",{url:data[0].url,re:data[0].re,k:data[0].k,tag:data[0].tag});
});
}
else
{
	db.getJSONTarget(t,function(err,data)
	{
		if(err)
			res.send("Error Occurred");
		else
			res.render("addJSONTarget",{url:data[0].url,k:data[0].k,tag:data[0].tag});
	});
}
});
db.connect(function(err)
{
if(err)
{
	console.log(err);
	return;
}
app.listen(8082,function()
{
console.log("Started");
});
});

/////////////////////////////////////////
/*var mongoClient=mongoDb.MongoClient;
var db;
mongoClient.connect("mongodb://localhost:27017/NewsYard",function(err,db1)
	{
		if(err)
			console.log(err);
	app.listen(8082,function()
{
	db=db1;
console.log("started");
});
	
	});
function getTodayNews(k)
{
	var today=new Date();
	var newsList=[];
var cursor=db.collection("headlines").find({date:dateFormat(today)})
cursor.each(function(err,doc)
	{
		if(doc==null)
			{
				k(newsList);
				return;
			}
			if(doc)
				newsList.push(doc);
	});
}*/
function dateFormat(dt)
{
return ((parseInt(dt.getMonth())+1)+"/"+dt.getDate()+"/"+dt.getFullYear());
}
function execTestScript(simpleSrc,url,re,kFile,onExec)
{
console.log("Exec ");
var child=childProcess.exec("groovy MatchList.groovy "+(simpleSrc?"simple_src":"json_src")+" '"+url+"'"+(simpleSrc?" '"+re+"'":"")+" < "+kFile,
	function(err,stdout,stderr)
	{
		var data="";
	try
	{
		console.log(stdout);
		console.log(stderr);
data=JSON.parse(stdout);
}
catch(e)
{
	onExec(null);
	return;
}
onExec(data);
});
console.log("E");
}
