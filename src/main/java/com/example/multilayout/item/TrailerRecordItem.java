package com.example.multilayout.item;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class TrailerRecordItem extends RecordBaseItem {

	private int size;
	private int total;
}
