package com.cesarbassani.pecbr.repository;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.config.ConfiguracaoFirebase;
import com.cesarbassani.pecbr.config.GlideApp;
import com.cesarbassani.pecbr.constants.GuestConstants;
import com.cesarbassani.pecbr.model.Abate;
import com.cesarbassani.pecbr.model.Bonificacao;
import com.cesarbassani.pecbr.model.Penalizacao;
import com.cesarbassani.pecbr.utils.Tools;
import com.cesarbassani.pecbr.views.ViewPDFActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.itextpdf.text.BadElementException;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ExceptionConverter;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPCellEvent;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPageEventHelper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by cesarbassani on 18/02/18.
 */

public class TemplatePDF extends PdfPageEventHelper {

    private Context context;
    private File pdfFile;
    private Document document;
    private PdfWriter pdfWriter;
    private Paragraph paragraph;
    private Font fTitle = new Font(Font.FontFamily.HELVETICA, 24, Font.BOLD);
    private Font fSubtitle = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(114, 102, 95));
    private Font fText = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private Font fHighText = new Font(Font.FontFamily.HELVETICA, 15, Font.BOLD, BaseColor.RED);
    private Image image;
    private Image imageLote;
    private Font ffont = new Font(Font.FontFamily.UNDEFINED, 5, Font.ITALIC);
    private String header;
    private String title;
    private String subTitle;
    private String date;
    private Double totalBonificacao = 0.0;
    private Double mediaTotalBonificacao = 0.0;
    private Double totalPenalizacao = 0.0;
    private Double mediaTotalPenalizacao = 0.0;
    private String tecnicoResponsavel;
    private Float tamanhoTela;

    private StorageReference storageReference;


    /***
     * Variables for further use....
     */
    BaseColor mColorAccent = new BaseColor(189, 167, 128, 255);
    float mHeadingFontSize = 20.0f;
    float mValueFontSize = 20.0f;

    /**
     * How to USE FONT....
     */
    private BaseFont urTexto = BaseFont.createFont("assets/fonts/brandon_light.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
    private BaseFont urSubtitulo = BaseFont.createFont("assets/fonts/brandon_bold.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

//    typeFace_date = Typeface.createFromAsset(this.getAssets(), "fonts/brandon_light.otf");
//    typeFace_title = Typeface.createFromAsset(this.getAssets(), "fonts/brandon_bold.otf");

    public TemplatePDF(Context context) throws IOException, DocumentException {

        this.context = context;
    }

    public void openDocument(Float pageSize) {
//        pageSize += 800f;
        tamanhoTela = pageSize;
        createFile();
        try {
            Rectangle pageSizeDocument = new Rectangle(600f, pageSize);
            document = new Document(pageSizeDocument, 36, 36, 130, 15);
            pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

            pdfWriter.setPageEvent(this);

            document.open();

        } catch (Exception e) {
            Log.e("openDocument", e.toString());
        }
    }

    private void createFile() {

        File folder = new File(Environment.getExternalStorageDirectory().toString(), "PDF");

        if (!folder.exists())
            folder.mkdirs();

        pdfFile = new File(folder, "AcompanhamentoAbate.pdf");
    }

    public void closeDocument() {
        document.close();
    }

//    public void addMetaData(String title, String subject, String author) {
//        document.addTitle(title);
//        document.addSubject(subject);
//        document.addAuthor(author);
//    }

    public void addTitles(String title, String subTitle, String date) {
        this.title = title;
        this.subTitle = subTitle;
        this.date = date;
    }

    private void addChildP(Paragraph childParagraph) {
        childParagraph.setAlignment(Element.ALIGN_LEFT);
        paragraph.add(childParagraph);
    }

    public void addParagraph(String text) {
        try {
            paragraph = new Paragraph(text, fText);
            paragraph.setSpacingAfter(5);
            paragraph.setSpacingBefore(5);
            document.add(paragraph);
        } catch (DocumentException e) {
            Log.e("addParagraph", e.toString());
        }
    }

    public void addFormulario(Abate abate) throws DocumentException {

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();

        if (abate.getTecnico().getNome() != null)
            tecnicoResponsavel = abate.getTecnico().getNome();

        Paragraph p = new Paragraph();
        p.setSpacingAfter(10);

        Paragraph pTable = new Paragraph();
        p.setSpacingAfter(20);

        LineSeparator lineSeparator = new LineSeparator();
        lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
        lineSeparator.setPercentage(60);
        lineSeparator.setAlignment(0);
        // Adding Line Breakable Space....

        // Title Order Details...
        // Adding Title....
//        Font mOrderDetailsTitleFont = new Font(urTexto, 28.0f, Font.NORMAL, BaseColor.BLACK);
//        Chunk mOrderDetailsTitleChunk = new Chunk("Detalhes do Abate", mOrderDetailsTitleFont);
//        Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
//        mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_LEFT);
//        document.add(mOrderDetailsTitleParagraph);
//
//        document.add(p);

        try {
            addHeader(pdfWriter);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Font mOrderIdValueFont = new Font(urSubtitulo, 24.0f, Font.NORMAL, BaseColor.BLACK);
        Chunk mOrderIdValueChunk = new Chunk(abate.getLote().getNomeCliente(), mOrderIdValueFont);
        Paragraph mOrderIdValueParagraph = new Paragraph(mOrderIdValueChunk);
        document.add(mOrderIdValueParagraph);

        // Adding Line Breakable Space....
//        document.add(new Paragraph(""));
        // Adding Horizontal Line...
//        document.add(new Chunk(lineSeparator));
        // Adding Line Breakable Space....
//        document.add(new Paragraph(""));

        Font mOrderDateValueFont = new Font(urTexto, mValueFontSize, Font.ITALIC, BaseColor.BLACK);
        Chunk mOrderDateValueChunk = new Chunk(abate.getLote().getPropriedade(), mOrderDateValueFont);
        Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
        document.add(mOrderDateValueParagraph);

//        document.add(new Paragraph(""));
//        document.add(new Chunk(lineSeparator));
        document.add(p);
//        document.add(new Paragraph(""));

        // Fields of Order Details...
        Font mOrderAcNameFont = new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK);
        Chunk quantidadeAnimais = new Chunk("Quantidade: " + abate.getLote().getQtdeAnimaisLote() + " " + validaQuantidade(Integer.parseInt(abate.getLote().getQtdeAnimaisLote())), mOrderAcNameFont);
        Chunk unidadeFrigorifico = new Chunk("Frigorífico: " + abate.getFrigorifico(), mOrderAcNameFont);

        Paragraph paragrafoUnidadeFrigorifico = new Paragraph(unidadeFrigorifico);
        document.add(paragrafoUnidadeFrigorifico);

        Paragraph paragrafoQuantidadeAnimais = new Paragraph(quantidadeAnimais);
        document.add(paragrafoQuantidadeAnimais);

        if (!abate.getCategoria().getCategoria().equals("")) {
            Font mOrderAcNameValueFont = new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderAcNameValueChunk = new Chunk("Categoria: " + abate.getCategoria().getCategoria() + " - " + abate.getCategoria().getRacial(), mOrderAcNameValueFont);
            Paragraph mOrderAcNameValueParagraph = new Paragraph(mOrderAcNameValueChunk);
            document.add(mOrderAcNameValueParagraph);

            document.add(p);
        } else {
            document.add(p);
        }

        Paragraph dadosDoRendimento = new Paragraph(new Chunk("Dados do Rendimento", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
        document.add(dadosDoRendimento);

        if (!abate.getRendimento().getPesoFazendaKilo().equals("0.0")) {
            Paragraph pesoFazenda = new Paragraph(new Chunk("Peso fazenda: " + abate.getRendimento().getPesoFazendaKilo() + "Kg (" + abate.getRendimento().getPesoFazendaArroba() + "@)", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            document.add(pesoFazenda);
        }

        Paragraph pesoCarcaca = new Paragraph(new Chunk("Peso carcaça: " + abate.getRendimento().getPesoCarcacaKilo() + "Kg (" + abate.getRendimento().getPesoCarcacaArroba() + "@)", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
        document.add(pesoCarcaca);

        if (!abate.getRendimento().getRendimentoCarcaça().equals("0.0")) {
            Paragraph rendimento = new Paragraph(new Chunk("Rendimento: " + abate.getRendimento().getRendimentoCarcaça() + "%", new Font(urSubtitulo, mValueFontSize, Font.BOLD, BaseColor.BLACK)));
            document.add(rendimento);
        }

        if (!abate.getRendimento().getRendimentoEstimado().equals("-")) {
            Paragraph rendimentoEstimado = new Paragraph(new Chunk("Rendimento Estimado: " + abate.getRendimento().getRendimentoEstimado(), new Font(urTexto, 16.0f, Font.NORMAL, BaseColor.BLACK)));
            document.add(rendimentoEstimado);
        }

        document.add(p);

        int somaBezerros = (Integer.parseInt(abate.getCategoria().getQtdeBezerrosGrandes()) + Integer.parseInt(abate.getCategoria().getQtdeBezerrosMedios()) + Integer.parseInt(abate.getCategoria().getQtdeBezerrosPequenos()));
        if (somaBezerros > 0) {
            Paragraph bezerros = new Paragraph(new Chunk("Bezerros", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            document.add(bezerros);

            if (Integer.parseInt(abate.getCategoria().getQtdeBezerrosPequenos()) > 0) {
                Paragraph bezerrosPequenos = new Paragraph(new Chunk("Pequeno - " + abate.getCategoria().getQtdeBezerrosPequenos(), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                document.add(bezerrosPequenos);
            }

            if (Integer.parseInt(abate.getCategoria().getQtdeBezerrosMedios()) > 0) {
                Paragraph bezerrosMedios = new Paragraph(new Chunk("Médio - " + abate.getCategoria().getQtdeBezerrosMedios(), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                document.add(bezerrosMedios);
            }

            if (Integer.parseInt(abate.getCategoria().getQtdeBezerrosGrandes()) > 0) {
                Paragraph bezerrosGrandes = new Paragraph(new Chunk("Grande - " + abate.getCategoria().getQtdeBezerrosGrandes(), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                document.add(bezerrosGrandes);
            }

            document.add(p);

        } else if (abate.getCategoria().getCategoria().equals("Vacas") || abate.getCategoria().getCategoria().equals("Novilhas") || abate.getCategoria().getCategoria().equals("Vacas e Novilhas")) {
            Paragraph bezerros = new Paragraph(new Chunk("Bezerros", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            document.add(bezerros);
            Paragraph semBezerros = new Paragraph(new Chunk("Nenhum bezerro", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            document.add(semBezerros);

            document.add(p);
        }

        int somaAcabamento = (Integer.parseInt(abate.getAcabamento().getQtdeAusente()) + Integer.parseInt(abate.getAcabamento().getQtdeEscasso()) + Integer.parseInt(abate.getAcabamento().getQtdeEscassoMenos()) + Integer.parseInt(abate.getAcabamento().getQtdeMediano()) + Integer.parseInt(abate.getAcabamento().getQtdeUniforme()) + Integer.parseInt(abate.getAcabamento().getQtdeExcessivo()));
        if (somaAcabamento > 0) {

            document.add(pTable);
//            Paragraph acabamento = new Paragraph(new Chunk("Acabamento", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//            document.add(acabamento);

            PdfPTable acabamentoTable = new PdfPTable(3);
            acabamentoTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            acabamentoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
//            acabamentoTable.setTotalWidth((PageSize.A4.getWidth() - document.leftMargin()
//                    - document.rightMargin()) * acabamentoTable.getWidthPercentage() / 100);
            acabamentoTable.setWidthPercentage(100);
            acabamentoTable.setKeepTogether(true);

            PdfPCell tituloAcabamento = new PdfPCell(new Paragraph("Acabamento", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            tituloAcabamento.setBorder(PdfPCell.NO_BORDER);
            tituloAcabamento.setBorderWidthBottom(1.5f);
            tituloAcabamento.setBorderColorBottom(BaseColor.GRAY);
            tituloAcabamento.setUseBorderPadding(true);
            tituloAcabamento.setPaddingBottom(5f);
//                tituloMaturidade.addElement(new Phrase("Maturidade", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            acabamentoTable.addCell(tituloAcabamento);

            PdfPCell tituloAcabamentoQtd = new PdfPCell(new Paragraph("Quantidade", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            tituloAcabamentoQtd.setBorder(PdfPCell.NO_BORDER);
            tituloAcabamentoQtd.setBorderWidthBottom(1.5f);
            tituloAcabamentoQtd.setBorderColorBottom(BaseColor.GRAY);
            tituloAcabamentoQtd.setHorizontalAlignment(Element.ALIGN_CENTER);
            tituloAcabamentoQtd.setUseBorderPadding(true);
            tituloAcabamentoQtd.setPaddingBottom(5f);
//                tituloMaturidadeQtd.addElement(new Phrase("Quantidade", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            acabamentoTable.addCell(tituloAcabamentoQtd);

            PdfPCell tituloAcabamentoPercentual = new PdfPCell(new Paragraph("%", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            tituloAcabamentoPercentual.setBorder(PdfPCell.NO_BORDER);
            tituloAcabamentoPercentual.setBorderWidthBottom(1.5f);
            tituloAcabamentoPercentual.setBorderColorBottom(BaseColor.GRAY);
            tituloAcabamentoPercentual.setHorizontalAlignment(Element.ALIGN_CENTER);
            tituloAcabamentoPercentual.setUseBorderPadding(true);
            tituloAcabamentoPercentual.setPaddingBottom(5f);
//                tituloMaturidadePercentual.addElement(new Phrase("%", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            acabamentoTable.addCell(tituloAcabamentoPercentual);

            if (Integer.parseInt(abate.getAcabamento().getQtdeAusente()) > 0) {

                tabelaAcabamento(acabamentoTable, abate.getAcabamento().getQtdeAusente(), "Ausente", abate.getAcabamento().getPercentualAusente());

//                Paragraph acabamentoAusente = new Paragraph(new Chunk("Ausente - " + abate.getAcabamento().getQtdeAusente() + " " + validaQuantidade(Integer.parseInt(abate.getAcabamento().getQtdeAusente())) + " (" + abate.getAcabamento().getPercentualAusente() + ")", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//                document.add(acabamentoAusente);
            }

            if (Integer.parseInt(abate.getAcabamento().getQtdeEscassoMenos()) > 0) {
                tabelaAcabamento(acabamentoTable, abate.getAcabamento().getQtdeEscassoMenos(), "Escasso Menos", abate.getAcabamento().getPercentualEscassoMenos());

//                Paragraph acabamentoEscassoMenos = new Paragraph(new Chunk("Escasso Menos - " + abate.getAcabamento().getQtdeEscassoMenos() + " " + validaQuantidade(Integer.parseInt(abate.getAcabamento().getQtdeEscassoMenos())) + " (" + abate.getAcabamento().getPercentualEscassoMenos() + ")", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//                document.add(acabamentoEscassoMenos);
            }

            if (Integer.parseInt(abate.getAcabamento().getQtdeEscasso()) > 0) {
                tabelaAcabamento(acabamentoTable, abate.getAcabamento().getQtdeEscasso(), "Escasso", abate.getAcabamento().getPercentualEscasso());

//                Paragraph acabamentoEscasso = new Paragraph(new Chunk("Escasso - " + abate.getAcabamento().getQtdeEscasso() + " " + validaQuantidade(Integer.parseInt(abate.getAcabamento().getQtdeEscasso())) + " (" + abate.getAcabamento().getPercentualEscasso() + ")", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//                document.add(acabamentoEscasso);
            }

            if (Integer.parseInt(abate.getAcabamento().getQtdeMediano()) > 0) {
                tabelaAcabamento(acabamentoTable, abate.getAcabamento().getQtdeMediano(), "Mediano", abate.getAcabamento().getPercentualMediano());

//                Paragraph acabamentoMediano = new Paragraph(new Chunk("Mediano - " + abate.getAcabamento().getQtdeMediano() + " " + validaQuantidade(Integer.parseInt(abate.getAcabamento().getQtdeMediano())) + " (" + abate.getAcabamento().getPercentualMediano() + ")", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//                document.add(acabamentoMediano);
            }

            if (Integer.parseInt(abate.getAcabamento().getQtdeUniforme()) > 0) {
                tabelaAcabamento(acabamentoTable, abate.getAcabamento().getQtdeUniforme(), "Uniforme", abate.getAcabamento().getPercentualUniforme());

//                Paragraph acabamentoUniforme = new Paragraph(new Chunk("Uniforme - " + abate.getAcabamento().getQtdeUniforme() + " " + validaQuantidade(Integer.parseInt(abate.getAcabamento().getQtdeUniforme())) + " (" + abate.getAcabamento().getPercentualUniforme() + ")", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//                document.add(acabamentoUniforme);
            }

            if (Integer.parseInt(abate.getAcabamento().getQtdeExcessivo()) > 0) {
                tabelaAcabamento(acabamentoTable, abate.getAcabamento().getQtdeExcessivo(), "Excessivo", abate.getAcabamento().getPercentualExcessivo());

//                Paragraph acabamentoExcessivo = new Paragraph(new Chunk("Excessivo - " + abate.getAcabamento().getQtdeExcessivo() + " " + validaQuantidade(Integer.parseInt(abate.getAcabamento().getQtdeExcessivo())) + " (" + abate.getAcabamento().getPercentualExcessivo() + ")", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//                document.add(acabamentoExcessivo);
            }

            PdfPCell finalTabelaAcabamento = new PdfPCell();
            finalTabelaAcabamento.setBorder(PdfPCell.NO_BORDER);
            finalTabelaAcabamento.setColspan(3);
            finalTabelaAcabamento.setBorderWidthBottom(1.5f);
            finalTabelaAcabamento.setBorderColorBottom(BaseColor.GRAY);
            finalTabelaAcabamento.setUseBorderPadding(true);
            finalTabelaAcabamento.setPaddingBottom(5f);
            acabamentoTable.addCell(finalTabelaAcabamento);

            PdfPCell espacoFinalTabelaAcabamento = new PdfPCell(new Paragraph(" ", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            espacoFinalTabelaAcabamento.setBorder(PdfPCell.NO_BORDER);
            espacoFinalTabelaAcabamento.setColspan(3);
            espacoFinalTabelaAcabamento.setUseBorderPadding(true);
            espacoFinalTabelaAcabamento.setPaddingBottom(5f);
            acabamentoTable.addCell(espacoFinalTabelaAcabamento);

            acabamentoTable.getTotalHeight();

            document.add(acabamentoTable);

            document.add(p);
        }

        int somaMaturidade = (Integer.parseInt(abate.getMaturidade().getQtdeZeroDentes()) + Integer.parseInt(abate.getMaturidade().getQtdeDoisDentes()) + Integer.parseInt(abate.getMaturidade().getQtdeQuatroDentes()) + Integer.parseInt(abate.getMaturidade().getQtdeSeisDentes()) + Integer.parseInt(abate.getMaturidade().getQtdeOitoDentes()));
        if (somaMaturidade > 0) {
//            Paragraph maturidade = new Paragraph(new Chunk("Maturidade", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//            document.add(maturidade);

            document.add(pTable);

            PdfPTable maturidadeTable = new PdfPTable(3);
            maturidadeTable.setHorizontalAlignment(Element.ALIGN_LEFT);
            maturidadeTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);
            maturidadeTable.setWidthPercentage(100);
            maturidadeTable.setKeepTogether(true);

            PdfPCell tituloMaturidade = new PdfPCell(new Paragraph("Maturidade", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            tituloMaturidade.setBorder(PdfPCell.NO_BORDER);
            tituloMaturidade.setBorderWidthBottom(1.5f);
            tituloMaturidade.setBorderColorBottom(BaseColor.GRAY);
            tituloMaturidade.setUseBorderPadding(true);
            tituloMaturidade.setPaddingBottom(5f);
//                tituloMaturidade.addElement(new Phrase("Maturidade", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            maturidadeTable.addCell(tituloMaturidade);

            PdfPCell tituloMaturidadeQtd = new PdfPCell(new Paragraph("Quantidade", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            tituloMaturidadeQtd.setBorder(PdfPCell.NO_BORDER);
            tituloMaturidadeQtd.setBorderWidthBottom(1.5f);
            tituloMaturidadeQtd.setBorderColorBottom(BaseColor.GRAY);
            tituloMaturidadeQtd.setHorizontalAlignment(Element.ALIGN_CENTER);
            tituloMaturidadeQtd.setUseBorderPadding(true);
            tituloMaturidadeQtd.setPaddingBottom(5f);
//                tituloMaturidadeQtd.addElement(new Phrase("Quantidade", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            maturidadeTable.addCell(tituloMaturidadeQtd);

            PdfPCell tituloMaturidadePercentual = new PdfPCell(new Paragraph("%", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            tituloMaturidadePercentual.setBorder(PdfPCell.NO_BORDER);
            tituloMaturidadePercentual.setBorderWidthBottom(1.5f);
            tituloMaturidadePercentual.setBorderColorBottom(BaseColor.GRAY);
            tituloMaturidadePercentual.setHorizontalAlignment(Element.ALIGN_CENTER);
            tituloMaturidadePercentual.setUseBorderPadding(true);
            tituloMaturidadePercentual.setPaddingBottom(5f);
//                tituloMaturidadePercentual.addElement(new Phrase("%", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            maturidadeTable.addCell(tituloMaturidadePercentual);

            if (Integer.parseInt(abate.getMaturidade().getQtdeZeroDentes()) > 0) {

                tabelaMaturidade(maturidadeTable, abate.getMaturidade().getQtdeZeroDentes(), "0", abate.getMaturidade().getPercentualZeroDentes());

//                Paragraph maturidadeZeroDentes = new Paragraph(new Chunk("0 dentes - " + abate.getMaturidade().getQtdeZeroDentes() + " " + validaQuantidade(Integer.parseInt(abate.getMaturidade().getQtdeZeroDentes())) + " (" + abate.getMaturidade().getPercentualZeroDentes() + ")", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//                document.add(maturidadeZeroDentes);
            }

            if (Integer.parseInt(abate.getMaturidade().getQtdeDoisDentes()) > 0) {
                tabelaMaturidade(maturidadeTable, abate.getMaturidade().getQtdeDoisDentes(), "2", abate.getMaturidade().getPercentualDoisDentes());

//                Paragraph maturidadeDoisDentes = new Paragraph(new Chunk("2 dentes - " + abate.getMaturidade().getQtdeDoisDentes() + " " + validaQuantidade(Integer.parseInt(abate.getMaturidade().getQtdeDoisDentes())) + " (" + abate.getMaturidade().getPercentualDoisDentes() + ")", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//                document.add(maturidadeDoisDentes);
            }

            if (Integer.parseInt(abate.getMaturidade().getQtdeQuatroDentes()) > 0) {
                tabelaMaturidade(maturidadeTable, abate.getMaturidade().getQtdeQuatroDentes(), "4", abate.getMaturidade().getPercentualQuatroDentes());

//                Paragraph maturidadeQuatroDentes = new Paragraph(new Chunk("4 dentes - " + abate.getMaturidade().getQtdeQuatroDentes() + " " + validaQuantidade(Integer.parseInt(abate.getMaturidade().getQtdeQuatroDentes())) + " (" + abate.getMaturidade().getPercentualQuatroDentes() + ")", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//                document.add(maturidadeQuatroDentes);
            }

            if (Integer.parseInt(abate.getMaturidade().getQtdeSeisDentes()) > 0) {
                tabelaMaturidade(maturidadeTable, abate.getMaturidade().getQtdeSeisDentes(), "6", abate.getMaturidade().getPercentualSeisDentes());

//                Paragraph maturidadeSeisDentes = new Paragraph(new Chunk("6 dentes - " + abate.getMaturidade().getQtdeSeisDentes() + " " + validaQuantidade(Integer.parseInt(abate.getMaturidade().getQtdeSeisDentes())) + " (" + abate.getMaturidade().getPercentualSeisDentes() + ")", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//                document.add(maturidadeSeisDentes);
            }

            if (Integer.parseInt(abate.getMaturidade().getQtdeOitoDentes()) > 0) {
                tabelaMaturidade(maturidadeTable, abate.getMaturidade().getQtdeOitoDentes(), "8", abate.getMaturidade().getPercentualOitoDentes());

//                Paragraph maturidadeOitoDentes = new Paragraph(new Chunk("8 dentes - " + abate.getMaturidade().getQtdeOitoDentes() + " " + validaQuantidade(Integer.parseInt(abate.getMaturidade().getQtdeOitoDentes())) + " (" + abate.getMaturidade().getPercentualOitoDentes() + ")", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
//                document.add(maturidadeOitoDentes);
            }

            PdfPCell finalTabelaMaturidade = new PdfPCell();
            finalTabelaMaturidade.setBorder(PdfPCell.NO_BORDER);
            finalTabelaMaturidade.setColspan(3);
            finalTabelaMaturidade.setBorderWidthBottom(1.5f);
            finalTabelaMaturidade.setBorderColorBottom(BaseColor.GRAY);
            finalTabelaMaturidade.setUseBorderPadding(true);
            finalTabelaMaturidade.setPaddingBottom(5f);
            maturidadeTable.addCell(finalTabelaMaturidade);

            PdfPCell espacoFinalTabelaMaturidade = new PdfPCell(new Paragraph(" ", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            espacoFinalTabelaMaturidade.setBorder(PdfPCell.NO_BORDER);
            espacoFinalTabelaMaturidade.setColspan(3);
            espacoFinalTabelaMaturidade.setUseBorderPadding(true);
            espacoFinalTabelaMaturidade.setPaddingBottom(5f);
            maturidadeTable.addCell(espacoFinalTabelaMaturidade);

            document.add(maturidadeTable);

            document.add(p);

        }

//        document.add(Chunk.NEXTPAGE);

        if (abate.getBonificacoes().size() > 0) {

            Paragraph bonificacoes = new Paragraph(new Chunk("Bonificação", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            document.add(bonificacoes);

            for (Bonificacao bonificacao : abate.getBonificacoes()) {
                Paragraph bonificacaoTipo = new Paragraph(new Chunk(bonificacao.getTipo(), new Font(urTexto, mValueFontSize, Font.BOLDITALIC, BaseColor.BLACK)));
                document.add(bonificacaoTipo);

                Paragraph bonificacaoQtde = new Paragraph(new Chunk(bonificacao.getQtde(), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                document.add(bonificacaoQtde);

                if (bonificacao.getTotal() != null) {
                    Paragraph bonificacaoTotal = new Paragraph(new Chunk("Total: R$ " + formatDecimal(Double.valueOf(bonificacao.getTotal())), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(bonificacaoTotal);
                    totalBonificacao += Double.valueOf(bonificacao.getTotal());

                    Paragraph bonificacaoMedia = new Paragraph(String.valueOf(new Chunk("Média no lote: R$ " + formatDecimal(Double.valueOf(bonificacao.getMediaLote())) + "/@)")), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK));
                    document.add(bonificacaoMedia);
                    mediaTotalBonificacao += Double.valueOf(bonificacao.getMediaLote());
                }

                if (!bonificacao.getObservacoes().equals("")) {

                    Paragraph observacoesBonificacao = new Paragraph(new Chunk(bonificacao.getObservacoes(), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(observacoesBonificacao);
                }

                document.add(new Paragraph(""));
                document.add(new Chunk(lineSeparator));
                document.add(new Paragraph(""));
            }

            if (totalBonificacao > 0.0) {
                Paragraph bonificacaoTotalGeral = new Paragraph(String.valueOf(new Chunk("Bonificação total: R$ " + formatDecimal(totalBonificacao) + " (+ R$ " + formatDecimal(mediaTotalBonificacao) + "/@)")), new Font(urTexto, mValueFontSize, Font.BOLD, BaseColor.BLACK));
                document.add(bonificacaoTotalGeral);
            }

            document.add(p);

        }


        if (abate.getPenalizacoes().size() > 0) {

            Paragraph penalizacoes = new Paragraph(new Chunk("Penalização", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            document.add(penalizacoes);

            for (Penalizacao penalizacao : abate.getPenalizacoes()) {
                Paragraph penalizacaoDescricao = new Paragraph(new Chunk(penalizacao.getDescricao(), new Font(urTexto, mValueFontSize, Font.ITALIC, BaseColor.BLACK)));
                document.add(penalizacaoDescricao);

                Paragraph penalizacaoQtde = new Paragraph(new Chunk(penalizacao.getQuantidade(), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                document.add(penalizacaoQtde);

                if (penalizacao.getTotal() != null) {
                    Paragraph penalizacaoTotal = new Paragraph(new Chunk("Total: R$ " + formatDecimal(Double.valueOf(penalizacao.getTotal())), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.RED)));
                    document.add(penalizacaoTotal);
                    totalPenalizacao += Double.valueOf(penalizacao.getTotal());

                    Paragraph penalizacaoMedia = new Paragraph(new Chunk("Média no lote: " + formatDecimal(Double.valueOf(penalizacao.getMedia())) + "/@)", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.RED)));
                    document.add(penalizacaoMedia);
                    mediaTotalPenalizacao += Double.valueOf(penalizacao.getMedia());
                }

                if (!penalizacao.getObservacoes().equals("")) {

                    Paragraph observacoesPenalizacao = new Paragraph(new Chunk(penalizacao.getObservacoes(), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(observacoesPenalizacao);
                }

                document.add(new Paragraph(""));
                document.add(new Chunk(lineSeparator));
                document.add(new Paragraph(""));
            }

            if (totalPenalizacao > 0.0) {
                Paragraph penalizacaoTotalGeral = new Paragraph(String.valueOf(new Chunk("Penalização total: R$ " + formatDecimal(totalPenalizacao) + " (- R$ " + formatDecimal(mediaTotalPenalizacao) + "/@)")), new Font(urTexto, mValueFontSize, Font.BOLD, BaseColor.RED));
                document.add(penalizacaoTotalGeral);
            }

            document.add(p);
        }

        if (!abate.getAcerto().getTotalLiquido().equals("") || !abate.getAcerto().getArrobaNegociada().equals("")) {

            if (!abate.getAcerto().getTotalLiquido().equals("")) {

                Paragraph acerto = new Paragraph(new Chunk("Acerto", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                document.add(acerto);

                if (!abate.getAcerto().getArrobaNegociada().equals("")) {
                    Paragraph acertoArrobaNogociada = new Paragraph(new Chunk("@ Negociada: R$ " + formatDecimal(Double.valueOf(abate.getAcerto().getArrobaNegociada())), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(acertoArrobaNogociada);
                }

                if (!abate.getAcerto().getTotalBruto().equals("")) {
                    Paragraph acertoTotalBruto = new Paragraph(new Chunk("Total Bruto: R$ " + formatDecimal(Double.valueOf(abate.getAcerto().getTotalBruto())), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(acertoTotalBruto);
                }

                if (!abate.getAcerto().getTotalLiquido().equals("")) {
                    Paragraph acertoTotalLiquido = new Paragraph(new Chunk("Total Líquido: R$ " + formatDecimal(Double.valueOf(abate.getAcerto().getTotalLiquido())), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(acertoTotalLiquido);
                }

                if (!abate.getAcerto().getArrobaRecebidaComFunrural().equals("0.0")) {
                    Paragraph acertoArrobaRecebidaComFunrural = new Paragraph(new Chunk("Valor da @ com Funrural: R$ " + formatDecimal(Double.valueOf(abate.getAcerto().getArrobaRecebidaComFunrural())), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(acertoArrobaRecebidaComFunrural);
                }

                if (!abate.getAcerto().getArrobaRecebida().equals("")) {
                    Paragraph acertoArrobaRecebida = new Paragraph(new Chunk("@ Recebida: R$ " + formatDecimal(Double.valueOf(abate.getAcerto().getArrobaRecebida())) + " (Livre de Funrural)", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(acertoArrobaRecebida);
                }

                if (!(abate.getAcerto().getPrazo() == null) && abate.getAcerto().getPrazo().equals("A prazo")) {
                    Paragraph formaDePagamento = new Paragraph(new Chunk("Forma de pagamento: " + abate.getAcerto().getPrazo() + " (" + abate.getAcerto().getDias() + " dias)", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(formaDePagamento);
                } else if (!(abate.getAcerto().getPrazo() == null) && abate.getAcerto().getPrazo().equals("À vista")) {
                    Paragraph formaDePagamento = new Paragraph(new Chunk("Forma de pagamento: " + abate.getAcerto().getPrazo(), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(formaDePagamento);
                }

                document.add(p);

            } else if (!abate.getAcerto().getArrobaNegociada().equals("")) {
                Paragraph acerto = new Paragraph(new Chunk("Acerto", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                document.add(acerto);

                Paragraph acertoArrobaNogociada = new Paragraph(new Chunk("@ Negociada: R$ " + formatDecimal(Double.valueOf(abate.getAcerto().getArrobaNegociada())), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                document.add(acertoArrobaNogociada);

                if (!(abate.getAcerto().getPrazo() == null) && abate.getAcerto().getPrazo().equals("A prazo")) {
                    Paragraph formaDePagamento = new Paragraph(new Chunk("Forma de pagamento: " + abate.getAcerto().getPrazo() + " (" + abate.getAcerto().getDias() + " dias)", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(formaDePagamento);
                } else if (!(abate.getAcerto().getPrazo() == null) && abate.getAcerto().getPrazo().equals("À vista")) {
                    Paragraph formaDePagamento = new Paragraph(new Chunk("Forma de pagamento: " + abate.getAcerto().getPrazo(), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                    document.add(formaDePagamento);
                }

                document.add(p);
            }
        } else if (!(abate.getAcerto().getPrazo() == null) && !(abate.getAcerto().getPrazo().equals(""))) {
            Paragraph acerto = new Paragraph(new Chunk("Acerto", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            document.add(acerto);

            if (!(abate.getAcerto().getPrazo() == null) && abate.getAcerto().getPrazo().equals("A prazo")) {
                Paragraph formaDePagamento = new Paragraph(new Chunk("Forma de pagamento: " + abate.getAcerto().getPrazo() + " (" + abate.getAcerto().getDias() + " dias)", new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                document.add(formaDePagamento);
            } else if (!(abate.getAcerto().getPrazo() == null) && abate.getAcerto().getPrazo().equals("À vista")) {
                Paragraph formaDePagamento = new Paragraph(new Chunk("Forma de pagamento: " + abate.getAcerto().getPrazo(), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
                document.add(formaDePagamento);
            }

            document.add(p);
        }

        if (!abate.getObservacoes().equals("")) {
            Paragraph observacoes = new Paragraph(new Chunk("Observações", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            document.add(observacoes);

            Paragraph observacoesAbate = new Paragraph(new Chunk(abate.getObservacoes(), new Font(urTexto, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            document.add(observacoesAbate);

            document.add(p);
        }

        if (imageLote != null) {
//            document.add(Chunk.NEXTPAGE);
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            Paragraph fotosLote = new Paragraph(new Chunk("Fotos do Abate", new Font(urSubtitulo, mValueFontSize, Font.NORMAL, BaseColor.BLACK)));
            document.add(fotosLote);

            document.add(imageLote);

//            addFooter(pdfWriter);

//            pdfWriter.getPageSize();
//            pdfWriter.setPageSize(new Rectangle(600, 5000));
//
//            document.setPageSize(new Rectangle(600, 5000));
//            document.getPageSize();

//            PdfPTable footer = new PdfPTable(1);
//            footer.setWidthPercentage(80);
//            footer.setTotalWidth(527);
//            footer.setWidths(new int[]{1});
//            footer.getDefaultCell().setBorder(Rectangle.TOP);
//            footer.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
//
//            PdfPCell cell = new PdfPCell();
//            Paragraph titulo = new Paragraph("PECBR Soluções e Consultoria em Agronegócios – contato@pecbr.com Tel: 067-30458338\n" +
//                    "Campo Grande-MS. Rua 13 de Junho, 1811. CENTRO", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(110, 92, 82)));
//            titulo.setAlignment(Element.ALIGN_CENTER);
//            cell.addElement(titulo);
//            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
//            cell.setBorder(Rectangle.TOP);
//            cell.setBorderColor(BaseColor.LIGHT_GRAY);
//
//            footer.addCell(cell);

//        document.add(table);

//        footer.writeSelectedRows(0, -1, 36, 64, writer.getDirectContent());

            // write page
//            PdfContentByte canvas = pdfWriter.getDirectContent();
//            canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
//            footer.writeSelectedRows(0, -1, 34, 60, canvas);
//            canvas.endMarkedContentSequence();

        }

        addFooter(pdfWriter);
    }

    public void tabelaMaturidade(PdfPTable maturidadeTable, String qtdAnimais, String qtdDentes, String percentual) {
        PdfPCell textMaturidade = new PdfPCell(new Paragraph(qtdDentes + " dentes", new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
//                textMaturidade.setVerticalAlignment(Element.ALIGN_LEFT);
        textMaturidade.setBorder(PdfPCell.NO_BORDER);
        textMaturidade.setBorderWidthBottom(0.5f);
        textMaturidade.setBorderColorBottom(BaseColor.GRAY);
        textMaturidade.setMinimumHeight(30f);
        textMaturidade.setUseBorderPadding(true);
        textMaturidade.setPaddingBottom(5f);
//                textMaturidade.setCellEvent(new SolidBorder(PdfPCell.TOP));
//                textMaturidade.addElement(new Phrase("0 dentes - ", new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
        maturidadeTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        maturidadeTable.addCell(textMaturidade);

        PdfPCell textMaturidadeQtd = new PdfPCell(new Paragraph(qtdDentes, new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
//                textMaturidade.setVerticalAlignment(Element.ALIGN_LEFT);
        textMaturidadeQtd.setBorder(PdfPCell.NO_BORDER);
        textMaturidadeQtd.setBorderWidthBottom(0.5f);
        textMaturidadeQtd.setBorderColorBottom(BaseColor.GRAY);
        textMaturidadeQtd.setUseBorderPadding(true);
        textMaturidadeQtd.setPaddingBottom(5f);
        textMaturidadeQtd.setMinimumHeight(30f);
        textMaturidadeQtd.setHorizontalAlignment(Element.ALIGN_CENTER);
//                textMaturidade.setCellEvent(new SolidBorder(PdfPCell.TOP));
//                textMaturidadeQtd.addElement(new Phrase(abate.getMaturidade().getQtdeZeroDentes(), new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
        maturidadeTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        maturidadeTable.addCell(textMaturidadeQtd);

        PdfPCell textMaturidadePercentual = new PdfPCell(new Paragraph(percentual, new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
//                textMaturidade.setVerticalAlignment(Element.ALIGN_LEFT);
        textMaturidadePercentual.setBorder(PdfPCell.NO_BORDER);
        textMaturidadePercentual.setBorderWidthBottom(0.5f);
        textMaturidadePercentual.setBorderColorBottom(BaseColor.GRAY);
        textMaturidadePercentual.setUseBorderPadding(true);
        textMaturidadePercentual.setMinimumHeight(30f);
        textMaturidadePercentual.setPaddingBottom(5f);
        textMaturidadePercentual.setHorizontalAlignment(Element.ALIGN_CENTER);
//                textMaturidade.setCellEvent(new SolidBorder(PdfPCell.TOP));
//                textMaturidadePercentual.addElement(new Phrase(abate.getMaturidade().getPercentualZeroDentes(), new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
        maturidadeTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        maturidadeTable.addCell(textMaturidadePercentual);
    }

    public void tabelaAcabamento(PdfPTable acabamentoTable, String qtdAnimais, String tipoAcabamento, String percentual) {

        BaseColor corFonteAcabamento;

        if (tipoAcabamento.equals("Ausente") || tipoAcabamento.equals("Escasso Menos")) {
            corFonteAcabamento = BaseColor.RED;
        }else {
            corFonteAcabamento = BaseColor.BLACK;
        }

        PdfPCell textAcabamento = new PdfPCell(new Paragraph(tipoAcabamento, new Font(urTexto, mHeadingFontSize, Font.NORMAL, corFonteAcabamento)));
        textAcabamento.setBorder(PdfPCell.NO_BORDER);
        textAcabamento.setBorderWidthBottom(0.5f);
        textAcabamento.setBorderColorBottom(BaseColor.GRAY);
        textAcabamento.setMinimumHeight(30f);
        textAcabamento.setUseBorderPadding(true);
        textAcabamento.setPaddingBottom(5f);
//                textMaturidade.setCellEvent(new SolidBorder(PdfPCell.TOP));
//                textMaturidade.addElement(new Phrase("0 dentes - ", new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
        acabamentoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        acabamentoTable.addCell(textAcabamento);

        PdfPCell textAcabamentoQtd = new PdfPCell(new Paragraph(qtdAnimais, new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
//                textMaturidade.setVerticalAlignment(Element.ALIGN_LEFT);
        textAcabamentoQtd.setBorder(PdfPCell.NO_BORDER);
        textAcabamentoQtd.setBorderWidthBottom(0.5f);
        textAcabamentoQtd.setBorderColorBottom(BaseColor.GRAY);
        textAcabamentoQtd.setUseBorderPadding(true);
        textAcabamentoQtd.setPaddingBottom(5f);
        textAcabamentoQtd.setMinimumHeight(30f);
        textAcabamentoQtd.setHorizontalAlignment(Element.ALIGN_CENTER);
//                textMaturidade.setCellEvent(new SolidBorder(PdfPCell.TOP));
//                textMaturidadeQtd.addElement(new Phrase(abate.getMaturidade().getQtdeZeroDentes(), new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
        acabamentoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        acabamentoTable.addCell(textAcabamentoQtd);

        PdfPCell textAcabamentoPercentual = new PdfPCell(new Paragraph(percentual, new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
//                textMaturidade.setVerticalAlignment(Element.ALIGN_LEFT);
        textAcabamentoPercentual.setBorder(PdfPCell.NO_BORDER);
        textAcabamentoPercentual.setBorderWidthBottom(0.5f);
        textAcabamentoPercentual.setBorderColorBottom(BaseColor.GRAY);
        textAcabamentoPercentual.setUseBorderPadding(true);
        textAcabamentoPercentual.setMinimumHeight(30f);
        textAcabamentoPercentual.setPaddingBottom(5f);
        textAcabamentoPercentual.setHorizontalAlignment(Element.ALIGN_CENTER);
//                textMaturidade.setCellEvent(new SolidBorder(PdfPCell.TOP));
//                textMaturidadePercentual.addElement(new Phrase(abate.getMaturidade().getPercentualZeroDentes(), new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
        acabamentoTable.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
        acabamentoTable.addCell(textAcabamentoPercentual);
    }

//        document.add(new Paragraph(""));
//        document.add(new Chunk(lineSeparator));
//        document.add(new Paragraph(""));

    public void carregaImagemDoLote(Bitmap imagemLote) {

        try {
            if (imagemLote != null) {
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imagemLote.compress(Bitmap.CompressFormat.PNG, 100, stream);
                imageLote = Image.getInstance(stream.toByteArray());
                imageLote.scaleToFit(525, 525);
            }
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Image addImage(Context context) {

        try {
            Drawable drawable = context.getResources().getDrawable(R.drawable.logopecbr);
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            Bitmap bmp = bitmapDrawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
            image = Image.getInstance(stream.toByteArray());
            image.scaleToFit(150, 150);
//            document.add(image);
        } catch (BadElementException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void createTable(String[] header, ArrayList<String[]> clients) {
//        try {
//            paragraph = new Paragraph();
//            paragraph.setFont(fText);
//            PdfPTable pdfPTable = new PdfPTable(header.length);
//            pdfPTable.setWidthPercentage(100);
//            pdfPTable.setSpacingBefore(20);
//            PdfPCell pdfPCell;
//            int indexC = 0;
//            while (indexC < header.length) {
//                pdfPCell = new PdfPCell(new Phrase(header[indexC++], fSubtitle));
//                pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                pdfPCell.setBackgroundColor(BaseColor.GREEN);
//                pdfPTable.addCell(pdfPCell);
//            }
//
//            for (int indexR = 0; indexR < clients.size(); indexR++) {
//
//                for (indexC = 0; indexC < header.length; indexC++) {
//                    String[]row = clients.get(indexR);
//                    pdfPCell = new PdfPCell(new Phrase(row[indexC]));
//                    pdfPCell.setHorizontalAlignment(Element.ALIGN_CENTER);
//                    pdfPCell.setFixedHeight(40);
//                    pdfPTable.addCell(pdfPCell);
//                }
//            }
//
//            paragraph.add(pdfPTable);
//            document.add(paragraph);
//        } catch (DocumentException e) {
//            Log.e("createTable", e.toString());
//        }
    }

    public void viewPDF() {
        Intent intent = new Intent(context, ViewPDFActivity.class);
        intent.putExtra("path", pdfFile.getAbsolutePath());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


//    public void onStartPage() {
//        try {
//            addHeader(pdfWriter);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void onEndPage(PdfWriter pdfWriter, Document document) {
//        try {
//            addHeader(pdfWriter);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        this.pdfWriter = pdfWriter;

//        try {
//            addFooter(pdfWriter);
//        } catch (DocumentException e) {
//            e.printStackTrace();
//        }
    }

//    public void onEndPage() throws DocumentException, IOException {
////        PdfPTable table = new PdfPTable(1);
////        try {
////            table.setWidths(new int[]{24});
////            table.setTotalWidth(527);
////            table.setLockedWidth(true);
////            table.getDefaultCell().setFixedHeight(20);
////            table.getDefaultCell().setBorder(Rectangle.BOTTOM);
////            table.addCell(header);
////            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_LEFT);
////          table.addCell(String.format("Page %d of", pdfWriter.getPageNumber()));
////
////            PdfPCell cell = new PdfPCell(addImage(context));
////            cell.setBorder(Rectangle.BOTTOM);
////            table.addCell(cell);
////            table.writeSelectedRows(0, -1, 34, 803, pdfWriter.getDirectContent());
////        }
////        catch(DocumentException de) {
////            throw new ExceptionConverter(de);
////        }
//
////        Rectangle rect = pdfWriter.getBoxSize("rectangle");
////        // BOTTOM LEFT
////        ColumnText.showTextAligned(pdfWriter.getDirectContent(),
////                Element.ALIGN_CENTER, new Phrase("BOTTOM LEFT"),
////                rect.getLeft()+15, rect.getBottom(), 0);
////
////        // BOTTOM MEDIUM
////        ColumnText.showTextAligned(pdfWriter.getDirectContent(),
////                Element.ALIGN_CENTER, new Phrase("BOTTOM MEDIUM"),
////                rect.getRight() / 2, rect.getBottom(), 0);
////
////        // BOTTOM RIGHT
////        ColumnText.showTextAligned(pdfWriter.getDirectContent(),
////                Element.ALIGN_CENTER, new Phrase("BOTTOM RIGHT"),
////                rect.getRight()-10, rect.getBottom(), 0);
//
////        Rectangle page = document.getPageSize();
//
//        PdfPTable table = new PdfPTable(1);
//        table.setWidthPercentage(100);
//        table.setTotalWidth(523);
//        table.setWidths(new int[]{1});
//        PdfPCell cell = new PdfPCell();
//        Paragraph titulo = new Paragraph("PECBR Soluções e Consultoria em Agronegócios – contato@pecbr.com Tel: 067-30458338\n" +
//                "Campo Grande-MS. Rua 13 de Junho, 1811. CENTRO", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(110, 92, 82)));
//        titulo.setAlignment(Element.ALIGN_CENTER);
//        cell.addElement(titulo);
//        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
//        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
//        cell.setBorder(Rectangle.NO_BORDER);
////        cell.setPaddingBottom(30);
//        table.addCell(cell);
//
////        document.add(table);
//
//        table.writeSelectedRows(0, -1, 36, 64, pdfWriter.getDirectContent());
//    }

//    private void addHeader(PdfWriter writer) throws IOException, DocumentException {
////        PdfPTable header = new PdfPTable(2);
////        try {
////            // set defaults
////            header.setWidths(new int[]{2, 24});
////            header.setTotalWidth(527);
////            header.setLockedWidth(true);
////            header.getDefaultCell().setFixedHeight(15);
////            header.getDefaultCell().setBorder(Rectangle.BOTTOM);
////            header.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
////
////            // add image
////            Image logo = Image.getInstance(addImage(context));
////            header.addCell(logo);
////
////            // add text
////            PdfPCell text = new PdfPCell();
////            text.setPaddingBottom(15);
////            text.setPaddingLeft(10);
////            text.setBorder(Rectangle.BOTTOM);
////            text.setHorizontalAlignment(Element.ALIGN_CENTER);
////            text.setBorderColor(BaseColor.LIGHT_GRAY);
////            text.addElement(new Phrase("iText PDF Header Footer Example", new Font(Font.FontFamily.HELVETICA, 12)));
////            text.addElement(new Phrase("https://memorynotfound.com", new Font(Font.FontFamily.HELVETICA, 8)));
////            header.addCell(text);
////
////            // write content
////            header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());
////        } catch (DocumentException de) {
////            throw new ExceptionConverter(de);
////        }
//
////            paragraph = new Paragraph();
////            addChildP(new Paragraph(title, fTitle));
////            addChildP(new Paragraph(subtitle, fSubtitle));
////            addChildP(new Paragraph("Generado: " + date, fHighText));
////            paragraph.setSpacingAfter(30);
////            document.add(paragraph);
//
//            PdfPTable table = new PdfPTable(2);
//            table.setWidthPercentage(100);
//            table.setWidths(new int[]{1, 4});
////            table.setLockedWidth(true);
////            table.getDefaultCell().setFixedHeight(50);
////            table.getDefaultCell().setBorder(Rectangle.BOTTOM);
////            table.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);
//
//            PdfPCell cellLogo = new PdfPCell();
//            cellLogo.addElement(addImage(context));
//            cellLogo.setBorder(Rectangle.NO_BORDER);
//
//            table.addCell(cellLogo);
//
//            PdfPCell cell = new PdfPCell();
//            paragraph = new Paragraph();
//            addChildP(new Paragraph(title, fTitle));
//
//            PdfPTable dataGerado = new PdfPTable(2);
//            dataGerado.setWidthPercentage(100);
//            dataGerado.setWidths(new int[]{1, 6});
//            PdfPCell cellGerado = new PdfPCell();
//            cellGerado.setBorder(Rectangle.NO_BORDER);
//            cellGerado.addElement(new Paragraph("Data:", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(110, 92, 82))));
//            cellGerado.setVerticalAlignment(Element.ALIGN_BOTTOM);
//            dataGerado.addCell(cellGerado);
//            PdfPCell cellDate = new PdfPCell();
//            cellDate.setBorder(Rectangle.NO_BORDER);
//            cellDate.addElement(new Paragraph(date, new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD, new BaseColor(189, 167, 128))));
//            cellDate.setVerticalAlignment(Element.ALIGN_BOTTOM);
//            dataGerado.addCell(cellDate);
//            paragraph.add(dataGerado);
//            paragraph.setSpacingAfter(15);
//
//            cell.addElement(paragraph);
//            cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
//            cell.setBorder(Rectangle.NO_BORDER);
//            table.addCell(cell);
//            document.add(table);
//
//            LineSeparator lineSeparator = new LineSeparator();
//            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 30));
//            lineSeparator.setLineWidth(2);
//
//            // Adding Line Breakable Space....
//            document.add(new Paragraph(""));
//
//            // Adding Horizontal Line...
//            document.add(new Chunk(lineSeparator));
//            // Adding Line Breakable Space....
//            document.add(new Paragraph(""));
//
//    }

    private void addHeader(PdfWriter writer) throws MalformedURLException {
        PdfPTable header = new PdfPTable(2);
        try {
            // set defaults
            header.setWidths(new int[]{1, 4});
            header.setTotalWidth(527);
            header.setWidthPercentage(100);
            header.getDefaultCell().setFixedHeight(100);
            header.getDefaultCell().setVerticalAlignment(Element.ALIGN_BOTTOM);
            header.getDefaultCell().setPaddingBottom(10);
            header.getDefaultCell().setPaddingRight(10);
            header.getDefaultCell().setBorder(Rectangle.BOTTOM);
            header.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

            // add image
            Image logo = Image.getInstance(addImage(context));
            header.addCell(logo);

            // add text
            PdfPCell text = new PdfPCell();
            text.setPaddingBottom(15);
//            text.setPaddingLeft(10);
            text.setVerticalAlignment(Element.ALIGN_BOTTOM);
            text.setBorder(Rectangle.BOTTOM);
            text.setBorderColor(BaseColor.LIGHT_GRAY);
            text.addElement(new Phrase(title, new Font(fTitle)));
            text.addElement(new Phrase("Técnico Responsável: " + tecnicoResponsavel, new Font(urTexto, mHeadingFontSize, Font.NORMAL, BaseColor.BLACK)));
            text.addElement(new Phrase("Data: " + date, new Font(fSubtitle)));
            header.addCell(text);

            // write content
//            header.writeSelectedRows(0, -1, 34, 803, writer.getDirectContent());

            // write page
            PdfContentByte canvas = writer.getDirectContent();
            canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
            header.writeSelectedRows(0, -1, 34, (tamanhoTela - 20f), canvas);
            canvas.endMarkedContentSequence();
        } catch (DocumentException de) {
            throw new ExceptionConverter(de);
        }
    }

    private void addFooter(PdfWriter writer) throws DocumentException {

        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(80);
        footer.setTotalWidth(527);
        footer.setWidths(new int[]{1});
        footer.getDefaultCell().setBorder(Rectangle.TOP);
        footer.getDefaultCell().setBorderColor(BaseColor.LIGHT_GRAY);

        PdfPCell cell = new PdfPCell();
        Paragraph titulo = new Paragraph("PECBR Soluções e Consultoria em Agronegócios – contato@pecbr.com Tel: 067-30458338\n" +
                "Campo Grande-MS. Rua 13 de Junho, 1811. CENTRO", new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, new BaseColor(110, 92, 82)));
        titulo.setAlignment(Element.ALIGN_CENTER);
        cell.addElement(titulo);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        cell.setBorder(Rectangle.TOP);
        cell.setBorderColor(BaseColor.LIGHT_GRAY);
//        cell.setPaddingBottom(30);
        footer.addCell(cell);

//        document.add(table);

//        footer.writeSelectedRows(0, -1, 36, 64, writer.getDirectContent());

        // write page
        PdfContentByte canvas = writer.getDirectContent();
        canvas.beginMarkedContentSequence(PdfName.ARTIFACT);
        footer.writeSelectedRows(0, -1, 34, 50, canvas);
        canvas.endMarkedContentSequence();

    }

    public String formatDecimal(Double valor) {
        DecimalFormat df = new DecimalFormat("#,###,##0.00");
        return df.format(valor);
    }

    private String validaQuantidade(Integer qtde) {
        if (qtde > 1)
            return "animais";
        else
            return "animal";
    }


    private  class SolidBorder extends CustomBorder {

        public SolidBorder(int border) {
            super(border);
        }
        public void setLineDash(PdfContentByte canvas) {
            canvas.setLineDash(1, 1);
        }
    }

    abstract class CustomBorder implements PdfPCellEvent {
        private int border = 0;
        public CustomBorder(int border) {
            this.border = border;
        }
        public void cellLayout(PdfPCell cell, Rectangle position,
                               PdfContentByte[] canvases) {
            PdfContentByte canvas = canvases[PdfPTable.LINECANVAS];
            canvas.saveState();
            setLineDash(canvas);
            if ((border & PdfPCell.TOP) == PdfPCell.TOP) {
                canvas.moveTo(position.getRight(), position.getTop());
                canvas.lineTo(position.getLeft(), position.getTop());
            }
            if ((border & PdfPCell.BOTTOM) == PdfPCell.BOTTOM) {
                canvas.moveTo(position.getRight(), position.getBottom());
                canvas.lineTo(position.getLeft(), position.getBottom());
            }
            if ((border & PdfPCell.RIGHT) == PdfPCell.RIGHT) {
                canvas.moveTo(position.getRight(), position.getTop());
                canvas.lineTo(position.getRight(), position.getBottom());
            }
            if ((border & PdfPCell.LEFT) == PdfPCell.LEFT) {
                canvas.moveTo(position.getLeft(), position.getTop());
                canvas.lineTo(position.getLeft(), position.getBottom());
            }
            canvas.stroke();
            canvas.restoreState();
        }

        public abstract void setLineDash(PdfContentByte canvas);
    }
}
