package br.gov.mec.aghu.registrocolaborador.business;

import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.EJBTransactionRolledbackException;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.validation.ConstraintViolationException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.dao.PerfisUsuariosDAO;
import br.gov.mec.aghu.dominio.DominioSituacaoVinculo;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapChefias;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.prescricaomedica.vo.ConselhoProfissionalServidorVO;
import br.gov.mec.aghu.registrocolaborador.dao.RapChefiasDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapPessoasFisicasDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.vo.RapServidoresVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseOptimisticLockException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@SuppressWarnings("PMD.AghuTooManyMethods")
@Stateless
public class ServidorON extends BaseBusiness {


	@EJB
	private ServidorRN servidorRN;
	
	@EJB
	private IAghuFacade aghuFacade;
		
	@EJB
	private IRegistroColaboradorFacade registroColaboradoFacade;
	
	@Inject
	private PerfisUsuariosDAO perfisUsuariosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private static final Log LOG = LogFactory.getLog(ServidorON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private RapChefiasDAO rapChefiasDAO;
	
	@Inject
	private RapServidoresDAO rapServidoresDAO;
	
	@Inject
	private RapPessoasFisicasDAO rapPessoasFisicasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8063085006645360887L;

	public enum ServidorONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_SERVIDOR_NAO_INFORMADO, MENSAGEM_VINCULO_INVALIDO, MENSAGEM_PESSOA_FISICA_INVALIDA, MENSAGEM_CENTRO_CUSTO_LOTACAO_INVALIDO, MENSAGEM_CENTRO_CUSTO_ATUACAO_INVALIDO, MENSAGEM_OCUPACAO_INVALIDA, CHEFIA_DATA_INICIO_NULA,EMAIL_INVALIDO;
	}
	
	/**
	 * Retorna true se o servidor estiver com vinculo ativo na data fornecida.
	 * 
	 * @param servidor
	 * @param aData
	 * @return
	 */
	public boolean isAtivo(RapServidores servidor, java.util.Date aData) {
		if (DominioSituacaoVinculo.A.equals(servidor.getIndSituacao())) {
			return true;
		}
		if (DominioSituacaoVinculo.P.equals(servidor.getIndSituacao())
				&& aData.after(servidor.getDtFimVinculo())) {
			return true;
		}
		return false;
	}

	/**
	 * Retorna true se o servidor estiver com vinculo ativo na data corrente.
	 * 
	 * @param servidor
	 * @return
	 */
	public boolean isAtivo(RapServidores servidor) {
		java.util.Date hoje = Calendar.getInstance().getTime();
		return this.isAtivo(servidor, hoje);
	}

	public void inserir(RapServidores servidor) throws ApplicationBusinessException {
		try{
			
			if (servidor == null) {
				throw new ApplicationBusinessException(ServidorONExceptionCode.MENSAGEM_SERVIDOR_NAO_INFORMADO);
			}

			ServidorRN servidorRN = getServidorRN();
			servidorRN.validarInclusaoServidores(servidor);
			servidorRN.inserirServidor(servidor);
			
		} catch(EJBTransactionRolledbackException cde){
			if (StringUtils.containsIgnoreCase(((ConstraintViolationException) cde.getCause()).getLocalizedMessage(), "EMAIL")) {
				throw new ApplicationBusinessException(ServidorONExceptionCode.EMAIL_INVALIDO);
			}
			throw cde;
		}
	}

	/**
	 * Quando for HCPA, carregar a descrição da ocupação cadastrada nas tabelas da STARH
	 */
	public String obterDescricaoOcupacaoTabelaSTARH(Integer codigoOcupacaoCargo) throws ApplicationBusinessException{
		if (isHCPA()) {
			return aghuFacade.buscarDescricaoOcupacao(codigoOcupacaoCargo); 
		}
		return null;
	}

