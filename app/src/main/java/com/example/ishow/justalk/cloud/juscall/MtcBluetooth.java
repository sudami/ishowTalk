package com.example.ishow.justalk.cloud.juscall;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Iterator;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothHeadset;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.util.Log;

/**
 * This is a utility to detect bluetooth headset connection and establish audio
 * connection for android API >= 8. This includes a work around for API < 11 to
 * detect already connected headset before the application starts. This work
 * around would only fails if Sco audio connection is accepted but the connected
 * device is not a headset.
 * 
 * 
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class MtcBluetooth {
	protected Context mContext;

	private BluetoothAdapter mBluetoothAdapter = null;
	private BluetoothHeadset mBluetoothHeadset = null;
	private BluetoothDevice mConnectedHeadset  = null;
	private HashSet<BluetoothDevice> mBluetoothDevices = new HashSet<BluetoothDevice>();

	private AudioManager mAudioManager;
	private final String BLUETOOTH_ADDRESS = "00::00:00::00";
	private final String BLUETOOTH_NAME = "Bluetooth Headset";
	private boolean mIsCountDownOn;
	private boolean mIsStarting;
	private boolean mIsHeadsetConnected;
	private boolean mIsOnHeadsetSco;
	private boolean mIsStarted;

	private static final String TAG = "MtcBluetooth";

	/**
	 * Constructor
	 * 
	 * @param context
	 */
	public MtcBluetooth(Context context) {
		mContext = context;
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		mAudioManager = (AudioManager) context.getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			mBroadcastReceiver = new BroadcastReceiver() {
				@SuppressWarnings({ "deprecation", "synthetic-access" })
				@Override
				public void onReceive(Context context, Intent intent) {
					String action = intent.getAction();
					if (action.equals(BluetoothDevice.ACTION_ACL_CONNECTED)) {
						BluetoothDevice device = intent
								.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
						BluetoothClass bluetoothClass = device
								.getBluetoothClass();
						if (bluetoothClass != null) {
							// Check if device is a headset. Besides the 2
							// below, are
							// there other
							// device classes also qualified as headset?
							int deviceClass = bluetoothClass.getDeviceClass();
							if (deviceClass == BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE
									|| deviceClass == BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET) {
								mConnectedHeadset = device;
								if (!mIsHeadsetConnected) {
									mIsHeadsetConnected = true;
									onHeadsetConnected(device.getAddress(),
											device.getName());
								}
							}
						}

						Log.d(TAG, device.getName() + " connected");
					} else if (action
							.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
						Log.d(TAG, "Headset disconnected");

						if (mIsCountDownOn) {
							mIsCountDownOn = false;
							mCountDown.cancel();
						}

						mAudioManager.setMode(AudioManager.MODE_NORMAL);

						// override this if you want to do other thing when the
						// device
						// is disconnected.
						if (mIsHeadsetConnected) {
							mIsHeadsetConnected = false;
							onHeadsetDisconnected(mConnectedHeadset != null ? mConnectedHeadset
									.getAddress() : BLUETOOTH_ADDRESS);
						}
						mConnectedHeadset = null;
					} else if (action
							.equals(AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED)) {
						int state = intent.getIntExtra(
								AudioManager.EXTRA_SCO_AUDIO_STATE,
								AudioManager.SCO_AUDIO_STATE_ERROR);

						if (state == AudioManager.SCO_AUDIO_STATE_CONNECTED) {
							if (mIsStarting) {
								// When the device is connected before the
								// application
								// starts,
								// ACTION_ACL_CONNECTED will not be received, so
								// call
								// onHeadsetConnected here
								mIsStarting = false;
								if (!mIsHeadsetConnected) {
									mIsHeadsetConnected = true;
									onHeadsetConnected(BLUETOOTH_ADDRESS,
											BLUETOOTH_NAME);
								}
							}

							if (mIsCountDownOn) {
								mIsCountDownOn = false;
								mCountDown.cancel();
							}

							// override this if you want to do other thing when
							// Sco
							// audio is connected.
							if (!mIsOnHeadsetSco) {
								mIsOnHeadsetSco = true;
								onScoAudioConnected();
							}

							Log.d(TAG, "Sco connected");
						} else if (state == AudioManager.SCO_AUDIO_STATE_DISCONNECTED) {
							Log.d(TAG, "Sco disconnected");

							// Always receive SCO_AUDIO_STATE_DISCONNECTED on
							// call to
							// startBluetooth()
							// which at that stage we do not want to do
							// anything. Thus
							// the if condition.
							if (!mIsStarting) {
								// Need to call stopBluetoothSco(), otherwise
								// startBluetoothSco()
								// will not be successful.
								mAudioManager.stopBluetoothSco();
                                mAudioManager.setBluetoothScoOn(false);

								// override this if you want to do other thing
								// when Sco
								// audio is disconnected.
								if (mIsOnHeadsetSco) {
									mIsOnHeadsetSco = false;
									onScoAudioDisconnected();
								}
							}
						}
					}
				}
			};
			mCountDown = new CountDownTimer(10000, 1000) {

				@SuppressWarnings("synthetic-access")
				@Override
				public void onTick(long millisUntilFinished) {
					// When this call is successful, this count down timer will
					// be
					// canceled.
                    mAudioManager.setBluetoothScoOn(true);
					mAudioManager.startBluetoothSco();

					Log.d(TAG, "\nonTick start bluetooth Sco");
				}

				@SuppressWarnings("synthetic-access")
				@Override
				public void onFinish() {
					// Calls to startBluetoothSco() in onStick are not
					// successful.
					// Should implement something to inform user of this failure
					mIsCountDownOn = false;
					mAudioManager.setMode(AudioManager.MODE_NORMAL);

					Log.d(TAG, "\nonFinish fail to connect to headset audio");
				}
			};
		} else {
			mHeadsetProfileListener = new BluetoothProfile.ServiceListener() {

				/**
				 * This method is never called, even when we closeProfileProxy
				 * on onPause. When or will it ever be called???
				 */
				@Override
				public void onServiceDisconnected(int profile) {
					Log.d(TAG, "Profile listener onServiceDisconnected");
					stopBluetooth11();
				}

				@SuppressWarnings("synthetic-access")
				@TargetApi(Build.VERSION_CODES.HONEYCOMB)
				@Override
				public void onServiceConnected(int profile,
						BluetoothProfile proxy) {
					Log.d(TAG, "Profile listener onServiceConnected");

					// mBluetoothHeadset is just a headset profile,
					// it does not represent a headset device.
					mBluetoothHeadset = (BluetoothHeadset) proxy;

					// If a headset is connected before this application starts,
					// ACTION_CONNECTION_STATE_CHANGED will not be broadcast.
					// So we need to check for already connected headset.
					for (BluetoothDevice device : mBluetoothHeadset
							.getConnectedDevices()) {
						// Only one headset can be connected at a time,
						// so the connected headset is at index 0.
						if (!mBluetoothDevices.contains(device)) {
							mBluetoothDevices.add(device);
							onHeadsetConnected(device.getAddress(),
									device.getName());
						}
					}
					mHeadsetBroadcastReceiver = new BroadcastReceiver() {
						@SuppressWarnings("synthetic-access")
						@TargetApi(Build.VERSION_CODES.HONEYCOMB)
						@Override
						public void onReceive(Context context, Intent intent) {
							String action = intent.getAction();
							BluetoothDevice device;
							int state;
							if (action
									.equals(BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED)) {
								state = intent.getIntExtra(
										BluetoothHeadset.EXTRA_STATE,
										BluetoothHeadset.STATE_DISCONNECTED);
								device = intent
										.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
								Log.d(TAG, "\nAction = " + action + "\nState = "
										+ state);
								if (state == BluetoothHeadset.STATE_CONNECTED) {
									if (!mBluetoothDevices.contains(device)) {
										mBluetoothDevices.add(device);
										onHeadsetConnected(device.getAddress(),
												device.getName());
									}
								} else if (state == BluetoothHeadset.STATE_DISCONNECTED) {
									if (device.equals(mConnectedHeadset)) {
										// Calling stopVoiceRecognition always returns
										// false
										// here
										// as it should since the headset is no longer
										// connected.
										if (mIsCountDownOn) {
											mIsCountDownOn = false;
											mCountDown11.cancel();
										}
										mConnectedHeadset = null;
									}

									// override this if you want to do other thing when
									// the
									// device is disconnected.
									if (mBluetoothDevices.contains(device)) {
										mBluetoothDevices.remove(device);
										onHeadsetDisconnected(device.getAddress());
									}
								}
							} else // audio
							{
								state = intent.getIntExtra(
										BluetoothHeadset.EXTRA_STATE,
										BluetoothHeadset.STATE_AUDIO_DISCONNECTED);
								Log.d(TAG, "\nAction = " + action + "\nState = "
										+ state);
								if (state == BluetoothHeadset.STATE_AUDIO_CONNECTED) {
									Log.d(TAG, "\nHeadset audio connected");

									if (mIsCountDownOn) {
										mIsCountDownOn = false;
										mCountDown11.cancel();
									}

									// override this if you want to do other thing when
									// headset
									// audio is connected.
									if (!mIsOnHeadsetSco) {
										mIsOnHeadsetSco = true;
										onScoAudioConnected();
									}
								} else if (state == BluetoothHeadset.STATE_AUDIO_DISCONNECTED) {
									// The headset audio is disconnected, but calling
									// stopVoiceRecognition always returns true here.

//                                  mBluetoothHeadset
//                                  .stopVoiceRecognition(mConnectedHeadset);
                            
								    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
								        mAudioManager.stopBluetoothSco();
		                                mAudioManager.setBluetoothScoOn(false);
								    } else {
                                        try {
                                            Class<?> cls =  mBluetoothHeadset.getClass();
                                            Method method = cls.getDeclaredMethod("stopScoUsingVirtualVoiceCall", BluetoothDevice.class);
                                            method.invoke(mBluetoothHeadset, mConnectedHeadset);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
								    }
								    
									// override this if you want to do other thing when
									// headset
									// audio is disconnected.
									if (mIsOnHeadsetSco) {
										mIsOnHeadsetSco = false;
										onScoAudioDisconnected();
									}

									Log.d(TAG, "Headset audio disconnected");
								}
							}
						}
					};
					// During the active life time of the app, a user may turn
					// on and
					// off the headset.
					// So register for broadcast of connection states.
					mContext.registerReceiver(
							mHeadsetBroadcastReceiver,
							new IntentFilter(
									BluetoothHeadset.ACTION_CONNECTION_STATE_CHANGED));
					// Calling startVoiceRecognition does not result in
					// immediate audio
					// connection.
					// So register for broadcast of audio connection states.
					// This
					// broadcast will
					// only be sent if startVoiceRecognition returns true.
					mContext.registerReceiver(
							mHeadsetBroadcastReceiver,
							new IntentFilter(
									BluetoothHeadset.ACTION_AUDIO_STATE_CHANGED));
				}
			};
			
			mCountDown11 = new CountDownTimer(10000, 1000) {
				@TargetApi(Build.VERSION_CODES.HONEYCOMB)
				@SuppressWarnings("synthetic-access")
				@Override
				public void onTick(long millisUntilFinished) {
					// First stick calls always returns false. The second stick
					// always returns true if the countDownInterval is set to
					// 1000.
					// It is somewhere in between 500 to a 1000.
//                  mBluetoothHeadset.startVoiceRecognition(mConnectedHeadset);
				    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        mAudioManager.startBluetoothSco();
                        mAudioManager.setBluetoothScoOn(true);
				    } else {
                        try {
                            Class<?> cls =  mBluetoothHeadset.getClass();
                            Method method = cls.getDeclaredMethod("startScoUsingVirtualVoiceCall", BluetoothDevice.class);
                            method.invoke(mBluetoothHeadset, mConnectedHeadset);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
				    }
					Log.d(TAG, "onTick startVoiceRecognition");
				}

				@SuppressWarnings("synthetic-access")
				@Override
				public void onFinish() {
					// Calls to startVoiceRecognition in onStick are not
					// successful.
					// Should implement something to inform user of this failure
					mIsCountDownOn = false;
					Log.d(TAG, "\nonFinish fail to connect to headset audio");
				}
			};
		}
	}

	/**
	 * Call this to start BluetoothHeadsetUtils functionalities.
	 * 
	 * @return The return value of startBluetooth() or startBluetooth11()
	 */
	public boolean start() {
		if (!mIsStarted) {
			mIsStarted = true;

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				mIsStarted = startBluetooth();
			} else {
				mIsStarted = startBluetooth11();
			}
		}

		return mIsStarted;
	}

	public boolean link(String address) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (mIsHeadsetConnected && mIsCountDownOn == false
					&& mIsOnHeadsetSco == false) {
				mAudioManager.setMode(AudioManager.MODE_IN_CALL);
				mIsCountDownOn = true;
				mCountDown.start();
				Log.d(TAG, "Start link count down");
				return true;
			}
		} else {
			if (mConnectedHeadset != null) {
				if (mConnectedHeadset.getAddress().equals(address))
					return true;
				if (mIsCountDownOn) {
					mIsCountDownOn = false;
					mCountDown11.cancel();
				}
                if (mIsOnHeadsetSco) {
//	                  mBluetoothHeadset.stopVoiceRecognition(mConnectedHeadset);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        mAudioManager.stopBluetoothSco();
                        mAudioManager.setBluetoothScoOn(false);
                    } else {
                        try {
                            Class<?> cls =  mBluetoothHeadset.getClass();
                            Method method = cls.getDeclaredMethod("stopScoUsingVirtualVoiceCall", BluetoothDevice.class);
                            method.invoke(mBluetoothHeadset, mConnectedHeadset);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                mConnectedHeadset = null;
			}
			if (mIsCountDownOn == false) {
				for (Iterator<BluetoothDevice> items = mBluetoothDevices
						.iterator(); items.hasNext();) {
					{
						BluetoothDevice device = (BluetoothDevice) items.next();
						if (device.getAddress().equals(address)) {
							mConnectedHeadset = device;
							mIsCountDownOn = true;
							mCountDown11.start();
							Log.d(TAG, "Start link count down");
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	public boolean unlink() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
			if (mIsHeadsetConnected) {
				if (mIsCountDownOn) {
					mIsCountDownOn = false;
					mCountDown.cancel();
				}
				if (mIsOnHeadsetSco) {
                    mAudioManager.stopBluetoothSco();
                    mAudioManager.setBluetoothScoOn(false);
                }
				return true;
			}
		} else {
			if (mConnectedHeadset != null) {
				if (mIsCountDownOn) {
					mIsCountDownOn = false;
					mCountDown11.cancel();
				}
                if (mIsOnHeadsetSco) {
//                  mBluetoothHeadset.stopVoiceRecognition(mConnectedHeadset);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                        mAudioManager.stopBluetoothSco();
                        mAudioManager.setBluetoothScoOn(false);
                    } else {
                        try {
                            Class<?> cls =  mBluetoothHeadset.getClass();
                            Method method = cls.getDeclaredMethod("stopScoUsingVirtualVoiceCall", BluetoothDevice.class);
                            method.invoke(mBluetoothHeadset, mConnectedHeadset);
                        } catch (Exception e) {
                            e.printStackTrace();
                    }
                    }
                }

				mConnectedHeadset = null;
				return true;
			}
		}
		return false;
	}

	/**
	 * Should call this on onResume or onDestroy. Unregister broadcast receivers
	 * and stop Sco audio connection and cancel count down.
	 */
	public void stop() {
		if (mIsStarted) {
			mIsStarted = false;

			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
				stopBluetooth();
			} else {
				stopBluetooth11();
			}
		}
	}

	/**
	 * 
	 * @return true if headset is connected.
	 */
	public boolean isHeadsetConnected() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)
			return mIsHeadsetConnected;
		else
			return !mBluetoothDevices.isEmpty();
	}

	/**
	 * 
	 * @return true if audio is connected through headset.
	 */
	public boolean isOnHeadsetSco() {
		return mIsOnHeadsetSco;
	}

	public abstract void onHeadsetDisconnected(final String address);

	public abstract void onHeadsetConnected(final String address,
			final String name);

	public abstract void onScoAudioDisconnected();

	public abstract void onScoAudioConnected();

	/**
	 * Register for bluetooth headset connection states and Sco audio states.
	 * Try to connect to bluetooth headset audio by calling startBluetoothSco().
	 * This is a work around for API < 11 to detect if a headset is connected
	 * before the application starts.
	 * 
	 * The official documentation for startBluetoothSco() states
	 * 
	 * "This method can be used by applications wanting to send and received
	 * audio to/from a bluetooth SCO headset while the phone is not in call."
	 * 
	 * Does this mean that startBluetoothSco() would fail if the connected
	 * bluetooth device is not a headset?
	 * 
	 * Thus if a call to startBluetoothSco() is successful, i.e
	 * mBroadcastReceiver will receive an ACTION_SCO_AUDIO_STATE_CHANGED with
	 * intent extra SCO_AUDIO_STATE_CONNECTED, then we assume that a headset is
	 * connected.
	 * 
	 * @return false if device does not support bluetooth or current platform
	 *         does not supports use of SCO for off call.
	 */
	@SuppressWarnings("deprecation")
	private boolean startBluetooth() {
		Log.d(TAG, "startBluetooth");

		// Device support bluetooth
		if (mBluetoothAdapter != null) {
			if (mAudioManager.isBluetoothScoAvailableOffCall()) {
				mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(
						BluetoothDevice.ACTION_ACL_CONNECTED));
				mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(
						BluetoothDevice.ACTION_ACL_DISCONNECTED));
				mContext.registerReceiver(mBroadcastReceiver, new IntentFilter(
						AudioManager.ACTION_SCO_AUDIO_STATE_CHANGED));

//				if (!mIsHeadsetConnected) {
//					mIsHeadsetConnected = true;
//					onHeadsetConnected(BLUETOOTH_ADDRESS, BLUETOOTH_NAME);
//				}
				// need for audio sco, see mBroadcastReceiver
				mIsStarting = true;

				return true;
			}
		}

		return false;
	}

	/**
	 * Register a headset profile listener
	 * 
	 * @return false if device does not support bluetooth or current platform
	 *         does not supports use of SCO for off call or error in getting
	 *         profile proxy.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private boolean startBluetooth11() {
		Log.d(TAG, "startBluetooth11");

		// Device support bluetooth
		if (mBluetoothAdapter != null) {
			if (mAudioManager.isBluetoothScoAvailableOffCall()) {
				// All the detection and audio connection are done in
				// mHeadsetProfileListener
				if (mBluetoothAdapter.getProfileProxy(mContext,
						mHeadsetProfileListener, BluetoothProfile.HEADSET)) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * API < 11 Unregister broadcast receivers and stop Sco audio connection and
	 * cancel count down.
	 */
	private void stopBluetooth() {
		Log.d(TAG, "stopBluetooth");

		if (mIsCountDownOn) {
			mIsCountDownOn = false;
			mCountDown.cancel();
		}

		// Need to stop Sco audio connection here when the app
		// change orientation or close with headset still turns on.
		mContext.unregisterReceiver(mBroadcastReceiver);
		mAudioManager.stopBluetoothSco();
		mAudioManager.setMode(AudioManager.MODE_NORMAL);
	    mAudioManager.setBluetoothScoOn(false);
	}

	/**
	 * API >= 11 Unregister broadcast receivers and stop Sco audio connection
	 * and cancel count down.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	protected void stopBluetooth11() {
		Log.d(TAG, "stopBluetooth11");

		if (mIsCountDownOn) {
			mIsCountDownOn = false;
			mCountDown11.cancel();
		}
		if (mHeadsetBroadcastReceiver != null) {
			mContext.unregisterReceiver(mHeadsetBroadcastReceiver);
			mHeadsetBroadcastReceiver = null;
		}
		if (mBluetoothHeadset != null) {
		    // Need to call stopVoiceRecognition here when the app
			// change orientation or close with headset still turns on.
//          mBluetoothHeadset.stopVoiceRecognition(mConnectedHeadset);
            
		    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
                mAudioManager.stopBluetoothSco();
                mAudioManager.setBluetoothScoOn(false);
		    } else {
               try {
                   Class<?> cls =  mBluetoothHeadset.getClass();
                   Method method = cls.getDeclaredMethod("stopScoUsingVirtualVoiceCall", BluetoothDevice.class);
                   method.invoke(mBluetoothHeadset, mConnectedHeadset);
               } catch (Exception e) {
                   e.printStackTrace();
               }
		    }

           mBluetoothAdapter.closeProfileProxy(BluetoothProfile.HEADSET,
					mBluetoothHeadset);
           mBluetoothHeadset = null;
		}
	}

	/**
	 * Broadcast receiver for API < 11 Handle headset and Sco audio connection
	 * states.
	 */
	private BroadcastReceiver mBroadcastReceiver = null;

	/**
	 * API < 11 Try to connect to audio headset in onTick.
	 */
	private CountDownTimer mCountDown = null;

	/**
	 * API >= 11 Check for already connected headset and if so start audio
	 * connection. Register for broadcast of headset and Sco audio connection
	 * states.
	 */
	private BluetoothProfile.ServiceListener mHeadsetProfileListener = null;

	/**
	 * API >= 11 Handle headset and Sco audio connection states.
	 */
	private BroadcastReceiver mHeadsetBroadcastReceiver = null;

	/**
	 * API >= 11 Try to connect to audio headset in onTick.
	 */
	private CountDownTimer mCountDown11 = null;
}
