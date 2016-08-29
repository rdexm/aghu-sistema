package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.RapServidores;

public class SolicitacaoCompraMaterialVO implements Serializable {

	private static final long serialVersionUID = 8395069922104949691L;

	private InfoProcdCirgRequisicaoOPMEVO infoReqOPME;
	private MateriaisProcedimentoOPMEVO matProcOPME;
	private RapServidores servidorLogado;

	public InfoProcdCirgRequisicaoOPMEVO getInfoReqOPME() {
		return infoReqOPME;
	}

	public void setInfoReqOPME(InfoProcdCirgRequisicaoOPMEVO infoReqOPME) {
		this.infoReqOPME = infoReqOPME;
	}

	public MateriaisProcedimentoOPMEVO getMatProcOPME() {
		return matProcOPME;
	}

	public void setMatProcOPME(MateriaisProcedimentoOPMEVO matProcOPME) {
		this.matProcOPME = matProcOPME;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

}
