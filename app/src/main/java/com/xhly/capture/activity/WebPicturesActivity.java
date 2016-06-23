package com.xhly.capture.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.xhly.capture.R;
import com.xhly.capture.adapter.ImageGridAdapter;
import com.xhly.capture.adapter.base.AdapterBase;
import com.xhly.capture.bean.HistoryUrl;
import com.xhly.capture.bean.ImageBean;
import com.xhly.capture.dao.HistoryUrlDao;
import com.xhly.capture.listener.MyCacheCallback;
import com.xhly.capture.listener.OnOptionCallback;
import com.xhly.capture.util.AppUtils;
import com.xhly.capture.util.Constants;
import com.xhly.capture.widget.LoadingView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class WebPicturesActivity extends Activity {

    private LoadingView loadView;
    private RecyclerView rv;

    private ImageView iv_pictures_download;
    private CheckBox cb_pictures_select;
    private TextView tv_pictures_info;

    private String url; //声明为属性：页面url
    private SearchView searchView;

    private List<ImageBean> imageBeans; //所有图片url的集合
    private HashSet<ImageBean> imageUrlSet; //所有图片Url的set集合, 用于过滤
    private ImageGridAdapter adapter;

    private boolean isLoading = false;
    private int selected = 0;
    private HistoryUrlDao dao;
    private List<HistoryUrl> historyUrls;

    private String html;
    private boolean stopDeep = false;
    private int pecentCnt = 0;
    private boolean isDeeped = false;
    private boolean isVertical = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppUtils.keepScreenOn(this);
        setContentView(R.layout.activity_web_pictures);

        initView();
        init();
        initData();
        initAdapter();
        initListener();
    }

    private void initListener() {
        adapter.setOnItemClickLitener(new AdapterBase.OnItemClickLitener() {
            @Override
            public void onItemClick(View view, int position) {
                if (isLoading) {
                    return;
                }

                if (adapter.isEdit()) {
                    reverseAdapterChecked(position);
                } else {
                    Intent intent = new Intent(WebPicturesActivity.this, DragImageActivity.class);
                    intent.putExtra("position", position);
                    intent.putExtra("imageBeans", (ArrayList) imageBeans);
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                if (isLoading) {
                    return;
                }
                if (!adapter.isEdit()) {
                    scanToEdit();
                }
                reverseAdapterChecked(position);
            }
        });

        cb_pictures_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox cb = (CheckBox) v;
                if (cb.isChecked()) {
                    adapter.setAllChecked(true);
                    selected = imageBeans.size();
                } else {
                    adapter.setAllChecked(false);
                    selected = 0;
                }
                adapter.notifyDataSetChanged();
                setTitleText(selected + "/" + imageBeans.size());
            }
        });

        rv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isLoading;
            }
        });

        loadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProgress(false);
                stopDeep = true;
            }
        });
    }

    private void reverseAdapterChecked(int position) {
        imageBeans.get(position).checked = !imageBeans.get(position).checked;
        adapter.notifyDataSetChanged();
        if (imageBeans.get(position).checked) {
            setSelectCnt(true);
        } else {
            setSelectCnt(false);
        }
        setTitleText(selected + "/" + imageBeans.size());
    }

    private void initAdapter() {
        rv.setItemAnimator(new DefaultItemAnimator());
        rv.setLayoutManager(new StaggeredGridLayoutManager(3,
                StaggeredGridLayoutManager.VERTICAL));
        adapter = new ImageGridAdapter(imageBeans);
        rv.setAdapter(adapter);
    }

    private void init() {
        //前面的初始化代码
        url = getIntent().getStringExtra("url");
        url = AppUtils.checkUrlPre(url);//必须调用有返回值的此方法
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
        loadView = (LoadingView) findViewById(R.id.loadView);
        iv_pictures_download = (ImageView) findViewById(R.id.iv_pictures_download);
        cb_pictures_select = (CheckBox) findViewById(R.id.cb_pictures_select);
        tv_pictures_info = (TextView) findViewById(R.id.tv_pictures_info);
        showProgress(true, "\t轻触停止抓取\n正在加载,少侠莫急");
        dao = new HistoryUrlDao();
        historyUrls = dao.getAll();
    }

    private void initData() {
        imageBeans = new ArrayList<>();
        imageUrlSet = new HashSet<>();
        AppUtils.getHttpImages(url, imageBeans, imageUrlSet, new OnOptionCallback() {
            public void afterOption(String html) {
                updateUI(html);
            }
        });
        setTitleText("从网页抓取" + imageBeans.size() + "张图片");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu, menu);
        MenuItem item = menu.findItem(R.id.menu_search);
        MenuItem menu_deep = menu.findItem(R.id.menu_deep);
        if (html == null || Constants.state == Constants.S_SDCARD) {
            menu_deep.setVisible(false);
        } else {
            menu_deep.setVisible(true);
        }
        MenuItem direct = menu.findItem(R.id.menu_direct);

        if(isVertical){
            direct.setTitle("水平");
        }else{
            direct.setTitle("竖直");
        }

        initSearchView(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_history:
                showHistory();
                break;
            case R.id.menu_download:
                webToLocal();
                showDownloadImage();
                break;
            case R.id.menu_deep:
                if (!isDeeped) {
                    isDeeped = true;
                    pecentCnt = 0;
                    deepSearch(html);
                } else {
                    Toast.makeText(WebPicturesActivity.this, "这已经是最大深度了!", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.menu_direct:
                updateDirect();
                break;
        }
        return true;
    }

    private void updateDirect() {
        if(isVertical){
            rv.setLayoutManager(new StaggeredGridLayoutManager(3,
                    StaggeredGridLayoutManager.HORIZONTAL));
        }else{
            rv.setLayoutManager(new StaggeredGridLayoutManager(3,
                    StaggeredGridLayoutManager.VERTICAL));
        }
        adapter.notifyDataSetChanged();
        isVertical = !isVertical;
        invalidateOptionsMenu();
    }

    private void showDownloadImage() {
        imageBeans = AppUtils.getDownloadImages(Constants.SAVE_DIR);
        adapter.setList(imageBeans);
        adapter.notifyDataSetChanged();
        webToLocal();
    }

    private void showHistory() {
        final String[] items = new String[historyUrls.size()];

        for (int i = 0; i < items.length; i++) {
            items[i] = historyUrls.get(i).getUrl();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("查看历史记录");


        if (historyUrls.size() == 0) {
            builder.setItems(new String[]{"没有记录"}, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
        } else {
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AppUtils.getHttpImages(items[which], imageBeans, imageUrlSet, new OnOptionCallback() {
                        public void showProgress() {
                            WebPicturesActivity.this.showProgress(true, "\t轻触停止抓取\n正在加载,少侠莫急");
                        }

                        public void dismissProgress() {
                            WebPicturesActivity.this.showProgress(false);
                        }

                        public void afterOption(String html) {
                            updateUI(html);
                        }
                    });
                }
            });
        }
        builder.show();
    }

    private void initSearchView(MenuItem item) {
        searchView = (SearchView) item.getActionView();
        //设置searchView属性
        searchView.setSubmitButtonEnabled(true);
        searchView.setQueryHint("请输入网址");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String url) {
                url = AppUtils.checkUrlPre(url);//必须调用有返回值的此方法
                WebPicturesActivity.this.url = url;
                isDeeped = false;
                AppUtils.getHttpImages(url, imageBeans, imageUrlSet, new OnOptionCallback() {
                    public void showProgress() {
                        WebPicturesActivity.this.showProgress(true, "\t轻触停止抓取\n正在加载,少侠莫急");
                    }

                    public void dismissProgress() {
                        WebPicturesActivity.this.showProgress(false);
                    }

                    public void afterOption(String html) {
                        updateUI(html);
                    }
                });

                addToHistory(url);
                // 清除焦点, 软键盘收回
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.isFocused()) {
                    //设置actionbar
                    getActionBar().setDisplayHomeAsUpEnabled(true);
                } else {
                    getActionBar().setDisplayHomeAsUpEnabled(false);
                }
            }
        });
    }

    private void updateUI(String html) {
        adapter.setList(imageBeans);
        adapter.notifyDataSetChanged();
        selected = 0;
        setTitleText("从网页抓取" + imageBeans.size() + "张图片");
        WebPicturesActivity.this.showProgress(false);
        WebPicturesActivity.this.html = html;
        invalidateOptionsMenu();
    }

    private void addToHistory(String url) {
        dao.save(new HistoryUrl(0, url));
        historyUrls.add(new HistoryUrl(0, url));
    }

    private void showProgress(boolean isShow, String... string) {

        isLoading = isShow;
        if (isLoading) {
            loadView.setVisibility(View.VISIBLE);
            loadView.setLoadingText(string[0]);
        } else {
            loadView.setVisibility(View.GONE);
        }
    }

    private void setSelectCnt(boolean isAdd) {
        if (isAdd)
            selected++;
        else
            selected--;
        if (selected == imageBeans.size()) {
            cb_pictures_select.setChecked(true);
        }
        if (selected != imageBeans.size()) {
            cb_pictures_select.setChecked(false);
        }
    }

    private void setOptBar(boolean isShow) {
        if (!isShow) {
            iv_pictures_download.setVisibility(View.GONE);
            cb_pictures_select.setVisibility(View.GONE);
        } else {
            iv_pictures_download.setVisibility(View.VISIBLE);
            cb_pictures_select.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (adapter.isEdit()) {
                editToScan();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    private void editToScan() {
        adapter.setEdit(false);
        adapter.setAllChecked(false);
        setOptBar(false);
        selected = 0;
        if (Constants.state == Constants.S_WEB) {
            setTitleText("从网页抓取" + imageBeans.size() + "张图片");
        } else {
            tv_pictures_info.setText("从本地获取" + imageBeans.size() + "张图片");
        }
    }

    private void scanToEdit() {
        adapter.setEdit(true);
        setOptBar(true);
    }

    private void webToLocal() {
        Constants.state = Constants.S_SDCARD;
        iv_pictures_download.setImageResource(R.drawable.op_del_press);
        tv_pictures_info.setText("从本地获取" + imageBeans.size() + "张图片");
        updateDirect();
       // invalidateOptionsMenu();
    }

    private void localToWeb() {
        Constants.state = Constants.S_WEB;
        iv_pictures_download.setImageResource(R.drawable.icon_s_download_press);
    }

    public void dowmOrDelImage(View v) {

        if (Constants.state == Constants.S_WEB) {
            for (ImageBean ib : imageBeans) {
                if (ib.checked) {
                    AppUtils.downLoadImage(ib.url);
                }
            }
            AppUtils.showToast("下载完成!");
        } else {
            for (int i = 0; i < imageBeans.size(); i++) {
                ImageBean imageBean = imageBeans.get(i);
                if (imageBean.checked) {
                    // 当前图片需要删除
                    File file = new File(imageBean.url);
                    if (file.exists()) {
                        file.delete();// 删除
                    }
                    imageBeans.remove(i);
                    i--;
                }
            }
            adapter.notifyDataSetChanged();
            AppUtils.showToast("所选图片删除完毕!");
        }
        editToScan();
    }

    private void setTitleText(String string) {
        tv_pictures_info.setText(string);
    }

    private void deepSearch(String html) {
        Document doc = Jsoup.parse(html);// 解析HTML页面
        // 获取页面中的所有连接
        Elements links = doc.select("a[href]");
        final List<String> useLinks = AppUtils.getUseableLinks(url, links);// 过滤
        showProgress(true, "轻触停止抓取\n" +
                "正在加载,0" + "/" + useLinks.size());
        for (int i = 0; i < useLinks.size(); i++) {
            final String href = useLinks.get(i);
            RequestParams params = new RequestParams(href);
            params.setConnectTimeout(2000);
            x.http().get(params, new MyCacheCallback<String>() {
                @Override
                public void onSuccess(String html) {
                    if (stopDeep) {
                        return;
                    }
                    AppUtils.showImagesFromHtml(href, html, imageBeans, imageUrlSet);
                    updateProgress(useLinks.size());
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    if (stopDeep) {
                        return;
                    }
                    updateProgress(useLinks.size());
                }
            });
        }
    }

    public void updateProgress(int total) {
        int percent = 0;

        pecentCnt++;
        if (pecentCnt >= total) {
            percent = 100;
            showProgress(false);
            pecentCnt = 0;
        } else {
            percent = pecentCnt * 100 / total;
            showProgress(true, "轻触停止抓取\n" +
                    "正在加载," + percent + "%");
        }

        setTitleText("从网页抓取" + imageBeans.size() + "张图片");
    }
}
