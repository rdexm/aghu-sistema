package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.pesquisa.vo.PesquisaLoteExamesFiltroVO;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.model.AelLoteExameUsual;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;


public class AelLoteExameUsualDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelLoteExameUsual> {

	private static final long serialVersionUID = 752880145748081742L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelLoteExameUsual.class);
		return criteria;
    }
	
	public List<AelLoteExameUsual> obterLotesPorUnidadeFuncional(AghUnidadesFuncionais unidadeSolicitante) {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.UNFSEQ.toString(), unidadeSolicitante));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public List<AelLoteExameUsual> obterLotesPorGrupo() {
		DetachedCriteria criteria = obterCriteria();
		
		criteria.add(Restrictions.in(AelLoteExameUsual.Fields.ORIGEM.toString(), new Object[]{DominioOrigemAtendimento.I, DominioOrigemAtendimento.N}));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.DEFAULT.toString(), DominioSimNao.S));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	public List<AelLoteExameUsual> obterLotesDefaultComIdDiferente(AelLoteExameUsual leu) {
		DetachedCriteria criteria = obterCriteria();
		if(leu.getSeq()!=null){
			criteria.add(Restrictions.not(Restrictions.eq(AelLoteExameUsual.Fields.SEQ.toString(), leu.getSeq())));
		}
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.ORIGEM.toString(), leu.getOrigem()));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.DEFAULT.toString(), DominioSimNao.S));
		
		return executeCriteria(criteria);
	}
	
	public List<AelLoteExameUsual> obterLotesDefaultComIdDiferentePorEspOrigem(AelLoteExameUsual leu) {
		DetachedCriteria criteria = obterCriteria();
		
		if(leu.getSeq()!=null){
			criteria.add(Restrictions.not(Restrictions.eq(AelLoteExameUsual.Fields.SEQ.toString(), leu.getSeq())));
		}
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.ORIGEM.toString(), leu.getOrigem()));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.ESPSEQ.toString(), leu.getEspSeq()));
		
		return executeCriteria(criteria);
	}
	
	public List<AelLoteExameUsual> obterLotesDefaultComIdDiferentePorUnidadeOrigem(AelLoteExameUsual leu) {
		DetachedCriteria criteria = obterCriteria();
		
		if(leu.getSeq()!=null){
			criteria.add(Restrictions.not(Restrictions.eq(AelLoteExameUsual.Fields.SEQ.toString(), leu.getSeq())));
		}
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.ORIGEM.toString(), leu.getOrigem()));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.UNFSEQ.toString(), leu.getUnfSeq()));
		
		
		return executeCriteria(criteria);
	}
	
	public List<AelLoteExameUsual> obterLotesDefaultComIdDiferentePorGrupoOrigem(AelLoteExameUsual leu) {
		DetachedCriteria criteria = obterCriteria();
		
		if(leu.getSeq()!=null){
			criteria.add(Restrictions.not(Restrictions.eq(AelLoteExameUsual.Fields.SEQ.toString(), leu.getSeq())));
		}
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.ORIGEM.toString(), leu.getOrigem()));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.GRUSEQ.toString(), leu.getGruSeq()));
		
		
		return executeCriteria(criteria);
	}
	
	
	public List<AelLoteExameUsual> obterLotesPorEspecialidade(AghEspecialidades especialidade) {
		if (especialidade == null) {
			return null;
		}
		
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.ESPSEQ.toString(), especialidade));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.ESPSEQ.toString(), especialidade.getEspecialidadeAgrupaLoteExame()));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.ORIGEM.toString(), DominioOrigemAtendimento.A));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		return executeCriteria(criteria);
	}
	
	
	public List<AelLoteExameUsual> getDadosLoteUnidades() {
		DetachedCriteria criteria = obterCriteria();

		criteria.createCriteria(AelLoteExameUsual.Fields.SERVIDOR.toString(), "servidor", Criteria.INNER_JOIN);
		criteria.createCriteria(AelLoteExameUsual.Fields.UNFSEQ.toString(), AelLoteExameUsual.Fields.UNFSEQ.toString(), Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.isNotNull(AelLoteExameUsual.Fields.UNFSEQ.toString()));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc(AelLoteExameUsual.Fields.UNFSEQDESCRICAO.toString()));  
		
		return executeCriteria(criteria);
	}
	
	public List<AelLoteExameUsual> getDadosLoteGrupos() {
		DetachedCriteria criteria = obterCriteria();

		criteria.createCriteria(AelLoteExameUsual.Fields.SERVIDOR.toString(), "servidor", Criteria.INNER_JOIN);
		criteria.createCriteria(AelLoteExameUsual.Fields.GRUSEQ.toString(), AelLoteExameUsual.Fields.GRUSEQ.toString(), Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.isNotNull(AelLoteExameUsual.Fields.GRUSEQ.toString()));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc(AelLoteExameUsual.Fields.GRUDESCRICAO.toString()));  
		
		return executeCriteria(criteria);
	}
	
	public List<AelLoteExameUsual> getDadosLoteEspecialidades() {
		DetachedCriteria criteria = obterCriteria();

		criteria.createCriteria(AelLoteExameUsual.Fields.SERVIDOR.toString(), "servidor", Criteria.INNER_JOIN);
		criteria.createCriteria(AelLoteExameUsual.Fields.ESPSEQ.toString(), AelLoteExameUsual.Fields.ESPSEQ.toString(), Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.isNotNull(AelLoteExameUsual.Fields.ESPSEQ.toString()));
		criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc(AelLoteExameUsual.Fields.ESPDESCRICAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	
	public Long pesquisaLotesPorParametrosCount(PesquisaLoteExamesFiltroVO filtro){
		DetachedCriteria criteria = obterCriteria();

		criteria.createCriteria(AelLoteExameUsual.Fields.GRUSEQ.toString(), "gru", Criteria.LEFT_JOIN);
		criteria.createCriteria(AelLoteExameUsual.Fields.ESPSEQ.toString(), "esp", Criteria.LEFT_JOIN);
		criteria.createCriteria(AelLoteExameUsual.Fields.UNFSEQ.toString(), "unf", Criteria.LEFT_JOIN);

		if(filtro.getCodigoLote()!=null){
			criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.SEQ.toString(), Short.parseShort(filtro.getCodigoLote().toString())));	
		}

		if(filtro.getGrupoExame()!=null){
			criteria.add(Restrictions.eq("gru."+ AelGrupoExameUsual.Fields.SEQ.toString(), filtro.getGrupoExame().getSeq()));
		}

		if(filtro.getEspecialidade() != null){
			criteria.add(Restrictions.eq("esp."+AghEspecialidades.Fields.SEQ, filtro.getEspecialidade().getSeq()));	
		}

		if(filtro.getUnidadeFuncional() != null){
			criteria.add(Restrictions.eq("unf."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), filtro.getUnidadeFuncional().getSeq()));	
		}

		if(filtro.getIndLoteDefault() != null){
			criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.DEFAULT.toString(), filtro.getIndLoteDefault()));	
		}
		
		if(filtro.getOrigemAtendimento() != null){
			criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.ORIGEM.toString(), filtro.getOrigemAtendimento()));	
		}

		if(filtro.getIndSituacao() != null){
			criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.SITUACAO.toString(), filtro.getIndSituacao()));	
		}

		return this.executeCriteriaCount(criteria);
	}
	
	public List<AelLoteExameUsual> pesquisaLotesPorParametros(Integer firstResult,	Integer maxResult, String orderProperty, boolean asc, PesquisaLoteExamesFiltroVO filtro){
		DetachedCriteria criteria = obterCriteria();

		criteria.createCriteria(AelLoteExameUsual.Fields.GRUSEQ.toString(), "gru", Criteria.LEFT_JOIN);
		criteria.createCriteria(AelLoteExameUsual.Fields.ESPSEQ.toString(), "esp", Criteria.LEFT_JOIN);
		criteria.createCriteria(AelLoteExameUsual.Fields.UNFSEQ.toString(), "unf", Criteria.LEFT_JOIN);

		if(filtro.getCodigoLote()!=null){
			criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.SEQ.toString(), Short.parseShort(filtro.getCodigoLote().toString())));	
		}

		if(filtro.getGrupoExame()!=null){
			criteria.add(Restrictions.eq("gru."+ AelGrupoExameUsual.Fields.SEQ.toString(), filtro.getGrupoExame().getSeq()));
		}

		if(filtro.getEspecialidade() != null){
			criteria.add(Restrictions.eq("esp."+AghEspecialidades.Fields.SEQ, filtro.getEspecialidade().getSeq()));	
		}

		if(filtro.getUnidadeFuncional() != null){
			criteria.add(Restrictions.eq("unf."+AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), filtro.getUnidadeFuncional().getSeq()));	
		}

		if(filtro.getIndLoteDefault() != null){
			criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.DEFAULT.toString(), filtro.getIndLoteDefault()));	
		}

		if(filtro.getOrigemAtendimento() != null){
			criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.ORIGEM.toString(), filtro.getOrigemAtendimento()));	
		}
		
		if(filtro.getIndSituacao() != null){
			criteria.add(Restrictions.eq(AelLoteExameUsual.Fields.SITUACAO.toString(), filtro.getIndSituacao()));	
		}

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	@Override
	protected void obterValorSequencialId(AelLoteExameUsual elemento) {
		Query query = createHibernateQuery(" select max(seq) from AelLoteExameUsual");
		Short retValue = (Short) query.uniqueResult();

		if (retValue != null) {
			elemento.setSeq((short) (retValue + 1));
		}else{
			elemento.setSeq((short)1);		
		}
	}
}
