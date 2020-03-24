package com.cnergee.mypage.obj;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.billdesk.sdk.LibraryPaymentStatusProtocol;
import com.cnergee.mypage.BaseApplication;
import com.cnergee.mypage.MakeMyPayments_Billdesk;
import com.cnergee.mypage.TransResponseBillDesk;
import com.cnergee.mypage.caller.AfterInsertPaymentsCaller;
import com.cnergee.mypage.caller.RenewalCaller;
import com.cnergee.mypage.utils.AlertsBoxFactory;
import com.cnergee.mypage.utils.FinishEvent;
import com.cnergee.mypage.utils.Utils;
import com.cnergee.widgets.ProgressHUD;

import all.interface_.IBillDesk;
import cnergee.myapp.shengli_pune.R;

public class MyObject implements LibraryPaymentStatusProtocol, Parcelable{
	Activity ctx;
	IBillDesk iBillDesk;
	public static String TxStatus,TxId,issuerRefNo,authIdCode,pgTxnNo,TxMsg;
	public static String PlanName,updatefrom,TrackId,amount;
	PaymentsObj paymentsObj;
	public static AdditionalAmount additionalAmount;
	InsertAfterPayemnt InsertBeforePayemnt;
	RenewalProcessWebService RenewalProcessWebService;
	public static String rslt = "",responseMsg="";
	String type = "Renew";
	Utils utils = new Utils();
	public static boolean Changepack;
		public MyObject() {
			
		}
		
		public MyObject(Parcel in) {
			
		}
		
		@Override
		public void paymentStatus(String status, Activity context) {
			
			try{
			Log.e("Status","is:"+status);
			Log.e("Changepack","is:"+Changepack);

		
			Utils.log("Class", context.getClass().getSimpleName());
			ctx=context;
			
			String sharedPreferences_name =ctx. getString(R.string.shared_preferences_name);
			SharedPreferences sharedPreferences_ = ctx.getApplicationContext().getSharedPreferences(sharedPreferences_name, 0); // 0 - for private mode
			
			utils.setSharedPreferences(sharedPreferences_);
			
			String[] str = null;
			
			BaseApplication.getEventBus().post(
					new FinishEvent(MakeMyPayments_Billdesk.class.getSimpleName()));
			//context.finish();
			
			if(status.length()>0){
				if(status.contains("|")){
					str=status.split("\\|");
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
				     }
			     }
			
				authIdCode=str[2];
				pgTxnNo=str[6];
				TxMsg=str[24];
				TxId=TrackId;
				issuerRefNo=str[3];
				
				Utils.log("TrackID", "is"+TrackId);
				
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
					 new InsertAfterPayemnt().executeOnExecutor(
							AsyncTask.THREAD_POOL_EXECUTOR, (String) null);
		
				} else {
					 new InsertAfterPayemnt().execute((String) null);
				  }
			}catch(Exception e){
				e.printStackTrace();
			}
				
					/*Intent intent = new Intent(ctx,TransResponseBillDesk.class);
					intent.putExtra("order_id", str[1]); 
					//intent.putExtra("tracking_id", str[2]);
					intent.putExtra("transaction_msg",str[24]);
					intent.putExtra("transaction_ref_no",str[2]);
					intent.putExtra("transStatus", TxStatus);
				    intent.putExtra("bank_ref_no", str[3]);
				    intent.putExtra("PG_track_id",str[6]);
				
					ctx.startActivity(intent);*/
		}

	@Override
	public void tryAgain() {

	}

	@Override
	public void onError(Exception e) {

	}

	@Override
	public void cancelTransaction() {

	}

	@Override
		public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
		}
		@Override
		public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		}



