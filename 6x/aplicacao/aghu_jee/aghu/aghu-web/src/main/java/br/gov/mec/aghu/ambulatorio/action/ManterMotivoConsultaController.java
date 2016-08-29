package br.gov.mec.aghu.ambulatorio.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.model.AacMotivos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterMotivoConsultaController extends ActionController {

	private static final long serialVersionUID = 3471413308086495495L;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	private AacMotivos aacMotivos;
	
	private static final String MOTIVO_CONSULTA_LIST = "manterMotivoConsultaList";

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public void iniciar() {
	 

	 

		if (this.aacMotivos == null) {
			this.aacMotivos = new AacMotivos();
			this.aacMotivos.setAtivo(true);
		}
	
	}
	

	public String confirmar() {
		
		try {
			boolean create = this.aacMotivos.getCodigo() == null;
			
			if (create) {
				this.ambulatorioFacade.persistirMotivoConsulta(this.aacMotivos);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_MOTIVO_CONSULTA", this.aacMotivos.getDescricao());
			} else {
				this.ambulatorioFacade.atualizarMotivoConsulta(this.aacMotivos);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_MOTIVO_CONSULTA", this.aacMotivos.getDescricao());
			}
			
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);

			return null;
		}
		
		return cancelar();
	}
	
	public String cancelar() {
		aacMotivos = null;
		return MOTIVO_CONSULTA_LIST;
	}

	public AacMotivos getAacMotivos() {
		return aacMotivos;
	}

	public void setAacMotivos(AacMotivos aacMotivos) {
		this.aacMotivos = aacMotivos;
	}
}