/**
 * Created by wanghuaq on 3/3/2016.
 */
package david
import java.io.{PrintWriter, File}
import edu.mit.jwi.item.POS

import io.Source._

import edu.mit.jwi._
import edu.mit.jwi.morph.WordnetStemmer


import com.itextpdf.text.DocumentException
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor

import scala.collection.mutable.HashMap
import collection.mutable.Set
import scala.collection.JavaConversions._

class wordNLP {
  val fDist = new File("./dict")
  val dict = new Dictionary(fDist)
  dict.open()
  val stemmer=new WordnetStemmer(dict)

  def getBaseForm(word:String):String = {
    val ret=
    try {
      val root0 = stemmer.findStems(word, null)
      if (root0.isEmpty)
        "BADWORD"
      else{
        val root=stemmer.findStems(root0.get(0),null)
        if (root0.get(0) == root.get(0))
          root.get(0)
        else
          "BADWORD"

      }
    }
    catch{
      case _ =>
        "BADWORD"
    }

    //if(word == "insomenumber" || ret=="insomenumber")
     //println(word + " => " +ret)
    ret
  }

  def test()={
    println("dogs: "+getBaseForm("dogs"))
    println("prepared: "+getBaseForm("prepared"))
    println("went: "+getBaseForm("went"))
    println("lkjalkdsjfa: "+getBaseForm("lkjalkdsjfa"))
    println("11: "+getBaseForm("11"))
    println("ter: "+getBaseForm(("ter")))
    println("teb: "+getBaseForm(("teb")))
    println("insomenumbers: "+getBaseForm(("insomenumbers")))
    println("insomenumber: "+getBaseForm(("insomenumber")))

    //val words = parseTXTFile("C:\\Working-Intel\\working\\maven\\mergeUtf16Files\\CET6-m1-words.txt")
    //words.foreach(println)

    //val pdfFile="C:\\Working-Intel\\ARM\\1.pdf"
    //val pdfFile="C:\\Working-Intel\\book\\Programming in Scala  2nd Edition.pdf"
    val txtFile="C:\\Working-Intel\\working\\python\\output\\DesktopVoc\\托福单词-words.txt"
    val dictFolder="C:\\Working-Intel\\working\\python\\output\\ciba-cet6"
    val exWords = getExclusiveWords(txtFile, dictFolder)
    saveToFile(exWords,"PDFFile.txt")
    println("Exclusive words: "+exWords.size)
    //exWords.foreach(println)
  }

  def saveToFile(data:Array[String],fName:String): Unit ={
    var fTobeWrite = new PrintWriter(fName)
    data.map(l=>l+"\n").foreach(fTobeWrite.write)
    fTobeWrite.close()
  }

  def parseTXTFile(file:String):Array[String]={
    val wSet = Set.empty[String]
    /*
    var t1=fromFile(file).getLines().toList
    t1=t1.map(_.replaceAll("\\n-",""))
    t1=t1.map( _.replaceAll("\\W"," "))
    t1=t1.map( _.replaceAll("\\b*\\d\\w*\\b",""))
    t1=t1.map( _.replaceAll("\\s+"," "))
    t1=t1.map( _.replaceAll("_", " "))
    t1.map( _.toLowerCase).map(wSet.add)
    */
    val lines = fromFile(file).getLines().toList
      .map(
        _.replaceAll("\\n-", "")
          .replaceAll("\\W", " ")
          .replaceAll("\\b*\\d\\w*\\b", "")
          .replaceAll("\\s+", " ")
          .replaceAll("_", " ")
          .toLowerCase)

    lines.map(_.split(' ').toArray).reduce(_ ++ _).foreach(wSet.add)
    wSet.add("lkjslkjflasjdlkfa")
    println("Before getBase, num="+wSet.size)
    var words = wSet.toArray
    words.map(getBaseForm)

    words
  }

  def parsePDFFile(file:String):Array[String]={
    val reader = new PdfReader(file)
    val num = reader.getNumberOfPages()
    val pdfContent =
     for {
       i <- 1 to num
       nText = PdfTextExtractor.getTextFromPage(reader, i)
         .replaceAll("\\n-","").replaceAll("\\W"," ")
         .replaceAll("\\b*\\d\\w*\\b","")
         .replaceAll("\\s+"," ")
         .replaceAll("_"," ")
         .toLowerCase()
     } yield nText

    val wSet = Set.empty[String]
    if(!pdfContent.isEmpty)
       pdfContent.map(_.split(' ')).reduce(_ ++ _).map(wSet.add)
    wSet.map(getBaseForm)

    wSet.toArray
  }

  def createEnglishKnownLib(folderName:String):Array[String] = {
    val folder = new File(folderName)

    val dictSet = Set.empty[String]
    val filenames =folder.listFiles().filter(_.isFile).toList
    val libdicts=
    for (f <- filenames){
    /* Required for dictionary file: one word in one line */
      fromFile(f).getLines().toList.map(_.replaceAll("[\\.|\\s]","")).map(dictSet.add)
    }
    dictSet.toArray
  }

  def getExclusiveWords(sourceFile:String, dictFolder:String):Array[String]={
    try {
      val wDict = createEnglishKnownLib(dictFolder)
      val wPDF = parseTXTFile(sourceFile)
      println("Dictionary words: "+wDict.size)
      println("PDF words: "+wPDF.size)
      val exclusiveWords =
      for{
        w <- wPDF
        if(w.length>4)
        if (!wDict.contains(w))
      }yield w
      exclusiveWords
    }
    catch{
      case _=>
        println("Create excluding words failed")
        new Array[String](0)
    }
  }
}
