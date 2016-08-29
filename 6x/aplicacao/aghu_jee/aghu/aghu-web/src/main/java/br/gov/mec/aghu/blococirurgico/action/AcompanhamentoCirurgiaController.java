package br.gov.mec.aghu.blococirurgico.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.business.IBlocoCirurgicoPortalPlanejamentoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;


/**
 * @author aghu
 *
 */
public class AcompanhamentoCirurgiaController extends ActionController{

	
	private static final String BT_SELECTED = "bt-selected";

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	
	private static final long serialVersionUID = 1001254663147702012L;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;	
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private MbcCirurgias cirurgia;
	private List<MbcSalaCirurgica> listaSalas;
	private Boolean disabledSelectSalas;
	private Boolean habilitaResultadoPesquisa;
	private AipPacientes paciente;
	
	private Integer crgSeq;
	private String cameFrom;
	private String voltarPara;
	private Boolean edicao = true;
	private String selectedAGND = null;
	private String selectedCHAM = null;
	private String selectedPREP = null;
	private String selectedTRAN = null;
	private String selectedRZDA = null;
	private String selectedRZDASR = null;
	
	private MbcDestinoPaciente destinoPaciente;
	private Date dataHoraSaida = null;
	
	private Boolean apresentaSR = false;
	
	AghParametros parametroDestPaciente = null;
	
	private final String PAG_BLOCO_DETALHAR_CIRURGIA = "detalhaRegistroCirurgia";
		
	public void inicio() {
		//Limpar informação anterior antes de inicializar os botões
		setApresentaSR(false);
		try {
			parametroDestPaciente = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DEST_SALA_REC);
			this.cirurgia = this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(crgSeq, new Enum[] {MbcCirurgias.Fields.PACIENTE, MbcCirurgias.Fields.UNIDADE_FUNCIONAL}, new Enum[] { MbcCirurgias.Fields.ESPECIALIDADE, MbcCirurgias.Fields.AGENDA, MbcCirurgias.Fields.AGENDA_ESPPROCCIRGS});
			AipPacientes paciente = pacienteFacade.obterPacientePorCodigo(cirurgia.getPaciente().getCodigo());
			cirurgia.setPaciente(paciente);
			
			if(cirurgia.getDestinoPaciente() != null) {
			  MbcDestinoPaciente destinoPaciente = getBlocoCirurgicoFacade().obterDestinoPaciente(cirurgia.getDestinoPaciente().getSeq());
			  cirurgia.setDestinoPaciente(destinoPaciente);
			 }
			
			if(cirurgia.getSalaCirurgica() != null){			
			 MbcSalaCirurgica salaCir = getBlocoCirurgicoFacade().obterSalaCirurgicaPorId(cirurgia.getSalaCirurgica().getId());
			 cirurgia.setSalaCirurgica(salaCir);
			}
			
			setListaSalas(getBlocoCirurgicoPortalPlanejamentoFacade().buscarSalasCirurgicasPorUnfSeq(cirurgia.getUnidadeFuncional().getSeq()));
			if (cirurgia.getDestinoPaciente() != null && cirurgia.getDestinoPaciente().getSeq().equals(parametroDestPaciente.getVlrNumerico().byteValue())){
				setApresentaSR(true);
			}
			setarFocusBotaoSituacao();
					
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}
	
	
	private void setarFocusBotaoSituacao() {
		limparBotoes();
		if(DominioSituacaoCirurgia.AGND.equals(cirurgia.getSituacao())){
			selectedAGND = BT_SELECTED;
		}else if(DominioSituacaoCirurgia.CHAM.equals(cirurgia.getSituacao())){
			selectedCHAM = BT_SELECTED;			
		}else if(DominioSituacaoCirurgia.PREP.equals(cirurgia.getSituacao())){
			selectedPREP = BT_SELECTED;
		}else if(DominioSituacaoCirurgia.TRAN.equals(cirurgia.getSituacao())){
			selectedTRAN = BT_SELECTED;
		}else if(DominioSituacaoCirurgia.RZDA.equals(cirurgia.getSituacao())){
			if (cirurgia.getDataEntradaSr() != null && cirurgia.getDataSaidaSr() == null){
				selectedRZDASR = BT_SELECTED;
			}else{
				selectedRZDA = BT_SELECTED;
				setApresentaSR(false);
			}			
		}
	}
	
	public void setarDefaultDataHoraSaida(){
		dataHoraSaida = new Date();
		limparDestinoPaciente();
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesCirurgicas(String objPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade.buscarUnidadesFuncionaisCirurgia(objPesquisa),pesquisarUnidadesCirurgicasCount(objPesquisa));
	}

