package br.gov.mec.aghu.faturamento.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatMotivoPendencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroMotivoDePendenciaDaContaController extends
		ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5342598682416406948L;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private FatMotivoPendencia fatMotivoPendencia = new FatMotivoPendencia();

	private boolean emEdicao;

	private String voltarPara;

	/**
	 * Gravar
	 */
	public String gravar() {
		try {
			this.faturamentoFacade
					.persistirFatMotivoPendencia(this.fatMotivoPendencia);

			if (fatMotivoPendencia.getSeq() != null && this.emEdicao) { // EDIÇÃO
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_ALTERACAO_MOTIVO_PENDENCIA",
						fatMotivoPendencia.getDescricao());

			} else { // INCLUSÃO
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CADASTRO_MOTIVO_PENDENCIA",
						fatMotivoPendencia.getDescricao());
				this.fatMotivoPendencia = new FatMotivoPendencia();

			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Método que realiza a ação do botão voltar
	 */
	public String voltar() {

		final String retorno = this.voltarPara;
		this.fatMotivoPendencia = new FatMotivoPendencia();
		this.emEdicao = false;
		this.voltarPara = null;
		return retorno;
	}

	/**
	 * @return the fatMotivoPendencia
	 */
	public FatMotivoPendencia getFatMotivoPendencia() {
		return fatMotivoPendencia;
	}

	/**
	 * @param fatMotivoPendencia
	 *            the fatMotivoPendencia to set
	 */
	public void setFatMotivoPendencia(FatMotivoPendencia fatMotivoPendencia) {
		this.fatMotivoPendencia = fatMotivoPendencia;
	}

	/**
	 * @return the emEdicao
	 */
	public boolean isEmEdicao() {
		return emEdicao;
	}

	/**
	 * @param emEdicao
	 *            the emEdicao to set
	 */
	public void setEmEdicao(boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	/**
	 * @return the voltarPara
	 */
	public String getVoltarPara() {
		return voltarPara;
	}

	/**
	 * @param voltarPara
	 *            the voltarPara to set
	 */
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

}
