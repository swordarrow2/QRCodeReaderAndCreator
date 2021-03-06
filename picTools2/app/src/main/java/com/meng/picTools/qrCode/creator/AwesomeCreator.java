package com.meng.picTools.qrCode.creator;

import android.*;
import android.app.*;
import android.content.*;
import android.content.pm.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.support.v4.app.*;
import android.view.*;
import android.widget.*;

import com.meng.picTools.*;
import com.meng.picTools.libAndHelper.ContentHelper;
import com.meng.picTools.libAndHelper.FileHelper;
import com.meng.picTools.libAndHelper.FileType;
import com.meng.picTools.libAndHelper.AwesomeQRCode;
import com.meng.picTools.libAndHelper.mengViews.*;

import java.io.*;
import java.text.*;

import android.app.Fragment;

import com.meng.picTools.R;

public class AwesomeCreator extends Fragment {

    private ImageView qrCodeImageView;
    private MengEditText mengEtDotScale, mengEtContents, mengEtMargin, mengEtSize;
    private CheckBox ckbWhiteMargin;
    private Bitmap backgroundImage = null;

    private boolean generating = false;
    private CheckBox ckbAutoColor;
    private ScrollView scrollView;
    private CheckBox ckbBinarize;
    private CheckBox cbCrop;
    private MengEditText mengEtBinarize;
    private Button btnSave;
    private TextView imgPathTextView;
    private Bitmap bmpQRcode = null;
    private MengColorBar mColorBar;

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.awesomeqr_main, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mColorBar = (MengColorBar) view.findViewById(R.id.gif_arb_qr_main_colorBar);
        scrollView = (ScrollView) view.findViewById(R.id.awesomeqr_main_scrollView);
        qrCodeImageView = (ImageView) view.findViewById(R.id.awesomeqr_main_qrcode);
        mengEtContents = (MengEditText) view.findViewById(R.id.awesomeqr_main_content);
        mengEtSize = (MengEditText) view.findViewById(R.id.awesomeqr_main_mengEdittext_size);
        mengEtMargin = (MengEditText) view.findViewById(R.id.awesomeqr_main_margin);
        mengEtDotScale = (MengEditText) view.findViewById(R.id.awesomeqr_main_dotScale);
        ckbWhiteMargin = (CheckBox) view.findViewById(R.id.awesomeqr_main_whiteMargin);
        ckbAutoColor = (CheckBox) view.findViewById(R.id.awesomeqr_main_autoColor);
        ckbBinarize = (CheckBox) view.findViewById(R.id.awesomeqr_main_binarize);
        mengEtBinarize = (MengEditText) view.findViewById(R.id.awesomeqr_main_mengEdittext_binarizeThreshold);
        btnSave = (Button) view.findViewById(R.id.awesomeqr_mainButton_save);
        imgPathTextView = (TextView) view.findViewById(R.id.awesomeqr_main_imgPathTextView);
        cbCrop = (CheckBox) view.findViewById(R.id.awesomeqr_main_crop);
        ckbAutoColor.setOnCheckedChangeListener(check);
        ckbBinarize.setOnCheckedChangeListener(check);
        ((Button) view.findViewById(R.id.awesomeqr_main_backgroundImage)).setOnClickListener(click);
        ((Button) view.findViewById(R.id.awesomeqr_main_removeBackgroundImage)).setOnClickListener(click);
        ((Button) view.findViewById(R.id.awesomeqr_main_generate)).setOnClickListener(click);
        btnSave.setOnClickListener(click);
    }

    CompoundButton.OnCheckedChangeListener check = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.awesomeqr_main_autoColor:
                    mColorBar.setVisibility(isChecked ? View.GONE : View.VISIBLE);
                    if (!isChecked) LogTool.t("如果颜色搭配不合理,二维码将会难以识别");
                    break;
                case R.id.awesomeqr_main_binarize:
                    mengEtBinarize.setEnabled(isChecked);
                    break;
            }
        }
    };

    View.OnClickListener click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.awesomeqr_main_backgroundImage:
                    MainActivity2.instence.selectImage(AwesomeCreator.this);
                    break;
                case R.id.awesomeqr_main_removeBackgroundImage:
                    backgroundImage = null;
                    imgPathTextView.setVisibility(View.GONE);
                    LogTool.t(getResources().getString(R.string.Background_image_removed));
                    break;
                case R.id.awesomeqr_main_generate:
                    generate(mengEtContents.getString(),
                            mengEtSize.getInt(),
                            mengEtMargin.getInt(),
                            Float.parseFloat(mengEtDotScale.getString()),
                            mColorBar.getTrueColor(),
                            ckbAutoColor.isChecked() ? Color.WHITE : mColorBar.getFalseColor(),
                            backgroundImage,
                            ckbWhiteMargin.isChecked(),
                            ckbAutoColor.isChecked(),
                            ckbBinarize.isChecked(),
                            mengEtBinarize.getInt()
                    );
                    btnSave.setVisibility(View.VISIBLE);
                    break;
                case R.id.awesomeqr_mainButton_save:
                    String s = FileHelper.saveBitmap(bmpQRcode, FileType.awesomeQR);
                    if (s == null) {
                        LogTool.e("保存出错");
                        break;
                    }
                    LogTool.t("已保存至" + s);
                    getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(s))));//更新图库
                    break;
            }
        }
    };

    public void setDataStr(String s) {
        mengEtContents.setString(s);
    }

    @Override
    public void onResume() {
        super.onResume();
        acquireStoragePermissions();
    }


    private void acquireStoragePermissions() {
        int permission = ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    getActivity(),
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private Uri cropPhoto(Uri uri, boolean needCrop) {
        if (!needCrop) return uri;
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, MainActivity2.instence.CROP_REQUEST_CODE);
        return uri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity2.instence.SELECT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data.getData() != null) {
            imgPathTextView.setVisibility(View.VISIBLE);
            String path = ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(), cropPhoto(data.getData(), cbCrop.isChecked()));
            imgPathTextView.setText(MessageFormat.format("当前图片：{0}", path));
            if (!cbCrop.isChecked()) {
                backgroundImage = BitmapFactory.decodeFile(path);
            }
        } else if (requestCode == MainActivity2.instence.CROP_REQUEST_CODE) {
            Bundle bundle = data.getExtras();
            if (bundle == null) {
                LogTool.t("bundle is null");
            }
            if (bundle != null) {
                backgroundImage = bundle.getParcelable("data");
                LogTool.t(getResources().getString(R.string.Background_image_added));
            } else {
                LogTool.t("取消了添加图片");
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            LogTool.t("取消选择图片");
        } else {
            MainActivity2.instence.selectImage(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void generate(final String contents, final int size, final int margin, final float dotScale,
                          final int colorDark, final int colorLight, final Bitmap background, final boolean whiteMargin,
                          final boolean autoColor, final boolean binarize, final int binarizeThreshold) {
        if (generating) return;
        generating = true;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap b = AwesomeQRCode.create(contents, size, margin, dotScale, colorDark, colorLight, background, whiteMargin, autoColor, binarize, binarizeThreshold);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            qrCodeImageView.setImageBitmap(b);
                            bmpQRcode = b;
                            scrollView.post(new Runnable() {
                                @Override
                                public void run() {
                                    scrollView.fullScroll(View.FOCUS_DOWN);
                                }
                            });
                            generating = false;
                        }
                    });
                } catch (Exception e) {
                    LogTool.e(e);
                    generating = false;
                }
            }
        }).start();
    }
}
