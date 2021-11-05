package edu.nyu.queryprocessor.util;

import weka.core.Stopwords;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DocParserUtil {
    static String maxWordLength;

    static {
        try {
            maxWordLength = new ConfigUtil().getConfig("max_word_length");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DocParserUtil() throws IOException {
    }

    public static boolean check(String input) {
        Pattern pattern=Pattern.compile("(?<=(<DOCNO>))(\\w|\\d|\\n|[().,\\-:;@#$%^&*\\[\\]\"'+–/\\/®°⁰!?{}|`~]| )+?(?=(</DOCNO>))");
        Matcher matcher=pattern.matcher(input);
        return matcher.find();
    }

    public static String countTerms(StringTokenizer stringTokenizer, Integer docId) throws IOException {
        Map<String, Integer> record = new HashMap<>();
        StringBuilder res = new StringBuilder();

        while (stringTokenizer.hasMoreTokens()) {
//            String cur = stringTokenizer.nextToken().replaceAll("[\n`~!@#$%^&*()+=|{}';',\\[\\]<>?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？\"]+","");
            String cur = stringTokenizer.nextToken();
            if (StringUtil.isEnglish(cur) && !Stopwords.isStopword(cur) && cur.length() < Integer.parseInt(maxWordLength)) {
                record.put(cur.toLowerCase(), record.getOrDefault(cur, 0) + 1);
            }
        }
        for (Map.Entry<String, Integer> each : record.entrySet()) {
            if (each.getKey().equals("")) {
                continue;
            }
            res.append(each.getKey()+":"+docId+","+each.getValue()+"\n");
        }
        return res.toString();
    }

    public static void read(File file) throws IOException {
        DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file));
        while (dataInputStream.available() > 0){
            System.out.println(Integer.toBinaryString(dataInputStream.readByte()));
        }
    }
}
