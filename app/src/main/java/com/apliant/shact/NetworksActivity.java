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
import com.android.volley.toolbox.JsonObjectRequest;
import com.apliant.shact.models.Network;
import com.apliant.shact.models.User;
import com.apliant.shact.network.VolleySingleton;
import com.apliant.shact.utils.SetupUI;
import com.apliant.shact.utils.UrlEndpoints;
import com.apliant.shact.views.adapters.NetworksAdapter;
import com.baoyz.widget.PullRefreshLayout;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
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
                                                    String url = UrlEndpoints.BASE_URL + UrlEndpoints.USER + "/" + app.getCurrentUser().get_id() + UrlEndpoints.POP_PROFILE;
                                                    JSONObject params = new JSONObject();
                                                    try {
                                                        params.put("profileId", network.getProfile().get_id());
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }

                                                    Log.i(TAG, params.toString());
                                                    JsonObjectRequest request = makeRequest(Request.Method.PUT, url, params, new MyRequestListener() {
                                                        @Override
                                                        public void onSuccess(JSONObject response) {
                                                            app.getCurrentUser().getProfiles().remove(network.getProfile());
                                                            network.setProfile(null);
                                                            mAdapter.notifyDataSetChanged();
                                                        }

                                                        @Override
                                                        public void onError(Error error) {
                                                            Log.i(TAG, error.toString());
                                                        }
                                                    });
                                                    mRequestQueue.add(request);
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
                        public void onSubmit(final String result) {
                            String url = UrlEndpoints.BASE_URL + UrlEndpoints.USER + "/" + app.getCurrentUser().get_id() + UrlEndpoints.PUSH_PROFILE;
                            Log.i(TAG, url);
                            JSONObject params = new JSONObject();
                            try {
                                params.put("name", result);
                                params.put("networkId", network.get_id());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            JsonObjectRequest request = makeRequest(Request.Method.PUT, url, params, new MyRequestListener() {
                                @Override
                                public void onSuccess(JSONObject response) {
                                    Log.i(TAG, response.toString());
                                    User.Profile profile = new User.Profile(result, network.get_id(),"");
                                    app.getCurrentUser().getProfiles().add(profile);
                                    network.setProfile(profile);
                                    mAdapter.notifyDataSetChanged();
                                }

                                @Override
                                public void onError(Error error) {
                                    Log.i(TAG, error.toString());
                                }
                            });

                            mRequestQueue.add(request);

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

    private void pushProfile() {

    }

    private void popProfile() {

    }

    private void sendJsonRequest() {
        String url = UrlEndpoints.BASE_URL + UrlEndpoints.NETWORK;
        JsonArrayRequest request = new JsonArrayRequest(
                Request.Method.GET,
                url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.i(TAG, response.toString());
                        try {
                            networks.clear();
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject networkJson = response.getJSONObject(i);
                                Gson gson = new Gson();
                                Integer color = Color.parseColor(networkJson.getString("color"));
                                String name = networkJson.getString("name");
                                String icon = networkJson.getString("icon");
                                String identifier = networkJson.getString("identifier");
                                String _id = networkJson.getString("_id");


                                Network network = new Network(_id, name, color, icon, identifier);

                                User.Profile profile = app.getCurrentUser().hasProfileOnNetwork(network);

                                if (profile != null) {
                                    Log.i(TAG, "has profile on " + network.getName() + " " + profile.getName());
                                    network.setProfile(profile);
                                }

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
