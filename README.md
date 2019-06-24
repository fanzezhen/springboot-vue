# springboot-vue
前后端分离demo，前端使用Vue框架，后端是SpringBoot的api接口。
# 框架版本
- Spring Boot: 2.1.4.RELEASE（JDK 是1.8）
- Vue.js: 2.x
# 基本流程
## 前端：编写 Vue 组件
&emsp;&emsp;首先用 vue-cli 搭好脚手架，我这个 Demo 用到的第三方库有：
 - axios：负责 HTTP 请求
 - bootstrap-vue：Bootstrap 和 Vue.js 的整合，方便设计页面
 - vue-router：管理路由
 - qs：实现 CORS

然后写一个登录组件：
```html
<!-- 下面是我直接从 bootstrap-vue 文档抄下来的模板  -->
<template>
  <div>
    <b-form @submit="onSubmit" @reset="onReset" v-if="show">
      <b-form-group id="exampleInputGroup1"
                    label="Username:"
                    label-for="exampleInput1">
        <b-form-input id="exampleInput1"
                      type="text"
                      v-model="form.username"
                      required
                      placeholder="Enter username">
        </b-form-input>
      </b-form-group>
      <b-form-group id="exampleInputGroup2"
                    label="Password:"
                    label-for="exampleInput2">
        <b-form-input id="exampleInput2"
                      type="text"
                      v-model="form.password"
                      required
                      placeholder="Enter password">
        </b-form-input>
      </b-form-group>
      <b-form-group id="exampleGroup4">
        <b-form-checkbox-group v-model="form.checked" id="exampleChecks">
          <b-form-checkbox value="me">Check me out</b-form-checkbox>
          <b-form-checkbox value="that">Check that out</b-form-checkbox>
        </b-form-checkbox-group>
      </b-form-group>
      <b-button type="submit" variant="primary">Submit</b-button>
      <b-button type="reset" variant="danger">Reset</b-button>
    </b-form>
  </div>
</template>

<script>
//...
</script>
```
&emsp;&emsp;记得配置路由：
```javascript
// src/router/index.js

import Vue from 'vue'
import Router from 'vue-router'
import Login from '@/components/Login.vue'
import Information from '@/components/Information.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Login',
      component: Login
    },
    {
      path: '/information',
      name: 'Information',
      component: Information
    }
  ]
})

```
## 后端：提供 RESTful API
&emsp;&emsp;因为只有后端提供了接口，前端才能调用，所以现在要进行后端开发。RESTful 是现在很流行的 API 设计风格，所以我这里也实践了一下。下面是 controller 的代码，完整源码地址附在文末。
```java
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
```
&emsp;&emsp;后端的 API 现在有了，就差前端调用了。但是没这么简单，接下来就要解决我前面提到的问题。
# 实现 CORS
&emsp;&emsp;在这个 Demo 中前端占用的端口是8080，后端是 8088。这就存在跨域的问题，如果不解决的话后端就没法接收前端的请求。

&emsp;&emsp;我参考了[这个例子](https://github.com/boylegu/SpringBoot-vue/blob/master/src/main/java/com/boylegu/springboot_vue/config/CORSConfig.java)，通过配置 Spring MVC 实现了 CORS：
```java
@Configuration
public class CORSConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(ALL)
                .allowedMethods(ALL)
                .allowedHeaders(ALL)
                .allowCredentials(true);
    }
}
```
&emsp;&emsp;后端配置好了还不行，前端也要有一些配置，要用 axios 顺利地发送请求并保证后端能接收到，需要对请求参数做处理。我参考[这个回答](https://segmentfault.com/q/1010000008292792)用 qs 库对请求参数做了处理：
```javascript
qs.stringify({
        'username': this.form.username,
        'password': this.form.password
      })
```
现在只需完善前端调用后端 API 的代码：
```html
// Login.vue
<script>
export default {
  data () {
    return {
      form: {
        username: '',
        password: '',
        checked: []
      },
      show: true
    }
  },
  methods: {
    onSubmit (evt) {
      evt.preventDefault();
      
      // 关键就在于要对参数进行处理
      axios.post('http://localhost:8088/api/login',qs.stringify({
        'username': this.form.username,
        'password': this.form.password
      })).then((response) => {
        let status = response.data.code;
        if(status === '0000') {
          this.$router.push('/hello-world');
        } else {
          alert(response.data.message);
        }
        console.log(response);
      }).catch((error) => {
        console.log(response);
      });
    }
  }
}
</script>
```
至此，终于实现了前后端的分离，并且保证前后端能够顺利交互。
