package com.assignment.Verve;

import com.assignment.Verve.Exception.VerveServiceException;
import com.assignment.Verve.Service.Impl.VerveServiceImpl;
import com.assignment.Verve.Service.VerveMetricService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


@RunWith(MockitoJUnitRunner.class)
class VerveApplicationTests {

	@InjectMocks
	VerveServiceImpl restService;
	@Mock
	VerveMetricService requestMetricService;
	@Mock
	RestTemplate restTemplate;

	@Test
	public void testCallEndpointUrl() throws VerveServiceException {
		String time = Calendar.getInstance().toString();
		Mockito.when(requestMetricService.getRequestCountForId(time, "11")).thenReturn(100);
		ResponseEntity responseEntity = new ResponseEntity<String>(HttpStatus.OK);

		Mockito.when(restTemplate.getForEntity(Mockito.anyString(),Mockito.any(), Mockito.anyMap())).thenReturn(responseEntity);
		restService.callRequestEndPoint(
				"11","https://jsonplaceholder.typicode.com/todos/1", null);

		Map<String, Integer> params = new HashMap<>();
		params.put("count", 100);
		Mockito.verify(restTemplate).getForEntity(
				"https://jsonplaceholder.typicode.com/todos/1?countOfUniqueIds={count}", Object.class, params);
	}

}
