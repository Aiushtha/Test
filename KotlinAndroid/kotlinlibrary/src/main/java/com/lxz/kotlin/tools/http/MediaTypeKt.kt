package com.lxz.kotlin.tools.http

import okhttp3.MediaType

/**
 * Created by linxingzhu on 2018/1/11.
 */
/**text/html;HTML格式*/
val MEDIATYPE_TEXT_HTML = MediaType.parse("text/html")
/**text/plain;纯文本格式*/
val MEDIATYPE_TEXT_PLAIN = MediaType.parse("text/plain")
/**text/xml;XML格式*/
val MEDIATYPE_TEXT_XML = MediaType.parse("text/xml")
/**image/gif;gif图片格式*/
val MEDIATYPE_TEXT_GIF = MediaType.parse("image/gif")
/**image/jpeg;jpg图片格式*/
val MEDIATYPE_TEXT_JPEG = MediaType.parse("image/jpeg")
/**image/png;png图片格式*/
val MEDIATYPE_TEXT_PNG = MediaType.parse("image/png")
/**application/xhtml+xml;XHTML格式*/
val MEDIATYPE_XHTML = MediaType.parse("application/xhtml+xml")
/**application/xml;XML数据格式*/
val MEDIATYPE_XML = MediaType.parse("application/xml")
/**application/atom+xml;Atom XML聚合格式*/
val MEDIATYPE_ATOM = MediaType.parse("application/atom+xml")
/**application/json;JSON数据格式*/
val MEDIATYPE_JSON = MediaType.parse("application/json")
/*application/pdf;pdf格式*/
val MEDIATYPE_PDF = MediaType.parse("application/pdf")
/**application/msword;Word文档格式*/
val MEDIATYPE_MSWORD = MediaType.parse("application/msword")
/**application/octet-stream ： 二进制流数据（如常见的文件下载）*/
val MEDIATYPE_STREAM = MediaType.parse("application/octet-stream")
/**application/x-www-form-urlencoded ： <form encType=””>中默认的encType，form表单数据被编码为key/value格式发送到服务器（表单默认的提交数据的格式）*/
val MEDIATYPE_FROM = MediaType.parse("application/x-www-form-urlencoded")