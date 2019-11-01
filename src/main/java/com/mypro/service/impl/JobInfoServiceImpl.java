package com.mypro.service.impl;

import com.mypro.dao.JobInfoDao;
import com.mypro.pojo.JobInfo;
import com.mypro.service.JobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class JobInfoServiceImpl implements JobInfoService {

    @Autowired
    private JobInfoDao jobInfoDao;

    @Override
    @Transactional
    public void save(JobInfo jobInfo) {
        JobInfo param = new JobInfo();
        param.setUrl(jobInfo.getUrl());
        param.setTime(jobInfo.getTime());
        List<JobInfo> list = findJobInfo(param);
        if(list.size() ==0){
            jobInfoDao.save(jobInfo);
        }
    }

    @Override
    public List<JobInfo> findJobInfo(JobInfo jobInfo) {

        Example example = Example.of(jobInfo);
        List<JobInfo> list = jobInfoDao.findAll(example);
        return list;
    }
}
