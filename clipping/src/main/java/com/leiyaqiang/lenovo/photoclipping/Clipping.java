package com.leiyaqiang.lenovo.photoclipping;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.widget.ImageView;
import android.widget.Toast;

import com.kevin.crop.UCrop;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by lenovo on 2017/9/28.
 */

public class Clipping {

    private static ImageView imageView;
    private static Uri mDestinationUri;
    // 拍照临时图片
    private static String mTempPhotoPath;
    private static Activity contex;


    protected static final int REQUEST_STORAGE_READ_ACCESS_PERMISSION = 101;
    protected static final int REQUEST_STORAGE_WRITE_ACCESS_PERMISSION = 102;

    private static final int GALLERY_REQUEST_CODE = 0;    // 相册选图标记
    private static final int CAMERA_REQUEST_CODE = 1;    // 相机拍照标记

    private static android.support.v7.app.AlertDialog mAlertDialog;

    private static getImg getImg;


    /**
     * 图片选择的监听回调
     */
    private static OnPictureSelectedListener mOnPictureSelectedListener;

    public interface getImg{
        void img(String fileUri, Bitmap bitmap);
    }

    public static void selectPicture(ImageView view, Activity context){
        imageView = view;
        contex = context;
        getImg = (Clipping.getImg) context;

        mDestinationUri = Uri.fromFile(new File(contex.getCacheDir(), "photo" + (int) (Math.random() * 10000000) + "cropImage.jpeg"));
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpeg";

        showSelectPictureDialog();


        // 设置裁剪图片结果监听
        setOnPictureSelectedListener(new OnPictureSelectedListener() {
            @Override
            public void onPictureSelected(Uri fileUri, Bitmap bitmap) {
//                transferimage.setImageBitmap(bitmap);
                imageView.setImageBitmap(bitmap);
                String filePath = fileUri.getEncodedPath();
                String imagePath = Uri.decode(filePath);
//                transferbar.setVisibility(View.VISIBLE);
//                upLoad2Upyun(imagePath);
                getImg.img(imagePath,bitmap);

            }
        });
    }

    /**
     *  拍照 图库 弹框
     */
    private static void showSelectPictureDialog() {
        new ActionSheetDialog(contex)
                .builder()
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .addSheetItem("拍照",
                        ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(int which) {
                                //填写事件
                                takePhoto();
                            }
                        })
                .addSheetItem("图库",
                        ActionSheetDialog.SheetItemColor.Blue,
                        new ActionSheetDialog.OnSheetItemClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onClick(int which) {
                                //填写事件
                                pickFromGallery();
                            }
                        })
                .show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(contex, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.CAMERA,
                    "拍照时需要存储权限",
                    REQUEST_STORAGE_WRITE_ACCESS_PERMISSION);
        } else {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //下面这句指定调用相机拍照后的照片存储的路径
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mTempPhotoPath)));
            contex.startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private static void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(contex, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    "选择图片时需要读取权限",
                    REQUEST_STORAGE_READ_ACCESS_PERMISSION);
        } else {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
            // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            contex.startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
        }

    }


    /**
     * 请求权限
     * <p>
     * 如果权限被拒绝过，则提示用户需要权限
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    protected static void requestPermission(final String permission, String rationale, final int requestCode) {
        if (contex.shouldShowRequestPermissionRationale(permission)) {
            showAlertDialog("权限需求", rationale,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            contex.requestPermissions(new String[]{permission}, requestCode);
                        }
                    }, "确定", null, "取消");
        } else {
            contex.requestPermissions(new String[]{permission}, requestCode);
        }
    }

    /**
     * 显示指定标题和信息的对话框
     *
     * @param title                         - 标题
     * @param message                       - 信息
     * @param onPositiveButtonClickListener - 肯定按钮监听
     * @param positiveText                  - 肯定按钮信息
     * @param onNegativeButtonClickListener - 否定按钮监听
     * @param negativeText                  - 否定按钮信息
     */
    protected static void showAlertDialog(@Nullable String title, @Nullable String message,
                                          @Nullable DialogInterface.OnClickListener onPositiveButtonClickListener,
                                          @NonNull String positiveText,
                                          @Nullable DialogInterface.OnClickListener onNegativeButtonClickListener,
                                          @NonNull String negativeText) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(contex);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(positiveText, onPositiveButtonClickListener);
        builder.setNegativeButton(negativeText, onNegativeButtonClickListener);
        mAlertDialog = builder.show();
    }

    public static void callback(int requestCode, int resultCode, Intent data){
        if (resultCode == contex.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:   // 调用相机拍照
                    File temp = new File(mTempPhotoPath);
                    startCropActivity(Uri.fromFile(temp));
                    break;
                case GALLERY_REQUEST_CODE:  // 直接从相册获取
                    startCropActivity(data.getData());
                    break;
                case UCrop.REQUEST_CROP:    // 裁剪图片结果
                    handleCropResult(data);
                    break;
                case UCrop.RESULT_ERROR:    // 裁剪图片错误
                    handleCropError(data);
                    break;
            }
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public static void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                .withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .withTargetActivity(CropActivity.class)
                .start(contex);
    }


    /**
     * 处理剪切成功的返回值
     *
     * @param result
     */
    private static void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        final Uri resultUri = UCrop.getOutput(result);
        if (null != resultUri && null != mOnPictureSelectedListener) {
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(contex.getContentResolver(), resultUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mOnPictureSelectedListener.onPictureSelected(resultUri, bitmap);
        } else {
            Toast.makeText(contex, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理剪切失败的返回值
     *
     * @param result
     */
    private static void handleCropError(Intent result) {
        deleteTempPhotoFile();
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
//            Log.e(TAG, "handleCropError: ", cropError);
//            Toast.makeText(getActivity(), cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
//            Toast.makeText(getActivity(), "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除拍照临时文件
     */
    private static void deleteTempPhotoFile() {
        File tempFile = new File(mTempPhotoPath);
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }


    /**
     * 图片选择的回调接口
     */
    public interface OnPictureSelectedListener {
        /**
         * 图片选择的监听回调
         *
         * @param fileUri
         * @param bitmap
         */
        void onPictureSelected(Uri fileUri, Bitmap bitmap);
    }


    /**
     * 设置图片选择的回调监听
     *
     * @param l
     */
    public static void setOnPictureSelectedListener(OnPictureSelectedListener l) {
        mOnPictureSelectedListener = l;
    }
}
