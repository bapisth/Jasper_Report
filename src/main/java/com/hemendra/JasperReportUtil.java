package com.hemendra;

/**
 * Created by BIP034 on 7/19/2017.
 */
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;

public class JasperReportUtil {

    public static JasperReport getCompiledReport(String reportFilePath) throws JRException, IOException
    {
        URL reportPath = JasperReportUtil.class.getClassLoader().getResource(reportFilePath);
        //URL reportPath = ResourceUtil.findClasspathResource(reportFilePath);
        if (reportPath == null) {
            throw new FileNotFoundException("File(" + reportFilePath + ") is not found.");
        }

        return JasperCompileManager.compileReport( reportPath.openStream());
    }

}
