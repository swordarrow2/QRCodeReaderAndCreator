package com.meng.qrtools.creator;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.meng.MainActivity2;
import com.meng.qrtools.R;
import com.meng.qrtools.lib.ContentHelper;
import com.meng.qrtools.lib.qrcodelib.QrUtils;
import com.meng.qrtools.log;
import com.meng.qrtools.mengViews.mengEdittext;
import com.waynejo.androidndkgif.GifEncoder;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Administrator on 2018/8/23.
 */

public class gifCreator extends Fragment{

    private CheckBox cbAutoSize;
    private mengEdittext mengEtGifHeight;
    private mengEdittext mengEtGifWidth;
    private mengEdittext mengEtFrameDelay;
    private Button btnAddFrame;
    private Button btnFinish;
    private String filePath;
    private HashMap<Integer,Bitmap> dataMap;
    private int bitmapFlag=0;
    private final int CROP_REQUEST_CODE=3;
    private int bmpH;
    private int bmpW;

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
        // TODO: Implement this method
        return inflater.inflate(R.layout.gif_creator,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        // TODO: Implement this method
        super.onViewCreated(view,savedInstanceState);
        cbAutoSize=(CheckBox)view.findViewById(R.id.gif_creator_autosize);
        mengEtGifHeight=(mengEdittext)view.findViewById(R.id.gif_creator_height);
        mengEtGifWidth=(mengEdittext)view.findViewById(R.id.gif_creator_width);
        mengEtFrameDelay=(mengEdittext)view.findViewById(R.id.gif_creator_delay);
        btnAddFrame=(Button)view.findViewById(R.id.gif_creator_add);
        btnFinish=(Button)view.findViewById(R.id.gif_creator_finish);
        btnAddFrame.setOnClickListener(listenerBtnClick);
        btnFinish.setOnClickListener(listenerBtnClick);
        dataMap=new HashMap<Integer,Bitmap>();
        cbAutoSize.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){
                mengEtGifHeight.setVisibility(isChecked?View.GONE:View.VISIBLE);
                mengEtGifWidth.setVisibility(isChecked?View.GONE:View.VISIBLE);
            }
        });
    }

    View.OnClickListener listenerBtnClick=new View.OnClickListener(){


        @Override
        public void onClick(View v){
            switch(v.getId()){
                case R.id.gif_creator_add:
                    MainActivity2.selectImage(gifCreator.this);
                    break;
                case R.id.gif_creator_finish:
                    try{
                        filePath=Environment.getExternalStorageDirectory().getAbsolutePath()+
                                "/Pictures/QRcode/gif"+(new Date()).toString()+".gif";
                        GifEncoder gifEncoder=new GifEncoder();
                        gifEncoder.setDither(false);
                        if(cbAutoSize.isChecked()){
                        //    Bitmap bmp=BitmapFactory.decodeFile(selectedPicturePath);
							bmpW=dataMap.get(0).getWidth();
                            bmpH=dataMap.get(0).getHeight();                
                            gifEncoder.init(
                                    bmpW,
                                    bmpH,
                                    filePath,
                                    GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
                        }else{
                            bmpW=mengEtGifWidth.getInt();
                            bmpH=mengEtGifHeight.getInt();
                            gifEncoder.init(
                                    bmpW,
                                    bmpH,
                                    filePath,
                                    GifEncoder.EncodingType.ENCODING_TYPE_NORMAL_LOW_MEMORY);
                        }

                        for(int i=0;i<bitmapFlag;i++){
                            gifEncoder.encodeFrame(
                                    QrUtils.scale(
                                            dataMap.get(i),
                                            bmpW,
                                            bmpH),
                                    mengEtFrameDelay.getInt());
                        }
                        gifEncoder.close();
                        getActivity().getApplicationContext().sendBroadcast(
                                new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(filePath))));
                        log.t("完成 : "+filePath);
                        dataMap.clear();
                        bitmapFlag=0;
                    }catch(IOException e){
                        log.e(e);
                    }
                    break;
            }
        }
    };

    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode==MainActivity2.SELECT_FILE_REQUEST_CODE&&resultCode==getActivity().RESULT_OK&&data.getData()!=null){
            cropPhoto(data.getData());
        }else if(requestCode==CROP_REQUEST_CODE&&resultCode==getActivity().RESULT_OK){
            Bundle bundle=data.getExtras();
            if(bundle!=null){
                dataMap.put(bitmapFlag,(Bitmap)bundle.getParcelable("data"));
                bitmapFlag++;
                log.t("图片添加成功");
            }else{
                log.t("取消了添加图片");
            }
        }else if(resultCode==getActivity().RESULT_CANCELED){
            log.t("取消选择图片");
        }
        super.onActivityResult(requestCode,resultCode,data);
    }

    private Uri cropPhoto(Uri uri){
        Intent intent=new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri,"image/*");
        intent.putExtra("crop","true");
        intent.putExtra("return-data",true);
        startActivityForResult(intent,CROP_REQUEST_CODE);
        return uri;
    }
}
