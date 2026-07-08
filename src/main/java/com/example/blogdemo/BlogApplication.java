package com.example.blogdemo;

import java.util.Map;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class BlogDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogDemoApplication.class, args);
    }

    @GetMapping(value = "/", produces = "text/html;charset=UTF-8")
    public String home() {
        return """
                <!doctype html>
                <html lang="zh-CN">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport" content="width=device-width, initial-scale=1.0">
                    <title>我的博客上线了</title>
                    <style>
                        body {
                            margin: 0;
                            min-height: 100vh;
                            display: grid;
                            place-items: center;
                            font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
                            background: #f6f7f9;
                            color: #1f2937;
                        }
                        main {
                            width: min(92vw, 680px);
                            padding: 40px;
                            background: #ffffff;
                            border: 1px solid #e5e7eb;
                            border-radius: 8px;
                            box-shadow: 0 16px 48px rgba(15, 23, 42, 0.08);
                        }
                        h1 {
                            margin: 0 0 16px;
                            font-size: clamp(32px, 6vw, 56px);
                            line-height: 1.05;
                        }
                        p {
                            margin: 0;
                            font-size: 18px;
                            color: #4b5563;
                        }
                    </style>
                </head>
                <body>
                    <main>
                        <h1>我的博客上线了</h1>
                        <p>Spring Boot 3 单体 demo 已经准备好部署到 AWS Lightsail。</p>
                    </main>
                </body>
                </html>
                """;
    }

    @GetMapping("/api/health")
    public Map<String, String> health() {
        return Map.of("status", "UP");
    }
}
