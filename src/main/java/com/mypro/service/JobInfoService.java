package com.mypro.service;

import com.mypro.pojo.JobInfo;

import java.util.List;

public interface JobInfoService {

    public void save(JobInfo jobInfo);

    public List<JobInfo> findJobInfo(JobInfo jobInfo);
}
