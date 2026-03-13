package org.devberat.model;

import lombok.Getter;

@Getter
public enum UserType {
    ADMIN,
    PASSENGER,
    CAPTAIN,
    TOWER,
    FIRST_OFFICER,
    INFLIGHT_CHEF,
    FLIGHT_ATTENDANT,
    PURSER;
}