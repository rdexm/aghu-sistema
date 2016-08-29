package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import br.gov.mec.aghu.dominio.DominioSituacaoEtapaPac;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * Modulo: Compras
 * @author jccouto (Jean Couto)
 * @since 22/08/2014
 */
@Entity
@Table(name = "SCO_LOG_ETAPA_PAC", schema = "AGH")
@SequenceGenerator(name = "scoLepSq1", sequenceName = "AGH.SCO_LEP_SQ1", allocationSize = 1)
public class ScoLogEtapaPac extends BaseEntitySeq<Short> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7286070885279744349L;

	private Short seq;
	private ScoEtapaPac etapa;
	private DominioSituacaoEtapaPac situacao;
	private Short tempoPrevisto;
	private String apontamentoUsuario;
	private RapServidores servidor;
	private Date dataApontamento;
	private Date dataAlteracao;

	public ScoLogEtapaPac() {

	}

	public ScoLogEtapaPac(Short seq, ScoEtapaPac etapa,
							DominioSituacaoEtapaPac situacao, Short tempoPrevisto,
							String apontamentoUsuario, RapServidores servidor,
							Date dataApontamento, Date dataAlteracao) {

		this.seq = seq;
		this.etapa = etapa;
		this.situacao = situacao;
		this.tempoPrevisto = tempoPrevisto;
		this.apontamentoUsuario = apontamentoUsuario;
		this.servidor = servidor;
		this.dataApontamento = dataApontamento;
		this.dataAlteracao = dataAlteracao;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "scoLepSq1")
	@Column(name = "SEQ", unique = true, nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="CODIGO_ETAPA", referencedColumnName = "CODIGO")
	public ScoEtapaPac getEtapa() {
		return etapa;
	}

	public void setEtapa(ScoEtapaPac etapa) {
		this.etapa = etapa;
	}

	@Column(name = "SITUACAO", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoEtapaPac getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoEtapaPac situacao) {
		this.situacao = situacao;
	}

	@Column(name = "TEMPO_PREVISTO", precision = 3, scale = 0)
	public Short getTempoPrevisto() {
		return tempoPrevisto;
	}

	public void setTempoPrevisto(Short tempoPrevisto) {
		this.tempoPrevisto = tempoPrevisto;
	}

	@Column(name = "APONTAMENTO_USUARIO", length = 60)
	public String getApontamentoUsuario() {
		return apontamentoUsuario;
	}

	public void setApontamentoUsuario(String apontamentoUsuario) {
		this.apontamentoUsuario = apontamentoUsuario;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
					@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")})
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_APONTAMENTO", length = 7)
	public Date getDataApontamento() {
		return dataApontamento;
	}

	public void setDataApontamento(Date dataApontamento) {
		this.dataApontamento = dataApontamento;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DATA_ALTERACAO", length = 7)
	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}
	
	public enum Fields {

		SEQ("seq"),
		ETAPA("etapa"),
		CODIGO_ETAPA("etapa.codigo"),
		SITUACAO("situacao"),
		TEMPO_PREVISTO("tempoPrevisto"),
		APONTAMENTO_USUARIO("apontamentoUsuario"),
		SERVIDOR("servidor"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		DATA_APONTAMENTO("dataApontamento"),
		DATA_ALTERACAO("dataAlteracao");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
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
		ScoLogEtapaPac other = (ScoLogEtapaPac) obj;
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
