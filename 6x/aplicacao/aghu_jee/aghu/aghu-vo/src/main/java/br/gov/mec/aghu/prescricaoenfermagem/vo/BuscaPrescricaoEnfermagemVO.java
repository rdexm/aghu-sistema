package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;


public class BuscaPrescricaoEnfermagemVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3481960466410914799L;

	private Date dataHoraInicio;

	private Date dataHoraFim;

	private DominioSituacaoPrescricao situacao;

	private Integer seqPrescricaoEnfermagem;

	private Date dataHoraMovimentoPendente;

	private Date dataHoraMovimento;

	private Date dataReferencia;

	public BuscaPrescricaoEnfermagemVO() {
	}

	public BuscaPrescricaoEnfermagemVO(Date dataHoraInicio, Date dataHoraFim,
			DominioSituacaoPrescricao situacao, Integer seqPrescricaoEnfermagem,
			Date dataHoraMovimentoPendente, Date dataHoraMovimento,
			Date dataReferencia) {
		this.dataHoraInicio = dataHoraInicio;
		this.dataHoraFim = dataHoraFim;
		this.situacao = situacao;
		this.seqPrescricaoEnfermagem = seqPrescricaoEnfermagem;
		this.dataHoraMovimentoPendente = dataHoraMovimentoPendente;
		this.dataHoraMovimento = dataHoraMovimento;
		this.dataReferencia = dataReferencia;
	}

	public Date getDataHoraInicio() {
		return dataHoraInicio;
	}

	public void setDataHoraInicio(Date dataHoraInicio) {
		this.dataHoraInicio = dataHoraInicio;
	}

	public Date getDataHoraFim() {
		return dataHoraFim;
	}

	public void setDataHoraFim(Date dataHoraFim) {
		this.dataHoraFim = dataHoraFim;
	}

	public DominioSituacaoPrescricao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoPrescricao situacao) {
		this.situacao = situacao;
	}

	public Integer getSeqPrescricaoEnfermagem() {
		return seqPrescricaoEnfermagem;
	}

	public void setSeqPrescricaoEnfermagem(Integer seqPrescricaoEnfermagem) {
		this.seqPrescricaoEnfermagem = seqPrescricaoEnfermagem;
	}

	public Date getDataHoraMovimentoPendente() {
		return dataHoraMovimentoPendente;
	}

	public void setDataHoraMovimentoPendente(
			Date dataHoraMovimentoPendente) {
		this.dataHoraMovimentoPendente = dataHoraMovimentoPendente;
	}

	public Date getDataHoraMovimento() {
		return dataHoraMovimento;
	}

	public void setDataHoraMovimento(Date dataHoraMovimento) {
		this.dataHoraMovimento = dataHoraMovimento;
	}

	public Date getDataReferencia() {
		return dataReferencia;
	}

	public void setDataReferencia(Date dataReferencia) {
		this.dataReferencia = dataReferencia;
	}

}
