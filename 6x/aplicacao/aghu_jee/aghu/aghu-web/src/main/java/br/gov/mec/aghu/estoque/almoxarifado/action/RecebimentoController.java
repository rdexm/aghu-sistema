package br.gov.mec.aghu.estoque.almoxarifado.action;

import java.net.UnknownHostException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioBoletimOcorrencias;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoDevolucao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.RecebimentoFiltroVO;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.model.SceNotaRecebProvisorio;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.VScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class RecebimentoController extends ActionController implements ActionPaginator {

	private static final Log LOG = LogFactory.getLog(RecebimentoController.class);

	private static final long serialVersionUID = 8919020356375253001L;
	
	private static final String PAGE_ITENSRECEBIMENTO = "itensRecebimento";
	private static final String PAGE_GERACAODEVOLUCAO =	"geracaoDevolucao";
	
	
	@Inject @Paginator
	private DynamicDataModel<SceNotaRecebProvisorio> dataModel;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private ICascaFacade cascaService;
	
		
	private RecebimentoFiltroVO filtroVO = new RecebimentoFiltroVO();
	private VScoFornecedor fornecedor;
	private ScoMaterial material;
	private ScoServico servico;
	private DominioSimNao indConfirmado;
	private DominioSimNao indEstorno;
	private Integer numeroSeq;
	private Boolean indFornecedor;
	private Boolean confirmaEstorno;
	private Boolean readOnlySituacaoDevolucao;
	private SceNotaRecebProvisorio notaRecebimentoProvisorio;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Limpa os filtros da pesquisa
	 */
	public void limpar() {
		this.setFornecedor(null);
		this.setMaterial(null);
		this.setServico(null);
		this.setFiltroVO(new RecebimentoFiltroVO());
		this.setIndEstorno(null);
		this.setIndConfirmado(null);
		//this.setAtivo(false);
		this.dataModel.limparPesquisa();
	}
	
	@Override
	public Long recuperarCount() {
		return this.estoqueFacade.contarNotasRecebimentoProvisorio(this.popularFiltro());
	}

	@Override
	public List<SceNotaRecebProvisorio> recuperarListaPaginada(final Integer arg0, final Integer arg1, final String arg2, final boolean arg3) {
		final List<ApplicationBusinessException> erros = new ArrayList<ApplicationBusinessException>();
		final List<SceNotaRecebProvisorio> retorno = this.estoqueFacade
				.pesquisarNotasRecebimentoProvisorio(this.popularFiltro(), arg0, arg1, arg2, arg3, erros);
			
		for (final ApplicationBusinessException erro : erros) {
			this.apresentarExcecaoNegocio(erro);
		}
		return retorno;
	}

	private RecebimentoFiltroVO popularFiltro() {
		if (this.getFiltroVO() == null) {
			this.setFiltroVO(new RecebimentoFiltroVO());
		}
		if (this.getFornecedor() != null) {
			this.getFiltroVO().setNumeroFornecedor(this.getFornecedor().getNumeroFornecedor());
		} else {
			this.getFiltroVO().setNumeroFornecedor(null);
		}
		if (this.getMaterial() != null) {
			this.getFiltroVO().setCodigoMaterial(this.getMaterial().getCodigo());
		} else {
			this.getFiltroVO().setCodigoMaterial(null);
		}
		if (this.getServico() != null) {
			this.getFiltroVO().setCodigoServico(this.getServico().getCodigo());
		} else {
			this.getFiltroVO().setCodigoServico(null);
		}
		if (this.getIndConfirmado() != null) {
			this.getFiltroVO().setIndConfirmado(DominioSimNao.S.equals(this.getIndConfirmado()));
		} else {
			this.getFiltroVO().setIndConfirmado(null);
		}
		if (this.getIndEstorno() != null) {
			this.getFiltroVO().setIndEstorno(DominioSimNao.S.equals(this.getIndEstorno()));
		} else {
			this.getFiltroVO().setIndEstorno(null);
		}
		return this.getFiltroVO();
	}

	public String pesquisar() {
		if (this.estoqueFacade.contarNotasRecebimentoProvisorio(this.popularFiltro())==1) {
			final List<SceNotaRecebProvisorio> retorno = this.estoqueFacade.pesquisarNotasRecebimentoProvisorio(this.popularFiltro(), Integer.valueOf(0),  Integer.valueOf(1), null, false, null);
			if (retorno!=null && retorno.size()==1) {
				numeroSeq = retorno.get(0).getSeq();
				if (retorno.get(0).getScoAfPedido()!=null && retorno.get(0).getScoAfPedido().getScoAutorizacaoForn()!=null) {
					indFornecedor = retorno.get(0).getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor().getCgc() != null ||
						retorno.get(0).getScoAfPedido().getScoAutorizacaoForn().getPropostaFornecedor().getFornecedor().getCpf() != null ? true : false;
				} else {
					indFornecedor = false;
				}
			}
			return PAGE_ITENSRECEBIMENTO;
		} else {
			this.dataModel.reiniciarPaginator();
			return null;
		}
	}
	
	/**
	 * Produtor para informacoes para a controller ItensRecebimentoController
	 * 
	 * @return
	 */
	@Produces @RequestScoped @SelectionQualifier
	public RecebimentoDetalhaVO obterRecebimentoDetalhaVO() {
		RecebimentoDetalhaVO vo = new RecebimentoDetalhaVO();
		
		vo.setNotaRecebimentoProvisorioSeq(this.numeroSeq);
		vo.setIndFornecedor(this.indFornecedor);
		
		return vo;
	}
	
	
	public String visualizarItensRecebimento() {
		//view="/estoque/almoxarifado/itensRecebimento.xhtml"
		return PAGE_ITENSRECEBIMENTO;
	}
	
	public String devolverRecebimento() {
		//<!-- view="/estoque/almoxarifado/geracaoDevolucao.xhtml" -->
		return PAGE_GERACAODEVOLUCAO;
	}
	
	public String voltarPesquisa() {
		this.confirmaEstorno = false;
		// this.reiniciarPaginator();
		return null; // "listaRecebimentos";
	}
	
	public  void prepararEstorno(SceNotaRecebProvisorio notaRecProvisorio) {	
		this.notaRecebimentoProvisorio = notaRecProvisorio;
		
		if (this.notaRecebimentoProvisorio.getIndEstorno()) {
			apresentarMsgNegocio(Severity.INFO, "M2_RECEBIMENTO_JA_ESTORNADO");
		} else {
			if (this.notaRecebimentoProvisorio.getIndConfirmado() 
					&& this.notaRecebimentoProvisorio.getNotaRecebimento()!=null 
				    && this.notaRecebimentoProvisorio.getNotaRecebimento().getSeq() != null) {
				
				if (!this.cascaService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "estornarRecebConfirmado")) {
					 apresentarMsgNegocio(Severity.INFO, "M1_RECEBIMENTO_NAO_PODE_SER_ESTORNADO");
				} else{
					 this.setConfirmaEstorno(true);	
			    }
				
			} else {
				this.setConfirmaEstorno(true);	
		    }
		}
	}
	
	public String estornarRecebimento() throws ApplicationBusinessException {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(), e);
		}
		try {
			if (notaRecebimentoProvisorio.getIndConfirmado() && notaRecebimentoProvisorio.getNotaRecebimento() != null
					&& notaRecebimentoProvisorio.getNotaRecebimento().getSeq() != null) {
				//Confirmado
				this.estoqueFacade.estornarRecebConfirmado(notaRecebimentoProvisorio, nomeMicrocomputador);
			} else {
				//Provisório
				SceNotaRecebProvisorio notaRecebimentoProvisorioOriginal = this.estoqueFacade.clonarNotaRecebimentoProvisorio(notaRecebimentoProvisorio); 
				this.estoqueFacade.estornarRecebimento(notaRecebimentoProvisorio, notaRecebimentoProvisorioOriginal);
			}
			//this.estoqueFacade.flush();
			//this.dirty = true;
			apresentarMsgNegocio(Severity.INFO, "M4_ESTORNO_REALIZADO_SUCESSO", notaRecebimentoProvisorio.getSeq());
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.apresentarExcecaoNegocio(e);
		}
		this.fecharModalConfirmacaoEstorno();
		
		return null; //"estornoRecebimentos";
	}
	
	public String buscarDescrBoletimOcorrencias(Integer seqNotaRecebProvisorio) {
		//Busca a descrição da devolução na SCE_BOLETIM_OCORRENCIAS
		if (seqNotaRecebProvisorio != null) {
			List<SceBoletimOcorrencias> listaBoletimOcorrencia = this.estoqueFacade.pesquisarBoletimOcorrenciaNotaRecebimentoSituacao(
					seqNotaRecebProvisorio, DominioBoletimOcorrencias.S, true
			);
			
			if (listaBoletimOcorrencia != null && !listaBoletimOcorrencia.isEmpty()) {
				return listaBoletimOcorrencia.get(listaBoletimOcorrencia.size()-1).getSituacao().getDescricao();
			}
		}
		return null;
	}
	
	public String getUrlImprimirNR(final Integer numNotaRec) {
		final String msg = "pages/estoque/relatorios/imprimirNotaRecebimentoPdf.xhtml?numNotaRec={0}&considerarNotaEmpenho=false&considerarUnidadeFuncional=false&esconderVoltar=true";
		//&conversationPropagation=begin
		return MessageFormat.format(msg, numNotaRec.toString());
	}
	
	public Boolean verificarRecebimentoServico(SceNotaRecebProvisorio notaRecebProv) {
		return this.estoqueFacade.verificarRecebimentoServico(notaRecebProv);
	}
	
	public Boolean isVerificaConfirmarDevolucao(Integer seqNotaRecebProvisorio){		
		return this.estoqueFacade.verificaExisteBoletimOcorrencia(seqNotaRecebProvisorio);		
	}	
	
	public void fecharModalConfirmacaoEstorno(){
		this.confirmaEstorno = false;
	}
	public List<VScoFornecedor> pesquisarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.returnSGWithCount(this.comprasFacade.pesquisarVFornecedorPorCgcCpfRazaoSocial(parametro, 100),contarFornecedoresPorCgcCpfRazaoSocial(parametro));
	}

	public Long contarFornecedoresPorCgcCpfRazaoSocial(String parametro) {
		return this.comprasFacade.contarVFornecedorPorCgcCpfRazaoSocial(parametro);
	}

	public List<ScoMaterial> listarMateriais(String filter) {
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(filter, null, true),listarMateriaisCount(filter));
	}

	public Long listarMateriaisCount(String filter) {
		return this.comprasFacade.listarScoMateriaisCount(filter, null, true);
	}

	public List<ScoServico> listarServicos(String filter) {
		return this.returnSGWithCount(this.comprasFacade.listarServicos(filter),listarServicosCount(filter));
	}

	public Long listarServicosCount(String filter) {
		return this.comprasFacade.listarServicosCount(filter);
	}

	/*
	 * Getters e setters
	 */
	
	public void setFiltroVO(RecebimentoFiltroVO filtroVO) {
		this.filtroVO = filtroVO;
	}

	public RecebimentoFiltroVO getFiltroVO() {
		return filtroVO;
	}

	public void setFornecedor(VScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public VScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setIndConfirmado(DominioSimNao indConfirmado) {
		this.indConfirmado = indConfirmado;
	}

	public DominioSimNao getIndConfirmado() {
		return indConfirmado;
	}

	public void setIndEstorno(DominioSimNao indEstorno) {
		this.indEstorno = indEstorno;
	}

	public DominioSimNao getIndEstorno() {
		return indEstorno;
	}
	
	public Integer getNumeroSeq() {
		return numeroSeq;
	}

	public void setNumeroSeq(Integer numeroSeq) {
		this.numeroSeq = numeroSeq;
	}

	public Boolean getIndFornecedor() {
		return indFornecedor;
	}

	public void setIndFornecedor(Boolean indFornecedor) {
		this.indFornecedor = indFornecedor;
	}

	public IEstoqueFacade getEstoqueFacade() {
		return estoqueFacade;
	}

	public void setEstoqueFacade(IEstoqueFacade estoqueFacade) {
		this.estoqueFacade = estoqueFacade;
	}

	public SceNotaRecebProvisorio getNotaRecebimentoProvisorio() {
		return notaRecebimentoProvisorio;
	}

	public void setNotaRecebimentoProvisorio(
			SceNotaRecebProvisorio notaRecebimentoProvisorio) {
		this.notaRecebimentoProvisorio = notaRecebimentoProvisorio;
	}

	public Boolean getConfirmaEstorno() {
		return confirmaEstorno;
	}

	public void setConfirmaEstorno(Boolean confirmaEstorno) {
		this.confirmaEstorno = confirmaEstorno;
	}
	
	public Boolean getReadOnlySituacaoDevolucao() {
		
		if (this.getFiltroVO().getTipoDevolucao() != null) {
			if (this.getFiltroVO().getSituacaoDevolucao() == null) {
				this.filtroVO.setSituacaoDevolucao(DominioSituacaoDevolucao.E);
				readOnlySituacaoDevolucao = false;
			}
		} else { 
			this.filtroVO.setSituacaoDevolucao(null);
			readOnlySituacaoDevolucao = true; 
		}

		return readOnlySituacaoDevolucao;	
	}
 


	public DynamicDataModel<SceNotaRecebProvisorio> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<SceNotaRecebProvisorio> dataModel) {
	 this.dataModel = dataModel;
	}
}