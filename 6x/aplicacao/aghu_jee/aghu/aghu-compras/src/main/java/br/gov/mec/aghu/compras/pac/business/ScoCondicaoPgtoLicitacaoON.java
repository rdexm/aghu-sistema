package br.gov.mec.aghu.compras.pac.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoCondicaoPgtoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.ScoCondicaoPgtoLicitacao;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.suprimentos.vo.ScoCondicaoPgtoLicitacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ScoCondicaoPgtoLicitacaoON extends BaseBusiness {


@EJB
private ScoCondicaoPgtoLicitacaoRN scoCondicaoPgtoLicitacaoRN;

@EJB
private ScoCondicaoPgtoLicitacaoValidacaoON scoCondicaoPgtoLicitacaoValidacaoON;

	private static final Log LOG = LogFactory.getLog(ScoCondicaoPgtoLicitacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private ScoCondicaoPgtoLicitacaoDAO scoCondicaoPgtoLicitacaoDAO;
	
	@Inject
	private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1258530808240796905L;

 
	public List<ScoCondicaoPgtoLicitacaoVO> obterCondicaoPgtoPac(
			Integer numeroLicitacao, Short numeroItem, Integer first,
			Integer max, String order, boolean asc) {

		List<ScoCondicaoPgtoLicitacao> listaCondicaoPgtoLicitacao = getScoCondicaoPgtoLicitacaoDAO()
				.obterCondicaoPgtoPac(numeroLicitacao, numeroItem, first, max,
						order, asc);

		List<ScoCondicaoPgtoLicitacaoVO> listaCondicaoPgtoLicitacaoVO = new ArrayList<ScoCondicaoPgtoLicitacaoVO>();

		for (ScoCondicaoPgtoLicitacao condicaoPgtoLicitacao : listaCondicaoPgtoLicitacao) {
			ScoCondicaoPgtoLicitacaoVO condicaoPgtoLicitacaoVO = new ScoCondicaoPgtoLicitacaoVO();

			Long numParcelas = getScoParcelasPagamentoDAO()
					.obterParcelasPagamentoCount(condicaoPgtoLicitacao.getSeq());
			condicaoPgtoLicitacaoVO.setNumParcelas(numParcelas.intValue());

			condicaoPgtoLicitacaoVO
					.setCondicaoPgtoLicitacao(condicaoPgtoLicitacao);

			condicaoPgtoLicitacaoVO.setIndExclusao(false);

			listaCondicaoPgtoLicitacaoVO.add(condicaoPgtoLicitacaoVO);
		}

		return listaCondicaoPgtoLicitacaoVO;
	}

	public void gravarCondicaoPagtoParcelas(
			ScoCondicaoPgtoLicitacao condicaoPagamento,
			List<ScoParcelasPagamento> listaParcelas,
			List<ScoParcelasPagamento> listaParcelasExcluidas)
			throws ApplicationBusinessException {

		getScoCondicaoPgtoLicitacaoValidacaoON().regrasNegocioParcelasDaCondicaoPagamento(listaParcelas, condicaoPagamento);
		
		// Remove da base de dados as parcelas que devem ser excluidas
		for (ScoParcelasPagamento parcela : listaParcelasExcluidas) {
			if (parcela.getSeq() != null) {
				Integer seqParcela = parcela.getSeq();
				getScoParcelasPagamentoDAO().remover(getScoParcelasPagamentoDAO().obterPorChavePrimaria(seqParcela));			
			}
		}

		getScoCondicaoPgtoLicitacaoRN().persistir(condicaoPagamento);

		// Set da condição de pagamento em cada parcela e grava desta
		for (ScoParcelasPagamento parcela : listaParcelas) {
			if (parcela.getSeq() == null) {
				parcela.setCondicaoPgtoLicitacao(condicaoPagamento);
				getScoParcelasPagamentoDAO().persistir(parcela);
			} else {
				getScoParcelasPagamentoDAO().atualizar(parcela);
			}
		}
	}

	public void excluirCondicaoPgto(Integer seqCondicaoPgto){		
		List<ScoParcelasPagamento> listaParcelas = getScoParcelasPagamentoDAO().obterParcelasPagamento(seqCondicaoPgto);
		
		for (ScoParcelasPagamento parcela : listaParcelas){
			getScoParcelasPagamentoDAO().remover(parcela);
		}

		getScoCondicaoPgtoLicitacaoDAO().remover(getScoCondicaoPgtoLicitacaoDAO().obterPorChavePrimaria(seqCondicaoPgto));		
	}
	
	public boolean verificarParamUnicaCondicaoPgto(){
		AghParametros parametro;
		try {
			parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ACEITA_UNICA_COND_PGTO);
		} catch (ApplicationBusinessException e) {
			return true;
		}

		if (parametro != null){				
			String parametroUnicaCondicaoPgto = parametro.getVlrTexto();
			if (parametroUnicaCondicaoPgto.equalsIgnoreCase("S")){
				return true;				
			} else {
				return false;
			}
		}
		
		return true;
	}
	
	public boolean permitirNovaCondicaoPgto(Integer numeroLicitacao, Short numeroItem){
		boolean unicaCondicao = this.verificarParamUnicaCondicaoPgto();

		if (!unicaCondicao){
			return true;
		} else {
			// Caso permitir somente uma condição de pagamento deve-se verificar 
			// existência de condição já cadastrada
			Long numCondicoes = this.obterCondicaoPgtoPacCount(numeroLicitacao, numeroItem);
			if (numCondicoes != null && numCondicoes > 0){
				return false;
			} else {
				return true;
			}
		}
	}
		
	public Long obterCondicaoPgtoPacCount(Integer numeroLicitacao,
			Short numeroItem) {
		return getScoCondicaoPgtoLicitacaoDAO().obterCondicaoPgtoPacCount(
				numeroLicitacao, numeroItem);
	}

	private ScoCondicaoPgtoLicitacaoDAO getScoCondicaoPgtoLicitacaoDAO() {
		return scoCondicaoPgtoLicitacaoDAO;
	}

	private ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO() {
		return scoParcelasPagamentoDAO;
	}
	
	private ScoCondicaoPgtoLicitacaoValidacaoON getScoCondicaoPgtoLicitacaoValidacaoON(){
		return scoCondicaoPgtoLicitacaoValidacaoON;
	}

	protected ScoCondicaoPgtoLicitacaoRN getScoCondicaoPgtoLicitacaoRN() {
		return scoCondicaoPgtoLicitacaoRN;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

			
}
