package cn.oyzh.easymysql.store;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easymysql.MysqlConst;
import cn.oyzh.easymysql.domain.DBPageInfo;
import cn.oyzh.fx.common.store.ObjectFileStore;


/**
 * 页面信息储存
 *
 * @author oyzh
 * @since 2023/01/17
 */
public class DBPageInfoStore extends ObjectFileStore<DBPageInfo> {

    /**
     * 当前实例
     */
    public static final DBPageInfoStore INSTANCE = new DBPageInfoStore();

    /**
     * 当前设置
     */
    public static final DBPageInfo PAGE_INFO = INSTANCE.load();

    {
        this.filePath(MysqlConst.STORE_PATH + "page_info.json");
        StaticLog.info("DBPageInfoStore filePath:{} charset:{} init {}.", this.filePath(), this.charset(), super.init() ? "success" : "fail");
    }

    @Override
    public synchronized DBPageInfo load() {
        DBPageInfo pageInfo = null;
        try {
            // 从文件中读取文本内容
            String text = FileUtil.readString(this.storeFile(), this.charset());
            if (StrUtil.isNotBlank(text)) {
                // 将文本内容转换为页面信息对象
                pageInfo = JSONUtil.toBean(text, DBPageInfo.class);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        // 如果页面信息为空，则创建一个新的页面信息对象
        if (pageInfo == null) {
            pageInfo = new DBPageInfo();
        }
        return pageInfo;
    }
}
