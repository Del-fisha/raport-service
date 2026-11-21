package raport.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import raport.service.NominativeRank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PersonDto {
    private String firstName;
    private String lastName;
    private String middleName;

    @Getter(AccessLevel.NONE)
    private String rank;

    private String position;
    private String gender;

    public String getRank() {
        return NominativeRank.format(rank);
    }
}