package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ExamesAnatomopatologicosPOLRN extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ExamesAnatomopatologicosPOLRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IParametroFacade parametroFacade;

@EJB
private IExamesFacade examesFacade;


	private static final long serialVersionUID = -6379111650420484914L;

	public enum ExamesAnatomopatologicosPOLRNExceptionCode implements	BusinessExceptionCode {
		AEL_01202
	}
	
	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public Map<Integer, Vector<Short>> processarResultadosNotasAdicionaisPorCirurgia(
			Integer seqMbcCirurgia, Integer ordemImpressao, Boolean prntolAdmin) throws ApplicationBusinessException {
		AghParametros pSituacaoLiberado = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros pSituacaoExecutado = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_SITUACAO_EXECUTADO);
		
		List<AelItemSolicitacaoExames> itensSolExames = getExamesFacade()
				.buscarItemSolicitacaoExamesPorCirurgia(seqMbcCirurgia,
						pSituacaoLiberado.getVlrTexto(),pSituacaoExecutado.getVlrTexto());
		
		if(itensSolExames == null){
			throw new ApplicationBusinessException(ExamesAnatomopatologicosPOLRNExceptionCode.AEL_01202);
		}
		
		// Loop C_ISE
		Map<Integer, Vector<Short>> solicitacoes = new HashMap<Integer, Vector<Short>>();
		for (AelItemSolicitacaoExames item : itensSolExames){
			solicitacoes.put(item.getId().getSoeSeq(), new Vector<Short>());
			solicitacoes.get(item.getId().getSoeSeq()).add(item.getId().getSeqp());
		}
		return solicitacoes;
	}

}