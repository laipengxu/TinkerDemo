package com.roc.tinkerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.roc.tinkerdemo.retrofit.ApiManager;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadPatch();

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Version 1.0 fixed");
            }
        });
    }

    private void loadPatch() {

        // 本地存放补丁的位置
        final String localFile = getApplicationContext().getExternalCacheDir() + "/hotfix/tinker.patch";
        if (!new File(localFile).getParentFile().exists()) {
            //noinspection ResultOfMethodCallIgnored
            new File(localFile).getParentFile().mkdirs();
        }

        // 补丁名
        String patchFileName = "tinker.patch";

        ApiManager
                .getInstance()
                .downloadVersion(patchFileName)
                .concatMap(new Function<byte[], ObservableSource<String>>() {
                    @Override
                    public ObservableSource<String> apply(final byte[] bytes) throws Exception {
                        return Observable.create(new ObservableOnSubscribe<String>() {
                            @Override
                            public void subscribe(ObservableEmitter<String> emitter) throws Exception {
                                InputStream inputStream = new ByteArrayInputStream(bytes);
                                int len;
                                byte buffer[] = new byte[1024];
                                FileOutputStream outputStream = new FileOutputStream(localFile);
                                while ((len = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, len);
                                    outputStream.flush();
                                }
                                inputStream.close();
                                outputStream.close();
                                emitter.onNext(localFile);
                            }
                        });
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                                   @Override
                                   public void onNext(String patchPath) {
                                       TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), patchPath);
                                       Tinker tinker = Tinker.with(getApplicationContext());
                                       boolean isLoadSuccess = tinker.isTinkerLoaded();
                                       if (isLoadSuccess) {
                                           showToast("加载补丁成功");
                                       } else {
                                           showToast("加载补丁失败");
                                       }
                                   }

                                   @Override
                                   public void onError(Throwable throwable) {

                                   }

                                   @Override
                                   public void onComplete() {
                                   }
                               }
                );
    }

    private void showToast(String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG);
        }else{
            mToast.setText(msg);
        }
        mToast.show();
    }

}