	public void alterar(RapServidores servidor) throws ApplicationBusinessException {
		try{
			if (servidor == null) {
				throw new ApplicationBusinessException(ServidorONExceptionCode.MENSAGEM_SERVIDOR_NAO_INFORMADO);
			}
	
			RapServidores servidorOriginal = obterServidor(servidor.getId());
			servidorRN.validarAtualizacaoServidores(servidor, servidorOriginal);
			servidorRN.atualizarServidor(servidor, servidorOriginal);
			
		} catch(EJBTransactionRolledbackException cde){
			if(cde.getCausedByException() instanceof BaseOptimisticLockException){
				throw (BaseOptimisticLockException) cde.getCausedByException();
			}
			
			if (StringUtils.containsIgnoreCase(((ConstraintViolationException) cde.getCause()).getLocalizedMessage(), "EMAIL")) {
				throw new ApplicationBusinessException(ServidorONExceptionCode.EMAIL_INVALIDO);
			}
			
			throw cde;
		}
	}
		

	public RapServidores obterServidor(RapServidoresId id) {
		if (id == null) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		return getRapServidoresDAO().obterServidorComPessoaFisicaEVinculoPorVinCodigoMatricula(id.getMatricula(),id.getVinCodigo());
	}

	public RapServidores obterServidor(Short vinculo, Integer matricula) {
		return this.obterServidor(new RapServidoresId(matricula, vinculo));
	}

	public void verificarInformacoesValidas(RapVinculos vinculo, Short vinCodigo, RapPessoasFisicas pessoaFisica,
			Integer codigoPessoa, FccCentroCustos centroCustoLotacao, Integer codigoCCustoLotacao, FccCentroCustos centroCustoAtuacao,
			Integer codigoCCustoAtuacao, RapOcupacaoCargo ocupacaoCargo, Integer codigoOcupacao) throws ApplicationBusinessException {
		
		if (pessoaFisica == null && codigoPessoa != null) {
			throw new ApplicationBusinessException(ServidorONExceptionCode.MENSAGEM_PESSOA_FISICA_INVALIDA);
		}

		if (vinculo == null && vinCodigo != null) {
			throw new ApplicationBusinessException(ServidorONExceptionCode.MENSAGEM_VINCULO_INVALIDO);
		}

		if (centroCustoLotacao == null && codigoCCustoLotacao != null) {
			throw new ApplicationBusinessException(ServidorONExceptionCode.MENSAGEM_CENTRO_CUSTO_LOTACAO_INVALIDO);
		}

		if (centroCustoAtuacao == null && codigoCCustoAtuacao != null) {
			throw new ApplicationBusinessException(ServidorONExceptionCode.MENSAGEM_CENTRO_CUSTO_ATUACAO_INVALIDO);
		}

		if ((ocupacaoCargo == null || ocupacaoCargo.getCodigo() == null) && codigoOcupacao != null) {
			throw new ApplicationBusinessException(ServidorONExceptionCode.MENSAGEM_OCUPACAO_INVALIDA);
		}
	}
	
	public List<RapServidores> pesquisarRapServidoresPorCodigoDescricao(
			Object objPesquisa) {
		return this.getRapServidoresDAO().pesquisarRapServidoresPorCodigoDescricao(objPesquisa);
	}

	public List<RapServidoresVO> pesquisarRapServidoresVOPorCodigoDescricao(Object objPesquisa) {
		return this.getRapServidoresDAO().pesquisarRapServidoresVOPorCodigoDescricao(objPesquisa);
	}

	public RapServidores obterRapServidor(RapServidoresId id) {
		return this.getRapServidoresDAO().obterPorChavePrimaria(id, true,
				RapServidores.Fields.PESSOA_FISICA);
	}
	
	public RapPessoasFisicas obterRapPessoasFisicas(RapServidoresId id) {
		return this.getRapPessoasFisicasDAO().obterRapPessoasFisicas(id);
	}
	
	public List<RapServidores> pesquisarRapServidoresPorCodigoPessoa(Integer codigoPessoa) {
		if (codigoPessoa == null) {
			logError("ServidorON.pesquisarRapServidoresPorCodigoPessoa(): parametro null.");
			return null;
		}
		return this.getRapServidoresDAO().pesquisarRapServidoresPorCodigoPessoa(codigoPessoa);
	}
	
