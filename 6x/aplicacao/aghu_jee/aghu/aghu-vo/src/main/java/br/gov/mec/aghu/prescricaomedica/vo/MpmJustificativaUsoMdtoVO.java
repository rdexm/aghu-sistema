package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoMedicamento;

public class MpmJustificativaUsoMdtoVO {

	private Integer seq;
	private Integer atdSeq;
	private Short gupSeq;
	private DominioSituacaoSolicitacaoMedicamento situacao;
	private Boolean candAprovLote;
	private Boolean gestante;
	private Date criadoEm;
	private Date alteradoEm;
	private Integer serMatriculaConhecimento;
	private Short serVinCodigoConhecimento;
	
	public MpmJustificativaUsoMdtoVO() {
	
	}
	
	public MpmJustificativaUsoMdtoVO(Integer seq, Integer atdSeq, Short gupSeq,
			DominioSituacaoSolicitacaoMedicamento situacao,
			Boolean candAprovLote, Boolean gestante, Date criadoEm,
			Date alteradoEm, Integer serMatriculaConhecimento,
			Short serVinCodigoConhecimento) {
		super();
		this.seq = seq;
		this.atdSeq = atdSeq;
		this.gupSeq = gupSeq;
		this.situacao = situacao;
		this.candAprovLote = candAprovLote;
		this.gestante = gestante;
		this.criadoEm = criadoEm;
		this.alteradoEm = alteradoEm;
		this.serMatriculaConhecimento = serMatriculaConhecimento;
		this.serVinCodigoConhecimento = serVinCodigoConhecimento;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Short getGupSeq() {
		return gupSeq;
	}

	public void setGupSeq(Short gupSeq) {
		this.gupSeq = gupSeq;
	}

	public DominioSituacaoSolicitacaoMedicamento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoSolicitacaoMedicamento situacao) {
		this.situacao = situacao;
	}

	public Boolean getCandAprovLote() {
		return candAprovLote;
	}

	public void setCandAprovLote(Boolean candAprovLote) {
		this.candAprovLote = candAprovLote;
	}

	public Boolean getGestante() {
		return gestante;
	}

	public void setGestante(Boolean gestante) {
		this.gestante = gestante;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Date getAlteradoEm() {
		return alteradoEm;
	}

	public void setAlteradoEm(Date alteradoEm) {
		this.alteradoEm = alteradoEm;
	}

	public Integer getSerMatriculaConhecimento() {
		return serMatriculaConhecimento;
	}

	public void setSerMatriculaConhecimento(Integer serMatriculaConhecimento) {
		this.serMatriculaConhecimento = serMatriculaConhecimento;
	}

	public Short getSerVinCodigoConhecimento() {
		return serVinCodigoConhecimento;
	}

	public void setSerVinCodigoConhecimento(Short serVinCodigoConhecimento) {
		this.serVinCodigoConhecimento = serVinCodigoConhecimento;
	}

	public enum Fields {
		SEQ("seq"), 
		ATD_SEQ("atdSeq"), 
		GUP_SEQ("gupSeq"), 
		SITUACAO("situacao"),
		CAND_APROV_LOTE("candAprovLote"),
		GESTANTE("gestante"), 
		CRIADO_EM("criadoEm"),
		ALTERADO_EM("alteradoEm"),
		SER_MATRICULA_CONHECIMENTO("serMatriculaConhecimento"),
		SER_VIN_CODIGO_CONHECIMENTO("serVinCodigoConhecimento");

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
