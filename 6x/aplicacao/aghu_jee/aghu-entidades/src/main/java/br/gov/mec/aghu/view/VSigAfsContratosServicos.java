package br.gov.mec.aghu.view;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "V_SIG_AFS_CONTRATOS_SERVICOS", schema="AGH")
@Immutable
public class VSigAfsContratosServicos extends BaseEntitySeq<Integer> {

	private static final long serialVersionUID = -8228400290856284446L;

	@Id
	@Column(name="seq")
	private Integer seq;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name = "cont_seq")
	private ScoContrato scoContrato;
	
	@Column(name="numerointerno_af")
	private Integer numeroInternoAf;
	
	@Column(name="numero_af")
	private Integer numeroAf;
	
	@Column(name="complemento_af")
	private Integer complementoAf;
	
	@Column(name= "srv_nome")
	private String nomeServico;
	
	@Column(name= "srv_codigo")
	private Integer codigoServico;
	
	@Column(name="total_item")
	private BigDecimal totalItem;
	
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public ScoContrato getScoContrato() {
		return scoContrato;
	}

	public void setScoContrato(ScoContrato scoContrato) {
		this.scoContrato = scoContrato;
	}

	public Integer getNumeroInternoAf() {
		return numeroInternoAf;
	}

	public void setNumeroInternoAf(Integer numeroInternoAf) {
		this.numeroInternoAf = numeroInternoAf;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Integer getComplementoAf() {
		return complementoAf;
	}

	public void setComplementoAf(Integer complementoAf) {
		this.complementoAf = complementoAf;
	}

	public String getNomeServico() {
		return nomeServico;
	}

	public void setNomeServico(String nomeServico) {
		this.nomeServico = nomeServico;
	}
	
	public Integer getCodigoServico() {
		return codigoServico;
	}
	
	public void setCodigoServico(Integer codigoServico) {
		this.codigoServico = codigoServico;
	}

	public BigDecimal getTotalItem() {
		return totalItem;
	}

	public void setTotalItem(BigDecimal totalItem) {
		this.totalItem = totalItem;
	}

	@Transient
	public String getNomeItemAf() {
		Locale locBR = new Locale("pt", "BR");
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
		DecimalFormat format = new DecimalFormat("#,###,###,###,##0.00", dfSymbols);
		return numeroAf + " - " + complementoAf +" - " + nomeServico +" - " + format.format(totalItem);
	}

	public enum Fields {
		
		SEQ("seq"),
		CONTRATO("scoContrato.seq"),
		NUMERO_INTERNO_AF("numerointerno_af"),
		NUMERO_AF("numero_af"), 
		COMPLEMENTO_AF("complemento_af"), 
		NOME("nome"),
		TOTAL_ITEM("total_item");
	
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
