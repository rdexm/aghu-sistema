package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class EspecialidadePaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5263932127621158790L;

	private final String PAGE_CADASTRAR_ESPECIALIDADE = "especialidadeCRUD";
	private final String PAGE_PESQUISAR_ESPECIALIDADE = "especialidadeList";
	
	@EJB
	private IAghuFacade aghuFacade;
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private Short codigoEspecialidade;
	private String nomeEspecialidade;
	private String siglaEspecialidade;
	private DominioSituacao situacao;
	private AghEspecialidades espGenericaPesq;
	private AghEspecialidades especialidade;
	private FccCentroCustos centroCustoPesq;
	private AghClinicas clinicaPesq;
	
	@Inject @Paginator
	private DynamicDataModel<AghEspecialidades> dataModel;	
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public List<AghEspecialidades> pesquisaEspGenerica(String parametro) {
		return this.returnSGWithCount(cadastrosBasicosInternacaoFacade.pesquisarEspecialidadeGenerica((parametro != null ? parametro : null)),pesquisaEspGenericaCount(parametro));
	}
	
	public Long pesquisaEspGenericaCount(String parametro) {
		return cadastrosBasicosInternacaoFacade.pesquisarEspecialidadeGenericaCount((parametro != null ? parametro : null));
	}
	
	public List<FccCentroCustos> pesquisaCentroCusto(String parametro) {
		String filtro = "";
		if(parametro != null){
			filtro = parametro;
		}
		return this.returnSGWithCount(centroCustoFacade.pesquisarCentroCustosPorCodigoDescricao(filtro),pesquisaCentroCustoCount(parametro));
	}

	public Long pesquisaCentroCustoCount(String filtro) {
		return centroCustoFacade.pesquisarCentroCustosPorCodigoDescricaoCount(filtro != null ? filtro : null);
	}

	public List<AghClinicas> pesquisarClinicas(String paramPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarClinicas(paramPesquisa == null ? null : paramPesquisa),pesquisarClinicasHQLCount(paramPesquisa));
	}

	public Integer pesquisarClinicasHQLCount(String paramPesquisa) {
		return this.aghuFacade.pesquisarClinicasHQLCount(paramPesquisa == null ? null : paramPesquisa);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}	

	public void limparPesquisa() {
		this.espGenericaPesq = null;
		this.especialidade = this.cadastrosBasicosInternacaoFacade.gerarNovaEspecialidade();
		this.centroCustoPesq = null;
		this.clinicaPesq = null;
		this.codigoEspecialidade = null;
		this.nomeEspecialidade = null;
		this.siglaEspecialidade = null;
		this.situacao = null;
		this.dataModel.limparPesquisa();
	}
	
	@Override
	public Long recuperarCount() {
		return this.cadastrosBasicosInternacaoFacade.pesquisarEspecialidadesCount(
				codigoEspecialidade,
				nomeEspecialidade,
				siglaEspecialidade,
				(espGenericaPesq != null ? espGenericaPesq.getSeq() : null),
				(centroCustoPesq != null ? centroCustoPesq.getCodigo() : null), 
				(clinicaPesq != null ? clinicaPesq.getCodigo() : null), 
				situacao);
	}

	@Override
	public List<AghEspecialidades> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		return this.cadastrosBasicosInternacaoFacade.pesquisarEspecialidades(
					firstResult, maxResult, orderProperty, asc,
					codigoEspecialidade, nomeEspecialidade,
					siglaEspecialidade, 
					(espGenericaPesq != null ? espGenericaPesq.getSeq() : null),
					(centroCustoPesq != null ? centroCustoPesq.getCodigo() : null), 
					(clinicaPesq != null ? clinicaPesq.getCodigo() : null), situacao);
	}
	
	public String novo() {
		begin(conversation);
		especialidade = this.cadastrosBasicosInternacaoFacade.gerarNovaEspecialidade();
		return PAGE_CADASTRAR_ESPECIALIDADE;
	}	
	
	public String editar() {
		begin(conversation);
		return PAGE_CADASTRAR_ESPECIALIDADE;
	}
	
	public String cancelar() {
		return PAGE_PESQUISAR_ESPECIALIDADE;
	}

	public Short getCodigoEspecialidade() {
		return codigoEspecialidade;
	}

	public void setCodigoEspecialidade(Short codigoEspecialidade) {
		this.codigoEspecialidade = codigoEspecialidade;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public AghEspecialidades getEspGenericaPesq() {
		return espGenericaPesq;
	}

	public void setEspGenericaPesq(AghEspecialidades espGenericaPesq) {
		this.espGenericaPesq = espGenericaPesq;
	}

	public FccCentroCustos getCentroCustoPesq() {
		return centroCustoPesq;
	}

	public void setCentroCustoPesq(FccCentroCustos centroCustoPesq) {
		this.centroCustoPesq = centroCustoPesq;
	}

	public AghClinicas getClinicaPesq() {
		return clinicaPesq;
	}

	public void setClinicaPesq(AghClinicas clinicaPesq) {
		this.clinicaPesq = clinicaPesq;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DynamicDataModel<AghEspecialidades> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghEspecialidades> dataModel) {
		this.dataModel = dataModel;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}
}