package com.feibo.joke.view.module.mine.edit;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.feibo.joke.R;
import com.feibo.joke.app.AppContext;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.model.UserDetail;
import com.feibo.joke.utils.StringUtil;
import com.feibo.joke.utils.UIUtil;
import com.feibo.joke.view.dialog.AddressDialog;
import com.feibo.joke.view.dialog.AddressDialog.OnAddressChangedListener;
import com.feibo.joke.view.dialog.SexDialog;
import com.feibo.joke.view.dialog.SexDialog.DialogItemClickListener;
import com.feibo.joke.view.dialog.TimeDialog;
import com.feibo.joke.view.dialog.TimeDialog.OnTimeChangedListener;
import com.feibo.joke.view.util.LaunchUtil;
import com.feibo.joke.view.util.ToastUtil;
import com.feibo.joke.view.widget.VImageView;

public class PersonEditFragment extends BaseTakePhotoFragment implements OnClickListener {

	private TextView tvHeadRight;

	private VImageView ivHead;
	private TextView tvNickname, tvSex, tvArea, tvBirthday, tvSignature;
	private View itemHead, itemNickname, itemSex, itemArea, itemBirthday, itemSignature;

	AddressDialog addressDialog;
	TimeDialog timeDialog;

	boolean isEditPhoto;
	
	private String province, city, birth;

	@Override
	public View containChildView() {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_person_edit, null);
		itemHead = view.findViewById(R.id.item_head);
		itemNickname = view.findViewById(R.id.item_nickname);
		itemArea = view.findViewById(R.id.item_area);
		itemSex = view.findViewById(R.id.item_sex);
		itemBirthday = view.findViewById(R.id.item_birthday);
		itemSignature = view.findViewById(R.id.item_signature);

		ivHead = (VImageView) view.findViewById(R.id.item_avatar);
		tvNickname = (TextView) view.findViewById(R.id.tv_nickname);
		tvSex = (TextView) view.findViewById(R.id.tv_sex);
		tvArea = (TextView) view.findViewById(R.id.tv_area);
		tvBirthday = (TextView) view.findViewById(R.id.tv_birthday);
		tvSignature = (TextView) view.findViewById(R.id.tv_signature);

		itemHead.setOnClickListener(this);
		itemNickname.setOnClickListener(this);
		itemArea.setOnClickListener(this);
		itemSex.setOnClickListener(this);
		itemBirthday.setOnClickListener(this);
		itemSignature.setOnClickListener(this);

		ivHead.showSensation(false);

		User user = UserManager.getInstance().getUser();
		tvNickname.setText(user.nickname);
		String sex = user.detail.gender == UserDetail.MAN ? "男" : (user.detail.gender == UserDetail.WOMAN ? "女" : "");
		tvSex.setText(sex);
		UIUtil.setVAvatar(user.avatar, false, ivHead);
		tvSignature.setText(user.detail.signature);

		if (!StringUtil.isEmpty(user.detail.province) && !StringUtil.isEmpty(user.detail.city)) {
			tvArea.setText(user.detail.province + " " + user.detail.city);
		}
		if (!StringUtil.isEmpty(user.detail.birth) && !user.detail.birth.startsWith("0000")) {
			tvBirthday.setText(user.detail.birth);
		} else {
			tvBirthday.setText("");
		}
		
		province = user.detail.province;
		city = user.detail.city;
		birth = user.detail.birth;

