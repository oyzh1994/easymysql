package cn.oyzh.easymysql.db.data;// package cn.oyzh.easymysql.db.data;
//
// import cn.hutool.core.util.StrUtil;
// import cn.oyzh.fx.common.file.SkipAbleFileReader;
// import com.alibaba.fastjson.JSONReader;
// import lombok.Data;
// import lombok.Getter;
// import lombok.NonNull;
// import lombok.Setter;
// import lombok.experimental.Accessors;
//
// import java.io.File;
// import java.io.IOException;
// import java.nio.charset.Charset;
// import java.rmi.RemoteException;
// import java.util.ArrayList;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
//
// /**
//  * @author oyzh
//  * @since 2024-09-02
//  */
// public class DataJsonFileReader extends SkipAbleFileReader {
//
//     @Getter
//     @Setter
//     private String rootName = "RECORDS";
//
//     private boolean readRootAlready;
//
//     public DataJsonFileReader(@NonNull String filePath) {
//         super(filePath);
//     }
//
//     public DataJsonFileReader(@NonNull File file) {
//         super(file);
//     }
//
//     public DataJsonFileReader(@NonNull File file, Charset charset) {
//         super(file, charset);
//     }
//
//     private void doReadRoot() throws IOException {
//         if (this.readRootAlready) {
//             return;
//         }
//         boolean oStart = false;
//         boolean rStart = false;
//         StringBuilder nameBuilder = new StringBuilder();
//         while (this.ready()) {
//             int i = this.read();
//             if (i == -1) {
//                 break;
//             }
//             char c = (char) i;
//             if (c == '{') {
//                 if (this.rootName == null) {
//                     this.readRootAlready = true;
//                     break;
//                 }
//                 oStart = true;
//                 continue;
//             }
//             if (c == '[') {
//                 oStart = true;
//                 continue;
//             }
//             if (!oStart) {
//                 continue;
//             }
//             if (rStart && c == '\"') {
//                 if (StrUtil.equalsIgnoreCase(nameBuilder.toString(), this.rootName)) {
//                     this.readRootAlready = true;
//                 }
//                 break;
//             }
//             if (c == '\"') {
//                 rStart = true;
//                 continue;
//             }
//             if (rStart) {
//                 nameBuilder.append(c);
//             }
//         }
//         if (!this.readRootAlready) {
//             throw new RemoteException("Root node not found");
//         }
//     }
//
//     @Data
//     @Accessors(fluent = true, chain = true)
//     private static class JsonToken {
//
//         private Character prevChar;
//
//         private boolean objStart;
//
//         private boolean keyStart;
//
//         private boolean keyFinish;
//
//         private boolean valueStart;
//
//         private boolean valueFinish;
//
//         private StringBuilder keyBuilder;
//
//         private StringBuilder valueBuilder;
//
//         public void appendKey(char c) {
//             if (this.keyBuilder == null) {
//                 this.keyBuilder = new StringBuilder();
//             }
//             this.keyBuilder.append(c);
//         }
//
//         public void appendValue(char c) {
//             if (this.valueBuilder == null) {
//                 this.valueBuilder = new StringBuilder();
//             }
//             this.valueBuilder.append(c);
//         }
//
//         public String getKey() {
//             if (this.keyBuilder == null) {
//                 return null;
//             }
//             return this.keyBuilder.toString().trim();
//         }
//
//         private String getValueTrim() {
//             if (this.valueBuilder == null) {
//                 return null;
//             }
//             String val = this.valueBuilder.toString();
//             val = val.trim();
//             return val;
//         }
//
//         public Object getValue() {
//             if (this.valueBuilder == null) {
//                 return null;
//             }
//             String val = this.valueBuilder.toString();
//             val = val.trim();
//             switch (val) {
//                 // null
//                 case "null" -> {
//                     return null;
//                 }
//                 // bool true
//                 case "true" -> {
//                     return true;
//                 }
//                 // bool false
//                 case "false" -> {
//                     return false;
//                 }
//                 // 空字符串
//                 case "" -> {
//                     return "";
//                 }
//             }
//             // string
//             if (val.startsWith("\"")) {
//                 return val.substring(1);
//             }
//             // float
//             if (val.contains(".")) {
//                 return Double.valueOf(val);
//             }
//             // number
//             return Long.valueOf(val);
//         }
//
//         public void clear() {
//             this.keyBuilder = null;
//             this.valueBuilder = null;
//             this.keyStart = false;
//             this.keyFinish = false;
//             this.valueStart = false;
//             this.valueFinish = false;
//         }
//
//         public boolean checkKeyEnd(char c) {
//             if (this.keyStart) {
//                 if (c == '\"') {
//                     return this.prevChar == null || this.prevChar != '\\';
//                 }
//             }
//             return false;
//         }
//
//         public boolean checkValueEnd(char c) {
//             if (this.valueStart) {
//                 String val = this.getValueTrim();
//                 if (val == null) {
//                     return false;
//                 }
//                 if (c == '\"') {
//                     if (this.prevChar != null && this.prevChar == '\\') {
//                         return false;
//                     }
//                     return val.startsWith("\"");
//                 }
//                 if (c == ',') {
//                     if (val.startsWith("\"")) {
//                         if (val.endsWith("\\\"")) {
//                             return false;
//                         }
//                         return val.endsWith("\"");
//                     }
//                     return !val.startsWith("\"") && !val.endsWith("\"");
//                 }
//             }
//             return false;
//         }
//
//         public boolean checkObjEnd(char c) {
//             if (!this.keyStart && !this.valueStart && this.objStart) {
//                 return c == '}';
//             }
//             return false;
//         }
//
//         public boolean checkObjStart(char c) {
//             return !this.objStart && c == '{';
//         }
//     }
//
//     /**
//      * 读取下个记录
//      *
//      * @return 记录
//      * @throws IOException 异常
//      */
//     public Map<String, Object> readNextRecord() throws IOException {
//         this.doReadRoot();
//         Map<String, Object> record = null;
//         JsonToken token = new JsonToken();
//         while (this.ready()) {
//             int i = this.read();
//             if (i == -1) {
//                 break;
//             }
//             char c = (char) i;
//             if (c == '\n') {
//                 continue;
//             }
//             if (token.checkObjStart(c)) {
//                 token.objStart(true);
//                 continue;
//             }
//             if (!token.objStart()) {
//                 continue;
//             }
//             if (token.checkObjEnd(c)) {
//                 break;
//             }
//             try {
//                 if (!token.keyFinish()) {
//                     if (token.keyStart()) {
//                         if (token.checkKeyEnd(c)) {
//                             token.keyStart(false);
//                             token.keyFinish(true);
//                         } else {
//                             token.appendKey(c);
//                         }
//                     } else if (c == '\"') {
//                         token.keyStart(true);
//                     }
//                     continue;
//                 }
//                 if (!token.valueFinish()) {
//                     if (token.valueStart()) {
//                         if (token.checkValueEnd(c)) {
//                             token.valueStart(false);
//                             token.valueFinish(true);
//                             if (record == null) {
//                                 record = new HashMap<>();
//                             }
//                             record.put(token.getKey(), token.getValue());
//                             token.clear();
//                         } else {
//                             token.appendValue(c);
//                         }
//                     } else if (c == ':') {
//                         token.valueStart(true);
//                     }
//                 }
//             } finally {
//                 token.prevChar(c);
//             }
//         }
//         return record;
//     }
//
//     // public List<Map<String, Object>> readRecords(int count) throws IOException {
//     //     List<Map<String, Object>> records = new ArrayList<>();
//     //     int c = 0;
//     //     while (true) {
//     //         Map<String, Object> record = this.readNextRecord();
//     //         if (record == null) {
//     //             break;
//     //         }
//     //         records.add(record);
//     //         if (++c >= count) {
//     //             break;
//     //         }
//     //     }
//     //     return records;
//     // }
//
//
//     private JSONReader jsonReader;
//
//     public List<Map<String, Object>> readRecords(int count) {
//         // 数据列表
//         List<Map<String, Object>> records = new ArrayList<>();
//         // 初始化读取器
//         if (this.jsonReader == null) {
//             this.jsonReader = new JSONReader(this.reader);
//             this.jsonReader.startObject();
//             // 初始化
//             if (this.jsonReader.hasNext()) {
//                 String key = this.jsonReader.readString();
//                 if (key.equalsIgnoreCase(this.rootName)) {
//                     this.jsonReader.startArray();
//                 }
//             }
//         }
//         // 结束标志位
//         boolean endFlag = true;
//         // 读取数据
//         while (this.jsonReader.hasNext()) {
//             HashMap item =  this.jsonReader.readObject(HashMap.class);
//             records.add(item);
//             if (records.size() >= count) {
//                 endFlag = false;
//                 break;
//             }
//         }
//         // 结束
//         if (endFlag) {
//             this.jsonReader.endArray();
//             this.jsonReader.endObject();
//         }
//         return records;
//     }
// }
