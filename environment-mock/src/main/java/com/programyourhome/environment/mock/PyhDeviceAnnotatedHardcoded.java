package com.programyourhome.environment.mock;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.programyourhome.ir.model.PyhDevice;

@JsonSerialize(as = PyhDevice.class)
public interface PyhDeviceAnnotatedHardcoded extends PyhDevice {
}