		return view;
	}

	@Override
	public int setTitleLayoutId() {
		return R.layout.base_titlebar;
	}

	@Override
	public void setTitlebar() {
		TitleBar titleBar = getTitleBar();
		((TextView) titleBar.title).setText("个人编辑");
		tvHeadRight = titleBar.tvHeadRight;
		tvHeadRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				updateInfo();
			}
		});
		tvHeadRight.setText("保存");
	}

	protected void updateInfo() {
		if (!AppContext.isNetworkAvailable()) {// 如果网络不可用, 直接退出
			ToastUtil.showSimpleToast(R.string.not_network);
			getActivity().finish();
			return;
		}
		if (isEditInfo()) {
			User user = new User();
			user.detail = new UserDetail();

			String nickname = tvNickname.getText().toString();
			String signature = tvSignature.getText().toString();

			user.detail.gender = tvSex.getText().equals("男") ? UserDetail.MAN : (tvSex.getText().equals("女") ? UserDetail.WOMAN : UserDetail.UN_KNOW);
			user.detail.birth = StringUtil.isEmpty(birth) ? "" : birth;
			user.nickname = StringUtil.isEmpty(nickname) ? "" : nickname;
			user.detail.province = StringUtil.isEmpty(province) ? "" : province;
			user.detail.city = StringUtil.isEmpty(city) ? "" : city;
			user.detail.signature = StringUtil.isEmpty(signature) ? "" : signature;
			if (!isEditPhoto) {
				user.avatar = UserManager.getInstance().getUser().avatar;
			}

			uploadUserInfo(user);
		} else {
			this.getActivity().finish();
		}
	}

	@Override
	public void onReleaseView() {

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.item_head:
			showChoosePhotoDialog();
			break;
		case R.id.item_nickname:
			LaunchUtil.launchSubActivity(getActivity(), NicknameFragment.class, NicknameFragment.buildBundle(tvNickname.getText().toString()));
			break;
		case R.id.item_sex:
			SexDialog.show(getActivity()).setOnDialogItemClickListener(new DialogItemClickListener() {

				@Override
				public void onClick(int type) {
					if (getActivity() != null) {
						tvSex.setText(SexDialog.MAN == type ? "男" : "女");
					}
				}
			});
			break;
		case R.id.item_area:
			if (addressDialog == null) {
				addressDialog = AddressDialog.show(getActivity(), new OnAddressChangedListener() {

					@Override
					public void onChange(String province, String city) {
						PersonEditFragment.this.province = province;
						PersonEditFragment.this.city = city;
						
						tvArea.setText(province + " " + city);
					}
				});
			} else {
				addressDialog.show();
			}
			break;
		case R.id.item_birthday:
			if (timeDialog == null) {
				timeDialog = TimeDialog.show(getActivity(), new OnTimeChangedListener() {

					@Override
					public void onChange(String year, String month, String day) {
						year = year.substring(0, year.length() - 1);
						month = month.substring(0, month.length() - 1);
						day = day.substring(0, day.length() - 1);
						if(Integer.valueOf(month) < 10) {
							month = "0" + month;
						}
						if(Integer.valueOf(day) < 10) {
							day = "0" + day;
						}
						
						PersonEditFragment.this.birth = year + "-" + month + "-" + day;
						tvBirthday.setText(PersonEditFragment.this.birth);
					}
				});
			} else {
				timeDialog.show();
			}
			break;
		case R.id.item_signature:
			LaunchUtil.launchSubActivity(getActivity(), SignatureFragment.class, SignatureFragment.buildBundle(tvSignature.getText().toString()));
			break;
		default:
			break;
		}
	}

	@Override
	public void onDataChange(int code) {
		super.onDataChange(code);

		if(getFinishBundle() == null) {
			return;
		}
		String s = StringUtil.isEmpty(getFinishBundle().getString("text")) ? "" : getFinishBundle().getString("text");
		if (code == 1) {
			tvSignature.setText(s);
		} else if (code == 2) {
			tvNickname.setText(s);
		}
	}

	@Override
	public void fillImageView(Bitmap bitmap, String path) {
		ivHead.getImageView().setImageBitmap(bitmap);
		isEditPhoto = true;
	}

	private boolean isEditInfo() {
		if (isEditPhoto) {
			return true;
		}

		User user = UserManager.getInstance().getUser();
		int sex = tvSex.getText().equals("男") ? UserDetail.MAN : (tvSex.getText().equals("女") ? UserDetail.WOMAN : UserDetail.UN_KNOW);

		if (!tvNickname.getText().equals(user.nickname))
			return true;
		if (sex != user.detail.gender)
			return true;
		if (!city.equals(user.detail.city))
			return true;
		if (!province.equals(user.detail.province))
			return true;
		if(!birth.equals(user.detail.birth))
			return true;
		if(!tvSignature.getText().equals(user.detail.signature))
			return true;
		return false;
	}

}
