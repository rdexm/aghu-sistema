package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmTipoRespostaConsultoria;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterTiposRespostasConsultoriaController extends ActionController {

	private static final long serialVersionUID = 3372414896472728283L;

	private static final String PAGE_PESQUISAR_TIPOS_RESPOSTAS = "pesquisarTiposRespostasConsultoria";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	private MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria;
	private Boolean indSituacao;

	public void inicio() {
		if (this.mpmTipoRespostaConsultoria != null) {
			this.mpmTipoRespostaConsultoria = this.prescricaoMedicaFacade.obterMpmRespostaConsultoriaPorSeq(this.mpmTipoRespostaConsultoria.getSeq());
			this.indSituacao = this.mpmTipoRespostaConsultoria.getIndSituacao().isAtivo();
			
		} else {
			this.mpmTipoRespostaConsultoria = new MpmTipoRespostaConsultoria();
			this.indSituacao = Boolean.TRUE;
		}
	}
	
	public String confirmar() {
		
		try {
			if (this.mpmTipoRespostaConsultoria.getSeq() == null) {
				this.mpmTipoRespostaConsultoria.setIndSituacao(this.indSituacao.equals(Boolean.TRUE) ? DominioSituacao.A : DominioSituacao.I);
				this.prescricaoMedicaFacade.inserirTipoRespostaConsultoria(this.mpmTipoRespostaConsultoria);
				
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_TIPO_RESPOSTA_CONSULTORIA",
						this.mpmTipoRespostaConsultoria.getDescricao());
			} else {
				this.mpmTipoRespostaConsultoria.setIndSituacao(this.indSituacao.equals(Boolean.TRUE) ? DominioSituacao.A : DominioSituacao.I);
				this.prescricaoMedicaFacade.atualizarTipoRespostaConsultoria(this.mpmTipoRespostaConsultoria);
				
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_TIPO_RESPOSTA_CONSULTORIA",
						this.mpmTipoRespostaConsultoria.getDescricao());
			}
			limpar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_PESQUISAR_TIPOS_RESPOSTAS;
	}
	
	public String cancelar() {
		limpar();
		return PAGE_PESQUISAR_TIPOS_RESPOSTAS;
	}
	
	private void limpar() {
		this.mpmTipoRespostaConsultoria = null;
		this.indSituacao = null;
	}

	// getters & setters
	
	public MpmTipoRespostaConsultoria getMpmTipoRespostaConsultoria() {
		return mpmTipoRespostaConsultoria;
	}

	public void setMpmTipoRespostaConsultoria(
			MpmTipoRespostaConsultoria mpmTipoRespostaConsultoria) {
		this.mpmTipoRespostaConsultoria = mpmTipoRespostaConsultoria;
	}

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}
}
