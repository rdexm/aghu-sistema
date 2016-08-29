package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmTextoPadraoLaudo;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterJustificativaLaudosController extends ActionController {

	private static final long serialVersionUID = 389846195098687184L;
	private static final String PAGE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";
	private static final Log LOG = LogFactory.getLog(ManterJustificativaLaudosController.class);
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB 
	private IPacienteFacade pacienteFacade;
	
	@EJB 
	private IAghuFacade aghuFacade;
	
	@Inject 
	private PesquisaPacienteController pesquisaPacienteController;
	
	private Integer prontuario;
	
	private AinLeitos leito;
	
	private AghUnidadesFuncionais unidadeFuncional;
	
	private AinQuartos quarto;
	
	private AghAtendimentos atendimento;
	
	private AipPacientes paciente;
	private Integer codPac;
	private Integer pacCodigoFonetica;
	
	private List<MpmLaudo> listaLaudos = new ArrayList<MpmLaudo>(0);
	
	private MpmLaudo laudoEmEdicao;//Laudo que esta tendo sua justificativa editada

	private MpmLaudo laudoEmEdicaoClone;//Clone do Laudo que esta tendo sua justificativa editada...
										//utilizado para a journal que está implementada na trigger MPMT_LAD_ARU. 
	
	private String justificativaEmEdicao;//Justificativa que esta tendo editada
	
	private Integer atendimentoSeq;
	
	private List<MpmTextoPadraoLaudo> listaTextoPadraoLaudo = new ArrayList<MpmTextoPadraoLaudo>(0);

	private Map<MpmTextoPadraoLaudo, Boolean> textosPadraoSelecionados = new HashMap<MpmTextoPadraoLaudo, Boolean>();

	private boolean desabilitarBotaoImprimir;
	
	private boolean hideModal;
	
	private String voltarPara;
	
	private Integer atendimentoSeqAux;

	private enum ManterJustificativaLaudosControllerExceptionCode implements BusinessExceptionCode {
		JUSTIFICATIVA_LAUDO_EDITADA_COM_SUCESSO, 
		MENSAGEM_ERRO_EDITAR_LAUDO,//Esta mensagem de erro é utilizada ao ocorrer algum problema na clonagem do laudo em edicao para executar o journal.
		JUSTIFICATIVA_EM_BRANCO
		, MENSAGEM_DADOS_MINIMOS_CONSULTA_PACIENTE
		, AIP_PACIENTE_NAO_ENCONTRADO 
		, ERRO_PRESCRICAO_ENFERMAGEM_PACIENTE_NAO_INTERNADO
	}
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	
	public void inicio() {
	 

		this.inicio(false);
	
	}
	
	public void inicio(boolean origemModal) {
		// Busca as informações do paciente caso já tenha sido feita a pesquisa fonética 
    	if (codPac != null || prontuario != null || pacCodigoFonetica != null) {
    		obterInformacoesPaciente(true);
    	}
    	
		if (atendimentoSeq != null) {
			this.setAtendimento(this.aghuFacade.obterAghAtendimentoPorChavePrimaria(atendimentoSeq));
			this.setPaciente(this.getAtendimento().getPaciente());
			this.setLeito(this.getAtendimento().getLeito());
			this.setQuarto(this.getAtendimento().getQuarto());
			this.setUnidadeFuncional(this.getAtendimento().getUnidadeFuncional());
			this.setAtendimentoSeqAux(atendimentoSeq);
			this.listarLaudos(origemModal);
		} 
		this.desabilitarBotaoImprimir = true;
		this.hideModal = true;
	}

	public String redirecionarPesquisaFonetica(){		
		this.pesquisaPacienteController.setCameFrom("manterJustificativaLaudos");
		this.pesquisaPacienteController.setExibeBotaoEditar(false);
		this.pesquisaPacienteController.setParamExibeBotaoIncluir(false);
		return PAGE_PESQUISA_PACIENTE_COMPONENTE;
	}
	
	/**
	 * Obtem as informações do paciente (nome, codigo e prontuario) e retorna se foi possivel obter
	 * 
	 * @return true caso consiga obter as informações, false caso contrário
	 */
	private boolean obterInformacoesPaciente(boolean inicio) {
				
		if (codPac == null && prontuario == null && pacCodigoFonetica == null) {
			this.apresentarMsgNegocio(Severity.ERROR, ManterJustificativaLaudosControllerExceptionCode.MENSAGEM_DADOS_MINIMOS_CONSULTA_PACIENTE.toString());
			
		} else if (prontuario != null && !inicio) {
    		paciente = pacienteFacade.obterPacientePorProntuario(prontuario);
    		if(paciente != null){	
    			codPac = paciente.getCodigo();
    			listarLaudos();
    		} else {
    			this.apresentarMsgNegocio(Severity.ERROR, ManterJustificativaLaudosControllerExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO.toString());   
    			return false;
    		}
    		
    		return true;
    	}
		else if (codPac != null  && !inicio) {
    		
			paciente = pacienteFacade.obterPacientePorCodigo(codPac);
			
			if(paciente != null){
				codPac = paciente.getCodigo();
				prontuario = paciente.getProntuario();
				listarLaudos();
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, ManterJustificativaLaudosControllerExceptionCode.AIP_PACIENTE_NAO_ENCONTRADO.toString()); 
    			return false;
			}
    		    		
    		return true;
    		
    	} else if (pacCodigoFonetica != null && inicio) {
			paciente = pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			codPac = paciente.getCodigo();
			prontuario = paciente.getProntuario();
			listarLaudos();
		}
		return false;
	}
	
	public void listarLaudos(){
		listarLaudos(false);
		
	}
	
	public void listarLaudos(boolean origemModal){
		if(paciente != null){
			
			if (!origemModal || atendimento == null){
				atendimento = prescricaoMedicaFacade.obterAtendimentoPorProntuarioLeito(paciente.getProntuario());
			}

			if(atendimento != null){
				this.setAtendimento(atendimento);
				this.setLeito(atendimento.getLeito());
				this.setQuarto(atendimento.getQuarto());
				this.setUnidadeFuncional(atendimento.getUnidadeFuncional());
			
				this.listaLaudos = prescricaoMedicaFacade.listarLaudosPorAtendimento(this.atendimento.getSeq());
				if(this.listaLaudos != null && !this.listaLaudos.isEmpty()){
					this.desabilitarBotaoImprimir = false;
				}		
			} else {
				apresentarMsgNegocio(Severity.ERROR, 						
						ManterJustificativaLaudosControllerExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_PACIENTE_NAO_INTERNADO.toString());
				limparCampos();
			}
			
		} else {
			limparCampos();
		}
	}
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			if (paciente != null) {
				listarLaudos();	
			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}	
	public String imprimirReport() {
//	<redirect view-id="/prescricaomedica/relatorios/RelatorioLaudosProcSusPdf.xhtml" >
//		<param name="asu_apa_atd_seq" value="#{manterJustificativaLaudosController.atendimento.seq}"/>
//		<!-- 
//		<param name="asu_apa_seq" value="#{manterJustificativaLaudosController.}"/>
//			<param name="asu_seqp" value="#{manterJustificativaLaudosController.}"/>
//		 	-->
//	</redirect> 
		return "redirecionaImprimir";//TODO Aguardar migração do RelatorioLaudosProcSusPdf
	}
	
	public void editarJustificativaLaudo(MpmLaudo laudo){
		try{
			this.laudoEmEdicao = laudo;
			
			this.laudoEmEdicaoClone =   (MpmLaudo)BeanUtils.cloneBean(this.laudoEmEdicao);//Clonando o laudo para usar na journal ao atualizar.
			
			this.justificativaEmEdicao = laudo.getJustificativa();
			
			this.listaTextoPadraoLaudo = prescricaoMedicaFacade.listarTextoPadraoLaudosPorLaudo(laudo.getSeq());
			
			this.textosPadraoSelecionados.clear();
	
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			ApplicationBusinessException ex = new ApplicationBusinessException(ManterJustificativaLaudosControllerExceptionCode.MENSAGEM_ERRO_EDITAR_LAUDO);
			apresentarExcecaoNegocio(ex);
		}
	}
	
	public void adicionarTextosNaJustificativa(){
		boolean adicionou = false;
		StringBuilder str = new StringBuilder("");
		if(StringUtils.isNotBlank(this.justificativaEmEdicao)){
			str.append(this.justificativaEmEdicao);
			str.append('\n');
		}
		if(this.listaTextoPadraoLaudo != null && !this.listaTextoPadraoLaudo.isEmpty()){
			for (MpmTextoPadraoLaudo textoPadrao : this.listaTextoPadraoLaudo) {
				if(!this.textosPadraoSelecionados.isEmpty() && this.textosPadraoSelecionados.get(textoPadrao)== true){
					str.append(textoPadrao.getDescricao());
					str.append('\n');
					adicionou = true;
				}
			}
		}
		if(adicionou){
			this.justificativaEmEdicao = str.toString();
		}
	}
	
	public void atribuirJustificativaAoLaudo(){
		this.laudoEmEdicao.setJustificativa(justificativaEmEdicao);
		this.justificativaEmEdicao = "";//limpa
	}
	
	public String gravar(){
		try {
//Validacao retirada por orientacao do analista.			
//			if(justificativaEmEdicao == null || StringUtils.isEmpty(justificativaEmEdicao.trim())){
//				this.hideModal = false;
//				throw new ApplicationBusinessException(ManterJustificativaLaudosControllerExceptionCode.JUSTIFICATIVA_EM_BRANCO);
//			}
			
			this.hideModal = true;
			
			this.laudoEmEdicao.setJustificativa(justificativaEmEdicao);
			this.justificativaEmEdicao = "";//limpa
			
			this.prescricaoMedicaFacade.atualizarLaudo(laudoEmEdicao, laudoEmEdicaoClone);
		
			apresentarMsgNegocio(Severity.INFO, ManterJustificativaLaudosControllerExceptionCode.JUSTIFICATIVA_LAUDO_EDITADA_COM_SUCESSO.toString());
		
		} catch (BaseException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
		} catch(BaseRuntimeException e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.ERROR,	e.getCode().toString());
			
		}
		return null;//Ao clicar no botão gravar, fica na mesma página e se der erro, também fica na mesma página
	}
	
	public void cancelarEdicao(){
		this.hideModal = true;
	}
	
	public String cancelarPesquisa(){
		return voltarPara;
	}

	/**
	 * Usado na suggestion de pronturario
	 */
	public List<AipPacientes> pesquisarPaciente(Object objParam) throws ApplicationBusinessException {
		String strPesquisa = (String) objParam;
		Integer prontuario = null;
		AghAtendimentos atendimento = null;
		if(!StringUtils.isBlank(strPesquisa)){
			prontuario = Integer.valueOf(strPesquisa);
			atendimento = prescricaoMedicaFacade.obterAtendimentoPorProntuario(prontuario);
			if(atendimento != null){
				this.setAtendimento(atendimento);
				this.setLeito(atendimento.getLeito());
				this.setQuarto(atendimento.getQuarto());
				this.setUnidadeFuncional(atendimento.getUnidadeFuncional());
			}
		}
			
		List<AipPacientes> pacientes = new ArrayList<AipPacientes>();
		if(atendimento != null){
			pacientes.add(atendimento.getPaciente());	
		}
		return pacientes; 
	}
	
	public void limparCampos() {
		atendimentoSeq = null;
		this.setPaciente(null);
		prontuario = null;
		codPac = null;
		pacCodigoFonetica = null;
		this.setLeito(null);
		this.setQuarto(null);
		this.setUnidadeFuncional(null);
		this.setAtendimento(null);
		this.listaLaudos.clear();
		this.desabilitarBotaoImprimir = true;
		
	}

	public List<AinLeitos> pesquisarLeito(String objParam) throws ApplicationBusinessException {
		String strPesquisa = (String) objParam;
		if(!StringUtils.isBlank(strPesquisa)){
			strPesquisa = strPesquisa.toUpperCase();
		} else if(this.leito != null){
			strPesquisa = this.leito.getLeitoID(); 
		}
		try{
			AghAtendimentos atendimento = prescricaoMedicaFacade.obterAtendimentoPorLeito(strPesquisa);
			if(atendimento != null){
				this.setAtendimento(atendimento);
				this.setPaciente(atendimento.getPaciente());
				this.setQuarto(atendimento.getQuarto());
				this.setUnidadeFuncional(atendimento.getUnidadeFuncional());
				
			} 
			List<AinLeitos> leitos = new ArrayList<AinLeitos>();
			if(atendimento!=null){
				leitos.add(atendimento.getLeito());	
			}
			return leitos;

		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
//### GETTERs and SETTERs ####
	
	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}
	

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}


	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AinQuartos getQuarto() {
		return quarto;
	}

	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		if(paciente != null){
			this.setCodPac(paciente.getCodigo());
			this.setProntuario(paciente.getProntuario());
		} else {
			this.setCodPac(null);
			this.setProntuario(null);
		}
		this.paciente = paciente;
	}


	public List<MpmLaudo> getListaLaudos() {
		return listaLaudos;
	}


	public void setListaLaudos(List<MpmLaudo> listaLaudos) {
		this.listaLaudos = listaLaudos;
	}


	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}


	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}


	public List<MpmTextoPadraoLaudo> getListaTextoPadraoLaudo() {
		return listaTextoPadraoLaudo;
	}


	public void setListaTextoPadraoLaudo(
			List<MpmTextoPadraoLaudo> listaTextoPadraoLaudo) {
		this.listaTextoPadraoLaudo = listaTextoPadraoLaudo;
	}


	public Map<MpmTextoPadraoLaudo, Boolean> getTextosPadraoSelecionados() {
		return textosPadraoSelecionados;
	}


	public void setTextosPadraoSelecionados(
			Map<MpmTextoPadraoLaudo, Boolean> textosPadraoSelecionados) {
		this.textosPadraoSelecionados = textosPadraoSelecionados;
	}


	public MpmLaudo getLaudoEmEdicao() {
		return laudoEmEdicao;
	}


	public void setLaudoEmEdicao(MpmLaudo laudoEmEdicao) {
		this.laudoEmEdicao = laudoEmEdicao;
	}


	public String getJustificativaEmEdicao() {
		return justificativaEmEdicao;
	}


	public void setJustificativaEmEdicao(String justificativaEmEdicao) {
		this.justificativaEmEdicao = justificativaEmEdicao;
	}


	public MpmLaudo getLaudoEmEdicaoClone() {
		return laudoEmEdicaoClone;
	}


	public void setLaudoEmEdicaoClone(MpmLaudo laudoEmEdicaoClone) {
		this.laudoEmEdicaoClone = laudoEmEdicaoClone;
	}


	public boolean isDesabilitarBotaoImprimir() {
		return desabilitarBotaoImprimir;
	}


	public void setDesabilitarBotaoImprimir(boolean desabilitarBotaoImprimir) {
		this.desabilitarBotaoImprimir = desabilitarBotaoImprimir;
	}


	public boolean isHideModal() {
		return hideModal;
	}


	public void setHideModal(boolean hideModal) {
		this.hideModal = hideModal;
	}


	public String getVoltarPara() {
		return voltarPara;
	}


	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}


	public Integer getCodPac() {
		return codPac;
	}


	public void setCodPac(Integer codPac) {
		this.codPac = codPac;
	}


	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}


	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public Integer getAtendimentoSeqAux() {
		return atendimentoSeqAux;
	}

	public void setAtendimentoSeqAux(Integer atendimentoSeqAux) {
		this.atendimentoSeqAux = atendimentoSeqAux;
	}	
}
