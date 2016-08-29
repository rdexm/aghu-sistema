package br.gov.mec.aghu.faturamento.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndComparacao;
import br.gov.mec.aghu.dominio.DominioIndCompatExclus;
import br.gov.mec.aghu.faturamento.dao.FatCompatExclusItemDAO;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * <p>
 * Linhas: 482 <br/>
 * Cursores: 4 <br/>
 * Forks de transacao: 0 <br/>
 * Consultas: 18 tabelas <br/>
 * Alteracoes: 0 tabelas <br/>
 * Metodos: 2 <br/>
 * Metodos externos: 0 <br/>
 * </p>
 * <p>
 * ORADB: <code>FATK_ICT_RN</code><br/>
 * ORADB: <code>FATK_ICT_RN_UN</code><br/>
 * </p>
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class FaturamentoFatkIctRN extends AbstractFatDebugLogEnableRN {

private static final String NULL = "null";
private static final Log LOG = LogFactory.getLog(FaturamentoFatkIctRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatCompatExclusItemDAO fatCompatExclusItemDAO;
	private static final long serialVersionUID = -8837237031202437050L;

	/**
	 * Verifica se o item a ser inserido na conta possui restricao de nao poder ser cobrado junto com outro que ja esta na conta.<br>
	 * 
	 * <p>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>OK</b><br/>
	 * Linhas: 137 <br/>
	 * Cursores: 0 <br/>
	 * Forks de transacao: 0 <br/>
	 * Consultas: 6 tabelas <br/>
	 * Alteracoes: 0 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_ICTC_VER_CMPEXC_I</code>
	 * </p>
	 * @param pTabProcNovo 
	 * @param pProcNovo 
	 * @param pTabProcExistente 
	 * @param pProcExistente
	 * @param mapRnIctcVerCmpexcI
	 * @return 
	 * @see FatCompatExclusItem
	 * @see FaturamentoFatkIctRN#rnIctcVerCmpexcILoad()
	 * @see FaturamentoFatkIctRN#getKeyMapRnIctVerCmpexcI(Short, Integer, Short, Integer)
	 */
	public boolean rnIctcVerCmpexcI(Short pTabProcNovo, Integer pProcNovo, Short pTabProcExistente, Integer pProcExistente, Map<String, FatCompatExclusItem> mapRnIctcVerCmpexcI) {
		String key;
		key = getKeyMapRnIctVerCmpexcI(pTabProcNovo, pProcNovo, pTabProcExistente, pProcExistente);
		if (mapRnIctcVerCmpexcI.containsKey(key)) {
			logar("restricao itens novo x existente ");
			return false;
		}
		
		key = getKeyMapRnIctVerCmpexcI(pTabProcExistente, pProcExistente, pTabProcNovo, pProcNovo);
		if (mapRnIctcVerCmpexcI.containsKey(key)) {
			logar("restricao itens existente x novo ");
			return false;
		}
		
		return true;
//		FatCompatExclusItemDAO fatCompatExclusItemDAO = getFatCompatExclusItemDAO();
//
//		DominioIndCompatExclus[] indCompatExclus = new DominioIndCompatExclus[] { DominioIndCompatExclus.PNI,
//				DominioIndCompatExclus.INP };
//		DominioIndComparacao indComparacao = DominioIndComparacao.I;
//		Boolean indInternacao = true;
//
//		FatCompatExclusItem aux = fatCompatExclusItemDAO.buscarFatCompatExclusItemCountI(pTabProcNovo, pProcNovo, pTabProcExistente,
//				pProcExistente, indCompatExclus, indComparacao, indInternacao);
//		if (aux != null) {
//			logar("restricao itens novo x existente ");
//			return false;
//		}
//
//		aux = fatCompatExclusItemDAO.buscarFatCompatExclusItemCountI(pTabProcExistente, pProcExistente, pTabProcNovo, pProcNovo,
//				indCompatExclus, indComparacao, indInternacao);
//		if (aux != null) {
//			logar("restricao itens existente x novo ");
//			return false;
//		}
//
//		return true;
	}
	
	/**
	 * Monta um Mapa com a lista de FatCompatExclusItem<br>
	 * onde chave deste Mapa eh:<br>
	 * FatItensProcedHospitalar.id,<br>
	 * FatItensProcedHospitalar.id (compatibiliza)<br>
	 * 
	 * @return Map:String, FatCompatExclusItem.
	 * 
	 * @see FaturamentoFatkIctRN#getKeyMapRnIctVerCmpexcILoad(FatCompatExclusItem)
	 */
	public Map<String, FatCompatExclusItem> rnIctcVerCmpexcILoad(Date pCompetencia) {
		final Map<String, FatCompatExclusItem> map = new HashMap<String, FatCompatExclusItem>();
		
		if(pCompetencia != null){
			pCompetencia = DateUtil.obterDataInicioCompetencia(pCompetencia);
			
			DominioIndCompatExclus[] indCompatExclus = new DominioIndCompatExclus[] { DominioIndCompatExclus.PNI, DominioIndCompatExclus.INP };
			DominioIndComparacao indComparacao = DominioIndComparacao.I;
			Boolean indInternacao = true;
			
			List<FatCompatExclusItem> lista = getFatCompatExclusItemDAO().buscarFatCompatExclusItemCountILoad( indCompatExclus, 
																											   indComparacao, 
																											   indInternacao,
																											   pCompetencia);
			
			for (FatCompatExclusItem fatCompatExclusItem : lista) {
				String key = getKeyMapRnIctVerCmpexcILoad(fatCompatExclusItem); 
				map.put(key, fatCompatExclusItem);
			}
		}
		
		return map;
	}
	
	private String getKeyMapRnIctVerCmpexcILoad(FatCompatExclusItem fatCompatExclusItem) {
		Short phoSeq1 = fatCompatExclusItem.getItemProcedHosp().getId().getPhoSeq();
		Integer seq1 = fatCompatExclusItem.getItemProcedHosp().getId().getSeq();
		
		Short phoSeq2 = fatCompatExclusItem.getItemProcedHospCompatibiliza().getId().getPhoSeq();
		Integer seq2 = fatCompatExclusItem.getItemProcedHospCompatibiliza().getId().getSeq();

		return getKeyMapRnIctVerCmpexcI(phoSeq1 ,  seq1 , phoSeq2 , seq2);
	}
	
	private String getKeyMapRnIctVerCmpexcI(Short iphPhoSeq, Integer iphSeq, Short iphPhoSeqCompatibiliza, Integer iphSeqCompatibiliza) {
		String p1 = iphPhoSeq != null ? iphPhoSeq.toString() : NULL; 
		String p2 = iphSeq != null ? iphSeq.toString() : NULL; 
		String p3 = iphPhoSeqCompatibiliza != null ? iphPhoSeqCompatibiliza.toString() : NULL; 
		String p4 = iphSeqCompatibiliza != null ? iphSeqCompatibiliza.toString() : NULL; 
		
		return p1 + "_" + p2  + "_"  + p3 + "_" + p4;		
	}


	/**
	 * <p>
	 * Assinatura: ok<br/>
	 * Referencias externas: ok<br/>
	 * Tabelas: ok<br/>
	 * Codigos de erro: ok<br/>
	 * Implementacao: <b>OK</b><br/>
	 * Linhas: 345 <br/>
	 * Cursores: 4 <br/>
	 * Forks de transacao: 0 <br/>
	 * Consultas: 12 tabelas <br/>
	 * Alteracoes: 0 tabelas <br/>
	 * Metodos externos: 0 <br/>
	 * </p>
	 * <p>
	 * ORADB: <code>RN_ICTC_VER_CMPEXC_R</code>
	 * </p>
	 * 
	 * Se o proced realizado tem algum PCI, so permite cobranca de alguns itens
	 * (os cadastrados como PCI)
	 * 
	 * Se o proced realizado tem algum PNI, nao permite cobranca de alguns itens
	 * (os cadastrados como PNI)
	 * 
	 * Se o item tem algum ICP, so permite cobranca em alguns proced realizados
	 * (os cadastrados como ICP)
	 * 
	 * Se o item tem algum INP, nao permite cobranca em alguns proced realizados
	 * (os cadastrados como INP)
	 * 
	 * @param pTabProcRealizado
	 * @param pProcRealizado
	 * @param pTabProcItem
	 * @param pTipoTrans
	 * @return
	 * @see FatCompatExclusItem
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public boolean rnIctcVerCmpexcR(Short pTabProcRealizado, Integer pProcRealizado, Short pTabProcItem, Integer pProcItem, String pTipoTrans,Date pCompetencia) {
		
		FatCompatExclusItemDAO fatCompatExclusItemDAO = getFatCompatExclusItemDAO();
		DominioIndCompatExclus vIndCompatExclus = null;
		Boolean vCompativel = true;
		
		// Verifica se o realizado e o item sao o mesmo procedimento
		if (CoreUtil.igual(pTabProcRealizado, pTabProcItem) && CoreUtil.igual(pProcRealizado, pProcItem)) {
			// Se o item e' o proprio realizado considera que e' compativel
			return true;
		}
		
		if(pCompetencia != null){
	
			// Busca tipos de restricoes existentes para o realizado
			final DominioIndComparacao indComparacao = DominioIndComparacao.R;
			final Boolean indInternacao = Boolean.TRUE;
			
			pCompetencia = DateUtil.obterDataInicioCompetencia(pCompetencia);
	
			List<DominioIndCompatExclus> listaDistinctIndCompatExclus = fatCompatExclusItemDAO.buscaTiposRestricoesExistentesParaRealizado(pTabProcRealizado, pProcRealizado, 
					   indComparacao, indInternacao, pCompetencia,
					   DominioIndCompatExclus.PCI, DominioIndCompatExclus.PNI);

			logar("restr do realiz antes: {0}", vIndCompatExclus);
			
			if (listaDistinctIndCompatExclus != null && !listaDistinctIndCompatExclus.isEmpty()) {
				for (int i = 0; i < listaDistinctIndCompatExclus.size(); i++) {
					vIndCompatExclus = listaDistinctIndCompatExclus.get(i);
	
					logar("restr do realiz: {0}", vIndCompatExclus);
	
					if (DominioIndCompatExclus.PCI.equals(vIndCompatExclus)) {
						// Se o proced realizado tem alguma restricao do tipo PCI,
						// so pode cobrar os itens que estiverem cadastrados como PCI.
						// Verifica se o item e' um dos PCI cadastrados:
	
						// Verifica se o item e' um dos cadastrados como PCI p/ o realizado
						final FatCompatExclusItem aux = fatCompatExclusItemDAO.buscarFatCompatExclusItemCountR(pTabProcRealizado, pProcRealizado, 
																							 pTabProcItem, pProcItem, 
																							 indComparacao, indInternacao, 
																							 pTipoTrans, pCompetencia,
																							 DominioIndCompatExclus.PCI, DominioIndCompatExclus.ICP);//-- Marina 24/07/2013
																							 
						if (aux == null) {
							logar("item nao eh um dos PCI do realiz -> nao pode cobrar");
							vCompativel = false; // NAO PERMITE COBRAR!
						}
					} else if (DominioIndCompatExclus.PNI.equals(vIndCompatExclus)) {
						// Se o proced realizado tem alguma restricao do tipo PNI,
						// nao pode cobrar os itens que estiverem cadastrados como PNI.
						// Verifica se o item e' um dos PNI cadastrados:
	
						// Verifica se o item e' um dos cadastrados como PNI p/ o realizado
						FatCompatExclusItem aux = fatCompatExclusItemDAO.buscarFatCompatExclusItemCountR(pTabProcRealizado, pProcRealizado,
																						pTabProcItem, pProcItem,
																						indComparacao, indInternacao, pTipoTrans, pCompetencia,
																						DominioIndCompatExclus.PNI);//-- Marina 24/07/2013

						if (aux != null) {
							logar("item eh um dos PNI do realiz -> nao pode cobrar");
							vCompativel = false; // NAO PERMITE COBRAR!
						}
					}
				}
			}
	
			// Busca tipos de restricoes existentes para o item
			listaDistinctIndCompatExclus = fatCompatExclusItemDAO.buscaTiposRestricoesExistentesParaItem( pTabProcItem, pProcItem, indComparacao, indInternacao, pCompetencia,
					DominioIndCompatExclus.ICP, DominioIndCompatExclus.INP);//-- Marina 24/07/2013
			if (listaDistinctIndCompatExclus != null && !listaDistinctIndCompatExclus.isEmpty()) {
				for (int i = 0; i < listaDistinctIndCompatExclus.size(); i++) {
					vIndCompatExclus = listaDistinctIndCompatExclus.get(i);
	
					logar("restr do item: {0}", vIndCompatExclus);
					
					if (DominioIndCompatExclus.ICP.equals(vIndCompatExclus)) {
						// Se o item tem alguma restricao do tipo ICP, so permite
						// cobranca dele c/os proced realizados cadastrados como ICP.
						// Verifica se o proced realizado e' um dos ICP cadastrados:
	
						// Verifica se o realizado e' um dos cadastrados como ICP p/ o item
						FatCompatExclusItem aux = fatCompatExclusItemDAO.buscarFatCompatExclusItemCountR( pTabProcRealizado, pProcRealizado, pTabProcItem,
								pProcItem, indComparacao, indInternacao,
								pTipoTrans, pCompetencia,
								DominioIndCompatExclus.ICP);//-- Marina 24/07/2013
								
						if (aux == null) {
							logar("realiz nao eh um dos ICP do item -> nao pode cobrar");
							vCompativel = false; // NAO PERMITE COBRAR!
						}
					} else if (DominioIndCompatExclus.INP.equals(vIndCompatExclus)) {
						// Se o item tem alguma resticao do tipo INP, nao permite
						// cobranca dele c/os proced realizados cadastrados como INP.
						// Verifica se o proced realizado e' um dos INP cadastrados:
	
						// Verifica se o realizado e' um dos cadastrados como INP p/ o item
						FatCompatExclusItem aux =
								fatCompatExclusItemDAO.buscarFatCompatExclusItemCountR( pTabProcRealizado, pProcRealizado, pTabProcItem,
										pProcItem, indComparacao, indInternacao, 
										pTipoTrans, pCompetencia,
										DominioIndCompatExclus.INP);//-- Marina 24/07/2013
	
						if (aux != null) {
							logar("realiz eh um dos INP do item -> nao pode cobrar");
							vCompativel = false; // NAO PERMITE COBRAR!
						}
					}
				}
			}
		}

		return vCompativel;
	}
	
	protected FatCompatExclusItemDAO getFatCompatExclusItemDAO() {
		return fatCompatExclusItemDAO;
	}
	
}
