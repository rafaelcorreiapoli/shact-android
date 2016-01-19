package com.apliant.shact.utils;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.apliant.shact.models.User;
import com.apliant.shact.network.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rafa93br on 07/01/2016.
 */
public class Login {
    public static final Integer INVALID_CREDENTIALS = 1;
    public static final Integer JSON_ERROR = 2;
    public static final Integer REQUEST_ERROR = 3;

    private String username;
    private String password;
    private String token;

    public interface LoginCallback {
        public void onSuccess(String token, User user);

        public void onError(LoginError error);

    }

    public class LoginError {
        private Integer code;
        private String description;

        public LoginError(Integer code, String description) {
            this.code = code;
            this.description = description;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void printStackTrack(String tag) {
            Log.i(tag, "[" + getCode() + "] " + getDescription());
        }

    }


    public JsonObjectRequest login(final String username, final String password, final LoginCallback loginCallback) {
        String url = UrlEndpoints.BASE_URL + UrlEndpoints.AUTH + UrlEndpoints.LOGIN;
        Map<String, String> params = new HashMap<String, String>();
        params.put("username", username);
        params.put("password", password);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String token;
                        String name;
                        String username;
                        Boolean success;
                        try {
                            Log.i("TAG", response.toString());
                            success = response.getBoolean("success");
                            if (success) {
                                try {
                                    JSONObject userJson = response.getJSONObject("user");
                                    Gson gson = new Gson();
                                    User user = gson.fromJson(userJson.toString(), User.class);
                                    token = response.getString("token");
                                    Log.i("TAG", user.getProfiles().size() + "  profiles ");
                                    Log.i("TAG", user.getUsername());
                                    loginCallback.onSuccess(token, user);
                                } catch (JSONException e) {
                                    Log.i("TAG", "erro!");
                                    e.printStackTrace();
                                }
                            } else {
                                loginCallback.onError(new LoginError(INVALID_CREDENTIALS, "invalid username or password."));
                            }
                        } catch (JSONException e) {
                            loginCallback.onError(new LoginError(JSON_ERROR, "error processing json."));
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                loginCallback.onError(new LoginError(REQUEST_ERROR, "request error."));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return super.getHeaders();
            }
        };

        return request;
    }

    public JsonObjectRequest loginWithToken(final String token, final LoginCallback loginCallback) {
        String url = UrlEndpoints.BASE_URL + UrlEndpoints.AUTH + UrlEndpoints.LOGIN_WITH_TOKEN;
        Map<String, String> params = new HashMap<String, String>();
        params.put("token", token);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(params), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String token;
                String name;
                String username;
                Boolean success;
                try {
                    success = response.getBoolean("success");
                    if (success) try {
                        JSONObject userJson = response.getJSONObject("user");
                        Gson gson = new Gson();
                        Log.i("TAG", userJson.toString());
                        User user = gson.fromJson(userJson.toString(), User.class);
                        Log.i("TAG", Integer.toString(user.getProfiles().size()));
                        token = response.getString("token");
                        loginCallback.onSuccess(token, user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    else
                        loginCallback.onError(new LoginError(INVALID_CREDENTIALS, "invalid username or password."));
                } catch (JSONException e) {
                    loginCallback.onError(new LoginError(JSON_ERROR, "error processing json."));
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                loginCallback.onError(new LoginError(REQUEST_ERROR, "request error."));
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return super.getHeaders();
            }
        };

        return request;
    }


}
