package br.gov.mec.aghu.ambulatorio.dao;


import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacRadioAcompTomos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;

public class AacRadioAcompTomosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacRadioAcompTomos> {

	private static final long serialVersionUID = -8785600274273701608L;
	
	// classe vazia	
	/**
	 * C3 estoria 40230
	 * 
	 */
public List<AacRadioAcompTomos> pesquisarPorDataPaceienteServidor(Integer pacCodigo,Date dtConsulta,Short serVinCodigoAtendido,Integer serMatriculaAtendido){
	
		
	final DetachedCriteria criteria = DetachedCriteria.forClass(AacRadioAcompTomos.class);
		criteria.add(Restrictions.eq(AacRadioAcompTomos.Fields.DTHR_EXAME.toString(),dtConsulta));
		criteria.add(Restrictions.eq(AacRadioAcompTomos.Fields.PACIENTE.toString()+"."+AipPacientes.Fields.CODIGO,pacCodigo));
		criteria.add(Restrictions.isNull(AacRadioAcompTomos.Fields.SERVIDOR.toString() + "." + RapServidores.Fields.MATRICULA));
		
		return executeCriteria(criteria);
				
	}


	/**
	 * Estória do Usuário #40230
	 * ORADB: Trigger AACT_RAT_BRU
	 * @author marcelo.deus
	 * 
	 */
	 public void atualizarSituacaoAacRadioAcompTomos(AacRadioAcompTomos newAacRadioAcompTomos, AacRadioAcompTomos oldAacRadioAcompTomos){
		 if(newAacRadioAcompTomos.getSeq() != oldAacRadioAcompTomos.getSeq() && 
			 oldAacRadioAcompTomos.getDtBlc() == null &&
			 newAacRadioAcompTomos.getDtRx() != null &&
			 newAacRadioAcompTomos.getDtEfl() != null &&
			 newAacRadioAcompTomos.getDtC3d() != null &&
			 newAacRadioAcompTomos.getDtAm() != null &&
			 newAacRadioAcompTomos.getDtMrc() != null &&
			 newAacRadioAcompTomos.getDtPln() != null &&
			 newAacRadioAcompTomos.getDtAm2() != null &&
			 newAacRadioAcompTomos.getDtLib() != null &&
			 newAacRadioAcompTomos.getSituacao().equals(DominioSituacao.A)){
		 newAacRadioAcompTomos.setSituacao(DominioSituacao.I);
		 atualizar(newAacRadioAcompTomos);
	 }
 }
	


}
