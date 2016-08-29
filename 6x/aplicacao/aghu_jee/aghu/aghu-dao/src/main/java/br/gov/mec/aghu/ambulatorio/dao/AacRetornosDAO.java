package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.ambulatorio.vo.FiltroConsultaRetornoConsultaVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroParametrosPadraoConsultaVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AacRetornosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacRetornos> {
	
	private static final long serialVersionUID = 4551625195625715203L;

	public AacRetornos obterDescricaoRetornoPorCodigo(Integer retSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacRetornos.class);
		criteria.add(Restrictions.eq(AacRetornos.Fields.SEQ.toString(), retSeq));
		return (AacRetornos) executeCriteriaUniqueResult(criteria);
	}
	
	
		
	public List<AacRetornos> obterListaRetornosAtivos(String objPesquisa){
		String parametro = StringUtils.trimToNull(objPesquisa);
		DetachedCriteria criteria = DetachedCriteria.forClass(AacRetornos.class);
		if (StringUtils.isNotEmpty(parametro)) {
			Short seq = -1;			
			if (CoreUtil.isNumeroShort(parametro)){
				seq = Short.valueOf(parametro); 			
			}
			if (seq != -1) {
				criteria.add(Restrictions.eq(AacRetornos.Fields.SEQ.toString(), seq));
			} else {
				criteria.add(Restrictions.ilike(AacRetornos.Fields.DESCRICAO.toString(), parametro, MatchMode.ANYWHERE));
			}
		}
		criteria.add(Restrictions.eq(AacRetornos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.addOrder(Order.asc(AacRetornos.Fields.SEQ.toString()));
		return this.executeCriteria(criteria);
	}
	
	public List<AacRetornos> pesquisarPorNomeCodigoAtivo(Object objPesquisa) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacRetornos.class);
		if (objPesquisa!=null && !((String)objPesquisa).isEmpty()){
			String pesq = (String)objPesquisa; 
			if (StringUtils.isNumeric(pesq)){
				criteria.add(Restrictions.eq(AacRetornos.Fields.SEQ.toString(), Integer.valueOf(pesq)));
			}else{	
				criteria.add(Restrictions.ilike(AacRetornos.Fields.DESCRICAO.toString(), pesq, MatchMode.ANYWHERE));
			}
		}	
		criteria.add(Restrictions.eq(AacRetornos.Fields.IND_SITUACAO.toString(),
				DominioSituacao.A));
		criteria.addOrder(Order.asc(AacRetornos.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	
	public Long pesquisarConsultaCountRetornoConsulta(FiltroConsultaRetornoConsultaVO filtroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacRetornos.class); 
		criteria = montarCriteriaRetornoConsulta(filtroConsulta, criteria);
		return executeCriteriaCount(criteria);
	}

	public List<AacRetornos> pesquisarConsultaPaginadaRetornoConsulta(FiltroParametrosPadraoConsultaVO filtroPadrao, FiltroConsultaRetornoConsultaVO filtroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacRetornos.class); 
		criteria = montarCriteriaRetornoConsulta(filtroConsulta, criteria);
		criteria.addOrder(Order.asc(AacRetornos.Fields.SEQ.toString()));
		return executeCriteria(criteria, filtroPadrao.getFirstResult(), filtroPadrao.getMaxResult(), filtroPadrao.getOrderProperty(), filtroPadrao.isOrdenacaoAscDesc());
	}
	
	private DetachedCriteria montarCriteriaRetornoConsulta(FiltroConsultaRetornoConsultaVO filtro, DetachedCriteria criteria) {
		if (filtro.getSeq() != null) {
			criteria.add(Restrictions.eq(AacRetornos.Fields.SEQ.toString(), filtro.getSeq()));
		}
		if (filtro.getDescricao() != null && StringUtils.isNotBlank(filtro.getDescricao())) {
			criteria.add(Restrictions.ilike(AacRetornos.Fields.DESCRICAO.toString(), filtro.getDescricao(), MatchMode.ANYWHERE));
		}
		if (filtro.getSituacao() != null) {
			criteria.add(Restrictions.eq(AacRetornos.Fields.IND_SITUACAO.toString(), filtro.getSituacao()));
		}
		if (filtro.getAbsenteismo() != null) {
			criteria.add(Restrictions.eq(AacRetornos.Fields.IND_ABSENTEISMO.toString(), filtro.getAbsenteismo()));  
		}
		if (filtro.getDominioSimNao() != null && filtro.getDominioSimNao().isSim()) {
			criteria.add(Restrictions.eq(AacRetornos.Fields.IND_AUSENTE_AMBU.toString(), true));  
		}else if (filtro.getDominioSimNao() != null && !filtro.getDominioSimNao().isSim()) {
			criteria.add(Restrictions.eq(AacRetornos.Fields.IND_AUSENTE_AMBU.toString(), false));  
		}
		
		return criteria;
	}
	
	/**
	 * #27521 #8236 #8233 C4
	 * Query para obter todos os retornos para o relatório de Agenda de Consultas
	 * @param retornoMaximo Quantidade de registros a ser retornado
	 * @return Lista com todos os retornos
	 */
	public List<AacRetornos> obterTodosRetornosRelatorioAgenda(int retornoMaximo){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AacRetornos.class);
		criteria.add(Restrictions.eq(AacRetornos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		return this.executeCriteria(criteria, 0, retornoMaximo, AacRetornos.Fields.SEQ.toString(), true);
	}
}


