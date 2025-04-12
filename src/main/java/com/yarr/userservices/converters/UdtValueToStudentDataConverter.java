package com.yarr.userservices.converters;

import com.datastax.oss.driver.api.core.data.UdtValue;
import com.yarr.userservices.entity.FormData;
import com.yarr.userservices.entity.StudentData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class UdtValueToStudentDataConverter implements Converter<UdtValue, StudentData> {

    private final UdtValueToFormDataConverter udtValueToFormDataConverter;

    public UdtValueToStudentDataConverter(UdtValueToFormDataConverter udtValueToFormDataConverter) {
        this.udtValueToFormDataConverter = udtValueToFormDataConverter;
    }

    @Override
    public StudentData convert(UdtValue source) {
        StudentData studentData = new StudentData();
        studentData.setStudent_id(source.getString("student_id"));
        studentData.setInstitute_id(source.getString("institute_id"));
        studentData.setStatus(source.getString("status"));
        studentData.setIs_continuing(source.getBoolean("is_continuing"));
        studentData.setSession(source.getString("session"));
        studentData.setCreated_at(source.getInstant("created_at"));
        studentData.setUpdated_at(source.getInstant("updated_at"));

        List<UdtValue> formDataList = source.getList("form_data", UdtValue.class);
        List<FormData> formData = new ArrayList<>();
        if(formDataList != null) {
            for(UdtValue formDataUdt : formDataList) {
                formData.add(udtValueToFormDataConverter.convert(formDataUdt));
            }
        }
        studentData.setForm_data(formData);
        return studentData;
    }
}
