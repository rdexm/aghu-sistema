package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name="mptBloSeq", sequenceName="AGH.MPT_BLO_SQ1", allocationSize = 1)
@Table(name = "MPT_BLOQUEIO", schema = "AGH")
public class MptBloqueio extends BaseEntitySeq<Short> implements Serializable, Cloneable {


	/**
	 * 
	 */
	private static final long serialVersionUID = -7229117901613994072L;

	private Short seq;
	private MptTipoSessao tipoSessao;
	private MptSalas sala;
	private MptLocalAtendimento localAtendimento;
	private String descricao;
	private Date criadoEm;
	private Date apartirDe;
	private Date ate;
	private RapServidores servidor;	
	private MptJustificativa justificativa;	
	
	public MptBloqueio() {
		
	}

	public MptBloqueio(Short seq, MptTipoSessao tipoSessao, MptSalas sala, MptLocalAtendimento localAtendimento) {
		this.seq = seq;
		this.tipoSessao = tipoSessao;
		this.sala = sala;
		this.localAtendimento = localAtendimento;
	}
	
	
	public MptBloqueio(Short seq, MptTipoSessao tipoSessao, MptSalas sala, MptLocalAtendimento localAtendimento, String descricao,
			Date criadoEm, Date apartirDe, Date ate, RapServidores servidor) {
		this.seq = seq;
		this.tipoSessao = tipoSessao;
		this.sala = sala;
		this.localAtendimento = localAtendimento;
		this.descricao = descricao;
		this.criadoEm = criadoEm;
		this.apartirDe = apartirDe;
		this.ate = ate;
		this.servidor = servidor;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		TPS("tipoSessao"),
		TPS_SEQ("tipoSessao.seq"),
		SAL("sala"),
		SAL_SEQ("sala.seq"),		
		LOA("localAtendimento"),
		LOA_SEQ("localAtendimento.seq"),
		DESCRICAO("descricao"),
		CRIADO_EM("criadoEm"),
		SER("servidor"),
		SER_ID("servidor.id"),
		SER_MATRICULA("servidor.id.matricula"),
		SER_VIN_CODIGO("servidor.id.vinCodigo"),
		JUS("justificativa"),
		JUS_SEQ("justificativa.seq"),
		APARTIR_DE("apartirDe"),
		ATE("ate");
		
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
        if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		
		MptBloqueio other = (MptBloqueio) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
    }


    @Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mptBloSeq")
	@Column(name = "SEQ", nullable = false, precision = 4, scale = 0)
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPS_SEQ", referencedColumnName = "SEQ", nullable = false)
	public MptTipoSessao getTipoSessao() {
		return tipoSessao;
	}

	public void setTipoSessao(MptTipoSessao tipoSessao) {
		this.tipoSessao = tipoSessao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SAL_SEQ", referencedColumnName = "SEQ", nullable = false)
	public MptSalas getSala() {
		return sala;
	}

	public void setSala(MptSalas sala) {
		this.sala = sala;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOA_SEQ", referencedColumnName = "SEQ", nullable = false)
	public MptLocalAtendimento getLocalAtendimento() {
		return localAtendimento;
	}

	public void setLocalAtendimento(MptLocalAtendimento localAtendimento) {
		this.localAtendimento = localAtendimento;
	}

	@Column(name = "DESCRICAO", nullable = true, precision = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "ATE", nullable = false, length = 7)
	public Date getAte() {
		return ate;
	}

	public void setAte(Date ate) {
		this.ate = ate;
	}

	@Column(name = "APARTIR_DE", nullable = false, length = 7)
	public Date getApartirDe() {
		return apartirDe;
	}

	public void setApartirDe(Date apartirDe) {
		this.apartirDe = apartirDe;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "JUS_SEQ", referencedColumnName = "SEQ", nullable = false)
	public MptJustificativa getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(MptJustificativa justificativa) {
		this.justificativa = justificativa;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
