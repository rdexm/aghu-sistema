package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.registrocolaborador.dao.RapVinculosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;

@Stateless
public class VinculoON extends BaseBusiness {

	@EJB
	private VinculoRN vinculoRN;
	
	private static final Log LOG = LogFactory.getLog(VinculoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private EmailUtil emailUtil;
	
	@Inject
	private RapVinculosDAO rapVinculosDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3952384003552117242L;

	private enum VinculoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_PARAMETRO_NAO_INFORMADO, MENSAGEM_ERRO_REMOVER_VINCULO_COM_SERVIDOR, MENSAGEM_ERRO_REMOVER_VINCULO, MENSAGEM_ERRO_CODIGO_VINCULO_JA_EXISTE, MENSAGEM_ERRO_REMOVER_VINCULO_COM_PERFIL, MENSAGEM_ERRO_REMOVER_VINCULO_COM_PERFIL_VINCULO, MENSAGEM_ERRO_REMOVER_VINCULO_COM_SERV_CCUSTOS, MENSAGEM_ERRO_DESCRICAO_VINCULO_JA_EXISTE;
	}

	public void enviaEmail(Short codigo, String descricao,
			DominioSimNao geraMatricula) throws ApplicationBusinessException {

		// Realizar o envio de email ao criar um novo vínculo
		// Remetente
		AghParametros emailDe = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO);
		if (emailDe.getVlrTexto() == null) {
			return;
		}

