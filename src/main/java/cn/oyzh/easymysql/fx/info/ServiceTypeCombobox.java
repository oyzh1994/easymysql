package cn.oyzh.easymysql.fx.info;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.fx.plus.controls.combo.FlexComboBox;
import cn.oyzh.fx.plus.i18n.I18nHelper;

/**
 * @author oyzh
 * @since 2024-09-06
 */
public class ServiceTypeCombobox extends FlexComboBox<String> {

    {
        this.addItem(I18nHelper.serviceName());
        this.addItem("SID");
    }

    public void init(String serviceType) {
        if (StrUtil.equalsIgnoreCase("SID", serviceType)) {
            this.select(1);
        } else {
            this.select(0);
        }
    }
}
