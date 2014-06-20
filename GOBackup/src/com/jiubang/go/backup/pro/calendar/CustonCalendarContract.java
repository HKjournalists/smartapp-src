package com.jiubang.go.backup.pro.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Entity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.RemoteException;
import android.provider.BaseColumns;
import android.provider.SyncStateContract;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;

// CHECKSTYLE:OFF
/**
 * 日历提供者与应用的Contract，SDK14以上版本才具有这个类，这是从SDK14的源码抠出来的 做了一定的修改
 *
 * @author wencan
 *
 */
public class CustonCalendarContract {
	private static final String TAG = "Calendar";

	/**
	 * Broadcast Action: This is the intent that gets fired when an alarm
	 * notification needs to be posted for a reminder.
	 *
	 */
	//	    @SdkConstant(SdkConstantType.ACTIVITY_INTENT_ACTION)
	public static final String ACTION_EVENT_REMINDER = "android.intent.action.EVENT_REMINDER";

	/**
	 * Intent Extras key: The start time of an event or an instance of a
	 * recurring event. (milliseconds since epoch)
	 */
	public static final String EXTRA_EVENT_BEGIN_TIME = "beginTime";

	/**
	 * Intent Extras key: The end time of an event or an instance of a recurring
	 * event. (milliseconds since epoch)
	 */
	public static final String EXTRA_EVENT_END_TIME = "endTime";

	/**
	 * Intent Extras key: When creating an event, set this to true to create an
	 * all-day event by default
	 */
	public static final String EXTRA_EVENT_ALL_DAY = "allDay";

	/**
	 * This authority is used for writing to or querying from the calendar
	 * provider. Note: This is set at first run and cannot be changed without
	 * breaking apps that access the provider.
	 */
	public static final String AUTHORITY = "com.android.calendar";

	/**
	 * The content:// style URL for the top-level calendar authority
	 */
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	/**
	 * An optional insert, update or delete URI parameter that allows the caller
	 * to specify that it is a sync adapter. The default value is false. If set
	 * to true, the modified row is not marked as "dirty" (needs to be synced)
	 * and when the provider calls
	 * {@link ContentResolver#notifyChange(android.net.Uri, android.database.ContentObserver, boolean)}
	 * , the third parameter "syncToNetwork" is set to false. Furthermore, if
	 * set to true, the caller must also include {@link Calendars#ACCOUNT_NAME}
	 * and {@link Calendars#ACCOUNT_TYPE} as query parameters.
	 *
	 * @see Uri.Builder#appendQueryParameter(java.lang.String, java.lang.String)
	 */
	public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";

	/**
	 * A special account type for calendars not associated with any account.
	 * Normally calendars that do not match an account on the device will be
	 * removed. Setting the account_type on a calendar to this will prevent it
	 * from being wiped if it does not match an existing account.
	 *
	 * @see SyncColumns#ACCOUNT_TYPE
	 */
	public static final String ACCOUNT_TYPE_LOCAL = "LOCAL";

	public static final String ACCOUNT_TYPE_GOOGLE = "com.google";

	/**
	 * This utility class cannot be instantiated
	 */
	private CustonCalendarContract() {
	}

	/**
	 * Generic columns for use by sync adapters. The specific functions of these
	 * columns are private to the sync adapter. Other clients of the API should
	 * not attempt to either read or write this column. These columns are
	 * editable as part of the Calendars Uri, but can only be read if accessed
	 * through any other Uri.
	 */
	protected interface CalendarSyncColumnsIceCream {

		/**
		 * Generic column for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CAL_SYNC1 = "cal_sync1";

		/**
		 * Generic column for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CAL_SYNC2 = "cal_sync2";

		/**
		 * Generic column for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CAL_SYNC3 = "cal_sync3";

		/**
		 * Generic column for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CAL_SYNC4 = "cal_sync4";

		/**
		 * Generic column for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CAL_SYNC5 = "cal_sync5";

		/**
		 * Generic column for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CAL_SYNC6 = "cal_sync6";

		/**
		 * Generic column for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CAL_SYNC7 = "cal_sync7";

		/**
		 * Generic column for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CAL_SYNC8 = "cal_sync8";

		/**
		 * Generic column for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CAL_SYNC9 = "cal_sync9";

		/**
		 * Generic column for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CAL_SYNC10 = "cal_sync10";
	}

	protected interface CalendarSyncColumnsFroyo {
		public static final String SYNC_VERSION_FROYO = "_sync_version";
		public static final String SYNC_TIME_FROYO = "_sync_time";
		public static final String SYNC_LOCAL_ID_FROYO = "_sync_local_id";
		public static final String SYNC_MARK_FROYO = "_sync_mark";
		public static final String SYNC_DIRTY_FROYO = "_sync_dirty";
	}

	protected interface CalendarSyncColumnsHoneycomb {
		public static final String SYNC1_HONEYCOMB = "sync1";
		public static final String SYNC2_HONEYCOMB = "sync2";
		public static final String SYNC3_HONEYCOMB = "sync3";
		public static final String SYNC4_HONEYCOMB = "sync4";
		public static final String SYNC5_HONEYCOMB = "sync5";
	}

	protected interface SyncColumns {
		/**
		 * The unique ID for a row assigned by the sync source. NULL if the row
		 * has never been synced. This is used as a reference id for exceptions
		 * along with {@link BaseColumns#_ID}.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String _SYNC_ID = "_sync_id";

		/**
		 * Whether the row has been deleted but not synced to the server. A
		 * deleted row should be ignored.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String DELETED = "deleted";
	}

	/**
	 * Columns for Sync information used by Calendars and Events tables. These
	 * have specific uses which are expected to be consistent by the app and
	 * sync adapter.
	 *
	 */
	protected interface SyncColumnsIceCream extends CalendarSyncColumnsIceCream {
		/**
		 * The account that was used to sync the entry to the device. If the
		 * account_type is not {@link #ACCOUNT_TYPE_LOCAL} then the name and
		 * type must match an account on the device or the calendar will be
		 * deleted.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String ACCOUNT_NAME_ICE_CREAM = "account_name";

		/**
		 * The type of the account that was used to sync the entry to the
		 * device. A type of {@link #ACCOUNT_TYPE_LOCAL} will keep this event
		 * form being deleted if there are no matching accounts on the device.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String ACCOUNT_TYPE_ICE_CREAM = "account_type";

		/**
		 * Used to indicate that local, unsynced, changes are present.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String DIRTY_ICE_CREAM = "dirty";

		/**
		 * If set to 1 this causes events on this calendar to be duplicated with
		 * {@link Events#LAST_SYNCED} set to 1 whenever the event transitions
		 * from non-dirty to dirty. The duplicated event will not be expanded in
		 * the instances table and will only show up in sync adapter queries of
		 * the events table. It will also be deleted when the originating event
		 * has its dirty flag cleared by the sync adapter.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String CAN_PARTIALLY_UPDATE_ICE_CREAM = "canPartiallyUpdate";
	}

	protected interface SyncColumnsFroyo extends CalendarSyncColumnsFroyo {
		public static final String ACCOUNT_NAME_FROYO = "_sync_account";
		public static final String ACCOUNT_TYPE_FROYO = "_sync_account_type";
	}

	/**
	 * Columns specific to the Calendars Uri that other Uris can query.
	 */
	protected interface CalendarColumnsIceCream {
		/**
		 * The color of the calendar. This should only be updated by the sync
		 * adapter, not other apps, as changing a calendar's color can adversely
		 * affect its display.
		 * <P>
		 * Type: INTEGER (color value)
		 * </P>
		 */
		public static final String CALENDAR_COLOR_ICE_CREAM = "calendar_color";

		/**
		 * The display name of the calendar. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CALENDAR_DISPLAY_NAME_ICE_CREAM = "calendar_displayName";

		/**
		 * The level of access that the user has for the calendar
		 * <P>
		 * Type: INTEGER (one of the values below)
		 * </P>
		 */
		public static final String CALENDAR_ACCESS_LEVEL_ICE_CREAM = "calendar_access_level";

		/**
		 * Is the calendar selected to be displayed? 0 - do not show events
		 * associated with this calendar. 1 - show events associated with this
		 * calendar
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String VISIBLE_ICE_CREAM = "visible";

		/**
		 * The time zone the calendar is associated with.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CALENDAR_TIME_ZONE_ICE_CREAM = "calendar_timezone";

		/**
		 * Is this calendar synced and are its events stored on the device? 0 -
		 * Do not sync this calendar or store events for this calendar. 1 - Sync
		 * down events for this calendar.
		 * <p>
		 * Type: INTEGER (boolean)
		 * </p>
		 */
		public static final String SYNC_EVENTS_ICE_CREAM = "sync_events";

		/**
		 * The owner account for this calendar, based on the calendar feed. This
		 * will be different from the _SYNC_ACCOUNT for delegated calendars.
		 * Column name.
		 * <P>
		 * Type: String
		 * </P>
		 */
		public static final String OWNER_ACCOUNT_ICE_CREAM = "ownerAccount";

