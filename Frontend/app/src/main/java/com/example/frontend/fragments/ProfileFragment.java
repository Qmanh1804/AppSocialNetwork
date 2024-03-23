package com.example.frontend.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import androidx.fragment.app.FragmentManager;

import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.frontend.R;
import com.example.frontend.activities.FollowsActivity;
import com.example.frontend.activities.FragmentReplacerActivity;
import com.example.frontend.activities.MainActivity;
import com.example.frontend.utils.SharedPreferenceLocal;

public class ProfileFragment extends Fragment {

    ImageButton editprofileImageBtn,menuSetting;
    Button editprofileBtn, logoutBtn, qrcodeBtn;
    TextView username;
    LinearLayout openFollows,openFollowing;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        logoutBtn = view.findViewById(R.id.logout);
        qrcodeBtn = view.findViewById(R.id.qrcodeBtn);
        username = view.findViewById(R.id.toolbarNameTV);
        openFollows = view.findViewById(R.id.openFollows);
        openFollowing = view.findViewById(R.id.openFollowing);
        editprofileBtn = view.findViewById(R.id.edit_profileBtn);
        editprofileImageBtn = view.findViewById(R.id.edit_profileImage);
        menuSetting = view.findViewById(R.id.menu_btn);

        openFollows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowsActivity.class);
                // Thêm dữ liệu cho Intent để FragmentReplacerActivity biết cần thay thế fragment nào
                intent.putExtra("fragment_to_load", "openFollows");
                // Bắt đầu activity với Intent đã tạo
                startActivity(intent);
            }
        });
        openFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FollowsActivity.class);
                // Thêm dữ liệu cho Intent để FragmentReplacerActivity biết cần thay thế fragment nào
                intent.putExtra("fragment_to_load", "openFollowing");
                // Bắt đầu activity với Intent đã tạo
                startActivity(intent);
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferenceLocal.save(getContext(),"userId", "");
                Intent intent = new Intent(getActivity (), FragmentReplacerActivity.class);
                startActivity(intent);
            }
        });

        editprofileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), FragmentReplacerActivity.class);
                // Thêm dữ liệu cho Intent để FragmentReplacerActivity biết cần thay thế fragment nào
                intent.putExtra("fragment_to_load", "edit_profile");
                // Bắt đầu activity với Intent đã tạo
                startActivity(intent);

            }
        });

        editprofileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(),"Clicked",Toast.LENGTH_LONG).show();
            }
        });
        menuSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                intent.putExtra("fragment_to_load", "setting_btn");
//                startActivity(intent);
                String usernameValue = username.getText().toString();
                Bundle bundle = new Bundle();
                bundle.putString("username", usernameValue);
                SettingFragment settingFragment = new SettingFragment();
                settingFragment.setArguments(bundle); // Thiết lập arguments cho SettingFragment
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_layout_main, settingFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        qrcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Tạo instance của fragment mới
                Fragment QRCodeFragment = new QRCodeFragment();

                // Lấy instance của FragmentManager
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

                // Bắt đầu transaction để thay thế fragment hiện tại bằng fragment mới
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_layout_main, QRCodeFragment);
                fragmentTransaction.addToBackStack(null); // Thêm transaction này vào stack back để quay trở lại fragment trước đó (nếu cần)
                fragmentTransaction.commit();
            }
        });
        return view;
    }
}