package com.doubtech.universalremote.io;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;
import android.view.View;

import com.doubtech.universalremote.R;

public class BackgroundLoader {
    private static final HashMap<String, Integer> BUTTON_NAMES_TO_RESOURCES = new HashMap<String, Integer>();
    private static final SparseArray<String> BUTTON_RESOURCES_TO_NAMES = new SparseArray<String>();

    static {
        BUTTON_NAMES_TO_RESOURCES.put("empty_center_button_normal", R.drawable.empty_center_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_button", R.drawable.rounded_button);
        BUTTON_NAMES_TO_RESOURCES.put("square_button_pressed", R.drawable.square_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_top_button_normal", R.drawable.edge_buttons_top_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_left_button_pressed", R.drawable.rounded_left_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_top_button_pressed", R.drawable.rounded_top_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_top_button", R.drawable.edge_buttons_top_button);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_bottom_right_button_normal", R.drawable.edge_buttons_bottom_right_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_bottom_right_button", R.drawable.edge_buttons_bottom_right_button);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_top_left_button_normal", R.drawable.edge_buttons_top_left_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_top_button_normal", R.drawable.rounded_top_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_bottom_left_button_pressed", R.drawable.edge_buttons_bottom_left_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_bottom_left_button", R.drawable.edge_buttons_bottom_left_button);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_bottom_button_pressed", R.drawable.rounded_bottom_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_left_button", R.drawable.rounded_left_button);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_top_right_button_pressed", R.drawable.edge_buttons_top_right_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("empty_center_button", R.drawable.empty_center_button);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_bottom_button", R.drawable.rounded_bottom_button);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_right_button_pressed", R.drawable.rounded_right_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_bottom_right_button_pressed", R.drawable.edge_buttons_bottom_right_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_left_button_normal", R.drawable.edge_buttons_left_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_right_button_normal", R.drawable.edge_buttons_right_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_right_button", R.drawable.edge_buttons_right_button);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_bottom_button_normal", R.drawable.edge_buttons_bottom_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_top_button_pressed", R.drawable.edge_buttons_top_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_button_normal", R.drawable.rounded_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_bottom_button_pressed", R.drawable.edge_buttons_bottom_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_button_pressed", R.drawable.rounded_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("square_button", R.drawable.square_button);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_top_left_button", R.drawable.edge_buttons_top_left_button);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_left_button_normal", R.drawable.rounded_left_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_right_button", R.drawable.rounded_right_button);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_left_button_pressed", R.drawable.edge_buttons_left_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_top_button", R.drawable.rounded_top_button);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_bottom_button", R.drawable.edge_buttons_bottom_button);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_left_button", R.drawable.edge_buttons_left_button);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_top_right_button_normal", R.drawable.edge_buttons_top_right_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_top_right_button", R.drawable.edge_buttons_top_right_button);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_bottom_button_normal", R.drawable.rounded_bottom_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("rounded_right_button_normal", R.drawable.rounded_right_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("empty_center_button_pressed", R.drawable.empty_center_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("square_button_normal", R.drawable.square_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_bottom_left_button_normal", R.drawable.edge_buttons_bottom_left_button_normal);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_top_left_button_pressed", R.drawable.edge_buttons_top_left_button_pressed);
        BUTTON_NAMES_TO_RESOURCES.put("edge_buttons_right_button_pressed", R.drawable.edge_buttons_right_button_pressed);

        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.empty_center_button_normal, "empty_center_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_button, "rounded_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.square_button_pressed, "square_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_top_button_normal, "edge_buttons_top_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_left_button_pressed, "rounded_left_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_top_button_pressed, "rounded_top_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_top_button, "edge_buttons_top_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_bottom_right_button_normal, "edge_buttons_bottom_right_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_bottom_right_button, "edge_buttons_bottom_right_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_top_left_button_normal, "edge_buttons_top_left_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_top_button_normal, "rounded_top_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_bottom_left_button_pressed, "edge_buttons_bottom_left_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_bottom_left_button, "edge_buttons_bottom_left_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_bottom_button_pressed, "rounded_bottom_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_left_button, "rounded_left_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_top_right_button_pressed, "edge_buttons_top_right_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.empty_center_button, "empty_center_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_bottom_button, "rounded_bottom_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_right_button_pressed, "rounded_right_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_bottom_right_button_pressed, "edge_buttons_bottom_right_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_left_button_normal, "edge_buttons_left_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_right_button_normal, "edge_buttons_right_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_right_button, "edge_buttons_right_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_bottom_button_normal, "edge_buttons_bottom_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_top_button_pressed, "edge_buttons_top_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_button_normal, "rounded_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_bottom_button_pressed, "edge_buttons_bottom_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_button_pressed, "rounded_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.square_button, "square_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_top_left_button, "edge_buttons_top_left_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_left_button_normal, "rounded_left_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_right_button, "rounded_right_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_left_button_pressed, "edge_buttons_left_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_top_button, "rounded_top_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_bottom_button, "edge_buttons_bottom_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_left_button, "edge_buttons_left_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_top_right_button_normal, "edge_buttons_top_right_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_top_right_button, "edge_buttons_top_right_button");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_bottom_button_normal, "rounded_bottom_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.rounded_right_button_normal, "rounded_right_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.empty_center_button_pressed, "empty_center_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.square_button_normal, "square_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_bottom_left_button_normal, "edge_buttons_bottom_left_button_normal");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_top_left_button_pressed, "edge_buttons_top_left_button_pressed");
        BUTTON_RESOURCES_TO_NAMES.put(R.drawable.edge_buttons_right_button_pressed, "edge_buttons_right_button_pressed");
    }

    public static String getBackgroundName(Context context, int resource) {
        return BUTTON_RESOURCES_TO_NAMES.get(resource);
    }

    public static String setBackground(View view, String background) {
        Integer bg = BUTTON_NAMES_TO_RESOURCES.get(background);
        if (null != bg) {
            view.setBackgroundResource(bg);
        } else if (new File(background).exists()){
            Bitmap bitmap = BitmapFactory.decodeFile(background);
            view.setBackground(new BitmapDrawable(view.getContext().getResources(), bitmap));
        }
        return background;
    }
}
