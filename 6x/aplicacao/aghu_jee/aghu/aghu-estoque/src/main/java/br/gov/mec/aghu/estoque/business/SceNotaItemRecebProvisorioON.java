package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import org.hibernate.Hibernate;
import br.gov.mec.aghu.estoque.dao.SceItemRecbXProgrEntregaDAO;
import br.gov.mec.aghu.estoque.dao.SceItemRecebProvisorioDAO;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO;
import br.gov.mec.aghu.estoque.vo.QtdeRpVO.NotaRecebimentoProvisorio;
import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.SceItemRecbXProgrEntrega;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoProgEntregaItemAutorizacaoFornecimento;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * Objeto de negócio responsável por itens de notas de recebimento provisório.
 * 
 * @author mlcruz
 */
@Stateless
public class SceNotaItemRecebProvisorioON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SceNotaItemRecebProvisorioON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceItemRecbXProgrEntregaDAO sceItemRecbXProgrEntregaDAO;
@Inject
private SceItemRecebProvisorioDAO sceItemRecebProvisorioDAO;



	private static final long serialVersionUID = -4614455082481993853L;

	/**
	 * Obtem quantidade total de itens de uma ou mais notas de recebimento
	 * provisório associadas a um item de AF.
	 * 
	 * @param itemAf Item de AF
	 * @param maxRps 
	 * @return Quantidade
	 */
	public QtdeRpVO somarQtdeItensNotaRecebProvisorio(
			ScoItemAutorizacaoForn itemAf, int maxRps) {
		QtdeRpVO soma = new QtdeRpVO();
		
		Long qtde = getSceItemRecebProvisorioDAO()
				.somarQtdeItensNotaRecebProvisorio(itemAf);
		
		soma.setQuantidade(qtde != null ? qtde : 0);
		
		soma.setNotasRecebimento(getSceItemRecebProvisorioDAO()
				.pesquisarNotasRecebimentoProvisorio(itemAf, maxRps));

		for (NotaRecebimentoProvisorio notaRP : soma.getNotasRecebimento()){
			ScoFornecedor fornecedor = null;
			
			Hibernate.initialize(notaRP.getDocumentoFiscalEntrada());
			if (notaRP.getDocumentoFiscalEntrada() != null){ 
				Hibernate.initialize(notaRP.getDocumentoFiscalEntrada().getFornecedor());
				
				fornecedor = notaRP.getDocumentoFiscalEntrada().getFornecedor();
				notaRP.setFornecedor(fornecedor);
			}
		}
		
		soma.setMostrarAlerta(this.mostrarAlerta(soma));
		return soma;
	}

	/**
	 * Obtem quantidade total de itens de uma ou mais notas de recebimento
	 * provisório associadas a uma parcela de item de AF.
	 * 
	 * @param parcela Parcela de Item de AF
	 * @param maxRps
	 * @return Quantidade
	 */
	public QtdeRpVO somarQtdeItensNotaRecebProvisorio(
			ScoProgEntregaItemAutorizacaoFornecimento parcela, int maxRps) {
		QtdeRpVO soma = new QtdeRpVO();
		
		Long qtde = (Long) CoreUtil.nvl(getSceItemRecbXProgrEntregaDAO().obterSomaItemRecbXProgrEntregaPorItemAF(parcela),Long.valueOf("0"));
		
		soma.setQuantidade(qtde);
		
		if (parcela.getItensRecebProvisorioXProgEntrega() != null) {
			
			List<SceItemRecbXProgrEntrega> lista =getSceItemRecbXProgrEntregaDAO().pesquisarItemRecbXProgrEntregaPorItemAutorizacaoFornecimento(parcela);
			//(parcela.getItensRecebProvisorioXProgEntrega());
		
			List<NotaRecebimentoProvisorio> listaNotaRecebimentoProvisorio = new ArrayList<QtdeRpVO.NotaRecebimentoProvisorio>();
			
			for (SceItemRecbXProgrEntrega itemRecbXProgrEntrega: lista){
				NotaRecebimentoProvisorio notaRecebimentoProvisorio = new NotaRecebimentoProvisorio();
				notaRecebimentoProvisorio.setId(itemRecbXProgrEntrega.getSceItemRecebProvisorio().getNotaRecebimentoProvisorio().getSeq());
				notaRecebimentoProvisorio.setDataGeracao(itemRecbXProgrEntrega.getSceItemRecebProvisorio().getNotaRecebimentoProvisorio().getDtGeracao());
				notaRecebimentoProvisorio.setIndEstorno(itemRecbXProgrEntrega.getSceItemRecebProvisorio().getNotaRecebimentoProvisorio().getIndEstorno());
				
				if (itemRecbXProgrEntrega.getSceItemRecebProvisorio().getNotaRecebimentoProvisorio().getDocumentoFiscalEntrada() != null){
					SceDocumentoFiscalEntrada docFiscalEntrada = itemRecbXProgrEntrega.getSceItemRecebProvisorio().getNotaRecebimentoProvisorio().getDocumentoFiscalEntrada();
					notaRecebimentoProvisorio.setDocumentoFiscalEntrada(docFiscalEntrada);
					Hibernate.initialize(docFiscalEntrada.getFornecedor());
					notaRecebimentoProvisorio.setFornecedor(docFiscalEntrada.getFornecedor());
				}
				
				listaNotaRecebimentoProvisorio.add(notaRecebimentoProvisorio);
			}
			
			soma.setNotasRecebimento(listaNotaRecebimentoProvisorio);
		}
		
		soma.setMostrarAlerta(this.mostrarAlerta(soma));
		return soma;
	}
	
	private Boolean mostrarAlerta(QtdeRpVO soma) {
		return soma.getQuantidade() > 0 || (soma.getNotasRecebimento() != null && !soma.getNotasRecebimento().isEmpty());  
	}

	// Dependências
	
	protected SceItemRecebProvisorioDAO getSceItemRecebProvisorioDAO() {
		return sceItemRecebProvisorioDAO;
	}
	
	protected SceItemRecbXProgrEntregaDAO getSceItemRecbXProgrEntregaDAO() {
		return sceItemRecbXProgrEntregaDAO;
	}
}