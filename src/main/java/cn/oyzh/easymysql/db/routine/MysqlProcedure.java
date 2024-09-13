package cn.oyzh.easymysql.db.routine;

import cn.oyzh.fx.common.util.ObjectCopier;

/**
 * @author oyzh
 * @since 2024/06/29
 */
public class MysqlProcedure extends MysqlRoutineSchema implements ObjectCopier<MysqlProcedure> {

    @Override
    public void copy(MysqlProcedure procedure) {
        this.setParams(procedure.getParams());
        this.setDefiner(procedure.getDefiner());
        this.setComment(procedure.getComment());
        this.setDefinition(procedure.getDefinition());
        this.setSecurityType(procedure.getSecurityType());
        this.setCharacteristic(procedure.getCharacteristic());
    }
}
