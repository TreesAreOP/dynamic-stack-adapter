# DynamicStackAdapter
This library helps to achieve a stack behaviour for RecyclerViews. It includes a Builder which handles all the
creation and configuration of the Adapter. </br>
![Imgur](http://i.imgur.com/oAfmXdE.gif)</br>
Keep in mind that scrolling is disabled because it would interfere with the Drag&Drop mechanics.

# Setup

Add jitpack.io to your repositories build.gradle file:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

And then add the dependency
```
		dependencies {
		compile 'com.github.TreesAreOp:dynamic-stack-adapter:v0.3-alpha'
	}
```

# Usage
To configure the Dynamic Stack Adapter you should use the DynamicStackBuilder class. The following example shows 
a minimum configuration of the RecyclerView
```java
final MyAdapter adapter = (MyAdapter) new DynamicStackBuilder()
                .stackRecyclerView(stackView)
                .withAdapterType(MyAdapter.class)
                .withViewHolderType(MyViewHolder.class)
                .withItemLayoutID(R.layout.item_layout)
                .build();
```
You need to provide your own DynamicStackAdapter and DynamicStackViewHolder as well as a layout for the item views. Your custom Adapter
needs to extend the DynamicStackAdapter and you need to specify your Item Type T and your custom DynamicStackViewHolder VH. 
```java
public abstract class DynamicStackAdapter<T, VH extends DynamicStackViewHolder> 
```
Subclass example:
```java
class MyAdapter extends DynamicStackAdapter<DataItem, MyViewHolder>
```

A custom DynamicStackViewHolder could be implemented like this:
```java
class MyViewHolder extends DynamicStackViewHolder {

    //you can reference all your views of the item layout here
    TextView text;

    protected MyViewHolder(View itemView, DynamicStackAdapter adapter) {
        super(itemView, adapter);
        df = new DecimalFormat("#");
    }

    //here you get your views from the layout
    @Override
    protected void findCustomViews(View view) {
        text = (TextView) view.findViewById(R.id.content);
    }

    //this method is called when the user resizes the item AND when a new item is added
    @Override
    protected void updateOnResize(int i, Object o, float percentage) {
        text.setText("percentage: " + df.format(percentage * 100f) + "%");
    }

}
```
And thats basically all you need to know. 
1. extend DynamicStackAdapter and DynamicStackViewHolder and implement your own versions
2. run the builder

Through the builder you can customize the Adapter, ViewHolder and RecyclerView. All of the following commands are optional. 
Most of them are self explanatory. You can read the documentation if anything is unclear. Default values will be used if you don't set those values yourself.
```java
final MyAdapter adapter = (MyAdapter) new DynamicStackBuilder() //reqired
                .stackRecyclerView(stackView) //reqired
                .withAdapterType(MyAdapter.class) //reqired
                .withViewHolderType(MyViewHolder.class) //reqired
                .withItemLayoutID(R.layout.item_layout) //reqired
                .allowUserResizingItems(true) //optional
                .provideResizeAreaID(R.id.resize_view) //optional
                .setPixelPadding(2) //optional
                .setMaxItems(6) //optional
                .setAutoResizeItems(true) //optional
                .allowUserMoveItems(true) //optional
                .allowUserDeleteItems(true) //optional
                .setDataSet(items) //optional
                .build();
```

## Note:
I highly recommend not using any margins for your item views! 
if you really must use margin you can look into the setPixelPadding method (which is just a workaround for now) 
or try to encapsulate the item view in a layout and use margin on the parent layout. Heres an example:
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent
    android:layout_height="50dp"  
    android:gravity="center">

    <FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="vertical"
        android:layout_marginBottom="2dp"
        android:layout_marginTop="2dp">
        
        ...your content here
        
    </FrameLayout>
</RelativeLayout>
```
I'd also like to mention that i used some helper classes which handle touch events from a tutorial by Paul Burke. 
You can read it here: https://medium.com/@ipaulpro/drag-and-swipe-with-recyclerview-b9456d2b1aaf

# License

Copyright 2017 Adrian Bernhart

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
