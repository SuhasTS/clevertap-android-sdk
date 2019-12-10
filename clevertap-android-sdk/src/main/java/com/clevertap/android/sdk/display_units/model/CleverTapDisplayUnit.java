package com.clevertap.android.sdk.display_units.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.clevertap.android.sdk.Constants;
import com.clevertap.android.sdk.Logger;
import com.clevertap.android.sdk.display_units.CTDisplayUnitConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This model class holds the data of an individual Display Unit.
 */
public class CleverTapDisplayUnit implements Parcelable {

    /**
     * Display unit identifier
     */
    private String unitID;

    /**
     * Display Type Could be (banner,carousel,custom key value etc.)
     */
    private CTDisplayUnitConstants.CTDisplayUnitType displayUnitType;

    /**
     * Background Color
     */
    private String bgColor;


    /**
     * List of Display Content Items
     */
    private ArrayList<CleverTapDisplayUnitContent> displayUnitContentItems;

    /**
     * Custom Key Value Pairs
     */
    private HashMap<String, String> customExtras;

    private JSONObject jsonObject;

    private String error;

    //constructors
    private CleverTapDisplayUnit(JSONObject jsonObject, String unitID, CTDisplayUnitConstants.CTDisplayUnitType displayUnitType,
                                 String bgColor, ArrayList<CleverTapDisplayUnitContent> contentArray,
                                 JSONObject kvObject, String error) {
        this.jsonObject = jsonObject;
        this.unitID = unitID;
        this.displayUnitType = displayUnitType;
        this.bgColor = bgColor;
        this.displayUnitContentItems = contentArray;
        this.customExtras = getKeyValues(kvObject);
        this.error = error;
    }

    /**
     * static method to convert json to Display Unit
     *
     * @param jsonObject - Display Unit Item in Json form
     * @return - CleverTapDisplayUnit - always returns non-null instance
     */
    @NonNull
    public static CleverTapDisplayUnit toDisplayUnit(JSONObject jsonObject) {
        //logic to convert json obj to item
        try {
            String unitID = jsonObject.has(Constants.NOTIFICATION_ID_TAG) ? jsonObject.getString(Constants.NOTIFICATION_ID_TAG) : Constants.TEST_IDENTIFIER;
            CTDisplayUnitConstants.CTDisplayUnitType displayUnitType = jsonObject.has(Constants.KEY_TYPE) ? CTDisplayUnitConstants.CTDisplayUnitType.type(jsonObject.getString(Constants.KEY_TYPE)) : null;

            String bgColor = jsonObject.has(Constants.KEY_BG) ? jsonObject.getString(Constants.KEY_BG) : "";

            JSONArray contentArray = jsonObject.has(Constants.KEY_CONTENT) ? jsonObject.getJSONArray(Constants.KEY_CONTENT) : null;
            ArrayList<CleverTapDisplayUnitContent> contentArrayList = new ArrayList<>();
            if (contentArray != null) {
                for (int i = 0; i < contentArray.length(); i++) {
                    CleverTapDisplayUnitContent displayUnitContent = CleverTapDisplayUnitContent.toContent(contentArray.getJSONObject(i));
                    if (TextUtils.isEmpty(displayUnitContent.getError())) {
                        contentArrayList.add(displayUnitContent);
                    }
                }
            }
            JSONObject customKV = null;
            //custom KV can be added to Display unit of any types, no need to add type check here
            if (jsonObject.has(Constants.KEY_CUSTOM_KV)) {
                customKV = jsonObject.getJSONObject(Constants.KEY_CUSTOM_KV);
            }
            return new CleverTapDisplayUnit(jsonObject, unitID, displayUnitType, bgColor, contentArrayList, customKV, null);
        } catch (Exception e) {
            Logger.d(Constants.FEATURE_DISPLAY_UNIT, "Unable to init CleverTapDisplayUnit with JSON - " + e.getLocalizedMessage());
            return new CleverTapDisplayUnit(null, "", null, null, null, null, "Error Creating Display Unit from JSON : " + e.getLocalizedMessage());
        }
    }

    /**
     * Getter for the unitId of the Display Unit
     *
     * @return String
     */
    public String getUnitID() {
        return unitID;
    }

    public String getError() {
        return error;
    }

    /**
     * Getter for the Key Value pairs of the Display Unit
     *
     * @return HashMap<String, String>
     */
    @SuppressWarnings("unused")
    public HashMap<String, String> getCustomExtras() {
        return customExtras;
    }

    /**
     * Getter for the JsonObject corresponding to the CleverTapDisplayUnit object
     *
     * @return JSONObject
     */
    public JSONObject getJsonObject() {
        return jsonObject;
    }

    /**
     * Getter for the hex-value background color of the Display Unit e.g. #000000
     *
     * @return String
     */
    @SuppressWarnings("unused")
    public String getBgColor() {
        return bgColor;
    }

    /**
     * Getter for the DisplayUnitType of the Display Unit, Refer{@link CTDisplayUnitConstants.CTDisplayUnitType}
     *
     * @return CTDisplayUnitConstants.CTDisplayUnitType
     */
    @SuppressWarnings("unused")
    public CTDisplayUnitConstants.CTDisplayUnitType getDisplayUnitType() {
        return displayUnitType;
    }

