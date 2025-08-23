package co.com.crediya.app;

import java.util.LinkedList;

import static co.com.crediya.utilities.StringUtils.join;
import static co.com.crediya.utilities.StringUtils.split;
import static co.com.crediya.app.MessageUtils.getMessage;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.WordUtils;

@Slf4j
public class App {
    public static void main(String[] args) {
        LinkedList tokens = split(getMessage());
        String result = join(tokens);
        log.info(WordUtils.capitalize(result));
    }
}
