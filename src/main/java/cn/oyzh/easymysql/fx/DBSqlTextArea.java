package cn.oyzh.easymysql.fx;

import cn.oyzh.common.thread.TaskManager;
import cn.oyzh.easymysql.db.DBDialect;
import cn.oyzh.easymysql.query.DBQueryUtil;
import cn.oyzh.easymysql.sql.DBSqlParser;
import cn.oyzh.fx.plus.util.FXUtil;
import cn.oyzh.fx.rich.RichTextStyle;
import cn.oyzh.fx.rich.richtextfx.control.BaseRichTextArea;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * db查询文本域
 *
 * @author oyzh
 * @since 2024/02/18
 */
public class DBSqlTextArea extends BaseRichTextArea {

    {
        this.showLineNum();
        this.addTextChangeListener((observable, oldValue, newValue) -> this.initTextStyle());
    }

    /**
     * 方言
     */
    @Setter
    @Getter
    private DBDialect dialect;

    /**
     * 美化sql
     *
     */
    public void pretty() throws Exception {
        String sql = this.getText();
        String prettySql = DBSqlParser.prettySql(sql, this.dialect);
        this.setText(prettySql);
        this.initTextStyle();
    }

    /**
     * sql关键字正则模式
     */
    private static Pattern Sql_Symbol_Pattern;

    private static Pattern sqlSymbolPattern() {
        if (Sql_Symbol_Pattern == null) {
            StringBuilder keywords = new StringBuilder();
            for (String keyword : DBQueryUtil.getKeywords()) {
                keywords.append("|").append(keyword);
            }
            String regex = "(?i)\\b(" + keywords.substring(1) + ")\\b";
            Sql_Symbol_Pattern = Pattern.compile(regex);
        }
        return Sql_Symbol_Pattern;
    }

    @Override
    public void initTextStyle() {
        Runnable task = () -> {
            this.clearTextStyle();
            String text = this.getText();
            if (!text.isEmpty()) {
                Matcher matcher1 = sqlSymbolPattern().matcher(text);
                List<RichTextStyle> styles = new ArrayList<>();
                while (matcher1.find()) {
                    styles.add(new RichTextStyle(matcher1.start(), matcher1.end(), "-fx-fill: #4169E1;"));
                }
                for (RichTextStyle style : styles) {
                    this.setStyle(style);
                }
            }
        };
        TaskManager.startDelay("db:sql:initTextStyle:" + this.hashCode(), () -> FXUtil.runLater(task), 150);
    }
}
