package com.maheshgaya.android.coolwallpapers.sync;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

/**
 * Created by Mahesh Gaya on 2/20/17.
 */

public class PostJobService extends JobService {
    @Override
    public boolean onStartJob(JobParameters job) {
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return false;
    }
}
