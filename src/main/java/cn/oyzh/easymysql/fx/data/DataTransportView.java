package cn.oyzh.easymysql.fx.data;

/**
 * 数据传输视图
 *
 * @author oyzh
 * @since 2024-09-06
 */
public class DataTransportView {

    /**
     * 视图名称
     */
    private String name;

    /**
     * 是否选中
     */
    private boolean selected = true;

    /** 获取视图名称 */
    public String getName() {
        return name;
    }

    /** 设置视图名称 */
    public void setName(String name) {
        this.name = name;
    }

    /** 是否选中 */
    public boolean isSelected() {
        return selected;
    }

    /** 设置是否选中 */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
