package com.meng.qrtools.creator;
import android.app.*;
import android.content.*;
import android.graphics.*;
import android.net.*;
import android.os.*;
import android.view.*;
import android.view.View.*;
import android.widget.*;
import com.meng.qrtools.*;
import java.io.*;
import com.google.zxing.*;

public class creator extends Fragment{
	ImageView qrcode1;
	EditText et;
	Button btn;
	Button btnSave;
	private Bitmap b;
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState){
		// TODO: Implement this method
		return inflater.inflate(R.layout.qr_main,container,false);
	}

	@Override
	public void onViewCreated(View view,Bundle savedInstanceState){
		// TODO: Implement this method
		super.onViewCreated(view,savedInstanceState);
		qrcode1=(ImageView)view. findViewById(R.id.qrcode5);
		et=(EditText)view.findViewById(R.id.qr_mainEditText);
		btn=(Button)view.findViewById(R.id.qr_mainButton);
		btnSave=(Button)view.findViewById(R.id.qr_mainButtonSave);
		btn.setOnClickListener(new OnClickListener(){	

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					 b=QRCode.createQRCode(et.getText().toString()==null||et.getText().toString().equals("")?et.getHint().toString():et.getText().toString(),BarcodeFormat.QR_CODE);
					qrcode1.setImageBitmap(b);
					btnSave.setVisibility(View.VISIBLE);
				}
			});		
		btnSave.setOnClickListener(new OnClickListener(){

				@Override
				public void onClick(View p1){
					// TODO: Implement this method
					try{
						String s= QRCode.saveMyBitmap(Environment.getExternalStorageDirectory().getAbsolutePath()+"/Pictures/QRcode/QR"+SystemClock.elapsedRealtime()+".png",b);
						Toast.makeText(getActivity().getApplicationContext(),"已保存至"+s,Toast.LENGTH_LONG).show();
						getActivity().getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,Uri.fromFile(new File(s))));//更新图库
					}catch(IOException e){
						Toast.makeText(getActivity().getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
					}
				}
			});
	}




}
