package vn.minhdat.jobhunter_be.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import vn.minhdat.jobhunter_be.entity.Blog;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LikeBlogRequest {
    @NotNull(message = "liked blog must not be null")
    private Blog blog;
    private boolean liked;
}
