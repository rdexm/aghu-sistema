package br.gov.mec.aghu.suprimentos.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ScoLocalizacaoProcessoComprasVO implements BaseBean {

	private static final long serialVersionUID = -7546427736042946795L;

	private Integer protocolo;
	private Short localizacaoSeq;
	private String localizacaoDesc;
	private String localizacao;
	private Integer nroPAC;
	private String modalidadeDesc;
	private String modalidade;
	private String descricao;
	private Integer nroAF;
	private Short cp;
	DominioSituacaoAutorizacaoFornecimento situacao;
	Date dtEntrada;
	String responsavel;
	Integer ramal;
	Integer diasPerm;

	public ScoLocalizacaoProcessoComprasVO() {

	}

	public enum Fields {
		PROTOCOLO("protocolo"), LOCALIZACAO_SEQ("localizacaoSeq"), LOCALIZACAO_DESC("localizacaoDesc"), LOCALIZACAO("localizacao"), NRO_PAC(
				"nroPAC"), MODALIDADE_DESC("modalidadeDesc"), MODALIDADE("modalidade"), DESCRICAO("descricao"), NRO_AF("nroAF"), CP("cp"), SITUACAO(
				"situacao"), DT_ENTRADA("dtEntrada"), RESPONSAVEL("responsavel"), RAMAL("ramal");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(Integer protocolo) {
		this.protocolo = protocolo;
	}

	public Short getLocalizacaoSeq() {
		return localizacaoSeq;
	}

	public void setLocalizacaoSeq(Short localizacaoSeq) {
		this.localizacaoSeq = localizacaoSeq;
	}

	public String getLocalizacaoDesc() {
		return localizacaoDesc;
	}

	public void setLocalizacaoDesc(String localizacaoDesc) {
		this.localizacaoDesc = localizacaoDesc;
	}

	public String getLocalizacao() {
		if (getLocalizacaoSeq() != null) {
			localizacao = getLocalizacaoSeq().toString() + " - " + getLocalizacaoDesc();
		}
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public Integer getNroPAC() {
		return nroPAC;
	}

	public void setNroPAC(Integer nroPAC) {
		this.nroPAC = nroPAC;
	}

	public String getModalidadeDesc() {
		return modalidadeDesc;
	}

	public void setModalidadeDesc(String modalidadeDesc) {
		this.modalidadeDesc = modalidadeDesc;
	}

	public String getModalidade() {
		String modConcat = null;
		if (modalidade != null) {
			modConcat = modalidade + " - " + getModalidadeDesc();
		}
		return modConcat;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public Integer getNroAF() {
		return nroAF;
	}

	public void setNroAF(Integer nroAF) {
		this.nroAF = nroAF;
	}

	public Short getCp() {
		return cp;
	}

	public void setCp(Short cp) {
		this.cp = cp;
	}

	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
	}

	public Date getDtEntrada() {
		return dtEntrada;
	}

	public void setDtEntrada(Date dtEntrada) {
		this.dtEntrada = dtEntrada;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public Integer getRamal() {
		return ramal;
	}

	public void setRamal(Integer ramal) {
		this.ramal = ramal;
	}

	public Integer getDiasPerm() {
		Integer difDatas = DateUtil.calcularDiasEntreDatas(getDtEntrada(), new Date());
		setDiasPerm(difDatas);
		return diasPerm;
	}

	public void setDiasPerm(Integer diasPerm) {
		this.diasPerm = diasPerm;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
