package cn.oyzh.easymysql.db;

import cn.hutool.core.util.StrUtil;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author oyzh
 * @since 2024/1/30
 */
public class DBDatabase {

    /**
     * 库名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 库字符集
     */
    private SimpleStringProperty charsetProperty;

    /**
     * 库排序规则
     */
    private SimpleStringProperty collationProperty;

    public SimpleStringProperty charsetProperty() {
        if (this.charsetProperty == null) {
            this.charsetProperty = new SimpleStringProperty();
        }
        return this.charsetProperty;
    }

    public void setCharset(String charset) {
        this.charsetProperty().setValue(charset);
    }

    public String getCharset() {
        return this.charsetProperty == null ? null : this.charsetProperty.get();
    }

    public SimpleStringProperty collationProperty() {
        if (this.collationProperty == null) {
            this.collationProperty = new SimpleStringProperty();
        }
        return this.collationProperty;
    }

    public void setCollation(String collation) {
        this.collationProperty().setValue(collation);
    }

    public String getCollation() {
        return this.collationProperty == null ? null : this.collationProperty.get();
    }

    public void setCharsetAndCollation(String collation) {
        if (StrUtil.isNotBlank(collation)) {
            String charset = collation.split("_")[0];
            this.setCharset(charset);
            if (collation.contains("_")) {
                this.setCollation(collation);
            } else {
                this.setCollation(null);
            }
        }
    }
}
