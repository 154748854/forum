package com.example.forum.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * 配置mybatis的扫描路径
 */

@Configuration
@MapperScan("com.example.forum.dao")
public class MybatisConfig {
}
