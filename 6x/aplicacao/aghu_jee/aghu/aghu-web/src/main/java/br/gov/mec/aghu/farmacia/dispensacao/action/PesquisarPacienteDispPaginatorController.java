package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioCoresPacientesTriagem;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.VAfaPrcrDispMdtos;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;


public class PesquisarPacienteDispPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = -8913134700455418041L;
	
	private Date dataReferencia;
	private AghUnidadesFuncionais farmacia;
	private AghUnidadesFuncionais unidadeFuncionalPrescricao;
	private AinLeitos leito;
	private AinQuartos quarto;
	private AipPacientes paciente;
	
	private DominioCoresPacientesTriagem dominioCoresSituacao;
	
	private Boolean refreshPesquisa;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	private Integer atdSeqPrescricaoMedica;
	private Integer seqPrescricaoMedica;
	
	private Boolean userLogadoPossuiMicroComputador;
	private String msgErroUserLogado;
	
	private String nomeComputadorRede;
	private AghMicrocomputador microDispensador; 

	@Inject @Paginator
	private DynamicDataModel<VAfaPrcrDispMdtos> dataModel;
	
	@Inject
	private RealizarTriagemMedPrescrPaginatorController realizarTriagemMedPrescrPaginatorController;
	@Inject
	private DispMdtosCbPaginatorController dispMdtosCbPaginatorController;
	
	public void iniciarPagina(){
	 

		begin(conversation);
		dominioCoresSituacao = DominioCoresPacientesTriagem.AMARELO;
		
		try {
			nomeComputadorRede = getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException("NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR", Severity.ERROR));
		}

		if(dataReferencia ==null) {
			dataReferencia = DateUtil.hojeSeNull(null);
		}
		if(refreshPesquisa == null) {
			refreshPesquisa = Boolean.FALSE;
		}
		
		if(userLogadoPossuiMicroComputador == null){
			verificaSeComputadorCadastrado();
		}
		
		try {
			if(farmacia == null){
				farmacia = farmaciaDispensacaoFacade.getFarmaciaMicroComputador(microDispensador, nomeComputadorRede);
			}
		} catch (ApplicationBusinessException e) {
			//nada a fazer, simplesmente a farmacia não é alterada
			farmacia = null;
		}
	
	}
	
	public List<AghUnidadesFuncionais> pesquisarFarmacias(String strPesquisa){
		return this.returnSGWithCount(farmaciaDispensacaoFacade.listarFarmaciasAtivasByPesquisa(strPesquisa),pesquisarFarmaciasCount(strPesquisa));
	}
	
	public Long pesquisarFarmaciasCount(String strPesquisa){
		return farmaciaDispensacaoFacade.listarFarmaciasAtivasByPesquisaCount(strPesquisa);
	}
	
	public List<AinLeitos> pesquisarLeitos(String strPesquisa){
		return this.returnSGWithCount(this.internacaoFacade.obterLeitosAtivosPorUnf(
				strPesquisa,
				unidadeFuncionalPrescricao != null ? unidadeFuncionalPrescricao
						.getSeq() : null),pesquisarLeitosCount(strPesquisa));
	}
	
	public Long pesquisarLeitosCount(String strPesquisa){
		return this.internacaoFacade.obterLeitosAtivosPorUnfCount(
				strPesquisa,
				unidadeFuncionalPrescricao != null ? unidadeFuncionalPrescricao
						.getSeq() : null);
	}

	public List<AghUnidadesFuncionais> pesquisarUnidades(String strPesquisa){
		return this.returnSGWithCount(farmaciaFacade.listarUnidadesPmeInformatizadaByPesquisa(strPesquisa),pesquisarUnidadesCount(strPesquisa));
	}
	
	public Long pesquisarUnidadesCount(String strPesquisa){
		return farmaciaFacade.listarUnidadesPmeInformatizadaByPesquisaCount(strPesquisa);
	}
	
	public List<AinQuartos> pesquisarQuartos(String strPesquisa){	
		 return this.returnSGWithCount(internacaoFacade.listarQuartosPorUnidadeFuncionalEDescricao((String)strPesquisa, unidadeFuncionalPrescricao != null ? unidadeFuncionalPrescricao
					.getSeq() : null, Boolean.TRUE, new AinQuartos.Fields[]{ AinQuartos.Fields.NUMERO}),pesquisarQuartosCount(strPesquisa));
	}
	
	public Long pesquisarQuartosCount(String strPesquisa){	
		 return internacaoFacade.listarQuartosPorUnidadeFuncionalEDescricaoCount((String)strPesquisa, unidadeFuncionalPrescricao != null ? unidadeFuncionalPrescricao
					.getSeq() : null, Boolean.TRUE);
	}
	
	public List<AipPacientes> pesquisarPacientes(String objPesquisa){
		List<AipPacientes> pacientes = new ArrayList<AipPacientes>();
		String strPesquisa = String.valueOf(objPesquisa);
		if (strPesquisa != null && !strPesquisa.trim().equals("null") && !"".equals(strPesquisa.trim())) {
			strPesquisa = strPesquisa.trim().replaceAll("/", "");
			Integer nroProntuario = Integer.valueOf(strPesquisa);
			AipPacientes pac = this.pacienteFacade.obterPacienteComAtendimentoPorProntuario(nroProntuario);
			if(pac!=null ) {
				pacientes.add(pac);
			}
		}
		return pacientes;
	}
	
	@Override
	public Long recuperarCount() {
		return farmaciaDispensacaoFacade.pesquisarPacientesParaDispensacaoCount(
				dataReferencia, farmacia, unidadeFuncionalPrescricao, leito,
				quarto, paciente);
	}

	@Override
	public List<VAfaPrcrDispMdtos> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		try {
			if(dataReferencia == null) {
				return null;
			} else {
				return farmaciaDispensacaoFacade.pesquisarPacientesParaDispensacao(
						firstResult, maxResult, orderProperty, asc, dataReferencia,farmacia,
						unidadeFuncionalPrescricao, leito, quarto, paciente);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public void efetuarPesquisa(){
		dataModel.reiniciarPaginator();
	}
	
	public void limpar(){
		dataReferencia = DateUtil.hojeSeNull(null);
		farmacia = null;
		unidadeFuncionalPrescricao = null;
		leito = null;
		quarto = null;
		paciente = null;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		dataModel.limparPesquisa();
	}
	
	public void verificaSeComputadorCadastrado(){
		try{
			microDispensador = administracaoFacade.obterAghMicroComputadorPorNomeOuIPException(nomeComputadorRede);
			userLogadoPossuiMicroComputador = Boolean.TRUE;
			msgErroUserLogado = "";
		} catch (ApplicationBusinessException e) {
			userLogadoPossuiMicroComputador = Boolean.FALSE;
			msgErroUserLogado = e.getMessage();
		}
	}
	
	public void exibeMsgComputadorNaoCadastrado(){
		this.apresentarMsgNegocio(Severity.ERROR,msgErroUserLogado);
	}
	
	public String goToRealizarTriagemMdto(VAfaPrcrDispMdtos itemSelecionado) throws ApplicationBusinessException{
		realizarTriagemMedPrescrPaginatorController.setAtdSeqPrescricao(itemSelecionado.getId().getAtdSeq());
		realizarTriagemMedPrescrPaginatorController.setSeqPrescricao(itemSelecionado.getId().getSeq());
		realizarTriagemMedPrescrPaginatorController.setUnfSeq(getFarmacia().getSeq());
		realizarTriagemMedPrescrPaginatorController.setUrlBtVoltar("pesquisarPacientesParaDispensacaoList");
		realizarTriagemMedPrescrPaginatorController.setEncerraConversacaoBtVoltar(Boolean.TRUE);
		realizarTriagemMedPrescrPaginatorController.iniciarPagina();
		return "realizarTriagemMedicamentosPrescricaoList";
	}
	
	public String goToDispensarMedicamento(VAfaPrcrDispMdtos itemSelecionado) throws ApplicationBusinessException{
		dispMdtosCbPaginatorController.setAtdSeqPrescricao(itemSelecionado.getId().getAtdSeq());
		dispMdtosCbPaginatorController.setSeqPrescricao(itemSelecionado.getId().getSeq());
		dispMdtosCbPaginatorController.setUrlBtVoltar("pesquisarPacientesParaDispensacaoList");
		dispMdtosCbPaginatorController.setEncerraConversacaoBtVoltar(Boolean.TRUE);
		dispMdtosCbPaginatorController.iniciarPagina();
		return "dispensacaoMdtosCodBarrasList";
	}
	
	public Date getDataReferencia() {
		return dataReferencia;
	}

	public void setDataReferencia(Date dataReferencia) {
		this.dataReferencia = dataReferencia;
	}

	public AghUnidadesFuncionais getFarmacia() {
		return farmacia;
	}

	public void setFarmacia(AghUnidadesFuncionais farmacia) {
		this.farmacia = farmacia;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalPrescricao() {
		return unidadeFuncionalPrescricao;
	}

	public void setUnidadeFuncionalPrescricao(
			AghUnidadesFuncionais unidadeFuncionalPrescricao) {
		this.unidadeFuncionalPrescricao = unidadeFuncionalPrescricao;
	}

	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public AinQuartos getQuarto() {
		return quarto;
	}

	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
	public String showDispensar(){
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AFAF_DISP_MDTO_SP_DESENV");
		return null;
	}
	public String showTriarFunction2(){
		//Estória #5712
		return "triagemDeMedicamentosDaPrescricao";
	}
	public String showTriarFunction1(){
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_PACIENTE_QUIMIOTERAPICO");
		return null;
	}
	public String showPreparar(){
		if(farmacia==null) {
			this.apresentarMsgNegocio(Severity.INFO, "AFA_01390");
		} else {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AFAF_ESC_PROD_INT_DESENV");
		}
		return null;
	}
	
	public DominioCoresPacientesTriagem[] getDominioCoresSituacaoLista(){
		return this.dominioCoresSituacao.values();
	}

	public Boolean getRefreshPesquisa() {
		return refreshPesquisa;
	}

	public void setRefreshPesquisa(Boolean refreshPesquisa) {
		this.refreshPesquisa = refreshPesquisa;
	}

	public DominioCoresPacientesTriagem getDominioCoresSituacao() {
		return dominioCoresSituacao;
	}

	public void setDominioCoresSituacao(
			DominioCoresPacientesTriagem dominioCoresSituacao) {
		this.dominioCoresSituacao = dominioCoresSituacao;
	}

	public Integer getAtdSeqPrescricaoMedica() {
		return atdSeqPrescricaoMedica;
	}

	public void setAtdSeqPrescricaoMedica(Integer atdSeqPrescricaoMedica) {
		this.atdSeqPrescricaoMedica = atdSeqPrescricaoMedica;
	}

	public Integer getSeqPrescricaoMedica() {
		return seqPrescricaoMedica;
	}

	public void setSeqPrescricaoMedica(Integer seqPrescricaoMedica) {
		this.seqPrescricaoMedica = seqPrescricaoMedica;
	}

	public Boolean getUserLogadoPossuiMicroComputador() {
		return userLogadoPossuiMicroComputador;
	}

	public void setUserLogadoPossuiMicroComputador(
			Boolean userLogadoPossuiMicroComputador) {
		this.userLogadoPossuiMicroComputador = userLogadoPossuiMicroComputador;
	}

	public String getMsgErroUserLogado() {
		return msgErroUserLogado;
	}

	public void setMsgErroUserLogado(String msgErroUserLogado) {
		this.msgErroUserLogado = msgErroUserLogado;
	}

	public void setNomeComputadorRede(String nomeComputadorRede) {
		this.nomeComputadorRede = nomeComputadorRede;
	}

	public String getNomeComputadorRede() {
		return nomeComputadorRede;
	}

	public void setMicroDispensador(AghMicrocomputador microDispensador) {
		this.microDispensador = microDispensador;
	}

	public AghMicrocomputador getMicroDispensador() {
		return microDispensador;
	}

	public DynamicDataModel<VAfaPrcrDispMdtos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<VAfaPrcrDispMdtos> dataModel) {
		this.dataModel = dataModel;
	}
	

}