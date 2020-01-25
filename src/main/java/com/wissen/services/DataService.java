package com.wissen.services;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.wissen.component.HTTPHeaderComponent;
import com.wissen.dto.LeaderboardModel;
import com.wissen.dto.UserModel;
import com.wissen.dto.UserRankModel;
import com.wissen.util.ExcelReaderUtil;
import com.wissen.util.ExcelWriterUtil;
import com.wissen.util.HackerRankException;

@Service
public class DataService implements AbstractService<Resource>
{

	@Autowired
	HTTPHeaderComponent httpHeaderComponent;

	private ExcelReaderUtil excelReader = new ExcelReaderUtil();

	private ExcelWriterUtil excelWriter = new ExcelWriterUtil();

	private static Logger LOG = LoggerFactory.getLogger(DataService.class);

	private boolean trackingForGrads = Boolean.FALSE;

	private LocalDate trackingStartDate = LocalDate.parse("2019-08-16");

	@Override
	public void service(Resource resource, Resource weeklyQuestions) throws HackerRankException
	{
		try
		{
			if (resource.getFilename().contains("grad"))
			{
				trackingForGrads = Boolean.TRUE;
			}
			List<UserModel> users = excelReader.getValidProfiles(resource.getInputStream());
			Set<String> reqdQuiestions = excelReader.requiredQuestions(weeklyQuestions.getInputStream());
			TreeSet<UserRankModel> rankList = getRankList(users, reqdQuiestions);

			excelWriter.writeToExcel(rankList, trackingForGrads, trackingStartDate);
		} catch (Exception e)
		{
			throw new HackerRankException(e);
		}
	}

	public TreeSet<UserRankModel> getRankList(List<UserModel> users, Set<String> reqdQuiestions) throws Exception
	{

		LOG.info("started Writing");

		ConcurrentHashMap<String, TreeSet<LeaderboardModel>> allQuesLeadBoard = new ConcurrentHashMap<String, TreeSet<LeaderboardModel>>();
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		List<Future<UserRankModel>> UserRankModelsFuture = new ArrayList<Future<UserRankModel>>();
		TreeSet<UserRankModel> UserRankModels = new TreeSet<UserRankModel>(new Comparator<UserRankModel>() {

			@Override
			public int compare(UserRankModel o1, UserRankModel o2)
			{
				Integer c1 = o1.getDateToCount().values().stream().mapToInt(i -> i.intValue()).sum();
				Integer c2 = o2.getDateToCount().values().stream().mapToInt(i -> i.intValue()).sum();
				if (c2.compareTo(c1) == 0)
				{
					return o1.getModel().getHacker().compareTo(o2.getModel().getHacker());
				}
				return c2.compareTo(c1);
			}
		});
		for (UserModel user : users)
		{
			HackerrankThread hackerrankThread = new HackerrankThread(allQuesLeadBoard, user,
					httpHeaderComponent.getHttpHeaders(trackingForGrads), trackingStartDate, reqdQuiestions);
			UserRankModelsFuture.add(executorService.submit(hackerrankThread));
		}
		for (Future<UserRankModel> future : UserRankModelsFuture)
		{
			UserRankModels.add(future.get());
		}

		return UserRankModels;
	}

}
