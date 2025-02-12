package cn.oyzh.easymysql.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easymysql.MysqlConst;
import cn.oyzh.easymysql.domain.MysqlGroup;
import cn.oyzh.store.json.ArrayFileStore;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * db分组存储
 *
 * @author oyzh
 * @since 2023/5/12
 */
public class DBGroupStore extends ArrayFileStore<MysqlGroup> {

    /**
     * 当前实例
     */
    public static final DBGroupStore INSTANCE = new DBGroupStore();

    /**
     * 已加载的db节点
     */
    private final List<MysqlGroup> groups;

    {
        this.filePath(MysqlConst.STORE_PATH + "db_group.json");
        JulLog.info("DBGroupStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
        this.groups = this.load();
    }

    @Override
    public synchronized List<MysqlGroup> load() {
        if (this.groups == null) {
            // 读取存储文件中的文本
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StrUtil.isBlank(text)) {
                return new ArrayList<>();
            }
            // 将文本转换为DBGroup列表
            List<MysqlGroup> DBGroups = JSONUtil.toList(text, MysqlGroup.class);
            if (CollUtil.isNotEmpty(DBGroups)) {
                // 对DBGroup列表进行排序
                DBGroups = DBGroups.parallelStream().sorted().collect(Collectors.toList());
            }
            return DBGroups;
        }
        return this.groups;
    }

    /**
     * 添加分组
     *
     * @param groupName 分组名称
     * @return 结果
     */
    public synchronized MysqlGroup add(@NonNull String groupName) {
        MysqlGroup group = new MysqlGroup(UUID.fastUUID().toString(true), groupName, false);
        if (this.add(group)) {
            return group;
        }
        return null;
    }

    @Override
    public synchronized boolean add(@NonNull MysqlGroup DBGroup) {
        try {
            if (!this.groups.contains(DBGroup)) {
                // 添加到集合
                this.groups.add(DBGroup);
                // 更新数据
                return this.save(this.groups);
            }
        } catch (Exception e) {
            JulLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean update(@NonNull MysqlGroup DBGroup) {
        try {
            // 更新数据
            if (this.groups.contains(DBGroup)) {
                return this.save(this.groups);
            }
        } catch (Exception e) {
            JulLog.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull MysqlGroup DBGroup) {
        try {
            // 删除数据
            if (this.groups.remove(DBGroup)) {
                return this.save(this.groups);
            }
        } catch (Exception e) {
            JulLog.warn("delete error,err:{}", e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 是否存在此分组信息
     *
     * @param DBGroup 分组信息
     * @return 结果
     */
    public synchronized boolean exist(MysqlGroup DBGroup) {
        // 如果传入的分组信息为空，则直接返回false
        if (DBGroup == null) {
            return false;
        }
        // 遍历this.DBGroups列表，检查是否存在与传入的分组信息相同的分组
        for (MysqlGroup group : this.groups) {
            if (Objects.equals(group.getName(), DBGroup.getName()) && group != DBGroup) {  // 如果分组名称相同且不是同一个对象，则说明存在相同的分组信息，返回true
                return true;
            }
        }
        // 循环结束后仍未找到相同的分组信息，返回false
        return false;
    }

}
