package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioCalculoPermanencia;
import br.gov.mec.aghu.dominio.DominioRepasse;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoMovimentoConta;
import br.gov.mec.aghu.dominio.DominioTipoValorConta;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigCalculoAtdPaciente;
import br.gov.mec.aghu.model.SigCalculoAtdPermanencia;
import br.gov.mec.aghu.model.SigCalculoObjetoCusto;
import br.gov.mec.aghu.model.SigMvtoContaMensal;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.model.SigProcessamentoCusto;
import br.gov.mec.aghu.sig.custos.vo.MedicamentosInternacaoVO;

/**
 * @author rogeriovieira
 */
public class SigCalculoAtdPermanenciaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCalculoAtdPermanencia> {

	private static final String CPP = "cpp.";
	private static final long serialVersionUID = 5459210627790250315L;
	
	
	public void removerPorProcessamento(Integer idProcessamento) {
		
		StringBuilder sql = new StringBuilder(100);
		sql.append(" DELETE ").append(SigCalculoAtdPermanencia.class.getSimpleName().toString()).append(" ca ");
		sql.append(" WHERE ca.").append(SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString()).append('.').append(SigCalculoAtdPaciente.Fields.SEQ.toString());
		sql.append(" IN ( ");
			sql.append(" SELECT c.").append(SigCalculoAtdPaciente.Fields.SEQ.toString());
			sql.append(" FROM ").append( SigCalculoAtdPaciente.class.getSimpleName()).append(" c ");
			sql.append(" WHERE c.").append(SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString()).append('.').append(SigProcessamentoCusto.Fields.SEQ.toString()).append(" = :pSeq");
		sql.append(" ) ");
		javax.persistence.Query query = this.createQuery(sql.toString());
		query.setParameter("pSeq", idProcessamento);
		query.executeUpdate();
	}
	
	/**
	 * Buscar uma permanencia por um centro de custo (unidade funcional) e um atendimento
	 * 
	 * @author rogeriovieira
	 * @param atendimento atendimento do paciente
	 * @param centroCusto centro de custo da unidade funcional do atendimento
	 * @return {@link SigCalculoAtdPermanencia}
	 */
	public SigCalculoAtdPermanencia buscarPermanenciaPorCentroCusto (AghAtendimentos atendimento, FccCentroCustos centroCusto, SigProcessamentoCusto processamentoCusto){
		return this.buscarPermanencia(atendimento, processamentoCusto, centroCusto, null, null);
	}
	
	/**
	 * Busca uma permanencia por uma especialidade
	 * 
	 * @author rogeriovieira
	 * @param atendimento atendimento do paciente
	 * @param especialidade especialida utilizada no atendimento
	 * @return {@link SigCalculoAtdPermanencia}
	 */
	public SigCalculoAtdPermanencia buscarPermanenciaPorEspecialidade(AghAtendimentos atendimento, AghEspecialidades especialidade, SigProcessamentoCusto processamentoCusto){
		return this.buscarPermanencia(atendimento, processamentoCusto, null, especialidade, null);
	}
	
	/**
	 * Busca uma permanencia por uma equipe
	 * 
	 * @author rogeriovieira
	 * @param atendimento atendimento do paciente
	 * @param responsavel pessoa que representa a equipe responsável pelo atendimento
	 * @return {@link SigCalculoAtdPermanencia}
	 */
	public SigCalculoAtdPermanencia buscarPermanenciaPorEquipe (AghAtendimentos atendimento, RapServidores responsavel, SigProcessamentoCusto processamentoCusto){
		return this.buscarPermanencia(atendimento, processamentoCusto, null, null, responsavel);
	}
	
