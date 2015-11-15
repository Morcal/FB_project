package com.feibo.snacks.view.module.person;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.feibo.snacks.R;
import com.feibo.snacks.app.Constant;
import com.feibo.snacks.app.Snacks;
import com.feibo.snacks.manager.global.Share4GiftConfigManager;
import com.feibo.snacks.manager.global.ContactManager;
import com.feibo.snacks.manager.global.RedPointManager;
import com.feibo.snacks.manager.global.UserManager;
import com.feibo.snacks.model.bean.RedPointInfo;
import com.feibo.snacks.model.bean.ServiceContact;
import com.feibo.snacks.model.bean.User;
import com.feibo.snacks.view.base.BaseSwitchActivity;
import com.feibo.snacks.view.base.BaseTitleFragment;
import com.feibo.snacks.view.module.MainActivity;
import com.feibo.snacks.view.module.coupon.MyCouponActivity;
import com.feibo.snacks.view.module.goods.goodsdetail.WebAcitivity;
import com.feibo.snacks.view.module.person.collect.CollectFragment;
import com.feibo.snacks.view.module.person.login.LoginFragment;
import com.feibo.snacks.view.module.person.login.LoginGroup;
import com.feibo.snacks.view.module.person.login.RegisterFragment;
import com.feibo.snacks.view.module.person.orders.OrdersActivity;
import com.feibo.snacks.view.module.person.setting.EditFragment;
import com.feibo.snacks.view.module.person.setting.FeedbackFragment;
import com.feibo.snacks.view.module.person.setting.SettingFragment;
import com.feibo.snacks.view.util.LaunchUtil;
import com.feibo.snacks.view.util.RemindControl;
import com.feibo.snacks.view.util.UIUtil;
import com.feibo.snacks.view.widget.BaseItemLayout;
import com.feibo.snacks.view.widget.BaseRedIcon;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import fbcore.log.LogUtil;

public class PersonFragment extends BaseTitleFragment{
    private static final String TAG = PersonFragment.class.getSimpleName();

    public static final String PARAM_NEED_SHOW = "isInMainActivity";

    private static final int REQUEST_LOGIN_ORDER = 0x100;
    private static final int REQUEST_LOGIN_COUPON = 0x101;
    private static final int REQUEST_LOGIN_COLLECT = 0x102;
    private static final int REQUEST_LOGIN_SEND_GIFT = 0X103;

    @Bind(R.id.item_invite_send_gift)
    BaseItemLayout sendGiftItem;

    @Bind(R.id.item_contact)
    BaseItemLayout serviceItem;

    @Bind(R.id.btn_shopping_car)
    BaseRedIcon shoppingCarBtn;

    @Bind(R.id.btn_order_wait_pay)
    BaseRedIcon waitPayBtn;

    @Bind(R.id.btn_order_wait_send)
    BaseRedIcon waitSendBtn;

    @Bind(R.id.btn_order_wait_receive)
    BaseRedIcon waitReceiveBtn;

    @Bind(R.id.btn_order_wait_assess)
    BaseRedIcon waitAssessBtn;

    @Bind(R.id.board_login)
    View loginBoard;

    @Bind(R.id.board_logout)
    View logoutBoard;

    @Bind(R.id.text_nickname)
    TextView userNameView;

    @Bind(R.id.image_avatar)
    ImageView userAvatarView;

    private UserManager userManager;
    private RedPointManager redPointManager;
    private ContactManager contactManager;

    private RedPointManager.RedPointObserver redPointObserver;  // 红点观察类

    public static enum SnackOrder{
        ALL, WAIT_PAY, WAIT_SEND, WAIT_RECEIVE, WAIT_ASSESS
    }
    private SnackOrder selectedOrder;

    public PersonFragment(){
    }

    @Override
    public int onCreateTitleBar() {
        return R.layout.layout_base_header;
    }

    @Override
    public View onCreateContentView() {
        RemindControl.cancelToast();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_person, null);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initTitleBar(TitleBar titleBar) {
        titleBar.headView.setVisibility(View.GONE);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        setFragmentName(getResources().getString(R.string.personFragment));

        userManager = UserManager.getInstance();
        redPointManager = RedPointManager.getInstance();
        contactManager = ContactManager.getInstance();
        selectedOrder = SnackOrder.ALL;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        redPointObserver = new RedPointManager.RedPointObserver(){
            @Override
            public void updateRedPoint(RedPointInfo info) {
                LogUtil.i(TAG, "loadRedPoint");
                initRedPointView();
            }
        };
        redPointManager.addObserver(redPointObserver);
        // 官方客服
        serviceItem.setMessageHintTitle(ContactManager.getInstance().getServiceContact().phone);
    }

    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        redPointManager.deleteObserver(redPointObserver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 登录成功
        if (requestCode == REQUEST_LOGIN_ORDER && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT){
            if(selectedOrder != null){
                entryOrderSnack();
            }
        }

        // 登录成功跳转我的优惠券
        if (requestCode == REQUEST_LOGIN_COUPON && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT){
            handleCoupon();
        }

        // 登录成功跳转到我的收藏
        if (requestCode == REQUEST_LOGIN_COLLECT && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            handleCollect();
        }
        if (requestCode == REQUEST_LOGIN_SEND_GIFT && resultCode == LoginGroup.RESULT_CODE_FOR_LOGINFRAGMENT) {
            handleSendGift();
        }
    }

