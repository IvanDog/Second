package com.example.driver;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;

public class ParkingDetailDialog extends AlertDialog {

		public ParkingDetailDialog(Context context, int theme) {
		    super(context, theme);
		}

		public ParkingDetailDialog(Context context) {
		    super(context);
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
		    super.onCreate(savedInstanceState);
		    setContentView(R.layout.dialog_parking_information);
		}
}
