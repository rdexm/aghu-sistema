package br.gov.mec.aghu.compras.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoCondicaoPagamentoProposDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoPrazoPagamentoDAO;
import br.gov.mec.aghu.compras.vo.PropostasVencedorasFornecedorVO;
import br.gov.mec.aghu.compras.vo.PropostasVencedorasVO;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class ConsultarPropostasVencedorasRN extends BaseBusiness {

	private static final long serialVersionUID = -7620204295459637684L;

	private static final Log LOG = LogFactory.getLog(ConsultarPropostasVencedorasRN.class);

	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;

	@Inject
	private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

	@Inject
	private ScoCondicaoPagamentoProposDAO scoCondicaoPagamentoProposDAO;

	@Inject
	private ScoPrazoPagamentoDAO scoPrazoPagamentoDAO;	
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public List<PropostasVencedorasVO> consultarPropostasVencedoras(Integer numeroLicitacao) {
		List<PropostasVencedorasVO> retornoList = new ArrayList<PropostasVencedorasVO>();
		
		// C2
		for (ScoItemLicitacao item : getScoItemLicitacaoDAO().listarItensLicitacao(numeroLicitacao)) {
			constroiRetorno(retornoList, item);
		}
		return retornoList;
	}

	private void constroiRetorno(List<PropostasVencedorasVO> retornoList, ScoItemLicitacao item) {
		PropostasVencedorasVO retorno;
		retorno = new PropostasVencedorasVO();
		retorno.setItem(item.getId().getNumero());
		avaliaMotivoCancelamento(retorno, item);
		retornoList.add(retorno);
	}

	private void avaliaMotivoCancelamento(PropostasVencedorasVO retorno, ScoItemLicitacao item) {
		if(item.getMotivoCancel() == null){
			buscaNumeroPropostas(retorno, item);
		}else{
			retorno.setMotivoCancelamento(item.getMotivoCancel());
		}
	}

	private void buscaNumeroPropostas(PropostasVencedorasVO retorno, ScoItemLicitacao item) {
		// C3
		List<ScoItemPropostaFornecedor> numPropostas = getScoItemPropostaFornecedorDAO().consultarPropostasVencedoras(item.getId().getLctNumero(), 
																													  item.getId().getNumero());
		
		avaliaPropostas(retorno, item, numPropostas);
	}

	private void avaliaPropostas(PropostasVencedorasVO retorno,	ScoItemLicitacao item, List<ScoItemPropostaFornecedor> numPropostas) {
		if(numPropostas.size() == 0){
			retorno.setFornecedor("FALTA ESCOLHER VENCEDOR");
		}else if(numPropostas.size() > 1){
			retorno.setFornecedor("ERRO: MAIS DE UM FORNECEDOR ESCOLHIDO");
		}else if(numPropostas.size() == 1){
			avaliaUnicaProposta(retorno, item, numPropostas);
		}
	}

	private void avaliaUnicaProposta(PropostasVencedorasVO retorno,	ScoItemLicitacao item, List<ScoItemPropostaFornecedor> numPropostas) {
		// C4
		List<PropostasVencedorasFornecedorVO> itemFornecedorList = getScoItemPropostaFornecedorDAO().buscarItemFornecedor(numPropostas.get(0), item.getLicitacao().getNumero());
		if(itemFornecedorList.size() == 0){
			retorno.setFornecedor("ERRO: FORNECEDOR NÂO ENCONTRADO");
		}else if(itemFornecedorList.size() > 0){
			avaliaListaFornecedoresValida(retorno, itemFornecedorList);
		}
	}

	private void avaliaListaFornecedoresValida(PropostasVencedorasVO retorno, List<PropostasVencedorasFornecedorVO> itemFornecedorList) {
		retorno.setFornecedor(itemFornecedorList.get(0).getRazaoSocial());
		// C5
		retorno.setCondicao(getScoCondicaoPagamentoProposDAO().buscaCondicoesPagamento(itemFornecedorList.get(0).getCdpNumero()));
		// C6
		retorno.setParcela(getScoPrazoPagamentoDAO().buscaParcela(itemFornecedorList.get(0).getCdpNumero()));
	}
	
	private ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}
	
	private ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}

	private ScoCondicaoPagamentoProposDAO getScoCondicaoPagamentoProposDAO() {
		return scoCondicaoPagamentoProposDAO;
	}
	
	private ScoPrazoPagamentoDAO getScoPrazoPagamentoDAO() {
		return scoPrazoPagamentoDAO;
	}
	
}
