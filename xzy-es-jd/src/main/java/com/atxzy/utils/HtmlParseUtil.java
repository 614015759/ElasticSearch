package com.atxzy.utils;

import com.atxzy.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.context.annotation.Bean;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class HtmlParseUtil {

    public static void main(String[] args) throws Exception {
        HtmlParseUtil htmlParseUtil = new HtmlParseUtil();
        List<Content> contents = htmlParseUtil.parseJD("金超超");
        for(Content content:contents){
            System.out.println(content.toString());
        }
    }

    public List<Content> parseJD(String keyWord) throws Exception {
        //获得请求   https://s.taobao.com/search?q=java&type=p&tmhkh5=&spm=a21wu.241046-cn.a2227oh.d100&from=sea_1_searchbutton&catId=100
        //前提：需要联网  ajax不能获取到   模拟浏览器才能
        String url = "https://search.jd.com/Search?keyword="+keyWord+"&enc=utf-8&wq=ja&pvid=6176740dad71400ca0544f506e543fdb";
        //解析网页(jsoup的document就是js页面对象  就是浏览器的document对象)
        //这种图片特别多的网站，图片都是采取懒加载的过程
        //data-lazy-img
        Document document = Jsoup.parse(new URL(url), 3000);
        //所有在js中可以使用的方法这里都能用
        Element items = document.getElementById("J_goodsList");
        //获取所有的li元素
        Elements lis = document.getElementsByTag("li");
        //获取元素中的元素这里的 li就是每一个li标签了
       List<Content> contents = new ArrayList<>();
        for (Element li : lis){
            String imgSrc = li.getElementsByTag("img").eq(0).attr("src");
            String price = li.getElementsByClass("p-price").eq(0).text();
            String title = li.getElementsByClass("p-name").eq(0).text();
//            System.out.println("=================================================");
//            System.out.println(imgSrc);
//            System.out.println(price);
//            System.out.println(title);
            contents.add(new Content(title,imgSrc,price));
        }

    return contents;
    }
}
