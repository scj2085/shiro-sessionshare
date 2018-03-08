package com.gome.meidian.account.shiroimagecode1;

import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.color.RandomRangeColorGenerator;
import com.octo.captcha.component.image.deformation.ImageDeformation;
import com.octo.captcha.component.image.deformation.ImageDeformationByFilters;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.textpaster.DecoratedRandomTextPaster;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.textdecorator.LineTextDecorator;
import com.octo.captcha.component.image.textpaster.textdecorator.TextDecorator;
import com.octo.captcha.component.image.wordtoimage.DeformedComposedWordToImage;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.image.gimpy.GimpyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.ImageFilter;

/**
 * 验证码的样式类
 */
public class CustomListImageCaptchaEngine extends ListImageCaptchaEngine {

    private static final Logger logger = LoggerFactory.getLogger(CustomListImageCaptchaEngine.class);

    @Override
    protected void buildInitialFactories() {
        logger.info("---------CSRCaptchaEngine buildInitialFactories2------");
        int minWordLength = 4;
        int maxWordLength = 4;
        int fontSize = 50;
        int imageWidth = 152;
        int imageHeight = 70;
        WordGenerator wordGenerator = new RandomWordGenerator("0123456789abcdefghijklmnopqrstuvwxyz");
        TextPaster randomPaster = new DecoratedRandomTextPaster(minWordLength, maxWordLength,
                new RandomRangeColorGenerator(new int[] { 0, 150 }, new int[] { 0, 150 }, new int[] { 0, 150 }),
                new TextDecorator[] { new LineTextDecorator(new Integer(1), Color.BLACK) });
				/*
				 * TextPaster randomPaster = new
				 * DecoratedRandomTextPaster(minWordLength, maxWordLength, new
				 * RandomListColorGenerator(new Color[] { new Color(23, 170,
				 * 27), new Color(220, 34, 11), new Color(23, 67, 172) }), new
				 * TextDecorator[] {new LineTextDecorator(new Integer(2),
				 * Color.BLACK)});
				 */

        BackgroundGenerator background = new UniColorBackgroundGenerator(imageWidth, imageHeight, Color.gray);

				/*
				 * BackgroundGenerator background = new
				 * FunkyBackgroundGenerator(new Integer( 260), new Integer(70));
				 */
        FontGenerator font = new RandomFontGenerator(fontSize, fontSize,
                new Font[] { new Font("nyala", Font.BOLD, fontSize), new Font("Bell MT", Font.PLAIN, fontSize),
                        new Font("Credit valley", Font.BOLD, fontSize) });

        ImageDeformation postDef = new ImageDeformationByFilters(new ImageFilter[] {});
        ImageDeformation backDef = new ImageDeformationByFilters(new ImageFilter[] {});
        ImageDeformation textDef = new ImageDeformationByFilters(new ImageFilter[] {});

        WordToImage word2image = new DeformedComposedWordToImage(font, background, randomPaster, backDef,
                textDef, postDef);
        addFactory(new GimpyFactory(wordGenerator, word2image));
    }
}
