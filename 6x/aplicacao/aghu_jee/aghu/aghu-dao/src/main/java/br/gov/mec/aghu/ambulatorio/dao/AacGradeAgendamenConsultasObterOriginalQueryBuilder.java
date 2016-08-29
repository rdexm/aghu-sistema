package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;

public class AacGradeAgendamenConsultasObterOriginalQueryBuilder extends br.gov.mec.aghu.core.persistence.dao.QueryBuilder<Query> {
	
	private static final long serialVersionUID = -5538400217394746187L;
	
	private Integer gradeSeq;

	@Override
	protected Query createProduct() {
		return this.createQueryStateless(this.makeHQL());
	}

	@Override
	protected void doBuild(Query aProduct) {
		aProduct.setParameter("entityId", getGradeSeq());
		
	}
	
	@SuppressWarnings("PMD.ConsecutiveLiteralAppends")
	private String makeHQL() {
		StringBuilder hql = new StringBuilder(1300);
		
		hql.append("SELECT o.seq, ")
		.append("   aacUnidFuncionalSala, ")
		.append("   o.criadoEm, ")
		.append("   o.programada, ")
		.append("   o.convenioSus, ")
		.append("   o.enviaSamis, ")
		.append("   o.emiteTicket, ")
		.append("   o.emiteBa, ")
		.append("   o.procedimento, ")
		.append("   equipe, ")
		.append("   especialidade, ")
		.append("   servidor, ")
		.append("   servidorAlterado, ")
		.append("   profEspecialidade, ")
		.append("   profServidor, ")
		.append("   o.alteradoEm, ")
		.append("   o.dtUltimaGeracao, ")
		.append("   o.exigeProntuario, ")
		.append("   o.indAvisaConsultaTurno, ")
		.append("   o.tempoAtendAnterior, ")
		.append("   projetoPesquisa, ")
		.append("   o.indRefCompleta, ")
		.append("   condicaoAtendimento, ")
		.append("   tipoAgendamento, ")
		.append("   pagador, ")
		.append("   formaAgendamento, ")
		.append("   unidadeFuncional, ")
		.append("   o.uslSala, ")
		.append("   o.preSerMatricula, ")
		.append("   o.preSerVinCodigo, ")
		.append("   o.indSituacao ")
		.append("FROM AacGradeAgendamenConsultas o ")
		.append("LEFT OUTER JOIN o.aacUnidFuncionalSala aacUnidFuncionalSala ")
		.append("LEFT OUTER JOIN o.equipe equipe ")
		.append("LEFT OUTER JOIN o.especialidade especialidade ")
		.append("LEFT OUTER JOIN o.servidor servidor ")
		.append("LEFT OUTER JOIN o.servidorAlterado servidorAlterado ")
		.append("LEFT OUTER JOIN o.profEspecialidade profEspecialidade ")
		.append("LEFT OUTER JOIN o.profServidor profServidor ")
		.append("LEFT OUTER JOIN o.projetoPesquisa projetoPesquisa ")
		.append("LEFT OUTER JOIN o.condicaoAtendimento condicaoAtendimento ")
		.append("LEFT OUTER JOIN o.tipoAgendamento tipoAgendamento ")
		.append("LEFT OUTER JOIN o.pagador pagador ")
		.append("LEFT OUTER JOIN o.formaAgendamento formaAgendamento ")
		.append("LEFT OUTER JOIN o.unidadeFuncional unidadeFuncional ")
		.append("WHERE o.seq = :entityId ");
	    
		return hql.toString();
	}

	private Integer getGradeSeq() {
		return gradeSeq;
	}

	public void setGradeSeq(Integer seq) {
		this.gradeSeq = seq;
	}

	public AacGradeAgendamenConsultas materialize(List<Object[]> camposLst) {
		AacGradeAgendamenConsultas entityReturn = null;
		
		if (camposLst != null && camposLst.size() > 0) {
			entityReturn = new AacGradeAgendamenConsultas();
			Object[] campos = camposLst.get(0);
			
			entityReturn.setSeq((Integer)campos[0]);
			entityReturn.setAacUnidFuncionalSala((AacUnidFuncionalSalas)campos[1]);
			entityReturn.setCriadoEm((Date) campos[2]);
			entityReturn.setProgramada((Boolean) campos[3]);
			entityReturn.setConvenioSus((Integer) campos[4]);
			entityReturn.setEnviaSamis((Boolean) campos[5]);
			entityReturn.setEmiteTicket((Boolean) campos[6]);
			entityReturn.setEmiteBa((Boolean) campos[7]);
			entityReturn.setProcedimento((Boolean) campos[8]);
			entityReturn.setEquipe((AghEquipes) campos[9]);
			entityReturn.setEspecialidade((AghEspecialidades) campos[10]);
			entityReturn.setServidor((RapServidores) campos[11]);
			entityReturn.setServidorAlterado((RapServidores) campos[12]);
			entityReturn.setProfEspecialidade((AghProfEspecialidades) campos[13]);
			entityReturn.setProfServidor((RapServidores) campos[14]);
			entityReturn.setAlteradoEm((Date) campos[15]);
			entityReturn.setDtUltimaGeracao((Date) campos[16]);
			entityReturn.setExigeProntuario((Boolean) campos[17]);
			entityReturn.setIndAvisaConsultaTurno((Boolean) campos[18]);
			entityReturn.setTempoAtendAnterior((Integer) campos[19]);
			entityReturn.setProjetoPesquisa((AelProjetoPesquisas) campos[20]);
			entityReturn.setIndRefCompleta((String) campos[21]);
			entityReturn.setCondicaoAtendimento((AacCondicaoAtendimento) campos[22]);
			entityReturn.setTipoAgendamento((AacTipoAgendamento) campos[23]);
			entityReturn.setPagador((AacPagador) campos[24]);
			entityReturn.setFormaAgendamento((AacFormaAgendamento) campos[25]);
			entityReturn.setUnidadeFuncional((AghUnidadesFuncionais) campos[26]);
			entityReturn.setUslSala((Short) campos[27]);
			entityReturn.setPreSerMatricula((Integer) campos[28]);
			entityReturn.setPreSerVinCodigo((Short) campos[29]);
			entityReturn.setIndSituacao((DominioSituacao) campos[30]);
		}
		
		return entityReturn;
	}

}
