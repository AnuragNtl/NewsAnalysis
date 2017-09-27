import groovy.json.JsonSlurper;
import groovy.swing.SwingBuilder;
import groovy.util.Eval;
import org.codehaus.groovy.control.CompilationFailedException;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import regextester.*;
class RegexTest
{
static def t1,t2,l1,b1,t4;
static void main(String[] args)
{
	
		System.setProperty("https.protocols","TLSv1,TLSv1.1,TLSv1.2");
def sb=new SwingBuilder();
def f=sb.frame([title:"Regex Test",size:[600,400],location:[40,40],defaultCloseOperation:JFrame.EXIT_ON_CLOSE])
{

}
f.setLayout(new BorderLayout());
t1=new JTextArea();
t2=new JTextArea();
l1=new JList();
b1=new JButton("Test");
def p1=new JPanel();
p1.setLayout(new GridLayout(1,2));
p1.add(new JLabel("URL"));
p1.add(t1);
p1.add(new JLabel("Regex"));
p1.add(t2);
t1.setText("https://www.inshorts.com/en/read");
t2.setText("<span itemprop=\"headline\">.*</span>");
t4=new JTextArea();
p1.add(b1);
f.add(p1,BorderLayout.NORTH);
f.add(l1,BorderLayout.CENTER);
f.add(t4,BorderLayout.SOUTH);
f.show();
b1.addActionListener(new ActionListener()
{
public void actionPerformed(ActionEvent e)
{
	def k;
	try {
		k=Eval.me("def k="+t4.getText()+";\nreturn k;");
		if(!k instanceof Closure)
		return;
	}
	catch(CompilationFailedException compltnfexcepn) {
	return;	
	}
	l1.removeAll();
	URLConnection con=new URL(t1.getText()).openConnection();
	String[] r=RegexTester.listMatches(con.getInputStream(),t2.getText());
	def s=new String[r.length];
	int i=0;
	r.each
	{
		s[i]=k.call(it);
		i++;
	}
	l1.setListData(s);
}
	});
}
};