	/**
	 * Método usado para alimentar a modal de servidores nas telas de centro de
	 * custo.
	 * @dbtables RapServidores select
	 * 
	 * @param param
	 * @return
	 */
	public List<RapServidores> pesquisarRapServidores(Object param) {
		return this.getRapServidoresDAO().pesquisarRapServidores(param);
	}

	public Long pesquisarRapServidoresCount(Object param) {
		return this.getRapServidoresDAO().pesquisarRapServidoresCount(param);
	}
	
	/**
	 * Método para buscar servidor através do ID (matricula + codigo_vinculo)
	 * 
	 * @dbtables RapServidores select
	 * 
	 * @param Objeto
	 *            RapServidores com ID preenchido (matricula + codigo_vinculo)
	 * @return servidor
	 */
	public RapServidores obterServidor(RapServidores servidor) {
		return this.getRapServidoresDAO().obterServidor(servidor);
	}
	
	
	public RapServidores obterServidorPessoa(RapServidores servidor) {
		return this.getRapServidoresDAO().obterPorChavePrimaria(servidor.getId(), true, RapServidores.Fields.PESSOA_FISICA);
	}

	/**
	 * Método usado para incluir uma chefia.
	 * 
	 * @dbtables RapChefias insert
	 * 
	 * @param chefia
	 * @throws ApplicationBusinessException
	 */
	public void incluirChefia(RapChefias chefia) throws ApplicationBusinessException {
		validaDadosChefia(chefia);

		this.getRapChefiasDAO().persistir(chefia);
	}

	/**
	 * Método usado para validar campos requeridos de CHefia
	 * 
	 * @param chefia
	 * @throws ApplicationBusinessException
	 */
	public void validaDadosChefia(RapChefias chefia) throws ApplicationBusinessException {
		if (chefia.getDtInicio() == null) {
			throw new ApplicationBusinessException(ServidorONExceptionCode.CHEFIA_DATA_INICIO_NULA);
		}
	}
	
	/**
	 * #39000 - Serviço que retorna existe servidor categoria prof medicos
	 * @param matricula
	 * @param vinculo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public Boolean existeServidorCategoriaProfMedico(Integer matricula, Short vinculo) throws ApplicationBusinessException{
		Integer categoriaProfMedico = null;
		RapServidores rapServidores = getRegistroColaboradorFacade().obterServidor(vinculo, matricula);
		AghParametros parametro = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_CATEG_PROF_MEDICO);
		if (parametro != null) {
			categoriaProfMedico = parametro.getVlrNumerico().intValue();
		}
		if (rapServidores != null && categoriaProfMedico != null) {
			return getPerfisUsuariosDAO().existeServidorCategoriaProfMedico(rapServidores.getUsuario(), categoriaProfMedico);
		}
		return false;	
	}
	

	public Boolean permiteSerResponsSolicExame(Short vinCodigo, Integer matricula) throws ApplicationBusinessException {
		
		List<ConselhoProfissionalServidorVO> lista = rapServidoresDAO.pesquisarConselhoProfissional(matricula, vinCodigo, true);
		
		String codConselhos = getParametroFacade().buscarValorTexto(AghuParametrosEnum.P_AGHU_CONSELHO_RESP_SOLICITA_EXAME);
		
		for (ConselhoProfissionalServidorVO vo : lista) {
			if (!StringUtils.isBlank(vo.getNumeroRegistroConselho()) && vo.getCodigoConselho() != null) {
				String vetorConselhos[] = codConselhos.split("\\,");
				for (String codigoCons : vetorConselhos) {
					if (codigoCons.equals(vo.getCodigoConselho().toString())) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade(){
		return this.registroColaboradoFacade;
	}

	protected RapChefiasDAO getRapChefiasDAO() {
		return rapChefiasDAO;
	}

	protected RapPessoasFisicasDAO getRapPessoasFisicasDAO() {
		return rapPessoasFisicasDAO;
	}

	protected RapServidoresDAO getRapServidoresDAO() {
		return rapServidoresDAO;
	}

	protected ServidorRN getServidorRN() {
		return servidorRN;
	}

	protected PerfisUsuariosDAO getPerfisUsuariosDAO() {
		return perfisUsuariosDAO;
	}



}