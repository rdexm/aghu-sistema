package br.gov.mec.aghu.faturamento.vo;



public class MesAnoVO {

	private Integer mes;
	private Integer ano;
	
	public Integer getMes() {
		return mes;
	}
	public void setMes(Integer mes) {
		this.mes = mes;
	}
	public Integer getAno() {
		return ano;
	}
	public void setAno(Integer ano) {
		this.ano = ano;
	}
	
	
public enum Fields {

		
		MES("mes"),
		ANO("ano");
		
		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	
	
}
