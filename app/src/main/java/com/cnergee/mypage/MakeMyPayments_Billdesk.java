package com.cnergee.mypage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.billdesk.sdk.PaymentOptions;
import com.citruspay.citruspaylib.model.Address;
import com.citruspay.citruspaylib.model.Card;
import com.citruspay.citruspaylib.model.Customer;
import com.citruspay.citruspaylib.model.ExtraParams;
import com.cnergee.mypage.SOAP.GetPhoneDetailsSOAP;
import com.cnergee.mypage.SOAP.Get_BillDesk_SignatureSOAP;
import com.cnergee.mypage.caller.AfterInsertPaymentsCaller;
import com.cnergee.mypage.caller.BeforePaymentInsertCaller;
import com.cnergee.mypage.caller.InsertBeforeWithTrackCaller;
import com.cnergee.mypage.caller.PaymentGatewayCaller;
import com.cnergee.mypage.caller.RenewalCaller;
import com.cnergee.mypage.obj.AdditionalAmount;
import com.cnergee.mypage.obj.MyObject;
import com.cnergee.mypage.obj.PaymentsObj;
import com.cnergee.mypage.utils.AlertsBoxFactory;
import com.cnergee.mypage.utils.FinishEvent;
import com.cnergee.mypage.utils.Utils;
import com.cnergee.widgets.ProgressHUD;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.SoapFault;

import java.math.BigInteger;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import all.interface_.IError;
import cnergee.myapp.shengli_pune.R;


public class MakeMyPayments_Billdesk extends BaseActivity {
	//private static final String retrunURL = "https://sandbox.citruspay.com/demo/jsp/response_android.jsp";

	public static String dynamic_retrunURL = "";

	public static Context context;
	Utils utils = new Utils();
	public static String rslt = "";
	public long memberid;
	String isRenew = "";
	public static String responseMsg = "";
	public static String getTransactionResponse = "";
	public static String amount;
	String ServiceTax, UpdateFrom;
	LinearLayout linnhome, linnprofile, linnnotification, linnhelp;
	private InsertBeforeWithTrackId insertBeforeWithTrackId =null;

	TextView txtloginid, txtemailid, txtcontactno, txtnewpackagename,txtnewamount, txtnewvalidity;
	CheckBox privacy, terms;
	private String sharedPreferences_name;

    /*public static Map<String, MemberDetailsObj> mapMemberDetails;
	public static Map<String, PaymentGatewayObj> mappaymentgateways;
	public static Map<String, CitrusConstantsObj> mapcitrusconstants;*/

	private PaymentGateWayDetails getpaymentgatewaysdetails = null;

	boolean isLogout = false;
	//CitrusSSLLibrary citrusSSLLibrary = new CitrusSSLLibrary();
	List<NameValuePair> nameValuePairs;
	static String generatedHMAC, currencyValue;
	String authIdCode="", TxId="", TxStatus="", pgTxnNo="", issuerRefNo="", TxMsg="";
	public static boolean Changepack;
	private static final int ACTION_WEB_VIEW = 111;
	private String customername;
	String TrackId;
	String[] str;
	Button btnnb;
	Address address;
	ExtraParams extraParam;
	Card cards;
	Customer customer;
	public boolean isDetailShow = false;
	// String address;
	public static String adjTrackval = "";
	public static String HMACSignature = "";

	String GetSignature;

	private LinearLayout responseLayout;
	private ScrollView payNowView, responseScrollLayout;
	private InsertBeforePayemnt InsertBeforePayemnt = null;
	private InsertAfterPayemnt InsertAfterPayemnt = null;
	private RenewalProcessWebService RenewalProcessWebService = null;
	String discount = "";
	TableLayout tableRowDiscount;
	TextView tvDiscountLabel;
	String ClassName = " ";
	AdditionalAmount additionalAmount;
	public boolean is_payemnt_detail = false;
	TextView tvClickDetails;
	LinearLayout ll_addtional_details, llClickDetails;
	String type = "Renew";
	private boolean is_activity_running=false;
	String MemberloginName,MemberId;

	String payu_merchant_id="",payu_salt="";
	String s_url="http://mypage.cmaya.in/paymentgateway/payu/PaymentPAYUMobileSuccess.aspx",f_url="http://mypage.cmaya.in/paymentgateway/payu/PaymentPAYUMobileSuccess.aspx";

	HashMap<String, String> payu_params = new HashMap<String, String>();

	String Payment_Status="";
	String email_id="",mob_number="",merchant_id="",security_id="",token_req="",return_url="",msg="";
	MyObject myObject;

