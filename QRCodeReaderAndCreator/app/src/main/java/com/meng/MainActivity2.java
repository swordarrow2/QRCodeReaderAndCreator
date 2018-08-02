package com.meng;

import android.app.*;
import android.content.res.*;
import android.os.*;
import android.support.v4.widget.*;
import android.view.*;
import android.widget.*;
import com.meng.qrtools.*;
import com.meng.qrtools.creator.*;
import com.meng.qrtools.lib.materialDesign.*;
import com.meng.qrtools.reader.*;

public class MainActivity2 extends Activity{
	public static MainActivity2 instence;
	public static String logString="这都被发现了(\n感谢岁41发现了一个玄学问题(\n并且已经修正\n大概吧(\n";
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
	private RelativeLayout rt;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerArrowDrawable drawerArrow;
    private boolean drawerArrowColor;

    private welcome welcomeFragment;
    private creator creatorFragment;
    private logoCreator logoCreatorFragment;
    public awesomeCreator awesomeCreatorFragment;
    public cameraReader cameraReaderFragment;
    public galleryReader galleryReaderFragment;
    private about aboutFragment;
	private settings settingsFragment;
	private TextView rightText;
	
	public FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
		instence=this;
        ActionBar ab = getActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);
		manager=getFragmentManager();

        initFragment();

		rt=(RelativeLayout)findViewById(R.id.right_drawer);
		rightText=(TextView)findViewById(R.id.main_activityTextViewRight);
        mDrawerLayout=(DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList=(ListView) findViewById(R.id.navdrawer);
        drawerArrow=new DrawerArrowDrawable(this) {
            @Override
            public boolean isLayoutRtl(){
                return false;
            }
        };
        mDrawerToggle=new ActionBarDrawerToggle(this,mDrawerLayout,
												drawerArrow,R.string.open,
												R.string.close) {

            public void onDrawerClosed(View view){
                super.onDrawerClosed(view);
                invalidateOptionsMenu();
				rightText.setText(logString);
            }

            public void onDrawerOpened(View drawerView){
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
				rightText.setText(logString);
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        String[] values = new String[]{
			"首页(?)",
			"读取相册二维码",
			"相机扫描二维码",
			"创建普通二维码",
			"创建Logo二维码",
			"创建Awesome二维码",
			"关于",
			"设置",
			"退出"
        };
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,android.R.id.text1,values));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent,View view,int position,long id){
                    /*		switch (position) {
					 case 0:
					 mDrawerToggle.setAnimateEnabled(false);
					 drawerArrow.setProgress(1f);
					 break;
					 case 1:
					 mDrawerToggle.setAnimateEnabled(false);
					 drawerArrow.setProgress(0f);
					 break;
					 case 2:
					 mDrawerToggle.setAnimateEnabled(true);
					 mDrawerToggle.syncState();
					 break;
					 case 3:
					 if (drawerArrowColor) {
					 drawerArrowColor = false;
					 drawerArrow.setColor(R.color.ldrawer_color);
					 } else {
					 drawerArrowColor = true;
					 drawerArrow.setColor(R.color.drawer_arrow_second_color);
					 }
					 mDrawerToggle.syncState();
					 break;
					 }
					 */

					switch(position){
						case 0:
							FragmentTransaction transactionWelcome = manager.beginTransaction();
							if(welcomeFragment==null){
								welcomeFragment=new welcome();
								transactionWelcome.add(R.id.main_activityLinearLayout,welcomeFragment);
							}
							hideFragment(transactionWelcome);
							transactionWelcome.show(welcomeFragment);
							transactionWelcome.commit();
							break;
						case 1:
							FragmentTransaction transactionGalleryReaderFragment = manager.beginTransaction();
							if(galleryReaderFragment==null){
								galleryReaderFragment=new galleryReader();
								transactionGalleryReaderFragment.add(R.id.main_activityLinearLayout,galleryReaderFragment);
							}
							hideFragment(transactionGalleryReaderFragment);
							transactionGalleryReaderFragment.show(galleryReaderFragment);
							transactionGalleryReaderFragment.commit();
							break;
						case 2:
							FragmentTransaction transactionCameraReaderFragment = manager.beginTransaction();
							if(cameraReaderFragment==null){
								cameraReaderFragment=new cameraReader();
								transactionCameraReaderFragment.add(R.id.main_activityLinearLayout,cameraReaderFragment,"cameraReader");
							}
							hideFragment(transactionCameraReaderFragment);
							transactionCameraReaderFragment.show(cameraReaderFragment);
							transactionCameraReaderFragment.commit();
							break;
						case 3:
							FragmentTransaction transactionCreatorFragment = manager.beginTransaction();
							if(creatorFragment==null){
								creatorFragment=new creator();
								transactionCreatorFragment.add(R.id.main_activityLinearLayout,creatorFragment);
							}
							hideFragment(transactionCreatorFragment);
							transactionCreatorFragment.show(creatorFragment);
							transactionCreatorFragment.commit();
							break;
						case 4:
							FragmentTransaction transactionLogoCreatorFragment = manager.beginTransaction();
							if(logoCreatorFragment==null){
								logoCreatorFragment=new logoCreator();
								transactionLogoCreatorFragment.add(R.id.main_activityLinearLayout,logoCreatorFragment);
							}
							hideFragment(transactionLogoCreatorFragment);
							transactionLogoCreatorFragment.show(logoCreatorFragment);
							transactionLogoCreatorFragment.commit();
							break;
						case 5:
							FragmentTransaction transactionAwesomeCreatorFragment = manager.beginTransaction();
							if(awesomeCreatorFragment==null){
								awesomeCreatorFragment=new awesomeCreator();
								transactionAwesomeCreatorFragment.add(R.id.main_activityLinearLayout,awesomeCreatorFragment);
							}
							hideFragment(transactionAwesomeCreatorFragment);
							transactionAwesomeCreatorFragment.show(awesomeCreatorFragment);
							transactionAwesomeCreatorFragment.commit();
							break;
						case 6:
							FragmentTransaction transactionAboutFragment = manager.beginTransaction();
							if(aboutFragment==null){
								aboutFragment=new about();
								transactionAboutFragment.add(R.id.main_activityLinearLayout,aboutFragment);
							}
							hideFragment(transactionAboutFragment);
							transactionAboutFragment.show(aboutFragment);
							transactionAboutFragment.commit();
							break;
						case 7:
							FragmentTransaction transactionsettings = manager.beginTransaction();
							if(settingsFragment==null){
								settingsFragment=new settings();
								transactionsettings.add(R.id.main_activityLinearLayout,settingsFragment);
							}
							hideFragment(transactionsettings);
							transactionsettings.show(settingsFragment);
							transactionsettings.commit();
							break;
						case 8:
							if(MainActivity.sharedPreference.getBoolean("exitsettings")){
								System.exit(0);
							}else{
								finish();
							}
							break;
					}
					mDrawerToggle.syncState();
					mDrawerLayout.closeDrawer(mDrawerList);
				}
			});
		if(MainActivity.sharedPreference.getBoolean("opendraw",true)){
			mDrawerLayout.openDrawer(mDrawerList);
		}
		
	}

	private void initFragment(){
		FragmentTransaction transactionWelcome = manager.beginTransaction();
        if(welcomeFragment==null){
            welcomeFragment=new welcome();
            transactionWelcome.add(R.id.main_activityLinearLayout,welcomeFragment);
        }
		if(MainActivity.sharedPreference.getBoolean("ldgr")){
			FragmentTransaction transactionGalleryReaderFragment = manager.beginTransaction();
			if(galleryReaderFragment==null){
				galleryReaderFragment=new galleryReader();
				transactionGalleryReaderFragment.add(R.id.main_activityLinearLayout,galleryReaderFragment);
			}
			hideFragment(transactionGalleryReaderFragment);
			transactionGalleryReaderFragment.commit();
		}
		if(MainActivity.sharedPreference.getBoolean("ldcr")){
			FragmentTransaction transactionCameraReaderFragment = manager.beginTransaction();
			if(cameraReaderFragment==null){
				cameraReaderFragment=new cameraReader();
				transactionCameraReaderFragment.add(R.id.main_activityLinearLayout,cameraReaderFragment,"cameraReader");
			}
			hideFragment(transactionCameraReaderFragment);
			transactionCameraReaderFragment.commit();
		}
		if(MainActivity.sharedPreference.getBoolean("ldqr")){
			FragmentTransaction transactionCreatorFragment = manager.beginTransaction();
			if(creatorFragment==null){
				creatorFragment=new creator();
				transactionCreatorFragment.add(R.id.main_activityLinearLayout,creatorFragment);
			}
			hideFragment(transactionCreatorFragment);
			transactionCreatorFragment.commit();
		}
		if(MainActivity.sharedPreference.getBoolean("ldlgqr")){
			FragmentTransaction transactionLogoCreatorFragment = manager.beginTransaction();
			if(logoCreatorFragment==null){
				logoCreatorFragment=new logoCreator();
				transactionLogoCreatorFragment.add(R.id.main_activityLinearLayout,logoCreatorFragment);
			}
			hideFragment(transactionLogoCreatorFragment);
			transactionLogoCreatorFragment.commit();
		}
		if(MainActivity.sharedPreference.getBoolean("ldaws")){
			FragmentTransaction transactionAwesomeCreatorFragment = manager.beginTransaction();
			if(awesomeCreatorFragment==null){
				awesomeCreatorFragment=new awesomeCreator();
				transactionAwesomeCreatorFragment.add(R.id.main_activityLinearLayout,awesomeCreatorFragment);
			}	
			hideFragment(transactionAwesomeCreatorFragment);
			transactionAwesomeCreatorFragment.commit();
		}
		if(MainActivity.sharedPreference.getBoolean("about")){
			FragmentTransaction transactionAboutFragment = manager.beginTransaction();
			if(aboutFragment==null){
				aboutFragment=new about();
				transactionAboutFragment.add(R.id.main_activityLinearLayout,aboutFragment);
			}
			hideFragment(transactionAboutFragment);
			transactionAboutFragment.commit();
		}
		if(MainActivity.sharedPreference.getBoolean("settings")){
			FragmentTransaction transactionsettings = manager.beginTransaction();
			if(settingsFragment==null){
				settingsFragment=new settings();
				transactionsettings.add(R.id.main_activityLinearLayout,settingsFragment);
			}
			hideFragment(transactionsettings);
			transactionsettings.commit();
		}
		transactionWelcome.show(welcomeFragment);
        transactionWelcome.commit();
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId()==android.R.id.home){
            if(mDrawerLayout.isDrawerOpen(mDrawerList)){
                mDrawerLayout.closeDrawer(mDrawerList);
            }else{
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState){
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onKeyDown(int keyCode,KeyEvent event){
        // TODO: Implement this method
        if(keyCode==KeyEvent.KEYCODE_BACK){
            if(mDrawerLayout.isDrawerOpen(mDrawerList)){
                mDrawerLayout.closeDrawer(mDrawerList);
            }else{
                mDrawerLayout.openDrawer(mDrawerList);
            }
            return true;
        }
        return super.onKeyDown(keyCode,event);
    }


    public void hideFragment(FragmentTransaction transaction){
        if(welcomeFragment!=null){
            transaction.hide(welcomeFragment);
        }
        if(creatorFragment!=null){
            transaction.hide(creatorFragment);
        }
        if(logoCreatorFragment!=null){
            transaction.hide(logoCreatorFragment);
        }
        if(awesomeCreatorFragment!=null){
            transaction.hide(awesomeCreatorFragment);
        }
        if(cameraReaderFragment!=null){
            transaction.hide(cameraReaderFragment);
        }
        if(galleryReaderFragment!=null){
            transaction.hide(galleryReaderFragment);
        }
        if(aboutFragment!=null){
            transaction.hide(aboutFragment);
        }
		if(settingsFragment!=null){
            transaction.hide(settingsFragment);
        }
    }

}

