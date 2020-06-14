package com.wissen.services;

import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wissen.dto.LeaderboardDTO;
import com.wissen.dto.LeaderboardModel;
import com.wissen.dto.QuestionsDTO;
import com.wissen.dto.QuestionsModel;
import com.wissen.dto.UserModel;
import com.wissen.dto.UserRankModel;
import com.wissen.util.HackerRankException;
import com.wissen.util.LeadBoardUrlUtil;

public class HackerrankThread implements Callable<UserRankModel>
{
	private static Logger LOG = LoggerFactory.getLogger(HackerrankThread.class);

	private ConcurrentHashMap<String, TreeSet<LeaderboardModel>> allQuesLeadBoard;

	private UserModel user;

	private LeadBoardUrlUtil urlUtil = new LeadBoardUrlUtil();

	private HttpHeaders headers;

	private LocalDate trackingStartDate;
	private Set<String> reqdQuiestions;

	RestTemplate restTemplate = new RestTemplate();

	public HackerrankThread(ConcurrentHashMap<String, TreeSet<LeaderboardModel>> allQuesLeadBoard, UserModel user,
							HttpHeaders headers, LocalDate trackingStartDate, Set<String> reqdQuiestions)
	{
		this.allQuesLeadBoard = allQuesLeadBoard;
		this.user = user;
		this.headers = headers;
		this.trackingStartDate = trackingStartDate;
		this.reqdQuiestions = reqdQuiestions;
	}

	@Override
	public UserRankModel call() throws Exception
	{
		LOG.info("Starting calculation for user: {}", user.getHacker());
		UserRankModel model = new UserRankModel();
		model.setModel(user);
		model.setDateToCount(new HashMap<>());
		List<QuestionsModel> submissions = getAllQuestionforUser();
		hasSolvedReqdQuestions(model, submissions);
		for (QuestionsModel questionsModel : submissions)
		{
			String question = questionsModel.getCh_slug();
			if (allQuesLeadBoard.get(question) != null)
			{
				updateUserRankModel(model, question);
			} else
			{
				updateMap(question);
				updateUserRankModel(model, question);
			}
		}
		LOG.info("Completed calculation for user: {}", user.getHacker());
		return model;
	}

	private void hasSolvedReqdQuestions(UserRankModel model, List<QuestionsModel> submissions) {
		Set<String> solvedQuestions = new HashSet<>();
		for(QuestionsModel submission : submissions) {
			solvedQuestions.add(submission.getCh_slug());
		}
		Set<String> copyOfReqdQues = new HashSet<>(this.reqdQuiestions);
		copyOfReqdQues.removeAll(solvedQuestions);
		model.getModel().setUnSolvedReqdQuestions(copyOfReqdQues.size());
	}

	private void updateUserRankModel(UserRankModel model, String question)
	{
		LeaderboardModel leaderboardModel = new LeaderboardModel();
		leaderboardModel.setHacker(user.getProfile());
		if (allQuesLeadBoard.get(question).contains(leaderboardModel))
		{
			leaderboardModel = allQuesLeadBoard.get(question).tailSet(leaderboardModel).first();
			LocalDate date = Instant.ofEpochSecond(leaderboardModel.getTime_taken())
					.atZone(TimeZone.getDefault().toZoneId()).toLocalDate();
			if (date.compareTo(trackingStartDate) >= 0 && date.compareTo(LocalDate.now()) < 0
					&& leaderboardModel.getRank().equals("1"))
			{
				Map<LocalDate, Integer> dateToCount = model.getDateToCount();
				dateToCount.putIfAbsent(date, 0);
				dateToCount.put(date, dateToCount.get(date) + 1);
			}
		}
	}

	private void updateMap(String question)
	{
		String leaderBoardUrl = urlUtil.leadboardUrlFor(question);
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		ResponseEntity<LeaderboardDTO> respEntity = null;
		List<LeaderboardModel> friendsLeaderboard = new ArrayList<LeaderboardModel>();

		try
		{
			respEntity = restTemplate.exchange(leaderBoardUrl, HttpMethod.GET, entity, LeaderboardDTO.class);
			friendsLeaderboard = respEntity.getBody().getModels();

		} catch (RestClientException e)
		{
			LOG.info(e.getMessage());
		}

		allQuesLeadBoard.putIfAbsent(question, new TreeSet<>(friendsLeaderboard));

	}

	private List<QuestionsModel> getAllQuestionforUser() throws HackerRankException
	{
		String url = urlUtil.questionsUrlFor(user.getProfile());

		try
		{
			HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

			ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, entity, String.class);

			ObjectMapper mapper = new ObjectMapper();
			QuestionsDTO resp = null;

			resp = mapper.readValue(response.getBody(), new TypeReference<QuestionsDTO>() {
			});
			return resp.getModels();
		} catch (Exception e)
		{
			LOG.error(e.getMessage(), e);
			throw new HackerRankException(e);
		}
	}

}
