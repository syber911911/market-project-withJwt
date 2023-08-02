package com.likelion.market.negotiation.validator;

import com.likelion.market.negotiation.annotations.Status;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StatusValidator implements ConstraintValidator<Status, String> {
    private List<String> statusList;
    @Override
    public void initialize(Status constraintAnnotation) {
        statusList = new ArrayList<>();
        statusList.addAll(Arrays.asList(constraintAnnotation.statusList()));
        // status 에 관한 validation 을 진행할 때 status 에 null 이 들어오는 경우가 있음
        // null 도 정상적인 요청이기에 statusList 에 null 추가
        statusList.add(null);
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return this.statusList.contains(value);
    }
}
