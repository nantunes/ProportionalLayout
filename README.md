## Synopsis

Layout that calculates its height based on its width, or vice versa.

Fork of com.android.contacts.common.widget.ProportionalLayout from the Android Open Source Project (authored by Chiao Cheng).

## Motivation

Need to show widgets with variable size (responsive interface) but keeping the same aspect ratio, particularly standardized ones.

## Code Example

```xml
<net.nantunes.widget.ProportionalLayout android:layout_margin="10dp" android:background="@android:color/white"
      pl:ratio="4:3" pl:direction="width:height"
      android:layout_height="wrap_content" android:layout_width="100dp">

    <ImageView
        android:layout_width="match_parent"
        android:layout_height="match_parent" android:scaleType="centerCrop" android:src="@drawable/poster"/>

</net.nantunes.widget.ProportionalLayout>
```

## API Reference

- "ratio" is the intended aspect ratio. Can be a float value, a percentage, a proportion in the format XX:YY or some common predefined aspect ratios (golden, paper_iso, hdtv, ...).
- "direction" indicates which dimension depends from the other. The default is "width:height", in which height := width * ratio.

## Installation

For now... clone and use it.

## License

License kept: Apache License, Version 2.0.

### Stating changes

- Changed the package name;
- Deployed it as a Android Library;
- Changed xml values of "direction" attribute from "widthToHeight" and "heightToWidth" to "width:height" and "height:width" respectively, for better identification with the XX:YY ratio format;
- Added support in the "ratio" attribute for percentage, proportion in the XX:YY format and some predefined common aspect ratios;
- Added simple test app.
