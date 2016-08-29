package br.gov.mec.aghu.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "mtxPtrSq1", sequenceName = "AGH.MTX_PTR_SQ1", allocationSize = 1)
@Table(name = "MTX_PERIODO_RETORNO", schema = "AGH")
public class MtxPeriodoRetorno extends BaseEntitySeq<Integer> implements Comparable<MtxPeriodoRetorno>{

	private static final long serialVersionUID = 5074337979513385935L;

	private Integer seq;
	private MtxTipoRetorno tipoRetorno;
	private DominioSituacao indSituacao;
	private RapServidores servidor;
	private Date criadoEm;
	private Integer version;
	private Set<MtxItemPeriodoRetorno> listaItemPeriodoRetorno = new HashSet<MtxItemPeriodoRetorno>(0);;

	public MtxPeriodoRetorno() {
		this.tipoRetorno = new MtxTipoRetorno();
	}

	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mtxPtrSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 5, scale = 0)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@JoinColumn(name = "TRE_SEQ", referencedColumnName = "SEQ", nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	public MtxTipoRetorno getTipoRetorno() {
		return tipoRetorno;
	}

	public void setTipoRetorno(MtxTipoRetorno tipoRetorno) {
		this.tipoRetorno = tipoRetorno;
	}

	@Column(name = "IND_SITUACAO", length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@ManyToOne(fetch = FetchType.LAZY)
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM")
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "periodoRetorno")
	public Set<MtxItemPeriodoRetorno> getListaItemPeriodoRetorno() {
		return listaItemPeriodoRetorno;
	}

	public void setListaItemPeriodoRetorno(Set<MtxItemPeriodoRetorno> listaItemPeriodoRetorno) {
		this.listaItemPeriodoRetorno = listaItemPeriodoRetorno;
	}

	public enum Fields {
		SEQ("seq"), 
		TIPO_RETORNO("tipoRetorno"), 
		IND_SITUACAO("indSituacao"), 
		SERVIDOR("servidor"), 
		CRIADO_EM("criadoEm"),
		LISTA_ITEM_PERIODO_RETORNO("listaItemPeriodoRetorno");
		

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
	public int compareTo(MtxPeriodoRetorno mtxPr) {
	return this.getSeq().compareTo(mtxPr.getSeq());
	
	}
}