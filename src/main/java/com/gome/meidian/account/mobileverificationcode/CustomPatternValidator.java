package com.gome.meidian.account.mobileverificationcode;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Pattern;
import java.util.regex.Matcher;
import java.util.regex.PatternSyntaxException;

/**
 * Created by dinggang on 2016/10/9.
 */
public class CustomPatternValidator implements ConstraintValidator<CustomPattern, CharSequence> {
    private static final Log log = LoggerFactory.make();

    private java.util.regex.Pattern pattern;

    @Override
    public void initialize(CustomPattern parameters) {
        Pattern.Flag[] flags = parameters.flags();
        int intFlag = 0;
        for (Pattern.Flag flag : flags) {
            intFlag = intFlag | flag.getValue();
        }

        try {
            pattern = java.util.regex.Pattern.compile(parameters.regexp(), intFlag);
        } catch (PatternSyntaxException e) {
            throw log.getInvalidRegularExpressionException(e);
        }
    }

    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null) {
            return true;
        }

        value = new String(Base64.decodeBase64(value.toString()));
        Matcher m = pattern.matcher(value);
        return m.matches();
    }
}
