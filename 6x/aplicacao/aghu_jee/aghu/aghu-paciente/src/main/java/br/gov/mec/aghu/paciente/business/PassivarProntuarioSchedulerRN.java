package br.gov.mec.aghu.paciente.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.HeuristicMixedException;
import javax.transaction.HeuristicRollbackException;
import javax.transaction.NotSupportedException;
import javax.transaction.RollbackException;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacConsultasJn;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class PassivarProntuarioSchedulerRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PassivarProntuarioSchedulerRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}


	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@Inject
	private EmailUtil emailUtil;
	
	@Resource
	private UserTransaction userTransaction;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4189551028696476972L;

	public void agendarPassivarProntuario(String secao, Date dthr, boolean incluiPassivos, RapServidores servidorLogado) throws ApplicationBusinessException {		
		this.logDebug("LOGPP: INICIO DE AGENDAMENTO");
		
		// FIXME Centralizar solucao no AGHUBaseBusiness ou alguma classe utilitaria para scheduler.
		// Recupera a instancia de Identity, criando-a se nao existir nenhuma.
		// Caso identity nao esteja logado, usar o usuario passado por parametro
//		if (!identity.isLoggedIn()) {
//			identity.acceptExternallyAuthenticatedPrincipal(new SimplePrincipal(username));
//		}
		
		
		
		executarPassivarProntuario(secao, dthr, incluiPassivos);
		
		//return null;
		
	}
	
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void executarPassivarProntuario(String secao, Date dthr,
			boolean incluiPassivos) throws ApplicationBusinessException {	
		
		this.logDebug("LOGPP: INICIO DA EXECUCAO ASSINCRONA");		
		
		Calendar cDthr = Calendar.getInstance();
		cDthr.setTime(dthr);
		cDthr.set(Calendar.DAY_OF_MONTH, cDthr.getActualMaximum(Calendar.DAY_OF_MONTH));
		cDthr.set(Calendar.MINUTE, 0);
		cDthr.set(Calendar.HOUR_OF_DAY, 0);
		cDthr.set(Calendar.SECOND, 0);

		secao = StringUtils.leftPad(secao, 2, '0');

//		UserTransaction userTx = (UserTransaction) org.jboss.seam.Component
//		.getInstance("org.jboss.seam.transaction.transaction");
		//TODOmigracao
		
		int totalPassivados = 0;
		int totalJaPassivos = 0;
		
		try {
			
			userTransaction.setTransactionTimeout(60 * 60 * 8);
			userTransaction.begin();
			
			
			
			IPacienteFacade pacienteFacade = this.getPacienteFacade();
			
		//	userTx.commit();
		//	userTx.setTransactionTimeout(60 * 60 * 4);
		//	userTx.begin();
			
		//	getEntityManager().joinTransaction();
			
			this.logDebug("LOGPP: CHAMANDO BUSCA DE PACIENTES");		

			Iterator<AipPacientes> listaPac = pacienteFacade.obterPacientesParaPassivarProntuarioScrollableResults(secao, cDthr, null, incluiPassivos).iterator();
			AipPacientes pac = null;

			List<AipPacientes> listaPacAux = new ArrayList<AipPacientes>();
			int i = 0;
			while (listaPac.hasNext()) {
				i++;
				pac = (AipPacientes) listaPac.next();
				if (pac.getPrntAtivo().equals(DominioTipoProntuario.P)&& incluiPassivos) {
					totalJaPassivos++;
					this.logDebug("LOGPP: DADOS PACIENTE PASSIVO::::");
					this.logDebug("Indice do paciente: " + i);
					this.logDebug("Total Passivos ate agora: " + totalJaPassivos);
					this.logDebug("Nome: " + pac.getNome());
					this.logDebug("Prontuario: " + pac.getProntuario());
					this.logDebug("Status: " + pac.getPrntAtivo());
				} else {
					if(!pac.getPrntAtivo().equals(DominioTipoProntuario.P)){
						if (this.obterConsultasPacientesParaPassivarProntuario(pac.getCodigo(), cDthr) == null) {
							pac = this.getPacienteFacade().obterPacientePorCodigoOuProntuario(null, pac.getCodigo(),null);
							pac.setPrntAtivo(DominioTipoProntuario.P);
							listaPacAux.add(pac);
							totalPassivados++;
							this.logDebug("LOGPP: DADOS PACIENTE PASSIVADO::::");
							this.logDebug("Indice do paciente: " + i);
							this.logDebug("Total Passivados ate agora: " + totalPassivados);
							this.logDebug("Nome: " + pac.getNome());
							this.logDebug("Prontuario: " + pac.getProntuario());
							this.logDebug("Status: " + pac.getPrntAtivo());
							this.getCadastroPacienteFacade().persistirPacienteSemValidacao(pac);
						}
					}
				}
				this.flush();
				this.evict(pac);
			}

			userTransaction.commit();

		} catch (SecurityException | SystemException | IllegalStateException
				| NotSupportedException | RollbackException
				| HeuristicMixedException | HeuristicRollbackException e) {
			LOG.error(e.getMessage(), e);
			try {
				userTransaction.rollback();
			} catch (IllegalStateException | SecurityException
					| SystemException e1) {
				LOG.error(e.getMessage(), e);
			}

		}
		
		this.logDebug("LOGPP: PROCESSO CONCLUIDO");
		// 'ncorrea@hcpa.ufrgs.br' ||' jpedrollo@hcpa.ufrgs.br');
	}

	/**
	 * Busca uma lista de pacientes para serem passivados, conforme secao e
	 * data.
	 */
	private AacConsultas obterConsultasPacientesParaPassivarProntuario(
			Integer pacCodigo, Calendar dthr) {
		boolean achouConsulta = false;

		List<Object[]> listConsultas = this.getAmbulatorioFacade().listarConsultasPacientesParaPassivarProntuario(pacCodigo, dthr);

		AacConsultas consultaAux = null;
		Float diferencaDatas = null;
		for (int i = 0; i < listConsultas.size(); i++) {
			Object[] columns = listConsultas.get(i);
			Date dataAux = obterUltimaDataConsultaDaJournalConsultas((Integer) columns[1]);
			if (dataAux != null) {
				diferencaDatas = CoreUtil.diferencaEntreDatasEmDias((Date) columns[0],
						dataAux);
				// diferencaDatas = diferencaDatas * 24;
				if (diferencaDatas > 0.5) {
					consultaAux = new AacConsultas();
					consultaAux.setDtConsulta((Date) columns[0]);
					consultaAux.setNumero((Integer) columns[1]);
					achouConsulta = true;
					break;
				}
			}else{
				consultaAux = new AacConsultas();
				consultaAux.setDtConsulta((Date) columns[0]);
				consultaAux.setNumero((Integer) columns[1]);
				achouConsulta = true;
				break;
			}
		}
		if (!achouConsulta) {
			consultaAux = null;
		}
		return consultaAux;
	}
	
	/**
	 * Busca a última data de consulta que existe na journal de consultas.
	 * Tradução da procedure AACC_BUSCA_DT_CONJ
	 * 
	 * @param numeroConsulta
	 */
	private Date obterUltimaDataConsultaDaJournalConsultas(
			Integer numeroConsulta) {
		Date dataAlteracao = null;

		List<AacConsultasJn> listaConsultasJnVolta = this.getAmbulatorioFacade().listaConsultasJn(numeroConsulta, "L");
		
		if (listaConsultasJnVolta.size() > 0) {
			dataAlteracao = listaConsultasJnVolta.get(0).getDataAlteracao();
		}

		return dataAlteracao;
	}

	
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return (IPacienteFacade) pacienteFacade;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return (IParametroFacade) parametroFacade;
	}
	
	protected EmailUtil getEmailUtil() {
		return (EmailUtil) emailUtil;
	}

}
