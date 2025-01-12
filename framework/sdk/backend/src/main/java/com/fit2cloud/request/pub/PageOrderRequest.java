package com.fit2cloud.request.pub;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;

@Accessors(chain = true)
@Data
public class PageOrderRequest extends PageRequest implements PageOrderRequestInterface {

    @Serial
    private static final long serialVersionUID = 7813088780673224646L;

    @ApiModelProperty(value = "排序", example = " {\"column\":\"createTime\",\"asc\":false}")
    private OrderRequest order;

}
