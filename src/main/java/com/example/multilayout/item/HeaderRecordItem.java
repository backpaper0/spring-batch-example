package com.example.multilayout.item;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class HeaderRecordItem extends RecordBaseItem {

	@DateTimeFormat(pattern = "uuuuMMdd")
	private LocalDate date;
	private String filler;
}
