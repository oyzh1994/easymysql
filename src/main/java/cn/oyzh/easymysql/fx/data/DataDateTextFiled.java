package cn.oyzh.easymysql.fx.data;

import cn.oyzh.fx.plus.controls.select.SelectTextFiled;

/**
 * @author oyzh
 * @since 2024/9/2
 */
public class DataDateTextFiled extends SelectTextFiled {

    {
        this.addData("yyyy-MM-dd HH:mm:ss");
        this.addData("yyyy/MM/dd HH:mm:ss");

        this.addData("yyyy-M-d HH:mm:ss");
        this.addData("yyyy/M/d HH:mm:ss");

        this.addData("dd-MM-yyyy HH:mm:ss");
        this.addData("dd/MM/yyyy HH:mm:ss");

        this.addData("MM-dd-yyyy HH:mm:ss");
        this.addData("MM/dd/yyyy HH:mm:ss");

        this.addData("M-d-yyyy HH:mm:ss");
        this.addData("M/d/yyyy HH:mm:ss");

        this.addData("d-M-yyyy HH:mm:ss");
        this.addData("d/M/yyyy HH:mm:ss");
    }
}
