package br.gov.mec.aghu.paciente.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoEndereco;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MamAreaAtuacao;
import br.gov.mec.aghu.model.MpmPostoSaude;
import br.gov.mec.aghu.model.VAipEnderecoPaciente;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipEnderecosPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.vo.PacientePortadorGermeVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.mail.EmailUtil;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 */
@Stateless
public class PacienteRN extends BaseBusiness {

	private final String ESPACO_BRANCO = " "; 
	private static final Log LOG = LogFactory.getLog(PacienteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@Inject
	private AipEnderecosPacientesDAO aipEnderecosPacientesDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@Inject
	private AipPacientesDAO aipPacienteDAO;
	
	@Inject
	private EmailUtil emailUtil;
	
	@EJB
	private ISchedulerFacade schedulerFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -146365378765544407L;
	
	enum PacienteRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_ENVIO_EMAIL, MENSAGEM_NAO_EXISTE_P_EMAIL_NOTIF_GMR, MENSAGEM_NAO_EXISTE_P_DIAS_NOTIF_GMR_AMBUL, MENSAGEM_NAO_EXISTE_P_AGHU_TUF_NOTIF_GMR_AMBUL, MENSAGEM_NAO_EXISTE_P_AGHU_PARAMETRO_HU;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public String mamcGetPostoPac(Integer p_pac_codigo) throws ApplicationBusinessException {
		AipEnderecosPacientesDAO aipEnderecosPacientesDAO = this.getAipEnderecosPacientesDAO();
		IAmbulatorioFacade ambulatorioFacade = this.getAmbulatorioFacade();
		IPrescricaoMedicaFacade prescricaoMedicaFacade = this.getPrescricaoMedicaFacade();
		
		Integer v_lgr_codigo = null;
		Integer v_nro_logradouro = null;
		String v_desc_logradouro = null;
		Integer v_cdd_codigo = null;
		Integer v_cod_poa;
		Integer v_atu_seq = null;
		String v_descricao_posto;

		// busca o valor da cidade padrão, se a cidade do paciente for = a esse parâmetro
		// continua buscando o posto senão vai fora. Busca só postos de Porto Alegre
		try {
			v_cod_poa = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CODIGO_CIDADE_PADRAO).getVlrNumerico().intValue();
		} catch (ApplicationBusinessException e) { // if c_param%notfound then
			if (e.getCode().equals(AghParemetrosONExceptionCode.AGH_PARAMETRO_NAO_EXISTENTE)) {
				return null;
			}
			else {
				throw e;
			}
		}
		
		// cursor c_end
		List<Object[]> c_end_result = aipEnderecosPacientesDAO.obterInformacoesEnderecoPacienteMamcGetPostoPac(p_pac_codigo,
				DominioTipoEndereco.R);
		if (c_end_result.size() > 0) {
			Object[] resultado = c_end_result.get(0);
			v_lgr_codigo = (Integer)resultado[0];
			v_nro_logradouro = (Integer)resultado[1];
			v_desc_logradouro = (String)resultado[2];
			v_cdd_codigo = (Integer)resultado[3] == null ? (Integer)resultado[4] : (Integer)resultado[3]; // nvl(lgr.cdd_codigo,ende.cdd_codigo)
		}
		
		if (v_cdd_codigo==null)
		 {
			v_cdd_codigo = 0; // nvl(v_cdd_codigo,0)
		}
		if (!v_cod_poa.equals(v_cdd_codigo)) { // if v_cod_poa <> nvl(v_cdd_codigo,0) then
			return null;
		}
		
		// cursor c_atu
		List<MamAreaAtuacao> c_atu_result = ambulatorioFacade.listarAreasAtuacaoPorCodigoLogradouroESituacao(v_lgr_codigo, DominioSituacao.A);
		if (c_atu_result.size() > 0) {  
			v_atu_seq = c_atu_result.get(0).getSeq();
		}
		
		if (v_atu_seq==null && !StringUtils.isBlank(v_desc_logradouro)) {
			// tenta procurar pela descricao do logradouro
			v_desc_logradouro = v_desc_logradouro.replace("RUA ", ""); //RETIRA RUA do nome do logradouro
			// cursor c_atu_descricao
			List<MamAreaAtuacao> c_atu_descricao_result = ambulatorioFacade.listarAreasAtuacaoPorDescricaoLogradouroESituacao(
					v_desc_logradouro, DominioSituacao.A);
			if (c_atu_descricao_result.size() == 0) {
				v_atu_seq = null;
			} else {
				v_atu_seq = c_atu_descricao_result.get(0).getSeq();
			}
		}

		// c_atu_nros (c_atu_nros_pares e c_atu_nros_impares)
		List<MpmPostoSaude> c_atu_nros_result = prescricaoMedicaFacade.listarPostosSaudePorNumeroLogradouroParesEImpares(v_atu_seq,
				v_nro_logradouro);
		if (c_atu_nros_result.size() > 0) {
			v_descricao_posto = c_atu_nros_result.get(0).getDescricao();
		} else {
			v_descricao_posto = null;
		}
		
		return v_descricao_posto;
		
	}
	
