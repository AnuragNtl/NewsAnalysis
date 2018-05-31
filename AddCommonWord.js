if(process.argv.length<=2)
{
	console.log("Usage: node AddCommonWords.js \"word\"");
	process.exit();
}
var newsdb=require("./NewsDB.js");
newsdb.markWordAsCommon(process.argv[2],function(err,res)
{
if(err)
{
	console.log("Error: "+err);
}
else
{
	console.log("Completed marking \""+process.argv[2]+"\" word as common");
}
process.exit();
});
