package br.gov.mec.aghu.internacao.business;

import java.util.ArrayList;
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
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.business.vo.EscalaIntenacaoVO;
import br.gov.mec.aghu.internacao.business.vo.ProfissionaisEscalaIntenacaoVO;
import br.gov.mec.aghu.internacao.dao.AinEscalasProfissionalIntDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinEscalasProfissionalIntId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * @author rpetter
 * 
 */
@Stateless
public class EscalaProfissionaisInternacaoON extends BaseBusiness {


@EJB
private EscalaProfissionaisInternacaoRN escalaProfissionaisInternacaoRN;

private static final Log LOG = LogFactory.getLog(EscalaProfissionaisInternacaoON.class);

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
private IServidorLogadoFacade servidorLogadoFacade;

@Inject
private AinEscalasProfissionalIntDAO ainEscalasProfissionalIntDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7232274476302807366L;

	protected enum EscalaProfissionaisInternacaoONExceptionCode implements
			BusinessExceptionCode {
		/**
		 * Informe a data de fim da escala antes de informar o profissional
		 * substituto.
		 */
		MENSAGEM_INFORME_DATA_FIM_ANTES_SUBSTITUTO, MENSAGEM_SERVIDOR_NAO_ENCONTRADO, MENSAGEM_PERIODO_ENCERRADO_ALTERACAO_ESCALA, MENSAGEM_DATA_INICIO_OBRIGATORIA, //
		/**
		 * Data de fim deve ser maior ou igual a data de início.
		 */
		AIN00319;

	}

	public List<ProfissionaisEscalaIntenacaoVO> pesquisarProfissionaisEscala(
			Short vinculo, Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade,
			Short codigoConvenio, String descricaoConvenio,
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) throws ApplicationBusinessException {

		List<ProfissionaisEscalaIntenacaoVO> profissionaisEscala = getEscalaProfissionaisInternacaoRN()
				.pesquisarProfissionaisEscala(vinculo, matricula,
						conselhoProfissional, nomeServidor, siglaEspecialidade,
						codigoConvenio, descricaoConvenio, firstResult,
						maxResult, orderProperty, asc);
		return profissionaisEscala;
	}

	public Long pesquisarProfissionaisEscalaCount(Short vinculo,
			Integer matricula, String conselhoProfissional,
			String nomeServidor, String siglaEspecialidade,
			Short codigoConvenio, String descricaoConvenio)
			throws ApplicationBusinessException {
		return getEscalaProfissionaisInternacaoRN()
				.pesquisarProfissionaisEscalaCount(vinculo, matricula,
						conselhoProfissional, nomeServidor, siglaEspecialidade,
						codigoConvenio, descricaoConvenio);
	}

	public Long pesquisarEscalaCount(Short vinculo, Integer matricula, Integer seqEspecialidade, Short codigoConvenio,
			Date dataInicio, Date dataFim) {

		return getAinEscalasProfissionalIntDAO().pesquisarEscalaCount(vinculo, matricula, seqEspecialidade, codigoConvenio,
				dataInicio, dataFim);
	}

	/*
	 * Pesquisas da parte 2 - Pesquisa escala do profissional
	 */
	@Secure("#{s:hasPermission('escalaProfissionais','pesquisar')}")
	public List<AinEscalasProfissionalInt> pesquisarEscala(Short vinculo,
			Integer matricula, Integer seqEspecialidade, Short codigoConvenio,
			Date dataInicio, Date dataFim, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		return getEscalaProfissionaisInternacaoRN().pesquisarEscala(vinculo,
				matricula, seqEspecialidade, codigoConvenio, dataInicio,
				dataFim, firstResult, maxResult, orderProperty, asc);

	}

