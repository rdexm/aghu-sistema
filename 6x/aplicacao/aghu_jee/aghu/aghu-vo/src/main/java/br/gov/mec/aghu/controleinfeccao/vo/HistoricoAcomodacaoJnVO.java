package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.core.commons.BaseBean;


public class HistoricoAcomodacaoJnVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4698369139994839005L;
	
	private Integer seqJn;
	private Date dataAlt;						// LOCAIS_JN.JN_DATE_TIME
	private String usuario;						// LOCAIS_JN.JN_USER 
	private String descAcomodacaoJn;			// LOCAIS_JN.DESCRICAO
	private Boolean reserva;					// LOCAIS_JN.IND_RESERVA
	private DominioTipoAcomodacao tipoLocal;	// LOCAIS_JN.TIPO_LOCAL
	private DominioSituacao situacao;			// LOCAIS_JN.IND_SITUACAO
	

	public HistoricoAcomodacaoJnVO() {
		super();
	}
	
	public enum Fields {

		SEQ_JN("seqJn"),
		DATA_ALT("dataAlt"),
		USUARIO("usuario"),
		DESC_ACOMODACAO_JN("descAcomodacaoJn"),
		RESERVA("reserva"),
		TIPO_LOCAL("tipoLocal"),
		SITUACAO("situacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Date getDataAlt() {
		return dataAlt;
	}

	public void setDataAlt(Date dataAlt) {
		this.dataAlt = dataAlt;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getDescAcomodacaoJn() {
		return descAcomodacaoJn;
	}

	public void setDescAcomodacaoJn(String descAcomodacaoJn) {
		this.descAcomodacaoJn = descAcomodacaoJn;
	}

	public Boolean getReserva() {
		return reserva;
	}

	public void setReserva(Boolean reserva) {
		this.reserva = reserva;
	}

	public DominioTipoAcomodacao getTipoLocal() {
		return tipoLocal;
	}

	public void setTipoLocal(DominioTipoAcomodacao tipoLocal) {
		this.tipoLocal = tipoLocal;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Integer getSeqJn() {
		return seqJn;
	}

	public void setSeqJn(Integer seqJn) {
		this.seqJn = seqJn;
	}

	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getSeqJn());
		umHashCodeBuilder.append(this.getDataAlt());
		umHashCodeBuilder.append(this.getUsuario());
		umHashCodeBuilder.append(this.getDescAcomodacaoJn());
		umHashCodeBuilder.append(this.getReserva());
		umHashCodeBuilder.append(this.getTipoLocal());
		umHashCodeBuilder.append(this.getSituacao());
		return umHashCodeBuilder.toHashCode();
	}	

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof HistoricoAcomodacaoJnVO)) {
			return false;
		}
		HistoricoAcomodacaoJnVO other = (HistoricoAcomodacaoJnVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getSeqJn(), other.getSeqJn());
		umEqualsBuilder.append(this.getDataAlt(), other.getDataAlt());
		umEqualsBuilder.append(this.getUsuario(), other.getUsuario());
		umEqualsBuilder.append(this.getDescAcomodacaoJn(), other.getDescAcomodacaoJn());
		umEqualsBuilder.append(this.getReserva(), other.getReserva());
		umEqualsBuilder.append(this.getTipoLocal(), other.getTipoLocal());
		umEqualsBuilder.append(this.getSituacao(), other.getSituacao());
		return umEqualsBuilder.isEquals();
		
	}
}