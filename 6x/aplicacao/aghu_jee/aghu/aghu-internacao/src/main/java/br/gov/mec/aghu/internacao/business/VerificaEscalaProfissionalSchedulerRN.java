package br.gov.mec.aghu.internacao.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.internacao.dao.AinEscalasProfissionalIntDAO;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinEscalasProfissionalInt;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@SuppressWarnings({"deprecation", "PMD.JUnit4TestShouldUseTestAnnotation"})
@Stateless
public class VerificaEscalaProfissionalSchedulerRN extends BaseBusiness {


	private static final String END_FONT_BR = "</font> <br />";

	@EJB
	private InternacaoON internacaoON;
	
	@EJB
	private InternacaoRN internacaoRN;
	
	private static final Log LOG = LogFactory.getLog(VerificaEscalaProfissionalSchedulerRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@Inject
	private AinInternacaoDAO ainInternacaoDAO;
	
	@Inject
	private EmailUtil emailUtil;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AinEscalasProfissionalIntDAO ainEscalasProfissionalIntDAO;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -256469517024936419L;

	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

	private enum VerificaEscalaProfissionalSchedulerRNExceptionCode implements
			BusinessExceptionCode {
		ERRO_ATUALIZAR_INTERNACAO
	}

	private static final String ERRO_19 = " Erro ao atualizar escalas de profissionais da Internação. (Código #19)";

	private static final String ERRO_25 = " Não há dados de escalas de profissionais da Internação para o dia. (Código #25)";

	
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}
	
	/**
	 * ORADB Procedure AINP_TROCA_PAC_EQPES
	 * 
	 * @param date
	 * @param cron
	 * @throws AGHUNegocioException
	 */
	public void verificarEscalaProfissional(String nomeMicrocomputador, final RapServidores servidorLogado, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		InternacaoON internacaoON = this.getInternacaoON();
		InternacaoRN internacaoRN = this.getInternacaoRN();
		//AinInternacaoDAO ainInternacaoDAO = this.getAinInternacaoDAO();

		this.logInfo("Rotina VerificaEscalaProfissionalSchedulerRN.verificarEscalaProfissional iniciada em: "
						+ Calendar.getInstance().getTime());
		
		List<AinEscalasProfissionalInt> res = this.obterEscalas(null);

		for (AinEscalasProfissionalInt item : res) {

			if (hasSubstituto(item, null)) {

				// Obtendo as internações que serão atualizadas.
				List<AinInternacao> ints = internacaoON.obterInternacao(
						Boolean.TRUE, item.getId().getPecPreSerMatricula(),
						item.getId().getPecPreSerVinCodigo(), item.getId()
								.getPecCnvCodigo(), item.getId()
								.getPecPreEspSeq());

				for (AinInternacao it : ints) {
					it.setServidorProfessor(item.getServidorProfessor());

					it.setServidorDigita(item.getServidorProfessorDigitada());

					// Execução de triggers de Atualização.
					try {
						internacaoRN.atualizarInternacao(it, nomeMicrocomputador, dataFimVinculoServidor, false, servidorLogado, false);
					} catch (BaseException e) {
						this.verificarParametroEmail(ERRO_19);
						this.logInfo("Rotina VerificaEscalaProfissionalSchedulerRN.verificarEscalaProfissional: Erro ao atualizar internações.");
						throw new ApplicationBusinessException(
								VerificaEscalaProfissionalSchedulerRNExceptionCode.ERRO_ATUALIZAR_INTERNACAO);
					}
				}
			}
		}

		if (res.isEmpty()) {
			this.verificarParametroEmail(ERRO_25);
			this.logInfo("Rotina VerificaEscalaProfissionalSchedulerRN.verificarEscalaProfissional sem resultados.");
		}

		this.logInfo("Rotina VerificaEscalaProfissionalSchedulerRN.verificarEscalaProfissional finalizada em: "
						+ Calendar.getInstance().getTime());
	}


