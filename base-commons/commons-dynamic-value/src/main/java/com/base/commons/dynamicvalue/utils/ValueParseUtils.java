package com.base.commons.dynamicvalue.utils;

import com.alibaba.cloud.nacos.parser.NacosDataParserHandler;
import java.io.IOException;
import java.util.Map;

public class ValueParseUtils {

    private static final NacosDataParserHandler DATA_PARSER_HANDLER = NacosDataParserHandler.getInstance();

    public static Map<String, Object> parseFileData(String file, String data) throws IOException {
        return parseData(data, parseExtension(file));
    }

    public static Map<String, Object> parseData(String data, String extension) throws IOException {
        return DATA_PARSER_HANDLER.parseNacosData(data, extension);
    }

    public static String parseExtension(String file) {
        int splitAt = file.indexOf(".");

        if (splitAt < 0) {
            return "properties";
        }

        return file.substring(splitAt + 1);
    }
}
