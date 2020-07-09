package com.atxzy.controller;

import com.atxzy.service.ContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class ContentController {

    @Autowired
    ContentService contentService ;

    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword) throws Exception {
        boolean b = contentService.parseContent(keyword);

        return b;
    }


    @GetMapping("/searchPage/{keyWord}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> searchPage(
            @PathVariable("keyWord") String keyWord,
            @PathVariable("pageNo") int pageNo,
            @PathVariable("pageSize")int pageSize)
            throws IOException {


        List<Map<String, Object>> maps = contentService.searchPage(keyWord, pageNo, pageSize);
        return maps;
    }

    @GetMapping("/searchPageHighLight/{keyWord}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> searchPageHighLight(
            @PathVariable("keyWord") String keyWord,
            @PathVariable("pageNo") int pageNo,
            @PathVariable("pageSize")int pageSize)
            throws IOException {


        List<Map<String, Object>> maps = contentService.searchPageHighLight(keyWord, pageNo, pageSize);
        return maps;
    }

}
