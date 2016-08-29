package br.gov.mec.aghu.patrimonio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioStatusTicket;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.PtmStatusTicket;
import br.gov.mec.aghu.model.PtmTicket;
import br.gov.mec.aghu.model.PtmUserTicket;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.vo.ResponsaveisStatusTicketsVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PtmStatusTicketDAO extends BaseDao<PtmStatusTicket>{

	private static final String UTK = "UTK";

	private static final String UTI = "UTI";

	private static final String SER = "SER";

	private static final String PES = "PES";

	private static final long serialVersionUID = -3937191715347644941L;
	
	private static final String TICKET_ALIAS_PES = "PES.";

	private static final String TICKET_ALIAS = "TKT";
	
	private static final String TICKET_ALIAS_UTI = "UTI.";
	
	private static final String TICKET_ALIAS_EXT = "TKT.";
	
	private static final String TICKET_ALIAS_SER = "SER.";
	
	private static final String TICKET_2_ALIAS = "TKT2";
	
	private static final String TICKET_2_ALIAS_EXT = "TKT2.";
	
	private static final String STATUS_TICKET_ALIAS = "STT";
	
	private static final String STATUS_TICKET_ALIAS_EXT = "STT.";
	
	private static final String STATUS_TICKET_2_ALIAS = "STT2";

	private static final String STATUS_TICKET_2_ALIAS_EXT = "STT2.";

	private static final String PTM_ITEM_RECEB_PROVISORIO_ALIAS = "PIR";
	
	private static final String PTM_ITEM_RECEB_PROVISORIO_ALIAS_EXT = "PIR.";
	
	private static final String SCE_ITEM_RECEB_PROVISORIO_ALIAS = "IRP";

	/**
	 * RN03 da estória #44297
	 * Obtém todos os tickets que estão em abertos.
	 * @return
	 */
	public List<PtmStatusTicket> pesquisarTicketPorStatusAtual() {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_ALIAS);

		criteria.createAlias(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.TICKET.toString(), TICKET_ALIAS, JoinType.INNER_JOIN);
		criteria.createAlias(TICKET_ALIAS_EXT + PtmTicket.Fields.ITEM_RECEB_PROVISORIO.toString(), PTM_ITEM_RECEB_PROVISORIO_ALIAS, JoinType.INNER_JOIN);
		criteria.createAlias(PTM_ITEM_RECEB_PROVISORIO_ALIAS_EXT + PtmItemRecebProvisorios.Fields.SCE_ITEM_RECEB_PROVISORIO.toString(),
				SCE_ITEM_RECEB_PROVISORIO_ALIAS, JoinType.INNER_JOIN);
		
		DetachedCriteria subQuery = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_2_ALIAS);

		subQuery.setProjection(Projections.max(STATUS_TICKET_2_ALIAS_EXT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()));

		subQuery.createAlias(STATUS_TICKET_2_ALIAS_EXT + PtmStatusTicket.Fields.TICKET.toString(), TICKET_2_ALIAS, JoinType.INNER_JOIN);

		subQuery.add(Restrictions.eqProperty(TICKET_ALIAS_EXT + PtmTicket.Fields.SEQ.toString(), TICKET_2_ALIAS_EXT + PtmTicket.Fields.SEQ.toString()));

		criteria.add(Subqueries.propertyEq(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.DATA_CRIACAO.toString(), subQuery));
		criteria.add(Restrictions.or(
				Restrictions.eq(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.STATUS.toString(), DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo()),
				Restrictions.eq(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.STATUS.toString(), DominioStatusTicket.EXPIRADO.getCodigo())));
		
		return executeCriteria(criteria);
	}

	/**
	 * RN06 da estória #44297
	 * Consulta para obter todos os tickets que possui o status aguardando atendimento e está expirado.
	 * @return
	 */
	public List<PtmStatusTicket> pesquisarTicketsStatusAguardandoAtendimento() {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_ALIAS);

		criteria.createAlias(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.TICKET.toString(), TICKET_ALIAS, JoinType.INNER_JOIN);
		criteria.createAlias(TICKET_ALIAS_EXT + PtmTicket.Fields.ITEM_RECEB_PROVISORIO.toString(), PTM_ITEM_RECEB_PROVISORIO_ALIAS, JoinType.INNER_JOIN);
		criteria.createAlias(PTM_ITEM_RECEB_PROVISORIO_ALIAS_EXT + PtmItemRecebProvisorios.Fields.AREA_TECNICA_AVALIACAO.toString(), "ATA", JoinType.INNER_JOIN);

		DetachedCriteria subQuery = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_2_ALIAS);

		subQuery.setProjection(Projections.max(STATUS_TICKET_2_ALIAS_EXT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()));

		subQuery.createAlias(STATUS_TICKET_2_ALIAS_EXT + PtmStatusTicket.Fields.TICKET.toString(), TICKET_2_ALIAS, JoinType.INNER_JOIN);

		subQuery.add(Restrictions.eqProperty(TICKET_ALIAS_EXT + PtmTicket.Fields.SEQ.toString(), TICKET_2_ALIAS_EXT + PtmTicket.Fields.SEQ.toString()));

		criteria.add(Subqueries.propertyEq(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.DATA_CRIACAO.toString(), subQuery));
		criteria.add(Restrictions.eq(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.STATUS.toString(), DominioStatusTicket.AGUARDANDO_ATENDIMENTO.getCodigo()));
		criteria.add(Restrictions.lt(TICKET_ALIAS_EXT + PtmTicket.Fields.DATA_VALIDADE.toString(), new Date()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Consulta que retorna o status mais recente do ticket.
	 * 
	 * @param ticket
	 * @return
	 */
	public PtmStatusTicket obterStatusTicketAtual(PtmTicket ticket){

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_ALIAS);
		
		criteria.createAlias(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.TICKET.toString(), TICKET_ALIAS);

		DetachedCriteria subQuery = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_2_ALIAS);

		subQuery.setProjection(Projections.max(STATUS_TICKET_2_ALIAS_EXT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()));
		
		subQuery.add(Restrictions.eqProperty(STATUS_TICKET_2_ALIAS_EXT + PtmStatusTicket.Fields.SEQ.toString(),
				STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.SEQ.toString()));

		criteria.add(Subqueries.propertyEq(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.DATA_CRIACAO.toString(), subQuery));
		criteria.add(Restrictions.eq(TICKET_ALIAS_EXT + PtmTicket.Fields.SEQ.toString(), ticket.getSeq()));

		List<PtmStatusTicket> retorno = executeCriteria(criteria);
		
		if (retorno.isEmpty()) {
			return null;
		}
		
		return retorno.get(0);
	}
	
	public List<PtmStatusTicket> obterListaStatusDoTicket(PtmTicket ticket){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_ALIAS);
		criteria.createAlias(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.TICKET.toString(), TICKET_ALIAS);
		criteria.createAlias(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.USER_TICKETS.toString(), UTK);
		criteria.add(Restrictions.eq(TICKET_ALIAS_EXT + PtmTicket.Fields.SEQ.toString(), ticket.getSeq()));
		criteria.addOrder(Order.desc(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.DATA_CRIACAO.toString()));
		return executeCriteria(criteria);
	}

	/**
	 * Selecionar os responsáveis por cada status do ticket retornado em C1 
	 * @param seqTicket
	 * @return List<ResponsaveisStatusTicketsVO>
	 */
	public List<ResponsaveisStatusTicketsVO> obterResponsaveisStatusDoTicket(Integer seqTicket){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmStatusTicket.class, STATUS_TICKET_ALIAS);
		
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property(TICKET_ALIAS_UTI + PtmUserTicket.Fields.SERVIDOR.toString()), ResponsaveisStatusTicketsVO.Fields.MATRICULA.toString());
		projectionList.add(Projections.property(TICKET_ALIAS_PES + RapPessoasFisicas.Fields.NOME.toString()), ResponsaveisStatusTicketsVO.Fields.NOME.toString());				
		criteria.setProjection(projectionList);
		
		criteria.createAlias(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.USER_TICKETS.toString(), UTI, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(TICKET_ALIAS_UTI + PtmUserTicket.Fields.SERVIDOR.toString(), SER, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(TICKET_ALIAS_SER + RapServidores.Fields.PESSOA_FISICA.toString(), PES, JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.SEQ.toString(), seqTicket));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ResponsaveisStatusTicketsVO.class));
		return executeCriteria(criteria);
	}
}
