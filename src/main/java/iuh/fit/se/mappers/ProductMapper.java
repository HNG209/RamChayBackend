package iuh.fit.se.mappers;

import iuh.fit.se.dtos.response.MediaUploadResponse;
import iuh.fit.se.dtos.response.ProductCreationResponse;
import iuh.fit.se.entities.Media;
import iuh.fit.se.entities.Product;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface ProductMapper {

    @Mapping(target = "imageUrl", source = "mediaFiles", qualifiedByName = "getMainImage")
    @Mapping(target = "mediaList", source = "mediaFiles", qualifiedByName = "mapMediaList")
    ProductCreationResponse toProductCreationResponse(Product product);
/*
    Giải thích hàm: Frontend cần imageUrl (String), nhưng Entity lại có Set<Media>.
    MapStruct cần được chỉ dẫn cách lấy ảnh đầu tiên trong Set làm ảnh đại diện.
 */
    @Named("getMainImage")
    default String getMainImage(Set<Media> mediaFiles) {
        if (mediaFiles == null || mediaFiles.isEmpty()) {
            return null;
        }
        // Lấy URL của ảnh đầu tiên tìm thấy
        return mediaFiles.stream()
                .sorted(Comparator.comparing(Media::getId))
                .map(Media::getSecureUrl)
                .findFirst()
                .orElse(null);
    }

    @Named("mapMediaList")
    default List<MediaUploadResponse> mapMediaList(Set<Media> mediaFiles) {
        if (mediaFiles == null) return null;

        return mediaFiles.stream()
                .sorted(Comparator.comparing(Media::getId))
                .map(media -> MediaUploadResponse.builder()
                        .id(media.getId()) // Quan trọng: Lấy ID để xóa
                        .secureUrl(media.getSecureUrl())
                        .publicId(media.getPublicId())
                        .build())
                .collect(Collectors.toList());
    }
}
