package br.gov.mec.aghu.exames.solicitacao.vo;

import java.util.Date;

import br.gov.mec.aghu.model.AelItemSolicitacaoExames;

public class ExameOrdemCronologicaVO {

	private Date dtExame;
	private Short ordemNivel1;
	private Short ordemNivel2;
	private String descricaoUsual;
	private String matDescricao;
	private AelItemSolicitacaoExames itemSolicitacaoExame;
	private String siglaExame;
	private Integer manSeq;
	private Integer soeSeq;
	private Short seqp;
	
	public Date getDtExame() {
		return dtExame;
	}
	public void setDtExame(Date dtExame) {
		this.dtExame = dtExame;
	}
	public Short getOrdemNivel1() {
		return ordemNivel1;
	}
	public void setOrdemNivel1(Short ordemNivel1) {
		this.ordemNivel1 = ordemNivel1;
	}
	public Short getOrdemNivel2() {
		return ordemNivel2;
	}
	public void setOrdemNivel2(Short ordemNivel2) {
		this.ordemNivel2 = ordemNivel2;
	}
	public String getDescricaoUsual() {
		return descricaoUsual;
	}
	public void setDescricaoUsual(String descricaoUsual) {
		this.descricaoUsual = descricaoUsual;
	}
	public String getMatDescricao() {
		return matDescricao;
	}
	public void setMatDescricao(String matDescricao) {
		this.matDescricao = matDescricao;
	}
	public AelItemSolicitacaoExames getItemSolicitacaoExame() {
		return itemSolicitacaoExame;
	}
	public void setItemSolicitacaoExame(
			AelItemSolicitacaoExames itemSolicitacaoExame) {
		this.itemSolicitacaoExame = itemSolicitacaoExame;
	}
	
	public enum Fields {
		DT_EXAME("dtExame"),
		ORDEM_NIVEL1("ordemNivel1"),
		ORDEM_NIVEL2("ordemNivel2"),
		DESCRICAO_USUAL("descricaoUsual"),
		MAT_DESCRICAO("matDescricao"),
		ITEM_SOLICITACAO_EXAME("itemSolicitacaoExame"),
		SIGLA("siglaExame"),
		MAN_SEQ("manSeq"),
		SOLICITACAO_SEQ("soeSeq"),
		ITEM_SOLICITACAO_SEQ("seqp");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public String getSiglaExame() {
		return siglaExame;
	}
	public void setSiglaExame(String siglaExame) {
		this.siglaExame = siglaExame;
	}
	public Integer getManSeq() {
		return manSeq;
	}
	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
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
}
