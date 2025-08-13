package com.louis.mygson

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.louis.mygson.other.Class_Protected_Constructor
import com.louis.mygson.other.Class_Public_Constructor
import com.louisgeek.library.KotlinTest_Library
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


object KotlinTest {
    fun test() {
        val objJson = getObjJson()
//    val userBean = Gson().fromJson(objJson, UserBean().javaClass)
        val userBean = Gson().fromJson(objJson, UserBean::class.java) //普通对象
        println("userBean=$userBean") //userBean=UserBean{userid=1, username='test_1'}
        println("------------------------------------------")

        val objBaseJson = getObjBaseJson()
//    val baseBean = Gson().fromJson(objBaseJson,  BaseBean<Any>().javaClass)
        val baseBean = Gson().fromJson(objBaseJson, BaseBean<UserBean>().javaClass) //泛型被擦除
        println("baseBean=$baseBean") //baseBean=BaseBean{code=100, data={userid=1.0, username=test_1}}


        val baseUser = Gson().fromJson<BaseBean<UserBean>>(objBaseJson, object : TypeToken<BaseBean<UserBean>>() {  //TypeToken 用来解决运行时泛型类型被擦除的问题
        }.getType())
        println("baseUser=$baseUser") //baseUser=BaseBean{code=100, data=UserBean{userid=1, username='test_1'}}
        println("------------------------------------------")


        //泛型擦除测试
        val stringList = ArrayList<String>()
        val intList = ArrayList<Int>()
        println("intList type=" + intList.javaClass) //intList type=class java.util.ArrayList
        println("stringList type=" + stringList.javaClass) //stringList type=class java.util.ArrayList
        println(stringList.javaClass.isAssignableFrom(intList.javaClass)) //true

        //TypeToken 能力测试
        val stringTypeToken = object : TypeToken<ArrayList<String>>() {}
        val intTypeToken = object : TypeToken<ArrayList<Int>>() {}
        println("stringTypeToken=" + stringTypeToken.getType()) //stringTypeToken=java.util.ArrayList<java.lang.String>
        println("intTypeToken=" + intTypeToken.getType()) //intTypeToken=java.util.ArrayList<java.lang.Integer>
        println("------------------------------------------")

        //List<XxxBean> 测试
        val listJson = getListJson()
        val userBeanList =
            Gson().fromJson<List<UserBean>>(listJson, object : TypeToken<List<UserBean>>() {}.getType())

        for (i in userBeanList.indices) {
            val userBeanTmp = userBeanList.get(i)
            println("i=$i userBean=$userBeanTmp")
        }

        println("------------------------------------------")

        //匿名对象测试
        val type1 = Class_Public_Constructor().type
        //java protected 同一包中的类和其他包中的子类可访问
        val type2 = object : Class_Protected_Constructor() {}.type //匿名对象（object expression 对象表达式，对应 Java 的匿名内部类）

        val list  = ArrayList<String>() //直接创建了 ArrayList 类的实例
        val list2  = object : ArrayList<String>() {} //创建一个匿名内部类（ArrayList 的子类），然后实例化一个这个子类的对象

        println("list=$list")
        println("list2=$list2")

        println("------------------------------------------")

        val userBean2 = fromJson<UserBean>(objJson)
        println("userBean2=$userBean2") //userBean2=UserBean{userid=1, username='test_1'}

        val baseUser2 = fromJson<BaseBean<UserBean>>(objBaseJson) //泛型被擦除
        println("baseUser2=$baseUser2") //baseUser2=BaseBean{code=100, data={userid=1.0, username=test_1}}

        val baseUser3 = parseJson<BaseBean<UserBean>>(objBaseJson)
        println("baseUser3=$baseUser3") //baseUser3=BaseBean{code=100, data=UserBean{userid=1, username='test_1'}}

        val userBeanList2 = parseJson<List<UserBean>>(listJson)
        println("userBeanList2=$userBeanList2") //userBeanList2=[UserBean{userid=1, username='test_1'}, UserBean{userid=2, username='test_2'}, UserBean{userid=3, username='test_3'}]

        val userBeanList3 = parseJsonToList<UserBean>(listJson)
        println("userBeanList3=$userBeanList3") //userBeanList3=[UserBean{userid=1, username='test_1'}, UserBean{userid=2, username='test_2'}, UserBean{userid=3, username='test_3'}]

        val userBeanList4 = KotlinTest_Library.parseJsonToList<UserBean>(listJson)
        println("userBeanList4=$userBeanList4") //userBeanList3=[UserBean{userid=1, username='test_1'}, UserBean{userid=2, username='test_2'}, UserBean{userid=3, username='test_3'}]


        val typeLiteral = object : TypeLiteral<BaseBean<UserBean>>() {}.type
        val typeToken = object : TypeToken<BaseBean<UserBean>>() {}.type

        println("typeLiteral=$typeLiteral")
        println("typeToken=$typeToken")
    }

    private fun getObjJson(): String {
        val userBean = UserBean()
        userBean.userid = 1
        userBean.username = "test_1"
        return Gson().toJson(userBean)
    }

    private fun getObjBaseJson(): String {
        val userBean = UserBean()
        userBean.userid = 1
        userBean.username = "test_1"

        val baseBean = BaseBean<UserBean>()
        baseBean.code = 100
        baseBean.data = userBean
        return Gson().toJson(baseBean)
    }

    private fun getListJson(): String {
        val userBeanList: MutableList<UserBean> = ArrayList()
        for (i in 0..2) {
            val userBean = UserBean()
            userBean.userid = (i + 1)
            userBean.username = "test_" + (i + 1)
            userBeanList.add(userBean)
        }
        return Gson().toJson(userBeanList)
    }


    inline fun <reified T> fromJson(json: String): T {
        return Gson().fromJson(json, T::class.java)
    }

    inline fun <reified T> parseJson(json: String): T {
        //仍旧借助 TypeToken 类获取具体的泛型类型
        val type = object : TypeToken<T>() {}.type
        return Gson().fromJson(json, type)
    }

    inline fun <reified T> parseJsonToList(json: String): List<T> {
        //在 app module 写 parseJsonToList 然后在 app module 调用，在 id("org.jetbrains.kotlin.android") version "1.8.10" 及以下版本会丢失泛型，1.8.20 及以上没问题
        //但是如果在 library module 写 parseJsonToList 然后在 app module 调用，不管 kotlin 啥版本都没问题
        //如果在 library module 写 parseJsonToList 然后在 library module 调用，在 id("org.jetbrains.kotlin.android") version "1.8.10" 及以下版本会丢失泛型，1.8.20 及以上没问题
        return parseJson<List<T>>(json)
    }

}



open class TypeLiteral<T> {
    val type: Type
        get() = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
}