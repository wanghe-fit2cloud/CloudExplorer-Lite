package com.fit2cloud.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fit2cloud.base.entity.CloudAccount;
import com.fit2cloud.base.entity.VmCloudImage;
import com.fit2cloud.base.mapper.BaseVmCloudImageMapper;
import com.fit2cloud.common.utils.ColumnNameUtil;
import com.fit2cloud.common.utils.JsonUtil;
import com.fit2cloud.common.utils.PageUtil;
import com.fit2cloud.controller.request.images.PageVmCloudImageRequest;
import com.fit2cloud.controller.request.images.VmCloudImageRequest;
import com.fit2cloud.dao.mapper.VmCloudImageMapper;
import com.fit2cloud.dto.VmCloudImageDTO;
import com.fit2cloud.provider.impl.huawei.entity.OsConfig;
import com.fit2cloud.provider.impl.huawei.entity.request.HuaweiBaseRequest;
import com.fit2cloud.service.IVmCloudImageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author fit2cloud
 * @since
 */
@Service
public class VmCloudImageServiceImpl extends ServiceImpl<BaseVmCloudImageMapper, VmCloudImage> implements IVmCloudImageService {

    @Resource
    private VmCloudImageMapper imageMapper;

    @Override
    public IPage<VmCloudImageDTO> pageVmCloudImage(PageVmCloudImageRequest request) {
        // 构建查询参数
        QueryWrapper<VmCloudImageDTO> wrapper = addQuery(request);
        Page<VmCloudImageDTO> page = PageUtil.of(request, VmCloudImageDTO.class, new OrderItem(ColumnNameUtil.getColumnName(VmCloudImage::getCreateTime, true), false), true);
        return imageMapper.pageList(page, wrapper);
    }

    private QueryWrapper<VmCloudImageDTO> addQuery(PageVmCloudImageRequest request) {
        QueryWrapper<VmCloudImageDTO> wrapper = new QueryWrapper<>();

        wrapper.like(StringUtils.isNotBlank(request.getImageName()), ColumnNameUtil.getColumnName(VmCloudImage::getImageName, true), request.getImageName());
        wrapper.like(StringUtils.isNotBlank(request.getAccountName()), ColumnNameUtil.getColumnName(CloudAccount::getName, true), request.getAccountName());
        return wrapper;
    }

    @Override
    public List<VmCloudImage> listVmCloudImage(String request) {
        return listVmCloudImage(JsonUtil.parseObject(request, VmCloudImageRequest.class));
    }

    public List<VmCloudImage> listVmCloudImage(VmCloudImageRequest request) {
        LambdaQueryWrapper<VmCloudImage> queryWrapper = new LambdaQueryWrapper<VmCloudImage>()
                .eq(StringUtils.isNotBlank(request.getAccountId()), VmCloudImage::getAccountId, request.getAccountId())
                .eq(StringUtils.isNotBlank(request.getRegion()), VmCloudImage::getRegion, request.getRegion())
                .eq(StringUtils.isNotBlank(request.getRegionId()), VmCloudImage::getRegion, request.getRegionId())
                .like(StringUtils.isNotBlank(request.getOs()), VmCloudImage::getOs, request.getOs())
                .ne(VmCloudImage::getStatus, "DELETED");

        return list(queryWrapper);
    }

    /**
     * 返回操作系统版本
     *
     * @param request
     * @return
     */
    public List<OsConfig> listOsVersion(String request) {
        HuaweiBaseRequest baseRequest = JsonUtil.parseObject(request, HuaweiBaseRequest.class);
        VmCloudImageRequest req = JsonUtil.parseObject(request, VmCloudImageRequest.class);
        req.setRegion(baseRequest.getRegionId());
        List<OsConfig> result = new ArrayList<>();
        if (StringUtils.isEmpty(req.getRegion()) || StringUtils.isEmpty(req.getOs())) {
            return result;
        }
        List<VmCloudImage> imagesTmp = listVmCloudImage(req);
        //只取公共镜像
        List<VmCloudImage> images = imagesTmp.stream()
                .filter(v -> StringUtils.equalsIgnoreCase("gold", v.getImageType()))
                .toList();
        //操作系统去重复
        Map<String, VmCloudImage> osMap = images.stream().collect(Collectors.toMap(VmCloudImage::getOs, a -> a, (k1, k2) -> k1));
        //转换对象，这里存储的镜像镜像理论上不会用到了，因为创建虚拟机的时候，重新查询可用镜像
        osMap.values().forEach(v -> {
            OsConfig osConfig = new OsConfig();
            osConfig.setOs(req.getOs());
            osConfig.setOsVersion(v.getOs());
            osConfig.setImageName(v.getImageName());
            osConfig.setImageId(v.getImageId());
            osConfig.setImageMinDiskSize(v.getDiskSize());
            result.add(osConfig);
        });
        return result.stream().sorted(Comparator.comparing(OsConfig::getOsVersion)).collect(Collectors.toList());
    }

    public List<Map<String, String>> listOs(String request) {
        List<Map<String, String>> result = new ArrayList<>();
        List<String> osList = Arrays.asList("Windows", "RedHat", "CentOS", "SUSE", "Debian", "OpenSUSE", "Oracle Linux", "Fedora", "Ubuntu", "EulerOS", "CoreOS", "ESXi", "Other", "openEuler");
        osList.stream().sorted().forEach(v -> {
            Map<String, String> m = new HashMap<>();
            m.put("id", v);
            m.put("name", v);
            result.add(m);
        });
        return result;
    }

}
