package cn.oyzh.easymysql.dto;

import cn.oyzh.common.dto.Project;
import cn.oyzh.common.json.JSONUtil;
import cn.oyzh.common.log.JulLog;
import cn.oyzh.easymysql.domain.MysqlConnect;
import com.alibaba.fastjson2.JSONObject;

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
    private String version;

    /**
     * 平台
     */
    private String platform;

    /**
     * 导出连接数据
     */
    private List<MysqlConnect> connects;

    /**
     * 从db连接数据生成
     *
     * @param dbInfos 连接列表
     * @return 导出对象
     */
    public static MysqlInfoExport fromConnects(List<MysqlConnect> dbInfos) {
        MysqlInfoExport export = new MysqlInfoExport();
        Project project = Project.load();
        export.version = project.getVersion();
        export.connects = dbInfos;
        export.platform = System.getProperty("os.name");
        return export;
    }

    /**
     * 从json对象数据生成
     *
     * @param json json字符串
     * @return 导出对象
     */
    public static MysqlInfoExport fromJSON(String json) {
        JulLog.info("json: {}", json);
        JSONObject object = JSONUtil.parseObject(json);
        MysqlInfoExport export = new MysqlInfoExport();
        export.connects = new ArrayList<>();
        export.version = object.getString("version");
        export.connects = JSONUtil.toList(object, "connects", MysqlConnect.class);
        return export;
    }

    /**
     * 转成json字符串
     *
     * @return json字符串
     */
    public String toJSONString() {
        return JSONUtil.toJson(this);
    }

    /**
     * 获取程序版本号
     *
     * @return 程序版本号
     */
    public String getVersion() {
        return version;
    }

    /**
     * 设置程序版本号
     *
     * @param version 程序版本号
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * 获取平台
     *
     * @return 平台
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * 设置平台
     *
     * @param platform 平台
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * 获取连接列表
     *
     * @return 连接列表
     */
    public List<MysqlConnect> getConnects() {
        return connects;
    }

    /**
     * 设置连接列表
     *
     * @param connects 连接列表
     */
    public void setConnects(List<MysqlConnect> connects) {
        this.connects = connects;
    }
}
