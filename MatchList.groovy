import groovy.util.Eval;
import java.sql.*;
import groovy.sql.*;
import groovy.json.JsonSlurper;
import groovy.json.JsonOutput;
static void main(String[] args)throws IOException
{
	boolean json_source=false;
	def url,r;
	if(args[0]=="simple_src")
	{
		if(args.length<3)
		return;
	url=args[1];
	r=args[2];
	}
	else if(args[0]=="json_src")
	{
		if(args.length<2)
		return;
		url=args[1];
		json_source=true;
	}
	else
	return;
	def rd=new BufferedReader(new InputStreamReader(System.in));
	def n="";
	def s=rd.readLine();
	while(s!=null)
	{
		n=n+s+System.lineSeparator();
	s=rd.readLine();
	}
	while(s!=null);
	def k=null;
	try
	{
	k=Eval.me("def k="+n+";\n return k;");
	}
	catch(MissingPropertyException msngpexcepn)
	{
		println(JsonOutput.toJson([error:true,message:msngpexcepn.toString()]));
		return;
	}
	def con=new URL(url).openConnection();
	con.setRequestProperty("User-Agent","Mozilla/5.0 (X11; Ubuntu; Linux i686; rv:60.0) Gecko/20100101 Firefox/60.0");
	def txt=con.getInputStream().getText();
	def hList=[],urlList=[],extras=[];
	if(json_source)
	{
		txt=new JsonSlurper().parseText(txt);
		k.call(txt,hList,urlList,extras);
	}
	else
	{
	txt.eachMatch(r)
	{
		k.call(it,hList,urlList,extras);
	}
	}
	def p=["headline_list":hList,"urlList":urlList,"extras":extras];
	println(JsonOutput.toJson(p));
}
