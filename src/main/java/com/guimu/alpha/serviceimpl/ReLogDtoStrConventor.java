package com.guimu.alpha.serviceimpl;

import com.guimu.alpha.dto.ReLogDto;
import java.util.StringJoiner;
import org.springframework.util.StringUtils;

/**
 * @Description: ReLogDto和Str 转换器
 * @Author: Guimu
 * @Create: 2019/02/18 17:42:40
 **/

public interface ReLogDtoStrConventor {

    default ReLogDto toReLogFromStr(String source) {
        String[] valArr = source.split("-");
        ReLogDto reLogDto = new ReLogDto();
        reLogDto.setUserId(valArr[0]);
        reLogDto.setBatchNo(valArr[1]);
        reLogDto.setLevelStr(valArr[2]);
        return reLogDto;
    }

    default String toStrFromReLogDto(ReLogDto reLogDto) {
        StringJoiner stringJoiner = new StringJoiner("-", "", "");
        stringJoiner.add(reLogDto.getUserId());
        stringJoiner.add(reLogDto.getBatchNo());
        stringJoiner.add(reLogDto.getLevelStr());
        return stringJoiner.toString();

    }

    default boolean checkIsEmpty(ReLogDto reLogDto) {
        return null == reLogDto || StringUtils.isEmpty(reLogDto.getLevelStr()) || StringUtils
            .isEmpty(reLogDto.getBatchNo()) || StringUtils
            .isEmpty(reLogDto.getUserId());
    }
}
