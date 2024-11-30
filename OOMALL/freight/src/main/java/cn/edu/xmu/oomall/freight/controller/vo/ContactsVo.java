package cn.edu.xmu.oomall.freight.controller.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 2023-dgn3-009
 * @author huangzian
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactsVo {
    private String name;
    private String mobile;
    private Long regionId;
    private String address;
}
