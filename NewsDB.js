var mysql=require("mysql");
function NewsDB(dbHost,dbUser,dbPass,db)
{
this.con=mysql.createConnection({
	host:"localhost",
	user:"root",
	password:"ASharma:",
	database:"NewsWikiFlash"
});
}
NewsDB.prototype.con=null;
NewsDB.prototype.connect=function(onConnect)
{
this.con.connect(onConnect);
}
NewsDB.prototype.fetchNews=function(date,i,srch,onFetch)
{
	/*
	create view freq as (select id,count(*) as freq 
	from nwfrec,words as w1,words as w2 where
	nwfrec.id=w1.nid  and w1.word=w2.word and w1.nid!=w2.nid group by id);
	select nwfrec.id,cntt,url,orign,whn,freq 
	from nwfrec left outer join freq
	on (nwfrec.id=freq.id) order by freq desc;
	*/
	this.con.query("select * from ranking where whn>="+(date?"'"+date+"' and whn<=date_add('"
		+date
		+"',interval "+i+" day)":"curdate()")+(srch?" and cntt like '%"+srch+"%'":""),
	function(err,res)
		{
			onFetch(err,res);
		});
}
NewsDB.prototype.getRegisteredDates=function(onFetch)
{
	this.con.query("select distinct cast(whn as date) as p from nwfrec order by p desc",
		function(err,res)
		{
			onFetch(err,res);
		});
}
NewsDB.prototype.getTopNWords=function(n,date,i,onFetch)
{
	this.con.query("select word,count(*) as s from "+
	 "words inner join nwfrec on(id=nid) and whn>="+(date?"'"+date+"' and whn<=date_add('"
		+date
		+"',interval "+i+" day)":"curdate()")
	  +" group by word order by s desc limit "+n+";",function(err,res)
	  {
	  	onFetch(err,res);
	  });
}
NewsDB.prototype.getCommonWordsList=function(onFetch)
{
	this.con.query("select * from common_words",onFetch);
}
NewsDB.prototype.markWordAsCommon=function(word,k)
{
	var tt=this;
	this.con.query("delete from words where	word='"+word+"'",function(err,res)
	{
		if(err)
		{
			k(err,res);
		}
		else
		{
			tt.con.query("insert into common_words values('"+word+"');",function(err1,res1)
			{
				k(err1,res1);
			});
		}
	});
}
NewsDB.prototype.removeCommonWord=function(word,k)
{
this.con.query("delete from common_words where word='"+word+"';",k);
}
function escapeForQuery(k)
{
	var k1="";
	for(var i=0;i<k.length;i++)
	{
		var e=k.charCodeAt(i);
		switch(e)
		{
			 case  9 : k1+="    "; break;
            case 10 : k1+="\\n"; break;
            case 13 : k1+="\\r"; break;
            case 34 : k1+="\\\""; break;
            case 39 : k1+="\\'"; break;
            case 92 : k1+="\\\\";break;
            default:
            k1+=String.fromCharCode(e);
            break;
		}
	}
	return k1;
}
NewsDB.prototype.addSource=function(url,re,k,tag,onAdd)
{
	k=escapeForQuery(k);
	re=escapeForQuery(re);
	console.log(re);
	url=escapeForQuery(url);
	console.log("insert into sources(url,re,k,tag) values('"+url+"','"+re+"','"+k+"','"+tag+"') on duplicate key update re='"+re+"',k='"+k+"';");
	this.con.query("insert into sources(url,re,k,tag) values('"+url+"','"+re+"','"+k+"','"+tag+"') on duplicate key update re='"+re+"',k='"+k+"';"
		,function(err)
		{
			onAdd(err);
		});
}
NewsDB.prototype.removeSource=function(url,onUpdate)
{
this.con.query("delete from sources where url='"+url+"';",onUpdate);
}
NewsDB.prototype.getAllTargets=function(onFetch)
{
	this.con.query("select url,'simple_source' as type from sources",onFetch);
}
NewsDB.prototype.getTarget=function(t,onFetch)
{
	this.con.query("select * from sources where url='"+t+"';",onFetch);
}
NewsDB.prototype.addJSONTarget=function(url,k,tag,onAdd)
{
		k=escapeForQuery(k);
		url=escapeForQuery(url);
	this.con.query("insert into json_sources(url,k,tag) values('"+url+"','"+k+"','"+tag+"') on duplicate key update k='"+k+"';",onAdd);
}
NewsDB.prototype.removeJSONTarget=function(url,onUpdate)
{
	this.con.query("delete from json_sources where url='"+url+"';",onUpdate);
}
NewsDB.prototype.getAllJSONTargets=function(onFetch)
{
	this.con.query("select url,'json_source' as type from json_sources;",onFetch);
}
NewsDB.prototype.getJSONTarget=function(url,onFetch)
{
	this.con.query("select * from json_sources where url='"+url+"';",onFetch);
}
NewsDB.prototype.getDetails=function(onFetch)
{
	var that=this;
	var r={};
this.con.query("select count(*) as 'total_news' from nwfrec;",function(err,res)
	{
		if(err || !res[0])
		{
			onFetch(null);
			return;
		}
		r.total_news=res[0]["total_news"];
		that.con.query("select count(*) as 'total_words' from words",function(err1,res1)
			{
				if(err1 || !res1[0])
				{
					onFetch(null);
					return;
				}
				r.total_words=res1[0]["total_words"];
				that.con.query("select count(*) as 'total_common_words' from common_words",function(err2,res2)
					{
						if(err2 || !res2[0])
							{
								onFetch(null);
								return;
							}
							r.total_common_words=res2[0]["total_common_words"];
						onFetch(r);
					});
			});
	});

}
NewsDB.prototype.exportNews=function()
{
	
}
module.exports=new NewsDB("","","","");
