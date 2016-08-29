package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PortalPlanejamentoCirurgiasSalaVO implements Serializable {
	
	
	private static final long serialVersionUID = -8330973447112863782L;
	
	private Short numeroSala;
	private List<PortalPlanejamentoCirurgiasTurnoVO> listaTurnos;
	private List<PortalPlanejamentoCirurgiasTurno2VO> listaTurnos2 = new ArrayList<PortalPlanejamentoCirurgiasTurno2VO>();
	private Short unfSeq;

	public PortalPlanejamentoCirurgiasSalaVO() {
		super();
	}
	
	public PortalPlanejamentoCirurgiasSalaVO(Short numeroSala) {
		super();
		this.numeroSala = numeroSala;
	}

	public PortalPlanejamentoCirurgiasSalaVO(Short numeroSala, Short unfSeq) {
		super();
		this.numeroSala = numeroSala;
		this.unfSeq = unfSeq;
	}

	public Short getNumeroSala() {
		return numeroSala;
	}
	public void setNumeroSala(Short numeroSala) {
		this.numeroSala = numeroSala;
	}
	public List<PortalPlanejamentoCirurgiasTurnoVO> getListaTurnos() {
		return listaTurnos;
	}
	public void setListaTurnos(List<PortalPlanejamentoCirurgiasTurnoVO> listaTurnos) {
		this.listaTurnos = listaTurnos;
	}
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public List<PortalPlanejamentoCirurgiasTurno2VO> getListaTurnos2() {
		return listaTurnos2;
	}
	public void setListaTurnos2(
			List<PortalPlanejamentoCirurgiasTurno2VO> listaTurnos2) {
		this.listaTurnos2 = listaTurnos2;
	}	
	
	public enum Fields {
		SALA("numeroSala");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
