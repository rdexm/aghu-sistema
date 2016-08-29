package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DadosExSolicitacaoCompraEstocavelVO {
	
//	public static void main(String[] args) {
//	    System.out.println("teste...");
//	}

	public static List<RelatorioSolicitacaoCompraEstocavelVO> retornaVO() {
		List<RelatorioSolicitacaoCompraEstocavelVO> listaSolicitacaoCompraEstocavelVO = new ArrayList<RelatorioSolicitacaoCompraEstocavelVO>();
		
		RelatorioSolicitacaoCompraEstocavelVO solicitacaoCompraEstocavelVO = new RelatorioSolicitacaoCompraEstocavelVO();
		
		solicitacaoCompraEstocavelVO.setCodigo(89249);

		solicitacaoCompraEstocavelVO.setUnidade("PC");
		solicitacaoCompraEstocavelVO.setNumSolicitacao(502414);
		solicitacaoCompraEstocavelVO
				.setDescricao("ELETRODO DESC ADES.C/GEL SOLIDO P/ MONITORRIS. CARD. ADULTO DIAMETRO  MAX 4CM DE DIÂMETRO OU 32MMX40MM");

		ConsumoMedioMensalVO mes1 = new ConsumoMedioMensalVO();
		mes1.setMesAno("Mai/2012");
		mes1.setConsumo(30650);

		ConsumoMedioMensalVO mes2 = new ConsumoMedioMensalVO();
		mes2.setMesAno("Jun/2012");
		mes2.setConsumo(28840);

		ConsumoMedioMensalVO mes3 = new ConsumoMedioMensalVO();
		mes3.setMesAno("Jul/2012");
		mes3.setConsumo(42500);

		ConsumoMedioMensalVO mes4 = new ConsumoMedioMensalVO();
		mes4.setMesAno("Ago/2012");
		mes4.setConsumo(24800);

		ConsumoMedioMensalVO mes5 = new ConsumoMedioMensalVO();
		mes5.setMesAno("Set/2012");
		mes5.setConsumo(30450);

		ConsumoMedioMensalVO mes6 = new ConsumoMedioMensalVO();
		mes6.setMesAno("Out/2012");
		mes6.setConsumo(39450);

		ConsumoMedioMensalVO mes7 = new ConsumoMedioMensalVO();
		mes7.setMesAno("Nov/2012");
		mes7.setConsumo(210000);
		
		ConsumoMedioMensalVO medMensal = new ConsumoMedioMensalVO();
		medMensal.setMesAno("MedMensal");
		medMensal.setConsumo(34667);		

		List<ConsumoMedioMensalVO> listaConsumoMedioMensalVO = new ArrayList<ConsumoMedioMensalVO>();
		listaConsumoMedioMensalVO.add(mes1);
		listaConsumoMedioMensalVO.add(mes2);
		listaConsumoMedioMensalVO.add(mes3);
		listaConsumoMedioMensalVO.add(mes4);
		listaConsumoMedioMensalVO.add(mes5);
		listaConsumoMedioMensalVO.add(mes6);
		listaConsumoMedioMensalVO.add(mes7);
		listaConsumoMedioMensalVO.add(medMensal);
		solicitacaoCompraEstocavelVO.setConsumoMensal(listaConsumoMedioMensalVO);

		solicitacaoCompraEstocavelVO.setMediaMensal(24667);
		solicitacaoCompraEstocavelVO.setPeriodo("Primavera (Set-Nov)");
		solicitacaoCompraEstocavelVO.setConsumoMedioSazonal(30650);
		solicitacaoCompraEstocavelVO.setEstoqueMaximo(74380);
		solicitacaoCompraEstocavelVO.setPontoPedido(37190);
		solicitacaoCompraEstocavelVO.setEstoqueSeg(123456);
		solicitacaoCompraEstocavelVO.setLoteRep(35000);
		solicitacaoCompraEstocavelVO.setClasseAbc("A B");
		solicitacaoCompraEstocavelVO.setDtUltimaMovimentacao(new Date(21/11/2012));
		solicitacaoCompraEstocavelVO.setDtUltimaCompra(new Date(21/11/2012));
		solicitacaoCompraEstocavelVO.setValorUnitarioCompra(new BigDecimal("0.1540"));
		solicitacaoCompraEstocavelVO.setSaldoTotalEstoque(49980);
		solicitacaoCompraEstocavelVO.setQuantidadeBloqueada(49980);
		solicitacaoCompraEstocavelVO.setQuantidadeDisponivel(0);
		solicitacaoCompraEstocavelVO.setQuantidadeSolicitada(35000);

		SubRelatorioSolicitacoesEstocaveisVO solicitacaoEstocaveis1 = new SubRelatorioSolicitacoesEstocaveisVO(
				502414, new Date(21/11/2012), 35000, null, null, null,
				null, null, null, null, null);

		SubRelatorioSolicitacoesEstocaveisVO solicitacaoEstocaveis2 = new SubRelatorioSolicitacoesEstocaveisVO(
				479177, new Date(22/12/2011), 300000, 126122, "1261333/2", 3,
				new Date(30/06/2012), 300000, 49980, "PA", "PRODIET FARMACEUTICA S.A.");

		SubRelatorioSolicitacoesEstocaveisVO solicitacaoEstocaveis3 = new SubRelatorioSolicitacoesEstocaveisVO(
				499120, new Date(18/10/2012), 350000, 12741, null, 3,
				null, null, null, null, null);
		
		List<SubRelatorioSolicitacoesEstocaveisVO> listaSolicitacoesEstocaveis = new ArrayList<SubRelatorioSolicitacoesEstocaveisVO>();
		listaSolicitacoesEstocaveis.add(solicitacaoEstocaveis1);
		listaSolicitacoesEstocaveis.add(solicitacaoEstocaveis2);
		listaSolicitacoesEstocaveis.add(solicitacaoEstocaveis3);
		
		solicitacaoCompraEstocavelVO.setSolicitacoesEstocaveis(listaSolicitacoesEstocaveis);
		
		solicitacaoCompraEstocavelVO.setDataSolicitacao(new Date(21/11/2012));
		solicitacaoCompraEstocavelVO.setCentroCustoRequisitante(44200);
		solicitacaoCompraEstocavelVO.setAlmox(1);
		solicitacaoCompraEstocavelVO.setTempoReposicao(30);
		solicitacaoCompraEstocavelVO.setSaldoAtual(49980);
		solicitacaoCompraEstocavelVO.setDuracaoEstoque(new BigDecimal("40.32"));
		solicitacaoCompraEstocavelVO.setPontoPedidoPlanejamento(37190);
		solicitacaoCompraEstocavelVO.setPontoPedCalc("S");
		solicitacaoCompraEstocavelVO.setQuantidadeSolcitadaReferente(35000);
		solicitacaoCompraEstocavelVO.setQuantidadeAutorizada(null);

		listaSolicitacaoCompraEstocavelVO.add(solicitacaoCompraEstocavelVO);
		
		
		return listaSolicitacaoCompraEstocavelVO;
	}
	
	public static List<ConsumoMedioMensalVO> retornaConsumoMedioMensal(){
		
		List<RelatorioSolicitacaoCompraEstocavelVO> listaSolicitacaoCompraEstocavelVO = retornaVO();
		
		List<ConsumoMedioMensalVO> listaConsumoMedioMensalVO = listaSolicitacaoCompraEstocavelVO.get(0).getConsumoMensal();
		
		return listaConsumoMedioMensalVO; 
	}

	public static List<SubRelatorioSolicitacoesEstocaveisVO> retornaSubSolicitacoesEstocaveis(){
		
		List<RelatorioSolicitacaoCompraEstocavelVO> listaSolicitacaoCompraEstocavelVO = retornaVO();
		
		List<SubRelatorioSolicitacoesEstocaveisVO> listaSubRelatorioSolicitacoesEstocaveisVO = listaSolicitacaoCompraEstocavelVO.get(0).getSolicitacoesEstocaveis();
		
		return listaSubRelatorioSolicitacoesEstocaveisVO;
	}
	
	
	
	
}
