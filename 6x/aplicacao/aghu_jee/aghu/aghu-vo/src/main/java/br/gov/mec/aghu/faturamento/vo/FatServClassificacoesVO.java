package br.gov.mec.aghu.faturamento.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.faturamento.vo.FatTabRegistroServicoVO.TipoProcessado;
import br.gov.mec.aghu.model.FatServClassificacoes;

public class FatServClassificacoesVO {

	private Integer seq;

	private String servclas;

	private String classificacao;

	private TipoProcessado processado;

	private DominioSituacao situacao;

	private String dtCompetencia;

	private boolean indAlteracao;

	private FatServClassificacoes fatServclassificacao;

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getServclas() {
		return servclas;
	}

	public void setServclas(String servclas) {
		this.servclas = servclas;
	}

	public String getClassificacao() {
		return classificacao;
	}

	public void setClassificacao(String classificacao) {
		this.classificacao = classificacao;
	}

	public TipoProcessado getProcessado() {
		return processado;
	}

	public void setProcessado(TipoProcessado processado) {
		this.processado = processado;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public String getDtCompetencia() {
		return dtCompetencia;
	}

	public void setDtCompetencia(String dtCompetencia) {
		this.dtCompetencia = dtCompetencia;
	}

	public FatServClassificacoes getFatServclassificacao() {
		return fatServclassificacao;
	}

	public void setFatServclassificacao(
			FatServClassificacoes fatServclassificacao) {
		this.fatServclassificacao = fatServclassificacao;
	}

	public boolean isIndAlteracao() {
		return indAlteracao;
	}

	public void setIndAlteracao(boolean indAlteracao) {
		this.indAlteracao = indAlteracao;
	}
}