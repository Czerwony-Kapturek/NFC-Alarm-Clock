package com.nfcalarmclock.statistics;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;
import java.util.List;

/**
 * Data access object for storing when alarms were created.
 */
@Dao
public interface NacAlarmCreatedStatisticDao
	extends NacAlarmStatisticDao<NacAlarmCreatedStatistic>
{

	/**
	 * Delete all rows from the table.
	 */
	@Query("DELETE FROM alarm_created_statistic")
	int deleteAll();

	/**
	 * Get all instances when alarms were created.
	 *
	 * @return All instances when alarms were created.
	 */
	@Query("SELECT * FROM alarm_created_statistic")
	LiveData<List<NacAlarmCreatedStatistic>> getAll();

	/**
	 * Count the number of created alarm statistics.
	 *
	 * @return The number of created alarm statistics.
	 */
	@Query("SELECT COUNT(id) FROM alarm_created_statistic")
	long getCount();

}
