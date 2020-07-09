package com.atxzy.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Data
@Component
@NoArgsConstructor //无参构造
@AllArgsConstructor  //有参构造
public class Content {

    private String title;
    private String imgUrl;
    private String price;

}