	public Long pesquisarUnidadesCirurgicasCount(String objPesquisa) {
		return blocoCirurgicoCadastroApoioFacade.contarUnidadesFuncionaisCirurgia(objPesquisa);
	}
	
	public void pesquisarSalasCirurgicas() {
		setListaSalas(getBlocoCirurgicoPortalPlanejamentoFacade().buscarSalasCirurgicasPorUnfSeq(cirurgia.getUnidadeFuncional().getSeq()));	
	}
	
	public void processarBuscaPacientePorCodigo(){
		if(cirurgia.getPaciente().getCodigo() != null){
			setPaciente(pacienteFacade.buscaPaciente(cirurgia.getPaciente().getCodigo()));
		}else{
			setPaciente(pacienteFacade.obterPacientePorProntuario(null));
		}
	}
	
	public void processarBuscaPacientePorProntuario(){
		if(cirurgia.getPaciente().getProntuario() != null){
			setPaciente(pacienteFacade.obterPacientePorProntuario(cirurgia.getPaciente().getProntuario()));
		}else{
			setPaciente(pacienteFacade.obterPacientePorProntuario(null));
		}
	}
	
	public void buscarUltimaVersaoDaCirurgiaDoPaciente(){
		MbcCirurgias cirurgiaTela =  this.getCirurgia();
		
		this.cirurgia = this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(crgSeq, new Enum[] {MbcCirurgias.Fields.PACIENTE, MbcCirurgias.Fields.UNIDADE_FUNCIONAL}, new Enum[] { MbcCirurgias.Fields.ESPECIALIDADE, MbcCirurgias.Fields.AGENDA, MbcCirurgias.Fields.AGENDA_ESPPROCCIRGS, MbcCirurgias.Fields.ATENDIMENTO});
		cirurgia.setPaciente(pacienteFacade.obterPacientePorCodigo(cirurgiaTela.getPaciente().getCodigo()));
	
		if(cirurgiaTela.getDestinoPaciente() != null) {
			MbcDestinoPaciente destinoPaciente = getBlocoCirurgicoFacade().obterDestinoPaciente(cirurgiaTela.getDestinoPaciente().getSeq());
			cirurgia.setDestinoPaciente(destinoPaciente);
		}
	
		//Sala Cirurgia
		if(cirurgiaTela.getSalaCirurgica() != null) {			
			MbcSalaCirurgica salaCir = getBlocoCirurgicoFacade().obterSalaCirurgicaPorId(cirurgiaTela.getSalaCirurgica().getId());
			cirurgia.setSalaCirurgica(salaCir);
		}
		
		cirurgia.setDataFimCirurgia(cirurgiaTela.getDataFimCirurgia());
		cirurgia.setDataInicioCirurgia(cirurgiaTela.getDataInicioCirurgia());
		cirurgia.setDocumentoPaciente(cirurgiaTela.getDocumentoPaciente());
		
	}
	
	public String alterarSituacaoAgendada(){
		buscarUltimaVersaoDaCirurgiaDoPaciente();
		cirurgia.setSituacao(DominioSituacaoCirurgia.AGND);
		selectedAGND = BT_SELECTED;
		return alterarSituacao(false);
		
	}
	
	public String alterarSituacaoChamado(){
		buscarUltimaVersaoDaCirurgiaDoPaciente();
		cirurgia.setSituacao(DominioSituacaoCirurgia.CHAM);
		return  alterarSituacao(false);
	}
	
	public String alterarSituacaoPreparo(){
		buscarUltimaVersaoDaCirurgiaDoPaciente();
		cirurgia.setSituacao(DominioSituacaoCirurgia.PREP);
		String retorno =  alterarSituacao(true);
		return retorno;
	}
	
