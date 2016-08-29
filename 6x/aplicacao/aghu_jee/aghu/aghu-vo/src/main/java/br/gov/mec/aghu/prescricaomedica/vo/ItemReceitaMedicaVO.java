package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSimNao;

/**
 * Representa um item de receita médica normal ou especial.
 * 
 * @author cvagheti
 * 
 */

public class ItemReceitaMedicaVO implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8480790289499253643L;
	private String medicamento;
	private String quantidade;
	private String formaDeUso;
	private DominioSimNao usoContinuo;
	private DominioSimNao usoInterno;

	public ItemReceitaMedicaVO() {

	}

	public ItemReceitaMedicaVO(String medicamento) {
		this.medicamento = medicamento;
	}

	public String getMedicamento() {
		return medicamento;
	}

	public void setMedicamento(String medicamento) {
		this.medicamento = medicamento;
	}

	public String getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(String quantidade) {
		this.quantidade = quantidade;
	}

	public String getFormaDeUso() {
		return formaDeUso;
	}

	public void setFormaDeUso(String formaDeUso) {
		this.formaDeUso = formaDeUso;
	}

	public void setUsoContinuo(DominioSimNao usoContinuo) {
		this.usoContinuo = usoContinuo;
	}

	public void setUsoInterno(DominioSimNao usoInterno) {
		this.usoInterno = usoInterno;
	}

	public boolean isUsoContinuo() {
		return this.usoContinuo == DominioSimNao.S;
	}

	public boolean isUsoInterno() {
		return this.usoInterno == DominioSimNao.S;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("medicamento", this.medicamento).toString();
	}

	public enum Fields {
		MEDICAMENTO("medicamento"), QUANTIDADE("quantidade"), FORMA_USO(
				"formaDeUso"), USO_CONTINUO("usoContinuo"), USO_INTERNO(
				"usoInterno");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
