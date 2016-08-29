package br.gov.mec.aghu.sicon.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.dominio.DominioClassificacaoErroXML;
import br.gov.mec.aghu.dominio.DominioStatusEnvio;
import br.gov.mec.aghu.model.ScoLogEnvioSicon;

public class SiconResponseVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2109786709606543741L;

	private DominioStatusEnvio statusEnvio;
	private DominioClassificacaoErroXML classificaoErroXML;
	private String codigoErro;
	private String descricaoErro;
	private List<EnvioItemVO> listEnvioItemVO;
	private ScoLogEnvioSicon logEnvioSicon;
	
	
	public DominioStatusEnvio getStatusEnvio() {
		return statusEnvio;
	}

	public void setStatusEnvio(DominioStatusEnvio statusEnvio) {
		this.statusEnvio = statusEnvio;
	}
	
	public DominioClassificacaoErroXML getClassificaoErroXML() {
		return classificaoErroXML;
	}

	public void setClassificaoErroXML(DominioClassificacaoErroXML classificaoErroXML) {
		this.classificaoErroXML = classificaoErroXML;
	}
	
	public String getCodigoErro() {
		return codigoErro;
	}

	public void setCodigoErro(String codigoErro) {
		this.codigoErro = codigoErro;
	}

	public String getDescricaoErro() {
		return descricaoErro;
	}

	public void setDescricaoErro(String descricaoErro) {
		this.descricaoErro = descricaoErro;
	}

	public List<EnvioItemVO> getListEnvioItemVO() {
		return listEnvioItemVO;
	}

	public void setListEnvioItemVO(List<EnvioItemVO> listEnvioItemVO) {
		this.listEnvioItemVO = listEnvioItemVO;
	}

	public ScoLogEnvioSicon getLogEnvioSicon() {
		return logEnvioSicon;
	}

	public void setLogEnvioSicon(ScoLogEnvioSicon logEnvioSicon) {
		this.logEnvioSicon = logEnvioSicon;
	}

}
