package br.gov.mec.aghu.patrimonio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoCxtPostalServidor;
import br.gov.mec.aghu.model.AghCaixaPostal;
import br.gov.mec.aghu.model.AghCaixaPostalServidor;
import br.gov.mec.aghu.model.PtmStatusTicket;
import br.gov.mec.aghu.model.PtmTicket;
import br.gov.mec.aghu.model.PtmUserTicket;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.vo.ResponsavelAceiteTecnicoPendenteVO;
import br.gov.mec.aghu.patrimonio.vo.UserTicketVO;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class PtmUserTicketDAO extends BaseDao<PtmUserTicket>{

	private static final String ALIAS_PES = "PES.";

	private static final String ALIAS_UTI = "UTI.";

	private static final long serialVersionUID = -4086395074936744559L;

	private static final String USER_TICKET_ALIAS = "UST";
	
	private static final String USER_TICKET_ALIAS_EXT = "UST.";
	
	private static final String SERVIDOR_ALIAS = "SER";

	private static final String SERVIDOR_ALIAS_EXT = "SER.";
	
	private static final String TICKET_ALIAS = "TKT";
	
	private static final String TICKET_ALIAS_EXT = "TKT.";
	
	private static final String STATUS_TICKET_ALIAS = "STT";
	
	private static final String STATUS_TICKET_ALIAS_EXT = "STT.";
	
	private static final String CAIXA_POSTAL_ALIAS = "CAP";
	
	private static final String CAIXA_POSTAL_ALIAS_EXT = "CAP.";
	
	private static final String CAIXA_POSTAL_SERVIDOR_ALIAS = "CPS";
	
	private static final String CAIXA_POSTAL_SERVIDOR_ALIAS_EXT = "CPS.";

	/**
	 * Obtém todos os User Tickets cadastrados, retornando-os em uma lista de objetos VO.
	 * 
	 * @return Lista de UserTicketVO
	 */
	public List<UserTicketVO> listarTodosUserTicketVO() {

		DetachedCriteria criteria = DetachedCriteria.forClass(PtmUserTicket.class, USER_TICKET_ALIAS);
		
		ProjectionList projections = Projections.projectionList();

		projections.add(Projections.property(USER_TICKET_ALIAS_EXT + PtmUserTicket.Fields.SEQ.toString()), UserTicketVO.Fields.USER_TICKET_SEQ.toString());
		projections.add(Projections.property(SERVIDOR_ALIAS_EXT + RapServidores.Fields.MATRICULA.toString()), UserTicketVO.Fields.MATRICULA.toString());
		projections.add(Projections.property(SERVIDOR_ALIAS_EXT + RapServidores.Fields.VIN_CODIGO.toString()), UserTicketVO.Fields.VINCULO.toString());
		projections.add(Projections.property(TICKET_ALIAS_EXT + PtmTicket.Fields.SEQ.toString()), UserTicketVO.Fields.TICKET.toString());
		
		criteria.setProjection(projections);
		
		criteria.createAlias(USER_TICKET_ALIAS_EXT + PtmUserTicket.Fields.STATUS_TICKET.toString(), STATUS_TICKET_ALIAS);
		criteria.createAlias(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.TICKET.toString(), TICKET_ALIAS);
		criteria.createAlias(USER_TICKET_ALIAS_EXT + PtmTicket.Fields.SERVIDOR.toString(), SERVIDOR_ALIAS);

		criteria.addOrder(Order.asc(TICKET_ALIAS_EXT + PtmTicket.Fields.SEQ.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(UserTicketVO.class));
		
		return executeCriteria(criteria);
	}

	/**
	 * #43446 - Obtem instancia da entidade passando como parametro o usuario selecionado.
	 * @param servidor - usuario selecionado
	 * @return instancia da entidade
	 */
	public PtmUserTicket obterPorUsuarioSelecionado(RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmUserTicket.class, USER_TICKET_ALIAS);
		criteria.createAlias(USER_TICKET_ALIAS_EXT + PtmUserTicket.Fields.STATUS_TICKET.toString(), STATUS_TICKET_ALIAS, JoinType.INNER_JOIN);
		criteria.createAlias(USER_TICKET_ALIAS_EXT + PtmUserTicket.Fields.CAIXA_POSTAL.toString(), CAIXA_POSTAL_ALIAS, JoinType.INNER_JOIN);
		criteria.createAlias(CAIXA_POSTAL_ALIAS_EXT + AghCaixaPostal.Fields.CAIXA_POSTAL_SERVIDORES.toString(), CAIXA_POSTAL_SERVIDOR_ALIAS, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(STATUS_TICKET_ALIAS_EXT + PtmStatusTicket.Fields.TICKET.toString(), TICKET_ALIAS, JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(USER_TICKET_ALIAS_EXT + PtmUserTicket.Fields.SERVIDOR.toString(), servidor));
		criteria.add(Restrictions.ne(CAIXA_POSTAL_SERVIDOR_ALIAS_EXT + AghCaixaPostalServidor.Fields.SITUACAO.toString(), DominioSituacaoCxtPostalServidor.E));
		List<PtmUserTicket> lista = executeCriteria(criteria);
		if (lista != null && !lista.isEmpty()) {
			return lista.get(0);
		}
		return null;
	}
	
	/**
	 * #48321 - C2
	 * @param seqStatusSeq
	 */
	public List<ResponsavelAceiteTecnicoPendenteVO> obterResponsaveisPorItemRecebimento(Integer seqStatusSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmUserTicket.class, "UTI");
		criteria.createAlias(ALIAS_UTI + PtmUserTicket.Fields.SERVIDOR.toString(), "SER");
		criteria.createAlias(SERVIDOR_ALIAS_EXT + RapServidores.Fields.PESSOA_FISICA.toString(), "PES");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(ALIAS_UTI + PtmUserTicket.Fields.SER_MATRICULA.toString()),ResponsavelAceiteTecnicoPendenteVO.Fields.MATRICULA.toString())
				.add(Projections.property(ALIAS_UTI + PtmUserTicket.Fields.SER_VINCULO.toString()),ResponsavelAceiteTecnicoPendenteVO.Fields.VINCULO.toString())
				.add(Projections.property(ALIAS_PES + RapPessoasFisicas.Fields.NOME.toString()),ResponsavelAceiteTecnicoPendenteVO.Fields.NOME_RESPONSAVEL.toString())
				);
		if(seqStatusSeq != null){
			criteria.add(Restrictions.eq(ALIAS_UTI + PtmUserTicket.Fields.STT_SEQ.toString(), seqStatusSeq));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(ResponsavelAceiteTecnicoPendenteVO.class));
		return executeCriteria(criteria);
	}
	
	/**
	 * #44297 - C8
	 * @param seqStatus
	 */
	public List<PtmUserTicket> obterUsuariosVinculadoAoStatusTicket(Integer seqStatus){
		DetachedCriteria criteria = DetachedCriteria.forClass(PtmUserTicket.class, "UTI");
		
		if(seqStatus != null){
			criteria.add(Restrictions.eq(ALIAS_UTI + PtmUserTicket.Fields.STT_SEQ.toString(), seqStatus));
		}
		
		return executeCriteria(criteria);
	}
	
}
