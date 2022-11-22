package com.fit2cloud.provider.impl.openstack.api;

import com.fit2cloud.common.provider.impl.openstack.entity.request.OpenStackBaseRequest;
import com.fit2cloud.provider.entity.F2CDisk;
import com.fit2cloud.provider.entity.F2CImage;
import com.fit2cloud.provider.entity.F2CVirtualMachine;
import com.fit2cloud.provider.impl.openstack.entity.CheckStatusResult;
import com.fit2cloud.provider.impl.openstack.entity.VolumeType;
import com.fit2cloud.provider.impl.openstack.entity.request.OpenStackDiskActionRequest;
import com.fit2cloud.provider.impl.openstack.entity.request.OpenStackDiskCreateRequest;
import com.fit2cloud.provider.impl.openstack.entity.request.OpenStackDiskEnlargeRequest;
import com.fit2cloud.provider.impl.openstack.entity.request.OpenStackInstanceActionRequest;
import com.fit2cloud.provider.impl.openstack.util.OpenStackUtils;
import org.apache.commons.lang3.StringUtils;
import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Action;
import org.openstack4j.model.compute.RebootType;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.image.v2.Image;
import org.openstack4j.model.storage.block.Volume;
import org.openstack4j.model.storage.block.builder.VolumeBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OpenStackCloudApi {


    public static List<F2CVirtualMachine> listVirtualMachine(OpenStackBaseRequest request) {
        List<F2CVirtualMachine> list = new ArrayList<>();
        try {
            OSClient.OSClientV3 osClient = request.getOSClient();

            List<String> regions = OpenStackUtils.getRegionList(osClient);

            for (String region : regions) {
                osClient.useRegion(region);
                List<? extends Server> instances = osClient.compute().servers().list(true);
                Map<String, Image> imageMap = osClient.imagesV2().list().stream().collect(Collectors.toMap(Image::getId, image -> image));
                for (Server instance : instances) {
                    list.add(OpenStackUtils.toF2CVirtualMachine(osClient, instance, region, imageMap));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return list;
    }

    public static List<F2CImage> listImage(OpenStackBaseRequest request) {
        List<F2CImage> list = new ArrayList<>();

        try {
            OSClient.OSClientV3 osClient = request.getOSClient();

            List<String> regions = OpenStackUtils.getRegionList(osClient);
            for (String region : regions) {
                osClient.useRegion(region);
                for (Image image : osClient.imagesV2().list()) {
                    list.add(OpenStackUtils.toF2CImage(image, region));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;
    }

    public static List<F2CDisk> listDisk(OpenStackBaseRequest request) {
        List<F2CDisk> list = new ArrayList<>();

        try {
            OSClient.OSClientV3 osClient = request.getOSClient();

            List<String> regions = OpenStackUtils.getRegionList(osClient);
            for (String region : regions) {
                osClient.useRegion(region);
                for (Volume volume : osClient.blockStorage().volumes().list()) {
                    list.add(OpenStackUtils.toF2CDisk(volume, region));
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

        return list;

    }

    public static boolean powerOff(OpenStackInstanceActionRequest request) {
        try {
            OSClient.OSClientV3 osClient = request.getOSClient();
            osClient.useRegion(request.getRegionId());

            Server server = osClient.compute().servers().get(request.getUuid());
            if (server == null) {
                throw new RuntimeException("server not exist");
            }

            ActionResponse response = osClient.compute().servers().action(request.getUuid(), Action.STOP);
            if (!response.isSuccess()) {
                throw new RuntimeException(response.getFault());
            }
            CheckStatusResult result = OpenStackUtils.checkServerStatus(osClient, server, Server.Status.SHUTOFF);
            if (result.isSuccess()) {
                return true;
            } else {
                throw new RuntimeException(result.getFault());
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean powerOn(OpenStackInstanceActionRequest request) {
        try {
            OSClient.OSClientV3 osClient = request.getOSClient();
            osClient.useRegion(request.getRegionId());

            Server server = osClient.compute().servers().get(request.getUuid());
            if (server == null) {
                throw new RuntimeException("server not exist");
            }

            ActionResponse response = osClient.compute().servers().action(request.getUuid(), Action.START);
            if (!response.isSuccess()) {
                throw new RuntimeException(response.getFault());
            }
            CheckStatusResult result = OpenStackUtils.checkServerStatus(osClient, server, Server.Status.ACTIVE);
            if (result.isSuccess()) {
                return true;
            } else {
                throw new RuntimeException(result.getFault());
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean rebootInstance(OpenStackInstanceActionRequest request) {
        try {
            OSClient.OSClientV3 osClient = request.getOSClient();
            osClient.useRegion(request.getRegionId());

            Server server = osClient.compute().servers().get(request.getUuid());
            if (server == null) {
                throw new RuntimeException("server not exist");
            }

            ActionResponse response;
            if (!request.getForce()) {
                response = osClient.compute().servers().reboot(request.getUuid(), RebootType.SOFT);
            } else {
                response = osClient.compute().servers().reboot(request.getUuid(), RebootType.HARD);
            }
            if (!response.isSuccess()) {
                throw new RuntimeException(response.getFault());
            }
            CheckStatusResult result = OpenStackUtils.checkServerStatus(osClient, server, Server.Status.ACTIVE);
            if (result.isSuccess()) {
                return true;
            } else {
                throw new RuntimeException(result.getFault());
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean deleteInstance(OpenStackInstanceActionRequest request) {
        try {
            OSClient.OSClientV3 osClient = request.getOSClient();
            osClient.useRegion(request.getRegionId());

            Server server = osClient.compute().servers().get(request.getUuid());
            if (server == null) {
                return true;
            }
            ActionResponse response;
            if (!request.getForce()) {
                response = osClient.compute().servers().delete(request.getUuid());
            } else {
                //force DElETE
                response = osClient.compute().servers().action(request.getUuid(), Action.FORCEDELETE);
            }
            if (response.isSuccess()) {
                return true;
            } else {
                throw new RuntimeException(response.getFault());
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean attachDisk(OpenStackDiskActionRequest request) {
        try {
            OSClient.OSClientV3 osClient = request.getOSClient();
            osClient.useRegion(request.getRegionId());

            Volume volume = osClient.blockStorage().volumes().get(request.getDiskId());
            if (volume == null) {
                throw new RuntimeException("volume not exist");
            }
            Server server = osClient.compute().servers().get(request.getInstanceUuid());
            if (server == null) {
                throw new RuntimeException("server not exist");
            }
            osClient.compute().servers().attachVolume(request.getInstanceUuid(), request.getDiskId(), request.getDevice());

            CheckStatusResult result = OpenStackUtils.checkDiskStatus(osClient, volume, Volume.Status.IN_USE);
            if (result.isSuccess()) {
                return true;
            } else {
                throw new RuntimeException(result.getFault());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean detachDisk(OpenStackDiskActionRequest request) {
        try {
            OSClient.OSClientV3 osClient = request.getOSClient();
            osClient.useRegion(request.getRegionId());

            Volume volume = osClient.blockStorage().volumes().get(request.getDiskId());
            if (volume == null) {
                throw new RuntimeException("volume not exist");
            }
            ActionResponse response;
            if (!request.isForce()) {
                response = osClient.blockStorage().volumes().detach(request.getDiskId(), null);
            } else {
                //force
                response = osClient.blockStorage().volumes().forceDetach(request.getDiskId(), null, null);
            }
            if (!response.isSuccess()) {
                throw new RuntimeException(response.getFault());
            }
            CheckStatusResult result = OpenStackUtils.checkDiskStatus(osClient, volume, Volume.Status.AVAILABLE);
            if (result.isSuccess()) {
                return true;
            } else {
                throw new RuntimeException(result.getFault());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean deleteDisk(OpenStackDiskActionRequest request) {
        try {
            OSClient.OSClientV3 osClient = request.getOSClient();
            osClient.useRegion(request.getRegionId());

            Volume volume = osClient.blockStorage().volumes().get(request.getDiskId());
            if (volume == null) {
                return true;
            }

            ActionResponse response;
            if (!request.isForce()) {
                response = osClient.blockStorage().volumes().delete(request.getDiskId());
            } else {
                //force
                response = osClient.blockStorage().volumes().forceDelete(request.getDiskId());
            }
            if (response.isSuccess()) {
                return true;
            } else {
                throw new RuntimeException(response.getFault());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static boolean enlargeDisk(OpenStackDiskEnlargeRequest request) {
        try {
            OSClient.OSClientV3 osClient = request.getOSClient();
            osClient.useRegion(request.getRegionId());

            Volume volume = osClient.blockStorage().volumes().get(request.getDiskId());
            if (volume == null) {
                throw new RuntimeException("volume not exist");
            }
            if (StringUtils.isNotBlank(request.getInstanceUuid())) {
                Server server = osClient.compute().servers().get(request.getInstanceUuid());
                if (server == null) {
                    throw new RuntimeException("server not exist");
                }
                //可能需要关机操作？目前z版测试不需要
            }

            ActionResponse response = osClient.blockStorage().volumes().extend(request.getDiskId(), request.getNewDiskSize());

            if (response.isSuccess()) {
                return true;
            } else {
                throw new RuntimeException(response.getFault());
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static F2CDisk createDisk(OpenStackDiskCreateRequest request) {
        try {
            OSClient.OSClientV3 osClient = request.getOSClient();
            osClient.useRegion(request.getRegionId());

            VolumeBuilder builder = Builders.volume()
                    .name(request.getDiskName())
                    .description(request.getDescription())
                    .size(request.getSize())
                    .zone(request.getZone());
            if (StringUtils.isNotBlank(request.getDiskType())) {
                builder.volumeType(request.getDiskType());
            }
            Volume volume = osClient.blockStorage().volumes().create(builder.build());
            CheckStatusResult result = OpenStackUtils.checkDiskStatus(osClient, volume, Volume.Status.AVAILABLE);
            if (!result.isSuccess()) {
                throw new RuntimeException(result.getFault());
            }

            //创建出来后挂载
            if (StringUtils.isNotBlank(request.getInstanceUuid())) {
                Server server = osClient.compute().servers().get(request.getInstanceUuid());
                if (server == null) {
                    throw new RuntimeException("server not exist");
                }
                osClient.compute().servers().attachVolume(request.getInstanceUuid(), volume.getId(), null);
                result = OpenStackUtils.checkDiskStatus(osClient, volume, Volume.Status.IN_USE);
                if (!result.isSuccess()) {
                    throw new RuntimeException(result.getFault());
                }
            }

            return OpenStackUtils.toF2CDisk((Volume) result.getObject(), request.getRegionId());

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }

    }

    public static List<VolumeType> listVolumeType(OpenStackBaseRequest request) {
        List<VolumeType> list = new ArrayList<>();
        try {
            OSClient.OSClientV3 osClient = request.getOSClient();
            osClient.useRegion(request.getRegionId());

            for (org.openstack4j.model.storage.block.VolumeType type : osClient.blockStorage().volumes().listVolumeTypes()) {
                list.add(new VolumeType().setId(type.getId()).setName(type.getName()));
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return list;
    }
}