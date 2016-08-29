package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.AelDadosCadaveres;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroCadaverPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 1261995388391265335L;

	private static final String CADASTRO_CADAVER = "cadastroCadaver";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	private Short convenioId;
	private Byte planoId;
	
	
	private AelDadosCadaveres filtros = new AelDadosCadaveres();
	
	private Integer seqExclusao;
	
	
	@Inject @Paginator
	private DynamicDataModel<AelDadosCadaveres> dataModel;
	
	private AelDadosCadaveres selecionado;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void excluir() {

		try {
			this.cadastrosApoioExamesFacade.excluirAelDadosCadaveres(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_DADOS_CADAVER", selecionado.getNome());
		} catch (BaseListException ex) {
			apresentarExcecaoNegocio(ex);		
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return CADASTRO_CADAVER;
	}
	
	public String editar(){
		return CADASTRO_CADAVER;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		filtros = new AelDadosCadaveres();
		this.convenioId = null;
		this.planoId = null;
	}

	@Override
	public List<AelDadosCadaveres> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.cadastrosApoioExamesFacade.obterAelDadosCadaveresList(filtros, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return this.cadastrosApoioExamesFacade.obterAelDadosCadaveresListCount(filtros);
	}
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String)filtro);
	}
	
	public void atribuirPlano() {
		if (filtros != null && filtros.getConvenioSaudePlano() != null) {
			this.convenioId = filtros.getConvenioSaudePlano().getId().getCnvCodigo();
			this.planoId = filtros.getConvenioSaudePlano().getId().getSeq();
		} else {
			this.convenioId = null;
			this.planoId = null;
		}
	}
	
	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			filtros.setConvenioSaudePlano(this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId));		
		}
	}

	public AelDadosCadaveres getFiltros() {
		return filtros;
	}

	public void setFiltros(AelDadosCadaveres filtros) {
		this.filtros = filtros;
	}

	public Integer getSeqExclusao() {
		return seqExclusao;
	}

	public void setSeqExclusao(Integer seqExclusao) {
		this.seqExclusao = seqExclusao;
	}

	public Short getConvenioId() {
		return convenioId;
	}

	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	public Byte getPlanoId() {
		return planoId;
	}

	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	public DynamicDataModel<AelDadosCadaveres> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelDadosCadaveres> dataModel) {
		this.dataModel = dataModel;
	}

	public AelDadosCadaveres getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelDadosCadaveres selecionado) {
		this.selecionado = selecionado;
	}
}