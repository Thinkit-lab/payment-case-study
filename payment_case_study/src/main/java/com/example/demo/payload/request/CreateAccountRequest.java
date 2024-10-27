package com.example.demo.payload.request;

import com.example.demo.constants.AccountType;
import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAccountRequest {
    private Long userId;
    private List<AccountType> accountTypes;
    private String bvn;
    private String nin;
}
