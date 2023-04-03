package xyz.chener.zp.datasharing.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.PathVariable;

public interface DataSharingService {

    void sharing( String requestId , Boolean istest, HttpServletRequest request, HttpServletResponse response);

}
