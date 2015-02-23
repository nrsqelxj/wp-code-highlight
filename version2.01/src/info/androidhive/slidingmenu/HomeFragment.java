package info.androidhive.slidingmenu;


import java.util.List;

import com.sails.engine.LocationRegion;
import com.sails.engine.PathRoutingManager;
import com.sails.engine.SAILS;
import com.sails.engine.SAILSMapView;
import com.sails.engine.overlay.Marker;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class HomeFragment extends Fragment {
	//private Button btnMapActivity;
	static SAILS mSails;
	static SAILSMapView mSailsMapView;
    ImageView zoomin;
    ImageView zoomout;
    ImageView lockcenter;
	public HomeFragment(){}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
 
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        zoomin = (ImageView) rootView.findViewById(R.id.zoomin);
        zoomout = (ImageView) rootView.findViewById(R.id.zoomout);
        lockcenter = (ImageView) rootView.findViewById(R.id.lockcenter);
        zoomin.setOnClickListener(controlListener);
        zoomout.setOnClickListener(controlListener);
        lockcenter.setOnClickListener(controlListener);
        

        
        
        
        mSails = new SAILS(getActivity());
		// set location mode.
		 mSails.setMode(SAILS.WIFI_GFP_IMU);
		 //create location change call back.
		 
		//set floor number sort rule from descending to ascending.
	        mSails.setReverseFloorList(true);
	        //create location change call back.
	        mSails.setOnLocationChangeEventListener(new SAILS.OnLocationChangeEventListener() {
	            @Override
	            public void OnLocationChange() {

	                if (mSailsMapView.isCenterLock() && !mSailsMapView.isInLocationFloor() && !mSails.getFloor().equals("") && mSails.isLocationFix()) {
	                    //set the map that currently location engine recognize.
	                	mSailsMapView.getMapViewPosition().setZoomLevel((byte) 20);
	                    mSailsMapView.loadCurrentLocationFloorMap();
	                    Toast t = Toast.makeText(getActivity(), mSails.getFloorDescription(mSails.getFloor()), Toast.LENGTH_SHORT);
	                    t.show();
	                }
	            }
	        });
	        
	        
	        
		mSailsMapView = new SAILSMapView(getActivity());
		((FrameLayout)rootView.findViewById(R.id.SAILSMap)).addView(mSailsMapView);
		// configure SAILS map after map preparation finish.
		mSailsMapView.post(new Runnable() {
			@Override
			public void run() {
				// please change token and building id to your own building
				// project in cloud.
				mSails.loadCloudBuilding("69b66fd80ecf4561aaced3f0d1840865",
						"54c62c61d98797a814000769",
						new SAILS.OnFinishCallback() {
							@Override
							public void onSuccess(String response) {
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
								
											mapViewInitial();
										
									}
								});

							}

							@Override
							public void onFailed(String response) {
								Toast t = Toast
										.makeText(
												getActivity(),
												"Load cloud project fail, please check network connection.",
												Toast.LENGTH_SHORT);
								t.show();
							}
						});
			}
		}); 
        return rootView;
    }


	
	 void mapViewInitial() {
	        //establish a connection of SAILS engine into SAILS MapView.
		 	mSailsMapView.setSAILSEngine(mSails);
	        //set location pointer icon.
	        mSailsMapView.setLocationMarker(R.drawable.circle, R.drawable.arrow, null, 35);

	        //set location marker visible.
	       // mSailsMapView.setLocatorMarkerVisible(true);

	        //load first floor map in package.
	        mSailsMapView.loadFloorMap(mSails.getFloorNameList().get(0));
	      //  actionBar.setTitle("Map POIs");

	        //Auto Adjust suitable map zoom level and position to best view position.
	        mSailsMapView.autoSetMapZoomAndView();
	        
	        //design some action in mode change call back.
	        mSailsMapView.setOnModeChangedListener(new SAILSMapView.OnModeChangedListener() {
	            @Override
	            public void onModeChanged(int mode) {
	                if (((mode & SAILSMapView.LOCATION_CENTER_LOCK) == SAILSMapView.LOCATION_CENTER_LOCK) && ((mode & SAILSMapView.FOLLOW_PHONE_HEADING) == SAILSMapView.FOLLOW_PHONE_HEADING)) {
	                    lockcenter.setImageDrawable(getResources().getDrawable(R.drawable.center3));
	                } else if ((mode & SAILSMapView.LOCATION_CENTER_LOCK) == SAILSMapView.LOCATION_CENTER_LOCK) {
	                    lockcenter.setImageDrawable(getResources().getDrawable(R.drawable.center2));
	                } else {
	                    lockcenter.setImageDrawable(getResources().getDrawable(R.drawable.center1));
	                }
	            }
	        });


	        
	   }
	   
	    View.OnClickListener controlListener = new View.OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	mSails.startLocatingEngine();
	            mSailsMapView.setLocatorMarkerVisible(true);
                mSailsMapView.setMode(SAILSMapView.LOCATION_CENTER_LOCK | SAILSMapView.FOLLOW_PHONE_HEADING);
                lockcenter.setVisibility(View.VISIBLE);
	            if (v == zoomin) {
	                //set map zoomin function.
	                mSailsMapView.zoomIn();
	            } else if (v == zoomout) {
	                //set map zoomout function.
	                mSailsMapView.zoomOut();
	            } else if (v == lockcenter) {
	                if (!mSails.isLocationFix() || !mSails.isLocationEngineStarted()) {
	                    Toast t = Toast.makeText(getActivity(), "Location Not Found.", Toast.LENGTH_SHORT);
	                    t.show();
	                    return;
	                }
	                if (!mSailsMapView.isCenterLock() && !mSailsMapView.isInLocationFloor()) {
	                    //set the map that currently location engine recognize.
	                    mSailsMapView.loadCurrentLocationFloorMap();

	                    Toast t = Toast.makeText(getActivity(), "Go Back to Locating Floor First.", Toast.LENGTH_SHORT);
	                    t.show();
	                    return;
	                }
	                //set map mode.
	                //FOLLOW_PHONE_HEADING: the map follows the phone's heading.
	                //LOCATION_CENTER_LOCK: the map locks the current location in the center of map.
	                //ALWAYS_LOCK_MAP: the map will keep the mode even user moves the map.
	                if (mSailsMapView.isCenterLock()) {
	                    if ((mSailsMapView.getMode() & SAILSMapView.FOLLOW_PHONE_HEADING) == SAILSMapView.FOLLOW_PHONE_HEADING)
	                        //if map control mode is follow phone heading, then set mode to location center lock when button click.
	                        mSailsMapView.setMode(mSailsMapView.getMode() & ~SAILSMapView.FOLLOW_PHONE_HEADING);
	                    else
	                        //if map control mode is location center lock, then set mode to follow phone heading when button click.
	                        mSailsMapView.setMode(mSailsMapView.getMode() | SAILSMapView.FOLLOW_PHONE_HEADING);
	                } else {
	                    //if map control mode is none, then set mode to loction center lock when button click.
	                    mSailsMapView.setMode(mSailsMapView.getMode() | SAILSMapView.LOCATION_CENTER_LOCK);
	                }
	            }
	            /*else if (v == endRouteButton) {
	                endRouteButton.setVisibility(View.INVISIBLE);
	                distanceView.setVisibility(View.INVISIBLE);
	                currentFloorDistanceView.setVisibility(View.INVISIBLE);
	                msgView.setVisibility(View.INVISIBLE);
	                //end route.
	                mSailsMapView.getRoutingManager().disableHandler();
	            } else if (v == pinMarkerButton) {
	                Toast.makeText(getApplication(), "Please Touch Map and Set PinMarker.", Toast.LENGTH_SHORT).show();
	                mSailsMapView.getPinMarkerManager().setOnPinMarkerGenerateCallback(Marker.boundCenterBottom(getResources().getDrawable(R.drawable.parking_target)), new PinMarkerManager.OnPinMarkerGenerateCallback() {
	                    @Override
	                    public void OnGenerate(MarkerManager.LocationRegionMarker locationRegionMarker) {
	                        Toast.makeText(getApplication(), "One PinMarker Generated.", Toast.LENGTH_SHORT).show();
	                    }
	                });
	            }
	            */
	        }
	    };

	    
	    @Override
		public void onResume() {
	        super.onResume();
	        mSailsMapView.onResume();
	    }

	    @Override
		public void onPause() {
	        super.onPause();
	        mSailsMapView.onPause();
	    }

}
