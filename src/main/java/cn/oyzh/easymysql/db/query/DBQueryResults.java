package cn.oyzh.easymysql.db.query;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/02/19
 */
@Data
public class DBQueryResults<R extends DBQueryResult> {

    private String errMsg;

    private List<R> results;

    public void addResult(R result) {
        if (this.results == null) {
            this.results = new ArrayList<>();
        }
        this.results.add(result);
    }

    public boolean isEmpty() {
        return CollUtil.isEmpty(this.results);
    }

    public boolean isSuccess() {
        return StrUtil.isEmpty(this.errMsg);
    }

    public void parseError(Exception ex) {
        this.errMsg = ex.getMessage();
    }
}
