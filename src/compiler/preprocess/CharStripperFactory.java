/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.preprocess;
import java.util.ArrayList;
import java.util.stream.IntStream;

/**
 *
 * @author leijurv
 */
public class CharStripperFactory {
    private final ArrayList<Character> chars = new ArrayList<>();
    private final ArrayList<StripLocation> locations = new ArrayList<>();
    public CharStripperFactory addChar(char c, StripLocation loc) {
        chars.add(c);
        locations.add(loc);
        return this;
    }
    public LineBasedTransform build() {
        return new StripChars();
    }
    private Character[] stripBegin() {
        return IntStream.range(0, chars.size()).filter(i -> locations.get(i).stripBegin()).mapToObj(i -> chars.get(i)).toArray(Character[]::new);
    }
    private Character[] stripEnd() {
        return IntStream.range(0, chars.size()).filter(i -> locations.get(i).stripEnd()).mapToObj(i -> chars.get(i)).toArray(Character[]::new);
    }
    private boolean shouldStrip(Character[] chars, char test) {
        for (char c : chars) {
            if (test == c) {
                return true;
            }
        }
        return false;
    }

    private class StripChars extends LineBasedTransform {
        @Override
        public String transform(String line) {
            if (line.equals("")) {
                return line;
            }
            Character[] begin = stripBegin();
            Character[] end = stripEnd();
            int stripBegin;
            for (stripBegin = 0; stripBegin < line.length() && shouldStrip(begin, line.charAt(stripBegin)); stripBegin++);
            int stripEnd;
            for (stripEnd = line.length() - 1; stripEnd >= 0 && shouldStrip(end, line.charAt(stripEnd)); stripEnd--);
            if (stripBegin > stripEnd) {
                System.out.println("IM STRIPPING " + line);
                return "";
            }
            return line.substring(stripBegin, stripEnd + 1);
        }
    }
}