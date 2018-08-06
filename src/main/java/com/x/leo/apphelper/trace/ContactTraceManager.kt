package com.x.leo.apphelper.trace

import android.app.Activity
import android.os.Build
import android.provider.ContactsContract
import android.text.TextUtils
import com.x.leo.apphelper.log.xlog.XLog
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * @作者:XLEO
 * @创建日期: 2017/9/5 15:31
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
object ContactTraceManager {
    fun uplodMessage(act: Activity) {
        val contactInfo = getContactInfo(act)
        TraceSender.sendInfo(act.applicationContext, contactInfo)
    }

    private val UPDATETIME: String = "contact trace update time"

    /**
     * Get Contact info
     * @param mactivity
     * *
     * @return
     */
    fun getContactInfo(mactivity: Activity): JSONObject {
        val contactsEntity = JSONObject()
        var totalNumber: Long = 0L

        val contactArray = JSONArray()

        val contentResolver = mactivity.contentResolver
        if (contentResolver == null) {
            XLog.d("get contentResolver failed",10)

            return contactsEntity
        }

        val cursor = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        while (cursor != null && cursor.moveToNext()) {
//            if (cursor.isFirst) {
//                cursor.printColumn("contacts column")
//            }
            val contact = JSONObject()
            var lastUpdateTime: String? = null
            val name = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
            val contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
            if (Build.VERSION.SDK_INT >= 18) {
                lastUpdateTime = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP))
            }
            //get the phone number
            val phoneNumbers = getPhoneNumbers(mactivity, contactId)
            //val emails = getEmail(mactivity, contactId)
            try {
                if (phoneNumbers.length() == 0)
                    continue

                totalNumber++
                contact.put("name", name)
                contact.put("number", phoneNumbers)
                getDetailInfo(contactId,mactivity,contact)
                //contact.put("email", emails)
                //                contact.put("lastUpdate", new SimpleDateFormat("yyyy-MM-dd").format(new Date(Long.parseLong(lastUpdateTime))));
                contact.put("lastUpdate", lastUpdateTime)
                contactArray.put(contact)
            } catch (ex: JSONException) {
                XLog.d("JSONException: " + ex.message,10)
            }
        }
        cursor?.close()



        try {
            val packageInfo = mactivity.packageManager.getPackageInfo(mactivity.packageName, 0)
            contactsEntity.put("protocolName", ProtocolName.CONTACT)
            contactsEntity.put("protocolVersion", ProtocolVersion.V_1_0)
            contactsEntity.put("versionName", packageInfo.versionName)

            contactsEntity.put("totalNumber", totalNumber)
            val currentTime = System.currentTimeMillis()
            contactsEntity.put("latestTime", currentTime)
            contactsEntity.put("earliestTime", LocalTimestamp.getTimestamp(mactivity.applicationContext,UPDATETIME))
            LocalTimestamp.addTimestamp(mactivity.applicationContext, UPDATETIME,currentTime)
            contactsEntity.put("data", contactArray)
        } catch (ex: Exception) {
            XLog.d("JSONException: " + ex.message,10)
        }
        XLog.d(contactsEntity.toString(),10)
        return contactsEntity
    }

    private fun getDetailInfo(contactId: String?, mactivity: Activity, contact: JSONObject) {
        try {
            val dataCursor = mactivity.contentResolver.query(ContactsContract.Data.CONTENT_URI, null, ContactsContract.Data.CONTACT_ID + " = " + contactId, null, null)
            if (dataCursor != null && dataCursor.moveToNext()) {
              //  dataCursor.printColumn("data cursor")
                val contact_time = dataCursor.getInt(dataCursor.getColumnIndex(ContactsContract.Data.TIMES_CONTACTED))
                val status = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.CONTACT_STATUS))
                val last_contact = dataCursor.getString(dataCursor.getColumnIndex(ContactsContract.Data.LAST_TIME_CONTACTED))
                contact.put("contact_times",contact_time)
                contact.put("last_contact_time",last_contact)
                val columnIndex = dataCursor.getColumnIndex("nickname")
                if (columnIndex >= 0) {
                    val customName = dataCursor.getString(columnIndex)
                    contact.put("nickname",customName)
                }
                val columnIndex1 = dataCursor.getColumnIndex("data2")
                if(columnIndex1 >= 0) {
                    val relationShip = dataCursor.getString(columnIndex1)
                    contact.put("relation",relationShip)
                }
                contact.put("status",status)
            }
            dataCursor.close()
        } catch (e: Exception) {
            XLog.e( "get contact status error",e,100)
        }
    }

    private fun getEmail(mactivity: Activity, contactId: String?): JSONArray {
        val emails = JSONArray()
        try {
            var emailCursor1 = mactivity.contentResolver.query(ContactsContract.CommonDataKinds.Email.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null)
            while (emailCursor1 != null && emailCursor1.moveToNext()) {
                val email = emailCursor1.getString(emailCursor1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                emails.put(email)
            }
            emailCursor1?.close()
            return emails
        } catch (e: Throwable) {
            XLog.e( "Obtain contact error",e,100)
        }
        return emails
    }

    private fun getPhoneNumbers(act: Activity, contactId: String?): JSONArray {
        val phoneNumbers = JSONArray()
        try {
            val phoneCursor1 = act.contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null)
            while (phoneCursor1 != null && phoneCursor1.moveToNext()) {
//                if (phoneCursor1.isFirst) {
//                    phoneCursor1.printColumn("phone cursor")
//                }
                val numberObj = JSONObject()
                var number = phoneCursor1.getString(phoneCursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                if (!TextUtils.isEmpty(number)) {
                    number = number.replace("-", "")
                    number = number.replace(" ", "")
                }
                numberObj.put("number",number)
                var lastcontact = phoneCursor1.getString(phoneCursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_USED))
                numberObj.put("last_time_used",lastcontact)
                var contacttimes = phoneCursor1.getInt(phoneCursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TIMES_USED))
                numberObj.put("time_used",contacttimes)
                var data2 = phoneCursor1.getInt(phoneCursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DATA2))
                val typeLabel = ContactsContract.CommonDataKinds.Phone.getTypeLabel(act.resources, data2, "CUSTOME")
                numberObj.put("type_label",typeLabel)
                phoneNumbers.put(numberObj)
            }
            phoneCursor1?.close()
            return phoneNumbers
        } catch (e: Exception) {
            XLog.e( "Exception on contact obtain",e,100)
        }
        return phoneNumbers
    }
}