	/**
	 * Busca a permanência de acordo com a unidade de internação, a especialidade e o responsável. 
	 * Pelo menos um dos parâmetros, fora o atendimento, deverá ser informado.
	 * 
	 * @author rogeriovieira
	 * @param atendimento atendimento do paciente
	 * @param centroCusto centro de custo da unidade funcional do atendimento
	 * @param especialidade  especialida utilizada no atendimento
	 * @param responsavel pessoa que representa a equipe responsável pelo atendimento
	 * @return {@link SigCalculoAtdPermanencia}
	 */
	private SigCalculoAtdPermanencia buscarPermanencia(AghAtendimentos atendimento, SigProcessamentoCusto processamentoCusto, FccCentroCustos centroCusto, AghEspecialidades especialidade, RapServidores responsavel){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPermanencia.class, "cpp");
		criteria.createAlias(CPP + SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE.toString(), "cac");
		criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.ATENDIMENTO.toString(), atendimento));
		criteria.add(Restrictions.eq("cac." + SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO.toString(), processamentoCusto));
		
		if(centroCusto != null){
			criteria.add(Restrictions.eq(CPP + SigCalculoAtdPermanencia.Fields.CENTRO_CUSTO, centroCusto));
		}
		else if(especialidade != null){
			criteria.add(Restrictions.eq(CPP + SigCalculoAtdPermanencia.Fields.ESPECIALIDADE.toString(), especialidade));
		}
		else if(responsavel != null){
			criteria.add(Restrictions.eq(CPP + SigCalculoAtdPermanencia.Fields.RESPONSAVEL.toString(), responsavel));
		}
		
		List<SigCalculoAtdPermanencia> retorno = executeCriteria(criteria, 0, 1, null, false);
		
		if(retorno != null && !retorno.isEmpty()){
			return retorno.get(0);
		}
		return null;
	}
	
	/**
	 * Busca todos os medicamentosde iternação de um atendimento que estajam dentro de um intervalo de tempo.
	 * 
	 * @author rmalvezzi
	 * @param atdSeq				Seq da Atividade
	 * @param dtInicioProcessamento Data do inicio do processamento
	 * @param dtFimProcessamento	Data do fim do processamento
	 * @return						ScrollableResults com a lista de medicamentos
	 */
	public List<MedicamentosInternacaoVO> buscarMedicamentosInternacao(Integer atdSeq, Date dtInicioProcessamento, Date dtFimProcessamento) {
		StringBuilder sql = new StringBuilder(721);

		sql.append(" SELECT ")
		.append("phi." ).append( FatProcedHospInternos.Fields.SEQ.toString()).append(" as phiSeq")
		.append(", pmd." ).append( MpmPrescricaoMdto.Fields.VIA_ADMINISTRACAO.toString() ).append('.' ).append( AfaViaAdministracao.Fields.SIGLA.toString()).append(" as vadSigla")	
		.append(", dsm." ).append( AfaDispensacaoMdtos.Fields.CRIADO_EM.toString()).append(" as criadoEm")
		.append(", pmd." ).append( MpmPrescricaoMdto.Fields.TIPO_FREQ_APZ_SEQ.toString()).append(" as tfdSeq")
		.append(", pmd." ).append( MpmPrescricaoMdto.Fields.FREQUENCIA.toString()).append(" as frequecia")
		//Extract não existe no ORACLE, a difirença das datas deve ser calculada no java
		//.append(", extract(minute from pme." ).append( MpmPrescricaoMedica.Fields.DTHR_FIM.toString() ).append( " - " ).append( "pme." ).append( MpmPrescricaoMedica.Fields.DTHR_INICIO.toString() ).append( ")")
		.append(", pme." ).append( MpmPrescricaoMedica.Fields.DTHR_INICIO.toString()).append(" as dataHoraInicio")
		.append(", pme." ).append( MpmPrescricaoMedica.Fields.DTHR_FIM.toString()).append(" as dataHoraFim")
		.append(", sum(coalesce(dsm." ).append( AfaDispensacaoMdtos.Fields.QTDE_SOLICITADA.toString() ).append( ", 0))").append(" as qtdeSolicitada")
		.append(", sum(case when dsm." ).append( AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString() ).append( " is null then 0 else (coalesce(dsm.") 
		.append(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString() ).append( ",0) - coalesce(dsm." ).append( AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString() ).append( ",0)) end)").append(" as qtdeDispensada")
		
		.append(" FROM ")
		.append(AfaDispensacaoMdtos.class.getSimpleName() ).append( " dsm, ")
		.append(MpmPrescricaoMedica.class.getSimpleName() ).append( " pme, ")
		.append(MpmPrescricaoMdto.class.getSimpleName() ).append( " pmd, ")
		.append(FatProcedHospInternos.class.getSimpleName() ).append( " phi, ")
		.append(AfaMedicamento.class.getSimpleName() ).append( " med, ")
		.append(AfaTipoUsoMdto.class.getSimpleName() ).append( " tum ")

		.append(" WHERE ")
		.append("pme." ).append( MpmPrescricaoMedica.Fields.ATD_SEQ.toString() ).append( " = :atdSeq")
		.append(" AND pme." ).append( MpmPrescricaoMedica.Fields.DT_REFERENCIA.toString() ).append( " between :dtInicio and :dtFim")
		.append(" AND pme." ).append( MpmPrescricaoMedica.Fields.SEQ.toString() ).append( " = dsm." ).append( AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_SEQ.toString())
		.append(" AND pme." ).append( MpmPrescricaoMedica.Fields.ATD_SEQ.toString() ).append( " = dsm." ).append( AfaDispensacaoMdtos.Fields.PRESCRICAO_MEDICA_ATD_SEQ.toString())
		.append(" AND pmd." ).append( MpmPrescricaoMdto.Fields.ATD_SEQ.toString() ).append( " = dsm." ).append( AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO_PMD_ATD_SEQ.toString())
		.append(" AND pmd." ).append( MpmPrescricaoMdto.Fields.SEQ.toString() ).append( " = dsm." ).append( AfaDispensacaoMdtos.Fields.ITEM_PRESCRICAO_MEDICAMENTO_PMD_SEQ.toString())
		.append(" AND dsm." ).append( AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString() ).append( " = phi." ).append( FatProcedHospInternos.Fields.MAT_CODIGO.toString())

		.append(" AND dsm." ).append( AfaDispensacaoMdtos.Fields.MED_MAT_CODIGO.toString() ).append( " = med." ).append( AfaMedicamento.Fields.MAT_CODIGO.toString())
		.append(" AND med." ).append( AfaMedicamento.Fields.TUM_SIGLA.toString() ).append( " = tum." ).append( AfaTipoUsoMdto.Fields.SIGLA.toString())		
		.append(" AND tum." ).append( AfaTipoUsoMdto.Fields.IND_QUIMIOTERAPICO.toString() ).append( " = 'N'")
		
		.append(" GROUP BY ")
		.append("phi." ).append( FatProcedHospInternos.Fields.SEQ.toString())
		.append(", pmd." ).append( MpmPrescricaoMdto.Fields.VIA_ADMINISTRACAO.toString() ).append('.' ).append( AfaViaAdministracao.Fields.SIGLA.toString())
		.append(", dsm." ).append( AfaDispensacaoMdtos.Fields.CRIADO_EM.toString())
		.append(", pmd." ).append( MpmPrescricaoMdto.Fields.TIPO_FREQ_APZ_SEQ.toString())
		.append(", pmd." ).append( MpmPrescricaoMdto.Fields.FREQUENCIA.toString())
		.append(", pme." ).append( MpmPrescricaoMedica.Fields.DTHR_FIM.toString())
		.append(", pme." ).append( MpmPrescricaoMedica.Fields.DTHR_INICIO.toString())
		
		.append(" HAVING ")
		.append("sum(case when dsm." ).append( AfaDispensacaoMdtos.Fields.DTHR_DISPENSACAO.toString() ).append( " is null then 0 else (coalesce(dsm.") 
		.append(AfaDispensacaoMdtos.Fields.QTDE_DISPENSADA.toString() ).append( ",0) - coalesce(dsm." ).append( AfaDispensacaoMdtos.Fields.QTDE_ESTORNADA.toString() ).append( ",0)) end) > 0")
		
		.append(" ORDER BY ")
		.append("dsm." ).append( AfaDispensacaoMdtos.Fields.CRIADO_EM.toString()) //Consulta foi ordenada pela data de criação (criado_em) para otimizar o processamento
		.append(", phi." ).append( FatProcedHospInternos.Fields.SEQ.toString())
		.append(", pmd." ).append( MpmPrescricaoMdto.Fields.VIA_ADMINISTRACAO.toString() ).append('.' ).append( AfaViaAdministracao.Fields.SIGLA.toString())
		.append(", pmd." ).append( MpmPrescricaoMdto.Fields.TIPO_FREQ_APZ_SEQ.toString())
		.append(", pmd." ).append( MpmPrescricaoMdto.Fields.FREQUENCIA.toString())
		.append(", pme." ).append( MpmPrescricaoMedica.Fields.DTHR_FIM.toString())
		.append(", pme." ).append( MpmPrescricaoMedica.Fields.DTHR_INICIO.toString());
		
		Query createQuery = this.createHibernateQuery(sql.toString());
		createQuery.setParameter("atdSeq", atdSeq);
		createQuery.setParameter("dtInicio", dtInicioProcessamento);
		createQuery.setParameter("dtFim", dtFimProcessamento);
		
		List<MedicamentosInternacaoVO> retorno = new ArrayList<MedicamentosInternacaoVO>();
		List<Object[]> lista = createQuery.list();
		
		if(lista != null){
			for(Object[] linha : lista){
				retorno.add(MedicamentosInternacaoVO.create(linha));
			}
		}
		return retorno;
	}
	
	public List<SigCalculoObjetoCusto> buscarCalculosObjetoCustoParaPacientePorCompetencia(SigProcessamentoCusto processamentoCusto){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoObjetoCusto.class, "cbj");
		criteria.createAlias("cbj."+SigCalculoObjetoCusto.Fields.OBJETO_CUSTO_VERSAO, "ocv");
		criteria.createAlias("ocv."+SigObjetoCustoVersoes.Fields.OBJETO_CUSTO, "obj");
		criteria.createAlias("cbj."+SigCalculoObjetoCusto.Fields.CENTRO_CUSTO, "cct");
		
		criteria.add(Restrictions.eq("cbj."+SigCalculoObjetoCusto.Fields.PROCESSAMENTO_CUSTOS, processamentoCusto));
		criteria.add(Restrictions.eq("ocv."+SigObjetoCustoVersoes.Fields.IND_SITUACAO, DominioSituacaoVersoesCustos.A));
		criteria.add(Restrictions.eq("ocv."+SigObjetoCustoVersoes.Fields.IND_REPASSE, DominioRepasse.P));
		
		return executeCriteria(criteria);
	}
	
	public List<SigCalculoAtdPermanencia> buscarPermanenciaCentroCusto(SigProcessamentoCusto processamentoCusto){
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCalculoAtdPermanencia.class, "cpp");
		criteria.createAlias("cpp."+SigCalculoAtdPermanencia.Fields.CALCULO_ATD_PACIENTE, "cca");
		
		criteria.add(Restrictions.eq("cca."+SigCalculoAtdPaciente.Fields.PROCESSAMENTO_CUSTO, processamentoCusto));
		criteria.add(Restrictions.eq("cpp."+SigCalculoAtdPermanencia.Fields.TIPO, DominioCalculoPermanencia.UI));
		
		return executeCriteria(criteria);
	}
	
	public List<SigMvtoContaMensal> buscarMovimentos(List<SigCalculoObjetoCusto> listaCbj, SigProcessamentoCusto sigProcessamentoCusto){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SigMvtoContaMensal.class, "msl");
		
		criteria.add(Restrictions.in("msl."+SigMvtoContaMensal.Fields.CALCULO_OBJETO_CUSTO, listaCbj));
		
		criteria.add(Restrictions.in("msl."+SigMvtoContaMensal.Fields.TIPO_VALOR, new DominioTipoValorConta[]{
				DominioTipoValorConta.II, DominioTipoValorConta.IP, DominioTipoValorConta.IE, DominioTipoValorConta.IS
		}));
		
		criteria.add(Restrictions.eq("msl."+SigMvtoContaMensal.Fields.TIPO_MOVIMENTO, DominioTipoMovimentoConta.SIP));
		
		criteria.add(Restrictions.eq("msl."+SigMvtoContaMensal.Fields.PROCESSAMENTO_CUSTOS, sigProcessamentoCusto));
		
		return executeCriteria(criteria);
	}
}
