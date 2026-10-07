package cn.oyzh.easymysql.fx.record;


import cn.oyzh.fx.gui.text.field.LimitTextField;
import javafx.scene.control.Skin;

/**
 * json文本输入框
 *
 * @author oyzh
 * @since 2024/7/21
 */
@Deprecated
public class DBJsonTextFiled extends LimitTextField {

    /**
     * 构造json文本输入框
     */
    public DBJsonTextFiled() {
        this.setSkin(new DBJsonTextFiledSkin(this));
    }

    @Override
    public DBJsonTextFiledSkin skin() {
        return (DBJsonTextFiledSkin) super.skin();
    }

    @Override
    protected DBJsonTextFiledSkin createDefaultSkin() {
        return new DBJsonTextFiledSkin(this);
    }

    /** 设置展开宽度 */
    public void setEnlargeWidth(double width) {
        this.skin().setEnlargeWidth(width);
    }

    /** 获取展开宽度 */
    public double getEnlargeWidth() {
        return this.skin().getEnlargeWidth();
    }

    /** 设置展开高度 */
    public void setEnlargeHeight(double height) {
        this.skin().setEnlargeHeight(height);
    }

    /** 获取展开高度 */
    public double getEnlargeHeight() {
        return this.skin().getEnlargeHeight();
    }
}
