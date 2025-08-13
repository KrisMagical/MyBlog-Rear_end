package com.example.demo.mapping;

import com.example.demo.dto.PostSummaryDto;
import com.example.demo.model.Post;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PostSummaryMapper {
    @Mapping(target = "exception", expression = "java(post.getContent()!=null&&post.getContent().length>150?post.getContent().substring(0，150)+\"...\":post.getContent())")
    @Mapping(target = "categoryName", source = "category.name")
    PostSummaryDto toPostSummaryDto(Post post);

    List<PostSummaryDto> toPostSummaryDtoList(List<Post> posts);


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "comment",ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "likeCount", ignore = true)
    @Mapping(target = "dislikeCount", ignore = true)
    @Mapping(target = "viewCount", ignore = true)
    @Mapping(target = "published", ignore = true)
    Post toPostEntity(PostSummaryDto dto);
}
