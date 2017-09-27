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
module.exports=new NewsDB("","","","");
