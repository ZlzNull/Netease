package com.zlz.intf

import Bean.MusicAndLyric.*
import com.google.gson.Gson
import okhttp3.*

val client by lazy { OkHttpClient() }

fun getMusicListDetails(map:HashMap<String,Any>,id:String){
    val request = Request.Builder()
        .url("http://139.159.236.48:3000/playlist/detail?$id")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if (response.isSuccessful){
        map["code"] = 200
        val result = response.body()?.string()
        println("result = " + result?.length)
        val list = Gson().fromJson(result, PlayListDetail::class.java)
        println("code = " + list.code)
        getMusicUrl(list, map)
    }
}

fun getMusicUrl(list: PlayListDetail, map:HashMap<String,Any>){
    val tracks = list.playlist.tracks
    val arr = ArrayList<Music>()
    for (i in tracks){
        val request = Request.Builder()
            .url("http://139.159.236.48:3000/song/url?id=${i.id}")
            .get()
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful){
            val result = response.body()?.string()
            println("result = " + result?.length)
            val temp = Gson().fromJson(result, SongUrl::class.java)
            println("code = " + temp.data[0].url)
            if(temp.data[0].url.isNotEmpty()){
                arr.add(Music(i.name, i.ar[0].name, i.al.picUrl, temp.data[0].url, i.id))
            }
        }
    }
    map["url"] = arr
    map["num"] = arr.size
}

fun getMusicLyric(id:String,map: HashMap<String, Any>){

    map["code"] = 200
    if(id.isEmpty()){
        map["state"] = false
    }else{
        val arr = ArrayList<Lyric>()
        val request = Request.Builder()
            .url("http://139.159.236.48:3000/lyric?$id")
            .get()
            .build()

        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val result = response.body()?.string()
            println("result = " + result?.length)
            val temp = Gson().fromJson(result, LyricAll::class.java)
            println("code = " + temp.code)
            toLyric(temp.lrc.lyric, arr)
            map["state"] = true
            map["lrc"] = arr
        }else{
            map["state"] = true
        }
    }
}

fun toLyric(str:String,arr:ArrayList<Lyric>){
    val temp = str.split("[")
    println(temp.size)
    temp.forEach {
        println("It = $it")
        if(it.isNotEmpty()){
            val a = it.split("]")
            if(a[0][0] in '0'..'9'){
                val time = getTime(a[0])
                println("a = {" + a[0] + "} | time = {" + time + "} | lrc = {" + a[1] + "}")
                var s = a[1]
                if (s == "\n"){
                    s = "(music)"
                }
                arr.add(Lyric(time, s))
            }else{
                println("a = null")
            }
        }
    }
    println(temp[0])
}

fun getTime(time:String):Int{
    val min = time.substring(0,2).toInt() * 60 * 1000
    val sec = time.substring(3,5).toInt() * 1000
    var mil = time.substring(6,time.length).toInt()
    if (mil < 100){
        mil *= 10
    }

    return min + sec + mil
}