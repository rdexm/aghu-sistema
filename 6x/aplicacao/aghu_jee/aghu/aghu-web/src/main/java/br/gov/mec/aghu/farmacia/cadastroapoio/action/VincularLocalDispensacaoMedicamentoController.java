package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaLocalDispensacaoMdtosId;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

/**
 * Classe responsável por controlar as ações por vincular de
 * local de dispensacao de medicamentos.
 * 
 * by Carlos Leilson
 */


public class VincularLocalDispensacaoMedicamentoController extends ActionController implements ActionPaginator {

	@Inject @Paginator
	private DynamicDataModel<AfaLocalDispensacaoMdtos> dataModel;

	private static final Log LOG = LogFactory.getLog(VincularLocalDispensacaoMedicamentoController.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5581082623859153569L;

	public enum vincularLocalDispensacaoMedicamentoControllerExceptionCode implements
	BusinessExceptionCode {
		VIOLATION_UNIQUE;
	}

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private AfaLocalDispensacaoMdtos localDispensacao;
	private AfaLocalDispensacaoMdtosId localDispensacaoId;
	private AfaMedicamento medicamento;
	private AghUnidadesFuncionais unidadeFuncional;

	private Integer matCodigo;
	private Short seq;
	private Boolean isUpdate;
	private String descricao;
	
	private AghUnidadesFuncionais unidadeFuncionalSolicitante;
	

	@PostConstruct
	protected void inicializar() {
		LOG.info("inicializar");
		this.begin(conversation);
	}
	
	public void iniciarPagina(){
		this.dataModel.setPesquisaAtiva(true);
		if(this.dataModel.getFirst()==0){
			this.dataModel.reiniciarPaginator();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AfaLocalDispensacaoMdtos> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		List<AfaLocalDispensacaoMdtos> result = this.farmaciaApoioFacade.pesqueisarLocalDispensacaoPorUnidades(unidadeFuncionalSolicitante, firstResult, maxResult, orderProperty, asc);
		
		if (result == null){
			result = new ArrayList<AfaLocalDispensacaoMdtos>();
		}
		
		return result;
	}

	@Override
	public Long recuperarCount() {
		return farmaciaApoioFacade.pesqueisarLocalDispensacaoPorUnidadesCount(unidadeFuncionalSolicitante);
	}
	
	/**
	 * Pesquisar por unidades
	 */
	public void pesquisar()  {
		this.dataModel.reiniciarPaginator();
	}	

	/**
	 * Vincular por unidades
	 */
	public void vincularUnidades(){
		try {
			farmaciaApoioFacade.incluirTodosMedicamentoPorUnidades(unidadeFuncionalSolicitante,registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado()));
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(),e);
		}
		this.dataModel.reiniciarPaginator();
	}

	public void excluir(){
		AfaLocalDispensacaoMdtos localDispensacao = this.farmaciaApoioFacade
		.obterLocalDispensacao(new AfaLocalDispensacaoMdtosId(this.getMatCodigo(), this.getSeq()));
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		
		try {
		    if (localDispensacao != null) {
		    	farmaciaApoioFacade.removerLocalDispensacaoMedicamento(localDispensacao, nomeMicrocomputador, new Date());
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_LOCAL_DISPENSACAO");
			} else {
				this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_REMOCAO_LOCAL_DISPENSACAO_INVALIDO");
			}
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		this.dataModel.reiniciarPaginator();
	}	
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCaracteristicas(String strPesquisa) {
		return returnSGWithCount(farmaciaFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(strPesquisa),
				farmaciaFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(strPesquisa));
	}	
	
	public List<AghUnidadesFuncionais> listarFarmaciasAtivasByPesquisa(Object strPesquisa) {

		List<AghUnidadesFuncionais> result2 = farmaciaFacade.listarFarmaciasAtivasByPesquisa(strPesquisa);
		return result2;
	}
	
	public Long listarFarmaciasAtivasByPesquisaCount(Object strPesquisa) {

		Long count2 = farmaciaFacade.listarFarmaciasAtivasByPesquisaCount(strPesquisa);
		return count2;
	}	

	public IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	public void setFarmaciaFacade(IFarmaciaFacade farmaciaFacade) {
		this.farmaciaFacade = farmaciaFacade;
	}

	public IFarmaciaApoioFacade getFarmaciaApoioFacade() {
		return farmaciaApoioFacade;
	}

	public void setFarmaciaApoioFacade(IFarmaciaApoioFacade farmaciaApoioFacade) {
		this.farmaciaApoioFacade = farmaciaApoioFacade;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	public void setRegistroColaboradorFacade(
			IRegistroColaboradorFacade registroColaboradorFacade) {
		this.registroColaboradorFacade = registroColaboradorFacade;
	}

	public AfaLocalDispensacaoMdtos getLocalDispensacao() {
		return localDispensacao;
	}

	public void setLocalDispensacao(AfaLocalDispensacaoMdtos localDispensacao) {
		this.localDispensacao = localDispensacao;
	}

	public AfaLocalDispensacaoMdtosId getLocalDispensacaoId() {
		return localDispensacaoId;
	}

	public void setLocalDispensacaoId(AfaLocalDispensacaoMdtosId localDispensacaoId) {
		this.localDispensacaoId = localDispensacaoId;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalSolicitante() {
		return unidadeFuncionalSolicitante;
	}

	public void setUnidadeFuncionalSolicitante(
			AghUnidadesFuncionais unidadeFuncionalSolicitante) {
		this.unidadeFuncionalSolicitante = unidadeFuncionalSolicitante;
	}

	public DynamicDataModel<AfaLocalDispensacaoMdtos> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaLocalDispensacaoMdtos> dataModel) {
	 this.dataModel = dataModel;
	}
}