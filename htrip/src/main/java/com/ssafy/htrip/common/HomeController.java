package com.ssafy.htrip.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
public class HomeController {

    @GetMapping("/")
    public String home() throws Exception{
        return "index";
    }

    @GetMapping("/index")
    public String index() throws Exception{
        return "index";
    }

}
