package cn.oyzh.easymysql.db.table;

import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.fx.common.util.ObjectCopier;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import cn.oyzh.fx.plus.i18n.I18nHelper;
import cn.oyzh.fx.plus.util.TableViewUtil;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @author oyzh
 * @since 2024/09/11
 */
@EqualsAndHashCode(callSuper = true)
public class DBCheck extends DBObjectStatus implements ObjectCopier<DBCheck> {

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

    public DBCheck() {

    }

    public DBCheck(String name) {
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
    public void copy(DBCheck check) {
        if (check != null) {
            this.name = check.name;
            this.dbName = check.dbName;
            this.clause = check.clause;
            this.tableName = check.tableName;
        }
    }

    public ClearableTextField getNameControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> {
            this.setName(newValue);
        });
        if (this.name != null) {
            textField.setText(this.name);
        }
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    public ClearableTextField getClauseControl() {
        ClearableTextField textField = new ClearableTextField();
        textField.setPromptText(I18nHelper.pleaseInputName());
        textField.addTextChangeListener((observable, oldValue, newValue) -> {
            this.setClause(newValue);
        });
        if (this.clause != null) {
            textField.setText(this.clause);
        }
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }
}
