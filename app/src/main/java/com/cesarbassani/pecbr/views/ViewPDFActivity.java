package com.cesarbassani.pecbr.views;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cesarbassani.pecbr.R;
import com.cesarbassani.pecbr.constants.GuestConstants;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewPDFActivity extends AppCompatActivity {

    private PDFView pdfView;
    private File file;

    private Toolbar mToolbar;
    private Typeface typeFace_date;
    private Typeface typeFace_title;
    private String dateString;
//    private List<Uri> uriImages;
//    private View parent_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pdf);

        long data = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
        dateString = sdf.format(data);

        initToolBar();

        verifyStoragePermissions(this);

        pdfView = findViewById(R.id.pdfView);
//        parent_view = findViewById(android.R.id.content);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            file = new File(bundle.getString("path", ""));
        }

        pdfView.fromFile(file)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .enableAntialiasing(true)
                .load();
    }

    public void initToolBar() {

        mToolbar = findViewById(R.id.tb_main);
        mToolbar.setTitle("RESUMO DE ABATE - PECBR");
        mToolbar.setSubtitle("Data: " + dateString);
//        this.mViewHolder.mToolbar.setLogo(R.mipmap.ic_launcher);

        typeFace_date = Typeface.createFromAsset(this.getAssets(), "fonts/brandon_light.otf");
        typeFace_title = Typeface.createFromAsset(this.getAssets(), "fonts/brandon_bold.otf");

        ((TextView) mToolbar.getChildAt(0)).setTypeface(typeFace_title);
        ((TextView) mToolbar.getChildAt(0)).setTextSize(16);

        ((TextView) mToolbar.getChildAt(1)).setTypeface(typeFace_date);
        ((TextView) mToolbar.getChildAt(1)).setTextSize(14);

        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.voltar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ViewPDFActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    GuestConstants.PERMISSIONS.PERMISSIONS_STORAGE,
                    GuestConstants.PERMISSIONS.REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.enviar:
                enviarWhatsapp();
                return true;
            case R.id.compartilhar:
                compartilhar();
                return true;
//            case R.id.gerar_png:
//                uriImages = new ArrayList<>();
//                ArrayList<Bitmap> bitmaps = pdfToBitmap(file);
//                for (Bitmap bmp : bitmaps) {
//                    uriImages.add(storeImage(bmp));
//                }
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public ArrayList<Bitmap> pdfToBitmap(File file) {
        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        try {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY));

            Bitmap bitmap;
            final int pageCount = renderer.getPageCount();
            for (int i = 0; i < pageCount; i++) {
                PdfRenderer.Page page = renderer.openPage(i);

                int width = getResources().getDisplayMetrics().densityDpi / 72 * page.getWidth();
                int height = getResources().getDisplayMetrics().densityDpi / 72 * page.getHeight();
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(Color.WHITE);

                Canvas canvas = new Canvas(bitmap);
                canvas.drawBitmap(bitmap, 0, 0, null);

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

                bitmaps.add(bitmap);

                // close the page
                page.close();

            }

            // close the renderer
            renderer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

//        if (bitmaps.size() > 0)
//            Snackbar.make(parent_view, R.string.success_convert_pdf_png, Snackbar.LENGTH_SHORT).show();

        return bitmaps;

    }

    private Uri storeImage(Bitmap image) {
        File pictureFile = getOutputMediaFile();
        if (pictureFile == null) {
            Log.d("SAVEBMP1", "Error creating media file, check storage permissions: ");// e.getMessage());
//            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            image.setHasAlpha(true);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d("SAVEBMP2", "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d("SAVBMP3", "Error accessing file: " + e.getMessage());
        }

        Uri mImageUri = Uri.parse(pictureFile.getAbsolutePath());

        return mImageUri;
    }

    /**
     * Create a File for saving an image or video
     */
    private File getOutputMediaFile() {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + getApplicationContext().getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmm").format(new Date());
        File mediaFile;
        String mImageName = "MI_" + timeStamp + ".png";
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + mImageName);
        return mediaFile;
    }

    private void enviarWhatsapp() {
//        Uri uri = Uri.fromFile(file);

        Uri uri = FileProvider.getUriForFile(
                ViewPDFActivity.this,
                "com.cesarbassani.pecbr",
                file);

//        for (Uri uri : uriImages) {
        Intent share = new Intent();
        share.setAction(Intent.ACTION_SEND);
        share.setType("application/pdf");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.setPackage("com.whatsapp");

        startActivity(share);

//        }
    }

    private void compartilhar() {
        Uri uri = FileProvider.getUriForFile(
                ViewPDFActivity.this,
                "com.cesarbassani.pecbr",
                file);

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.setType("application/pdf");
//        String texto = "Olá sou um texto compartilhado"
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
//        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }
}
