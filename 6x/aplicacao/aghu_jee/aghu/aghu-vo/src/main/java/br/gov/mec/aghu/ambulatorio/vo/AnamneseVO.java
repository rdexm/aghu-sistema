package br.gov.mec.aghu.ambulatorio.vo;

import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.core.commons.BaseBean;

public class AnamneseVO implements BaseBean {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8969552694060453772L;

	private Boolean render;
	
	private MamTipoItemAnamneses tipoItemAnamnese;
	
	private String texto;

	public Boolean getRender() {
		return render;
	}

	public void setRender(Boolean render) {
		this.render = render;
	}

	public MamTipoItemAnamneses getTipoItemAnamnese() {
		return tipoItemAnamnese;
	}

	public void setTipoItemAnamnese(MamTipoItemAnamneses tipoItemAnamnese) {
		this.tipoItemAnamnese = tipoItemAnamnese;
	}

	public String getTexto() {
		return texto;
	}

	public void setTexto(String texto) {
		this.texto = texto;
	}
}
