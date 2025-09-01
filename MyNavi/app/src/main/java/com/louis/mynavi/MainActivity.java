package com.louis.mynavi;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.louis.mynavi.home.HomeFragment;
import com.louis.mynavi.navi.NavManager;
import com.louis.mynavi.navi.PageCondition;
import com.louis.mynavi.navi.PageNavigator;
import com.louis.mynavi.navi.PageNode;
import com.louis.mynavi.navi.PageNodeManager;
import com.louis.mynavi.ui.AgreementFragment;
import com.louis.mynavi.ui.SplashFragment;
import com.louis.mynavi.user.ui.login.LoginFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //    private NavManager navManager;
//    private FeatureManager featureManager;
    private PageNavigator mPageNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavManager navManager = new NavManager(getSupportFragmentManager());
        PageNodeManager pageNodeManager = new PageNodeManager();
        mPageNavigator = new PageNavigator(navManager, pageNodeManager);

        //回退逻辑 Home→Setting、Mine→Setting   从Home进则回Home，从Mine进则回Mine
        //拦截非法跳转
        //DETAIL 仅允许回退（无跳转目标）


        PageCondition loginCondition = () -> PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("isLoggedIn", false);

        PageCondition agreeCondition = () -> PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("isAgreed", false);

        PageNode splashNode = new PageNode(SplashFragment.class);//无条件

        PageNode privacyNode = new PageNode(AgreementFragment.class, () -> !agreeCondition.isSatisfied()); //条件：未同意隐私协议

        PageNode loginNode = new PageNode(LoginFragment.class, () -> agreeCondition.isSatisfied() && !loginCondition.isSatisfied()); //条件：同意隐私协议且未登录
        loginNode.addDependency(privacyNode); //登录页 依赖 隐私协议页

        PageNode homeNode = new PageNode(HomeFragment.class, () -> agreeCondition.isSatisfied() && loginCondition.isSatisfied()); //条件：同意隐私协议且已登录
        homeNode.addDependency(loginNode); //主页页 依赖 登录页

//        mPageNavigator.addPageNode(splashNode);
//        mPageNavigator.addPageNode(privacyNode);
//        mPageNavigator.addPageNode(loginNode);
//        mPageNavigator.addPageNode(homeNode);

        mPageNavigator.addPageNodes(splashNode, privacyNode, loginNode, homeNode);

//        mPageNodeManager.addEdge("AgreementFragment", "LoginFragment");
//        //
//        mPageNodeManager.addEdge("HomeFragment", "LoginFragment");
//        mPageNodeManager.addEdge("HomeFragment", "AgreementFragment");

        // 3. 检查是否存在循环依赖（调试用）
//        if (mPageNodeManager.hasCycle()) {
//            Toast.makeText(this, "流程存在循环依赖，无法跳转！", Toast.LENGTH_LONG).show();
//            return;
//        }
        //

//        navigator.addPage("LoginFragment");
//        navigator.addTransition("AgreementFragment", "LoginFragment");
//        navigator.navigateTo();


//        mPageNodeManager.addPageNode(NavNode.LIST, List.of(NavNode.DETAIL)); // LIST→DETAIL
//        mPageNodeManager.addPageNode(NavNode.DETAIL, List.of(NavNode.PAY));  // DETAIL→PAY（需登录）
//        mPageNodeManager.addPageNode(NavNode.CART, new ArrayList<>());       // CART 无跳转
//        mPageNodeManager.addPageNode(NavNode.SETTING, new ArrayList<>());    // SETTING 无跳转
//        mPageNodeManager.addPageNode(NavNode.LOGIN, new ArrayList<>());      // LOGIN 无跳转
//        mPageNodeManager.addPageNode(NavNode.PAY, new ArrayList<>());

        // 4. 开始跳转流程
        startNavigation();
        // 4. 执行导航（会自动跳过未满足条件的Fragment）
        mPageNavigator.navigateTo("Home"); // 实际路径：privacyNode -> loginNode（按照条件满足实际处理 可能Home不会加载）
    }

    private void startNavigation() {
//        NavNode target = navGraph.get(targetTag);
//        if (target.dependencies != null) {
//            for (String dep : target.dependencies) {
//                if (!isFragmentInBackStack(dep)) {
//                    // 先跳转到依赖的Fragment
//                    navigateTo(dep);
//                }
//            }
//        }
//        // 执行目标Fragment跳转
//        Fragment targetFragment = getFragmentInstance(targetTag);
//        targetFragment.setArguments(target.data);
//        getSupportFragmentManager().beginTransaction()
//                .replace(R.id.container, targetFragment)
//                .addToBackStack(targetTag)
//                .commit();
    }

//    @Override
//    public void onBackPressed() {
//        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            navManager.back();
//        } else {
//            super.onBackPressed();
//        }
//    }
}