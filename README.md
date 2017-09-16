# DynamicStackAdapter
This library helps to achieve a stack behaviour for RecyclerViews. The user can move, delete and resize the items on the stack if desired. It works with the standard RecyclerView from the android support library.</br></br>
![Imgur](http://i.imgur.com/oAfmXdE.gif)</br></br>
Note: Keep in mind that scrolling is disabled because it would interfere with the Drag&Drop mechanics.

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
		...
		compile 'com.github.TreesAreOp:dynamic-stack-adapter:CURRENT_VERSION'
	}
```

# Usage
You should use the DynamicStackBuilder class to set up the stack behaviour. This Library works without it but using the Builder makes the configuration process less complicated and configures the used RecyclerView appropiately. The following example shows a minimum configuration of the RecyclerView through the Builder:
```java
final MyAdapter adapter = (MyAdapter) new DynamicStackBuilder()
                .stackRecyclerView(stackView)
                .withAdapterType(MyAdapter.class)
                .withViewHolderType(MyViewHolder.class)
                .withItemLayoutID(R.layout.item_layout)rrrrr
                .build();
```
You need to provide your own DynamicStackAdapter and DynamicStackViewHolder as well as a layout for the item views. Your custom Adapter
needs to extend the DynamicStackAdapter and you need to specify your Item Type T and your custom DynamicStackViewHolder VH. The withCreateViewHolder and withBindViewHolder methods can be implemented like usual.
Minimal example:
```java
class MyAdapter extends DynamicStackAdapter<Data, MyViewHolder> {
    public MyAdapter(RecyclerView container, Class holderClass) {
        super(container, holderClass);
    }

    @Override
    public void withCreateViewHolder(MyViewHolder myViewHolder) {
	//this method is called when a ViewHolder is created
    }

    @Override
    public void withBindViewHolder(MyViewHolder myViewHolder, int i, Data data) {
    	//this method is called when a ViewHolder is bound to a position
    }
}
```

A custom DynamicStackViewHolder could be implemented like this:
```java
class MyViewHolder extends DynamicStackViewHolder {

    //you can reference all your views of the item layout here

    protected MyViewHolder(View itemView, DynamicStackAdapter adapter) {
        super(itemView, adapter);
    }

    @Override
    protected void findCustomViews(View view) {
       //here you get your views from the layout with view.findViewByID(...)
    }

    @Override
    protected void updateOnResize(int i, Object o, float percentage) {
        //this method is called when the user resizes the item AND when a new item is added
    }

}
```
And thats basically all you need to know. 
1. extend DynamicStackAdapter and DynamicStackViewHolder and implement your own versions
2. run the builder and set up your RecyclerView, DynamicStackAdapter and DynamicStackViewHolder

A working example can be found in the app folder.
</br>
As mentioned before the builder allows you to customize the Adapter, ViewHolder and RecyclerView. All commands are listed below.
Most of them are self explanatory but the documentation should provide more insight if anything is unclear. Default values will be used if the optional methods are not executed.
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
If your application needs to handle orientation changes and other lifecycle related events
you can use the DynamicStackSaveManager. Just call the save and load methods in the 
 onSaveInstanceState and onRestoreInstanceState methods of your activity. The onRestoreInstanceState
 method needs to be the protected one with only the Bundle parameter. Additionally the used Data class
 needs to implement the java.io.Serializable Interface in order to allow saving and loading.

```Java 
@Override
protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    if (adapter != null) {
        DynamicStackSaveManager.saveDynamicStackAdapter(outState, adapter);
    }
}

@Override
protected void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    if (savedInstanceState != null) {
        DynamicStackSaveManager.restoreDynamicStackAdapter(savedInstanceState, adapter);
    }
}
```

## Note:
I highly recommend not using any margins for your item views because some height calculations could become incorrect! 
if you really must use margins you can look into the setPixelPadding method (which is just a workaround and brings other issues
with it) or try to encapsulate the item view in a parent layout and then add your margins to the actual (now child) item layout. Heres an example:
```xml
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
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
