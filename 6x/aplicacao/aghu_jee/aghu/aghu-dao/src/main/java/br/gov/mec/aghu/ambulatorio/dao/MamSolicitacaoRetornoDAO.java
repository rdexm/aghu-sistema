package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.ambulatorio.vo.CursorCTicketRetornoVO;
import br.gov.mec.aghu.ambulatorio.vo.CursorCurTicketVO;
import br.gov.mec.aghu.ambulatorio.vo.MamSolicitacaoRetornoDocumentoImpressoVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.MamSolicitacaoRetorno;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

/**
 * Classe responsável pelo acesso a banco da entidade MamSolicitacaoRetorno.
 * 
 */
public class MamSolicitacaoRetornoDAO extends BaseDao<MamSolicitacaoRetorno> {

	private static final long serialVersionUID = -117414245427996527L;

	/**
	 * Realiza uma busca por Solicitação de Retorno a partir da Consulta de retorno informada.
	 * 
	 * @param numeroConsulta - Número da Consulta de retorno
	 * @return Lista de Solicitações de Retorno relacionadas
	 */
	public List<MamSolicitacaoRetorno> obterSolicitacaoRetornoPorConsultaRetorno(Integer numeroConsulta) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamSolicitacaoRetorno.class, "MSR");

		criteria.createAlias("MSR." + MamSolicitacaoRetorno.Fields.AAC_CONSULTAS_BY_CON_NUMERO_RETORNO.toString(), "CON");
		
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * 
	 */
	public MamSolicitacaoRetorno obterEspecialidadeSolicitacaoRetorno (MamSolicitacaoRetorno mamSolicitacaoRetorno){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamSolicitacaoRetorno.class);
		criteria.add(Restrictions.eq(MamSolicitacaoRetorno.Fields.SEQ.toString(), mamSolicitacaoRetorno.getSeq()));
		return (MamSolicitacaoRetorno) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #43033 C1
	 * Consultas usadas para verificar se existe algum documento a ser impresso para o registro selecionado.
	 * @param conNumero
	 * @return
	 */
	public List<MamSolicitacaoRetornoDocumentoImpressoVO> obterDocumentoASerImpresso(Integer conNumero, boolean cursor){
		MamSolicitacaoRetornoDocumentoImpressoQueryBuilder builder = new MamSolicitacaoRetornoDocumentoImpressoQueryBuilder();
		return executeCriteria(builder.build(conNumero, cursor));
	}
	
	/**
	 * #43033 P3 
	 * Consultas para obter CursorCurTicketVO
	 * Substitui Cursor cur_ticket em P3
	 * @param conNumero
	 * @return
	 */
	public List<CursorCurTicketVO> obterCursorCurTicketVO(Integer conNumero, boolean cursor){
		MamSolicitacaoRetornoDocumentoImpressoQueryBuilder builder = new MamSolicitacaoRetornoDocumentoImpressoQueryBuilder();
		return executeCriteria(builder.build(conNumero, cursor));
	}
	
	/**
	 * #43033 P1 
	 * Consultas para obter CURSOR c_TICKET_RETORNO
	 * Substitui CURSOR c_TICKET_RETORNO em P1
	 * @param cSorSeq
	 * @return
	 */
	public List<CursorCTicketRetornoVO> obterCursorTicketRetorno(Long cSorSeq){
		CursorCTicketRetornoVOQueryBuilder builder = new CursorCTicketRetornoVOQueryBuilder();
		return executeCriteria(builder.build(cSorSeq));
	}

	@Override
	public MamSolicitacaoRetorno obterPorChavePrimaria(Object pk) {
		MamSolicitacaoRetorno mamSolicitacaoRetorno = super.obterPorChavePrimaria(pk);
		return mamSolicitacaoRetorno;
	}

	@Override
	public MamSolicitacaoRetorno atualizar(MamSolicitacaoRetorno elemento) {
		MamSolicitacaoRetorno retorno = super.atualizar(elemento);
		retorno.setIndImpresso("S");
		return retorno;
	}
	
	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamSolicitacaoRetorno buscarSolicitacaoRetornoPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamSolicitacaoRetorno.class);

		criteria.add(Restrictions.eq(MamSolicitacaoRetorno.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamSolicitacaoRetorno> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
}
