package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para a tela de pesquisa de Procedimentos Cirúrgicos.
 * 
 * @author dpacheco
 *
 */
public class ProcedimentoCirurgicoPaginatorController extends ActionController implements ActionPaginator {

    private static final long serialVersionUID = -1416809997206809748L;
    
    private static final String PROCEDIMENTO_CRUD = "procedimentoCirurgicoCRUD";
	private static final String SINONIMO_CRUD = "sinonimoCRUD";
	private static final String TRICOTOMIA_CRUD = "tricotomiaAssocProcedCRUD";
	private static final String ESPEC_CRUD = "especialidadeProcedimentoCirurgicoCRUD";
	private static final String PESQUISA_SIGTAP = "pesquisaProcedimentoSIGTAP";
	private static final String COMP_SANG_CRUD = "compSangAssocProcedCRUD";

    @Inject @Paginator
    private DynamicDataModel<MbcProcedimentoCirurgicos> dataModel;

    @EJB
    private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	@Inject
	private ProcedimentoCirurgicoController procedimentoCirurgicoController;
	@Inject
	private SinonimoCRUDController sinonimoCRUDController;
	@Inject
	private TricotomiaAssocProcedCRUDController tricotomiaAssocProcedCRUDController;
	@Inject 
	private EspecialidadePrcdCirgCRUDController especialidadePrcdCirgCRUDController;
	@Inject 
	private PesquisarProcedimentoSIGTAPController pesquisarProcedimentoSIGTAPController;
	@Inject
	private CompSangAssocProcedCRUDController compSangAssocProcedCRUDController;
	
	private Integer filtroSeq;
	private String filtroDescricao;
	private DominioSituacao filtroIndSituacao;
	private DominioSimNao filtroIndGeraImagensPacs;
	private DominioSimNao filtroIndInteresseCcih;
	private Integer seqSelecionado;
	private Boolean exibeBotaoNovo = Boolean.FALSE;
	private Boolean habilitaBotoesLaterais = Boolean.FALSE;

    @PostConstruct
    protected void inicializar(){
        this.begin(conversation);
    }
    
	@Override
	public Long recuperarCount() {
		Boolean indGeraImagensPacs = null;
		if (filtroIndGeraImagensPacs != null) {
			indGeraImagensPacs = filtroIndGeraImagensPacs.isSim();
		}
		
		Boolean indInteresseCcih = null;
		if (filtroIndInteresseCcih != null) {
			indInteresseCcih = filtroIndInteresseCcih.isSim();
		}
		
		return blocoCirurgicoCadastroApoioFacade
				.obterCountProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(
						filtroSeq, filtroDescricao, filtroIndSituacao,
						indGeraImagensPacs, indInteresseCcih);
	}

	@Override
	public List<MbcProcedimentoCirurgicos> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		Boolean indGeraImagensPacs = null;
		if (filtroIndGeraImagensPacs != null) {
			indGeraImagensPacs = filtroIndGeraImagensPacs.isSim();
		}
		
		Boolean indInteresseCcih = null;
		if (filtroIndInteresseCcih != null) {
			indInteresseCcih = filtroIndInteresseCcih.isSim();
		}
		
		orderProperty = MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString();
		asc = true;
		
