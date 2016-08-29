package br.gov.mec.aghu.dominio;

import br.gov.mec.aghu.core.dominio.Dominio;

/**
 * Domínio que indica as opções de relatórios excel.
 * 
 * @author rafael.fonseca
 */
public enum DominioRelatorioExcel implements Dominio {
	AF("AF - AF's Pendentes por Comprador"),
	CM("CM - Compras no Mês"),
	IP("IP - Investimentos Pendentes"),
	IE("IE - Investimentos Efetivados"),
	CT("CT - Contratos Pendentes"),
	NA("NA - AF's Não Assinadas"),
	SP("SP - SC's Pendentes"),
	VA("VA - Verificação Auditoria"),
	AP("AP - Andamento Processo de Compras"),
	ES("ES - Entradas sem Empenho ou Assinatura de AF"),
	VL("VL - Valor Atualizado das Listas do SIAFI"),
	EP("EP - Encerramento de Processos de Compra"),
	LG("LG - TCU - Lict Geradas ou Efetivadas no Ano"),
	LE("LE - TCU - Lict Ger ou Efet no Ano - Empenho"),
	DG("DG - TCU - Disp Geradas ou Efetivadas no Ano"),
	DE("DE - TCU - Disp Ger ou Efet no Ano - Empenho"),
	DM("DM - Dispensas Art 24, Inc IV no Mês Informado"),
	TM("TM - Valor Total Aprovado por Modl de Licitação"),
	IPC("IP - Itens Constantes em Mais de um PAC no Ano"),
	SPG("SP - Solicitações Pendentes, por Grupo Mat"),
	CMM("CM - Compras por Modl, no Mês"),
	AEP("AEP - AFPs Assinadas, com Entrega Pendente"),
	SC("SC - Solicitações Serviços - Completa")
	;
	
	private String descricao;
	
	private DominioRelatorioExcel(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public int getCodigo() {
		return ordinal();
	}

	@Override
	public String getDescricao() {
		return descricao;
	}
}