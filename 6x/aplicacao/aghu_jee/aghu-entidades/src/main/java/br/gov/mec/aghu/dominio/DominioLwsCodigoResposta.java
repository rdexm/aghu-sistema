package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLwsCodigoResposta implements Dominio{
	
	A00,
	A01,
	E01,
	E02,
	E03,
	E04,
	E05,
	E06,
	E07,
	E08,
	E09,
	E10,
	E11,
	E12,
	E99,
	W01;
	
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case A00:
			return "Mensagem processada com sucesso";		
		case A01:
			return "Mensagem processada mas com restrições";
		case E01:
			return "Erro de Sintaxe nos dados da mensagem";			
		case E02:
			return "Erro: Mensagem Inválida";
		case E03:
			return "Erro: Sistema/Módulo Destino Inválido";
		case E04:
			return "Erro: Sistema/Módulo Origem Inválido";
		case E05:
			return "Erro: Transbordo de buffer no destino";
		case E06:
			return "Erro: Campos obrigatórios em branco";
		case E07:
			return "Erro: Exame Inválido";
		case E08:
			return "Erro: Parâmetro Inválido";
		case E09:
			return "Erro: Servidor LabWide desativado";
		case E10:
			return "Erro: Driver Inválido.";
		case E11:
			return "Erro: Tipo/Grupo de equipamento inválido";
		case E12:
			return "Erro: Memória insuficiente no sistema destino";
		case E99:
			return "Erro Desconhecido";
		case W01:
			return "Aviso: Driver Inativo";			
		default:
			return "";
		}
	}
}
