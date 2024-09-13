package cn.oyzh.easymysql.generator.routine;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.routine.DBProcedure;
import cn.oyzh.easymysql.db.routine.DBRoutineParam;
import cn.oyzh.easymysql.util.DBUtil;

import java.util.List;

/**
 * 国策sql生成器
 *
 * @author oyzh
 * @since 2024/08/09
 */
public class DBProcedureSqlGenerator {

    public static final DBProcedureSqlGenerator INSTANCE = new DBProcedureSqlGenerator();

    public String generate(DBProcedure procedure) {
        String sql = "CREATE ";
        // 定义者
        if (StrUtil.isNotBlank(procedure.getDefiner())) {
            sql += " DEFINER = " + procedure.getDefiner();
        }
        sql += " PROCEDURE " + DBUtil.wrap(procedure.getName());
        // 参数
        sql += " (";
        List<DBRoutineParam> params = procedure.getParams();
        if (CollUtil.isNotEmpty(params)) {
            for (DBRoutineParam param : params) {
                sql = sql + "\n" + param.getDefinition() + ",";
            }
        }
        sql = StrUtil.replaceLast(sql, ",", "");
        sql += ") ";
        // 注释
        if (StrUtil.isNotBlank(procedure.getComment())) {
            sql += " \nCOMMENT " + DBUtil.wrapData(procedure.getComment());
        }
        // 安全性
        if (StrUtil.isNotBlank(procedure.getSecurityType())) {
            sql += " \nSQL SECURITY " + procedure.getSecurityType();
        }
        // 特征
        if (StrUtil.isNotBlank(procedure.getCharacteristic())) {
            sql += " \n" + procedure.getCharacteristic();
        }
        sql += " \n" + procedure.getDefinition();
        return sql;
    }
}
