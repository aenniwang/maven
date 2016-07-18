package david.english

import io.Source._
import java.io.{PrintWriter, File}

/**
 * @author ${user.name}
 */
object App {


def getWords(a:List[File]): Array[String] ={
  var ret=Array[String]()
  for(f <- a){
    val fName=f.toString
    var content = fromFile(fName,"utf-16").mkString
    if(content(0)=='+'){
      println("First character of "+fName.toString+"is +")
      content = content.substring(1)
    }
    val records=content.split('+')
    ret = ret++records
  }
  ret
}
  def saveToFiles(words:Array[String]): Unit ={
    var fileIndex = 0
    val wordsPerFile = 100
    val wordsInFiles=words.toList.grouped(wordsPerFile).toList
    for (  fileToContent <- wordsInFiles){
      val fileName="EnglishWords_"+fileIndex+".txt"
      try {
        val fw = new PrintWriter(new File(fileName), "utf-16le")
        fw.write(0xfeff)
        for (w <- fileToContent) {
          fw.write('+')
          fw.write(w)
        }
        fw.close

        fileIndex = fileIndex + 1
      }
      catch{
        case _=>
          println("Error in wrting file "+fileName)
          return
      }
    }
  }

  def main(args : Array[String]) {
    println( "Hello World!" )

    def getMsgs(f:File):Array[String]={
      if (f.exists && f.isDirectory) {
        val files=f.listFiles().filter(_.isFile).toList
        getWords(files)
      }
      else {
        Array[String]()
      }
    }

    val folder = "C:\\Working-Intel\\working\\python\\output\\ciba-cet6-utf8-oneline"
    var englishWords=getMsgs(new File(folder))

    println(englishWords.length)
    saveToFiles(englishWords)
  }

}