    private void initData() {
        Snacks snacks = (Snacks) getActivity().getApplication();
        if (snacks.needShowShare4GiftView()) {
            sendGiftItem.setVisibility(View.VISIBLE);
        } else {
            sendGiftItem.setVisibility(View.GONE);
        }
        if (userManager.isLogin()) {
            // 登录状态
            loginBoard.setVisibility(View.VISIBLE);
            logoutBoard.setVisibility(View.INVISIBLE);
            User user = userManager.getUser();
            userNameView.setText(limitCharNumber(user.getNickname()));
            UIUtil.setImage(getActivity(),
                    user.getAvatar(),
                    R.drawable.default_photo_person,
                    R.drawable.default_photo_person,
                    userAvatarView);
        } else {
            // 未登录状态
            loginBoard.setVisibility(View.INVISIBLE);
            logoutBoard.setVisibility(View.VISIBLE);
            userNameView.setText(getResources().getString(R.string.default_nickname));
            Bitmap defaultBitmap = ((BitmapDrawable)(getResources().getDrawable(R.drawable.default_photo_person))).getBitmap();
            userAvatarView.setImageBitmap(defaultBitmap);
        }

        // 红点信息
        initRedPointView();
    }

    private void initRedPointView() {
        RedPointInfo redPointInfo = redPointManager.getRedPointInfo();
        if (redPointInfo != null) {
            shoppingCarBtn.setNum(redPointInfo.cartNum);
            waitPayBtn.setNum(redPointInfo.waitPayNum);
            waitSendBtn.setNum(redPointInfo.waitSendNum);
            waitReceiveBtn.setNum(redPointInfo.waitReceiveNum);
            waitAssessBtn.setNum(redPointInfo.waitCommentNum);
        }
    }

    // 进入购物车
    @OnClick(R.id.btn_shopping_car)
    public void handleCart(){
        LaunchUtil.launchCartActivityForResult(MainActivity.ENTRY_HOME_SCENCE, getActivity());
    }

    // 修改头像
    @OnClick( {R.id.image_avatar, R.id.text_nickname})
    public void handlePersonData(){
        if(userManager.isLogin()) {
            LaunchUtil.launchActivity(getActivity(),
                    BaseSwitchActivity.class,
                    EditFragment.class,
                    null);
        } else {
            handleLogin();
        }
    }

    // 上传头像
    public void handleAvatarUpload(Uri uri) {
        LogUtil.i(TAG, "handleAvatarUpload:" + uri.getPath());
        RemindControl.showProgressDialog(getActivity(), "正在上传头像", null);
        userManager.uploadAvatar(new File(uri.getPath()), new UserManager.UploadAvatarListener() {
            @Override
            public void onSuccess(String url) {
                RemindControl.hideProgressDialog();
                RemindControl.showSimpleToast(getActivity(), "头像上传成功");
                UIUtil.setImage(getActivity(),
                        url,
                        R.drawable.default_photo,
                        R.drawable.default_photo,
                        userAvatarView);
            }

            @Override
            public void onFail(String msg) {
                RemindControl.hideProgressDialog();
                RemindControl.showSimpleToast(getActivity(), msg);
            }
        });
    }

    // 登录按钮
    @OnClick(R.id.btn_login)
    public void handleLogin(){
        LaunchUtil.launchActivity(getActivity(),
                BaseSwitchActivity.class,
                LoginFragment.class,
                null);
    }

    @OnClick(R.id.item_invite_send_gift)
    public void hanleShare4Gift() {
        if (!UserManager.getInstance().isLogin()) {
            LaunchUtil.launchActivityForResult(REQUEST_LOGIN_SEND_GIFT, getActivity(), BaseSwitchActivity.class, LoginFragment.class, null);
            return;
        }
        handleSendGift();
    }

    private void handleSendGift() {
        Intent intent = new Intent(getActivity(), WebAcitivity.class);
        intent.putExtra(WebAcitivity.SHOPPING_URL, Share4GiftConfigManager.buildShare4GitfUrl());
        getActivity().startActivity(intent);
    }

    // 注册按钮
    @OnClick(R.id.btn_register)
    public void handleRegister(){
        LaunchUtil.launchActivity(getActivity(),
                BaseSwitchActivity.class,
                RegisterFragment.class,
                null);
    }