	public String alterarSituacaoTransOperatorio() throws BaseException{
		String msg = " ";
		try {
			buscarUltimaVersaoDaCirurgiaDoPaciente();
			blocoCirurgicoFacade.validarSeExisteCirurgiatransOperatorio(cirurgia);
			cirurgia.setSituacao(DominioSituacaoCirurgia.TRAN);
			msg = blocoCirurgicoFacade.gravarMbcCirurgias(cirurgia);
			apresentarMsgNegocio(Severity.INFO, msg);			
			
		}catch (ApplicationBusinessException e) {			
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		this.setarFocusBotaoSituacao();	
		return null;
	} 
	
	public void limparBotoes() {
		selectedAGND = null;
		selectedCHAM = null;
		selectedPREP = null;
		selectedTRAN = null;
		selectedRZDA = null;
		selectedRZDASR = null;
	}
	
	public void limparDestinoPaciente() {
		destinoPaciente = null;
	}
	
	public void	limparSalasCirurgicas() {
		listaSalas = new ArrayList<MbcSalaCirurgica>();
	}

	public void gravar(){
		this.buscarUltimaVersaoDaCirurgiaDoPaciente();
		gravarAcompanhamentoCirurgia(false);
	}
	
	public String alterarSituacao(Boolean inserirAtendimento){
		gravarAcompanhamentoCirurgia(inserirAtendimento);
		return null;
	}

	public String gravarAcompanhamentoCirurgia(Boolean inserirAtendimento){
		String msg = " ";
		try {
			
			//#37148 - Acompanhar Cirurgias - Permitir que se altere o horário de início e fim da cirurgia na tela que faz o acompanhamento
			if(DateUtil.validaDataMaior(cirurgia.getDataEntradaSala(), cirurgia.getDataInicioCirurgia())){
				cirurgia.setDataEntradaSala(cirurgia.getDataInicioCirurgia());
			}

			msg = blocoCirurgicoFacade.gravarAcompanhamentoMbcCirurgias(cirurgia, inserirAtendimento);
//			msg = blocoCirurgicoFacade.gravarMbcCirurgias(cirurgia);
			apresentarMsgNegocio(Severity.INFO, msg);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			//blocoCirurgicoFacade.refresh(cirurgia);
			return null;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		
		setarFocusBotaoSituacao();
		return msg;
	}
	public String alterarSituacaoCirurgia(){
		
		buscarUltimaVersaoDaCirurgiaDoPaciente();
		cirurgia.setDestinoPaciente(destinoPaciente);
		cirurgia.setSituacao(DominioSituacaoCirurgia.RZDA);
		
		apresentaSR = false;
		
		String labelDestino = getBundle().getString("LABEL_DESTINO");
		if(destinoPaciente == null){
			apresentarMsgNegocio("destinoPaciente", Severity.ERROR, "CAMPO_OBRIGATORIO",labelDestino);			
			openDialog("modalSelecaoDestinoWG");
		}else{
			String retornoAlteracao = gravarAcompanhamentoCirurgia(false);
			if(retornoAlteracao != null){
				if (destinoPaciente.getSeq().equals(parametroDestPaciente.getVlrNumerico().byteValue())){
					setApresentaSR(true);
				}
				destinoPaciente = null;
				return null;
			}else{//Ocorreu algum erro
				destinoPaciente = null;
				openDialog("modalSelecaoDestinoWG");
			}
		}
		return null;
	}
	
	public String alterarSituacaoSalaRecuperacao(){
		cirurgia.setDestinoPaciente(destinoPaciente);
		cirurgia.setDataSaidaSr(dataHoraSaida);
		boolean erro = false;
		String labelDestino = getBundle().getString("LABEL_DESTINO");
		String labelDataHoraSaida = getBundle().getString("LABEL_DESTINO_DATA_HORA_SAIDA");
		if(dataHoraSaida == null){				
			apresentarMsgNegocio("dataHoraSaida", Severity.ERROR, "CAMPO_OBRIGATORIO", labelDataHoraSaida);
			erro = true;
		}
		if(destinoPaciente == null){
			apresentarMsgNegocio("destinoPaciente", Severity.ERROR, "CAMPO_OBRIGATORIO", labelDestino);
			erro = true;
		}
		if (!erro){
			return alterarSituacao(false);
		}else{
			openDialog("modalRealizaSaidaSalaRecuperacaoWG");
			return null;
		}
	}
	
	public List<MbcDestinoPaciente> pesquisarDestinoPacienteAtivoPorSeqOuDescricao(String objPesquisa){
		return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarDestinoPacienteAtivoPorSeqOuDescricao(objPesquisa, true, null, MbcDestinoPaciente.Fields.DESCRICAO,  MbcDestinoPaciente.Fields.SEQ),pesquisarDestinoPacienteAtivoPorSeqOuDescricaoCount(objPesquisa));
	}
	
	
	public Long pesquisarDestinoPacienteAtivoPorSeqOuDescricaoCount(String objPesquisa){
		return blocoCirurgicoFacade.pesquisarDestinoPacienteAtivoPorSeqOuDescricaoCount(objPesquisa, null);
	}
	
	public List<MbcDestinoPaciente> pesquisarDestinoPacienteAtivoPorSeqOuDescricaoSemSR(String objPesquisa){
		return this.returnSGWithCount(blocoCirurgicoFacade
				.pesquisarDestinoPacienteAtivoPorSeqOuDescricao(objPesquisa,
						true, parametroDestPaciente.getVlrNumerico().byteValue(),
						MbcDestinoPaciente.Fields.DESCRICAO,
						MbcDestinoPaciente.Fields.SEQ),pesquisarDestinoPacienteAtivoPorSeqOuDescricaoSemSRCount(objPesquisa));
	}
	
	
	public Long pesquisarDestinoPacienteAtivoPorSeqOuDescricaoSemSRCount(String objPesquisa){
		return blocoCirurgicoFacade.pesquisarDestinoPacienteAtivoPorSeqOuDescricaoCount(objPesquisa, parametroDestPaciente.getVlrNumerico().byteValue());
	}
	
	public String voltar() {
		return voltarPara;
	}
	
	public String redirecionaDetalharCirurgia(){
		return PAG_BLOCO_DETALHAR_CIRURGIA;
	}
	
	//Get e Set
	
	public IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	public void setAghuFacade(IAghuFacade aghuFacade) {
		this.aghuFacade = aghuFacade;
	}

	public void setDisabledSelectSalas(Boolean disabledSelectSalas) {
		this.disabledSelectSalas = disabledSelectSalas;
	}

	public Boolean getDisabledSelectSalas() {
		return disabledSelectSalas;
	}

	public void setHabilitaResultadoPesquisa(Boolean habilitaResultadoPesquisa) {
		this.habilitaResultadoPesquisa = habilitaResultadoPesquisa;
	}

	public Boolean getHabilitaResultadoPesquisa() {
		return habilitaResultadoPesquisa;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public String getCameFrom() {
		return cameFrom;
	}

	public void setCameFrom(String cameFrom) {
		this.cameFrom = cameFrom;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	private IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}


	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}


	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setBlocoCirurgicoCadastroApoioFacade(
			IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade) {
		this.blocoCirurgicoCadastroApoioFacade = blocoCirurgicoCadastroApoioFacade;
	}

	public IBlocoCirurgicoCadastroApoioFacade getBlocoCirurgicoCadastroApoioFacade() {
		return blocoCirurgicoCadastroApoioFacade;
	}

	public void setBlocoCirurgicoPortalPlanejamentoFacade(
			IBlocoCirurgicoPortalPlanejamentoFacade blocoCirurgicoPortalPlanejamentoFacade) {
		this.blocoCirurgicoPortalPlanejamentoFacade = blocoCirurgicoPortalPlanejamentoFacade;
	}

	public IBlocoCirurgicoPortalPlanejamentoFacade getBlocoCirurgicoPortalPlanejamentoFacade() {
		return blocoCirurgicoPortalPlanejamentoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public void setDataHoraSaida(Date dataHoraSaida) {
		this.dataHoraSaida = dataHoraSaida;
	}

	public Date getDataHoraSaida() {
		return dataHoraSaida;
	}

	public void setDestinoPaciente(MbcDestinoPaciente destinoPaciente) {
		this.destinoPaciente = destinoPaciente;
	}

	public MbcDestinoPaciente getDestinoPaciente() {
		return destinoPaciente;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public String getSelectedAGND() {
		return selectedAGND;
	}

	public String getSelectedCHAM() {
		return selectedCHAM;
	}

	public String getSelectedPREP() {
		return selectedPREP;
	}

	public String getSelectedTRAN() {
		return selectedTRAN;
	}

	public String getSelectedRZDA() {
		return selectedRZDA;
	}

	public String getSelectedRZDASR() {
		return selectedRZDASR;
	}

	public void setSelectedAGND(String selectedAGND) {
		this.selectedAGND = selectedAGND;
	}

	public void setSelectedCHAM(String selectedCHAM) {
		this.selectedCHAM = selectedCHAM;
	}

	public void setSelectedPREP(String selectedPREP) {
		this.selectedPREP = selectedPREP;
	}

	public void setSelectedTRAN(String selectedTRAN) {
		this.selectedTRAN = selectedTRAN;
	}

	public void setSelectedRZDA(String selectedRZDA) {
		this.selectedRZDA = selectedRZDA;
	}

	public void setSelectedRZDASR(String selectedRZDASR) {
		this.selectedRZDASR = selectedRZDASR;
	}

	public void setListaSalas(List<MbcSalaCirurgica> listaSalas) {
		this.listaSalas = listaSalas;
	}
	
	public List<MbcSalaCirurgica> getListaSalas() {
		return listaSalas;
	}
	
	public void setApresentaSR(Boolean apresentaSR) {
		this.apresentaSR = apresentaSR;
	}

	public Boolean getApresentaSR() {
		return apresentaSR;
	}
}