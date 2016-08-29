package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

public enum DominioProgrGeralEntregaAF implements Dominio {
	AF {
		public String getDescricao() {
			return "AF";
		}
	},
	CP {
		public String getDescricao() {
			return "CP";
		}
	},
	AFP {
		public String getDescricao() {
			return "AFP";
		}
	},
	ITEM {
		public String getDescricao() {
			return "Item";
		}
	},
	PARCELA {
		public String getDescricao() {
			return "Parcela";
		}
	},
	PREVISAO_DE_ENTREGA {
		public String getDescricao() {
			return "Previsão de Entrega";
		}
	},
	QUANTIDADE_DA_PARCELA {
		public String getDescricao() {
			return "Quantidade da Parcela";
		}
	},
	UNIDADE_DE_MEDIDA_FORNECEDOR {
		public String getDescricao() {
			return "Unidade de Medida Fornecedor";
		}
	},
	FATOR_DE_CONVERSAO {
		public String getDescricao() {
			return "Fator de Conversão";
		}
	},
	UNIDADE_DE_MEDIDA_ESTOQUE_DO_HU {
		public String getDescricao() {
			return "Unidade de Medida Estoque do HU";
		}
	},
	QUANTIDADE_A_RECEBER {
		public String getDescricao() {
			return "Quantidade a Receber";
		}
	},
	ALMOXARIFADO {
		public String getDescricao() {
			return "Almoxarifado";
		}
	},
	ESPACO_DISPONIVEL {
		public String getDescricao() {
			return "Espaço Disponível";
		}
	},
	GRUPO {
		public String getDescricao() {
			return "Grupo";
		}
	},
	ABC {
		public String getDescricao() {
			return "ABC";
		}
	},
	MATERIAL_SERVICO {
		public String getDescricao() {
			return "Material/Serviço";
		}
	},
	FORNECEDOR {
		public String getDescricao() {
			return "Fornecedor";
		}
	},
	PARCELA_COM_EMPENHO {
		public String getDescricao() {
			return "Parcela com Empenho";
		}
	},
	PARCELA_CANCELADA {
		public String getDescricao() {
			return "Parcela Cancelada";
		}
	},
	PARCELA_PLANEJADA {
		public String getDescricao() {
			return "Parcela Planejada";
		}
	},
	PARCELA_ASSINADA {
		public String getDescricao() {
			return "Parcela Assinada";
		}
	},
	PARCELA_ENVIADA_AO_FORNECEDOR {
		public String getDescricao() {
			return "Parcela Enviada ao Fornecedor";
		}
	},
	PARCELA_COM_RECALCULO_AUTOMATICO {
		public String getDescricao() {
			return "Parcela com Recálculo Automático";
		}
	},
	PARCELA_COM_RECALCULO_MANUAL {
		public String getDescricao() {
			return "Parcela com Recálculo Manual";
		}
	},
	PARCELA_DE_ENTREGA_IMEDIATA {
		public String getDescricao() {
			return "Parcela de Entrega Imediata";
		}
	},
	TRAMITE_INTERNO {
		public String getDescricao() {
			return "Trâmite Interno";
		}
	},
	DATA_ENTREGA {
		public String getDescricao() {
			return "Data Entrega";
		}
	},
	ENVIO_AFP {
		public String getDescricao() {
			return "Envio AFP";
		}
	},
	QUANTIDADE_ITEM_DA_AF {
		public String getDescricao() {
			return "Quantidade Item da AF";
		}
	},
	QUANTIDADE_TOTAL_PROGRAMADA {
		public String getDescricao() {
			return "Quantidade Total Programada";
		}
	},
	QUANTIDADE_ENTREGUE_PARCELA {
		public String getDescricao() {
			return "Quantidade Entregue Parcela";
		}
	},
	CENTRO_DE_CUSTO_SOLICITANTE {
		public String getDescricao() {
			return "Centro de Custo Solicitante";
		}
	},
	SOLICITACAO_DE_COMPRA_SERVICO {
		public String getDescricao() {
			return "Solicitação de Compra/Serviço";
		}
	},
	ASSINATURA {
		public String getDescricao() {
			return "Assinatura";
		}
	},
	JUSTIFICATIVA_DE_EMPENHO {
		public String getDescricao() {
			return "Justificativa de Empenho";
		}
	},
	QUANTIDADE_RECEBIDA_AF {
		public String getDescricao() {
			return "Quantidade Recebida AF";
		}
	},
	QUANTIDADE_TOTAL_ENTREGUE {
		public String getDescricao() {
			return "Quantidade Total Entregue";
		}
	},
	VALOR_EFETIVADO {
		public String getDescricao() {
			return "Valor Efetivado";
		}
	},
	CENTRO_DE_CUSTO_APLICACAO {
		public String getDescricao() {
			return "Centro de Custo Aplicação";
		}
	},
	OBSERVACAO {
		public String getDescricao() {
			return "Observação";
		}
	};

	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	@Override
	public abstract String getDescricao();
}
