package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class MotivoAltaMedicaController extends ActionController {

	private static final long serialVersionUID = -8668021667054213166L;

	private static final String PAGE_PESQUISA_MOTIVO_ALTA_MEDICA = "motivoAltaMedicaList";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	private MpmMotivoAltaMedica motivoAltaMedica = new MpmMotivoAltaMedica();

	private DominioSituacao indSituacao;

	private boolean desabilitarCodigo;
	private boolean exigeComplemento;
	private boolean outros;

	@PostConstruct
	public void init() {
		this.begin(conversation);
	}

	/**
	 * Testa campos em branco
	 * 
	 * @param motivoAltaMedica
	 * @return
	 */
	private boolean validaCamposRequeridosEmBranco(MpmMotivoAltaMedica motivoAltaMedica) {
		boolean retorno = true;
		if (motivoAltaMedica != null) {
			if (StringUtils.isBlank(motivoAltaMedica.getDescricao())) {
				retorno = false;
				motivoAltaMedica.setDescricao(null);
				this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Descrição");
			}
			if (StringUtils.isBlank(motivoAltaMedica.getSigla())) {
				retorno = false;
				motivoAltaMedica.setSigla(null);
				this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Sigla");
			}
		}
		return retorno;
	}

	public void iniciar() {
		if (!desabilitarCodigo) {
			this.motivoAltaMedica = new MpmMotivoAltaMedica();
			this.desabilitarCodigo = false;
			this.motivoAltaMedica.setIndSituacao(DominioSituacao.A);
		}
	}
	
	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * acomodação
	 */
	public String confirmar() {
		try {
			// Tarefa 904 - Testa campos em branco
			if (validaCamposRequeridosEmBranco(motivoAltaMedica)) {
				// Tarefa 659 - deixar todos textos das entidades em caixa alta
				// via toUpperCase()
				transformarTextosCaixaAlta();

				boolean create = this.motivoAltaMedica.getSeq() == null;

				this.cadastrosBasicosPrescricaoMedicaFacade.persistMotivoAltaMedica(this.motivoAltaMedica);

				if (create) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_MOTIVO_ALTA_MEDICA", this.motivoAltaMedica.getDescricao());
				} else {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_MOTIVO_ALTA_MEDICA", this.motivoAltaMedica.getDescricao());
				}
				this.desabilitarCodigo = false;
				this.motivoAltaMedica = new MpmMotivoAltaMedica();
			} else {
				return null;
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_PESQUISA_MOTIVO_ALTA_MEDICA;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Acomodação
	 */
	public String cancelar() {
		this.motivoAltaMedica = new MpmMotivoAltaMedica();
		this.desabilitarCodigo = false;
		return PAGE_PESQUISA_MOTIVO_ALTA_MEDICA;
	}

	private void transformarTextosCaixaAlta() {
		this.motivoAltaMedica.setDescricao(this.motivoAltaMedica.getDescricao() == null ? null : this.motivoAltaMedica.getDescricao().toUpperCase());
		this.motivoAltaMedica.setSigla(this.motivoAltaMedica.getSigla() == null ? null : this.motivoAltaMedica.getSigla().toUpperCase());
	}

	/**
	 * Getters and Setters
	 */
	public MpmMotivoAltaMedica getMotivoAltaMedica() {
		return motivoAltaMedica;
	}

	public void setMotivoAltaMedica(MpmMotivoAltaMedica motivoAltaMedica) {
		this.motivoAltaMedica = motivoAltaMedica;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public boolean isDesabilitarCodigo() {
		return desabilitarCodigo;
	}

	public void setDesabilitarCodigo(boolean desabilitarCodigo) {
		this.desabilitarCodigo = desabilitarCodigo;
	}

	public boolean isExigeComplemento() {
		return exigeComplemento;
	}

	public void setExigeComplemento(boolean exigeComplemento) {
		this.exigeComplemento = exigeComplemento;
	}

	public boolean isOutros() {
		return outros;
	}

	public void setOutros(boolean outros) {
		this.outros = outros;
	}
}