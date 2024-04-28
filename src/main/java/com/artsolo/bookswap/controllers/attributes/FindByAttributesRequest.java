package com.artsolo.bookswap.controllers.attributes;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindByAttributesRequest {
    private List<Long> genreIds;
    private Long qualityId;
    private Long statusId;
    private Long languageId;
}
