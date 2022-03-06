package com.rangi.nanodet.fragment.news;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Size;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
//import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.camera.core.UseCase;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LifecycleOwner;

import com.rangi.nanodet.Box;
//import com.rangi.nanodet.MyApp;
import com.rangi.nanodet.NanoDet;
import com.rangi.nanodet.R;
import com.rangi.nanodet.core.BaseFragment;
import com.rangi.nanodet.databinding.FragmentRecogeBinding;
//import com.rangi.nanodet.person.personMsg;
//import com.xuexiang.xaop.annotation.SingleClick;
import com.rangi.nanodet.fragment.news.person.personMsg;
import com.xuexiang.xaop.annotation.SingleClick;
import com.xuexiang.xpage.annotation.Page;
import com.xuexiang.xrouter.annotation.AutoWired;
import com.xuexiang.xrouter.launcher.XRouter;
import com.xuexiang.xui.adapter.simple.XUISimpleAdapter;
import com.xuexiang.xui.utils.DensityUtils;
import com.xuexiang.xui.widget.popupwindow.popup.XUIListPopup;
import com.xuexiang.xui.widget.popupwindow.popup.XUIPopup;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.xuexiang.xutil.XUtil.runOnUiThread;

@Page
public class RecogeFragment extends BaseFragment<FragmentRecogeBinding> implements View.OnClickListener{
    public static final String KEY_TITLE_NAME = "title_name";
    String st="abc";
    boolean Fg=true;
    boolean Fl=true;
    public static boolean USE_GPU = false;
    public static CameraX.LensFacing CAMERA_ID = CameraX.LensFacing.BACK;
    private static final int REQUEST_CAMERA = 1;
    private static String[] PERMISSIONS_CAMERA = {
            Manifest.permission.CAMERA
    };
//    private ImageView resultImageView;
    private EditText show;
    private double threshold = 0.3, nms_threshold = 0.7;
    private AtomicBoolean detecting = new AtomicBoolean(false);
    private AtomicBoolean detectPhoto = new AtomicBoolean(false);

    private long startTime = 0;
    private long endTime = 0;
    private int width;
    private int height;
    private XUIListPopup historyPopup;
    private LinearLayout detailMainRL;
    String[] listItems = new String[]{
            "",
            "",
            "",
    };
    String old="";
    private int n=2;
    double total_fps = 0;
    int fps_count = 0;

    //    String st = box.getLabel();
    protected Bitmap mutableBitmap;
    ExecutorService detectService = Executors.newSingleThreadExecutor();

    /**
     * 自动注入参数，不能是private
     */
    @AutoWired(name = KEY_TITLE_NAME)
    String title;


    @NonNull
    @Override
    protected FragmentRecogeBinding viewBindingInflate(LayoutInflater inflater, ViewGroup container) {
        return FragmentRecogeBinding.inflate(inflater, container, false);
    }

    @Override
    protected void initArgs() {
        // 自动注入参数必须在initArgs里进行注入
        XRouter.getInstance().inject(this);
    }


    @Override
    protected String getPageTitle() {
        return title;
    }

    @Override
    protected void initViews() {
        show = findViewById(R.id.input);
        detailMainRL = (LinearLayout) findViewById(R.id.root_layout);
    }
    @Override
    protected void initListeners() {
        binding.imageView.setOnClickListener((View.OnClickListener) this);
        binding.yes.setOnClickListener((View.OnClickListener) this);
        binding.send.setOnClickListener((View.OnClickListener) this);
        binding.btnHistory.setOnClickListener((View.OnClickListener) this);

    }
    @SingleClick
    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id==R.id.imageView){
            detectPhoto.set(false);
        }else if(id==R.id.yes){
            if(!Fl)
                Fl=true;
            else
                Fl=false;
            old =show.getText().toString()+" ";
            if(!listItems[0].equals("")){
                listItems[0]=listItems[1];
                listItems[1]=listItems[2];
                listItems[2]=show.getText().toString();
            }
            else {
                listItems[n]=show.getText().toString();
                System.out.println(n);
                n--;
            }
        }else if(id==R.id.send){
            String input=show.getText().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Class.forName("com.mysql.jdbc.Driver");
                        java.sql.Connection cn= DriverManager.getConnection("jdbc:mysql://106.14.35.94/eladmin","eladmin","123456");
                        String sql = "insert into sys_user_message (message_id,user_id,user_name,message,send_to) values(?, ?, ?, ?, ?)";
                        //4. 创建PreparedStatement对象
                        PreparedStatement ps = cn.prepareStatement(sql);
//                            MyApp app =(MyApp) getApplication();
//                            String name= app.getName();
//                            int index= app.getIndex();
//                            System.out.println(name);
                        ps.setInt(1, 2);
//                            ps.setInt(2, index);
//                            ps.setString(3, name);
                        ps.setString(4, input);
                        ps.setString(5, "admin");
//                            ps.setInt(6, 25);
//                        int i = ps.executeUpdate();
                        ps.close();
                        cn.close();
                        System.out.println("连接数据库成功");
                    } catch (ClassNotFoundException e) {
                        System.out.println("连接数据库失败");
                        e.printStackTrace();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            System.out.println(input);
            Fg=true;
            openNewPage(personMsg.class,personMsg.KEY_INPUT,input);
//
        }else if(id==R.id.btn_history){
            history();
            historyPopup.setAnimStyle(XUIPopup.ANIM_GROW_FROM_CENTER);
            historyPopup.setPreferredDirection(XUIPopup.DIRECTION_TOP);
            historyPopup.show(view);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int permission = ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_CAMERA,
                    REQUEST_CAMERA
            );
