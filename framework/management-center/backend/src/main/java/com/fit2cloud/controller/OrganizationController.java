package com.fit2cloud.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.fit2cloud.base.entity.Organization;
import com.fit2cloud.base.mapper.BaseOrganizationMapper;
import com.fit2cloud.common.log.annotation.OperatedLog;
import com.fit2cloud.common.log.constants.OperatedTypeEnum;
import com.fit2cloud.common.log.constants.ResourceTypeEnum;
import com.fit2cloud.common.validator.annnotaion.CustomValidated;
import com.fit2cloud.common.validator.group.ValidationGroup;
import com.fit2cloud.common.validator.handler.ExistHandler;
import com.fit2cloud.controller.handler.ResultHolder;
import com.fit2cloud.controller.request.OrganizationBatchRequest;
import com.fit2cloud.controller.request.OrganizationRequest;
import com.fit2cloud.controller.request.PageOrganizationRequest;
import com.fit2cloud.service.IOrganizationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.BeanUtils;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.ArrayList;

/**
 * @Author:张少虎
 * @Date: 2022/8/30  11:06 AM
 * @Version 1.0
 * @注释:
 */
@RestController
@RequestMapping("/api/organization")
@Api("组织相关接口")
@Validated
public class OrganizationController {
    @Resource
    private IOrganizationService organizationService;

    @ApiOperation(value = "分页查询组织", notes = "分页查询组织")
    @GetMapping("/page")
    @PreAuthorize("hasAnyCePermission('ORGANIZATION:READ')")
    public ResultHolder<IPage<Organization>> page(@Validated PageOrganizationRequest pageOrganizationRequest) {
        return ResultHolder.success(organizationService.pageOrganization(pageOrganizationRequest));
    }

    @ApiOperation(value = "查询组织数量", notes = "查询组织数量")
    @GetMapping("/count")
    @PreAuthorize("hasAnyCePermission('ORGANIZATION:READ')")
    public ResultHolder<Long> count() {
        return ResultHolder.success(organizationService.countOrganization());
    }

    @GetMapping("/{organizationId}")
    @ApiOperation(value = "根据组织id查询组织", notes = "根据组织id查询组织")
    public ResultHolder<Organization> getOrganization(@ApiParam("组织id")
                                                      @NotNull(message = "{i18n.organization.name.is.not.empty}")
                                                      @CustomValidated(mapper = BaseOrganizationMapper.class, handler = ExistHandler.class, message = "{i18n.organization.id.is.not.existent}", exist = false)
                                                      @PathVariable("organizationId") String id) {
        return ResultHolder.success(organizationService.getOne(id, null));
    }

    @ApiOperation(value = "添加组织", notes = "添加组织")
    @PostMapping
    @PreAuthorize("hasAnyCePermission('ORGANIZATION:CREATE')")
    @OperatedLog(resourceType = ResourceTypeEnum.ORGANIZATION, operated = OperatedTypeEnum.ADD,
            content = "'添加组织['+#request.name+']'",
            param = "#request")
    public ResultHolder<Organization> save(@RequestBody
                                           @Validated(ValidationGroup.SAVE.class) OrganizationRequest request) {
        Organization organization = new Organization();
        BeanUtils.copyProperties(request, organization);
        organizationService.save(organization);
        return ResultHolder.success(organizationService.getById(organization.getId()));
    }

    @ApiOperation(value = "批量添加组织", notes = "批量添加组织")
    @PostMapping("/batch")
    @PreAuthorize("hasAnyCePermission('ORGANIZATION:CREATE')")
    @OperatedLog(resourceType = ResourceTypeEnum.ORGANIZATION, operated = OperatedTypeEnum.BATCH_ADD,
            content = "'批量添加组织['+#request.orgDetails.![name]+']'",
            param = "#request")
    public ResultHolder<Boolean> batch(@RequestBody
                                       @Validated(ValidationGroup.SAVE.class) OrganizationBatchRequest request) {
        Boolean batch = organizationService.batch(request);
        return ResultHolder.success(batch);
    }

    @ApiOperation(value = "修改组织", notes = "修改组织")
    @PutMapping
    @PreAuthorize("hasAnyCePermission('ORGANIZATION:EDIT')")
    @OperatedLog(resourceType = ResourceTypeEnum.ORGANIZATION, operated = OperatedTypeEnum.MODIFY,
            resourceId = "#request.id",
            content = "'修改组织['+#request.name+']'",
            param = "#request")
    public ResultHolder<Organization> update(@RequestBody
                                             @Validated(ValidationGroup.UPDATE.class) OrganizationRequest request) {
        organizationService.update(request);
        return ResultHolder.success(organizationService.getById(request.getId()));
    }

    @ApiOperation(value = "删除组织", notes = "删除组织")
    @DeleteMapping("/{organizationId}")
    @PreAuthorize("hasAnyCePermission('ORGANIZATION:DELETE')")
    @OperatedLog(resourceType = ResourceTypeEnum.ORGANIZATION, operated = OperatedTypeEnum.DELETE,
            resourceId = "#id",
            content = "'删除组织'",
            param = "#id")
    public ResultHolder<Boolean> delete(@ApiParam("组织id")
                                        @NotNull(message = "{i18n.organization.name.is.not.empty}")
                                        @CustomValidated(mapper = BaseOrganizationMapper.class, handler = ExistHandler.class, message = "{i18n.organization.id.is.not.existent}", exist = false)
                                        @PathVariable("organizationId") String id) {
        return ResultHolder.success(organizationService.removeTreeById(id));
    }

    @ApiOperation(value = "批量删除组织", notes = "批量删除组织")
    @DeleteMapping
    @PreAuthorize("hasAnyCePermission('ORGANIZATION:DELETE')")
    @OperatedLog(resourceType = ResourceTypeEnum.ORGANIZATION, operated = OperatedTypeEnum.BATCH_DELETE,
            resourceId = "#organizationIds.![id]",
            content = "'批量删除了['+#organizationIds.size+']个组织'",
            param = "#organizationIds")
    public ResultHolder<Boolean> deleteBatch(@ApiParam("批量删除组织")
                                             @Size(min = 1, message = "{i18n.organization.id.size.gt.one}")
                                             @NotNull(message = "{i18n.organization.id.is.not.empty}")
                                             @RequestBody ArrayList<Organization> organizationIds) {
        return ResultHolder.success(organizationService.removeBatchTreeByIds(organizationIds));
    }
}
