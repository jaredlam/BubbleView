# BubbleView
Bubble view for Android

![alt tag](https://media.giphy.com/media/xTiTnhGVIMGO6z07oA/giphy.gif)

- [Introduction](#Introduction)
- [Download](#Download)
- [Usage](#Usage)
- [License](#Introduction)

# Introduction

- Bubble layout will generate Bubble view at random location on screen.

- Bubble view is extend from TextView.

- Bubble views will bounce with each other.

- Bubble layout will try to contains all child inside its bounds.

# Download
```groovy
dependencies {
    compile 'com.jaredlam.bubbleview:library:0.1.0'
}
```

# Usage

```xml
<com.jaredlam.bubbleview.BubbleLayout
        android:id="@+id/bubble_layout"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
```

```java
BubbleLayout layout = (BubbleLayout) findViewById(R.id.bubble_layout);



for (String label : labels) {
    BubbleView bubbleView = new BubbleView(this);
    bubbleView.setText(label);
    bubbleView.setGravity(Gravity.CENTER);
    bubbleView.setPadding(10, 10, 10, 10);
    bubbleView.setTextColor(Color.parseColor("#000000"));
    layout.addViewSortByWidth(bubbleView);
}
```

# License

Copyright (C) 2015 Jared Luo
jaredlam86@gmail.com

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.










