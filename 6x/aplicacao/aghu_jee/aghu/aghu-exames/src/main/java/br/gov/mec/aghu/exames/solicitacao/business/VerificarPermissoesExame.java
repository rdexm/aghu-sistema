package br.gov.mec.aghu.exames.solicitacao.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class VerificarPermissoesExame extends DesenhaDataHoraISE {

	private static final Log LOG = LogFactory.getLog(VerificarPermissoesExame.class);

	@Inject
	private VerificarPermissaoHorariosRotina sucessorImpl;
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Override
	public TipoCampoDataHoraISE processRequest(ItemSolicitacaoExameVO itemSolicitacaoExameVO) throws ApplicationBusinessException {
		TipoCampoDataHoraISE campo;
		
//		Se o checkbox URGENTE estiver marcado, sempre será um calendário ou
//		Se a combo Situação estiver com qualquer valor selecionado, menos A COLETAR será um calendário.		
		if (itemSolicitacaoExameVO.getUrgente() == Boolean.TRUE
				|| (itemSolicitacaoExameVO.getSituacaoCodigo() != null 
						&& !itemSolicitacaoExameVO.getSituacaoCodigo().getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AC.toString()))){
			campo = TipoCampoDataHoraISE.CALENDAR;
		} else {
			campo = sucessorImpl.processRequest(itemSolicitacaoExameVO);
		}
		
		return campo;
	}

}
