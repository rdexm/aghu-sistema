package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoCondicaoPagamentoProposDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoParcelasPagamentoDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoParcelasPagamento;
import br.gov.mec.aghu.suprimentos.vo.ScoCondicaoPagamentoProposVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ScoCondicaoPagamentoProposRN extends BaseBusiness{

@EJB
private ScoParcelasPagamentoRN scoParcelasPagamentoRN;

private static final Log LOG = LogFactory.getLog(ScoCondicaoPagamentoProposRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoCondicaoPagamentoProposDAO scoCondicaoPagamentoProposDAO;

@EJB
private IPacFacade pacFacade;

@Inject
private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

@Inject
private ScoParcelasPagamentoDAO scoParcelasPagamentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8067774112360772224L;

	public List<ScoCondicaoPagamentoProposVO> obterCondicaoPagamentoPropos(
			Integer nroFornecedor, Integer nroLicitacao, Short numeroItem,
			Integer first, Integer max, String order, boolean asc) {
		List<ScoCondicaoPagamentoPropos> listaCondicaoPagamentoPropos = getScoCondicaoPagamentoProposDAO()
				.obterCondicaoPagamentoPropos(nroFornecedor, nroLicitacao,
						numeroItem, first, max, order, asc);
		
		List<ScoCondicaoPagamentoProposVO> listaCondicaoPagamentoProposVO = new ArrayList<ScoCondicaoPagamentoProposVO>();

		for(ScoCondicaoPagamentoPropos condicaoPagamentoPropos : listaCondicaoPagamentoPropos){
			ScoCondicaoPagamentoProposVO vo = new  ScoCondicaoPagamentoProposVO();
			vo.setCondicaoPgtoProposta(condicaoPagamentoPropos);
			vo.setIndEscolhida(condicaoPagamentoPropos.getIndCondEscolhida());
			
			Long numParcelas = getScoParcelasPagamentoDAO().obterParcelasPgtoPropostaCount(condicaoPagamentoPropos.getNumero());
			vo.setNumParcelas(numParcelas.intValue());
			
			listaCondicaoPagamentoProposVO.add(vo);
		}
		
		return listaCondicaoPagamentoProposVO;
	}
	
	public void inserir(ScoCondicaoPagamentoPropos condicao, List<ScoParcelasPagamento> parcelas) 
			throws BaseException {
		validarSomaPercentuais(parcelas);
		validarSomaValores(condicao, parcelas);
		getScoCondicaoPagamentoProposDAO().persistir(condicao);
		
		for (ScoParcelasPagamento parcela : parcelas) {
			getScoParcelasPagamentoRN().persistir(parcela);
		}
	}

	public void atualizar(ScoCondicaoPagamentoPropos condicao, 
			List<ScoParcelasPagamento> parcelas, List<ScoParcelasPagamento> parcelasExcluidas) 
			throws BaseException {
		
		validarSomaPercentuais(parcelas);
		validarSomaValores(condicao, parcelas);
		scoCondicaoPagamentoProposDAO.merge(condicao);
		
		for (ScoParcelasPagamento parcela : parcelas) {
			getScoParcelasPagamentoRN().persistir(parcela);
		}
		
		for (ScoParcelasPagamento parcela : parcelasExcluidas) {
			getScoParcelasPagamentoRN().remover(parcela);
		}
	}
		
	public boolean permitirNovaCondicaoPgtoProposta(Integer frnNumero, Integer numeroLicitacao, Short numeroItem){
		boolean unicaCondicao = getPacFacade().verificarParamUnicaCondicaoPgto();

		if (!unicaCondicao){
			return true;
		} else {
			// Caso permitir somente uma condição de pagamento deve-se verificar 
			// existência de condição já cadastrada
			Long numCondicoes = getScoCondicaoPagamentoProposDAO().obterCondicaoPgtoProposCount(frnNumero, numeroLicitacao, numeroItem);
			if (numCondicoes != null && numCondicoes > 0){
				return false;
			} else {
				return true;
			}
		}
	}
	
	
	
	/**
	 * Valida RN01.
	 * 
	 * A soma dos valores percentuais das parcelas deverá ser 100 ou zero
	 * (quando não informado para nenhuma parcela).
	 * 
	 * @param condicao Condição
	 * @throws ApplicationBusinessException 
	 */
	private void validarSomaPercentuais(List<ScoParcelasPagamento> parcelas) 
			throws ApplicationBusinessException {
		BigDecimal sum = BigDecimal.ZERO;
		
		for (ScoParcelasPagamento parcela : parcelas) {
			if (parcela.getPercPagamento() != null) {
				sum = sum.add(parcela.getPercPagamento());
			} else if (!sum.equals(BigDecimal.ZERO)) {
				throw new ApplicationBusinessException(
						ScoCondicaoPagamentoProposBusinessExceptionCode
							.MESSAGE_VALOR_PERCENTUAL_INFORMADO_TODAS_PARCELAS);
			}
		}
		
		if (sum.equals(BigDecimal.ZERO)) {
			return;
		}
		
		sum = sum.setScale(2, RoundingMode.HALF_UP);
		BigDecimal hundred = BigDecimal.valueOf(100.00).setScale(2, RoundingMode.HALF_UP);

		if (!hundred.equals(sum)) {
			throw new ApplicationBusinessException(
					ScoCondicaoPagamentoProposBusinessExceptionCode
						.MESSAGE_SOMA_VALORES_PERCENTUAIS_DEVE_SER_100);
		}
	}

	/**
	 * Valida RN02.
	 * 
	 * A soma dos valores da parcela deverá ser igual ao campo Valor Total,
	 * indicado abaixo da grade.
	 * 
	 * @param condicao Condição
	 */
	private void validarSomaValores(ScoCondicaoPagamentoPropos condicao, 
			List<ScoParcelasPagamento> parcelas) 
			throws ApplicationBusinessException {
		BigDecimal sum = BigDecimal.ZERO;
		
		for (ScoParcelasPagamento parcela : parcelas) {
			if (parcela.getValorPagamento() != null) {
				sum = sum.add(parcela.getValorPagamento());
			} else if (!sum.equals(BigDecimal.ZERO)) {
				throw new ApplicationBusinessException(
						ScoCondicaoPagamentoProposBusinessExceptionCode
							.MESSAGE_VALOR_INFORMADO_TODAS_PARCELAS);
			}
		}
		
		if (sum.equals(BigDecimal.ZERO)) {
			return;
		}
		
		sum = sum.setScale(2, RoundingMode.HALF_UP);
		BigDecimal total = null;
		
		if (condicao.getPropostaFornecedor() != null) {
			total = getPacFacade()
					.obterValorTotalProposta(condicao.getPropostaFornecedor());
		} else if (condicao.getItemPropostaFornecedor() != null) {
			total = getPacFacade()
					.obterValorTotalItemProposta(condicao.getItemPropostaFornecedor());
		}
		
		total = total.setScale(2, RoundingMode.HALF_UP);
		
		if (!sum.equals(total)) {
			if (condicao.getPropostaFornecedor() != null) {
				throw new ApplicationBusinessException(
						ScoCondicaoPagamentoProposBusinessExceptionCode
							.MESSAGE_SOMA_VALORES_PERCENTUAIS_DEVE_SER_TOTAL_PROPOSTA);
			} else if (condicao.getItemPropostaFornecedor() != null) {
				throw new ApplicationBusinessException(
						ScoCondicaoPagamentoProposBusinessExceptionCode
							.MESSAGE_SOMA_VALORES_PERCENTUAIS_DEVE_SER_TOTAL_ITENS_PROPOSTA);
			}
		}
	}

	/**
	 * Exclui condição de pagamento de proposta não julgada.
	 * 
	 * @param id ID da condição.
	 * @throws ApplicationBusinessException
	 */
	public void excluir(Integer id) throws ApplicationBusinessException {
		if (getScoItemPropostaFornecedorDAO().existeItemAssociadoACondicao(id)) {
			throw new ApplicationBusinessException(
					ScoCondicaoPagamentoProposBusinessExceptionCode
						.MESSAGE_EXCLUSAO_COND_PGTO_PROPOS_NAO_PERMITIDA);
		} else {
			ScoCondicaoPagamentoPropos condicao = 
					getScoCondicaoPagamentoProposDAO().obterPorChavePrimaria(id);
			
			getScoCondicaoPagamentoProposDAO().remover(condicao);
		}
	}
	
	protected ScoCondicaoPagamentoProposDAO getScoCondicaoPagamentoProposDAO() {
		return scoCondicaoPagamentoProposDAO;
	}
	
	protected ScoParcelasPagamentoDAO getScoParcelasPagamentoDAO() {
		return scoParcelasPagamentoDAO;
	}
	
	protected ScoParcelasPagamentoRN getScoParcelasPagamentoRN() {
		return scoParcelasPagamentoRN;
	}
	
	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	protected IPacFacade getPacFacade() {
		return pacFacade;
	}
		
	public enum ScoCondicaoPagamentoProposBusinessExceptionCode
			implements BusinessExceptionCode {
		MESSAGE_EXCLUSAO_COND_PGTO_PROPOS_NAO_PERMITIDA, 
		MESSAGE_ALTERACAO_COND_PGTO_PROPOS_NAO_PERMITIDA, 
		MESSAGE_SOMA_VALORES_PERCENTUAIS_DEVE_SER_100,
		MESSAGE_SOMA_VALORES_PERCENTUAIS_DEVE_SER_TOTAL_PROPOSTA,
		MESSAGE_SOMA_VALORES_PERCENTUAIS_DEVE_SER_TOTAL_ITENS_PROPOSTA,
		MESSAGE_VALOR_PERCENTUAL_INFORMADO_TODAS_PARCELAS,
		MESSAGE_VALOR_INFORMADO_TODAS_PARCELAS
	}
}
