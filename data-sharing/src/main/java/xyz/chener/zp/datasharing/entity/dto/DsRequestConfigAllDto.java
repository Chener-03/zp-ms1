package xyz.chener.zp.datasharing.entity.dto;

import jakarta.validation.Valid;
import lombok.Data;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.*;

/**
 * @Author: chenzp
 * @Date: 2023/04/13/15:41
 * @Email: chen@chener.xyz
 */

@Data
public class DsRequestConfigAllDto {

    @Valid
    private DsRequestConfigDto dsRequestConfigDto;

    private AuthPe authPe;

    private InPe inPe;

    private InJsPe inJsPe;

    private SqlPe sqlPe;

    private OutPe outPe;

    private OutJsPe outJsPe;

    private OutDataPe outDataPe;

}
