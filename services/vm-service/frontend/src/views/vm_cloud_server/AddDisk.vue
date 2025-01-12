<script setup lang="ts">
import { computed, onMounted, ref } from "vue";
import VmCloudDiskApi from "@/api/vm_cloud_disk";
import VmCloudServerApi from "@/api/vm_cloud_server";
import BaseCloudAccountApi from "@commons/api/cloud_account";
import { useRouter } from "vue-router";
import type { VmCloudServerVO } from "@/api/vm_cloud_server/type";
import type { CloudAccount } from "@commons/api/cloud_account/type";
import { ElMessage } from "element-plus";
import { useI18n } from "vue-i18n";
const { t } = useI18n();

const router = useRouter();
const id = ref<string>(router.currentRoute.value.params.id as string);
const loading = ref(false);
const vmCloudServer = ref<VmCloudServerVO>();
const cloudAccount = ref<CloudAccount>();
const ceFormRef = ref<any>();
const createDiskFormData = ref();
const otherParams = computed(() => {
  return {
    ...cloudAccount.value,
    regionId: vmCloudServer.value?.region,
    zone: vmCloudServer.value?.zone,
    zoneId: vmCloudServer.value?.zone,
    instanceUuid: vmCloudServer.value?.instanceUuid,
    instanceTypeDTO: vmCloudServer.value,
    instanceChargeType: vmCloudServer.value?.instanceChargeType,
    instanceType: vmCloudServer.value?.instanceType,
  };
});

onMounted(() => {
  if (router.currentRoute.value.params.id) {
    // 获取云主机详情
    VmCloudServerApi.getVmCloudServerById(id.value, loading).then((res) => {
      vmCloudServer.value = res.data;

      // 获取云账号详情
      BaseCloudAccountApi.getCloudAccount(
        vmCloudServer.value.accountId as string,
        loading
      ).then((res) => {
        cloudAccount.value = res.data;

        // 获取相应云平台创建磁盘表单数据
        VmCloudDiskApi.getCreateDiskForm(
          cloudAccount.value.platform,
          loading
        ).then((res) => {
          createDiskFormData.value = res.data;
        });
      });
    });
  }
});

const handleCancel = () => {
  router.push({ name: "vm_cloud_server" });
};

const handleCreate = () => {
  // 单独校验 vmware 存储器
  if (
    cloudAccount.value?.platform == "fit2cloud_vsphere_platform" &&
    ceFormRef.value.getFormData().datastoreType !== "only-a-flag" &&
    ceFormRef.value.getFormData().datastore == null
  ) {
    ElMessage.error(t("vm_cloud_disk.msg.vm", "存储器不能为空"));
    return;
  }

  ceFormRef.value.validate().then(() => {
    const dataInfo = {
      regionId: vmCloudServer.value?.region,
      ...vmCloudServer.value,
      ...cloudAccount.value,
      ...ceFormRef.value.getFormData(),
    };

    // 获取相应云平台创建磁盘API
    VmCloudDiskApi.createDisk(dataInfo, loading).then(() => {
      router.push({ name: "vm_cloud_server" });
    });
  });
};
</script>

<template>
  <base-container>
    <template #form>
      <base-container>
        <template #header>
          <span>{{ $t("vm_cloud_disk.label.vm", "所属云主机") }}</span>
        </template>
        <template #content>
          {{ $t("vm_cloud_disk.label.cloudVm", "云主机") }}：{{
            vmCloudServer?.instanceName
          }}
        </template>
      </base-container>

      <base-container>
        <template #header>
          <span>{{ $t("vm_cloud_disk.label.disk_info", "磁盘信息") }}</span>
        </template>
        <template #content v-if="createDiskFormData?.forms">
          <CeForm
            ref="ceFormRef"
            :formViewData="createDiskFormData.forms"
            :otherParams="otherParams"
          ></CeForm>
        </template>
      </base-container>
    </template>
    <template #formFooter>
      <el-button @click="handleCancel()"
        >{{ $t("commons.btn.cancel") }}
      </el-button>
      <el-button type="primary" @click="handleCreate()"
        >{{ $t("commons.btn.save") }}
      </el-button>
    </template>
  </base-container>
</template>
