package br.gov.mec.aghu.paciente.vo;

import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AipEnderecoPacienteVO implements BaseBean {

	private static final long serialVersionUID = -5253081342741137704L;

	private Integer bclCloLgrCodigo;
	private Integer cddCodigo;
	private String cidade;
	private DominioTipoEndereco tipoEndereco;
	
	public enum Fields {

		BCL_CLO_LGR_CODIGO("bclCloLgrCodigo"), 
		CDD_CODIGO("cddCodigo"),
		CIDADE("cidade"),
		TIPO_ENDERECO("tipoEndereco"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getBclCloLgrCodigo() {
		return bclCloLgrCodigo;
	}

	public void setBclCloLgrCodigo(Integer bclCloLgrCodigo) {
		this.bclCloLgrCodigo = bclCloLgrCodigo;
	}

	public Integer getCddCodigo() {
		return cddCodigo;
	}

	public void setCddCodigo(Integer cddCodigo) {
		this.cddCodigo = cddCodigo;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public DominioTipoEndereco getTipoEndereco() {
		return tipoEndereco;
	}

	public void setTipoEndereco(DominioTipoEndereco tipoEndereco) {
		this.tipoEndereco = tipoEndereco;
	}
	
}
