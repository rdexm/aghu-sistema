package br.gov.mec.aghu.configuracao.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.enums.ConselhoRegionalEnfermagemEnum;
import br.gov.mec.aghu.enums.ConselhoRegionalFarmaciaEnum;
import br.gov.mec.aghu.enums.ConselhoRegionalMedicinaEnum;
import br.gov.mec.aghu.enums.ConselhoRegionalOdontologiaEnum;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.internacao.vo.ProfConveniosListVO;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.AghProfissionaisEspConvenio;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapQualificacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapTipoQualificacao;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@SuppressWarnings({ "PMD.CyclomaticComplexity"})
public class AghProfEspecialidadesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghProfEspecialidades> {

	private static final long serialVersionUID = 918402221135247716L;

	public List<AghProfEspecialidades> listaProfEspecialidades(RapServidores servidor, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class);
		
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.SERVIDOR.toString(), servidor));
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), true));

		return executeCriteria(criteria);
	}

	public boolean verificarExisteProfEspecialidadePorServidorEspSeq(RapServidores servidor, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class);
		
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.SERVIDOR.toString(), servidor));
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_PROF_REALIZA_CONSULTORIA.toString(), DominioSimNao.S));
		
		return executeCriteriaExists(criteria);
	}

	public List<AghProfEspecialidades> listaProfEspecialidadesEquipe(AghEspecialidades especialidade, RapServidores servidorEquipe) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class);

		if (especialidade.getEspecialidade()!=null){
			criteria.add(Restrictions.or(Restrictions.eq(
					AghProfEspecialidades.Fields.ESP_SEQ.toString(), especialidade.getSeq()), Restrictions.eq(
					AghProfEspecialidades.Fields.ESP_SEQ.toString(), especialidade.getEspecialidade().getSeq())));
		}	
		
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.SERVIDOR.toString(), servidorEquipe));

		return executeCriteria(criteria);
	}
	
	
	public AghProfEspecialidades findById(Integer serMatricula, Short serVinCodigo,Short espSeq){
		AghProfEspecialidadesId id = new AghProfEspecialidadesId(serMatricula, serVinCodigo, espSeq);
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class);
		criteria.add(Restrictions.eq("id", id));
		return (AghProfEspecialidades) executeCriteriaUniqueResult(criteria);		
	}
	
	/**
	 * ORADB capacidade_prof
	 * 
	 * @param pEsp
	 * @param pVin
	 * @param pMatr
	 * @return
	 */
	public BigDecimal capacidadeProf(Short pEsp, Short pVin, Integer pMatr) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghProfEspecialidades.class);

		cri.add(Restrictions.eq(AghProfEspecialidades.Fields.ESP_SEQ.toString(), pEsp));
		cri.add(Restrictions.eq(AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), pVin));
		cri.add(Restrictions.eq(AghProfEspecialidades.Fields.SER_MATRICULA.toString(), pMatr));
		cri.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_ATUA_INTERNACAO.toString(), DominioSimNao.S));

		AghProfEspecialidades aghProfEspecialidades = (AghProfEspecialidades) executeCriteriaUniqueResult(cri);

		if (aghProfEspecialidades != null && aghProfEspecialidades.getCapacReferencial() != null) {
			return BigDecimal.valueOf(aghProfEspecialidades.getCapacReferencial());
		} else {
			return BigDecimal.valueOf(0);
		}
	}

	public List<AghProfEspecialidades> pesquisarProfEspecialidadesCirurgiao(Short pEsp, Short pVin, Integer pMatr) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class);
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.ESP_SEQ.toString(), pEsp));
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), pVin));
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.SER_MATRICULA.toString(), pMatr));
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_CIRURGIAO_BLOCO.toString(), DominioSimNao.S));
		return executeCriteria(criteria);
	}
	
	public List<AghProfEspecialidades> listarEspecialidadesPorServidor(RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class);
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.SERVIDOR.toString(), servidor));

		return executeCriteria(criteria);
	}

	private DetachedCriteria obterCriteriaPesquisarEspCrmVO(Object strPesquisa, AghEspecialidades especialidade,
			DominioSimNao ambulatorio, Integer[] tiposQualificacao) throws ApplicationBusinessException {
		return obterCriteriaPesquisarEspCrmVO(strPesquisa, especialidade, ambulatorio, true, tiposQualificacao,true, true);
	}

	private DetachedCriteria obterCriteriaPesquisarEspCrmVO(Object strPesquisa, AghEspecialidades especialidade,
			DominioSimNao ambulatorio, Boolean situacaoAtiva, Integer[] tiposQualificacao, Boolean requerQualificacao, Boolean requerRegistro) throws ApplicationBusinessException {

		DetachedCriteria cri = DetachedCriteria.forClass(AghProfEspecialidades.class);
		cri.setProjection(Projections.projectionList().add(Projections.property("QF.nroRegConselho"), "nroRegConselho")
				.add(Projections.property("PF.nome"), "nomeMedico").add(Projections.property("PF.codigo"), "codigo")
				.add(Projections.property("aghEspecialidade.seq"), "espSeq")
				.add(Projections.property("SERVIDOR.id.matricula"), "matricula")
				.add(Projections.property("SERVIDOR.id.vinCodigo"), "vinCodigo"));
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "SERVIDOR", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				"PF", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QF", JoinType.LEFT_OUTER_JOIN);
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			cri.add(Restrictions.or(Restrictions.ilike("QF.nroRegConselho", strPesquisa.toString(), MatchMode.ANYWHERE),
					Restrictions.ilike("PF." + RapPessoasFisicas.Fields.NOME.toString(), strPesquisa.toString(), MatchMode.ANYWHERE)));
		}
		if (especialidade != null) {
			cri.add(Restrictions.eq("aghEspecialidade.seq", especialidade.getSeq()));
		}
		if (ambulatorio == null || ambulatorio.equals(DominioSimNao.N)) {
			cri.add(Restrictions.eq("indAtuaInternacao", DominioSimNao.S));
		}
		if (situacaoAtiva) {
			cri.add(Restrictions.or(
					Restrictions.eq("SERVIDOR.indSituacao", DominioSituacaoVinculo.A),
					Restrictions.and(Restrictions.eq("SERVIDOR.indSituacao", DominioSituacaoVinculo.P),
							Restrictions.ge("SERVIDOR.dtFimVinculo", Calendar.getInstance().getTime()))));
		}

		if (requerQualificacao){
			cri.add(Restrictions.in("QF.tipoQualificacao.codigo", tiposQualificacao));
		}

		if (ambulatorio != null && ambulatorio.equals(DominioSimNao.S)) {
			cri.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), true));
		}
		if (requerRegistro){
			cri.add(Restrictions.isNotNull("QF." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		}
		cri.setResultTransformer(Transformers.aliasToBean(EspCrmVO.class));

		return cri;
	}

	public List<EspCrmVO> pesquisarEspCrmVO(Object strPesquisa, AghEspecialidades especialidade, DominioSimNao ambulatorio,
			Integer[] tiposQualificacao) throws ApplicationBusinessException {
		DetachedCriteria cri = obterCriteriaPesquisarEspCrmVO(strPesquisa, especialidade, ambulatorio, tiposQualificacao);

		cri.addOrder(Order.asc("PF.nome"));

		return executeCriteria(cri, 0, 25, null, false);
	}

	public EspCrmVO obterEspCrmVO(Object strPesquisa, AghEspecialidades especialidade, DominioSimNao ambulatorio,
			RapServidores servidor, Integer[] tiposQualificacao) throws ApplicationBusinessException {
		DetachedCriteria cri = obterCriteriaPesquisarEspCrmVO(strPesquisa, especialidade, ambulatorio, false, tiposQualificacao, false, false);
		cri.add(Restrictions.eq(AghProfEspecialidades.Fields.SERVIDOR.toString(), servidor));
		
		List<EspCrmVO> listEspCrmVO = executeCriteria(cri); 
		
		if (listEspCrmVO!=null && !listEspCrmVO.isEmpty()){
			return listEspCrmVO.get(0);
		}
		return null;
	}
	
	public EspCrmVO obterCrmENomeMedicoPorServidor(RapServidores servidor, AghEspecialidades especialidade,
			Integer qualificacaoMedicina) throws ApplicationBusinessException {
		DetachedCriteria cri = DetachedCriteria.forClass(AghProfEspecialidades.class);

		cri.setProjection(Projections.projectionList().add(Projections.property("QF.nroRegConselho"), "nroRegConselho")
				.add(Projections.property("PF.nome"), "nomeMedico").add(Projections.property("PF.nomeUsual"), "nomeUsual")
				.add(Projections.property("PF.codigo"), "codigo").add(Projections.property("aghEspecialidade.seq"), "espSeq")
				.add(Projections.property("SERVIDOR.id.matricula"), "matricula")
				.add(Projections.property("SERVIDOR.id.vinCodigo"), "vinCodigo"));

		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "SERVIDOR", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				"PF", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QF", JoinType.LEFT_OUTER_JOIN);

		if (servidor != null && !"".equalsIgnoreCase(servidor.toString())) {
			cri.add(Restrictions.eq("SERVIDOR.id", servidor.getId()));
		}

		cri.add(Restrictions.eq("aghEspecialidade.seq", especialidade.getSeq()));
		cri.add(Restrictions.eq("indAtuaInternacao", DominioSimNao.S));
		cri.add(Restrictions.or(
				Restrictions.eq("SERVIDOR.indSituacao", DominioSituacaoVinculo.A),
				Restrictions.and(Restrictions.eq("SERVIDOR.indSituacao", DominioSituacaoVinculo.P),
						Restrictions.ge("SERVIDOR.dtFimVinculo", Calendar.getInstance().getTime()))));
		cri.add(Restrictions.eq("QF.tipoQualificacao.codigo", qualificacaoMedicina));

		cri.setResultTransformer(Transformers.aliasToBean(EspCrmVO.class));

		cri.addOrder(Order.asc("PF.nome"));

		return (EspCrmVO) executeCriteriaUniqueResult(cri);
	}
	
	/**
	 * ORADB View V_AIN_PES_REF_ESP_PRO.
	 * 
	 * @return Criteria referente a view (sem as colunas calculadas).
	 */
	private DetachedCriteria criarCriteriaReferencialEspecialidadeProfissonalView(Short serVinCodigo, Integer serMatricula, Short espSeq) {
		DominioSimNao indAtuaInternacao = DominioSimNao.S;
		Integer capacReferencial = 0;
		Integer quantPacInternados = 0;

		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class);
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_ATUA_INTERNACAO.toString(), indAtuaInternacao));
		criteria.add(Restrictions.or(
				Restrictions.gt(AghProfEspecialidades.Fields.QUANT_PAC_INTERNADOS.toString(), quantPacInternados),
				Restrictions.gt(AghProfEspecialidades.Fields.CAPAC_REFERENCIAL.toString(), capacReferencial)));

		if (serVinCodigo != null && serMatricula != null) {
			DetachedCriteria criteriaServidor = criteria.createCriteria(AghProfEspecialidades.Fields.SERVIDOR.toString());
			criteriaServidor.add(Restrictions.eq(RapServidores.Fields.CODIGO_VINCULO.toString(), serVinCodigo));
			criteriaServidor.add(Restrictions.eq(RapServidores.Fields.MATRICULA.toString(), serMatricula));
		}
		if (espSeq != null) {

			criteria.createAlias(AghProfEspecialidades.Fields.ESPECIALIDADE.toString(), "ESPECIALIDADE", JoinType.INNER_JOIN);
			criteria.add(Restrictions.eq("ESPECIALIDADE." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
		}

		AghProfEspecialidades.Fields ESP_SEQ = AghProfEspecialidades.Fields.ESP_SEQ;
		AghProfEspecialidades.Fields SER_VIN_CODIGO = AghProfEspecialidades.Fields.SER_VIN_CODIGO;
		AghProfEspecialidades.Fields SER_MATRICULA = AghProfEspecialidades.Fields.SER_MATRICULA;
		AghProfEspecialidades.Fields CAPAC_REFERENCIAL = AghProfEspecialidades.Fields.CAPAC_REFERENCIAL;

		ProjectionList pList = Projections.projectionList();
		pList.add(Property.forName(ESP_SEQ.toString()));
		pList.add(Property.forName(SER_VIN_CODIGO.toString()));
		pList.add(Property.forName(SER_MATRICULA.toString()));
		pList.add(Property.forName(CAPAC_REFERENCIAL.toString()));

		criteria.setProjection(pList);

		return criteria;
	}
	
	public Long pesquisarReferencialEspecialidadeProfissonalGridVOCount(AghEspecialidades especialidade)
			throws ApplicationBusinessException {
		Short serVinCodigo = null;
		Integer serMatricula = null;
		Short espSeq = especialidade.getSeq();

		DetachedCriteria criteriaView = this.criarCriteriaReferencialEspecialidadeProfissonalView(serVinCodigo, serMatricula, espSeq);
		return executeCriteriaCount(criteriaView);
	}

	public List<Object[]> pesquisarReferencialEspecialidadeProfissonalGridVO(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, AghEspecialidades especialidade) throws ApplicationBusinessException {
		Short serVinCodigo = null;
		Integer serMatricula = null;
		Short espSeq = especialidade.getSeq();

		DetachedCriteria criteriaView = this.criarCriteriaReferencialEspecialidadeProfissonalView(serVinCodigo, serMatricula, espSeq);
		return executeCriteria(criteriaView, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * @param matricula
	 * @param vinCodigo
	 * @return List<EspCrmVO>, contendo os dados do medico (CRM, nomeMedico,
	 *         nomeUsual, espSeq, cpf), onde cada elemento da lista armazena uma
	 *         especialidade do medico.
	 */
	public List<EspCrmVO> obterDadosDoMedicoPelaMatriculaEVinCodigo(Integer matricula, Short vinCodigo) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghProfEspecialidades.class);
		cri.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("QF.nroRegConselho"), "nroRegConselho").add(Projections.property("PF.nome"), "nomeMedico")
				.add(Projections.property("SERVIDOR.id.matricula"), "matricula")
				.add(Projections.property("SERVIDOR.id.vinCodigo"), "vinCodigo")
				.add(Projections.property("aghEspecialidade.seq"), "espSeq").add(Projections.property("PF.cpf"), "cpf")
				.add(Projections.property("PF.nomeUsual"), "nomeUsual")));
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "SERVIDOR", JoinType.INNER_JOIN);
		cri.createAlias("SERVIDOR." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.INNER_JOIN);
		cri.createAlias("PF." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QF", JoinType.INNER_JOIN);
		cri.createAlias("QF." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "TQ", JoinType.INNER_JOIN);
		cri.createAlias("TQ." + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CP", JoinType.INNER_JOIN);
		if (matricula != null && vinCodigo != null) {
			cri.add(Restrictions.eq("SERVIDOR.id.matricula", matricula));
			cri.add(Restrictions.eq("SERVIDOR.id.vinCodigo", vinCodigo));
		}
		cri.add(Restrictions.or(Restrictions.gt("SERVIDOR.dtFimVinculo", new Date()), Restrictions.isNull("SERVIDOR.dtFimVinculo")));
		List<String> siglas = new ArrayList<String>();
		siglas.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		siglas.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());
		cri.add(Restrictions.in("CP.sigla", siglas));
		cri.add(Restrictions.isNotNull("QF.nroRegConselho"));
		cri.setResultTransformer(Transformers.aliasToBean(EspCrmVO.class));
		return executeCriteria(cri);
	}
	
	/**
	 * ORADB VIEW V_AIN_SERV_TRANSF Este método implementa a query antes do
	 * UNION da view V_AIN_SERV_TRANSF em conjunto com a view
	 * V_AIN_SERV_INTERNA.
	 * 
	 * @dbtables AghEspecialidades select
	 * @dbtables AghProfEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * 
	 * @param especialidade
	 *            , strPesquisa
	 * @return listaObjetos
	 */
	public List<Object[]> obterCriteriaProfessoresInternacaoUnion1(String strPesquisa, Integer matriculaProfessor,
			Short vinCodigoProfessor) {

		DetachedCriteria cri = DetachedCriteria.forClass(AghProfEspecialidades.class);

		cri.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("rapServidor.id.matricula")), "matricula")
				.add(Projections.property("rapServidor.id.vinCodigo"), "vinCodigo").add(Projections.property("ESP.seq"), "espSeq")

				/*-------------APENAS PARA MANTER O ARRAY DE OBJETOS--------------*/
				.add(Projections.property("rapServidor.id.vinCodigo"), "cnvCodigo")
				/*----------------------------------------------------------------*/

				.add(Projections.property("ESP.sigla"), "sigla").add(Projections.property("PES.nome"), "nome")
				.add(Projections.property("QLF.nroRegConselho"), "nroRegConselho")
				.add(Projections.property("capacReferencial"), "capacReferencial")
				.add(Projections.property("quantPacInternados"), "quantPacInternados")

				/*-------------APENAS PARA MANTER O ARRAY DE OBJETOS--------------*/
				.add(Projections.property("PES.nome"), "atuaCti").add(Projections.property("PES.nome"), "dtInicioEscala")
		/*----------------------------------------------------------------*/
		);

		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "SERVIDOR", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				"PES", JoinType.INNER_JOIN);

		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QLF", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString() + "." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(),
				"TQL", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString() + "." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString()
				+ "." + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CPR", JoinType.INNER_JOIN);

		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa)) {
			cri.add(Restrictions.or(Restrictions.ilike("QLF.nroRegConselho", strPesquisa, MatchMode.ANYWHERE),
					Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}

		Criterion dtFimVinculoIsNull = Restrictions.isNull("SERVIDOR.dtFimVinculo");
		Criterion dtFimVinculoMaiorQueDataAtual = Restrictions.gt("SERVIDOR.dtFimVinculo", Calendar.getInstance().getTime());
		cri.add(Restrictions.or(dtFimVinculoIsNull, dtFimVinculoMaiorQueDataAtual));
		cri.add(Restrictions.isNotNull("QLF.nroRegConselho"));
		cri.add(Restrictions.eq("indInterna", DominioSimNao.S));

		List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());
		cri.add(Restrictions.in("CPR.sigla", restricoes));

		if (matriculaProfessor != null && vinCodigoProfessor != null) {
			cri.add(Restrictions.eq("SERVIDOR." + RapServidores.Fields.MATRICULA.toString(), matriculaProfessor));
			cri.add(Restrictions.eq("SERVIDOR." + RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigoProfessor));
		}

		return executeCriteria(cri);
	}
	
	public List<Object[]> obterProfessoresInternacao(String strPesquisa, Integer matriculaProfessor, Short vinCodigoProfessor) {
		
		List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());
		DetachedCriteria cri = montarCriteriaProfessoresInternacao(strPesquisa, matriculaProfessor, vinCodigoProfessor, restricoes);
		return executeCriteria(cri);
	}
	
	public Long obterProfessoresInternacaoCount(String strPesquisa, Integer matriculaProfessor, Short vinCodigoProfessor) {
		
		List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());
		DetachedCriteria cri = montarCriteriaProfessoresInternacao(strPesquisa, matriculaProfessor, vinCodigoProfessor, restricoes);
		return this.executeCriteriaCount(cri);
	}

	public List<Object[]> obterProfessoresInternacaoTodos(String strPesquisa, Integer matriculaProfessor, Short vinCodigoProfessor) {

		List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalEnfermagemEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalFarmaciaEnum.getListaValores());
		DetachedCriteria cri = montarCriteriaProfessoresInternacao(strPesquisa, matriculaProfessor, vinCodigoProfessor, restricoes);
		cri.addOrder(Order.asc("nome"));
		return this.executeCriteria(cri, 0, 100, null, Boolean.TRUE);
	}

	public Long obterProfessoresInternacaoTodosCount(String strPesquisa, Integer matriculaProfessor, Short vinCodigoProfessor) {
		List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalEnfermagemEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalFarmaciaEnum.getListaValores());
		DetachedCriteria cri = montarCriteriaProfessoresInternacao(strPesquisa, matriculaProfessor, vinCodigoProfessor, restricoes);
		return this.executeCriteriaCount(cri);
	}
	
	private DetachedCriteria montarCriteriaProfessoresInternacao(
			String strPesquisa, Integer matriculaProfessor,
			Short vinCodigoProfessor, List<String> restricoes) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghProfEspecialidades.class);

		cri.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("rapServidor.id.matricula")), "matricula")
				.add(Projections.property("rapServidor.id.vinCodigo"), "vinCodigo").add(Projections.property("PES.nome"), "nome")
								.add(Projections.property("QLF.nroRegConselho"), "nroRegConselho")
				.add(Projections.property("CPR.sigla"), "sigla"));

		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "SERVIDOR", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				"PES", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QLF", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString() + "." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(),
				"TQL", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString() + "." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString()
				+ "." + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CPR", JoinType.INNER_JOIN);

		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa)) {
			cri.add(Restrictions.or(Restrictions.ilike("QLF.nroRegConselho", strPesquisa, MatchMode.ANYWHERE),
			Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}

		Criterion dtFimVinculoIsNull = Restrictions.isNull("SERVIDOR.dtFimVinculo");
		Criterion dtFimVinculoMaiorQueDataAtual = Restrictions.gt("SERVIDOR.dtFimVinculo", Calendar.getInstance().getTime());
		cri.add(Restrictions.or(dtFimVinculoIsNull, dtFimVinculoMaiorQueDataAtual));
		cri.add(Restrictions.isNotNull("QLF.nroRegConselho"));
		cri.add(Restrictions.eq("indInterna", DominioSimNao.S));
		
		if (restricoes != null){
			cri.add(Restrictions.in("CPR.sigla", restricoes));
		}

		if (matriculaProfessor != null && vinCodigoProfessor != null) {
			cri.add(Restrictions.eq("SERVIDOR." + RapServidores.Fields.MATRICULA.toString(), matriculaProfessor));
			cri.add(Restrictions.eq("SERVIDOR." + RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigoProfessor));
		}
		return cri;
	}
	
	/**
	 * ORADB VIEW V_AIN_SERV_TRANSF Este método implementa a query depois do
	 * UNION da view V_AIN_SERV_TRANSF em conjunto com a view
	 * V_AIN_SERV_INTERNA.
	 * 
	 * @dbtables AghProfEspecialidades select
	 * @dbtables AghEspecialidades select
	 * @dbtables RapServidores select
	 * @dbtables RapPessoasFisicas select
	 * @dbtables RapQualificacao select
	 * @dbtables RapTipoQualificacao select
	 * @dbtables AghProfissionaisEspConvenio select
	 * @dbtables AinEscalasProfissionalInt select
	 * 
	 * @param especialidade
	 *            , strPesquisa
	 * @return listaObjetos
	 */
	public List<Object[]> obterCriteriaProfessoresInternacaoUnion2(String strPesquisa, Integer matriculaProfessor,
			Short vinCodigoProfessor) {
		DetachedCriteria cri = DetachedCriteria.forClass(AghProfEspecialidades.class);

		cri.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("rapServidor.id.matricula")), "matricula")
				.add(Projections.property("rapServidor.id.vinCodigo"), "vinCodigo").add(Projections.property("ESP.seq"), "espSeq")
				.add(Projections.property("EPI.id.pecCnvCodigo"), "cnvCodigo").add(Projections.property("ESP.sigla"), "sigla")
				.add(Projections.property("PES.nome"), "nome").add(Projections.property("QLF.nroRegConselho"), "nroRegConselho")
				.add(Projections.property("capacReferencial"), "capacReferencial")
				.add(Projections.property("quantPacInternados"), "quantPacInternados")
				.add(Projections.property("EPI.indAtuaCti"), "atuaCti").add(Projections.property("EPI.id.dtInicio"), "dtInicioEscala"));

		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "SERVIDOR", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.ESCALAS_PROFISSIONAIS_INT.toString(), "EPI", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.PROFISSIONAIS_ESP_CONVENIO.toString(), "PEC", JoinType.INNER_JOIN);

		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(),
				"PES", JoinType.INNER_JOIN);

		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QLF", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString() + "." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(),
				"TQL", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString() + "." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString()
				+ "." + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "CPR", JoinType.INNER_JOIN);

		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa)) {
			cri.add(Restrictions.or(Restrictions.ilike("QLF.nroRegConselho", strPesquisa, MatchMode.ANYWHERE),
					Restrictions.ilike("PES." + RapPessoasFisicas.Fields.NOME.toString(), strPesquisa, MatchMode.ANYWHERE)));
		}

		Criterion dtFimVinculoIsNull = Restrictions.isNull("SERVIDOR.dtFimVinculo");
		Criterion dtFimVinculoMaiorQueDataAtual = Restrictions.gt("SERVIDOR.dtFimVinculo", Calendar.getInstance().getTime());
		cri.add(Restrictions.or(dtFimVinculoIsNull, dtFimVinculoMaiorQueDataAtual));
		cri.add(Restrictions.isNotNull("QLF.nroRegConselho"));
		cri.add(Restrictions.eq("indInterna", DominioSimNao.S));

		List<String> restricoes = new ArrayList<String>();
		restricoes.addAll(ConselhoRegionalMedicinaEnum.getListaValores());
		restricoes.addAll(ConselhoRegionalOdontologiaEnum.getListaValores());
		cri.add(Restrictions.in("CPR.sigla", restricoes));

		cri.add(Restrictions.eq("PEC.indInterna", true));
		cri.add(Restrictions.le("EPI.id.dtInicio", Calendar.getInstance().getTime()));
		Criterion criterionDtFimNull = Restrictions.isNull("EPI.dtFim");
		Criterion criterionDtFimMaiorQueDataAtual = Restrictions.ge("EPI.dtFim", Calendar.getInstance().getTime());
		cri.add(Restrictions.or(criterionDtFimNull, criterionDtFimMaiorQueDataAtual));
		cri.add(Restrictions.eqProperty("EPI." + AinEscalasProfissionalInt.Fields.ID_CONVENIO_CODIGO.toString(), "PEC."
				+ AghProfissionaisEspConvenio.Fields.ID_CONVENIO_CODIGO.toString()));

		if (matriculaProfessor != null && vinCodigoProfessor != null) {
			cri.add(Restrictions.eq("SERVIDOR." + RapServidores.Fields.MATRICULA.toString(), matriculaProfessor));
			cri.add(Restrictions.eq("SERVIDOR." + RapServidores.Fields.CODIGO_VINCULO.toString(), vinCodigoProfessor));
		}

		return executeCriteria(cri);
	}
	
	/***
	 * Realiza a contagem de itens da pesquisa de convênios
	 *  
	 * @dbtables AghProfEspecialidades select
	 * @return Quantidade de itens da pesquisa
	 * */
	@SuppressWarnings("PMD.NPathComplexity")
	public Integer pesquisaProfConveniosListCount(Integer vinCodigo, Integer matricula, String nome, Long cpf, String siglaEspecialidade) {
		Long count = 0l;
		StringBuilder hql = new StringBuilder(400);
		boolean buff = false;

		hql.append(" select ");
		hql.append(" count (*) ");
		hql.append(" from ");
		
		hql.append(" AghProfEspecialidades pe ");
		hql.append(" left join pe.profissionaisEspConvenio pec ");
		hql.append(" join pe.rapServidor s ");
		hql.append(" join s.pessoaFisica pessoa");
		hql.append(" join pe.aghEspecialidade e ");

		if (vinCodigo != null || matricula != null || (nome != null && !nome.isEmpty()) || cpf != null || 
				(siglaEspecialidade != null && !siglaEspecialidade.isEmpty())) {
			hql.append(" where ");

			if (vinCodigo != null) {
				hql.append(" s.id.vinCodigo = :vinCodigo ");
				buff = true;
			}
			if (matricula != null) {
				if (buff) {
					hql.append(" and ");  
				}
				hql.append(" s.id.matricula = :matricula ");
				buff = true;
			}
			if (nome != null && !nome.isEmpty()) {
				if (buff) {
					hql.append(" and ");  
				}
				hql.append(" pessoa.nome like :nome ");
				buff = true;
			}
			if (cpf != null) {
				if (buff) {
					hql.append(" and ");  
				}
				hql.append(" pessoa.cpf = :cpf ");
				buff = true;
			}
			if (siglaEspecialidade != null && !siglaEspecialidade.isEmpty()) {
				if (buff) {
					hql.append(" and ");  
				}
				hql.append(" e.sigla = :siglaEspecialidade ");
				buff = true;
			}
		}
		
		hql.append(" group by e.seq, s.id.vinCodigo, s.id.matricula, pessoa.nome, pessoa.cpf, e.sigla, e.nomeEspecialidade");

		javax.persistence.Query query = this.createQuery(hql.toString());
		
		if (vinCodigo != null) {
			query.setParameter("vinCodigo", vinCodigo.shortValue());
		}
		if (matricula != null) {
			query.setParameter("matricula", matricula);
		}
		if (nome != null && !nome.isEmpty()) {
			query.setParameter("nome", "%".concat(nome.toUpperCase()).concat("%"));
		}
		if (cpf != null) {
			query.setParameter("cpf", cpf);
		}
		if (siglaEspecialidade != null && !siglaEspecialidade.isEmpty()) {
			query.setParameter("siglaEspecialidade", siglaEspecialidade.toUpperCase());
		}

		count = Long.valueOf(query.getResultList().size());

		return count.intValue();
	}

	/***
	 * Realiza a pesquisa de detalhamento de leitos
	 * 
	 * @dbtables AghProfEspecialidades select
	 * @param codigo do leito
	 * @return lista
	 * */
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<ProfConveniosListVO> pesquisaProfConvenioslist(Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc, Integer vinCodigo, Integer matricula, String nome, Long cpf, String siglaEspecialidade) {

		StringBuilder hql = new StringBuilder(500);
		boolean buff = false;
		
		hql.append(" select ");

		hql.append(" s.id.vinCodigo, s.id.matricula, "); 
		hql.append(" pessoa.nome, pessoa.cpf, ");
		hql.append(" e.sigla, e.nomeEspecialidade, count(pec.id.cnvCodigo), e.seq ");

		hql.append(" from ");
		
		hql.append(" AghProfEspecialidades pe ");
		hql.append(" left join pe.profissionaisEspConvenio pec ");
		hql.append(" join pe.rapServidor s ");
		hql.append(" join s.pessoaFisica pessoa");
		hql.append(" join pe.aghEspecialidade e ");

		if (vinCodigo != null || matricula != null || (nome != null && !nome.isEmpty()) || cpf != null || 
				(siglaEspecialidade != null && !siglaEspecialidade.isEmpty())) {
			hql.append(" where ");

			if (vinCodigo != null) {
				hql.append(" s.id.vinCodigo = :vinCodigo ");
				buff = true;
			}
			if (matricula != null) {
				if (buff) {
					hql.append(" and ");  
				}
				hql.append(" s.id.matricula = :matricula ");
				buff = true;
			}
			if (nome != null && !nome.isEmpty()) {
				if (buff) {
					hql.append(" and ");  
				}
				hql.append(" pessoa.nome like :nome ");
				buff = true;
			}
			if (cpf != null) {
				if (buff) {
					hql.append(" and ");  
				}
				hql.append(" pessoa.cpf = :cpf ");
				buff = true;
			}
			if (siglaEspecialidade != null && !siglaEspecialidade.isEmpty()) {
				if (buff) {
					hql.append(" and ");  
				}
				hql.append(" e.sigla = :siglaEspecialidade ");
				buff = true;
			}
		}

		hql.append(" group by e.seq, s.id.vinCodigo, s.id.matricula, pessoa.nome, pessoa.cpf, e.sigla, e.nomeEspecialidade");

		hql.append(" order by e.seq ");
		
		javax.persistence.Query query;
		List<ProfConveniosListVO> listaProfConveniosList = new ArrayList<ProfConveniosListVO>();

		query = this.createQuery(hql.toString());

		if (vinCodigo != null) {
			query.setParameter("vinCodigo", vinCodigo.shortValue());
		}
		if (matricula != null) {
			query.setParameter("matricula", matricula);
		}
		if (nome != null && !nome.isEmpty()) {
			query.setParameter("nome", "%".concat(nome.toUpperCase()).concat("%"));
		}
		if (cpf != null) {
			query.setParameter("cpf", cpf);
		}
		if (siglaEspecialidade != null && !siglaEspecialidade.isEmpty()) {
			query.setParameter("siglaEspecialidade", siglaEspecialidade.toUpperCase());
		}

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResult);

		Iterator<Object[]> it = query.getResultList().iterator();
		
		while (it.hasNext()) {
			Object[] obj = it.next();
			ProfConveniosListVO vo = new ProfConveniosListVO();
			
			if (obj[0] != null) {
				vo.setVinCodigo((String) obj[0].toString());
			}else{
				vo.setVinCodigo("");
			}

			if (obj[1] != null) {
				vo.setSerMatricula((String) obj[1].toString());
			}else{
				vo.setSerMatricula("");
			}

			if (obj[2] != null) {
				vo.setNome((String) obj[2]);
			}else{
				vo.setNome("");
			}

			if (obj[3] != null) {
				vo.setCpf((String) obj[3].toString());
			}else{
				vo.setCpf("");
			}

			if (obj[4] != null) {
				vo.setSigla((String) obj[4]);
			}else{
				vo.setSigla("");
			}

			if (obj[5] != null) {
				vo.setEspecialidade((String) obj[5]);
			}else{
				vo.setEspecialidade("");
			}

			if (obj[6] != null) {
				vo.setTotalConvenios((String) obj[6].toString());
			}else{
				vo.setTotalConvenios("");
			}

			if (obj[7] != null) {
				vo.setSeqEspecialidade((String) obj[7].toString());
			}else{
				vo.setSeqEspecialidade("");
			}

			listaProfConveniosList.add(vo);
		}
		
		return listaProfConveniosList;
	}
	
	//#1289 - C2
	private DetachedCriteria montarCriteriaConsultoresPorEspecialidade(final AghEspecialidades especialidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class, "pre");
		criteria.createAlias("pre." + AghProfEspecialidades.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias("pre." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "ser");
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		
		criteria.add(Restrictions.ne("ser." + RapServidores.Fields.IND_SITUACAO.toString(), DominioSituacaoVinculo.I));
		criteria.add(Restrictions.eq("pre." + AghProfEspecialidades.Fields.IND_PROF_REALIZA_CONSULTORIA.toString(), DominioSimNao.S));
		criteria.add(Restrictions.eq("esp." + AghEspecialidades.Fields.SIGLA.toString(), especialidade.getSigla()));
		
		return criteria;
	}
	
	public List<AghProfEspecialidades> pesquisarConsultoresPorEspecialidade(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, final AghEspecialidades especialidade) {
		DetachedCriteria criteria = this.montarCriteriaConsultoresPorEspecialidade(especialidade);
		criteria.addOrder(Order.asc("pes." + RapPessoasFisicas.Fields.NOME.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, true);
	}
	
	public Long pesquisarConsultoresPorEspecialidadeCount(final AghEspecialidades especialidade) {
		DetachedCriteria criteria = this.montarCriteriaConsultoresPorEspecialidade(especialidade);
		return this.executeCriteriaCount(criteria);
	}		

	public List<AghProfEspecialidades> listarProfEspecialidades(Integer matricula, Integer codigoVinculo, Integer codigoEspecialidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class);
		
		criteria.createAlias(AghProfEspecialidades.Fields.PROFISSIONAIS_ESP_CONVENIO.toString(),"ESPCONV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ESPCONV." + AghProfissionaisEspConvenio.Fields.FAT_CONVENIO_SAUDE,"FAT_CONV", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AghProfEspecialidades.Fields.ESPECIALIDADE.toString(),"ESP", JoinType.LEFT_OUTER_JOIN);

		if (matricula != null) {
			criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.SER_MATRICULA.toString(), matricula));
		}

		if (codigoVinculo != null) {
			criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString(), codigoVinculo.shortValue()));
		}

		if (codigoEspecialidade != null) {
			criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.ESP_SEQ.toString(), codigoEspecialidade.shortValue()));
		}

		return executeCriteria(criteria);
	}
	
	public Integer obterQtdContaInternados(Short serVinCodigo, Integer serMatricula, Short espSeq){
		DetachedCriteria criteriaViewProfEsp = this.criarCriteriaReferencialEspecialidadeProfissonalView(serVinCodigo, serMatricula, espSeq);
		
		criteriaViewProfEsp.createAlias("ESPECIALIDADE."+AghEspecialidades.Fields.INTERNACOES.toString(),"INTERNACAO", JoinType.INNER_JOIN);
		criteriaViewProfEsp.createAlias("INTERNACAO." + AinInternacao.Fields.LEITO.toString(),"LEITO", JoinType.INNER_JOIN);
		
		criteriaViewProfEsp.add(Restrictions.eq("INTERNACAO." + AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString(), serMatricula));
		criteriaViewProfEsp.add(Restrictions.eq("INTERNACAO." + AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString(), serVinCodigo));
		criteriaViewProfEsp.add(Restrictions.eq("INTERNACAO." + AinInternacao.Fields.ESP_SEQ.toString(), espSeq));
		criteriaViewProfEsp.add(Restrictions.eq("INTERNACAO." + AinInternacao.Fields.IND_PACIENTE_INTERNADO.toString(), true));
		
		criteriaViewProfEsp.add(Restrictions.eqProperty("INTERNACAO." + AinInternacao.Fields.SERVIDOR_PROFESSOR_VIN_CODIGO.toString(), 
				AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString()));
		criteriaViewProfEsp.add(Restrictions.eqProperty("INTERNACAO." + AinInternacao.Fields.SERVIDOR_PROFESSOR_MATRICULA.toString(), 
				AghProfEspecialidades.Fields.SER_MATRICULA.toString()));
		criteriaViewProfEsp.add(Restrictions.eqProperty("INTERNACAO." + AinInternacao.Fields.ESP_SEQ.toString(), 
				AghProfEspecialidades.Fields.ESP_SEQ.toString()));
		
		criteriaViewProfEsp.add(Restrictions.eq("LEITO." + AinLeitos.Fields.IND_PERTENCE_REFL.toString(), DominioSimNao.S));

		List<AghProfEspecialidades> listaInternados = executeCriteria(criteriaViewProfEsp);
		return listaInternados.size();
	}
	
	
	
	public List<Object[]> pesquisarEspCrmVOAmbulatorioEquipe(Object strPesquisa,
			AghEquipes equipe, AghEspecialidades especialidade) throws ApplicationBusinessException{
		
		DetachedCriteria cri = DetachedCriteria.forClass(AghProfEspecialidades.class, "prof");
		
		cri.setProjection(Projections.projectionList()
				.add(Projections.property("PF.nome"),"nomeMedico")
				.add(Projections.property("SERVIDOR.id.matricula"),"matricula")
				.add(Projections.property("SERVIDOR.id.vinCodigo"),"vinCodigo")
				.add(Projections.property("prof.aghEspecialidade.seq"), "espSeq"));
		
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "SERVIDOR", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString() 
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QF",JoinType.LEFT_OUTER_JOIN);
		cri.createAlias("SERVIDOR."+ RapServidores.Fields.EQUIPES.toString(), "EQP", JoinType.INNER_JOIN);
				
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			if (StringUtils.isNumeric(strPesquisa.toString())) {
				if (Integer.valueOf(strPesquisa.toString()) < Short.MAX_VALUE){
					cri.add(Restrictions.or(
							Restrictions.eq("SERVIDOR.id.matricula", Integer.valueOf(strPesquisa.toString())),
							Restrictions.eq("SERVIDOR.id.vinCodigo", Short.valueOf(strPesquisa.toString()))));
				}else{
					cri.add(Restrictions.eq("SERVIDOR.id.matricula", Integer.valueOf(strPesquisa.toString())));
				}
			} else {
				cri.add(Restrictions.ilike("PF." + RapPessoasFisicas.Fields.NOME.toString(),strPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}
		
		cri.add(Restrictions.eq("EQP."+AghEquipes.Fields.CODIGO.toString(), equipe.getSeq()));
		cri.add(Restrictions.or(Restrictions.eq("SERVIDOR.indSituacao",DominioSituacaoVinculo.A), 
						Restrictions.and(Restrictions.eq("SERVIDOR.indSituacao",DominioSituacaoVinculo.P), 
								Restrictions.ge("SERVIDOR.dtFimVinculo", Calendar.getInstance().getTime()))));
		
		cri.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), true));
		cri.add(Restrictions.eq(AghProfEspecialidades.Fields.ID_ESPECIALIDADE_SEQ.toString(),especialidade.getSeq()));
		
		return executeCriteria(cri);
		
	}
	
	public List<Object[]> pesquisarEspCrmVOAmbulatorioEspecialidade(Object strPesquisa, AghEspecialidades especialidade) throws ApplicationBusinessException{
		DetachedCriteria cri = DetachedCriteria.forClass(AghProfEspecialidades.class, "prof");
		
		cri.setProjection(Projections.projectionList()
				.add(Projections.property("PF.nome"),"nomeMedico")
				.add(Projections.property("SERVIDOR.id.matricula"),"matricula")
				.add(Projections.property("SERVIDOR.id.vinCodigo"),"vinCodigo")
				.add(Projections.property("prof.aghEspecialidade.seq"), "espSeq"));
		
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "SERVIDOR", JoinType.INNER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString(), "PF", JoinType.LEFT_OUTER_JOIN);
		cri.createAlias(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString() + "." + RapServidores.Fields.PESSOA_FISICA.toString() 
				+ "." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "QF",JoinType.LEFT_OUTER_JOIN);
				
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			if (StringUtils.isNumeric(strPesquisa.toString())) {
				if (Integer.valueOf(strPesquisa.toString()) < Short.MAX_VALUE){
					cri.add(Restrictions.or(
							Restrictions.eq("SERVIDOR.id.matricula", Integer.valueOf(strPesquisa.toString())),
							Restrictions.eq("SERVIDOR.id.vinCodigo", Short.valueOf(strPesquisa.toString()))));
				}else{
					cri.add(Restrictions.eq("SERVIDOR.id.matricula", Integer.valueOf(strPesquisa.toString())));
				}
			} else {
				cri.add(Restrictions.ilike("PF." + RapPessoasFisicas.Fields.NOME.toString(),strPesquisa.toString(), MatchMode.ANYWHERE));
			}
		}
		cri.add(Restrictions.or(Restrictions.eq("SERVIDOR.indSituacao",DominioSituacaoVinculo.A), 
						Restrictions.and(Restrictions.eq("SERVIDOR.indSituacao",DominioSituacaoVinculo.P), 
								Restrictions.ge("SERVIDOR.dtFimVinculo", Calendar.getInstance().getTime()))));
		
		cri.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_ATUA_AMBT.toString(), true));
		cri.add(Restrictions.eq(AghProfEspecialidades.Fields.ID_ESPECIALIDADE_SEQ.toString(),especialidade.getSeq()));
		
		return executeCriteria(cri);
		
	}
	
	public List<AghProfEspecialidades> pesquisarEspecialidadeMesmaEspecialidadeCirurgia(final Integer crgSeq, final Short crgEspMae) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class, "pre");
		criteria.createAlias("pre." + AghProfEspecialidades.Fields.ESPECIALIDADE.toString(), "esp");
		Criterion lhs = Restrictions.eq("esp." + AghEspecialidades.Fields.SEQ.toString(), crgEspMae);
		Criterion rhs = Restrictions.eq("esp." + AghEspecialidades.Fields.ESP_SEQ.toString(), crgEspMae);
		criteria.add(Restrictions.or(lhs, rhs));
		
		DetachedCriteria subQueryPpg = DetachedCriteria.forClass(MbcProfCirurgias.class, "ppg");
		subQueryPpg.setProjection(Projections.property("ppg." + MbcProfCirurgias.Fields.CRG_SEQ.toString()));
		subQueryPpg.add(Restrictions.eq("ppg." + MbcProfCirurgias.Fields.CRG_SEQ.toString(), crgSeq));
		subQueryPpg.add(Restrictions.eq("ppg." + MbcProfCirurgias.Fields.IND_RESPONSAVEL.toString(), Boolean.TRUE));
		subQueryPpg.add(Property.forName("ppg." + MbcProfCirurgias.Fields.PUC_SER_MATRICULA.toString()).eqProperty("pre." +  AghProfEspecialidades.Fields.SER_MATRICULA.toString()));
		subQueryPpg.add(Property.forName("ppg." + MbcProfCirurgias.Fields.PUC_SER_VIN_CODIGO.toString()).eqProperty("pre." +  AghProfEspecialidades.Fields.SER_VIN_CODIGO.toString()));

		criteria.add(Subqueries.exists(subQueryPpg));
		
		return executeCriteria(criteria);
	}
	
	public List<AghProfEspecialidades> listarProfEspecialidadesPorServidor(RapServidores servidor) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class, "prf");
		criteria.createAlias("prf." + AghProfEspecialidades.Fields.ESPECIALIDADE.toString() , "esp");
		criteria.createAlias("prf." + AghProfEspecialidades.Fields.SERVIDOR_DIGITADOR.toString(), "srd", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("srd." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes");
		criteria.add(Restrictions.eq("prf." + AghProfEspecialidades.Fields.SERVIDOR.toString(), servidor));
		
		criteria.addOrder(Order.asc("esp." + AghEspecialidades.Fields.SIGLA.toString()));
		return executeCriteria(criteria);
	}

	
	public String pesquisarNroRegConselho(Integer matricula, Short vinCodigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class, "pre");
		criteria.createAlias("pre." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "ser", JoinType.INNER_JOIN);
		criteria.createAlias("ser." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.INNER_JOIN);
		criteria.createAlias("pes." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "qlf", JoinType.INNER_JOIN);
		criteria.createAlias("qlf." + RapQualificacao.Fields.TIPO_QUALIFICACAO.toString(), "tql", JoinType.INNER_JOIN);
		criteria.createAlias("tql." + RapTipoQualificacao.Fields.CONSELHO_PROFISSIONAL.toString(), "cpr", JoinType.INNER_JOIN);
		criteria.setProjection(Projections.property("qlf." + RapQualificacao.Fields.NRO_REGISTRO_CONSELHO.toString()));
		criteria.add(Restrictions.eq("pre." + AghProfEspecialidades.Fields.IND_INTERNA.toString(), DominioSimNao.S));
		criteria.add(Restrictions.eq("ser." + RapServidores.Fields.MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq("ser." + RapServidores.Fields.VIN_CODIGO.toString(), vinCodigo));
		
		List<String> list = new ArrayList<String>();
		list = executeCriteria(criteria);
		if(list == null || list.isEmpty()){
			return null;
		}
		return list.get(0);
	}
	
	public List<AghProfEspecialidades> listarProfEspecialidadesPorEspSeq(Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class);
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.eq(AghProfEspecialidades.Fields.IND_ATUA_INTERNACAO.toString(), DominioSimNao.S));
		return executeCriteria(criteria);
	}
	
	public AghProfEspecialidades obterProfEspecialidadeComServidorAtivoProgramado(AghProfEspecialidadesId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghProfEspecialidades.class, "PRE");
		criteria.createAlias("PRE." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("PRE." + AghProfEspecialidades.Fields.ID.toString(), id));
		criteria.add(Restrictions.in("SER." + RapServidores.Fields.IND_SITUACAO.toString(),
				new DominioSituacaoVinculo[]{DominioSituacaoVinculo.A, DominioSituacaoVinculo.P}));
		return (AghProfEspecialidades) executeCriteriaUniqueResult(criteria);
	}
}
