package com.zlz.intf

import Bean.FindMusicList.FindMusicList
import Bean.FindMusicList.MusicList
import Bean.LoginCallPhone.LoginCallPhone
import com.google.gson.Gson
import okhttp3.Request

fun loginWithCallPhone(map:HashMap<String,Any>,loginMassage:String){
    val request = Request.Builder()
        .url("http://139.159.236.48:3000/login/cellphone?$loginMassage")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if(response.isSuccessful){
        map["code"] = 200
        val result = response.body()?.string()
        val list = Gson().fromJson(result, LoginCallPhone::class.java)
        map["id"] = list.account.id
        println("LoginWithCallPhone's code = " + list.code)
        findMusicList(map,list.account.id)
    }
}

fun findMusicList(map:HashMap<String,Any>,id:Int){
    val request = Request.Builder()
        .url("http://139.159.236.48:3000/user/playlist?uid=$id")
        .get()
        .build()

    val response = client.newCall(request).execute()
    if(response.isSuccessful){
        val arr = ArrayList<MusicList>()
        val result = response.body()?.string()
        val list = Gson().fromJson(result,FindMusicList::class.java)
        val temp = list.playlist
        temp.forEach {
            arr.add(MusicList(it.name,it.id))
        }
        map["musicList"] = arr
        map["num"] = arr.size
    }
}