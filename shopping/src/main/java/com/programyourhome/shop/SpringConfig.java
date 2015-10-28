package com.programyourhome.shop;

import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
// Set the order of the @Transactional annotation interception to 'pretty important'. (range from Integer.MIN_VALUE to Integer.MAX_VALUE)
@EnableTransactionManagement(order = -1)
public class SpringConfig {

}
