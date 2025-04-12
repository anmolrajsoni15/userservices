package com.yarr.userservices.converters;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import com.yarr.userservices.entity.FormData;
import com.yarr.userservices.entity.StudentData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class StudentDataToUdtValueConverter implements Converter<StudentData, UdtValue> {

    private final UserDefinedType userType;

    private final FormDataToUdtValueConverter formDataToUdtValueConverter;

    public StudentDataToUdtValueConverter(UserDefinedType userType,
            FormDataToUdtValueConverter formDataToUdtValueConverter) {
        this.userType = userType;
        this.formDataToUdtValueConverter = formDataToUdtValueConverter;
    }

    @Override
    public UdtValue convert(StudentData source) {
        System.out.println("StudentDataToUdtValueConverter.convert() - Converting StudentData: " + source);

        // Create UDT without form_data first
        UdtValue udtValue = userType.newValue()
                .setString("student_id", source.getStudent_id())
                .setString("institute_id", source.getInstitute_id())
                .setString("status", source.getStatus())
                .setBoolean("is_continuing", source.getIs_continuing())
                .setString("session", source.getSession())
                .setInstant("created_at", source.getCreated_at())
                .setInstant("updated_at", source.getUpdated_at());

        // Handle form_data separately - set to null if not available
        List<UdtValue> formDataList = new ArrayList<>();
        if (source.getForm_data() != null) {
            for(FormData formData : source.getForm_data()) {
                formDataList.add(formDataToUdtValueConverter.convert(formData));
            }
        }

        udtValue.setList("form_data", formDataList, UdtValue.class);

        return udtValue;
    }
}
