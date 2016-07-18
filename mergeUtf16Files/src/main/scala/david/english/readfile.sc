import io.Source._
import java.io.File
import scala.util.matching.Regex

def getWords(a:List[File]): List[String] ={
  var ret=List[String]()
  for(f <- a){
    val fName=f.toString
    val content = fromFile(fName,"utf-16").mkString
    // Make one line for one Enlish record
    val MarkLR = "__ENDOFLINE"
    val subLRtoMark="""\r\n""".r
    var records=subLRtoMark.replaceAllIn(content,MarkLR)
    val subAddtoMark="""\+""".r
    val MarkAdd = "+\r\n"
    records = subAddtoMark(records, MarkAdd)

    val lines = content.getLines.toList
    ret =ret ::: lines
    println("length of list"+ ret.length)
  }
  ret
}



val folder = "C:\\Working-Intel\\working\\python\\Merge"
var f = new File(folder)
if (f.exists && f.isDirectory) {
  val files=f.listFiles().filter(_.isFile).toList
  getWords(files).length

  // Make record in one line
}

