package cn.oyzh.easymysql.db.check;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.object.ObjectCopier;
import cn.oyzh.easymysql.db.DBObjectStatus;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author oyzh
 * @since 2024/09/11
 */
@EqualsAndHashCode(callSuper = true)
public class MysqlCheck extends DBObjectStatus implements ObjectCopier<MysqlCheck> {

    /**
     * 库名称
     */
    @Getter
    @Setter
    private String dbName;

    /**
     * 表名称
     */
    @Getter
    @Setter
    private String tableName;

    /**
     * 名称
     */
    @Getter
    private String name;

    /**
     * 子语句
     */
    @Getter
    private String clause;

    public MysqlCheck() {

    }

    public MysqlCheck(String name) {
        this.name = name;
    }

    public void setName(String name) {
        this.name = name;
        super.putOriginalData("name", name);
    }

    public boolean isNameChanged() {
        return super.checkOriginalData("name", this.name);
    }

    public String originalName() {
        return (String) super.getOriginalData("name");
    }

    public void setClause(String clause) {
        this.clause = clause;
        super.putOriginalData("clause", clause);
    }

    public boolean isClauseChanged() {
        return super.checkOriginalData("clause", this.clause);
    }

    @Override
    public void copy(MysqlCheck check) {
        if (check != null) {
            this.name = check.name;
            this.dbName = check.dbName;
            this.clause = check.clause;
            this.tableName = check.tableName;
        }
    }

    public boolean isInvalid() {
        return StrUtil.isBlank(this.name) || StrUtil.isBlank(this.clause);
    }
}
