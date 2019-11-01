package com.mypro.task;

import com.mypro.pojo.JobInfo;
import com.mypro.util.MathSalary;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Component
public class JobProcess implements PageProcessor {


    @Autowired
    private JobPipeline jobPipeline;


    @Scheduled(initialDelay = 1000,fixedDelay = 1000*100)
    public void process(){
        String url = "https://search.51job.com/list/000000,000000,0000,01%252C32,9,99,java,2,1.html?lang=c&stype=&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&providesalary=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line=&specialarea=00";
        Spider.create(new JobProcess()).addUrl(url)
                .setScheduler(new QueueScheduler().setDuplicateRemover(new BloomFilterDuplicateRemover(10000000)))
                .addPipeline(jobPipeline).thread(5).run();
    }

    @Override
    public void process(Page page) {
        List<Selectable> nodes = page.getHtml().$("div#resultList div.el").nodes();
        if(nodes.isEmpty()){
            try {
                this.saveJobInfo(page);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            for (Selectable node : nodes) {
                String jobUrl = node.links().toString();
                page.addTargetRequest(jobUrl);
                List<String> listUrl = page.getHtml().$("div.p_in li.bk").links().all();
                page.addTargetRequests(listUrl);
            }
        }
    }

    private Site site = Site.me();
    @Override
    public Site getSite() {
        return site;
    }

    public void saveJobInfo(Page page){

        JobInfo jobInfo = new JobInfo();
        Html html = page.getHtml();
        //公司名称
        jobInfo.setCompanyName(Jsoup.parse(html.css("p.cname > a").toString()).text());
        //公司地址
        jobInfo.setCompanyAddr(Jsoup.parse(html.css("div.bmsg").nodes().get(1).css("p").toString()).text());
        //公司信息
        jobInfo.setCompanyInfo(Jsoup.parse(html.css("div.tmsg").toString()).text());
        //职位名称
        jobInfo.setJobName(Jsoup.parse(html.css("div.cn > h1").toString()).text());
        //工作地点
        jobInfo.setJobAddr(Jsoup.parse(html.css("div.bmsg").nodes().get(1).css("p").toString()).text());
        //职位信息
        jobInfo.setJobInfo(Jsoup.parse(html.css("div.job_msg").toString()).text());
        //工资范围
        String salaryStr = Jsoup.parse(html.css("div.cn > strong").toString()).text();
        Integer[] salary = MathSalary.getSalary(salaryStr);
        jobInfo.setSalaryMin(salary[0]);
        jobInfo.setSalaryMax(salary[1]);
        //职位详情url
        jobInfo.setUrl(page.getUrl().toString());
        //职位发布时间
        String timeStr = Jsoup.parse(html.css("p.ltype").toString()).text();
        int index = timeStr.indexOf("发布");
        String time = "";
        if(index != -1){
            time = timeStr.substring(index-5,index);
        }
        jobInfo.setTime(time);

        page.putField("jobInfo",jobInfo);
    }
}
