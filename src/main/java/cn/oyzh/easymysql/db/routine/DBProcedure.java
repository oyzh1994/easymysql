package cn.oyzh.easymysql.db.routine;

import cn.oyzh.easymysql.db.routine.DBRoutineSchema;
import cn.oyzh.fx.common.util.ObjectCopier;

/**
 * @author oyzh
 * @since 2024/06/29
 */
public class DBProcedure extends DBRoutineSchema implements ObjectCopier<DBProcedure> {

    @Override
    public void copy(DBProcedure procedure) {
        this.setParams(procedure.getParams());
        this.setDefiner(procedure.getDefiner());
        this.setComment(procedure.getComment());
        this.setDefinition(procedure.getDefinition());
        this.setSecurityType(procedure.getSecurityType());
        this.setCharacteristic(procedure.getCharacteristic());
    }
}
