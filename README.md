# CircleProgressbarDemo
CircleProgressbar

``compile 'com.solarexsoft.circleprogressbar:circleprogressbar:1.0.0'``

Usage:

```
    <com.solarexsoft.circleprogressbar.WaveProgressbar
            android:layout_height="wrap_content"
            android:layout_width="wrap_content"
            app:antiAlias="true"
            app:circleColor="@android:color/holo_blue_bright"
            app:bgCircleColor="@android:color/darker_gray"
            app:hint="剩余流量"
            app:hintSize="15sp"
            app:value="70"
            app:valueSize="30sp"
            />
        <com.solarexsoft.circleprogressbar.ArcProgressbar
            android:layout_width="160dp"
            android:layout_height="160dp"
            app:arc_bottom_text="STORAGE"
            app:arc_bottom_text_size="16sp"
            app:arc_finished_color="@android:color/holo_red_dark"
            app:arc_progress="40"
            app:arc_unfinished_color="@android:color/holo_blue_bright"
            />
        <com.solarexsoft.circleprogressbar.CircleProgressbar
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:animTime="1000"
            app:antiAlias="true"
            app:bgArcColor="@color/colorAccent"
            app:hint="今天您一共走了"
            app:hintSize="15sp"
            app:maxValue="10000"
            app:startAngle="135"
            app:sweepAngle="270"
            app:unit="步"
            app:unitSize="15sp"
            app:value="10000"
            app:valueSize="30sp"
            app:arcColors="@array/colors"/>
        <com.solarexsoft.circleprogressbar.DialProgressbar
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            app:animTime="1000"
            app:hint="当前车速"
            app:hintSize="15sp"
            app:value="160"
            app:valueSize="30sp"
            app:maxValue="300"
            app:unit="KM/H"
            app:unitSize="15sp"
            app:bgArcColor="@android:color/background_dark"
            app:arcColors="@array/colors"
            app:startAngle="144"
            app:sweepAngle="252"/>
```

More attributes see [attrs.xml](https://github.com/flyfire/CircleProgressbarDemo/blob/master/circleprogressbar/src/main/res/values/attrs.xml)

<iframe width="560" height="315" src="https://www.youtube.com/embed/PJH8Fy2ppiA" frameborder="0" allowfullscreen></iframe>