//		public static final Creator CREATOR = new Creator()
//		{
//			@Override
//			public MyObject createFromParcel(Parcel in)
//			{
//			return new MyObject(in);
//			}
//			@Override
//			public Object[] newArray(int size)
//			{
//			return new MyObject[size];
//			}
//		};


		public static final Creator CREATOR = new Creator()
		{
			@Override
			public MyObject createFromParcel(Parcel in)
			{
				return new MyObject(in);
			}
			@Override
			public Object[] newArray(int size)
			{
				return new MyObject[size];
			}
		};


		public PaymentsObj getPaymentsObj() {
			return paymentsObj;
		}
		public void setPaymentsObj(PaymentsObj paymentsObj) {
			this.paymentsObj = paymentsObj;
		}
		public AdditionalAmount getAdditionalAmount() {
			return additionalAmount;
		}
		public void setAdditionalAmount(AdditionalAmount additionalAmount) {
			this.additionalAmount = additionalAmount;
		}

		public String getAmount() {
			return amount;
		}
		public void setAmount(String amount) {
			this.amount = amount;
		}

		

		public String getUpdatefrom() {
			return updatefrom;
		}
		public void setUpdatefrom(String updatefrom) {
			this.updatefrom = updatefrom;
		}
		public boolean isChangepack() {
			return Changepack;
		}
		public void setChangepack(boolean changepack) {
			Changepack = changepack;
		}


		public String getTrackId() {
			return TrackId;
		}
		public void setTrackId(String trackId) {
			TrackId = trackId;
		}

		public String getPlanName() {
			return PlanName;
		}
		public void setPlanName(String planName) {
			PlanName = planName;
		}




		private class InsertAfterPayemnt extends AsyncTask<String, Void, Void> implements OnCancelListener {
			
			ProgressHUD mProgressHUD;
			PaymentsObj paymentsObj = new PaymentsObj();

			protected void onPreExecute() {

				mProgressHUD = ProgressHUD.show(ctx,ctx.getString(R.string.app_please_wait_label), true,true, this);
			}

			@Override
			protected void onCancelled() {
			
				mProgressHUD.dismiss();
				InsertBeforePayemnt = null;
				// submit.setClickable(true);
			}

			protected void onPostExecute(Void unused) {
				
				if(mProgressHUD!=null&&mProgressHUD.isShowing())
				mProgressHUD.dismiss();
				
				// submit.setClickable(true);
				InsertBeforePayemnt = null;

				if (rslt.trim().equalsIgnoreCase("ok")) {
					 Log.e("Now in renew",":"+additionalAmount.toString());
					Log.e("Now in renew",":"+additionalAmount.getAdditionalAmountType());
					Log.e("TxStatus",":"+TxStatus);
					if (TxStatus.equalsIgnoreCase("Success")) {
						if (additionalAmount != null) {
							if (additionalAmount.getAdditionalAmountType() != null) {
								if (additionalAmount.getAdditionalAmountType().length() > 0) {
									if (additionalAmount.getAdditionalAmountType().contains("#")) {
										String split[] = additionalAmount.getAdditionalAmountType().split("#");
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
						ctx.finish();
						Intent intent = new Intent(ctx,TransResponseBillDesk.class);
						intent.putExtra("order_id", TxId);
						intent.putExtra("tracking_id", authIdCode);
						intent.putExtra("transStatus", TxStatus);
						intent.putExtra("bank_ref_no", issuerRefNo);
						intent.putExtra("amount", amount);
						intent.putExtra("PG_track_id",pgTxnNo);
						intent.putExtra("transaction_msg",TxMsg);
						ctx.startActivity(intent);
						ctx.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
					}
				} 
			 else 
			   {
				  AlertsBoxFactory.showAlert(rslt, ctx);
				  return;
			   }
			}

			@Override
			protected Void doInBackground(String... params) {
				try {
					// setCurrDateTime();
					// Log.i(" >>>>> ",getCurrDateTime());

					AfterInsertPaymentsCaller caller = new AfterInsertPaymentsCaller(
							ctx.getResources().getString(
									R.string.WSDL_TARGET_NAMESPACE),
									ctx.getResources().getString(
									R.string.SOAP_URL), ctx
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
						} 
						catch (Exception ex) {}
					}

				} catch (Exception e) {
					 AlertsBoxFactory.showAlert(rslt,ctx);
				}
				return null;
			}

			@Override
			public void onCancel(DialogInterface dialog) {
				// TODO Auto-generated method stub
				
				mProgressHUD.dismiss();

			}
		}
		



	private class RenewalProcessWebService extends
	AsyncTask<String, Void, Void> implements OnCancelListener {

	ProgressHUD mProgressHUD1;
	PaymentsObj paymentsObj = new PaymentsObj();
	
	protected void onPreExecute() {
		rslt="";
		mProgressHUD1 = ProgressHUD.show(ctx,ctx.getString(R.string.app_please_wait_label), true,true, this);
	}

	@Override
	protected void onCancelled() {
		
		this.mProgressHUD1.dismiss();
		RenewalProcessWebService = null;
		// submit.setClickable(true);
	}

	protected void onPostExecute(Void unused) {

		//if(mProgressHUD1!=null&&mProgressHUD1.isShowing())
		
		mProgressHUD1.dismiss();
		// submit.setClickable(true);
		RenewalProcessWebService = null;

		if (rslt.trim().equalsIgnoreCase("ok")) {
			// finish();
			ctx.finish();
			
			Intent intent = new Intent(ctx,TransResponseBillDesk.class);
			intent.putExtra("order_id", TxId);
			intent.putExtra("tracking_id", authIdCode);
			intent.putExtra("transStatus", TxStatus);
			intent.putExtra("bank_ref_no", issuerRefNo);
			intent.putExtra("amount", amount);
			intent.putExtra("PG_track_id",pgTxnNo);
			intent.putExtra("transaction_msg",TxMsg);
	
			ctx.startActivity(intent);
			ctx.overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

		} else {
			
			AlertsBoxFactory.showAlert(rslt, ctx);
			return;
		}
	}

	@Override
	protected Void doInBackground(String... params) {
		try {
			
			RenewalCaller caller = new RenewalCaller(
					ctx.getResources().getString(
							R.string.WSDL_TARGET_NAMESPACE),
							ctx.getResources().getString(
							R.string.SOAP_URL),
							ctx.getResources().getString(
							R.string.METHOD_RENEWAL_REACTIVATE_METHOD));

			paymentsObj.setMobileNumber(utils.getMobileNoPrimary());
			paymentsObj.setSubscriberID(utils.getMemberLoginID());
			paymentsObj.setPlanName(PlanName);
			paymentsObj.setPaidAmount(Double.parseDouble(amount));
			paymentsObj.setTrackId(TrackId);
			// System.out.println("-------------Change Package :-----------"
			// + Changepack);
			paymentsObj.setIsChangePlan(Changepack);
			paymentsObj.setActionType(updatefrom);
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
}