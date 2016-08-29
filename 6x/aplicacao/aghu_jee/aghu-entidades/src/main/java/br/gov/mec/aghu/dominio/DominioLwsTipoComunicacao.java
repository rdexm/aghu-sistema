package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioLwsTipoComunicacao implements Dominio{
	
	PEDIDO_CARGA_EXAMES(1),
	PEDIDO_RESULTADOS_GESTAO_AMOSTRA(2),
	CANCELAMENTO_PEDIDO_EXAME(3),
	PROPOSTA_PEDIDO_CARGA_EXAMES(4),
	RECEPCAO_RESULTADOS(5),
	CADASTRO_PACIENTES_EXAMES_CONTCELL(6),
	RESULTADOS_CONTCELL(7),
	RESPOSTA_CONTCELL(8),
	ENVIO_RESULTADOS_URGEVIEW(9),
	RESULTADOS_CONTROLE_CQ(10);
	
	private int value;

	private DominioLwsTipoComunicacao(int value) {
		this.value = value;
	}
	
	@Override
	public int getCodigo() {
		return this.value;
	}

	@Override
	public String getDescricao() {
		switch (this) {
		case PEDIDO_CARGA_EXAMES:
			return "Pedido de carga de Exames";
		case PEDIDO_RESULTADOS_GESTAO_AMOSTRA:
			return "Pedido de resultados para o Gestão da Amostra";
		case CANCELAMENTO_PEDIDO_EXAME:
			return "Cancelamento de Pedido de Exame";
		case PROPOSTA_PEDIDO_CARGA_EXAMES:
			return "Resposta do pedido de carga de exames";
		case RECEPCAO_RESULTADOS:
			return "Recepção de Resultados";
		case CADASTRO_PACIENTES_EXAMES_CONTCELL:
			return "Cadastro de pacientes e exames no ContCell";
		case RESULTADOS_CONTCELL:
			return "Resultados para o Contcell";
		case RESPOSTA_CONTCELL:
			return "Resposta do Contcell";
		case ENVIO_RESULTADOS_URGEVIEW:
			return "Envio de Resultados para o UrgeView";
		case RESULTADOS_CONTROLE_CQ:
			return "Resultados de Controle para CQ";
		default:
			return "";
		}
	}
}
