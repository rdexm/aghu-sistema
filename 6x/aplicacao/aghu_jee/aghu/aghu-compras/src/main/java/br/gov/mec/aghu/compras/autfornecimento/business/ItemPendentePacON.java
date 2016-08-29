package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaItensPendentesPacVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.PesquisaItensPendentesPacVO.SituacaoItem;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ItemPendentePacON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ItemPendentePacON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoItemLicitacaoDAO scoItemLicitacaoDAO;

	private static final long serialVersionUID = -101869066447890001L;
	
	public enum ItemPendentePacONExceptionCode implements BusinessExceptionCode {
		;
	}

	/**
	 * Pesquisa os itens de solicitação pendentes PAC.
	 * 
	 * @param numeroLicitacao
	 * @return
	 */
	public List<PesquisaItensPendentesPacVO> pesquisarItemLicitacaoPorNumeroLicitacao(final Integer numeroLicitacao) {
		final List<PesquisaItensPendentesPacVO> retorno = new ArrayList<PesquisaItensPendentesPacVO>();
		retorno.addAll(this.setSituacao(this.getScoItemLicitacaoDAO().pesquisarItemLicitacaoExcluidoPorNumeroLicitacao(numeroLicitacao),
				PesquisaItensPendentesPacVO.SituacaoItem.EXCLUIDO));
		retorno.addAll(this.setSituacao(this.getScoItemLicitacaoDAO().pesquisarItemLicitacaoSemPropostaPorNumeroLicitacao(numeroLicitacao),
				PesquisaItensPendentesPacVO.SituacaoItem.SEM_PROPOSTA));
		retorno.addAll(this.setSituacao(this.getScoItemLicitacaoDAO().pesquisarItemLicitacaoSemEscolhaPorNumeroLicitacao(numeroLicitacao),
				PesquisaItensPendentesPacVO.SituacaoItem.NAO_JULGADO));
		retorno.addAll(this.setSituacao(this.getScoItemLicitacaoDAO().pesquisarItemLicitacaoPendenteAFPorNumeroLicitacao(numeroLicitacao),
				PesquisaItensPendentesPacVO.SituacaoItem.SEM_AF));
		
		for (PesquisaItensPendentesPacVO pesquisaItensPendentesPacVO : retorno) {
			if(pesquisaItensPendentesPacVO.getSolicitacaoDeCompra() != null){
				pesquisaItensPendentesPacVO.getSolicitacaoDeCompra().getNumero();				
				if(pesquisaItensPendentesPacVO.getSolicitacaoDeCompra().getMaterial()!=null){
					pesquisaItensPendentesPacVO.getSolicitacaoDeCompra().getMaterial().getNome();
				}
				if(pesquisaItensPendentesPacVO.getSolicitacaoDeCompra().getServidorExclusao()!=null){
					pesquisaItensPendentesPacVO.getSolicitacaoDeCompra().getServidorExclusao().getPessoaFisica().getNome();
				}
				if(pesquisaItensPendentesPacVO.getSolicitacaoDeCompra().getVerbaGestao()!=null){
					pesquisaItensPendentesPacVO.getSolicitacaoDeCompra().getVerbaGestao().getDescricao();
				}
				if(pesquisaItensPendentesPacVO.getSolicitacaoDeCompra().getNaturezaDespesa()!=null){
					pesquisaItensPendentesPacVO.getSolicitacaoDeCompra().getNaturezaDespesa().getDescricao();
					if(pesquisaItensPendentesPacVO.getSolicitacaoDeCompra().getNaturezaDespesa().getGrupoNaturezaDespesa()!=null){
						pesquisaItensPendentesPacVO.getSolicitacaoDeCompra().getNaturezaDespesa().getGrupoNaturezaDespesa().getDescricao();
					}
				}
			}
			if(pesquisaItensPendentesPacVO.getSolicitacaoServico() != null){
				pesquisaItensPendentesPacVO.getSolicitacaoServico().getNumero();
				if(pesquisaItensPendentesPacVO.getSolicitacaoServico().getServico() != null){
					pesquisaItensPendentesPacVO.getSolicitacaoServico().getServico().getDescricao();
				}
				if(pesquisaItensPendentesPacVO.getSolicitacaoServico().getServidorExcluidor() != null){
					pesquisaItensPendentesPacVO.getSolicitacaoServico().getServidorExcluidor().getPessoaFisica().getNome();
				}
				if(pesquisaItensPendentesPacVO.getSolicitacaoServico().getVerbaGestao()!=null){
					pesquisaItensPendentesPacVO.getSolicitacaoServico().getVerbaGestao().getDescricao();
				}			
				if(pesquisaItensPendentesPacVO.getSolicitacaoServico().getNaturezaDespesa() != null){
					pesquisaItensPendentesPacVO.getSolicitacaoServico().getNaturezaDespesa().getDescricao();
					if(pesquisaItensPendentesPacVO.getSolicitacaoServico().getNaturezaDespesa().getGrupoNaturezaDespesa() !=null){
						pesquisaItensPendentesPacVO.getSolicitacaoServico().getNaturezaDespesa().getGrupoNaturezaDespesa().getDescricao();
					}
				}
				
			}
		}
		Collections.sort(retorno);
		return retorno;
	}
	
	private List<PesquisaItensPendentesPacVO> setSituacao(final List<PesquisaItensPendentesPacVO> pesquisarItemLicitacaoExcluidoPorNumeroLicitacao,
			final SituacaoItem situacao) {
		for (PesquisaItensPendentesPacVO pesquisaItensPendentesPacVO : pesquisarItemLicitacaoExcluidoPorNumeroLicitacao) {
			pesquisaItensPendentesPacVO.setSituacao(situacao);
		}
		return pesquisarItemLicitacaoExcluidoPorNumeroLicitacao;
	}

	protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}

}
