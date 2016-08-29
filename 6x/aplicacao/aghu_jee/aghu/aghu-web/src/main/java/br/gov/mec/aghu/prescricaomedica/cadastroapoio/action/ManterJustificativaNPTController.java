package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmJustificativaNpt;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterJustificativaNPTController extends ActionController {
	
	private static final String MANTER_JUSTIFICATIVA_NPT_LIST = "prescricaomedica-manterJustificativaNPTList";

	private static final long serialVersionUID = 4390715661115003861L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private ManterJustificativaNPTPaginatorController manterJustificativaNPTPaginatorController;
		
	private MpmJustificativaNpt justificativa;
	private Boolean edicao;
	private Boolean editado;
	private Short codigo;
	private String descricao;
	private Boolean situacao;
	
	public String confirmar() {
		manterJustificativaNPTPaginatorController.getDataModel().reiniciarPaginator();
		
		if(justificativa.getSeq() != null){
			this.edicao = true;
		}
		justificativa.setSituacao((situacao) ? DominioSituacao.A : DominioSituacao.I);
		
		try {
			this.prescricaoMedicaFacade.persistirMpmJustificativasNPT(this.justificativa);

			if (!edicao) {
				apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_CRIACAO_JUSTIFICATIVA_NPT", this.justificativa.getDescricao());
				limparCampos();
			} else {
				apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_EDICAO_JUSTIFICATIVA_NPT", this.justificativa.getDescricao());
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String cancelar() {
		limparCampos();
		return MANTER_JUSTIFICATIVA_NPT_LIST;
	}
	
	public void limparCampos(){
		this.codigo = null;
		this.descricao = null;
		this.situacao = true;
		justificativa = new MpmJustificativaNpt();
		edicao = Boolean.FALSE;
	}
	
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}
	
	public MpmJustificativaNpt getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(MpmJustificativaNpt justificativa) {
		this.justificativa = justificativa;
	}

	public Boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public Short getCodigo() {
		return codigo;
	}

	public Boolean isEditado() {
		return editado;
	}

	public void setEditado(Boolean editado) {
		this.editado = editado;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}
