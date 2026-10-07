package cn.oyzh.easymysql.fx.data;

import cn.oyzh.common.util.CollectionUtil;
import cn.oyzh.easymysql.mysql.column.MysqlColumn;
import cn.oyzh.fx.gui.text.field.SaveFileTextField;
import cn.oyzh.fx.plus.chooser.FXChooser;
import cn.oyzh.fx.plus.chooser.FileExtensionFilter;
import cn.oyzh.fx.plus.controls.button.FXCheckBox;
import cn.oyzh.fx.plus.tableview.TableViewUtil;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 数据导出表
 *
 * @author oyzh
 * @since 2024/08/27
 */
public class DataExportTable {

    /**
     * 表名称
     */
    private String name;

    /**
     * 字段列表
     */
    private List<DataExportColumn> columns;

    /**
     * 文件路径属性
     */
    private StringProperty filePathProperty;

    /**
     * 是否选中属性
     */
    private BooleanProperty selectedProperty;

    /**
     * 扩展后缀属性
     */
    private ObjectProperty<FileExtensionFilter> extensionProperty;

    /** 获取是否选中属性 */
    public BooleanProperty selectedProperty() {
        if (this.selectedProperty == null) {
            this.selectedProperty = new SimpleBooleanProperty(false);
            this.selectedProperty.addListener((observable, oldValue, newValue) -> {
                if (newValue && this.getFilePath() == null) {
                    this.updateFilePath();
                }
            });
        }
        return this.selectedProperty;
    }

    /** 是否选中 */
    public boolean isSelected() {
        return this.selectedProperty != null && this.selectedProperty.get();
    }

    /** 设置是否选中 */
    public void setSelected(boolean selected) {
        this.selectedProperty().set(selected);
    }

    /** 获取选中控件 */
    public FXCheckBox getSelectedControl() {
        FXCheckBox checkBox = new FXCheckBox();
        checkBox.setSelected(this.isSelected());
        AtomicBoolean ignoreChanged = new AtomicBoolean(false);
        checkBox.selectedChanged((observable, oldValue, newValue) -> {
            ignoreChanged.set(true);
            this.setSelected(newValue);
            ignoreChanged.set(false);
        });
        this.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (!ignoreChanged.get()) {
                checkBox.setSelected(newValue);
            }
        });
        TableViewUtil.selectRowOnMouseClicked(checkBox);
        return checkBox;
    }

    /** 获取文件路径属性 */
    public StringProperty filePathProperty() {
        if (filePathProperty == null) {
            this.filePathProperty = new SimpleStringProperty();
        }
        return this.filePathProperty;
    }

    /** 获取文件路径 */
    public String getFilePath() {
        return filePathProperty == null ? null : filePathProperty.get();
    }

    /** 设置文件路径 */
    public void setFilePath(String filePath) {
        this.filePathProperty().set(filePath);
    }

    /** 获取文件路径控件 */
    public SaveFileTextField getFilePathControl() {
        SaveFileTextField textField = new SaveFileTextField();
        textField.setText(this.getFilePath());
        textField.setExtension(this.getExtension());
        textField.setInitFileName(this.fileName());
        textField.setOnSelectedFile(file -> {
            textField.setText(file.getPath());
            textField.setInitFileName(file.getName());
            this.setFilePath(file.getPath());
        });
        this.filePathProperty().addListener((observable, oldValue, newValue) -> textField.setText(newValue));
        this.extensionProperty().addListener((observable, oldValue, newValue) -> {
            textField.setExtension(newValue);
            textField.setInitFileName(this.fileName());
        });
        TableViewUtil.rowOnCtrlS(textField);
        TableViewUtil.selectRowOnMouseClicked(textField);
        return textField;
    }

    /** 获取扩展后缀属性 */
    public ObjectProperty<FileExtensionFilter> extensionProperty() {
        if (this.extensionProperty == null) {
            this.extensionProperty = new SimpleObjectProperty<>();
            this.extensionProperty.addListener((observable, oldValue, newValue) -> this.updateFilePath());
        }
        return this.extensionProperty;
    }

    /** 获取扩展后缀 */
    public FileExtensionFilter getExtension() {
        return this.extensionProperty == null ? null : this.extensionProperty.get();
    }

    /** 设置扩展后缀 */
    public void setExtension(FileExtensionFilter extension) {
        this.extensionProperty().set(extension);
    }

    /** 生成导出文件名 */
    private String fileName() {
        if (this.getExtension() != null) {
            return this.name + this.getExtension().getExtension().substring(1);
        }
        return "";
    }

    /**
     * 设置字段列表
     *
     * @param columns 字段列表
     */
    public void columns(List<? extends MysqlColumn> columns) {
        this.columns = new ArrayList<>();
        for (MysqlColumn column : columns) {
            DataExportColumn exportColumn = new DataExportColumn();
            exportColumn.copy(column);
            this.columns.add(exportColumn);
        }
    }

    /** 获取字段列表 */
    public List<MysqlColumn> columns() {
        return new ArrayList<>(this.columns);
    }

    /** 获取选中的字段列表 */
    public List<MysqlColumn> selectedColumns() {
        List<MysqlColumn> selectedColumns = new ArrayList<>();
        for (DataExportColumn column : this.columns) {
            if (column.isSelected()) {
                selectedColumns.add(column);
            }
        }
        return selectedColumns;
    }

    /** 获取选中的字段名称列表 */
    public List<String> selectedColumnNames() {
        List<String> selectedColumns = new ArrayList<>();
        for (MysqlColumn column : this.selectedColumns()) {
            selectedColumns.add(column.getName());
        }
        return selectedColumns;
    }

    /** 是否存在字段 */
    public boolean hasColumns() {
        return CollectionUtil.isNotEmpty(this.columns);
    }

    /** 更新文件路径 */
    private void updateFilePath() {
        if (this.isSelected() || this.getFilePath() != null) {
            this.setFilePath(FXChooser.getDesktopDirectory() + File.separator + this.fileName());
        }
    }

    /** 获取表名称 */
    public String getName() {
        return name;
    }

    /** 设置表名称 */
    public void setName(String name) {
        this.name = name;
    }

    /** 获取字段列表 */
    public List<DataExportColumn> getColumns() {
        return columns;
    }

    /** 设置字段列表 */
    public void setColumns(List<DataExportColumn> columns) {
        this.columns = columns;
    }
}
