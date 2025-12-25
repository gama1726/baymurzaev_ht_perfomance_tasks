import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/*
 * values.json: {"values":[{"id":1,"value":"..."} , ...]}
 * tests.json:  {"tests":[{"id":1,"title":"...","value":"","values":[...]} , ...]}
 * Нужно проставить value по id и сохранить в report.json.
 *
 * Без внешних библиотек: ниже очень простой JSON парсер.
 */
public class Main {

    // ---------- очень простой JSON ----------
    static class P {
        String s;
        int i;

        P(String s) { this.s = s; }

        void ws() {
            while (i < s.length()) {
                char c = s.charAt(i);
                if (c==' '||c=='\n'||c=='\r'||c=='\t') i++;
                else break;
            }
        }

        Object val() {
            ws();
            char c = s.charAt(i);
            if (c=='{') return obj();
            if (c=='[') return arr();
            if (c=='"') return str();
            if (c=='t' || c=='f') return bool();
            if (c=='n') { i += 4; return null; }
            return num();
        }

        Map<String,Object> obj() {
            Map<String,Object> m = new LinkedHashMap<>();
            i++; // {
            ws();
            if (s.charAt(i)=='}') { i++; return m; }
            while (true) {
                ws();
                String k = str();
                ws();
                i++; // :
                Object v = val();
                m.put(k, v);
                ws();
                if (s.charAt(i)=='}') { i++; break; }
                i++; // ,
            }
            return m;
        }

        List<Object> arr() {
            List<Object> a = new ArrayList<>();
            i++; // [
            ws();
            if (s.charAt(i)==']') { i++; return a; }
            while (true) {
                Object v = val();
                a.add(v);
                ws();
                if (s.charAt(i)==']') { i++; break; }
                i++; // ,
            }
            return a;
        }

        String str() {
            StringBuilder sb = new StringBuilder();
            i++; // "
            while (i < s.length()) {
                char c = s.charAt(i++);
                if (c=='"') break;
                if (c=='\\') {
                    char e = s.charAt(i++);
                    if (e=='"') sb.append('"');
                    else if (e=='\\') sb.append('\\');
                    else if (e=='n') sb.append('\n');
                    else if (e=='r') sb.append('\r');
                    else if (e=='t') sb.append('\t');
                    else sb.append(e);
                } else {
                    sb.append(c);
                }
            }
            return sb.toString();
        }

        Boolean bool() {
            if (s.startsWith("true", i)) { i += 4; return true; }
            i += 5; return false;
        }

        Number num() {
            int st = i;
            if (s.charAt(i)=='-') i++;
            while (i < s.length() && Character.isDigit(s.charAt(i))) i++;
            String t = s.substring(st, i);
            return Long.parseLong(t);
        }
    }

    static String readText(String path) throws Exception {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        InputStream is = new FileInputStream(path);
        byte[] buf = new byte[8192];
        int r;
        while ((r = is.read(buf)) != -1) bos.write(buf, 0, r);
        is.close();
        return bos.toString(StandardCharsets.UTF_8.name());
    }

    static void writeText(String path, String text) throws Exception {
        OutputStream os = new FileOutputStream(path);
        os.write(text.getBytes(StandardCharsets.UTF_8));
        os.close();
    }

    static void buildMap(Object valuesRoot, Map<Long,Object> map) {
        if (!(valuesRoot instanceof Map)) return;
        Object vv = ((Map<?,?>) valuesRoot).get("values");
        if (!(vv instanceof List)) return;

        for (Object it : (List<?>) vv) {
            if (!(it instanceof Map)) continue;
            Map<?,?> m = (Map<?,?>) it;
            Object id = m.get("id");
            Object value = m.get("value");
            if (id instanceof Number) {
                map.put(((Number) id).longValue(), value);
            }
        }
    }

    @SuppressWarnings("unchecked")
    static void fill(Object node, Map<Long,Object> map) {
        if (node instanceof Map) {
            Map<String,Object> m = (Map<String,Object>) node;

            Object id = m.get("id");
            if (id instanceof Number) {
                long k = ((Number) id).longValue();
                if (map.containsKey(k)) {
                    m.put("value", map.get(k));
                }
            }

            Object tests = m.get("tests");
            if (tests instanceof List) fill(tests, map);

            Object values = m.get("values");
            if (values instanceof List) fill(values, map);

        } else if (node instanceof List) {
            for (Object it : (List<?>) node) fill(it, map);
        }
    }

    // простой вывод (чтобы было читаемо)
    static void dump(Object v, StringBuilder sb, int ind) {
        if (v == null) { sb.append("null"); return; }
        if (v instanceof String) {
            sb.append('"');
            String s = (String) v;
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c=='"') sb.append("\\\"");
                else if (c=='\\') sb.append("\\\\");
                else if (c=='\n') sb.append("\\n");
                else sb.append(c);
            }
            sb.append('"');
            return;
        }
        if (v instanceof Number || v instanceof Boolean) {
            sb.append(v.toString());
            return;
        }
        if (v instanceof Map) {
            Map<?,?> m = (Map<?,?>) v;
            sb.append("{\n");
            int k = 0;
            for (Map.Entry<?,?> e : m.entrySet()) {
                for (int t = 0; t < ind + 2; t++) sb.append(' ');
                sb.append('"').append(e.getKey()).append('"').append(": ");
                dump(e.getValue(), sb, ind + 2);
                k++;
                if (k < m.size()) sb.append(",");
                sb.append("\n");
            }
            for (int t = 0; t < ind; t++) sb.append(' ');
            sb.append("}");
            return;
        }
        if (v instanceof List) {
            List<?> a = (List<?>) v;
            sb.append("[\n");
            for (int i = 0; i < a.size(); i++) {
                for (int t = 0; t < ind + 2; t++) sb.append(' ');
                dump(a.get(i), sb, ind + 2);
                if (i + 1 < a.size()) sb.append(",");
                sb.append("\n");
            }
            for (int t = 0; t < ind; t++) sb.append(' ');
            sb.append("]");
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java Main values.json tests.json report.json");
            return;
        }

        Object valuesRoot = new P(readText(args[0])).val();
        Object testsRoot = new P(readText(args[1])).val();

        HashMap<Long,Object> map = new HashMap<>();
        buildMap(valuesRoot, map);

        fill(testsRoot, map);

        StringBuilder sb = new StringBuilder();
        dump(testsRoot, sb, 0);
        writeText(args[2], sb.toString());
    }
}
