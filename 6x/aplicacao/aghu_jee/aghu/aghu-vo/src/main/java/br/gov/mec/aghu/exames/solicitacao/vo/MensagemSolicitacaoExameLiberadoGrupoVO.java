package br.gov.mec.aghu.exames.solicitacao.vo;


public class MensagemSolicitacaoExameLiberadoGrupoVO extends MensagemSolicitacaoExameGrupoVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7895450398118020186L;

	@Override
	public TipoFilaExameInternet getTipoFila() {
		return TipoFilaExameInternet.NOVO;
	}

}
