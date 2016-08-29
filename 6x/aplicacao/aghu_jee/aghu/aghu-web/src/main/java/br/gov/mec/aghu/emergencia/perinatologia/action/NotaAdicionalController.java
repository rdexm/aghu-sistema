package br.gov.mec.aghu.emergencia.perinatologia.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import br.gov.mec.aghu.dominio.DominioEventoNotaAdicional;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class NotaAdicionalController extends ActionController {

	private static final long serialVersionUID = 4126313594932214952L;
	
	@EJB
	private IEmergenciaFacade emergenciaFacade;
	
	private Integer pacCodigo;
	private Short gsoSeqp;
	private Integer conNumero;
	private DominioEventoNotaAdicional evento;
	private String notaAdicional;
	
	private boolean gerarPendenciaDeAssinaturaDigital = Boolean.FALSE;	
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public void gravarNotaAdicional(){
		try {
			this.emergenciaFacade.gravarNotaAdicional(this.pacCodigo, this.gsoSeqp, this.conNumero, this.evento, this.notaAdicional);	
			this.gerarPendenciaDeAssinaturaDigital = this.emergenciaFacade.gerarPendenciaDeAssinaturaDigitalDoUsuarioLogado();
			if(this.gerarPendenciaDeAssinaturaDigital){
				this.apresentarMsgNegocio(Severity.INFO, "MSG_PENDENCIA_ASSINATURA_GERADA_SUCESSO");
			}			
			this.limparDados();
		} catch (ApplicationBusinessException e) {
			FacesContext.getCurrentInstance().validationFailed();
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void limparDados(){
		this.notaAdicional = null;
		this.gerarPendenciaDeAssinaturaDigital = Boolean.FALSE;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public DominioEventoNotaAdicional getEvento() {
		return evento;
	}

	public void setEvento(DominioEventoNotaAdicional evento) {
		this.evento = evento;
	}

	public String getNotaAdicional() {
		return notaAdicional;
	}

	public void setNotaAdicional(String notaAdicional) {
		this.notaAdicional = notaAdicional;
	}			
	
	public boolean isGerarPendenciaDeAssinaturaDigital() {
		return gerarPendenciaDeAssinaturaDigital;
	}
	
}
