import java.time.LocalDateTime

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.linalg.distributed.RowMatrix
import org.apache.spark.streaming.Seconds
import org.apache.spark.{SparkConf, SparkContext}

import scala.sys.process._
/**
  * Created by wanghuaq on 7/15/2016.
  */

/*
Application Starting method: YARN cluster

 */
object testsvd {
    def main(args: Array[String]) {

        if(args.length < 1){
            println("Usage: testsvd <master> <csvfile>")
            return
        }

        val master = args(0)

        println("Spark MLlib SVD Algorithm Test")

        val osName = System.getProperty("os.name")
        val conf = new SparkConf().setAppName("svdTest").setMaster(master)
        val sc = new SparkContext(conf)

        val defSrcFile =
            if (osName.indexOf("Win") == 0)
                "file:/C:\\Working-intel\\working\\baidu\\CPU_Custermize\\data-2nd\\core_scaling\\svd_1.csv"
            else
                "/data/mllib/svd_1.csv"
        val srcFile=
            if(args.length>1)
            args(1)
        else
            defSrcFile

        val data = sc.textFile(srcFile).map(_.split(',').map(_.toDouble)).map(Vectors.dense)
        val numPart = data.count()
        //println(s"data length is $numPart")

        val mat = new RowMatrix(data)

        val testlength=20 until numPart.toInt
        for(i <- testlength) {
            val timeStart = System.currentTimeMillis()

            val svd = mat.computeSVD(i, true)
            val svdsize = svd.s.size

            val timeStop = System.currentTimeMillis()
            val seconds = (timeStop - timeStart) / 1000
            println(s"===> size: $svdsize, cost $seconds seconds")
        }
    }
}

object testSvdWithYarn{
    def main(args: Array[String]) {
        println("Calling testSvd with Yarn")
        var sparkRoot = System.getProperty("sparkroot")
        if(sparkRoot==null){
            sparkRoot="C:\\Working-Intel\\spark-1.6.2-bin-hadoop2.6"
            println("Please set spark root folder by -Dsparkroot=\"<your spark home>\"")

        }
        println(s"sparkroot = $sparkRoot")

        val sparkSubmit =
            if(System.getProperty("os.name").indexOf("Win")==0)
                s"$sparkRoot\\bin\\spark-submit.cmd"
            else
                s"$sparkRoot/bin/spark-submit"

        val sparkParaClass = "testsvd"
        val sparkParaMaster = "spark://localhost:7077"
        val sparkParaMode = "client"
        val appJar = "testmllibsvd-1.0-jar-with-dependencies.jar"
        val appPara0 = "yarn-client"
        val cmd = Seq(sparkSubmit,
            "--class", sparkParaClass,
            "--master",sparkParaMaster,
            "--deploy-mode",
            sparkParaMode,appJar,appPara0)
        cmd!

     //   val parameters =(s"$sparkroot\\spark-submit"，
      //  ""
      //      )
    }
}