	/**
	 * Método para enviar email.
	 * 
	 * @param errorCode
	 *            - Oriundo da procedura do AGH.
	 * @throws ApplicationBusinessException
	 */
	private void sendEmail(String errorMsg) throws ApplicationBusinessException {
		
		if (verificarParametroEmailLista()){
			
			AghParametros emailDe = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO);
			List<String> emailParaList = new ArrayList<String>();
			
			// Obtem emails.
			StringTokenizer emailPara = new StringTokenizer(
				this.getParametroFacade().buscarAghParametro(
					AghuParametrosEnum.P_AGHU_EMAIL_TROCA_PAC_EQPES).getVlrTexto(), ";"
			);
	
			// Cria lista de emails.
			while (emailPara.hasMoreTokens()) {
				emailParaList.add(emailPara.nextToken().trim().toLowerCase());
			}
			
			//Quando não for possível identificar o IP, usar valores de não encontrado (uma rotina do Quartz)
			String localAddress = "IP da máquina Servidora não encontrado";
			String remoteAddress = "IP da máquina cliente não encontrado";
		   /*if (FacesContext.getCurrentInstance() != null && FacesContext.getCurrentInstance().getExternalContext() != null) {
			ServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
			HttpServletRequest servletRequest = (HttpServletRequest) request;
				
			localAddress = servletRequest.getLocalAddr();
			remoteAddress = servletRequest.getRemoteAddr();
			}*/
			
			final StringBuffer conteudoEmail = new StringBuffer(330);
			
			conteudoEmail.append("<font size='2' face='Courier New'>").append(errorMsg).append(END_FONT_BR)
			.append("<font size='2' face='Courier New'> JBoss Server: ").append(localAddress).append(END_FONT_BR)
			.append("<font size='2' face='Courier New'> Remote IP: ").append(remoteAddress).append(END_FONT_BR)
			.append("<font size='2' face='Courier New'> Oracle?: ").append((ainInternacaoDAO.isOracle() ? "Sim":"Não")).append(END_FONT_BR)
			.append("<font size='2' face='Courier New'>Mensagem gerada automaticamente pelo sistema.</font> <br />");
	
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			
			// Envia email.
			this.getEmailUtil().enviaEmail(emailDe.getVlrTexto(), emailParaList, null,
					errorMsg + " - " + sdf.format(new Date()), conteudoEmail.toString());
		}
	}
	
	/**
	 * @throws ApplicationBusinessException
	 * 
	 */
	public void testVerificarEscalaProfissional(Date date, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		InternacaoON internacaoON = this.getInternacaoON();
		InternacaoRN internacaoRN = this.getInternacaoRN();
		AinInternacaoDAO ainInternacaoDAO = this.getAinInternacaoDAO();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		this.logInfo("Rotina VerificaEscalaProfissionalSchedulerRN.verificarEscalaProfissional iniciada em: "
						+ Calendar.getInstance().getTime());

		List<AinEscalasProfissionalInt> res = this.obterEscalas(date);

		for (AinEscalasProfissionalInt item : res) {

			if (hasSubstituto(item, date)) {

				// Obtendo as internações que serão atualizadas.
				List<AinInternacao> ints = internacaoON.obterInternacao(
						Boolean.TRUE, item.getId().getPecPreSerMatricula(),
						item.getId().getPecPreSerVinCodigo(), item.getId()
								.getPecCnvCodigo(), item.getId()
								.getPecPreEspSeq());

				for (AinInternacao it : ints) {
					it.setServidorProfessor(item.getServidorProfessor());

					it.setServidorDigita(item.getServidorProfessorDigitada());

					// Execução de triggers de Atualização.
					try {
						internacaoRN.atualizarInternacao(it, nomeMicrocomputador, dataFimVinculoServidor, false, servidorLogado, false);
						ainInternacaoDAO.flush();
					} catch (ApplicationBusinessException e) {
						this.verificarParametroEmail(ERRO_19);
						this.logInfo("Rotina VerificaEscalaProfissionalSchedulerRN.verificarEscalaProfissional: Erro ao atualizar internações."
										+ Calendar.getInstance());
						throw new ApplicationBusinessException(
								VerificaEscalaProfissionalSchedulerRNExceptionCode.ERRO_ATUALIZAR_INTERNACAO);
					}
				}
			}
		}

		if (res.isEmpty()) {
			this.verificarParametroEmail(ERRO_25);
			this.logInfo("Rotina VerificaEscalaProfissionalSchedulerRN.verificarEscalaProfissional sem resultados.");
		}

		this.logInfo("Rotina VerificaEscalaProfissionalSchedulerRN.verificarEscalaProfissional finalizada em: "
						+ Calendar.getInstance().getTime());

	}

	/**
	 * ORADB Cursor C_SUBSTITUTO Procedure AINP_TROCA_PAC_EQPES
	 * 
	 * @param item
	 *            <code>AinEscalasProfissionalInt</code>
	 * @return Possui ou não substitudo.
	 */
	private boolean hasSubstituto(AinEscalasProfissionalInt item, Date date) {
		return this.getAinEscalasProfissionalIntDAO().hasSubstituto(item, date);
	}

	/**
	 * ORADB Cursor C_ESCALA Procedure AINP_TROCA_PAC_EQPES
	 * 
	 * @return List <code>AinEscalasProfissionalInt</code>
	 */
	private List<AinEscalasProfissionalInt> obterEscalas(Date date) {
		return this.getAinEscalasProfissionalIntDAO().obterEscalas(date);
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected InternacaoON getInternacaoON() {
		return internacaoON;
	}

	protected InternacaoRN getInternacaoRN() {
		return internacaoRN;
	}

	protected EmailUtil getEmailUtil() {
		return this.emailUtil;
	}

	protected AinInternacaoDAO getAinInternacaoDAO() {
		return ainInternacaoDAO;
	}
	
	protected AinEscalasProfissionalIntDAO getAinEscalasProfissionalIntDAO() {
		return ainEscalasProfissionalIntDAO;
	}
	
    public void verificarParametroEmail(String error) throws ApplicationBusinessException {
        AghParametros emailDe = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO);
        if (!StringUtils.isEmpty(emailDe.getVlrTexto())){
            this.sendEmail(error);
        }
    }
    
    public Boolean verificarParametroEmailLista() throws ApplicationBusinessException {
        AghParametros emailLista = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_TROCA_PAC_EQPES);
        if (!StringUtils.isEmpty(emailLista.getVlrTexto())){
            return true;
        }
        return false;
    }
}
