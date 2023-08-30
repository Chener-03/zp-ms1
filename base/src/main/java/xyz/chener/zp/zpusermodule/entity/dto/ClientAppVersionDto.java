package xyz.chener.zp.zpusermodule.entity.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClientAppVersionDto {
    private String appVersion;
    private boolean forceUpdate;
}
