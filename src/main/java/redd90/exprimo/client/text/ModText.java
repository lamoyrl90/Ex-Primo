package redd90.exprimo.client.text;

import javax.annotation.Nonnull;

import net.minecraft.util.text.Color;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.Style;

public class ModText {
	public static IFormattableTextComponent withColor(IFormattableTextComponent textIn, @Nonnull int colorIn) {
		Style style = textIn.getStyle();
		style.setColor(Color.func_240743_a_(colorIn));
		textIn.mergeStyle(style);
		return textIn;
	}
}
