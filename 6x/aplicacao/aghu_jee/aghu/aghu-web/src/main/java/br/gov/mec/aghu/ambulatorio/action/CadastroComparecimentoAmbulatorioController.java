package br.gov.mec.aghu.ambulatorio.action;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamAtestados;
import br.gov.mec.aghu.model.MamTipoAtestado;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.action.CadastroOutrosAtestadosAmbulatorioController;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class CadastroComparecimentoAmbulatorioController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = -297951711640974927L;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private CadastroAcompanhamentoAmbulatorioController cadastroAcompanhamentoAmbulatorioController;
	
	@Inject
	private CadastroAtestadoFgtsPisPasepAmbulatorioController cadastroAtestadoFgtsPisPasepAmbulatorioControllerController;
	
	@Inject
	private CadastroOutrosAtestadosAmbulatorioController cadastroOutrosAtestadosAmbulatorioController;
	
	@Inject
	private CadastroAtestadoMedicoAmbulatorioController cadastroAtestadoMedicoAmbulatorioController;
	
	@Inject
	private CadastroRenovacaoReceitaAmbulatorioController cadastroRenovacaoReceitaAmbulatorioController;
	
	@Inject
	private CadastroAtestadoMarcacaoAmbulatorioController cadastroAtestadoMarcacaoAmbulatorioController;
	
	//campos cabeçalho
	private AacConsultas consultaSelecionada;
	private String idadeFormatada;
	private String labelZona;
	private AipPacientes paciente;
	
	private Integer numeroConsulta;
	
	private List<MamAtestados> listaAtestados;
	
	private String declaracaoParte1;
	private MamAtestados atestado = new MamAtestados();
	private MamAtestados itemSelecionado;
	private Boolean modoEdicao = Boolean.FALSE;
	private MamTipoAtestado tipoAtestado;
	private Integer indexSelecionado;
	private boolean buscaArvoreCid = false;
	private BigDecimal parametro;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}
	
	public void inicio(){
		try {
			if (!buscaArvoreCid) {
				validaParametrosRequest();
				atestado.setNroVias(Byte.valueOf("1"));
				consultaSelecionada = ambulatorioFacade.obterAacConsultasAtenderPacientesAgendados(numeroConsulta);
				parametro = ambulatorioFacade.obterParametroAtestadoComparecimento();
				tipoAtestado = ambulatorioFacade.obterTipoAtestadoOriginal(parametro.shortValue());
				listaAtestados = ambulatorioFacade.obterAtestadosDaConsulta(getConsultaSelecionada(), parametro.shortValue());
				this.declaracaoParte1 = montarDeclaracaoParte1(this.consultaSelecionada.getPaciente().getNome(), this.consultaSelecionada.getPaciente().getProntuario());
				
		
				//#11943
				cadastroAcompanhamentoAmbulatorioController.setConsultaSelecionada(consultaSelecionada);
				cadastroAcompanhamentoAmbulatorioController.inicio();
				
				//#11944
				cadastroAtestadoMedicoAmbulatorioController.setConsultaSelecionada(consultaSelecionada);
				cadastroAtestadoMedicoAmbulatorioController.inicio();
				
				//#11946
				cadastroAtestadoFgtsPisPasepAmbulatorioControllerController.setConsultaSelecionada(this.consultaSelecionada);
				cadastroAtestadoFgtsPisPasepAmbulatorioControllerController.inicio();
				
				//#11947
				cadastroOutrosAtestadosAmbulatorioController.setConsultaSelecionada(this.consultaSelecionada);
				cadastroOutrosAtestadosAmbulatorioController.inicio();
				
				//#45902
				cadastroRenovacaoReceitaAmbulatorioController.setConsultaSelecionada(consultaSelecionada);
				cadastroRenovacaoReceitaAmbulatorioController.inicio();
				
				//#11945
				cadastroAtestadoMarcacaoAmbulatorioController.setConsultaSelecionada(consultaSelecionada);
				cadastroAtestadoMarcacaoAmbulatorioController.inicio();
				
				this.indexSelecionado = 0;
			} else {
				this.indexSelecionado = 3;
				buscaArvoreCid = false;
			}
		
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void validaParametrosRequest() {
		if(getRequestParameter("numeroConsulta") != null){
			this.numeroConsulta = Integer.valueOf(getRequestParameter("numeroConsulta").trim());
		}
		if(getRequestParameter("idadeFormatada") != null){
			this.idadeFormatada = getRequestParameter("idadeFormatada").trim();
		}
		if( getRequestParameter("labelZona") != null){
			this.labelZona = getRequestParameter("labelZona").trim();
		}
	}
	public String montarDeclaracaoParte1(String nomePaciente, Integer prontuario) {
		return getBundle().getString("MSG_CAD_COMPARECIMENTO_DECLARACAO_P1").concat(nomePaciente)
				.concat(getBundle().getString("MSG_CAD_COMPARECIMENTO_DECLARACAO_P2")).concat(" ").concat(prontuario.toString()).concat(",")
				.concat(" ");
	}
	
	private String declaracaoGrid(String nomePaciente, Integer prontuario){
		return getBundle().getString("MSG_CAD_COMPARECIMENTO_DECLARACAO_P1").concat(nomePaciente)
				.concat(getBundle().getString("MSG_CAD_COMPARECIMENTO_DECLARACAO_P2")).concat(" ").concat(prontuario.toString()).concat(",")
				.concat(getBundle().getString("LABEL_CONSULTOU_NESTE_HOSPITAL"));
	}
	
	public String obterDeclaracaoTruncada(Integer tamanhoMaximo){
		String declaracao = declaracaoGrid(this.consultaSelecionada.getPaciente().getNome(), this.consultaSelecionada.getPaciente().getProntuario());
		if (declaracao.length() > tamanhoMaximo) {
			declaracao = StringUtils.abbreviate(declaracao, tamanhoMaximo);
		}
		return declaracao;
	}
	
	public String declaracaoCompletaGrid(){
		return declaracaoGrid(this.consultaSelecionada.getPaciente().getNome(), this.consultaSelecionada.getPaciente().getProntuario());
	}
	

	public void gravar(){
		try {
		ambulatorioFacade.validarCamposPreenchidosAtestadoComparecimento(atestado);
		ambulatorioFacade.validarValorMinimoNumeroVias(atestado);
		ambulatorioFacade.validarHoraInicioFimAtestado(atestado.getDataInicial(), atestado.getDataFinal());
		boolean novo = atestado.getSeq() == null ? true : false; 
		atestado.setDataInicial(DateUtil.comporDiaHora(atestado.getDthrCons(), atestado.getDataInicial()));
		atestado.setDataFinal(DateUtil.comporDiaHora(atestado.getDthrCons(), atestado.getDataFinal()));
		if(atestado.getSeq() == null){
			atestado.setConsulta(consultaSelecionada);//
			atestado.setAipPacientes(consultaSelecionada.getPaciente());
			RapServidores servidor =servidorLogadoFacade.obterServidorLogado();
			atestado.setServidor(servidor);
			atestado.setServidorValida(servidor);
			atestado.setIndPendente(DominioIndPendenteAmbulatorio.P);
			atestado.setIndImpresso(Boolean.FALSE);
			atestado.setDthrCriacao(new Date());
			atestado.setMamTipoAtestado(tipoAtestado);
		}
			ambulatorioFacade.gravarAtestado(atestado);
			if(novo){
				apresentarMsgNegocio(Severity.INFO, "MSG_ATESTADO_CADASTRADO_SUCESSO");
			} else{
				apresentarMsgNegocio(Severity.INFO, "MSG_ATESTADO_ALTERADO_SUCESSO");
			}
			limpar();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		listaAtestados = ambulatorioFacade.obterAtestadosDaConsulta(getConsultaSelecionada(), parametro.shortValue());
	}
	
	public void limpar(){
		itemSelecionado = null;
		modoEdicao = Boolean.FALSE;
		atestado = new MamAtestados();
		atestado.setNroVias(Byte.valueOf("1"));
	}
	
	public void editar(){
		modoEdicao = Boolean.TRUE;
	}
	
	public boolean editandoRegistro(MamAtestados item){
		if(atestado.getSeq()!=null && atestado.equals(item)){
			return true;
		}
		return false;
	}
	
	public void selecionarItem(MamAtestados item){
		setItemSelecionado(item);
	}
		
	public void excluir(){
		try {
			ambulatorioFacade.excluirAtestadoComparecimento(itemSelecionado);
			apresentarMsgNegocio(Severity.INFO, "MSG_ATENDER_PACIENTES_AGENDADOS_REGISTRO_EXCLUIDO");
			listaAtestados = ambulatorioFacade.obterAtestadosDaConsulta(getConsultaSelecionada(), parametro.shortValue());
			if(modoEdicao){
				setItemSelecionado(atestado);
			}
			//limpar();
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "MSG_ERRO_EXCLUIR_REGISTRO");
		}
		
	}
		
	public void excluir(MamAtestados item){
		setItemSelecionado(item);
		excluir();
	}
	
	public String getLabelZona() {
		return labelZona;
	}

	public void setLabelZona(String labelZona) {
		this.labelZona = labelZona;
	}

	public AacConsultas getConsultaSelecionada() {
		return consultaSelecionada;
	}

	public void setConsultaSelecionada(AacConsultas consultaSelecionada) {
		this.consultaSelecionada = consultaSelecionada;
	}

	public String getIdadeFormatada() {
		return idadeFormatada;
	}

	public void setIdadeFormatada(String idadeFormatada) {
		this.idadeFormatada = idadeFormatada;
	}

	public String getDeclaracaoParte1() {
		return declaracaoParte1;
	}

	public void setDeclaracaoParte1(String declaracaoParte1) {
		this.declaracaoParte1 = declaracaoParte1;
	}

	public MamAtestados getAtestado() {
		return atestado;
	}

	public void setAtestado(MamAtestados atestado) {
		this.atestado = atestado;
	}

	public MamAtestados getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MamAtestados itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}

	public List<MamAtestados> getListaAtestados() {
		return listaAtestados;
	}

	public void setListaAtestados(List<MamAtestados> listaAtestados) {
		this.listaAtestados = listaAtestados;
	}
	
	public Integer getNumeroConsulta() {
		return numeroConsulta;
	}

	public void setNumeroConsulta(Integer numeroConsulta) {
		this.numeroConsulta = numeroConsulta;
	}
	
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
	
	public Integer getIndexSelecionado() {
		return indexSelecionado;
	}

	public void setIndexSelecionado(Integer indexSelecionado) {
		this.indexSelecionado = indexSelecionado;
	}

	public boolean isBuscaArvoreCid() {
		return buscaArvoreCid;
	}

	public void setBuscaArvoreCid(boolean buscaArvoreCid) {
		this.buscaArvoreCid = buscaArvoreCid;
	}
	
}

