package xyz.chener.zp.common.config.unifiedReturn.warper;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.propertyeditors.URLEditor;
import org.springframework.http.MediaType;
import xyz.chener.zp.common.entity.R;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * @Author: chenzp
 * @Date: 2023/04/23/09:48
 * @Email: chen@chener.xyz
 */
public final class FileWarper implements OutputStreamWarper{

    private final byte[] bytes;

    private final String filename;

    public FileWarper(String string){
        this.bytes = string.getBytes(StandardCharsets.UTF_8);
        this.filename = UUID.randomUUID().toString();
    }

    public FileWarper(String string, String fileName)  {
        this.bytes = string.getBytes(StandardCharsets.UTF_8);
        this.filename = fileName;
    }

    public FileWarper(InputStream inputStream) throws IOException {
        this.bytes = inputStream.readAllBytes();
        this.filename = UUID.randomUUID().toString();
    }

    public FileWarper(byte[] bytes){
        this.bytes = bytes;
        this.filename = UUID.randomUUID().toString();
    }


    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(bytes);
    }

    @Override
    public void setResponse(HttpServletResponse response) {
        response.setStatus(R.HttpCode.HTTP_OK.get());
        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
        response.setContentLength(bytes.length);
    }
}
