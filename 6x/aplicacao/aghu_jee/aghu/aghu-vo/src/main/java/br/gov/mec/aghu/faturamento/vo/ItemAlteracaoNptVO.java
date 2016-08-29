package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class ItemAlteracaoNptVO {
	
	//c_npts
	private Date alteradoEm;
	private Integer pnpAtdSeq;
	private Integer pnpSeq;
	private Integer atdSeq;
	private Integer seq;
	private Short pedSeq;
	private Integer pnp1AtdSeq;
	private Integer pnp1Seq;
	private Integer matricula;
	private Short vinCodigo;
	
	private Integer pacCodigo;
	private Date dtHrInicio;
	
	//cproc
	private Integer pprAtdSeq;
	private Long 	pprSeq;
	private Integer ppr1AtdSeq;
	private Long ppr1Seq;
	private Integer matCodigo;
	private Integer pciSeq;
	private Long pprPprSeq;
	
	public Date getAlteradoEm() {
		return alteradoEm;
	}
	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
	public Integer getPnpAtdSeq() {
		return pnpAtdSeq;
	}
	public void setPnpAtdSeq(Integer pnpAtdSeq) {
		this.pnpAtdSeq = pnpAtdSeq;
	}
	public Integer getPnpSeq() {
		return pnpSeq;
	}
	public void setPnpSeq(Integer pnpSeq) {
		this.pnpSeq = pnpSeq;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Short getPedSeq() {
		return pedSeq;
	}
	public void setPedSeq(Short pedSeq) {
		this.pedSeq = pedSeq;
	}
	public Integer getPnp1AtdSeq() {
		return pnp1AtdSeq;
	}
	public void setPnp1AtdSeq(Integer pnp1AtdSeq) {
		this.pnp1AtdSeq = pnp1AtdSeq;
	}
	public Integer getPnp1Seq() {
		return pnp1Seq;
	}
	public void setPnp1Seq(Integer pnp1Seq) {
		this.pnp1Seq = pnp1Seq;
	}
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public Short getVinCodigo() {
		return vinCodigo;
	}
	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public Integer getPciSeq() {
		return pciSeq;
	}
	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	public Integer getPprAtdSeq() {
		return pprAtdSeq;
	}
	public void setPprAtdSeq(Integer pprAtdSeq) {
		this.pprAtdSeq = pprAtdSeq;
	}
	public Long getPprSeq() {
		return pprSeq;
	}
	public void setPprSeq(Long pprSeq) {
		this.pprSeq = pprSeq;
	}
	public Integer getPpr1AtdSeq() {
		return ppr1AtdSeq;
	}
	public void setPpr1AtdSeq(Integer ppr1AtdSeq) {
		this.ppr1AtdSeq = ppr1AtdSeq;
	}
	public Long getPpr1Seq() {
		return ppr1Seq;
	}
	public void setPpr1Seq(Long ppr1Seq) {
		this.ppr1Seq = ppr1Seq;
	}
	public Long getPprPprSeq() {
		return pprPprSeq;
	}
	public void setPprPprSeq(Long pprPprSeq) {
		this.pprPprSeq = pprPprSeq;
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	public Date getDtHrInicio() {
		return dtHrInicio;
	}
	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}
	
	public enum Fields {
		ALTERADO_EM("alteradoEm"), 
		PNP_ATD_SEQ("pnpAtdSeq"), 
		PNP_SEQ("pnpSeq"), 
		ATD_SEQ("atdSeq"),
		SEQ("seq"),
		PED_SEQ("pedSeq"),
		PNP1_ATD_SEQ("pnp1AtdSeq"),
		PNP1_SEQ("pnp1Seq"),
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"), 
		MAT_CODIGO("matCodigo"),
		PCI_SEQ("pciSeq"),
		PPR_ATD_SEQ("pprAtdSeq"),
		PPR_PPR_SEQ("pprPprSeq"),
		PPR_SEQ("pprSeq"),
		PPR1_ATD_SEQ("ppr1AtdSeq"),
		PPR1_SEQ("ppr1Seq"),
		PAC_CODIGO("pacCodigo"),
		DTHR_INICIO("dtHrInicio")
		;

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