	public static int REQUEST_CODE=10;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.makemypayments);
		iError=(IError) this;
		Utils.log("BillDesk","MakeMyPaymnts");
		String sharedPreferences_name = getString(R.string.shared_preferences_name);
		SharedPreferences sharedPreferences_ = getApplicationContext().getSharedPreferences(sharedPreferences_name, 0); // 0 - for private mode

		utils.setSharedPreferences(sharedPreferences_);

		MemberloginName = utils.getMemberLoginID();
		MemberId=utils.getMemberId();
		myObject=new MyObject();
		is_activity_running=false;
		txtloginid = (TextView) findViewById(R.id.txtloginid);
		txtemailid = (TextView) findViewById(R.id.txtemailid);
		txtcontactno = (TextView) findViewById(R.id.txtcontactno);
		txtnewpackagename = (TextView) findViewById(R.id.txtnewpackagename);
		txtnewamount = (TextView) findViewById(R.id.txtnewamount);
		txtnewvalidity = (TextView) findViewById(R.id.txtnewvalidity);
		btnnb = (Button) findViewById(R.id.btnnb);
		ll_addtional_details = (LinearLayout) findViewById(R.id.ll_addtional_details);
		tvClickDetails = (TextView) findViewById(R.id.tvClickDetails);
		//citrusSSLLibrary = new CitrusSSLLibrary();
		responseScrollLayout = (ScrollView) findViewById(R.id.responseScrollLayout);
		responseLayout = (LinearLayout) findViewById(R.id.responseLayout);
		payNowView = (ScrollView) findViewById(R.id.payNowLayout);
		privacy = (CheckBox) findViewById(R.id.privacy);
		terms = (CheckBox) findViewById(R.id.terms);
		tableRowDiscount = (TableLayout) findViewById(R.id.tableLayoutDiscount);
		tvDiscountLabel = (TextView) findViewById(R.id.tvDiscountLabel);
		llClickDetails = (LinearLayout) findViewById(R.id.llClickDetails);
		ll_addtional_details = (LinearLayout) findViewById(R.id.ll_addtional_details);
		context = this;
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		// Log.i(">>>>>BUndle<<<<<", extra.toString());

		/* Log.i(">>>>>PAckageAmount<<<<",bundle .getString("PackageAmount")); */
		txtnewamount.setText(bundle .getString("PackageAmount"));
		txtnewpackagename.setText(bundle.getString("PackageName"));
		txtnewvalidity.setText(bundle.getString("PackageValidity"));
		ServiceTax = bundle.getString("ServiceTax");
		UpdateFrom = bundle.getString("updateFrom");
		discount = bundle.getString("discount");
		ClassName = bundle.getString("ClassName");
		additionalAmount = (AdditionalAmount) bundle.getSerializable("addtional_amount");

		Utils.log("UpdateFrom", "is:" + UpdateFrom);
		Utils.log("datafrom", "is:" + bundle.getString("datafrom"));

		if (bundle.getString("datafrom").equalsIgnoreCase("changepack")) {
			Changepack = true;
			tvDiscountLabel.setVisibility(View.GONE);

		} else {
			Changepack = false;
			Utils.log("Renew", "account");
			tvDiscountLabel.setVisibility(View.VISIBLE);
		}

		if (additionalAmount != null) {
			if (additionalAmount.getDiscountPercentage().length() > 0) {
				if (Double.valueOf(additionalAmount.getDiscountPercentage()) > 0) {
					// tvDiscountLabel.setText("You have got "+additionalAmount.getDiscountPercentage()+"% discount for online payment.");
					tvDiscountLabel.setText("Avail of a " + additionalAmount.getDiscountPercentage()
							+ "% discount by paying online.");
				} else {
					tvDiscountLabel.setVisibility(View.GONE);
				}
			} else {
				tvDiscountLabel.setVisibility(View.GONE);
			}

			if (Double.valueOf(additionalAmount.getFinalcharges()) > 0) {
				txtnewamount.setText(additionalAmount.getFinalcharges());

			}
			if (Double.valueOf(additionalAmount.getDaysFineAmount()) > 0) {
				is_payemnt_detail = true;
			}
			if (Double.valueOf(additionalAmount.getFineAmount()) > 0) {
				is_payemnt_detail = true;
			}
			if (Double.valueOf(additionalAmount.getDiscountAmount()) > 0) {
				is_payemnt_detail = true;
			}
			if (Double.valueOf(additionalAmount.getDaysFineAmount()) > 0) {
				is_payemnt_detail = true;
			}
			if (is_payemnt_detail) {
				txtnewamount.setText(additionalAmount.getFinalcharges());
				llClickDetails.setVisibility(View.VISIBLE);
			} else {
				llClickDetails.setVisibility(View.GONE);
			}
		} else {
			tvDiscountLabel.setVisibility(View.GONE);
		}
		txtnewamount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (is_payemnt_detail) {
					showPaymentDetailsDialog(additionalAmount);
				}
			}
		});

		llClickDetails.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (is_payemnt_detail) {
					if (isDetailShow) {
						ll_addtional_details.setVisibility(View.GONE);
						isDetailShow = false;
					} else {
						ll_addtional_details.setVisibility(View.VISIBLE);
						isDetailShow = true;
					}

					showPaymentDetails(additionalAmount);
				} else {
					ll_addtional_details.setVisibility(View.GONE);
				}
			}
		});
		sharedPreferences_name = getString(R.string.shared_preferences_name);
		SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(sharedPreferences_name, 0);

		utils.setSharedPreferences(sharedPreferences);

		memberid = Long.parseLong(utils.getMemberId());

		isRenew = sharedPreferences.getString(Utils.IS_RENEWAL,"0");
		payNowView.setVisibility(View.VISIBLE);
		responseScrollLayout.setVisibility(View.GONE);

		btnnb.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				try{
				if (Double.parseDouble(txtnewamount.getText().toString()) > 0) {
					if (terms.isChecked() == true&& privacy.isChecked() == true) {
						if(Utils.isOnline(MakeMyPayments_Billdesk.this)){
							TrackId = "";
							if(Utils.isOnline(MakeMyPayments_Billdesk.this)){
								if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
									insertBeforeWithTrackId = new InsertBeforeWithTrackId();
									insertBeforeWithTrackId.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String) null);

								} else {

									insertBeforeWithTrackId = new InsertBeforeWithTrackId();
									insertBeforeWithTrackId.execute((String) null);
								}
							}

						}
						else{
							Toast.makeText(MakeMyPayments_Billdesk.this,
									getString(R.string.app_please_wait_label),
									Toast.LENGTH_LONG).show();


						}
						/*if(Utils.isOnline(MakeMyPayments_Billdesk.this)){
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
								getpaymentgatewaysdetails = new PaymentGateWayDetails();
								getpaymentgatewaysdetails.executeOnExecutor(
										AsyncTask.THREAD_POOL_EXECUTOR, (String) null);
							} else {
								getpaymentgatewaysdetails = new PaymentGateWayDetails();
								getpaymentgatewaysdetails.execute((String) null);
							}
						}
						else{
							if(is_activity_running)
							AlertsBoxFactory.showAlert("Please connect to internet !!", MakeMyPayments_Billdesk.this);
						}*/
					} else {
						Toast.makeText(MakeMyPayments_Billdesk.this,
								"Please accept the terms and condition",
								Toast.LENGTH_LONG).show();
						return;
					}

				} else {
					if(is_activity_running)
					AlertsBoxFactory
							.showAlert(
									"Due to some data mismatch we are unable to process your request\n Please contact admin",
									MakeMyPayments_Billdesk.this);
				}
				}
				catch (Exception e) {
					// TODO: handle exception
					if(is_activity_running)
					AlertsBoxFactory
					.showAlert(
							"Due to some data mismatch we are unable to process your request\n Please contact admin",
							MakeMyPayments_Billdesk.this);
				}
			}
		});

		linnhome = (LinearLayout) findViewById(R.id.inn_banner_home);
		linnprofile = (LinearLayout) findViewById(R.id.inn_banner_profile);
		linnnotification = (LinearLayout) findViewById(R.id.inn_banner_notification);

		linnhome.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MakeMyPayments_Billdesk.this.finish();
				Intent i = new Intent(MakeMyPayments_Billdesk.this, IONHome.class);
				startActivity(i);
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				BaseApplication.getEventBus().post(
						new FinishEvent("PlanCalculatorActivity"));
				BaseApplication.getEventBus().post(
						new FinishEvent("RenewPackage"));
				BaseApplication.getEventBus().post(
						new FinishEvent(Utils.Last_Class_Name));
			}
		});

		linnprofile.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MakeMyPayments_Billdesk.this.finish();
				Intent i = new Intent(MakeMyPayments_Billdesk.this, Profile.class);
				startActivity(i);
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				BaseApplication.getEventBus().post(
						new FinishEvent("PlanCalculatorActivity"));
				BaseApplication.getEventBus().post(
						new FinishEvent("RenewPackage"));
				BaseApplication.getEventBus().post(
						new FinishEvent(Utils.Last_Class_Name));
			}
		});

		linnnotification.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MakeMyPayments_Billdesk.this.finish();
				Intent i = new Intent(MakeMyPayments_Billdesk.this,
						NotificationListActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				BaseApplication.getEventBus().post(
						new FinishEvent("PlanCalculatorActivity"));
				BaseApplication.getEventBus().post(
						new FinishEvent("RenewPackage"));
				BaseApplication.getEventBus().post(
						new FinishEvent(Utils.Last_Class_Name));
			}
		});

		linnhelp = (LinearLayout) findViewById(R.id.inn_banner_help);

		linnhelp.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				MakeMyPayments_Billdesk.this.finish();
				Intent i = new Intent(MakeMyPayments_Billdesk.this, HelpActivity.class);
				startActivity(i);
				overridePendingTransition(R.anim.slide_in_left,
						R.anim.slide_out_right);
				BaseApplication.getEventBus().post(
						new FinishEvent("PlanCalculatorActivity"));
				BaseApplication.getEventBus().post(
						new FinishEvent("RenewPackage"));
				BaseApplication.getEventBus().post(
						new FinishEvent(Utils.Last_Class_Name));
			}
		});

		/*if(Utils.isOnline(MakeMyPayments_Billdesk.this)){
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				getpaymentgatewaysdetails = new PaymentGateWayDetails();
				getpaymentgatewaysdetails.executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, (String) null);

			} else {
				getpaymentgatewaysdetails = new PaymentGateWayDetails();
				getpaymentgatewaysdetails.execute((String) null);
			}
		}
		else{
			if(is_activity_running)
			AlertsBoxFactory.showAlert("Please connect to internet !!", MakeMyPayments_Billdesk.this);
		}*/

	}

	@Override
	protected void onResume() {
		super.onResume();
		is_activity_running=true;
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		is_activity_running=false;
	}

	private class InsertBeforeWithTrackId extends AsyncTask<String,Void,Void> implements OnCancelListener
	{

		ProgressHUD mProgressHUD;
		PaymentsObj paymentsObj = new PaymentsObj();

		@Override
		protected void onPreExecute() {
			if(is_activity_running){
				mProgressHUD = ProgressHUD
						.show(MakeMyPayments_Billdesk.this,
								getString(R.string.app_please_wait_label), true,
								true, this);
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			if(is_activity_running)
				mProgressHUD.dismiss();
			insertBeforeWithTrackId = null;
		}

		@Override
		protected Void doInBackground(String... strings) {
			try {
				// setCurrDateTime();
				// Log.i(" >>>>> ",getCurrDateTime());

				InsertBeforeWithTrackCaller caller = new InsertBeforeWithTrackCaller(
						getApplicationContext().getResources().getString(
								R.string.WSDL_TARGET_NAMESPACE),
						getApplicationContext().getResources().getString(
								R.string.SOAP_URL), getApplicationContext()
						.getResources().getString(
								R.string.METHOD_BEFORE_MEMBER_PAY_WITH_TRACKID),true);

				paymentsObj.setMemberId(Long.valueOf(utils.getMemberId()));
				paymentsObj.setTrackId(TrackId);
				paymentsObj.setAmount(txtnewamount.getText().toString().trim());
				paymentsObj.setPackageName(txtnewpackagename.getText().toString());
				paymentsObj.setServiceTax(ServiceTax);
				paymentsObj.setDiscount_Amount(additionalAmount.getDiscountAmount());
				paymentsObj.setBankcode("BD");


				if(Utils.pg_sms_request){
					if(Utils.pg_sms_uniqueid.length()>0){
						paymentsObj.setPg_sms_unique_id(Utils.pg_sms_uniqueid);
					}
					else{
						paymentsObj.setPg_sms_unique_id(null);
					}
				}
				else{
					paymentsObj.setPg_sms_unique_id(null);
				}
				paymentsObj.setIs_renew(isRenew);

				caller.setPaymentdata(paymentsObj);

				caller.join();
				caller.start();
				rslt = "START";

				while (rslt == "START") {
					try {
						Thread.sleep(10);
					} catch (Exception ex) {
					}
				}

			} catch (Exception e) {
				/* AlertsBoxFactory.showAlert(rslt,context ); */
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			InsertBeforePayemnt = null;

			if(is_activity_running)
				mProgressHUD.dismiss();
			insertBeforeWithTrackId = null;

			if (rslt.trim().equalsIgnoreCase("ok")) {

				Log.e("RESPONSE TRACKID",":"+ MakeMyPayments_Billdesk.responseMsg);
				TrackId = MakeMyPayments_Billdesk.responseMsg;

				if(TrackId!=null && TrackId.length()>0 && !TrackId.equalsIgnoreCase("null") ) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						new GetBillDeskAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String) null);
					} else {
						new GetBillDeskAsyncTask().execute((String) null);
					}
				}else{
					AlertsBoxFactory.showAlert("TrackId not generated. Please try Again !!!", MakeMyPayments_Billdesk.this);
				}

			} else {
				if(is_activity_running)
					AlertsBoxFactory.showAlert(rslt, context);
				return;
			}
		}
	}
	private class PaymentGateWayDetails extends AsyncTask<String, Void, Void>
			implements OnCancelListener {

		ProgressHUD mProgressHUD;

		protected void onPreExecute() {

			mProgressHUD = ProgressHUD
					.show(MakeMyPayments_Billdesk.this,getString(R.string.app_please_wait_label), true,true, this);
			Utils.log("3 Progress", "start");
		}

		@Override
		protected void onCancelled() {
			mProgressHUD.dismiss();
			getpaymentgatewaysdetails = null;
		}

		protected void onPostExecute(Void unused) {
			Utils.log("3 Progress", "end");
			if(mProgressHUD!=null&&mProgressHUD.isShowing())
			mProgressHUD.dismiss();
			getpaymentgatewaysdetails = null;

			if (rslt.trim().equalsIgnoreCase("ok")) {
				try {
					if(Utils.isOnline(MakeMyPayments_Billdesk.this)){
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
							new InsertBeforePayemnt().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,(String) null);
				         }
					 else
						{
						 new InsertBeforePayemnt().execute((String) null);
						}
					}
						else{
							if(is_activity_running)
							AlertsBoxFactory.showAlert("Please connect to internet !!", MakeMyPayments_Billdesk.this);
						}

					TrackId = adjTrackval;
					// Log.i(">>>>TrackId<<<<", TrackId);
				} catch (NumberFormatException nue) {
					RadioGroup radioGroup = (RadioGroup) findViewById(R.id.radioPayMode);
					radioGroup.clearCheck();
					// Log.i(">>>>>New PLan Rate<<<<<<", adjTrackval);
				}

			} else {
				AlertsBoxFactory.showAlert("Invalid web-service response "
						+ rslt, context);
			}
			this.cancel(true);
		}

		@Override
		protected Void doInBackground(String... arg0) {

			try {
				PaymentGatewayCaller adjCaller = new PaymentGatewayCaller(
						getApplicationContext().getResources().getString(
								R.string.WSDL_TARGET_NAMESPACE),
						getApplicationContext().getResources().getString(
								R.string.SOAP_URL), getApplicationContext()
								.getResources().getString(
										R.string.METHOD_GET_TRANSACTIONID_WITH_BANK_NAME),"BD");
				adjCaller.setMemberId(utils.getMemberId());
				// adjCaller.setAreaCode(AreaCode);
				// adjCaller.setAreaCodeFilter(AreaCodeFilter);
				adjCaller.setTopup_falg(false);

				adjCaller.join();
				adjCaller.start();
				rslt = "START";

				while (rslt == "START") {
					try {
						Thread.sleep(10);
					} catch (Exception ex) {
					}
				}

			} catch (Exception e) {
				/*
				 * AlertsBoxFactory.showErrorAlert("Error web-service response "
				 * + e.toString(), context);
				 */
			}
			return null;
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			mProgressHUD.dismiss();

		}

	}

	public String generateRandomMerchantTransactionId() {
		BigInteger b = new BigInteger(64, new Random());
		String tId = b.toString();
		return tId;
	}



	/* Insert Before Going to Payment Gateway */

	private class InsertBeforePayemnt extends AsyncTask<String, Void, Void>
			implements OnCancelListener {

		ProgressHUD mProgressHUD;
		PaymentsObj paymentsObj = new PaymentsObj();

		protected void onPreExecute() {

		mProgressHUD = ProgressHUD.show(MakeMyPayments_Billdesk.this,getString(R.string.app_please_wait_label), true,true, this);
	 }

		@Override
		protected void onCancelled() {
			if(is_activity_running)
			mProgressHUD.dismiss();
			InsertBeforePayemnt = null;
			// submit.setClickable(true);
		}

		protected void onPostExecute(Void unused) {
			if(is_activity_running)
			mProgressHUD.dismiss();
			// submit.setClickable(true);
			InsertBeforePayemnt = null;

			Utils.log("Billdesk result",":"+rslt);
			if (rslt.trim().equalsIgnoreCase("ok")) {
				//params_to_send_payu();
				if(Utils.isOnline(MakeMyPayments_Billdesk.this)){
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
						 new GetBillDeskAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (String) null);
					} else {
						 new GetBillDeskAsyncTask().execute((String) null);
					}
				}
				else
				{
					if(is_activity_running)

					AlertsBoxFactory.showAlert("Please connect to internet !!", MakeMyPayments_Billdesk.this);
				}

			} else
			   {
				if(is_activity_running)

				AlertsBoxFactory.showAlert(rslt, context);
				return;
			   }
		}

		@SuppressLint("WrongThread")
		@Override
		protected Void doInBackground(String... params) {
			try {
				// setCurrDateTime();
				// Log.i(" >>>>> ",getCurrDateTime());

				BeforePaymentInsertCaller caller = new BeforePaymentInsertCaller(
						getApplicationContext().getResources().getString(
								R.string.WSDL_TARGET_NAMESPACE),
						getApplicationContext().getResources().getString(
								R.string.SOAP_URL), getApplicationContext()
								.getResources().getString(
										R.string.METHOD_BEFORE_MEMBER_PAYMENTS),true);

				paymentsObj.setMemberId(Long.valueOf(utils.getMemberId()));
				paymentsObj.setTrackId(TrackId);
				paymentsObj.setAmount(txtnewamount.getText().toString().trim());
				paymentsObj.setPackageName(txtnewpackagename.getText().toString());
				paymentsObj.setServiceTax(ServiceTax);
				paymentsObj.setDiscount_Amount(additionalAmount.getDiscountAmount());
				caller.setPaymentdata(paymentsObj);
				paymentsObj.setIs_renew(isRenew);
				paymentsObj.setUpdate_from(UpdateFrom);

				caller.join();
				caller.start();
				rslt = "START";

				while (rslt == "START") {
					try {
						  Thread.sleep(10);
					   } catch (Exception ex) {
					}
				}

			} catch (Exception e) {
				/* AlertsBoxFactory.showAlert(rslt,context ); */
				}
			return null;
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			if(is_activity_running)
			mProgressHUD.dismiss();
		}
	}

	/* Insert Before Going to Payment Gateway */

	private class InsertAfterPayemnt extends AsyncTask<String, Void, Void> implements OnCancelListener {

		ProgressHUD mProgressHUD;
		PaymentsObj paymentsObj = new PaymentsObj();

		protected void onPreExecute() {

			mProgressHUD = ProgressHUD
					.show(MakeMyPayments_Billdesk.this,getString(R.string.app_please_wait_label), true,true, this);
		}

		@Override
		protected void onCancelled() {
			if(is_activity_running)
			mProgressHUD.dismiss();
			InsertBeforePayemnt = null;
			// submit.setClickable(true);
		}

		protected void onPostExecute(Void unused) {
			if(is_activity_running){
			if(mProgressHUD!=null&&mProgressHUD.isShowing())
			mProgressHUD.dismiss();
			}
			// submit.setClickable(true);
			InsertBeforePayemnt = null;

			if (rslt.trim().equalsIgnoreCase("ok")) {
				// Utils.log("Now in renew",":"+additionalAmount.getAdditionalAmountType());

				if (TxStatus.equalsIgnoreCase("Success")) {
					if (additionalAmount != null) {
						if (additionalAmount.getAdditionalAmountType() != null) {
							if (additionalAmount.getAdditionalAmountType()
									.length() > 0) {
								if (additionalAmount.getAdditionalAmountType()
										.contains("#")) {
									String split[] = additionalAmount
											.getAdditionalAmountType().split(
													"#");
									if (split.length > 0) {
										type = split[1];
									}
								}
							} else {

							}
						}
					} else {

					}
					RenewalProcessWebService = new RenewalProcessWebService();
					 if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
					 {
						 RenewalProcessWebService.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
					 }
					 else{
						 RenewalProcessWebService.execute((String) null);
					 }
				} else {
					MakeMyPayments_Billdesk.this.finish();

					Intent intent = new Intent(getApplicationContext(),TransResponseBillDesk.class);
					intent.putExtra("order_id", TxId);
					intent.putExtra("tracking_id", TxId);
					intent.putExtra("transStatus", TxStatus);
					intent.putExtra("bank_ref_no", issuerRefNo);
					intent.putExtra("amount", txtnewamount.getText().toString());
					startActivity(intent);
					overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
					// MakeMyPayments.this.finish();

				}


			} else {
				if(is_activity_running)
				AlertsBoxFactory.showAlert(rslt, context);
				return;
			}
		}

		@Override
		protected Void doInBackground(String... params) {
			try {
				// setCurrDateTime();
				// Log.i(" >>>>> ",getCurrDateTime());

				AfterInsertPaymentsCaller caller = new AfterInsertPaymentsCaller(
						getApplicationContext().getResources().getString(
								R.string.WSDL_TARGET_NAMESPACE),
						getApplicationContext().getResources().getString(
								R.string.SOAP_URL), getApplicationContext()
								.getResources().getString(
										R.string.METHOD_AFTER_MEMBER_PAYMENTS),true);

				paymentsObj.setAuthIdCode(authIdCode);
				paymentsObj.setTxId(TxId);
				paymentsObj.setTxStatus(TxStatus);
				paymentsObj.setPgTxnNo(pgTxnNo);
				paymentsObj.setIssuerRefNo(issuerRefNo);
				paymentsObj.setTxMsg(TxMsg);

				caller.setPaymentdata(paymentsObj);

				caller.join();
				caller.start();
				rslt = "START";

				while (rslt == "START") {
					try {
						Thread.sleep(10);
					} catch (Exception ex) {
					}
				}

			} catch (Exception e) {
				/* AlertsBoxFactory.showAlert(rslt,context ); */
			}
			return null;
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			if(is_activity_running)
			mProgressHUD.dismiss();

		}
	}

	/* For Renewal Process */

	private class RenewalProcessWebService extends
			AsyncTask<String, Void, Void> implements OnCancelListener {

		ProgressHUD mProgressHUD1;
		PaymentsObj paymentsObj = new PaymentsObj();

		protected void onPreExecute() {

			mProgressHUD1 = ProgressHUD
					.show(MakeMyPayments_Billdesk.this,getString(R.string.app_please_wait_label), true,true, this);
		}

		@Override
		protected void onCancelled() {
			if(is_activity_running)
			this.mProgressHUD1.dismiss();
			RenewalProcessWebService = null;
			// submit.setClickable(true);
		}

		protected void onPostExecute(Void unused) {

			//if(mProgressHUD1!=null&&mProgressHUD1.isShowing())
			if(is_activity_running)
				mProgressHUD1.dismiss();
			// submit.setClickable(true);
			RenewalProcessWebService = null;

			if (rslt.trim().equalsIgnoreCase("ok")) {
				// finish();
				MakeMyPayments_Billdesk.this.finish();
				/*Intent intent = new Intent(getApplicationContext(),TransResponseBillDesk.class);
				intent.putExtra("order_id", TxId);
				intent.putExtra("tracking_id", authIdCode);
				intent.putExtra("transStatus", TxStatus);
				intent.putExtra("bank_ref_no", issuerRefNo);
				intent.putExtra("amount", txtnewamount.getText().toString());

				startActivity(intent);*/
				overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

				/*Intent intent = new Intent(getApplicationContext(),
						TransResponse.class);
				intent.putExtra("jsvalue",
						citrusSSLLibrary.getWebClientJsResponse());
				startActivity(intent);*/
				/*
				 * Intent intent = new
				 * Intent(MakeMyPayments.this,IONHome.class);
				 * startActivity(intent);
				 */
			} else {
				if(is_activity_running)
				AlertsBoxFactory.showAlert(rslt, context);
				return;
			}
		}

		@SuppressLint("WrongThread")
		@Override
		protected Void doInBackground(String... params) {
			try {
				// setCurrDateTime();
				// Log.i(" >>>>> ",getCurrDateTime());

				/*
				 * RenewalCaller caller = new RenewalCaller(
				 * getApplicationContext().getResources().getString(
				 * R.string.WSDL_TARGET_NAMESPACE),
				 * getApplicationContext().getResources().getString(
				 * R.string.SOAP_URL), getApplicationContext()
				 * .getResources().getString(
				 * R.string.METHOD_MYPAGE_RENEWAL_PROCESS))
				 */;
				RenewalCaller caller = new RenewalCaller(
						getApplicationContext().getResources().getString(
								R.string.WSDL_TARGET_NAMESPACE),
						getApplicationContext().getResources().getString(
								R.string.SOAP_URL),
						getApplicationContext().getResources().getString(
								R.string.METHOD_RENEWAL_REACTIVATE_METHOD));

				paymentsObj.setMobileNumber(utils.getMobileNoPrimary());
				paymentsObj.setSubscriberID(utils.getMemberLoginID());
				paymentsObj.setPlanName(txtnewpackagename.getText().toString());
				paymentsObj.setPaidAmount(Double.parseDouble(txtnewamount.getText().toString().trim()));
				paymentsObj.setTrackId(TrackId);
				// System.out.println("-------------Change Package :-----------"
				// + Changepack);
				paymentsObj.setIsChangePlan(Changepack);
				paymentsObj.setActionType(UpdateFrom);
				paymentsObj.setPaymentId(authIdCode);
				//paymentsObj.setPaymentId("123456");
				paymentsObj.setTxMsg(TxMsg);

				caller.setPaymentdata(paymentsObj);
				caller.setRenewalType(type);

				caller.join();
				caller.start();
				rslt = "START";

				while (rslt == "START") {
					try {
						Thread.sleep(10);
					} catch (Exception ex) {
					}
				}

			} catch (Exception e) {
				/* AlertsBoxFactory.showAlert(rslt,context ); */
			}
			return null;
		}

		/*
		 * @Override public void onClick(View v) { // TODO Auto-generated method
		 * stub
		 *
		 * }
		 */

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			mProgressHUD1.dismiss();
		}
	}



	public class GetAllDetailsAsyncTask extends AsyncTask<String, Void, Void>
			implements OnCancelListener {

		ProgressHUD mProgressHUD;

		String AppVersion = "";

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub

			mProgressHUD = ProgressHUD
					.show(MakeMyPayments_Billdesk.this,getString(R.string.app_please_wait_label), true,true, this);

			PackageInfo pInfo = null;

			try {
				pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				AppVersion = pInfo.versionName;
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		@Override
		protected Void doInBackground(String... params) {
			// TODO Auto-generated method stub
			GetPhoneDetailsSOAP getPhoneDetailsSOAP = new GetPhoneDetailsSOAP(
					getApplicationContext().getResources().getString(
							R.string.WSDL_TARGET_NAMESPACE),
					getApplicationContext().getResources().getString(
							R.string.SOAP_URL), getApplicationContext()
							.getResources().getString(
									R.string.METHOD_UPDATE_PHONE_DETAILS));

			try {
				getPhoneDetailsSOAP.getPhoneDetails(memberid,
						Build.MANUFACTURER.toString(), AppVersion);
			} catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SocketException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SoapFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if(is_activity_running){
			if(mProgressHUD!=null&&mProgressHUD.isShowing())
			mProgressHUD.dismiss();
			}
			InsertBeforePayemnt = new InsertBeforePayemnt();
			InsertBeforePayemnt.execute((String) null);
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			// TODO Auto-generated method stub
			if(is_activity_running)
			mProgressHUD.dismiss();
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub

		this.finish();
		overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
		/*
		 * Intent i = new Intent(MakeMyPayments.this,RenewPackage.class);
		 * startActivity(i); overridePendingTransition(R.anim.slide_in_left,
		 * R.anim.slide_out_right);
		 */
	}

	public void showPaymentDetailsDialog(AdditionalAmount additionalAmount) {
		if (additionalAmount != null) {
			final Dialog dialog = new Dialog(MakeMyPayments_Billdesk.this);
			dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
			// tell the Dialog to use the dialog.xml as it's layout description
			dialog.setContentView(R.layout.dialog_additional_amount);

			int width = 0;
			int height = 0;

			Point size = new Point();
			WindowManager w = ((Activity) MakeMyPayments_Billdesk.this).getWindowManager();

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
				w.getDefaultDisplay().getSize(size);
				width = size.x;
				height = size.y;
			} else {
				Display d = w.getDefaultDisplay();
				width = d.getWidth();
				height = d.getHeight();
				;
			}

			LinearLayout ll_package_rate, ll_add_amt, ll_add_reason, ll_discount_amt, ll_fine_amount, ll_days_fine_amt, ll_discount_per, ll_final_amt;

			TextView tv_package_rate, tv_add_amt, tv_add_reason, tv_discount_amt, tv_fine_amount, tv_days_fine_amt, tv_discount_per, tv_final_amt;

			ll_package_rate = (LinearLayout) dialog.findViewById(R.id.ll_package_rate);
			ll_add_amt = (LinearLayout) dialog.findViewById(R.id.ll_add_amt);
			ll_add_reason = (LinearLayout) dialog.findViewById(R.id.ll_add_reason);
			ll_discount_amt = (LinearLayout) dialog.findViewById(R.id.ll_discount_amt);
			ll_fine_amount = (LinearLayout) dialog.findViewById(R.id.ll_fine_amt);
			ll_days_fine_amt = (LinearLayout) dialog.findViewById(R.id.ll_days_fine_amt);
			ll_discount_per = (LinearLayout) dialog.findViewById(R.id.ll_discount_per);
			ll_final_amt = (LinearLayout) dialog.findViewById(R.id.ll_final_amount);

			tv_package_rate = (TextView) dialog.findViewById(R.id.tv_package_rate);
			tv_add_amt = (TextView) dialog.findViewById(R.id.tv_add_amt);
			tv_add_reason = (TextView) dialog.findViewById(R.id.tv_add_reason);
			tv_discount_amt = (TextView) dialog.findViewById(R.id.tv_discount_amt);
			tv_fine_amount = (TextView) dialog.findViewById(R.id.tv_fine_amt);
			tv_days_fine_amt = (TextView) dialog.findViewById(R.id.tv_days_fine_amt);
			tv_discount_per = (TextView) dialog.findViewById(R.id.tv_discount_per);
			tv_final_amt = (TextView) dialog.findViewById(R.id.tv_final_amount);

			if (Double.valueOf(additionalAmount.getPackageRate()) > 0) {
				ll_package_rate.setVisibility(View.VISIBLE);
				tv_package_rate.setText(additionalAmount.getPackageRate());
			} else {
				ll_package_rate.setVisibility(View.GONE);
			}

			if (Double.valueOf(additionalAmount.getAdditionalAmount()) > 0) {
				ll_add_amt.setVisibility(View.VISIBLE);
				tv_add_amt.setText(additionalAmount.getAdditionalAmount());
			} else {
				ll_add_amt.setVisibility(View.GONE);

			}

			if (additionalAmount.getAdditionalAmountType().length() > 0) {
				ll_add_reason.setVisibility(View.GONE);
				tv_add_reason.setText(additionalAmount
						.getAdditionalAmountType());
			} else {

				ll_add_reason.setVisibility(View.GONE);

			}

			if (Double.valueOf(additionalAmount.getDiscountAmount()) > 0) {
				ll_discount_amt.setVisibility(View.VISIBLE);
				tv_discount_amt.setText(additionalAmount.getDiscountAmount());
			} else {

				ll_discount_amt.setVisibility(View.GONE);

			}

			if (Double.valueOf(additionalAmount.getFineAmount()) > 0) {
				ll_fine_amount.setVisibility(View.VISIBLE);
				tv_fine_amount.setText(additionalAmount.getFineAmount());
			} else {

				ll_fine_amount.setVisibility(View.GONE);

			}

			if (Double.valueOf(additionalAmount.getDaysFineAmount()) > 0) {
				ll_days_fine_amt.setVisibility(View.VISIBLE);
				tv_days_fine_amt.setText(additionalAmount.getDaysFineAmount());
			} else {

				ll_days_fine_amt.setVisibility(View.GONE);

			}
			if (additionalAmount.getDiscountPercentage().length() > 0) {
				if (Double.valueOf(additionalAmount.getDiscountPercentage()) > 0) {
					ll_discount_per.setVisibility(View.VISIBLE);
					tv_discount_per.setText(additionalAmount.getDiscountPercentage());
				} else {
					ll_discount_per.setVisibility(View.GONE);
				}
			} else {
				ll_discount_per.setVisibility(View.GONE);
			}

			if (Double.valueOf(additionalAmount.getFinalcharges()) > 0) {
				ll_final_amt.setVisibility(View.VISIBLE);
				tv_final_amt.setText(additionalAmount.getFinalcharges());
			} else {
				ll_final_amt.setVisibility(View.GONE);
			}
			Button dialogButton = (Button) dialog.findViewById(R.id.btnSubmit);

			dialogButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					dialog.dismiss();
				}
			});

			dialog.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
			dialog.getWindow().setLayout((width / 2) + (width / 2) / 2,
					LayoutParams.WRAP_CONTENT);
			dialog.show();
		}
	}

	public void showPaymentDetails(AdditionalAmount additionalAmount) {
		if (additionalAmount != null) {
			// ll_addtional_details.setVisibility(View.VISIBLE);
			LinearLayout ll_package_rate, ll_add_amt, ll_add_reason, ll_discount_amt, ll_fine_amount, ll_days_fine_amt, ll_discount_per, ll_final_amt;

			TextView tv_package_rate, tv_add_amt, tv_add_reason, tv_discount_amt, tv_fine_amount, tv_days_fine_amt, tv_discount_per, tv_final_amt;

			ll_package_rate = (LinearLayout) findViewById(R.id.ll_package_rate);
			ll_add_amt = (LinearLayout) findViewById(R.id.ll_add_amt);
			ll_add_reason = (LinearLayout) findViewById(R.id.ll_add_reason);
			ll_discount_amt = (LinearLayout) findViewById(R.id.ll_discount_amt);
			ll_fine_amount = (LinearLayout) findViewById(R.id.ll_fine_amt);
			ll_days_fine_amt = (LinearLayout) findViewById(R.id.ll_days_fine_amt);
			ll_discount_per = (LinearLayout) findViewById(R.id.ll_discount_per);
			ll_final_amt = (LinearLayout) findViewById(R.id.ll_final_amount);

			tv_package_rate = (TextView) findViewById(R.id.tv_package_rate);
			tv_add_amt = (TextView) findViewById(R.id.tv_add_amt);
			tv_add_reason = (TextView) findViewById(R.id.tv_add_reason);
			tv_discount_amt = (TextView) findViewById(R.id.tv_discount_amt);
			tv_fine_amount = (TextView) findViewById(R.id.tv_fine_amt);
			tv_days_fine_amt = (TextView) findViewById(R.id.tv_days_fine_amt);
			tv_discount_per = (TextView) findViewById(R.id.tv_discount_per);
			tv_final_amt = (TextView) findViewById(R.id.tv_final_amount);

			if (Double.valueOf(additionalAmount.getPackageRate()) > 0) {
				ll_package_rate.setVisibility(View.VISIBLE);
				tv_package_rate.setText(additionalAmount.getPackageRate());
			} else {
				ll_package_rate.setVisibility(View.GONE);
			}

			if (Double.valueOf(additionalAmount.getAdditionalAmount()) > 0) {
				ll_add_amt.setVisibility(View.VISIBLE);
				tv_add_amt.setText(additionalAmount.getAdditionalAmount());
			} else {
				ll_add_amt.setVisibility(View.GONE);
			}

			if (additionalAmount.getAdditionalAmountType().length() > 0) {
				ll_add_reason.setVisibility(View.GONE);
				tv_add_reason.setText(additionalAmount
						.getAdditionalAmountType());
			} else {
				ll_add_reason.setVisibility(View.GONE);
			}

			if (Double.valueOf(additionalAmount.getDiscountAmount()) > 0) {
				ll_discount_amt.setVisibility(View.VISIBLE);
				tv_discount_amt.setText(additionalAmount.getDiscountAmount());
			} else {

				ll_discount_amt.setVisibility(View.GONE);

			}

			if (Double.valueOf(additionalAmount.getFineAmount()) > 0) {
				ll_fine_amount.setVisibility(View.VISIBLE);
				tv_fine_amount.setText(additionalAmount.getFineAmount());
			} else {

				ll_fine_amount.setVisibility(View.GONE);

			}

			if (Double.valueOf(additionalAmount.getDaysFineAmount()) > 0) {
				ll_days_fine_amt.setVisibility(View.VISIBLE);
				tv_days_fine_amt.setText(additionalAmount.getDaysFineAmount());
			} else {

				ll_days_fine_amt.setVisibility(View.GONE);

			}
			if (additionalAmount.getDiscountPercentage().length() > 0) {
				if (Double.valueOf(additionalAmount.getDiscountPercentage()) > 0) {
					ll_discount_per.setVisibility(View.VISIBLE);
					tv_discount_per.setText(additionalAmount
							.getDiscountPercentage());
				} else {
					ll_discount_per.setVisibility(View.GONE);
				}
			} else {
				ll_discount_per.setVisibility(View.GONE);
			}

			if (Double.valueOf(additionalAmount.getFinalcharges()) > 0) {
				ll_final_amt.setVisibility(View.VISIBLE);
				tv_final_amt.setText(additionalAmount.getFinalcharges());
			} else {
				ll_final_amt.setVisibility(View.GONE);
			}
		} else {
			ll_addtional_details.setVisibility(View.GONE);
		}
	}


	 protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	        if (requestCode == MakeMyPayments_Billdesk.REQUEST_CODE) {
	        	//String s=data.getStringExtra("status");
	        	//Utils.log("Result", ":"+s);
	        }
	        Utils.log("Back", "Result");
	    }

	 public void after_payment_json(String json){
		try {

			Utils.log("Parse", "Payment Response");
			JSONObject main_json= new JSONObject(json);
			TxId=TrackId;
			String Resmode=main_json.optString("Resmode","blank");
			TxStatus=main_json.optString("STATUS","blank");
			String Responsekey=main_json.optString("Responsekey","blank");
			pgTxnNo=main_json.optString("Responseerror","blank");
			 TxMsg=main_json.optString("Resbankcode","blank");
			issuerRefNo=main_json.optString("respbank_ref_num","blank");
			authIdCode=main_json.optString("ResTransIDPayu","blank");

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				 new InsertAfterPayemnt().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, (String) null);

			} else {
				 new InsertAfterPayemnt().execute((String) null);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block.

			TxId=TrackId;
        	TxStatus="Back pressed Cancelled";

			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
				 new InsertAfterPayemnt().executeOnExecutor(
						AsyncTask.THREAD_POOL_EXECUTOR, (String) null);

			} else {
				 new InsertAfterPayemnt().execute((String) null);
			}

			e.printStackTrace();
			Utils.log("Error Parse", "Payment Response:"+e);
		}
	 }

	 public class GetBillDeskAsyncTask extends AsyncTask<String, Void, Void>{
	    	Get_BillDesk_SignatureSOAP get_BillDesk_SignatureSOAP;
	    	String rslt="",response="";
	    	ProgressDialog dialog;
	    	@Override
	    	protected void onPreExecute() {
	    		// TODO Auto-generated method stub
	    		super.onPreExecute();
	    		dialog=new ProgressDialog(MakeMyPayments_Billdesk.this);
	    		dialog.show();
	    	}

			@SuppressLint("WrongThread")
			@Override
			protected Void doInBackground(String... params) {
				// TODO Auto-generated method stub
				get_BillDesk_SignatureSOAP=new Get_BillDesk_SignatureSOAP(getString(R.string.WSDL_TARGET_NAMESPACE), getString(R.string.SOAP_URL), getString(R.string.METHOD_GET_BILLDESK_TOKEN));
				try
				{
					rslt=get_BillDesk_SignatureSOAP.getSignature(MemberId, txtnewamount.getText().toString(), TrackId);
					response=get_BillDesk_SignatureSOAP.getResponse();
				} catch (SocketTimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SocketException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return null;
			}


			@Override
			protected void onPostExecute(Void result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				dialog.dismiss();

				if(rslt.equalsIgnoreCase("OK")){
					if(response.length()>0){
						parse_json(response);
					}
				}
			}
	    }


	 public void parse_json(String json){
	    	if(json.length()>0){
	    		try {
					JSONObject main_json=new JSONObject(json);
					JSONObject newdataset_json=main_json.getJSONObject("NewDataSet");
					if(newdataset_json.has("Table1")){
						if(newdataset_json.get("Table1") instanceof JSONArray){
							JSONArray table_json_array=newdataset_json.getJSONArray("Table1");
							Log.d("Parse","data:"+json);
							for(int i=0;i<table_json_array.length();i++){
								JSONObject json_obj=table_json_array.getJSONObject(i);
								email_id=json_obj.optString("Emailid");
								mob_number=json_obj.optString("MobileNo");
								merchant_id=json_obj.optString("MerchantId");
								security_id=json_obj.optString("securityId");
								token_req=json_obj.optString("Emailid");
								msg=json_obj.optString("strRequestStatus");
								Log.d("Email","is:"+email_id);
								Log.d("Mobile","is:"+mob_number);
								ArrayList<NameValuePair> alPostData=new  ArrayList<NameValuePair>();
								alPostData.add(new BasicNameValuePair("Token", token_req));
							//	new CommonAsyncTask(MainActivity.this, getString(R.string.TOKEN_BILLDESK_URL), alPostData).execute();
							}
						}

						if(newdataset_json.get("Table1") instanceof JSONObject){
							JSONObject json_obj=newdataset_json.getJSONObject("Table1");
							Log.d("Parse","data:"+json);

								email_id=json_obj.optString("Emailid");
								mob_number=json_obj.optString("MobileNo");
								merchant_id=json_obj.optString("MerchantId");
								security_id=json_obj.optString("securityId	");
								return_url=json_obj.optString("RU");

								msg=json_obj.optString("strRequestStatus");

								 Log.d("Email","is:"+email_id);
								Log.d("Mobile","is:"+mob_number);
								Utils.log("TrackID", "is"+TrackId);

								MyObject.additionalAmount=(additionalAmount);
								MyObject.amount=(txtnewamount.getText().toString());
								MyObject.Changepack=(Changepack);
								MyObject.TrackId=(TrackId);
								MyObject.PlanName=(txtnewpackagename.getText().toString());
								MyObject.updatefrom=(UpdateFrom);
								this.finish();
								Intent intent=new Intent(MakeMyPayments_Billdesk.this,PaymentOptions.class);

								BaseApplication.getEventBus().post(new FinishEvent(RenewPackage.class.getSimpleName()));
								BaseApplication.getEventBus().post(new FinishEvent(ChangePackage.class.getSimpleName()));

								Log.d("Server","is:"+msg);

								intent.putExtra("msg", msg);
								intent.putExtra("user-email", email_id);
								intent.putExtra("user-mobile", mob_number);

								intent.putExtra("amount", Double.valueOf(txtnewamount.getText().toString()));
								intent.putExtra("callback",myObject);

								startActivity(intent);
								//startActivityForResult(intent, REQUEST_CODE);

								//ArrayList<NameValuePair> alPostData=new  ArrayList<NameValuePair>();
								//alPostData.add(new BasicNameValuePair("token", token_req));
								//new CommonAsyncTask2(MainActivity.this, getString(R.string.TOKEN_BILLDESK_URL), token_req).execute();
						}
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	    	}
	    }

	/*@Override
	public void AfterTransactionBillDesk(String s) {
		// TODO Auto-generated method stub
		Utils.log("after transaction", ":"+s);
		
		if(s.length()>0){
			if(s.contains("|")){
				str=s.split("|");
				for(int k=0;k<str.length;k++){
					Utils.log("Array position"+k, ":"+str[k]);
				}
				
				Utils.log("AuthStatus",":"+str[14]);
				
				if(str[14].equalsIgnoreCase("0300")){
					TxStatus="SUCCESS";
				 }else{
					if(str[14].equalsIgnoreCase("0399")){
						TxStatus="FAILED TRANSACTION";
					  }else{
						 if(str[14].equalsIgnoreCase("NA")||str[14].equalsIgnoreCase("0001")){
							TxStatus="CANCEL TRANSACTION";
						 }else{
							if(str[14].equalsIgnoreCase("0002")){
								TxStatus="PENDING TRANSACTION";
						  }
						}
					  }
				    }
				
				Intent intent = new Intent(getApplicationContext(),TransResponsePayu.class);
				intent.putExtra("order_id", str[1]); 
				intent.putExtra("tracking_id", str[2]);
				intent.putExtra("transStatus", TxStatus);
				intent.putExtra("bank_ref_no", str[3]);
				intent.putExtra("amount", txtnewamount.getText().toString());
				startActivity(intent);
			}
		}
	}*/
}
