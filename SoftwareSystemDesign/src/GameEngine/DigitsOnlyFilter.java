package GameEngine;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

/**
 * 숫자로만 이루어진 long 범위의 0 이상 베팅 입력을 허용하는 Swing 문서 필터이다.
 */
public class DigitsOnlyFilter extends DocumentFilter {
    /** 베팅 입력 검증 필터를 생성한다. */
    public DigitsOnlyFilter() { }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void insertString(FilterBypass fb, int offset, String text, AttributeSet attributes) throws BadLocationException {
		if(text != null) {
			replace(fb, offset, 0, text, attributes);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attributes) throws BadLocationException{
		String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
		String replacementText = text == null ? "" : text;
		String result = currentText.substring(0, offset) + replacementText + currentText.substring(offset + length);
		if(isValid(result)) {
			fb.replace(offset, length, replacementText, attributes);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void remove(FilterBypass fb, int offset, int length) throws BadLocationException{
		fb.remove(offset, length);
	}
	private boolean isValid(String text) {
		if(text.isEmpty()) {
			return true;
		}
		if(!text.matches("\\d+") || text.length() > 19) {
			return false;
		}
		try {
			Long.parseLong(text);
			return true;
		}
		catch(NumberFormatException e) {
			return false;
		}
	}
}
