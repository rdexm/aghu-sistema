package br.gov.mec.aghu.sig.custos.vo;

import java.util.HashMap;
import java.util.Map;

import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdConsumo;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.model.SigProcessamentoPassos;

public class SigProcessamentoContagemVO {
	
	private SigProcessamentoCusto sigProcessamentoCusto;
	private RapServidores rapServidores;
	private SigProcessamentoPassos sigProcessamentoPassos;
	private Map<String, SigCalculoAtdConsumo> mapCalculosConsumo;
	private String mensagem;
		
	public SigProcessamentoContagemVO(){}

	public SigProcessamentoCusto getSigProcessamentoCusto() {
		return sigProcessamentoCusto;
	}

	public void setSigProcessamentoCusto(SigProcessamentoCusto sigProcessamentoCusto) {
		this.sigProcessamentoCusto = sigProcessamentoCusto;
	}

	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public SigProcessamentoPassos getSigProcessamentoPassos() {
		return sigProcessamentoPassos;
	}

	public void setSigProcessamentoPassos(
			SigProcessamentoPassos sigProcessamentoPassos) {
		this.sigProcessamentoPassos = sigProcessamentoPassos;
	}

	public Map<String, SigCalculoAtdConsumo> getMapCalculosConsumo() {
		if(mapCalculosConsumo == null){
			mapCalculosConsumo = new HashMap<String, SigCalculoAtdConsumo>();
		}
		return mapCalculosConsumo;
	}

	public void setMapCalculosConsumo(
			Map<String, SigCalculoAtdConsumo> mapCalculosConsumo) {
		this.mapCalculosConsumo = mapCalculosConsumo;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}
}
