/* Copyright (c) 2015 Intel Corporation. All rights reserved.
* Use of this source code is governed by a MIT-style license that can be
* found in the LICENSE file.
*/

package com.intel.cordovacontext;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.location.Location;

import com.google.gson.Gson;
import com.intel.context.Sensing;
import com.intel.context.error.ContextError;
import com.intel.context.exception.ContextProviderException;
import com.intel.context.item.ContextType;
import com.intel.context.item.Item;
import com.intel.context.item.LocationCurrent;
import com.intel.context.item.ActivityRecognition;

import com.intel.context.item.TerminalContext;
import com.intel.context.item.activityrecognition.PhysicalActivity;
import com.intel.context.option.activity.ActivityOptionBuilder;
import com.intel.context.option.activity.Mode;
import com.intel.context.option.activity.ReportType;
import com.intel.context.sensing.ContextTypeListener;
import com.intel.context.sensing.GetItemCallback;
import com.intel.context.sensing.InitCallback;
import com.intel.context.sensing.SensingEvent;
import com.intel.context.sensing.SensingStatusListener;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class CordovaContext extends CordovaPlugin {
	// Using enum to organize the code
	// This enum represent all of the supported context type
	private static enum CONTEXT_TYPE {
		// "TAPPING", "SHAKING", "GESTURE_FLICK", "GESTURE_EAR_TOUCH"
		MUSIC(ContextType.MUSIC),
		LOCATION(ContextType.LOCATION),
		TERMINAL_CONTEXT(ContextType.TERMINAL_CONTEXT),
		CALL(ContextType.CALL),
		ACTIVITY_RECOGNITION(ContextType.ACTIVITY_RECOGNITION),
		INSTANT_ACTIVITY(ContextType.INSTANT_ACTIVITY),
		AUDIO(ContextType.AUDIO),
		PEDOMETER(ContextType.PEDOMETER),
		BATTERY(ContextType.BATTERY),
		DEVICE_INFORMATION(ContextType.DEVICE_INFORMATION),
		NETWORK(ContextType.NETWORK),
		DEVICE_POSITION(ContextType.DEVICE_POSITION),
		TAPPING(ContextType.TAPPING),
		SHAKING(ContextType.SHAKING),
		GESTURE_FLICK(ContextType.GESTURE_FLICK),
		GESTURE_EAR_TOUCH(ContextType.GESTURE_EAR_TOUCH);

		private final ContextType ct;

		CONTEXT_TYPE(ContextType ct) {
			this.ct = ct;
		}

		public ContextType getValue() {
			return ct;
		}
	}

	private CallbackContext cb;

	private static final int ECHO_ACITIVTY = 1;
	private String IntentAction = "";

	private Sensing mSensing;
	private boolean sensingDaemonStarted = false;

	private static final String ECHO_ACTIVITY_INTENT_ACTION = "com.intel.context.settings.echoactivity";

	private HashMap<String, CallbackContext> CallbackCache = new HashMap<String, CallbackContext>();
	private ContextTypeListener mListener;

	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		// Get the cordova context
		mSensing = new Sensing(this.cordova.getActivity().getApplicationContext(), new MySensingListener());
		mListener = new CordovaContextListner();

	}

	@Override
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		cb = callbackContext;
		if (action.equals("start")) {
			mSensing.start(new InitCallback() {

				@Override
				public void onSuccess() {
					cb.success();
					sensingDaemonStarted = true;
				}

				@Override
				public void onError(ContextError error) {
					cb.error(error.getMessage());
				}
			});
			return true;
		}
		if (sensingDaemonStarted == false) {
			cb.error("Sensing daemon not started");
			return false;
		} else {
			if (action.equals("enablesensing")) {
				Bundle bundle = null;
				String ctxSensing = args.get(0).toString();
				if (args.length() == 0) {
					cb.error("Please specify a sensing context");
					return false;
				}

				try {
					ContextType ctx = getContextType(ctxSensing);
					if (ctx == ContextType.ACTIVITY_RECOGNITION) {
						ActivityOptionBuilder activitySettings = new ActivityOptionBuilder();
				        activitySettings.setMode(Mode.NORMAL);
				        activitySettings.setReportType(ReportType.FREQUENCY);

				        bundle = activitySettings.toBundle();
					}
					mSensing.enableSensing(ctx, bundle);
					if (CallbackCache.containsKey(ctx.toString())) {
						cb.error("Sensor already enabled");
						return false;
					}
					mSensing.addContextTypeListener(ctx, mListener);
					// cb.success();
					CallbackCache.put(ctx.toString(), cb);
					return true;
				} catch (ContextProviderException e) {
					// TODO Auto-generated catch block
					cb.error("Error enabling sensing.  " + e.getLocalizedMessage());
					return true;
				}
			} else if (action.equals("disablesensing")) {
				try {
					String ctxSensing = args.get(0).toString();
					if (ctxSensing != null && !ctxSensing.isEmpty()) {

						ContextType ctx = getContextType(ctxSensing);
						if (CallbackCache.containsKey(ctx.toString())) {
							CallbackCache.remove(ctx.toString());
						}
						mSensing.disableSensing(ctx);
					} else {
						mSensing.disableSensing();
					}

					cb.success();
					return true;
				} catch (ContextProviderException e) {
					// TODO Auto-generated catch block
					cb.error("Error disabling sensing " + e.getLocalizedMessage());
					return true;
				}
			} else if (action.equals("getItem")) {
				if (args.length() == 0) {
					cb.error("Please specify a sensing context");
					return false;
				}
				Bundle bundle = null;
				String ctxSensing = args.get(0).toString();
				try {
					ContextType ctx = getContextType(ctxSensing);
					// mSensing.enableSensing(ctx, bundle);
					mSensing.getItem(ctx, new GetItemCallback() {

						@Override
						public void onResult(Item state) {
							JSONObject data = processItemState(state, false);
							cb.success(data);
						}

						@Override
						public void onError(ContextError error) {
							cb.error(error.getMessage());
						}
					});
					return true;
				} catch (ContextProviderException e) {
					// TODO Auto-generated catch block
					cb.error("Error getting sensing item.  " + e.getLocalizedMessage());
					return true;
				}
			} else if (action.equals("stop")) {
				if (sensingDaemonStarted == false)
					return true;
				try {
					mSensing.removeContextTypeListener(mListener);
				} catch (ContextProviderException e) {
					// TODO Auto-generated catch block
					cb.error("Error stopping sensing.  " + e.getLocalizedMessage());
					return false;
				}
				mSensing.stop();
				sensingDaemonStarted = false;
				return true;
			}
		}
		return false;
	}

	private ContextType getContextType(String ctx) {
		return CONTEXT_TYPE.valueOf(ctx).getValue();
	}

	private boolean contextTypeExists(String ctx) {
		ContextType[] items = ContextType.values();

		for (int i = 0; i < items.length; i++) {
			Log.d("Ctx", items[i].toString());
			if (items[i].toString() == ctx)
				return true;
		}

		return false;
	}

	private class MySensingListener implements SensingStatusListener {

		private final String LOG_TAG = MySensingListener.class.getName();

		MySensingListener() {
		}

		@Override
		public void onEvent(SensingEvent event) {

			Log.i(LOG_TAG, "Event: " + event.getDescription());
		}

		@Override
		public void onFail(ContextError error) {
			Log.e(LOG_TAG, "Context Sensing Error: " + error.getMessage());
		}
	}

	private class CordovaContextListner implements ContextTypeListener {

		private final String LOG_TAG = CordovaContextListner.class.getName();

		public void onReceive(Item state) {
			processItemState(state, true);
		}

		public void onError(ContextError error) {
			Log.e(CordovaContextListner.class.getName(), "Error: " + error.getMessage());
		}
	}

	private JSONObject processItemState(Item state, boolean executeCallback) {
		// Covert state into a JSONObject
		// I can't find a good way to do it with the Android JSONObject,
		// So I used GSON and then parse it into JSONObject.
		JSONObject data = new JSONObject();
		String json = new Gson().toJson(state);
		try {
			data = new JSONObject(json);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (executeCallback) {
			CallbackContext callbackContext = null;

			if (CallbackCache.containsKey(state.getContextType()))
				callbackContext = CallbackCache.get(state.getContextType());

			if (callbackContext != null) {
				PluginResult result = new PluginResult(PluginResult.Status.OK, data);
				result.setKeepCallback(true);
				callbackContext.sendPluginResult(result);
			}
		}

		return data;
	}
}
