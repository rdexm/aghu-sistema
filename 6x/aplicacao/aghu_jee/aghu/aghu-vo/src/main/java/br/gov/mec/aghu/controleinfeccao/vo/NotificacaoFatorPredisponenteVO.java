package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class NotificacaoFatorPredisponenteVO implements BaseBean {

	private static final long serialVersionUID = 8449010552707373999L;

	private Integer seq;
	private Integer atdSeq;
	private Integer pacCodigo;
	private Date dtInicio;
	private Date dtFim;
	private Short fpdSeq;
	private String descricao;
	private Short unfSeq;
	private Short qrtNumero;
	private String ltoLtoId;
	private Short unfSeqNotificado;
	private Short qrtNumeroNotificado;
	private String ltoLtoIdNotificado;
	private Date criadoEm;
	private Integer matricula;
	private Short vinCodigo;
	private Integer matriculaEncerrado;
	private Short vinCodigoEncerrado;
	private Date dthrFim;
	private Integer numero;

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Date getDtInicio() {
		return dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	public Short getFpdSeq() {
		return fpdSeq;
	}

	public void setFpdSeq(Short fpdSeq) {
		this.fpdSeq = fpdSeq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Short getQrtNumero() {
		return qrtNumero;
	}

	public void setQrtNumero(Short qrtNumero) {
		this.qrtNumero = qrtNumero;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public Short getUnfSeqNotificado() {
		return unfSeqNotificado;
	}

	public void setUnfSeqNotificado(Short unfSeqNotificado) {
		this.unfSeqNotificado = unfSeqNotificado;
	}

	public Short getQrtNumeroNotificado() {
		return qrtNumeroNotificado;
	}

	public void setQrtNumeroNotificado(Short qrtNumeroNotificado) {
		this.qrtNumeroNotificado = qrtNumeroNotificado;
	}

	public String getLtoLtoIdNotificado() {
		return ltoLtoIdNotificado;
	}

	public void setLtoLtoIdNotificado(String ltoLtoIdNotificado) {
		this.ltoLtoIdNotificado = ltoLtoIdNotificado;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
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

	public Integer getMatriculaEncerrado() {
		return matriculaEncerrado;
	}

	public void setMatriculaEncerrado(Integer matriculaEncerrado) {
		this.matriculaEncerrado = matriculaEncerrado;
	}

	public Short getVinCodigoEncerrado() {
		return vinCodigoEncerrado;
	}

	public void setVinCodigoEncerrado(Short vinCodigoEncerrado) {
		this.vinCodigoEncerrado = vinCodigoEncerrado;
	}

	public Date getDtFim() {
		return dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}
	
	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}





	public enum Fields {
		DTHR_FIM("dthrFim"),
		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		LTO_LTO_ID_NOTIFICADO("ltoLtoIdNotificado"),
		QRT_NUMERO_NOTIFICADO("qrtNumeroNotificado"),
		UNF_SEQ_NOTIFICADO("unfSeqNotificado"),
		LTO_LTO_ID("ltoLtoId"),
		QRT_NUMERO("qrtNumero"),
		UNF_SEQ("unfSeq"),
		DESCRICAO("descricao"),
		FPD_SEQ("fpdSeq"),
		PACIENTE_CODIGO("pacCodigo"),
		DATA_INICIO("dtInicio"),
		DATA_FIM("dtFim"),
		ATENDIMENTO_SEQ("atdSeq"),
		MATRICULA("matricula"), 
		CODIGO_VINCULO("vinCodigo"), 
		MATRICULA_ENC("matriculaEncerrado"),
		NUMERO("numero"),
		CODIGO_VINCULO_ENC("vinCodigoEncerrado");

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
