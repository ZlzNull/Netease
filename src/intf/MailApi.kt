package com.zlz.intf

import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.insert
import me.liuwj.ktorm.dsl.select
import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.varchar
import java.math.BigInteger
import java.util.*
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Message
import javax.mail.Session
import javax.mail.internet.*

fun sendMail(receiveMail:String,code:String){
    val sendMail = "1163452838@qq.com"
    val password = "fifzgjpmbwmnbaai"
    val props = Properties()
    props.setProperty("mail.smtp.ssl.enable","true")
    //需要请求认证
    props.setProperty("mail.smtp.auth","true")
    //使用的协议（JavaMail规范要求）
    props.setProperty("mail.transport.protocol","smtp")
    //发件人的邮箱的 SMTP 服务器地址
    props.setProperty("mail.smtp.host","smtp.qq.com")

    props.setProperty("mail.smtp.port","465")
    props.setProperty("mail.smtp.socketFactory.class","javax.net.ssl.SSLSocketFactory")
    props.setProperty("mail.smtp.socketFactory.fallback","false")
    props.setProperty("mail.smtp.socketFactory.port","465")

    val session = Session.getInstance(props)

//    session.debug = true

    val message = createMimeMessage(session,sendMail,"$receiveMail@qq.com",code)

    val transport = session.transport

    transport.connect(sendMail,password)

    transport.sendMessage(message,message.allRecipients)

    transport.close()
}

fun createMimeMessage(session: Session,sendMail:String,receiveMail:String,code:String):MimeMessage{

    val message = MimeMessage(session)

    message.setFrom(InternetAddress(sendMail,"西邮派","UTF-8"))

    message.addRecipient(Message.RecipientType.TO, InternetAddress(receiveMail,"我的测试邮件_收件人昵称","UTF-8"))

    message.setSubject("西邮派邮箱验证邮件", "UTF-8")

//    val image = MimeBodyPart()
//    image.dataHandler = DataHandler(FileDataSource("G:\\Picture\\周静怡.png"))
//    image.contentID = "image_fairy_tail"

    val text = MimeBodyPart()
    text.setContent("您的验证码为<br/><h1>$code</h>", "text/html;charset=UTF-8")

//    val mm_text_image = MimeMultipart()
//    mm_text_image.addBodyPart(text)
//    mm_text_image.addBodyPart(image)
//    mm_text_image.setSubType("related")

//    val text_image = MimeBodyPart()
//    text_image.setContent(mm_text_image)

//    val attachment = MimeBodyPart()
//    val dh = DataHandler(FileDataSource("G:\\房屋租赁合同.docx"))
//    attachment.dataHandler = dh
//    attachment.fileName = MimeUtility.encodeText(dh.name)

    val mm = MimeMultipart()
    mm.addBodyPart(text)
//    mm.addBodyPart(attachment)
//    mm.addBodyPart(attachment)
    mm.setSubType("mixed")

    message.setContent(mm)

    message.sentDate = Date()

    message.saveChanges()

    return message
}

fun saveCode(qq:String,code:String){
    Database.connect("jdbc:mysql://139.159.236.48:3306/ktorm",driver = "com.mysql.cj.jdbc.Driver",user = "root", password = "Whyzshngrd1@my")

    QQtoCode.insert {
        it.qq to qq
        it.code to code
    }

}

//描述数据库表
object QQtoCode:Table<Nothing>("qq_to_code"){
    val qq by varchar("qq").primaryKey()
    val code by varchar("code")
    val time by long("time")
}

fun test(){
    Database.connect("jdbc:mysql://139.159.236.48:3306/ktorm",driver = "com.mysql.cj.jdbc.Driver",user = "root", password = "Whyzshngrd1@my")

    QQtoCode.insert {
        it.qq to "30269744"
        it.code to "456321"
        it.time to Date().time
    }
    for(row in QQtoCode.select()){
        println(row[QQtoCode.qq] + " | " + row[QQtoCode.code] + " | " + row[QQtoCode.time])
    }
}


fun sendCode(qq:String){
    //初始化一个标志，表示该QQ是否被注册
    var flag = false
    //判断该QQ是否已被注册，如被注册返回false，未被注册返回true
    flag = findQQ(qq)

    var code = ""

    for(i in 1..6){
        code += (0..9).random()
    }

    if(flag){
        sendMail(qq,code)
        saveCode(qq,code)
    }
}

fun findQQ(qq:String):Boolean{
//    return false
    Date().time
    return true
}
