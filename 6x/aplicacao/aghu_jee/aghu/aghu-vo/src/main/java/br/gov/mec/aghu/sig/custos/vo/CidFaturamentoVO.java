package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.model.AghCid;


public class CidFaturamentoVO implements Serializable {
	
	private static final long serialVersionUID = 5339954567542543039L;
	private AghCid cid;
	private DominioPrioridadeCid prioridadeCid;
	
	public enum Fields {
		CID ("cid"),
		PRIORIDADE_CID("prioridadeCid");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	public AghCid getCid() {
		return cid;
	}
	
	public void setCid(AghCid cid) {
		this.cid = cid;
	}
	
	public DominioPrioridadeCid getPrioridadeCid() {
		return prioridadeCid;
	}
	
	public void setPrioridadeCid(DominioPrioridadeCid prioridadeCid) {
		this.prioridadeCid = prioridadeCid;
	}
}
