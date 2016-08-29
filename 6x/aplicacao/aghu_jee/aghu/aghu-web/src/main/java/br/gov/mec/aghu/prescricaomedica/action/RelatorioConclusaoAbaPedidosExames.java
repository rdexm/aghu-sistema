package br.gov.mec.aghu.prescricaomedica.action;

import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.core.action.ActionController;

/**
 * Controller da aba de <b>Pedido de Exames</b> da tela de Relatorio de
 * Conclusao do Sumario Alta.<br>
 * 
 * @author rcorvalao
 * 
 */
public class RelatorioConclusaoAbaPedidosExames extends ActionController {

	private static final long serialVersionUID = -1279501962536571145L;
	
	private MpmAltaSumario altaSumario;
	private String altaTipoOrigem;

	/**
	 * Metodo responsavel por inicializar as variaveis e fazer load do
	 * Relatorio.<br>
	 * Chamado toda vez que um click for efetudado na aba.<br>
	 * 
	 * @param altaSumario
	 * @param altaTipoOrigem
	 */
	public void renderAba(MpmAltaSumario altaSumarioAtual, String pAltaTipoOrigem) {
		if (this.getAltaSumario() == null || (altaSumarioAtual != null && !this.getAltaSumario().getId().equals(altaSumarioAtual.getId()))) {
			this.setAltaSumario(altaSumarioAtual);
			this.setAltaTipoOrigem(pAltaTipoOrigem);
		}

	}

	/**
	 * @param altaSumario
	 *            the altaSumario to set
	 */
	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	/**
	 * @return the altaSumario
	 */
	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	/**
	 * @param altaTipoOrigem
	 *            the altaTipoOrigem to set
	 */
	public void setAltaTipoOrigem(String altaTipoOrigem) {
		this.altaTipoOrigem = altaTipoOrigem;
	}

	/**
	 * @return the altaTipoOrigem
	 */
	public String getAltaTipoOrigem() {
		return altaTipoOrigem;
	}

}
