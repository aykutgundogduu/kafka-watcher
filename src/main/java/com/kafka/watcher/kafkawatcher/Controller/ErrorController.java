package com.kafka.watcher.kafkawatcher.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
@RequestMapping("/errors")
public class ErrorController {
    
    @GetMapping("/404")
    public String get404() {
        return "errors/404";
        
    }
    
}
