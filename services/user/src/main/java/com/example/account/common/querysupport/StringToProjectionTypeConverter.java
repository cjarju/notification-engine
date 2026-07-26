package com.example.account.common.querysupport;

import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.example.account.common.enums.ProjectionType;

@Component
public class StringToProjectionTypeConverter
        implements Converter<String, ProjectionType> {

    @Override
    public ProjectionType convert(String source) {
        return ProjectionType.valueOf(source.toUpperCase(Locale.ROOT));
    }
}
