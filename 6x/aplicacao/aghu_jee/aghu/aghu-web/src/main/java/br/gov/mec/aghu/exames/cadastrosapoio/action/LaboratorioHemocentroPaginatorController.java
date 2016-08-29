package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;


public class LaboratorioHemocentroPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 6372212326186253945L;

	private static final String LABORATORIO_HEMOCENTRO_CRUD = "laboratorioHemocentroCRUD";

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	private Short convenioId;
	private Byte planoId;
	private AelLaboratorioExternos filtros = new AelLaboratorioExternos();
	
	@Inject @Paginator
	private DynamicDataModel<AelLaboratorioExternos> dataModel;
	
	private AelLaboratorioExternos selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void excluir() {
		try {
			
			this.cadastrosApoioExamesFacade.removerLaboratorioExterno(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_LABORATORIO_EXTERNO", selecionado.getNome());
			
		} catch (BaseListException ex) {
			apresentarExcecaoNegocio(ex);
			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		return LABORATORIO_HEMOCENTRO_CRUD;
	}
	
	public String editar(){
		return LABORATORIO_HEMOCENTRO_CRUD;
	}
	
	@Override
	public List<AelLaboratorioExternos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.cadastrosApoioExamesFacade.pesquisarLaboratorioHemocentro(filtros, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	public Long recuperarCount() {
		return this.cadastrosApoioExamesFacade.countLaboratorioHemocentro(filtros);
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		this.filtros = new AelLaboratorioExternos();
		dataModel.limparPesquisa();
		convenioId = null;
		planoId = null;
	}
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String filtro) {
		return faturamentoApoioFacade.pesquisarConvenioSaudePlanos((String)filtro);
	}
	
	public void atribuirPlano() {
		if (filtros != null && filtros.getConvenio() != null) {
			this.convenioId = filtros.getConvenio().getId().getCnvCodigo();
			this.planoId = filtros.getConvenio().getId().getSeq();
		} else {
			this.convenioId = null;
			this.planoId = null;
		}
	}
	
	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			filtros.setConvenio(this.faturamentoApoioFacade.obterPlanoPorId(this.planoId, this.convenioId));		
		}
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

	public AelLaboratorioExternos getFiltros() {
		return filtros;
	}

	public void setFiltros(AelLaboratorioExternos filtros) {
		this.filtros = filtros;
	}

	public DynamicDataModel<AelLaboratorioExternos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelLaboratorioExternos> dataModel) {
		this.dataModel = dataModel;
	}

	public AelLaboratorioExternos getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AelLaboratorioExternos selecionado) {
		this.selecionado = selecionado;
	}
}
