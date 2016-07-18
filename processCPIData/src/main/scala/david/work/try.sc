import java.io.{File, FileWriter}

import com.sun.org.apache.bcel.internal.classfile.SourceFile

import scala.io.Source
import scala.sys.process._

val rootFoldData = "C:\\Working-Intel\\working\\baidu\\CPU_Custermize\\data-2nd\\output"


// List all folder name
val fRootData = new File(rootFoldData)
if(!fRootData.exists() || !fRootData.isDirectory){
    println(s"$rootFoldData is not folder")
    //return
}

def recursiveListFolder(f:File) :Array[File] ={
    val foldersDir=f.listFiles.filter(_.isDirectory)
    foldersDir ++ foldersDir.flatMap(recursiveListFolder)
}

var files =recursiveListFolder(fRootData)
files = files.filter(_.getName.matches("^\\d+core.*|^baseline.*"))

files(0).getPath

def run_script_bdx(f:File): Unit = {
    println(s"Processing ${f.getName}")
    val cmd=Seq("cmd.exe","/c" ,"process-bdx.cmd")
    val a=Process(cmd,f).!!
    a.foreach(println)
}

val emonM=files(5).getPath.concat("\\emon-M.dat")

val emonMdata = Source.fromFile(emonM).getLines().toArray

def processLine(l:String) : Int={
    val ret =
        if(l.matches("^\\s*\\d+.*"))
            1
        else
            0
    ret
}

val cpu_num=emonMdata.map(processLine).sum

println(s"cpu number is $cpu_num")

val newEmonMFile= "emon-M-modi.txt"

val outputEmon = new FileWriter(files(5).getPath.concat("\\").concat(newEmonMFile))
val newEmonF= new File(newEmonMFile)
println(s" Files status${newEmonF.isFile}")
var curProcessorIndex:Int = 0
for(l <- emonMdata) {
    if (1 == processLine(l)) {
        val newl = l.replaceFirst("\\d+", curProcessorIndex.toString)
        curProcessorIndex = curProcessorIndex + 1
        outputEmon.write(newl)
        outputEmon.write("\r\n")

    }
    else
        outputEmon.write(l + "\r\n")
}
outputEmon.close()


// read csv file
//val edpSummery= new File(files(5).getPath.concat("\\__edp_system_view_summary.csv"))
//val edplines=Source.fromFile(edpSummery).getLines().toArray

//run_script_bdx(files(1))
