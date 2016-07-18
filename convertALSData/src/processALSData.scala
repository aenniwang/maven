/**
 * Created by wanghuaq on 4/26/2016.
 */
package ALSData

import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class processALSData {
  val sourceFile="C:\\Working-Intel\\working\\maven\\convertALSData\\src\\data_test.txt"
  def parseFromFile( file:String=sourceFile)={
    val rawDataLines=Source.fromFile(file).getLines()
    val userItems=
      for(l<-rawDataLines; v=l.split(",") )
        yield(v(0).toInt,v(1).toInt,v(2).toInt)
    val arrayUserItem = userItems.toArray.sortBy(_._1)
    arrayUserItem
  }

  def splitCSRMatrix(rawArray: Array[(Int,Int,Int)], divBlocks:Int)
  :ArrayBuffer[Array[(Int,Int,Int)]]={

    var maxUserID = rawArray(rawArray.length-1)._1
    var newArray = new ArrayBuffer[Array[(Int,Int,Int)]]()
    var blkSize= maxUserID/divBlocks
    if(blkSize < 2){
      println("Too may blocks defined")
      newArray
    }

    var offset:Int=0
    var newArrayBuf=new ArrayBuffer[(Int,Int,Int)]

    for (i<-0 until rawArray.length){
      if(rawArray(i)._1<=(blkSize+offset)) {
        newArrayBuf += ((rawArray(i)._1 - offset, rawArray(i)._2, rawArray(i)._3))
      }
      else{
        newArray += newArrayBuf.toArray
        newArrayBuf.clear()
        offset+=blkSize
        if(offset == blkSize*(divBlocks-1)){
          blkSize = maxUserID - offset
        }
      }
    }
    if(newArrayBuf.nonEmpty){
      newArray += newArrayBuf.toArray
      newArrayBuf.clear()
    }
    newArray
  }

  def createCSR3Matrix(rawData:Array[(Int,Int,Int)])={
    // rawData is sorted by first element
    val CSRValues=rawData.map(_._3)
    val CSRColumns=rawData.map(_._2)
    val rows=rawData.map(_._1)

    val max=rows.last
    // CSR rows array has units of rows of matrix plus 1
    val CSRRows=new Array[Int](max+1)
    var rowIndex=0
    val CSRBase=1// one base CSR3 matrix
    var rowValue=CSRBase
    CSRRows(0)=CSRBase
    var CSRRowIndex=1
    while(rowIndex<rows.length)
    {
      if(rows(rowIndex)==(CSRRowIndex-1+CSRBase)) {
        rowValue=rowValue+1
        rowIndex =rowIndex+1
        CSRRows(CSRRowIndex)=rowValue
      }
      else{
        CSRRows(CSRRowIndex)=rowValue
        CSRRowIndex=CSRRowIndex+1
      }
    }
    (CSRValues,CSRColumns,CSRRows)
  }

  def dumpCSRMatrix(csr:(Array[Int], Array[Int], Array[Int])): Unit ={
    println("Matrix")
    print("Values : ")
    for(i<-0 until csr._1.length){
      print(csr._1(i))
      print(" ")
    }
    println("")
    print("Column : ")
    for(i<-0 until csr._2.length){
      print(csr._2(i))
      print(" ")
    }
    println("")
    print("Row : ")
    for(i<-0 until csr._3.length){
      print(csr._3(i))
      print(" ")
    }
    println("")
  }
}
