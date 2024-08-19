package com.passionpro.myapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ForumPostTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static ForumPost getForumPostSample1() {
        return new ForumPost().id(1L).name("name1").description("description1").created_by("created_by1");
    }

    public static ForumPost getForumPostSample2() {
        return new ForumPost().id(2L).name("name2").description("description2").created_by("created_by2");
    }

    public static ForumPost getForumPostRandomSampleGenerator() {
        return new ForumPost()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .created_by(UUID.randomUUID().toString());
    }
}
