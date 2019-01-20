import groovy.json.JsonSlurper;
import groovy.swing.SwingBuilder;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import javax.swing.event.*;
import java.sql.*;
import groovy.sql.Sql;
import groovy.util.Eval;
class SearchNews
{
	static def hList=[];
	static def urlList=[],oUrlList=[],extras=[];
	static def curURL="";
	static def nLen=0,bRead=0;
	private static NewsDump nd=new NewsDump();
	static def searcherList=[];/*["https://www.inshorts.com/en":["<span itemprop=\"headline\">.*</span>",
	{
		def hLine=it.replace("<span itemprop=\"headline\">","").replace("</span>","");
		hList.add(hLine);
		urlList.add(curURL);
		nLen++;	
	},
	["/read/","/read/national/","/read/business","/read/world","/read/technology","/read/startup"]
	],
	"https://www.infoworld.com/category":
	[
	"<(h1|h2|h3)>(.|\n)*?<\\/(h1|h2|h3)>",
	{
		def i1=it[0].replaceAll("<[^<]*>","").trim();
		hList.add(i1);
		urlList.add(curURL);
		nLen++;
	},
	["/application-development/","/cloud-computing/","/internet-of-things/","/database/","/it-careers/"
	,"/mobile-technology/","/networking/","/open-source-tools/","/operating-systems/","/security/","/virtualization/"]
	],
	"https://www.enggwave.com/category/off-campus-jobs":["<(h3)(.|\n)*?</(h3)>",
	{
		def i1=it[0].replaceAll("<[^<]*>","").trim();
		hList.add(i1);
		urlList.add(curURL);
		nLen++;
		},
		["/","/page/2","/page/3","/page/4","/page/5"]
		]
	];*/
	static def stxt;
public static void main(String[] args)
{
	def tag="";
	System.setProperty("https.protocols","TLSv1,TLSv1.1,TLSv1.2");
	def wait=30000,showTime=10000;
	if(args.length==0)
	return;
	else if(args.length>0)
	tag=args[0];
	if(args.length>1)
		wait=Integer.parseInt(args[1]);
		if(args.length>2)
		showTime=Integer.parseInt(args[2]);
		searcherList=nd.getBots(tag);
	new Thread(new Runnable()
	{
		public void run()
		{
	searcherList["sources"].each
	{
	Searcher srchr=new Searcher(new URL(it["url"]));
	def k=Eval.me("def k="+it["k"]+";\nreturn k;");
	bRead+=srchr.searchPattern(it["re"])
	{
		uText->
		def lb=hList.size();
		try
		{
		k.call(uText,hList,urlList,extras);
		}
		catch(Exception e)
		{
			k.call(uText,hList,urlList);
		}
		def ub=hList.size();
		if(ub>lb)
		oUrlList.add(it["url"]);
		nLen++;
	}
	}
	searcherList["json_sources"].each
	{
		try {
		def resp=Searcher.fetchJson(new URL(it["url"]));
		def k=Eval.me("def k="+it["k"]+";\nreturn k;");
		def lb=hList.size();
		try
		{
		k.call(resp,hList,urlList,extras);
		}
		catch(Exception e)
		{
			k.call(uText,hList,urlList);
		}
		def ub=hList.size();
		for(def i=lb;i<ub;i++)
		{
			oUrlList.add(it["url"]);
			nLen++;
		}
	}
	catch(Exception e)
	{
		println("Cannot fetch from ${it['url']}");
	}
		//bRead+=resp.length();
	}
		for(def i=0;i<hList.size();i++)
		{
			def e=null;
			if(extras.size()>i)
			e=extras.get(i);
	nd.addHeadLine((String)hList.get(i),urlList.get(i),oUrlList.get(i),tag,e);
	}
}
}).start();
if(!GraphicsEnvironment.isHeadless())
{
	NewsWikiFlash nwf=new NewsWikiFlash();
	def vManager=VoiceManager.getInstance();
	def voice=vManager.getVoice("kevin");
	voice.allocate();
	while(true)
	{
		try{Thread.sleep(wait);}catch(InterruptedException intrrpdexcepn){}
		if(hList.size()<2)
		continue;
		def p=(int)(Math.random()*hList.size());
		def txt="<a style='color:white;text-decoration:none;' href='"+urlList.get(p)+"'>"+hList.get(p)+"</a>";
		def extra=(p<extras.size() && extras.get(p)!=null?extras.get(p):"");
			nwf.setText("<h1>"+txt+"</h1> <span style='color:white;'>("+
				nLen+" headlines read, "+(bRead<1024?bRead+" bytes ":bRead/1024+" KBs ")+"used)</span>"+"<div style='color:white'>$extra</div>");
println extra
		stxt=txt.replaceAll("&[^;]*;","").replaceAll("<[^<]*>","");
		new Thread(new Runnable()
			{
				public void run()
				{
							voice.speak(stxt);
			}
			}).start();
		nwf.flash(showTime);
	}
}
}
};
public class NewsWikiFlash
{
	def ep,f;
public NewsWikiFlash()
{
def sb=new SwingBuilder();
f=sb.frame(title:"",location:[0,0],size:[1024,122],defaultCloseOperation:WindowConstants.EXIT_ON_CLOSE)
{

};
def gc=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds();
def w=gc.getWidth(),h=gc.getHeight();
f.setSize((int)w,(int)(h/10));
f.setUndecorated(true);
//f.setBackground(new Color(0,0,0,0));
f.setFocusableWindowState(false);
f.setAlwaysOnTop(true);
ep=new JEditorPane();
ep.setContentType("text/html");
ep.setBackground(Color.black);
ep.setEditable(false);
f.add(new JScrollPane(ep));
f.setVisible(false);
ep.addHyperlinkListener(new HyperlinkListener()
{
	public void hyperlinkUpdate(HyperlinkEvent e)
	{
		if(e.getEventType()==HyperlinkEvent.EventType.ACTIVATED)
		{
		URL url1=e.getURL();
		Desktop d1=null;
		if(Desktop.isDesktopSupported())
		d1=Desktop.getDesktop();
		else
		return;
		if(d1!=null)
		{
			if(d1.isSupported(Desktop.Action.BROWSE))
			{
				try
				{
				d1.browse(url1.toURI());
			}
			catch(IOException ioexcepn){}
			}
		}
	}
	}
	});
ep.addKeyListener(new KeyListener()
{
public void keyPressed(KeyEvent e){}
public void keyReleased(KeyEvent e)
{
if(e.getKeyCode()==KeyEvent.VK_ENTER)
f.setVisible(false);
}
public void keyTyped(KeyEvent e){}
	});
}
public def setText(txt)
{
	ep.setText(txt);
}
public def flash(millis)
{
f.setVisible(true);
try
{
	Thread.sleep(millis);
}
catch(InterruptedException intrrpdexcepn){}
f.setVisible(false);
}
};
class Searcher
{
private URL url;
public Searcher(URL url)
{
	this.url=url;
}
public def searchPattern(pat,eachPattern)
{
	def rLen=0;
	try
	{
		def txt=url.getText([requestProperties:[Host:url.getHost(),"User-Agent":"NewsWikiFlash"]]);
		rLen=txt.length();
	txt.eachMatch(pat,eachPattern);
	}
	catch(Exception e)
	{
		System.err.println(e.toString());
	}
	return rLen;
}
public static def fetchJson(url)
{
	def txt=url.getText();
	println("Cannot fetch from $url");
	return new JsonSlurper().parseText(txt);
}
};
class NewsDump
{
private def dbCon=Sql.newInstance("jdbc:mysql://localhost:3306/NewsWikiFlash",
		"root","ASharma:","com.mysql.jdbc.Driver");
public void addHeadLine(hline,url,oUrl,tag,extras)
{
	def params=[hline,url,oUrl,extras,tag];
			def es="insert into nwfrec(cntt,url,orign,extras,tag) values(?,?,?,?,?)";
			try {
					dbCon.execute(es,params);
						String id=getHeadLineId(hline);
						//	println("Headline Id $id");
					def vals=hline.split("[^a-zA-Z]+");
					//println("Headline $hline");
					for(s in vals)
					{
						def ss=(String)s;
					//	println(ss);
						if(ss.length()>1)
						{
						addWord(ss,id);
					//	println("Adding word $ss");
					}
					//else
					//println("Not adding $ss");
					}
			}
			catch(Exception e) {
				println(e.toString());
			}
		
}
public String getHeadLineId(hline)
{
	String id=null;
dbCon.eachRow("select id from nwfrec where cntt=$hline")
{
id=it[0];
}
return id;
}
public def addWord(word,nid)
{
	word=(String)word;
	nid=Integer.parseInt((String)nid);
	def params=[nid,word,word];
	println("Inserting word ($word,$nid)");
dbCon.execute(" insert into words"+
	" select w,? from (select (?) as w) as s where"+
	" (select count(*) from common_words where word=?)=0;",params);
}
public def getBots(tag)
{
	def srcs=["sources":[],"json_sources":[]];
	try
	{
	dbCon.eachRow("select * from sources where tag=$tag")
	{
		srcs["sources"].add(["url":it[0],"re":it[1],"k":it[2],"tag":it[3]]);
	}
	dbCon.eachRow("select * from json_sources where tag=$tag")
	{
		srcs["json_sources"].add(["url":it[0],"k":it[1],"tag":it[2]]);
	}
}
catch(Throwable t)
{
	println t.getMessage();
}
	return srcs;
}
};
