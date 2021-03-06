package me.leon


object WenShu {


    @JvmStatic
    fun main(args: Array<String>) {

        getList()

        getDetail()

    }


    fun getList() {
        for (i in 1..1) {
            getDocList(i)
            Thread.sleep(3000)
        }
    }

    private fun getDocList(page: Int = 1, pageSize: Int = 20) {
        val params = mapOf(
                "pageNum" to page.toString(),
                "pageSize" to pageSize.toString(),
                "sortFields" to "s50:desc",
                "ciphertext" to Cipher.binary(),
                "devid" to "23a9c9828da443abbcfa8ab452201faa",
                "devtype" to 1.toString(),
                "queryCondition" to mutableListOf(QueryCondition("s8", "03"))
        )

        val q = mapOf(
                "id" to Cipher.stamp,
                "command" to "queryDoc",
                "params" to params
        )


        val result = OkHttpUtils.getInstance()
                .postString(
                        "http://wenshuapp.court.gov.cn/appinterface/rest.q4w",
                        "request=${q.toJson().b64()}",
                        WenShuRsp::class.java, null
                )

        val decryptTxt = Encrypt.desDecrypt(result.data.content, result.data.secretKey)
        //这里是解密后的结果,
        println(decryptTxt)

        val r = decryptTxt.fromJson<QueryList>()



        r.queryResult.resultList.also { println(it) }
                .map { it.casename }.let(::println)

        //自己根据 结果的docId 进行解析
//        r.queryResult.resultList.also { println(it) }
//                .map { getDetail(it.docId) }
    }


    fun getDetail(docId: String = "318e78a15f0e4d088a8aab9d00c350ec") {
        val params = mapOf(
                "ciphertext" to Cipher.binary(),
                "docId" to docId,
                "devid" to "23a9c9828da443abbcfa8ab452201faa", "devtype" to 1.toString()
        )

        val q = mapOf(
                "id" to Cipher.stamp,
                "command" to "docInfoSearch",
                "cfg" to "com.lawyee.judge.dc.parse.dto.SearchDataDsoDTO@docInfoSearch",
                "params" to params
        )


        val detail = OkHttpUtils.getInstance()
                .postString(
                        "http://wenshuapp.court.gov.cn/appinterface/rest.q4w ",
                        "request=${q.toJson().b64()}"
                        , WenShuRsp::class.java, null
                )


        val detailTxt = Encrypt.desDecrypt(detail.data.content, detail.data.secretKey)
        println(detailTxt)
        println(detailTxt.fromJson<DocDetail>())

    }

}
