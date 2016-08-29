package br.gov.mec.aghu.compras.pac.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.vo.ItensPACVO;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class RelatorioItensPACON extends BaseBusiness implements Serializable{

private static final Log LOG = LogFactory.getLog(RelatorioItensPACON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoLicitacaoDAO scoLicitacaoDAO;

@Inject
private ScoItemLicitacaoDAO scoItemLicitacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4216766104835407921L;

	public enum RelatorioQuantidadePrescricoesDispensacaoONExceptionCode implements	BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA
	}	

	/**
	 * Obtem dados para o Relatório Itens da Licitacao
	 * 
	 * @param numero
	 * @return listaVOs
	 * @throws ApplicationBusinessException
	 */
	public List<ItensPACVO> pesquisarRelatorioItensPAC(Integer numero,boolean flagNaoExcluidas) 
	throws ApplicationBusinessException {


		List<ItensPACVO> listaItensLicitacao =  getScoItemLicitacaoDAO().pesquisarRelatorioItensLicitacao(numero, DominioTipoFaseSolicitacao.C,flagNaoExcluidas);

		List<ItensPACVO> listaItensLicitacaoUnion =  getScoItemLicitacaoDAO().pesquisarRelatorioItensLicitacaoUnion(numero, DominioTipoFaseSolicitacao.S,flagNaoExcluidas);


		if( (listaItensLicitacao==null || listaItensLicitacao.isEmpty()) && (listaItensLicitacaoUnion==null || listaItensLicitacaoUnion.isEmpty()) ){
			throw new ApplicationBusinessException(RelatorioQuantidadePrescricoesDispensacaoONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}

		Set<ItensPACVO> listaItens = new HashSet<ItensPACVO>();

		listaItens.addAll(listaItensLicitacao);

		listaItens.addAll(listaItensLicitacaoUnion);


		ScoLicitacao licitacao = getScoLicitacaoDAO().obterPorChavePrimaria(numero);


		for (ItensPACVO itemVO: listaItens){

			processaItemLicitacaoVO(licitacao, itemVO);

		}			
		
		
		List<ItensPACVO> listaVOs = new ArrayList<ItensPACVO>();	
		
		listaVOs.addAll(listaItens);
		
		CoreUtil.ordenarLista(listaVOs, "numeroItem", true);

		return listaVOs;

	}
	
	
	/**
	 * Popula VO para o Relatório Itens da Licitacao
	 * @param itemVO 
	 * 
	 * @param numero
	 * @return listaVOs
	 * @throws ApplicationBusinessException
	 */
	private void processaItemLicitacaoVO(ScoLicitacao licitacao, ItensPACVO itemVO) throws ApplicationBusinessException {		
		
		//Seta a data da digitacao da Licitacao
		itemVO.setDtDigitacao(DateUtil.dataToString(licitacao.getDtDigitacao(), "dd/MM/yyyy"));
		
		//Seta a data da limite do aceite da proposta
		itemVO.setDtLimiteAceiteProposta(DateUtil.dataToString(licitacao.getDtLimiteAceiteProposta(), "dd/MM/yyyy"));
		
		//Formata o campo descricao da Licitacao e da Modalidade da Licitacao
		itemVO.setDescricaoLicitacaoModalidade(licitacao.getDescricao() + "-" + licitacao.getModalidadeLicitacao().getDescricao());
		
		//Seta a data/hora da abertura da proposta			
		itemVO.setDthrAberturaProposta(DateUtil.dataToString(licitacao.getDthrAberturaProposta(), "dd/MM/yyyy HH:mm"));

	}
	

	private ScoItemLicitacaoDAO getScoItemLicitacaoDAO(){
		return scoItemLicitacaoDAO;
	}
	
	private ScoLicitacaoDAO getScoLicitacaoDAO(){
		return scoLicitacaoDAO;
	}

	
}
