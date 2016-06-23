package com.xhly.capture.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.xhly.capture.R;
import com.xhly.capture.bean.ImageBean;
import com.xhly.capture.listener.MyCacheCallback;
import com.xhly.capture.listener.OnOptionCallback;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xutils.common.util.FileUtil;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by 新火燎塬 on 2016/6/20. 以及  on 11:34!^-^
 */
public final class AppUtils {
    public static Context context;
    public static ImageOptions smallImageOptions;
    public static ImageOptions bigImageOptions;
    static {
        smallImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.CENTER) //等比例放大/缩小到充满长/宽居中显示
                .setLoadingDrawableId(R.drawable.default_image)
                .setFailureDrawableId(R.drawable.default_image)
                        .setConfig(Bitmap.Config.RGB_565)
                .build();

        bigImageOptions = new ImageOptions.Builder()
                .setImageScaleType(ImageView.ScaleType.FIT_CENTER)//等比例缩小到充满长/宽居中显示, 或原样显示
                .setLoadingDrawableId(R.drawable.default_image)
                .setFailureDrawableId(R.drawable.default_image)
                .setConfig(Bitmap.Config.ARGB_8888)
                .build();
    }

    public static Context getContext() {
        return context;
    }

    public static void initApp(Context context) {
        AppUtils.context = context;
    }


    public static void keepScreenOn(Activity activity) {
        // 保持屏幕常亮
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
        );
    }

    /**
     * 检查<img>中src属性值
     *
     * @param url
     * @param src
     * @return
     */
    public static String checkSrc(String url, String src) {
        if (src.startsWith("http")) {
            url = src;
        } else {
            if (src.startsWith("/")) {
                url = url + src;
            } else {
                url = url + "/" + src;
            }
        }
        return url;
    }

    public static String checkUrlPre(String url) {
        if (!url.startsWith("http")) {
            url = "http://" + url;
        }
        return url;
    }

    /**
     * 获取SDcard根路径
     *
     * @return
     */
    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取网址中的图片名称
     *
     * @param url
     * @return
     */
    public static String cutImagePath(String url) {
        String res = "";
        int start = url.lastIndexOf("/") + 1;
        res = url.substring(start);
        return res;
    }

    public static void showToast(String string){
        Toast.makeText(AppUtils.getContext(), string, Toast.LENGTH_SHORT).show();
    }

    public static void getHttpImages(final String url, final List imageBeans, final Set imageUrlSet, final OnOptionCallback onOptionCallback) {
        //保存当前状态
        Constants.state = Constants.S_WEB;
        //显示pd
        //showProgressDialog();
        onOptionCallback.showProgress();
        //发送请求
        x.http().get(new RequestParams(url), new MyCacheCallback<String>() {
            /**
             * 请求成功返回
             * @param html 网页文本
             */
            @Override
            public void onSuccess(String html) {//成功得到网页html格式字符串
                //先清理以前的集合数据
                imageBeans.clear();
                imageUrlSet.clear();
                //显示网页中包含的所有图片
                showImagesFromHtml(url, html, imageBeans, imageUrlSet);
                //移除pd
                //dismissProgressDialog();
                onOptionCallback.dismissProgress();
                onOptionCallback.afterOption(html);

                //显示深度抓取提醒
                //showDeepDailog(html);
                //tv_pictures_info.setText("从网页抓取" + imageBeans.size() + "张图片");
            }

            /**
             * 请求失败
             * @param ex
             * @param isOnCallback
             */
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showToast("拾取图片失败!");
                onOptionCallback.dismissProgress();
            }
        });

    }


    /**
     * 解析html请求图片显示
     *
     * @param html
     */
    public static void showImagesFromHtml(String url, String html, List imageBeans, Set imageUrlSet) {
        List<ImageBean> list = parseHtml(url, html,imageUrlSet);
        imageBeans.addAll(list);
//        adapter.setList(imageBeans);
//        selectCount = 0;
    }

    /**
     * 解析网页文本, 找出其中所有图片信息对象的集合
     *
     * @param url
     * @param html
     * @return
     */
    public static List<ImageBean> parseHtml(String url, String html,Set imageUrlSet) {
        List<ImageBean> list = new ArrayList<>();
        Document doc = Jsoup.parse(html);
        List<Element> imgs = doc.getElementsByTag("img");
        for (Element img : imgs) {
            String src = img.attr("src");
            if (src.toLowerCase().endsWith("jpg") || src.toLowerCase().endsWith("png")) {
                src = checkSrc(url, src);
                ImageBean imageBean = new ImageBean(src);
                if (!imageUrlSet.contains(imageBean) && src.indexOf("/../") == -1) {
                    imageUrlSet.add(imageBean);
                    list.add(imageBean);
                }
            }
        }
        return list;
    }

    public static void downLoadImage(final String url) {
        // 下载文件夹
        File fdir = new File(Constants.SAVE_DIR);
        // 创建文件夹
        if (!fdir.exists()) {
            fdir.mkdirs();
        }
        final String filePath = Constants.SAVE_DIR + "/" + System.currentTimeMillis() + AppUtils.cutImagePath(url);
        RequestParams params = new RequestParams(url);
        Log.e("TAG", "url=" + url);
        params.setConnectTimeout(5000);
        //params.setSaveFilePath(filePath);//指定图片保存的路径
        x.http().get(params, new MyCacheCallback<File>() {

            //从内存缓存中保存图片file
            @Override
            public boolean onCache(File result) {
                Log.e("TAG", "onCache() " + result.getAbsolutePath());
                FileUtil.copy(result.getAbsolutePath(), filePath);
                //updateDownImageProgress();
                return true;
            }

            //远程请求保存图片File
            @Override
            public void onSuccess(File result) {
                Log.e("TAG", "onSucess() " + result.getAbsolutePath());
                FileUtil.copy(result.getAbsolutePath(), filePath);
                //updateDownImageProgress();
            }

            //请求失败
            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                super.onError(ex, isOnCallback);
                Log.e("TAG", "onError() " + ex.getMessage());
                AppUtils.showToast("图片" + url + "下载失败!");

            }
        });
    }


    /* 获取文件中的所有图片的绝对路径
*/
    public static List<ImageBean> getDownloadImages(String dir) {
        List<ImageBean> list = new ArrayList<ImageBean>();
        File fdir = new File(dir);
        File[] files = fdir.listFiles();
        if (files != null) {
            // 遍历
            for (int i = 0; i < files.length; i++) {
                list.add(new ImageBean(files[i].getAbsolutePath()));// 绝对路径
            }
        }
        return list;
    }



    /**
     * 过滤出有效链接
     *
     * @param links
     * @return
     */
    public static List<String> getUseableLinks(String url,Elements links) {
        //用于过滤重复url的集合
        HashSet<String> set = new HashSet<String>();
        //用于保存有效url的集合
        List<String> lstLinks = new ArrayList<String>();

        //遍历所有links,过滤,保存有效链接
        for (Element link : links) {
            String href = link.attr("href");// abs:href, "http://"
            //Log.i("spl","过滤前,链接:"+href);
            // 设置过滤条件
            if (href.equals("")) {
                continue;// 跳过
            }
            if (href.equals(url)) {
                continue;// 跳过
            }
            if (href.startsWith("javascript")) {
                continue;// 跳过
            }

            if (href.startsWith("/")) {
                href = url + href;
            }
            if (!set.contains(href)) {
                set.add(href);// 将有效链接保存至哈希表中
                lstLinks.add(href);
            }
        }
        return lstLinks;
    }
}