package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;
import java.util.Date;

public class TaxaOcupacaoVO implements Serializable {
	
	private static final long serialVersionUID = -1467697338962486744L;

	private String dia;
	
	private Long contador;
	
	private String descricao;	
	
	private Short salSeq;
	
	private String nomeSala;
	
	private Short tpsSeq;
	
	private Date dataInicio;
	
	private Date dataFim;
	
	public String getDia() {
		return dia;
	}

	public void setDia(String dia) {
		this.dia = dia;
	}

	public Long getContador() {
		return contador;
	}

	public void setContador(Long contador) {
		this.contador = contador;
	}
	
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public Short getSalSeq() {
		return salSeq;
	}
 
	public void setSalSeq(Short salSeq) {
		this.salSeq = salSeq;
	}
	
	public String getNomeSala() {
		return nomeSala;
	}

	public void setNomeSala(String nomeSala) {
		this.nomeSala = nomeSala;
	}
    
	public enum Fields {
		
		DIA("dia"),
		CONTADOR("contador"),
		DESCRICAO("descricao"),
		SAL_SEQ("salSeq"),
		NOME_SALA("nomeSala"),
		TPS_SEQ("tpsSeq"),
		DATA_INICIO("dataInicio"),
		DATA_FIM("dataFim");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Short getTpsSeq() {
		return tpsSeq;
	}

	public void setTpsSeq(Short tpsSeq) {
		this.tpsSeq = tpsSeq;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

}
