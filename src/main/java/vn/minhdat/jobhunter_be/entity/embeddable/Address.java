package vn.minhdat.jobhunter_be.entity.embeddable;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    private String number;
    private String street;
    private String ward;
    private String district;
    private String city;
    private String country;
}