    /**
     * Getter for the list of Content Display Unit Items.
     *
     * @return ArrayList<CleverTapDisplayUnitContent>
     */
    @SuppressWarnings("unused")
    public ArrayList<CleverTapDisplayUnitContent> getDisplayUnitContentItems() {
        return displayUnitContentItems;
    }

    /**
     * Getter for the WiZRK fields obj to be passed in the data for recording event.
     *
     * @return JSONObject
     */
    public JSONObject getWZRKFields() {
        try {
            if (jsonObject != null) {
                Iterator<String> iterator = jsonObject.keys();
                JSONObject object = new JSONObject();
                while (iterator.hasNext()) {
                    String keyName = iterator.next();
                    if (keyName.startsWith(Constants.WZRK_PREFIX)) {
                        object.put(keyName, jsonObject.get(keyName));
                    }
                }
                return object;
            }
        } catch (Exception e) {
            //no op
            Logger.d(Constants.FEATURE_DISPLAY_UNIT, "Error in getting WiZRK fields " + e.getLocalizedMessage());
        }
        return null;
    }

    /**
     * populates the custom key values pairs from json
     *
     * @param kvObj- Custom Key Values
     */
    private HashMap<String, String> getKeyValues(JSONObject kvObj) {
        try {
            if (kvObj != null) {
                Iterator<String> keys = kvObj.keys();
                if (keys != null) {
                    String key, value;
                    HashMap<String, String> hashMap = null;
                    while (keys.hasNext()) {
                        key = keys.next();
                        value = kvObj.getString(key);
                        if (!TextUtils.isEmpty(key)) {
                            if (hashMap == null)
                                hashMap = new HashMap<>();
                            hashMap.put(key, value);
                        }
                    }
                    return hashMap;
                }
            }
        } catch (Exception e) {
            //no op
            Logger.d(Constants.FEATURE_DISPLAY_UNIT, "Error in getting Key Value Pairs " + e.getLocalizedMessage());
        }
        return null;
    }

    public static final Creator<CleverTapDisplayUnit> CREATOR = new Creator<CleverTapDisplayUnit>() {
        @Override
        public CleverTapDisplayUnit createFromParcel(Parcel in) {
            return new CleverTapDisplayUnit(in);
        }

        @Override
        public CleverTapDisplayUnit[] newArray(int size) {
            return new CleverTapDisplayUnit[size];
        }
    };

    @SuppressWarnings("unchecked")
    private CleverTapDisplayUnit(Parcel in) {
        try {
            this.unitID = in.readString();
            this.displayUnitType = (CTDisplayUnitConstants.CTDisplayUnitType) in.readValue(CTDisplayUnitConstants.CTDisplayUnitType.class.getClassLoader());
            this.bgColor = in.readString();

            if (in.readByte() == 0x01) {
                displayUnitContentItems = new ArrayList<>();
                in.readList(displayUnitContentItems, CleverTapDisplayUnitContent.class.getClassLoader());
            } else {
                displayUnitContentItems = null;
            }

            this.customExtras = in.readHashMap(null);
            this.jsonObject = in.readByte() == 0x00 ? null : new JSONObject(in.readString());
            this.error = in.readString();
        } catch (Exception e) {
            error = "Error Creating Display Unit from parcel : " + e.getLocalizedMessage();
            Logger.d(Constants.FEATURE_DISPLAY_UNIT, error);
        }
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(unitID);
        parcel.writeValue(displayUnitType);
        parcel.writeString(bgColor);

        if (displayUnitContentItems == null) {
            parcel.writeByte((byte) (0x00));
        } else {
            parcel.writeByte((byte) (0x01));
            parcel.writeList(displayUnitContentItems);
        }

        parcel.writeMap(customExtras);
        if (jsonObject == null) {
            parcel.writeByte((byte) (0x00));
        } else {
            parcel.writeByte((byte) (0x01));
            parcel.writeString(jsonObject.toString());
        }
        parcel.writeString(error);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @NonNull
    @Override
    public String toString() {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("[");
            stringBuilder.append(" Unit id- ").append(unitID);
            stringBuilder.append(", Type- ").append((displayUnitType != null ? displayUnitType.toString() : null));
            stringBuilder.append(", bgColor- ").append(bgColor);
            if (displayUnitContentItems != null && !displayUnitContentItems.isEmpty()) {
                for (int i = 0; i < displayUnitContentItems.size(); i++) {
                    CleverTapDisplayUnitContent item = displayUnitContentItems.get(i);
                    if (item != null) {
                        stringBuilder.append(", Content Item:").append(i).append(" ").append(item.toString());
                        stringBuilder.append("\n");
                    }
                }
            }
            if (customExtras != null) {
                stringBuilder.append(", Custom KV:").append(customExtras);
            }
            stringBuilder.append(", JSON -").append(jsonObject);
            stringBuilder.append(", Error-").append(error);
            stringBuilder.append(" ]");
            return stringBuilder.toString();
        } catch (Exception e) {
            Logger.d(Constants.FEATURE_DISPLAY_UNIT, "Exception in toString:" + e);
        }
        return super.toString();
    }
}