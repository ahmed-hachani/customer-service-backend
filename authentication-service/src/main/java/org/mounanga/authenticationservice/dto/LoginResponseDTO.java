package org.mounanga.authenticationservice.dto;

import java.util.List;

public record LoginResponseDTO(String jwt, boolean passwordNeedToBeUpdate, List<String> roles
) {
}
