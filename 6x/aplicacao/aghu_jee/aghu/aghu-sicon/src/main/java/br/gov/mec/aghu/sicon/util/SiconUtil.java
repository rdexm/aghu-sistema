package br.gov.mec.aghu.sicon.util;

import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.ScoContrato;

public class SiconUtil {
	
	private static final Log LOG = LogFactory.getLog(SiconUtil.class);

	public static final String removeCaracteresCNPJCPF(String cnpj) {

		cnpj = StringUtils.remove(cnpj, ".");
		cnpj = StringUtils.remove(cnpj, "/");
		cnpj = StringUtils.remove(cnpj, "-");

		return cnpj;
	}

	public static final XMLGregorianCalendar obtemAnoXGC(Date data) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(data);

		try {
			XMLGregorianCalendar date2 = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(c);

			date2.setTimezone(DatatypeConstants.FIELD_UNDEFINED);
			date2.setTime(DatatypeConstants.FIELD_UNDEFINED,
					DatatypeConstants.FIELD_UNDEFINED,
					DatatypeConstants.FIELD_UNDEFINED);
			date2.setMonth(DatatypeConstants.FIELD_UNDEFINED);
			date2.setDay(DatatypeConstants.FIELD_UNDEFINED);

			return date2;
		} catch (DatatypeConfigurationException e1) {
			LOG.error(e1.getMessage());
		}

		return null;
	}

	public static final Integer obtemAnoString(Date data) {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(data);

		try {
			XMLGregorianCalendar date2 = DatatypeFactory.newInstance()
					.newXMLGregorianCalendar(c);
			return date2.getYear();
		} catch (DatatypeConfigurationException e1) {
			LOG.error(e1.getMessage());
		}

		return null;
	}

	public static final String obtemAcao(ScoContrato contrato) {
		String INCLUSAO = "inclusão";
		String ALTERACAO = "alteração";

		String auxRetorno = null;
		switch (contrato.getSituacao()) {
		case A:
			auxRetorno = INCLUSAO;
			break;
		case E:
			return ALTERACAO;
		case AR:
			auxRetorno = ALTERACAO;
			break;
		case EE:
			if (contrato.getCodInternoUasg() != null) {
				auxRetorno = ALTERACAO;
			} else {
				auxRetorno = INCLUSAO;
			}
			break;
		}
		
		return auxRetorno;
	}
	
	public static String retiraEspacosConsecutivos(
			String descricao) {

		boolean existeEspacoSobrando = true;
		
		while(existeEspacoSobrando) {

			existeEspacoSobrando = false;
			
			for (int i = 0; i < descricao.length(); i++) {
				if (i + 2 < descricao.length()) {
					if (descricao.substring(i, i + 2).equals("  ")) {
						
						descricao = descricao.replaceAll("  ", " ");
						
						existeEspacoSobrando = true;
					}
				}
			}

		}

		return descricao;
	}
	
	
}