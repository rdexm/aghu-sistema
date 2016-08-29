package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.DominioString;

public enum DominioTipoDocumentoFiscalEntrada implements DominioString{
	/**
	 */
	NFS,
	/**
	 */
	DFE, 
	/**
	 */
	DFS;

	@Override
	public String getCodigo() {
		return this.toString();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case NFS:
			return "NFS";
		case DFE:
			return "DFE";
		case DFS:
			return "DFS";
			
		default:
			return "";
		}
	}
}
