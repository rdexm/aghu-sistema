package br.gov.mec.aghu.exames.solicitacao.vo;

import java.util.Date;

public class MensagemSolicitacaoExameReenvioGrupoVO extends MensagemSolicitacaoExameGrupoVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7895450398118020186L;

	@Override
	public TipoFilaExameInternet getTipoFila() {
		return TipoFilaExameInternet.REENVIO;
	}

	private Date reenviarEm;

	public Date getReenviarEm() {
		return reenviarEm;
	}

	public void setReenviarEm(Date reenviarEm) {
		this.reenviarEm = reenviarEm;
	}

	public Integer getSoeSeq() {
		return getSeqSolicitacaoExame();
	}

	public void setSoeSeq(Integer soeSeq) {
		setSeqSolicitacaoExame(soeSeq);
	}

	public Integer getGeiSeq() {
		return getSeqExameInternetGrupo();
	}

	public void setGeiSeq(Integer geiSeq) {
		setSeqExameInternetGrupo(geiSeq);
	}

}
