package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;
import javax.validation.constraints.Digits;


import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntity;


/**
 * The persistent class for the sco_itens_contratos database table.
 * 
 */
@Entity
@Table(name="SCO_ITENS_CONTRATOS", schema="AGH")
public class ScoItensContrato implements BaseEntity {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4107285290854195954L;

	@Id
	@SequenceGenerator(name="SCO_ITENS_CONTRATOS_SEQ_GENERATOR", sequenceName="SCO_ICON_SQ1", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCO_ITENS_CONTRATOS_SEQ_GENERATOR")
	private Integer seq;

	@Column(name="ALTERADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	private Date alteradoEm;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CONT_SEQ")
	private ScoContrato contrato;

	@Column(name="CRIADO_EM")
	@Temporal(TemporalType.TIMESTAMP)
	private Date criadoEm;

	
	private String descricao;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MAT_CODIGO")
	private ScoMaterial material;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MCM_CODIGO")
	private ScoMarcaComercial marcaComercial;

	@Column(name="NR_ITEM")
	private Integer nrItem;

	private Integer quantidade;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	private RapServidores servidor;	

	
	@ManyToOne(fetch = FetchType.LAZY)	
	@JoinColumn(name="SRV_CODIGO")
	private ScoServico servico;

	private String unidade;

	@Column(name = "VERSION", length= 7)
	@Version
	private Integer version;

	@Column(name="VL_UNITARIO")
	@Digits(integer=13, fraction=2, message="Valor unitario no máximo 13 números inteiros e 2 decimais")
	private BigDecimal vlUnitario;
	
	@OneToMany(mappedBy="scoItensContrato", fetch = FetchType.LAZY)
	private List<ScoConvItensContrato> convItensContrato;
	
	@Transient
	private BigDecimal vlTotal;
	
	@Transient
	private String nomeItemContrato; 
	
	@Transient
	private BigDecimal vlEstimadoMes;

    public ScoItensContrato() {
    }

	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Date getAlteradoEm() {
		return this.alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}


	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public Integer getNrItem() {
		return this.nrItem;
	}

	public void setNrItem(Integer nrItem) {
		this.nrItem = nrItem;
	}

	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}


	public String getUnidade() {
		return this.unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public BigDecimal getVlUnitario() {
		return this.vlUnitario;
	}

	public void setVlUnitario(BigDecimal vlUnitario) {
		this.vlUnitario = vlUnitario;
	}


	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
			.append("seq",this.seq)
		.toString();
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
		if (!(obj instanceof ScoItensContrato)){
			return false;
		}
		ScoItensContrato other = (ScoItensContrato) obj;
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
		CONTRATO("contrato"),
		CONT_SEQ("contrato.seq"), 
		MATERIAL_COD("material.codigo"), 
		MATERIAL("material"),
		SERVICO("servico"),
		MARCA_COMERCIAL_CODIGO("marcaComercial.codigo"),
		MARCA_COMERCIAL("marcaComercial"),
		NR_ITEM("nrItem"),
		CONV_ITENS_CONTRATO("convItensContrato");
	
	private String field;

	private Fields(String field) {
		this.field = field;
	}

	@Override
	public String toString() {
		return this.field;
	}
	}

	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public BigDecimal getVlTotal() {
		
		if(this.getVlUnitario() != null &&
		   this.getQuantidade() != null){
			
			this.vlTotal = this.getVlUnitario().multiply(new BigDecimal(this.getQuantidade().intValue()));
		}
		
		return vlTotal;
	}

	public void setVlTotal(BigDecimal vlTotal) {
		this.vlTotal = vlTotal;
	}

	public List<ScoConvItensContrato> getConvItensContrato() {
		return convItensContrato;
	}

	public void setConvItensContrato(List<ScoConvItensContrato> convItensContrato) {
		this.convItensContrato = convItensContrato;
	}	

	public String getNomeItemContrato(){
		Locale locBR = new Locale("pt", "BR");
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
		DecimalFormat format = new DecimalFormat("#,###,###,###,##0.00", dfSymbols);
		if(getVlTotal() == null){
			this.nomeItemContrato = this.getNrItem() + " - " + this.getDescricao();	
		}else{
			this.nomeItemContrato = this.getNrItem() + " - " + this.getDescricao() + " - " + format.format(this.getVlTotal());
		}
		
		return nomeItemContrato;
	}

	public void setNomeItemContrato(String nomeItemContrato) {
		this.nomeItemContrato = nomeItemContrato;
	}

	public BigDecimal getVlEstimadoMes() {
		if(this.getContrato() != null  && 
	       this.getContrato().getDtInicioVigencia() != null 
		   && this.getContrato().getDtFimVigencia() != null){
			  Integer prazoMessesMinimo = Integer.valueOf(1); 
	          Calendar cInicio = Calendar.getInstance();
	          cInicio.setTime(this.getContrato().getDtInicioVigencia());
	          Calendar cFim = Calendar.getInstance();
	          cFim.setTime(this.getContrato().getDtFimVigencia());
	          Integer difMes = cFim.get(Calendar.MONTH)-cInicio.get(Calendar.MONTH);  
	          Integer difAno = ((cFim.get(Calendar.YEAR)-cInicio.get(Calendar.YEAR))*12);  
	          Integer prazoMesses = difAno+difMes;  
	          if(prazoMessesMinimo >= prazoMesses){
	        	  this.vlEstimadoMes = getVlTotal();
	          }else{
	        	  this.vlEstimadoMes = getVlTotal().divide(new BigDecimal(prazoMesses), 2, RoundingMode.HALF_UP);
	          }
			
		}
		
		return vlEstimadoMes;
	}

	public void setVlEstimadoMes(BigDecimal vlEstimadoMes) {
		this.vlEstimadoMes = vlEstimadoMes;
	}
	
}