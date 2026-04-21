package com.poetryapp.common.utils;

import java.util.List;
import java.util.Random;

/**
 * 自动昵称生成器 —— 古风风格
 */
public final class NicknameGenerator {

    private NicknameGenerator() {}

    private static final List<String> PREFIXES = List.of(
            "墨", "诗", "云", "竹", "梅", "兰", "松", "月", "风", "雪",
            "晴", "霜", "江", "山", "翠", "清", "远", "幽", "轻", "淡"
    );
    private static final List<String> MIDDLES = List.of(
            "心", "韵", "影", "意", "思", "语", "声", "怀", "情", "志"
    );
    private static final List<String> SUFFIXES = List.of(
            "书生", "才子", "小童", "学子", "诗友", "秀才", "童生", "小生"
    );

    private static final Random RANDOM = new Random();

    public static String generate() {
        String p = PREFIXES.get(RANDOM.nextInt(PREFIXES.size()));
        String m = MIDDLES.get(RANDOM.nextInt(MIDDLES.size()));
        String s = SUFFIXES.get(RANDOM.nextInt(SUFFIXES.size()));
        return p + m + s;
    }
}
