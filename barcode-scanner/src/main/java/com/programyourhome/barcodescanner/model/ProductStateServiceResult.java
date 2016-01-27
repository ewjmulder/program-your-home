package com.programyourhome.barcodescanner.model;

import com.programyourhome.common.response.ServiceResult;
import com.programyourhome.shop.model.PyhProductState;

/**
 * Tagging interface, so the <T> of ServiceResult is known at runtime.
 * (for Jackson's MrBean module to figure out the right types to construct)
 */
public interface ProductStateServiceResult extends ServiceResult<PyhProductState> {

}
