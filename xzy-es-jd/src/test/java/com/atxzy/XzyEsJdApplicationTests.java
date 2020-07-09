package com.atxzy;

import com.atxzy.service.ContentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class XzyEsJdApplicationTests {

    @Autowired
    ContentService contentService;

    @Test
    void contextLoads() throws Exception {
        boolean java = contentService.parseContent("java");
        System.out.println(java);
    }

}
