package br.gov.mec.aghu.compras.pac.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.dao.ScoArquivoAnexoDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoItemPropostaFornecedorDAO;
import br.gov.mec.aghu.compras.dao.ScoUnidadeMedidaDAO;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacao;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.suprimentos.vo.ScoItemPacVO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class DadosItemLicitacaoON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(DadosItemLicitacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private ScoUnidadeMedidaDAO scoUnidadeMedidaDAO;

@Inject
private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;

@Inject
private ScoItemLicitacaoDAO scoItemLicitacaoDAO;

@Inject
private ScoItemPropostaFornecedorDAO scoItemPropostaFornecedorDAO;

@EJB
private IAutFornecimentoFacade autFornecimentoFacade;

@EJB
private IComprasFacade comprasFacade;

@EJB
private ISolicitacaoServicoFacade solicitacaoServicoFacade;

@Inject
private ScoArquivoAnexoDAO scoArquivoAnexoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4149853215470373163L;
	
	/**
	 * Retorna a unidade do material do item da licitacao (PAC)
	 * @param item
	 * @return String
	 */
	public String obterUnidadeMaterial(ScoItemLicitacao item) {
		String unidadeMaterial = null;
			
        List<ScoFaseSolicitacao> listaFases= this.getScoFaseSolicitacaoDAO().obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(item.getId().getLctNumero(), item.getId().getNumero()); 
		
		if (listaFases != null && !listaFases.isEmpty()) {
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(listaFases).get(0);	
			if (fase.getSolicitacaoDeCompra() != null){
				fase.setSolicitacaoDeCompra(this.comprasFacade.obterScoSolicitacaoDeCompraPorChavePrimaria(fase.getSolicitacaoDeCompra().getNumero()));
				if (fase.getSolicitacaoDeCompra().getMaterial() != null){
					ScoMaterial material = this.comprasFacade.obterScoMaterial(fase.getSolicitacaoDeCompra().getMaterial().getCodigo());
					unidadeMaterial  = material.getUmdCodigo();
				}
			}			
		}		
		
		return unidadeMaterial;
	}
	
	/**
	 * Retorna se a unidade de medida do item de proposta do fornecedor é a mesma da solicitação
	 * @param unidadeSolicitada
	 * @param embalagem
	 * @return Boolean
	 */
	public Boolean validarEmbalagemProposta(String unidadeSolicitada, ScoUnidadeMedida embalagem) {
		Boolean embalagemIgual = Boolean.FALSE;
		ScoUnidadeMedida scoUnid = this.getScoUnidadeMedidaDAO().obterPorChavePrimaria(unidadeSolicitada);
		
		if (scoUnid != null && scoUnid.equals(embalagem)) {
			embalagemIgual = Boolean.TRUE;
		}
	
		return embalagemIgual;
	}
	
	/**
	 * Retorna o número do complemento da AF de determinado item de licitação (PAC)
	 * @param item
	 * @return String
	 */
	public String obterComplementoAutorizacaoFornecimento(ScoItemLicitacao item) {
		String complementoAf = null;
		
		List<ScoFaseSolicitacao> listaFases = this.getScoFaseSolicitacaoDAO().obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(item.getId().getLctNumero(), item.getId().getNumero());

		if (listaFases !=null && !listaFases.isEmpty()) {
			for (ScoFaseSolicitacao fase : listaFases) {
				if (!fase.getExclusao()) {
					ScoItemAutorizacaoForn itemAf = null;
					if (fase.getTipo().equals(DominioTipoFaseSolicitacao.C)) {
						itemAf = this.getAutFornecimentoFacade().obterItemAfPorSolicitacaoCompra(fase.getSolicitacaoDeCompra().getNumero(), false, false);
					} else {
						itemAf = this.getAutFornecimentoFacade().obterItemAfPorSolicitacaoServico(fase.getSolicitacaoServico().getNumero(), false, false);
					}

					if (itemAf != null) {
						complementoAf = itemAf.getAutorizacoesForn().getNroComplemento().toString();
						break;
					}
				}
			}
		}
		return complementoAf;
	}
	
	/**
	 * Obtém o tipo (DominioTipoSolicitacao) de determinado item da licitação (PAC)
	 * @param item
	 * @return DominioTipoSolicitacao
	 */
	public DominioTipoSolicitacao obterTipoSolicitacao(ScoItemLicitacao item) {
		DominioTipoSolicitacao tipoSolicitacao = DominioTipoSolicitacao.SC;
		item.setFasesSolicitacao(new HashSet<ScoFaseSolicitacao>(this.scoFaseSolicitacaoDAO.obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(item.getId().getLctNumero(), item.getId().getNumero())));
		
		if (item.getFasesSolicitacao() !=null && !item.getFasesSolicitacao().isEmpty()) {
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(item.getFasesSolicitacao()).get(0);
			if (fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null) {
				tipoSolicitacao = DominioTipoSolicitacao.SC;
			} else {
				tipoSolicitacao = DominioTipoSolicitacao.SS;
			}
		}
		return tipoSolicitacao;
	}
	
	/**
	 * Obtem uma descrição da solicitação de compra/serviço de um item da licitação (PAC) conforme preenchimento dos campos
	 * @param item
	 * @return String
	 */
	public String obterDescricaoSolicitacao(ScoItemLicitacao item) {
		String descricaoSolicitacao = null;
		
		if (item.getFasesSolicitacao()!=null && !item.getFasesSolicitacao().isEmpty()){
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(item.getFasesSolicitacao()).get(0);
			if (fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getCodigo() != null){
				descricaoSolicitacao = fase.getSolicitacaoDeCompra().getDescricao();
			}
			else{
				ScoSolicitacaoServico scoSolicitacaoServico = solicitacaoServicoFacade.obterSolicitacaoServico(fase.getSolicitacaoServico().getNumero());
				descricaoSolicitacao = scoSolicitacaoServico.getDescricao();
			}
				
		}
		return descricaoSolicitacao;		
	}
	
	/**
	 * Retorna o nome de um material ou serviço de um item de licitação (PAC)
	 * @param item
	 * @param concatenarCodigo
	 * @return String
	 */
	public String obterNomeMaterialServico(ScoItemLicitacao item, Boolean concatenarCodigo) {
		return this.getScoFaseSolicitacaoDAO().obterNomeMaterialServico(item, concatenarCodigo);
	}
	
	/**
	 * Retorna a descrição de um material/serviço de um item de licitação (PAC)
	 * @param item
	 * @return String
	 */
	public String obterDescricaoMaterialServico(ScoItemLicitacao item){
		
		String descricaoMatServ = null;	
		
		List<ScoFaseSolicitacao> listaFases= this.getScoFaseSolicitacaoDAO().obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(item.getId().getLctNumero(), item.getId().getNumero()); 
		
		if (listaFases != null && !listaFases.isEmpty()) {
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(listaFases).get(0);	
			if (fase.getSolicitacaoDeCompra() != null){
				fase.setSolicitacaoDeCompra(this.comprasFacade.obterScoSolicitacaoDeCompraPorChavePrimaria(fase.getSolicitacaoDeCompra().getNumero()));
				if (fase.getSolicitacaoDeCompra().getMaterial() != null){
					ScoMaterial material = this.comprasFacade.obterScoMaterial(fase.getSolicitacaoDeCompra().getMaterial().getCodigo());
					descricaoMatServ = material.getDescricao();
				}
			}
			else {
				if (fase.getSolicitacaoServico() != null){
					fase.setSolicitacaoServico(this.solicitacaoServicoFacade.obterSolicitacaoServico(fase.getSolicitacaoServico().getNumero()));
					if (fase.getSolicitacaoServico().getServico() != null){
						descricaoMatServ = this.comprasFacade.obterServicoPorId(fase.getSolicitacaoServico().getServico().getCodigo()).getDescricao();
					}
				}
			}
		}
		return descricaoMatServ;
	}

	/**
	 * Retorna o ScoMaterial de um item de licitação (PAC) 
	 * @param item
	 * @return
	 */
	public ScoMaterial obterMaterialItemLicitacao(ScoItemLicitacao item){
		ScoMaterial material = null;
		
		if (item.getFasesSolicitacao() != null && !item.getFasesSolicitacao().isEmpty()) {
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(item.getFasesSolicitacao()).get(0);
			if(fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial() != null) {
				material = fase.getSolicitacaoDeCompra().getMaterial();
			}
		}
		return material;
	}

	/**
	 * Obtém a quantidade solicitada do material/serviço de um item de licitação (PAC)
	 * @param item
	 * @return Integer
	 */
	public Integer obterQuantidadeMaterialServico(ScoItemLicitacao item) {
		Integer quantidadeSolicitacao = 0;
		
		List<ScoFaseSolicitacao> listaFases= this.getScoFaseSolicitacaoDAO().obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(item.getId().getLctNumero(), item.getId().getNumero()); 
		
		if (listaFases != null && !listaFases.isEmpty()) {
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(listaFases).get(0);	
			if (fase.getSolicitacaoDeCompra() != null){
				fase.setSolicitacaoDeCompra(this.comprasFacade.obterScoSolicitacaoDeCompraPorChavePrimaria(fase.getSolicitacaoDeCompra().getNumero()));
				if (fase.getSolicitacaoDeCompra().getMaterial() != null){
					if (fase.getSolicitacaoDeCompra().getQtdeAprovada() != null) {
						quantidadeSolicitacao = fase.getSolicitacaoDeCompra().getQtdeAprovada().intValue();
					}
				}
			}
			else {
				if (fase.getSolicitacaoServico() != null){
					fase.setSolicitacaoServico(this.solicitacaoServicoFacade.obterSolicitacaoServico(fase.getSolicitacaoServico().getNumero()));
					if (fase.getSolicitacaoServico().getServico() != null){
						quantidadeSolicitacao = fase.getSolicitacaoServico().getQtdeSolicitada();
					}
				}
			}
		}		
		
		return quantidadeSolicitacao;		
	}
	
	/**
	 * Retorna o valor total da licitação (PAC)
	 * @param numeroLicitacao
	 * @return BigDecimal
	 */
	public BigDecimal obterValorTotalPorNumeroLicitacao(Integer numeroLicitacao) {
		
		List<ScoItemLicitacao> listaItens = this.getScoItemLicitacaoDAO().
				pesquisarItemLicitacaoPorNumeroLicitacao(null, null, null,false, numeroLicitacao);
		
		BigDecimal valorTotalItens = BigDecimal.ZERO;
		
		for (ScoItemLicitacao item : listaItens) {
			if (item.getValorUnitario() != null) {
				valorTotalItens = valorTotalItens.add(item.getValorUnitario().multiply(new BigDecimal(this.obterQuantidadeMaterialServico(item))));
			}
		}
		
		return valorTotalItens;
	}
	
	/**
	 * Retorna o valor de um item de licitacao (PAC)
	 * @param numeroLicitacao
	 * @param numeroItem
	 * @return BigDecimal
	 */
	public BigDecimal obterValorTotalItemPac(Integer numeroLicitacao, Short numeroItem){
		BigDecimal valorTotalItem = BigDecimal.ZERO;
		
		ScoItemLicitacao itemLicitacao = this.getScoItemLicitacaoDAO().obterItemLicitacaoPorNumeroLicitacaoENumeroItem(numeroLicitacao, numeroItem);
		
		if (itemLicitacao != null && itemLicitacao.getValorUnitario() != null) {
			Integer qtd = this.obterQuantidadeMaterialServico(itemLicitacao);
			if (qtd != null) {
				valorTotalItem = itemLicitacao.getValorUnitario().multiply(new BigDecimal(qtd));
			}
		}
		
		return valorTotalItem;
	}

	/**
	 * Obtem o codigo da solicitacao de compra ou servico associado a determinado item de licitacao
	 * @param item
	 * @return Integer
	 */
	public Integer obterNumeroSolicitacao(ScoItemLicitacao item) {
		Integer numeroSolicitacao = null;
		
		if (item.getFasesSolicitacao() != null && !item.getFasesSolicitacao().isEmpty()) {
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(item.getFasesSolicitacao()).get(0);
			if(fase.getSolicitacaoDeCompra() != null) {
				numeroSolicitacao = Integer.valueOf(fase.getSolicitacaoDeCompra().getNumero());
			} else if (fase.getSolicitacaoServico() != null) {
				numeroSolicitacao = Integer.valueOf(fase.getSolicitacaoServico().getNumero());
			}
		}		
		
		return numeroSolicitacao;
	}
		
	/**
	 * Monta um ScoItemPacVO baseado em um ScoItemLicitacao 
	 * @param item
	 * @return ScoItemPacVO
	 */
	public ScoItemPacVO montarItemObjetoVO(ScoItemLicitacao item) {
		ScoItemPacVO itemPacVO = new ScoItemPacVO();
		
		itemPacVO.setNumeroItem(item.getId().getNumero());
		itemPacVO.setValorUnitarioPrevisto(item.getValorUnitario()); 
		itemPacVO.setIndFrequencia(item.getIndFrequenciaEntrega());
		itemPacVO.setFrequenciaEntrega(item.getFrequenciaEntrega()); 
		itemPacVO.setIndEmAf(item.getEmAf());
		itemPacVO.setIndJulgada(item.getJulgParcial());
		itemPacVO.setNumeroSolicitacao(this.obterNumeroSolicitacao(item));
		itemPacVO.setTipoSolicitacao(this.obterTipoSolicitacao(item));
		itemPacVO.setNomeMaterial(this.getNomeMaterialTruncado(obterNomeMaterialServico(item, false), 15));
		itemPacVO.setUnidadeMaterial(this.obterUnidadeMaterial(item));
		itemPacVO.setQtdeSolicitada(this.obterQuantidadeMaterialServico(item));
		itemPacVO.setComplemento(this.obterComplementoAutorizacaoFornecimento(item));
		itemPacVO.setCodMatServ(this.obterCodMatServ(item));
		
		List<ScoItemPropostaFornecedor> listaItensProposta = this.getScoItemPropostaFornecedorDAO().
				pesquisarItemPropostaEscolhidaPorNumeroLicitacaoENumeroItem(item.getId().getLctNumero(), item.getId().getNumero());

		for (ScoItemPropostaFornecedor itemProposta : listaItensProposta) {
			itemPacVO.setIndEscolhido(itemProposta.getIndEscolhido());
			if (itemProposta.getCriterioEscolhaProposta() != null) {
				itemPacVO.setCriterioEscolha(itemProposta.getCriterioEscolhaProposta().getDescricao());
				itemPacVO.setForncedorVencedor(itemProposta.getPropostaFornecedor().getFornecedor());
				break;
			}
		}

		itemPacVO.setIndExclusao(item.getExclusao());
		itemPacVO.setItemLicitacaoOriginal(item);
		if (item.getLoteLicitacao() != null) {
			itemPacVO.setNumeroLote(item.getLoteLicitacao().getId().getNumero());
		}
		
		itemPacVO.setItemOutroPac(getScoFaseSolicitacaoDAO().verificarSolicOutroPAC(obterNumeroSolicitacao(item), item.getId().getLctNumero(), itemPacVO.getTipoSolicitacao()));
		
		if (item.getMotivoCancel() != null && item.getExclusao() != null && item.getExclusao()){
			itemPacVO.setMotivoExclusao("Item cancelado através do julgamento - " + item.getMotivoExclusao());
		} else {
			if (item.getExclusao() != null && item.getExclusao()){
				itemPacVO.setMotivoExclusao(item.getMotivoExclusao());
			}
		}
		if (itemPacVO.getTipoSolicitacao().equals(DominioTipoSolicitacao.SC)) {
			itemPacVO.setPossuiAnexo(getScoArquivoAnexoDAO().verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento.SC, itemPacVO.getNumeroSolicitacao()));	
		} else {
			itemPacVO.setPossuiAnexo(getScoArquivoAnexoDAO().verificarExistenciaArquivosPorNumeroOrigem(DominioOrigemSolicitacaoSuprimento.SS, itemPacVO.getNumeroSolicitacao()));
		}
		
		return itemPacVO;
	}

	
	/**
	 * Retorna a descrição de um codigo material/servico de um item de licitação (PAC)
	 * @param item
	 * @return Integer
	 */
	public Integer obterCodMatServ(ScoItemLicitacao item){
		
		Integer codMatServ = null;
		
		List<ScoFaseSolicitacao> listaFases= this.getScoFaseSolicitacaoDAO().obterFasesSolicitacaoPorNumeroLicitacaoENumeroItemLicitacao(item.getId().getLctNumero(), item.getId().getNumero()); 
		
		if (listaFases != null && !listaFases.isEmpty()) {
			ScoFaseSolicitacao fase = new ArrayList<ScoFaseSolicitacao>(listaFases).get(0);	
			if (fase.getSolicitacaoDeCompra() != null){
				fase.setSolicitacaoDeCompra(this.comprasFacade.obterScoSolicitacaoDeCompraPorChavePrimaria(fase.getSolicitacaoDeCompra().getNumero()));
				if (fase.getSolicitacaoDeCompra().getMaterial() != null){
					codMatServ = fase.getSolicitacaoDeCompra().getMaterial().getCodigo();
				}
			}
			else {
				if (fase.getSolicitacaoServico() != null){
					fase.setSolicitacaoServico(this.solicitacaoServicoFacade.obterSolicitacaoServico(fase.getSolicitacaoServico().getNumero()));
					if (fase.getSolicitacaoServico().getServico() != null){
						codMatServ = fase.getSolicitacaoServico().getServico().getCodigo();
					}
				}
			}
		}
		return codMatServ;
	}

	
	/**
	 * Remove o item passado como paramaetro da lista tambem passada por parametro
	 * @param item
	 * @param list
	 */
	public void removerItemLista(ScoItemPacVO item, List<ScoItemPacVO> list) {
		Integer index = list.indexOf(item);

		if (index >= 0) {
			list.remove(index);
		}
	}
	
	private String getNomeMaterialTruncado(String codNomeMaterial, Integer tamanhoMaximo) {
		if (codNomeMaterial != null && codNomeMaterial.length() > tamanhoMaximo) {
			codNomeMaterial = codNomeMaterial.substring(0, tamanhoMaximo) + "...";
		}
		return codNomeMaterial;
	}
	
	protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}
	
	protected ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
	
	protected ScoArquivoAnexoDAO getScoArquivoAnexoDAO() {
		return scoArquivoAnexoDAO;
	}

	protected ScoUnidadeMedidaDAO getScoUnidadeMedidaDAO() {
		return scoUnidadeMedidaDAO;
	}
	
	protected ScoItemPropostaFornecedorDAO getScoItemPropostaFornecedorDAO() {
		return scoItemPropostaFornecedorDAO;
	}
	
	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}	

}
