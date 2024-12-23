//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"cn.edu.xmu.javaee.core","cn.edu.xmu.oomall.payment"})
@MapperScan({"cn.edu.xmu.oomall.payment.mapper.generator", "cn.edu.xmu.oomall.payment.mapper.manual"})
@EnableFeignClients
@EnableDiscoveryClient
public class PaymentApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}

}
