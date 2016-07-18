//package david.scrapper

import edu.uci.ics.crawler4j.crawler.{CrawlController, CrawlConfig, Page, WebCrawler}
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.robotstxt.{RobotstxtServer, RobotstxtConfig}
import edu.uci.ics.crawler4j.url.WebURL
import org.slf4j.{LoggerFactory,Logger}

/**
 * Created by wanghuaq on 3/13/2016.
 */

class crawler extends WebCrawler{
  val baseDomain = "http://faxian.smzdm.com/"
  val itemPattern="http://www.smzdm.com/p/"
  var count = 0

  var itemsSMZDM = List[(String,String)]()

  override def getMyLocalData ={
    itemsSMZDM
  }

    override def shouldVisit(p:Page,url:WebURL):Boolean={
      val href = url.getURL.toLowerCase
      count += 1
      println(s"$count: Parsing "+url.getURL)
      href.startsWith(itemPattern)
  }

  def shouldParse(url: String): Boolean=true

  override def visit(page: Page){
    val url = page.getWebURL.getURL
    if(!shouldParse(url))return

    println(s"Parsing url: $url")
    val pageHtml = page.getParseData match {
      case data: HtmlParseData => data.getHtml
    }

    val patternTitlePre="""<h1 class=\"article_title \"> """.r
    val pageHtmlLines = pageHtml.split('\n')

    var title=""
    var price=""
    for(l <- pageHtmlLines) {

      if (patternTitlePre.findFirstIn(l) != None)
        {
          var strs = l.replaceAll("<span[^>]*>","\n").split('\n')
          title=strs(0).replaceAll("<[^>]*>","").replaceAll("\\s","")
          price=strs(1).replaceAll("<[^>]*>","").replaceAll("\\s","")
        }
    }

    if(title.nonEmpty)
      itemsSMZDM=itemsSMZDM :+ (title,price)
  }

  override def onBeforeExit()={

  }
}

object crawler{

  def main(args: Array[String]) {
    val crawlStorageFolder = "/tmp"
    val numberOfCrawlers = 10

    val config = new CrawlConfig()
    config.setCrawlStorageFolder(crawlStorageFolder)
    config.setPolitenessDelay(1000)
    config.setMaxDepthOfCrawling(1)
    config.setMaxPagesToFetch(50)
    config.setResumableCrawling(false)
    config.setProxyHost("child-prc.intel.com")
    config.setProxyPort(911)
    config.setUserAgentString("Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36")

    val pageFetcher = new PageFetcher(config)
    val robotstxtConfig = new RobotstxtConfig()
    robotstxtConfig.setEnabled(false)
    val robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher)
    val controller = new CrawlController(config, pageFetcher, robotstxtServer)
    controller.addSeed("http://faxian.smzdm.com/")

    controller.start(classOf[crawler], numberOfCrawlers)

    controller.waitUntilFinish()
    println("SMZDM Item list:")
    println("")
    var items = controller.getCrawlersLocalData().toArray()

    def dump_list(a:AnyRef) {
      var w = a.asInstanceOf[List[(String, String)]]
      w.foreach(i => println(i._1+" Price is "+i._2))
    }
    items.foreach(dump_list(_))

  }
}