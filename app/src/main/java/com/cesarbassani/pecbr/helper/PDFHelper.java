package com.cesarbassani.pecbr.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import com.cesarbassani.pecbr.model.Abate;
import com.cesarbassani.pecbr.repository.TemplatePDF;
import com.itextpdf.text.DocumentException;

import java.io.IOException;

public class PDFHelper {

    private static TemplatePDF templatePDF;

    public static void pdfView(Abate abate, Context context, Bitmap imagem) throws IOException, DocumentException {

        templatePDF = new TemplatePDF(context);
        templatePDF.openDocument(0f);
        templatePDF.addTitles("RESUMO DE ABATE - PECBR", "Data: ", abate.getDataAbate());
//        templatePDF.onStartPage();
//        templatePDF.carregaImagemDoLote(imagem);
        templatePDF.addFormulario(abate);
//        templatePDF.onEndPage();
        templatePDF.closeDocument();
        templatePDF.viewPDF();
    }
}
