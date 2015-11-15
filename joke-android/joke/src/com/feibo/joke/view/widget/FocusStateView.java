package com.feibo.joke.view.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.feibo.joke.R;
import com.feibo.joke.dao.ReturnCode;
import com.feibo.joke.manager.LoadListener;
import com.feibo.joke.manager.work.OperateManager;
import com.feibo.joke.manager.work.UserManager;
import com.feibo.joke.model.User;
import com.feibo.joke.view.dialog.RemindDialog;
import com.feibo.joke.view.dialog.RemindDialog.OnDialogClickListener;
import com.feibo.joke.view.dialog.RemindDialog.RemindSource;
import com.feibo.joke.view.util.ToastUtil;

/**
 * @author BigTiger
 * @explain 根据关注状态显示不同的按钮控件
 * @date 2015年4月7日 下午1:34:28
 */
public class FocusStateView extends ImageView implements OnClickListener {
	/** 默认关注状态 **/
	private int currentState = User.RELATIONSHIP_NULL;
	/** 互相关注时的图片 **/
	private Drawable focusEach;
	/** 已关注时显示的图片 **/
	private Drawable focusAlready;
	/** 没有关注时显示的图片 **/
	private Drawable focusNull;
	/** 邀请显示的图片 **/
	private Drawable focusInvita;
	/** 当前显示的图片 **/
	private Drawable currentBg;

	private Context mContext;

	private User user;

	private OnStatuClicklistener statuListener;
	private OnClickListener invitationLister;
	private OnStatuChangeListener onStatuChangeListener;
	
	/** 关注成功时Toast */
	private boolean showToastWhenAttentionSuccuss;
    /** 取消关注成功时Toast */
    private boolean showToastWhenCancleAttentionSuccuss;
    /** 取消关注时弹出确认取消关注对话框 */
	private boolean canShowDialogIfCancleAttention;
    /**是否可以取消关注*/
    private boolean isCanCancel=true;

	private static final int STATU_INVITATION = 4;

	public FocusStateView(Context context) {
		this(context, null);
	}

