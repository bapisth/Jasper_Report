package com.hemendra;

/**
 * Created by BIP034 on 7/19/2017.
 */
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporter;
import net.sf.jasperreports.engine.JRQuery;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

public class JasperCustomReport {
    private Integer reportId;
    private JasperReport jasperReport = null;
    private static Logger log = Logger.getLogger(JasperCustomReport.class.getName());

    // private InputStream inputStream = null;

    public JasperCustomReport() {
        // Default Constructor.
        // Added default constructor because reportId is not used and its usage not known.
    }

    public JasperCustomReport(Integer reportId) {
        this.reportId = reportId;
    }


    public void compileReport(InputStream reportBlobStream) throws JRException {
        jasperReport = JasperCompileManager.compileReport(reportBlobStream);
    }

    public void compileReportByName(String reportName) throws JRException {
        jasperReport = JasperCompileManager.compileReport(reportName);
    }


    public void loadCompiledReport(File fileObj) {
        try {
            jasperReport = ((JasperReport) JRLoader.loadObject(fileObj));
        } catch (JRException e) {
        }
    }

    public void loadCompiledReport(InputStream reportStream) {
        try {
            jasperReport = ((JasperReport) JRLoader.loadObject(reportStream));
        } catch (JRException e) {
        }
    }




    public String getQueryText() throws JRException {
        JRQuery query = jasperReport.getMainDataset().getQuery();
        return query.getText();
    }

    public byte[] runReportToPdf(Map<String, Object> params, Connection sqlConn) throws JRException {
        return JasperRunManager.runReportToPdf(jasperReport, params, sqlConn);
    }

    public byte[] compileAndRunReportToPdf(Map<String, Object> params, Connection sqlConn, String reportPath) throws JRException, MalformedURLException,
            IOException {
        JasperReport report = JasperReportUtil.getCompiledReport(reportPath);
        return JasperRunManager.runReportToPdf(report, params, sqlConn);
    }

    // this method is to get number of copies of a single report.
    public byte[] runReportToPdf(Map<String, Object> params, Connection sqlConn, String reportPath,int noOfCopies) throws JRException, MalformedURLException,
            IOException {
        jasperReport = JasperReportUtil.getCompiledReport(reportPath);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        List<JasperPrint> jasperPrintlist = new ArrayList<JasperPrint>();
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params,sqlConn);
        if(noOfCopies>1){
            for (int i=1; i<=noOfCopies; i++) {
                jasperPrintlist.add(jasperPrint);
            }
        }else
            jasperPrintlist.add(jasperPrint);
        JRExporter exporter = new JRPdfExporter();
        exporter.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrintlist);
        exporter.setParameter(JRPdfExporterParameter.OUTPUT_STREAM, byteArrayOutputStream);
        exporter.exportReport();
        return byteArrayOutputStream.toByteArray();
    }

    /*public byte[] runReportToPdf(Map<String, Object> params, Connection sqlConn, String reportName) throws JRException, MalformedURLException,
            IOException {
        //InputStream inputStream = new java.io.FileInputStream(new File(reportName));
    //	InputStream inputStream = JRLoader.getURLInputStream(reportName);
    //	InputStream inputStream = JRLoader.getFileInputStream(reportName);
        return JasperRunManager.runReportToPdf(inputStream, params, sqlConn);
    }
*/
    public void viewReport(Map<String, Object> params, Connection sqlConn) throws JRException {
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, params, sqlConn);
        JasperViewer.viewReport(jasperPrint, false);
    }

    /**
     * Common method for export a PDF report
     */
    public byte[] exportReportToPdf(Map<String, Object> params,String reportPath,String dataSourceKey) throws JRException {
        try {
            JasperPrint jasperPrint=null;

            jasperReport = JasperReportUtil.getCompiledReport(reportPath);





            JRBeanCollectionDataSource beanCollectionDataSource = new JRBeanCollectionDataSource((Collection)params.get(dataSourceKey));
            params.put(dataSourceKey, beanCollectionDataSource);

            if (jasperReport != null) {
                if(params.get(dataSourceKey)!=null){
                    jasperPrint = JasperFillManager.fillReport(
                            jasperReport, params, new JREmptyDataSource()
                            //new JRBeanCollectionDataSource((Collection) params.get(dataSourceKey))
                            //new JRMapCollectionDataSource((Collection) params.get(dataSourceKey))
                    );
                }
				 /*else{  // From web service we cannot get the connection parameter values
					 Connection connnection = Transaction.current().getCurrentConnection();
					 if(connnection != null) {
						 jasperPrint = JasperFillManager.fillReport(jasperReport, params,connnection);
					 } else {
						 jasperPrint = JasperFillManager.fillReport(
								jasperReport, params,new JREmptyDataSource());
					 }
				 }*/
            }
            return JasperExportManager.exportReportToPdf(jasperPrint);
        } catch (Exception e) {
            log.info(""+e.getMessage());}
        return null;
    }

    /**
     * Common method for print a XLS report
     */
    public byte[]  exportReportToXLS(Map<String, Object> params,String reportPath,String dataSourceKey) throws JRException, IOException {

        jasperReport = JasperReportUtil.getCompiledReport(reportPath);
        JasperPrint jasperPrint = null;
        if (params.get(dataSourceKey) != null) {
            jasperPrint = JasperFillManager.fillReport(jasperReport,params,new JRBeanCollectionDataSource((Collection) params.get(dataSourceKey)));
        }
		/*else {
			Connection connnection = Transaction.current().getCurrentConnection();
			if (connnection != null) {
				jasperPrint = JasperFillManager.fillReport(jasperReport,params, connnection);
			} else {
				jasperPrint = JasperFillManager.fillReport(jasperReport, params,new JREmptyDataSource());
			}
		}*/
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        JRXlsExporter exporterXLS = new JRXlsExporter();
        exporterXLS.setParameter(JRXlsExporterParameter.JASPER_PRINT,jasperPrint);
        exporterXLS.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
        exporterXLS.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE,Boolean.FALSE);
        exporterXLS.setParameter(JRXlsExporterParameter.OUTPUT_STREAM,byteArrayOutputStream);
        exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS,Boolean.TRUE);
        exporterXLS.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS,Boolean.TRUE);
        exporterXLS.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET,Boolean.FALSE);
        exporterXLS.exportReport();
        return byteArrayOutputStream.toByteArray();
    }
}
