package com.ae2morepainting;

import net.minecraftforge.common.property.IUnlistedProperty;

import appeng.api.util.AEColor;

public final class ColorProperty implements IUnlistedProperty<AEColor> {

    public static final ColorProperty INSTANCE = new ColorProperty();

    private ColorProperty() {}

    @Override
    public String getName() {
        return "color";
    }

    @Override
    public boolean isValid(final AEColor value) {
        return value != null;
    }

    @Override
    public Class<AEColor> getType() {
        return AEColor.class;
    }

    @Override
    public String valueToString(final AEColor value) {
        return value.name();
    }
}
