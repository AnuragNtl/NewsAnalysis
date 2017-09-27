package regextester;
import java.io.*;
import java.util.*;
import java.lang.String.*;
import java.lang.Math.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.*;
import java.applet.Applet.*;
import java.net.*;
import java.beans.*;
import javax.imageio.*;
import java.util.regex.*;
import java.sql.*;
public class RegexTester
{
public static String[] listMatches(InputStream in,String srch)throws IOException
{
	RegexGlobalMatchesLister rLister=new RegexGlobalMatchesLister(in);
	return rLister.find(srch);
}
public static String[] listMatches(String src,String srch)throws IOException
{
	RegexGlobalMatchesLister rLister=new RegexGlobalMatchesLister(src);
	return rLister.find(srch);	
}
};
class RegexGlobalMatchesLister
{
private BufferedReader br1;
public RegexGlobalMatchesLister(String src)
{
br1=new BufferedReader(new StringReader(src));
}
public RegexGlobalMatchesLister(InputStream in)
{
	this.br1=new BufferedReader(new InputStreamReader(in));
}
public String[] find(String s)throws IOException
{
String rd1=null;
String r="";
ArrayList<String> r1=new ArrayList<String>();
do
{
	if(rd1!=null)
	r=r+rd1;
rd1=br1.readLine();
if(rd1!=null)
r=r+"\n";
}
while(rd1!=null);
Pattern p=Pattern.compile(s);
Matcher matcher=p.matcher(r);
matcher.useTransparentBounds(true);
while(matcher.find())
{
r1.add(matcher.group());
}
return r1.toArray(new String[0]);
}
};
