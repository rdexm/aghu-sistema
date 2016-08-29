package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.Date;

public class HistoricoNumeroUnicoVO implements Serializable {

	private static final long serialVersionUID = -5810377702656505765L;

	private String jnUser;
	private Date dataAlteracao;
	private Integer soeSeq;
	private Short seqp;
	private Integer nroUnico;
	private Date dtNumeroUnico;
	private String responsavel;
		
	public String getJnUser() {
		return jnUser;
	}
	public void setJnUser(String jnUser) {
		this.jnUser = jnUser;
	}
	public Date getDataAlteracao() {
		return dataAlteracao;
	}
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}
	public Integer getSoeSeq() {
		return soeSeq;
	}
	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}
	public Short getSeqp() {
		return seqp;
	}
	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}
	public Integer getNroUnico() {
		return nroUnico;
	}
	public void setNroUnico(Integer nroUnico) {
		this.nroUnico = nroUnico;
	}
	public Date getDtNumeroUnico() {
		return dtNumeroUnico;
	}
	public void setDtNumeroUnico(Date dtNumeroUnico) {
		this.dtNumeroUnico = dtNumeroUnico;
	}
	public String getResponsavel() {
		return responsavel;
	}
	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public enum Fields {
		JN_USER("jnUser"),
		DATA_ALTERACAO("dataAlteracao"),
		SOE_SEQ("soeSeq"),
		SEQP("seqp"),
		NRO_UNICO("nroUnico"),
		DT_NUMERO_UNICO("dtNumeroUnico")
		;
		
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
