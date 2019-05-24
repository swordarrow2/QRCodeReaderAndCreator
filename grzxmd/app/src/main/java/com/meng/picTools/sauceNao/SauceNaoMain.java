package com.meng.picTools.sauceNao;

import android.app.*;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.*;
import android.support.annotation.*;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import com.meng.picTools.*;
import com.meng.picTools.helpers.ContentHelper;
import com.meng.picTools.helpers.SharedPreferenceHelper;
import com.meng.picTools.lib.MaterialDesign.*;
import com.meng.picTools.qrCode.creator.LogoQRCreator;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SauceNaoMain extends Fragment {
    private FloatingActionButton mFab;
    private FloatingActionButton mFabSelect;
    private ListView listView;
    public HashMap<String, Bitmap> hashMap = new HashMap<>();
    public ExecutorService threadPool;
    public String fileAbsPath;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.saucenao_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFab = (FloatingActionButton) view.findViewById(R.id.fab_upload);
        mFabSelect = (FloatingActionButton) view.findViewById(R.id.fab_select);
        listView = (ListView) view.findViewById(R.id.list);
        threadPool = Executors.newFixedThreadPool(Integer.parseInt(SharedPreferenceHelper.getValue("threads", "3")));
        mFab.setOnClickListener(onClickListener);
        mFabSelect.setOnClickListener(onClickListener);
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.fab_start_download:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            FileInputStream fInputStream;
                            try {
                                fInputStream = new FileInputStream(new File(fileAbsPath));
                                Connection.Response response = Jsoup.connect("https://saucenao.com/search.php?db=" + 999)
                                        .timeout(60000).data("file", "image.jpg", fInputStream).method(Connection.Method.POST).execute();
                                if (response.statusCode() != 200) {
                                    LogTool.e("发生错误" + response.statusCode());
                                    return;
                                }
                                PicResults mResults = new PicResults(Jsoup.parse(response.body()));
                                ResultAdapter resultAdapter = new ResultAdapter(getActivity(), mResults.getResults());
                                listView.setAdapter(resultAdapter);
                       /*         int size = mResults.getResults().size();
                                if (size < 1) {
                                    LogTool.t("没有相似度较高的图片");
                                }
                                for (int i = 0; i < size; i++) {
                                    StringBuilder sBuilder = new StringBuilder("");
                                    PicResults.Result tmpr = mResults.getResults().get(i);
                                    File dFile = null;
                                    File files = new File(Autoreply.appDirectory + "picSearch\\tmp\\");
                                    if (!files.exists()) {
                                        files.mkdirs();
                                    }
                                    URL url = new URL(tmpr.mThumbnail);
                                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                                    connection.setConnectTimeout(60000);
                                    InputStream is = connection.getInputStream();
                                    dFile = new File(Autoreply.appDirectory + "picSearch\\tmp\\",
                                            Autoreply.instence.random.nextInt() + picNumFlag++ + "pic.jpg");
                                    FileOutputStream out = new FileOutputStream(dFile);
                                    int ii = 0;
                                    while ((ii = is.read()) != -1) {
                                        out.write(ii);
                                    }
                                    out.close();
                                    is.close();
                                    String[] titleAndMetadata = tmpr.mTitle.split("\n", 2);
                                    if (titleAndMetadata.length > 0) {
                                        sBuilder.append("\n").append(titleAndMetadata[0]).append("\n");
                                        if (titleAndMetadata.length == 2) {
                                            tmpr.mColumns.add(0, titleAndMetadata[1]);
                                        }
                                        for (String string : tmpr.mColumns) {
                                            sBuilder.append(string).append("\n");
                                        }
                                    }
                                    sBuilder.append(Autoreply.instence.CC.image(dFile)).append("\n");
                                    if (tmpr.mExtUrls.size() == 2) {
                                        sBuilder.append("图片&画师:").append(tmpr.mExtUrls.get(1)).append("\n");
                                        sBuilder.append(tmpr.mExtUrls.get(0)).append("\n");
                                    } else if (tmpr.mExtUrls.size() == 1) {
                                        sBuilder.append("链接:").append(tmpr.mExtUrls.get(0)).append("\n");
                                    }
                                    if (!tmpr.mSimilarity.isEmpty()) {
                                        sBuilder.append("相似度:").append(tmpr.mSimilarity);
                                    }
                                    String tmp = sBuilder.toString().isEmpty() ? "没有相似度较高的图片" : sBuilder.toString();
                                    Methods.sendMsg(fromGroup, fromQQ, tmp.contains("sankakucomplex") ? tmp + "\n小哥哥注意身体哦" : tmp);
                                }*/
                            } catch (Exception e) {

                            }
                        }
                    }).start();
                    break;
                case R.id.fab_select:
                    MainActivity.instence.selectImage(SauceNaoMain.this);
                    break;
            }
        }
    };

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFab.hide(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mFab.show(true);
                mFab.setShowAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_bottom));
                mFab.setHideAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.hide_to_bottom));
            }
        }, 300);
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
        startActivityForResult(intent, MainActivity.instence.CROP_REQUEST_CODE);
        return uri;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MainActivity.instence.SELECT_FILE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data.getData() != null) {
            String path = ContentHelper.absolutePathFromUri(getActivity().getApplicationContext(), cropPhoto(data.getData(), cbCrop.isChecked()));
            tvImgPath.setText("当前图片：" + path);
            if (!cbCrop.isChecked()) {
                logoImage = BitmapFactory.decodeFile(path);
            }
        } else if (requestCode == MainActivity.instence.CROP_REQUEST_CODE && resultCode == getActivity().RESULT_OK) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                logoImage = bundle.getParcelable("data");
                LogTool.t("图片添加成功");
            } else {
                LogTool.t("取消了添加图片");
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
            Toast.makeText(getActivity().getApplicationContext(), "取消选择图片", Toast.LENGTH_SHORT).show();
        } else {
            MainActivity.instence.selectImage(this);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
