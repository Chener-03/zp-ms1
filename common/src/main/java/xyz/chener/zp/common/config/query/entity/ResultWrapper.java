package xyz.chener.zp.common.config.query.entity;

import xyz.chener.zp.common.config.query.QueryHelper;

import java.util.List;

/**
 * @Author: chenzp
 * @Date: 2023/03/09/14:41
 * @Email: chen@chener.xyz
 */
public record ResultWrapper(ChainParam param,List<QueryHelper.TableField> result) {

}
