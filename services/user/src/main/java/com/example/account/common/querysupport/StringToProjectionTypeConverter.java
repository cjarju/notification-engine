package com.example.account.common.querysupport;

import com.example.account.common.enums.ProjectionType;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
public class StringToProjectionTypeConverter
        implements Converter<String, ProjectionType> {

    @Override
    public ProjectionType convert(String source) {
        return ProjectionType.valueOf(source.toUpperCase(Locale.ROOT));
    }
}
