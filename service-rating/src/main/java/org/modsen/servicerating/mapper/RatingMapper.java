package org.modsen.servicerating.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.modsen.servicerating.dto.request.RatingRequest;
import org.modsen.servicerating.dto.response.RatingResponse;
import org.modsen.servicerating.model.Rating;

@Mapper(componentModel = "spring")
public interface RatingMapper {
    Rating toRating(RatingRequest ratingRequest);

    RatingResponse toRatingResponse(Rating rating);

    void updateRating(RatingRequest ratingRequest, @MappingTarget Rating rating);
}