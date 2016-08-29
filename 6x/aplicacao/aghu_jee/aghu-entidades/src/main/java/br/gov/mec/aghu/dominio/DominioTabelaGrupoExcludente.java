package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;


public enum DominioTabelaGrupoExcludente implements Dominio{
	
	UM(1, "#FED189"),
	DOIS(2, "#C5D7ED"),
	TRES(3, "#D5E2B7"),
	QUATRO(4, "#C6B9E4"),
	CINCO(5, "#AFE8DB"),
	SEIS(6, "#EF984A"),
	SETE(7, "#6AD3E5"),
	OITO(8, "#3D8699"),
	NOVE(9, "#DDC0A8"),
	DEZ(10, "#FBDD6F"),
	ONZE(11, "#F3E800"),
	DOZE(12, "#C6C6BC"),
	TREZE(13, "#C2B2B5"),
	QUATORZE(14, "#D3C89D");
	
	private Integer codigo;
	private String cor;
	
	DominioTabelaGrupoExcludente(Integer codigo, String cor) {
		this.codigo = codigo;
		this.cor = cor;
	}
	
	@Override
	public int getCodigo() {
		return codigo;
	}
	@Override
	public String getDescricao() {
		return cor;
	}
	
	public static DominioTabelaGrupoExcludente getInstance(Integer codigo) {
		switch (codigo) {
		case 1:
			return DominioTabelaGrupoExcludente.UM;
		case 2:
			return DominioTabelaGrupoExcludente.DOIS;
		case 3:
			return DominioTabelaGrupoExcludente.TRES;
		case 4:
			return DominioTabelaGrupoExcludente.QUATRO;
		case 5:
			return DominioTabelaGrupoExcludente.CINCO;
		case 6:
			return DominioTabelaGrupoExcludente.SEIS;
		case 7:
			return DominioTabelaGrupoExcludente.SETE;
		case 8:
			return DominioTabelaGrupoExcludente.OITO;
		case 9:
			return DominioTabelaGrupoExcludente.NOVE;
		case 10:
			return DominioTabelaGrupoExcludente.DEZ;
		case 11:
			return DominioTabelaGrupoExcludente.ONZE;
		case 12:
			return DominioTabelaGrupoExcludente.DOZE;
		case 13:
			return DominioTabelaGrupoExcludente.TREZE;
		case 14:
			return DominioTabelaGrupoExcludente.QUATORZE;
		default:
			return null;
		}
	}
	
}
