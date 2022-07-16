package com.example.fileanddboutput.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.fileanddboutput.model.FileAndDbOutputExample;

@Mapper
public interface FileAndDbOutputExampleMapper {

	List<FileAndDbOutputExample> selectByFlagIsFalse();

	int updateFlagToTrue(FileAndDbOutputExample model);
}
