package cn.oyzh.easymysql.db.event;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.DBObjectStatus;
import cn.oyzh.easymysql.util.DBUtil;
import cn.oyzh.fx.common.util.ObjectComparator;
import cn.oyzh.fx.common.util.ObjectCopier;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @author oyzh
 * @since 2024/09/09
 */
public class MysqlEvent extends DBObjectStatus implements ObjectCopier<MysqlEvent>, ObjectComparator<MysqlEvent> {

    /**
     * 名称
     */
    @Getter
    @Setter
    private String name;

    /**
     * 类型
     * ONE TIME 单次
     * RECURRING 循环
     */
    @Getter
    @Setter
    private String type;

    /**
     * 定期-循环值
     */
    @Getter
    @Setter
    private Integer intervalValue;

    /**
     * 定期-循环类型
     */
    @Getter
    @Setter
    private String intervalField;

    /**
     * 状态
     */
    @Getter
    @Setter
    private String status;

    /**
     * 定义者
     */
    @Getter
    @Setter
    private String definer;

    /**
     * 单次-执行时间
     */
    @Getter
    @Setter
    private Object executeAt;

    /**
     * 定期-开始时间
     */
    @Getter
    @Setter
    private Object starts;

    /**
     * 定期-开始循环值
     */
    @Getter
    @Setter
    private Integer startIntervalValue;

    /**
     * 定期-开始循环类型
     */
    @Getter
    @Setter
    private String startIntervalField;

    /**
     * 定期-结束时间
     */
    @Getter
    @Setter
    private Object ends;

    /**
     * 定期-结束循环值
     */
    @Getter
    @Setter
    private Integer endIntervalValue;

    /**
     * 定期-结束循环类型
     */
    @Getter
    @Setter
    private String endIntervalField;

    /**
     * 数据库名称
     */
    @Getter
    @Setter
    private String dbName;

    /**
     * 注释
     */
    @Getter
    @Setter
    private String comment;

    /**
     * 定义
     */
    @Getter
    @Setter
    private String definition;

    /**
     * 完成时
     */
    @Getter
    @Setter
    private String onCompletion;

    /**
     * 创建定义
     */
    @Getter
    private String createDefinition;

    @Override
    public void copy(MysqlEvent obj) {
        this.setEnds(obj.getEnds());
        this.setType(obj.getType());
        this.setStarts(obj.getStarts());
        this.setStatus(obj.getStatus());
        this.setDefiner(obj.getDefiner());
        this.setComment(obj.getComment());
        this.setExecuteAt(obj.getExecuteAt());
        this.setDefinition(obj.getDefinition());
        this.setOnCompletion(obj.getOnCompletion());
        this.setIntervalValue(obj.getIntervalValue());
        this.setIntervalField(obj.getIntervalField());
        this.setCreateDefinition(obj.getCreateDefinition());
    }

    public boolean isNew() {
        return StrUtil.isBlank(this.getDefinition());
    }

    @Override
    public boolean compare(MysqlEvent value) {
        if (value == null) {
            return false;
        }
        return StrUtil.equalsIgnoreCase(this.dbName, value.dbName) && StrUtil.equalsIgnoreCase(this.name, value.name);
    }

    public void setCreateDefinition(String createDefinition) {
        this.createDefinition = createDefinition;
        if (StrUtil.isNotBlank(createDefinition)) {
            String[] arr = createDefinition.split(" ");
            for (String string : arr) {
                if (StrUtil.startWithIgnoreCase(string, "DEFINER=")) {
                    this.definer = string.substring(8);
                    break;
                }
            }
        }
    }

    public boolean isOnTimeType() {
        return StrUtil.equalsIgnoreCase("ONE TIME", this.type);
    }

    public boolean isRecurringType() {
        return StrUtil.equalsIgnoreCase("RECURRING", this.type);
    }

    public Object executeAt() {
        if (this.executeAt instanceof Date date) {
            Object val = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
            return DBUtil.wrapData(val);
        }
        return this.executeAt;
    }

    public Object starts() {
        if (this.starts instanceof Date date) {
            Object val = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
            return DBUtil.wrapData(val);
        }
        return this.starts;
    }

    public Object ends() {
        if (this.ends instanceof Date date) {
            Object val = DateUtil.format(date, "yyyy-MM-dd HH:mm:ss");
            return DBUtil.wrapData(val);
        }
        return this.ends;
    }

    public boolean isEnable() {
        return StrUtil.equalsIgnoreCase("ENABLE", this.status);
    }

    public boolean isPreserve() {
        return StrUtil.equalsIgnoreCase("PRESERVE", this.onCompletion);
    }
}
