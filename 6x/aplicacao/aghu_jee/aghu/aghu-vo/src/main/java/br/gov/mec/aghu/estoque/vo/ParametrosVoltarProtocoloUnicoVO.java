package br.gov.mec.aghu.estoque.vo;

import br.gov.mec.aghu.model.AghParametros;

public class ParametrosVoltarProtocoloUnicoVO {

	private AghParametros parametroSituacaoExecutando; 
	private AghParametros parametroAreaExecutora;
	private AghParametros parametroSituacaoLiberado;
	private AghParametros parametroSituacaoCancelado; 
	private AghParametros parametroMocCancelaDept;

	public AghParametros getParametroSituacaoExecutando() {
		return parametroSituacaoExecutando;
	}
	public void setParametroSituacaoExecutando(
			AghParametros parametroSituacaoExecutando) {
		this.parametroSituacaoExecutando = parametroSituacaoExecutando;
	}
	public AghParametros getParametroAreaExecutora() {
		return parametroAreaExecutora;
	}
	public void setParametroAreaExecutora(AghParametros parametroAreaExecutora) {
		this.parametroAreaExecutora = parametroAreaExecutora;
	}
	public AghParametros getParametroSituacaoLiberado() {
		return parametroSituacaoLiberado;
	}
	public void setParametroSituacaoLiberado(AghParametros parametroSituacaoLiberado) {
		this.parametroSituacaoLiberado = parametroSituacaoLiberado;
	}
	public AghParametros getParametroSituacaoCancelado() {
		return parametroSituacaoCancelado;
	}
	public void setParametroSituacaoCancelado(
			AghParametros parametroSituacaoCancelado) {
		this.parametroSituacaoCancelado = parametroSituacaoCancelado;
	}
	public AghParametros getParametroMocCancelaDept() {
		return parametroMocCancelaDept;
	}
	public void setParametroMocCancelaDept(AghParametros parametroMocCancelaDept) {
		this.parametroMocCancelaDept = parametroMocCancelaDept;
	}


}
