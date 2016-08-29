package br.gov.mec.aghu.util.migrador;

import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
@SuppressWarnings("PMD")
public class AjustadorJSFComponents {

	public void ajustarSetPropertyActionListener(
			List<Element> setPropertyActionListenerList) {
		for (Element setPropertyActionListener : setPropertyActionListenerList) {

			Attribute forAttribute = setPropertyActionListener.attribute("for");
			if (forAttribute == null) {
				setPropertyActionListener.addAttribute("for", "command");
			}

		}

	}

	public void ajustarValidateLongRange(List<Element> validateLongRangeList) {
		for (Element validateLongRangeElement : validateLongRangeList) {
			validateLongRangeElement.addAttribute("for", "inputId");
			
		}
		
	}

}
