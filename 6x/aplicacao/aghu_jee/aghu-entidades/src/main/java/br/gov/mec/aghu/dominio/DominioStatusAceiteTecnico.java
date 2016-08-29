package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioStatusAceiteTecnico implements Dominio {
	
	RECEBIDO(1),
	SOLICITADA_AVALIACAO_TECNICA(2),
	EM_AVALIACAO_TECNICA(3),
	TROCA_AREA_AVALIACAO_TECNICA(4),
	AGUARDANDO_FORNECEDOR(5),
	NAO_CONFORME(6),
	CONCLUIDA(7),
	DEVOLVIDO(8),
	PARCIALMENTE_ACEITO(9),
	ACEITO(10);

	private int value;
	
	DominioStatusAceiteTecnico(int codigo) {
		this.value = codigo;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case RECEBIDO:
			return "Recebido";
		case SOLICITADA_AVALIACAO_TECNICA:
			return "Solicitada Avaliação Técnica";
		case EM_AVALIACAO_TECNICA:
			return "Em Avaliação Técnica";
		case TROCA_AREA_AVALIACAO_TECNICA:
			return "Troca de Área Avaliação Técnica";
		case AGUARDANDO_FORNECEDOR:
			return "Aguardando Fornecedor";
		case NAO_CONFORME:
			return "Não Conforme";
		case CONCLUIDA:
			return "Concluída";
		case DEVOLVIDO:
			return "Devolvido";
		case PARCIALMENTE_ACEITO:
			return "Parcialmente Aceito";
		case ACEITO:
			return "Aceito";
		default:
			return "";
		}
	}
	
	public static DominioStatusAceiteTecnico obterDominioStatusAceiteTecnico(Integer value) {
		DominioStatusAceiteTecnico status;
		switch (value) {
		case 1:
			status = DominioStatusAceiteTecnico.RECEBIDO;
			break;
		case 2:
			status = DominioStatusAceiteTecnico.SOLICITADA_AVALIACAO_TECNICA;
			break;
		case 3:
			status = DominioStatusAceiteTecnico.EM_AVALIACAO_TECNICA;
			break;
		case 4:
			status = DominioStatusAceiteTecnico.TROCA_AREA_AVALIACAO_TECNICA;
			break;
		case 5:
			status = DominioStatusAceiteTecnico.AGUARDANDO_FORNECEDOR;
			break;
		case 6:
			status = DominioStatusAceiteTecnico.NAO_CONFORME;
			break;
		case 7:
			status = DominioStatusAceiteTecnico.CONCLUIDA;
			break;
		case 8:
			status = DominioStatusAceiteTecnico.DEVOLVIDO;
			break;
		case 9:
			status = DominioStatusAceiteTecnico.PARCIALMENTE_ACEITO;
			break;
		case 10:
			status = DominioStatusAceiteTecnico.ACEITO;
			break;
		default:
			status = null;
			break;
		}
		return status;
	}
}
