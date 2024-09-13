package cn.oyzh.easymysql.dto;

import cn.hutool.extra.spring.SpringUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.StaticLog;
import cn.oyzh.easymysql.domain.MysqlInfo;
import cn.oyzh.fx.common.dto.Project;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * db连接导出对象
 *
 * @author oyzh
 * @since 2023/06/22
 */
public class MysqlInfoExport {

    /**
     * 导出程序版本号
     */
    @Getter
    private String version;

    /**
     * 平台
     */
    @Getter
    private String platform;

    /**
     * 导出连接数据
     */
    @Getter
    private List<MysqlInfo> connects;

    /**
     * 从db连接数据生成
     *
     * @param dbInfos 连接列表
     * @return DBInfoExport
     */
    public static MysqlInfoExport fromConnects(@NonNull List<MysqlInfo> dbInfos) {
        MysqlInfoExport export = new MysqlInfoExport();
        Project project = SpringUtil.getBean(Project.class);
        export.version = project.getVersion();
        export.connects = dbInfos;
        export.platform = System.getProperty("os.name");
        return export;
    }

    /**
     * 从json对象数据生成
     *
     * @param json json字符串
     * @return RedisInfoExport
     */
    public static MysqlInfoExport fromJSON(@NonNull String json) {
        StaticLog.info("json: {}", json);
        JSONObject object = JSONUtil.parseObj(json);
        MysqlInfoExport export = new MysqlInfoExport();
        export.connects = new ArrayList<>();
        export.version = object.getStr("version");
        export.connects = object.getBeanList("connects", MysqlInfo.class);
        return export;
    }

    /**
     * 转成json字符串
     *
     * @return json字符串
     */
    public String toJSONString() {
        return JSONUtil.toJsonStr(this);
    }
}
