package cn.oyzh.easymysql.db;

import cn.hutool.core.collection.CollUtil;
import cn.oyzh.easymysql.domain.MysqlConnect;
import cn.oyzh.easymysql.trees.connect.DBConnectTreeItem;
import lombok.NonNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 连接管理
 *
 * @author oyzh
 * @since 2023/5/12
 */
public interface DBConnectManager {

    /**
     * 添加连接
     *
     * @param redisInfo 连接信息
     */
    void addConnect(@NonNull MysqlConnect redisInfo);

    /**
     * 删除多个连接
     *
     * @param redisInfos 连接列表
     */
    default void addConnects(List<MysqlConnect> redisInfos) {
        if (CollUtil.isNotEmpty(redisInfos)) {
            for (MysqlConnect redisInfo : redisInfos) {
                this.addConnect(redisInfo);
            }
        }
    }

    /**
     * 添加连接键
     *
     * @param item 连接键
     */
    void addConnectItem(@NonNull DBConnectTreeItem item);

    /**
     * 添加多个连接键
     *
     * @param items 连接键列表
     */
    void addConnectItems(@NonNull List<DBConnectTreeItem> items);

    /**
     * 删除连接键
     *
     * @param item 连接键
     * @return 结果
     */
    boolean delConnectItem(@NonNull DBConnectTreeItem item);

    /**
     * 获取连接键
     *
     * @return 连接键
     */
    List<DBConnectTreeItem> getConnectItems();

    /**
     * 获取已连接的连接节点
     *
     * @return 已连接的连接节点
     */
    default List<DBConnectTreeItem> getConnectedItems() {
        return this.getConnectItems().parallelStream().filter(DBConnectTreeItem::isConnected).collect(Collectors.toList());
    }

}
