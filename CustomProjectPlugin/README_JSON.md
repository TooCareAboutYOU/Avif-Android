## 插件配置

1、选填配置：pgyConfig、firImConfig</br>
2、必填配置：ddConfig、ddContent中根据自定义的msgtype选择对应的配置模型</br>

```
{
  "apkOutputPath": "/Users/zhangshuai/Project____/Android/AvifAndroid/app/release/app-release.apk",
  "apkName": "app-release.apk",
  "isPgy": false,
  "pgyConfig": {
    "pgyBaseUrl": "https://www.pgyer.com/apiv2/app/",
    "pgyAppKey": "b047ec0d32eebb0b3d3412f8eac0a82d",
    "pgyApiKey": "de0f88af701cd8853bec33df259cc83e",
    "pgyBuildType": "apk",
    "pgyOversea": 2,
    "pgyBuildInstallType": 1,
    "pgyBuildPassword": "",
    "pgyBuildDescription": "",
    "pgyBuildUpdateDescription": "",
    "pgyBuildInstallDate": 2,
    "pgyBuildInstallStartDate": "",
    "pgyBuildInstallEndDate": "",
    "pgyBuildChannelShortcut": ""
  },
  "firImConfig": {
    "firBaseUrl": "http://api.bq04.com/apps/",
    "type": "android",
    "packageName": "zs.android.avif",
    "apiToken": "62fa6ffe5a53ec00a5e6265a0e00c372",
    "icon": "/Users/zhangshuai/Project____/Android/AvifAndroid/app/release/icon_app.png",
    "xName": "Avif",
    "xVersion": "1.0.2",
    "xBuild": "102",
    "xChangeLog": "Fir.im版本更新"
  },
  "ddConfig": {
    "ddWebSecret": "SECa3a5ddaadf2b297d00b9684a8a6b64f88f52a8288e6fc0bdc0a6db49bccec2f5",
    "ddWebHookUrl": "https://oapi.dingtalk.com/robot/send?access_token=d0847aaa3043c3d85e96a6784502c1304b6e3103f8b872d6b82e7b6c4f04a0a1"
  },
  "ddContent": {
    "msgtype": "link",
    "text": {
      "content": "我是来自蒲公英Apk动态发布的消息测试用例 @zs18874703156 "
    },
    "link": {
      "text": "我是来自蒲公英的Apk动态发布的消息测试用例",
      "title": "Release version",
      "picUrl": "https://pro-icon-qn.bv40.com/19f628d4b5a4327808102bce9c778a0a5c782bdb?attname=icon_app.png&tmp=1670322537.9097416",
      "messageUrl": "https://www.pgyer.com/OXKA"
    },
    "photo": {
      "photoURL": "https://www.pgyer.com/app/qrcodeHistory/2043a9d570d91ffb95da85e6ee745108ead34c27c7b7f7947bfb35ca44235055"
    },
    "markdown": {
      "title": "上海天气",
      "text": "#### 上海天气 @zs18874703156 \n > 9度，西北风1级，空气良89，相对温度73%\n > ![screenshot](https://www.pgyer.com/app/qrcodeHistory/2043a9d570d91ffb95da85e6ee745108ead34c27c7b7f7947bfb35ca44235055)\n > ###### 17点50分发布 [天气](https://www.dingtalk.com) \n"
    },
    "actionCard": {
      "title": "我是Apk动态发布的消息测试用例",
      "text": "![screenshot](https://www.pgyer.com/app/qrcodeHistory/2043a9d570d91ffb95da85e6ee745108ead34c27c7b7f7947bfb35ca44235055) 我是Apk动态发布的消息测试用例",
      "btnOrientation": "0",
      "singleTitle": "阅读全文",
      "singleURL": "https://www.pgyer.com/OXKA"
    },
    "feedCard": {
      "links": [
        {
          "title": "我是Apk动态发布的消息测试用例-1",
          "messageURL": "https://www.pgyer.com/OXKA",
          "picURL": "https://www.pgyer.com/app/qrcodeHistory/2043a9d570d91ffb95da85e6ee745108ead34c27c7b7f7947bfb35ca44235055"
        },
        {
          "title": "我是Apk动态发布的消息测试用例-2",
          "messageURL": "https://www.pgyer.com/OXKA",
          "picURL": "https://www.pgyer.com/app/qrcodeHistory/2043a9d570d91ffb95da85e6ee745108ead34c27c7b7f7947bfb35ca44235055"
        },
        {
          "title": "我是Apk动态发布的消息测试用例-3",
          "messageURL": "https://www.pgyer.com/OXKA",
          "picURL": "https://www.pgyer.com/app/qrcodeHistory/2043a9d570d91ffb95da85e6ee745108ead34c27c7b7f7947bfb35ca44235055"
        }
      ]
    },
    "at": {
      "atMobiles": [
        "18874703156"
      ],
      "atUserIds": [
        "zs18874703156"
      ],
      "isAtAll": false
    }
  }
}
```

钉钉官网API: <https://open.dingtalk.com/document/group/custom-robot-access></br>
蒲公英官网API：<https://www.pgyer.com/doc/view/api#fastUploadApp></br>

### 注：内容需要包含钉钉机器人配置的关键字