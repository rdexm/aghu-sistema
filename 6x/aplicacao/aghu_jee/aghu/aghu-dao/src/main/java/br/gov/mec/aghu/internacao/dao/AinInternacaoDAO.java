package br.gov.mec.aghu.internacao.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.PropertyProjection;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.DateType;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dao.SequenceID;
import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacientesAdmitidos;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPaciente;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoCompetencia;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.dominio.DominioSituacaoUnidadeFuncional;
import br.gov.mec.aghu.enums.ConselhoRegionalMedicinaEnum;
import br.gov.mec.aghu.enums.ConselhoRegionalOdontologiaEnum;
import br.gov.mec.aghu.faturamento.vo.ReInternacoesVO;
import br.gov.mec.aghu.internacao.vo.AinLeitosVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.QuartoDisponibilidadeVO;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTiposCaraterInternacao;
import br.gov.mec.aghu.model.AinTiposMovimentoLeito;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EcpControlePaciente;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;

/**
 * 
 * @author lalegre
 * 
 */
@SuppressWarnings({ "PMD.NcssTypeCount", "PMD.ExcessiveClassLength"})
public class AinInternacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AinInternacao> {

	private static final long serialVersionUID = 4807065455736366455L;
	private static final String SEPARATOR = ".";
	private static final String ALIAS_INTERNACAO = "int";
	private static final String CAMPO_INTERNACAO_PACIENTE = ALIAS_INTERNACAO + SEPARATOR + AinInternacao.Fields.PACIENTE.toString();
	private static final String CAMPO_INTERNACAO_ESPECIALIDADE = ALIAS_INTERNACAO + SEPARATOR
			+ AinInternacao.Fields.ESPECIALIDADE.toString();
	private static final String CAMPO_INTERNACAO_SERVIDORPROFESSOR = ALIAS_INTERNACAO + SEPARATOR
			+ AinInternacao.Fields.SERVIDOR_PROFESSOR.toString();
	private static final String CAMPO_INTERNACAO_LEITO = ALIAS_INTERNACAO + SEPARATOR + AinInternacao.Fields.LEITO.toString();
	private static final String CAMPO_INTERNACAO_LEITO_QUARTO = CAMPO_INTERNACAO_LEITO + SEPARATOR
			+ AinLeitos.Fields.QUARTO.toString();
	private static final String CAMPO_INTERNACAO_LEITO_QUARTO_UNIDADE = CAMPO_INTERNACAO_LEITO_QUARTO + SEPARATOR
			+ AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString();
	private static final String CAMPO_INTERNACAO_QUARTO = ALIAS_INTERNACAO + SEPARATOR + AinInternacao.Fields.QUARTO.toString();
	private static final String CAMPO_INTERNACAO_QUARTO_UNIDADE = CAMPO_INTERNACAO_QUARTO + SEPARATOR
			+ AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString();
	private static final String CAMPO_INTERNACAO_UNIDADE = ALIAS_INTERNACAO + SEPARATOR
			+ AinInternacao.Fields.UNIDADE_FUNCIONAL.toString();

