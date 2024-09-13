package cn.oyzh.easymysql.db.routine;

import cn.oyzh.easymysql.db.routine.DBRoutineParam;
import cn.oyzh.easymysql.db.routine.DBRoutineSchema;
import cn.oyzh.fx.common.util.ObjectCopier;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oyzh
 * @since 2024/06/29
 */
@Setter
@Getter
public class DBFunction extends DBRoutineSchema implements ObjectCopier<DBFunction> {

    /**
     * 返回参数
     */
    private DBRoutineParam returnParam;

    @Override
    public void setParams(List<DBRoutineParam> params) {
        List<DBRoutineParam> paramsList = new ArrayList<>();
        for (DBRoutineParam param : params) {
            if (param.isReturnParam()) {
                this.returnParam = param;
            } else {
                paramsList.add(param);
            }
        }
        super.setParams(paramsList);
    }

    public String getReturnType() {
        return this.returnParam == null ? null : this.returnParam.getType();
    }

    @Override
    public void copy(DBFunction function) {
        this.setParams(function.getParams());
        this.setComment(function.getComment());
        this.setDefiner(function.getDefiner());
        this.setDefinition(function.getDefinition());
        this.setReturnParam(function.getReturnParam());
        this.setSecurityType(function.getSecurityType());
        this.setCharacteristic(function.getCharacteristic());
    }
}
