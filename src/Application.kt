package com.zlz

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import kotlinx.html.*
import kotlinx.css.*
import io.ktor.gson.*
import io.ktor.features.*
import com.fasterxml.jackson.databind.*
import com.zlz.intf.*
import io.ktor.jackson.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.sessions.*
import io.ktor.sessions.cookie
import java.io.File
import java.text.DateFormat
import kotlin.collections.HashMap

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    install(DefaultHeaders)
    install(Compression)
    install(CallLogging)
    install(ContentNegotiation) {
        gson {
            setDateFormat(DateFormat.LONG)
            setPrettyPrinting()
        }

        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }
    data class SampleSession(val name: String, val value: Int)
    install(Sessions) {
        cookie<SampleSession>(
            "SESSION_FEATURE_SESSION_ID",
            directorySessionStorage(File(".sessions"), cached = true)
        ) {
            cookie.path = "/" // Specify cookie's path '/' so it can be used in the whole site
        }
    }

    val client by lazy { HttpClient(Apache) }

//    routing {
//        get("/") {
//            call.respondText("HELLO WORLD!", contentType = ContentType.Text.Plain)
//        }
//
//        get("/html-dsl") {
//            call.respondHtml {
//                body {
//                    h1 { +"HTML" }
//                    ul {
//                        for (n in 1..10) {
//                            li { +"$n" }
//                        }
//                    }
//                }
//            }
//        }
//
//        get("/styles.css") {
//            call.respondCss {
//                body {
//                    backgroundColor = Color.red
//                }
//                p {
//                    fontSize = 2.em
//                }
//                rule("p.myclass") {
//                    color = Color.blue
//                }
//            }
//        }
//
//        get("/json/gson") {
//            call.respond(mapOf("hello" to "world"))
//        }
//
//        get("/json/jackson") {
////            call.respondText(""" {"'hello'":"world"} """,ContentType.Application.Json)
//            call.respondTextWriter(ContentType.Application.Json, HttpStatusCode.Accepted) {
//                this.write("""{"demo":"demo"}""")
//            }
//        }
//    }


    routing {
        get("/music/{id}") {
            call.response.header("Access-Control-Allow-Origin","*")


            val id = call.parameters["id"] ?: ""
            val map = HashMap<String,Any>()
            getMusicListDetails(map,id)
            call.respond(map)
        }

        get("/music/lrc/{id}"){
            call.response.header("Access-Control-Allow-Origin","*")

            val id = call.parameters["id"] ?: ""
            println(id)
            val map = HashMap<String,Any>()
            getMusicLyric(id, map)
            call.respond(map)
        }

        get("/login/callphone/{loginMassage}"){
            call.response.header("Access-Control-Allow-Origin","*")

            println(call.response.headers["Origin"])

            val loginMassage  = call.parameters["loginMassage"] ?: ""
            println(loginMassage)
            val map = HashMap<String,Any>()
            loginWithCallPhone(map,loginMassage)
            call.respond(map)
        }

        get("/code/{qq}"){
            call.response.header("Access-Control-Allow-Origin","*")
            val qq  = call.parameters["qq"] ?: ""
            println(qq)
            sendCode(qq)
            val map = HashMap<String,Any>()
            map["code"] = 200
            map["msg"] = "验证码发送成功"
            call.respond(map)
        }

        post("/test"){
            call.response.header("Access-Control-Allow-Origin","*")
//            println(Date().time)
//            println(call.sessions.toString())
            val map = HashMap<String,Any>()
            map["code"] = 200
            call.respond(map)
            val mySession: SampleSession? = call.sessions.get<SampleSession>()
            println(call.response.cookies["name"])

        }
    }
}

fun FlowOrMetaDataContent.styleCss(builder: CSSBuilder.() -> Unit) {
    style(type = ContentType.Text.CSS.toString()) {
        +CSSBuilder().apply(builder).toString()
    }
}

fun CommonAttributeGroupFacade.style(builder: CSSBuilder.() -> Unit) {
    this.style = CSSBuilder().apply(builder).toString().trim()
}

suspend inline fun ApplicationCall.respondCss(builder: CSSBuilder.() -> Unit) {
    this.respondText(CSSBuilder().apply(builder).toString(), ContentType.Text.CSS)
}
