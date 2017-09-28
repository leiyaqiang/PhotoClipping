package com.leiyaqiang.lenovo.photoclipping;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class AlertDialog {
	private Context context;
	private Dialog dialog;
	private LinearLayout lLayout_bg;
	private TextView txt_title;
	private TextView txt_msg;
    private EditText et_msg;
	private Button btn_neg;
	private Button btn_pos;
	private ImageView img_line;
	private Display display;
	private boolean showTitle = false;
	private boolean showMsg = false;
    private boolean showEtMsg = false;
	private boolean showPosBtn = false;
	private boolean showNegBtn = false;

	public AlertDialog(Context context) {
		this.context = context;
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		display = windowManager.getDefaultDisplay();
	}

	public AlertDialog builder() {

		View view = LayoutInflater.from(context).inflate(
				R.layout.view_alertdialog, null);


		lLayout_bg = (LinearLayout) view.findViewById(R.id.lLayout_bg);
		txt_title = (TextView) view.findViewById(R.id.txt_title);
		txt_title.setVisibility(View.GONE);
		txt_msg = (TextView) view.findViewById(R.id.txt_msg);
		txt_msg.setVisibility(View.GONE);
        et_msg= (EditText) view.findViewById(R.id.et_msg);
        et_msg.setVisibility(View.GONE);
		btn_neg = (Button) view.findViewById(R.id.btn_neg);
		btn_neg.setVisibility(View.GONE);
		btn_pos = (Button) view.findViewById(R.id.btn_pos);
		btn_pos.setVisibility(View.GONE);
		img_line = (ImageView) view.findViewById(R.id.img_line);
		img_line.setVisibility(View.GONE);


		dialog = new Dialog(context, R.style.AlertDialogStyle);
		dialog.setContentView(view);


		lLayout_bg.setLayoutParams(new FrameLayout.LayoutParams((int) (display
				.getWidth() * 0.85), LayoutParams.WRAP_CONTENT));

		return this;
	}

	public AlertDialog setTitle(String title) {
		showTitle = true;
		if ("".equals(title)) {
			txt_title.setText("标题");
		} else {
			txt_title.setText(title);
		}
		return this;
	}

	public AlertDialog setMsg(String msg) {
		showMsg = true;
		if ("".equals(msg)) {
			txt_msg.setText("内容");
		} else {
			txt_msg.setText(msg);
		}
		return this;
	}

    public AlertDialog setEtMsg(String msg) {
        showEtMsg = true;
        if ("".equals(msg)) {
            et_msg.setHint("");
        } else {
            et_msg.setHint(msg);
        }
        return this;
    }

	public AlertDialog setCancelable(boolean cancel) {
		dialog.setCancelable(cancel);
		return this;
	}

	public AlertDialog setPositiveButton(String text, ActionSheetDialog.SheetItemColor color,
                                         final OnClickListener listener) {
		showPosBtn = true;
		if ("".equals(text)) {
			btn_pos.setText("确定");
		} else {
			btn_pos.setText(text);
		}
		btn_pos.setTextColor(Color.parseColor(color.getName()));
		btn_pos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
                if (listener instanceof OnClickListenerWithEditView){
                    String content=et_msg.getText().toString();
                    if (content==null || content.equals("")){
                        return;
                    }
                    OnClickListenerWithEditView onClickListenerWithEditView= (OnClickListenerWithEditView) listener;
                    onClickListenerWithEditView.onClick(v,content);
                }else{
                    listener.onClick(v);
                }
				dialog.dismiss();
			}
		});
		return this;
	}

	public AlertDialog setForceButton(String text, ActionSheetDialog.SheetItemColor color,
                                      final OnClickListener listener) {
		showPosBtn = true;
		if ("".equals(text)) {
			btn_pos.setText("确定");
		} else {
			btn_pos.setText(text);
		}
		btn_pos.setTextColor(Color.parseColor(color.getName()));
		btn_pos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (listener instanceof OnClickListenerWithEditView){
					String content=et_msg.getText().toString();
					if (content==null || content.equals("")){
						return;
					}
					OnClickListenerWithEditView onClickListenerWithEditView= (OnClickListenerWithEditView) listener;
					onClickListenerWithEditView.onClick(v,content);
				}else{
					listener.onClick(v);
				}
			}
		});
		return this;
	}

    public interface OnClickListenerWithEditView extends OnClickListener {
        void onClick(View v, String text);
    }

	public AlertDialog setNegativeButton(String text,
			final OnClickListener listener) {
		showNegBtn = true;
		if ("".equals(text)) {
			btn_neg.setText("取消");
		} else {
			btn_neg.setText(text);
		}
		btn_neg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				listener.onClick(v);
				dialog.dismiss();
			}
		});
		return this;
	}

	private void setLayout() {
		if (!showTitle && !showMsg) {
			txt_title.setText("标题");
			txt_title.setVisibility(View.VISIBLE);
		}

		if (showTitle) {
			txt_title.setVisibility(View.VISIBLE);
		}

		if (showMsg) {
			txt_msg.setVisibility(View.VISIBLE);
		}

        if (showEtMsg) {
            et_msg.setVisibility(View.VISIBLE);
        }

		if (!showPosBtn && !showNegBtn) {
			btn_pos.setText("确定");
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
			btn_pos.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog.dismiss();
				}
			});
		}

		if (showPosBtn && showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.alertdialog_right_selector);
			btn_neg.setVisibility(View.VISIBLE);
			btn_neg.setBackgroundResource(R.drawable.alertdialog_left_selector);
			img_line.setVisibility(View.VISIBLE);
		}

		if (showPosBtn && !showNegBtn) {
			btn_pos.setVisibility(View.VISIBLE);
			btn_pos.setBackgroundResource(R.drawable.alertdialog_single_selector);
		}

		if (!showPosBtn && showNegBtn) {
			btn_neg.setVisibility(View.VISIBLE);
			btn_neg.setBackgroundResource(R.drawable.alertdialog_single_selector);
		}
	}

	public void show() {
		setLayout();
		dialog.show();
	}
}
