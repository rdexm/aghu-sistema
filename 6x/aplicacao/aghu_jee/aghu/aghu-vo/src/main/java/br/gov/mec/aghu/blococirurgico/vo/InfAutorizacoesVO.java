package br.gov.mec.aghu.blococirurgico.vo;

public class InfAutorizacoesVO {

	private String nomeServidor;
	private String cargoServidor;
	private String dataAutorizacao;
	
	public enum Fields {	
		
		NOME_SERVIDOR("nomeServidor"),
		CARGO_SERVIDOR("cargoServidor"),
		DATA_AUTORIZACAO("dataAutorizacao");
	
		private String fields;
	
		private Fields(String fields) {
			this.fields = fields;
		}
	
		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public String getNomeServidor() {
		return nomeServidor;
	}

	public void setNomeServidor(String nomeServidor) {
		this.nomeServidor = nomeServidor;
	}

	public String getCargoServidor() {
		return cargoServidor;
	}

	public void setCargoServidor(String cargoServidor) {
		this.cargoServidor = cargoServidor;
	}

	public String getDataAutorizacao() {
		return dataAutorizacao;
	}

	public void setDataAutorizacao(String dataAutorizacao) {
		this.dataAutorizacao = dataAutorizacao;
	}
}
