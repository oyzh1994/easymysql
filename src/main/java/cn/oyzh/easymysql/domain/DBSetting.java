package cn.oyzh.easymysql.domain;


import cn.oyzh.fx.plus.domain.Setting;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * db设置
 *
 * @author oyzh
 * @since 2023/6/16
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class DBSetting extends Setting {

    /**
     * 记录每页限制
     */
    private Integer recordPageLimit;

    public void setRecordPageLimit(Integer recordPageLimit) {
        if (recordPageLimit == null || recordPageLimit <= 0) {
            this.recordPageLimit = 100;
        } else {
            this.recordPageLimit = recordPageLimit;
        }
    }

    public Integer getRecordPageLimit() {
        if (this.recordPageLimit == null || this.recordPageLimit <= 0) {
            return 100;
        }
        return this.recordPageLimit;
    }
}
