package br.gov.mec.aghu.emergencia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.MamCapacidadeAtendVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * @author israel.haas
 */

public class CapacidadeAtendimentoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3735240336688571771L;
	
	private static final String REDIRECIONA_MANTER_CAPACIDADE_ATENDIMENTO = "capacidadeAtendimentoCRUD";

	@Inject
	private IEmergenciaFacade emergenciaFacade;
	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}

	private Especialidade especialidade;
	private DominioSituacao situacao;
	private boolean exibirBotaoIncluir;
	private boolean permManterCapacidadeAtendimento;
	private List<Short> listaEspId;
	private MamCapacidadeAtendVO capacidadeSelecionada;
	@Inject @Paginator
	private DynamicDataModel<MamCapacidadeAtendVO> dataModel;
	
	
	@PostConstruct
	public void init(){
		this.begin(conversation);
		setListaEspId(null);
		
		this.permManterCapacidadeAtendimento = getPermissionService().usuarioTemPermissao(
				obterLoginUsuarioLogado(), "manterCapacidadeAtendimento", "executar");
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.exibirBotaoIncluir = true;
	}

	public void limparPesquisa() {
		this.especialidade = null;
		this.situacao = null;
		this.exibirBotaoIncluir = false;
		this.dataModel.setPesquisaAtiva(false);
	}

	public List<Especialidade> pesquisarEspecialidadeListaSeq(String objPesquisa) {
		setListaEspId(this.emergenciaFacade.pesquisarSeqsEspecialidadesEmergencia(null, DominioSituacao.A));
		
		return returnSGWithCount(this.emergenciaFacade.pesquisarEspecialidadeListaSeq(getListaEspId(), objPesquisa), this.emergenciaFacade.pesquisarEspecialidadeListaSeqCount(getListaEspId(), objPesquisa));		
	}
	
	public Long pesquisarEspecialidadeListaSeqCount(String objPesquisa) {
		return this.emergenciaFacade.pesquisarEspecialidadeListaSeqCount(getListaEspId(), objPesquisa);
	}
	
	// ### Paginação ###

	@Override
	public Long recuperarCount() {
		Short espSeq = this.especialidade != null ? this.especialidade.getSeq() : null;
		DominioSituacao indSituacao = this.situacao != null ? this.situacao : null;
		
		Long count = this.emergenciaFacade.pesquisarCapacidadesAtendsCount(espSeq, indSituacao);

		return count;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MamCapacidadeAtendVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		Short espSeq = this.especialidade != null ? this.especialidade.getSeq() : null;
		DominioSituacao indSituacao = this.situacao != null ? this.situacao : null;
		
		List<MamCapacidadeAtendVO> result = new ArrayList<MamCapacidadeAtendVO>();
		try {
			result = this.emergenciaFacade
					.pesquisarCapacidadesAtends(firstResult, maxResults, orderProperty, asc, espSeq, indSituacao);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return result;
	}
	
	public void reiniciarPaginator() {
		dataModel.reiniciarPaginator();		
	}
	
	public void excluir() {
		this.emergenciaFacade.excluirCapacidadeAtend(this.capacidadeSelecionada);
		this.dataModel.reiniciarPaginator();
		this.apresentarMsgNegocio(Severity.INFO, "EXCLUSAO_CAPACIDADE_ATEND_SUCESSO");
	}
	
	public String editarIncluir() {
		return REDIRECIONA_MANTER_CAPACIDADE_ATENDIMENTO;
	}

	// ### GETs e SETs ###
	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public boolean isExibirBotaoIncluir() {
		return exibirBotaoIncluir;
	}

	public void setExibirBotaoIncluir(boolean exibirBotaoIncluir) {
		this.exibirBotaoIncluir = exibirBotaoIncluir;
	}

	public boolean isPermManterCapacidadeAtendimento() {
		return permManterCapacidadeAtendimento;
	}

	public void setPermManterCapacidadeAtendimento(
			boolean permManterCapacidadeAtendimento) {
		this.permManterCapacidadeAtendimento = permManterCapacidadeAtendimento;
	}

	public List<Short> getListaEspId() {
		return listaEspId;
	}

	public void setListaEspId(List<Short> listaEspId) {
		this.listaEspId = listaEspId;
	}

	public MamCapacidadeAtendVO getCapacidadeSelecionada() {
		return capacidadeSelecionada;
	}

	public void setCapacidadeSelecionada(MamCapacidadeAtendVO capacidadeSelecionada) {
		this.capacidadeSelecionada = capacidadeSelecionada;
	}

	public DynamicDataModel<MamCapacidadeAtendVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MamCapacidadeAtendVO> dataModel) {
		this.dataModel = dataModel;
	}
}
