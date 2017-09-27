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
class SearchNews
{
	static def hList=[];
	static def urlList=[];
	static def curURL="";
	static def nLen=0,bRead=0;
	private static NewsDump nd=new NewsDump();
	static def searcherList=["https://www.inshorts.com/en":["<span itemprop=\"headline\">.*</span>",
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
	];
	static def stxt;
public static void main(String[] args)
{
	println(System.getProperty("https.protocols"));
	System.setProperty("https.protocols","TLSv1,TLSv1.1,TLSv1.2");
	def wait=30000,showTime=10000;
	if(args.length>0)
		wait=Integer.parseInt(args[0]);
		if(args.length>1)
		showTime=Integer.parseInt(args[1]);
	new Thread(new Runnable()
	{
		public void run()
		{
	searcherList.each
	{
		key,val->
		val[2].each
		{
	Searcher srchr=new Searcher(new URL(key+it));
	curURL=key+it;
	bRead+=srchr.searchPattern(val[0],val[1]);
		}
	}
		for(def i=0;i<hList.size();i++)
		{
	nd.addHeadLine((String)hList.get(i),urlList.get(i),urlList.get(i));
	}
}
}).start();
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
			nwf.setText("<h1>"+txt+"</h1> <span style='color:white;'>("+
				nLen+" headlines read, "+(bRead<1024?bRead+" bytes ":bRead/1024+" KBs ")+"used)</span>");
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
f.setBackground(new Color(0,0,0,0));
f.setFocusableWindowState(false);
f.setAlwaysOnTop(true);
ep=new JEditorPane();
ep.setContentType("text/html");
ep.setBackground(Color.black);
ep.setEditable(false);
f.add(ep);
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
	return new JsonSlurper().parseText(url.getText());
}
};
class NewsDump
{
private def dbCon=Sql.newInstance("jdbc:mysql://localhost:3306/NewsWikiFlash",
		"root","ASharma:","com.mysql.jdbc.Driver");
public void addHeadLine(hline,url,oUrl)
{
	def params=[hline,url,oUrl];
			def es="insert into nwfrec(cntt,url,orign) values(?,?,?)";
			try {
					dbCon.execute(es,params);
						String id=getHeadLineId(hline);
							println("Headline Id $id");
					def vals=hline.split("[^a-zA-Z]+");
					println("Headline $hline");
					for(s in vals)
					{
						def ss=(String)s;
						println(ss);
						if(ss.length()>1)
						{
						addWord(ss,id);
						println("Adding word $ss");
					}
					else
					println("Not adding $ss");
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
};
