//package cn.oyzh.easymysql.store;
//
//import cn.hutool.core.collection.CollUtil;
//import cn.hutool.core.io.FileUtil;
//import cn.hutool.core.util.StrUtil;
//import cn.oyzh.common.dto.Paging;
//import cn.oyzh.common.log.JulLog;
//import cn.oyzh.easymysql.MysqlConst;
//import cn.oyzh.easymysql.domain.MysqlSearchHistory;
//import cn.oyzh.store.json.ArrayFileStore;
//import lombok.NonNull;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
///**
// * db搜索历史存储
// *
// * @author oyzh
// * @since 2022/12/16
// */
//public class DBSearchHistoryStore extends ArrayFileStore<MysqlSearchHistory> {
//
//    /**
//     * 最大历史数量
//     */
//    public static int His_Max_Size = 50;
//
//    /**
//     * 当前实例
//     */
//    public static final DBSearchHistoryStore INSTANCE = new DBSearchHistoryStore();
//
//    {
//        this.filePath(MysqlConst.STORE_PATH + "db_search_history.json");
//        JulLog.info("dbSearchHistoryStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
//    }
//
//    @Override
//    public synchronized List<MysqlSearchHistory> load() {
//        // 从文件中读取字符串内容
//        String text = FileUtil.readString(this.storeFile(), this.charset());
//        // 如果字符串为空，则返回空列表
//        if (StrUtil.isBlank(text)) {
//            return new ArrayList<>();
//        }
//        // 将字符串解析为搜索历史记录列表
//        return JSONUtil.toList(text, MysqlSearchHistory.class);
//    }
//
//    /**
//     * 获取词汇
//     *
//     * @return 词汇列表
//     */
//    public synchronized List<String> getKw(int type) {
//        return this.load().parallelStream().filter(h -> Objects.equals(h.getType(), type)).map(MysqlSearchHistory::getKw).collect(Collectors.toList());
//    }
//
//    @Override
//    public synchronized boolean add( MysqlSearchHistory history) {
//        try {
//            // 历史列表
//            List<MysqlSearchHistory> histories = this.load();
//            // 过滤出当前类型
//            List<MysqlSearchHistory> hisList = histories.parallelStream().filter(h -> Objects.equals(h.getType(), history.getType())).collect(Collectors.toList());
//            // 最新的数据是当前数据，则无需添加
//            if (history.compare(CollUtil.getLast(hisList))) {
//                return true;
//            }
//            // 移除当前添加内容
//            histories.removeIf(h -> h.compare(history));
//            // 添加到集合
//            histories.add(history);
//            // 对超出限制的数据，进行删除
//            int limit = hisList.size() - His_Max_Size + 1;
//            if (limit > 0) {
//                List<MysqlSearchHistory> delList = hisList.parallelStream().limit(limit).toList();
//                histories.removeAll(delList);
//            }
//            // 保存数据
//            return this.save(histories);
//        } catch (Exception e) {
//            JulLog.warn("add error,err:{}", e.getMessage());
//        }
//        return false;
//    }
//
//    /**
//     * 添加搜索历史
//     *
//     * @param kw 关键词
//     * @return 结果
//     */
//    public synchronized boolean addSearchHistory( String kw) {
//        return this.add(new MysqlSearchHistory(kw, 1));
//    }
//
//    /**
//     * 添加替换历史
//     *
//     * @param kw 关键词
//     * @return 结果
//     */
//    public synchronized boolean addReplaceHistory( String kw) {
//        return this.add(new MysqlSearchHistory(kw, 2));
//    }
//
//    @Override
//    public Paging<MysqlSearchHistory> getPage(int limit, Map<String, Object> params) {
//        return super.getPage(limit, params);
//    }
//
//    /**
//     * 获取搜索词
//     *
//     * @return 搜索词列表
//     */
//    public synchronized List<String> getSearchKw() {
//        return this.load().parallelStream().filter(h -> Objects.equals(h.getType(), 1)).map(MysqlSearchHistory::getKw).collect(Collectors.toList());
//    }
//
//    /**
//     * 获取替换词
//     *
//     * @return 替换词列表
//     */
//    public synchronized List<String> getReplaceKw() {
//        return this.load().parallelStream().filter(h -> Objects.equals(h.getType(), 2)).map(MysqlSearchHistory::getKw).collect(Collectors.toList());
//    }
//}
