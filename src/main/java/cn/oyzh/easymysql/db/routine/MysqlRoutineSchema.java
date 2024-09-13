package cn.oyzh.easymysql.db.routine;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.common.util.ObjectComparator;
import javafx.beans.property.SimpleStringProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * db程序
 *
 * @author oyzh
 * @since 2024/06/28
 */
public class MysqlRoutineSchema implements ObjectComparator<MysqlRoutineSchema> {

    /**
     * 参数列表
     */
    @Setter
    @Getter
    private List<MysqlRoutineParam> params;

    /**
     * 库名称
     */
    @Setter
    @Getter
    private String dbName;

    /**
     * 注释
     */
    @Getter
    @Setter
    private String comment;

    /**
     * 定义者
     */
    @Getter
    @Setter
    private String definer;

    /**
     * 安全性
     */
    @Getter
    @Setter
    private String securityType;

    /**
     * 特征
     */
    @Getter
    @Setter
    private String characteristic;

    /**
     * 程序名称
     */
    private SimpleStringProperty nameProperty;

    /**
     * 程序定义
     */
    private SimpleStringProperty definitionProperty;

    public SimpleStringProperty nameProperty() {
        if (this.nameProperty == null) {
            this.nameProperty = new SimpleStringProperty();
        }
        return this.nameProperty;
    }

    public void setName(String name) {
        this.nameProperty().setValue(name);
    }

    public String getName() {
        return this.nameProperty == null ? null : this.nameProperty.get();
    }

    public SimpleStringProperty definitionProperty() {
        if (this.definitionProperty == null) {
            this.definitionProperty = new SimpleStringProperty();
        }
        return this.definitionProperty;
    }

    public void setDefinition(String definition) {
        this.definitionProperty().setValue(definition);
    }

    public String getDefinition() {
        return this.definitionProperty == null ? null : this.definitionProperty.get();
    }

    /**
     * 程序创建定义
     */
    private SimpleStringProperty createDefinitionProperty;

    public SimpleStringProperty createDefinitionProperty() {
        if (this.createDefinitionProperty == null) {
            this.createDefinitionProperty = new SimpleStringProperty();
        }
        return this.createDefinitionProperty;
    }

    public void setCreateDefinition(String createDefinition) {
        this.createDefinitionProperty().setValue(createDefinition);
        if (StrUtil.isNotBlank(createDefinition)) {
            String[] arr = createDefinition.split(" ");
            for (String string : arr) {
                if (StrUtil.startWithIgnoreCase(string, "DEFINER=")) {
                    this.definer = string.substring(8);
                    break;
                }
            }
            String[] arr1 = createDefinition.split("COMMENT '");
            if (arr1.length >= 2) {
                this.comment = arr1[1].substring(0, arr1[1].indexOf("'"));
            }
            String[] arr2 = createDefinition.split("COMMENT \"");
            if (arr2.length >= 2) {
                this.comment = arr2[1].substring(0, arr2[1].indexOf("\""));
            }
        }
    }

    public String getCreateDefinition() {
        return this.createDefinitionProperty == null ? null : this.createDefinitionProperty.get();
    }

    @Override
    public boolean compare(MysqlRoutineSchema routine) {
        if (routine == null) {
            return false;
        }
        if (routine == this) {
            return true;
        }
        if (!StrUtil.equals(this.getDbName(), routine.getDbName())) {
            return false;
        }
        return StrUtil.equals(this.getName(), routine.getName());
    }

    public boolean isNew() {
        return StrUtil.isBlank(this.getDefinition());
    }
}
