//School of Informatics Xiamen University, GPL-3.0 license
package cn.edu.xmu.oomall.payment.controller;

import cn.edu.xmu.javaee.core.aop.Audit;
import cn.edu.xmu.javaee.core.aop.LoginUser;
import cn.edu.xmu.javaee.core.model.ReturnNo;
import cn.edu.xmu.javaee.core.model.vo.PageVo;
import cn.edu.xmu.javaee.core.model.ReturnObject;
import cn.edu.xmu.javaee.core.model.dto.UserDto;
import cn.edu.xmu.oomall.payment.controller.vo.*;
import cn.edu.xmu.oomall.payment.controller.dto.AccountDto;
import cn.edu.xmu.oomall.payment.dao.bo.*;
import cn.edu.xmu.oomall.payment.service.ChannelService;
import cn.edu.xmu.oomall.payment.service.RefundService;
import cn.edu.xmu.oomall.payment.service.PaymentService;
import cn.edu.xmu.javaee.core.util.CloneFactory;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static cn.edu.xmu.javaee.core.model.Constants.*;

/**
 * 管理人员的接口
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/platforms/{shopId}", produces = "application/json;charset=UTF-8")
public class AdminPaymentController {

    private final static Logger logger = LoggerFactory.getLogger(AdminPaymentController.class);

    private final ChannelService channelService;


    @Audit(departName = "platforms")
    @PutMapping("/channels/{id}/valid")
    public ReturnObject validChannel(@PathVariable("shopId") Long shopId,
                                           @PathVariable("id") Long channelId,
                                           @LoginUser UserDto user) {
        if(!shopId.equals(PLATFORM)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "支付渠道", channelId, shopId));
        }
        this.channelService.validChannel(channelId, user);
        return new ReturnObject();
    }

    @Audit(departName = "platforms")
    @PutMapping("/channels/{id}/invalid")
    public ReturnObject invalidChannel(@PathVariable("shopId") Long shopId,
                                           @PathVariable("id") Long channelId,
                                           @LoginUser UserDto user) {
        if(!shopId.equals(PLATFORM)){
            return new ReturnObject(ReturnNo.RESOURCE_ID_OUTSCOPE, String.format(ReturnNo.RESOURCE_ID_OUTSCOPE.getMessage(), "支付渠道", channelId, shopId));
        }
        this.channelService.invalidChannel(channelId, user);
        return new ReturnObject();
    }

}
