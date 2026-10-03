package com.grokfunnel.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.grokfunnel.data.local.dao.*
import com.grokfunnel.data.local.entities.*

@Database(
    entities = [
        LeadEntity::class,
        PipelineStageEntity::class,
        AgentEntity::class,
        EmailSequenceEntity::class,
        GigEntity::class,
        ActivityLogEntity::class,
        CrmConfigEntity::class,
        PaymentRecordEntity::class,
        TeamSettingsEntity::class,
        PaystackWebhookLogEntity::class,
        ViralVideoEntity::class,
        VideoProjectEntity::class,
        TrafficMonetizationEntity::class,
        MonetizationConversionLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun leadDao(): LeadDao
    abstract fun pipelineStageDao(): PipelineStageDao
    abstract fun agentDao(): AgentDao
    abstract fun emailSequenceDao(): EmailSequenceDao
    abstract fun gigDao(): GigDao
    abstract fun activityLogDao(): ActivityLogDao
    abstract fun crmDao(): CrmDao
    abstract fun paymentDao(): PaymentDao
    abstract fun teamSettingsDao(): TeamSettingsDao
    abstract fun paystackWebhookLogDao(): PaystackWebhookLogDao
    abstract fun viralVideoDao(): ViralVideoDao
    abstract fun videoProjectDao(): VideoProjectDao
    abstract fun trafficMonetizationDao(): TrafficMonetizationDao
    abstract fun monetizationConversionLogDao(): MonetizationConversionLogDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "grok_funnel_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
