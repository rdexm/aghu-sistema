package br.gov.mec.aghu.internacao.pesquisa.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioGrupoConvenioPesquisaLeitos;
import br.gov.mec.aghu.dominio.DominioMovimentoLeito;
import br.gov.mec.aghu.internacao.dao.AinAtendimentosUrgenciaDAO;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Regras de negócio do Caso de Uso Pesquisa Leitos
 * 
 * @author lalegre
 * 
 */
@Stateless
public class PesquisaLeitosRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisaLeitosRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AinAtendimentosUrgenciaDAO ainAtendimentosUrgenciaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7155557145716317628L;

	/**
	 * Verifica qual data block deve ser executado ORADB PROCEDURE
	 * EVT_KEY_EXEQRY 0 - VPL 1 - VPL1 2 - STP
	 * 
	 * @return
	 */
	public Integer verificarDataBlock(FatConvenioSaude convenio,
			DominioGrupoConvenioPesquisaLeitos grupoConvenio,
			DominioMovimentoLeito mvtoLeito) {

		if (mvtoLeito == DominioMovimentoLeito.L) {
			return 2;
		} else if (convenio != null
				|| (grupoConvenio != null
						&& StringUtils.isNotBlank(grupoConvenio.getDescricao()) || mvtoLeito == DominioMovimentoLeito.O)) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * Verifica se o leito possui At. Urgencia ORADB PROCEDURE AINP_VERIF_ATU
	 * 
	 * @param leitoId
	 * @return
	 */
	public boolean possuiAtUrgencia(String leitoId) {
		Integer seq = getAinAtendimentosUrgenciaDAO().obterSeqAtUrgencia(leitoId);
		if (seq != null) {
			return true;
		}
		return false;
	}

	protected AinAtendimentosUrgenciaDAO getAinAtendimentosUrgenciaDAO() {
		return ainAtendimentosUrgenciaDAO;
	}

}
