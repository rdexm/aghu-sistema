package br.gov.mec.aghu.compras.autfornecimento.business;

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
import br.gov.mec.aghu.compras.autfornecimento.vo.RelatorioAFPVO;
import br.gov.mec.aghu.compras.autfornecimento.vo.ScoItemAFPVO;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.dao.ScoAutorizacaoFornecedorPedidoDAO;
import br.gov.mec.aghu.compras.dao.ScoCumXProgrEntregaDAO;
import br.gov.mec.aghu.compras.dao.ScoFaseSolicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoProgEntregaItemAutorizacaoFornecimentoDAO;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.compras.vo.AFPFornecedoresVO;
import br.gov.mec.aghu.compras.vo.AcessoFornProgEntregaFiltrosVO;
import br.gov.mec.aghu.compras.vo.AutorizacaoFornPedidosVO;
import br.gov.mec.aghu.compras.vo.PesquisaAutFornPedidosVO;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedido;
import br.gov.mec.aghu.model.ScoAutorizacaoFornecedorPedidoId;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimentoId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ScoAutorizacaoFornecedorPedidoON extends BaseBusiness{

	@EJB
	private ScoProgEntregaItemAutorizacaoFornecimentoRN scoProgEntregaItemAutorizacaoFornecimentoRN;
	
	@EJB
	private ScoAutorizacaoFornecedorPedidoRN scoAutorizacaoFornecedorPedidoRN;
	
	private static final Log LOG = LogFactory.getLog(ScoAutorizacaoFornecedorPedidoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPacFacade pacFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;
	
	@EJB
	protected IRegistroColaboradorFacade registroColaboradorFacade;
	
	
	@Inject
	private ScoFaseSolicitacaoDAO scoFaseSolicitacaoDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	
	@Inject
	private ScoAutorizacaoFornDAO scoAutorizacaoFornDAO;
	
	@Inject
	private ScoAutorizacaoFornecedorPedidoDAO scoAutorizacaoFornecedorPedidoDAO;
	
	@Inject
	private ScoCumXProgrEntregaDAO scoCumXProgrEntregaDAO;
	
	@Inject
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO scoProgEntregaItemAutorizacaoFornecimentoDAO;	

	private static final long serialVersionUID = 2211955322391131841L;

	public enum ScoAutorizacaoFornecedorPedidoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAM_OBRIG,
		MESSAGE_DATA_FINAL_MAIOR,
		MESSAGE_SEM_FILTROS_PESQUISA;		
	}
	
	public List<AutorizacaoFornPedidosVO> pesquisarAutFornPedidosPorFiltro(Integer first, Integer max,PesquisaAutFornPedidosVO filtroVO){
		
		List<AutorizacaoFornPedidosVO> lista = new ArrayList<AutorizacaoFornPedidosVO>();
		List<AutorizacaoFornPedidosVO> novaLista = new ArrayList<AutorizacaoFornPedidosVO>();
		
		this.validaPrevisaoEntrega(filtroVO);
		
		/*#30847  #5566 - Alterações na Pesquisa*/
		if (this.verificarConsultaDefault(filtroVO)){
			lista = this.getScoAutorizacaoFornecedorPedidoDAO().pesquisarAutFornPedidosPorDefault(first,max);
		}
		else {
			lista = this.getScoAutorizacaoFornecedorPedidoDAO().pesquisarAutFornPedidosPorFiltro(first,max,filtroVO);
		}
		
		if (lista != null && !lista.isEmpty()) {
			for (AutorizacaoFornPedidosVO retPesquisa : lista) {
				AutorizacaoFornPedidosVO itemNovaLista = new AutorizacaoFornPedidosVO();
				
				itemNovaLista.setAfnNumero(retPesquisa.getAfnNumero());
				itemNovaLista.setNumeroAFP(retPesquisa.getNumeroAFP());
				itemNovaLista.setLctNumero(retPesquisa.getLctNumero());
				itemNovaLista.setNumeroComplemento(retPesquisa.getNumeroComplemento());
				itemNovaLista.setNumeroFornecedor(retPesquisa.getNumeroFornecedor());
				itemNovaLista.setDtEnvioFornecedor(retPesquisa.getDtEnvioFornecedor());
				itemNovaLista.setRazaoSocial(retPesquisa.getRazaoSocial());
				itemNovaLista.setCgc(retPesquisa.getCgc());
				itemNovaLista.setCpf(retPesquisa.getCpf());
				itemNovaLista.setNomeGestor(retPesquisa.getNomeGestor());
				itemNovaLista.setIndPublicado(retPesquisa.getIndPublicado());
				itemNovaLista.setDtPublicacao(retPesquisa.getDtPublicacao());
				this.popularOrigem(itemNovaLista);
				itemNovaLista.setNomeServidor(retPesquisa.getNomeServidor());
				itemNovaLista.setFornecedor(retPesquisa.getFornecedor());
				// #30395 - copiado o método para lá
//				this.popularEmpenho(itemNovaLista);
				
				//Obtém a proposta fornecedor da AF, para buscar o fornecedor necessário no envio de e-mail
				ScoAutorizacaoForn scoAutForn = this.getScoAutorizacaoFornDAO().obterAfByNumero(retPesquisa.getAfnNumero());
				if (scoAutForn != null && scoAutForn.getPropostaFornecedor() != null) {
					itemNovaLista.setScoPropostaFornecedor(scoAutForn.getPropostaFornecedor());
				}
				
				novaLista.add(itemNovaLista);
			}
		}
		
		/*if (!novaLista.isEmpty()) {
			int indPrimeiro = first;
			int indUltimo = first + max;
			int tamLista = novaLista.size();
			if (indUltimo > tamLista) {
				indUltimo = tamLista;
			}
			return novaLista.subList(indPrimeiro, indUltimo);
		}*/

		
		return novaLista;
	}
	
   public Long pesquisarAutFornPedidosPorFiltroCount(PesquisaAutFornPedidosVO filtroVO){
	   Long count = 0L;
				
		this.validaPrevisaoEntrega(filtroVO);
		
		/*#30847  #5566 - Alterações na Pesquisa*/
		if (this.verificarConsultaDefault(filtroVO)){
			count = this.getScoAutorizacaoFornecedorPedidoDAO().pesquisarAutFornPedidosPorDefaultCount();
		}
		else {
			count = this.getScoAutorizacaoFornecedorPedidoDAO().pesquisarAutFornPedidosPorFiltroCount(filtroVO);
		}
		
		return count;
	}  

	
	
	private boolean verificarConsultaDefault(PesquisaAutFornPedidosVO filtroVO){
		if (filtroVO.getNumeroPAC()!=null || filtroVO.getFornecedor()!=null || filtroVO.getDataInicioEnvio() !=null
				|| filtroVO.getIndImpressa() !=null || filtroVO.getIndEnviada() !=null || filtroVO.getServidorGestor() !=null
				|| filtroVO.getGrupoMaterial() !=null || filtroVO.getMaterial() !=null || filtroVO.getServico() !=null
				|| filtroVO.getGrupoServico() !=null){
			return false;
		}
		
		return true;
		
	}
	
	private void popularOrigem(AutorizacaoFornPedidosVO item) {
		
		Integer nrEntregas = this.getScoCumXProgrEntregaDAO().contarProgrEntregasPorAf(item.getAfnNumero());
		if (nrEntregas == null || nrEntregas <= 0) {
			DominioTipoFaseSolicitacao tipo = this.getScoFaseSolicitacaoDAO().obterTipoFaseSolicitacaoPorNumeroAF(item.getAfnNumero());
			if (tipo == null) {
				this.setOrigemCUM(item);
			} else {
				switch (tipo) {
				case C:
					item.setOrigem(this.getResourceBundleValue("VALOR_ORIG_MAT_AF_ENTREGA_LIB"));
					item.setHintOrigem(this.getResourceBundleValue("HINT_ORIG_MAT_AF_ENTREGA_LIB"));
					item.setCorFundoOrigem("");
					break;
				case S:
					item.setOrigem(this.getResourceBundleValue("VALOR_ORIG_SRV_AF_ENTREGA_LIB"));
					item.setHintOrigem(this.getResourceBundleValue("HINT_ORIG_SRV_AF_ENTREGA_LIB"));
					item.setCorFundoOrigem("");
					break;
				default:
					this.setOrigemCUM(item);
					break;
				}
			}
		} else {
			this.setOrigemCUM(item);
		}
	}
	
	private void setOrigemCUM(AutorizacaoFornPedidosVO item) {
		item.setOrigem(this.getResourceBundleValue("VALOR_ORIG_CUM_AF_ENTREGA_LIB"));
		item.setHintOrigem("");
		item.setCorFundoOrigem("#0099FF");  //blue
	}
	
	
	
	private void validaPrevisaoEntrega(PesquisaAutFornPedidosVO filtroVO) {
		
		if((filtroVO.getDataInicioEnvio() != null && filtroVO.getDataFimEnvio() != null) ||
			(filtroVO.getDataInicioEnvio() == null && filtroVO.getDataFimEnvio() != null)) {
				filtroVO.setDataFimEnvio(DateUtil.adicionaDias(filtroVO.getDataFimEnvio(), 1));
		}
		else {
			if(filtroVO.getDataInicioEnvio() != null && filtroVO.getDataFimEnvio() == null) {
				filtroVO.setDataFimEnvio(DateUtil.truncaData(new Date()));
			}
		}
	}
	
	/**
	 * Altera SCO_AF_PEDIDOS e SCO_PROGR_ENTREGA_ITENS_AF
	 * @param numeroAF, numeroAFP
	 * @author dilceia.alves
	 * @since 24/06/2013
	 * @throws ApplicationBusinessException 
	 */
	public void alterarAFP(List<AutorizacaoFornPedidosVO> listaSel)
		throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (listaSel == null || listaSel.isEmpty() || servidorLogado == null) {
			throw new ApplicationBusinessException(
					ScoAutorizacaoFornecedorPedidoONExceptionCode.MENSAGEM_PARAM_OBRIG);
		}
		
		for (AutorizacaoFornPedidosVO item : listaSel) {
			//Obtém autorização pedido fornecedor para alterar
			ScoAutorizacaoFornecedorPedidoId autFornPedidoId = new ScoAutorizacaoFornecedorPedidoId();
			autFornPedidoId.setAfnNumero(item.getAfnNumero());
			autFornPedidoId.setNumero(item.getNumeroAFP());
			ScoAutorizacaoFornecedorPedido autFornPedido = this.getScoAutorizacaoFornecedorPedidoDAO().obterPorChavePrimaria(autFornPedidoId);
			//Atualiza SCO_AF_PEDIDOS
			autFornPedido.setDtEnvioFornecedor(new Date());
			autFornPedido.setSerMatriculaEnvioForn(servidorLogado.getId().getMatricula());
			autFornPedido.setSerVinCodigoEnvioForn(servidorLogado.getId().getVinCodigo().intValue());
			this.getScoAutorizacaoFornecedorPedidoRN().persistir(autFornPedido);
			
			//Obtém parcelas de entrega para alterar
			final List<ScoProgEntregaItemAutorizacaoFornecimento> listaProgrEntrega = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().pesquisaProgEntregaPorPedidoAf(item.getAfnNumero(), item.getNumeroAFP());
			if (listaProgrEntrega != null && !listaProgrEntrega.isEmpty()) {
				for (final ScoProgEntregaItemAutorizacaoFornecimento itemProgEntrega : listaProgrEntrega) {
					ScoProgEntregaItemAutorizacaoFornecimentoId progEntregaAutFornId = new ScoProgEntregaItemAutorizacaoFornecimentoId();
					progEntregaAutFornId.setIafAfnNumero(itemProgEntrega.getId().getIafAfnNumero());
					progEntregaAutFornId.setIafNumero(itemProgEntrega.getId().getIafNumero());
					progEntregaAutFornId.setParcela(itemProgEntrega.getId().getParcela());
					progEntregaAutFornId.setSeq(itemProgEntrega.getId().getSeq());
					ScoProgEntregaItemAutorizacaoFornecimento progEntregaAutForn = this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterPorChavePrimaria(progEntregaAutFornId);
					//Atualiza SCO_PROGR_ENTREGA_ITENS_AF
					progEntregaAutForn.setIndEnvioFornecedor(true);
					progEntregaAutForn.setObservacao("AFP NÃO ENVIADA AO FORNECEDOR. APENAS MARCADA COMO ENVIADA, VIA BOTÃO -NÃO ENVIAR- DA TELA DE ENVIO");
					this.getScoProgEntregaItemAutorizacaoFornecimentoRN().persistir(progEntregaAutForn);
				}
			}
			
			this.getScoAutorizacaoFornecedorPedidoDAO().flush();
			this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().flush();
		}
	}
	
	public void updateAFP(Integer numAf, Integer numAfp) throws ApplicationBusinessException{
		
		ScoAutorizacaoFornecedorPedidoId id = new ScoAutorizacaoFornecedorPedidoId();
		id.setAfnNumero(numAf);
		id.setNumero(numAfp);
		ScoAutorizacaoFornecedorPedido afp = this.getScoAutorizacaoFornecedorPedidoDAO().obterPorChavePrimaria(id);
		
		if (afp!=null && afp.getId()!=null && afp.getDtEnvioFornecedor()!=null){
			afp.setDtEnvioFornecedor(new Date());
			this.persistir(afp);
		}
		
	}
	
	
	public RelatorioAFPVO listarAfsPorPedidoENumAf(Integer afpNumero, Integer numeroPac, Short nroComplemento, int espEmpenho)
			throws ApplicationBusinessException{
		
		AghParametros pEspecieEmpenho = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ESPECIE_EMPENHO_ORIG);
		RelatorioAFPVO afVO = new RelatorioAFPVO();
		
		List<ScoItemAFPVO> listaItensAf =  this.getScoAutorizacaoFornecedorPedidoDAO().
				listarAfsPorPedidoENumAf(afpNumero,numeroPac, nroComplemento , pEspecieEmpenho.getVlrNumerico().intValue());
		
		for (ScoItemAFPVO item: listaItensAf){
			ScoProgEntregaItemAutorizacaoFornecimentoId itemAfpId = new ScoProgEntregaItemAutorizacaoFornecimentoId();
			itemAfpId.setIafAfnNumero(item.getIafAfnNumero());
			itemAfpId.setIafNumero(item.getIafNumero());
			itemAfpId.setParcela(item.getNumeroParcela());
			itemAfpId.setSeq(item.getSeqItemAfp());
			item.setItemAutorizacaoFornPedido(this.getScoProgEntregaItemAutorizacaoFornecimentoDAO().obterPorChavePrimaria(itemAfpId));
			afVO.setAfp(this.getScoAutorizacaoFornecedorPedidoDAO().obterPorChavePrimaria(item.getItemAutorizacaoFornPedido().getScoAfPedido().getId()));
			// #31467 Setar AF para envio de email na assinatura da AF
			if (item.getItemAutorizacaoFornPedido().getScoAfPedido()!=null && 
							item.getItemAutorizacaoFornPedido().getScoAfPedido().getScoAutorizacaoForn()==null){
				    item.setAutorizacaoForn(this.comprasFacade.obterScoAutorizacaoFornPorChavePrimaria(item.getAutorizacaoForn().getNumero()));
					item.getItemAutorizacaoFornPedido().getScoAfPedido().setScoAutorizacaoForn(item.getAutorizacaoForn());
			}
			
			if(item.getAutorizacaoForn().getPropostaFornecedor() != null) {
			   item.getAutorizacaoForn().setPropostaFornecedor(this.pacFacade.obterPropostaFornecedor(item.getAutorizacaoForn().getPropostaFornecedor().getId()));
			   if(item.getAutorizacaoForn().getPropostaFornecedor().getLicitacao()!= null){
				   item.getAutorizacaoForn().getPropostaFornecedor().setLicitacao(this.pacFacade.obterLicitacao(item.getAutorizacaoForn().getPropostaFornecedor().getLicitacao().getNumero()));
				   if(item.getAutorizacaoForn().getPropostaFornecedor().getLicitacao().getModalidadeLicitacao()!= null){
					  item.getAutorizacaoForn().getPropostaFornecedor().getLicitacao().setModalidadeLicitacao(this.comprasCadastrosBasicosFacade.obterModalidadeLicitacao(item.getAutorizacaoForn().getPropostaFornecedor().getLicitacao().getModalidadeLicitacao().getCodigo()));					   
				   }
			   }
			   
			   if(item.getAutorizacaoForn().getPropostaFornecedor().getFornecedor()!= null){
				  item.getAutorizacaoForn().getPropostaFornecedor().setFornecedor(this.comprasFacade.obterFornecedorPorNumero(item.getAutorizacaoForn().getPropostaFornecedor().getFornecedor().getNumero()));
			   }
			}
			if(item.getAutorizacaoForn().getCondicaoPagamentoPropos() != null) {
			   item.getAutorizacaoForn().setCondicaoPagamentoPropos(this.pacFacade.obterCondicaoPagamentoPropostaPorNumero(item.getAutorizacaoForn().getCondicaoPagamentoPropos().getNumero()));
			   
			   if(item.getAutorizacaoForn().getCondicaoPagamentoPropos().getFormaPagamento() != null){
			      item.getAutorizacaoForn().getCondicaoPagamentoPropos().setFormaPagamento(this.comprasCadastrosBasicosFacade.obterFormaPagamento(item.getAutorizacaoForn().getCondicaoPagamentoPropos().getFormaPagamento().getCodigo()));
			   }
			}
			
			if(item.getAutorizacaoForn() != null){
			   item.getAutorizacaoForn().setServidorGestor(buscarServidorPessoa(item.getAutorizacaoForn().getServidorGestor()));
			   item.getAutorizacaoForn().setServidorAssinaCoord(buscarServidorPessoa(item.getAutorizacaoForn().getServidorAssinaCoord()));
			   item.getAutorizacaoForn().setServidorAutorizado(buscarServidorPessoa(item.getAutorizacaoForn().getServidorAutorizado()));
			}
			
			obterItemAutForPedidoSolicitacoes(item);
			
		}
		afVO.setListaItensAfVO(listaItensAf);
		return afVO;
	}
	
	private void obterItemAutForPedidoSolicitacoes(ScoItemAFPVO item){
		if(item.getItemAutorizacaoFornPedido().getScoItensAutorizacaoForn() != null){
		   item.getItemAutorizacaoFornPedido().setScoItensAutorizacaoForn(this.comprasFacade.obterItemAutorizacaoFornPorId(item.getItemAutorizacaoFornPedido().getScoItensAutorizacaoForn().getId().getAfnNumero(), item.getItemAutorizacaoFornPedido().getScoItensAutorizacaoForn().getId().getNumero()));
			  
		   if(item.getItemAutorizacaoFornPedido().getScoItensAutorizacaoForn().getItemPropostaFornecedor()!= null){
			  item.getItemAutorizacaoFornPedido().getScoItensAutorizacaoForn().setItemPropostaFornecedor(this.comprasFacade.obterItemPropostaFornPorChavePrimaria(item.getItemAutorizacaoFornPedido().getScoItensAutorizacaoForn().getItemPropostaFornecedor().getId()));
				    
			  if (item.getItemAutorizacaoFornPedido().getScoItensAutorizacaoForn().getItemPropostaFornecedor().getItemLicitacao() != null){
				  item.getItemAutorizacaoFornPedido().getScoItensAutorizacaoForn().getItemPropostaFornecedor().setItemLicitacao(this.pacFacade.obterItemLicitacaoPorNumeroLicitacaoENumeroItem(item.getItemAutorizacaoFornPedido().getScoItensAutorizacaoForn().getItemPropostaFornecedor().getItemLicitacao().getId().getLctNumero(), item.getItemAutorizacaoFornPedido().getScoItensAutorizacaoForn().getItemPropostaFornecedor().getItemLicitacao().getId().getNumero())); 
			  }
		   }
				
		}
			
		if(item.getSolicitacaoCompra()!= null){
		   item.setSolicitacaoCompra(this.comprasFacade.obterScoSolicitacaoDeCompraPorChavePrimaria(item.getSolicitacaoCompra().getNumero()));
		   item.getSolicitacaoCompra().setMaterial(this.comprasFacade.obterMaterialPorId(item.getSolicitacaoCompra().getMaterial().getCodigo()));
		}
		else if(item.getSolicitacaoServico()!= null){
				item.setSolicitacaoServico(this.solicitacaoServicoFacade.obterSolicitacaoServico(item.getSolicitacaoServico().getNumero()));
				item.getSolicitacaoServico().setServico(this.comprasFacade.obterServicoPorId(item.getSolicitacaoServico().getServico().getCodigo()));
		}
	}
	
    private RapServidores buscarServidorPessoa(RapServidores servidor) throws ApplicationBusinessException{
		
		if (servidor != null) {
			servidor = this.registroColaboradorFacade.obterRapServidor(servidor.getId());
		    if(servidor.getPessoaFisica() != null) {
		    	servidor.setPessoaFisica(this.registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo()));
		    }
		}
		
		return servidor;
		
	}
	
	public void persistir(ScoAutorizacaoFornecedorPedido autorizacaoFornecedorPedido) {
		this.getScoAutorizacaoFornecedorPedidoRN().persistir(autorizacaoFornecedorPedido);
	}

	protected ScoAutorizacaoFornecedorPedidoRN getScoAutorizacaoFornecedorPedidoRN() {
		return scoAutorizacaoFornecedorPedidoRN;
	}
	
	protected ScoProgEntregaItemAutorizacaoFornecimentoRN getScoProgEntregaItemAutorizacaoFornecimentoRN() {
		return scoProgEntregaItemAutorizacaoFornecimentoRN;
	}
	
	protected ScoAutorizacaoFornecedorPedidoDAO getScoAutorizacaoFornecedorPedidoDAO() {
		return scoAutorizacaoFornecedorPedidoDAO;
	}
	
	private ScoCumXProgrEntregaDAO getScoCumXProgrEntregaDAO() {
		return scoCumXProgrEntregaDAO;
	}
	
	private ScoProgEntregaItemAutorizacaoFornecimentoDAO getScoProgEntregaItemAutorizacaoFornecimentoDAO() {
		return scoProgEntregaItemAutorizacaoFornecimentoDAO;
	}

	private ScoFaseSolicitacaoDAO getScoFaseSolicitacaoDAO() {
		return scoFaseSolicitacaoDAO;
	}
	
	private ScoAutorizacaoFornDAO getScoAutorizacaoFornDAO() {
		return scoAutorizacaoFornDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	public List<AFPFornecedoresVO> listProgEntregaFornecedor(AcessoFornProgEntregaFiltrosVO filtro, Boolean isCount, Integer firstResult, Integer maxResult) {
		return getScoAutorizacaoFornecedorPedidoDAO().listarProgEntregaFornecedor(filtro, firstResult, maxResult);
	}

	public Long listProgEntregaFornecedorCount(AcessoFornProgEntregaFiltrosVO filtro) {
		return getScoAutorizacaoFornecedorPedidoDAO().listarProgEntregaFornecedorCount(filtro);
	}
	
	public void validaPesquisaProgEntregaFornecedor(AcessoFornProgEntregaFiltrosVO filtro) throws ApplicationBusinessException{
		validaFiltrosProgEntregaFornecedor(filtro);
		validaPeriodoDatasProgEntregaFornecedor(filtro);
	}
	
	private void validaFiltrosProgEntregaFornecedor(AcessoFornProgEntregaFiltrosVO filtro) throws ApplicationBusinessException {
		
		if(filtro.getComplemento() == null && filtro.getDataAcessoFinal() == null && filtro.getDataAcessoInicial() == null
				&& filtro.getDataPublicacaoFinal() == null && filtro.getDataPublicacaoInicial() == null 
				&& filtro.getFornecedor() == null && filtro.getNumeroAF() == null && filtro.getNumeroAFP() == null
				&& filtro.getPublicacao()== null ){
			
			throw new ApplicationBusinessException(ScoAutorizacaoFornecedorPedidoONExceptionCode.MESSAGE_SEM_FILTROS_PESQUISA);
		}		
	}
	
	private void validaPeriodoDatasProgEntregaFornecedor(AcessoFornProgEntregaFiltrosVO filtro) throws ApplicationBusinessException {
		if (filtro.getDataPublicacaoInicial() != null && filtro.getDataPublicacaoFinal() != null) {
			if (!DateUtil.validaDataMaiorIgual(filtro.getDataPublicacaoFinal(), filtro.getDataPublicacaoInicial())) {
				throw new ApplicationBusinessException(ScoAutorizacaoFornecedorPedidoONExceptionCode.MESSAGE_DATA_FINAL_MAIOR);

			}
		} else if (filtro.getDataPublicacaoFinal() != null) {
			throw new ApplicationBusinessException(ScoAutorizacaoFornecedorPedidoONExceptionCode.MESSAGE_DATA_FINAL_MAIOR);
		}

		if (filtro.getDataAcessoInicial() != null && filtro.getDataAcessoFinal() != null) {
			if (!DateUtil.validaDataMaiorIgual(filtro.getDataAcessoFinal(), filtro.getDataAcessoInicial())) {
				throw new ApplicationBusinessException(ScoAutorizacaoFornecedorPedidoONExceptionCode.MESSAGE_DATA_FINAL_MAIOR);

			}
		} else if (filtro.getDataAcessoFinal() != null) {
			throw new ApplicationBusinessException(ScoAutorizacaoFornecedorPedidoONExceptionCode.MESSAGE_DATA_FINAL_MAIOR);
		}
	}
	
}