    // 进入待付款订单列表
    @OnClick(R.id.btn_order_wait_pay)
    public void handleOrderWaitPay(){
        selectedOrder = SnackOrder.WAIT_PAY;
        if (!checkLogin(REQUEST_LOGIN_ORDER)) {
            return;
        }
        entryOrderSnack();
    }

    // 进入待发货订单列表
    @OnClick(R.id.btn_order_wait_send)
    public void handleOrderWaitSend(){
        selectedOrder = SnackOrder.WAIT_SEND;
        if (!checkLogin(REQUEST_LOGIN_ORDER)) {
            return;
        }
        entryOrderSnack();
    }

    // 进入待收货订单列表
    @OnClick(R.id.btn_order_wait_receive)
    public void handleOrderWaitReceive(){
        selectedOrder = SnackOrder.WAIT_RECEIVE;
        if (!checkLogin(REQUEST_LOGIN_ORDER)) {
            return;
        }
        entryOrderSnack();
    }

    // 进入待评价订单列表
    @OnClick(R.id.btn_order_wait_assess)
    public void handleOrderWaitAssess(){
        selectedOrder = SnackOrder.WAIT_ASSESS;
        if (!checkLogin(REQUEST_LOGIN_ORDER)) {
            return;
        }
        entryOrderSnack();
    }

    // 进入商城订单
    @OnClick(R.id.item_order_snack)
    public void handleOrderSnack(){
        selectedOrder = SnackOrder.ALL;
        if (!checkLogin(REQUEST_LOGIN_ORDER)) {
            return;
        }
        if(selectedOrder!=null){
            entryOrderSnack();
        }

    }

    private void entryOrderSnack() {
        int selectPos = 0;
        switch (selectedOrder){
            case WAIT_PAY:
                selectPos = 1;
                break;
            case WAIT_SEND:
                selectPos = 2;
                break;
            case WAIT_RECEIVE:
                selectPos = 3;
                break;
            case WAIT_ASSESS:
                selectPos = 4;
                break;
        }
        Intent intent = new Intent(getActivity(), OrdersActivity.class);
        intent.putExtra(OrdersActivity.PARAM_SELECT_POS, selectPos);
        startActivityForResult(intent, MainActivity.ENTRY_HOME_SCENCE);
    }

    // 进入淘宝订单
    @OnClick(R.id.item_order_taobao)
    public void handleOrderTaobao(){
        Intent intent = new Intent(getActivity(), WebAcitivity.class);
        intent.putExtra(WebAcitivity.TITLE, getResources().getString(R.string.taobao_order));
        intent.putExtra(WebAcitivity.SHOPPING_URL, Constant.WEB_TAOBAO_ORDER_URL);
        getActivity().startActivity(intent);
    }



    // TODO 优惠券
    @OnClick(R.id.item_coupon)
    public void handleCoupon(){
        MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_person_coupon));
        if (!checkLogin(REQUEST_LOGIN_COUPON)) {
            return;
        }
        LaunchUtil.launchActivityForResult(0, getActivity(), MyCouponActivity.class);
    }

    // 进入收藏
    @OnClick(R.id.item_collect)
    public void handleCollect(){
        MobclickAgent.onEvent(getActivity(), getResources().getString(R.string.click_person_collect));
        if (!checkLogin(REQUEST_LOGIN_COLLECT)) {
            return;
        }
        LaunchUtil.launchActivity(getActivity(),
                BaseSwitchActivity.class,
                CollectFragment.class,
                null);
    }

    // 进入官方客服;
    @OnClick(R.id.item_contact)
    public void handleContact(){
        final ServiceContact contact = contactManager.getServiceContact();
        RemindControl.showServiceRemind(getActivity(), contact, new RemindControl.OnRemindListener() {
            @Override
            public void onConfirm() {
                String phone = contactManager.getServiceContact().phone;
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phone));
                getActivity().startActivity(intent);
            }

            @Override
            public void onCancel() {
            }
        });
    }

    // 进入意见反馈
    @OnClick(R.id.item_feedback)
    public void handleFeedback(){
        LaunchUtil.launchActivity(getActivity(),
                BaseSwitchActivity.class,
                FeedbackFragment.class, null);
    }

    // 进入设置
    @OnClick({R.id.btn_setting})
    public void handleSetting(){
        LaunchUtil.launchActivity(getActivity(),
                BaseSwitchActivity.class,
                SettingFragment.class, null);
    }

    private boolean checkLogin(int requestCode) {
        if (!userManager.isLogin()) {
            LaunchUtil.launchActivityForResult(requestCode, getActivity(),
                    BaseSwitchActivity.class,
                    LoginFragment.class,
                    null);
            return false;
        }
        return true;
    }

    private CharSequence limitCharNumber(String nickname) {
        if (TextUtils.isEmpty(nickname)) {
            nickname = getResources().getString(R.string.default_nickname);
        } else if (nickname.length() > 20) {
            nickname = nickname.substring(0, 20) + "...";
        }
        return nickname;
    }
}
