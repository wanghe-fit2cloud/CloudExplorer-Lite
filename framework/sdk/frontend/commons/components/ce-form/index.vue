<script setup lang="ts">
import { ref, watch } from "vue";
import type { FormView } from "@commons/components/ce-form/type";
import formApi from "@commons/api/form_resource_api/index";
import type { FormInstance } from "element-plus";
import type { SimpleMap } from "@commons/api/base/type";
// 表单数据
const formData = ref<SimpleMap<any>>({});
// 校验实例对象
const ruleFormRef = ref<FormInstance>();
const resourceLoading = ref<boolean>(false);

const props = withDefaults(
  defineProps<{
    // 页面渲染数据
    formViewData: Array<FormView>;
    // 调用接口所需要的其他参数
    otherParams: any;
    // 是否只读
    readOnly: boolean;
    // 表单 label 的展示位置
    labelPosition: "left" | "right" | "top";
  }>(),
  { readOnly: false, labelPosition: "left" }
);

// 发生变化
const change = (formItem: FormView) => {
  const relationForms = props.formViewData.filter((item) =>
    item.relationTrigger.includes(formItem.field)
  );
  relationForms.forEach((relationItem) => {
    // 修改当前的值为null
    formData.value[relationItem.field] = undefined;
    if (
      relationItem.clazz &&
      relationItem.method &&
      relationItem.relationTrigger.every((r) => formData.value[r])
    ) {
      formApi
        .getResourceMyMethod(
          relationItem.clazz,
          relationItem.method,
          {
            ...formData.value,
            ...props.otherParams,
          },
          resourceLoading
        )
        .then((ok) => {
          relationItem.optionList = ok.data;
        });
    }
  });
};

/**
 * 表单数据初始化,调用函数
 * @param formItem 表单item
 */
const initFormItem = (formItem: FormView) => {
  const relationForms = props.formViewData.filter((item) =>
    item.relationTrigger.includes(formItem.field)
  );
  relationForms.forEach((relationItem) => {
    if (
      relationItem.clazz &&
      relationItem.method &&
      relationItem.relationTrigger.every((r) => formData.value[r])
    ) {
      formApi
        .getResourceMyMethod(
          relationItem.clazz,
          relationItem.method,
          {
            ...formData.value,
            ...props.otherParams,
          },
          resourceLoading
        )
        .then((ok) => {
          relationItem.optionList = ok.data;
        });
    }
  });
};

/**
 * 初始化表单显示数据,设置默认值
 */
const initDefaultData = () => {
  // 初始化默认值 defaultValue后端只能传入string,所以只能是string
  props.formViewData.forEach((item) => {
    if (item.defaultValue && !formData.value[item.field]) {
      if (item.defaultJsonValue) {
        formData.value[item.field] = JSON.parse(item.defaultValue);
      } else {
        formData.value[item.field] = item.defaultValue;
      }
    }
    if (
      item.clazz &&
      item.method &&
      item.relationTrigger.every((r) => formData.value[r])
    ) {
      formApi
        .getResourceMyMethod(item.clazz, item.method, {
          ...formData.value,
          ...props.otherParams,
        })
        .then((ok) => {
          item.optionList = ok.data;
        });
    }
  });
};

// 提交表单
const submit = (exec: (formData: any) => void) => {
  if (!ruleFormRef.value) return;
  ruleFormRef.value.validate((valid) => {
    if (exec && valid) {
      exec(formData.value);
    }
  });
};

const validate = () => {
  if (!ruleFormRef.value) return;
  return ruleFormRef.value.validate();
};

// 监控formViewData 用于初始化数据
watch(
  () => props.formViewData,
  (pre) => {
    if (pre) {
      initDefaultData();
      Object.keys(formData.value).forEach((key: string) => {
        const form = pre.find((f) => f.field === key);
        if (form) {
          initFormItem(form);
        }
      });
    }
  },
  {
    immediate: true,
  }
);
/**
 * 获取数据
 */
const getFormData = () => {
  return formData.value;
};

/**
 * 设置表单数据
 * @param data 表单数据
 */
const setData = (data: any) => {
  const dataKeys = Object.keys(data);
  dataKeys.forEach((key: string) => {
    try {
      formData.value[key] = JSON.parse(data[key]);
    } catch (e) {
      formData.value[key] = data[key];
    }
  });
};
/**
 * 清除表单数据
 */
const clearData = () => {
  formData.value = {};
};

// 暴露获取当前表单数据函数
defineExpose({
  getFormData,
  submit,
  clearData,
  initDefaultData,
  setData,
  validate,
});
</script>
<template>
  <el-form
    style="width: 100%"
    ref="ruleFormRef"
    label-width="130px"
    label-suffix=":"
    :label-position="labelPosition"
    :model="formData"
    v-loading="resourceLoading"
  >
    <div v-for="item in formViewData" :key="item.field" style="width: 100%">
      <el-form-item
        style="width: 100%"
        v-if="item.relationShowValues? item.relationShows.every((i:string) => item.relationShowValues.includes(formData[i])):item.relationShows.every((i:string) => formData[i])"
        :label="item.label"
        :prop="item.field"
        :rules="{
          message: item.label + '不能为空',
          trigger: 'blur',
          required: item.required,
        }"
      >
        <component
          :disabled="readOnly"
          style="width: 75%"
          @change="change(item)"
          v-model="formData[item.field]"
          :is="item.inputType"
          :formItem="item"
          :field="item.field"
          v-bind="{ ...JSON.parse(item.attrs) }"
        ></component>
        <span v-if="item.unit" style="padding-left: 15px">{{ item.unit }}</span>
      </el-form-item>
    </div>
  </el-form>
</template>

<style lang="scss"></style>
