package com.meta_force.meta_force.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.meta_force.meta_force.utils.NotificationHelper

class WaterReminderWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        // En un entorno de producción, aquí podrías comprobar 
        // si el usuario ha registrado haber bebido agua hoy en la BD (Room/Supabase)
        // y enviar la notificación condicionalmente.
        
        NotificationHelper.showNotification(
            context = applicationContext,
            title = "Recordatorio de Hidratación \uD83D\uDCA7",
            content = "¡Mantente hidratado! Es momento de beber un vaso de agua.",
            notificationId = 1001
        )
        
        return Result.success()
    }
}
