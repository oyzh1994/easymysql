package cn.oyzh.easymysql.db.query;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/02/19
 */
public class MysqlQueryResults<R extends MysqlQueryResult> {

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

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public List<R> getResults() {
        return results;
    }

    public void setResults(List<R> results) {
        this.results = results;
    }
}
