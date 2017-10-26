import groovy.util.Eval;
import java.sql.*;
import groovy.sql.*;
import groovy.json.JsonSlurper;
import groovy.json.JsonOutput;
static void main(String[] args)throws IOException
{
	if(args.length<2)
	return;
	def url=args[0];
	def r=args[1];
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
	def txt=new URL(args[0]).getText();
	def hList=[],urlList=[];
	txt.eachMatch(r)
	{
		k.call(it,hList,urlList);
	}
	def p=["headline_list":hList,"urlList":urlList];
	println(JsonOutput.toJson(p));
}
