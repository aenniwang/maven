package david.work
import java.io.{File, FileWriter}
import java.text.SimpleDateFormat
import java.util.Date

import scala.io.Source
import scala.sys.process._

/**
  * Created by wanghuaq on 7/18/2016.
  */

class CPIDataProcessing(){
    val defRootFoldData = "C:\\Working-Intel\\working\\baidu\\CPU_Custermize\\data-2nd\\output"

    def getDataFolders(root:File) :Array[File]={
        val folders=root.listFiles().filter(_.isDirectory)
        folders ++ folders.flatMap(getDataFolders(_))
    }

    def fixEmonMissue(targetFolder:File): Unit ={
        val srcEmonM="emon-M.dat"
        val destEmonM="emon-M-fix.dat"

        println(s"Fix emon issue for ${targetFolder.getName}")
       /* if(targetFolder.listFiles().map(_.getName).contains(destEmonM)){
            println(s"$destEmonM exists")
            return
        }*/

        val fDestEmonM = new FileWriter(targetFolder.getPath.concat("\\").concat(destEmonM))
        val lines = Source.fromFile(targetFolder.getPath.concat("\\").concat(srcEmonM)).getLines().toArray

        def isCPUlines(l:String):Boolean = {
            if(l.matches("\\s*\\d+.*"))
                true
            else
                false
        }

        var curProcessIndex = 0
        for(l <- lines){
            if(isCPUlines(l)) {
                val newl = l.replaceFirst("\\d+", curProcessIndex.toString)
                curProcessIndex += 1
                fDestEmonM.write(s"$newl\r\n")
            }
            else {
                fDestEmonM.write(s"$l\r\n")
            }
        }
        fDestEmonM.close()
    }

    val edpFolder="C:\\Working-Intel\\working\\baidu\\CPU_Custermize\\Intel_provide_package\\Baidu-scalability-study-updat_ b_huaqiang\\EDP"
    def copyEDPFiles(srcEdp:String,folder:File): Unit ={
        val cmdCPXML=Seq("cmd.exe","/c","copy",s"$srcEdp\\bdx-ep.xml",folder.getPath)
        val cmdCPrb=Seq("cmd.exe","/c","copy",s"$srcEdp\\edp.rb",folder.getPath)
        val cmdCPprocess=Seq("cmd.exe","/c","copy",s"$srcEdp\\process.cmd",folder.getPath)

        cmdCPXML.!
        cmdCPrb.!
        cmdCPprocess.!
    }

    def generateReport(f:File): Unit ={
        println(s"Generate report for ${f.getName}")
        val cmd=Seq("cmd.exe","/c","process.cmd")
        Process(cmd,f).!!
    }

    def updateMetricReport(strMetric:String,fData:File): Unit = {
        val edpSysSummary = "__edp_system_view_summary.csv"

        println(s"Update report for ${fData.getName}")
        if (!fData.listFiles().map(_.getName).contains(edpSysSummary)) {
            println(s"File $edpSysSummary is not found in $fData! ")
            return
        }

        var copyCount = 0
        val lines = Source.fromFile(s"${fData.getPath}\\$edpSysSummary").getLines().toArray
        for (i <- 0 until lines.length) {
            if (lines(i).matches("metric_.*"))
                copyCount = i + 1
        }
        val linesTobeCopy = lines.slice(0, copyCount)

        var emptyStr = ""
        for (i <- 1 until linesTobeCopy(0).split(',').length)
            emptyStr += ","

        val titleLine = new Array[String](1)
        titleLine(0) = s"${fData.getName}$emptyStr"

        val newLines = titleLine ++ linesTobeCopy
        val reportLines =
            if (new File(strMetric).exists) {
                val linesMetric = Source.fromFile(strMetric).getLines().toArray
                val __reportlines =
                    if (newLines.length <= linesMetric.length) {
                        val _emptyStr = emptyStr
                        val _reportLines = new Array[String](linesMetric.length)
                        for (i <- 0 until linesMetric.length) {
                            if (i < newLines.length)
                                _reportLines(i) = s"${linesMetric(i)},${newLines(i)}\r\n"
                            else
                                _reportLines(i) = s"${linesMetric(i)},${_emptyStr}\r\n"
                        }
                        _reportLines
                    } else {
                        val _reportLines = new Array[String](newLines.length)
                        val _items = linesMetric(0).split(',').length
                        var _emptyStr = ""
                        for (i <- 1 until _items)
                            _emptyStr += ","

                        for (i <- 0 until newLines.length) {
                            if (i < linesMetric.length)
                                _reportLines(i) = s"${linesMetric(i)},${newLines(i)}\r\n"
                            else
                                _reportLines(i) = s"${_emptyStr},${newLines(i)}"
                        }
                        _reportLines
                    }
                __reportlines
            } else {
                newLines.map(l=>s"$l\r\n")
            }
        val fMetricReport = new FileWriter(strMetric)
        reportLines.foreach(l => fMetricReport.write(l))
        fMetricReport.close()
    }

    def processData(folders:Array[File]): Unit ={

        val dataFmt= new SimpleDateFormat("yyyy-MM-dd_HH_mm_ss")
        val stamp=dataFmt.format(new Date())
        val metricDataFile=s"Baidu_CPI_Data-$stamp.csv"

        val strMetricData =s"$edpFolder\\$metricDataFile"

        val dataFolder = folders.filter(_.getName.matches("^baseline.*|^\\d+core.*"))
        for(targetData <- dataFolder){
            if(targetData.listFiles.map(_.getName).contains("emon-M.dat")){
                fixEmonMissue(targetData)
                copyEDPFiles(edpFolder,targetData)
                generateReport(targetData)
                updateMetricReport(strMetricData,targetData)
            }
        }
    }
}

object testProcessData{
    def main(args: Array[String]) {

        val defRootFoldData = "C:\\Working-Intel\\working\\baidu\\CPU_Custermize\\data-2nd\\output"
        val processCPI = new CPIDataProcessing()
        val folders = processCPI.getDataFolders(new File(defRootFoldData))
        processCPI.processData(folders)
    }
}