package com.winter.demo3.controller;

import com.winter.demo3.service.demoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
public class IndexController {
    @Autowired
    demoService demoServ;


    @RequestMapping(path={"/","/index"})
    @ResponseBody
    public String index( HttpSession httpSession){
        return demoServ.getMessage(1)+"Hello" + httpSession.getAttribute("msg");
    }

    @RequestMapping(path={"/profile/{groupID}/{userID}"})
    @ResponseBody
    public String profile(@PathVariable("userID") int userID,
                          @PathVariable("groupID") String groupID,
                          @RequestParam(value="type",defaultValue = "1") int type,
                          @RequestParam(value="key",required = false) String key){
        return String.format("Profile Page of %s / %d,type:%d key: %s",groupID,userID,type,key);
    }

    @RequestMapping(path={"/vm"})
    public String template(Model model){
        model.addAttribute("value1","v1");
        List<String> colors = Arrays.asList(new String[]{"Red","Green","Blue"});
        model.addAttribute("colors",colors);
        Map<String,String> map = new HashMap<>();
        for(int i = 0; i < 3; i++){
            map.put(String.valueOf(i),String.valueOf(i*i));
        }
        model.addAttribute("map",map);
        return "home";
    }

    @RequestMapping(path={"/request"})
    @ResponseBody
    public String request(Model model, HttpServletResponse response,
                          HttpServletRequest request,
                          HttpSession httpSession,
                          @CookieValue("JSESSIONID") String sessionId) {
        StringBuilder sb = new StringBuilder();
        sb.append("cookieValue: " + sessionId);
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            sb.append(name + ":" + request.getHeader(name) + "<br>");
        }
        if(request.getCookies() != null){
            for(Cookie cookie : request.getCookies()){
                sb.append("Cookie: "+ cookie.getName() + "value: " + cookie.getValue());
            }
        }
        sb.append(request.getMethod() + "<br>");
        sb.append(request.getQueryString() + "<br>");
        sb.append(request.getPathInfo() + "<br>");
        sb.append(request.getRequestURI() + "<br>");
        return sb.toString();

    }
    @RequestMapping(path={"/redirect/{code}"})
    public RedirectView redirect(@PathVariable("code") int code,
                                 HttpSession httpSession) {
        httpSession.setAttribute("msg","jump from redirect");
        RedirectView red = new RedirectView("/",true);
        if(code == 301){
            red.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
        }
        return red;
    }
    @RequestMapping(path = {"/admin"})
    @ResponseBody
    public String admin(@RequestParam("key") String key){
        if("admin".equals(key)){
            return "hello admin";
        }
        throw new IllegalArgumentException("参数不对");
    }
    @ExceptionHandler()
    @ResponseBody
    public String error(Exception e){
        return "error: " + e.getMessage();
    }
}
