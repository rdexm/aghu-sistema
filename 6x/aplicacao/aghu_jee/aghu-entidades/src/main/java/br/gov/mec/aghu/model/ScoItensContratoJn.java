package br.gov.mec.aghu.model;
import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;


import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.model.BaseJournal;

@Entity
@Table(name = "SCO_ITENS_CONTRATOS_JN", schema="AGH", uniqueConstraints = @UniqueConstraint(columnNames = "SEQ_JN"))
@SequenceGenerator(name = "scoItensContratosJnSq1", sequenceName = "AGH.SCO_ICON_JN_SEQ", allocationSize = 1)
@Immutable
public class ScoItensContratoJn extends BaseJournal implements Serializable  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4171304406858524873L;
	private Integer seqJn;
	private Integer seq;
	private ScoMarcaComercial marcaComercial;
	private ScoContrato contrato;
	private Integer nrItem;
	private Integer version;
	private ScoMaterial material;
	private ScoServico servico;
	private Integer quantidade;
	private String unidade;
	private BigDecimal vlUnitario;
	private String descricao;


	// construtores
	
	public ScoItensContratoJn(){
	}


	// getters & setters
	@Id
	@Column(name = "SEQ_JN", length= 7, unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoItensContratosJnSq1")
	@Override	
	public Integer getSeqJn(){
		return this.seqJn;
	}
	
	public void setSeqJn(Integer seqJn){
		this.seqJn = seqJn;
	}
		
	@Column(name = "SEQ", length= 7, nullable = false)	 
	public Integer getSeq(){
		return this.seq;
	}
	
	public void setSeq(Integer seq){
		this.seq = seq;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MCM_CODIGO")
	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}
		
	@ManyToOne
	@JoinColumn(name="CONT_SEQ")
	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}
		
	@Column(name = "NR_ITEM", length= 7, nullable = false)	 
	public Integer getNrItem(){
		return this.nrItem;
	}
	
	public void setNrItem(Integer nrItem){
		this.nrItem = nrItem;
	}
		
	@Column(name = "VERSION", length= 7, nullable = false)	 
	public Integer getVersion(){
		return this.version;
	}
	
	public void setVersion(Integer version){
		this.version = version;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MAT_CODIGO")
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)	
	@JoinColumn(name="SRV_CODIGO") 
	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}
		
	@Column(name = "QUANTIDADE", length= 7, nullable = false)	 
	public Integer getQuantidade(){
		return this.quantidade;
	}
	
	public void setQuantidade(Integer quantidade){
		this.quantidade = quantidade;
	}
		
	@Column(name = "UNIDADE", length= 20, nullable = false)	 
	public String getUnidade(){
		return this.unidade;
	}
	
	public void setUnidade(String unidade){
		this.unidade = unidade;
	}
		
	@Column(name = "VL_UNITARIO", length= 15, nullable = false)	 
	public BigDecimal getVlUnitario(){
		return this.vlUnitario;
	}
	
	public void setVlUnitario(BigDecimal vlUnitario){
		this.vlUnitario = vlUnitario;
	}
		
	@Column(name = "DESCRICAO", length= 255)	 
	public String getDescricao(){
		return this.descricao;
	}
	
	public void setDescricao(String descricao){
		this.descricao = descricao;
	}
			
	public enum Fields {

		SEQ_JN("seqJn"),
		SEQ("seq"),
		MARCA_COMERCIAL("marcaComercial"),
		CONTRATO("contrato"),
		NR_ITEM("nrItem"),
		MATERIAL("material"),
		SERVICO("servico"),
		QUANTIDADE("quantidade"),
		UNIDADE("unidade"),
		VL_UNITARIO("vlUnitario"),
		DESCRICAO("descricao");

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