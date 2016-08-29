package br.gov.mec.aghu.paciente.vo;

import br.gov.mec.aghu.dominio.DominioTodosUltimo;

public class SolicitanteVO {

	private Short seq;
	
	private DominioTodosUltimo volumesManuseados;
	
	private String zonaSala;
	
	public Short getSeq() {
		return seq;
	}
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	public DominioTodosUltimo getVolumesManuseados() {
		return volumesManuseados;
	}
	public void setVolumesManuseados(DominioTodosUltimo volumesManuseados) {
		this.volumesManuseados = volumesManuseados;
	}
	public String getZonaSala() {
		return zonaSala;
	}
	public void setZonaSala(String zonaSala) {
		this.zonaSala = zonaSala;
	}
	
	
}