		/**
		 * Can the organizer respond to the event? If no, the status of the
		 * organizer should not be shown by the UI. Defaults to 1. Column name.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String CAN_ORGANIZER_RESPOND_ICE_CREAM = "canOrganizerRespond";

		/**
		 * Can the organizer modify the time zone of the event? Column name.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String CAN_MODIFY_TIME_ZONE_ICE_CREAM = "canModifyTimeZone";

		/**
		 * The maximum number of reminders allowed for an event. Column name.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String MAX_REMINDERS_ICE_CREAM = "maxReminders";

		/**
		 * A comma separated list of reminder methods supported for this
		 * calendar in the format "#,#,#". Valid types are
		 * {@link Reminders#METHOD_DEFAULT}, {@link Reminders#METHOD_ALERT},
		 * {@link Reminders#METHOD_EMAIL}, {@link Reminders#METHOD_SMS}. Column
		 * name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String ALLOWED_REMINDERS_ICE_CREAM = "allowedReminders";
	}

	protected interface CalendarColumnsFroyo {
		public static final String CALENDAR_COLOR_FROYO = "color";
		public static final String CALENDAR_DISPLAY_NAME_FROYO = "displayName";
		public static final String CALENDAR_ACCESS_LEVEL_FROYO = "access_level";
		public static final String CALENDAR_TIME_ZONE_FROYO = "timezone";
		public static final String SYNC_EVENTS_FROYO = "sync_events";
		public static final String OWNER_ACCOUNT_FROYO = "ownerAccount";
		public static final String ORGANIZER_CAN_RESPOND_FROYO = "organizerCanRespond";
		public static final String URL_FROYO = "url";
		public static final String HIDDEN_FROYO = "hidden";
		public static final String SELECTED_FROYO = "selected";
	}

	/**
	 * Class that represents a Calendar Entity. There is one entry per calendar.
	 * This is a helper class to make batch operations easier.
	 */
	public static final class CalendarEntity
			implements
				BaseColumns,
				SyncColumns,
				SyncColumnsIceCream,
				SyncColumnsFroyo,
				CalendarColumnsIceCream,
				CalendarColumnsFroyo {

		/**
		 * The default Uri used when creating a new calendar EntityIterator.
		 */
		@SuppressWarnings("hiding")
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/calendar_entities");

		/**
		 * This utility class cannot be instantiated
		 */
		private CalendarEntity() {
		}

		/**
		 * Creates an entity iterator for the given cursor. It assumes the
		 * cursor contains a calendars query.
		 *
		 * @param cursor
		 *            query on {@link #CONTENT_URI}
		 * @return an EntityIterator of calendars
		 */
		public static EntityIterator newEntityIterator(Cursor cursor, int sdk) {
			return new EntityIteratorImpl(cursor, sdk);
		}

		private static class EntityIteratorImpl extends CursorEntityIterator {
			private int mSdk;
			public EntityIteratorImpl(Cursor cursor, int sdk) {
				super(cursor);
				mSdk = sdk;
			}

			@Override
			public Entity getEntityAndIncrementCursor(Cursor cursor) throws RemoteException {
				// we expect the cursor is already at the row we need to read from
				final long calendarId = cursor.getLong(cursor.getColumnIndexOrThrow(_ID));

				// Create the content value
				ContentValues cv = new ContentValues();
				cv.put(_ID, calendarId);

				if (mSdk < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					loadFroyoField(cursor, cv);
				} else {
					loadIceCreamField(cursor, cv);
				}

				// Create the Entity from the ContentValue
				Entity entity = new Entity(cv);

				// Set cursor to next row
				cursor.moveToNext();

				// Return the created Entity
				return entity;
			}

			private void loadFroyoField(Cursor cursor, ContentValues cv) {
				cursorStringToContentValuesIfPresent(cursor, cv, Calendars._SYNC_ID);
				cursorStringToContentValuesIfPresent(cursor, cv, Calendars.NAME);
				cursorIntToContentValuesIfPresent(cursor, cv, Calendars.ORGANIZER_CAN_RESPOND_FROYO);
				cursorStringToContentValuesIfPresent(cursor, cv, Calendars.OWNER_ACCOUNT_FROYO);
				cursorIntToContentValuesIfPresent(cursor, cv, SYNC_EVENTS_FROYO);

				cursorStringToContentValuesIfPresent(cursor, cv, ACCOUNT_NAME_FROYO);
				cursorStringToContentValuesIfPresent(cursor, cv, ACCOUNT_TYPE_FROYO);
				cursorIntToContentValuesIfPresent(cursor, cv, Calendars.CALENDAR_COLOR_FROYO);
				cursorStringToContentValuesIfPresent(cursor, cv, Calendars.CALENDAR_LOCATION_FROYO);
				cursorStringToContentValuesIfPresent(cursor, cv, CALENDAR_TIME_ZONE_FROYO);
				cursorStringToContentValuesIfPresent(cursor, cv,
						Calendars.CALENDAR_DISPLAY_NAME_FROYO);
				cursorIntToContentValuesIfPresent(cursor, cv, CALENDAR_ACCESS_LEVEL_FROYO);
				cursorLongToContentValuesIfPresent(cursor, cv, SYNC_DIRTY_FROYO);

				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_VERSION_FROYO);
				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_TIME_FROYO);
				cursorIntToContentValuesIfPresent(cursor, cv, SYNC_LOCAL_ID_FROYO);
				cursorIntToContentValuesIfPresent(cursor, cv, SYNC_MARK_FROYO);
				cursorStringToContentValuesIfPresent(cursor, cv, URL_FROYO);
				cursorIntToContentValuesIfPresent(cursor, cv, HIDDEN_FROYO);
				cursorIntToContentValuesIfPresent(cursor, cv, SELECTED_FROYO);
			}

