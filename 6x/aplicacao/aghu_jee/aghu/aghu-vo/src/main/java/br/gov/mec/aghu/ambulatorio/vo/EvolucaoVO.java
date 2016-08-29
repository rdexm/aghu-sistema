package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.MamTipoItemEvolucao;

public class EvolucaoVO implements Serializable {

	private static final long serialVersionUID = 1973453830524934530L;
	
	private Boolean render;
	private MamTipoItemEvolucao tipoItemEvolucao;
	private String texto;

	List<PreGeraItemQuestVO> listaPreGeraItemQuestVO;
	
	public Boolean getRender() {
		return render;
	}

	public void setRender(Boolean render) {
		this.render = render;
	}

	public MamTipoItemEvolucao getTipoItemEvolucao() {
		return tipoItemEvolucao;
	}

	public void setTipoItemEvolucao(MamTipoItemEvolucao tipoItemEvolucao) {
		this.tipoItemEvolucao = tipoItemEvolucao;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}

	public List<PreGeraItemQuestVO> getListaPreGeraItemQuestVO() {
		return listaPreGeraItemQuestVO;
	}

	public void setListaPreGeraItemQuestVO(
			List<PreGeraItemQuestVO> listaPreGeraItemQuestVO) {
		this.listaPreGeraItemQuestVO = listaPreGeraItemQuestVO;
	}
	
	
		
}
