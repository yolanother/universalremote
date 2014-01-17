package com.doubtech.universalremote.jsonretreivers;

public interface JsonRetreiver {
    String getButtonsJson(String brandId, String modelId);
    String getModelsJson(String brandId);
    String getBrandsJson();
}