	public String buscaUfPaciente(Integer pacCodigo) {
		try {
			VAipEnderecoPaciente vAipEnderecoPaciente = this.getCadastroPacienteFacade().obterEndecoPaciente(pacCodigo);
			return vAipEnderecoPaciente.getUf();
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			return null;
		}
	}
	

	/**
	 * @ORADB MCIP_NOTIF_GMR_AMBUL
	 * @throws ApplicationBusinessException
	 */
	public void enviarEmailPacienteGMR(AghJobDetail job) throws ApplicationBusinessException {

		AghParametros paramTuf = null;		
		AghParametros paramDiasAnt = null;	
		AghParametros paramEmailNotif = null;		
		AghParametros paramHCPA = null;
		
		try {
			paramTuf = obterParamTuf(job);		
			paramDiasAnt = obterParamDiasAnt(job);	
			paramEmailNotif = obterParamEmailNotif(job);		
			paramHCPA = obterparamHCPA(job);
		} catch (ApplicationBusinessException e) {
			return;
		}
		
		Date dataAtual = new Date();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dataAtual);
		int diaDaSemana = calendar.get(Calendar.DAY_OF_WEEK);
		
		Integer diasAnt = paramDiasAnt.getVlrNumerico().intValue();		
		
		if (diaDaSemana == 6) {
			diasAnt = diasAnt + 1;
		} else if (diaDaSemana == 7) {
			diasAnt = diasAnt - 1;
		}
		
		Date dataConsulta = DateUtil.adicionaDias(dataAtual, diasAnt);
		String cabecalho = formatarCabecalhoMsgGmrAmbul();
		
		List<PacientePortadorGermeVO> listaPacientes = aipPacienteDAO.consultarPacientesPortadoresGermesMultiresistentes(dataConsulta, paramTuf.getVlrNumerico().intValue());
		
		String bacPcd = "";
		String gmrAmbul = "";
		if (listaPacientes != null && !listaPacientes.isEmpty()) {
			for (PacientePortadorGermeVO paciente : listaPacientes) {
				if (paciente.getCodBacteria() != null && (paciente.getCodBacteria().intValue() == 27 || paciente.getCodBacteria().intValue() == 79)) {
					bacPcd = "(Portador de Clostridium Difficile)";
				} else {
					bacPcd = "";
				}
								
				gmrAmbul = formatarGmrAmbul(bacPcd, gmrAmbul, paciente);
			}
		}
		
		String assunto = "NOTIFICACAO GMR AMBULATORIO " + paramHCPA.getVlrTexto();
		
