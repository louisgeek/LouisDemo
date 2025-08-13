package com.louis.mygson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.louis.mygson.other.Class_Public_Constructor;
import com.louis.mygson.other.Class_Protected_Constructor;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class JavaTest {

    private static String getObjJson() {
        UserBean userBean = new UserBean();
        userBean.userid = 1;
        userBean.username = "test_1";
        return new Gson().toJson(userBean);
    }

    private static String getObjBaseJson() {
        UserBean userBean = new UserBean();
        userBean.userid = 1;
        userBean.username = "test_1";

        BaseBean<UserBean> baseBean = new BaseBean<>();
        baseBean.code = 100;
        baseBean.data = userBean;
        return new Gson().toJson(baseBean);
    }

    private static String getListJson() {
        List<UserBean> userBeanList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            UserBean userBean = new UserBean();
            userBean.userid = (i + 1);
            userBean.username = "test_" + (i + 1);
            userBeanList.add(userBean);
        }
        return new Gson().toJson(userBeanList);
    }

    public static void test() {
        String objJson = getObjJson();
//        UserBean userBean = new Gson().fromJson(objJson, new UserBean().getClass());
        UserBean userBean = new Gson().fromJson(objJson, UserBean.class); //普通对象
        System.out.println("userBean=" + userBean); //userBean=UserBean{userid=1, username='test_1'}
        System.out.println("------------------------------------------");

        String objBaseJson = getObjBaseJson();
//        BaseBean<UserBean> baseBean = new Gson().fromJson(objBaseJson, new BaseBean<>().getClass());
        BaseBean<UserBean> baseBean = new Gson().fromJson(objBaseJson, new BaseBean<UserBean>().getClass()); //泛型被擦除
        System.out.println("baseBean=" + baseBean); //baseBean=BaseBean{code=100, data={userid=1.0, username=test_1}}

        BaseBean<UserBean> baseUser = new Gson().fromJson(objBaseJson, new TypeToken<BaseBean<UserBean>>() { //TypeToken 用来解决运行时泛型类型被擦除的问题
        }.getType());
        System.out.println("baseUser=" + baseUser); //baseUser=BaseBean{code=100, data=UserBean{userid=1, username='test_1'}}
        System.out.println("------------------------------------------");

        //泛型擦除测试
        ArrayList<String> stringList = new ArrayList<>();
        ArrayList<Integer> intList = new ArrayList<>();
        System.out.println("intList type=" + intList.getClass()); //intList type=class java.util.ArrayList
        System.out.println("stringList type=" + stringList.getClass()); //stringList type=class java.util.ArrayList
        System.out.println(stringList.getClass().isAssignableFrom(intList.getClass())); //true

        //TypeToken 能力测试
        TypeToken<ArrayList<String>> stringTypeToken = new TypeToken<ArrayList<String>>() {
        };
        TypeToken<ArrayList<Integer>> intTypeToken = new TypeToken<ArrayList<Integer>>() {
        };
        System.out.println("stringTypeToken=" + stringTypeToken.getType()); //stringTypeToken=java.util.ArrayList<java.lang.String>
        System.out.println("intTypeToken=" + intTypeToken.getType()); //intTypeToken=java.util.ArrayList<java.lang.Integer>
        System.out.println("------------------------------------------");

        //List<XxxBean> 测试
        String listJson = getListJson();
        List<UserBean> userBeanList = new Gson().fromJson(listJson, new TypeToken<List<UserBean>>() {
        }.getType());

        for (int i = 0; i < userBeanList.size(); i++) {
            UserBean userBeanTmp = userBeanList.get(i);
            System.out.println("i=" + i + " userBean=" + userBeanTmp);
        }

        System.out.println("------------------------------------------");

        //匿名内部类测试
        Type type1 = new Class_Public_Constructor().getType();
        //java protected 同一包中的类和其他包中的子类可访问
        Type type2 = new Class_Protected_Constructor() {
        }.getType();

        List<String> list = new ArrayList<>(); //直接创建了 ArrayList 类的实例
        List<String> list2 = new ArrayList<>() {}; //创建一个匿名内部类（ArrayList 的子类），然后实例化一个这个子类的对象--这里的{}定义了一个继承自ArrayList<String>的匿名内部类，list2引用的是这个匿名子类的实例，而非ArrayList本身的实例

        System.out.println("list=" + list);
        System.out.println("list2=" + list2);
    }
}