	public FocusStateView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public FocusStateView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext = context;
		initView(attrs);
	}

	private void initView(AttributeSet attrs) {
		TypedArray a = mContext.obtainStyledAttributes(attrs,
				R.styleable.FocusStateView);
		currentState = a.getInteger(R.styleable.FocusStateView_focusState,
				currentState);
		int ivId1 = a.getResourceId(
				R.styleable.FocusStateView_focusAlreadyImage, 0);
		int ivId2 = a.getResourceId(R.styleable.FocusStateView_focusEachImage,
				0);
		int ivId3 = a.getResourceId(R.styleable.FocusStateView_focusNullImage,
				0);
		int ivId4 = a.getResourceId(
				R.styleable.FocusStateView_focusInviteImage,
				R.drawable.btn_focus_invita);
		showToastWhenCancleAttentionSuccuss = a.getBoolean(R.styleable.FocusStateView_cancleAttentionToast, true);
		showToastWhenAttentionSuccuss = a.getBoolean(R.styleable.FocusStateView_attentionToast, false);
		isCanCancel = a.getBoolean(R.styleable.FocusStateView_canCancleAttention, true);
		canShowDialogIfCancleAttention = a.getBoolean(R.styleable.FocusStateView_showDialogWhenCancleAttention, false);
		
		focusAlready = getResources().getDrawable(ivId1);
		focusEach = getResources().getDrawable(ivId2);
		focusNull = getResources().getDrawable(ivId3);
		focusInvita = getResources().getDrawable(ivId4);
		a.recycle();

		this.setOnClickListener(this);
		setFocusState(currentState);
	}
	
	public void setOnStatuClickListener(OnStatuClicklistener listener) {
		this.statuListener = listener;
	}

	public void setUser(User user) {
		this.user = user;
		setFocusState(user.relationship);
	}
	
	/**
	 * 获取当前关注状态
	 * 
	 * @return 当前关注状态
	 */
	public int getFoucsState() {
		return currentState;
	}

	/**
	 * 设置关注状态 ,并切换到相应的图片显示
	 * 
	 * @param state
	 *            关注状态
	 */
	private void setFocusState(int state) {
		currentState = state;
		if (currentState == STATU_INVITATION) {
			// 邀请状态
			currentBg = focusInvita;
		} else {
			switch (currentState) {
			case User.RELATIONSHIP_USER_BE_ATTENTION:
			case User.RELATIONSHIP_NULL:
				currentBg = focusNull;
				break;
			case User.RELATIONSHIP_ATTENTION:
				currentBg = focusAlready;
				break;
			case User.RELATIONSHIP_BOTH_ATTENTION:
				currentBg = focusEach;
				break;
			default:
				break;
			}
		}
		setImageDrawable(currentBg);
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		setFocusState(currentState);
	}
	
	public interface OnStatuClicklistener {
		public void onPreperadLogin();
	}

	public void onPostSuccessChangState() {
		int newStatu = 0;
		switch (currentState) {
		case User.RELATIONSHIP_NULL:
		    newStatu = User.RELATIONSHIP_ATTENTION;
			break;
		case User.RELATIONSHIP_ATTENTION:
		    newStatu = User.RELATIONSHIP_NULL;
			break;
		case User.RELATIONSHIP_BOTH_ATTENTION:
		    newStatu = User.RELATIONSHIP_USER_BE_ATTENTION;
			break;
		case User.RELATIONSHIP_USER_BE_ATTENTION:
			newStatu = User.RELATIONSHIP_BOTH_ATTENTION;
			break;
		default:
			break;
		}
		setFocusState(newStatu);
		user.relationship = newStatu;
	}

	public void onStatuClick() {
	    onStatuClick(false);
	}
	
	public void onStatuClick(boolean showSuccessToast) {
	    setEnabled(false);
		switch (currentState) {
		case User.RELATIONSHIP_USER_BE_ATTENTION:
		case User.RELATIONSHIP_NULL:
			dialogShow(true, showSuccessToast);
			break;
		case User.RELATIONSHIP_ATTENTION:
		case User.RELATIONSHIP_BOTH_ATTENTION:
		    if(!isCanCancel) {
                setEnabled(true);
		        return;
		    }
		    if(canShowDialogIfCancleAttention) {
		        showRemindDialog(showSuccessToast);
		        return;
		    }
            dialogShow(false, showSuccessToast);
			break;
		default:
            setEnabled(true);
			break;
		}
	}
	
	public void showRemindDialog(final boolean showSuccessToast) {
	    RemindDialog dialog = RemindDialog.show(this.getContext(), new RemindSource("确定取消关注吗", "确定", "取消"), true);
	    dialog.setOnDialogClickListener(new OnDialogClickListener() {

			@Override
			public void onConfirm() {
				dialogShow(false, showSuccessToast);
			}

			@Override
			public void onCancel() {
			}
		});
	    dialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				setEnabled(true);
			}
		});
	}
	
	public void dialogShow(boolean attention, final boolean showSuccessToast) {
	    final ProgressDialog dialog = new ProgressDialog(mContext);
	    dialog.setOnDismissListener(new OnDismissListener() {
            
            @Override
            public void onDismiss(DialogInterface dialog) {
                FocusStateView.this.setEnabled(true);
            }
        });
        dialog.setMessage("正在处理中...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        
        if(attention) {
            OperateManager.attentionUser(user.id, new LoadListener() {
                @Override
                public void onSuccess() {
                    onPostSuccessChangState();
                    if(dialog != null) {
                        dialog.dismiss();
                    }
                    if(onStatuChangeListener != null) {
                        onStatuChangeListener.onStatuChange(true);
                    }
                    if(showSuccessToast || showToastWhenAttentionSuccuss) {
//                        ToastUtil.showSimpleToast("关注成功");
                    }
                }
        
                @Override
                public void onFail(int code) {
					if (code == ReturnCode.RS_REPECT_CLICK) {
						onPostSuccessChangState();
					} else {
						ToastUtil.showSimpleToast("关注失败");
					}
                    if(dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
        } else {
            OperateManager.cancelAttentionUser(user.id, new LoadListener() {
				@Override
				public void onSuccess() {
					onPostSuccessChangState();
					if (dialog != null) {
						dialog.dismiss();
					}

					if (onStatuChangeListener != null) {
						onStatuChangeListener.onStatuChange(false);
					}
					if (showToastWhenCancleAttentionSuccuss) {
						ToastUtil.showSimpleToast("取消关注成功");
					}
				}

				@Override
				public void onFail(int code) {
					if (code == ReturnCode.RS_NONE_OBJECT) {
						onPostSuccessChangState();
					} else {
						ToastUtil.showSimpleToast("取消关注失败");
					}
					if (dialog != null) {
						dialog.dismiss();
					}
				}
			});
        }
	}
	
	public void setOnStatuChangelistener(OnStatuChangeListener onStatuChangeListener) {
	    this.onStatuChangeListener = onStatuChangeListener;
	}
	
	public interface OnStatuChangeListener {
	    public void onStatuChange(boolean isAttention);
	}
	
	@Override
	public void onClick(View v) {
		if (UserManager.getInstance().isLogin()) {
			if (currentState == STATU_INVITATION) {
				if (invitationLister != null) {
					invitationLister.onClick(v);
				}
			} else {
				onStatuClick();
			}
		} else {
			ToastUtil.showSimpleToast("要先登录哦");
			if (statuListener != null) {
				statuListener.onPreperadLogin();
			}
		}
	}

	public void setOnInvitationListener(OnClickListener invitationLister) {
		this.invitationLister = invitationLister;
	}

}
