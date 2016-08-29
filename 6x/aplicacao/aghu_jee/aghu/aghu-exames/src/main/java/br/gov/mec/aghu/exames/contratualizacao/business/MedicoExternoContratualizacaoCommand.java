package br.gov.mec.aghu.exames.contratualizacao.business;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.contratualizacao.util.MedicoSolicitante;
import br.gov.mec.aghu.exames.dao.AghMedicoExternoDAO;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.fonetizador.FonetizadorUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


public class MedicoExternoContratualizacaoCommand extends ContratualizacaoCommand {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4007537779282839689L;

	private static final Log LOG = LogFactory.getLog(MedicoExternoContratualizacaoCommand.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@Inject
	private IAghuFacade aghuFacade;

	@Inject
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@Inject
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private AghMedicoExternoDAO aghMedicoExternoDAO;

	public enum MedicoExternoContratualizacaoActionExceptionCode implements BusinessExceptionCode {
		MENSAGEM_DADOS_OBRIGATORIOS_MEDICO_EXTERNO, MENSAGEM_CRM_MEDICO_NAO_NUMERICO, MENSAGEM_CRM_MEDICO_MAIOR_TAMANHO_MAXIMO;
	}

	/**
	 * Localizar o médico externo recbido no xml da contratualização de exames.
	 * Caso não seja localizado, o mesmo será inserido automaticamente na base
	 * de dados.
	 * 
	 * @param nomeMedico
	 * @param crmMedico
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected AghMedicoExterno localizarMedicoExterno(String nomeMedico,
			String crmMedico) throws BaseException {
		nomeMedico = StringUtils.trim(nomeMedico);
		crmMedico = StringUtils.trim(crmMedico);
		if (StringUtils.isEmpty(nomeMedico) || StringUtils.isEmpty(crmMedico)) {
			throw new ApplicationBusinessException(
					MedicoExternoContratualizacaoActionExceptionCode.MENSAGEM_DADOS_OBRIGATORIOS_MEDICO_EXTERNO);
		}
		/* valida crm numerico
		if (! StringUtils.isNumeric(crmMedico)) {
			throw new ApplicationBusinessException(
					MedicoExternoContratualizacaoActionExceptionCode.MENSAGEM_CRM_MEDICO_NAO_NUMERICO, crmMedico);
		}*/
		if (crmMedico.length() > 9) {
			throw new ApplicationBusinessException(
					MedicoExternoContratualizacaoActionExceptionCode.MENSAGEM_CRM_MEDICO_MAIOR_TAMANHO_MAXIMO, crmMedico);
		}
		String nomeAjustado = FonetizadorUtil.ajustarNome(nomeMedico);
		AghMedicoExterno medicoExterno = this.buscarMedicoExterno(nomeAjustado,
				crmMedico);

		return medicoExterno;
	}

	/**
	 * buscar o médico externo. 1. retornar o médico externo caso localize pelo
	 * nome e crm; 2. gerar uma exception caso exista um médico cadastrado com
	 * crm e nome diferente; 3. inserir um novo médico caso o mesmo não seja
	 * localizado pelos critérios acima;
	 * 
	 * @param nome
	 * @param crm
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private AghMedicoExterno buscarMedicoExterno(String nome, String crm)
			throws BaseException {

		AghMedicoExterno medicoExterno = this.getAghMedicoExternoDAO()
				.obterMedicoExternoPeloNomeECrm(nome, crm);
		if (medicoExterno != null) {
			return medicoExterno;
		} else {
//			medicoExterno = this.getAghMedicoExternoDAO()
//					.obterMedicoExternoPeloNomeECrm(null, crm);
//			if (medicoExterno != null) {
//				throw new ApplicationBusinessException(
//						MedicoExternoContratualizacaoActionExceptionCode.MENSAGEM_MEDICO_EXTERNO_JA_CADASTRADO);
//			} else {
//				return this.inserirMedicoExterno(nome, crm);
//			}
			return this.inserirMedicoExterno(nome, crm);
		}

	}

	/**
	 * inserir o médico externo na base de dados
	 * 
	 * @param nome
	 * @param crm
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private AghMedicoExterno inserirMedicoExterno(String nome, String crm)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghMedicoExterno medicoExterno = new AghMedicoExterno();
		//truncar
		nome = StringUtils.substring(nome, 0, 60);
		
		medicoExterno.setCrm(crm);
		medicoExterno.setNome(nome);
		medicoExterno.setServidor(servidorLogado);
		medicoExterno.setCriadoEm(Calendar.getInstance().getTime());

		this.getCadastrosApoioExamesFacade().saveOrUpdateMedicoExterno(
				medicoExterno);
		return medicoExterno;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}

	protected AghMedicoExternoDAO getAghMedicoExternoDAO() {
		return aghMedicoExternoDAO;
	}

	protected ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade() {
		return cadastrosApoioExamesFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}

	@Override
	Map<String, Object> executar(Map<String, Object> parametros)
			throws NumberFormatException, BaseException, ParseException {

		MedicoSolicitante medicoSolicitante = null;
		if (parametros != null && parametros.containsKey(MEDICO_INTEGRACAO)) {
			medicoSolicitante = (MedicoSolicitante) parametros
					.get(MEDICO_INTEGRACAO);
		}

		if (medicoSolicitante != null) {
			AghMedicoExterno medicoExterno = this.localizarMedicoExterno(
					medicoSolicitante.getNome(), medicoSolicitante.getCrm());
			
			parametros.put(MEDICO_AGHU, medicoExterno);
			return parametros;
		} else {
			return null;
		}

	}

	@Override
	boolean comitar() {
		// TODO Auto-generated method stub
		return true;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
