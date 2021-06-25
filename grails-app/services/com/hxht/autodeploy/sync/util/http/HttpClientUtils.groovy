package com.hxht.autodeploy.sync.util.http

import cn.hutool.core.date.DateUtil
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.hxht.techcrt.utils.MD5Utils
import org.apache.http.HttpEntity
import org.apache.http.HttpStatus
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.CloseableHttpResponse
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.CloseableHttpClient
import org.apache.http.impl.client.HttpClients
import org.apache.http.util.EntityUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class HttpClientUtils {
    public static Logger logger = LoggerFactory.getLogger(HttpClientUtils.class)

    static JSONArray httpPost(String url) {
        // post请求返回结果
        def httpClient = HttpClients.createDefault()
        JSONObject jsonResult = null
        HttpPost httpPost = null
        JSONArray array = null
        try {
            httpPost = new HttpPost(url)
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000).setConnectTimeout(5000).build()
            httpPost.setConfig(requestConfig)
            JSONObject body = new JSONObject()
            /*是否为排期法庭
            是排期法庭：1
            非排期法庭：-1
            所有法庭：0*/
            body.put("ScheduleTrialCourt", 1)
            //设置头部信息
            String userid = "kjft"
            String password = "kjft_2E7ADA25"
            httpPost.setHeader("userid", userid)
            httpPost.setHeader("password", password)
            String timestamp = System.currentTimeMillis() + ""
            httpPost.setHeader("timestamp", timestamp)
            httpPost.setHeader("Content-Type", "application/json")
            httpPost.setHeader("charset", "UTF-8")//设置编码
            String s = "kjftkjft_2E7ADA25" + timestamp + JSON.toJSONString(body)
            httpPost.setHeader("sign", MD5Utils.encryption(s))

            // 解决中文乱码问题
            StringEntity entity = new StringEntity(JSON.toJSONString(body), "utf-8")
          /*  logger.info("对接罗湖接口请求体body=" + JSON.toJSONString(body))
            logger.info("对接罗湖接口sign=" + MD5Utils.encryption(s))*/
            entity.setContentEncoding("UTF-8")
            entity.setContentType("application/json")
            httpPost.setEntity(entity)
            CloseableHttpResponse result = httpClient.execute(httpPost)
            //请求发送成功，并得到响应
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                logger.info("对接罗湖接口与对方通讯已经成功----------------------")
                String str = ""
                //读取服务器返回过来的json字符串数据
                str = EntityUtils.toString(result.getEntity(), "utf-8")
                //把json字符串转换成json对象
                jsonResult = JSONObject.parseObject(str)
                int code = jsonResult.getIntValue("resCode")
                if (code == 200) {
                    //对方处理成功
//                    logger.info("对接罗湖接口法庭接口返回的数据:" + str)
                    array = jsonResult.getJSONArray("resData")
                } else {
                    //处理失败
                    logger.info("对接罗湖接口对方处理失败:" + jsonResult.getString("resTips"))
                }

            } else {
                logger.error("对接罗湖接口法庭接口请求不通")
            }
        } catch (IOException e) {
            logger.error("对接罗湖接口法庭请求出错:{}", e.getMessage())
        } finally {
            httpPost.releaseConnection()

        }
        array = array == null ? new JSONArray() : array
        return array
    }


    /**
     * post请求传输String参数
     * 例如：name=Jack&sex=1&type=2
     * Content-type:application/x-www-form-urlencoded
     *
     * @param url
     * @return
     */
    static JSONArray httpPost(String url, String courtRoomName) {
        // post请求返回结果
        CloseableHttpClient httpClient = HttpClients.createDefault()
        JSONObject jsonResult = null
        HttpPost httpPost = null
        JSONArray array = null
        try {
            httpPost = new HttpPost(url)
            // 设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom()
                    .setSocketTimeout(5000).setConnectTimeout(5000).build()
            httpPost.setConfig(requestConfig)
            JSONObject body = new JSONObject()
            def nowDate = DateUtil.beginOfDay(new Date())//当前时间0点
            body.put("startDate", DateUtil.format(nowDate as Date, "yyyy-MM-dd"))
            body.put("endDate", DateUtil.offsetDay(nowDate, 15))//获取15天的数据
            body.put("deptName", "")
            String[] arg = new String[1]
            arg[0] = courtRoomName
            body.put("trialCourtName", arg)
            //设置头部信息
            String userid = "kjft"
            String password = "kjft_2E7ADA25"
            httpPost.setHeader("userid", userid)
            httpPost.setHeader("password", password)
            String timestamp = System.currentTimeMillis() + ""
            httpPost.setHeader("timestamp", timestamp)
            httpPost.setHeader("Content-Type", "application/json")
            httpPost.setHeader("charset", "UTF-8")//设置编码
            String s = "kjftkjft_2E7ADA25" + timestamp + JSON.toJSONString(body)
            httpPost.setHeader("sign", MD5Utils.encryption(s))

            // 解决中文乱码问题
            StringEntity entity = new StringEntity(JSON.toJSONString(body), "utf-8")
            entity.setContentEncoding("UTF-8")
            entity.setContentType("application/json")
            httpPost.setEntity(entity)
            CloseableHttpResponse result = httpClient.execute(httpPost)
            /*logger.info("对接罗湖接口请求体body=" + JSON.toJSONString(body))
            logger.info("对接罗湖接口sign=" + MD5Utils.encryption(s))*/
            //请求发送成功，并得到响应
            if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                logger.info("对接罗湖接口与对方通讯已经成功----------------------")
                String str = ""
                //读取服务器返回过来的json字符串数据
                str = EntityUtils.toString(result.getEntity(), "utf-8")
                //把json字符串转换成json对象
                jsonResult = JSONObject.parseObject(str)
                int code = jsonResult.getIntValue("resCode")
                if (code == 200) {
                    //对方处理成功
//                    logger.info("对接罗湖接口排期接口返回的数据:" + str)
                    array = jsonResult.getJSONArray("resData")
                } else {
                    //处理失败
                    logger.info("对接罗湖接口对方处理失败:" + jsonResult.getString("resTips"))
                }

            } else {
                logger.error("对接罗湖接口排期接口请求不通")
            }
        } catch (IOException e) {
            logger.error("对接罗湖接口排期请求出错:{}", e.getMessage())
        } finally {
            if (httpPost != null) {
                httpPost.releaseConnection()
            }
        }
        array = array == null ? new JSONArray() : array
        return array
    }

    /**
     * 发送get请求
     *
     * @param url 路径
     * @return
     */
    static JSONObject httpGet(String url) {
        // get请求返回结果
        JSONObject jsonResult = null
        CloseableHttpClient client = HttpClients.createDefault()
        // 发送get请求
        HttpGet request = new HttpGet(url)
        // 设置请求和传输超时时间
        RequestConfig requestConfig = RequestConfig.custom()
                .setSocketTimeout(2000).setConnectTimeout(2000).build()
        request.setConfig(requestConfig)
        try {
            CloseableHttpResponse response = client.execute(request)

            //请求发送成功，并得到响应
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                //读取服务器返回过来的json字符串数据
                HttpEntity entity = response.getEntity()
                String strResult = EntityUtils.toString(entity, "utf-8")
                //把json字符串转换成json对象
                jsonResult = JSONObject.parseObject(strResult)
            } else {
                //logger.error("get请求提交失败:" + url)
            }
        } catch (IOException e) {
            // logger.error("get请求提交失败:" + url, e)
        } finally {
            request.releaseConnection()
        }
        return jsonResult
    }




    public static String plans="{\"resCode\":200,\"resTips\":\"\",\"resData\":[{\"pqid\":\"0c294d5084f84add9389ee78fd11a4e0\",\"ktrq\":\"2020-05-21\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"09:30:00\",\"jssj\":\"10:30:00\",\"sjy\":{\"id\":\"10195\",\"name\":\"叶雄\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初8258号\",\"laay\":\"合同纠纷\",\"cbr\":\"陈婉丹\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"熊辉\"},{\"ssdw\":\"被告\",\"name\":\"深圳广田集团股份有限公司\"},{\"ssdw\":\"被告\",\"name\":\"广田控股集团有限公司\"}],\"larq\":\"2020/3/12 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"13c02bb195be48ec9e5639ca7a553fb1\",\"ktrq\":\"2020-05-27\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"09:30:00\",\"jssj\":\"10:30:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初11896号\",\"laay\":\"侵害商标权纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳市亚洲眼镜光学科技有限公司\"},{\"ssdw\":\"被告\",\"name\":\"中威亚洲眼镜（深圳）有限公司\"}],\"larq\":\"2020/4/7 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"189b1a87010f492595b743daa99cde49\",\"ktrq\":\"2020-05-26\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"14:30:00\",\"jssj\":\"16:00:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},{\"roleName\":\"承办人\",\"userId\":\"10185\",\"userName\":\"王鸥\"},{\"roleName\":\"承办人\",\"userId\":\"10186\",\"userName\":\"杨玲\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初3605号\",\"laay\":\"侵害商标权纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"赢点创业孵化器（深圳）有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市罗湖区尚品饮品店\"}],\"larq\":\"2020/1/10 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"},{\"ah\":\"（2020）粤0303民初3607号\",\"laay\":\"侵害商标权纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"赢点创业孵化器（深圳）有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市罗湖区尚品饮品店\"}],\"larq\":\"2020/1/10 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"18c3f1a9b2154ae7869b43ca0831b3a2\",\"ktrq\":\"2020-06-01\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"14:30:00\",\"jssj\":\"16:00:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},{\"roleName\":\"承办人\",\"userId\":\"20233\",\"userName\":\"梁运芬\"},{\"roleName\":\"承办人\",\"userId\":\"20143\",\"userName\":\"黄平\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2019）粤0303民初33781号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"邱茂庭（广州）餐饮管理有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市罗湖区口口爽饮品店\"}],\"larq\":\"2019/10/16 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"1bd29839eeff4ee3a5631b6a992f7267\",\"ktrq\":\"2020-05-25\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"10:30:00\",\"jssj\":\"12:00:00\",\"sjy\":{\"id\":\"10194\",\"name\":\"曾艺\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"可开\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初8395号\",\"laay\":\"金融借款合同纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳农村商业银行股份有限公司\"},{\"ssdw\":\"被告\",\"name\":\"何仁旺\"},{\"ssdw\":\"被告\",\"name\":\"陈初枝\"}],\"larq\":\"2020/3/12 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"1e30fb04d04d47c9b66459b83ed6b1de\",\"ktrq\":\"2020-05-27\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"10:30:00\",\"jssj\":\"12:00:00\",\"sjy\":{\"id\":\"10190\",\"name\":\"赖安琪\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"鉴定笔录\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2019）粤0303民初32144号\",\"laay\":\"保证保险合同纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"中国人民财产保险股份有限公司深圳市分公司\"},{\"ssdw\":\"被告\",\"name\":\"赖飞达\"}],\"larq\":\"2019/9/20 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"2007b2b65e0f4dc69f9214e34afaac12\",\"ktrq\":\"2020-05-26\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"16:00:00\",\"jssj\":\"18:00:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},{\"roleName\":\"承办人\",\"userId\":\"10185\",\"userName\":\"王鸥\"},{\"roleName\":\"承办人\",\"userId\":\"10186\",\"userName\":\"杨玲\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初3605号\",\"laay\":\"侵害商标权纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"赢点创业孵化器（深圳）有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市罗湖区尚品饮品店\"}],\"larq\":\"2020/1/10 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"},{\"ah\":\"（2020）粤0303民初3607号\",\"laay\":\"侵害商标权纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"赢点创业孵化器（深圳）有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市罗湖区尚品饮品店\"}],\"larq\":\"2020/1/10 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"2b1bc1eb793c42478da5fc53eabb038d\",\"ktrq\":\"2020-06-01\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"10:30:00\",\"jssj\":\"12:00:00\",\"sjy\":{\"id\":\"10194\",\"name\":\"曾艺\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"可开\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初12063号\",\"laay\":\"侵害商标权纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"晶华宝岛（北京）眼镜有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市罗湖区名岛眼镜店\"}],\"larq\":\"2020/4/7 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"2c1bdcf3452745889dbbd01962d2c5b4\",\"ktrq\":\"2020-05-26\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"09:30:00\",\"jssj\":\"10:30:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初10058号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳市佐卡伊电子商务有限公司\"},{\"ssdw\":\"被告\",\"name\":\"东莞市长安陌尚饰品厂\"}],\"larq\":\"2020/3/25 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"2c76a3fb6d5648369880a070ee99e651\",\"ktrq\":\"2020-06-02\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"14:30:00\",\"jssj\":\"16:00:00\",\"sjy\":{\"id\":\"10194\",\"name\":\"曾艺\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"},{\"roleName\":\"承办人\",\"userId\":\"20135\",\"userName\":\"黄居庭\"},{\"roleName\":\"承办人\",\"userId\":\"20431\",\"userName\":\"谢榆灏\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初5799号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"王可伟\"},{\"ssdw\":\"被告\",\"name\":\"深圳市当代东方刺绣艺术研究中心\"}],\"larq\":\"2020/1/17 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"},{\"ah\":\"（2020）粤0303民初5804号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"王可伟\"},{\"ssdw\":\"被告\",\"name\":\"深圳市当代东方刺绣艺术研究中心\"}],\"larq\":\"2020/1/17 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"},{\"ah\":\"（2020）粤0303民初5803号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"王可伟\"},{\"ssdw\":\"被告\",\"name\":\"深圳市当代东方刺绣艺术研究中心\"}],\"larq\":\"2020/1/17 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"},{\"ah\":\"（2020）粤0303民初5801号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"王可伟\"},{\"ssdw\":\"被告\",\"name\":\"深圳市当代东方刺绣艺术研究中心\"}],\"larq\":\"2020/1/17 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"},{\"ah\":\"（2020）粤0303民初5800号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"王可伟\"},{\"ssdw\":\"被告\",\"name\":\"深圳市当代东方刺绣艺术研究中心\"}],\"larq\":\"2020/1/17 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"},{\"ah\":\"（2020）粤0303民初5802号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"王可伟\"},{\"ssdw\":\"被告\",\"name\":\"深圳市当代东方刺绣艺术研究中心\"}],\"larq\":\"2020/1/17 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"3445789dc2d44951804c967ec8fd8ed4\",\"ktrq\":\"2020-05-26\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"10:30:00\",\"jssj\":\"12:00:00\",\"sjy\":{\"id\":\"10190\",\"name\":\"赖安琪\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"可开庭\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10186\",\"userName\":\"杨玲\"},{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"},{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初842号\",\"laay\":\"侵害商标权纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳中国国际旅行社有限公司\"},{\"ssdw\":\"被告\",\"name\":\"国旅（深圳）国际旅行社有限公司旗舰营业部\"},{\"ssdw\":\"被告\",\"name\":\"国旅（深圳）国际旅行社有限公司\"},{\"ssdw\":\"被告\",\"name\":\"北京百度网讯科技有限公司\"}],\"larq\":\"2020/1/3 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"4cc19ebb6d1944b4946bf7f640ab2747\",\"ktrq\":\"2020-06-02\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"16:00:00\",\"jssj\":\"18:00:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"0\",\"name\":\"\"},\"pqbz\":\"\",\"hyt_List\":[],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[]},{\"pqid\":\"5556ed3985584b85be79e2fd33768ae1\",\"ktrq\":\"2020-06-05\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"14:30:00\",\"jssj\":\"16:00:00\",\"sjy\":{\"id\":\"10192\",\"name\":\"李慧\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初14430号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"苏宏\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"罗伊视效动漫有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市罗湖区金枫叶文具商店\"}],\"larq\":\"2020/4/21 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"572b52a4c27c49d390605a566cb88193\",\"ktrq\":\"2020-06-02\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"10:30:00\",\"jssj\":\"12:00:00\",\"sjy\":{\"id\":\"10194\",\"name\":\"曾艺\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"0\",\"name\":\"\"},\"pqbz\":\"\",\"hyt_List\":[],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[]},{\"pqid\":\"75e20932f6f747c49c2766770ffdf78d\",\"ktrq\":\"2020-05-25\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"16:00:00\",\"jssj\":\"18:00:00\",\"sjy\":{\"id\":\"10192\",\"name\":\"李慧\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初10889号\",\"laay\":\"民间借贷纠纷\",\"cbr\":\"苏宏\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳市诚信典当有限公司\"},{\"ssdw\":\"被告\",\"name\":\"林丽娜\"},{\"ssdw\":\"被告\",\"name\":\"钟运昌\"}],\"larq\":\"2020/4/7 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"8475f06f6bcc4ce19b9db1f918076a6f\",\"ktrq\":\"2020-05-28\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"10:30:00\",\"jssj\":\"12:00:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},{\"roleName\":\"承办人\",\"userId\":\"10186\",\"userName\":\"杨玲\"},{\"roleName\":\"承办人\",\"userId\":\"10185\",\"userName\":\"王鸥\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初1172号\",\"laay\":\"金融借款合同纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"宁波银行股份有限公司深圳罗湖支行\"},{\"ssdw\":\"被告\",\"name\":\"曾三峰\"},{\"ssdw\":\"被告\",\"name\":\"李姗徽\"}],\"larq\":\"2020/1/6 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"},{\"ah\":\"（2020）粤0303民初1173号\",\"laay\":\"金融借款合同纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"宁波银行股份有限公司深圳罗湖支行\"},{\"ssdw\":\"被告\",\"name\":\"潘小寨\"},{\"ssdw\":\"被告\",\"name\":\"陈韶辉\"}],\"larq\":\"2020/1/6 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"8a1ef2d90c734f4888e3840f4825ea03\",\"ktrq\":\"2020-06-05\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"09:30:00\",\"jssj\":\"10:30:00\",\"sjy\":{\"id\":\"10192\",\"name\":\"李慧\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"},{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"},{\"roleName\":\"承办人\",\"userId\":\"10185\",\"userName\":\"王鸥\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2019）粤0303民初32776号\",\"laay\":\"民间借贷纠纷\",\"cbr\":\"苏宏\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"曾维\"},{\"ssdw\":\"被告\",\"name\":\"吴云峰\"}],\"larq\":\"2019/9/29 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"},{\"ah\":\"（2019）粤0303民初32777号\",\"laay\":\"民间借贷纠纷\",\"cbr\":\"苏宏\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"曾维\"},{\"ssdw\":\"被告\",\"name\":\"戴小平\"}],\"larq\":\"2019/9/29 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"8a3821c3a25f460d88135217e42bc46b\",\"ktrq\":\"2020-06-03\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"14:30:00\",\"jssj\":\"16:00:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"监狱开庭\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10185\",\"userName\":\"王鸥\"},{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},{\"roleName\":\"承办人\",\"userId\":\"10186\",\"userName\":\"杨玲\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初1989号\",\"laay\":\"财产损害赔偿纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"吕利民\"},{\"ssdw\":\"被告\",\"name\":\"李兵\"}],\"larq\":\"2020/1/7 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"9383c60440064fb6b98d2304a8fdf0ef\",\"ktrq\":\"2020-06-03\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"10:30:00\",\"jssj\":\"12:00:00\",\"sjy\":{\"id\":\"10194\",\"name\":\"曾艺\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初13797号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"苏宏\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"邱茂庭（广州）餐饮管理有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市罗湖区马记白鹿亭奶茶店\"}],\"larq\":\"2020/4/17 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"97a492af8783433fab7b68c0d23a4d15\",\"ktrq\":\"2020-06-04\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"09:30:00\",\"jssj\":\"10:30:00\",\"sjy\":{\"id\":\"10192\",\"name\":\"李慧\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初13658号\",\"laay\":\"民间借贷纠纷\",\"cbr\":\"苏宏\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"陈泽璇\"},{\"ssdw\":\"被告\",\"name\":\"林海丰\"}],\"larq\":\"2020/4/16 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"97f4082b878d4fbe91719b98a9e67235\",\"ktrq\":\"2020-05-29\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"14:30:00\",\"jssj\":\"16:00:00\",\"sjy\":{\"id\":\"10192\",\"name\":\"李慧\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初10888号\",\"laay\":\"民间借贷纠纷\",\"cbr\":\"苏宏\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"孙晨\"},{\"ssdw\":\"被告\",\"name\":\"吕应微\"}],\"larq\":\"2020/4/7 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"a064a5559ff64c21ad5a7638c2ddb566\",\"ktrq\":\"2020-05-28\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"14:30:00\",\"jssj\":\"16:00:00\",\"sjy\":{\"id\":\"10190\",\"name\":\"赖安琪\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"普通，独任，可开\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初10040号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳市佐卡伊电子商务有限公司\"},{\"ssdw\":\"被告\",\"name\":\"泉州市珂蒂丽贸易有限公司\"}],\"larq\":\"2020/3/25 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"a2e4a79dad69425bbb0c5479da1f09be\",\"ktrq\":\"2020-06-04\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"14:30:00\",\"jssj\":\"16:00:00\",\"sjy\":{\"id\":\"10190\",\"name\":\"赖安琪\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\" 无纸化，可开\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初12759号\",\"laay\":\"房屋租赁合同纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"惠州市伟迪富电子有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市阿卡索资讯股份有限公司\"}],\"larq\":\"2020/4/14 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"a399bbc20c154d27937e4727dc2b77ed\",\"ktrq\":\"2020-06-05\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"16:00:00\",\"jssj\":\"18:00:00\",\"sjy\":{\"id\":\"10187\",\"name\":\"欧晓旋\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10045\",\"userName\":\"叶俐丽\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"可开庭\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10045\",\"userName\":\"叶俐丽\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初14207号\",\"laay\":\"劳务合同纠纷\",\"cbr\":\"叶俐丽\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"朱赖群\"},{\"ssdw\":\"被告\",\"name\":\"深圳市尚泰装饰设计工程有限公司\"}],\"larq\":\"2020/4/21 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"b1d7b07cf3ad469ab4bc11c80cfadb59\",\"ktrq\":\"2020-06-01\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"09:30:00\",\"jssj\":\"10:30:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"无纸化\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初10811号\",\"laay\":\"承揽合同纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"柯荣灿\"},{\"ssdw\":\"被告\",\"name\":\"孙亚强\"}],\"larq\":\"2020/4/2 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"b22287fccfe645e3b55e12a49bc40e82\",\"ktrq\":\"2020-05-21\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"16:00:00\",\"jssj\":\"18:00:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"无纸化\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初9366号\",\"laay\":\"特许经营合同纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳市沃尔弗斯珠宝实业股份有限公司\"},{\"ssdw\":\"被告\",\"name\":\"异彩珠宝国际贸易（苏州）有限公司\"}],\"larq\":\"2020/3/17 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"bc702b2342424d90924d3b8042df85d9\",\"ktrq\":\"2020-05-28\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"09:30:00\",\"jssj\":\"10:30:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},{\"roleName\":\"承办人\",\"userId\":\"10185\",\"userName\":\"王鸥\"},{\"roleName\":\"承办人\",\"userId\":\"10186\",\"userName\":\"杨玲\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初1172号\",\"laay\":\"金融借款合同纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"宁波银行股份有限公司深圳罗湖支行\"},{\"ssdw\":\"被告\",\"name\":\"曾三峰\"},{\"ssdw\":\"被告\",\"name\":\"李姗徽\"}],\"larq\":\"2020/1/6 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"},{\"ah\":\"（2020）粤0303民初1173号\",\"laay\":\"金融借款合同纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"宁波银行股份有限公司深圳罗湖支行\"},{\"ssdw\":\"被告\",\"name\":\"潘小寨\"},{\"ssdw\":\"被告\",\"name\":\"陈韶辉\"}],\"larq\":\"2020/1/6 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"bfb2183797a247648c4b80620816b58b\",\"ktrq\":\"2020-05-27\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"14:30:00\",\"jssj\":\"16:00:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},{\"roleName\":\"承办人\",\"userId\":\"10185\",\"userName\":\"王鸥\"},{\"roleName\":\"承办人\",\"userId\":\"10186\",\"userName\":\"杨玲\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初7441号\",\"laay\":\"特许经营合同纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"于潺\"},{\"ssdw\":\"被告\",\"name\":\"深圳市飞范国际服饰有限公司\"}],\"larq\":\"2020/3/6 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"c1cc7398c690488b9d7a473365663e65\",\"ktrq\":\"2020-05-29\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"16:00:00\",\"jssj\":\"17:30:00\",\"sjy\":{\"id\":\"10192\",\"name\":\"李慧\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初13652号\",\"laay\":\"物业服务合同纠纷\",\"cbr\":\"苏宏\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳市金峰园物业管理有限公司 \"},{\"ssdw\":\"被告\",\"name\":\"喻荣慧\"}],\"larq\":\"2020/4/16 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"cf60e730f9ed49e48ee909de1623af13\",\"ktrq\":\"2020-05-22\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"14:30:00\",\"jssj\":\"16:00:00\",\"sjy\":{\"id\":\"10195\",\"name\":\"叶雄\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"},{\"roleName\":\"承办人\",\"userId\":\"10185\",\"userName\":\"王鸥\"},{\"roleName\":\"承办人\",\"userId\":\"10186\",\"userName\":\"杨玲\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初4368号\",\"laay\":\"侵害商标权纠纷\",\"cbr\":\"陈婉丹\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳市仙迪化妆品股份有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市碧莱雅化妆品有限公司\"},{\"ssdw\":\"被告\",\"name\":\"广东保格丽生物科技实业有限公司\"},{\"ssdw\":\"被告\",\"name\":\"汕头市涵美堂化妆品有限公司\"}],\"larq\":\"2020/1/14 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"d0845f97dc1c423c9440695049802945\",\"ktrq\":\"2020-05-22\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"10:30:00\",\"jssj\":\"12:00:00\",\"sjy\":{\"id\":\"10195\",\"name\":\"叶雄\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初8243号\",\"laay\":\"买卖合同纠纷\",\"cbr\":\"陈婉丹\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"富昌迪科技（天津）有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳洪涛集团股份有限公司\"}],\"larq\":\"2020/3/12 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"d1479680db5d42c3a1f6e91045e0eae2\",\"ktrq\":\"2020-05-29\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"09:30:00\",\"jssj\":\"10:30:00\",\"sjy\":{\"id\":\"10192\",\"name\":\"李慧\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"普通程序，独任\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10048\",\"userName\":\"苏宏\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初10649号\",\"laay\":\"侵害商标权纠纷\",\"cbr\":\"苏宏\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"晶华宝岛（北京）眼镜有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市罗湖区江南眼镜店\"}],\"larq\":\"2020/3/31 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"d32f1f471eb349b3ba6b4a43f883c276\",\"ktrq\":\"2020-06-04\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"10:30:00\",\"jssj\":\"12:00:00\",\"sjy\":{\"id\":\"10194\",\"name\":\"曾艺\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"},{\"roleName\":\"承办人\",\"userId\":\"10045\",\"userName\":\"叶俐丽\"},{\"roleName\":\"承办人\",\"userId\":\"10185\",\"userName\":\"王鸥\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初2277号\",\"laay\":\"民间借贷纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"张忠\"},{\"ssdw\":\"被告\",\"name\":\"吴勇\"},{\"ssdw\":\"被告\",\"name\":\"伍敏佳\"}],\"larq\":\"2020/1/8 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"d6b46325e3a34c87963cb804e92fb251\",\"ktrq\":\"2020-06-01\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"16:00:00\",\"jssj\":\"18:00:00\",\"sjy\":{\"id\":\"10602\",\"name\":\"陆贤柔\"},\"zsfg\":{},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10004\",\"userName\":\"潘燕清\"},{\"roleName\":\"承办人\",\"userId\":\"20233\",\"userName\":\"梁运芬\"},{\"roleName\":\"承办人\",\"userId\":\"20143\",\"userName\":\"黄平\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2019）粤0303民初33781号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"潘燕清\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"邱茂庭（广州）餐饮管理有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市罗湖区口口爽饮品店\"}],\"larq\":\"2019/10/16 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"dc5878eac7574ef286243f0e0ee73b86\",\"ktrq\":\"2020-05-22\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"09:30:00\",\"jssj\":\"10:30:00\",\"sjy\":{\"id\":\"10190\",\"name\":\"赖安琪\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"普通独任，可开庭，无纸化\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10047\",\"userName\":\"王碧蕾\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初9547号\",\"laay\":\"著作权权属、侵权纠纷\",\"cbr\":\"王碧蕾\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳市佐卡伊电子商务有限公司\"},{\"ssdw\":\"被告\",\"name\":\"广州百葵贸易有限公司\"}],\"larq\":\"2020/3/19 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"ec987f276f1440bf9eef166574188bcd\",\"ktrq\":\"2020-05-21\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"14:30:00\",\"jssj\":\"16:00:00\",\"sjy\":{\"id\":\"10195\",\"name\":\"叶雄\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"调解\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初8321号\",\"laay\":\"企业借贷纠纷\",\"cbr\":\"陈婉丹\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳市国信保理有限公司\"},{\"ssdw\":\"被告\",\"name\":\"泰通建设集团有限公司\"},{\"ssdw\":\"被告\",\"name\":\"刘天茹\"},{\"ssdw\":\"被告\",\"name\":\"刘伟\"},{\"ssdw\":\"被告\",\"name\":\"戴荻茜\"}],\"larq\":\"2020/3/12 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"f4af76ebb49040e7987ba60c838b132a\",\"ktrq\":\"2020-05-22\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"16:00:00\",\"jssj\":\"18:00:00\",\"sjy\":{\"id\":\"10195\",\"name\":\"叶雄\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初4368号\",\"laay\":\"侵害商标权纠纷\",\"cbr\":\"陈婉丹\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"深圳市仙迪化妆品股份有限公司\"},{\"ssdw\":\"被告\",\"name\":\"深圳市碧莱雅化妆品有限公司\"},{\"ssdw\":\"被告\",\"name\":\"广东保格丽生物科技实业有限公司\"},{\"ssdw\":\"被告\",\"name\":\"汕头市涵美堂化妆品有限公司\"}],\"larq\":\"2020/1/14 0:00:00\",\"sycx\":\"普通\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]},{\"pqid\":\"fb321af0447649d5a412d46d7c34e02c\",\"ktrq\":\"2020-05-21\",\"ktdd\":{\"id\":\"8\",\"name\":\"第八法庭\"},\"kssj\":\"10:30:00\",\"jssj\":\"12:00:00\",\"sjy\":{\"id\":\"10195\",\"name\":\"叶雄\"},\"zsfg\":{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"},\"spz\":\"\",\"pqbm\":{\"id\":\"10\",\"name\":\"知识产权庭\"},\"pqbz\":\"\",\"hyt_List\":[{\"roleName\":\"承办人\",\"userId\":\"10046\",\"userName\":\"陈婉丹\"}],\"gsr_List\":[],\"pqsx_List\":[],\"ah_Info\":[{\"ah\":\"（2020）粤0303民初8258号\",\"laay\":\"合同纠纷\",\"cbr\":\"陈婉丹\",\"dsr\":[{\"ssdw\":\"原告\",\"name\":\"熊辉\"},{\"ssdw\":\"被告\",\"name\":\"深圳广田集团股份有限公司\"},{\"ssdw\":\"被告\",\"name\":\"广田控股集团有限公司\"}],\"larq\":\"2020/3/12 0:00:00\",\"sycx\":\"简易\",\"spcx\":\"一审\",\"ajlb\":\"民事\",\"cbbm\":\"知识产权庭\"}]}]}"
    public static String courtrooms ="{\"resCode\":200,\"resTips\":\"查询成功!\",\"resData\":[{\"id\":\"4\",\"name\":\"第四法庭\"},{\"id\":\"5\",\"name\":\"第五法庭\"},{\"id\":\"6\",\"name\":\"第六法庭\"},{\"id\":\"7\",\"name\":\"第七法庭\"},{\"id\":\"8\",\"name\":\"第八法庭\"},{\"id\":\"9\",\"name\":\"第九法庭\"},{\"id\":\"10\",\"name\":\"第十法庭\"},{\"id\":\"11\",\"name\":\"第十一法庭\"},{\"id\":\"12\",\"name\":\"第十二法庭\"},{\"id\":\"15\",\"name\":\"第十五法庭\"},{\"id\":\"16\",\"name\":\"第十六法庭\"},{\"id\":\"17\",\"name\":\"第十七法庭\"},{\"id\":\"18\",\"name\":\"第十八法庭\"},{\"id\":\"19\",\"name\":\"第十九法庭\"},{\"id\":\"20\",\"name\":\"第二十法庭\"},{\"id\":\"22\",\"name\":\"第二十二法庭\"},{\"id\":\"23\",\"name\":\"第二十三法庭\"},{\"id\":\"24\",\"name\":\"第二十四法庭\"},{\"id\":\"25\",\"name\":\"第二十五法庭\"},{\"id\":\"26\",\"name\":\"第二十六法庭\"},{\"id\":\"27\",\"name\":\"第二十七法庭\"},{\"id\":\"29\",\"name\":\"第二十九法庭\"},{\"id\":\"31\",\"name\":\"第三十一法庭\"},{\"id\":\"33\",\"name\":\"第三十三法庭\"},{\"id\":\"34\",\"name\":\"第三十四法庭\"},{\"id\":\"36\",\"name\":\"速裁一庭\"},{\"id\":\"37\",\"name\":\"速裁二庭\"},{\"id\":\"38\",\"name\":\"速裁三庭\"},{\"id\":\"39\",\"name\":\"调解一室\"},{\"id\":\"40\",\"name\":\"调解二室\"},{\"id\":\"41\",\"name\":\"律师工作室\"}]}"

}