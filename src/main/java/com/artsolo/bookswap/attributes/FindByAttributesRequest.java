package com.artsolo.bookswap.attributes;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FindByAttributesRequest {
    private List<Long> genreIds;
    private Long qualityId;
    private Long statusId;
    private Long languageId;
}