//           finish();
        }
        NanoDet.init(getContext().getAssets(), USE_GPU);

        startCamera();

        threshold = 0.4f;
        nms_threshold = 0.6f;
//        final String format = "Thresh: %.2f, NMS: %.2f";

    }


//    public void listenKeyboard() {
//        // TODO Auto-generated method stub
//        heightDiff = detailMainRL.getRootView().getHeight() - detailMainRL.getHeight();
//        if (heightDiff > detailMainRL.getRootView().getHeight()/3)
//        { // 说明键盘是弹出状态
//            Fg=false;
//        } else{
//            Fg=true;
//        }
//    }

//    private void updateTransform() {
//        Matrix matrix = new Matrix();
//        float[] rotations = {0, 90, 180, 270};
//    }

    private void startCamera() {
        CameraX.unbindAll();
        // 1. preview
        PreviewConfig previewConfig = new PreviewConfig.Builder()
                .setLensFacing(CAMERA_ID)
//                .setTargetAspectRatio()  // 宽高比
                .setTargetResolution(new Size(330, 440))  // 分辨率
                .build();

        Preview preview = new Preview(previewConfig);
        preview.setOnPreviewOutputUpdateListener(new Preview.OnPreviewOutputUpdateListener() {
            @Override
            public void onUpdated(Preview.PreviewOutput output) {
//                listenKeyboard();//监听键盘
        //                updateTransform();
            }
        });
        DetectAnalyzer detectAnalyzer = new DetectAnalyzer();
        CameraX.bindToLifecycle((LifecycleOwner) this, preview, gainAnalyzer(detectAnalyzer));

    }

    private UseCase gainAnalyzer(DetectAnalyzer detectAnalyzer) {
        ImageAnalysisConfig.Builder analysisConfigBuilder = new ImageAnalysisConfig.Builder();
        analysisConfigBuilder.setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE);
        analysisConfigBuilder.setTargetResolution(new Size(330, 440));  // 输出预览图像尺寸
        ImageAnalysisConfig config = analysisConfigBuilder.build();
        ImageAnalysis analysis = new ImageAnalysis(config);
        analysis.setAnalyzer(detectAnalyzer);
        return analysis;
    }

    private Bitmap imageToBitmap(ImageProxy image) {
        byte[] nv21 = imagetToNV21(image);

        YuvImage yuvImage = new YuvImage(nv21, ImageFormat.NV21, image.getWidth(), image.getHeight(), null);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        yuvImage.compressToJpeg(new Rect(0, 0, yuvImage.getWidth(), yuvImage.getHeight()), 100, out);
        byte[] imageBytes = out.toByteArray();

        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    }

    private byte[] imagetToNV21(ImageProxy image) {
        ImageProxy.PlaneProxy[] planes = image.getPlanes();
        ImageProxy.PlaneProxy y = planes[0];
        ImageProxy.PlaneProxy u = planes[1];
        ImageProxy.PlaneProxy v = planes[2];
        ByteBuffer yBuffer = y.getBuffer();
        ByteBuffer uBuffer = u.getBuffer();
        ByteBuffer vBuffer = v.getBuffer();
        int ySize = yBuffer.remaining();
        int uSize = uBuffer.remaining();
        int vSize = vBuffer.remaining();
        byte[] nv21 = new byte[ySize + uSize + vSize];
        // U and V are swapped
        yBuffer.get(nv21, 0, ySize);
        vBuffer.get(nv21, ySize, vSize);
        uBuffer.get(nv21, ySize + vSize, uSize);

        return nv21;
    }

    private class DetectAnalyzer implements ImageAnalysis.Analyzer {

        @Override
        public void analyze(ImageProxy image, final int rotationDegrees) {

            detectOnModel(image, rotationDegrees);
        }
    }


    private void detectOnModel(ImageProxy image, final int rotationDegrees) {
        if (detecting.get() || detectPhoto.get()) {
            return;
        }
        detecting.set(true);
        startTime = System.currentTimeMillis();
        final Bitmap bitmapsrc = imageToBitmap(image);  // 格式转换
        if (detectService == null) {
            detecting.set(false);
            return;
        }
        detectService.execute(new Runnable() {
            @Override
            public void run() {
                Matrix matrix = new Matrix();
                matrix.postRotate(rotationDegrees);
                width = bitmapsrc.getWidth();
                height = bitmapsrc.getHeight();
                Bitmap bitmap = Bitmap.createBitmap(bitmapsrc, 0, 0, Math.min(width, height), Math.min(width, height), matrix, false);

                Box[] result = null;
                result = NanoDet.detect(bitmap, threshold, nms_threshold);

                if (result == null) {
                    detecting.set(false);
                    return;
                }

                mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                mutableBitmap = drawBoxRects(mutableBitmap, result);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        detecting.set(false);
                        if (detectPhoto.get()) {
                            return;
                        }
                        binding.imageView.setImageBitmap(mutableBitmap);
                        endTime = System.currentTimeMillis();
                        long dur = endTime - startTime;
                        float fps = (float) (1000.0 / dur);
                        total_fps = (total_fps == 0) ? fps : (total_fps + fps);
                        fps_count++;
//                        String modelName = getModelName();
                    }
                });
            }
        });
    }


    protected Bitmap drawBoxRects(Bitmap mutableBitmap, Box[] results) {
        if (results == null || results.length <= 0) {
            return mutableBitmap;
        }
        Canvas canvas = new Canvas(mutableBitmap);
        final Paint boxPaint = new Paint();
        boxPaint.setAlpha(200);
        boxPaint.setStyle(Paint.Style.STROKE);
        boxPaint.setStrokeWidth(4 * mutableBitmap.getWidth() / 800.0f);
        boxPaint.setTextSize(40 * mutableBitmap.getWidth() / 800.0f);
//        listenKeyboard();
        for (Box box : results) {
            boxPaint.setColor(box.getColor());
            boxPaint.setStyle(Paint.Style.FILL);
            canvas.drawText(box.getLabel() + String.format(Locale.CHINESE, " %.3f", box.getScore()), box.x0 + 3, box.y0 + 40 * mutableBitmap.getWidth() / 1000.0f, boxPaint);
            if (Fg&&Fl){
                st=box.getLabel();

                show.setText(old+st);
            }


            boxPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRect(box.getRect(), boxPaint);
        }
        return mutableBitmap;
    }


    @Override
    public void onDestroy() {
        if (detectService != null) {
            detectService.shutdown();
            detectService = null;
        }
        CameraX.unbindAll();
        super.onDestroy();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
//                Toast.makeText(this, "Camera Permission!", Toast.LENGTH_SHORT).show();
//                this.finish();
                System.out.println("443");
                openNewPage(personMsg.class);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
           return;
        }
        detectPhoto.set(true);
        Bitmap image = getPicture(data.getData());
        if (image == null) {
//            Toast.makeText(this, "Photo is null", Toast.LENGTH_SHORT).show();
            openNewPage(personMsg.class);
            System.out.println("458");
            return;
        }
        Bitmap mutableBitmap = image.copy(Bitmap.Config.ARGB_8888, true);

        Box[] result = NanoDet.detect(image, threshold, nms_threshold);

        mutableBitmap = drawBoxRects(mutableBitmap, result);
        binding.imageView.setImageBitmap(mutableBitmap);
    }

    public Bitmap getPicture(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
        if (cursor == null) {
            return null;
        }
        cursor.moveToFirst();
        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        String picturePath = cursor.getString(columnIndex);
        cursor.close();
        Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
        if (bitmap == null) {
            return null;
        }
        int rotate = readPictureDegree(picturePath);
        return rotateBitmapByDegree(bitmap, rotate);
    }

    public int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    public Bitmap rotateBitmapByDegree(Bitmap bm, int degree) {
        Bitmap returnBm = null;
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        try {
            returnBm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), matrix, true);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        }
        if (returnBm == null) {
            returnBm = bm;
        }
        if (bm != returnBm) {
            bm.recycle();
        }
        return returnBm;
    }
    public boolean onKeyDown(int keyCode,KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0){
//            finish();
            openNewPage(personMsg.class);
            System.out.println("530");
            return true;
        }
        return false;

    }
    private void history() {
        if (historyPopup == null) {

            XUISimpleAdapter adapter = XUISimpleAdapter.create(getContext(), listItems);
            historyPopup = new XUIListPopup(getContext(), adapter);
            historyPopup.create(DensityUtils.dp2px(200), DensityUtils.dp2px(150), (adapterView, view, i, l) -> {
                show.setText(listItems[i]);
                Fl=false;
                historyPopup.dismiss();
            });
        }
    }


}
