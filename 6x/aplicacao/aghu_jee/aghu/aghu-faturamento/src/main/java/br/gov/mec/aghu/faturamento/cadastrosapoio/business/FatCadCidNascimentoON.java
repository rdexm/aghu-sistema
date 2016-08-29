package br.gov.mec.aghu.faturamento.cadastrosapoio.business;

import java.io.Serializable;

import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class FatCadCidNascimentoON extends BaseBusiness implements Serializable {

	private static final long serialVersionUID = 743885605333667741L;

	private static final Log LOG = LogFactory.getLog(FatCadCidNascimentoON.class);
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
}
