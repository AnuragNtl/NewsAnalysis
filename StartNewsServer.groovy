evaluate(new File("./NWFConfig.groovy"));
import static NWFConfigS.*;
def p1=[dbHost,dbUser,dbPass,dbUrl,db];
def p="";
for(s in p1)
p+=s+" ";
def prss=Runtime.getRuntime().exec("node ./NewsPortal.js "+p);
prss.waitFor();