	public List<EscalaIntenacaoVO> montarEscalaVO(Short vinculo,
			Integer matricula, Integer seqEspecialidade, Short codigoConvenio,
			Date dataInicio, Date dataFim, Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc)
			throws ApplicationBusinessException {

		List<EscalaIntenacaoVO> result = new ArrayList<EscalaIntenacaoVO>();

		List<AinEscalasProfissionalInt> listaEscala = pesquisarEscala(vinculo,
				matricula, seqEspecialidade, codigoConvenio, dataInicio,
				dataFim, firstResult, maxResult, orderProperty, asc);

		for (AinEscalasProfissionalInt escala : listaEscala) {
			EscalaIntenacaoVO novo = new EscalaIntenacaoVO();
			// se tem substituto busca o nome e crm
			if (escala.getServidorProfessor() != null
					&& escala.getServidorProfessor().getId() != null
					&& escala.getServidorProfessor().getId().getMatricula() != null
					&& escala.getServidorProfessor().getId().getVinCodigo() != null) {

				novo.setMatriculaServidorSubstituto(escala
						.getServidorProfessor().getId().getMatricula());
				novo.setVinculoServidorSubstituto(escala.getServidorProfessor()
						.getId().getVinCodigo());
				novo.setNomeServidorSubstituto(escala.getServidorProfessor()
						.getPessoaFisica().getNome());
				novo
						.setNumeroRegistroSubstituto(getEscalaProfissionaisInternacaoRN()
								.buscaNumeroRegistro(escala
										.getServidorProfessor()
										.getPessoaFisica().getCodigo()));
			}
			if (escala.getIndAtuaCti().equals("S")) {
				novo.setIndCTI(DominioSimNao.S);
			} else {
				novo.setIndCTI(DominioSimNao.N);
			}
			novo.setDataInicio(escala.getId().getDtInicio());
			novo.setDataFim(escala.getDtFim());
			result.add(novo);
		}

		return result;
	}

	/**
	 * Método utilizado para adicionar uma nova escala de um profissional da
	 * internação.
	 * 
	 * @param ainEscalasProfissionalInt
	 * @throws ApplicationBusinessException
	 */
	public void incluirEscala(AinEscalasProfissionalInt ainEscalasProfissionalInt) throws BaseException {
		ainEscalasProfissionalInt.setServidorProfessorDigitada(getServidorLogadoFacade().obterServidorLogado());
		this.evict(ainEscalasProfissionalInt);

		this.validar(ainEscalasProfissionalInt);
		getEscalaProfissionaisInternacaoRN().validar(ainEscalasProfissionalInt, false);
		AinEscalasProfissionalIntDAO ainEscalasProfissionalIntDAO = this.getAinEscalasProfissionalIntDAO();
		ainEscalasProfissionalIntDAO.persistir(ainEscalasProfissionalInt);
		ainEscalasProfissionalIntDAO.flush();
	}

	/**
	 * Método utilizado para alterar uma escala de um profissional da
	 * internação.
	 * 
	 * @param ainEscalasProfissionalInt
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public void alterarEscala(AinEscalasProfissionalInt ainEscalasProfissionalInt) throws BaseException {
		ainEscalasProfissionalInt.setServidorProfessorDigitada(getServidorLogadoFacade().obterServidorLogado());
		this.evict(ainEscalasProfissionalInt);

		this.validar(ainEscalasProfissionalInt);
		getEscalaProfissionaisInternacaoRN().validar(ainEscalasProfissionalInt, true);
		
		AinEscalasProfissionalIntDAO ainEscalasProfissionalIntDAO = this.getAinEscalasProfissionalIntDAO();
		ainEscalasProfissionalInt = ainEscalasProfissionalIntDAO.atualizar(ainEscalasProfissionalInt);
		ainEscalasProfissionalIntDAO.flush();
		ainEscalasProfissionalInt = this.getAinEscalasProfissionalIntDAO().merge(ainEscalasProfissionalInt);
		ainEscalasProfissionalInt = this.getAinEscalasProfissionalIntDAO().atualizar(ainEscalasProfissionalInt);

	}

	private void validar(AinEscalasProfissionalInt escala) throws BaseException {
		if (escala == null || escala.getId() == null) {
			throw new IllegalArgumentException("Argumento obrigatório");
		}

		if (escala.getId().getDtInicio() == null) {
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoONExceptionCode.MENSAGEM_DATA_INICIO_OBRIGATORIA);
		}

		if (escala.getDtFim() != null
				&& escala.getDtFim().before(escala.getId().getDtInicio())) {
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoONExceptionCode.AIN00319);
		}
		escala.setServidorProfessor(this.validaAssociacao(escala
				.getServidorProfessor()));

		if (escala.getDtFim() == null && escala.getServidorProfessor() != null) {
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoONExceptionCode.MENSAGEM_INFORME_DATA_FIM_ANTES_SUBSTITUTO);
		}
		
		validarDataFim(escala);
	}

	/**
	 * Valida se a data de fim foi modificada de forma inválida.
	 * #5015 - Não deixar o usuário alterar uma data de fim se a hora atua é posterior á data de fim da escala.
	 * @param escala Escala atual digitadana tela.
	 * @throws ApplicationBusinessException
	 */
	 
