package cn.oyzh.easymysql.condition;

import cn.hutool.core.util.StrUtil;
import cn.oyzh.easymysql.condition.DBBetweenCondition;
import cn.oyzh.easymysql.condition.DBContainsCondition;
import cn.oyzh.easymysql.condition.DBEmptyCondition;
import cn.oyzh.easymysql.condition.DBEndWithCondition;
import cn.oyzh.easymysql.condition.DBGtEqCondition;
import cn.oyzh.easymysql.condition.DBInListCondition;
import cn.oyzh.easymysql.condition.DBLtEqCondition;
import cn.oyzh.easymysql.condition.DBNotBetweenCondition;
import cn.oyzh.easymysql.condition.DBNotContainsCondition;
import cn.oyzh.easymysql.condition.DBNotEmptyCondition;
import cn.oyzh.easymysql.condition.DBNotEndWithCondition;
import cn.oyzh.easymysql.condition.DBNotEqCondition;
import cn.oyzh.easymysql.condition.DBNotInListCondition;
import cn.oyzh.easymysql.condition.DBNotNullCondition;
import cn.oyzh.easymysql.condition.DBNotStartWithCondition;
import cn.oyzh.easymysql.condition.DBNullCondition;
import cn.oyzh.easymysql.condition.DBStartWithCondition;
import cn.oyzh.easymysql.db.record.DBRecordFilter;
import cn.oyzh.easymysql.db.table.DBColumn;
import cn.oyzh.easymysql.util.DBNodeUtil;
import cn.oyzh.easymysql.util.DBUtil;
import cn.oyzh.fx.plus.controls.textfield.ClearableTextField;
import javafx.scene.Node;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

/**
 * 条件工具类
 *
 * @author oyzh
 * @since 2024/6/26
 */
@UtilityClass
public class DBConditionUtil {

    /**
     * 获取条件
     *
     * @return 条件列表
     */
    public static List<DBCondition> conditions() {
        List<DBCondition> conditions = new ArrayList<>();
        conditions.add(DBContainsCondition.INSTANCE);
        conditions.add(DBNotContainsCondition.INSTANCE);
        conditions.add(DBEqCondition.INSTANCE);
        conditions.add(DBGtCondition.INSTANCE);
        conditions.add(DBLtCondition.INSTANCE);
        conditions.add(DBNotEqCondition.INSTANCE);
        conditions.add(DBNullCondition.INSTANCE);
        conditions.add(DBNotNullCondition.INSTANCE);
        conditions.add(DBEmptyCondition.INSTANCE);
        conditions.add(DBNotEmptyCondition.INSTANCE);
        conditions.add(DBLtEqCondition.INSTANCE);
        conditions.add(DBGtEqCondition.INSTANCE);
        conditions.add(DBInListCondition.INSTANCE);
        conditions.add(DBNotInListCondition.INSTANCE);
        conditions.add(DBBetweenCondition.INSTANCE);
        conditions.add(DBNotBetweenCondition.INSTANCE);
        conditions.add(DBStartWithCondition.INSTANCE);
        conditions.add(DBEndWithCondition.INSTANCE);
        conditions.add(DBNotStartWithCondition.INSTANCE);
        conditions.add(DBNotEndWithCondition.INSTANCE);
        return conditions;
    }

    /**
     * 构建条件
     *
     * @param filters 过滤条件
     * @return 条件
     */
    public static String buildCondition(List<DBRecordFilter> filters) throws Exception {
        if (filters == null || filters.isEmpty()) {
            return "";
        }
        StringBuilder conditions = new StringBuilder();
        for (int i = 0; i < filters.size(); i++) {
            DBRecordFilter filter = filters.get(i);
            String condition = filter.condition();
            if (StrUtil.isNotBlank(condition)) {
                conditions.append(DBUtil.wrap(filter.column())).append(" ").append(condition).append(" ");
            }
            if (i != filters.size() - 1) {
                conditions.append(filter.getJoinSymbol()).append(" ");
            }
        }
        return conditions.toString();
    }

    /**
     * 是否in条件
     *
     * @param condition 条件
     * @return 结果
     */
    public static boolean isInCondition(DBCondition condition) {
        return condition == DBInListCondition.INSTANCE || condition == DBNotInListCondition.INSTANCE;
    }

    /**
     * 是否介于条件
     *
     * @param condition 条件
     * @return 结果
     */
    public static boolean isBetweenCondition(DBCondition condition) {
        return condition == DBBetweenCondition.INSTANCE || condition == DBNotBetweenCondition.INSTANCE;
    }

    /**
     * 生成节点
     *
     * @param column    字段
     * @param condition 条件
     * @return 节点
     */
    public static List<Node> generateNode(DBColumn column, DBCondition condition) {
        condition = condition == null ? conditions().getFirst() : condition;
        List<Node> list = new ArrayList<>();
        if (isInCondition(condition)) {
            ClearableTextField node = new ClearableTextField();
            node.setDisable(!condition.isRequireCondition());
            list.add(node);
        } else if (isBetweenCondition(condition)) {
            Node node1 = DBNodeUtil.generateNode(column, false);
            Node node2 = DBNodeUtil.generateNode(column, false);
            node1.setDisable(!condition.isRequireCondition());
            node2.setDisable(!condition.isRequireCondition());
            list.add(node1);
            list.add(node2);
        } else {
            Node node = DBNodeUtil.generateNode(column, false);
            node.setDisable(!condition.isRequireCondition());
            list.add(node);
        }
        return list;
    }

    /**
     * 设置节点值
     *
     * @param controls 组件
     * @param value    值
     */
    public static void setNodeVal(List<Node> controls, Object value) {
        for (int i = 0; i < controls.size(); i++) {
            if (value instanceof List<?> list) {
                DBNodeUtil.setNodeVal(controls.get(i), list.get(i));
            } else {
                DBNodeUtil.setNodeVal(controls.get(i), value);
            }
        }
    }

    /**
     * 获取节点值
     *
     * @param controls 组件
     * @return 值
     */
    public static Object getNodeVal(List<Node> controls) throws Exception {
        if (controls == null || controls.isEmpty()) {
            return null;
        }
        if (controls.size() == 1) {
            return DBNodeUtil.getNodeVal(controls.getFirst());
        }
        List<Object> list = new ArrayList<>();
        for (Node control : controls) {
            list.add(DBNodeUtil.getNodeVal(control));
        }
        return list;
    }
}
