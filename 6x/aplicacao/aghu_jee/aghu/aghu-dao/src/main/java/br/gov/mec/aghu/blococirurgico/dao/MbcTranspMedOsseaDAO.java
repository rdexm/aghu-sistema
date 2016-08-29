package br.gov.mec.aghu.blococirurgico.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.blococirurgico.vo.RelatorioTransplantesRealizTMOOutrosVO;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcTranspMedOssea;

public class MbcTranspMedOsseaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcTranspMedOssea> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5298597614490392971L;
	
	public List<RelatorioTransplantesRealizTMOOutrosVO> pesquisarTranspMedOsseaPorDtInicioEDtFim(Date dtInicio, Date dtFim) {
		String aliasPac = "pac";
		String aliasTmo = "tmo";
		String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTranspMedOssea.class, aliasTmo);
		
		Projection p = Projections.projectionList()
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.PRONTUARIO.toString()), RelatorioTransplantesRealizTMOOutrosVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.NOME.toString()), RelatorioTransplantesRealizTMOOutrosVO.Fields.NOME.toString())
				.add(Projections.property(aliasPac + ponto + AipPacientes.Fields.DATA_NASCIMENTO.toString()), RelatorioTransplantesRealizTMOOutrosVO.Fields.DT_NASCIMENTO.toString())
				.add(Projections.property(aliasTmo + ponto + MbcTranspMedOssea.Fields.DT_TRANSPLANTE.toString()), RelatorioTransplantesRealizTMOOutrosVO.Fields.DT_TRANSPLANTE.toString())
				.add(Projections.property(aliasTmo + ponto + MbcTranspMedOssea.Fields.IND_TMO.toString()), RelatorioTransplantesRealizTMOOutrosVO.Fields.IND_TMO.toString());
		
		criteria.setProjection(p);
		
		criteria.createAlias(aliasTmo + ponto + MbcTranspMedOssea.Fields.PACIENTE.toString(), aliasPac);
		
		criteria.add(Restrictions.between(aliasTmo + ponto + MbcTranspMedOssea.Fields.DT_TRANSPLANTE.toString(), dtInicio, dtFim));
		
		criteria.addOrder(Order.asc(aliasPac + ponto + AipPacientes.Fields.PRONTUARIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(RelatorioTransplantesRealizTMOOutrosVO.class));
		
		return executeCriteria(criteria);
	}
	
}
