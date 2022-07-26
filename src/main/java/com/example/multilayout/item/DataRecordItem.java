package com.example.multilayout.item;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class DataRecordItem extends RecordBaseItem {

	private String id;
	private String name;
	private int number;
}
