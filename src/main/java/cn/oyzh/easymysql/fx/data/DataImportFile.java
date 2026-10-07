package cn.oyzh.easymysql.fx.data;

import cn.oyzh.common.util.StringUtil;
import cn.oyzh.easymysql.fx.table.MysqlTableComboBox;
import cn.oyzh.easymysql.mysql.MysqlClient;
import cn.oyzh.fx.gui.text.field.ChooseFileTextField;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import cn.oyzh.fx.plus.window.StageManager;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.io.File;

/**
 * 数据导入文件
 *
 * @author oyzh
 * @since 2024/08/30
 */
public class DataImportFile {

    /** 数据库名称 */
    private String dbName;

    /** 设置数据库名称 */
    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    /** 数据库客户端 */
    private MysqlClient dbClient;

    /** 设置数据库客户端 */
    public void setDbClient(MysqlClient dbClient) {
        this.dbClient = dbClient;
    }

    /**
     * 文件属性
     */
    private ObjectProperty<File> fileProperty;

    /**
     * 目标表名称
     */
    private String targetTableName;

    /** 获取文件属性 */
    public ObjectProperty<File> fileProperty() {
        if (fileProperty == null) {
            this.fileProperty = new SimpleObjectProperty<>();
        }
        return this.fileProperty;
    }

    /** 获取文件 */
    public File getFile() {
        return fileProperty == null ? null : fileProperty.get();
    }

    /** 获取文件路径 */
    public String getFilePath() {
        File file = getFile();
        return file == null ? null : file.getPath();
    }

    /** 获取文件名称 */
    public String getFileName() {
        File file = getFile();
        return file == null ? null : file.getName();
    }

    /** 设置文件 */
    public void setFile(File file) {
        this.fileProperty().set(file);
    }

    /** 获取文件路径控件 */
    public ChooseFileTextField getFilePathControl() {
        ChooseFileTextField textField = new ChooseFileTextField();
        textField.setText(this.getFilePath());
        textField.setOnSelectedFile(this::setFile);
        this.fileProperty().addListener((observable, oldValue, newValue) -> textField.setText(newValue.getPath()));
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /** 获取目标表控件 */
    public MysqlTableComboBox getTargetTableControl() {
        MysqlTableComboBox comboBox = new MysqlTableComboBox();
        //String dbName = CacheHelper.get("mysql:dbName");
        //ShellMysqlClient dbClient = CacheHelper.get("mysql:dbClient");
        StageManager.showMask(() -> comboBox.init(this.dbName, this.getTableName(), this.dbClient));
        comboBox.selectedItemChanged((observable, oldValue, newValue) -> {
            this.setTargetTableName(newValue);
        });
        TableViewUtil.selectRowOnMouseClicked(comboBox);
        return comboBox;
    }

    /** 获取表名称 */
    public String getTableName() {
        String fileName = this.getFileName();
        if (StringUtil.isBlank(fileName)) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf("."));
    }

    /** 获取目标表名称 */
    public String getTargetTableName() {
        if (this.targetTableName == null) {
            return this.getTableName();
        }
        return this.targetTableName;
    }

    /** 设置目标表名称 */
    public void setTargetTableName(String targetTableName) {
        this.targetTableName = targetTableName;
    }
}
