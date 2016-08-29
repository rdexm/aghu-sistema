package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ViasAdministracaoController extends ActionController {

	private static final long serialVersionUID = 6041288920366520708L;

	private static final String PAGE_VIAS_ADMINISTRACAO_LIST = "viasAdministracaoList";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	private AfaViaAdministracao viaAdministracao;
	private AfaViaAdministracao viaAdministracaoAux;

	private String sigla;

	private boolean alterar;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void iniciar() {
	 

		if (this.viaAdministracao == null) {
			this.viaAdministracao = new AfaViaAdministracao();
			this.viaAdministracao.setIndSituacao(DominioSituacao.A);
		}
		setViaAdministracaoAux(this.viaAdministracao);
	
	}

	public String gravar() {
		try {
			if (validaCamposRequeridosEmBranco(viaAdministracao)) {
				this.cadastrosBasicosPrescricaoMedicaFacade.persistirViasAdministracao(this.viaAdministracao, this.viaAdministracaoAux);

				if (!this.isAlterar()) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_VIAS_ADMINISTRACAO", this.viaAdministracao.getDescricao());
				} else {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_VIAS_ADMINISTRACAO", this.viaAdministracao.getDescricao());
				}

				this.viaAdministracao = new AfaViaAdministracao();
				this.setSigla(null);
				this.setAlterar(false);
			} else {
				this.sigla = null;
				return null;
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);

			if (!this.viaAdministracao.getIndSituacao().isAtivo()
					&& (viaAdministracaoAux.getIndUsoNutricao().booleanValue() == viaAdministracao.getIndUsoNutricao().booleanValue())) {
				this.sigla = null;
			}

			return null;
		}

		return PAGE_VIAS_ADMINISTRACAO_LIST;
	}

	private void transformarTextosCaixaAlta() {
		this.viaAdministracao.setDescricao((this.viaAdministracao.getDescricao() == null || this.viaAdministracao.getDescricao().trim().equals("")) ? null : this.viaAdministracao
				.getDescricao().toUpperCase().trim());
		this.viaAdministracao.setSigla((this.viaAdministracao.getSigla() == null || this.viaAdministracao.getSigla().trim().equals("")) ? null : this.viaAdministracao.getSigla()
				.toUpperCase().trim());
	}

	/**
	 * Método que realiza a ação do botão cancelar
	 */
	public String cancelar() {
		this.setViaAdministracao(null);
		this.setSigla(null);
		this.setAlterar(false);
		return PAGE_VIAS_ADMINISTRACAO_LIST;
	}

	/**
	 * Testa campos em branco
	 * 
	 * @param vias
	 *            administração
	 * @return
	 */
	private boolean validaCamposRequeridosEmBranco(AfaViaAdministracao viaAdministracao) {
		boolean retorno = true;
		if (viaAdministracao != null) {

			transformarTextosCaixaAlta();

			if (StringUtils.isBlank(viaAdministracao.getDescricao())) {
				retorno = false;
				viaAdministracao.setDescricao(null);
				this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Descrição");
			}
			if (StringUtils.isBlank(viaAdministracao.getSigla())) {
				retorno = false;
				viaAdministracao.setSigla(null);
				this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Sigla");
			}
		}
		return retorno;
	}

	/**
	 * @return the viaAdministracao
	 */
	public AfaViaAdministracao getViaAdministracao() {
		return viaAdministracao;
	}

	/**
	 * @param viaAdministracao
	 *            the viaAdministracao to set
	 */
	public void setViaAdministracao(AfaViaAdministracao viaAdministracao) {
		this.viaAdministracao = viaAdministracao;
	}

	/**
	 * @return the sigla
	 */
	public String getSigla() {
		return sigla;
	}

	/**
	 * @param sigla
	 *            the sigla to set
	 */
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	/**
	 * @return the alterar
	 */
	public boolean isAlterar() {
		return alterar;
	}

	/**
	 * @param alterar
	 *            the alterar to set
	 */
	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public AfaViaAdministracao getViaAdministracaoAux() {
		return viaAdministracaoAux;
	}

	public void setViaAdministracaoAux(AfaViaAdministracao viaAdministracao) {
		this.viaAdministracaoAux = new AfaViaAdministracao();
		this.viaAdministracaoAux.setIndAplicaQuimioCca(viaAdministracao.getIndAplicaQuimioCca());
		this.viaAdministracaoAux.setIndPermiteBi(viaAdministracao.getIndPermiteBi());
		this.viaAdministracaoAux.setIndSituacao(viaAdministracao.getIndSituacao());
		this.viaAdministracaoAux.setIndUsoNutricao(viaAdministracao.getIndUsoNutricao());
		this.viaAdministracaoAux.setIndUsoQuimioterapia(viaAdministracao.getIndUsoQuimioterapia());
	}

}
