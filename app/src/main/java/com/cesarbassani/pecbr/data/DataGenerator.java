package com.cesarbassani.pecbr.data;

import android.content.Context;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.cesarbassani.pecbr.R;

@SuppressWarnings("ResourceType")
public class DataGenerator {

    public static List<String> getStringsMonth(Context ctx) {
        List<String> items = new ArrayList<>();
        String arr[] = ctx.getResources().getStringArray(R.array.month);
        for (String s : arr) items.add(s);
        Collections.shuffle(items);
        return items;
    }
}
