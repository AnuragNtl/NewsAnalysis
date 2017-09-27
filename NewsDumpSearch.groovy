/*
create table nwfrec(id int auto_increment,cntt varchar(180),url varchar(1000),orign varchar(1000),whn timestamp,primary key(id));
*/
/*import static NWFConfigS.*;
import java.sql.*;
import java.io.*;
import groovy.sql.Sql;
evaluate(new File("./NWFConfig.groovy"));
		def nds=new NewsDumpSearcher();
		println(args);
public class NewsDumpSearcher
{
	private def dBase;
	public NewsDumpSearcher()
	{	
		println("Host="+dbHost);
		dBase=Sql.newInstance(dbUrl,dbUser,dbPass,"com.mysql.jdbc.Driver");
	}
	private def searchFor(s)
	{
		println("Searching for $s");
		def r=[:];
		dBase.eachRow("select id,cntt,url,whn from nwfrec where cntt like '%"+s+"%' or url like '%"+s+"%' order by orign")
		{
			r.put("id",it[0]);
			r.put("content",it[1]);
			r.put("url",it[2]);
			r.put("when",it[3]);
		}
		return r;
	}
	private def addNews(cntt,url)
	{
		dBase.execute("insert into nwfrec(cntt,url) values(\"$cntt\",\"url\")");
	}
	private def close()
	{
		dBase.close();
	}
};
*/
import com.mongodb.*;
class NWS{
static void main(String[] args)
{
def k=new MongoClient("localhost",27017);
DB s=k.getDB("t1");
def cursor=s.getCollection("colT1").find();
while(cursor.hasNext())
println(cursor.next());
}
}
