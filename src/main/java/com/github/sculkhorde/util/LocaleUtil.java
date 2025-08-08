package com.github.sculkhorde.util;

import net.minecraft.ChatFormatting;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.level.ItemLike;
import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;

import org.jetbrains.annotations.Nullable;

public class LocaleUtil {

    public static MutableComponent getLocaleMessage(String langKey) {
        return getLocaleMessage(langKey, (ChatFormatting)null);
    }

    public static MutableComponent getLocaleMessage(String langKey, Component... args) {
        return getLocaleMessage(langKey, null, args);
    }

    public static MutableComponent getLocaleMessage(String langKey, @Nullable ChatFormatting format, Component... args) {
        MutableComponent localeMessage = Component.translatable(langKey, (Object[])args);

        if (format != null)
            localeMessage.withStyle(format);

        return localeMessage;
    }

    public static MutableComponent numToComponent(Number number) {
        return Component.literal(String.valueOf(number));
    }

    @Environment(EnvType.CLIENT)
    public static String getLocaleString(String langKey) {
        return getLocaleString(langKey, (ChatFormatting)null);
    }

    @Environment(EnvType.CLIENT)
    public static String getItemName(ItemLike object) {
        return I18n.get(object.asItem().getDescriptionId());
    }

    @Environment(EnvType.CLIENT)
    public static String getLocaleString(String langKey, String... args) {
        return getLocaleString(langKey, null, args);
    }

    @Environment(EnvType.CLIENT)
    public static String getLocaleString(String langKey, @Nullable ChatFormatting colour, String... args) {
        return (colour != null ? colour : "") + I18n.get(langKey, (Object[])args);
    }
}
