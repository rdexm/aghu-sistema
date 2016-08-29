package br.gov.mec.aghu.compras.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoRefCodes;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class VisualizaAFsGeradasNaoEfetivadasON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(VisualizaAFsGeradasNaoEfetivadasON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IComprasFacade comprasFacade;

@EJB
private IParametroFacade parametroFacade;

@Inject
private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -362638021162654277L;

	public enum VisualizaAFsGeradasONExceptionCode implements BusinessExceptionCode {
		VAGNE_ERRO_CAMPO_DEPENDENTE_NUM_COMPL, VAGNE_ERRO_CAMPO_DEPENDENTE_NUM_LICITACAO
	}
	
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	public List<ScoAutorizacaoForn> visualizarAFsGeradasNaoEfetivadas(Integer numeroLicitacao, Short nroComplemento, Date dtGeracao, Date dtPrevEntrega, 
			DominioModalidadeEmpenho modalidadeEmpenho, ScoRefCodes situacao, Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc) throws ApplicationBusinessException{
		
		List<DominioSituacaoAutorizacaoFornecimento> rvLowValues = recuperarLowValues();
		DominioSituacaoAutorizacaoFornecimento indSituacao = null;
		if(situacao != null){
			indSituacao = DominioSituacaoAutorizacaoFornecimento.valueOf(situacao.getRvLowValue());
		}
		
		return getScoAutorizacaoFornDAO().visualizarAFsGeradasNaoEfetivadas(numeroLicitacao, nroComplemento, dtGeracao, dtPrevEntrega, modalidadeEmpenho, 
				indSituacao, rvLowValues, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long visualizarAFsGeradasNaoEfetivadasCount(Integer numeroLicitacao, Short nroComplemento, Date dtGeracao, Date dtPrevEntrega, DominioModalidadeEmpenho modalidadeEmpenho, ScoRefCodes situacao) throws ApplicationBusinessException{
		List<DominioSituacaoAutorizacaoFornecimento> rvLowValues = recuperarLowValues();
		DominioSituacaoAutorizacaoFornecimento indSituacao = null;
		if(situacao != null){
			indSituacao = DominioSituacaoAutorizacaoFornecimento.valueOf(situacao.getRvLowValue());
		}
		
		return getScoAutorizacaoFornDAO().visualizarAFsGeradasNaoEfetivadasCount(numeroLicitacao, nroComplemento, dtGeracao, dtPrevEntrega, modalidadeEmpenho, indSituacao, rvLowValues);
	}

	public List<ScoRefCodes> buscarScoRefCodesPorSituacao(String consultaCampo) throws ApplicationBusinessException{
		List<String> situacoes = recuperarSituacaoRefCodes();
		
		// Efetua a busca, se a consulta for o rvLowValue exato a consulta retorna somente esse resultado se não efetua a busca pelo rvMeaning
		List<ScoRefCodes> scoRefCodesList = getComprasFacade().buscarScoRefCodesPorSituacao(consultaCampo, null, situacoes);
		if (scoRefCodesList != null && scoRefCodesList.size() == 0) {
			scoRefCodesList = getComprasFacade().buscarScoRefCodesPorSituacao(null, consultaCampo, situacoes);
		}
		
		return scoRefCodesList;
	}
	
	private List<DominioSituacaoAutorizacaoFornecimento> recuperarLowValues() throws ApplicationBusinessException{
		AghParametros parametros = null;
		List<DominioSituacaoAutorizacaoFornecimento> situacoes = new ArrayList<DominioSituacaoAutorizacaoFornecimento>();
		parametros = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VISUALIZAR_AFS_RV_LOW_VALUES_AFSGERADASNAOEFETIVADAS);
		String[] rvLowValues = parametros.getVlrTexto().split(",");
		for (String string : rvLowValues) {
			DominioSituacaoAutorizacaoFornecimento dom = DominioSituacaoAutorizacaoFornecimento.valueOf(string);
			situacoes.add(dom);				
		}		
		return situacoes;
	}
	
	private List<String> recuperarSituacaoRefCodes() throws ApplicationBusinessException{
		AghParametros parametros = null;
		List<String> situacoes = new ArrayList<String>();
		parametros = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_VISUALIZAR_AFS_RV_LOW_VALUES_REFCODES);
		String[] rvLowValues = parametros.getVlrTexto().split(",");
		for (String string : rvLowValues) {
			String rvLowValue = string;
			situacoes.add(rvLowValue);				
		}
	
		return situacoes;
	}

	public IParametroFacade getParametroFacade()  {
		return parametroFacade;
	}
	
	/**
	 * Verifica se os campos obrigatórios para pesquisa foram preenchidos
	 * Caso seja digitado algum valor para um campo o outro torna-se obrigatório
	 * Campos: Núm. A.F. e Núm. Compl.
	 *
	 */
	public void validaPreenchimentoCampos(Integer numeroLicitacao, Short nroComplemento) throws ApplicationBusinessException {
		if (nroComplemento != null || numeroLicitacao != null){
			if(nroComplemento == null){
				throw new ApplicationBusinessException(VisualizaAFsGeradasONExceptionCode.VAGNE_ERRO_CAMPO_DEPENDENTE_NUM_COMPL);
			}
			if(numeroLicitacao == null){
				throw new ApplicationBusinessException(VisualizaAFsGeradasONExceptionCode.VAGNE_ERRO_CAMPO_DEPENDENTE_NUM_LICITACAO);
			}
		}
	}
	
	protected ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}
	
}