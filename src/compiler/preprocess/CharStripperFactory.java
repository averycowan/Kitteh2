/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.preprocess;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.xml.bind.TypeConstraintException;

/**
 *
 * @author leijurv
 */
class CharStripperFactory {
    private final ArrayList<Character> chars = new ArrayList<>();
    private final ArrayList<StripLocation> locations = new ArrayList<>();
    public CharStripperFactory addChar(char c, StripLocation loc) {
        if (chars.contains(c)) {
            throw new IllegalStateException();
        }
        chars.add(c);
        locations.add(loc);
        return this;
    }
    public LineBasedTransform build() {
        return new StripChars();
    }
    /**
     * my favorite function
     *
     * @return
     */
    private HashSet<Character> stripBegin() {
        return IntStream.range(0, chars.size()).filter(i -> locations.get(i).stripBegin()).boxed().map(chars::get).collect(Collectors.toCollection(HashSet::new));
    }
    private HashSet<Character> stripEnd() {
        return IntStream.range(0, chars.size()).filter(i -> locations.get(i).stripEnd()).boxed().map(chars::get).collect(Collectors.toCollection(HashSet::new));
    }

    private class StripChars extends LineBasedTransform {
        private final HashSet<Character> begin = stripBegin();
        private final HashSet<Character> end = stripEnd();
        @Override
        public Line transform(Line lineObj) {
            String line = lineObj.raw();
            int stripBegin = 0;
            while (stripBegin < line.length() && begin.contains(line.charAt(stripBegin))) {
                stripBegin++;
            }
            int stripEnd = line.length() - 1;
            while (stripEnd >= 0 && end.contains(line.charAt(stripEnd))) {
                stripEnd--;
            }
            if (stripBegin > stripEnd) {
                return lineObj.withModifiedRaw("");
            }
            return lineObj.withModifiedRaw(line.substring(stripBegin, stripEnd + 1));
        }
    }
}
