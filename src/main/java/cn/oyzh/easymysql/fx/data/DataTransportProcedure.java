package cn.oyzh.easymysql.fx.data;

import lombok.Data;

/**
 * @author oyzh
 * @since 2024-09-06
 */
@Data
public class DataTransportProcedure {

    /**
     * 过程名称
     */
    private String name;

    /**
     * 是否选中
     */
    private boolean selected = true;
}
