package iuh.fit.se.mappers;

import iuh.fit.se.dtos.response.CategoryCreationResponse;
import iuh.fit.se.entities.Category;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface CategoryMapper {
    CategoryCreationResponse toCategoryCreationResponse(Category category);

}
