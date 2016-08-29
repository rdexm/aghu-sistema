package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica um tipo de atuação profissional
 * 
 * @author lcmoura
 *
 */
public enum DominioTipoAtuacao implements Dominio {
	
//	'RESP','1','SUBS','2','SUP','3','CIRG','4','AUX','5','ANES','6','ENF','7','OUTR','8'
//	NÃO ALTERAR A ORDEM ,porque irá alterar o getOrdinal e o mesmo é utilizado para ordenacao em outro momento
	
	
	/**
	 * Responsável
	 */
	RESP, 
	
	/**
	 * Substituto
	 */
	SUBS,
	
	/**
	 * Supervisor
	 */
	SUP, 
	
	/**
	 * Cirurgião
	 */
	CIRG, 
	
	/**
	 * Auxiliar
	 */
	AUX, 
	
	/**
	 * Anestesista
	 */
	ANES, 
	
	/**
	 * Enfermagem
	 */
	ENF, 
	
	/**
	 * Outros
	 */
	OUTR, 
	
	/**
	 * Executor Sedação
	 */
	ESE
	;

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {	
		case RESP:
			return "Responsável";
		case CIRG:
			return "Cirurgião";
		case AUX:
			return "Auxiliar";
		case ANES:
			return "Anestesista";
		case ENF:
			return "Enfermagem";
		case OUTR:
			return "Outros";
		case SUP:
			return "Supervisor";
		case SUBS:
			return "Substituto";
		case ESE:
			return "Executor Sedação";
		default:
			return "";
		}
	}
	
	public String getDescricaoProf() {
		switch (this) {	
		case RESP:
			return "Equipe";
		case SUBS:
			return "Prof. Substituto";
		default:
			return getDescricao();
		}
	}
	
	public String getDescricaoPdtProf() {
		switch (this) {	
		case CIRG:
			return "Executor";		
		default:
			return getDescricaoProf();
		}
	}
	
	public Integer getOrder() {
		switch (this) {	
		case RESP:
			return 1;
		case SUBS:
			return 2;
		case SUP:
			return 3;	
		case CIRG:
			return 4;
		case AUX:
			return 5;	
		case ANES:
			return 6;
		case ENF:
			return 7;
		case OUTR:
			return 8;
		default:
			return 99;
		}
	}
	
	
}
