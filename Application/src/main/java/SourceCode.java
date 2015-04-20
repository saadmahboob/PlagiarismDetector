import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by nurlan on 4/20/15.
 */
public class SourceCode {
    String fileName;
    String raw;
    public SourceCode(String fileName) {
        try {
            this.fileName = fileName;
            File file = new File(fileName);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(file), "UTF8"));

            StringBuilder stringBuilder = new StringBuilder();
            raw = "";
            String str;
            StringTokenizer stringTokenizer = null;
            while ((str = in.readLine()) != null) {
                stringTokenizer = new StringTokenizer(str);
                while (stringTokenizer.hasMoreTokens())
                    stringBuilder.append(stringTokenizer.nextToken());
            }
            raw = stringBuilder.toString().toLowerCase();
            in.close();
            preprocess();
        }
        catch (UnsupportedEncodingException e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        catch (IOException e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
            System.exit(-1);
        }
    }

    // TODO: implement preprocessing
    public void preprocess() {
        final int limit = 2;
        int opened = 0;
        String opening = "{", closing = "}";
        if (fileName.endsWith(".pas")) {
            opening = "begin";
            closing = "end";
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0 ; i < raw.length() ; i++) {
            if (i + opening.length() <= raw.length() && raw.substring(i, i + opening.length()).equals(opening)) {
                opened ++;
            }
            if (i + closing.length() <= raw.length() && raw.substring(i, i + closing.length()).equals(closing)) {
                opened --;
            }
            if (opened >= limit) {
                stringBuilder.append(raw.charAt(i));
            }
        }
        raw = stringBuilder.toString();
        //System.out.println(raw);
    }
    public LinkedHashMap<Long, ArrayList<Integer> > getFingerprints(Parameters parameters) {
        int K = parameters.K;
        LinkedHashMap<Long, ArrayList<Integer> > map = new LinkedHashMap<Long, ArrayList<Integer>>();

        // Rolling hashes of length K
        // of the form ( s[i] * p^(k-1) + s[i+1] * p^(k-2) + ... + s[i + K - 2] * p + s[i-K+1] )
        long currentHash = 0;
        int p = parameters.p;
        long pp = 1L; // will be p^(k-1)
        for (int i = 0 ; i < K - 1 ; i ++) {
            pp *= p;
        }
        for (int i = 0 ; i < raw.length() ; i ++) {
            if (i < K) {
                currentHash = currentHash * p + raw.charAt(i);
            } else {
                currentHash -= raw.charAt(i - K) * pp;
                currentHash = currentHash * p + raw.charAt(i);
            }
            if (i >= K - 1) {
                if (!map.containsKey(currentHash)) {
                    map.put(currentHash, new ArrayList<Integer>());
                }
                map.get(currentHash).add(i);
            }
        }
        return map;
    }
}
