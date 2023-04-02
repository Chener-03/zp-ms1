package xyz.chener.zp.datasharing.requestProcess.exec;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.http.MediaType;
import xyz.chener.zp.common.entity.R;
import xyz.chener.zp.common.utils.chain.AbstractChainExecute;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.OutDataPe;
import xyz.chener.zp.datasharing.requestProcess.entity.pe.PeAllParams;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class OutDataExec extends AbstractChainExecute {
    @Override
    protected Object handle(Object param) throws Exception {
        if (param instanceof PeAllParams pap){
            String type = pap.getOutDataPe().getType();
            if (type.equalsIgnoreCase(OutDataPe.TYPE.JSON)){
                outJson(pap);
            }

            if (type.equalsIgnoreCase(OutDataPe.TYPE.EXCEL)){
                outExcel(pap);
            }

            if (type.equalsIgnoreCase(OutDataPe.TYPE.PDF)){

            }

            return pap;
        }
        return null;
    }

    private void outJson(PeAllParams pap) throws Exception{
        ObjectMapper om = new ObjectMapper();
        om.registerModule(new JavaTimeModule());

        HttpServletResponse response = pap.getResponse();
        response.setStatus(R.HttpCode.HTTP_OK.get());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        R<Object> r = R.Builder.getInstance().setCode(R.HttpCode.HTTP_OK.get())
                .setMessage("请求成功")
                .setObj(pap.getResult())
                .build();
        byte[] bytes = om.writeValueAsBytes(r);
        try (OutputStream os = response.getOutputStream()) {
            os.write(bytes);
            os.flush();
        }
    }

    private void outExcel(PeAllParams pap) throws Exception{
        try (SXSSFWorkbook workbook = new SXSSFWorkbook()) {

            Map<String, List<Map<String, Object>>> result = pap.getResult();

            result.forEach((sheetName, data) -> {
                SXSSFSheet st = workbook.createSheet(sheetName);
                SXSSFRow titleRow = st.createRow(0);
                List<String> titles = null;
                if (data != null && data.size() > 0) {
                    titles = data.get(0).keySet().stream().toList();
                    for (int i = 0; i < titles.size(); i++) {
                        titleRow.createCell(i).setCellValue(titles.get(i));
                    }

                    for (int i = 0; i < data.size(); i++) {
                        SXSSFRow row = st.createRow(i + 1);
                        Map<String, Object> rowData = data.get(i);
                        for (int j = 0; j < titles.size(); j++) {
                            row.createCell(j).setCellValue(rowData.get(titles.get(j)).toString());
                        }
                    }
                }
            });
            ByteArrayOutputStream bios = new ByteArrayOutputStream();
            workbook.write(bios);
            bios.flush();
            byte[] bytes = bios.toByteArray();
            bios.close();
            HttpServletResponse response = pap.getResponse();
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setContentLength(bytes.length);

            String filename =  String.format("%s(%s).xlsx",pap.getDsRequestConfig().getRequestName()
                    ,pap.getDsRequestConfig().getId());
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
            ServletOutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
        }catch (Exception ignored){ }
    }

}
