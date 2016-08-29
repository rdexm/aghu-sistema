package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import javax.inject.Inject;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MciEtiologiaInfeccao;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.MciTopografiaInfeccao;
import br.gov.mec.aghu.model.MciTopografiaProcedimento;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;


public class MciMvtoInfeccaoTopografiaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciMvtoInfeccaoTopografias>{
    
	@Inject
    private IParametroFacade aIParametroFacade;

	private static final long serialVersionUID = -1081110401245709754L;

	/**
	 * 
	 * @param atdSeq
	 * @return Boolean
	 *  
	 * 
	 * PS: Utilizado HQL devido ao SUBSTRING da query.
	 */
	public Boolean obterVerificacaoMvtoInfeccaoDeAtendimento(Integer atdSeq) throws ApplicationBusinessException{
		
		IParametroFacade parametroFacade = getParametroFacade();
		AghParametros parametro = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPOS_ETIOLOGIA_VERIFICAR_INFECCAO);
		
		String tipos = parametro.getVlrTexto();
		
		Boolean retorno = false;
		StringBuilder hql = new StringBuilder(200);
		
		hql.append(" SELECT DISTINCT 1 ");
		hql.append(" FROM ");
		hql.append(MciMvtoInfeccaoTopografias.class.getSimpleName());
		hql.append(" mit JOIN mit.");
		hql.append(MciMvtoInfeccaoTopografias.Fields.TOPOGRAFIA_PROCEDIMENTO.toString());
		hql.append(" top JOIN top.");
		hql.append(MciTopografiaProcedimento.Fields.TOPOGRAFIA_INFECCAO.toString());
		hql.append(" toi WHERE mit.");
		hql.append(MciMvtoInfeccaoTopografias.Fields.ATENDIMENTO.toString());
		hql.append('.');
		hql.append(AghAtendimentos.Fields.SEQ.toString());
		hql.append(" = :atdSeq ");
		hql.append(" AND mit.");
		hql.append(MciMvtoInfeccaoTopografias.Fields.CONFIRMACAO_CCI.toString());
		hql.append(" in( :confirmacaoCci ) ");
		hql.append(" AND SUBSTRING (mit.");
		hql.append(MciMvtoInfeccaoTopografias.Fields.ETIOLOGIA_INFECCAO.toString());
		hql.append('.');
		hql.append(MciEtiologiaInfeccao.Fields.CODIGO.toString());
		hql.append(" , 1, 1) in ( :tipo ) ");
		hql.append(" AND toi.");
		hql.append(MciTopografiaInfeccao.Fields.PACIENTE_INFECTADO.toString());
		hql.append(" = :pacienteInfectado ");
		
		Query query = createHibernateQuery(hql.toString());
		query.setParameter("atdSeq", atdSeq);
		query.setParameterList("confirmacaoCci", new DominioConfirmacaoCCI[]{DominioConfirmacaoCCI.S, DominioConfirmacaoCCI.P});
		
		//query.setParameter("tipo", new String[]{"H","I","P","U","L","T"});
		query.setParameterList("tipo", tipos.split(","));
		query.setParameter("pacienteInfectado", true);
		
		Integer retornoConsulta =(Integer) query.uniqueResult();
		
		if(retornoConsulta != null && retornoConsulta.equals(1)){
			retorno = true;
		}
		
		return retorno; 
	}
	
	protected IParametroFacade getParametroFacade() {
		return aIParametroFacade;
	}
	
	/**
	 * 
	 * Retorna Movimentos de Infecção associados com instituição hospitalar
	 * 
	 * @return Internacao
	 */
	public List<MciMvtoInfeccaoTopografias> pesquisarMovimentosInfeccoesInstituicaoHospitalar(final Integer ihoSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoInfeccaoTopografias.class);
		criteria.add(Restrictions.eq(MciMvtoInfeccaoTopografias.Fields.INSTITUICAO_HOSPITALAR_SEQ.toString(), ihoSeq));
		return executeCriteria(criteria);
	}
}
