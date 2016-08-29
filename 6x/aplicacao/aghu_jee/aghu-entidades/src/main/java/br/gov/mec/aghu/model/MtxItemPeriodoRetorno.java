package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioRepeticaoRetorno;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mtxIrtSq1", sequenceName="AGH.MTX_IRT_SQ1", allocationSize = 1)
@Table(name = "MTX_ITEM_PERIODO_RETORNO", schema = "AGH")
public class MtxItemPeriodoRetorno extends BaseEntitySeq<Integer> implements Comparable<MtxItemPeriodoRetorno> {

	private static final long serialVersionUID = 5074337979113385935L;

	private Integer seq;
	private MtxPeriodoRetorno periodoRetorno;
	private Integer ordem;
	private DominioRepeticaoRetorno indRepeticao;
	private Integer quantidade;
	private Integer version;
	
	public MtxItemPeriodoRetorno() {
		
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxIrtSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 9, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@JoinColumn(name = "PRT_SEQ", referencedColumnName = "SEQ", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	public MtxPeriodoRetorno getPeriodoRetorno() {
		return periodoRetorno;
	}

	public void setPeriodoRetorno(MtxPeriodoRetorno periodoRetorno) {
		this.periodoRetorno = periodoRetorno;
	}

	@Column(name = "ORDEM", unique = false, nullable = false, precision = 5, scale = 0)
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	@Column(name = "IND_REPETICAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioRepeticaoRetorno getIndRepeticao() {
		return indRepeticao;
	}

	public void setIndRepeticao(DominioRepeticaoRetorno indRepeticao) {
		this.indRepeticao = indRepeticao;
	}

	@Column(name = "QTDE", unique = false, nullable = false, precision = 2, scale = 0)
	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (!super.equals(obj)){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		MtxItemPeriodoRetorno other = (MtxItemPeriodoRetorno) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}
	
	public enum Fields {

		SEQ("seq"), 
		PERIODO_RETORNO("periodoRetorno"), 
		ORDEM("ordem"), 
		IND_REPETICAO("indRepeticao"), 
		QUANTIDADE("quantidade");

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
	public int compareTo(MtxItemPeriodoRetorno mxtItem) {
		/*if(this.getSeq() != null && mxtItem.getSeq() != null) {
			return this.getSeq().compareTo(mxtItem.getSeq());
		}else */
			return 1;
	}

}