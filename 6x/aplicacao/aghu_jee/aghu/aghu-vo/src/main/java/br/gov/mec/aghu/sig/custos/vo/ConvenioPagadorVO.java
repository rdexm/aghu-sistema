package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.FatConvenioSaude;

public class ConvenioPagadorVO implements Serializable {
	
	private static final long serialVersionUID = -732847051529688047L;

	private AacPagador pagador;
	private FatConvenioSaude convenio;
	
	public ConvenioPagadorVO(AacPagador pagador) {
		this.pagador = pagador;
	}

	public ConvenioPagadorVO(FatConvenioSaude convenio) {
		this.convenio = convenio;
	}

	public AacPagador getPagador() {
		return pagador;
	}
	
	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}
	
	public FatConvenioSaude getConvenio() {
		return convenio;
	}
	
	public void setConvenio(FatConvenioSaude convenio) {
		this.convenio = convenio;
	}
	
	
	public Short getCodigo(){
		if(pagador != null){
			return pagador.getSeq();
		}
		else{
			return convenio.getCodigo();
		}
	}
	
	public String getDescricao(){
		if(pagador != null){
			return pagador.getDescricao();
		}
		else{
			return convenio.getDescricao();
		}
	}
	
	public String getGrupoConvenio(){
		if(pagador != null){
			return pagador.getGrupoConvenio().getDescricao();
		}
		else{
			return convenio.getGrupoConvenio().getDescricao();
		}
	}
	
	public String getObservacoes(){
		if(convenio != null){
			return convenio.getObservacoes();
		}
		return null;
	}
	
	public boolean isPagador(){
		return pagador != null;
	}
	
	public boolean isConvenio(){
		return convenio != null;
	}
}
