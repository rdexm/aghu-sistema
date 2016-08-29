package br.gov.mec.aghu.internacao.pesquisa.business;

import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.internacao.dao.AinExtratoLeitosDAO;
import br.gov.mec.aghu.model.AinExtratoLeitos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;

@Stateless
public class DetalhaLeitoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(DetalhaLeitoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinExtratoLeitosDAO ainExtratoLeitosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7452014778667246379L;

	/**
	 * 
	 * @dbtables AinExtratoLeitos select
	 * 
	 * @param leito
	 * @return
	 */
	@Secure("#{s:hasPermission('leito','pesquisarExtrato')}")
	public AinExtratoLeitos obterUltimoExtratoLeito(String leito) {
		List<AinExtratoLeitos> list = getAinExtratoLeitosDAO().pesquisarExtratoLeitos(leito, null, 0, 1,
				AinExtratoLeitos.Fields.CRIADO_EM.toString(), false);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}

	protected AinExtratoLeitosDAO getAinExtratoLeitosDAO() {
		return ainExtratoLeitosDAO;
	}
}
