import java.util.*;

/**
 * 条件接口，用于判断节点是否满足处理条件
 */
interface Condition2<T> {
    boolean isSatisfied(T node);
}

/**
 * 带条件判断的拓扑排序实现
 */
public class TopologicalSortWithCondition<T> {

    // 图的邻接表表示
    private Map<T, List<T>> adjacencyList;

    public TopologicalSortWithCondition() {
        adjacencyList = new HashMap<>();
    }

    /**
     * 向图中添加边
     */
    public void addEdge(T source, T destination) {
        adjacencyList.computeIfAbsent(source, k -> new ArrayList<>()).add(destination);
        // 确保目标节点也在图中
        adjacencyList.computeIfAbsent(destination, k -> new ArrayList<>());
    }

    /**
     * 执行拓扑排序，考虑条件判断
     *
     * @param condition 节点需要满足的条件
     * @return 拓扑排序结果
     * @throws IllegalArgumentException 如果图中存在环
     */
    public List<T> topologicalSort(Condition2<T> condition) {
        // 计算每个节点的入度
        Map<T, Integer> inDegree = new HashMap<>();
        for (T node : adjacencyList.keySet()) {
            inDegree.put(node, 0);
        }

        // 计算入度
        for (List<T> neighbors : adjacencyList.values()) {
            for (T neighbor : neighbors) {
                inDegree.put(neighbor, inDegree.get(neighbor) + 1);
            }
        }

        // 队列用于存储入度为0且满足条件的节点
        Queue<T> queue = new LinkedList<>();
        for (T node : adjacencyList.keySet()) {
            if (inDegree.get(node) == 0 && condition.isSatisfied(node)) {
                queue.add(node);
            }
        }

        List<T> result = new ArrayList<>();

        while (!queue.isEmpty()) {
            T current = queue.poll();
            result.add(current);

            // 处理当前节点的所有邻居
            for (T neighbor : adjacencyList.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);

                // 如果入度变为0且满足条件，则加入队列
                if (inDegree.get(neighbor) == 0 && condition.isSatisfied(neighbor)) {
                    queue.add(neighbor);
                }
            }
        }

        // 检查是否有环（如果结果大小不等于节点数量，则存在环）
        if (result.size() != adjacencyList.size()) {
            throw new IllegalArgumentException("图中存在环，无法进行拓扑排序");
        }

        return result;
    }

    // 示例用法
    public static void main(String[] args) {
        TopologicalSortWithCondition<String> sorter = new TopologicalSortWithCondition<>();

        // 添加图的边 (有向边)
        sorter.addEdge("课程A", "课程B");
        sorter.addEdge("课程A", "课程C");
        sorter.addEdge("课程B", "课程D");
        sorter.addEdge("课程C", "课程D");
        sorter.addEdge("课程B", "课程E");
        sorter.addEdge("课程D", "课程F");
        sorter.addEdge("课程E", "课程F");

        // 创建一个条件：只处理名称长度大于2的课程
        Condition2<String> condition = course -> course.length() > 2;

        try {
            List<String> order = sorter.topologicalSort(condition);
            System.out.println("拓扑排序结果:");
            for (String course : order) {
                System.out.println(course);
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
}
    