package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica a situação do titulo
 * 
 * @author aghu
 * 
 */
public enum DominioMotivoEstornoTitulo implements Dominio {
	DF, NR, OT;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case DF:
			return "Devolução Fornecedor";
		case NR:
			return "Nota Recebimento";
		case OT:
			return "Outros";
		default:
			return "";
		}
	}

	public static DominioMotivoEstornoTitulo getInstance(String value) {
		if (value != null && value.equalsIgnoreCase("DF")) {
			return DominioMotivoEstornoTitulo.DF;
		}
		if (value != null && value.equalsIgnoreCase("NR")) {
			return DominioMotivoEstornoTitulo.NR;
		}
		if (value != null && value.equalsIgnoreCase("OT")) {
			return DominioMotivoEstornoTitulo.OT;
		}
		return null;
	}
	
	@Override
	public String toString() {
		return getDescricao();
	}
	
}
