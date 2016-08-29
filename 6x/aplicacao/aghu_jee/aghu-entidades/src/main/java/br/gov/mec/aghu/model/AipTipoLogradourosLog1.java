package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * @author fbraganca
 */
@Entity
@Table(name = "AIP_TIPO_LOGRADOUROS_LOG1", schema = "AGH")
@SequenceGenerator(name="aipTipoLogradourosLog1Sq1", sequenceName="AGH.AIP_TLG_LG1_SQ1")
public class AipTipoLogradourosLog1 extends BaseEntitySeq<Integer> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1248563480881890934L;
	private Integer seq;
	private String tabela;
	private Integer codAntigo;
	private Integer codNovo;
	private Date dataHora;
	private String mensagem;
	
	public AipTipoLogradourosLog1() {
		
	}
	
	public AipTipoLogradourosLog1(Integer seq, String tabela, Integer codAntigo,
			Integer codNovo, Date dataHora, String mensagem) {
		this.seq = seq;
		this.tabela = tabela;
		this.codAntigo = codAntigo;
		this.codNovo = codNovo;
		this.dataHora = dataHora;
		this.mensagem = mensagem;
	}
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "aipTipoLogradourosLog1Sq1")
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "TABELA", length = 100)
	@Length(max = 100)
	public String getTabela() {
		return tabela;
	}
	
	public void setTabela(String tabela) {
		this.tabela = tabela;
	}
	
	@Column(name = "CODIGO_ANTIGO")
	public Integer getCodAntigo() {
		return codAntigo;
	}
	public void setCodAntigo(Integer codAntigo) {
		this.codAntigo = codAntigo;
	}
	
	@Column(name = "CODIGO_NOVO")
	public Integer getCodNovo() {
		return codNovo;
	}
	public void setCodNovo(Integer codNovo) {
		this.codNovo = codNovo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATAHORA")
	public Date getDataHora() {
		return dataHora;
	}
	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}
	
	@Column(name = "MENSAGEM", length = 2000)
	@Length(max = 2000)
	public String getMensagem() {
		return mensagem;
	}
	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
	
	public enum Fields {
		SEQ("seq"),
		TABELA("tabela"), 
		CODIGO_ANTIGO("codAntigo"), 
		CODIGO_NOVO("codNovo"),
		DATAHORA("dataHora"),
		MENSAGEM("mensagem");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		AipTipoLogradourosLog1 other = (AipTipoLogradourosLog1) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
		
}
