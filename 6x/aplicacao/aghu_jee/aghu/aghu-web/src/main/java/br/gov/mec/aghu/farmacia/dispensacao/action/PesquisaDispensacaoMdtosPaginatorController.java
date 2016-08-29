package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;

public class PesquisaDispensacaoMdtosPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -2572059528003442268L;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
		
	private AghAtendimentos aghAtendimentos;	
	private Integer atdSeq;	
	private Integer seqPresc;
	private AghUnidadesFuncionais unidadeSolicitante;
	private String nomePaciente;
	private Date dataInclusaoItem;
	private AfaMedicamento medicamento;	
	private DominioSituacaoDispensacaoMdto situacao;
	private AghUnidadesFuncionais farmacia;	
	private Boolean isLock;
	private String urlBtVoltar;
	private Boolean btVoltar;
	private String etiqueta;
	private Integer matCodigo;
	private MpmPrescricaoMedica prescricaoMedica;
	private Boolean exibirBotaoLimpar = true;
	private String loteCodigo;
	private Short unfSeq;
	private String nomeComputadorRede;
	private AghMicrocomputador microDispensador;
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;
	
	//Atributos do componente de pesquisa de paciente + pesquisa fonética
	//Fazer conforme manterJustificativaLaudos.xhtml mec:pesquisaPaciente
	private Integer codPac;
	private Integer prontuario;
	private AipPacientes paciente;
	private Integer pacCodigoFonetica;
	
	private Boolean indPmNaoEletronica;
	
	private AfaDispensacaoMdtos dispensacaoMdtosSelecionado;
	@Inject @Paginator
	private DynamicDataModel<AfaDispensacaoMdtos> dataModel;
	
	private static final String FARMACIA_MEDICAMENTOS_SITUACAO_DISPENSACAO_LIST = "medicamentosSituacaoDispensacaoList";

	@PostConstruct
	public void init(){
		this.begin(this.conversation);
	}

	public void inicio(){
		
		CodPacienteFoneticaVO codPacVO = codPacienteFonetica.get();
		
		if (codPacVO != null && codPacVO.getCodigo() > 0) {
			this.codPac = codPacVO.getCodigo();
		}
		
		if(codPac != null){
			this.paciente = pacienteFacade.obterPacientePorCodigo(codPac);
		}

		if(prontuario != null){
			this.paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
			
		}
		//Obtém a atendimento pelo id.
		if(atdSeq !=null){
			this.aghAtendimentos = pacienteFacade.obterAtendimento(atdSeq);			
			this.isLock=true;
			if(matCodigo != null){
				medicamento = farmaciaFacade.obterMedicamento(matCodigo);
			}
			if(unfSeq != null){
				farmacia = aghuFacade.obterUnidadeFuncionalPorChavePrimaria(new AghUnidadesFuncionais(unfSeq));
			}
			
		}else{
			if(farmacia == null){
				try {
					nomeComputadorRede = getEnderecoRedeHostRemoto();
					microDispensador = administracaoFacade.obterAghMicroComputadorPorNomeOuIPException(nomeComputadorRede);
					farmacia = farmaciaDispensacaoFacade.getFarmaciaMicroComputador(microDispensador, nomeComputadorRede);
				} catch (UnknownHostException e) {
					apresentarExcecaoNegocio(new ApplicationBusinessException("NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR", Severity.ERROR));
				} catch (ApplicationBusinessException e) {
					//Na versão 4 e 5 o erro de maquina não cadastrada é ignorada e não é exibida na tela. Igualando as versões
					//nada a fazer, simplesmente a farmacia não é alterada
					farmacia = null;
				}
			}
		}
		
		if(urlBtVoltar != null && !urlBtVoltar.isEmpty()){
			btVoltar = true;			
		}
		
		if(exibirBotaoLimpar==null){
			exibirBotaoLimpar=false;			
		}
		
		MpmPrescricaoMedicaId idPres  = new MpmPrescricaoMedicaId(atdSeq, seqPresc);
		prescricaoMedica = prescricaoMedicaFacade.obterMpmPrescricaoMedicaPorChavePrimaria(idPres);
	
	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método de inicialização da tela, executado quando chamada através do fluxo triar/dispensar da "Pesquisa de Pacientes"
	 * @see MedicamentosDispensacaoPaginatorController
	 */
	public void inicializaPaginaRedirecionada(){
		
		this.aghAtendimentos = this.aghuFacade.obterAghAtendimentoPorChavePrimaria(atdSeq);			
		this.isLock=true;
		if(matCodigo != null){
			medicamento = farmaciaFacade.obterMedicamento(matCodigo);
		}
		if(unfSeq != null){
			farmacia = aghuFacade.obterUnidadeFuncionalPorChavePrimaria(new AghUnidadesFuncionais(unfSeq));
		}
		
		MpmPrescricaoMedicaId idPres  = new MpmPrescricaoMedicaId(atdSeq, seqPresc);
		prescricaoMedica = prescricaoMedicaFacade.obterMpmPrescricaoMedicaPorChavePrimaria(idPres);
	}
	
	public void pesquisar(){
		if(prontuario != null){
			paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
		}else{ 
			if(codPac != null){
				paciente = pacienteFacade.obterPacientePorCodigo(codPac);
			}
		}
		codPac = paciente != null ? paciente.getCodigo() : null;
		prontuario = paciente != null ? paciente.getProntuario() : null;
		dataModel.reiniciarPaginator();	
	}	
	
	public void limparPesquisa(){
		this.atdSeq =null;
		this.seqPresc =null;
		this.prescricaoMedica = null;
		this.aghAtendimentos = null;
		this.unidadeSolicitante = null;
		this.codPac = null;
		this.paciente = null;
		this.prontuario = null;
		this.nomePaciente = null;
		this.dataInclusaoItem = null;
		this.medicamento = null;
		this.situacao = null;
		this.farmacia = null;	
		this.etiqueta = null;
		this.isLock=false;
		this.matCodigo=null;
		this.loteCodigo = null;
		this.dataModel.limparPesquisa();
		this.dataModel.setPesquisaAtiva(false);
		this.indPmNaoEletronica = null;
	}
	
	public String voltar() {
		return FARMACIA_MEDICAMENTOS_SITUACAO_DISPENSACAO_LIST;
	}

	// Estória #5387
	@Override
	public Long recuperarCount() {
		this.etiqueta = farmaciaDispensacaoFacade.validarCodigoBarrasEtiqueta(etiqueta);
		if (paciente != null){
			nomePaciente = paciente.getNome();
		}
		return farmaciaDispensacaoFacade.pesquisarItensDispensacaoMdtosCount(
				unidadeSolicitante, prontuario, nomePaciente, dataInclusaoItem,
				medicamento, situacao, farmacia, aghAtendimentos,
				this.etiqueta, prescricaoMedica, loteCodigo, indPmNaoEletronica);
	}
	
	@Override
	public List<AfaDispensacaoMdtos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		this.etiqueta = farmaciaDispensacaoFacade.validarCodigoBarrasEtiqueta(etiqueta);
		if (paciente != null){
			nomePaciente = paciente.getNome();
		}
		
		List<AfaDispensacaoMdtos> result = farmaciaDispensacaoFacade
				.pesquisarItensDispensacaoMdtos(firstResult, maxResult,
						orderProperty, asc, unidadeSolicitante, prontuario,
						nomePaciente, dataInclusaoItem, medicamento, situacao,
						farmacia, aghAtendimentos, this.etiqueta,
						prescricaoMedica, loteCodigo, indPmNaoEletronica);
		return result;
	}

	public List<AghUnidadesFuncionais> pesquisarUnidadeSolicitante(String strParam) {
		List<AghUnidadesFuncionais> result = farmaciaFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicas(strParam);
		return this.returnSGWithCount(result,pesquisarUnidadeSolicitanteCount(strParam));
	}
	
	public Long pesquisarUnidadeSolicitanteCount(String strParam) {
		return farmaciaFacade.pesquisarUnidadesFuncionaisPorCodigoDescricaoAlaAndarECaracteristicasCount(strParam);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFarmacia(String strParam) {

		List<AghUnidadesFuncionais> result = farmaciaDispensacaoFacade.listarFarmaciasAtivasByPesquisa(strParam);
		return this.returnSGWithCount(result,pesquisarUnidadesFarmaciaCount(strParam));
	}
	
	public Long pesquisarUnidadesFarmaciaCount(String strParam) {
		return farmaciaDispensacaoFacade.listarFarmaciasAtivasByPesquisaCount(strParam);
	}
	
	public List<AfaMedicamento> pesquisarMedicamentosPrescritos(
			String strParam) {

		List<AfaMedicamento> result = farmaciaApoioFacade.pesquisarTodosMedicamentos(strParam);
		return this.returnSGWithCount(result,pesquisarMedicamentosPrescritosCount(strParam));
	}
	
	// Fim Estória #5387
	public Long pesquisarMedicamentosPrescritosCount(String strParam) {
		return farmaciaApoioFacade.pesquisarTodosMedicamentosCount(strParam);
	}

	//Getters & Setters
	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
		this.paciente = this.pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
		this.codPac = paciente.getCodigo();
		this.prontuario = paciente.getProntuario();
	}
	
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
	public Integer getCodPac() {
		return codPac;
	}

	public void setCodPac(Integer codPac) {
		this.codPac = codPac;
	}

	public AghAtendimentos getAghAtendimentos() {
		return aghAtendimentos;
	}

	public void setAghAtendimentos(AghAtendimentos aghAtendimentos) {
		this.aghAtendimentos = aghAtendimentos;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getSeqPresc() {
		return seqPresc;
	}

	public void setSeqPresc(Integer seqPresc) {
		this.seqPresc = seqPresc;
	}

	public AghUnidadesFuncionais getUnidadeSolicitante() {
		return unidadeSolicitante;
	}

	public void setUnidadeSolicitante(AghUnidadesFuncionais unidadeSolicitante) {
		this.unidadeSolicitante = unidadeSolicitante;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Date getDataInclusaoItem() {
		return dataInclusaoItem;
	}

	public void setDataInclusaoItem(Date dataInclusaoItem) {
		this.dataInclusaoItem = dataInclusaoItem;
	}

	public AfaMedicamento getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(AfaMedicamento medicamento) {
		this.medicamento = medicamento;
	}

	public DominioSituacaoDispensacaoMdto getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoDispensacaoMdto situacao) {
		this.situacao = situacao;
	}

	public AghUnidadesFuncionais getFarmacia() {
		return farmacia;
	}

	public void setFarmacia(AghUnidadesFuncionais farmacia) {
		this.farmacia = farmacia;
	}

	public Boolean getIsLock() {
		return isLock;
	}

	public void setIsLock(Boolean isLock) {
		this.isLock = isLock;
	}

	public String getUrlBtVoltar() {
		return urlBtVoltar;
	}

	public void setUrlBtVoltar(String urlBtVoltar) {
		this.urlBtVoltar = urlBtVoltar;
	}

	public Boolean getBtVoltar() {
		return btVoltar;
	}

	public void setBtVoltar(Boolean btVoltar) {
		this.btVoltar = btVoltar;
	}

	public String getEtiqueta() {
		return etiqueta;
	}

	public void setEtiqueta(String etiqueta) {
		this.etiqueta = etiqueta;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public Boolean getExibirBotaoLimpar() {
		return exibirBotaoLimpar;
	}

	public void setExibirBotaoLimpar(Boolean exibirBotaoLimpar) {
		this.exibirBotaoLimpar = exibirBotaoLimpar;
	}

	public String getLoteCodigo() {
		return loteCodigo;
	}

	public void setLoteCodigo(String loteCodigo) {
		this.loteCodigo = loteCodigo;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public String getNomeComputadorRede() {
		return nomeComputadorRede;
	}

	public void setNomeComputadorRede(String nomeComputadorRede) {
		this.nomeComputadorRede = nomeComputadorRede;
	}

	public AghMicrocomputador getMicroDispensador() {
		return microDispensador;
	}

	public void setMicroDispensador(AghMicrocomputador microDispensador) {
		this.microDispensador = microDispensador;
	}

	public AfaDispensacaoMdtos getDispensacaoMdtosSelecionado() {
		return dispensacaoMdtosSelecionado;
	}

	public void setDispensacaoMdtosSelecionado(
			AfaDispensacaoMdtos dispensacaoMdtosSelecionado) {
		this.dispensacaoMdtosSelecionado = dispensacaoMdtosSelecionado;
	}

	public DynamicDataModel<AfaDispensacaoMdtos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaDispensacaoMdtos> dataModel) {
		this.dataModel = dataModel;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public Boolean getIndPmNaoEletronica() {
		return indPmNaoEletronica;
	}

	public void setIndPmNaoEletronica(Boolean indPmNaoEletronica) {
		this.indPmNaoEletronica = indPmNaoEletronica;
	}
}