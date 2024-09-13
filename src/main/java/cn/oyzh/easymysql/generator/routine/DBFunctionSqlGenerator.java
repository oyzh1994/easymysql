package cn.oyzh.easymysql.generator.routine;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.db.routine.DBFunction;
import cn.oyzh.easymysql.db.routine.DBRoutineParam;
import cn.oyzh.easymysql.util.DBUtil;

import java.util.List;

/**
 * 函数sql生成器
 *
 * @author oyzh
 * @since 2024/08/09
 */
public class DBFunctionSqlGenerator {

    public static final DBFunctionSqlGenerator INSTANCE = new DBFunctionSqlGenerator();

    public String generate(DBFunction function) {
        String sql = "CREATE ";
        // 定义者
        if (StrUtil.isNotBlank(function.getDefiner())) {
            sql += " DEFINER = " + function.getDefiner();
        }
        sql += " FUNCTION " + DBUtil.wrap(function.getName());
        // 参数
        sql += " (";
        List<DBRoutineParam> params = function.getParams();
        if (CollUtil.isNotEmpty(params)) {
            for (DBRoutineParam param : params) {
                sql = sql + "\n" + param.getDefinition() + ",";
            }
        }
        sql = StrUtil.replaceLast(sql, ",", "");
        sql += ") ";
        // 返回值
        DBRoutineParam returnParam = function.getReturnParam();
        if (returnParam != null) {
            sql += " \nRETURNS " + returnParam.getDefinition();
        }
        // 注释
        if (StrUtil.isNotBlank(function.getComment())) {
            sql += " \nCOMMENT " + DBUtil.wrapData(function.getComment());
        }
        // 安全性
        if (StrUtil.isNotBlank(function.getSecurityType())) {
            sql += " \nSQL SECURITY " + function.getSecurityType();
        }
        // 特征
        if (StrUtil.isNotBlank(function.getCharacteristic())) {
            sql += " \n" + function.getCharacteristic();
        }
        sql += " \n" + function.getDefinition();
        return sql;
    }
}
