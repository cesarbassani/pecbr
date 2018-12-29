package com.cesarbassani.pecbr.helper;

import android.content.Context;

import com.cesarbassani.pecbr.model.Abate;
import com.cesarbassani.pecbr.repository.TemplatePDF;
import com.itextpdf.text.DocumentException;

import java.io.IOException;

public class PDFHelper {

    private static TemplatePDF templatePDF;

    public static void pdfView(Abate abate, Context context) throws IOException, DocumentException {

        templatePDF = new TemplatePDF(context);
        templatePDF.openDocument();
        templatePDF.addTitles("RESUMO DE ABATE - PECBR", "Data: ", abate.getDataAbate());
//        templatePDF.onStartPage();
        templatePDF.addFormulario(abate);
//        templatePDF.onEndPage();
        templatePDF.closeDocument();
        templatePDF.viewPDF();
    }
}