		return blocoCirurgicoCadastroApoioFacade
				.pesquisarProcedimentoCirurgicosPorCodigoDescricaoSituacaoPacsCcih(
						firstResult, maxResult, orderProperty, asc, filtroSeq,
						filtroDescricao, filtroIndSituacao, indGeraImagensPacs,
						indInteresseCcih);			
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		exibeBotaoNovo = Boolean.TRUE;
	}
	
	public void limpar() {
		filtroSeq = null;
		filtroDescricao = null;
		filtroIndSituacao = null;
		filtroIndGeraImagensPacs = null;
		filtroIndInteresseCcih = null;
		seqSelecionado = null;
		exibeBotaoNovo = Boolean.FALSE;
		habilitaBotoesLaterais = Boolean.FALSE;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public String iniciarInclusao() {
        procedimentoCirurgicoController.iniciarInclusao();
		return PROCEDIMENTO_CRUD;
	}
	
	public String editar(Integer seq) {
		procedimentoCirurgicoController.setSeq(seq);
		return PROCEDIMENTO_CRUD;
	}
	
	public String redirecionarTricotomia() {
		tricotomiaAssocProcedCRUDController.setPciSeq(this.seqSelecionado);
		return TRICOTOMIA_CRUD;
	}

	public String redirecionarSinonimo() {
		sinonimoCRUDController.setPciSeq(this.seqSelecionado);
		return SINONIMO_CRUD;
	}

	public String redirecionarEspecialidade() {
		especialidadePrcdCirgCRUDController.setPciSeq(this.seqSelecionado);
		return ESPEC_CRUD;
	}
	
	public String redirecionarSigtap() {
		pesquisarProcedimentoSIGTAPController.setPciSeq(this.seqSelecionado);
		return PESQUISA_SIGTAP;
	}
	
	public String redirecionarCompSang() {
		compSangAssocProcedCRUDController.setPciSeq(this.seqSelecionado);
		return COMP_SANG_CRUD;
	}
	
	public void ativarInativar(Integer pciSeq) {
		this.dataModel.reiniciarPaginator();
		
		MbcProcedimentoCirurgicos procedimentoCirurgico = 
				blocoCirurgicoCadastroApoioFacade.obterProcedimentoCirurgico(pciSeq);
		
		Boolean novaIndSituacao = !procedimentoCirurgico.getIndSituacao().isAtivo();
		procedimentoCirurgico.setIndSituacao(DominioSituacao.getInstance((novaIndSituacao)));
		try {
			blocoCirurgicoCadastroApoioFacade.persistirProcedimentoCirurgico(procedimentoCirurgico);
			
			pesquisar();

			String keyMensagem = "";
			if (procedimentoCirurgico.getIndSituacao().isAtivo()) {
				keyMensagem = "MENSAGEM_SUCESSO_ATIVAR_PROCEDIMENTO_CIRURGICO";
			} else {
				keyMensagem = "MENSAGEM_SUCESSO_DESATIVAR_PROCEDIMENTO_CIRURGICO";
			}
			
			this.apresentarMsgNegocio(Severity.INFO, 
					keyMensagem, procedimentoCirurgico.getDescricao());			
		} catch (BaseException e) {
			// Desfaz alteração da situação
			procedimentoCirurgico.setIndSituacao(DominioSituacao.getInstance((!novaIndSituacao)));
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String abreviar(String str, int maxWidth){
		String abreviado = null;
		if (str != null) {
			abreviado = StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}
	
	public void habilitarBotoesLaterais() {
		habilitaBotoesLaterais = Boolean.TRUE;
	}
	
	public Integer getFiltroSeq() {
		return filtroSeq;
	}

	public void setFiltroSeq(Integer filtroSeq) {
		this.filtroSeq = filtroSeq;
	}

	public String getFiltroDescricao() {
		return filtroDescricao;
	}

	public void setFiltroDescricao(String filtroDescricao) {
		this.filtroDescricao = filtroDescricao;
	}

	public DominioSituacao getFiltroIndSituacao() {
		return filtroIndSituacao;
	}

	public void setFiltroIndSituacao(DominioSituacao filtroIndSituacao) {
		this.filtroIndSituacao = filtroIndSituacao;
	}

	public DominioSimNao getFiltroIndGeraImagensPacs() {
		return filtroIndGeraImagensPacs;
	}

	public void setFiltroIndGeraImagensPacs(DominioSimNao filtroIndGeraImagensPacs) {
		this.filtroIndGeraImagensPacs = filtroIndGeraImagensPacs;
	}

	public DominioSimNao getFiltroIndInteresseCcih() {
		return filtroIndInteresseCcih;
	}

	public void setFiltroIndInteresseCcih(DominioSimNao filtroIndInteresseCcih) {
		this.filtroIndInteresseCcih = filtroIndInteresseCcih;
	}

	public Integer getSeqSelecionado() {
		return seqSelecionado;
	}

	public void setSeqSelecionado(Integer seqSelecionado) {
		this.seqSelecionado = seqSelecionado;
	}

	public Boolean getExibeBotaoNovo() {
		return exibeBotaoNovo;
	}

	public void setExibeBotaoNovo(Boolean exibeBotaoNovo) {
		this.exibeBotaoNovo = exibeBotaoNovo;
	}

	public Boolean getHabilitaBotoesLaterais() {
		return habilitaBotoesLaterais;
	}

	public void setHabilitaBotoesLaterais(Boolean habilitaBotoesLaterais) {
		this.habilitaBotoesLaterais = habilitaBotoesLaterais;
	}

	public DynamicDataModel<MbcProcedimentoCirurgicos> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcProcedimentoCirurgicos> dataModel) {
	 this.dataModel = dataModel;
	}
}
