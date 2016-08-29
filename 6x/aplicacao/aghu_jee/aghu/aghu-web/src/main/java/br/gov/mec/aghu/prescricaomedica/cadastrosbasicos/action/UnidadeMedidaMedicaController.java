package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class UnidadeMedidaMedicaController extends ActionController {

	private static final long serialVersionUID = 212011874755947964L;
	
	private static final String PAGE_UNIDADE_MEDIDA_MEDICA_LIST = "unidadeMedidaMedicaList";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	private MpmUnidadeMedidaMedica unidade;

	private DominioSituacao indSituacao;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void iniciar() {
	 

		if (this.unidade == null) {
			unidade = new MpmUnidadeMedidaMedica();
		}
	
	} 

	public String confirmar() {
		try {
			
			boolean create = this.unidade.getSeq() == null;

			this.cadastrosBasicosPrescricaoMedicaFacade.persistUnidadeMedidaMedica(this.unidade);

			if (create) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_CRIACAO_UNIDADE_MEDICA",this.unidade.getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_UNIDADE_MEDICA", this.unidade.getDescricao());
			}

			this.unidade = new MpmUnidadeMedidaMedica();

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return PAGE_UNIDADE_MEDIDA_MEDICA_LIST;
	}
	
	public String cancelar() {
		unidade = null;
		return PAGE_UNIDADE_MEDIDA_MEDICA_LIST;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public MpmUnidadeMedidaMedica getUnidade() {
		return unidade;
	}

	public void setUnidade(MpmUnidadeMedidaMedica unidade) {
		this.unidade = unidade;
	}
}
