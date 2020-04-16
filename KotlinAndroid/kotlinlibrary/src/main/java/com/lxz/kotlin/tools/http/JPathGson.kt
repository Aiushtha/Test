package com.lxz.kotlin.tools.http

import com.google.gson.Gson
import com.jayway.jsonpath.JsonPath
import org.lxz.utils.log.L
import java.lang.reflect.Type
import java.util.ArrayList

/**
 * Created by linxingzhu on 2018/1/9.
 */

object JPathGson {

    fun <T> fromJson(json: String, jpath: String?, cla: Class<T>): T {
        val nodeJson: String
        if (jpath == null) {
            nodeJson = json
        } else {
            nodeJson = JsonPath.parse(json).read<Any>(jpath).toString()
        }
        L.d(nodeJson)
        return getGson().fromJson(nodeJson, cla)
    }

    fun <T> fromJson(json: String, jpath: String?, cla: Type): T? {
        val nodeJson: Any
        if (jpath == null) {
            return getGson().fromJson<T>(json, cla)
        } else {
            try {
                nodeJson = JsonPath.parse(json).read(jpath)
            } finally {

            }
        }
        return getGson().fromJson<T>(getGson().toJson(nodeJson), cla)
    }


    fun getGson(): Gson {
        return Gson()
    }

    fun readJsonPath(json: String, jPath: String?): String {
        return if (jPath == null || "" == jPath || "$." == jPath) json else JsonPath.parse(json).read<Any>(jPath).toString()
    }
}