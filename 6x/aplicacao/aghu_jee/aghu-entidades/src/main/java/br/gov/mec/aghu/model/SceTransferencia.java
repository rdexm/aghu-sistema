package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

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

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sce_transferencias database table.
 * 
 */
@Entity
@Table(name="SCE_TRANSFERENCIAS")
public class SceTransferencia extends BaseEntitySeq<Integer> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2429628934892242414L;
	private Integer seq;
	private ScoClassifMatNiv5 classifMatNiv5;
	private Date dtEfetivacao;
	private Date dtEstorno;
	private Date dtGeracao;
	private Boolean efetivada;
	private Boolean estorno;
	private Boolean transferenciaAutomatica;
	private ScePacoteMateriais pacoteMaterial;
	private Integer version;
	private Set<SceItemTransferencia> itensTransferencia;
	private SceAlmoxarifado almoxarifado;
	private SceAlmoxarifado almoxarifadoRecebimento;
	private SceTipoMovimento tipoMovimento;
	private RapServidores servidor;
	private RapServidores servidorEfetivado;
	private RapServidores servidorEstornado;

	
	public enum Fields {
		
		SEQ("seq"),
		CLASSIF_MAT_NIV5("classifMatNiv5"),
		DT_EFETIVACAO("dtEfetivacao"),
		DT_ESTORNO("dtEstorno"),
		DT_GERACAO("dtGeracao"),
		EFETIVADA("efetivada"),
		ESTORNO("estorno"),
		TRANSFERENCIA_AUTOMATICA("transferenciaAutomatica"),
		ITEM_TRANSFERENCIA("itensTransferencia"),
		SCE_ALMOX("almoxarifado"),
		SCE_ALMOX_RECEB("almoxarifadoRecebimento"),
		TIPO_MOVIMENTO("tipoMovimento"),
		SERVIDOR("servidor"),
		SERVIDOR_EFETIVADO("servidorEfetivado"),
		CENTRO_CUSTO_PROPRIETARIO_PACOTE("pacoteMaterial.id.codigoCentroCustoProprietario"),
		CENTRO_CUSTO_APLICACAO_PACOTE("pacoteMaterial.id.codigoCentroCustoAplicacao"),
		NUMERO_PACOTE("pacoteMaterial.id.numero"),
		SERVIDOR_ESTORNADO("servidorEstornado");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
    public SceTransferencia() {
    }


	@Id
	@SequenceGenerator(name="SCE_TRANSFERENCIAS_SEQ_GENERATOR", sequenceName="AGH.SCE_TRF_SQ1", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.AUTO, generator="SCE_TRANSFERENCIAS_SEQ_GENERATOR")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne
	@JoinColumn(name="ALM_SEQ", nullable = false)
	public SceAlmoxarifado getAlmoxarifado() {
		return this.almoxarifado;
	}

	@ManyToOne
	@JoinColumn(name="ALM_SEQ_RECEBE", nullable = false)
	public SceAlmoxarifado getAlmoxarifadoRecebimento() {
		return this.almoxarifadoRecebimento;
	}
	
	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}


	public void setAlmoxarifadoRecebimento(SceAlmoxarifado almoxarifadoRecebimento) {
		this.almoxarifadoRecebimento = almoxarifadoRecebimento;
	}
	
	@ManyToOne
	@JoinColumn(name="CN5_NUMERO")
	public ScoClassifMatNiv5 getClassifMatNiv5() {
		return classifMatNiv5;
	}
	
	public void setClassifMatNiv5(ScoClassifMatNiv5 classifMatNiv5) {
		this.classifMatNiv5 = classifMatNiv5;
	}

	@Column(name="DT_EFETIVACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEfetivacao() {
		return this.dtEfetivacao;
	}

	public void setDtEfetivacao(Date dtEfetivacao) {
		this.dtEfetivacao = dtEfetivacao;
	}


	@Column(name="DT_ESTORNO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEstorno() {
		return this.dtEstorno;
	}

	public void setDtEstorno(Date dtEstorno) {
		this.dtEstorno = dtEstorno;
	}


	@Column(name="DT_GERACAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}


	@Column(name="IND_EFETIVADA", nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEfetivada() {
		return efetivada;
	}
	
	public void setEfetivada(Boolean efetivada) {
		this.efetivada = efetivada;
	}


	@Column(name="IND_ESTORNO", nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getEstorno() {
		return estorno;
	}
	
	public void setEstorno(Boolean estorno) {
		this.estorno = estorno;
	}

	@Column(name="IND_TRANSF_AUTOMATICA", nullable = false)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getTransferenciaAutomatica() {
		return transferenciaAutomatica;
	}
	public void setTransferenciaAutomatica(Boolean transferenciaAutomatica) {
		this.transferenciaAutomatica = transferenciaAutomatica;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "PMT_CCT_CODIGO_REFERE", referencedColumnName = "CCT_CODIGO_REFERE"),
		@JoinColumn(name = "PMT_CCT_CODIGO", referencedColumnName = "CCT_CODIGO"), 
		@JoinColumn(name = "PMT_NUMERO", referencedColumnName = "NUMERO") })
		public ScePacoteMateriais getPacoteMaterial() {
		return pacoteMaterial;
	}
	
	public void setPacoteMaterial(ScePacoteMateriais pacoteMaterial) {
		this.pacoteMaterial = pacoteMaterial;
	}

	//bi-directional many-to-one association to RapServidores
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA", referencedColumnName="MATRICULA", nullable = false),
		@JoinColumn(name="SER_VIN_CODIGO", referencedColumnName="VIN_CODIGO", nullable = false)
		})
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	//bi-directional many-to-one association to RapServidores
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_EFETIVADO", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_EFETIVADO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidorEfetivado() {
		return this.servidorEfetivado;
	}

	public void setServidorEfetivado(RapServidores servidorEfetivado) {
		this.servidorEfetivado = servidorEfetivado;
	}
	
	
	
	//bi-directional many-to-one association to RapServidores
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_ESTORNO", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_ESTORNO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getServidorEstornado() {
		return this.servidorEstornado;
	}

	public void setServidorEstornado(RapServidores servidorEstornado) {
		this.servidorEstornado = servidorEstornado;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "TMV_SEQ", referencedColumnName = "SEQ",nullable = false),
			@JoinColumn(name = "TMV_COMPLEMENTO", referencedColumnName = "COMPLEMENTO",nullable = false)})
	public SceTipoMovimento getTipoMovimento() {
		return tipoMovimento;
	}
	
	@Transient
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	//bi-directional many-to-one association to SceItemTransferencia
	@OneToMany(mappedBy="transferencia")
	public Set<SceItemTransferencia> getItensTransferencia() {
		return this.itensTransferencia;
	}

	public void setItensTransferencia(Set<SceItemTransferencia> itensTransferencia) {
		this.itensTransferencia = itensTransferencia;
	}
	
	public void setTipoMovimento(SceTipoMovimento tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}
	
	@Transient
	public Boolean getIndAtivoTransfAutomatica() {
		
		if (this.seq != null) {
			return this.transferenciaAutomatica.equals(DominioSimNao.S);
		}
		return false;
	}	
	
	
	
	@Transient
	public void setIndAtivoTransfAutomatica(Boolean ativo) {
		if (ativo) {
			this.transferenciaAutomatica=Boolean.TRUE;
		} else {
			this.transferenciaAutomatica=Boolean.FALSE;
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
		SceTransferencia other = (SceTransferencia) obj;
		if (seq == null) {
			if (other.seq != null){
				return false;
			}
		} else if (!seq.equals(other.seq)){
			return false;
		}
		return true;
	}
}