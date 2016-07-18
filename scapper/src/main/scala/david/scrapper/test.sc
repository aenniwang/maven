
import edu.uci.ics.crawler4j.crawler.{CrawlController, CrawlConfig, Page, WebCrawler}
import edu.uci.ics.crawler4j.fetcher.PageFetcher
import edu.uci.ics.crawler4j.parser.HtmlParseData
import edu.uci.ics.crawler4j.robotstxt.{RobotstxtServer, RobotstxtConfig}
import edu.uci.ics.crawler4j.url.WebURL

/**
 * Created by wanghuaq on 3/13/2016.
 */

class crawler extends WebCrawler{
  val searchPattern=".*(\\.(png|jpg|png))$"
  val baseDomain = "http://www.newsmth.net/nForum/#!article/ITExpress/1626815"

  override def shouldVisit(referringPage: Page, url: WebURL) {
    val href = url.getURL.toLowerCase()
    href.startsWith(baseDomain) && !href.matches(searchPattern)
  }

  def shouldParse(url: String): Boolean=true

  override def visit(page: Page){
    val url = page.getWebURL.getURL
    println("Fetched url:"+url)
    if(!shouldParse(url))return

    println("Parsing url: $url")
    val pageHtml = page.getParseData match {
      case data: HtmlParseData => data.getHtml
    }
    println(pageHtml)
  }

  override def onBeforeExit()={

  }
}

object crawler{

  def main(args: Array[String]) {
    val config = new CrawlConfig()

    config.setMaxDepthOfCrawling(2)
    config.setMaxPagesToFetch(10)
    config.setIncludeBinaryContentInCrawling(false)
    config.setResumableCrawling(false)

    val pageFetcher = new PageFetcher(config)
    val robotstxtConfig = new RobotstxtConfig
    val robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher)
    val controller = new CrawlController(config, pageFetcher, robotstxtServer)

    controller.addSeed("http://www.newsmth.net/nForum/#!board/ITExpress")
    controller.start(classOf[crawler], 10)

  }
}