import elementZhLocale from "element-plus/lib/locale/lang/zh-cn";
import fit2cloudZhLocale from "fit2cloud-ui-plus/src/locale/lang/zh-cn"; // 加载 fit2cloud 的国际化文件
import subModuleZhLocale from "@/locales/lang/zh-cn"; // 加载子模块自定义的国际化文件

const message = {
  commons: {
    home: "首页",
    notice: "通知",
    to_do_list: "代办列表",
    view_all: "查看全部",
    operation: "操作",
    name: "名称",
    tag: "标签",
    org: "组织",
    workspace: "工作空间",
    os: "操作系统",
    os_version: "操作系统版本",
    status: "状态",
    create_time: "创建时间",
    update_time: "更新时间",
    cloud_account: {
      native: "云账号",
      name: "云账号名称",
      data_center: "数据中心",
      cluster: "集群",
      region: "区域",
      zone: "可用区",
      host: "宿主机",
      storage: "存储器",
      disk: "磁盘",
      vm: "虚拟机",
      image: "镜像",
    },
    message_box: {
      alert: "警告",
      confirm: "确认",
      prompt: "提示",
      confirm_delete: "确认删除",
    },
    btn: {
      login: "登录",
      yes: "是",
      no: "否",
      ok: "确定",
      add: "添加",
      create: "创建",
      delete: "删除",
      edit: "编辑",
      save: "保存",
      close: "关闭",
      submit: "提交",
      publish: "发布",
      cancel: "取消",
      return: "返回",
      grant: "授权",
      hide: "隐藏",
      display: "显示",
      enable: "启用",
      disable: "禁用",
      copy: "复制",
      sync: "同步",
      view_api: "查看 API",
      prev: "上一步",
      next: "下一步",
      switch_lang: "切换语言",
      add_favorites: "收藏",
      cancel_favorites: "取消收藏",
      search: "查找",
      refresh: "刷新",
      import: "导入",
      export: "导出",
      upload: "上传",
      download: "下载",
    },
    msg: {
      success: "{0}成功",
      op_success: "操作成功",
      save_success: "保存成功",
      delete_success: "删除成功",
      fail: "{0}失败",
    },
    validate: {
      limit: "长度在 {0} 到 {1} 个字符",
      input: "请输入{0}",
      select: "请选择{0}",
      confirm_pwd: "两次输入的密码不一致",
    },
    personal: {
      personal_info: "个人信息",
      edit_pwd: "修改密码",
      help_document: "帮助文档",
      exit_system: "退出系统",
      old_password: "原密码",
      new_password: "新密码",
      confirm_password: "确认密码",
      login_identifier: "登录标识",
      username: "用户名",
      phone: "手机号码",
      wechat: "企业微信号码",
    },
    date: {
      select_date: "选择日期",
      start_date: "开始日期",
      end_date: "结束日期",
      select_time: "选择时间",
      start_time: "开始时间",
      end_time: "结束时间",
      select_date_time: "选择日期时间",
      start_date_time: "开始日期时间",
      end_date_time: "结束日期时间",
      range_separator: "至",
      date_time_error: "开始日期不能大于结束日期",
    },
    login: {
      username: "用户名",
      password: "密码",
      title: "CloudExplorer 云服务平台",
      welcome: "欢迎回来，请输入用户名和密码登录",
      expires: "认证信息已过期，请重新登录",
    },
  },
};

export default {
  ...elementZhLocale,
  ...fit2cloudZhLocale,
  ...message,
  ...subModuleZhLocale,
};
