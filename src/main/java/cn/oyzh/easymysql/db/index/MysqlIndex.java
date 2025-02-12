package cn.oyzh.easymysql.db.index;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.common.util.ObjectCopier;
import cn.oyzh.easymysql.db.DBObjectStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * db表索引
 *
 * @author oyzh
 * @since 2024/01/24
 */
@EqualsAndHashCode(callSuper = true)
public class MysqlIndex extends DBObjectStatus implements ObjectCopier<MysqlIndex> {

    /**
     * 索引顺序
     */
    @Getter
    @Setter
    private int seqIndex;

    /**
     * 类型
     * 1. normal
     * 2. unique
     * 3. fulltext
     * 4. spatial
     */
    @Getter
    private String type;

    /**
     * 方式
     * 1. null|空字符串
     * 2. btree
     * 3. hash
     */
    @Getter
    private String method;

    /**
     * 注释
     */
    @Getter
    private String comment;

    /**
     * 名称
     */
    @Getter
    private String name;

    /**
     * 字段列表
     */
    @Getter
    private List<IndexColumn> columns;

    public String originalName() {
        return (String) super.getOriginalData("name");
    }

    public void setName(String name) {
        this.name = name;
        super.putOriginalData("name", name);
    }

    public void addColumn(String column, int subPart) {
        if (this.columns == null) {
            this.setColumns(new ArrayList<>());
        }
        this.columns.add(new IndexColumn(column, subPart));
    }

    public boolean isUnique() {
        return StrUtil.equalsIgnoreCase(this.getMethod(), "UNIQUE");
    }

    public void setColumns(List<IndexColumn> columns) {
        this.columns = columns;
        super.putOriginalData("columns", columns);
    }

    public void setType(String type) {
        this.type = type;
        super.putOriginalData("type", type);
    }

    public void setMethod(String method) {
        this.method = method;
        super.putOriginalData("method", method);
    }

    public void setComment(String comment) {
        this.comment = comment;
        super.putOriginalData("comment", comment);
    }

    public void type(String type, int noneUnique) {
        if (StrUtil.equalsIgnoreCase(type, "HASH") && noneUnique == 0) {
            this.setType("UNIQUE");
            this.setMethod("HASH");
        } else if (StrUtil.equalsIgnoreCase(type, "HASH") && noneUnique == 1) {
            this.setType("NORMAL");
            this.setMethod("HASH");
        } else if (StrUtil.equalsIgnoreCase(type, "BTREE") && noneUnique == 0) {
            this.setType("UNIQUE");
            this.setMethod("BTREE");
        } else if (StrUtil.equalsIgnoreCase(type, "BTREE") && noneUnique == 1) {
            this.setType("NORMAL");
            this.setMethod("BTREE");
        } else if (StrUtil.equalsIgnoreCase(type, "fulltext")) {
            this.setType("FULLTEXT");
            this.setMethod("");
        } else if (StrUtil.equalsIgnoreCase(type, "spatial")) {
            this.setType("SPATIAL");
            this.setMethod("");
        } else {
            this.setType("NORMAL");
            this.setMethod("BTREE");
        }
    }

    public String typeName() {
        if (this.type == null || "NORMAL".equalsIgnoreCase(this.type)) {
            return null;
        }
        return this.type.toUpperCase();
    }

    public String methodName() {
        return StrUtil.emptyToNull(this.method);
    }

    @Override
    public void copy(MysqlIndex t1) {
        if (t1 != null) {
            this.setName(t1.name);
            this.setType(t1.type);
            this.setMethod(t1.method);
            this.setComment(t1.comment);
            this.setColumns(t1.columns);
            this.setSeqIndex(t1.seqIndex);
        }
    }

    public boolean isInvalid() {
        return StrUtil.isBlank(this.name) || StrUtil.isBlank(this.type) || CollUtil.isEmpty(this.columns);
    }

    /**
     * 索引字段
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IndexColumn {

        /**
         * 字段名
         */
        private String columnName;

        /**
         * 子部分
         */
        private Integer subPart;

        @Override
        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof IndexColumn column) {
                return Objects.equals(column.subPart, this.subPart) && StrUtil.equals(this.columnName, column.columnName);
            }
            return false;
        }
    }
}