		try {
			String mensagem = "Pacientes com consulta em ".concat(DateUtil.obterDataFormatada(dataConsulta, "dd/MM/yyyy")).concat(" no ambulatório com notificação de GMR: \n\n");
			if (!gmrAmbul.isEmpty()) {
				emailUtil.enviaEmail("correio@hcpa.ufrgs.br", paramEmailNotif.getVlrTexto().replaceAll(";", ""), null, assunto, mensagem.concat(cabecalho).concat(ESPACO_BRANCO).concat(gmrAmbul));
			}									
		} catch (Exception e) {
			schedulerFacade.adicionarLog(job, getResourceBundleValue("MENSAGEM_ERRO_ENVIO_EMAIL"));
			return;
		}		
	}

	private AghParametros obterparamHCPA(AghJobDetail job) throws ApplicationBusinessException {		
		AghParametros paramHCPA = null;		
		try {
			paramHCPA = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_PARAMETRO_HU);
		} catch (ApplicationBusinessException e) {
			schedulerFacade.adicionarLog(job, getResourceBundleValue("MENSAGEM_NAO_EXISTE_P_AGHU_PARAMETRO_HU"));
			throw new ApplicationBusinessException(PacienteRNExceptionCode.MENSAGEM_NAO_EXISTE_P_AGHU_PARAMETRO_HU);
		}
		if (paramHCPA.getVlrTexto() == null || paramHCPA.getVlrTexto().trim().isEmpty()) {
			schedulerFacade.adicionarLog(job, getResourceBundleValue("MENSAGEM_NAO_EXISTE_P_AGHU_PARAMETRO_HU"));
			throw new ApplicationBusinessException(PacienteRNExceptionCode.MENSAGEM_NAO_EXISTE_P_AGHU_PARAMETRO_HU);
		}
		return paramHCPA;
	}

	private AghParametros obterParamEmailNotif(AghJobDetail job) throws ApplicationBusinessException {		
		AghParametros paramEmailNotif = null;		
		try {
			paramEmailNotif = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EMAIL_NOTIF_GMR);
		} catch (ApplicationBusinessException e) {
			schedulerFacade.adicionarLog(job, getResourceBundleValue("MENSAGEM_NAO_EXISTE_P_EMAIL_NOTIF_GMR"));
			throw new ApplicationBusinessException(PacienteRNExceptionCode.MENSAGEM_NAO_EXISTE_P_EMAIL_NOTIF_GMR); 
		}
		if (paramEmailNotif.getVlrTexto() == null || paramEmailNotif.getVlrTexto().trim().isEmpty()) {
			schedulerFacade.adicionarLog(job, getResourceBundleValue("MENSAGEM_NAO_EXISTE_P_EMAIL_NOTIF_GMR"));
			throw new ApplicationBusinessException(PacienteRNExceptionCode.MENSAGEM_NAO_EXISTE_P_EMAIL_NOTIF_GMR);
		}
		return paramEmailNotif;
	}

	private AghParametros obterParamDiasAnt(AghJobDetail job) throws ApplicationBusinessException {		
		AghParametros paramDiasAnt = null;		
		try {
			paramDiasAnt = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_NOTIF_GMR_AMBUL);
		} catch (ApplicationBusinessException e) {
			schedulerFacade.adicionarLog(job, getResourceBundleValue("MENSAGEM_NAO_EXISTE_P_DIAS_NOTIF_GMR_AMBUL"));
			throw new ApplicationBusinessException(PacienteRNExceptionCode.MENSAGEM_NAO_EXISTE_P_DIAS_NOTIF_GMR_AMBUL); 
		}
		if (paramDiasAnt.getVlrNumerico() == null) {
			schedulerFacade.adicionarLog(job, getResourceBundleValue("MENSAGEM_NAO_EXISTE_P_DIAS_NOTIF_GMR_AMBUL"));
			throw new ApplicationBusinessException(PacienteRNExceptionCode.MENSAGEM_NAO_EXISTE_P_DIAS_NOTIF_GMR_AMBUL);
		}
		return paramDiasAnt;
	}

	private AghParametros obterParamTuf(AghJobDetail job) throws ApplicationBusinessException {		
		AghParametros paramTuf = null;		
		try {
			paramTuf = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_TUF_NOTIF_GMR_AMBUL);			
		} catch (ApplicationBusinessException e) {
			schedulerFacade.adicionarLog(job, getResourceBundleValue("MENSAGEM_NAO_EXISTE_P_AGHU_TUF_NOTIF_GMR_AMBUL"));
			throw new ApplicationBusinessException(PacienteRNExceptionCode.MENSAGEM_NAO_EXISTE_P_AGHU_TUF_NOTIF_GMR_AMBUL);
		}
		if (paramTuf.getVlrNumerico() == null) {
			schedulerFacade.adicionarLog(job, getResourceBundleValue("MENSAGEM_NAO_EXISTE_P_AGHU_TUF_NOTIF_GMR_AMBUL"));
			throw new ApplicationBusinessException(PacienteRNExceptionCode.MENSAGEM_NAO_EXISTE_P_AGHU_TUF_NOTIF_GMR_AMBUL);
		}
		return paramTuf;
	}

	private String formatarCabecalhoMsgGmrAmbul() {
		String cabecalho = String.format("%-61s", "Unidade")
				.concat(String.format("%-6s", "Hora"))
				.concat(String.format("%-9s", "Consulta"))
				.concat(String.format("%-7s", "Agenda"))
				.concat(String.format("%-5s", "Sala")).concat("Paciente")
				.concat("\n");
		return cabecalho;
	}

	private String formatarGmrAmbul(String bacPcd, String gmrAmbul,
			PacientePortadorGermeVO paciente) {
		gmrAmbul = gmrAmbul.concat("\n").concat(String.format("%-61s", paciente.getUnidade()))
				.concat(String.format("%-6s",paciente.getHoraConsulta().toString()))
				.concat(String.format("%-9s",paciente.getConsulta().toString()))
				.concat(String.format("%-7s",paciente.getAgenda()))
				.concat(String.format("%-5s",paciente.getSala().toString()))					
				.concat(paciente.getProntuario().toString()).concat(" - ")
				.concat(paciente.getNomePaciente()).concat(ESPACO_BRANCO)
				.concat(bacPcd);
		return gmrAmbul;
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return cadastroPacienteFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AipEnderecosPacientesDAO getAipEnderecosPacientesDAO() {
		return aipEnderecosPacientesDAO;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return this.prescricaoMedicaFacade;
	}

	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
	
}
