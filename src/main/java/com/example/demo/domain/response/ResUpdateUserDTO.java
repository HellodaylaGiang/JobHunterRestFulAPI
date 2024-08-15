package com.example.demo.domain.response;

import java.time.Instant;

import com.example.demo.util.constant.GenderEnum;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;

}
