package com.apliant.shact.fragments;

import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.apliant.shact.R;
import com.mikepenz.iconics.context.IconicsContextWrapper;
import com.mikepenz.iconics.context.IconicsLayoutInflater;

import net.bozho.easycamera.DefaultEasyCamera;
import net.bozho.easycamera.EasyCamera;

import java.io.IOException;

/**
 * Created by rafa93br on 07/01/2016.
 */
public class SelfieFragment extends Fragment {
    SurfaceView mSurfaceView;
    public SurfaceHolder mSurfaceHolder;

    public EasyCamera getmCamera() {
        return mCamera;
    }

    public void setmCamera(EasyCamera mCamera) {
        this.mCamera = mCamera;
    }

    EasyCamera mCamera;
    EasyCamera.CameraActions mActions;

    /**
     * Create a new instance of DetailsFragment, initialized to
     * show the text at 'index'.
     */
    public static SelfieFragment newInstance(int index) {
        Log.i("TAG", "OI DO FRAGMENT");
        SelfieFragment f = new SelfieFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putInt("index", index);
        f.setArguments(args);

        return f;
    }

    public int getShownIndex() {
        return getArguments().getInt("index", 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i("TAG", "DESTROY VIEW");
        mCamera.close();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i("TAG", "ON DETACH");
        mCamera.close();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("TAG", "DESTROY!");
        mCamera.close();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("TAG", "ON RESUME!");
        mCamera = DefaultEasyCamera.open(Camera.CameraInfo.CAMERA_FACING_FRONT);
        mCamera.setDisplayOrientation(90);

    }

    public void resizeSurface(){
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int initialWidth = size.x;

        Integer height = mCamera.getParameters().getPreviewSize().width;
        Integer width = mCamera.getParameters().getPreviewSize().height;
        double aR = (double) width / height;
        Integer newHeight = new Double(initialWidth / aR).intValue();

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, newHeight);
        mSurfaceView.setLayoutParams(params);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_selfie, container, false);

        mSurfaceView = (SurfaceView) v.findViewById(R.id.surfaceView);
        mSurfaceHolder = mSurfaceView.getHolder();
        Log.i("TAG", "ON CREATE!");

        mSurfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                startStream();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                stopStream();
                mCamera.close();
            }
        });
        return v;
    }


    public void stopStream() {
        mCamera.stopPreview();
    }
    public void startStream() {
        try {
            if (mCamera !=null) {
                mActions = mCamera.startPreview(mSurfaceHolder);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
        Handler handler = new Handler(Looper.getMainLooper());
        final Runnable r = new Runnable() {
            public void run() {

            }
        };
        handler.postDelayed(r, 5000);
        */

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(IconicsContextWrapper.wrap(context));
    }


}