		// Destinatários
		List<String> listaDestinatarios = new ArrayList<String>();
		AghParametros emailDestino = getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_CONSULTORES);
		if (emailDestino.getVlrTexto() == null) {
			return;
		}
		StringTokenizer emailPara = new StringTokenizer(
				emailDestino.getVlrTexto(), ";");
		while (emailPara.hasMoreTokens()) {
			listaDestinatarios.add(emailPara.nextToken().trim().toLowerCase());
		}

		// Assunto do Email
		String assuntoEmail = "ATENÇÃO! A criação do vínculo "
				+ codigo
				+ " exige vinculação dos perfis adequados no sistema de segurança.";
		// Conteúdo do Email
		String conteudoEmail = "A criação do vínculo "
				+ codigo
				+ " - "
				+ descricao
				+ " exige vinculação dos perfis adequados no sistema de segurança.";
		// Realizar a chamada do envio de email
		getEmailUtil().enviaEmail(emailDe.getVlrTexto().toLowerCase(),
				listaDestinatarios, null, assuntoEmail, conteudoEmail);

		// Caso gera matrícula, enviar email sobre este indicador
		if (geraMatricula == DominioSimNao.S) {
			// Assunto do Email
			assuntoEmail = "ATENÇÃO! A criação do vínculo "
					+ codigo
					+ " com geração automática de número de matrícula exige a criação de parâmetro de sistema.";
			// Conteúdo do Email
			conteudoEmail = "A criação do vínculo "
					+ codigo
					+ " - "
					+ descricao
					+ " com geração automática de número de matrícula exige a criação de parâmetro de sistema.";
			// Realizar a chamada do envio de email
			getEmailUtil().enviaEmail(emailDe.getVlrTexto().toLowerCase(),
					listaDestinatarios, null, assuntoEmail, conteudoEmail);
		}

	}

	/**
	 * PopulaTabela tabela RAP_COD_STARH_LIVRES, caso esteja vazia
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void populaTabela() throws ApplicationBusinessException{
		Integer parametroInicial = null;
		Integer parametroFinal = null;

		AghParametros matriculaInicio = getParametroFacade()
		.buscarAghParametro(AghuParametrosEnum.P_AGHU_MATRICULA_INICIO);

		parametroInicial = matriculaInicio.getVlrNumerico().intValue();
		parametroFinal = parametroInicial + 5000;

		getVinculoRN().gerarCodStarh(parametroInicial, parametroFinal);		
	}
	
	/**
	 * Método para excluir um vínculo
	 */
	
	public void excluirVinculo(Short codigoVinculo) throws ApplicationBusinessException {

		try {
			RapVinculos vinculo = rapVinculosDAO.obterPorChavePrimaria(codigoVinculo);
			rapVinculosDAO.remover(vinculo);
			rapVinculosDAO.flush();
		} catch (Exception e) {
			LOG.error("Erro ao remover o vínculo.", e);
			if (e.getCause() != null && ConstraintViolationException.class.equals(e.getCause().getClass())) {
				ConstraintViolationException ecv = (ConstraintViolationException) e.getCause();

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(),"RAP_SER_VIN_FK1")) {
					throw new ApplicationBusinessException(VinculoONExceptionCode.MENSAGEM_ERRO_REMOVER_VINCULO_COM_SERVIDOR);
				}

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(),"CSE_POC_VIN_FK1")) {
					throw new ApplicationBusinessException(VinculoONExceptionCode.MENSAGEM_ERRO_REMOVER_VINCULO_COM_PERFIL);
				}

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(),"CSE_PVI_VIN_FK1")) {
					throw new ApplicationBusinessException(VinculoONExceptionCode.MENSAGEM_ERRO_REMOVER_VINCULO_COM_PERFIL_VINCULO);
				}

				if (StringUtils.containsIgnoreCase(ecv.getConstraintName(),"RAP_TOT_VIN_FK1")) {
					throw new ApplicationBusinessException(VinculoONExceptionCode.MENSAGEM_ERRO_REMOVER_VINCULO_COM_SERV_CCUSTOS);
				}
			}
			
			throw e;
		}
	}
	
	/**
	 * @param codigoVinculo
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public RapVinculos buscarVinculos(Short codigoVinculo, boolean somenteAtivo)
			throws ApplicationBusinessException {

		if (codigoVinculo == null) {
			throw new ApplicationBusinessException(
					VinculoONExceptionCode.MENSAGEM_PARAMETRO_NAO_INFORMADO);
		}

		RapVinculos vinculo = getVinculosDAO().obterVinculo(codigoVinculo);

		if (vinculo == null) {
			return vinculo;
		}

		if (somenteAtivo && vinculo.getIndSituacao() != DominioSituacao.A) {
			return null;
		}

		if (super.isHCPA()) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			Set<String> perfisUsuario = getICascaFacade().obterNomePerfisPorUsuario(servidorLogado.getUsuario());
			List<RapVinculos> perfisVinculos = getVinculosDAO().obterPerfisVinculos(perfisUsuario);
			if (!perfisVinculos.contains(vinculo)) {
				vinculo = null;
			}
		}

		return vinculo;
	}
	
	/**
	 * Retorna vínculos de acrodo com o código ou descrição informados por ordem
	 * de codigo
	 * 
	 * @dbtables RapVinculos select
	 * 
	 * @param vinculo
	 *            ou descricao
	 * @return vínculos encontrados lista vazia se não encontrado
	 */
	public List<RapVinculos> pesquisarVinculoPorCodigoDescricao(Object vinculo,
			boolean somenteAtivo) throws ApplicationBusinessException {
		List<RapVinculos> listaRetorno = new ArrayList<RapVinculos>();

		// Quando for HCPA apresenta somente os vínculos permitidos para o
		// usuário
		if (isHCPA()) {
			List<RapVinculos> lista = getVinculosDAO().pesquisarVinculoPorCodigoDescricao(vinculo, somenteAtivo);
			if (lista != null && !lista.isEmpty()) {
				listaRetorno = obterVinculosPorPerfil(lista);
			}
		} else {
			listaRetorno = getVinculosDAO().pesquisarVinculoPorCodigoDescricao(vinculo, somenteAtivo);
		}

		return listaRetorno;
	}	
	
	/**
	 * Buscar os vínculos permitidos para o perfil e montar a lista de vínculos
	 * 
	 * @param lista
	 * @return
	 *  
	 */
	private List<RapVinculos> obterVinculosPorPerfil(List<RapVinculos> lista) throws ApplicationBusinessException {

		List<RapVinculos> listaRetorno = new ArrayList<RapVinculos>();

		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Set<String> perfisUsuario = getICascaFacade().obterNomePerfisPorUsuario(servidorLogado.getUsuario()); 
		List<RapVinculos> perfisVinculos = getVinculosDAO().obterPerfisVinculos(perfisUsuario);
		
		if (perfisVinculos == null || perfisVinculos.isEmpty()) {
			return listaRetorno;
		}

		for (RapVinculos rapVinculos : lista) {
			if (perfisVinculos.contains(rapVinculos)) {
				listaRetorno.add(rapVinculos);
			}
		}

		return listaRetorno;
	}		

	protected VinculoRN getVinculoRN() {
		return vinculoRN;
	}	
	
	protected RapVinculosDAO getVinculosDAO() {
		return rapVinculosDAO;
	}
	
	protected EmailUtil getEmailUtil() {
		return this.emailUtil;
	}	
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}	
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}