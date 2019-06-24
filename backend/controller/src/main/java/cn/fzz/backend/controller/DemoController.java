package cn.fzz.backend.controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class DemoController {
    @RequestMapping(path = "/login", method = RequestMethod.POST)
    @ResponseBody
    public String login(@RequestParam String username,
                        @RequestParam String password) {
        // 简单处理一下，实际开发中肯定是要用到数据库的
        if (username.equals("123") && password.equals("123")) {
            return "{\"code\":\"0000\", \"message\":\"success\"}";
        } else {
            return "{\"code\":\"1111\", \"message\":\"failed\"}";
        }
    }
}