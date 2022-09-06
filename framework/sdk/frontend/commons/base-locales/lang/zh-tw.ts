import elementZhTwLocale from "element-plus/lib/locale/lang/zh-tw";
import fit2cloudZhTwLocale from "fit2cloud-ui-plus/src/locale/lang/zh-tw";
import subModuleTwLocale from "@/locales/lang/zh-tw";

const message = {
  commons: {
    home: "首頁",
    notice: "通知",
    to_do_list: "待辦清單",
    view_all: "查看全部",
    operation: "操作",
    name: "名稱",
    tag: "標簽",
    org: "組織",
    workspace: "工作空間",
    os: "操作系統",
    os_version: "操作系統版本",
    status: "狀態",
    create_time: "創建時間",
    update_time: "更新時間",
    description: "描述",
    cloud_account: {
      native: "雲賬號",
      name: "雲賬號名稱",
      data_center: "數據中心",
      cluster: "集群",
      region: "區域",
      zone: "可用區",
      host: "宿主機",
      storage: "存儲器",
      disk: "磁盤",
      vm: "虛擬機",
      image: "鏡像",
    },
    message_box: {
      alert: "警告",
      confirm: "確認",
      prompt: "提示",
      confirm_delete: "確認删除",
    },
    btn: {
      login: "登錄",
      yes: "是",
      no: "否",
      ok: "確定",
      add: "添加",
      create: "創建",
      delete: "刪除",
      edit: "編輯",
      save: "保存",
      close: "關閉",
      submit: "提交",
      publish: "發布",
      cancel: "取消",
      return: "返回",
      grant: "授權",
      hide: "隱藏",
      display: "顯示",
      enable: "啟用",
      disable: "禁用",
      copy: "複制",
      sync: "同步",
      view_api: "查看 API",
      prev: "上一步",
      next: "下一步",
      switch_lang: "切換語言",
      add_favorites: "收藏",
      cancel_favorites: "取消收藏",
      search: "查找",
      refresh: "刷新",
      import: "導入",
      export: "導出",
      upload: "上傳",
      download: "下載",
    },
    msg: {
      success: "{0}成功",
      op_success: "操作成功",
      save_success: "保存成功",
      delete_success: "刪除成功",
      fail: "{0}失敗",
      delete_canceled: "已取消刪除",
    },
    validate: {
      limit: "長度在 {0} 到 {1} 個字符",
      input: "請輸入{0}",
      select: "請選擇{0}",
      confirm_pwd: "兩次輸入的密碼不壹致",
      pwd: "有效密碼：8-30比特，英文大小寫字母+數位+特殊字元",
    },
    personal: {
      personal_info: "個人信息",
      edit_pwd: "修改密碼",
      help_document: "幫助文檔",
      exit_system: "退出系統",
      old_password: "原密碼",
      new_password: "新密碼",
      confirm_password: "確認密碼",
      login_identifier: "登錄標識",
      username: "用戶名",
      phone: "手機號碼",
      wechat: "企業微信號碼",
    },
    date: {
      select_date: "選擇日期",
      start_date: "開始日期",
      end_date: "結束日期",
      select_time: "選擇時間",
      start_time: "開始時間",
      end_time: "結束時間",
      select_date_time: "選擇日期時間",
      start_date_time: "開始日期時間",
      end_date_time: "結束日期時間",
      range_separator: "至",
      date_time_error: "開始日期不能大於結束日期",
    },
    login: {
      username: "用戶名",
      password: "密碼",
      title: "CloudExplorer 雲服務平臺",
      welcome: "歡迎回來，請輸入用戶名和密碼登錄",
      expires: "認證信息已過期，請重新登錄",
    },
  },
};

export default {
  ...elementZhTwLocale,
  ...fit2cloudZhTwLocale,
  ...message,
  ...subModuleTwLocale,
};
