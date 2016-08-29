package br.gov.mec.aghu.ambulatorio.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.BaseBean;

public class GeraAnamneseVO implements BaseBean {

	private static final long serialVersionUID = 5932000198491143892L;
	
	private Long anaSeq;
	private Long anaAnaSeq;
	private DominioSimNao replicaAnamnese;
	private DominioSimNao executaPreGera;
	private RapServidores servidor;
	private Date sysdate;
	
	public enum Fields {
		ANA_SEQ("anaSeq"), 
		ANA_ANA_SEQ("anaAnaSeq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Long getAnaSeq() {
		return anaSeq;
	}

	public void setAnaSeq(Long anaSeq) {
		this.anaSeq = anaSeq;
	}

	public Long getAnaAnaSeq() {
		return anaAnaSeq;
	}

	public void setAnaAnaSeq(Long anaAnaSeq) {
		this.anaAnaSeq = anaAnaSeq;
	}

	public DominioSimNao getReplicaAnamnese() {
		return replicaAnamnese;
	}

	public void setReplicaAnamnese(DominioSimNao replicaAnamnese) {
		this.replicaAnamnese = replicaAnamnese;
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
