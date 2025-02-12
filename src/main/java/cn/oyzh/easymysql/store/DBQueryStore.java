package cn.oyzh.easymysql.store;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.JulLog;
import cn.oyzh.easymysql.MysqlConst;
import cn.oyzh.easymysql.domain.MysqlQuery;
import cn.oyzh.fx.common.store.ArrayFileStore;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * db查询存储
 *
 * @author oyzh
 * @since 2024/02/19
 */
public class DBQueryStore extends ArrayFileStore<MysqlQuery> {

    /**
     * 当前实例
     */
    public static final DBQueryStore INSTANCE = new DBQueryStore();

    /**
     * 已加载的db节点
     */
    private final List<MysqlQuery> queryList;

    {
        this.filePath(MysqlConst.STORE_PATH + "db_query.json");
        JulLog.info("DBQueryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
        this.queryList = this.load();
    }

    @Override
    public synchronized List<MysqlQuery> load() {
        if (this.queryList == null) {
            // 读取存储文件中的文本
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StrUtil.isBlank(text)) {
                return new ArrayList<>();
            }
            List<MysqlQuery> list = JSONUtil.toList(text, MysqlQuery.class);
            if (CollUtil.isNotEmpty(list)) {
                list = list.parallelStream().sorted().collect(Collectors.toList());
            }
            return list;
        }
        return this.queryList;
    }

    public synchronized List<MysqlQuery> list(String iid, String dbName) {
        return this.load().parallelStream()
                .filter(p -> iid.equals(p.getIid()) && dbName.equalsIgnoreCase(p.getDbName()))
                .collect(Collectors.toList());
    }

    @Override
    public synchronized boolean add(@NonNull MysqlQuery query) {
        try {
            if (query.getId() == null) {
                query.setId(System.currentTimeMillis() + "");
            }
            Optional<MysqlQuery> optional = this.queryList.parallelStream().filter(q -> q.compare(query)).findAny();
            if (optional.isEmpty()) {
                // 添加到集合
                this.queryList.add(query);
                // 更新数据
                return this.save(this.queryList);
            }
        } catch (Exception e) {
            JulLog.warn("add error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean update(@NonNull MysqlQuery query) {
        try {
            Optional<MysqlQuery> optional = this.queryList.parallelStream().filter(q -> q.compare(query)).findAny();
            // 更新数据
            if (optional.isPresent()) {
                return this.save(this.queryList);
            }
        } catch (Exception e) {
            JulLog.warn("update error,err:{}", e.getMessage());
        }
        return false;
    }

    @Override
    public synchronized boolean delete(@NonNull MysqlQuery query) {
        try {
            // 删除数据
            if (this.queryList.remove(query)) {
                return this.save(this.queryList);
            }
        } catch (Exception e) {
            JulLog.warn("delete error,err:{}", e.getMessage());
            return false;
        }
        return true;
    }
}