	/**
	 * Retorna data de internação
	 * 
	 * @param intSeq
	 * @return
	 */
	public Date obterDataInternacao(final Integer intSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), intSeq));
		criteria.setProjection(Projections.property(AinInternacao.Fields.DT_INTERNACAO.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * ORADB CURSOR cur_int
	 * 
	 * @param intSeq
	 * @return
	 */
	public List executarCursorInt(final Integer intSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), intSeq));

		final ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AinInternacao.Fields.CSP_SEQ.toString()));
		p.add(Projections.property(AinInternacao.Fields.CSP_CNV_CODIGO.toString()));
		p.add(Projections.property(AinInternacao.Fields.DT_INTERNACAO.toString()));
		p.add(Projections.property(AinInternacao.Fields.TAM_CODIGO.toString()));
		criteria.setProjection(p);

		return this.executeCriteria(criteria);
	}

	/**
	 * ORADB CURSOR c_int
	 * 
	 * @param intSeq
	 * @return
	 */
	public List<Date> executarCursorInt2(final Integer intSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinMovimentosInternacao.class);
		criteria.add(Restrictions.eq(AinMovimentosInternacao.Fields.INTERNACAO_SEQ.toString(), intSeq));

		final ProjectionList p = Projections.projectionList();
		p.add(Projections.property(AinMovimentosInternacao.Fields.DATA_HORA_LANCAMENTO.toString()));
		criteria.setProjection(p);

		criteria.addOrder(Order.desc(AinMovimentosInternacao.Fields.CRIADO_EM.toString()));

		return this.executeCriteria(criteria);
	}

	/**
	 * 
	 * @param pacCodigo
	 * @param dtConsulta
	 * @param dataLimite
	 * @param pgdSeq
	 * @return
	 */
	public List<AinInternacao> pesquisarInternacaoIndependenteEspecialidade(final Integer pacCodigo, final Date dtConsulta, final Date dataLimite,
			final Short pgdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		if (pgdSeq != 1) {
			criteria.createAlias(AinInternacao.Fields.CONVENIO.toString(), AinInternacao.Fields.CONVENIO.toString());
			criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());
			criteria.add(Restrictions.eq(AinInternacao.Fields.PAC_CODIGO.toString(), pacCodigo));
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.gt(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(), dataLimite),
							Restrictions.le(AinInternacao.Fields.DT_INTERNACAO.toString(), dtConsulta)),
					Restrictions.isNull(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString())));
			criteria.addOrder(Order.desc(AinInternacao.Fields.SEQ.toString()));
		} else if (pgdSeq == 1) {
			criteria.createAlias(AinInternacao.Fields.CONVENIO.toString(), AinInternacao.Fields.CONVENIO.toString());
			criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());
			criteria.createAlias(AinInternacao.Fields.CONVENIO.toString() + "." + FatConvenioSaude.Fields.PAGADOR.toString(),
					AinInternacao.Fields.CONVENIO.toString() + "." + FatConvenioSaude.Fields.PAGADOR.toString());
			criteria.add(Restrictions.eq(AinInternacao.Fields.PAC_CODIGO.toString(), pacCodigo));
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.gt(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(), dataLimite),
							Restrictions.le(AinInternacao.Fields.DT_INTERNACAO.toString(), dtConsulta)),
					Restrictions.isNull(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString())));
			criteria.add(Restrictions.eq(AinInternacao.Fields.CONVENIO.toString() + "." + FatConvenioSaude.Fields.PAGADOR.toString()
					+ "." + AacPagador.Fields.SEQ.toString(), pgdSeq));
			criteria.addOrder(Order.desc(AinInternacao.Fields.SEQ.toString()));
		}
		return executeCriteria(criteria);
	}

	public List<AinInternacao> pesquisarInternacaoOutrasEspecialidades(final Integer pacCodigo, final Date dtConsulta, final Short espSeq,
			final Date dataLimite, final Short pgdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class, "int");
		if (pgdSeq != 1) {
			criteria.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(), "aliasEspecialidade");
			criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), "aliasPaciente");
			criteria.createAlias(AinInternacao.Fields.CONVENIO.toString(), "aliasConvenio");
			criteria.add(Restrictions.eq("aliasPaciente" + "." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.gt(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(), dataLimite),
							Restrictions.le(AinInternacao.Fields.DT_INTERNACAO.toString(), dtConsulta)),
					Restrictions.isNull(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString())));

			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.isNotNull("aliasEspecialidade" + "."
							+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()), Restrictions.ne("aliasEspecialidade"
							+ "." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString(), espSeq)),
					Restrictions.and(
							Restrictions.isNull("aliasEspecialidade" + "."
									+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()),
							Restrictions.ne("aliasEspecialidade" + "." + AghEspecialidades.Fields.SEQ.toString(), espSeq))));
			criteria.addOrder(Order.desc(AinInternacao.Fields.SEQ.toString()));

		} else if (pgdSeq == 1) {
			criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), "aliasPaciente");
			criteria.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(), "aliasEspecialidade");
			criteria.createAlias(AinInternacao.Fields.CONVENIO.toString(), "aliasConvenio");
			criteria.createAlias("aliasConvenio" + "." + FatConvenioSaude.Fields.PAGADOR.toString(), "aliasPagador");

			criteria.add(Restrictions.eq("aliasPaciente" + "." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.gt(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(), dataLimite),
							Restrictions.le(AinInternacao.Fields.DT_INTERNACAO.toString(), dtConsulta)),
					Restrictions.isNull(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString())));
			criteria.add(Restrictions.eq("aliasPagador" + "." + AacPagador.Fields.SEQ.toString(), pgdSeq));

			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.isNotNull("aliasEspecialidade" + "."
							+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()), Restrictions.ne("aliasEspecialidade"
							+ "." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString(), espSeq)),
					Restrictions.and(
							Restrictions.isNull("aliasEspecialidade" + "."
									+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()),
							Restrictions.ne("aliasEspecialidade" + "." + AghEspecialidades.Fields.SEQ.toString(), espSeq))));

			criteria.addOrder(Order.desc(AinInternacao.Fields.SEQ.toString()));
		}
		return executeCriteria(criteria);
	}

	public List<AinInternacao> pesquisarInternacaoEspecialidadeDiferente(final Integer pacCodigo, final Date dtConsulta, final Short espSeq,
			final Date dataLimite, final Short pgdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		if (pgdSeq != 1) {
			criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), "aliasPaciente");
			criteria.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(), "aliasEspecialidade");
			criteria.createAlias(AinInternacao.Fields.CONVENIO.toString(), "aliasConvenio");
			criteria.add(Restrictions.eq("aliasPaciente" + "." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.gt(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(), dataLimite),
							Restrictions.le(AinInternacao.Fields.DT_INTERNACAO.toString(), dtConsulta)),
					Restrictions.isNull(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString())));
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.isNotNull("aliasEspecialidade" + "."
							+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()), Restrictions.ne("aliasEspecialidade"
							+ "." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString(), espSeq)),
					Restrictions.and(
							Restrictions.isNull("aliasEspecialidade" + "."
									+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()),
							Restrictions.ne("aliasEspecialidade" + "." + AghEspecialidades.Fields.SEQ.toString(), espSeq))));
			criteria.addOrder(Order.desc(AinInternacao.Fields.SEQ.toString()));
		} else if (pgdSeq == 1) {
			criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), "aliasPaciente");
			criteria.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(), "aliasEspecialidade");
			criteria.createAlias(AinInternacao.Fields.CONVENIO.toString(), "aliasConvenio");
			criteria.createAlias("aliasConvenio" + "." + FatConvenioSaude.Fields.PAGADOR.toString(), "aliasPagador");
			criteria.add(Restrictions.eq("aliasPaciente" + "." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.gt(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(), dataLimite),
							Restrictions.le(AinInternacao.Fields.DT_INTERNACAO.toString(), dtConsulta)),
					Restrictions.isNull(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString())));
			criteria.add(Restrictions.eq("aliasPagador" + "." + AacPagador.Fields.SEQ.toString(), pgdSeq));
			criteria.add(Restrictions.or(
					Restrictions.and(Restrictions.isNotNull("aliasEspecialidade" + "."
							+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()), Restrictions.ne("aliasEspecialidade"
							+ "." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString(), espSeq)),
					Restrictions.and(
							Restrictions.isNull("aliasEspecialidade" + "."
									+ AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()),
							Restrictions.ne("aliasEspecialidade" + "." + AghEspecialidades.Fields.SEQ.toString(), espSeq))));
			criteria.addOrder(Order.desc(AinInternacao.Fields.SEQ.toString()));
		}
		return executeCriteria(criteria);
	}

	/**
	 * Busca leito (lto_lto_id) de uma conta hospitalar
	 * 
	 * @param contaHospitalar
	 */
	public String obterLeitoContaHospitalar(final Integer cthSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class, "it");

		criteria.setProjection(Projections.property(AinInternacao.Fields.LEITO_ID.toString()));
		criteria.createAlias(AinInternacao.Fields.FAT_CONTAS_INTERNACAO.toString(), "fci");
		criteria.add(Restrictions.eq("fci." + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString(), cthSeq));
		criteria.addOrder(Order.asc("fci." + FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES_SEQ.toString()));

		final List<String> results = executeCriteria(criteria);

		if (results != null && !results.isEmpty()) {
			return results.get(0);
		} else {
			return null;
		}
	}

	/**
	 * Retorna lista de reinternações de pacientes para o relatório
	 * FATR_REINTERNACOES
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<ReInternacoesVO> obterReInternacoesVO() {

		final StringBuffer sql = new StringBuffer(1950);

		sql.append("select ")
				.append(" int.pac_codigo 	 	 as paccodigo ")
				.append(" ,pac.prontuario 		 as prontuario ")
				.append(" ,int.seq 				 as intseq ")
				.append(" ,cth.seq 			 	 as cthseq ")
				.append(" ,dt_int_administrativa  as datainternacaoadministrativa ")
				.append(" ,dt_alta_administrativa as dtaltaadministrativa ")
				.append(" ,cth.ind_situacao		 as indsituacao ")

				.append(" from ")

				.append("  agh.aip_pacientes 		 	  pac ")
				.append(" ,agh.ain_internacoes 		  	  int ")
				.append(" ,agh.fat_contas_internacao   	  coi ")
				.append(" ,agh.fat_contas_hospitalares     cth ")
				.append(" ,agh.fat_documento_cobranca_aihs dci ")
				.append(" ,agh.fat_competencias 			  cpe ")

				.append(" where ")

				.append("     cpe.ind_situacao 	 = :prmdominiosituacaocompetencia ")
				.append(" and cpe.modulo  		 = :prm_modulo")

				.append(" and dci.cpe_modulo	  	 = cpe.modulo ")
				.append(" and dci.cpe_mes	  	 = cpe.mes ")
				.append(" and dci.cpe_ano	  	 = cpe.ano ")
				.append(" and cth.dci_codigo_dcih = dci.codigo_dcih ")
				.append(" and coi.cth_seq 		 = cth.seq")
				.append(" and int.seq 			 = coi.int_seq ")
				.append(" and trunc(cth.csp_cnv_codigo) = 1")
				.append(" and trunc(cth.csp_seq)  = 1 ")
				.append(" and cth.cth_seq is null ")
				.append(" and pac.codigo 		 = int.pac_codigo ")
				.append(" and cth.seq not in (select cthDes.cth_seq from agh.fat_contas_hospitalares cthDes where cthDes.ind_situacao = 'F' and cthDes.cth_seq is not null) ")

				.append(" union ")

				.append("select ")
				.append(" int.pac_codigo 	 	 as paccodigo ")
				.append(" ,pac.prontuario 		 as prontuario ")
				.append(" ,int.seq 				 as intseq ")
				.append(" ,cth.seq 			 	 as cthseq ")
				.append(" ,dt_int_administrativa  as datainternacaoadministrativa ")
				.append(" ,dt_alta_administrativa as dtaltaadministrativa ")
				.append(" ,cth.ind_situacao		 as indsituacao ")

				.append(" from ")

				.append("  agh.aip_pacientes			  pac ")
				.append(" ,agh.ain_internacoes 		  int ")
				.append(" ,agh.fat_contas_internacao   coi ")
				.append(" ,agh.fat_contas_hospitalares cth ")

				.append(" where ")

				.append("     cth.ind_situacao in (:prmcthindsituacao) ")
				.append(" and coi.cth_seq 	= cth.seq ")
				.append(" and int.seq 		= coi.int_seq ")
				.append(" and cth.cth_seq is null ")
				.append(" and trunc(cth.csp_cnv_codigo) = 1 ")
				.append(" and trunc(cth.csp_seq) 	   = 1 ")
				.append(" and pac.codigo 	= int.pac_codigo ")
				.append(" and cth.seq not in (select cthDes.cth_seq from agh.fat_contas_hospitalares cthDes where cthDes.ind_situacao = 'F' and cthDes.cth_seq is not null) ")

				.append(" order by 1 ");

		final SQLQuery q = createSQLQuery(sql.toString());

		q.setString("prmdominiosituacaocompetencia", DominioSituacaoCompetencia.A.toString());
		q.setString("prm_modulo", DominioModuloCompetencia.INT.toString());
		q.setParameterList("prmcthindsituacao", new String[] { DominioSituacaoConta.A.toString(), DominioSituacaoConta.F.toString() });

		final List<ReInternacoesVO> pacientes = q.addScalar("paccodigo", IntegerType.INSTANCE).addScalar("prontuario", IntegerType.INSTANCE)
				.addScalar("intseq", IntegerType.INSTANCE).addScalar("cthseq", IntegerType.INSTANCE)
				.addScalar("datainternacaoadministrativa", DateType.INSTANCE).addScalar("dtaltaadministrativa", DateType.INSTANCE)
				.addScalar("indsituacao", StringType.INSTANCE).setResultTransformer(Transformers.aliasToBean(ReInternacoesVO.class))
				.list();

		final StringBuffer sqlPacientesASeremExibidos = new StringBuffer(1200);

		sqlPacientesASeremExibidos.append(" select paccodigo from ( ").append(" select int.pac_codigo     as paccodigo ")
				.append("   from  agh.aip_pacientes 		  	   pac").append("		  ,agh.ain_internacoes 		  	   int")
				.append("	      ,agh.fat_contas_internacao   	   coi").append("		  ,agh.fat_contas_hospitalares	   cth")
				.append("		  ,agh.fat_documento_cobranca_aihs dci").append("         ,agh.fat_competencias cpe ")

				.append("where cpe.ind_situacao = 'A' ")

				.append(" and cpe.modulo = 'INT' ").append(" and dci.cpe_modulo= cpe.modulo  ").append(" and dci.cpe_mes= cpe.mes ")
				.append(" and dci.cpe_ano= cpe.ano   ").append(" and cth.dci_codigo_dcih = dci.codigo_dcih  ")
				.append(" and coi.cth_seq = cth.seq ").append(" and int.seq = coi.int_seq   ")
				.append(" and trunc(cth.csp_cnv_codigo) = 1 and  trunc(cth.csp_seq) =1 ").append(" and cth.cth_seq is null  ")
				.append(" and pac.codigo = int.pac_codigo ")

				.append(" union all ")

				.append(" select int.pac_codigo     as paccodigo  ")

				.append(" from ").append(" 		 agh.aip_pacientes 			 pac ").append("  		,agh.ain_internacoes 		 int ")
				.append(" 		,agh.fat_contas_internacao   coi ").append("  		,agh.fat_contas_hospitalares cth ")
				.append("  where   cth.ind_situacao in ('A','F')  ").append("  and coi.cth_seq = cth.seq ")
				.append("  and int.seq = coi.int_seq    ").append("  and cth.cth_seq is null  ")
				.append("  and trunc(cth.csp_cnv_codigo) = 1 and  trunc(cth.csp_seq) =1 ")
				.append("  and pac.codigo = int.pac_codigo ").append("  order by 1 ")

				.append(") SUBCONSULTA ").append("  group by paccodigo ").append("  having count(*) > 1 ");

		final SQLQuery qPacientesASeremExibidos = createSQLQuery(sqlPacientesASeremExibidos.toString());
		final List<Integer> pacientesIds = qPacientesASeremExibidos.addScalar("paccodigo", IntegerType.INSTANCE).list();

		final List<ReInternacoesVO> result = new ArrayList<ReInternacoesVO>();

		// Itera os Ids dos pacientes buscando-os na lista global
		for (final Integer paciente : pacientesIds) {
			int index = Collections.binarySearch(pacientes, paciente);

			if (index > 0) {

				// O paciente pode estar presente mais de uma vez na lista
				// global
				do {

					result.add(pacientes.get(index));
					pacientes.remove(index);
					index = Collections.binarySearch(pacientes, paciente);

				} while (index > 0);
			}
		}

		return result;
	}

	public AinInternacao obrterInternacaoPorPacienteInternado(final Integer pacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class,"int");
		criteria.createAlias("int." + AinInternacao.Fields.LEITO.toString(), "lei", JoinType.LEFT_OUTER_JOIN);
		// criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(),
		// AinInternacao.Fields.PACIENTE.toString());
		criteria.add(Restrictions.eq("int."+AinInternacao.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("int."+AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), Boolean.TRUE));

		return (AinInternacao) executeCriteriaUniqueResult(criteria);
	}
	
	public Integer obterIntSeqPorPacienteInternado(final Integer pacCodigo, final Integer intSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property(AinInternacao.Fields.SEQ.toString())));
		criteria.add(Restrictions.eq(AinInternacao.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.ne(AinInternacao.Fields.SEQ.toString(), intSeq));

		return (Integer) executeCriteriaUniqueResult(criteria);
	}

	public List<Object[]> pesquisarDatas(final Integer prontuario, final Date dataInternacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);

		if (prontuario != null) {
			criteria.createAlias("paciente", "paciente");
			criteria.add(Restrictions.eq(AinInternacao.Fields.PRONTUARIO_PACIENTE.toString(), prontuario));
		}

		if (dataInternacao != null) {
			final Calendar auxCalInicial = Calendar.getInstance();
			final Calendar auxCalFinal = Calendar.getInstance();

			auxCalInicial.setTime(dataInternacao);
			auxCalInicial.set(Calendar.HOUR_OF_DAY, 0);
			auxCalInicial.set(Calendar.MINUTE, 0);
			auxCalInicial.set(Calendar.SECOND, 0);
			auxCalInicial.set(Calendar.MILLISECOND, 0);

			auxCalFinal.setTime(dataInternacao);
			auxCalFinal.set(Calendar.HOUR_OF_DAY, 23);
			auxCalFinal.set(Calendar.MINUTE, 59);
			auxCalFinal.set(Calendar.SECOND, 59);
			auxCalFinal.set(Calendar.MILLISECOND, 999);

			criteria.add(Restrictions.between(AinInternacao.Fields.DT_INTERNACAO.toString(), auxCalInicial.getTime(),
					auxCalFinal.getTime()));
		}

		criteria.addOrder(Order.asc(AinInternacao.Fields.PAC_CODIGO.toString()));

		criteria.addOrder(Order.desc(AinInternacao.Fields.DT_INTERNACAO.toString()));
		criteria.createAlias("convenioSaude", "convenioSaude");
		criteria.createAlias("convenioSaudePlano", "convenioSaudePlano");

		criteria.setProjection(Projections
				.projectionList()
				.add(// 0
				Projections.property(AinInternacao.Fields.DT_INTERNACAO.toString()))
				.add(// 1
				Projections.property(AinInternacao.Fields.CSP_CNV_CODIGO.toString()))
				.add(// 2
				Projections.property(AinInternacao.Fields.CSP_SEQ.toString()))
				.add(// 3
				Projections.property(AinInternacao.Fields.CONVENIO.toString() + "." + FatConvenioSaude.Fields.DESCRICAO.toString()))
				.add(// 4
				Projections.property(AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString() + "."
						+ FatConvenioSaudePlano.Fields.DESCRICAO.toString())).add(// 5
						Projections.property(AinInternacao.Fields.SEQ.toString())));

		return executeCriteria(criteria);
	}

	/**
	 * 
	 * Busca Internacao Por Sequence
	 * 
	 * @return Internacao
	 */
	private DetachedCriteria createPesquisaInternacaoCriteria(final Integer sequence, final Integer prontuario) {
		final DetachedCriteria cri = DetachedCriteria.forClass(AinInternacao.class);

		if (prontuario != null) {
			cri.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());
			cri.add(Restrictions.eq(AinInternacao.Fields.PRONTUARIO_PACIENTE.toString(), prontuario));
		}

		if (sequence != null) {
			cri.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), sequence));
		}

		return cri;
	}

	public AinInternacao pesquisaInternacao(final Integer sequence, final Integer prontuario) {
		final DetachedCriteria criteria = createPesquisaInternacaoCriteria(sequence, prontuario);
		return (AinInternacao) executeCriteriaUniqueResult(criteria);
	}

	public Long pesquisaInternacaoCount(final Integer sequence, final Integer prontuario) {
		final DetachedCriteria criteria = createPesquisaInternacaoCriteria(sequence, prontuario);
		return executeCriteriaCount(criteria);
	}
	
	
	public boolean verificarExisteInternacaoPaciente(Integer prontuario){
		DetachedCriteria criteria = createPesquisaInternacaoCriteria(null, prontuario);
		
		return executeCriteriaExists(criteria);
	}

	public List<AinInternacao> listarInternacoes(final Integer sequence, final Integer prontuario) {
		final DetachedCriteria criteria = createPesquisaInternacaoCriteria(null, prontuario);
		return executeCriteria(criteria);
	}

	/**
	 * ORADB ainc_busca_ult_alta
	 */
	@SuppressWarnings("unchecked")
	public Date buscaUltimaAlta(final Integer internacaoSeq) {
		Date ultDataRetorno = null;
		StringBuilder hql = new StringBuilder(150);
		hql.append(" SELECT ");
		hql.append(" AIN1.paciente, AIN1.dthrInternacao ");
		hql.append(" FROM ");
		hql.append(" AinInternacao AIN1 ");
		hql.append(" WHERE ");
		hql.append(" AIN1.seq = :internacaoSeq ");

		javax.persistence.Query query = this.createQuery(hql.toString());

		query.setParameter("internacaoSeq", internacaoSeq);

		final Iterator<Object[]> list = query.getResultList().iterator();

		if (list.hasNext()) {
			final Object[] obj = list.next();

			if (obj[0] != null && obj[1] != null) {
				final AipPacientes aipPacientes = (AipPacientes) obj[0];
				final Date dthrInternacao = (Date) obj[1];

				hql = new StringBuilder();
				hql.append(" SELECT ");
				hql.append(" AIN1.dtSaidaPaciente ");
				hql.append(" FROM ");
				hql.append(" AinInternacao AIN1 ");
				hql.append(" WHERE ");
				hql.append(" AIN1.paciente  = :paciente ");
				hql.append(" AND AIN1.dthrInternacao <= :dthrInternacao ");
				hql.append(" AND AIN1.seq != :internacaoSeq ");
				hql.append(" ORDER BY ");
				hql.append(" AIN1.dtSaidaPaciente DESC ");

				query = this.createQuery(hql.toString());

				query.setParameter("paciente", aipPacientes);
				query.setParameter("dthrInternacao", dthrInternacao);
				query.setParameter("internacaoSeq", internacaoSeq);

				final List list_2 = query.getResultList();

				if (list_2 != null && list_2.size() > 0) {
					ultDataRetorno = (Date) list_2.get(0);
				}
			}
		}
		return ultDataRetorno;
	}

	public List<AinInternacao> pesquisarPacIntQrt(final List<QuartoDisponibilidadeVO> quartoList) {
		final ArrayList<DominioLocalPaciente> lista = new ArrayList<DominioLocalPaciente>();
		lista.add(DominioLocalPaciente.Q);
		lista.add(DominioLocalPaciente.L);
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq("indSaidaPac", false));
		criteria.add(Restrictions.in("indLocalPaciente", lista));
		return this.executeCriteria(criteria);
	}

	public AinInternacao obterInternacaoComLefts(Integer seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);

		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.LEITO.toString(), "LEI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.QUARTO.toString(), "QUA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.PROJETO_PESQUISA.toString(), "PPS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.CONVENIO.toString(), "CNV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString(), "CVS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(), "SPR", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.CARATER_INTERNACAO.toString(), "CRI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.ORIGEM_EVENTO.toString(), "ORI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AinInternacao.Fields.INSTITUICAO_HOSPITALAR.toString(), "INH", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), seq));
		
		return (AinInternacao) this.executeCriteria(criteria).get(0);
	}
	
	/**
	 * Cria um DetachedCriteria de acordo com os parâmetros de pesquisa
	 * 
	 * @author Stanley Araujo
	 * @param dataInicial
	 *            - Data inicial da pesquisa
	 * @param dataFinal
	 *            - Data final da pesquisa
	 * @return DetachedCriteria - critério instânciado
	 */
	private DetachedCriteria createPesquisaPacienteComPrevisaoAltaCriteria(final Date dataInicial, final Date dataFinal) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);

		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());

		criteria.createAlias(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(), AinInternacao.Fields.SERVIDOR_PROFESSOR.toString());

		criteria.createAlias(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString());

		if (dataInicial != null && dataFinal != null) {
			final Calendar auxCalInicial = Calendar.getInstance();
			final Calendar auxCalFinal = Calendar.getInstance();

			auxCalInicial.setTime(dataInicial);
			auxCalInicial.set(Calendar.HOUR_OF_DAY, 0);
			auxCalInicial.set(Calendar.MINUTE, 0);
			auxCalInicial.set(Calendar.SECOND, 0);
			auxCalInicial.set(Calendar.MILLISECOND, 0);

			auxCalFinal.setTime(dataFinal);
			auxCalFinal.set(Calendar.HOUR_OF_DAY, 23);
			auxCalFinal.set(Calendar.MINUTE, 59);
			auxCalFinal.set(Calendar.SECOND, 59);
			auxCalFinal.set(Calendar.MILLISECOND, 999);

			criteria.add(Restrictions.between(AinInternacao.Fields.DATA_PREV_ALTA.toString(), auxCalInicial.getTime(),
					auxCalFinal.getTime()));
		}

		criteria.setProjection(Projections.projectionList().add(// 0
				Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.PRONTUARIO.toString())).add(// 1
				Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.NOME.toString())).add(// 2
				Projections.property(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME.toString()))
				.add(// 3
				Projections.property(AinInternacao.Fields.LEITO.toString())).add(// 4
						Projections.property(AinInternacao.Fields.DT_INTERNACAO.toString())).add(// 5
						Projections.property(AinInternacao.Fields.DATA_PREV_ALTA.toString())).add(// 6
						Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString())).add(// 7
						Projections.property(AinInternacao.Fields.QUARTO.toString())).add(// 8
						Projections.property(AinInternacao.Fields.UNIDADE_FUNCIONAL.toString()))

		);

		return criteria;
	}

	/***
	 * Realiza a contagem de pacientes com previsão de alta em um intervalo de
	 * tempo
	 * 
	 * @author Stanley Araujo
	 * @param dataInicial
	 *            - Data inicial para contagem
	 * @param dataFinal
	 *            - Data final para a contagem
	 * @return Quantidade de pacientes com previsão de alta
	 * */
	public Long pesquisaPacientesComPrevisaoAltaCount(final Date dataInicial, final Date dataFinal) {

		final DetachedCriteria criteria = createPesquisaPacienteComPrevisaoAltaCriteria(dataInicial, dataFinal);

		return executeCriteriaCount(criteria);

	}

	/***
	 * Realiza a pesquisa de pacientes com previsão de alta em um intervalo de
	 * tempo
	 * 
	 * 
	 * @author Stanley Araujo
	 * @param firstResult
	 *            - Primeiro resultado
	 * @param maxResult
	 *            - Máximo resultado
	 * @param orderProperty
	 *            -
	 * @param asc
	 *            -
	 * @param dataInicial
	 *            - Data inicial para a pesquisa
	 * @param dataFinal
	 *            - Data final para a pesquisa
	 * @return Lista de pacientes com previsão de alta
	 * */
	public List<Object[]> pesquisaPacientesComPrevisaoAlta(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc,
			final Date dataInicial, final Date dataFinal) {

		final DetachedCriteria criteria = createPesquisaPacienteComPrevisaoAltaCriteria(dataInicial, dataFinal);

		criteria.addOrder(Order.asc(AinInternacao.Fields.DT_INTERNACAO.toString()));

		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	/***
	 * Cria um DetachedCriteria de acordo com os parâmetros de pesquisa
	 * 
	 * @author Stanley Araujo
	 * @param codigoEspecialidade
	 *            - Código(Chave) da especialidade
	 * @param origemPaciente
	 *            - Origem do Evento
	 * @param ordenacaoPesquisa
	 *            - Ordenação da pesquisa
	 * @param codigoClinica
	 *            - Código(cheve) da clínica
	 * @param codigoConvenio
	 *            - Código do convênio
	 * @param codigoPlano
	 *            - Código do plano associado ao convênio
	 * @param codigoPaciente
	 *            - Código do paciente
	 * @param dataInicial
	 *            - Data inicial para a pesquisa
	 * @param dataFinal
	 *            - Data final para a pesquisa
	 * @return DetachedCriteria - critério instânciado
	 * */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private DetachedCriteria createPesquisaPacienteAdmitidosCriteria(final AghEspecialidades codigoEspecialidade,
			final DominioOrigemPaciente origemPaciente, final DominioOrdenacaoPesquisaPacientesAdmitidos ordenacaoPesquisa, final AghClinicas codigoClinica,
			final Short codigoConvenio, final Byte codigoPlano, final Integer codigoPaciente, final Date dataInicial, final Date dataFinal) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());

		criteria.createAlias(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(), AinInternacao.Fields.SERVIDOR_PROFESSOR.toString());

		criteria.createAlias(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				RapServidores.Fields.PESSOA_FISICA.toString());

		criteria.createAlias(AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString(),
				AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString());

		criteria.createAlias(
				AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString() + "." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(),
				FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString());

		criteria.createAlias(AinInternacao.Fields.ORIGEM_EVENTO.toString(), AinInternacao.Fields.ORIGEM_EVENTO.toString());

		criteria.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(), AinInternacao.Fields.ESPECIALIDADE.toString());

		criteria.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString() + "." + AghEspecialidades.Fields.CLINICA.toString(),
				AghEspecialidades.Fields.CLINICA.toString());

		if (dataInicial != null && dataFinal != null) {
			final Calendar auxCalInicial = Calendar.getInstance();
			final Calendar auxCalFinal = Calendar.getInstance();

			auxCalInicial.setTime(dataInicial);
			auxCalInicial.set(Calendar.HOUR_OF_DAY, 0);
			auxCalInicial.set(Calendar.MINUTE, 0);
			auxCalInicial.set(Calendar.SECOND, 0);
			auxCalInicial.set(Calendar.MILLISECOND, 0);

			auxCalFinal.setTime(dataFinal);
			auxCalFinal.set(Calendar.HOUR_OF_DAY, 23);
			auxCalFinal.set(Calendar.MINUTE, 59);
			auxCalFinal.set(Calendar.SECOND, 59);
			auxCalFinal.set(Calendar.MILLISECOND, 999);

			criteria.add(Restrictions.between(AinInternacao.Fields.DT_INTERNACAO.toString(), auxCalInicial.getTime(),
					auxCalFinal.getTime()));
		}

		if (codigoEspecialidade != null) {
			criteria.add(Restrictions.eq(AinInternacao.Fields.ESP_SEQ.toString(), codigoEspecialidade.getSeq()));
		}

		if (codigoPaciente != null) {
			criteria.add(Restrictions.eq(AinInternacao.Fields.PAC_CODIGO.toString(), codigoPaciente));
		}

		if (codigoConvenio != null) {
			criteria.add(Restrictions.eq(AinInternacao.Fields.CSP_CNV_CODIGO.toString(), codigoConvenio.shortValue()));
		}
		if (codigoPlano != null) {
			criteria.add(Restrictions.eq(AinInternacao.Fields.CSP_SEQ.toString(), codigoPlano.byteValue()));
		}

		// **Instância a criteria para o Subqueries.exists*//*

		if (codigoClinica != null && codigoEspecialidade != null) {

			final DetachedCriteria criteriaEspecialidade = DetachedCriteria.forClass(AghEspecialidades.class);
			criteriaEspecialidade.setProjection(Property.forName(AghEspecialidades.Fields.SEQ.toString()));
			criteriaEspecialidade.add(Restrictions.eq(AghEspecialidades.Fields.CLINICA_CODIGO.toString(), codigoClinica.getCodigo()));
			criteriaEspecialidade.add(Restrictions.eq(AghEspecialidades.Fields.SEQ.toString(), codigoEspecialidade.getSeq()));
			criteria.add(Subqueries.exists(criteriaEspecialidade));
		}

		if (codigoEspecialidade == null && codigoClinica != null) {

			final DetachedCriteria criteriaEsp = DetachedCriteria.forClass(AghEspecialidades.class);
			criteriaEsp.createAlias(AghEspecialidades.Fields.CLINICA.toString(), AghEspecialidades.Fields.CLINICA.toString());

			criteriaEsp.add(Restrictions.eq(AghEspecialidades.Fields.CLINICA.toString() + "." + AghClinicas.Fields.CODIGO.toString(),
					codigoClinica.getCodigo()));

			criteriaEsp.setProjection(Projections.projectionList().add(Projections.property(AghEspecialidades.Fields.SEQ.toString())));

			criteria.add(Property.forName(AinInternacao.Fields.ESPECIALIDADE.toString()).in(criteriaEsp));
		}

		if (origemPaciente == DominioOrigemPaciente.A) {
			criteria.add(Restrictions.isNull(AinInternacao.Fields.INSTITUICAO_HOSPITALAR.toString()));
			criteria.add(Restrictions.isNull(AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString()));
		}

		if (origemPaciente == DominioOrigemPaciente.T) {
			criteria.add(Restrictions.isNotNull(AinInternacao.Fields.INSTITUICAO_HOSPITALAR.toString()));
		}

		if (origemPaciente == DominioOrigemPaciente.E) {
			criteria.add(Restrictions.isNotNull(AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString()));
		}

		/** Projeções */
		final ProjectionList projecao = Projections.projectionList();

		projecao.add(// 0
		Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.PRONTUARIO.toString()));
		projecao.add(// 1
		Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.NOME.toString()));
		projecao.add(// 2
		Projections.property(RapServidores.Fields.PESSOA_FISICA.toString() + "." + RapPessoasFisicas.Fields.NOME.toString()));
		projecao.add(// 3
		Projections.property(AinInternacao.Fields.LEITO.toString()));
		projecao.add(// 4
		Projections.property(AinInternacao.Fields.DT_INTERNACAO.toString()));
		projecao.add(// 5
		Projections.property(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()));
		projecao.add(// 7
		Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString() + "." + AghEspecialidades.Fields.NOME_REDUZIDO));
		projecao.add(// 8
		Projections.property(AinInternacao.Fields.QUARTO.toString()));
		projecao.add(// 9
		Projections.property(AinInternacao.Fields.UNIDADE_FUNCIONAL.toString()));

		projecao.add(// 10
		Projections.property(AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString() + "."
				+ FatConvenioSaudePlano.Fields.DESCRICAO.toString()));// 10
		projecao.add(// 11
		Projections.property(FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString() + "."
				+ FatConvenioSaude.Fields.DESCRICAO.toString()));// 11

		projecao.add(// 12
		Projections.property(AinInternacao.Fields.ORIGEM_EVENTO.toString() + "." + AghOrigemEventos.Fields.DESCRICAO.toString()));
		projecao.add(// 13
		Projections.property(AinInternacao.Fields.TIPO_ALTA_MEDICA.toString()));

		projecao.add(// 14
		Projections.property(AinInternacao.Fields.INSTITUICAO_HOSPITALAR.toString()));

		projecao.add(// 15
		Projections.property(AghEspecialidades.Fields.CLINICA.toString() + "." + AghClinicas.Fields.DESCRICAO.toString()));

		projecao.add(// 16
		Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.CODIGO.toString()));

		/* Novas projeções */
		projecao.add(// 17
		Projections.property(AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString()));
		
		projecao.add(// 18
		Projections.property(AinInternacao.Fields.SEQ.toString()));
		criteria.setProjection(projecao);
		
		projecao.add(// 19
		Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString() + "." + AghEspecialidades.Fields.NOME));
		
		return criteria;
	}

	public List<Object[]> pesquisaPacientesAdmitidos(final AghEspecialidades codigoEspecialidade, final DominioOrigemPaciente origemPaciente,
			final DominioOrdenacaoPesquisaPacientesAdmitidos ordenacaoPesquisa, final AghClinicas codigoClinica, final Short codigoConvenio,
			final Byte codigoPlano, final Date dataInicial, final Date dataFinal, final Integer codigoPaciente, final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {

		final DetachedCriteria criteria = createPesquisaPacienteAdmitidosCriteria(codigoEspecialidade, origemPaciente, ordenacaoPesquisa,
				codigoClinica, codigoConvenio, codigoPlano, codigoPaciente, dataInicial, dataFinal);

		if (ordenacaoPesquisa == DominioOrdenacaoPesquisaPacientesAdmitidos.P) { // ordenação
			// pelo nome do paciente
			criteria.addOrder(Order.asc(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.NOME.toString()));
		} else {// ordenação pelo nome reduzido da especialidade

			criteria.addOrder(Order.asc(AinInternacao.Fields.ESPECIALIDADE.toString() + "."
					+ AghEspecialidades.Fields.NOME_REDUZIDO.toString()));
		}

		if (firstResult == null && maxResult == null && orderProperty == null) {
			return executeCriteria(criteria);
		} else {
			return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
		}
	}

	public Long pesquisaPacientesAdmitidosCount(final AghEspecialidades codigoEspecialidade, final DominioOrigemPaciente origemPaciente,
			final DominioOrdenacaoPesquisaPacientesAdmitidos ordenacaoPesquisa, final AghClinicas codigoClinica, final Short codigoConveniosPlano,
			final Byte codigoPlano, final Date dataInicial, final Date dataFinal, final Integer codigoPaciente) {

		final DetachedCriteria criteria = createPesquisaPacienteAdmitidosCriteria(codigoEspecialidade, origemPaciente, ordenacaoPesquisa,
				codigoClinica, codigoConveniosPlano, codigoPlano, codigoPaciente, dataInicial, dataFinal);

		return executeCriteriaCount(criteria);
	}

	/**
	 * Método para buscar a última internação de um paciente através do código
	 * do paciente.
	 * 
	 * @param codigoPaciente
	 * @return objeto AinInternacao
	 */
	public AinInternacao obterInternacaoPacientePorCodPac(final Integer codigoPaciente) {
		AinInternacao internacao = null;
		if (codigoPaciente == null) {
			return null;
		} else {
			final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class,"int");
			criteria.createAlias("int."+AinInternacao.Fields.PACIENTE.toString(), "pac", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("int."+AinInternacao.Fields.LEITO.toString(), "lei", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("int."+AinInternacao.Fields.QUARTO.toString(), "qua", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("int."+AinInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("int."+AinInternacao.Fields.ESPECIALIDADE.toString(), "esp", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("int."+AinInternacao.Fields.PROJETO_PESQUISA.toString(), "projPesq", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("int."+AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString(), "convSaudPlan", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("int."+AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(), "servProf", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("int."+AinInternacao.Fields.CARATER_INTERNACAO.toString(), "carInt", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("int."+AinInternacao.Fields.ORIGEM_EVENTO.toString(), "oev", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("int."+AinInternacao.Fields.INSTITUICAO_HOSPITALAR.toString(), "iho", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("int."+AinInternacao.Fields.SERVIDOR_PROFESSOR_PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
			
			criteria.add(Restrictions.eq(AinInternacao.Fields.COD_PACIENTE.toString(), codigoPaciente));
			criteria.addOrder(Order.desc(AinInternacao.Fields.DT_INTERNACAO.toString()));

			final List<AinInternacao> lista = executeCriteria(criteria);
			if (lista != null && lista.size() > 0) {
				internacao = lista.get(0);
			}
			return internacao;
		}
	}

	/**
	 * Método que pesquisa as internações de um dado paciente, filtrando pelo
	 * prontuário
	 * 
	 * @param prontuario
	 * @return
	 */
	public List<AinInternacao> pesquisarInternacoesPorProntuarioUnidade(final Integer prontuario) {
		List<AinInternacao> retorno = new ArrayList<AinInternacao>();
		if (prontuario != null) {
			final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class, "ain");
			
			criteria.createAlias("ain." + AinInternacao.Fields.PACIENTE.toString(), "paciente");
			criteria.createAlias("ain." + AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(), "srvp");
			criteria.createAlias("srvp." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
			criteria.createAlias("pes." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "qual", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ain." + AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString(), "cnvPs");
			criteria.createAlias("cnvPs." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "cnvS");
			criteria.createAlias("ain." + AinInternacao.Fields.ESPECIALIDADE.toString(), "especialidade");
			criteria.createAlias("ain." + AinInternacao.Fields.QUARTO.toString(), "quarto", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ain." + AinInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "unidade", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ain." + AinInternacao.Fields.LEITO.toString(), "leito", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("leito." + AinLeitos.Fields.QUARTO.toString(), "quartoLeito", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("unidade." + AghUnidadesFuncionais.Fields.ALA.toString(), "undAla", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("ain." + AinInternacao.Fields.TIPO_ALTA_MEDICA.toString(), "tipoAlta", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq(AinInternacao.Fields.PRONTUARIO_PACIENTE.toString(), prontuario));
			criteria.addOrder(Order.desc(AinInternacao.Fields.DT_INTERNACAO.toString()));
			
			criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
			final List<AinInternacao> lista = executeCriteria(criteria);
			retorno = lista;
		}
		return retorno;
	}

	/**
	 * Método para retornar todos objetos de internação de um determinado
	 * paciente
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public List<AinInternacao> pesquisarInternacaoPorPaciente(final Integer codigoPaciente) {
		if (codigoPaciente == null) {
			return null;
		} else {
			final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
			criteria.add(Restrictions.eq(AinInternacao.Fields.COD_PACIENTE.toString(), codigoPaciente));
			return executeCriteria(criteria);
		}
	}

	/**
	 * Método para buscar todos registros de internação com o ID do projeto de
	 * pesquisa recebido por parâmetro.
	 * 
	 * @param seqProjetoPesquisa
	 * @return
	 */
	public List<AinInternacao> pesquisarInternacaoPorProjetoPesquisa(final Integer seqProjetoPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);

		criteria.add(Restrictions.eq(AinInternacao.Fields.PROJETO_PESQUISA_SEQ.toString(), seqProjetoPesquisa));

		return executeCriteria(criteria);
	}

	/**
	 * Método usado para verificar se um paciente está internado.
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public Boolean verificarPacienteInternado(final Integer codigoPaciente) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());
		criteria.add(Restrictions.eq(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.CODIGO.toString(),
				codigoPaciente));
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_SAIDA_PACIENTE.toString(), false));

		final Long totalInternacoes = this.executeCriteriaCount(criteria);

		return totalInternacoes > 0;
	}

	public Boolean verificarPacienteInternadoPorConsulta(Integer numeroConsulta){
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class, "AIN");
		
		criteria.createAlias("AIN." + AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString() , "ATU");
		
		criteria.add(Restrictions.eq("ATU." + AinAtendimentosUrgencia.Fields.CONSULTA_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.eq("AIN." + AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("ATU." + AinAtendimentosUrgencia.Fields.IND_PACIENTE_EM_ATENDIMENTO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq("ATU." + AinAtendimentosUrgencia.Fields.DATA_ALTA_ATENDIMENTO.toString(), null));
		
		return this.executeCriteriaExists(criteria);
	}
	
	/**
	 * Cria um DetachedCriteria de acordo com os parâmetros de pesquisa
	 * 
	 * @author Cirineu da Silva
	 * @param dataInicial
	 *            - Data inicial da pesquisa
	 * @param dataFinal
	 *            - Data final da pesquisa
	 * @return DetachedCriteria - critério instânciado
	 */
	private DetachedCriteria createPesquisaDetalheInternacaoCriteria(final Integer int_seq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);

		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());

		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_SAIDA_PACIENTE.toString(), false));

		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), int_seq));

		criteria.setProjection(Projections.projectionList().add(// 0
				Projections.property(AinInternacao.Fields.SEQ.toString())).add(// 1
				Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.CODIGO.toString())).add(// 2
				Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.PRONTUARIO.toString())).add(// 3
				Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.NOME.toString())).add(// 4
				Projections.property(AinInternacao.Fields.DT_INTERNACAO.toString())).add(// 5
				Projections.property(AinInternacao.Fields.DATA_PREV_ALTA.toString())).add(// 6
				Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.DATA_NASCIMENTO.toString()))
				.add(// 7
				Projections.property(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.SEXO.toString())).add(// 8
						Projections.property(AinInternacao.Fields.LEITO_ID.toString())).add(// 9
						Projections.property(AinInternacao.Fields.QRT_NUMERO.toString())).add(// 10
						Projections.property(AinInternacao.Fields.UNF_SEQ.toString())).add(// 11
						Projections.property(AinInternacao.Fields.CONVENIO.toString())).add(// 12
						Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString())).add(// 13
						Projections.property(AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString())).add(// 14
						Projections.property(AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString())).add(// 15
						Projections.property(AinInternacao.Fields.ORIGEM_EVENTO.toString())).add(// 16
						Projections.property(AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString())));

		return criteria;
	}

	public Object[] pesquisaDetalheInternacao(final Integer intSeq) {
		final DetachedCriteria criterio = createPesquisaDetalheInternacaoCriteria(intSeq);
		return (Object[]) executeCriteriaUniqueResult(criterio);
	}

	/**
	 * Metodo que busca internacoes de um paciente atraves do seu prontuario
	 * (obrigatorio) e sua data de internacao (opcional).
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param prontuario
	 *            - prontuario do paciente o qual desejamos buscar as
	 *            internacoes.
	 * @param dataInternacao
	 *            - data de internacao do paciente
	 * @return lista de internacoes do respectivo prontuario informado como
	 *         parametro.
	 */
	public List<AinInternacao> pesquisarInternacoesDoPacientePorProntuarioEDataInternacao(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Integer prontuario, final Date dataInternacao) {
		List<AinInternacao> lista = null;
		if (prontuario != null) {
			final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
			criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString(),
					CriteriaSpecification.INNER_JOIN);
			criteria.createAlias(AinInternacao.Fields.TIPO_ALTA_MEDICA.toString(), AinInternacao.Fields.TIPO_ALTA_MEDICA.toString(), JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.PRONTUARIO, prontuario));
            
			if (dataInternacao != null) {
				final GregorianCalendar cDate = new GregorianCalendar();
				cDate.setTime(dataInternacao);
				cDate.set(GregorianCalendar.HOUR_OF_DAY, 23);
				cDate.set(GregorianCalendar.MINUTE, 59);
				cDate.set(GregorianCalendar.SECOND, 59);
				criteria.add(Restrictions.between(AinInternacao.Fields.DT_INTERNACAO.toString(), dataInternacao, cDate.getTime()));
			}

			lista = executeCriteria(criteria, firstResult, maxResult, null, true);
		}
		return lista;
	}

	public Long pesquisarInternacoesDoPacientePorProntuarioEDataInternacaoCount(final Integer prontuario, final Date dataInternacao) {
		Long count = null;
		if (prontuario != null) {
			final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
			criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString(),
					CriteriaSpecification.INNER_JOIN);
			criteria.add(Restrictions.eq(AinInternacao.Fields.PACIENTE.toString() + "." + AipPacientes.Fields.PRONTUARIO, prontuario));

			if (dataInternacao != null) {
				final GregorianCalendar cDate = new GregorianCalendar();
				cDate.setTime(dataInternacao);
				cDate.set(GregorianCalendar.HOUR_OF_DAY, 23);
				cDate.set(GregorianCalendar.MINUTE, 59);
				cDate.set(GregorianCalendar.SECOND, 59);
				criteria.add(Restrictions.between(AinInternacao.Fields.DT_INTERNACAO.toString(), dataInternacao, cDate.getTime()));
			}

			criteria.setProjection(Projections.rowCount());
			count = (Long) this.executeCriteriaUniqueResult(criteria);
		}
		return count;
	}

	public List<Object[]> pesquisarAtendimentoPorPaciente(final Integer codigoPaciente) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class);
		criteria.createAlias(AghAtendimentos.Fields.INTERNACAO.toString(), "int");
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.property(AghAtendimentos.Fields.DTHR_INICIO.toString()))
				.add(Projections.property("int." + AinInternacao.Fields.IND_LOCAL_PACIENTE.toString()))
				.add(Projections.property("int." + AinInternacao.Fields.LEITO_ID.toString()))
				.add(Projections.property("int." + AinInternacao.Fields.QRT_NUMERO.toString()))
				.add(Projections.property("int." + AinInternacao.Fields.UNF_SEQ.toString()))
				.add(Projections.property("int." + AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()))
				.add(Projections.property(AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString() + "."
						+ AinAtendimentosUrgencia.Fields.SEQ.toString()))
				.add(Projections.property("int." + AinInternacao.Fields.TAM_CODIGO.toString())));
		criteria.add(Restrictions.eq("int." + AinInternacao.Fields.COD_PACIENTE.toString(), codigoPaciente));

		return executeCriteria(criteria);
	}
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	private Query criarQueryPesquisarInternacoesAtivas(final boolean countQuery, final String orderProperty, final Integer prontuario,
			final Integer pacCodigo, final String pacNome, final Short espSeq, final String leitoID, final Short qrtNum, final Short unfSeq, final Integer matriculaProfessor,
			final Short vinCodigoProfessor) {
		final boolean filtrarProntuario = prontuario != null;
		final boolean filtrarPacCodigo = pacCodigo != null;
		final boolean filtrarPacNome = StringUtils.isNotBlank(pacNome);
		final boolean filtrarEspSeq = espSeq != null;
		final boolean filtrarLeito = StringUtils.isNotBlank(leitoID);
		final boolean filtrarQuarto = qrtNum != null;
		final boolean filtrarUnidadeFuncional = unfSeq != null;
		final boolean filtrarCRM = matriculaProfessor != null && vinCodigoProfessor != null;

		final StringBuffer sql = new StringBuffer(300);
		sql.append(" select ");
		if (countQuery) {
			sql.append("count(").append(ALIAS_INTERNACAO).append(')');
		} else {
			sql.append(ALIAS_INTERNACAO);
		}
		sql.append(" from ");
		sql.append(AinInternacao.class.getSimpleName());
		sql.append(' ');
		sql.append(ALIAS_INTERNACAO);
		if(countQuery){
			sql.append(" inner join ").append(CAMPO_INTERNACAO_PACIENTE);
		}else{
			sql.append(" inner join fetch ").append(CAMPO_INTERNACAO_PACIENTE);			
		}
		sql.append(" inner join ").append(CAMPO_INTERNACAO_ESPECIALIDADE);
		sql.append(" inner join ").append(CAMPO_INTERNACAO_SERVIDORPROFESSOR);
		sql.append(" left join ").append(CAMPO_INTERNACAO_LEITO);
		sql.append(" left join ").append(CAMPO_INTERNACAO_LEITO_QUARTO);
		sql.append(" left join ").append(CAMPO_INTERNACAO_LEITO_QUARTO_UNIDADE);
		sql.append(" left join ").append(CAMPO_INTERNACAO_QUARTO);
		sql.append(" left join ").append(CAMPO_INTERNACAO_QUARTO_UNIDADE);
		sql.append(" left join ").append(CAMPO_INTERNACAO_UNIDADE);
		sql.append(" where ");
		// Filtro Indicação de Saída do Paciente
		sql.append(ALIAS_INTERNACAO);
		sql.append(SEPARATOR);
		sql.append(AinInternacao.Fields.IND_SAIDA_PACIENTE.toString());
		sql.append(" = :indSaidaPac ");
		if (filtrarProntuario) {
			sql.append(" and ");
			sql.append(CAMPO_INTERNACAO_PACIENTE);
			sql.append(SEPARATOR);
			sql.append(AipPacientes.Fields.PRONTUARIO.toString());
			sql.append(" = :prontuario ");
		}
		if (filtrarPacCodigo) {
			sql.append(" and ");
			sql.append(ALIAS_INTERNACAO);
			sql.append(SEPARATOR);
			sql.append(AinInternacao.Fields.PAC_CODIGO.toString());
			sql.append(" = :pacCodigo ");
		}
		if (filtrarPacNome) {
			sql.append(" and upper(");
			sql.append(CAMPO_INTERNACAO_PACIENTE);
			sql.append(SEPARATOR);
			sql.append(AipPacientes.Fields.NOME.toString());
			sql.append(") like upper('%' || :pacNome || '%') ");
		}
		if (filtrarEspSeq) {
			sql.append(" and ");
			sql.append(CAMPO_INTERNACAO_ESPECIALIDADE);
			sql.append(SEPARATOR);
			sql.append(AghEspecialidades.Fields.SEQ.toString());
			sql.append(" = :espSeq ");
		}
		if (filtrarLeito) {
			sql.append(" and upper(");
			sql.append(CAMPO_INTERNACAO_LEITO);
			sql.append(SEPARATOR);
			sql.append(AinLeitos.Fields.LTO_ID.toString());
			sql.append(") = upper(:leitoID) ");
		}
		if (filtrarQuarto) {
			sql.append(" and (");
			sql.append(CAMPO_INTERNACAO_QUARTO);
			sql.append(SEPARATOR);
			sql.append(AinQuartos.Fields.NUMERO.toString());
			sql.append(" = :qrtNum or ");
			sql.append(CAMPO_INTERNACAO_LEITO_QUARTO);
			sql.append(SEPARATOR);
			sql.append(AinQuartos.Fields.NUMERO.toString());
			sql.append(" = :qrtNum) ");
		}
		if (filtrarUnidadeFuncional) {
			sql.append(" and :unfSeq = ");
			sql.append("     (case when ");
			sql.append(CAMPO_INTERNACAO_LEITO).append(" is null and ");
			sql.append(CAMPO_INTERNACAO_QUARTO).append(" is null ");
			sql.append(" then ");
			sql.append(CAMPO_INTERNACAO_UNIDADE);
			sql.append(SEPARATOR);
			sql.append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString());
			sql.append("      else (case when ");
			sql.append(CAMPO_INTERNACAO_LEITO).append(" is null and ");
			sql.append(CAMPO_INTERNACAO_UNIDADE).append(" is null ");
			sql.append(" then ");
			sql.append(CAMPO_INTERNACAO_QUARTO_UNIDADE);
			sql.append(SEPARATOR);
			sql.append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString());
			sql.append("            else ");
			sql.append(CAMPO_INTERNACAO_LEITO_QUARTO_UNIDADE);
			sql.append(SEPARATOR);
			sql.append(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString());
			sql.append("            end) ");
			sql.append("      end) ");
		}
		if (filtrarCRM) {
			sql.append(" and ");
			sql.append(CAMPO_INTERNACAO_SERVIDORPROFESSOR);
			sql.append(SEPARATOR);
			sql.append(RapServidores.Fields.MATRICULA.toString());
			sql.append(" = :matriculaProfessor");
			sql.append(" and ");
			sql.append(CAMPO_INTERNACAO_SERVIDORPROFESSOR);
			sql.append(SEPARATOR);
			sql.append(RapServidores.Fields.CODIGO_VINCULO.toString());
			sql.append(" = :vinCodigoProfessor ");
		}
		if (!countQuery && StringUtils.isNotBlank(orderProperty)) {
			sql.append(" order by ").append(orderProperty).append(" asc ");
		}

		final Query query = createHibernateQuery(sql.toString());
		query.setParameter("indSaidaPac", Boolean.FALSE);
		if (filtrarProntuario) {
			query.setParameter("prontuario", prontuario);
		}
		if (filtrarPacCodigo) {
			query.setParameter("pacCodigo", pacCodigo);
		}
		if (filtrarPacNome) {
			query.setParameter("pacNome", pacNome);
		}
		if (filtrarEspSeq) {
			query.setParameter("espSeq", espSeq);
		}
		if (filtrarLeito) {
			query.setParameter("leitoID", leitoID);
		}
		if (filtrarQuarto) {
			query.setParameter("qrtNum", qrtNum);
		}
		if (filtrarUnidadeFuncional) {
			query.setParameter("unfSeq", unfSeq);
		}
		if (filtrarCRM) {
			query.setParameter("matriculaProfessor", matriculaProfessor);
			query.setParameter("vinCodigoProfessor", vinCodigoProfessor);
		}

		return query;
	}

	/**
	 * Pesquisa internacoes de pacientes que possuam o atributo indSaidaPaciente
	 * == false e tambem pelos seguintes parametros:
	 * 
	 * @param prontuario
	 * @param pacCodigo
	 * @param pacNome
	 * @param espSeq
	 *            - id da especialidade
	 * @param ltoId
	 *            - id do leito
	 * @param qrtNum
	 *            - numero do quarto
	 * @param unfSeq
	 *            - id da unidade funcional
	 * @param matriculaProfessor
	 * @param vinCodigoProfessor
	 * 
	 * @return List<AinInternacao> - lista de internacoes conforme os criterios
	 *         de pesquisa
	 */
	//@Restrict("#{s:hasPermission('internacao','pesquisar')}")
	@SuppressWarnings({"unchecked"})
	public List<AinInternacao> pesquisarInternacoesAtivas(final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc,
			final Integer prontuario, final Integer pacCodigo, final String pacNome, final Short espSeq, final String leitoID, final Short qrtNum, final Short unfSeq,
			final Integer matriculaProfessor, final Short vinCodigoProfessor) {

		final boolean countQuery = false;
		final String orderBy = CAMPO_INTERNACAO_LEITO + SEPARATOR + AinLeitos.Fields.LTO_ID.toString();
		final Query query = criarQueryPesquisarInternacoesAtivas(countQuery, orderBy, prontuario, pacCodigo, pacNome, espSeq, leitoID,
				qrtNum, unfSeq, matriculaProfessor, vinCodigoProfessor);

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);

		return query.list();
	}

	public Long pesquisarInternacoesAtivasCount(final Integer prontuario, final Integer pacCodigo, final String pacNome, final Short espSeq,
			final String leitoID, final Short qrtNum, final Short unfSeq, final Integer matriculaProfessor, final Short vinCodigoProfessor) {

		final boolean countQuery = true;
		final String orderBy = null;
		final Query query = criarQueryPesquisarInternacoesAtivas(countQuery, orderBy, prontuario, pacCodigo, pacNome, espSeq, leitoID,
				qrtNum, unfSeq, matriculaProfessor, vinCodigoProfessor);

		return (Long) query.uniqueResult();
	}

	private Query createPesquisaPacientesProfissionalEspecialidadeHql(final AghEspecialidades especialidade, final EspCrmVO profissional,
			final Integer firstResult, final Integer maxResults, final Boolean isCount) {

		//TODO: refatorar esta consulta para usar fields
		final StringBuffer hql = new StringBuffer(550);
		if (!isCount) {
			hql.append(" select int.paciente.prontuario, ");
			hql.append(" 		int.paciente.nome, ");
			hql.append(" 		int.dthrInternacao, ");
			hql.append(" 		case when pes.nomeUsual != null  then pes.nomeUsual else pes.nome end,");
			hql.append(" 		lto.leitoID, ");
			hql.append(" 		qto.descricao, ");
			hql.append(" 		unf.descricao, ");
			hql.append(" 		cli.descricao ");
		} else {
			hql.append(" select count(int)");
		}
		hql.append(" from ");
		if (profissional != null) {
			hql.append("    RapQualificacao qlf,  ");
		}
		hql.append(" 	AinInternacao int");
		hql.append(" 		left join int.unidadesFuncionais unf ");
		hql.append(" 		join int.servidorProfessor.pessoaFisica pes ");
		hql.append(" 		left join int.leito lto ");
		hql.append(" 		left join int.quarto qto ");
		hql.append(" 		left join int.especialidade esp ");
		hql.append(" 		left join esp.clinica cli ");
		hql.append(" where int.especialidade.seq = :seqEspecialidade ");
		hql.append(" 	and int.indPacienteInternado = :indPacienteInternado ");
		if (profissional != null) {
			hql.append(" 	and pes.codigo = qlf.id.pessoaFisica.codigo");
			hql.append(" 	and qlf.nroRegConselho = :nroRegConselho");
		}
		if (!isCount) {
			hql.append(" order by case when pes.nomeUsual != null  then pes.nomeUsual else pes.nome end , int.leito.leitoID ");
		}

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqEspecialidade", especialidade.getSeq());
		query.setParameter("indPacienteInternado", true);
		if (profissional != null) {
			query.setParameter("nroRegConselho", profissional.getNroRegConselho());
		}

		if (!isCount && firstResult != null) {
			query.setFirstResult(firstResult);
		}

		if (!isCount && maxResults != null) {
			query.setMaxResults(maxResults);
		}

		return query;
	}

	public Integer pesquisaPacientesProfissionalEspecialidadeCount(final AghEspecialidades especialidade, final EspCrmVO profissional)
			throws ApplicationBusinessException {
		final Query query = createPesquisaPacientesProfissionalEspecialidadeHql(especialidade, profissional, null, null, true);
		return ((Long) query.uniqueResult()).intValue();
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> pesquisaPacientesProfissionalEspecialidade(final Integer firstResult, final Integer maxResults, final String orderProperty,
			final boolean asc, final AghEspecialidades especialidade, final EspCrmVO profissional) throws ApplicationBusinessException {
		final Query query = createPesquisaPacientesProfissionalEspecialidadeHql(especialidade, profissional, firstResult, maxResults, false);
		return query.list();
	}

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_INTERNADOS
	 * 
	 * Conta internações para especialidade por origem para leitos do
	 * referencial.
	 */
	public Long[] contaInternados(final Short seqEspecialidade) {
		final StringBuffer hql = new StringBuffer(350);
		hql.append(" select sum(case when int.tipoCaracterInternacao.codigo = 2  then 1 else 0  end), ");
		hql.append(" 	sum(case when int.tipoCaracterInternacao.codigo = 2  then 0 else 1  end) ");
		hql.append(" from AinInternacao int ");
		hql.append(" where int.especialidade.seq = :seqEspecialidade ");
		hql.append(" 	and int.indPacienteInternado = :indPacienteInternado ");
		hql.append(" 	and int.leito.unidadeFuncional.seq != 157 ");
		// FIXME 157 = Magic Number migrado do AGH, referente a internações SIDA
		// ( HOSPITAL DIA SIDA - AIDS ). Refatorar.

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqEspecialidade", seqEspecialidade);
		query.setParameter("indPacienteInternado", Boolean.TRUE);

		final Object[] result = (Object[]) query.uniqueResult();

		Long[] auxReturn;
		if (result != null) {
			final Long pos1 = (Long) result[0];
			final Long pos2 = (Long) result[1];

			auxReturn = new Long[] { pos1 != null ? pos1 : 0, pos2 != null ? pos2 : 0 };
		} else {
			auxReturn = new Long[] { 0l, 0l };
		}

		return auxReturn;
	}

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_BLOQUEIOS
	 * 
	 * Conta leitos bloqueados em função do paciente do quarto considerando a
	 * especialidade do mesmo.
	 */
	public Long contaBloqueios(final Short seqEspecialidade) {
		final StringBuffer hql = new StringBuffer(500);
		hql.append(" select count(lto) ");
		hql.append(" from AinInternacao int ");
		hql.append(" 	join int.especialidade as esp, ");
		hql.append(" 	AinLeitos lto ");
		hql.append(" 	join lto.tipoMovimentoLeito as tml ");
		hql.append(" 	join lto.quarto as qrt ");
		hql.append(" where int.especialidade.seq = :seqEspecialidade ");
		hql.append(" 	and int.indPacienteInternado = :indPacienteInternado ");
		hql.append(" 	and tml.indBloqueioPaciente = :indBloqueioPaciente ");
		hql.append(" 	and int.leito.quarto.numero =  lto.quarto.numero ");
		hql.append(" 	and qrt.clinica = esp.clinica ");
		hql.append(" 	and qrt.indExclusivInfeccao != :indExclusivInfeccao ");
		hql.append(" 	and qrt.acomodacao.seq not in (1,2,3) ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqEspecialidade", seqEspecialidade);
		query.setParameter("indPacienteInternado", Boolean.TRUE);
		query.setParameter("indBloqueioPaciente", DominioSimNao.S);
		query.setParameter("indExclusivInfeccao", DominioSimNao.S);

		final Long retValue = (Long) query.uniqueResult();

		return retValue != null ? retValue : 0l;
	}

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_CTI
	 * 
	 * Conta internações em CTI considerando também os pacientes extras(sem
	 * leito).
	 */
	public Long[] contaCti(final Short seqEspecialidade) {
		Long pos1 = 0l;
		Long pos2 = 0l;

		final StringBuffer hql = new StringBuffer(420);
		hql.append(" select sum(case when int.tipoCaracterInternacao.codigo = 2  then 0 else 1  end), ");
		hql.append(" 	sum(case when int.tipoCaracterInternacao.codigo = 2  then 1 else 0  end) ");
		hql.append(" from AinLeitos lto ");
		hql.append(" 	join lto.internacao int ");
		hql.append(" 	join lto.unidadeFuncional uf ");
		hql.append(" where int.especialidade.seq = :seqEspecialidade ");
		hql.append(" 	and int.indPacienteInternado = :indPacienteInternado ");
		hql.append(" 	and uf.indUnidCti = :indUnidCti ");
		hql.append(" 	and lto = int.leito ");

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqEspecialidade", seqEspecialidade);
		query.setParameter("indPacienteInternado", Boolean.TRUE);
		query.setParameter("indUnidCti", DominioSimNao.S);

		Object[] result = (Object[]) query.uniqueResult();

		if (result != null) {
			final Long aux1 = (Long) result[0];
			final Long aux2 = (Long) result[1];

			if (aux1 != null) {
				pos1 += aux1;
			}

			if (aux2 != null) {
				pos2 += aux2;
			}
		}

		final StringBuffer hql2 = new StringBuffer(400);
		hql2.append(" select sum(case when int.tipoCaracterInternacao.codigo = 2  then 0 else 1  end), ");
		hql2.append(" 	sum(case when int.tipoCaracterInternacao.codigo = 2  then 1 else 0  end) ");
		hql2.append(" from AinInternacao int ");
		hql2.append(" 	join int.quarto qrt ");
		hql2.append(" 	join qrt.unidadeFuncional uf ");
		hql2.append(" where int.especialidade.seq = :seqEspecialidade ");
		hql2.append(" 	and int.indPacienteInternado = :indPacienteInternado ");
		hql2.append(" 	and uf.indUnidCti = :indUnidCti ");

		query = createHibernateQuery(hql2.toString());
		query.setParameter("seqEspecialidade", seqEspecialidade);
		query.setParameter("indPacienteInternado", Boolean.TRUE);
		query.setParameter("indUnidCti", DominioSimNao.S);

		result = (Object[]) query.uniqueResult();

		if (result != null) {
			final Long aux1 = (Long) result[0];
			final Long aux2 = (Long) result[1];

			if (aux1 != null) {
				pos1 += aux1;
			}

			if (aux2 != null) {
				pos2 += aux2;
			}
		}

		final StringBuffer hql3 = new StringBuffer(370);
		hql3.append(" select sum(case when int.tipoCaracterInternacao.codigo = 2  then 0 else 1  end), ");
		hql3.append(" 	sum(case when int.tipoCaracterInternacao.codigo = 2  then 1 else 0  end) ");
		hql3.append(" from AinInternacao int ");
		hql3.append(" 	join int.unidadesFuncionais uf ");
		hql3.append(" where int.especialidade.seq = :seqEspecialidade ");
		hql3.append(" 	and int.indPacienteInternado = :indPacienteInternado ");
		hql3.append(" 	and uf.indUnidCti = :indUnidCti ");

		query = createHibernateQuery(hql3.toString());
		query.setParameter("seqEspecialidade", seqEspecialidade);
		query.setParameter("indPacienteInternado", Boolean.TRUE);
		query.setParameter("indUnidCti", DominioSimNao.S);

		result = (Object[]) query.uniqueResult();

		if (result != null) {
			final Long aux1 = (Long) result[0];
			final Long aux2 = (Long) result[1];

			if (aux1 != null) {
				pos1 += aux1;
			}

			if (aux2 != null) {
				pos2 += aux2;
			}
		}

		return new Long[] { pos1, pos2 };
	}

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_APTOS
	 * 
	 * Conta internações em acomodação apartamentos.
	 */
	public Long[] contaAptos(final Short seqEspecialidade) {
		final StringBuffer hql = new StringBuffer(450);
		hql.append(" select sum(case when int.tipoCaracterInternacao.codigo = 2  then 0 else 1  end), ");
		hql.append(" 	sum(case when int.tipoCaracterInternacao.codigo = 2  then 1 else 0  end) ");
		hql.append(" from AinInternacao int ");
		hql.append(" 	join int.especialidade esp ");
		hql.append(" 	join int.leito lto ");
		hql.append(" 	join lto.quarto qrt ");
		hql.append(" where int.especialidade.seq = :seqEspecialidade ");
		hql.append(" 	and int.indPacienteInternado = :indPacienteInternado ");
		hql.append(" 	and lto.indPertenceRefl = :indPertenceRefl ");
		hql.append(" 	and qrt.acomodacao.seq in (1,2,3) ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqEspecialidade", seqEspecialidade);
		query.setParameter("indPacienteInternado", Boolean.TRUE);
		query.setParameter("indPertenceRefl", DominioSimNao.N);

		final Object[] result = (Object[]) query.uniqueResult();

		Long[] auxReturn;
		if (result != null) {
			final Long pos1 = (Long) result[0];
			final Long pos2 = (Long) result[1];

			auxReturn = new Long[] { pos1 != null ? pos1 : 0, pos2 != null ? pos2 : 0 };
		} else {
			auxReturn = new Long[] { 0l, 0l };
		}

		return auxReturn;
	}

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_OUTRAS_UNIDADES
	 * 
	 * Conta internações em leitos que não fazem parte do referencial, não são
	 * apartamentos e nem cti mas a clínica da internação é a mesma do leito.
	 */
	public Long[] contaOutrasUnidades(final Short seqEspecialidade) {
		final StringBuffer hql = new StringBuffer(560);
		hql.append(" select sum(case when int.tipoCaracterInternacao.codigo = 2  then 0 else 1  end), ");
		hql.append("	sum(case when int.tipoCaracterInternacao.codigo = 2  then 1 else 0  end) ");
		hql.append(" from AinInternacao int ");
		hql.append(" 	join int.especialidade esp ");
		hql.append(" 	join int.leito lto ");
		hql.append(" 	join lto.quarto qrt ");
		hql.append(" 	join lto.unidadeFuncional uf ");
		hql.append(" where int.especialidade.seq = :seqEspecialidade ");
		hql.append(" 	and esp.clinica = qrt.clinica ");
		hql.append(" 	and int.indPacienteInternado = :indPacienteInternado ");
		hql.append(" 	and lto.indPertenceRefl = :indPertenceRefl ");
		hql.append(" 	and uf.seq != 157 ");
		hql.append(" 	and uf.indUnidCti = :indUnidCti ");
		hql.append(" 	and qrt.acomodacao.seq not in (1,2,3) ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqEspecialidade", seqEspecialidade);
		query.setParameter("indPacienteInternado", Boolean.TRUE);
		query.setParameter("indPertenceRefl", DominioSimNao.N);
		query.setParameter("indUnidCti", DominioSimNao.N);

		final Object[] result = (Object[]) query.uniqueResult();

		Long[] auxReturn;
		if (result != null) {
			final Long pos1 = (Long) result[0];
			final Long pos2 = (Long) result[1];

			auxReturn = new Long[] { pos1 != null ? pos1 : 0, pos2 != null ? pos2 : 0 };
		} else {
			auxReturn = new Long[] { 0l, 0l };
		}

		return auxReturn;
	}

	/**
	 * ORADB Procedure AINK_PES_REF.CONTA_OUTRAS_CLINICAS
	 * 
	 * Conta internações para a clínica em questão onde os leitos ocupados são
	 * de clínica diferente, não são apartamentos e nem cti.
	 */
	public Long[] contaOutrasClinicas(final Short seqEspecialidade) {
		final StringBuffer hql = new StringBuffer(600);
		hql.append(" select sum(case when int.tipoCaracterInternacao.codigo = 2  then 0 else 1  end), ");
		hql.append("	sum(case when int.tipoCaracterInternacao.codigo = 2  then 1 else 0  end) ");
		hql.append(" from AinInternacao int ");
		hql.append(" 	join int.especialidade esp ");
		hql.append(" 	join int.leito lto ");
		hql.append(" 	join lto.quarto qrt ");
		hql.append(" 	join lto.unidadeFuncional uf ");
		hql.append(" where int.especialidade.seq = :seqEspecialidade ");
		hql.append(" 	and uf.seq != 157 ");
		hql.append(" 	and int.indPacienteInternado = :indPacienteInternado ");
		hql.append(" 	and esp.clinica != qrt.clinica ");
		hql.append(" 	and qrt.indExclusivInfeccao != :indExclusivInfeccao ");
		hql.append(" 	and qrt.acomodacao.seq not in (1,2,3) ");
		hql.append(" 	and uf.indUnidCti = :indUnidCti ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqEspecialidade", seqEspecialidade);
		query.setParameter("indPacienteInternado", Boolean.TRUE);
		query.setParameter("indExclusivInfeccao", DominioSimNao.S);
		query.setParameter("indUnidCti", DominioSimNao.N);

		final Object[] result = (Object[]) query.uniqueResult();

		Long[] auxReturn;
		if (result != null) {
			final Long pos1 = (Long) result[0];
			final Long pos2 = (Long) result[1];

			auxReturn = new Long[] { pos1 != null ? pos1 : 0, pos2 != null ? pos2 : 0 };
		} else {
			auxReturn = new Long[] { 0l, 0l };
		}

		return auxReturn;
	}

	@SuppressWarnings("unchecked")
	public List<Object[]> obterCensoUnion17(final Short unfSeq, final Short unfSeqMae, final Date data, final DominioSituacaoUnidadeFuncional status) {

		final StringBuffer hql = new StringBuffer(900);
		hql.append(" select ");
		hql.append(" 	unf.seq,  unfSeq.seq,");
		hql.append(" 	case when lto.leitoID is null then ( case when qrt.descricao is null then '' else (qrt.descricao) end ) else lto.leitoID end, ");
		hql.append(" 	pac.prontuario, pac.codigo, pac.nome,");
		hql.append(" 	int.dthrInternacao, esp.sigla, tpa.codigo,");
		hql.append(" 	int.seq, ser.id.matricula, ser.id.vinCodigo, cnv.codigo, pac.dtNascimento, cnv.descricao, tpa.descricao, esp.nomeEspecialidade, atd.seq"); 
		hql.append(" from AinInternacao int");
		hql.append(" join int.atendimento as atd ");
		hql.append(" join int.especialidade as esp ");
		hql.append(" join int.paciente as pac ");
		hql.append(" left join int.servidorProfessor as ser ");
		hql.append(" left join atd.unidadeFuncional as unf ");
		hql.append(" left join unf.unfSeq as unfSeq ");
		hql.append(" left join int.convenioSaude as cnv ");
		hql.append(" left join int.tipoAltaMedica as tpa");
		hql.append(" left join int.quarto as qrt");
		hql.append(" left join int.leito as lto");
		hql.append(" where atd.indPacAtendimento = :indPacAtd ");

		if (unfSeq != null) {
			hql.append(" and unf.seq = :unidadeFunc");
		}

		if (unfSeqMae != null) {
			hql.append(" and (unf.unfSeq.seq = :unidadeFuncMae or unf.seq = :unidadeFuncMae) ");
		}

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("indPacAtd", DominioPacAtendimento.S);

		if (unfSeq != null) {
			query.setParameter("unidadeFunc", unfSeq);
		}
		if (unfSeqMae != null) {
			query.setParameter("unidadeFuncMae", unfSeqMae);
		}

		return query.list();
	}

	/* Conta internações por profissional para leitos do referencial */
	public List<Object[]> contaInternadosNew(final Short vinculo, final Integer matricula, final Short especialidade) {
		// --------------------------------------------------------------------------------------------------------------
		final String aliasLeitos = "leito";
		final String aliasCI = "tipoCaracterInternacao";
		final String projecaoLeitoID = aliasLeitos + "." + AinLeitos.Fields.LTO_ID.toString();
		final String projecaoTciSeq = aliasCI + "." + AinTiposCaraterInternacao.Fields.CODIGO.toString();

		final DominioSimNao indAtuaInternacao = DominioSimNao.S;
		final Boolean indPacienteInternado = true;
		final Integer capacReferencial = 0;
		final Integer quantPacInternados = 0;

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.ESP_SEQ.toString(), especialidade));
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), indPacienteInternado));
		criteria.createCriteria(AinInternacao.Fields.LEITO.toString(), aliasLeitos, JoinType.INNER_JOIN);
		criteria.createCriteria(AinInternacao.Fields.CARATER_INTERNACAO.toString(), aliasCI, JoinType.INNER_JOIN);

		criteria.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(), "ESPECIALIDADE", JoinType.INNER_JOIN);

		criteria.createAlias("ESPECIALIDADE." + AghEspecialidades.Fields.PROF_ESPECIALIDADES.toString(), "PROF_ESPECIALIDADE",
				JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("PROF_ESPECIALIDADE." + AghProfEspecialidades.Fields.IND_ATUA_INTERNACAO.toString(),
				indAtuaInternacao));
		criteria.add(Restrictions.or(Restrictions.gt(
				"PROF_ESPECIALIDADE." + AghProfEspecialidades.Fields.QUANT_PAC_INTERNADOS.toString(), quantPacInternados),
				Restrictions.gt("PROF_ESPECIALIDADE." + AghProfEspecialidades.Fields.CAPAC_REFERENCIAL.toString(), capacReferencial)));

		criteria.createAlias(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(), "SERVIDOR_PROFESSOR", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("SERVIDOR_PROFESSOR." + RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("SERVIDOR_PROFESSOR." + RapServidores.Fields.CODIGO_VINCULO.toString(), vinculo));

		criteria.add(Restrictions.eqProperty("PROF_ESPECIALIDADE." + AghProfEspecialidades.Fields.SER_MATRICULA.toString(),
				"SERVIDOR_PROFESSOR." + RapServidores.Fields.MATRICULA.toString()));
		criteria.add(Restrictions.eqProperty("PROF_ESPECIALIDADE." + AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(),
				"SERVIDOR_PROFESSOR." + RapServidores.Fields.CODIGO_VINCULO.toString()));

		final ProjectionList pList = Projections.projectionList();
		pList.add(Property.forName(AinInternacao.Fields.ESP_SEQ.toString()));
		pList.add(Property.forName(projecaoLeitoID));
		pList.add(Property.forName(projecaoTciSeq));

		criteria.setProjection(pList);

		return executeCriteria(criteria);
	}

	/*
	 * Conta leitos bloqueados em função do paciente do quarto considerando o
	 * profissional do mesmo
	 */
	public Long contaBloqueios(final Short vinculo, final Integer matricula, final Short especialidade) {
		final StringBuffer hql = new StringBuffer(460);
		hql.append(" select count(lto) ");
		hql.append(" from AinInternacao int ");
		hql.append(" 	join int.especialidade as esp, ");
		hql.append(" 	AinLeitos lto ");
		hql.append(" 	join lto.tipoMovimentoLeito as tml ");
		hql.append(" where int.especialidade.seq = :seqEspecialidade ");
		hql.append(" 	and int.servidorProfessor.id.matricula = :matricula ");
		hql.append(" 	and int.servidorProfessor.id.vinCodigo = :vinCodigo ");
		hql.append(" 	and int.indPacienteInternado = :indPacienteInternado ");
		hql.append(" 	and tml.indBloqueioPaciente = :indBloqueioPaciente ");
		hql.append(" 	and int.leito.quarto.numero =  lto.quarto.numero ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqEspecialidade", especialidade);
		query.setParameter("vinCodigo", vinculo);
		query.setParameter("matricula", matricula);
		query.setParameter("indPacienteInternado", true);
		query.setParameter("indBloqueioPaciente", DominioSimNao.S);

		final Long pBloqueios = (Long) query.uniqueResult();

		return pBloqueios;
	}

	private DetachedCriteria criarCriteriaInternacaoContaCTI(final Short vinculo, final Integer matricula, final Short especialidade) {
		final Boolean indPacienteInternado = true;

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.ESP_SEQ.toString(), especialidade));
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), indPacienteInternado));

		final DetachedCriteria criteriaServidorProfessor = criteria.createCriteria(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString());
		criteriaServidorProfessor.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), matricula));
		criteriaServidorProfessor.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), vinculo));

		return criteria;
	}

	public List<AinInternacao> pesquisarContaCti(final Short vinculo, final Integer matricula, final Short especialidade) {
		final DetachedCriteria criteria = criarCriteriaInternacaoContaCTI(vinculo, matricula, especialidade);
		return executeCriteria(criteria);
	}

	/* Internações em acomodação apartamentos */
	public List<AinInternacao> internacoesAcomodacaoApartamento(final Short vinculo, final Integer matricula, final Short especialidade) {
		final DominioSimNao indPertenceRefl = DominioSimNao.N;
		final List<Integer> listaAcomodacoesSeq = Arrays.asList(1, 2, 3);

		final DetachedCriteria criteria = criarCriteriaInternacaoContaCTI(vinculo, matricula, especialidade);
		final DetachedCriteria criteriaLeito = criteria.createCriteria(AinInternacao.Fields.LEITO.toString(), JoinType.INNER_JOIN);
		criteriaLeito.add(Restrictions.eq(AinLeitos.Fields.IND_PERTENCE_REFL.toString(), indPertenceRefl));
		final DetachedCriteria criteriaQuarto = criteriaLeito.createCriteria(AinLeitos.Fields.QUARTO.toString(), JoinType.INNER_JOIN);
		final DetachedCriteria criteriaAcomodacao = criteriaQuarto.createCriteria(AinQuartos.Fields.ACOMODACAO.toString(),
				JoinType.INNER_JOIN);
		criteriaAcomodacao.add(Restrictions.in(AinAcomodacoes.Fields.SEQUENCIAL.toString(), listaAcomodacoesSeq));

		return executeCriteria(criteria);
	}

	/*
	 * Conta internações para a clínica em questão onde os leitos ocupados são
	 * de clínica diferente, não são apartamentos e nem cti
	 */
	public List<AinInternacao> internacoesOutraClinicasNaoAptoNaoCti(final Short vinculo, final Integer matricula, final Short especialidade,
			final Short hospDiaSiaAids) {
		final DominioSimNao indUnidCti = DominioSimNao.N;
		final DominioSimNao indExclusivInfeccao = DominioSimNao.S;
		final List<Integer> listaAcomodacoesSeq = Arrays.asList(1, 2, 3);
		final String aliasEspecialidade = "especialidade";
		final String aliasQuarto = "quarto";
		final String campoEspecialidadeClinica = aliasEspecialidade + "." + AghEspecialidades.Fields.CLINICA_CODIGO.toString();
		final String campoQuartoClinica = aliasQuarto + "." + AinQuartos.Fields.CLINICA.toString();

		final DetachedCriteria criteria = criarCriteriaInternacaoContaCTI(vinculo, matricula, especialidade);
		criteria.createCriteria(AinInternacao.Fields.ESPECIALIDADE.toString(), aliasEspecialidade, JoinType.INNER_JOIN);
		final DetachedCriteria criteriaLeito = criteria.createCriteria(AinInternacao.Fields.LEITO.toString(), JoinType.INNER_JOIN);
		final DetachedCriteria criteriaUnidadesFunc = criteriaLeito.createCriteria(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(),
				JoinType.INNER_JOIN);
		criteriaUnidadesFunc.add(Restrictions.ne(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), hospDiaSiaAids));
		final DetachedCriteria criteriaQuarto = criteriaLeito.createCriteria(AinLeitos.Fields.QUARTO.toString(), aliasQuarto,
				JoinType.INNER_JOIN);
		criteria.add(Restrictions.neProperty(campoQuartoClinica, campoEspecialidadeClinica));
		final DetachedCriteria criteriaAcomodacao = criteriaQuarto.createCriteria(AinQuartos.Fields.ACOMODACAO.toString(),
				JoinType.INNER_JOIN);
		criteriaAcomodacao.add(Restrictions.not(Restrictions.in(AinAcomodacoes.Fields.SEQUENCIAL.toString(), listaAcomodacoesSeq)));
		criteriaQuarto.add(Restrictions.ne(AinQuartos.Fields.IND_EXCLUSIV_INFECCAO.toString(), indExclusivInfeccao));
		criteriaUnidadesFunc.add(Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_CTI.toString(), indUnidCti));

		return executeCriteria(criteria);
	}

	/*
	 * Conta internações em leitos que não fazem parte do referencial, não são
	 * apartamentos e nem cti mas a clínica da internação é a mesma do leito
	 */
	public List<AinInternacao> internacoesMesmaClinicaNaoAptoNaoCti(final Short vinculo, final Integer matricula, final Short especialidade,
			final Short hospDiaSiaAids) {
		final DominioSimNao indUnidCti = DominioSimNao.N;
		final DominioSimNao indPertenceRefl = DominioSimNao.N;
		final List<Integer> listaAcomodacoesSeq = Arrays.asList(1, 2, 3);
		final String aliasEspecialidade = "especialidade";
		final String aliasQuarto = "quarto";
		final String campoEspecialidadeClinica = aliasEspecialidade + "." + AghEspecialidades.Fields.CLINICA_CODIGO.toString();
		final String campoQuartoClinica = aliasQuarto + "." + AinQuartos.Fields.CLINICA.toString();

		final DetachedCriteria criteria = criarCriteriaInternacaoContaCTI(vinculo, matricula, especialidade);
		// DetachedCriteria criteriaEspecialidade =
		criteria.createCriteria(AinInternacao.Fields.ESPECIALIDADE.toString(), aliasEspecialidade, JoinType.INNER_JOIN);
		final DetachedCriteria criteriaLeito = criteria.createCriteria(AinInternacao.Fields.LEITO.toString(), JoinType.INNER_JOIN);
		criteriaLeito.add(Restrictions.eq(AinLeitos.Fields.IND_PERTENCE_REFL.toString(), indPertenceRefl));
		final DetachedCriteria criteriaUnidadesFunc = criteriaLeito.createCriteria(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(),
				JoinType.INNER_JOIN);
		criteriaUnidadesFunc.add(Restrictions.ne(AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), hospDiaSiaAids));
		final DetachedCriteria criteriaQuarto = criteriaLeito.createCriteria(AinLeitos.Fields.QUARTO.toString(), aliasQuarto,
				JoinType.INNER_JOIN);
		criteria.add(Restrictions.eqProperty(campoQuartoClinica, campoEspecialidadeClinica));
		final DetachedCriteria criteriaAcomodacao = criteriaQuarto.createCriteria(AinQuartos.Fields.ACOMODACAO.toString(),
				JoinType.INNER_JOIN);
		criteriaAcomodacao.add(Restrictions.not(Restrictions.in(AinAcomodacoes.Fields.SEQUENCIAL.toString(), listaAcomodacoesSeq)));
		criteriaUnidadesFunc.add(Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_CTI.toString(), indUnidCti));

		return executeCriteria(criteria);
	}
	
	public List<Object[]> pesquisarInternacoesAvisoSamisUnidadeInternacao() {
		final DetachedCriteria criteriaAvisoInternacaoSAMES = obterCriteriaPesquisarInternacoesAvisoSamisUnidadeInternacao();
		return this.executeCriteria(criteriaAvisoInternacaoSAMES);
	}

	public Long pesquisarInternacoesAvisoSamisUnidadeInternacaoCount() {
		final DetachedCriteria criteriaAvisoInternacaoSAMES = obterCriteriaPesquisarInternacoesAvisoSamisUnidadeInternacao();
		return this.executeCriteriaCount(criteriaAvisoInternacaoSAMES);
	}
	
	private DetachedCriteria obterCriteriaPesquisarInternacoesAvisoSamisUnidadeInternacao() {
		final String separador = ".";

		final String pathProfessorInternacao = AinInternacao.Fields.SERVIDOR_PROFESSOR.toString() + separador
				+ RapServidores.Fields.PESSOA_FISICA.toString();

		final String pathQualificaoProfessorInternacao = pathProfessorInternacao + separador
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString();

		final String pathTipoQualificaoProfessorInternacao = pathQualificaoProfessorInternacao + separador
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString();

		final String pathConselhoTipoQualificaoProfessorInternacao = pathTipoQualificaoProfessorInternacao + separador
				+ RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString();

		final String pathClinicaEspecialidade = AinInternacao.Fields.ESPECIALIDADE.toString() + "."
				+ AghEspecialidades.Fields.CLINICA.toString();

		final String aliasPessoaFisicaProfessor = "pessoaFisicaProfessor";

		final String aliasQualificacoesProfessor = "qualificacoesProfessor";

		final String aliasTipoQualificacoesProfessor = "tipoQualificacoesProfessor";

		final String aliasConselhoTipoQualificacoesProfessorInternacao = "conselhoTipoQualificacaoProfessorInternacao";

		final String aliasClinicaEspecialidade = "clinicaEspecialidade";

		final DetachedCriteria criteriaAvisoInternacaoSAMES = DetachedCriteria.forClass(AinInternacao.class);

		// join com paciente
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());

		// join com professor
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(),
				AinInternacao.Fields.SERVIDOR_PROFESSOR.toString());

		// join com pessoa fisica do professor

		criteriaAvisoInternacaoSAMES.createAlias(pathProfessorInternacao, aliasPessoaFisicaProfessor);

		// join com qualificacações do professor da internação

		criteriaAvisoInternacaoSAMES.createAlias(pathQualificaoProfessorInternacao, aliasQualificacoesProfessor);

		// join com os tipos de qualificações do professor

		criteriaAvisoInternacaoSAMES.createAlias(pathTipoQualificaoProfessorInternacao, aliasTipoQualificacoesProfessor);

		// join com os conselhos profissionais dos tipos de qualificações do
		// professor

		criteriaAvisoInternacaoSAMES.createAlias(pathConselhoTipoQualificaoProfessorInternacao,
				aliasConselhoTipoQualificacoesProfessorInternacao);

		// join com carater internação
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.CARATER_INTERNACAO.toString(),
				AinInternacao.Fields.CARATER_INTERNACAO.toString());

		// join com unidade funcional
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.UNIDADE_FUNCIONAL.toString(),
				AinInternacao.Fields.UNIDADE_FUNCIONAL.toString());

		// join com especialidade
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(),
				AinInternacao.Fields.ESPECIALIDADE.toString());

		// join com clinica da especialidade
		criteriaAvisoInternacaoSAMES.createAlias(pathClinicaEspecialidade, aliasClinicaEspecialidade);

		// join com origem evento
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.ORIGEM_EVENTO.toString(),
				AinInternacao.Fields.ORIGEM_EVENTO.toString(), Criteria.LEFT_JOIN);

		// --------------------------------- restrições
		// -----------------------------//

		// retrição sobre indicador de saida do paciente
		criteriaAvisoInternacaoSAMES.add(Restrictions.eq(AinInternacao.Fields.IND_SAIDA_PACIENTE.toString(), false));

		// Restrição sobre as siglas dos conselhos profisisonais
		final List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());

		criteriaAvisoInternacaoSAMES.add(Restrictions.in(aliasConselhoTipoQualificacoesProfessorInternacao + separador
				+ RapConselhosProfissionais.Fields.SIGLA.toString(), restricoes));

		// retrição se o prontuário jah foi buscado
		criteriaAvisoInternacaoSAMES.add(Restrictions.isNull(AinInternacao.Fields.DATA_HORA_PRONTUARIO_BUSCADO.toString()));

		final Calendar seteDiasAtras = Calendar.getInstance();

		seteDiasAtras.add(Calendar.DAY_OF_MONTH, -7);

		// restrição apenas internações nos últimos 7 dias
		criteriaAvisoInternacaoSAMES.add(Restrictions.ge(AinInternacao.Fields.DATA_HORA_AVISO_SAMIS.toString(),
				seteDiasAtras.getTime()));

		// --------------------- projeções
		// -----------------------------------------//

		final PropertyProjection projectionSeq = Projections.property(AinInternacao.Fields.SEQ.toString());

		final PropertyProjection projectionAtendimento = Projections.property(AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString()
				+ separador + AinAtendimentosUrgencia.Fields.SEQ.toString());

		final PropertyProjection projectionPaciente = Projections.property(AinInternacao.Fields.PACIENTE.toString() + separador
				+ AipPacientes.Fields.CODIGO.toString());

		final PropertyProjection projectionProntuarioPaciente = Projections.property(AinInternacao.Fields.PACIENTE.toString() + separador
				+ AipPacientes.Fields.PRONTUARIO);

		final PropertyProjection projectionDtInternacao = Projections.property(AinInternacao.Fields.DT_INTERNACAO.toString());

		final PropertyProjection projectionNumeroQuarto = Projections.property(AinInternacao.Fields.QRT_NUMERO.toString());

		final PropertyProjection projectionLeitoID = Projections.property(AinInternacao.Fields.LEITO_ID.toString());

		final PropertyProjection projectionNomePaciente = Projections.property(AinInternacao.Fields.PACIENTE.toString() + separador
				+ AipPacientes.Fields.NOME);

		final PropertyProjection projectionEspecialidadeSigla = Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString()
				+ separador + AghEspecialidades.Fields.SIGLA.toString());

		final PropertyProjection projectionEspecialidadeNome = Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString()
				+ separador + AghEspecialidades.Fields.NOME.toString());

		final PropertyProjection projectionCodigoClinicaEspecialidade = Projections.property(aliasClinicaEspecialidade + separador
				+ AghClinicas.Fields.CODIGO.toString());

		final PropertyProjection projectionDescricaoClinicaEspecialidade = Projections.property(aliasClinicaEspecialidade + separador
				+ AghClinicas.Fields.DESCRICAO.toString());

		final PropertyProjection projectionNomeprofessor = Projections.property(aliasPessoaFisicaProfessor + separador
				+ RapPessoasFisicas.Fields.NOME.toString());

		final PropertyProjection projectionAndar = Projections.property(AinInternacao.Fields.UNIDADE_FUNCIONAL.toString() + separador
				+ AghUnidadesFuncionais.Fields.ANDAR);

		final PropertyProjection projectionAla = Projections.property(AinInternacao.Fields.UNIDADE_FUNCIONAL.toString() + separador
				+ AghUnidadesFuncionais.Fields.ALA);

		final PropertyProjection projectionDescricao = Projections.property(AinInternacao.Fields.UNIDADE_FUNCIONAL.toString() + separador
				+ AghUnidadesFuncionais.Fields.DESCRICAO);

		final PropertyProjection projectionDescricaoCaraterInternacao = Projections.property(AinInternacao.Fields.CARATER_INTERNACAO
				.toString() + separador + AinTiposCaraterInternacao.Fields.DESCRICAO);

		final PropertyProjection projectionDtHrAvisoSamisInternacao = Projections.property(AinInternacao.Fields.DATA_HORA_AVISO_SAMIS
				.toString());

		final PropertyProjection projectionDescricaoOrigemEvento = Projections.property(AinInternacao.Fields.ORIGEM_EVENTO.toString()
				+ separador + AghOrigemEventos.Fields.DESCRICAO);

		criteriaAvisoInternacaoSAMES.setProjection(Projections.projectionList().add(projectionSeq).add(projectionAtendimento)
				.add(projectionPaciente).add(projectionProntuarioPaciente).add(projectionDtInternacao).add(projectionNumeroQuarto)
				.add(projectionLeitoID).add(projectionNomePaciente).add(projectionEspecialidadeSigla).add(projectionEspecialidadeNome)
				.add(projectionCodigoClinicaEspecialidade).add(projectionDescricaoClinicaEspecialidade).add(projectionNomeprofessor)
				.add(projectionAndar).add(projectionAla).add(projectionDescricao).add(projectionDescricaoCaraterInternacao)
				.add(projectionDtHrAvisoSamisInternacao).add(projectionDescricaoOrigemEvento));
		return criteriaAvisoInternacaoSAMES;
	}

	public List<Object[]> pesquisarInternacoesAvisoSamisUnidadeLeito() {
		final DetachedCriteria criteriaAvisoInternacaoSAMES = obterCriteriaPesquisarInternacoesAvisoSamisUnidadeLeito();
		return this.executeCriteria(criteriaAvisoInternacaoSAMES);
	}

	public Long pesquisarInternacoesAvisoSamisUnidadeLeitoCount() {
		final DetachedCriteria criteriaAvisoInternacaoSAMES = obterCriteriaPesquisarInternacoesAvisoSamisUnidadeLeito();
		return this.executeCriteriaCount(criteriaAvisoInternacaoSAMES);
	}

	private DetachedCriteria obterCriteriaPesquisarInternacoesAvisoSamisUnidadeLeito() {
		final String separador = ".";

		final String pathProfessorInternacao = AinInternacao.Fields.SERVIDOR_PROFESSOR.toString() + separador
				+ RapServidores.Fields.PESSOA_FISICA.toString();

		final String pathQualificaoProfessorInternacao = pathProfessorInternacao + separador
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString();

		final String pathTipoQualificaoProfessorInternacao = pathQualificaoProfessorInternacao + separador
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString();

		final String pathConselhoTipoQualificaoProfessorInternacao = pathTipoQualificaoProfessorInternacao + separador
				+ RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString();

		final String pathClinicaEspecialidade = AinInternacao.Fields.ESPECIALIDADE.toString() + "."
				+ AghEspecialidades.Fields.CLINICA.toString();

		final String pathQuartoLeito = AinInternacao.Fields.LEITO.toString() + separador + AinLeitos.Fields.QUARTO.toString();

		final String pathUnidadeFuncionalLeito = AinInternacao.Fields.LEITO.toString() + separador
				+ AinLeitos.Fields.UNIDADE_FUNCIONAL.toString();

		final String aliasUnidadeFuncinalLeito = "unidadeFuncionalLeito";

		final String aliasPessoaFisicaProfessor = "pessoaFisicaProfessor";

		final String aliasQualificacoesProfessor = "qualificacoesProfessor";

		final String aliasTipoQualificacoesProfessor = "tipoQualificacoesProfessor";

		final String aliasConselhoTipoQualificacoesProfessorInternacao = "conselhoTipoQualificacaoProfessorInternacao";

		final String aliasClinicaEspecialidade = "clinicaEspecialidade";

		final String aliasQuartoLeito = "quartoLeito";

		final DetachedCriteria criteriaAvisoInternacaoSAMES = DetachedCriteria.forClass(AinInternacao.class);

		// join com paciente
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());

		// join com professor
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(),
				AinInternacao.Fields.SERVIDOR_PROFESSOR.toString());

		// join com pessoa fisica do professor

		criteriaAvisoInternacaoSAMES.createAlias(pathProfessorInternacao, aliasPessoaFisicaProfessor);

		// join com qualificacações do professor da internação

		criteriaAvisoInternacaoSAMES.createAlias(pathQualificaoProfessorInternacao, aliasQualificacoesProfessor);

		// join com os tipos de qualificações do professor

		criteriaAvisoInternacaoSAMES.createAlias(pathTipoQualificaoProfessorInternacao, aliasTipoQualificacoesProfessor);

		// join com os conselhos profissionais dos tipos de qualificações do
		// professor

		criteriaAvisoInternacaoSAMES.createAlias(pathConselhoTipoQualificaoProfessorInternacao,
				aliasConselhoTipoQualificacoesProfessorInternacao);

		// join com carater internação
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.CARATER_INTERNACAO.toString(),
				AinInternacao.Fields.CARATER_INTERNACAO.toString());

		// join com especialidade
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(),
				AinInternacao.Fields.ESPECIALIDADE.toString());

		// join com clinica da especialidade
		criteriaAvisoInternacaoSAMES.createAlias(pathClinicaEspecialidade, aliasClinicaEspecialidade);

		// join com Leito
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.LEITO.toString(), AinInternacao.Fields.LEITO.toString());

		// join com Quarto do Leito
		criteriaAvisoInternacaoSAMES.createAlias(pathQuartoLeito, aliasQuartoLeito);

		// join com unidade funcional
		criteriaAvisoInternacaoSAMES.createAlias(pathUnidadeFuncionalLeito, aliasUnidadeFuncinalLeito);

		// join com origem evento
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.ORIGEM_EVENTO.toString(),
				AinInternacao.Fields.ORIGEM_EVENTO.toString());

		// ----------------- restrições ---------------------------//

		// Restrição sobre as siglas dos conselhos profisisonais
		final List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());

		criteriaAvisoInternacaoSAMES.add(Restrictions.in(aliasConselhoTipoQualificacoesProfessorInternacao + separador
				+ RapConselhosProfissionais.Fields.SIGLA.toString(), restricoes));

		// retrição sobre indicador de saida do paciente
		criteriaAvisoInternacaoSAMES.add(Restrictions.eq(AinInternacao.Fields.IND_SAIDA_PACIENTE.toString(), false));

		// retrição se o prontuário jah foi buscado
		criteriaAvisoInternacaoSAMES.add(Restrictions.isNull(AinInternacao.Fields.DATA_HORA_PRONTUARIO_BUSCADO.toString()));

		final Calendar seteDiasAtras = Calendar.getInstance();

		seteDiasAtras.add(Calendar.DAY_OF_MONTH, -7);

		criteriaAvisoInternacaoSAMES.add(Restrictions.ge(AinInternacao.Fields.DATA_HORA_AVISO_SAMIS.toString(),
				seteDiasAtras.getTime()));

		// -------------------- projeções ---------------------//

		final PropertyProjection projectionSeq = Projections.property(AinInternacao.Fields.SEQ.toString());

		final PropertyProjection projectionAtendimento = Projections.property(AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString()
				+ separador + AinAtendimentosUrgencia.Fields.SEQ.toString());

		final PropertyProjection projectionPaciente = Projections.property(AinInternacao.Fields.PACIENTE.toString() + separador
				+ AipPacientes.Fields.CODIGO.toString());

		final PropertyProjection projectionProntuarioPaciente = Projections.property(AinInternacao.Fields.PACIENTE.toString() + separador
				+ AipPacientes.Fields.PRONTUARIO);

		final PropertyProjection projectionDtInternacao = Projections.property(AinInternacao.Fields.DT_INTERNACAO.toString());

		final PropertyProjection projectionNumeroQuarto = Projections.property(AinInternacao.Fields.QRT_NUMERO.toString());

		final PropertyProjection projectionLeitoID = Projections.property(AinInternacao.Fields.LEITO_ID.toString());

		final PropertyProjection projectionNomePaciente = Projections.property(AinInternacao.Fields.PACIENTE.toString() + separador
				+ AipPacientes.Fields.NOME);

		final PropertyProjection projectionEspecialidadeSigla = Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString()
				+ separador + AghEspecialidades.Fields.SIGLA.toString());

		final PropertyProjection projectionEspecialidadeNome = Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString()
				+ separador + AghEspecialidades.Fields.NOME.toString());

		final PropertyProjection projectionCodigoClinicaEspecialidade = Projections.property(aliasClinicaEspecialidade + separador
				+ AghClinicas.Fields.CODIGO.toString());

		final PropertyProjection projectionDescricaoClinicaEspecialidade = Projections.property(aliasClinicaEspecialidade + separador
				+ AghClinicas.Fields.DESCRICAO.toString());

		final PropertyProjection projectionNomeprofessor = Projections.property(aliasPessoaFisicaProfessor + separador
				+ RapPessoasFisicas.Fields.NOME.toString());

		final PropertyProjection projectionAndar = Projections.property(aliasUnidadeFuncinalLeito + separador
				+ AghUnidadesFuncionais.Fields.ANDAR);

		final PropertyProjection projectionAla = Projections.property(aliasUnidadeFuncinalLeito + separador
				+ AghUnidadesFuncionais.Fields.ALA);

		final PropertyProjection projectionDescricao = Projections.property(aliasUnidadeFuncinalLeito + separador
				+ AghUnidadesFuncionais.Fields.DESCRICAO);

		final PropertyProjection projectionDescricaoCaraterInternacao = Projections.property(AinInternacao.Fields.CARATER_INTERNACAO
				.toString() + separador + AinTiposCaraterInternacao.Fields.DESCRICAO);

		final PropertyProjection projectionDtHrAvisoSamisInternacao = Projections.property(AinInternacao.Fields.DATA_HORA_AVISO_SAMIS
				.toString());

		final PropertyProjection projectionDescricaoOrigemEvento = Projections.property(AinInternacao.Fields.ORIGEM_EVENTO.toString()
				+ separador + AghOrigemEventos.Fields.DESCRICAO);

		criteriaAvisoInternacaoSAMES.setProjection(Projections.projectionList().add(projectionSeq).add(projectionAtendimento)
				.add(projectionPaciente).add(projectionProntuarioPaciente).add(projectionDtInternacao).add(projectionNumeroQuarto)
				.add(projectionLeitoID).add(projectionNomePaciente).add(projectionEspecialidadeSigla).add(projectionEspecialidadeNome)
				.add(projectionCodigoClinicaEspecialidade).add(projectionDescricaoClinicaEspecialidade).add(projectionNomeprofessor)
				.add(projectionAndar).add(projectionAla).add(projectionDescricao).add(projectionDescricaoCaraterInternacao)
				.add(projectionDtHrAvisoSamisInternacao).add(projectionDescricaoOrigemEvento));
		return criteriaAvisoInternacaoSAMES;
	}

	public List<Object[]> pesquisarInternacoesAvisoSamisUnidadeQuarto() {
		final DetachedCriteria criteriaAvisoInternacaoSAMES = obterCriteriaPesquisarInternacoesAvisoSamisUnidadeQuarto();
		return this.executeCriteria(criteriaAvisoInternacaoSAMES);
	}

	public Long pesquisarInternacoesAvisoSamisUnidadeQuartoCount() {
		final DetachedCriteria criteriaAvisoInternacaoSAMES = obterCriteriaPesquisarInternacoesAvisoSamisUnidadeQuarto();
		return this.executeCriteriaCount(criteriaAvisoInternacaoSAMES);
	}
	
	private DetachedCriteria obterCriteriaPesquisarInternacoesAvisoSamisUnidadeQuarto() {
		final String separador = ".";

		final String pathProfessorInternacao = AinInternacao.Fields.SERVIDOR_PROFESSOR.toString() + separador
				+ RapServidores.Fields.PESSOA_FISICA.toString();

		final String pathQualificaoProfessorInternacao = pathProfessorInternacao + separador
				+ RapPessoasFisicas.Fields.QUALIFICACOES.toString();

		final String pathTipoQualificaoProfessorInternacao = pathQualificaoProfessorInternacao + separador
				+ RapQualificacao.Fields.TIPO_QUALIFICACAO.toString();

		final String pathConselhoTipoQualificaoProfessorInternacao = pathTipoQualificaoProfessorInternacao + separador
				+ RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString();

		final String pathClinicaEspecialidade = AinInternacao.Fields.ESPECIALIDADE.toString() + "."
				+ AghEspecialidades.Fields.CLINICA.toString();

		final String pathUnidadeFuncionalQuarto = AinInternacao.Fields.QUARTO.toString() + separador
				+ AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString();

		final String aliasUnidadeFuncinalQuarto = "unidadeFuncionalQuarto";

		final String aliasPessoaFisicaProfessor = "pessoaFisicaProfessor";

		final String aliasQualificacoesProfessor = "qualificacoesProfessor";

		final String aliasTipoQualificacoesProfessor = "tipoQualificacoesProfessor";

		final String aliasConselhoTipoQualificacoesProfessorInternacao = "conselhoTipoQualificacaoProfessorInternacao";

		final String aliasClinicaEspecialidade = "clinicaEspecialidade";

		final DetachedCriteria criteriaAvisoInternacaoSAMES = DetachedCriteria.forClass(AinInternacao.class);

		// join com paciente
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());

		// join com professor
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(),
				AinInternacao.Fields.SERVIDOR_PROFESSOR.toString());

		// join com pessoa fisica do professor

		criteriaAvisoInternacaoSAMES.createAlias(pathProfessorInternacao, aliasPessoaFisicaProfessor);

		// join com qualificacações do professor da internação

		criteriaAvisoInternacaoSAMES.createAlias(pathQualificaoProfessorInternacao, aliasQualificacoesProfessor);

		// join com os tipos de qualificações do professor

		criteriaAvisoInternacaoSAMES.createAlias(pathTipoQualificaoProfessorInternacao, aliasTipoQualificacoesProfessor);

		// join com os conselhos profissionais dos tipos de qualificações do
		// professor

		criteriaAvisoInternacaoSAMES.createAlias(pathConselhoTipoQualificaoProfessorInternacao,
				aliasConselhoTipoQualificacoesProfessorInternacao);

		// join com carater internação
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.CARATER_INTERNACAO.toString(),
				AinInternacao.Fields.CARATER_INTERNACAO.toString());

		// join com especialidade
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(),
				AinInternacao.Fields.ESPECIALIDADE.toString());

		// join com clinica da especialidade
		criteriaAvisoInternacaoSAMES.createAlias(pathClinicaEspecialidade, aliasClinicaEspecialidade);

		// join com Quarto
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.QUARTO.toString(), AinInternacao.Fields.QUARTO.toString());

		// join com unidade funcional
		criteriaAvisoInternacaoSAMES.createAlias(pathUnidadeFuncionalQuarto, aliasUnidadeFuncinalQuarto);

		// join com origem evento
		criteriaAvisoInternacaoSAMES.createAlias(AinInternacao.Fields.ORIGEM_EVENTO.toString(),
				AinInternacao.Fields.ORIGEM_EVENTO.toString(), Criteria.LEFT_JOIN);

		// ----------------------- Restrições ---------------//

		// retrição sobre indicador de saida do paciente
		criteriaAvisoInternacaoSAMES.add(Restrictions.eq(AinInternacao.Fields.IND_SAIDA_PACIENTE.toString(), false));

		// Restrição sobre as siglas dos conselhos profisisonais
		final List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());

		criteriaAvisoInternacaoSAMES.add(Restrictions.in(aliasConselhoTipoQualificacoesProfessorInternacao + separador
				+ RapConselhosProfissionais.Fields.SIGLA.toString(), restricoes));

		// retrição se o prontuário jah foi buscado
		criteriaAvisoInternacaoSAMES.add(Restrictions.isNull(AinInternacao.Fields.DATA_HORA_PRONTUARIO_BUSCADO.toString()));

		final Calendar seteDiasAtras = Calendar.getInstance();

		seteDiasAtras.add(Calendar.DAY_OF_MONTH, -7);

		criteriaAvisoInternacaoSAMES.add(Restrictions.ge(AinInternacao.Fields.DATA_HORA_AVISO_SAMIS.toString(),
				seteDiasAtras.getTime()));

		// -------------------------------- projeções
		// ------------------------------//

		final PropertyProjection projectionSeq = Projections.property(AinInternacao.Fields.SEQ.toString());

		final PropertyProjection projectionAtendimento = Projections.property(AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString()
				+ separador + AinAtendimentosUrgencia.Fields.SEQ.toString());

		final PropertyProjection projectionPaciente = Projections.property(AinInternacao.Fields.PACIENTE.toString() + separador
				+ AipPacientes.Fields.CODIGO.toString());

		final PropertyProjection projectionProntuarioPaciente = Projections.property(AinInternacao.Fields.PACIENTE.toString() + separador
				+ AipPacientes.Fields.PRONTUARIO);

		final PropertyProjection projectionDtInternacao = Projections.property(AinInternacao.Fields.DT_INTERNACAO.toString());

		final PropertyProjection projectionNumeroQuarto = Projections.property(AinInternacao.Fields.QRT_NUMERO.toString());

		final PropertyProjection projectionLeitoID = Projections.property(AinInternacao.Fields.LEITO_ID.toString());

		final PropertyProjection projectionNomePaciente = Projections.property(AinInternacao.Fields.PACIENTE.toString() + separador
				+ AipPacientes.Fields.NOME);

		final PropertyProjection projectionEspecialidadeSigla = Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString()
				+ separador + AghEspecialidades.Fields.SIGLA.toString());

		final PropertyProjection projectionEspecialidadeNome = Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString()
				+ separador + AghEspecialidades.Fields.NOME.toString());

		final PropertyProjection projectionCodigoClinicaEspecialidade = Projections.property(aliasClinicaEspecialidade + separador
				+ AghClinicas.Fields.CODIGO.toString());

		final PropertyProjection projectionDescricaoClinicaEspecialidade = Projections.property(aliasClinicaEspecialidade + separador
				+ AghClinicas.Fields.DESCRICAO.toString());

		final PropertyProjection projectionNomeprofessor = Projections.property(aliasPessoaFisicaProfessor + separador
				+ RapPessoasFisicas.Fields.NOME.toString());

		final PropertyProjection projectionAndar = Projections.property(aliasUnidadeFuncinalQuarto + separador
				+ AghUnidadesFuncionais.Fields.ANDAR);

		final PropertyProjection projectionAla = Projections.property(aliasUnidadeFuncinalQuarto + separador
				+ AghUnidadesFuncionais.Fields.ALA);

		final PropertyProjection projectionDescricao = Projections.property(aliasUnidadeFuncinalQuarto + separador
				+ AghUnidadesFuncionais.Fields.DESCRICAO);

		final PropertyProjection projectionDescricaoCaraterInternacao = Projections.property(AinInternacao.Fields.CARATER_INTERNACAO
				.toString() + separador + AinTiposCaraterInternacao.Fields.DESCRICAO);

		final PropertyProjection projectionDtHrAvisoSamisInternacao = Projections.property(AinInternacao.Fields.DATA_HORA_AVISO_SAMIS
				.toString());

		final PropertyProjection projectionDescricaoOrigemEvento = Projections.property(AinInternacao.Fields.ORIGEM_EVENTO.toString()
				+ separador + AghOrigemEventos.Fields.DESCRICAO);

		criteriaAvisoInternacaoSAMES.setProjection(Projections.projectionList().add(projectionSeq).add(projectionAtendimento)
				.add(projectionPaciente).add(projectionProntuarioPaciente).add(projectionDtInternacao).add(projectionNumeroQuarto)
				.add(projectionLeitoID).add(projectionNomePaciente).add(projectionEspecialidadeSigla).add(projectionEspecialidadeNome)
				.add(projectionCodigoClinicaEspecialidade).add(projectionDescricaoClinicaEspecialidade).add(projectionNomeprofessor)
				.add(projectionAndar).add(projectionAla).add(projectionDescricao).add(projectionDescricaoCaraterInternacao)
				.add(projectionDtHrAvisoSamisInternacao).add(projectionDescricaoOrigemEvento));
		return criteriaAvisoInternacaoSAMES;
	}
	
	/**
	 * Cria o criteria
	 * 
	 * @param dtInicialReferencia
	 * @param dtFinalReferencia
	 * @param sexo
	 * @return
	 */
	private DetachedCriteria createPesquisaPacientesComObitoCriteria(final Date dtInicialReferencia, final Date dtFinalReferencia, final DominioSexo sexo) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);

		// AIP_PACIENTES
		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());

		// AGH_ESPECIALIDADES
		criteria.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(), AinInternacao.Fields.ESPECIALIDADE.toString());

		criteria.setProjection(Projections
				.projectionList()

				// 0-Integer
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + SEPARATOR
						+ AipPacientes.Fields.PRONTUARIO.toString()))
				// 1-String
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + SEPARATOR + AipPacientes.Fields.NOME.toString()))
				// 2-DominioSexo
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + SEPARATOR + AipPacientes.Fields.SEXO.toString()))
				// 3-Date
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + SEPARATOR
						+ AipPacientes.Fields.DATA_NASCIMENTO.toString()))

				// 4-Date
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + SEPARATOR
						+ AipPacientes.Fields.DT_OBITO.toString()))
				// 5-Date
				.add(Projections.property(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()))

				// 6-String
				.add(Projections.property(AinInternacao.Fields.ESPECIALIDADE.toString() + SEPARATOR
						+ AghEspecialidades.Fields.NOME_REDUZIDO.toString())));

		criteria.add(Restrictions.ge(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(), dtInicialReferencia));
		criteria.add(Restrictions.le(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(), dtFinalReferencia));

		final List<String> listaAltaMedica = new ArrayList<String>();
		listaAltaMedica.add("C");
		listaAltaMedica.add("D");
		criteria.add(Restrictions.in(AinInternacao.Fields.TAM_CODIGO.toString(), listaAltaMedica));

		if (sexo != null && StringUtils.isNotBlank(sexo.getDescricao())) {
			criteria.add(Restrictions.eq(AinInternacao.Fields.PACIENTE.toString() + SEPARATOR + AipPacientes.Fields.SEXO.toString(),
					sexo));
		}

		return criteria;
	}

	/**
	 * Executa a pesquisa
	 * 
	 * @param dtInicialReferencia
	 * @param dtFinalReferencia
	 * @param sexo
	 * @return
	 */
	public List<Object[]> pesquisaPacientesComObito(final Date dtInicialReferencia, final Date dtFinalReferencia, final DominioSexo sexo) {
		final DetachedCriteria criteria = createPesquisaPacientesComObitoCriteria(dtInicialReferencia, dtFinalReferencia, sexo);
		return executeCriteria(criteria);
	}
	
	/**
	 * Cria o criteria
	 * 
	 * @param {Date} dtReferencia
	 * @return {DetachedCriteria}
	 */
	private DetachedCriteria createPesquisaPacientesAniversariantesCriteria(final Date dtReferencia) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);

		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());

		// Seta os campos que deseja retornar
		criteria.setProjection(Projections
				.projectionList()
				// 0 String
				.add(Projections.property(AinInternacao.Fields.LEITO_ID.toString()))
				// 1 Short
				.add(Projections.property(AinInternacao.Fields.QRT_NUMERO.toString()))
				// 2 Short
				.add(Projections.property(AinInternacao.Fields.UNF_SEQ.toString()))
				// 3 Integer
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + SEPARATOR
						+ AipPacientes.Fields.PRONTUARIO.toString()))
				// 4 String
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + SEPARATOR + AipPacientes.Fields.NOME.toString()))
				// 5 Date
				.add(Projections.property(AinInternacao.Fields.PACIENTE.toString() + SEPARATOR
						+ AipPacientes.Fields.DATA_NASCIMENTO.toString())));

		// Aplica Filtro
		final SimpleDateFormat formatador = new SimpleDateFormat("dd/MM");
		criteria.add(Restrictions.sqlRestriction("To_char(" + "DT_NASCIMENTO" + ",'dd/mm') = ?", formatador.format(dtReferencia),
				StringType.INSTANCE));

		criteria.add(Restrictions.ne(
				AinInternacao.Fields.PACIENTE.toString() + SEPARATOR + AipPacientes.Fields.DATA_NASCIMENTO.toString(), new Date()));

		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_SAIDA_PACIENTE.toString(), false));

		// Insere ordenação da consulta
		criteria.addOrder(Order.asc(AinInternacao.Fields.LEITO_ID.toString()));

		criteria.addOrder(Order.asc(AinInternacao.Fields.PACIENTE.toString() + SEPARATOR + AipPacientes.Fields.NOME.toString()));

		return criteria;
	}

	public List<Object[]> pesquisaPacientesAniversariantes(final Date dtReferencia) {
		final DetachedCriteria criteria = createPesquisaPacientesAniversariantesCriteria(dtReferencia);
		return executeCriteria(criteria);
	}
	
	public AinInternacao obterInternacaoPorProntuario(final Integer prontuario) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		return (AinInternacao) executeCriteriaUniqueResult(criteria);
	}
	

	public AinInternacao obterInternacaoPorLeito(final String leitoID) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.createAlias(AinInternacao.Fields.LEITO.toString(), "LET");
		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));
		criteria.add(Restrictions.eq("LET." + AinLeitos.Fields.LTO_ID.toString(), leitoID));
		return (AinInternacao) executeCriteriaUniqueResult(criteria);
	}
	
	public Long pesquisaInternacoesPorProntuarioCount(final Integer prontuario) {
		final DetachedCriteria cri = DetachedCriteria.forClass(AinInternacao.class);
		if (prontuario != null) {
			cri.createAlias("paciente", "paciente");
			cri.add(Restrictions.eq(AinInternacao.Fields.PRONTUARIO_PACIENTE.toString(), prontuario));
		}
		cri.setProjection(Projections.rowCount());
		return (Long) this.executeCriteriaUniqueResult(cri);
	}

	public List<AinInternacao> pesquisaInternacoesPorProntuario(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc, final Integer prontuario) {
		final DetachedCriteria cri = DetachedCriteria.forClass(AinInternacao.class, "int");
		if (prontuario != null) {
			cri.createAlias("int." + AinInternacao.Fields.PACIENTE.toString(), "pac");
			cri.add(Restrictions.eq("pac." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}
		cri.createAlias("int." + AinInternacao.Fields.LEITO.toString(), "lt", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias("int." + AinInternacao.Fields.QUARTO.toString(), "qrt", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias("int." + AinInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "unf", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias("unf." + AghUnidadesFuncionais.Fields.ALA.toString(), "ala", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias("int." + AinInternacao.Fields.ESPECIALIDADE.toString(), "esp");
		cri.createAlias("int." + AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString(), "csp");
		cri.createAlias("csp." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "cs");
		cri.createAlias("int." + AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(), "sp", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias("sp." + RapServidores.Fields.PESSOA_FISICA.toString(), "pf");
		
		cri.addOrder(Order.desc(AinInternacao.Fields.COD_PACIENTE.toString()));
		cri.addOrder(Order.desc(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()));
		return this.executeCriteria(cri, firstResult, maxResult, null, true);
	}
		
	public List<AinInternacao> pesquisarInternacao(final Integer firstResult, final Integer maxResults, final String orderProperty, final boolean asc,
			final Integer prontuario) {
		final DetachedCriteria criteria = createPesquisaCriteriaTransferenciaPaciente(prontuario);
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	public Long pesquisarInternacaoCount(final Integer prontuario) {
		final DetachedCriteria criteria = createPesquisaCriteriaTransferenciaPaciente(prontuario);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createPesquisaCriteriaTransferenciaPaciente(final Integer prontuario) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));
		if (prontuario != null) {
			criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
		}
		return criteria;
	}
		
	public List<AinInternacao> pesquisaInternacoesParaEstornarAltaPaciente(final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc, final Integer prontuario) {
		final DetachedCriteria cri = DetachedCriteria.forClass(AinInternacao.class);

		if (prontuario != null) {
			cri.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());
			cri.add(Restrictions.eq(AinInternacao.Fields.PRONTUARIO_PACIENTE.toString(), prontuario));
		}
		
		cri.createAlias(AinInternacao.Fields.LEITO.toString(), "LEITO", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AinInternacao.Fields.QUARTO.toString(),"QUARTO", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AinInternacao.Fields.UNIDADE_FUNCIONAL.toString(), "UMD_FUNC", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(),"ESP", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(),"SERV_PROF", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias("SERV_PROF." + RapServidores.Fields.PESSOA_FISICA, "PESFIS", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AinInternacao.Fields.CONVENIO_SAUDE_PLANO.toString(), "CONV_SAUDE", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias("CONV_SAUDE." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "FAT_COND_SAUDE", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AinInternacao.Fields.TIPO_ALTA_MEDICA.toString(), "TIPO_ALTA_MED", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AinInternacao.Fields.INSTITUICAO_HOSPITALAR_TRANSFERENCIA.toString(),"INST_HOSP", JoinType.LEFT_OUTER_JOIN);

		cri.add(Restrictions.or(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), false),
				Restrictions.eq(AinInternacao.Fields.IND_SAIDA_PACIENTE.toString(), true)));

		cri.addOrder(Order.desc(AinInternacao.Fields.COD_PACIENTE.toString()));
		cri.addOrder(Order.desc(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()));

		return this.executeCriteria(cri, firstResult, maxResult, null, true);
	}
	
	public Long pesquisaInternacoesParaEstornarAltaPacienteCount(final Integer prontuario) {
		final DetachedCriteria cri = DetachedCriteria.forClass(AinInternacao.class);

		if (prontuario != null) {
			cri.createAlias(AinInternacao.Fields.PACIENTE.toString(), AinInternacao.Fields.PACIENTE.toString());
			cri.add(Restrictions.eq(AinInternacao.Fields.PRONTUARIO_PACIENTE.toString(), prontuario));
		}

		cri.add(Restrictions.or(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), false),
				Restrictions.eq(AinInternacao.Fields.IND_SAIDA_PACIENTE.toString(), true)));

		cri.setProjection(Projections.rowCount());

		return (Long) this.executeCriteriaUniqueResult(cri);
	}
	

	public Boolean verificaPacienteInternado(final Integer intSeq) throws ApplicationBusinessException {

		final DetachedCriteria cri = DetachedCriteria.forClass(AinInternacao.class);
		cri.setProjection(Projections.projectionList().add(
				Projections.property(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString())));
		cri.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), intSeq));

		Boolean retorno = (Boolean) this.executeCriteriaUniqueResult(cri);
		if (retorno == null) {
			retorno = Boolean.TRUE;
		}
		return retorno;
	}
	
	/**
	 * Método responsável pela geração do Sequencial para o Id de uma internação
	 * de paciente
	 * 
	 * ORADB: AINK_INT_ATU.ATUALIZA_SEQ_INTERNACAO (não foi colocada em uma RN,
	 * pois já estava implementada na ON e é uma operação simples para setar o
	 * Id de Internação)
	 * 
	 * @param paciente
	 */
	@SuppressWarnings("deprecation")
	public Integer obterSequencialIdInternacao(final AinInternacao internacao) {
		return this.getNextVal(SequenceID.AIN_INT_SQ1);
	}
	
	/**
	 * 
	 * Método que obtem a data de internação anterior da internação
	 * 
	 * @param intSeq
	 * @return
	 */
	public Date obterDtInternacaoAnterior(final Integer intSeq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), intSeq));
		criteria.setProjection(Projections.projectionList().add(Projections.property(AinInternacao.Fields.DT_INTERNACAO.toString())));
		final Date dtInternacaoAnterior = (Date) this.executeCriteriaUniqueResult(criteria);

		return dtInternacaoAnterior;
	}

	public List<AinInternacao> pesquisarAinInternacao(final Integer pacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(
				AinInternacao.Fields.COD_PACIENTE.toString(), pacCodigo));
		return executeCriteria(criteria);
	}
	
	public List<AinInternacao> pesquisarInternacoesPorPaciente(final AipPacientes paciente) {
		final DetachedCriteria criteria = getCriteriaPesquisarInternacoesPorPaciente(paciente);
		criteria.addOrder(Order.desc("atds." + AghAtendimentos.Fields.DTHR_INICIO.toString()));

		final List<AinInternacao> listaVolta = executeCriteria(criteria);

		return listaVolta;
	}
	
	public Long pesquisarInternacoesPorPacienteCount(final AipPacientes paciente) {
		final DetachedCriteria criteria = getCriteriaPesquisarInternacoesPorPaciente(paciente);
		
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria getCriteriaPesquisarInternacoesPorPaciente(
			final AipPacientes paciente) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.createAlias("atendimento", "atds");
		criteria.add(Restrictions.eq(AinInternacao.Fields.COD_PACIENTE.toString(),
				paciente.getCodigo()));
		return criteria;
	}

	public DominioSexo obterSexoQuarto(final Short vQrtNumero) {
		final String aliasPaciente = "pac";
		final String campoSexoPaciente = aliasPaciente + "." + AipPacientes.Fields.SEXO.toString();

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.eq(AinInternacao.Fields.QRT_NUMERO.toString(), vQrtNumero));
		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), aliasPaciente);
		criteria.setProjection(Projections.distinct(Projections.property(campoSexoPaciente)));
		return (DominioSexo) executeCriteriaUniqueResult(criteria);
	}

	public DominioSexo obterSexoLeito(final Short vQrtNumero) {
		final String aliasPaciente = "pac";
		final String campoSexoPaciente = aliasPaciente + "." + AipPacientes.Fields.SEXO.toString();

		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), Boolean.TRUE));
		final DetachedCriteria criteriaLeito = criteria.createCriteria(AinInternacao.Fields.LEITO.toString());
		criteriaLeito.add(Restrictions.eq(AinLeitos.Fields.QRT_NUMERO.toString(), vQrtNumero));
		criteria.createCriteria(AinInternacao.Fields.PACIENTE.toString(), aliasPaciente);
		criteria.setProjection(Projections.distinct(Projections.property(campoSexoPaciente)));
		return (DominioSexo) executeCriteriaUniqueResult(criteria);
	}

	public List<AinInternacao> pesquisarPorNumeroQuarto(final Short numeroQuarto) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.QRT_NUMERO.toString(), numeroQuarto));
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), Boolean.TRUE));
		return executeCriteria(criteria);
	}
	
	/**
	 * Obter internações dados do professor.
	 * 
	 * @dbtables AinInternacao select
	 * 
	 * @param intSeq
	 * @return
	 */
	public List<AinInternacao> obterInternacao(final Boolean indPacienteInternado, final Integer matriculaProf, final Short vinCodigoProf,
			final Short codConvenio, final Short espSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), indPacienteInternado));
		criteria.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString(), matriculaProf));
		criteria.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString(), vinCodigoProf));
		criteria.add(Restrictions.eq(AinInternacao.Fields.CONVENIO_CODIGO.toString(), codConvenio));
		criteria.add(Restrictions.eq(AinInternacao.Fields.ESP_SEQ.toString(), espSeq));
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtém a última internação (NÃO VIGENTE) do paciente internado
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public AinInternacao obterUltimaInternacaoNaoVigentePaciente(final Integer codigoPaciente) {
		AinInternacao ultimaInternacao = null;
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.PAC_CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), false));
		criteria.addOrder(Order.desc(AinInternacao.Fields.DT_INTERNACAO.toString()));
		final List<AinInternacao> listaInternacoes = executeCriteria(criteria);
		if (!listaInternacoes.isEmpty()) {
			ultimaInternacao = listaInternacoes.get(0);
		}
		return ultimaInternacao;
	}

	public AinInternacao obterInternacaoJoinPaciente(final Integer intSeq) {
		final DetachedCriteria criteriaInternacao = DetachedCriteria.forClass(AinInternacao.class);
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), intSeq));
		criteriaInternacao.createCriteria("paciente", JoinType.INNER_JOIN);

		return (AinInternacao) this.executeCriteriaUniqueResult(criteriaInternacao);
	}

	private DetachedCriteria obterInternacaoAnteriorCriteria(Integer internacao){
		final DetachedCriteria cri = DetachedCriteria.forClass(AinInternacao.class);
		
		cri.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), internacao));
		
		cri.setProjection(Projections.projectionList()
				.add(Projections.property(AinInternacao.Fields.TAM_CODIGO.toString()),"tamCodigo")
				.add(Projections.property(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()),"dthrAltaMedica")
				.add(Projections.property(AinInternacao.Fields.DT_SAIDA_PACIENTE.toString()),"dtSaidaPaciente")
				.add(Projections.property(AinInternacao.Fields.CSP_CNV_CODIGO.toString()),"cspCnvCodigo")
				.add(Projections.property(AinInternacao.Fields.CSP_SEQ.toString()),"cspSeq")
				.add(Projections.property(AinInternacao.Fields.PAC_CODIGO.toString()),"pacCodigo")
				.add(Projections.property(AinInternacao.Fields.IPH_SEQ.toString()),"iphSeq")
				.add(Projections.property(AinInternacao.Fields.IPH_PHO_SEQ.toString()),"iphPhoSeq")
				.add(Projections.property(AinInternacao.Fields.DATA_HORA_PRIMEIRO_EVENTO.toString()),"dthrPrimeiroEvento")
				.add(Projections.property(AinInternacao.Fields.DATA_HORA_ULTIMO_EVENTO.toString()),"dthrUltimoEvento")
				.add(Projections.property(AinInternacao.Fields.DATA_PREV_ALTA.toString()),"dtPrevAlta")
				.add(Projections.property(AinInternacao.Fields.LEITO_ID.toString()),"leitoId")
				.add(Projections.property(AinInternacao.Fields.QRT_NUMERO.toString()),"qrtNumero")
				.add(Projections.property(AinInternacao.Fields.UNF_SEQ.toString()),"unfSeq")
				.add(Projections.property(AinInternacao.Fields.ESP_SEQ.toString()),"espSeq")
				.add(Projections.property(AinInternacao.Fields.IND_LOCAL_PACIENTE.toString()),"indLocalPaciente")
				.add(Projections.property(AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString()),"servidorProfessorMatricula")
				.add(Projections.property(AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString()),"servidorProfessorVinCodigo")
				.add(Projections.property(AinInternacao.Fields.DT_INTERNACAO.toString()),"dthrInternacao")
				.add(Projections.property(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString()),"indPacienteInternado")
				.add(Projections.property(AinInternacao.Fields.PROJETO_PESQUISA_SEQ.toString()),"projetoPesquisaSeq")
				.add(Projections.property(AinInternacao.Fields.SERVIDOR_DIGITA_MATRICULA.toString()),"servidorDigitaMatricula")
				.add(Projections.property(AinInternacao.Fields.SERVIDOR_DIGITA_VIN_CODIGO.toString()),"servidorDigitaVinCodigo")
				.add(Projections.property(AinInternacao.Fields.ENV_PRONT_UNID_INT.toString()),"envProntUnidInt")
				.add(Projections.property(AinInternacao.Fields.CODIGO_CARATER_INTERNACAO.toString()),"codTipoCaraterInternacao")
				.add(Projections.property(AinInternacao.Fields.ORIGEM_EVENTO_SEQ.toString()),"origemEventoSeq")
				.add(Projections.property(AinInternacao.Fields.IND_SAIDA_PACIENTE.toString()),"indSaidaPaciente")
				.add(Projections.property(AinInternacao.Fields.IND_DIF_CLASSE.toString()),"indDifClasse")
				.add(Projections.property(AinInternacao.Fields.ATENDIMENTO_URGENCIA_SEQ.toString()),"atuSeq")
				.add(Projections.property(AinInternacao.Fields.INSTITUICAO_HOSPITALAR_SEQ.toString()),"instituicaoHospitalarSeq")
				.add(Projections.property(AinInternacao.Fields.INSTITUICAO_HOSPITALAR_TRANSFERENCIA_SEQ.toString()),"instituicaoHospitalarTransferenciaSeq")
				.add(Projections.property(AinInternacao.Fields.JUSTIFICATIVA_ALT_DEL.toString()),"justificativaAltDel")
				.add(Projections.property(AinInternacao.Fields.OBS_PACIENTE_ALTA_CODIGO.toString()),"obsPacienteAltaCodigo")
				.add(Projections.property(AinInternacao.Fields.IND_CONS_MARCADA.toString()),"indConsMarcada")
				.add(Projections.property(AinInternacao.Fields.DOC_OBITO.toString()),"docObito")
				.add(Projections.property(AinInternacao.Fields.DATA_HORA_PRONTUARIO_BUSCADO.toString()),"dataHoraProntuarioBuscado")
				.add(Projections.property(AinInternacao.Fields.IND_ALTA_MANUAL.toString()),"indAltaManual")
				.add(Projections.property(AinInternacao.Fields.PRONTUARIO_LEGAL.toString()),"prontuarioLegal")
		);
		return cri;
	}

	public List<Object[]> obterInformacoesInternacaoAnterior(Integer internacao){
		final DetachedCriteria criteria = this.obterInternacaoAnteriorCriteria(internacao);
		return executeCriteria(criteria);
	}

	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_bloqueios)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	public Long obterQtdContaBloqueios(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		final StringBuffer hql = new StringBuffer(250);
		hql.append(" select count(lto."+ AinLeitos.Fields.QRT_NUMERO.toString() + ")");
		hql.append(" from AinInternacao int, ");
		hql.append(" 	AinLeitos lto ");
		hql.append(" 	join lto." + AinLeitos.Fields.TIPO_MOVIMENTO_LEITO.toString() + " as tml ");
		hql.append(" where tml." + AinTiposMovimentoLeito.Fields.BLOQUEIO_PACIENTE.toString() + " = :indBloqueioPaciente ");
		hql.append(" 	and int."+ AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString() + " = :indPacienteInternado ");
		hql.append(" 	and int."+ AinInternacao.Fields.QUARTO.toString() + "." + AinQuartos.Fields.NUMERO.toString() + " = ");
		hql.append(" 		lto.").append(AinLeitos.Fields.QRT_NUMERO.toString());
		hql.append(" 	and int." + AinInternacao.Fields.ESP_SEQ.toString() + " =:espSeq ");
		hql.append(" 	and int." + AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString()+ " =:serMatricula ");
		hql.append(" 	and int." + AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString()+ " =:serVinCodigo ");

		final Query query = createHibernateQuery(hql.toString());
		query.setParameter("indBloqueioPaciente", DominioSimNao.S);
		query.setParameter("indPacienteInternado", Boolean.TRUE);
		query.setParameter("espSeq", espSeq);
		query.setParameter("serMatricula", serMatricula);
		query.setParameter("serVinCodigo", serVinCodigo);

		final Long vContaBloqueios = (Long) query.uniqueResult();
		return vContaBloqueios;
	}
	
	public List<AinInternacao> listarInternacoesPorAtendimentoUrgencia(final AinAtendimentosUrgencia atendimentoUrgencia) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString(), atendimentoUrgencia));
		return executeCriteria(criteria);
	}

	public AinInternacao obterAtendimentoInternacaoCid(final Integer seqInternacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);

		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), seqInternacao));

		return (AinInternacao) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_cti1)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	public Integer obterQtdContaCti1(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		final DetachedCriteria criteriaInternacao = DetachedCriteria.forClass(AinInternacao.class);
		final DetachedCriteria criteriaLeito = criteriaInternacao.createCriteria(AinInternacao.Fields.LEITO.toString());
		final DetachedCriteria criteriaUnidadeFuncional = criteriaLeito.createCriteria(AinLeitos.Fields.UNIDADE_FUNCIONAL.toString());
		
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString(), serVinCodigo));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString(), serMatricula));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.ESP_SEQ.toString(), espSeq));
		criteriaUnidadeFuncional.add(Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_CTI.toString(), DominioSimNao.S));
		
		final List<AinInternacao> listaInt = executeCriteria(criteriaInternacao);
		return listaInt.size();
	}

	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_cti2)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	public Integer obterQtdContaCti2(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		final DetachedCriteria criteriaInternacao = DetachedCriteria.forClass(AinInternacao.class);
		final DetachedCriteria criteriaQuarto = criteriaInternacao.createCriteria(AinInternacao.Fields.QUARTO.toString());
		final DetachedCriteria criteriaUnidadeFuncional = criteriaQuarto.createCriteria(AinQuartos.Fields.UNIDADES_FUNCIONAIS.toString());
		
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString(), serVinCodigo));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString(), serMatricula));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.ESP_SEQ.toString(), espSeq));
		criteriaUnidadeFuncional.add(Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_CTI.toString(), DominioSimNao.S));
		
		final List<AinInternacao> listaInt = executeCriteria(criteriaInternacao);
		return listaInt.size();
	}
	
	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_cti3)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	public Integer obterQtdContaCti3(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		
		final DetachedCriteria criteriaInternacao = DetachedCriteria.forClass(AinInternacao.class);
		final DetachedCriteria criteriaUnidadeFuncional = criteriaInternacao.createCriteria(AinInternacao.Fields.UNIDADE_FUNCIONAL.toString());
		
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString(), serVinCodigo));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString(), serMatricula));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.ESP_SEQ.toString(), espSeq));
		criteriaUnidadeFuncional.add(Restrictions.eq(AghUnidadesFuncionais.Fields.IND_UNID_CTI.toString(), DominioSimNao.S));
		
		final List<AinInternacao> listaInt = executeCriteria(criteriaInternacao);
		return listaInt.size();
	}
	
	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_outras_clinicas)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	public Integer obterContaOutrasClinicas(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		
		final DetachedCriteria criteriaInternacao = DetachedCriteria.forClass(AinInternacao.class);
		
		criteriaInternacao.createAlias(AinInternacao.Fields.LEITO.toString(),"LEITO", JoinType.INNER_JOIN);
		criteriaInternacao.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(),"ESPECIALIDADE", JoinType.INNER_JOIN);
		criteriaInternacao.createAlias(AinInternacao.Fields.LEITO.toString() + "." + AinLeitos.Fields.QUARTO.toString(),
				"QUARTO", JoinType.INNER_JOIN);
		criteriaInternacao.createAlias(AinInternacao.Fields.LEITO.toString() + "." + AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(),
				"UNIDADE_FUNCIONAL", JoinType.INNER_JOIN);
		
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString(), serVinCodigo));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString(), serMatricula));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.ESP_SEQ.toString(), espSeq));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));
		
		//TODO COLOCAR PARÂMETRO 157 NA AGH_PARAMETROS (UNIDADE FUNCIONAL 157)
		final Short seqUnidadeFuncional = 157;
		criteriaInternacao.add(Restrictions.ne("LEITO."+AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), seqUnidadeFuncional));
		
		criteriaInternacao.add(Restrictions.neProperty("QUARTO."+AinQuartos.Fields.CLINICA_CODIGO.toString(), 
				"ESPECIALIDADE."+AghEspecialidades.Fields.CLINICA_CODIGO.toString()));
		criteriaInternacao.add(Restrictions.ne("QUARTO."+AinQuartos.Fields.IND_EXCLUSIV_INFECCAO.toString(), DominioSimNao.S));
		
		//TODO COLOCAR PARÂMETROS 1,2,3 (ABAIXO) NA AGH_PARAMETROS (ACOMODAÇÕES 1,2 E 3)
		final Criterion criterionAcmSeq1 = Restrictions.ne("QUARTO."+AinQuartos.Fields.ACOMODACAO_SEQ.toString(), 1);
		final Criterion criterionAcmSeq2 = Restrictions.ne("QUARTO."+AinQuartos.Fields.ACOMODACAO_SEQ.toString(), 2);
		final Criterion criterionAcmSeq3 = Restrictions.ne("QUARTO."+AinQuartos.Fields.ACOMODACAO_SEQ.toString(), 3);
		
		criteriaInternacao.add(criterionAcmSeq1);
		criteriaInternacao.add(criterionAcmSeq2);
		criteriaInternacao.add(criterionAcmSeq3);

		criteriaInternacao.add(Restrictions.eq("UNIDADE_FUNCIONAL."+AghUnidadesFuncionais.Fields.IND_UNID_CTI.toString(), DominioSimNao.N));
		
		final List<AinInternacao> listaInt = executeCriteria(criteriaInternacao);
		return listaInt.size();
	}
	
	/**
	 * ORADB AINC_CONTA_PAC_REFL (Cursor Conta_outras_unidades)
	 * @param serVinCodigo
	 * @param serMatricula
	 * @param espSeq
	 * @return
	 */
	public Integer obterContaOutrasUnidades(final Short serVinCodigo, final Integer serMatricula, final Short espSeq){
		final DetachedCriteria criteriaInternacao = DetachedCriteria.forClass(AinInternacao.class);
		
		criteriaInternacao.createAlias(AinInternacao.Fields.LEITO.toString(),"LEITO", JoinType.INNER_JOIN);
		criteriaInternacao.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(),"ESPECIALIDADE", JoinType.INNER_JOIN);
		criteriaInternacao.createAlias(AinInternacao.Fields.LEITO.toString() + "." + AinLeitos.Fields.QUARTO.toString(),
				"QUARTO", JoinType.INNER_JOIN);
		criteriaInternacao.createAlias(AinInternacao.Fields.LEITO.toString() + "." + AinLeitos.Fields.UNIDADE_FUNCIONAL.toString(),
				"UNIDADE_FUNCIONAL", JoinType.INNER_JOIN);
		
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString(), serVinCodigo));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString(), serMatricula));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.ESP_SEQ.toString(), espSeq));
		criteriaInternacao.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));
		
		criteriaInternacao.add(Restrictions.eq("LEITO."+AinLeitos.Fields.IND_PERTENCE_REFL.toString(), DominioSimNao.N));
		//TODO COLOCAR PARÂMETRO 157 NA AGH_PARAMETROS (UNIDADE FUNCIONAL 157)
		final Short seqUnidadeFuncional = 157;
		criteriaInternacao.add(Restrictions.ne("LEITO."+AinLeitos.Fields.UNIDADE_FUNCIONAL_SEQ.toString(), seqUnidadeFuncional));
		
		criteriaInternacao.add(Restrictions.eqProperty("QUARTO."+AinQuartos.Fields.CLINICA_CODIGO.toString(), 
				"ESPECIALIDADE."+AghEspecialidades.Fields.CLINICA_CODIGO.toString()));
		
		//TODO COLOCAR PARÂMETROS 1,2,3 (ABAIXO) NA AGH_PARAMETROS (ACOMODAÇÕES 1,2 E 3)
		final Criterion criterionAcmSeq1 = Restrictions.ne("QUARTO."+AinQuartos.Fields.ACOMODACAO_SEQ.toString(), 1);
		final Criterion criterionAcmSeq2 = Restrictions.ne("QUARTO."+AinQuartos.Fields.ACOMODACAO_SEQ.toString(), 2);
		final Criterion criterionAcmSeq3 = Restrictions.ne("QUARTO."+AinQuartos.Fields.ACOMODACAO_SEQ.toString(), 3);
		
		criteriaInternacao.add(criterionAcmSeq1);
		criteriaInternacao.add(criterionAcmSeq2);
		criteriaInternacao.add(criterionAcmSeq3);

		criteriaInternacao.add(Restrictions.eq("UNIDADE_FUNCIONAL."+AghUnidadesFuncionais.Fields.IND_UNID_CTI.toString(), DominioSimNao.N));
		
		final List<AinInternacao> listaInt = executeCriteria(criteriaInternacao);
		return listaInt.size();
	}
	
	
	
	
	/**
	 * Método usado para verificar se um paciente está internado.
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public Boolean habilitarDadosControle(final Integer cPacCodigo, final Integer atdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(EcpControlePaciente.class,"C");
		criteria.createAlias("C." + EcpControlePaciente.Fields.HORARIO.toString(), "H", JoinType.INNER_JOIN);
		criteria.createAlias("H." + EcpHorarioControle.Fields.ATENDIMENTO.toString(), "A", JoinType.INNER_JOIN);
		
		if (cPacCodigo != null) {
			criteria.add(Restrictions.eq("H." + EcpHorarioControle.Fields.PACIENTE_CODIGO.toString(), cPacCodigo));
		}

		if (atdSeq != null) {
			criteria.add(Restrictions.eq("A."+ AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		}
		
		final Long totalInternacoes = this.executeCriteriaCount(criteria);

		return totalInternacoes > 0;
	}
	
	public AinInternacao obterInternacaoConvenio(final Integer intSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.createAlias(AinInternacao.Fields.CONVENIO.toString(), "conv");
		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), intSeq));
		return (AinInternacao) executeCriteriaUniqueResult(criteria);
	}
	
	
	
	/**
	 * ORADB: cursor: FATK_CTH2_RN_UN.C_TCI2
	 */
	public Integer obterCodigoCaraterInternacaoPorAinInternacao(Integer seq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		
		criteria.createAlias(AinInternacao.Fields.CARATER_INTERNACAO.toString(), "TCI");

		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), seq));
		criteria.setProjection(Projections.property("TCI." + AinTiposCaraterInternacao.Fields.CODIGO_SUS.toString()));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	public AinInternacao obterInternacaoPorAtendimentoPacCodigo(final Integer pacCodigo) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.createAlias(AinInternacao.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.add(Restrictions.eq("ATD."+ AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("ATD."+ AghAtendimentos.Fields.IND_PAC_ATENDIMENTO.toString(), DominioPacAtendimento.S));
		criteria.add(Restrictions.eq("ATD."+ AghAtendimentos.Fields.ORIGEM.toString(), DominioOrigemAtendimento.I));
		
		return (AinInternacao) executeCriteriaUniqueResult(criteria);
	}
	
	public AinInternacao obterInternacaoPorSequencialPaciente(final Integer seq, final Integer pacCodigo) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		
		criteria.createAlias(AinInternacao.Fields.PACIENTE.toString(), "PAC");
		
		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq("PAC.".concat(AipPacientes.Fields.CODIGO.toString()), pacCodigo));
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), Boolean.TRUE));
		
		return (AinInternacao) executeCriteriaUniqueResult(criteria);
	}
	
	public AinInternacao obterAinInternacaoPorOrigemEAtdSeq(DominioOrigemAtendimento origem, Integer seq){
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class, "INT");
		criteria.createAlias(AinInternacao.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		if(seq != null){
			criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), seq));
		}	
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.ORIGEM.toString(), origem));
		
		return (AinInternacao)this.executeCriteriaUniqueResult(criteria);
	}
	
	public AinInternacao obterAinInternacaoPorAtdSeq(Integer atdSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class, "INT");
		criteria.createAlias(AinInternacao.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		
		return (AinInternacao)this.executeCriteriaUniqueResult(criteria);
	}

	public List<AinInternacao> pesquisarInternacoesPorPacientePOL(
			AipPacientes paciente) {
		final DetachedCriteria criteria = getCriteriaPesquisarInternacoesPorPaciente(paciente);
		
		criteria.setFetchMode(AinInternacao.Fields.ESPECIALIDADE.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AinInternacao.Fields.ESPECIALIDADE.toString()+"."+AghEspecialidades.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);
		criteria.setFetchMode(AinInternacao.Fields.SERVIDOR_PROFESSOR.toString(), FetchMode.JOIN);
	
		criteria.addOrder(Order.desc(AinInternacao.Fields.DT_INTERNACAO.toString()));
		
		final List<AinInternacao> listaVolta = executeCriteria(criteria);

		return listaVolta;
	}

	/**
	 * Obter os dados de uma internação de urgência
	 * 
	 * Web Service #38823
	 * 
	 * @param atuSeq
	 * @return
	 */
	public AinInternacao obterInternacaoPorAtendimentoUrgencia(Integer atuSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class, "INT");
		criteria.createAlias("INT." + AinInternacao.Fields.ATENDIMENTO_URGENCIA.toString(), "AAU");
		criteria.add(Restrictions.eq("AAU." + AinAtendimentosUrgencia.Fields.SEQ.toString(), atuSeq));
		List<AinInternacao> result = super.executeCriteria(criteria, 0, 1, null, true);
		if (result != null && !result.isEmpty()) {
			return result.get(0);
		}
		return null;
	}
	
	/**
	 * 
	 * Pesquisa por internações que possuam a instituição hospitalar como origem
	 * 
	 * @return Internacao
	 */
	public List<AinInternacao> pesquisarInternacoesInstituicaoHospitalarOrigem(final Integer ihoSeqOrigem) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.INSTITUICAO_HOSPITALAR_SEQ.toString(), ihoSeqOrigem));
		return executeCriteria(criteria);
	}
	
	/**
	 * 
	 * Pesquisa por internações que possuam a instituição hospitalar como transferência
	 * 
	 * @return Internacao
	 */
	public List<AinInternacao> pesquisarInternacoesInstituicaoHospitalarTransferencia(final Integer ihoSeqOrigem) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		criteria.add(Restrictions.eq(AinInternacao.Fields.INSTITUICAO_HOSPITALAR_TRANSFERENCIA_SEQ.toString(), ihoSeqOrigem));
		return executeCriteria(criteria);
	}

	public Boolean verificarUnidadeFuncionalControleAlta(AinInternacao internacao, String parametro) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		
		criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF_FUN");
		criteria.createAlias("UNF_FUN." + AghUnidadesFuncionais.Fields.CARACTERISTICAS.toString(), "UNID_CARAC");
		
		criteria.add(Restrictions.eq("UNID_CARAC." + AghCaractUnidFuncionais.Fields.DESCRICAO_CARACTERISTICA.toString(), ConstanteAghCaractUnidFuncionais.getEnum(parametro)));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.SEQ.toString(), internacao.getAtendimento().getSeq()));		
		
		return executeCriteriaCount(criteria) > 0 ? Boolean.TRUE : Boolean.FALSE;		
	}	
	
	public Date obterDtAltaMedicaInternacao(Integer seqInternacao, Integer seqContaHospitalar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class, "CTI");
		criteria.createAlias("CTI." + FatContasInternacao.Fields.INTERNACAO.toString(), "INT");
		
		if(seqInternacao != null){
			criteria.add(Restrictions.not(Restrictions.eq("CTI." + FatContasInternacao.Fields.INT_SEQ, seqInternacao)));		
		}
		if(seqContaHospitalar != null){
			criteria.add(Restrictions.eq("CTI." + FatContasInternacao.Fields.CTH_SEQ, seqContaHospitalar));	
		}
		
		criteria.setProjection(Projections.property("INT."+ AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()));
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	public Date obterDtAltaMedicaInternacaoPorSeqInternacao(Integer seqInternacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContasInternacao.class, "CTI");
		criteria.createAlias("CTI." + FatContasInternacao.Fields.INTERNACAO.toString(), "INT");
		
		if(seqInternacao != null){
			criteria.add(Restrictions.eq("INT." + AinInternacao.Fields.SEQ.toString(), seqInternacao));		
		}
		
		criteria.setProjection(Projections.property("INT."+ AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()));
		
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AinInternacao> verificaLeitoPossuiMovimentacao(AinLeitosVO leito) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);	
		criteria.add(Restrictions.eq(AinInternacao.Fields.LEITO_ID.toString(), leito.getLeitoID()));	
		return this.executeCriteria(criteria);
	}
	
	public AinInternacao obterInternacaoPaciente(Integer seqInternacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class,"INT");
		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(),seqInternacao));
		criteria.createAlias("INT." + AinInternacao.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));
		
		return (AinInternacao) this.executeCriteriaUniqueResult(criteria);
	}
	
	public AinInternacao obterInternacaoPacientePorSeq(Integer seqInternacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class,"INT");
		criteria.add(Restrictions.eq(AinInternacao.Fields.SEQ.toString(),seqInternacao));
		criteria.createAlias("INT." + AinInternacao.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		
		return (AinInternacao) this.executeCriteriaUniqueResult(criteria);
	}
	
	public List<AinInternacao> pesquisarAltasNoPeriodo(Date peridoInicial, Date periodoFinal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AinInternacao.class);
		
		criteria.createAlias(AinInternacao.Fields.ESPECIALIDADE.toString(), "ESP",JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.between(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString(), peridoInicial, periodoFinal))
				.add(Restrictions.or(	
										Restrictions.eq(AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), false), 
										Restrictions.eq(AinInternacao.Fields.IND_SAIDA_PACIENTE.toString(), true)
									));
		
		criteria.addOrder(Order.asc(AinInternacao.Fields.DATA_HORA_ALTA_MEDICA.toString()));
		
		return executeCriteria(criteria);
	}

}