			private void loadIceCreamField(Cursor cursor, ContentValues cv) {
				cursorStringToContentValuesIfPresent(cursor, cv, Calendars.NAME);
				cursorStringToContentValuesIfPresent(cursor, cv, Calendars.OWNER_ACCOUNT_ICE_CREAM);
				cursorIntToContentValuesIfPresent(cursor, cv,
						Calendars.CAN_ORGANIZER_RESPOND_ICE_CREAM);
				cursorStringToContentValuesIfPresent(cursor, cv, _SYNC_ID);
				cursorIntToContentValuesIfPresent(cursor, cv, SYNC_EVENTS_ICE_CREAM);

				cursorStringToContentValuesIfPresent(cursor, cv, ACCOUNT_NAME_ICE_CREAM);
				cursorStringToContentValuesIfPresent(cursor, cv, ACCOUNT_TYPE_ICE_CREAM);
				cursorStringToContentValuesIfPresent(cursor, cv,
						Calendars.CALENDAR_DISPLAY_NAME_ICE_CREAM);
				cursorIntToContentValuesIfPresent(cursor, cv, Calendars.CALENDAR_COLOR_ICE_CREAM);
				cursorIntToContentValuesIfPresent(cursor, cv, CALENDAR_ACCESS_LEVEL_ICE_CREAM);
				cursorStringToContentValuesIfPresent(cursor, cv, CALENDAR_TIME_ZONE_ICE_CREAM);
				cursorStringToContentValuesIfPresent(cursor, cv,
						Calendars.CALENDAR_LOCATION_ICE_CREAM);

				cursorLongToContentValuesIfPresent(cursor, cv, DIRTY_ICE_CREAM);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC1);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC2);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC3);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC4);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC5);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC6);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC7);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC8);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC9);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC10);
				cursorIntToContentValuesIfPresent(cursor, cv, VISIBLE_ICE_CREAM);
				cursorIntToContentValuesIfPresent(cursor, cv,
						Calendars.CAN_MODIFY_TIME_ZONE_ICE_CREAM);
				cursorIntToContentValuesIfPresent(cursor, cv, Calendars.MAX_REMINDERS_ICE_CREAM);
				cursorIntToContentValuesIfPresent(cursor, cv,
						Calendars.CAN_PARTIALLY_UPDATE_ICE_CREAM);
				cursorStringToContentValuesIfPresent(cursor, cv,
						Calendars.ALLOWED_REMINDERS_ICE_CREAM);
				cursorIntToContentValuesIfPresent(cursor, cv, DELETED);
			}
		}
	}

	/**
	 * Constants and helpers for the Calendars table, which contains details for
	 * individual calendars. <h3>Operations</h3> All operations can be done
	 * either as an app or as a sync adapter. To perform an operation as a sync
	 * adapter {@link #CALLER_IS_SYNCADAPTER} should be set to true and
	 * {@link #ACCOUNT_NAME} and {@link #ACCOUNT_TYPE} must be set in the Uri
	 * parameters. See
	 * {@link Uri.Builder#appendQueryParameter(java.lang.String, java.lang.String)}
	 * for details on adding parameters. Sync adapters have write access to more
	 * columns but are restricted to a single account at a time. Calendars are
	 * designed to be primarily managed by a sync adapter and inserting new
	 * calendars should be done as a sync adapter. For the most part, apps
	 * should only update calendars (such as changing the color or display
	 * name). If a local calendar is required an app can do so by inserting as a
	 * sync adapter and using an {@link #ACCOUNT_TYPE} of
	 * {@link #ACCOUNT_TYPE_LOCAL} .
	 * <dl>
	 * <dt><b>Insert</b></dt>
	 * <dd>When inserting a new calendar the following fields must be included:
	 * <ul>
	 * <li>{@link #ACCOUNT_NAME}</li>
	 * <li>{@link #ACCOUNT_TYPE}</li>
	 * <li>{@link #NAME}</li>
	 * <li>{@link #CALENDAR_DISPLAY_NAME}</li>
	 * <li>{@link #CALENDAR_COLOR}</li>
	 * <li>{@link #CALENDAR_ACCESS_LEVEL}</li>
	 * <li>{@link #OWNER_ACCOUNT}</li>
	 * </ul>
	 * The following fields are not required when inserting a Calendar but are
	 * generally a good idea to include:
	 * <ul>
	 * <li>{@link #SYNC_EVENTS} set to 1</li>
	 * <li>{@link #CALENDAR_TIME_ZONE}</li>
	 * <li>{@link #ALLOWED_REMINDERS}</li>
	 * </ul>
	 * <dt><b>Update</b></dt>
	 * <dd>To perform an update on a calendar the {@link #_ID} of the calendar
	 * should be provided either as an appended id to the Uri (
	 * {@link ContentUris#withAppendedId}) or as the first selection item--the
	 * selection should start with "_id=?" and the first selectionArg should be
	 * the _id of the calendar. Calendars may also be updated using a selection
	 * without the id. In general, the {@link #ACCOUNT_NAME} and
	 * {@link #ACCOUNT_TYPE} should not be changed after a calendar is created
	 * as this can cause issues for sync adapters.
	 * <dt><b>Delete</b></dt>
	 * <dd>Calendars can be deleted either by the {@link #_ID} as an appended id
	 * on the Uri or using any standard selection. Deleting a calendar should
	 * generally be handled by a sync adapter as it will remove the calendar
	 * from the database and all associated data (aka events).</dd>
	 * <dt><b>Query</b></dt>
	 * <dd>Querying the Calendars table will get you all information about a set
	 * of calendars. There will be one row returned for each calendar that
	 * matches the query selection, or at most a single row if the {@link #_ID}
	 * is appended to the Uri.</dd>
	 * </dl>
	 * <h3>Calendar Columns</h3> The following Calendar columns are writable by
	 * both an app and a sync adapter.
	 * <ul>
	 * <li>{@link #NAME}</li>
	 * <li>{@link #CALENDAR_DISPLAY_NAME}</li>
	 * <li>{@link #VISIBLE}</li>
	 * <li>{@link #SYNC_EVENTS}</li>
	 * </ul>
	 * The following Calendars columns are writable only by a sync adapter
	 * <ul>
	 * <li>{@link #ACCOUNT_NAME}</li>
	 * <li>{@link #ACCOUNT_TYPE}</li>
	 * <li>{@link #CALENDAR_COLOR}</li>
	 * <li>{@link #_SYNC_ID}</li>
	 * <li>{@link #DIRTY}</li>
	 * <li>{@link #OWNER_ACCOUNT}</li>
	 * <li>{@link #MAX_REMINDERS}</li>
	 * <li>{@link #ALLOWED_REMINDERS}</li>
	 * <li>{@link #CAN_MODIFY_TIME_ZONE}</li>
	 * <li>{@link #CAN_ORGANIZER_RESPOND}</li>
	 * <li>{@link #CAN_PARTIALLY_UPDATE}</li>
	 * <li>{@link #CALENDAR_LOCATION}</li>
	 * <li>{@link #CALENDAR_TIME_ZONE}</li>
	 * <li>{@link #CALENDAR_ACCESS_LEVEL}</li>
	 * <li>{@link #DELETED}</li>
	 * <li>{@link #CAL_SYNC1}</li>
	 * <li>{@link #CAL_SYNC2}</li>
	 * <li>{@link #CAL_SYNC3}</li>
	 * <li>{@link #CAL_SYNC4}</li>
	 * <li>{@link #CAL_SYNC5}</li>
	 * <li>{@link #CAL_SYNC6}</li>
	 * <li>{@link #CAL_SYNC7}</li>
	 * <li>{@link #CAL_SYNC8}</li>
	 * <li>{@link #CAL_SYNC9}</li>
	 * <li>{@link #CAL_SYNC10}</li>
	 * </ul>
	 */
	public static final class Calendars
			implements
				BaseColumns,
				SyncColumns,
				SyncColumnsIceCream,
				SyncColumnsFroyo,
				CalendarColumnsIceCream,
				CalendarColumnsFroyo,
				CalendarSyncColumnsHoneycomb {

		/**
		 * This utility class cannot be instantiated
		 */
		private Calendars() {
		}

		/**
		 * The content:// style URL for accessing Calendars
		 */
		@SuppressWarnings("hiding")
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/calendars");

		/**
		 * The default sort order for this table
		 */
		public static final String DEFAULT_SORT_ORDER_ICE_CREAM = CALENDAR_DISPLAY_NAME_ICE_CREAM;
		public static final String DEFAULT_SORT_ORDER_FROYO = CALENDAR_DISPLAY_NAME_FROYO;

		public static final String CALENDAR_NOT_DELETE_WHERE = Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB
				? (HIDDEN_FROYO + "=" + "0")
				: (DELETED + "=" + "0");

		/**
		 * The name of the calendar. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * The default location for the calendar. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String CALENDAR_LOCATION_ICE_CREAM = "calendar_location";
		public static final String CALENDAR_LOCATION_FROYO = "location";

		public static String getAccountTypeKey(int sdk) {
			return sdk < Build.VERSION_CODES.ICE_CREAM_SANDWICH
					? ACCOUNT_TYPE_FROYO
					: ACCOUNT_TYPE_ICE_CREAM;
		}

		public static String getAccountNameKey(int sdk) {
			return sdk < Build.VERSION_CODES.ICE_CREAM_SANDWICH
					? ACCOUNT_NAME_FROYO
					: ACCOUNT_NAME_ICE_CREAM;
		}
	}

	/**
	 * Columns from the Attendees table that other tables join into themselves.
	 */
	protected interface AttendeesColumns {

		/**
		 * The id of the event. Column name.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String EVENT_ID = "event_id";

		/**
		 * The name of the attendee. Column name.
		 * <P>
		 * Type: STRING
		 * </P>
		 */
		public static final String ATTENDEE_NAME = "attendeeName";

		/**
		 * The email address of the attendee. Column name.
		 * <P>
		 * Type: STRING
		 * </P>
		 */
		public static final String ATTENDEE_EMAIL = "attendeeEmail";

		/**
		 * The relationship of the attendee to the user. Column name.
		 * <P>
		 * Type: INTEGER (one of {@link #RELATIONSHIP_ATTENDEE}, ...}.
		 * </P>
		 */
		public static final String ATTENDEE_RELATIONSHIP = "attendeeRelationship";

		public static final int RELATIONSHIP_NONE = 0;
		public static final int RELATIONSHIP_ATTENDEE = 1;
		public static final int RELATIONSHIP_ORGANIZER = 2;
		public static final int RELATIONSHIP_PERFORMER = 3;
		public static final int RELATIONSHIP_SPEAKER = 4;

		/**
		 * The type of attendee. Column name.
		 * <P>
		 * Type: Integer (one of {@link #TYPE_REQUIRED}, {@link #TYPE_OPTIONAL})
		 * </P>
		 */
		public static final String ATTENDEE_TYPE = "attendeeType";

		public static final int TYPE_NONE = 0;
		public static final int TYPE_REQUIRED = 1;
		public static final int TYPE_OPTIONAL = 2;

		/**
		 * The attendance status of the attendee. Column name.
		 * <P>
		 * Type: Integer (one of {@link #ATTENDEE_STATUS_ACCEPTED}, ...).
		 * </P>
		 */
		public static final String ATTENDEE_STATUS = "attendeeStatus";

		public static final int ATTENDEE_STATUS_NONE = 0;
		public static final int ATTENDEE_STATUS_ACCEPTED = 1;
		public static final int ATTENDEE_STATUS_DECLINED = 2;
		public static final int ATTENDEE_STATUS_INVITED = 3;
		public static final int ATTENDEE_STATUS_TENTATIVE = 4;
	}

	/**
	 * Fields and helpers for interacting with Attendees. Each row of this table
	 * represents a single attendee or guest of an event. Calling
	 * {@link #query(ContentResolver, long, String[])} will return a list of
	 * attendees for the event with the given eventId. Both apps and sync
	 * adapters may write to this table. There are six writable fields and all
	 * of them except {@link #ATTENDEE_NAME} must be included when inserting a
	 * new attendee. They are:
	 * <ul>
	 * <li>{@link #EVENT_ID}</li>
	 * <li>{@link #ATTENDEE_NAME}</li>
	 * <li>{@link #ATTENDEE_EMAIL}</li>
	 * <li>{@link #ATTENDEE_RELATIONSHIP}</li>
	 * <li>{@link #ATTENDEE_TYPE}</li>
	 * <li>{@link #ATTENDEE_STATUS}</li>
	 * </ul>
	 */
	public static final class Attendees
			implements
				BaseColumns,
				AttendeesColumns,
				EventsColumnsIceCream,
				EventsColumnsFroyo {

		/**
		 * The content:// style URL for accessing Attendees data
		 */
		@SuppressWarnings("hiding")
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/attendees");
		private static final String ATTENDEES_WHERE = Attendees.EVENT_ID + "=?";

		/**
		 * This utility class cannot be instantiated
		 */
		private Attendees() {
		}

		/**
		 * Queries all attendees associated with the given event. This is a
		 * blocking call and should not be done on the UI thread.
		 *
		 * @param cr
		 *            The content resolver to use for the query
		 * @param eventId
		 *            The id of the event to retrieve attendees for
		 * @param projection
		 *            the columns to return in the cursor
		 * @return A Cursor containing all attendees for the event
		 */
		public static final Cursor query(ContentResolver cr, long eventId, String[] projection) {
			String[] attArgs = { Long.toString(eventId) };
			return cr.query(CONTENT_URI, projection, ATTENDEES_WHERE, attArgs /*
																			 * selection
																			 * args
																			 */, null /*
																					 * sort
																					 * order
																					 */);
		}
	}

	protected interface EventsColumns {
		/**
		 * The {@link Calendars#_ID} of the calendar the event belongs to.
		 * Column name.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String CALENDAR_ID = "calendar_id";

		/**
		 * The title of the event. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String TITLE = "title";

		/**
		 * The description of the event. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String DESCRIPTION = "description";

		/**
		 * Where the event takes place. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String EVENT_LOCATION = "eventLocation";

		/**
		 * A secondary color for the individual event. Reserved for future use.
		 * Column name.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String EVENT_COLOR = "eventColor";

		/**
		 * The event status. Column name.
		 * <P>
		 * Type: INTEGER (one of {@link #STATUS_TENTATIVE}...)
		 * </P>
		 */
		public static final String STATUS = "eventStatus";

		public static final int STATUS_TENTATIVE = 0;
		public static final int STATUS_CONFIRMED = 1;
		public static final int STATUS_CANCELED = 2;

		/**
		 * This is a copy of the attendee status for the owner of this event.
		 * This field is copied here so that we can efficiently filter out
		 * events that are declined without having to look in the Attendees
		 * table. Column name.
		 *
		 * <P>
		 * Type: INTEGER (int)
		 * </P>
		 */
		public static final String SELF_ATTENDEE_STATUS = "selfAttendeeStatus";

		/**
		 * The time the event starts in UTC millis since epoch. Column name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String DTSTART = "dtstart";

		/**
		 * The time the event ends in UTC millis since epoch. Column name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String DTEND = "dtend";

		/**
		 * The duration of the event in RFC2445 format. Column name.
		 * <P>
		 * Type: TEXT (duration in RFC2445 format)
		 * </P>
		 */
		public static final String DURATION = "duration";

		/**
		 * The timezone for the event. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String EVENT_TIMEZONE = "eventTimezone";

		/**
		 * Is the event all day (time zone independent). Column name.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String ALL_DAY = "allDay";

		/**
		 * Defines how the event shows up for others when the calendar is
		 * shared. Column name.
		 * <P>
		 * Type: INTEGER (One of {@link #ACCESS_DEFAULT}, ...)
		 * </P>
		 */
		public static final String ACCESS_LEVEL = "accessLevel";

		/**
		 * Default access is controlled by the server and will be treated as
		 * public on the device.
		 */
		public static final int ACCESS_DEFAULT = 0;
		/**
		 * Confidential is not used by the app.
		 */
		public static final int ACCESS_CONFIDENTIAL = 1;
		/**
		 * Private shares the event as a free/busy slot with no details.
		 */
		public static final int ACCESS_PRIVATE = 2;
		/**
		 * Public makes the contents visible to anyone with access to the
		 * calendar.
		 */
		public static final int ACCESS_PUBLIC = 3;

		/**
		 * Whether the event has an alarm or not. Column name.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String HAS_ALARM = "hasAlarm";

		/**
		 * Whether the event has extended properties or not. Column name.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String HAS_EXTENDED_PROPERTIES = "hasExtendedProperties";

		/**
		 * The recurrence rule for the event. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String RRULE = "rrule";

		/**
		 * The recurrence dates for the event. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String RDATE = "rdate";

		/**
		 * The recurrence exception rule for the event. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String EXRULE = "exrule";

		/**
		 * The recurrence exception dates for the event. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String EXDATE = "exdate";

		/**
		 * The allDay status (true or false) of the original recurring event for
		 * which this event is an exception. Column name.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String ORIGINAL_ALL_DAY = "originalAllDay";

		/**
		 * The last date this event repeats on, or NULL if it never ends. Column
		 * name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String LAST_DATE = "lastDate";

		/**
		 * Whether the event has attendee information. True if the event has
		 * full attendee data, false if the event has information about self
		 * only. Column name.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String HAS_ATTENDEE_DATA = "hasAttendeeData";

		/**
		 * Whether guests can modify the event. Column name.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String GUESTS_CAN_MODIFY = "guestsCanModify";

		/**
		 * Whether guests can invite other guests. Column name.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String GUESTS_CAN_INVITE_OTHERS = "guestsCanInviteOthers";

		/**
		 * Whether guests can see the list of attendees. Column name.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String GUESTS_CAN_SEE_GUESTS = "guestsCanSeeGuests";

		/**
		 * Email of the organizer (owner) of the event. Column name.
		 * <P>
		 * Type: STRING
		 * </P>
		 */
		public static final String ORGANIZER = "organizer";

		/**
		 * Whether the user can invite others to the event. The
		 * GUESTS_CAN_INVITE_OTHERS is a setting that applies to an arbitrary
		 * guest, while CAN_INVITE_OTHERS indicates if the user can invite
		 * others (either through GUESTS_CAN_INVITE_OTHERS or because the user
		 * has modify access to the event). Column name.
		 * <P>
		 * Type: INTEGER (boolean, readonly)
		 * </P>
		 */
		public static final String CAN_INVITE_OTHERS = "canInviteOthers";

		/**
		 * The original instance time of the recurring event for which this
		 * event is an exception. Column name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String ORIGINAL_INSTANCE_TIME = "originalInstanceTime";
	}

	/**
	 * Columns from the Events table that other tables join into themselves.
	 */
	protected interface EventsColumnsIceCream {
		/**
		 * This column is available for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SYNC_DATA1 = "sync_data1";

		/**
		 * This column is available for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SYNC_DATA2 = "sync_data2";

		/**
		 * This column is available for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SYNC_DATA3 = "sync_data3";

		/**
		 * This column is available for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SYNC_DATA4 = "sync_data4";

		/**
		 * This column is available for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SYNC_DATA5 = "sync_data5";

		/**
		 * This column is available for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SYNC_DATA6 = "sync_data6";

		/**
		 * This column is available for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SYNC_DATA7 = "sync_data7";

		/**
		 * This column is available for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SYNC_DATA8 = "sync_data8";

		/**
		 * This column is available for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SYNC_DATA9 = "sync_data9";

		/**
		 * This column is available for use by sync adapters. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String SYNC_DATA10 = "sync_data10";

		/**
		 * Used to indicate that a row is not a real event but an original copy
		 * of a locally modified event. A copy is made when an event changes
		 * from non-dirty to dirty and the event is on a calendar with
		 * {@link Calendars#CAN_PARTIALLY_UPDATE} set to 1. This copy does not
		 * get expanded in the instances table and is only visible in queries
		 * made by a sync adapter. The copy gets removed when the event is
		 * changed back to non-dirty by a sync adapter.
		 * <P>
		 * Type: INTEGER (boolean)
		 * </P>
		 */
		public static final String LAST_SYNCED_ICE_CREAM = "lastSynced";

		/**
		 * The timezone for the end time of the event. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String EVENT_END_TIMEZONE_ICE_CREAM = "eventEndTimezone";

		/**
		 * If this event counts as busy time or is still free time that can be
		 * scheduled over. Column name.
		 * <P>
		 * Type: INTEGER (One of {@link #AVAILABILITY_BUSY},
		 * {@link #AVAILABILITY_FREE})
		 * </P>
		 */
		public static final String AVAILABILITY_ICE_CREAM = "availability";

		/**
		 * Indicates that this event takes up time and will conflict with other
		 * events.
		 */
		public static final int AVAILABILITY_BUSY = 0;
		/**
		 * Indicates that this event is free time and will not conflict with
		 * other events.
		 */
		public static final int AVAILABILITY_FREE = 1;

		/**
		 * The {@link Events#_ID} of the original recurring event for which this
		 * event is an exception. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String ORIGINAL_ID_ICE_CREAM = "original_id";

		/**
		 * The _sync_id of the original recurring event for which this event is
		 * an exception. The provider should keep the original_id in sync when
		 * this is updated. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String ORIGINAL_SYNC_ID_ICE_CREAM = "original_sync_id";
	}

	protected interface EventsColumnsFroyo {
		public static final String HTML_URI_FROYO = "htmlUri";
		public static final String COMMENTS_URI_FROYO = "commentsUri";
		public static final String VISIBILITY_FROYO = "visibility";
		public static final String TRANSPARENCY_FROYO = "transparency";
		public static final String ORIGINAL_EVENT_FROYO = "originalEvent";
		public static final String DTSTART2_FROYO = "dtstart2";
		public static final String DTEND2_FROYO = "dtend2";
		public static final String EVENT_TIMEZONE2_FROYO = "eventTimezone2";
		public static final String SYNC_ADAPTER_DATA_FROYO = "syncAdapterData";
	}

	/**
	 * Class that represents an Event Entity. There is one entry per event.
	 * Recurring events show up as a single entry. This is a helper class to
	 * make batch operations easier. A {@link ContentResolver} or
	 * {@link ContentProviderClient} is required as the helper does additional
	 * queries to add reminders and attendees to each entry.
	 */
	public static final class EventsEntity
			implements
				BaseColumns,
				SyncColumns,
				SyncColumnsIceCream,
				SyncColumnsFroyo,
				EventsColumns,
				EventsColumnsIceCream,
				EventsColumnsFroyo {
		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/event_entities");

		/**
		 * This utility class cannot be instantiated
		 */
		private EventsEntity() {
		}

		/**
		 * Creates a new iterator for events
		 *
		 * @param cursor
		 *            An event query
		 * @param resolver
		 *            For performing additional queries
		 * @return an EntityIterator containing one entity per event in the
		 *         cursor
		 */
		public static EntityIterator newEntityIterator(Cursor cursor, ContentResolver resolver,
				int sdk) {
			return new EntityIteratorImpl(cursor, resolver, sdk);
		}

		/**
		 * Creates a new iterator for events
		 *
		 * @param cursor
		 *            An event query
		 * @param provider
		 *            For performing additional queries
		 * @return an EntityIterator containing one entity per event in the
		 *         cursor
		 */
		public static EntityIterator newEntityIterator(Cursor cursor,
				ContentProviderClient provider, int sdk) {
			return new EntityIteratorImpl(cursor, provider, sdk);
		}

		private static class EntityIteratorImpl extends CursorEntityIterator {
			private final ContentResolver mResolver;
			private final ContentProviderClient mProvider;

			private static final String[] REMINDERS_PROJECTION = new String[] { Reminders.MINUTES,
					Reminders.METHOD, };
			private static final int COLUMN_MINUTES = 0;
			private static final int COLUMN_METHOD = 1;

			private static final String[] ATTENDEES_PROJECTION = new String[] {
					Attendees.ATTENDEE_NAME, Attendees.ATTENDEE_EMAIL,
					Attendees.ATTENDEE_RELATIONSHIP, Attendees.ATTENDEE_TYPE,
					Attendees.ATTENDEE_STATUS, };
			private static final int COLUMN_ATTENDEE_NAME = 0;
			private static final int COLUMN_ATTENDEE_EMAIL = 1;
			private static final int COLUMN_ATTENDEE_RELATIONSHIP = 2;
			private static final int COLUMN_ATTENDEE_TYPE = 3;
			private static final int COLUMN_ATTENDEE_STATUS = 4;
			private static final String[] EXTENDED_PROJECTION = new String[] {
					ExtendedProperties._ID, ExtendedProperties.NAME, ExtendedProperties.VALUE };
			private static final int COLUMN_ID = 0;
			private static final int COLUMN_NAME = 1;
			private static final int COLUMN_VALUE = 2;

			private static final String WHERE_EVENT_ID = "event_id=?";

			private int mSdk;

			public EntityIteratorImpl(Cursor cursor, ContentResolver resolver, int sdk) {
				super(cursor);
				mResolver = resolver;
				mProvider = null;
				mSdk = sdk;
			}

			public EntityIteratorImpl(Cursor cursor, ContentProviderClient provider, int sdk) {
				super(cursor);
				mResolver = null;
				mProvider = provider;
				mSdk = sdk;
			}

			@Override
			public Entity getEntityAndIncrementCursor(Cursor cursor) throws RemoteException {
				// we expect the cursor is already at the row we need to read from
				final long eventId = cursor.getLong(cursor.getColumnIndexOrThrow(Events._ID));
				ContentValues cv = new ContentValues();
				cv.put(Events._ID, eventId);

				if (mSdk < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
					loadFroyoField(cursor, cv);
				} else {
					loadIceCreamField(cursor, cv);
				}

				Entity entity = new Entity(cv);
				Cursor subCursor;
				if (mResolver != null) {
					subCursor = mResolver
							.query(Reminders.CONTENT_URI, REMINDERS_PROJECTION, WHERE_EVENT_ID,
									new String[] { Long.toString(eventId) } /* selectionArgs */,
									null /* sortOrder */);
				} else {
					subCursor = mProvider
							.query(Reminders.CONTENT_URI, REMINDERS_PROJECTION, WHERE_EVENT_ID,
									new String[] { Long.toString(eventId) } /* selectionArgs */,
									null /* sortOrder */);
				}
				try {
					while (subCursor.moveToNext()) {
						ContentValues reminderValues = new ContentValues();
						reminderValues.put(Reminders.MINUTES, subCursor.getInt(COLUMN_MINUTES));
						reminderValues.put(Reminders.METHOD, subCursor.getInt(COLUMN_METHOD));
						entity.addSubValue(Reminders.CONTENT_URI, reminderValues);
					}
				} finally {
					subCursor.close();
				}

				if (mResolver != null) {
					subCursor = mResolver
							.query(Attendees.CONTENT_URI, ATTENDEES_PROJECTION, WHERE_EVENT_ID,
									new String[] { Long.toString(eventId) } /* selectionArgs */,
									null /* sortOrder */);
				} else {
					subCursor = mProvider
							.query(Attendees.CONTENT_URI, ATTENDEES_PROJECTION, WHERE_EVENT_ID,
									new String[] { Long.toString(eventId) } /* selectionArgs */,
									null /* sortOrder */);
				}
				try {
					while (subCursor.moveToNext()) {
						ContentValues attendeeValues = new ContentValues();
						attendeeValues.put(Attendees.ATTENDEE_NAME,
								subCursor.getString(COLUMN_ATTENDEE_NAME));
						attendeeValues.put(Attendees.ATTENDEE_EMAIL,
								subCursor.getString(COLUMN_ATTENDEE_EMAIL));
						attendeeValues.put(Attendees.ATTENDEE_RELATIONSHIP,
								subCursor.getInt(COLUMN_ATTENDEE_RELATIONSHIP));
						attendeeValues.put(Attendees.ATTENDEE_TYPE,
								subCursor.getInt(COLUMN_ATTENDEE_TYPE));
						attendeeValues.put(Attendees.ATTENDEE_STATUS,
								subCursor.getInt(COLUMN_ATTENDEE_STATUS));
						entity.addSubValue(Attendees.CONTENT_URI, attendeeValues);
					}
				} finally {
					subCursor.close();
				}

				if (mResolver != null) {
					subCursor = mResolver
							.query(ExtendedProperties.CONTENT_URI, EXTENDED_PROJECTION,
									WHERE_EVENT_ID,
									new String[] { Long.toString(eventId) } /* selectionArgs */,
									null /* sortOrder */);
				} else {
					subCursor = mProvider
							.query(ExtendedProperties.CONTENT_URI, EXTENDED_PROJECTION,
									WHERE_EVENT_ID,
									new String[] { Long.toString(eventId) } /* selectionArgs */,
									null /* sortOrder */);
				}
				try {
					while (subCursor.moveToNext()) {
						ContentValues extendedValues = new ContentValues();
						extendedValues.put(ExtendedProperties._ID, subCursor.getString(COLUMN_ID));
						extendedValues.put(ExtendedProperties.NAME,
								subCursor.getString(COLUMN_NAME));
						extendedValues.put(ExtendedProperties.VALUE,
								subCursor.getString(COLUMN_VALUE));
						entity.addSubValue(ExtendedProperties.CONTENT_URI, extendedValues);
					}
				} finally {
					subCursor.close();
				}

				cursor.moveToNext();
				return entity;
			}

			private void loadFroyoField(Cursor cursor, ContentValues cv) {
				cursorIntToContentValuesIfPresent(cursor, cv, CALENDAR_ID);
				cursorStringToContentValuesIfPresent(cursor, cv, TITLE);
				cursorLongToContentValuesIfPresent(cursor, cv, DTSTART);
				cursorLongToContentValuesIfPresent(cursor, cv, DTEND);
				cursorStringToContentValuesIfPresent(cursor, cv, EVENT_TIMEZONE);
				cursorStringToContentValuesIfPresent(cursor, cv, DURATION);
				cursorStringToContentValuesIfPresent(cursor, cv, EVENT_LOCATION);
				cursorStringToContentValuesIfPresent(cursor, cv, DESCRIPTION);
				cursorIntToContentValuesIfPresent(cursor, cv, STATUS);
				cursorIntToContentValuesIfPresent(cursor, cv, SELF_ATTENDEE_STATUS);
				cursorStringToContentValuesIfPresent(cursor, cv, ALL_DAY);
				cursorStringToContentValuesIfPresent(cursor, cv, RRULE);
				cursorStringToContentValuesIfPresent(cursor, cv, RDATE);
				cursorStringToContentValuesIfPresent(cursor, cv, EXRULE);
				cursorStringToContentValuesIfPresent(cursor, cv, EXDATE);
				cursorIntToContentValuesIfPresent(cursor, cv, HAS_ATTENDEE_DATA);
				cursorIntToContentValuesIfPresent(cursor, cv, GUESTS_CAN_MODIFY);
				cursorIntToContentValuesIfPresent(cursor, cv, GUESTS_CAN_INVITE_OTHERS);
				cursorIntToContentValuesIfPresent(cursor, cv, GUESTS_CAN_SEE_GUESTS);
				cursorStringToContentValuesIfPresent(cursor, cv, ORGANIZER);
				cursorIntToContentValuesIfPresent(cursor, cv, DELETED);
				cursorLongToContentValuesIfPresent(cursor, cv, ORIGINAL_INSTANCE_TIME);
				cursorIntToContentValuesIfPresent(cursor, cv, ORIGINAL_ALL_DAY);
				cursorStringToContentValuesIfPresent(cursor, cv, _SYNC_ID);
				cursorStringToContentValuesIfPresent(cursor, cv, HAS_ALARM);
				cursorStringToContentValuesIfPresent(cursor, cv, HAS_EXTENDED_PROPERTIES);
				cursorLongToContentValuesIfPresent(cursor, cv, LAST_DATE);

				cursorStringToContentValuesIfPresent(cursor, cv, HTML_URI_FROYO);
				cursorStringToContentValuesIfPresent(cursor, cv, COMMENTS_URI_FROYO);
				cursorIntToContentValuesIfPresent(cursor, cv, VISIBILITY_FROYO);
				cursorIntToContentValuesIfPresent(cursor, cv, TRANSPARENCY_FROYO);
				cursorIntToContentValuesIfPresent(cursor, cv, ORIGINAL_EVENT_FROYO);

				//				cursorLongToContentValuesIfPresent(cursor, cv, DTSTART2_FROYO);
				//				cursorLongToContentValuesIfPresent(cursor, cv, DTEND2_FROYO);
				//				cursorStringToContentValuesIfPresent(cursor, cv, EVENT_TIMEZONE2_FROYO);
				//				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_ADAPTER_DATA_FROYO);

				cursorStringToContentValuesIfPresent(cursor, cv, ACCOUNT_NAME_FROYO);
				cursorStringToContentValuesIfPresent(cursor, cv, ACCOUNT_TYPE_FROYO);
			}

			private void loadIceCreamField(Cursor cursor, ContentValues cv) {
				cursorIntToContentValuesIfPresent(cursor, cv, CALENDAR_ID);
				cursorStringToContentValuesIfPresent(cursor, cv, TITLE);
				cursorStringToContentValuesIfPresent(cursor, cv, DESCRIPTION);
				cursorStringToContentValuesIfPresent(cursor, cv, EVENT_LOCATION);
				cursorIntToContentValuesIfPresent(cursor, cv, STATUS);
				cursorIntToContentValuesIfPresent(cursor, cv, SELF_ATTENDEE_STATUS);
				cursorLongToContentValuesIfPresent(cursor, cv, DTSTART);
				cursorLongToContentValuesIfPresent(cursor, cv, DTEND);
				cursorStringToContentValuesIfPresent(cursor, cv, DURATION);
				cursorStringToContentValuesIfPresent(cursor, cv, EVENT_TIMEZONE);
				cursorStringToContentValuesIfPresent(cursor, cv, EVENT_END_TIMEZONE_ICE_CREAM);
				cursorStringToContentValuesIfPresent(cursor, cv, ALL_DAY);
				cursorIntToContentValuesIfPresent(cursor, cv, ACCESS_LEVEL);
				cursorIntToContentValuesIfPresent(cursor, cv, AVAILABILITY_ICE_CREAM);
				cursorStringToContentValuesIfPresent(cursor, cv, HAS_ALARM);
				cursorStringToContentValuesIfPresent(cursor, cv, HAS_EXTENDED_PROPERTIES);
				cursorStringToContentValuesIfPresent(cursor, cv, RRULE);
				cursorStringToContentValuesIfPresent(cursor, cv, RDATE);
				cursorStringToContentValuesIfPresent(cursor, cv, EXRULE);
				cursorStringToContentValuesIfPresent(cursor, cv, EXDATE);
				cursorStringToContentValuesIfPresent(cursor, cv, ORIGINAL_SYNC_ID_ICE_CREAM);
				cursorStringToContentValuesIfPresent(cursor, cv, ORIGINAL_ID_ICE_CREAM);
				cursorLongToContentValuesIfPresent(cursor, cv, ORIGINAL_INSTANCE_TIME);
				cursorIntToContentValuesIfPresent(cursor, cv, ORIGINAL_ALL_DAY);
				cursorLongToContentValuesIfPresent(cursor, cv, LAST_DATE);
				cursorIntToContentValuesIfPresent(cursor, cv, HAS_ATTENDEE_DATA);
				cursorIntToContentValuesIfPresent(cursor, cv, GUESTS_CAN_INVITE_OTHERS);
				cursorIntToContentValuesIfPresent(cursor, cv, GUESTS_CAN_MODIFY);
				cursorIntToContentValuesIfPresent(cursor, cv, GUESTS_CAN_SEE_GUESTS);
				cursorStringToContentValuesIfPresent(cursor, cv, ORGANIZER);
				cursorStringToContentValuesIfPresent(cursor, cv, _SYNC_ID);
				cursorLongToContentValuesIfPresent(cursor, cv, DIRTY_ICE_CREAM);
				cursorLongToContentValuesIfPresent(cursor, cv, LAST_SYNCED_ICE_CREAM);
				cursorIntToContentValuesIfPresent(cursor, cv, DELETED);
				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_DATA1);
				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_DATA2);
				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_DATA3);
				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_DATA4);
				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_DATA5);
				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_DATA6);
				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_DATA7);
				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_DATA8);
				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_DATA9);
				cursorStringToContentValuesIfPresent(cursor, cv, SYNC_DATA10);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC1);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC2);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC3);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC4);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC5);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC6);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC7);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC8);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC9);
				cursorStringToContentValuesIfPresent(cursor, cv, CAL_SYNC10);
			}
		}
	}

	/**
	 * Constants and helpers for the Events table, which contains details for
	 * individual events. <h3>Operations</h3> All operations can be done either
	 * as an app or as a sync adapter. To perform an operation as a sync adapter
	 * {@link #CALLER_IS_SYNCADAPTER} should be set to true and
	 * {@link #ACCOUNT_NAME} and {@link #ACCOUNT_TYPE} must be set in the Uri
	 * parameters. See
	 * {@link Uri.Builder#appendQueryParameter(java.lang.String, java.lang.String)}
	 * for details on adding parameters. Sync adapters have write access to more
	 * columns but are restricted to a single account at a time.
	 * <dl>
	 * <dt><b>Insert</b></dt>
	 * <dd>When inserting a new event the following fields must be included:
	 * <ul>
	 * <li>dtstart</li>
	 * <li>dtend -or- a (rrule or rdate) and a duration</li>
	 * <li>a calendar_id</li>
	 * </ul>
	 * There are also further requirements when inserting or updating an event.
	 * See the section on Writing to Events.</dd>
	 * <dt><b>Update</b></dt>
	 * <dd>To perform an update of an Event the {@link Events#_ID} of the event
	 * should be provided either as an appended id to the Uri (
	 * {@link ContentUris#withAppendedId}) or as the first selection item--the
	 * selection should start with "_id=?" and the first selectionArg should be
	 * the _id of the event. Updates may also be done using a selection and no
	 * id. Updating an event must respect the same rules as inserting and is
	 * further restricted in the fields that can be written. See the section on
	 * Writing to Events.</dd>
	 * <dt><b>Delete</b></dt>
	 * <dd>Events can be deleted either by the {@link Events#_ID} as an appended
	 * id on the Uri or using any standard selection. If an appended id is used
	 * a selection is not allowed. There are two versions of delete: as an app
	 * and as a sync adapter. An app delete will set the deleted column on an
	 * event and remove all instances of that event. A sync adapter delete will
	 * remove the event from the database and all associated data.</dd>
	 * <dt><b>Query</b></dt>
	 * <dd>Querying the Events table will get you all information about a set of
	 * events except their reminders, attendees, and extended properties. There
	 * will be one row returned for each event that matches the query selection,
	 * or at most a single row if the {@link Events#_ID} is appended to the Uri.
	 * Recurring events will only return a single row regardless of the number
	 * of times that event repeats.</dd>
	 * </dl>
	 * <h3>Writing to Events</h3> There are further restrictions on all Updates
	 * and Inserts in the Events table:
	 * <ul>
	 * <li>If allDay is set to 1 eventTimezone must be {@link Time#TIMEZONE_UTC}
	 * and the time must correspond to a midnight boundary.</li>
	 * <li>Exceptions are not allowed to recur. If rrule or rdate is not empty,
	 * original_id and original_sync_id must be empty.</li>
	 * <li>In general a calendar_id should not be modified after insertion. This
	 * is not explicitly forbidden but many sync adapters will not behave in an
	 * expected way if the calendar_id is modified.</li>
	 * </ul>
	 * The following Events columns are writable by both an app and a sync
	 * adapter.
	 * <ul>
	 * <li>{@link #CALENDAR_ID}</li>
	 * <li>{@link #ORGANIZER}</li>
	 * <li>{@link #TITLE}</li>
	 * <li>{@link #EVENT_LOCATION}</li>
	 * <li>{@link #DESCRIPTION}</li>
	 * <li>{@link #EVENT_COLOR}</li>
	 * <li>{@link #DTSTART}</li>
	 * <li>{@link #DTEND}</li>
	 * <li>{@link #EVENT_TIMEZONE}</li>
	 * <li>{@link #EVENT_END_TIMEZONE}</li>
	 * <li>{@link #DURATION}</li>
	 * <li>{@link #ALL_DAY}</li>
	 * <li>{@link #RRULE}</li>
	 * <li>{@link #RDATE}</li>
	 * <li>{@link #EXRULE}</li>
	 * <li>{@link #EXDATE}</li>
	 * <li>{@link #ORIGINAL_ID}</li>
	 * <li>{@link #ORIGINAL_SYNC_ID}</li>
	 * <li>{@link #ORIGINAL_INSTANCE_TIME}</li>
	 * <li>{@link #ORIGINAL_ALL_DAY}</li>
	 * <li>{@link #ACCESS_LEVEL}</li>
	 * <li>{@link #AVAILABILITY}</li>
	 * <li>{@link #GUESTS_CAN_MODIFY}</li>
	 * <li>{@link #GUESTS_CAN_INVITE_OTHERS}</li>
	 * <li>{@link #GUESTS_CAN_SEE_GUESTS}</li>
	 * </ul>
	 * The following Events columns are writable only by a sync adapter
	 * <ul>
	 * <li>{@link #DIRTY}</li>
	 * <li>{@link #_SYNC_ID}</li>
	 * <li>{@link #SYNC_DATA1}</li>
	 * <li>{@link #SYNC_DATA2}</li>
	 * <li>{@link #SYNC_DATA3}</li>
	 * <li>{@link #SYNC_DATA4}</li>
	 * <li>{@link #SYNC_DATA5}</li>
	 * <li>{@link #SYNC_DATA6}</li>
	 * <li>{@link #SYNC_DATA7}</li>
	 * <li>{@link #SYNC_DATA8}</li>
	 * <li>{@link #SYNC_DATA9}</li>
	 * <li>{@link #SYNC_DATA10}</li>
	 * </ul>
	 * The remaining columns are either updated by the provider only or are
	 * views into other tables and cannot be changed through the Events table.
	 */
	public static final class Events
			implements
				BaseColumns,
				SyncColumns,
				SyncColumnsIceCream,
				SyncColumnsFroyo,
				EventsColumnsIceCream,
				EventsColumnsFroyo,
				CalendarColumnsIceCream,
				CalendarColumnsFroyo,
				EventsColumns {

		/**
		 * The content:// style URL for interacting with events. Appending an
		 * event id using {@link ContentUris#withAppendedId(Uri, long)} will
		 * specify a single event.
		 */
		@SuppressWarnings("hiding")
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/events");

		/**
		 * The content:// style URI for recurring event exceptions. Insertions
		 * require an appended event ID. Deletion of exceptions requires both
		 * the original event ID and the exception event ID (see
		 * {@link Uri.Builder#appendPath}).
		 */
		public static final Uri CONTENT_EXCEPTION_URI = Uri.parse("content://" + AUTHORITY
				+ "/exception");

		/**
		 * This utility class cannot be instantiated
		 */
		private Events() {
		}

		/**
		 * The default sort order for this table
		 */
		private static final String DEFAULT_SORT_ORDER = "";

		public static final String EVENT_NOT_DELETE_WHERE = Events.DELETED + "=" + "0";

	}

	/**
	 * Fields and helpers for interacting with Instances. An instance is a
	 * single occurrence of an event including time zone specific start and end
	 * days and minutes. The instances table is not writable and only provides a
	 * way to query event occurrences.
	 */
	public static final class Instances
			implements
				BaseColumns,
				EventsColumnsIceCream,
				EventsColumnsFroyo,
				CalendarColumnsIceCream,
				CalendarColumnsFroyo {

		private static final String WHERE_CALENDARS_SELECTED = (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH
				? Calendars.HIDDEN_FROYO
				: Calendars.VISIBLE_ICE_CREAM) + "=?";
		private static final String[] WHERE_CALENDARS_ARGS = { (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH
				? "1"
				: "0") };

		/**
		 * This utility class cannot be instantiated
		 */
		private Instances() {
		}

		/**
		 * Performs a query to return all visible instances in the given range.
		 * This is a blocking function and should not be done on the UI thread.
		 * This will cause an expansion of recurring events to fill this time
		 * range if they are not already expanded and will slow down for larger
		 * time ranges with many recurring events.
		 *
		 * @param cr
		 *            The ContentResolver to use for the query
		 * @param projection
		 *            The columns to return
		 * @param begin
		 *            The start of the time range to query in UTC millis since
		 *            epoch
		 * @param end
		 *            The end of the time range to query in UTC millis since
		 *            epoch
		 * @return A Cursor containing all instances in the given range
		 */
		public static final Cursor query(ContentResolver cr, String[] projection, long begin,
				long end) {
			Uri.Builder builder = CONTENT_URI.buildUpon();
			ContentUris.appendId(builder, begin);
			ContentUris.appendId(builder, end);
			return cr.query(builder.build(), projection, WHERE_CALENDARS_SELECTED,
					WHERE_CALENDARS_ARGS, DEFAULT_SORT_ORDER);
		}

		/**
		 * Performs a query to return all visible instances in the given range
		 * that match the given query. This is a blocking function and should
		 * not be done on the UI thread. This will cause an expansion of
		 * recurring events to fill this time range if they are not already
		 * expanded and will slow down for larger time ranges with many
		 * recurring events.
		 *
		 * @param cr
		 *            The ContentResolver to use for the query
		 * @param projection
		 *            The columns to return
		 * @param begin
		 *            The start of the time range to query in UTC millis since
		 *            epoch
		 * @param end
		 *            The end of the time range to query in UTC millis since
		 *            epoch
		 * @param searchQuery
		 *            A string of space separated search terms. Segments
		 *            enclosed by double quotes will be treated as a single
		 *            term.
		 * @return A Cursor of instances matching the search terms in the given
		 *         time range
		 */
		public static final Cursor query(ContentResolver cr, String[] projection, long begin,
				long end, String searchQuery) {
			Uri.Builder builder = CONTENT_SEARCH_URI.buildUpon();
			ContentUris.appendId(builder, begin);
			ContentUris.appendId(builder, end);
			builder = builder.appendPath(searchQuery);
			return cr.query(builder.build(), projection, WHERE_CALENDARS_SELECTED,
					WHERE_CALENDARS_ARGS, DEFAULT_SORT_ORDER);
		}

		/**
		 * The content:// style URL for querying an instance range. The begin
		 * and end of the range to query should be added as path segments if
		 * this is used directly.
		 */
		@SuppressWarnings("hiding")
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/instances/when");
		/**
		 * The content:// style URL for querying an instance range by Julian
		 * Day. The start and end day should be added as path segments if this
		 * is used directly.
		 */
		public static final Uri CONTENT_BY_DAY_URI = Uri.parse("content://" + AUTHORITY
				+ "/instances/whenbyday");
		/**
		 * The content:// style URL for querying an instance range with a search
		 * term. The begin, end, and search string should be appended as path
		 * segments if this is used directly.
		 */
		public static final Uri CONTENT_SEARCH_URI = Uri.parse("content://" + AUTHORITY
				+ "/instances/search");
		/**
		 * The content:// style URL for querying an instance range with a search
		 * term. The start day, end day, and search string should be appended as
		 * path segments if this is used directly.
		 */
		public static final Uri CONTENT_SEARCH_BY_DAY_URI = Uri.parse("content://" + AUTHORITY
				+ "/instances/searchbyday");

		/**
		 * The default sort order for this table.
		 */
		private static final String DEFAULT_SORT_ORDER = "begin ASC";

		/**
		 * The beginning time of the instance, in UTC milliseconds. Column name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String BEGIN = "begin";

		/**
		 * The ending time of the instance, in UTC milliseconds. Column name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String END = "end";

		/**
		 * The _id of the event for this instance. Column name.
		 * <P>
		 * Type: INTEGER (long, foreign key to the Events table)
		 * </P>
		 */
		public static final String EVENT_ID = "event_id";

		/**
		 * The Julian start day of the instance, relative to the local time
		 * zone. Column name.
		 * <P>
		 * Type: INTEGER (int)
		 * </P>
		 */
		public static final String START_DAY = "startDay";

		/**
		 * The Julian end day of the instance, relative to the local time zone.
		 * Column name.
		 * <P>
		 * Type: INTEGER (int)
		 * </P>
		 */
		public static final String END_DAY = "endDay";

		/**
		 * The start minute of the instance measured from midnight in the local
		 * time zone. Column name.
		 * <P>
		 * Type: INTEGER (int)
		 * </P>
		 */
		public static final String START_MINUTE = "startMinute";

		/**
		 * The end minute of the instance measured from midnight in the local
		 * time zone. Column name.
		 * <P>
		 * Type: INTEGER (int)
		 * </P>
		 */
		public static final String END_MINUTE = "endMinute";
	}

	protected interface CalendarCacheColumns {
		/**
		 * The key for the setting. Keys are defined in {@link CalendarCache}.
		 */
		public static final String KEY = "key";

		/**
		 * The value of the given setting.
		 */
		public static final String VALUE = "value";
	}

	/**
	 * CalendarCache stores some settings for calendar including the current
	 * time zone for the instances. These settings are stored using a key/value
	 * scheme. A {@link #KEY} must be specified when updating these values.
	 */
	public static final class CalendarCache implements CalendarCacheColumns {
		/**
		 * The URI to use for retrieving the properties from the Calendar db.
		 */
		public static final Uri URI = Uri.parse("content://" + AUTHORITY + "/properties");

		/**
		 * This utility class cannot be instantiated
		 */
		private CalendarCache() {
		}

		/**
		 * They key for updating the use of auto/home time zones in Calendar.
		 * Valid values are {@link #TIMEZONE_TYPE_AUTO} or
		 * {@link #TIMEZONE_TYPE_HOME}.
		 */
		public static final String KEY_TIMEZONE_TYPE = "timezoneType";

		/**
		 * The key for updating the time zone used by the provider when it
		 * generates the instances table. This should only be written if the
		 * type is set to {@link #TIMEZONE_TYPE_HOME}. A valid time zone id
		 * should be written to this field.
		 */
		public static final String KEY_TIMEZONE_INSTANCES = "timezoneInstances";

		/**
		 * The key for reading the last time zone set by the user. This should
		 * only be read by apps and it will be automatically updated whenever
		 * {@link #KEY_TIMEZONE_INSTANCES} is updated with
		 * {@link #TIMEZONE_TYPE_HOME} set.
		 */
		public static final String KEY_TIMEZONE_INSTANCES_PREVIOUS = "timezoneInstancesPrevious";

		/**
		 * The value to write to {@link #KEY_TIMEZONE_TYPE} if the provider
		 * should stay in sync with the device's time zone.
		 */
		public static final String TIMEZONE_TYPE_AUTO = "auto";

		/**
		 * The value to write to {@link #KEY_TIMEZONE_TYPE} if the provider
		 * should use a fixed time zone set by the user.
		 */
		public static final String TIMEZONE_TYPE_HOME = "home";
	}

	/**
	 * A few Calendar globals are needed in the CalendarProvider for expanding
	 * the Instances table and these are all stored in the first (and only) row
	 * of the CalendarMetaData table.
	 *
	 * @hide
	 */
	protected interface CalendarMetaDataColumns {
		/**
		 * The local timezone that was used for precomputing the fields in the
		 * Instances table.
		 */
		public static final String LOCAL_TIMEZONE = "localTimezone";

		/**
		 * The minimum time used in expanding the Instances table, in UTC
		 * milliseconds.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String MIN_INSTANCE = "minInstance";

		/**
		 * The maximum time used in expanding the Instances table, in UTC
		 * milliseconds.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String MAX_INSTANCE = "maxInstance";

		/**
		 * The minimum Julian day in the EventDays table.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String MIN_EVENTDAYS = "minEventDays";

		/**
		 * The maximum Julian day in the EventDays table.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String MAX_EVENTDAYS = "maxEventDays";
	}

	/**
	 * @hide
	 */
	public static final class CalendarMetaData implements CalendarMetaDataColumns, BaseColumns {

		/**
		 * This utility class cannot be instantiated
		 */
		private CalendarMetaData() {
		}
	}

	protected interface EventDaysColumns {
		/**
		 * The Julian starting day number. Column name.
		 * <P>
		 * Type: INTEGER (int)
		 * </P>
		 */
		public static final String STARTDAY = "startDay";
		/**
		 * The Julian ending day number. Column name.
		 * <P>
		 * Type: INTEGER (int)
		 * </P>
		 */
		public static final String ENDDAY = "endDay";

	}

	/**
	 * Fields and helpers for querying for a list of days that contain events.
	 */
	public static final class EventDays implements EventDaysColumns {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/instances/groupbyday");
		private static final String SELECTION = "selected=1";

		/**
		 * This utility class cannot be instantiated
		 */
		private EventDays() {
		}

		/**
		 * Retrieves the days with events for the Julian days starting at
		 * "startDay" for "numDays". It returns a cursor containing startday and
		 * endday representing the max range of days for all events beginning on
		 * each startday.This is a blocking function and should not be done on
		 * the UI thread.
		 *
		 * @param cr
		 *            the ContentResolver
		 * @param startDay
		 *            the first Julian day in the range
		 * @param numDays
		 *            the number of days to load (must be at least 1)
		 * @param projection
		 *            the columns to return in the cursor
		 * @return a database cursor containing a list of start and end days for
		 *         events
		 */
		public static final Cursor query(ContentResolver cr, int startDay, int numDays,
				String[] projection) {
			if (numDays < 1) {
				return null;
			}
			int endDay = startDay + numDays - 1;
			Uri.Builder builder = CONTENT_URI.buildUpon();
			ContentUris.appendId(builder, startDay);
			ContentUris.appendId(builder, endDay);
			return cr.query(builder.build(), projection, SELECTION, null /*
																		 * selection
																		 * args
																		 */, STARTDAY);
		}
	}

	protected interface RemindersColumns {
		/**
		 * The event the reminder belongs to. Column name.
		 * <P>
		 * Type: INTEGER (foreign key to the Events table)
		 * </P>
		 */
		public static final String EVENT_ID = "event_id";

		/**
		 * The minutes prior to the event that the alarm should ring. -1
		 * specifies that we should use the default value for the system. Column
		 * name.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String MINUTES = "minutes";

		/**
		 * Passing this as a minutes value will use the default reminder
		 * minutes.
		 */
		public static final int MINUTES_DEFAULT = -1;

		/**
		 * The alarm method, as set on the server. {@link #METHOD_DEFAULT},
		 * {@link #METHOD_ALERT}, {@link #METHOD_EMAIL}, and {@link #METHOD_SMS}
		 * are possible values; the device will only process
		 * {@link #METHOD_DEFAULT} and {@link #METHOD_ALERT} reminders (the
		 * other types are simply stored so we can send the same reminder info
		 * back to the server when we make changes).
		 */
		public static final String METHOD = "method";

		public static final int METHOD_DEFAULT = 0;
		public static final int METHOD_ALERT = 1;
		public static final int METHOD_EMAIL = 2;
		public static final int METHOD_SMS = 3;
	}

	/**
	 * Fields and helpers for accessing reminders for an event. Each row of this
	 * table represents a single reminder for an event. Calling
	 * {@link #query(ContentResolver, long, String[])} will return a list of
	 * reminders for the event with the given eventId. Both apps and sync
	 * adapters may write to this table. There are three writable fields and all
	 * of them must be included when inserting a new reminder. They are:
	 * <ul>
	 * <li>{@link #EVENT_ID}</li>
	 * <li>{@link #MINUTES}</li>
	 * <li>{@link #METHOD}</li>
	 * </ul>
	 */
	public static final class Reminders
			implements
				BaseColumns,
				RemindersColumns,
				EventsColumnsIceCream,
				EventsColumnsFroyo {
		private static final String REMINDERS_WHERE = CustonCalendarContract.Reminders.EVENT_ID
				+ "=?";
		@SuppressWarnings("hiding")
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/reminders");

		/**
		 * This utility class cannot be instantiated
		 */
		private Reminders() {
		}

		/**
		 * Queries all reminders associated with the given event. This is a
		 * blocking call and should not be done on the UI thread.
		 *
		 * @param cr
		 *            The content resolver to use for the query
		 * @param eventId
		 *            The id of the event to retrieve reminders for
		 * @param projection
		 *            the columns to return in the cursor
		 * @return A Cursor containing all reminders for the event
		 */
		public static final Cursor query(ContentResolver cr, long eventId, String[] projection) {
			String[] remArgs = { Long.toString(eventId) };
			return cr.query(CONTENT_URI, projection, REMINDERS_WHERE, remArgs /*
																			 * selection
																			 * args
																			 */, null /*
																					 * sort
																					 * order
																					 */);
		}
	}

	protected interface CalendarAlertsColumns {
		/**
		 * The event that the alert belongs to. Column name.
		 * <P>
		 * Type: INTEGER (foreign key to the Events table)
		 * </P>
		 */
		public static final String EVENT_ID = "event_id";

		/**
		 * The start time of the event, in UTC. Column name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String BEGIN = "begin";

		/**
		 * The end time of the event, in UTC. Column name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String END = "end";

		/**
		 * The alarm time of the event, in UTC. Column name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String ALARM_TIME = "alarmTime";

		/**
		 * The creation time of this database entry, in UTC. Useful for
		 * debugging missed reminders. Column name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String CREATION_TIME = "creationTime";

		/**
		 * The time that the alarm broadcast was received by the Calendar app,
		 * in UTC. Useful for debugging missed reminders. Column name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String RECEIVED_TIME = "receivedTime";

		/**
		 * The time that the notification was created by the Calendar app, in
		 * UTC. Useful for debugging missed reminders. Column name.
		 * <P>
		 * Type: INTEGER (long; millis since epoch)
		 * </P>
		 */
		public static final String NOTIFY_TIME = "notifyTime";

		/**
		 * The state of this alert. It starts out as {@link #STATE_SCHEDULED},
		 * then when the alarm goes off, it changes to {@link #STATE_FIRED}, and
		 * then when the user dismisses the alarm it changes to
		 * {@link #STATE_DISMISSED}. Column name.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String STATE = "state";

		/**
		 * An alert begins in this state when it is first created.
		 */
		public static final int STATE_SCHEDULED = 0;
		/**
		 * After a notification for an alert has been created it should be
		 * updated to fired.
		 */
		public static final int STATE_FIRED = 1;
		/**
		 * Once the user has dismissed the notification the alert's state should
		 * be set to dismissed so it is not fired again.
		 */
		public static final int STATE_DISMISSED = 2;

		/**
		 * The number of minutes that this alarm precedes the start time. Column
		 * name.
		 * <P>
		 * Type: INTEGER
		 * </P>
		 */
		public static final String MINUTES = "minutes";

		/**
		 * The default sort order for this alerts queries
		 */
		public static final String DEFAULT_SORT_ORDER = "begin ASC,title ASC";
	}

	/**
	 * Fields and helpers for accessing calendar alerts information. These
	 * fields are for tracking which alerts have been fired. Scheduled alarms
	 * will generate an intent using {@link #ACTION_EVENT_REMINDER}. Apps that
	 * receive this action may update the {@link #STATE} for the reminder when
	 * they have finished handling it. Apps that have their notifications
	 * disabled should not modify the table to ensure that they do not conflict
	 * with another app that is generating a notification. In general, apps
	 * should not need to write to this table directly except to update the
	 * state of a reminder.
	 */
	public static final class CalendarAlerts
			implements
				BaseColumns,
				CalendarAlertsColumns,
				EventsColumnsIceCream,
				EventsColumnsFroyo,
				CalendarColumnsIceCream,
				CalendarColumnsFroyo {

		/**
		 * @hide
		 */
		public static final String TABLE_NAME = "CalendarAlerts";
		/**
		 * The Uri for querying calendar alert information
		 */
		@SuppressWarnings("hiding")
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/calendar_alerts");

		/**
		 * This utility class cannot be instantiated
		 */
		private CalendarAlerts() {
		}

		private static final String WHERE_ALARM_EXISTS = EVENT_ID + "=?" + " AND " + BEGIN + "=?"
				+ " AND " + ALARM_TIME + "=?";

		private static final String WHERE_FINDNEXTALARMTIME = ALARM_TIME + ">=?";
		private static final String SORT_ORDER_ALARMTIME_ASC = ALARM_TIME + " ASC";

		private static final String WHERE_RESCHEDULE_MISSED_ALARMS = STATE + "=" + STATE_SCHEDULED
				+ " AND " + ALARM_TIME + "<?" + " AND " + ALARM_TIME + ">?" + " AND " + END + ">=?";

		/**
		 * This URI is for grouping the query results by event_id and begin
		 * time. This will return one result per instance of an event. So events
		 * with multiple alarms will appear just once, but multiple instances of
		 * a repeating event will show up multiple times.
		 */
		public static final Uri CONTENT_URI_BY_INSTANCE = Uri.parse("content://" + AUTHORITY
				+ "/calendar_alerts/by_instance");

		private static final boolean DEBUG = false;

		/**
		 * Helper for inserting an alarm time associated with an event TODO move
		 * to Provider
		 *
		 * @hide
		 */
		public static final Uri insert(ContentResolver cr, long eventId, long begin, long end,
				long alarmTime, int minutes) {
			ContentValues values = new ContentValues();
			values.put(CalendarAlerts.EVENT_ID, eventId);
			values.put(CalendarAlerts.BEGIN, begin);
			values.put(CalendarAlerts.END, end);
			values.put(CalendarAlerts.ALARM_TIME, alarmTime);
			long currentTime = System.currentTimeMillis();
			values.put(CalendarAlerts.CREATION_TIME, currentTime);
			values.put(CalendarAlerts.RECEIVED_TIME, 0);
			values.put(CalendarAlerts.NOTIFY_TIME, 0);
			values.put(CalendarAlerts.STATE, STATE_SCHEDULED);
			values.put(CalendarAlerts.MINUTES, minutes);
			return cr.insert(CONTENT_URI, values);
		}

		/**
		 * Finds the next alarm after (or equal to) the given time and returns
		 * the time of that alarm or -1 if no such alarm exists. This is a
		 * blocking call and should not be done on the UI thread. TODO move to
		 * provider
		 *
		 * @param cr
		 *            the ContentResolver
		 * @param millis
		 *            the time in UTC milliseconds
		 * @return the next alarm time greater than or equal to "millis", or -1
		 *         if no such alarm exists.
		 * @hide
		 */
		public static final long findNextAlarmTime(ContentResolver cr, long millis) {
			String selection = ALARM_TIME + ">=" + millis;
			// TODO: construct an explicit SQL query so that we can add
			// "LIMIT 1" to the end and get just one result.
			String[] projection = new String[] { ALARM_TIME };
			Cursor cursor = cr.query(CONTENT_URI, projection, WHERE_FINDNEXTALARMTIME,
					(new String[] { Long.toString(millis) }), SORT_ORDER_ALARMTIME_ASC);
			long alarmTime = -1;
			try {
				if (cursor != null && cursor.moveToFirst()) {
					alarmTime = cursor.getLong(0);
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			return alarmTime;
		}

		/**
		 * Searches the CalendarAlerts table for alarms that should have fired
		 * but have not and then reschedules them. This method can be called at
		 * boot time to restore alarms that may have been lost due to a phone
		 * reboot. TODO move to provider
		 *
		 * @param cr
		 *            the ContentResolver
		 * @param context
		 *            the Context
		 * @param manager
		 *            the AlarmManager
		 * @hide
		 */
		public static final void rescheduleMissedAlarms(ContentResolver cr, Context context,
				AlarmManager manager) {
			// Get all the alerts that have been scheduled but have not fired
			// and should have fired by now and are not too old.
			long now = System.currentTimeMillis();
			long ancient = now - DateUtils.DAY_IN_MILLIS;
			String[] projection = new String[] { ALARM_TIME, };

			// TODO: construct an explicit SQL query so that we can add
			// "GROUPBY" instead of doing a sort and de-dup
			Cursor cursor = cr
					.query(CalendarAlerts.CONTENT_URI,
							projection,
							WHERE_RESCHEDULE_MISSED_ALARMS,
							(new String[] { Long.toString(now), Long.toString(ancient),
									Long.toString(now) }), SORT_ORDER_ALARMTIME_ASC);
			if (cursor == null) {
				return;
			}

			if (DEBUG) {
				Log.d(TAG, "missed alarms found: " + cursor.getCount());
			}

			try {
				long alarmTime = -1;

				while (cursor.moveToNext()) {
					long newAlarmTime = cursor.getLong(0);
					if (alarmTime != newAlarmTime) {
						if (DEBUG) {
							Log.w(TAG, "rescheduling missed alarm. alarmTime: " + newAlarmTime);
						}
						scheduleAlarm(context, manager, newAlarmTime);
						alarmTime = newAlarmTime;
					}
				}
			} finally {
				cursor.close();
			}
		}

		/**
		 * Schedules an alarm intent with the system AlarmManager that will
		 * notify listeners when a reminder should be fired. The provider will
		 * keep scheduled reminders up to date but apps may use this to
		 * implement snooze functionality without modifying the reminders table.
		 * Scheduled alarms will generate an intent using
		 * {@link #ACTION_EVENT_REMINDER}. TODO Move to provider
		 *
		 * @param context
		 *            A context for referencing system resources
		 * @param manager
		 *            The AlarmManager to use or null
		 * @param alarmTime
		 *            The time to fire the intent in UTC millis since epoch
		 * @hide
		 */
		public static void scheduleAlarm(Context context, AlarmManager manager, long alarmTime) {
			if (DEBUG) {
				Time time = new Time();
				time.set(alarmTime);
				String schedTime = time.format(" %a, %b %d, %Y %I:%M%P");
				Log.d(TAG, "Schedule alarm at " + alarmTime + " " + schedTime);
			}

			if (manager == null) {
				manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			}

			Intent intent = new Intent(ACTION_EVENT_REMINDER);
			intent.setData(ContentUris
					.withAppendedId(CustonCalendarContract.CONTENT_URI, alarmTime));
			intent.putExtra(ALARM_TIME, alarmTime);
			PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
			manager.set(AlarmManager.RTC_WAKEUP, alarmTime, pi);
		}

		/**
		 * Searches for an entry in the CalendarAlerts table that matches the
		 * given event id, begin time and alarm time. If one is found then this
		 * alarm already exists and this method returns true. TODO Move to
		 * provider
		 *
		 * @param cr
		 *            the ContentResolver
		 * @param eventId
		 *            the event id to match
		 * @param begin
		 *            the start time of the event in UTC millis
		 * @param alarmTime
		 *            the alarm time of the event in UTC millis
		 * @return true if there is already an alarm for the given event with
		 *         the same start time and alarm time.
		 * @hide
		 */
		public static final boolean alarmExists(ContentResolver cr, long eventId, long begin,
				long alarmTime) {
			// TODO: construct an explicit SQL query so that we can add
			// "LIMIT 1" to the end and get just one result.
			String[] projection = new String[] { ALARM_TIME };
			Cursor cursor = cr
					.query(CONTENT_URI,
							projection,
							WHERE_ALARM_EXISTS,
							(new String[] { Long.toString(eventId), Long.toString(begin),
									Long.toString(alarmTime) }), null);
			boolean found = false;
			try {
				if (cursor != null && cursor.getCount() > 0) {
					found = true;
				}
			} finally {
				if (cursor != null) {
					cursor.close();
				}
			}
			return found;
		}
	}

	protected interface ExtendedPropertiesColumns {
		/**
		 * The event the extended property belongs to. Column name.
		 * <P>
		 * Type: INTEGER (foreign key to the Events table)
		 * </P>
		 */
		public static final String EVENT_ID = "event_id";

		/**
		 * The name of the extended property. This is a uri of the form
		 * {scheme}#{local-name} convention. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String NAME = "name";

		/**
		 * The value of the extended property. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String VALUE = "value";
	}

	/**
	 * Fields for accessing the Extended Properties. This is a generic set of
	 * name/value pairs for use by sync adapters or apps to add extra
	 * information to events. There are three writable columns and all three
	 * must be present when inserting a new value. They are:
	 * <ul>
	 * <li>{@link #EVENT_ID}</li>
	 * <li>{@link #NAME}</li>
	 * <li>{@link #VALUE}</li>
	 * </ul>
	 */
	public static final class ExtendedProperties
			implements
				BaseColumns,
				ExtendedPropertiesColumns,
				EventsColumnsIceCream,
				EventsColumnsFroyo {
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
				+ "/extendedproperties");

		/**
		 * This utility class cannot be instantiated
		 */
		private ExtendedProperties() {
		}

		// TODO: fill out this class when we actually start utilizing extendedproperties
		// in the calendar application.
	}

	/**
	 * A table provided for sync adapters to use for storing private sync state
	 * data.
	 *
	 * @see SyncStateContract
	 */
	public static final class SyncState implements SyncStateContract.Columns {
		/**
		 * This utility class cannot be instantiated
		 */
		private SyncState() {
		}

		private static final String CONTENT_DIRECTORY = SyncStateContract.Constants.CONTENT_DIRECTORY;

		/**
		 * The content:// style URI for this table
		 */
		public static final Uri CONTENT_URI = Uri.withAppendedPath(
				CustonCalendarContract.CONTENT_URI, CONTENT_DIRECTORY);
	}

	/**
	 * Columns from the EventsRawTimes table
	 *
	 * @hide
	 */
	protected interface EventsRawTimesColumns {
		/**
		 * The corresponding event id. Column name.
		 * <P>
		 * Type: INTEGER (long)
		 * </P>
		 */
		public static final String EVENT_ID = "event_id";

		/**
		 * The RFC2445 compliant time the event starts. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String DTSTART_2445 = "dtstart2445";

		/**
		 * The RFC2445 compliant time the event ends. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String DTEND_2445 = "dtend2445";

		/**
		 * The RFC2445 compliant original instance time of the recurring event
		 * for which this event is an exception. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String ORIGINAL_INSTANCE_TIME_2445 = "originalInstanceTime2445";

		/**
		 * The RFC2445 compliant last date this event repeats on, or NULL if it
		 * never ends. Column name.
		 * <P>
		 * Type: TEXT
		 * </P>
		 */
		public static final String LAST_DATE_2445 = "lastDate2445";
	}

	/**
	 * @hide
	 */
	public static final class EventsRawTimes implements BaseColumns, EventsRawTimesColumns {

		/**
		 * This utility class cannot be instantiated
		 */
		private EventsRawTimes() {
		}
	}

	/**
	 * Reads a String out of a column in a Cursor and writes it to a
	 * ContentValues. Adds nothing to the ContentValues if the column isn't
	 * present or if its value is null.
	 *
	 * @param cursor
	 *            The cursor to read from
	 * @param column
	 *            The column to read
	 * @param values
	 *            The {@link ContentValues} to put the value into
	 */
	public static void cursorStringToContentValuesIfPresent(Cursor cursor, ContentValues values,
			String column) {
		final int index = cursor.getColumnIndex(column);
		if (index != -1 && !cursor.isNull(index)) {
			values.put(column, cursor.getString(index));
		}
	}

	/**
	 * Reads a Long out of a column in a Cursor and writes it to a
	 * ContentValues. Adds nothing to the ContentValues if the column isn't
	 * present or if its value is null.
	 *
	 * @param cursor
	 *            The cursor to read from
	 * @param column
	 *            The column to read
	 * @param values
	 *            The {@link ContentValues} to put the value into
	 */
	public static void cursorLongToContentValuesIfPresent(Cursor cursor, ContentValues values,
			String column) {
		final int index = cursor.getColumnIndex(column);
		if (index != -1 && !cursor.isNull(index)) {
			values.put(column, cursor.getLong(index));
		}
	}

	/**
	 * Reads a Short out of a column in a Cursor and writes it to a
	 * ContentValues. Adds nothing to the ContentValues if the column isn't
	 * present or if its value is null.
	 *
	 * @param cursor
	 *            The cursor to read from
	 * @param column
	 *            The column to read
	 * @param values
	 *            The {@link ContentValues} to put the value into
	 */
	public static void cursorShortToContentValuesIfPresent(Cursor cursor, ContentValues values,
			String column) {
		final int index = cursor.getColumnIndex(column);
		if (index != -1 && !cursor.isNull(index)) {
			values.put(column, cursor.getShort(index));
		}
	}

	/**
	 * Reads a Integer out of a column in a Cursor and writes it to a
	 * ContentValues. Adds nothing to the ContentValues if the column isn't
	 * present or if its value is null.
	 *
	 * @param cursor
	 *            The cursor to read from
	 * @param column
	 *            The column to read
	 * @param values
	 *            The {@link ContentValues} to put the value into
	 */
	public static void cursorIntToContentValuesIfPresent(Cursor cursor, ContentValues values,
			String column) {
		final int index = cursor.getColumnIndex(column);
		if (index != -1 && !cursor.isNull(index)) {
			values.put(column, cursor.getInt(index));
		}
	}

	/**
	 * Reads a Float out of a column in a Cursor and writes it to a
	 * ContentValues. Adds nothing to the ContentValues if the column isn't
	 * present or if its value is null.
	 *
	 * @param cursor
	 *            The cursor to read from
	 * @param column
	 *            The column to read
	 * @param values
	 *            The {@link ContentValues} to put the value into
	 */
	public static void cursorFloatToContentValuesIfPresent(Cursor cursor, ContentValues values,
			String column) {
		final int index = cursor.getColumnIndex(column);
		if (index != -1 && !cursor.isNull(index)) {
			values.put(column, cursor.getFloat(index));
		}
	}

	/**
	 * Reads a Double out of a column in a Cursor and writes it to a
	 * ContentValues. Adds nothing to the ContentValues if the column isn't
	 * present or if its value is null.
	 *
	 * @param cursor
	 *            The cursor to read from
	 * @param column
	 *            The column to read
	 * @param values
	 *            The {@link ContentValues} to put the value into
	 */
	public static void cursorDoubleToContentValuesIfPresent(Cursor cursor, ContentValues values,
			String column) {
		final int index = cursor.getColumnIndex(column);
		if (index != -1 && !cursor.isNull(index)) {
			values.put(column, cursor.getDouble(index));
		}
	}
}