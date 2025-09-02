package com.louis.mynavi;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.louis.mynavi.dag.DagManager;
import com.louis.mynavi.dag.DagNode;
import com.louis.mynavi.home.HomeFragment;
import com.louis.mynavi.navi.FlowManager;
import com.louis.mynavi.navi.NavManager;
import com.louis.mynavi.navi.PageCondition;
import com.louis.mynavi.navi.PageNavigator;
import com.louis.mynavi.navi.PageNode;
import com.louis.mynavi.node.PageNode2;
import com.louis.mynavi.node.PageNode3;
import com.louis.mynavi.node.PageNodeManager2;
import com.louis.mynavi.node.PageNodeManager3;
import com.louis.mynavi.ui.AgreementFragment;
import com.louis.mynavi.ui.SplashFragment;
import com.louis.mynavi.user.ui.login.LoginFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //    private NavManager navManager;
//    private FeatureManager featureManager;
//    private PageNavigator mPageNavigator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        testPage();
//        testDag();
//        setupNavigation3();
        setupNavigation4();
    }

    private void setupNavigation4() {
        NavManager.getInstance().init(this);
        NavNodeManager navNodeManager01 = NavManager.getInstance().getNavNodeManager01();
        NavNode startNode = navNodeManager01.getStartNode();
        Log.e(TAG, "setupNavigation4: startNode=" + startNode.getFragmentTag());
        if (startNode != null) {
            NavManager.getInstance().navigateToFragment(getSupportFragmentManager(), startNode.getFragmentClass(), null);
        } else {
            NavManager.getInstance().navigateToFragment(getSupportFragmentManager(), HomeFragment.class, null);
        }
    }

    public void setupNavigation3() {
        PageNodeManager3 manager = PageNodeManager3.getInstance();

        PageCondition agreeCondition = () -> PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("isAgreed6789", false);


        PageCondition loginCondition = () -> PageNavigator.getInstance().isLogined;
        // 创建页面节点
        PageNode3 splashPage = new PageNode3("splash", SplashFragment.class.getCanonicalName());
        PageNode3 agreementPage = new PageNode3("agreement", AgreementFragment.class.getCanonicalName());
        PageNode3 loginPage = new PageNode3("login", LoginFragment.class.getCanonicalName());
        PageNode3 homePage = new PageNode3("home", HomeFragment.class.getCanonicalName());

        // 设置依赖关系
        agreementPage.addDependency(SplashFragment.class.getCanonicalName());
        loginPage.addDependency(AgreementFragment.class.getCanonicalName());
        homePage.addDependency(LoginFragment.class.getCanonicalName());

        // 添加到管理器
        manager.addPageNode(splashPage);
        manager.addPageNode(agreementPage);
        manager.addPageNode(loginPage);
        manager.addPageNode(homePage);

        PageNodeManager3.getInstance().navigateToNext(getSupportFragmentManager());

    }

    public void setupNavigation2() {
        PageNodeManager2 manager = PageNodeManager2.getInstance();

        PageCondition agreeCondition = () -> PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("isAgreed6789", false);


        PageCondition loginCondition = () -> PageNavigator.getInstance().isLogined;
        // 创建页面节点
        PageNode2 splashPage = new PageNode2("splash", SplashFragment.class, () -> true);
        PageNode2 agreementPage = new PageNode2("agreement", AgreementFragment.class, () -> !agreeCondition.isSatisfied());
        PageNode2 loginPage = new PageNode2("login", LoginFragment.class, () -> agreeCondition.isSatisfied() && !loginCondition.isSatisfied());
        PageNode2 homePage = new PageNode2("home", HomeFragment.class, () -> agreeCondition.isSatisfied() && loginCondition.isSatisfied());

        // 设置依赖关系
        agreementPage.addDependency(splashPage);
        loginPage.addDependency(agreementPage);
        homePage.addDependency(loginPage);

        // 添加到管理器
        manager.addPageNode(splashPage);
        manager.addPageNode(agreementPage);
        manager.addPageNode(loginPage);
        manager.addPageNode(homePage);

        PageNodeManager2.getInstance().navigateToNext(getSupportFragmentManager());
    }

    private static final String TAG = "MainActivity";

    private void testDag() {
        // 初始化 DAG 管理器
        DagManager dagManager = new DagManager();

        try {
            // 创建节点（无依赖、无条件）
            DagNode nodeA = new DagNode("nodeA", "com.example.NodeAFragment");
            DagNode nodeB = new DagNode("nodeB", "com.example.NodeBFragment");
            DagNode nodeC = new DagNode("nodeC", "com.example.NodeCFragment");
            DagNode nodeD = new DagNode("nodeD", "com.example.NodeDFragment");

            // 注册所有节点到 DAG 管理器（必须先注册再添加依赖）
            dagManager.addNodes(nodeA, nodeB, nodeC, nodeD);
            Log.d(TAG, "所有节点注册完成");

            // 添加依赖关系（形成循环：C → D → C）
            nodeB.addDependency(dagManager, nodeA);   // B 依赖 A
            nodeC.addDependency(dagManager, nodeB);   // C 依赖 B
            nodeD.addDependency(dagManager, nodeC);   // D 依赖 C
            nodeC.addDependency(dagManager, nodeD);   // C 依赖 D（循环依赖）

            Log.d(TAG, "依赖关系添加完成，开始拓扑排序...");
            List<DagNode> sortedNodes = dagManager.topologicalSort();

            Log.d(TAG, "拓扑排序完成，序列长度: " + sortedNodes.size());
            for (DagNode node : sortedNodes) {
                Log.d(TAG, "已处理节点: " + node.getId());
            }

        } catch (IllegalArgumentException e) {
            Log.e(TAG, "节点或依赖非法: " + e.getMessage());
        } catch (IllegalStateException e) {
            Log.e(TAG, "拓扑排序失败（循环依赖）: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "测试异常: " + e.getMessage());
        }
    }

    private void testPage() {
        com.louis.mynavi.navi.NavManager navManager = new com.louis.mynavi.navi.NavManager(getSupportFragmentManager());
        PageNavigator.getInstance().init(navManager);

        //回退逻辑 Home→Setting、Mine→Setting   从Home进则回Home，从Mine进则回Mine
        //拦截非法跳转
        //DETAIL 仅允许回退（无跳转目标）

        PageCondition agreeCondition = () -> PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean("isAgreed6789", false);


        PageCondition loginCondition = () -> PageNavigator.getInstance().isLogined;


        PageNode splashNode = new PageNode(SplashFragment.class);//无条件

        PageNode privacyNode = new PageNode(AgreementFragment.class, () -> !agreeCondition.isSatisfied()); //条件：未同意隐私协议
        privacyNode.addDependency(splashNode); //

        PageNode loginNode = new PageNode(LoginFragment.class, () -> agreeCondition.isSatisfied() && !loginCondition.isSatisfied()); //条件：同意隐私协议且未登录
        loginNode.addDependency(privacyNode); //登录页 依赖 隐私协议页

        PageNode homeNode = new PageNode(HomeFragment.class, () -> agreeCondition.isSatisfied() && loginCondition.isSatisfied()); //条件：同意隐私协议且已登录
        homeNode.addDependency(loginNode); //主页页 依赖 登录页
//        loginNode.addDependency(homeNode);

//        mPageNavigator.addPageNode(splashNode);
//        mPageNavigator.addPageNode(privacyNode);
//        mPageNavigator.addPageNode(loginNode);
//        mPageNavigator.addPageNode(homeNode);

        PageNavigator.getInstance().addPageNodes(splashNode, privacyNode, loginNode, homeNode);

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
//        startNavigation();
        // 4. 执行导航（会自动跳过未满足条件的Fragment）
//        mPageNavigator.navigateTo("Home"); // 实际路径：privacyNode -> loginNode（按照条件满足实际处理 可能Home不会加载）
        // 调试：检查所有节点状态


//        PageNavigator.getInstance().navigateToNext(true);
        PageNavigator.getInstance().startNavigation();
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