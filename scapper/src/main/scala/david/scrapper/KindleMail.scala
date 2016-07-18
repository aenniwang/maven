package david.scrapper

import java.util.Properties
import javax.mail.Session


　

/**
 * Created by wanghuaq on 3/26/2016.
 */
class KindleMail extends Thread {
  val mailFrom="theponcho@163.com"
  val mailUser="theponcho"
  val mailPassword="genniwang016"
  val mailHost="smtp.163.com"
  val result:HtmlResult

  override def run: Unit ={
    try{
      val prop = new Properties()
      prop.setProperty("mail.host", mailHost)
      prop.setProperty("mail.transport.protocal","smtp")
      prop.setProperty("mail.smtp.auth","true")

      val session = Session.getInstance(prop)
      val ts=session.getTransport()
      ts.connect(mailHost,mailUser,mailPassword)
      MailData md = new MailData(result)
    }
  }
}