	protected void validarDataFim(AinEscalasProfissionalInt escala) throws ApplicationBusinessException {
		AinEscalasProfissionalInt old = getEscalaProfissionaisInternacaoRN().obterEscala(escala.getId());
		
		//Se a data foi modificada, e a data atual é maior que a data de fim da escala, estoura erro. 
		if (old != null && old.getDtFim() != null && escala.getDtFim() != null
				&& !(DateUtil.truncaData(old.getDtFim()).equals(DateUtil.truncaData(escala.getDtFim())))
				&& DateUtil.truncaData(Calendar.getInstance().getTime()).after(DateUtil.truncaData(escala.getDtFim()))) {
			throw new ApplicationBusinessException(EscalaProfissionaisInternacaoONExceptionCode.MENSAGEM_PERIODO_ENCERRADO_ALTERACAO_ESCALA);
		}
	}

	protected EscalaProfissionaisInternacaoRN getEscalaProfissionaisInternacaoRN() {
		return escalaProfissionaisInternacaoRN;
	}

	/**
	 * Valida a associação com o servidor substituto.
	 * 
	 * @param atendimento
	 * @return
	 * @throws ApplicationBusinessException
	 *             se não encontrado com am chave fornecida
	 */
	private RapServidores validaAssociacao(RapServidores servidorProfessor)
			throws ApplicationBusinessException {
		// se não informado ou chave incompleta
		if (servidorProfessor == null || servidorProfessor.getId() == null
				|| servidorProfessor.getId().getMatricula() == null
				|| servidorProfessor.getId().getVinCodigo() == null) {
			return null;
		}
		servidorProfessor = this.getRegistroColaboradorFacade().obterServidor(servidorProfessor.getId());
		if (servidorProfessor == null) {
			throw new ApplicationBusinessException(
					EscalaProfissionaisInternacaoONExceptionCode.MENSAGEM_SERVIDOR_NAO_ENCONTRADO);
		}

		return servidorProfessor;
	}

	/**
	 * Procura profissional substituto para os parâmetros fornecidos.<br>
	 * Substitutos são os profissionais que tem escala para a especialidade e
	 * convênio no dia fornecido.
	 * 
	 * @param especialidadeId
	 *            id da especialidade
	 * @param convenioId
	 *            id do convênio
	 * @param data
	 * @return matriculas(RapServidores) dos profissionais substitutos
	 * @throws ApplicationBusinessException
	 */
	public List<RapServidores> pesquisarProfissionaisSubstitutos(Short especialidadeId, Short convenioId, Date data,
			Object substitutoPesquisaLOV) throws ApplicationBusinessException {
		return getRegistroColaboradorFacade().pesquisarProfissionaisSubstitutos(especialidadeId, convenioId, data,
				substitutoPesquisaLOV, this.getMedicinaId(), this.getOdontologiaId());
	}

	private Integer getOdontologiaId() throws ApplicationBusinessException {
		AghParametros param = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_ODONTO);
		return param.getVlrNumerico().intValue();
	}

	/**
	 * Método utilizado para obter uma instância de AinEscalasProfissionalInt
	 * pela chave primária composta.
	 * 
	 * @param pecPreSerMatricula
	 * @param pecPreSerVinCodigo
	 * @param pecCnvCodigo
	 * @param pecPreEspSeq
	 * @param dtInicio
	 * @return
	 */
	public AinEscalasProfissionalInt obterProfissionalEscala(
			Integer pecPreSerMatricula, Integer pecPreSerVinCodigo,
			Integer pecCnvCodigo, Integer pecPreEspSeq, Date dtInicio) {

		AinEscalasProfissionalIntId id = new AinEscalasProfissionalIntId();

		id.setPecPreSerMatricula(pecPreSerMatricula);
		id.setPecPreSerVinCodigo(pecPreSerVinCodigo.shortValue());
		id.setPecCnvCodigo(pecCnvCodigo.shortValue());
		id.setPecPreEspSeq(pecPreEspSeq.shortValue());
		id.setDtInicio(dtInicio);

		return this.getAinEscalasProfissionalIntDAO().obterPorChavePrimaria(id);

	}

	private Integer getMedicinaId() throws ApplicationBusinessException {
		AghParametros param = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TIPO_QLF_MEDICINA);
		return param.getVlrNumerico().intValue();
	}
	
    public List<AinEscalasProfissionalInt> pesquisarProfissionalEscala(
    		Integer pecPreSerMatricula, Short pecPreSerVinCodigo, Short pecCnvCodigo, Short pecPreEspSeq) {
    	return getAinEscalasProfissionalIntDAO().pesquisarEscalaProfissionalInt(pecPreSerMatricula, pecPreSerVinCodigo, pecPreEspSeq, pecCnvCodigo);
    }

	protected AinEscalasProfissionalIntDAO getAinEscalasProfissionalIntDAO() {
		return ainEscalasProfissionalIntDAO;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
