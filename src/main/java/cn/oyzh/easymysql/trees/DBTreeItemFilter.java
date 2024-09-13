package cn.oyzh.easymysql.trees;

import cn.oyzh.easymysql.search.DBSearchHandler;
import cn.oyzh.easymysql.search.DBSearchParam;
import cn.oyzh.fx.plus.trees.RichTreeItem;
import cn.oyzh.fx.plus.trees.RichTreeItemFilter;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

/**
 * 树节点过滤器
 *
 * @author oyzh
 * @since 2023/06/30
 */
@Lazy
@Component
public class DBTreeItemFilter implements RichTreeItemFilter {

    /**
     * 仅看收藏键
     */
    @Setter
    @Getter
    private boolean onlyCollect;

    /**
     * db主页搜索处理
     */
    @Autowired
    private DBSearchHandler searchHandler;

    @Override
    public boolean test(RichTreeItem<?> item) {
        // 不参与过滤的节点
        if (item != null && !item.supportFilter()) {
            return true;
        }
        // 判断是否满足搜索要求
        DBSearchParam param = this.searchHandler.searchParam();
        if (param != null && !param.isEmpty() && param.isFilterMode()) {
            return this.searchHandler.getMatchType(item) != null;
        }
        return true;
    }

}
