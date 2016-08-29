package br.gov.mec.aghu.emergencia.perinatologia.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.emergencia.vo.ServidorIdVO;
import br.gov.mec.controller.AutenticacaoController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

public class RegistrarConsultaCOAcoesController extends ActionController {

	private static final long serialVersionUID = 4126313594932214952L;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	@Inject
	private RegistrarConsultaCOController registrarConsultaCOController;
	
	@Inject
	private NotaAdicionalController notaAdicionalController;
	
	@Inject
	private AutenticacaoController autenticacaoController;
		
	private ServidorIdVO servidorIdVO;
	private Integer matricula; 
	private Short vinCodigo; 
	private Integer atendimento; 
	private boolean exibeModalSolicitacaoExame; 
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public boolean moduloAgendamentoExameAtivo(){
		return !emergenciaFacade.moduloAgendamentoExameAtivo();
	}
	
	public void solicitarExame(){
		Integer pacCodigo = registrarConsultaCOController.getPacCodigo();
		Short seqp = registrarConsultaCOController.getSeqp();
		Integer numeroConsulta = registrarConsultaCOController.getNumeroConsulta();
		
		try {
			emergenciaFacade.validarConsultaFinalizada(pacCodigo, seqp, numeroConsulta);
			setExibeModalSolicitacaoExame(true);
			openDialog("modalAutenticacaoSolicitarExamesAbaConsultaSOWG");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void validarSolicitacaoExame() {
		matricula = servidorIdVO.getMatricula();
		vinCodigo = servidorIdVO.getSerVinCodigo();
		try {
			String usuarioLogado = super.obterLoginUsuarioLogado();
			emergenciaFacade.verificaPermissoesImpressaoRelatorio(matricula, vinCodigo, usuarioLogado);
			atendimento = emergenciaFacade.obterAtendimentoPorNumConsulta(registrarConsultaCOController.getNumeroConsulta());
			String usuarioSolicitante = this.autenticacaoController.getUsername();

			final String jsExecutaSolicitacaoExame = "parent.tab.addNewTab('tab_da_consultaCO', 'Exames',"
				+ "'/aghu/pages/exames/solicitacao/solicitacaoExameCRUD.xhtml?"
				+ "atendimento=" + this.atendimento + ";"
				+ "matricula=" + this.matricula + ";"
				+ "vinCodigo=" + this.vinCodigo + ";"
				+ "voltarPara=" + this.getTelaOrigem() + ";" 
				+ "abaOrigem=" + this.getAbaOrigem() + ";"
				+ "voltarEmergencia=true"  + ";"
				+ "pacCodigo=" + this.pacCodigo() + ";"
				+ "seqp=" + this.seqp() + ";"
				+ "numeroConsulta=" + this.numeroConsulta() + ";"
				+ "usuarioSolicitante=" + usuarioSolicitante + ";"
				+ "paramCid=#{javax.enterprise.context.conversation.id}'"
				+ ", '', '', 'false')";

			RequestContext.getCurrentInstance().execute(jsExecutaSolicitacaoExame);
			setExibeModalSolicitacaoExame(false);
		} catch (ApplicationBusinessException e) {
			FacesContext.getCurrentInstance().validationFailed();
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void prepararTelaNotaAdicional(){
		
		Integer pacCodigo = registrarConsultaCOController.getPacCodigo();
		Short seqp = registrarConsultaCOController.getSeqp();
		Integer numeroConsulta = registrarConsultaCOController.getNumeroConsulta();
			
		String evento = emergenciaFacade.buscarUltimoEventoParaCadastrarNotaAdicional(pacCodigo, seqp, numeroConsulta);
		DominioEventoNotaAdicional dominioEventoNotaAdicional = DominioEventoNotaAdicional.valueOf(evento);
		
		notaAdicionalController.setPacCodigo(pacCodigo);
		notaAdicionalController.setGsoSeqp(seqp);
		notaAdicionalController.setConNumero(numeroConsulta);
		notaAdicionalController.setEvento(dominioEventoNotaAdicional);			
	}
	
	public void desbloquearConsultaCO(){
		try {
			emergenciaFacade.desbloquearConsultaCO(registrarConsultaCOController.getDesbloqueioConsultaCOVO(), registrarConsultaCOController.getPacCodigo(), registrarConsultaCOController.getSeqp(), registrarConsultaCOController.getNumeroConsulta());
		} catch (ApplicationBusinessException e) {
			super.apresentarExcecaoNegocio(e);
		}		
	}
	
	public Integer numeroConsulta(){
		return registrarConsultaCOController.getNumeroConsulta();
	}
	
	public Short seqp(){
		return registrarConsultaCOController.getSeqp();
	}
	
	public Integer pacCodigo(){
		return registrarConsultaCOController.getPacCodigo();
	}
	
	public String getTelaOrigem(){
		return registrarConsultaCOController.getTelaOrigem();
	}
	
	public String getAbaOrigem(){
		return registrarConsultaCOController.getAbaOrigem();
	}

	public ServidorIdVO getServidorIdVO() {
		return servidorIdVO;
	}

	public void setServidorIdVO(ServidorIdVO servidorIdVO) {
		this.servidorIdVO = servidorIdVO;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(Integer atendimento) {
		this.atendimento = atendimento;
	}

	public boolean isExibeModalSolicitacaoExame() {
		return exibeModalSolicitacaoExame;
	}

	public void setExibeModalSolicitacaoExame(boolean exibeModalSolicitacaoExame) {
		this.exibeModalSolicitacaoExame = exibeModalSolicitacaoExame;
	}
	
	
}
