package br.gov.mec.aghu.dominio;

/**
 * Situacao Codigo da entidade AelItemSolicitacaoExames.
 * 
 * @author rcorvalao
 *
 */
public enum DominioSituacaoItemSolicitacaoExame implements br.gov.mec.aghu.core.dominio.DominioString {
	AC	//A COLETAR	
	,AX	//A EXECUTAR
	,AG	//AGENDADO	
	,AE	//AREA EXECUTORA	
	,CA	//CANCELADO	
	,CO	//COLETADO	
	,CS	//COLETADO PELO SOLICITANTE	
	,EC	//EM COLETA	
	,EO	//EXECUTADO	
	,EX	//EXECUTANDO	
	,LI	//LIBERADO	
	,RE	//RECEBIDO	
	,PE	//PENDENTE
	;
	
	@Override
	public String getCodigo() {
		switch (this) {
		case AC:
			return "AC";
		case AX:
			return "AX";
		case AG:
			return "AG";
		case AE:
			return "AE";
		case CA:
			return "CA";
		case CO:
			return "CO";
		case CS:
			return "CS";
		case EC:
			return "EC";
		case EO:
			return "EO";
		case EX:
			return "EX";
		case LI:
			return "LI";
		case RE:
			return "RE";
		case PE:
			return "PE";
		default:
			return "";
		}
	}

	@Override
	public String getDescricao() {
		// return this.toString();
		switch (this) {
		case AC:
			return "A Coletar";
		case AX:
			return "A Executar";
		case AG:
			return "Agendado";
		case AE:
			return "Area Executora";
		case CA:
			return "Cancelado";
		case CO:
			return "Coletado";
		case CS:
			return "Coletado pelo Solicitante";
		case EC:
			return "Em Coleta";
		case EO:
			return "Executado";
		case EX:
			return "Executando";
		case LI:
			return "Liberado";
		case RE:
			return "Recebido";
		case PE:
			return "Pendente";
		default:
			return "";
		}
	}
}
