package br.gov.mec.aghu.internacao.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.business.vo.ProfissionaisEscalaIntenacaoVO;
import br.gov.mec.aghu.internacao.dao.AinEscalasProfissionalIntDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinEscalasProfissionalIntId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class EscalaProfissionaisInternacaoRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(EscalaProfissionaisInternacaoRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IInternacaoFacade internacaoFacade;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AinEscalasProfissionalIntDAO ainEscalasProfissionalIntDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -3637024344953828774L;

	private enum EscalaProfissionaisInternacaoRNExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_PARAMETRO_OBRIGATORIO, MENSAGEM_DATA_INICIO_OBRIGATORIA, MENSAGEM_PARAMETRO_MEDICINA_NAO_ENCONTRADO, //
		MENSAGEM_PARAMETRO_ODONTO_NAO_ENCONTRADO, MENSAGEM_SUBSTITUTO_ESCALA_VALIDA, AIN00319, AIN00320, AIN00321, AIN00323, //
		/**
		 * Existe período de escala para este profissional, especialidade,
		 * convênio sem data de término informada.
		 */
		AIN00323DATAFIM, //
		AIN00324, AIN00436, AIN00437, AIN00035, AIN00585, AIN00037, AIN00038, //
		MENSAGEM_PROFISSIONAL_ESCALA_DATA_FIM, SOBREPOSICAO_DATA_TERMINO_MAIOR;
	}

	/**
	 * Método utilizado para validar inclusão ou edicão da escala de um
	 * profissional da internação e/ou seu substituto.
	 * 
	 * @param ainEscalasProfissionalInt
	 * @param isInclusao
	 * @throws ApplicationBusinessException
	 */
	public void validar(AinEscalasProfissionalInt ainEscalasProfissionalInt,
			boolean isAlteracao) throws ApplicationBusinessException {

		// RN14
		this.validaDominioindAtuaCti(ainEscalasProfissionalInt);
		// RN19
		this.validaProfissionalLogado(ainEscalasProfissionalInt);

		// Inclusao
		if (!isAlteracao) {
			// RN11
			this.validaDataInicioEscala(ainEscalasProfissionalInt);
			// RN13
			this.validaEscalaProfissionalInternacao(ainEscalasProfissionalInt);

			// Inclui nova escala somente se não tiver período em aberto
			this.validaDataFimNula(ainEscalasProfissionalInt);
		}

		// RN10
		if (isAlteracao) {
			evict(ainEscalasProfissionalInt);

			AinEscalasProfissionalInt old = obterEscala(ainEscalasProfissionalInt
					.getId());

			// verifica se data de fim da escala foi alterada
			if (ainEscalasProfissionalInt.getDtFim() != null
					&& (old == null || !ainEscalasProfissionalInt.getDtFim().equals(
							old.getDtFim()))) {
				this.validaSobreposicao(ainEscalasProfissionalInt, true);
			} else {
				// verifica se data de fim da escala foi apagada
				if (old == null || ainEscalasProfissionalInt.getDtFim() == null
						&& old.getDtFim() != null) {
					this.validaSobreposicao(ainEscalasProfissionalInt, true);
				}
			}
			evict(old);

		} else {
			this.validaSobreposicao(ainEscalasProfissionalInt, false);
		}

		// Substituto
		if (ainEscalasProfissionalInt.getServidorProfessor() != null
				&& ainEscalasProfissionalInt.getServidorProfessor().getId() != null) {

			// RN09, RN15, RN17, RN18
			this.validaSubstituto(ainEscalasProfissionalInt);

		}

		// RN12
		if (isAlteracao) {
			validaAlteracaoSubstitutoVigencia(ainEscalasProfissionalInt);
		}

	}

	private void validaDataInicioEscala(
			AinEscalasProfissionalInt ainEscalasProfissionalInt)
			throws ApplicationBusinessException {

		if (ainEscalasProfissionalInt.getId().getDtInicio().before(
				DateUtil.obterDataComHoraInical(null))) {
			// AIN00321=A data de início deve ser maior ou igual a data de hoje.
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoRNExceptionCode.AIN00321);
		}
	}

	/**
	 * Verifica se existe escala sem data de fim, evitando inclusão de nova se
	 * existir.
	 * 
	 * @param ainEscalasProfissionalInt
	 * @throws ApplicationBusinessException
	 */
	public void validaDataFimNula(
			AinEscalasProfissionalInt ainEscalasProfissionalInt)
			throws ApplicationBusinessException {

		List<AinEscalasProfissionalInt> lista = pesquisarEscala(
				ainEscalasProfissionalInt.getId().getPecPreSerVinCodigo(),
				ainEscalasProfissionalInt.getId().getPecPreSerMatricula(),
				ainEscalasProfissionalInt.getId().getPecPreEspSeq().intValue(),
				ainEscalasProfissionalInt.getId().getPecCnvCodigo(), null,
				null, 0, 1000, null, false);

		for (AinEscalasProfissionalInt escala : lista) {
			if (escala.getDtFim() == null) {
				throw new ApplicationBusinessException(
						EscalaProfissionaisInternacaoRNExceptionCode.AIN00323DATAFIM);
			}
		}
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void validaSobreposicao(
			AinEscalasProfissionalInt ainEscalasProfissionalInt,
			boolean alteracao) throws ApplicationBusinessException {

		List<AinEscalasProfissionalInt> lista = pesquisarEscala(
				ainEscalasProfissionalInt.getId().getPecPreSerVinCodigo(),
				ainEscalasProfissionalInt.getId().getPecPreSerMatricula(),
				ainEscalasProfissionalInt.getId().getPecPreEspSeq().intValue(),
				ainEscalasProfissionalInt.getId().getPecCnvCodigo(), null,
				null, 0, 1000, null, false);

		Date ins_dt_ini = ainEscalasProfissionalInt.getId().getDtInicio();
		Date ins_dt_fim = ainEscalasProfissionalInt.getDtFim();

		Date dt_ini_db;
		Date dt_fim_db;

		boolean sobrepos = false;

		for (AinEscalasProfissionalInt escala : lista) {
			// se é a propria que esta sendo alterada
			if (alteracao && escala.equals(ainEscalasProfissionalInt)) {
				continue;
			}

			dt_ini_db = escala.getId().getDtInicio();
			dt_fim_db = escala.getDtFim();

			// Caso seja nula a data fim inserida anteriormente.
			if (alteracao && dt_fim_db == null) {
				Calendar calendar = Calendar.getInstance();
				calendar.set(9999, Calendar.JANUARY, 1);
				dt_fim_db = calendar.getTime();
				// continue;
			}

			if (!alteracao && (ins_dt_ini.compareTo(dt_ini_db) == 0)) {
				sobrepos = true;
			}

			// Validações com data de fim diferente de nulo
			if (ins_dt_fim != null) {

				if ((ins_dt_ini.compareTo(dt_fim_db) == 0)
						|| (ins_dt_fim.compareTo(dt_ini_db) == 0)
						|| (ins_dt_fim.compareTo(dt_fim_db) == 0)) {
					sobrepos = true;
				}

				if ((ins_dt_ini.compareTo(dt_ini_db) > 0)
						&& (ins_dt_fim.compareTo(dt_fim_db) < 0)) {
					sobrepos = true;
				}

				if ((ins_dt_fim.compareTo(dt_ini_db) > 0)
						&& (ins_dt_fim.compareTo(dt_fim_db) <= 0)) {
					sobrepos = true;
				}

			}

			if ((ins_dt_ini.compareTo(dt_ini_db) > 0)
					&& (ins_dt_ini.compareTo(dt_fim_db) <= 0)) {
				sobrepos = true;
			}

			if (!alteracao && (ins_dt_ini.compareTo(dt_fim_db) < 0)) {
				throw new ApplicationBusinessException(
						EscalaProfissionaisInternacaoRNExceptionCode.SOBREPOSICAO_DATA_TERMINO_MAIOR);
			}

			if (sobrepos) {
				// AIN00323=Existe período de escala sobreposto para este
				// profissional,especialidade,convênio
				throw new ApplicationBusinessException(
						EscalaProfissionaisInternacaoRNExceptionCode.AIN00323);
			}

		}
	}

	private void validaSubstituto(
			AinEscalasProfissionalInt ainEscalasProfissionalInt)
			throws ApplicationBusinessException {

		// validações migradas para a ON cvagheti 23/11/2010
		// // RN08
		// // Verifica se o profissional da escala informou a data de fim da
		// escala
		// if (ainEscalasProfissionalInt.getDtFim() == null) {
		// throw new ApplicationBusinessException(
		// EscalaProfissionaisInternacaoRNExceptionCode.MENSAGEM_PROFISSIONAL_ESCALA_DATA_FIM);
		// }
		//
		// // RN18
		// RapServidores servidor = this.pesquisaServidor(
		// ainEscalasProfissionalInt.getServidorProfessor().getId()
		// .getVinCodigo(), ainEscalasProfissionalInt
		// .getServidorProfessor().getId().getMatricula());
		//
		// if (servidor == null || servidor.getId() == null) {
		// throw new ApplicationBusinessException(
		// EscalaProfissionaisInternacaoRNExceptionCode.AIN00037);
		// }

		// RN15
		// servidor substituo não pode ser o mesmo servidor da escala
		if (ainEscalasProfissionalInt.getServidorProfessor() != null
				&& ainEscalasProfissionalInt.getServidorProfessor().getId()
						.getMatricula() != null
				&& ainEscalasProfissionalInt.getId().getPecPreSerMatricula()
						.equals(
								ainEscalasProfissionalInt
										.getServidorProfessor().getId()
										.getMatricula())
				&& ainEscalasProfissionalInt.getId().getPecPreSerVinCodigo()
						.equals(
								ainEscalasProfissionalInt
										.getServidorProfessor().getId()
										.getVinCodigo())) {
			// AIN00320=O substituto indicado deve ser diferente do profissional
			// da escala
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoRNExceptionCode.AIN00320);
		}

		// RN09
		// servidor substituto deve existir, ser da internação e ter CRM
		List<ProfissionaisEscalaIntenacaoVO> substitutoVO = this
				.pesquisarProfissionaisEscala(ainEscalasProfissionalInt
						.getServidorProfessor().getId().getVinCodigo(),
						ainEscalasProfissionalInt.getServidorProfessor()
								.getId().getMatricula(), null, null, null,
						null, null, 0, 1, null, false);
		if (substitutoVO.size() == 0 || substitutoVO.get(0) == null) {
			// AIN00436=Profissional substituto deve estar ativo e possuir
			// registro no conselho de medicina (CRM) ou odontologia (CRO)
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoRNExceptionCode.AIN00436);
		}

		// RN17
		// deve ter escala vigente para o mesmo convenio e especialidade
		// busca lista de escalas do substituto
		List<AinEscalasProfissionalInt> lista = pesquisarEscala(
				ainEscalasProfissionalInt.getServidorProfessor().getId()
						.getVinCodigo(), ainEscalasProfissionalInt
						.getServidorProfessor().getId().getMatricula(),
				ainEscalasProfissionalInt.getId().getPecPreEspSeq().intValue(),
				ainEscalasProfissionalInt.getId().getPecCnvCodigo(), null,
				null, 0, 1000, null, false);
		boolean erro = true;

		// data do fim da escala do profissional da escala + 1
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(ainEscalasProfissionalInt.getDtFim());
		calendar.add(Calendar.DATE, 1);
		Date dataFim_maisUm = calendar.getTime();

		// percorre lista pa ver se existe data válida para a escala a ser
		// incluída
		for (AinEscalasProfissionalInt escala : lista) {
			// datadefimmaisum entre data de inicio e fim da escala ou
			// datadefimmaisum maior que data de inicio da escala e fim da
			// escala nulo
			if ((dataFim_maisUm.compareTo(escala.getId().getDtInicio()) >= 0)
					&& (escala.getDtFim() == null || dataFim_maisUm
							.compareTo(escala.getDtFim()) <= 0)) {
				erro = false;
				return;
			}
		}
		if (erro) {
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoRNExceptionCode.MENSAGEM_SUBSTITUTO_ESCALA_VALIDA);
		}
	}

	private void validaAlteracaoSubstitutoVigencia(
			AinEscalasProfissionalInt ainEscalasProfissionalInt)
			throws ApplicationBusinessException {

		// RN12
		// substituto não pode ser alterado qdo escala não vigente(fim<sysdate)
		if (ainEscalasProfissionalInt.getDtFim() != null
				&& ainEscalasProfissionalInt.getDtFim().before(
						DateUtil.obterDataComHoraInical(null))) {
			// retira do buffer
			evict(ainEscalasProfissionalInt);
			// busca o antigo q esta no banco
			AinEscalasProfissionalInt old = obterEscala(ainEscalasProfissionalInt
					.getId());
			// verifica se foi alterado
			if (CoreUtil.modificados(ainEscalasProfissionalInt
					.getServidorProfessor(), old.getServidorProfessor())) {
				// AIN00324=Não pode ser informado/modificado substituto para
				// período já encerrado
				throw new ApplicationBusinessException(
						EscalaProfissionaisInternacaoRNExceptionCode.AIN00324);
			}
		}
	}

	/**
	 * Retorna a qualificação pelo id fornecido.
	 * 
	 * @param id
	 * @return null se não encontrado
	 */
	public AinEscalasProfissionalInt obterEscala(
			AinEscalasProfissionalIntId id) {
		if (id == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		AinEscalasProfissionalInt escala = getAinEscalasProfissionalIntDAO().obterPorChavePrimaria(id);

		return escala;
	}

	public Integer[] buscaTipoQualificacao() throws ApplicationBusinessException {
		// Destinatários
		AghParametros qualificacaoMedicina = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_MEDICINA);
		if (qualificacaoMedicina == null
				|| qualificacaoMedicina.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoRNExceptionCode.MENSAGEM_PARAMETRO_MEDICINA_NAO_ENCONTRADO);
		}
		AghParametros qualificacaoOdonto = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_ODONTO);
		if (qualificacaoOdonto == null
				|| qualificacaoOdonto.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoRNExceptionCode.MENSAGEM_PARAMETRO_ODONTO_NAO_ENCONTRADO);
		}
		Integer[] tipoQualificacao = {
				qualificacaoOdonto.getVlrNumerico().intValue(),
				qualificacaoMedicina.getVlrNumerico().intValue() };
		return tipoQualificacao;
	}


	/**
	 * Retorna o número de registro no conselho profissional.
	 * 
	 * @param pessoa
	 *            id da pessoa física
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String buscaNumeroRegistro(Integer pessoa) throws ApplicationBusinessException {
		Integer[] tiposQualificacao = this.buscaTipoQualificacao();
		return getRegistroColaboradorFacade().buscaNumeroRegistro(pessoa, tiposQualificacao);
	}

	public List<ProfissionaisEscalaIntenacaoVO> pesquisarProfissionaisEscala(Short vinculo, Integer matricula,
			String conselhoProfissional, String nomeServidor, String siglaEspecialidade, Short codigoConvenio,
			String descricaoConvenio, Integer firstResult, Integer maxResult, String orderProperty, boolean asc)
			throws ApplicationBusinessException {

		Integer[] tiposQualificacao = this.buscaTipoQualificacao();

		return getAghuFacade().pesquisarProfissionaisEscala(vinculo, matricula, conselhoProfissional, nomeServidor,
				siglaEspecialidade, codigoConvenio, descricaoConvenio, tiposQualificacao, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarProfissionaisEscalaCount(Short vinculo, Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade, Short codigoConvenio, String descricaoConvenio)
			throws ApplicationBusinessException {

		Integer[] tiposQualificacao = this.buscaTipoQualificacao();

		return getAghuFacade().pesquisarProfissionaisEscalaCount(vinculo, matricula, conselhoProfissional, nomeServidor,
				siglaEspecialidade, codigoConvenio, descricaoConvenio, tiposQualificacao);
	}

	public List<AinEscalasProfissionalInt> pesquisarEscala(Short vinculo, Integer matricula, Integer seqEspecialidade,
			Short codigoConvenio, Date dataInicio, Date dataFim, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {

		return getAinEscalasProfissionalIntDAO().pesquisarEscala(vinculo, matricula, seqEspecialidade, codigoConvenio, dataInicio,
				dataFim, firstResult, maxResult, orderProperty, asc);
	}
	
	private void validaEscalaProfissionalInternacao(
			AinEscalasProfissionalInt ainEscalasProfissionalInt)
			throws ApplicationBusinessException {

		AinEscalasProfissionalInt escalasProfInt = obterEscala(ainEscalasProfissionalInt
				.getId());

		if (escalasProfInt != null) {
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoRNExceptionCode.AIN00035);
		}
	}

	private void validaDominioindAtuaCti(
			AinEscalasProfissionalInt ainEscalasProfissionalInt)
			throws ApplicationBusinessException {

		if (!ainEscalasProfissionalInt.getIndAtuaCti().equals(
				DominioSimNao.S.name())
				&& !ainEscalasProfissionalInt.getIndAtuaCti().equals(
						DominioSimNao.N.name())) {

			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoRNExceptionCode.AIN00585);
		}
	}

	private void validaProfissionalLogado(
			AinEscalasProfissionalInt ainEscalasProfissionalInt)
			throws ApplicationBusinessException {

		RapServidores servidor = this.pesquisaServidor(
				ainEscalasProfissionalInt.getServidorProfessorDigitada()
						.getId().getVinCodigo(), ainEscalasProfissionalInt
						.getServidorProfessorDigitada().getId().getMatricula());

		if (servidor == null || servidor.getId() == null) {
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoRNExceptionCode.AIN00038);
		}
	}

	private RapServidores pesquisaServidor(Short vinculo, Integer matricula) throws ApplicationBusinessException {
		return getRegistroColaboradorFacade().obterServidor(vinculo, matricula);
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
		
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AinEscalasProfissionalIntDAO getAinEscalasProfissionalIntDAO(){
		return ainEscalasProfissionalIntDAO;
	}	
}
