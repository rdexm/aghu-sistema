package br.gov.mec.aghu.patrimonio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioStatusTicket;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmStatusTicket;
import br.gov.mec.aghu.model.PtmTicket;
import br.gov.mec.aghu.model.PtmUserTicket;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.patrimonio.vo.AceiteTecnicoPendenteVO;
import br.gov.mec.aghu.patrimonio.vo.ItemRecebimentoVO;
import br.gov.mec.aghu.patrimonio.vo.TicketsVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PtmTicketDAO extends BaseDao<PtmTicket> {

	private static final String ALIAS_TIC = "TIC.";

	private static final String ALIAS_ATA = "ATA.";

	private static final String ALIAS_STT = "STT.";

	private static final String ALIAS_IRP = "IRP.";

	private static final long serialVersionUID = -1818559680134396834L;
	
	private static final String TICKET_ALIAS = "TKT";
	
	private static final String TICKET_ALIAS_EXT = "TKT.";
	
	private static final String TICKET_2_ALIAS = "TKT2";
	
	private static final String TICKET_2_ALIAS_EXT = "TKT2.";
	
	private static final String STATUS_TICKET_ALIAS = "STT";
	
	private static final String STATUS_TICKET_ALIAS_EXT = ALIAS_STT;
	
	private static final String STATUS_TICKET_2_ALIAS = "STT2";

	private static final String STATUS_TICKET_2_ALIAS_EXT = "STT2.";
	
	private static final String PTM_ITEM_RECEB_PROVISORIO_ALIAS = "PIR";
	
	private static final String PTM_ITEM_RECEB_PROVISORIO_ALIAS_EXT = "PIR.";
	
	private static final String PTM_USER_TICKET_ALIAS = "TKU";

	/**
	 * Obtém todos os Tickets cadastrados.
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return Lista de PtmTicket
	 */
	public List<PtmTicket> listarTodosTickets(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTicket.class);

		if (orderProperty == null) {
			criteria.addOrder(Order.asc(PtmTicket.Fields.SEQ.toString()));
		}

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Obtém quantidade de registros retornados na consulta por todos os Tickets cadastrados.
	 * 
	 * @return count da lista de PtmTicket
	 */
	public Long listarTodosTicketsCount() {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTicket.class);
		
		return executeCriteriaCount(criteria);
	}
	
	public List<PtmTicket> obterTicketPorItemRecebimento(Integer recebimento,
			Integer itemRecebimento){
		DetachedCriteria criteria = getCriteriaTicketPorItemReceb(recebimento, itemRecebimento);
		
		return executeCriteria(criteria);
	}
	
	public List<PtmTicket> obterTicketPorItemRecebimentoPorServidor(Integer recebimento, Integer itemRecebimento, RapServidoresId rapServidorId){
		DetachedCriteria criteria = getCriteriaTicketPorItemReceb(recebimento, itemRecebimento);
		
		if(rapServidorId != null){
			criteria.add(Restrictions.eq(PTM_USER_TICKET_ALIAS +"."+ PtmUserTicket.Fields.SER_MATRICULA.toString(), rapServidorId.getMatricula()));
			criteria.add(Restrictions.eq(PTM_USER_TICKET_ALIAS +"."+ PtmUserTicket.Fields.SER_VINCULO.toString(), rapServidorId.getVinCodigo()));
		}
		
		return executeCriteria(criteria);
	}

	/**
	 * @param recebimento
	 * @param itemRecebimento
	 * @return
	 */
	private DetachedCriteria getCriteriaTicketPorItemReceb(Integer recebimento,
			Integer itemRecebimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTicket.class, TICKET_ALIAS);
		criteria.createAlias(TICKET_ALIAS_EXT + PtmTicket.Fields.PTMSTATUSTICKET.toString(), STATUS_TICKET_ALIAS, JoinType.INNER_JOIN);
		criteria.createAlias(TICKET_ALIAS_EXT + PtmTicket.Fields.ITEM_RECEB_PROVISORIO.toString(), PTM_ITEM_RECEB_PROVISORIO_ALIAS, JoinType.INNER_JOIN);
		criteria.createAlias(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.USER_TICKETS.toString(), PTM_USER_TICKET_ALIAS, JoinType.INNER_JOIN);

		criteria.setFetchMode(TICKET_ALIAS_EXT + PtmTicket.Fields.PTMSTATUSTICKET.toString(), FetchMode.JOIN);
		criteria.setFetchMode(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.USER_TICKETS.toString(), FetchMode.JOIN);

		DetachedCriteria subQuery = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_2_ALIAS);
		subQuery.setProjection(Projections.max(STATUS_TICKET_2_ALIAS_EXT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()));
		subQuery.createAlias(STATUS_TICKET_2_ALIAS_EXT + PtmStatusTicket.Fields.TICKET.toString(), TICKET_2_ALIAS, JoinType.INNER_JOIN);
		subQuery.add(Restrictions.eqProperty(TICKET_ALIAS_EXT + PtmTicket.Fields.SEQ.toString(), TICKET_2_ALIAS_EXT + PtmTicket.Fields.SEQ.toString()));
		criteria.add(Subqueries.propertyEq(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.DATA_CRIACAO.toString(), subQuery));
		
		criteria.add(Restrictions.eq(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.STATUS.toString(), DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo()));
		if(recebimento != null){
			criteria.add(Restrictions.eq(PTM_ITEM_RECEB_PROVISORIO_ALIAS_EXT + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), recebimento));
		}
		if(itemRecebimento != null){
			criteria.add(Restrictions.eq(PTM_ITEM_RECEB_PROVISORIO_ALIAS_EXT + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento));
		}
		return criteria;
	}
							
	public List<PtmTicket> obterTicketPorItemRecebimento(Long itemRecebimento){
		DetachedCriteria criteria = obterCriteriaTicketPorSeqItemRecebProvisorio(itemRecebimento);
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaTicketPorSeqItemRecebProvisorio(
			Long itemRecebimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTicket.class, "TICKET");
		
		if(itemRecebimento != null){
			criteria.add(Restrictions.eq("TICKET."+ PtmTicket.Fields.ITEM_RECEB_PROVISORIO_SEQ.toString(), itemRecebimento));
		}
		return criteria;
	}
	
	public List<PtmTicket> obterTicketPorServidor(RapServidores servidor){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTicket.class, TICKET_ALIAS);
		criteria.createAlias(TICKET_ALIAS_EXT + PtmTicket.Fields.PTMSTATUSTICKET.toString(), STATUS_TICKET_ALIAS, JoinType.INNER_JOIN);
		criteria.createAlias(TICKET_ALIAS_EXT + PtmTicket.Fields.ITEM_RECEB_PROVISORIO.toString(), PTM_ITEM_RECEB_PROVISORIO_ALIAS, JoinType.INNER_JOIN);
		criteria.createAlias(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.USER_TICKETS.toString(), PTM_USER_TICKET_ALIAS, JoinType.INNER_JOIN);
		criteria.createAlias(PTM_USER_TICKET_ALIAS+"." + PtmUserTicket.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);

		criteria.setFetchMode(TICKET_ALIAS_EXT + PtmTicket.Fields.PTMSTATUSTICKET.toString(), FetchMode.JOIN);
		criteria.setFetchMode(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.USER_TICKETS.toString(), FetchMode.JOIN);

		DetachedCriteria subQuery = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_2_ALIAS);
		subQuery.setProjection(Projections.max(STATUS_TICKET_2_ALIAS_EXT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()));
		subQuery.createAlias(STATUS_TICKET_2_ALIAS_EXT + PtmStatusTicket.Fields.TICKET.toString(), TICKET_2_ALIAS, JoinType.INNER_JOIN);
		subQuery.add(Restrictions.eqProperty(TICKET_ALIAS_EXT + PtmTicket.Fields.SEQ.toString(), TICKET_2_ALIAS_EXT + PtmTicket.Fields.SEQ.toString()));
		criteria.add(Subqueries.propertyEq(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.DATA_CRIACAO.toString(), subQuery));
		
		criteria.add(Restrictions.in(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.STATUS.toString(), new Integer[]{1,2,3}) );
		
		criteria.add(Restrictions.eq("SER."+ RapServidores.Fields.MATRICULA.toString(), servidor.getId().getMatricula()));
		criteria.add(Restrictions.eq("SER."+ RapServidores.Fields.VIN_CODIGO.toString(), servidor.getId().getVinCodigo()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #48321 C1
	 * @return
	 */
	public List<AceiteTecnicoPendenteVO> obterAceitesPendentes(){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTicket.class, "TIC");
		criteria.createAlias(ALIAS_TIC+PtmTicket.Fields.ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_TIC+PtmTicket.Fields.PTMSTATUSTICKET.toString(), STATUS_TICKET_ALIAS, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_IRP+PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_IRP + PtmItemRecebProvisorios.Fields.SEQ.toString()), 
						AceiteTecnicoPendenteVO.Fields.IRP_SEQ.toString())
				.add(Projections.property(ALIAS_IRP + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString()), 
						AceiteTecnicoPendenteVO.Fields.RECEB.toString())
				.add(Projections.property(ALIAS_IRP + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString()), 
						AceiteTecnicoPendenteVO.Fields.ITEM.toString())
				.add(Projections.property(ALIAS_IRP + PtmItemRecebProvisorios.Fields.STATUS.toString()), 
						AceiteTecnicoPendenteVO.Fields.STATUS.toString())
				.add(Projections.property(ALIAS_STT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()), 
						AceiteTecnicoPendenteVO.Fields.DATA_ULT_ATUALIZACAO.toString())
				.add(Projections.property(ALIAS_STT + PtmStatusTicket.Fields.SEQ.toString()), 
						AceiteTecnicoPendenteVO.Fields.SEQ_PTM_STATUS_TICKET.toString())
				.add(Projections.property(ALIAS_ATA + PtmAreaTecAvaliacao.Fields.SEQ.toString()), 
						AceiteTecnicoPendenteVO.Fields.SEQ_PTM_AREA_TEC_AVALIACAO.toString())
				.add(Projections.property(ALIAS_ATA + PtmAreaTecAvaliacao.Fields.NOME_AREA_TEC_AVALIACAO.toString()), 
						AceiteTecnicoPendenteVO.Fields.NOME_AREA_TEC_AVALIACAO.toString())
				);
		criteria.add(Restrictions.ne(ALIAS_STT + PtmStatusTicket.Fields.STATUS.toString(), 5));
		criteria.add(Restrictions.ne(ALIAS_IRP + PtmItemRecebProvisorios.Fields.STATUS.toString(), 6));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class);
		subCriteria.setProjection(Projections.property(PtmAreaTecAvaliacao.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		subCriteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.IND_EMAIL_SUMARIZADO.toString(), Boolean.TRUE));
		
		criteria.add(Subqueries.propertyIn(ALIAS_ATA + PtmAreaTecAvaliacao.Fields.SEQ.toString(), subCriteria));
		
		DetachedCriteria subCriteria2 = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_ALIAS);
		subCriteria2.setProjection(Projections.max(ALIAS_STT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()));
		subCriteria2.add(Restrictions.eqProperty(ALIAS_STT + PtmStatusTicket.Fields.TICKET_SEQ.toString(), ALIAS_TIC+PtmTicket.Fields.SEQ.toString()));
		
		criteria.add(Subqueries.propertyEq(ALIAS_STT + PtmStatusTicket.Fields.DATA_CRIACAO.toString(), subCriteria2));
		
		criteria.addOrder(Order.asc(ALIAS_IRP + PtmItemRecebProvisorios.Fields.SEQ.toString()));
		criteria.addOrder(Order.desc(ALIAS_STT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AceiteTecnicoPendenteVO.class));
		
		
		return executeCriteria(criteria);
	}
	/**
	 * #44297 C7
	 * @return
	 */
	public AceiteTecnicoPendenteVO consultarStatusDeTicket(Integer ticSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTicket.class, "TIC");
		criteria.createAlias(ALIAS_TIC+PtmTicket.Fields.ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_TIC+PtmTicket.Fields.PTMSTATUSTICKET.toString(), STATUS_TICKET_ALIAS, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_IRP+PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_TIC+PtmTicket.Fields.SEQ.toString()), 
						AceiteTecnicoPendenteVO.Fields.TIC_SEQ.toString())
    			.add(Projections.property(ALIAS_STT + PtmStatusTicket.Fields.SEQ.toString()), 
						AceiteTecnicoPendenteVO.Fields.SEQ_PTM_STATUS_TICKET.toString())
						
				.add(Projections.property(ALIAS_ATA + PtmAreaTecAvaliacao.Fields.SERVIDOR_CC_MATRICULA.toString()), 
						AceiteTecnicoPendenteVO.Fields.MATRICULA.toString())
				.add(Projections.property(ALIAS_ATA + PtmAreaTecAvaliacao.Fields.SERVIDOR_CC_VIN_CODIGO.toString()), 
						AceiteTecnicoPendenteVO.Fields.VIN_CODIGO.toString())
				);
		criteria.add(Restrictions.ne(ALIAS_STT + PtmStatusTicket.Fields.STATUS.toString(), 5));
		criteria.add(Restrictions.ne(ALIAS_IRP + PtmItemRecebProvisorios.Fields.STATUS.toString(), 6));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(PtmAreaTecAvaliacao.class);
		subCriteria.setProjection(Projections.property(PtmAreaTecAvaliacao.Fields.SEQ.toString()));
		subCriteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.SITUACAO.toString(), DominioSituacao.A));
		subCriteria.add(Restrictions.eq(PtmAreaTecAvaliacao.Fields.IND_EMAIL_SUMARIZADO.toString(), Boolean.FALSE));
		
		criteria.add(Subqueries.propertyIn(ALIAS_ATA + PtmAreaTecAvaliacao.Fields.SEQ.toString(), subCriteria));
		
		DetachedCriteria subCriteria2 = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_ALIAS);
		subCriteria2.setProjection(Projections.max(ALIAS_STT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()));
		subCriteria2.add(Restrictions.eqProperty(ALIAS_STT + PtmStatusTicket.Fields.TICKET_SEQ.toString(), ALIAS_TIC+PtmTicket.Fields.SEQ.toString()));
		
		criteria.add(Subqueries.propertyEq(ALIAS_STT + PtmStatusTicket.Fields.DATA_CRIACAO.toString(), subCriteria2));
		
		criteria.add(Restrictions.eq( ALIAS_TIC+PtmTicket.Fields.SEQ.toString(), ticSeq));
		
		criteria.addOrder(Order.asc(ALIAS_STT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AceiteTecnicoPendenteVO.class));

		List<AceiteTecnicoPendenteVO> retorno = executeCriteria(criteria);

		if (retorno.isEmpty()) {
			return null;
		}
		return retorno.get(0);
		
	}
	
	
	/**
	 * Count da consulta selecionar todos os tickets para um determinado item de recebimento provisório.
	 * @param itemRecebimento
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @return 
	 */
	public List<TicketsVO> carregarTicketsItemRecebimentoProvisorio(ItemRecebimentoVO itemRecebimento, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarTicketsItemRecebimentoProvisorio(itemRecebimento);
		
		if (StringUtils.isEmpty(orderProperty)) {
			criteria.addOrder(Order.asc(ALIAS_TIC + PtmTicket.Fields.SEQ.toString()));
			criteria.addOrder(Order.desc(ALIAS_STT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()));
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Count da consulta selecionar todos os tickets para um determinado item de recebimento provisório.
	 * @param itemRecebimento
	 * @return Long
	 */
	public Long carregarTicketsItemRecebimentoProvisorioCount(ItemRecebimentoVO itemRecebimento) {
		DetachedCriteria criteria = montarTicketsItemRecebimentoProvisorio(itemRecebimento);
		
		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria montarTicketsItemRecebimentoProvisorio(ItemRecebimentoVO itemRecebimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmTicket.class, "TIC");
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property(ALIAS_STT + PtmStatusTicket.Fields.SEQ.toString()), TicketsVO.Fields.SEQ.toString());
		projectionList.add(Projections.property(ALIAS_STT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()), TicketsVO.Fields.DATA_CRIACAO.toString());
		projectionList.add(Projections.property(ALIAS_STT + PtmStatusTicket.Fields.STATUS.toString()), TicketsVO.Fields.STATUS.toString());
		
		criteria.setProjection(projectionList);
		
		criteria.createAlias(ALIAS_TIC + PtmTicket.Fields.ITEM_RECEB_PROVISORIO.toString(), "IRP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_TIC + PtmTicket.Fields.PTMSTATUSTICKET.toString(), "STT", JoinType.LEFT_OUTER_JOIN);
		
		Conjunction conjunction = Restrictions.conjunction();
		conjunction.add(Restrictions.eq(ALIAS_IRP + PtmItemRecebProvisorios.Fields.SCE_NRP_SEQ.toString(), itemRecebimento.getRecebimento()));
		conjunction.add(Restrictions.eq(ALIAS_IRP + PtmItemRecebProvisorios.Fields.SCE_NRO_ITEM.toString(), itemRecebimento.getItemRecebimento()));
		
		Disjunction disjunction = Restrictions.disjunction(); 
		disjunction.add(Restrictions.eq(ALIAS_TIC + PtmTicket.Fields.ITEM_RECEB_PROVISORIO_SEQ.toString(), itemRecebimento.getIrpSeq())); 
		disjunction.add(conjunction); 
		criteria.add(disjunction); 

	
		criteria.setResultTransformer(Transformers.aliasToBean(TicketsVO.class));		
		return criteria;
	}
	
	/**
	 * Melhoria #50614 estória #43446.
	 * Consulta que retornar um ticket para um item de recebimento.
	 * @param seqItemRecebProvisorio
	 * @return PtmTicket
	 */
	public PtmTicket obterTicketPorSeqItemRecebProvisorio(Long seqItemRecebProvisorio){
		DetachedCriteria criteria = obterCriteriaTicketPorSeqItemRecebProvisorio(seqItemRecebProvisorio);
		
		return (PtmTicket)executeCriteriaUniqueResult(criteria);
	}
}
