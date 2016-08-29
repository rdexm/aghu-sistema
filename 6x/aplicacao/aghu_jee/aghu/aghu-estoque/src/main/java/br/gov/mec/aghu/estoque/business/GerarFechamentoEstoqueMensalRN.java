package br.gov.mec.aghu.estoque.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;
import javax.transaction.SystemException;
import javax.transaction.UserTransaction;

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
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.estoque.dao.SceHistoricoFechamentoMensalDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceHistoricoFechamentoMensal;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * ORADB PROCEDURE SCEP_FECH_MENS
 * @author aghu
 *
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class GerarFechamentoEstoqueMensalRN extends BaseBusiness{

	@EJB
	private ExecutarFechamentoEstoqueMensalRN executarFechamentoEstoqueMensalRN;
	
	private static final Log LOG = LogFactory.getLog(GerarFechamentoEstoqueMensalRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceHistoricoFechamentoMensalDAO sceHistoricoFechamentoMensalDAO;
	
	@Inject
	private EmailUtil emailUtil;
	
	@Resource
	private UserTransaction userTransaction;
	
	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6769146933643926170L;
	
	private static final String DATA_PROXIMO_MES = "Data do próximo fechamento: ";
	private static final String EMAIL_TEXTO_INICIAL = "Seguem as etapas do processo de fechamento mensal:\n";

	
	public enum GerarFechamentoEstoqueMensalRNExceptionCode implements BusinessExceptionCode {
		ERRO_AO_TENTAR_GERAR_FECHAMENTO_ESTOQUE_MENSAL;
	}
	
	private static final String MENSAGEM_PADRAO_ROTINA = "Rotina gerarFechamentoEstoqueMensalRN.gerarFechamentoEstoqueMensal";
	
	

    public void gerarFechamentoEstoqueMensalManual(RapServidores servidorLogado) throws BaseException{
    	    	logInfo(MENSAGEM_PADRAO_ROTINA + " - DISPARADA MANUALMENTE");
    	    	
    	    	gerarFechamentoEstoqueMensal(servidorLogado);	
    }
    
    public void gerarFechamentoEstoqueMensalManual() throws BaseException{
    	logInfo(MENSAGEM_PADRAO_ROTINA + " - DISPARADA MANUALMENTE");
    	RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
    	gerarFechamentoEstoqueMensal(servidorLogado);	
}
    
	/**
	 * ORADB PROCEDURE SCEP_FECH_MENS
	 * Gera e executa fechamento mensal de estoque
	 */
	public void gerarFechamentoEstoqueMensal(RapServidores servidorLogado) throws BaseException{	
		
			SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			StringBuilder messagemEmail = new StringBuilder(EMAIL_TEXTO_INICIAL);
			boolean sucesso = false;
			final Date dataInicio = new Date();
			logAndMailInfo(messagemEmail, MENSAGEM_PADRAO_ROTINA + " INICIADA em: " + dateFormat.format(dataInicio));

			try {
				
				userTransaction.setTransactionTimeout(60 * 60 * 8);
				userTransaction.begin();
				
				// Executa fechamento mensal de estoque
				messagemEmail.append(this.getExecutarFechamentoEstoqueMensalRN().executarFechamentoEstoqueMensal(servidorLogado));
				
				// Grava a data/hora da execução do próximo fechamento no registro da etapa de conclusão do último fechamento
				this.gravarDataExecucaoProximoFechamento();
				this.getSceHistoricoFechamentoMensalDAO().flush();
				
				logAndMailInfo(messagemEmail, MENSAGEM_PADRAO_ROTINA + " iniciou em: " + dateFormat.format(dataInicio));
				logAndMailInfo(messagemEmail, MENSAGEM_PADRAO_ROTINA + " finalizou em: " + dateFormat.format(new Date()));
				
				sucesso = true;
				userTransaction.commit();
			}  catch (Exception e) {
				try {
					userTransaction.rollback();
				} catch (IllegalStateException | SecurityException
						| SystemException e1) {
					LOG.error(e.getMessage(),e);
				}
				logAndMailError(messagemEmail, e.getMessage(), e);
				throw new ApplicationBusinessException(GerarFechamentoEstoqueMensalRNExceptionCode.ERRO_AO_TENTAR_GERAR_FECHAMENTO_ESTOQUE_MENSAL);
			} finally {
				logAndMailInfo(messagemEmail, MENSAGEM_PADRAO_ROTINA + " iniciou em: " + dateFormat.format(dataInicio));
				logAndMailInfo(messagemEmail, MENSAGEM_PADRAO_ROTINA + " finalizou em: " + dateFormat.format(new Date()));
				
				enviaEmail(messagemEmail.toString(), sucesso);
			}		

	}
	
	public void logAndMailError(StringBuilder msgMail, Object mensagemInicio, Throwable erro) {
		super.logError(mensagemInicio, erro);
		msgMail.append("Erro durante o processo de fechamento mensal:<br/>").append(mensagemInicio);
	}
	
	public void logAndMailInfo(StringBuilder msgMail,Object mensagemInicio) {
		super.logInfo(mensagemInicio);
		msgMail.append(mensagemInicio).append("<br/>");
	}
	
	/**
	 * @param: String mensagem
	 * @throws ApplicationBusinessException 
	 */
	public void enviaEmail(String mensagem, boolean sucesso)throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		String remetente = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_ENVIO).getVlrTexto();
		String strDestinatarios = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_EMAIL_FECHAMENTO_ESTOQUE).getVlrTexto();
		
		if (StringUtils.isNotBlank(remetente) && StringUtils.isNotBlank(strDestinatarios)) {
			String assunto = MENSAGEM_PADRAO_ROTINA + (sucesso ? " Executada com Sucesso" : " não Executada");
			
			List<String> destinatarios = new ArrayList<String>();
			if (servidorLogado!=null && servidorLogado.getEmail() != null) {
				destinatarios.add(servidorLogado.getEmail());
			}
			StringTokenizer emailPara = new StringTokenizer(strDestinatarios, ";");
			while (emailPara.hasMoreTokens()) {
				destinatarios.add(emailPara.nextToken().trim().toLowerCase());
			}
	
			emailUtil.enviaEmail(remetente, destinatarios, null, assunto, mensagem);
		} else {
			LOG.warn("Gerar fechamento de estoque mensal: enviar email - nao enviou e-mail por falta de valores nos parametros: P_AGHU_EMAIL_ENVIO, P_AGHU_EMAIL_FECHAMENTO_ESTOQUE");
		}
	}
	
	/**
	 * Grava a data/hora da execução do próximo fechamento no registro da etapa de conclusão do último fechamento
	 * @param dataCompetencia
	 */
	protected void gravarDataExecucaoProximoFechamento() throws BaseException{
		
		// Busca a data de competência do mês que está fechando
		final AghParametros parametroCompetencia =  this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA);

		// Obtém a data de competência do mês
		final Date dataCompetencia = parametroCompetencia.getVlrData();
		
		// Obtém valor formatado da ocorrência no histórico de fechamento mensal
		final String valorOcorrencia = this.obterValorOcorrencia(dataCompetencia);
		
		// Pesquisa histórico de fechamento mensal através da data de competência e com fechamento mensal concluído e sem ocorrência
		List<SceHistoricoFechamentoMensal> listaHistoricoFechamentoMensal = this.getSceHistoricoFechamentoMensalDAO().pesquisartHistoricoFechamentoMensalConcluidoSemOcorrenciaPorDataCompetencia(dataCompetencia);
		
		for (SceHistoricoFechamentoMensal historicoFechamentoMensal : listaHistoricoFechamentoMensal) {
			
			// Seta ocorrência
			historicoFechamentoMensal.setOcorrencia(valorOcorrencia);
			
			// Atualiza histórico de fechamento mensal
			this.getSceHistoricoFechamentoMensalDAO().atualizar(historicoFechamentoMensal);
			this.getSceHistoricoFechamentoMensalDAO().flush();
		}
		
	}
	
	/**
	 * Obtém valor formatado da ocorrência no histórico de fechamento mensal
	 * Vide. formato "Data do próximo fechamento: dd/MM/yyyy hh:mm"
	 * @return
	 * @throws BaseException
	 */
	private String obterValorOcorrencia(final Date dataCompetencia) throws BaseException{

		// Busca data competência atual (DO MÊS NOVO) para programar a data e hora do próximo fechamento mensal
		Calendar calendarDataCompetenciaEditada = Calendar.getInstance();
		
		// Obtém o último dia do MÊS NOVO
		calendarDataCompetenciaEditada.setTime(DateUtil.obterUltimoDiaDoMes(dataCompetencia));
	
		// Busca à hora de competência do mês que está fechando
		final AghParametros parametroHoraCompetencia =  this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_HORA_ATUAL_FECHAMENTO_ESTOQUE);

		// O parâmetro recebe em seu valor texto o seguinte formado: HH:mm
		String[] valor = parametroHoraCompetencia.getVlrTexto().split(":");
		calendarDataCompetenciaEditada.set(Calendar.HOUR_OF_DAY, Integer.parseInt(valor[0]));
		calendarDataCompetenciaEditada.set(Calendar.MINUTE, Integer.parseInt(valor[1]));
		
		// Obtém a data de competência com o mês NOVO com as horas atualizadas
		final Date dataCompetenciaEditada = calendarDataCompetenciaEditada.getTime();
		
		// Formata a DATA da nova data de competência
		SimpleDateFormat formatarData = new SimpleDateFormat("dd/MM/yyyy");
		final String valorDataFormatada = formatarData.format(dataCompetenciaEditada);
		
		// Formata a HORA da nova data de competência
		SimpleDateFormat formatarHora = new SimpleDateFormat("HH:mm");
		final String valorHoraFormatada = formatarHora.format(dataCompetenciaEditada);
		
		// Gera a descrição para a nova data de competência
		return DATA_PROXIMO_MES + valorDataFormatada + " " + valorHoraFormatada;
		
	}
	
	/**
	 * Getters para RNs e DAOs
	 */
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}	
	
	protected ExecutarFechamentoEstoqueMensalRN getExecutarFechamentoEstoqueMensalRN() {
		return executarFechamentoEstoqueMensalRN;
	}

	protected SceHistoricoFechamentoMensalDAO getSceHistoricoFechamentoMensalDAO() {
		return sceHistoricoFechamentoMensalDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
