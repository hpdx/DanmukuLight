# DanmukuLight
Android上专为视频直播打造的轻量级弹幕库（100多kb）

## demo运行后的效果如下：
<img src="https://github.com/hpdx/DanmukuLight/blob/master/result_image.jpg" width="320px" />

[下载示例Apk](https://github.com/hpdx/DanmukuLight/blob/master/danmuku.apk)


## 目前支持以下需求
* 继承自View实现的DanMuView（直播间的弹幕，推荐使用这种）
* 继承自SurfaceView实现的DanMuView
* 支持显示富文本内容
* 支持弹幕单击事件的处理

## 使用：

在Module中的build.gradle文件里，添加以下依赖：
```
allprojects {
    repositories {
        jcenter()
        mavenCentral() // 切记要添加这个

        maven {
            url 'https://dl.bintray.com/hpdx/maven/'
        }
    }
}

compile 'com.anbetter:danmukulight:1.0.1'
```

在xml中添加
```
        <com.anbetter.danmuku.DanMuView
            android:id="@+id/danmaku_container_broadcast"
            android:layout_width="match_parent"
            android:layout_height="80dp"/>
```
注：每条弹道的高度目前为40dp


启动弹幕引擎
```
        mDanMuContainerBroadcast = (DanMuView) findViewById(R.id.danmaku_container_broadcast);
        mDanMuContainerBroadcast.prepare();
```


创建弹幕对象
```
        DanMuModel danMuView = new DanMuModel();
        danMuView.setDisplayType(DanMuModel.RIGHT_TO_LEFT);
        danMuView.setPriority(DanMuModel.NORMAL);
        danMuView.marginLeft = DimensionUtil.dpToPx(mContext, 30);

        // 显示的文本内容
        danMuView.textSize = DimensionUtil.spToPx(mContext, 14);
        danMuView.textColor = ContextCompat.getColor(mContext, R.color.light_green);
        danMuView.textMarginLeft = DimensionUtil.dpToPx(mContext, 5);

        if (entity.getRichText() != null) {
            danMuView.text = RichTextParse.parse(mContext, entity.getRichText(), DimensionUtil.spToPx(mContext, 18), false);
        } else {
            danMuView.text = entity.getText();
        }

        // 弹幕文本背景
        danMuView.textBackground = ContextCompat.getDrawable(mContext, R.drawable.corners_danmu);
        danMuView.textBackgroundMarginLeft = DimensionUtil.dpToPx(mContext, 15);
        danMuView.textBackgroundPaddingTop = DimensionUtil.dpToPx(mContext, 3);
        danMuView.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mContext, 3);
        danMuView.textBackgroundPaddingRight = DimensionUtil.dpToPx(mContext, 15);
```


将弹幕添加到弹道上
```
mDanMuContainerBroadcast.add(danMuView);
```
OK了，就这么简单，一条弹幕就发送成功了

隐藏或者显示弹幕
```
mDanMuContainerBroadcast.hideAllDanMuView(hide); // boolean
```

## 对了，若要弹幕是能响应单击事件，需要添加如下处理：

在xml中添加DanMuParentView
```
 <com.anbetter.danmuku.DanMuParentView
        android:id="@+id/dpv_broadcast"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="30dp">

        <com.anbetter.danmuku.DanMuView
            android:id="@+id/danmaku_container_broadcast"
            android:layout_width="match_parent"
            android:layout_height="80dp"/>

    </com.anbetter.danmuku.DanMuParentView>
```

在构建弹幕对象的代码中添加
```
        danMuView.enableTouch(true);
        danMuView.setOnTouchCallBackListener(new OnDanMuTouchCallBackListener() {
             @Override
             public void callBack(DanMuModel danMuView) {

             }
        });
```

## 在使用过程中有遇到bug，欢迎提issuse ! 若你觉得还不错，请点Star, 谢谢！

