package com.wissen.services;

import org.springframework.core.io.Resource;

public abstract interface AbstractService<T>
{

	public abstract void service(Resource resource, Resource weeklyQuestions) throws Throwable;
}
