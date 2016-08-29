package br.gov.mec.aghu.ambulatorio.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.BaseBean;

public class GeraEvolucaoVO implements BaseBean {

	private static final long serialVersionUID = -4961167409092621660L;

	private Long evoSeq;
	private Long evoEvoSeq;
	private DominioSimNao replicaEvolucao;
	private DominioSimNao executaPreGera;
	private RapServidores servidor;
	private Date sysdate;

	public enum Fields {
		EVO_SEQ("evoSeq"), EVO_EVO_SEQ("evoEvoSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Long getEvoSeq() {
		return evoSeq;
	}

	public void setEvoSeq(Long evoSeq) {
		this.evoSeq = evoSeq;
	}

	public Long getEvoEvoSeq() {
		return evoEvoSeq;
	}

	public void setEvoEvoSeq(Long evoEvoSeq) {
		this.evoEvoSeq = evoEvoSeq;
	}

	public DominioSimNao getReplicaEvolucao() {
		return replicaEvolucao;
	}

	public void setReplicaEvolucao(DominioSimNao replicaEvolucao) {
		this.replicaEvolucao = replicaEvolucao;
	}

	public DominioSimNao getExecutaPreGera() {
		return executaPreGera;
	}

	public void setExecutaPreGera(DominioSimNao executaPreGera) {
		this.executaPreGera = executaPreGera;
	}

	public Date getSysdate() {
		return sysdate;
	}

	public void setSysdate(Date sysdate) {
		this.sysdate = sysdate;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
}
