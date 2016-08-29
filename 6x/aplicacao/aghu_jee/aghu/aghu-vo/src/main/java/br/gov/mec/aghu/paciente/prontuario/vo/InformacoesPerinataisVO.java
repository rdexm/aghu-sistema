package br.gov.mec.aghu.paciente.prontuario.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.core.commons.BaseBean;

public class InformacoesPerinataisVO implements BaseBean{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4861339563242148319L;
	
	private Integer pacCodigo;
	private Integer gsoPacCodigo;
	private Short gsoSeqp;
	private Byte rnaSeqp;
	private Integer atdSeq;
	private Byte gsoGesta;
	private Byte gsoPara;
	private Byte gsoCesArea;
	private Byte gsoAborto;
	private Date dthrInicio;
	private DominioOrigemAtendimento tipo;
	private Integer conNumero;	
	
	public enum Fields {
		PAC_CODIGO("pacCodigo"), 
		GSO_PAC_CODIGO("gsoPacCodigo"),
		GSO_SEQP("gsoSeqp"),
		RNA_SEQP("rnaSeqp"),
		ATD_SEQ("atdSeq"),
		GSO_GESTA("gsoGesta"),
		GSO_PARA("gsoPara"),
		GSO_CESAREA("gsoCesArea"),
		GSO_ABORTO("gsoAborto"),
		DTHR_INICIO("dthrInicio"),
		CON_NUMERO("conNumero"),
		TIPO("tipo");

			private String fields;

			private Fields(String fields) {
				this.fields = fields;
			}

			@Override
			public String toString() {
				return this.fields;
			}

		}
	
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Integer getGsoPacCodigo() {
		return gsoPacCodigo;
	}
	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}
	public Short getGsoSeqp() {
		return gsoSeqp;
	}
	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}
	public Byte getRnaSeqp() {
		return rnaSeqp;
	}
	public void setRnaSeqp(Byte rnaSeqp) {
		this.rnaSeqp = rnaSeqp;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Byte getGsoGesta() {
		return gsoGesta;
	}
	public void setGsoGesta(Byte gsoGesta) {
		this.gsoGesta = gsoGesta;
	}
	public Byte getGsoPara() {
		return gsoPara;
	}
	public void setGsoPara(Byte gsoPara) {
		this.gsoPara = gsoPara;
	}
	public Byte getGsoCesArea() {
		return gsoCesArea;
	}
	public void setGsoCesArea(Byte gsoCesArea) {
		this.gsoCesArea = gsoCesArea;
	}
	public Byte getGsoAborto() {
		return gsoAborto;
	}
	public void setGsoAborto(Byte gsoAborto) {
		this.gsoAborto = gsoAborto;
	}
	public Date getDthrInicio() {
		return dthrInicio;
	}
	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}
	public DominioOrigemAtendimento getTipo() {
		return tipo;
	}
	public void setTipo(DominioOrigemAtendimento tipo) {
		this.tipo = tipo;
	}
	public Integer getConNumero() {
		return conNumero;
	}
	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atdSeq == null) ? 0 : atdSeq.hashCode());
		result = prime * result
				+ ((conNumero == null) ? 0 : conNumero.hashCode());
		result = prime * result
				+ ((gsoPacCodigo == null) ? 0 : gsoPacCodigo.hashCode());
		result = prime * result + ((gsoSeqp == null) ? 0 : gsoSeqp.hashCode());
		result = prime * result
				+ ((pacCodigo == null) ? 0 : pacCodigo.hashCode());
		result = prime * result + ((rnaSeqp == null) ? 0 : rnaSeqp.hashCode());
		return result;
	}
	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}	
		if (obj == null){
			return false;
		}	
		if (!(obj instanceof InformacoesPerinataisVO)){
			return false;
		}	
		InformacoesPerinataisVO other = (InformacoesPerinataisVO) obj;
		if (atdSeq == null) {
			if (other.atdSeq != null){
				return false;
			}	
		} else if (!atdSeq.equals(other.atdSeq)){
			return false;
		}	
		if (conNumero == null) {
			if (other.conNumero != null){
				return false;
			}	
		} else if (!conNumero.equals(other.conNumero)){
			return false;
		}	
		if (gsoPacCodigo == null) {
			if (other.gsoPacCodigo != null){
				return false;
			}	
		} else if (!gsoPacCodigo.equals(other.gsoPacCodigo)){
			return false;
		}	
		if (gsoSeqp == null) {
			if (other.gsoSeqp != null){
				return false;
			}	
		} else if (!gsoSeqp.equals(other.gsoSeqp)){
			return false;
		}	
		if (pacCodigo == null) {
			if (other.pacCodigo != null){
				return false;
			}	
		} else if (!pacCodigo.equals(other.pacCodigo)){
			return false;
		}	
		if (rnaSeqp == null) {
			if (other.rnaSeqp != null){
				return false;
			}	
		} else if (!rnaSeqp.equals(other.rnaSeqp)){
			return false;
		}	
		return true;
	}
}