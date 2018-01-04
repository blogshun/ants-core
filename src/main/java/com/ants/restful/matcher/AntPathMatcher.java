package com.ants.restful.matcher;

/**
 * @author MrShun
 * @version 1.0
 * Date 2017-04-27
 */


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class AntPathMatcher implements PathMatcher {
    public static final String DEFAULT_PATH_SEPARATOR = "/";
    private static final int CACHE_TURNOFF_THRESHOLD = 65536;
    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{[^/]+?\\}");
    private String pathSeparator;
    private AntPathMatcher.PathSeparatorPatternCache pathSeparatorPatternCache;
    private boolean trimTokens = true;
    private volatile Boolean cachePatterns;
    private final Map<String, String[]> tokenizedPatternCache = new ConcurrentHashMap(256);
    final Map<String, AntPathStringMatcher> stringMatcherCache = new ConcurrentHashMap(256);

    public AntPathMatcher() {
        this.pathSeparator = "/";
        this.pathSeparatorPatternCache = new AntPathMatcher.PathSeparatorPatternCache("/");
    }

    public AntPathMatcher(String pathSeparator) {
        if (pathSeparator == null) {
            throw new IllegalArgumentException("\'pathSeparator\' is required");
        }
        this.pathSeparator = pathSeparator;
        this.pathSeparatorPatternCache = new AntPathMatcher.PathSeparatorPatternCache(pathSeparator);
    }

    public void setPathSeparator(String pathSeparator) {
        this.pathSeparator = pathSeparator != null ? pathSeparator : "/";
        this.pathSeparatorPatternCache = new AntPathMatcher.PathSeparatorPatternCache(this.pathSeparator);
    }

    public void setTrimTokens(boolean trimTokens) {
        this.trimTokens = trimTokens;
    }

    public void setCachePatterns(boolean cachePatterns) {
        this.cachePatterns = Boolean.valueOf(cachePatterns);
    }

    private void deactivatePatternCache() {
        this.cachePatterns = Boolean.valueOf(false);
        this.tokenizedPatternCache.clear();
        this.stringMatcherCache.clear();
    }

    @Override
    public boolean isPattern(String path) {
        return path.indexOf(42) != -1 || path.indexOf(63) != -1;
    }

    @Override
    public boolean match(String pattern, String path) {
        return this.doMatch(pattern, path, true, (Map) null);
    }

    @Override
    public boolean matchStart(String pattern, String path) {
        return this.doMatch(pattern, path, false, (Map) null);
    }

    protected boolean doMatch(String pattern, String path, boolean fullMatch, Map<String, String> uriTemplateVariables) {
        if (path.startsWith(this.pathSeparator) != pattern.startsWith(this.pathSeparator)) {
            return false;
        } else {
            String[] pattDirs = this.tokenizePattern(pattern);
            String[] pathDirs = this.tokenizePath(path);
            int pattIdxStart = 0;
            int pattIdxEnd = pattDirs.length - 1;
            int pathIdxStart = 0;

            int pathIdxEnd;
            String i;
            for (pathIdxEnd = pathDirs.length - 1; pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd; ++pathIdxStart) {
                i = pattDirs[pattIdxStart];
                if ("**".equals(i)) {
                    break;
                }

                if (!this.matchStrings(i, pathDirs[pathIdxStart], uriTemplateVariables)) {
                    return false;
                }

                ++pattIdxStart;
            }

            int var19;
            if (pathIdxStart > pathIdxEnd) {
                if (pattIdxStart > pattIdxEnd) {
                    return pattern.endsWith(this.pathSeparator) ? path.endsWith(this.pathSeparator) : !path.endsWith(this.pathSeparator);
                } else if (!fullMatch) {
                    return true;
                } else if (pattIdxStart == pattIdxEnd && "*".equals(pattDirs[pattIdxStart]) && path.endsWith(this.pathSeparator)) {
                    return true;
                } else {
                    for (var19 = pattIdxStart; var19 <= pattIdxEnd; ++var19) {
                        if (!"**".equals(pattDirs[var19])) {
                            return false;
                        }
                    }

                    return true;
                }
            } else if (pattIdxStart > pattIdxEnd) {
                return false;
            } else if (!fullMatch && "**".equals(pattDirs[pattIdxStart])) {
                return true;
            } else {
                while (pattIdxStart <= pattIdxEnd && pathIdxStart <= pathIdxEnd) {
                    i = pattDirs[pattIdxEnd];
                    if ("**".equals(i)) {
                        break;
                    }

                    if (!this.matchStrings(i, pathDirs[pathIdxEnd], uriTemplateVariables)) {
                        return false;
                    }

                    --pattIdxEnd;
                    --pathIdxEnd;
                }

                if (pathIdxStart > pathIdxEnd) {
                    for (var19 = pattIdxStart; var19 <= pattIdxEnd; ++var19) {
                        if (!"**".equals(pattDirs[var19])) {
                            return false;
                        }
                    }

                    return true;
                } else {
                    while (pattIdxStart != pattIdxEnd && pathIdxStart <= pathIdxEnd) {
                        var19 = -1;

                        int patLength;
                        for (patLength = pattIdxStart + 1; patLength <= pattIdxEnd; ++patLength) {
                            if ("**".equals(pattDirs[patLength])) {
                                var19 = patLength;
                                break;
                            }
                        }

                        if (var19 == pattIdxStart + 1) {
                            ++pattIdxStart;
                        } else {
                            patLength = var19 - pattIdxStart - 1;
                            int strLength = pathIdxEnd - pathIdxStart + 1;
                            int foundIdx = -1;
                            int i1 = 0;

                            label140:
                            while (i1 <= strLength - patLength) {
                                for (int j = 0; j < patLength; ++j) {
                                    String subPat = pattDirs[pattIdxStart + j + 1];
                                    String subStr = pathDirs[pathIdxStart + i1 + j];
                                    if (!this.matchStrings(subPat, subStr, uriTemplateVariables)) {
                                        ++i1;
                                        continue label140;
                                    }
                                }

                                foundIdx = pathIdxStart + i1;
                                break;
                            }

                            if (foundIdx == -1) {
                                return false;
                            }

                            pattIdxStart = var19;
                            pathIdxStart = foundIdx + patLength;
                        }
                    }

                    for (var19 = pattIdxStart; var19 <= pattIdxEnd; ++var19) {
                        if (!"**".equals(pattDirs[var19])) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }
    }

    protected String[] tokenizePattern(String pattern) {
        String[] tokenized = null;
        Boolean cachePatterns = this.cachePatterns;
        if (cachePatterns == null || cachePatterns.booleanValue()) {
            tokenized = (String[]) this.tokenizedPatternCache.get(pattern);
        }

        if (tokenized == null) {
            tokenized = this.tokenizePath(pattern);
            if (cachePatterns == null && this.tokenizedPatternCache.size() >= 65536) {
                this.deactivatePatternCache();
                return tokenized;
            }

            if (cachePatterns == null || cachePatterns.booleanValue()) {
                this.tokenizedPatternCache.put(pattern, tokenized);
            }
        }

        return tokenized;
    }

    protected String[] tokenizePath(String path) {
        return StringUtils.tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);
    }

    private boolean matchStrings(String pattern, String str, Map<String, String> uriTemplateVariables) {
        return this.getStringMatcher(pattern).matchStrings(str, uriTemplateVariables);
    }

    protected AntPathMatcher.AntPathStringMatcher getStringMatcher(String pattern) {
        AntPathMatcher.AntPathStringMatcher matcher = null;
        Boolean cachePatterns = this.cachePatterns;
        if (cachePatterns == null || cachePatterns.booleanValue()) {
            matcher = (AntPathMatcher.AntPathStringMatcher) this.stringMatcherCache.get(pattern);
        }

        if (matcher == null) {
            matcher = new AntPathMatcher.AntPathStringMatcher(pattern);
            if (cachePatterns == null && this.stringMatcherCache.size() >= 65536) {
                this.deactivatePatternCache();
                return matcher;
            }

            if (cachePatterns == null || cachePatterns.booleanValue()) {
                this.stringMatcherCache.put(pattern, matcher);
            }
        }

        return matcher;
    }

    @Override
    public String extractPathWithinPattern(String pattern, String path) {
        String[] patternParts = StringUtils.tokenizeToStringArray(pattern, this.pathSeparator, this.trimTokens, true);
        String[] pathParts = StringUtils.tokenizeToStringArray(path, this.pathSeparator, this.trimTokens, true);
        StringBuilder builder = new StringBuilder();
        boolean pathStarted = false;

        for (int segment = 0; segment < patternParts.length; ++segment) {
            String patternPart = patternParts[segment];
            if (patternPart.indexOf(42) > -1 || patternPart.indexOf(63) > -1) {
                while (segment < pathParts.length) {
                    if (pathStarted || segment == 0 && !pattern.startsWith(this.pathSeparator)) {
                        builder.append(this.pathSeparator);
                    }

                    builder.append(pathParts[segment]);
                    pathStarted = true;
                    ++segment;
                }
            }
        }

        return builder.toString();
    }

    @Override
    public Map<String, String> extractUriTemplateVariables(String pattern, String path) {
        LinkedHashMap variables = new LinkedHashMap();
        boolean result = this.doMatch(pattern, path, true, variables);
        if (!result) {
            throw new IllegalStateException("Pattern \"" + pattern + "\" is not a match for \"" + path + "\"");
        }
        return variables;
    }

    @Override
    public String combine(String pattern1, String pattern2) {
        if (!StringUtils.hasText(pattern1) && !StringUtils.hasText(pattern2)) {
            return "";
        } else if (!StringUtils.hasText(pattern1)) {
            return pattern2;
        } else if (!StringUtils.hasText(pattern2)) {
            return pattern1;
        } else {
            boolean pattern1ContainsUriVar = pattern1.indexOf(123) != -1;
            if (!pattern1.equals(pattern2) && !pattern1ContainsUriVar && this.match(pattern1, pattern2)) {
                return pattern2;
            } else if (pattern1.endsWith(this.pathSeparatorPatternCache.getEndsOnWildCard())) {
                return this.concat(pattern1.substring(0, pattern1.length() - 2), pattern2);
            } else if (pattern1.endsWith(this.pathSeparatorPatternCache.getEndsOnDoubleWildCard())) {
                return this.concat(pattern1, pattern2);
            } else {
                int starDotPos1 = pattern1.indexOf("*.");
                if (!pattern1ContainsUriVar && starDotPos1 != -1 && !".".equals(this.pathSeparator)) {
                    String extension1 = pattern1.substring(starDotPos1 + 1);
                    int dotPos2 = pattern2.indexOf(46);
                    String fileName2 = dotPos2 == -1 ? pattern2 : pattern2.substring(0, dotPos2);
                    String extension2 = dotPos2 == -1 ? "" : pattern2.substring(dotPos2);
                    String extension = extension1.startsWith("*") ? extension2 : extension1;
                    return fileName2 + extension;
                } else {
                    return this.concat(pattern1, pattern2);
                }
            }
        }
    }

    private String concat(String path1, String path2) {
        return !path1.endsWith(this.pathSeparator) && !path2.startsWith(this.pathSeparator) ? path1 + this.pathSeparator + path2 : path1 + path2;
    }

    @Override
    public Comparator<String> getPatternComparator(String path) {
        return new AntPathMatcher.AntPatternComparator(path);
    }

    private static class PathSeparatorPatternCache {
        private final String endsOnWildCard;
        private final String endsOnDoubleWildCard;

        public PathSeparatorPatternCache(String pathSeparator) {
            this.endsOnWildCard = pathSeparator + "*";
            this.endsOnDoubleWildCard = pathSeparator + "**";
        }

        public String getEndsOnWildCard() {
            return this.endsOnWildCard;
        }

        public String getEndsOnDoubleWildCard() {
            return this.endsOnDoubleWildCard;
        }
    }

    protected static class AntPatternComparator implements Comparator<String> {
        private final String path;

        public AntPatternComparator(String path) {
            this.path = path;
        }

        @Override
        public int compare(String pattern1, String pattern2) {
            AntPathMatcher.AntPatternComparator.PatternInfo info1 = new AntPathMatcher.AntPatternComparator.PatternInfo(pattern1);
            AntPathMatcher.AntPatternComparator.PatternInfo info2 = new AntPathMatcher.AntPatternComparator.PatternInfo(pattern2);
            if (info1.isLeastSpecific() && info2.isLeastSpecific()) {
                return 0;
            } else if (info1.isLeastSpecific()) {
                return 1;
            } else if (info2.isLeastSpecific()) {
                return -1;
            } else {
                boolean pattern1EqualsPath = pattern1.equals(this.path);
                boolean pattern2EqualsPath = pattern2.equals(this.path);
                return pattern1EqualsPath && pattern2EqualsPath ? 0 : (pattern1EqualsPath ? -1 : (pattern2EqualsPath ? 1 : (info1.isPrefixPattern() && info2.getDoubleWildcards() == 0 ? 1 : (info2.isPrefixPattern() && info1.getDoubleWildcards() == 0 ? -1 : (info1.getTotalCount() != info2.getTotalCount() ? info1.getTotalCount() - info2.getTotalCount() : (info1.getLength() != info2.getLength() ? info2.getLength() - info1.getLength() : (info1.getSingleWildcards() < info2.getSingleWildcards() ? -1 : (info2.getSingleWildcards() < info1.getSingleWildcards() ? 1 : (info1.getUriVars() < info2.getUriVars() ? -1 : (info2.getUriVars() < info1.getUriVars() ? 1 : 0))))))))));
            }
        }

        private static class PatternInfo {
            private final String pattern;
            private int uriVars;
            private int singleWildcards;
            private int doubleWildcards;
            private boolean catchAllPattern;
            private boolean prefixPattern;
            private Integer length;

            public PatternInfo(String pattern) {
                this.pattern = pattern;
                if (this.pattern != null) {
                    this.initCounters();
                    this.catchAllPattern = "/**".equals(this.pattern);
                    this.prefixPattern = !this.catchAllPattern && "/**".endsWith(this.pattern);
                }

                if (this.uriVars == 0) {
                    this.length = Integer.valueOf(this.pattern != null ? this.pattern.length() : 0);
                }

            }

            protected void initCounters() {
                int pos = 0;

                while (true) {
                    while (pos < this.pattern.length()) {
                        if (this.pattern.charAt(pos) == 123) {
                            ++this.uriVars;
                            ++pos;
                        } else if (this.pattern.charAt(pos) == 42) {
                            if (pos + 1 < this.pattern.length() && this.pattern.charAt(pos + 1) == 42) {
                                ++this.doubleWildcards;
                                pos += 2;
                            } else if (!".*".equals(this.pattern.substring(pos - 1))) {
                                ++this.singleWildcards;
                                ++pos;
                            } else {
                                ++pos;
                            }
                        } else {
                            ++pos;
                        }
                    }

                    return;
                }
            }

            public int getUriVars() {
                return this.uriVars;
            }

            public int getSingleWildcards() {
                return this.singleWildcards;
            }

            public int getDoubleWildcards() {
                return this.doubleWildcards;
            }

            public boolean isLeastSpecific() {
                return this.pattern == null || this.catchAllPattern;
            }

            public boolean isPrefixPattern() {
                return this.prefixPattern;
            }

            public int getTotalCount() {
                return this.uriVars + this.singleWildcards + 2 * this.doubleWildcards;
            }

            public int getLength() {
                if (this.length == null) {
                    this.length = Integer.valueOf(AntPathMatcher.VARIABLE_PATTERN.matcher(this.pattern).replaceAll("#").length());
                }

                return this.length.intValue();
            }
        }
    }

    protected static class AntPathStringMatcher {
        private static final Pattern GLOB_PATTERN = Pattern.compile("\\?|\\*|\\{((?:\\{[^/]+?\\}|[^/{}]|\\\\[{}])+?)\\}");
        private static final String DEFAULT_VARIABLE_PATTERN = "(.*)";
        private final Pattern pattern;
        private final List<String> variableNames = new LinkedList();

        public AntPathStringMatcher(String pattern) {
            StringBuilder patternBuilder = new StringBuilder();
            Matcher m = GLOB_PATTERN.matcher(pattern);

            int end;
            for (end = 0; m.find(); end = m.end()) {
                patternBuilder.append(this.quote(pattern, end, m.start()));
                String match = m.group();
                if ("?".equals(match)) {
                    patternBuilder.append('.');
                } else if ("*".equals(match)) {
                    patternBuilder.append(".*");
                } else if (match.startsWith("{") && match.endsWith("}")) {
                    int colonIdx = match.indexOf(58);
                    if (colonIdx == -1) {
                        patternBuilder.append("(.*)");
                        this.variableNames.add(m.group(1));
                    } else {
                        String variablePattern = match.substring(colonIdx + 1, match.length() - 1);
                        patternBuilder.append('(');
                        patternBuilder.append(variablePattern);
                        patternBuilder.append(')');
                        String variableName = match.substring(1, colonIdx);
                        this.variableNames.add(variableName);
                    }
                }
            }

            patternBuilder.append(this.quote(pattern, end, pattern.length()));
            this.pattern = Pattern.compile(patternBuilder.toString());
        }

        private String quote(String s, int start, int end) {
            return start == end ? "" : Pattern.quote(s.substring(start, end));
        }

        public boolean matchStrings(String str, Map<String, String> uriTemplateVariables) {
            Matcher matcher = this.pattern.matcher(str);
            if (!matcher.matches()) {
                return false;
            } else {
                if (uriTemplateVariables != null) {
                    if (this.variableNames.size() != matcher.groupCount()) {
                        throw new IllegalArgumentException("The number of capturing groups in the pattern segment " + this.pattern + " does not match the number of URI template variables it defines, which can occur if " + " capturing groups are used in a URI template regex. Use non-capturing groups instead.");
                    }
                    for (int i = 1; i <= matcher.groupCount(); ++i) {
                        String name = (String) this.variableNames.get(i - 1);
                        String value = matcher.group(i);
                        uriTemplateVariables.put(name, value);
                    }
                }

                return true;
            }
        }
    }
}