package com.apliant.shact;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.apliant.shact.models.Network;
import com.apliant.shact.network.VolleySingleton;
import com.apliant.shact.utils.SetupUI;
import com.apliant.shact.utils.UrlEndpoints;
import com.apliant.shact.views.adapters.NetworksAdapter;
import com.baoyz.widget.PullRefreshLayout;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.gson.Gson;
import com.yalantis.flipviewpager.utils.FlipSettings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NetworksActivity extends BaseActivity {
    NetworksAdapter mAdapter;
    ListView networkList;
    List<Network> networks = new ArrayList<>();
    PullRefreshLayout pullToRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_networks);
        getSupportActionBar().setTitle("Perfis");
        mDrawer.setSelection(DRAWER_NETWORKS, false);

        pullToRefresh = (PullRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        pullToRefresh.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.i(TAG, "start refresh");
                sendJsonRequest();
            }
        });

        FlipSettings settings = new FlipSettings.Builder().defaultPage(1).build();
        mAdapter = new NetworksAdapter(getApplicationContext(), networks, settings);
        networkList = (ListView) findViewById(R.id.network_list);
        networkList.setAdapter(mAdapter);
        networkList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Network network = (Network) networkList.getAdapter().getItem(position);

                if (network.getProfile() != null) {

                    new BottomSheet.Builder(NetworksActivity.this)
                            .title(network.getName())
                            .sheet(R.menu.network_bottomsheet)
                            .listener(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case R.id.action_delete:
                                            popupDeleteProfile(network, new MyPopupDeleteCallback() {
                                                @Override
                                                public void onSubmit() {
                                                    //popProfile()
                                                    /*
                                                    currentUser.getProfiles().remove(socialProfile.getSocialNetwork().getIdentifier());
                                                    Map<String, Object> profiles = new HashMap<String, Object>();
                                                    profiles.put("profiles", currentUser.getProfiles());
                                                    userRef.updateChildren(profiles);
                                                    */
                                                }
                                            });
                                            break;
                                        case R.id.action_open:
                                            Log.i("TAG", "Open app");
                                            break;
                                        case R.id.action_cancel:
                                            dialog.dismiss();
                                            break;
                                    }
                                }
                            }).show();
                } else {
                    popupNewProfile(network, new MyPopupCallback() {
                        @Override
                        public void onSubmit(String result) {
                            /*
                            currentUser.getProfiles().put(socialProfile.getSocialNetwork().getIdentifier(), result);
                            Map<String, Object> profiles = new HashMap<String, Object>();
                            profiles.put("profiles", currentUser.getProfiles());
                            userRef.updateChildren(profiles);
                            */
                        }
                    });
                }
            }
        });

        sendJsonRequest();
    }

    private interface MyPopupCallback {
        void onSubmit(String result);
    }

    private interface MyPopupDeleteCallback {
        void onSubmit();
    }

    private void popupDeleteProfile(Network network, final MyPopupDeleteCallback myPopupDeleteCallback) {
        new AlertDialog.Builder(this)
                .setTitle("Descadastrar?")
                .setMessage("Seus amigos perderão o contato deste perfil")
                .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myPopupDeleteCallback.onSubmit();
                    }
                })
                .setNegativeButton("Não", null)
                .show();
    }

    private void popupNewProfile(Network socialNetwork, final MyPopupCallback popupCallback) {
        final EditText textInput = new EditText(this);

        new AlertDialog.Builder(this)
                .setTitle("Qual seu " + socialNetwork.getName() + "?")
                .setView(textInput)
                .setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        popupCallback.onSubmit(textInput.getText().toString());
                    }
                })
                .setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private void sendJsonRequest() {
        String url = UrlEndpoints.BASE_URL + UrlEndpoints.NETWORK;
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            networks.clear();
                            for (int i = 0; i < response.length(); i ++) {
                                JSONObject networkJson = response.getJSONObject(i);
                                Gson gson = new Gson();
                                Integer color = Color.parseColor(networkJson.getString("color"));
                                String name = networkJson.getString("name");
                                String icon = networkJson.getString("icon");
                                String identifier = networkJson.getString("identifier");

                                Network network = new Network(name, color, icon, identifier);

                                networks.add(network);
                            }
                            pullToRefresh.setRefreshing(false);
                            mAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            //loginCallback.onError(new LoginError(JSON_ERROR, "error processing json."));
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "error" + error.toString());
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return super.getHeaders();
            }
        };

        mRequestQueue.add(request);
    }
}
