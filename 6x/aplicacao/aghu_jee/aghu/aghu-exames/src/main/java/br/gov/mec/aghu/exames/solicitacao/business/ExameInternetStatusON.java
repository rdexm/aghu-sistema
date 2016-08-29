/**
 * 
 */
package br.gov.mec.aghu.exames.solicitacao.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.w3c.dom.Document;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.ISchedulerFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoExameInternet;
import br.gov.mec.aghu.dominio.DominioStatusExameInternet;
import br.gov.mec.aghu.dominio.DominioSubTipoImpressaoLaudo;
import br.gov.mec.aghu.exames.business.IExameInternetStatusBean;
import br.gov.mec.aghu.exames.dao.AelExameInternetGrupoDAO;
import br.gov.mec.aghu.exames.dao.AelExameInternetStatusDAO;
import br.gov.mec.aghu.exames.dao.AelExtratoItemSolicitacaoDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.solicitacao.integracao.business.IExameIntegracaoInternet;
import br.gov.mec.aghu.exames.solicitacao.vo.ConselhoProfissionalVO;
import br.gov.mec.aghu.exames.solicitacao.vo.DadosEnvioExameInternetVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameGrupoVO;
import br.gov.mec.aghu.exames.solicitacao.vo.MensagemSolicitacaoExameLiberadoGrupoVO;
import br.gov.mec.aghu.model.AelExameInternetGrupo;
import br.gov.mec.aghu.model.AelExameInternetStatus;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghJobDetail;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.criptografia.CriptografiaUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.seguranca.Token;
import br.gov.mec.aghu.core.seguranca.TokenIdentityException;

/**
 * Classe responsável por realizar a busca e inclusão dos exames oriundos do AGH
 * na fila
 * 
 * @author dcastro
 * 
 */
@Stateless
@Deprecated
public class ExameInternetStatusON extends BaseBusiness {

	private static final String PARAMETRO_NAO_INFORMADO = "Parâmetro não informado.";

	private static final Log LOG = LogFactory.getLog(ExameInternetStatusON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	@EJB
	private ProcessarFilaLaudoExames processarFilaLaudoExames;
	
	@EJB
	private ProcessarFilaExamesLiberados processarFilaExamesLiberados;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private ISchedulerFacade schedulerFacade;
	
	@Inject
	private AelExameInternetStatusDAO aelExameInternetStatusDAO;
	
	@Inject
	private AelExameInternetGrupoDAO aelExameInternetGrupoDAO;
	
	@Inject
	private AelExtratoItemSolicitacaoDAO aelExtratoItemSolicitacaoDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	private IExameIntegracaoInternet exameIntegracaoInternet;

	public enum ExameInternetStatusONExceptionCode implements
			BusinessExceptionCode {
		ERRO_CONTEXTO_XML, ERRO_CRIACAO_XML, ERRO_ENVIO_XML, SOLICITACAO_EXAME_NAO_ENCONTRADO, ERRO_CONFIGURACAO_DATA, EXAME_INTERNET_GRUPO_NAO_ENCONTRADO, 
		DATA_HORA_LIBERACAO_EXAME_NAO_ENCONTRADO, DATA_HORA_EXAME_NAO_ENCONTRADO, ERRO_NRO_CONSELHO_PROFISSIONAL, ERRO_CODIGO_PACIENTE, ERRO_CODIGO_GRUPO_EXAME, 
		LOCALIZADOR_EXAME_NAO_PREENCHIDO, ERRO_GERACAO_TOKEN, ERRO_CRIACAO_PDF;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 111167461223782587L;
	private static final String SIGLA_CREMERS = "CREMERS";
	private static final String SIGLA_CRM = "CRM";
	private static final String UF_RS = "RS";
	private static final String SPACE = " ";
	private static final String EXAMES_IMPRIMIR_RESULTADO_EXAME_EXTERNO_SEAM = "/exames/imprimirResultadoExameExterno.seam";
//	private static final String MSG_ERRO_PDF = "ocorreu um erro inesperado";

	/**
	 * Inserir as mensagens na fila ExamesLiberados através do scheduler
	 * @param job
	 * @return
	 */
	public boolean inserirFilaExamesLiberados(AghJobDetail job) {

		LOG.info("Tarefa: inserirExamesFila - Inicio ...");
		getSchedulerFacade().adicionarLog(job,
				"Tarefa: inserirExamesFila - Inicio ...");
		Integer maxResults = 100;
		
		if (getParametroFacade().verificarExisteAghParametro(AghuParametrosEnum.P_AGHU_MAX_REG_PROC_EXAMES_INTERNET)) {
			try {
				maxResults = getParametroFacade().buscarValorInteiro(AghuParametrosEnum.P_AGHU_MAX_REG_PROC_EXAMES_INTERNET);
			} catch (ApplicationBusinessException e) {
				LOG.error(e.getMessage(), e);
			}
		} 
		else {
			maxResults = 100;
		}		
		
		// pesquisar as solcitações de exames que estão liberadas e não processadas
		List<MensagemSolicitacaoExameLiberadoGrupoVO> list = this
				.getAelExameInternetStatusDAO()
				.buscarExamesAgrupadosPorSolicitacaoArea(
						DominioStatusExameInternet.LI, DominioSituacaoExameInternet.N, maxResults);
		
		getSchedulerFacade().adicionarLog(job, "Processando " + list.size() + " registros");
		
		// processa os exames, enviando para a fila
		for (MensagemSolicitacaoExameGrupoVO mensagemSolicitacaoExameGrupoVO : list) {
			getSchedulerFacade().adicionarLog(
					job,
					"Tarefa: inserirExamesFila - processando soeSeq + seqGrupo: "
							+ mensagemSolicitacaoExameGrupoVO
									.getSeqSolicitacaoExame()
							+ " + "
							+ mensagemSolicitacaoExameGrupoVO
									.getSeqExameInternetGrupo());

			if (this.inserirFilaExamesLiberados(mensagemSolicitacaoExameGrupoVO, false)) {
				getSchedulerFacade().adicionarLog(
						job,
						"Tarefa: inserirExamesFila - inseriu na fila soeSeq + seqGrupo: "
								+ mensagemSolicitacaoExameGrupoVO
										.getSeqSolicitacaoExame()
								+ " + "
								+ mensagemSolicitacaoExameGrupoVO
										.getSeqExameInternetGrupo());
			} else {
				getSchedulerFacade().adicionarLog(
						job,
						"Tarefa: inserirExamesFila - problema inserir na fila: "
								+ mensagemSolicitacaoExameGrupoVO
										.getSeqSolicitacaoExame()
								+ " + "
								+ mensagemSolicitacaoExameGrupoVO
										.getSeqExameInternetGrupo());

			}
		}

		getSchedulerFacade().adicionarLog(job,
				"Tarefa: inserirExamesFila - Final.");
		return true;
	}

	/**
	 * Inserir na fila ExamesLiberados (tanto um novo envio como também reenvio através da tela de gerenciamento dos log's
	 * @param mensagemSolicitacaoExameGrupoVO
	 * @param isReenvio
	 * @return
	 */
	public boolean inserirFilaExamesLiberados(
			MensagemSolicitacaoExameGrupoVO mensagemSolicitacaoExameGrupoVO, boolean isReenvio) {

		DominioStatusExameInternet statusAtualiza = null;
		DominioStatusExameInternet statusNovo = null;
		
		IExameInternetStatusBean exameInternetStatusBean = ServiceLocator.getBean(IExameInternetStatusBean.class, "aghu-exames");
		
		IExameInternetStatusBean exameInternetStatusFinalizaBean = ServiceLocator.getBean(IExameInternetStatusBean.class, "aghu-exames");
		
		if(!isReenvio ){
			statusAtualiza = DominioStatusExameInternet.LI; 
			statusNovo = DominioStatusExameInternet.FG;
		}else{
			AelExameInternetStatus exameInternetStatus = this.getAelExameInternetStatusDAO().buscarExameInternetStatus(mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame(), mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo(), null, null).get(0);
			exameInternetStatusBean.inserirStatusInternet(exameInternetStatus.getSolicitacaoExames(), exameInternetStatus.getItemSolicitacaoExames(), new Date(), DominioSituacaoExameInternet.N, DominioStatusExameInternet.RE, null, null);
			
			statusAtualiza = DominioStatusExameInternet.RE;
		}		
		
		try {

			this.getProcessarFilaExamesLiberados().enviar(
					mensagemSolicitacaoExameGrupoVO);
			exameInternetStatusBean.atualizarStatusInternet(
					mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame(),
					mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo(),
					statusAtualiza, DominioSituacaoExameInternet.R, statusNovo,
					null);
			return true;

		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			exameInternetStatusFinalizaBean.atualizarStatusInternet(
					mensagemSolicitacaoExameGrupoVO.getSeqSolicitacaoExame(),
					mensagemSolicitacaoExameGrupoVO.getSeqExameInternetGrupo(),
					statusAtualiza, DominioSituacaoExameInternet.E, null,
					e.getMessage());
			return false;
		}

	}
	
	
	/**
	 * Método responsável por gerar o xml de envio dos resultados
	 * 
	 * @param solicitacaoExame
	 * @param listaItensAgrupados
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String gerarXmlEnvioExameInternet(
			List<AelItemSolicitacaoExames> listaItensAgrupados,
			Integer seqExameInternetGrupo) throws BaseException {

		if (listaItensAgrupados == null || seqExameInternetGrupo == null) {
			throw new ApplicationBusinessException(
					ExameInternetStatusONExceptionCode.SOLICITACAO_EXAME_NAO_ENCONTRADO);
		}

		DadosEnvioExameInternetVO dadosEnvioExameInternetVO = new DadosEnvioExameInternetVO();
		AelSolicitacaoExames solicitacaoExame = listaItensAgrupados.get(0)
				.getSolicitacaoExame();

		this.atualizaGrupoExame(dadosEnvioExameInternetVO, seqExameInternetGrupo);
		
		dadosEnvioExameInternetVO.setSoeSeq(solicitacaoExame.getSeq()
				.toString());
		dadosEnvioExameInternetVO.setLocalizador(solicitacaoExame
				.getLocalizador());
		
		atualizaDadosPaciente(solicitacaoExame,	dadosEnvioExameInternetVO);
		if(DominioOrigemAtendimento.X.equals(solicitacaoExame.getAtendimento().getOrigem())){
			dadosEnvioExameInternetVO.setConselhoProfissionalMedico(this.atualizaConselhoMedicoExterno(solicitacaoExame
							.getAtendimento()));
		}else{
			dadosEnvioExameInternetVO.setConselhoProfissionalMedico(this.atualizaDadosConselhoMedico(solicitacaoExame
							.getServidorResponsabilidade()));
		}

		//Quando origem de atendimento for 'I - INTERNACAO' informa o médico responsável do atendimento 
		if(DominioOrigemAtendimento.I.equals(solicitacaoExame.getAtendimento().getOrigem())){			
			dadosEnvioExameInternetVO.setConselhoProfissionalResponsavel(this.atualizaDadosConselhoMedico(solicitacaoExame.getAtendimento().getServidor()));
		}

		if (solicitacaoExame.getConvenioSaude() != null) {
			dadosEnvioExameInternetVO.setConvenio(solicitacaoExame
					.getConvenioSaude().getDescricao());
		}

		//dadosEnvioExameInternetVO.setDataLiberacaoExame(this.buscaDataLiberacao(solicitacaoExame, seqExameInternetGrupo));
		
		try {
			dadosEnvioExameInternetVO.setDataLiberacaoExame(obterDataFormatada(listaItensAgrupados.get(0).getDthrLiberada(), true));
		} catch (DatatypeConfigurationException e) {
			throw new ApplicationBusinessException(
					ExameInternetStatusONExceptionCode.ERRO_CONFIGURACAO_DATA);
		}
		
		dadosEnvioExameInternetVO.setDataExame(this.buscaDataExame(solicitacaoExame));		
		dadosEnvioExameInternetVO.setNotaAdicional(this.possuiNotaAdicional(listaItensAgrupados));

		if (solicitacaoExame.getAtendimento() != null
				&& solicitacaoExame.getAtendimento()
						.getAtendimentoPacienteExterno() != null
				&& solicitacaoExame.getAtendimento()
						.getAtendimentoPacienteExterno()
						.getLaboratorioExterno() != null) {
			dadosEnvioExameInternetVO.setCnpjInstituicao(this.validaCnpj(solicitacaoExame
					.getAtendimento().getAtendimentoPacienteExterno()
					.getLaboratorioExterno().getCgc()));
		}

		return this.getExameIntegracaoInternet().gerarXmlEnvio(
				dadosEnvioExameInternetVO);
	}


	/**
	 * Validar o CNPJ que está sendo enviado
	 * @param cnpj
	 * @return
	 */
	private String validaCnpj(String cnpj) {
		// Acrescenta a consulta o critério do CGC/CNPJ do fornecedor
		if (CoreUtil.validarCNPJ(cnpj)) {
			return cnpj;
		} else {
			return null;
		}
	}
	
	/**
	 * Atualizar o conselho médico quando o atendimento for externo
	 * @param atendimento
	 * @return
	 */
	private ConselhoProfissionalVO atualizaConselhoMedicoExterno(AghAtendimentos atendimento){

		if(atendimento == null){
			throw new IllegalArgumentException(PARAMETRO_NAO_INFORMADO);
		}

		ConselhoProfissionalVO conselhoProfissional = new ConselhoProfissionalVO();
		if (atendimento.getAtendimentoPacienteExterno() != null
				&& atendimento.getAtendimentoPacienteExterno()
						.getMedicoExterno() != null
				&& atendimento.getAtendimentoPacienteExterno()
						.getMedicoExterno().getCrm() != null) {
			conselhoProfissional.setRegConselhoMedico(atendimento.getAtendimentoPacienteExterno()
							.getMedicoExterno().getCrm().toString()
							);
		}
		conselhoProfissional.setSiglaConselhoMedico(SIGLA_CRM);
		conselhoProfissional.setUfConselhoMedico(UF_RS);
		
		
		return conselhoProfissional;
	}
	
	/**
	 * Retorna a data formatada para ser utilizada na geração do XML
	 * 
	 * @param data
	 * @param comHoraMinSeg
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	private XMLGregorianCalendar obterDataFormatada(Date data,
			boolean comHoraMinSeg) throws DatatypeConfigurationException {

		if (data == null) {
			return null;
		} else {
			DatatypeFactory df = DatatypeFactory.newInstance();
			DateTime dt = new DateTime(data);

			XMLGregorianCalendar xmlGregorianCalendar = df
					.newXMLGregorianCalendar();
			xmlGregorianCalendar.setDay(dt.getDayOfMonth());
			xmlGregorianCalendar.setMonth(dt.getMonthOfYear());
			xmlGregorianCalendar.setYear(dt.getYear());
			if (comHoraMinSeg) {
				xmlGregorianCalendar.setTime(dt.getHourOfDay(),
						dt.getMinuteOfHour(), dt.getSecondOfMinute());
			}

			return xmlGregorianCalendar;
		}

	}

	/**
	 * Insere os dados do exame na fila: Solicitação, Grupo e arquivos PDF e XML
	 * 
	 * @param soeSeq
	 * @param seqGrupo
	 * @param arquivoLaudo
	 * @param arquivoXml
	 */
	public void inserirFilaLaudoExames(AelSolicitacaoExames solicitacaoExame, List<AelItemSolicitacaoExames> listItemSolicitacao, Integer seqGrupo,
			byte[] arquivoLaudo, String arquivoXml) {
		LOG.info("Tarefa: inserirFilaLaudoExames - Inicio ...");
		this.getProcessarFilaLaudoExames().enviar(solicitacaoExame.getSeq(), seqGrupo, solicitacaoExame.getLocalizador(),
				arquivoLaudo, arquivoXml);
		
		IExameInternetStatusBean exameInternetStatusBean = ServiceLocator.getBean(IExameInternetStatusBean.class, "aghu-exames");
		
		for(AelItemSolicitacaoExames itemSolicitacaoExames : listItemSolicitacao ){
			exameInternetStatusBean.inserirStatusInternet(solicitacaoExame, itemSolicitacaoExames, null, DominioSituacaoExameInternet.N, DominioStatusExameInternet.FE, null, null);			
		}

		LOG.info("Tarefa: inserirFilaLaudoExames - Fim ...");
	}

	/**
	 * Geração do Token para chamada do relatório de laudo dos exames
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String gerarToken() throws BaseException{
		AghParametros usuarioParam = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_USUARIO_CONSULTA_LAUDO_EXAME);
		
		String login = usuarioParam.getVlrTexto();		
		String encryptedPassword = CriptografiaUtil.criptografar(login);
		String token = null;
		
		try {
			token = Token.createToken(login,encryptedPassword);			
			
		} catch (TokenIdentityException e) {
			LOG.error("Problema na geração do TOKEN", e);
			throw new ApplicationBusinessException(ExameInternetStatusONExceptionCode.ERRO_GERACAO_TOKEN);
		}
		
		return token;
	}


	/**
	 * Atualizar os dados do Paciente do Exame
	 * @param solicitacaoExame
	 * @param dadosEnvioExameInternetVO
	 * @throws ApplicationBusinessException
	 */
	private void atualizaDadosPaciente(AelSolicitacaoExames solicitacaoExame,
			DadosEnvioExameInternetVO dadosEnvioExameInternetVO)
			throws ApplicationBusinessException {
		
		if(solicitacaoExame == null || dadosEnvioExameInternetVO == null){
			throw new IllegalArgumentException("Parâmetros não informados.");
		}
		
		if (solicitacaoExame.getAtendimento() != null
				&& solicitacaoExame.getAtendimento().getPaciente() != null) {
			try {
				dadosEnvioExameInternetVO.setCodigoPaciente(BigInteger
						.valueOf(solicitacaoExame.getAtendimento()
								.getPaciente().getCodigo().longValue()));
			} catch (NumberFormatException e) {
				throw new ApplicationBusinessException(
						ExameInternetStatusONExceptionCode.ERRO_CODIGO_PACIENTE);
			}

			if (solicitacaoExame.getAtendimento().getPaciente()
					.getDtNascimento() != null) {
				try {
					dadosEnvioExameInternetVO.setDataNascimentoPaciente(this
							.obterDataFormatada(solicitacaoExame
									.getAtendimento().getPaciente()
									.getDtNascimento(), false));
				} catch (DatatypeConfigurationException e) {
					throw new ApplicationBusinessException(
							ExameInternetStatusONExceptionCode.ERRO_CONFIGURACAO_DATA);
				}
			}
			dadosEnvioExameInternetVO.setNomePaciente(solicitacaoExame
					.getAtendimento().getPaciente().getNome());
		}

	}
	
	/**
	 * Atualizar dados do conselho médico a partir do servidor informado
	 * @param servidor
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private ConselhoProfissionalVO atualizaDadosConselhoMedico(RapServidores servidor) throws ApplicationBusinessException{

		if(servidor == null){
			throw new IllegalArgumentException(PARAMETRO_NAO_INFORMADO);
		}

		ConselhoProfissionalVO conselhoProfissionalMedicoVO = new ConselhoProfissionalVO();
		
		String nroRegConselhoProfissional = this.getNumeroConselhoProfissional(
				servidor.getId().getMatricula(), servidor.getId()
						.getVinCodigo());
		if (!StringUtils.isBlank(nroRegConselhoProfissional)) {
			try {
				conselhoProfissionalMedicoVO.setRegConselhoMedico(nroRegConselhoProfissional);
			} catch (NumberFormatException e) {
				LOG.error("Erro ao tentar converter CRM " + nroRegConselhoProfissional  +  " para inteiro ");
				LOG.error(e.getMessage(), e);
			}

		}

		conselhoProfissionalMedicoVO.setSiglaConselhoMedico(this
				.getSiglaConselhoProfissional(servidor.getId().getMatricula(),
						servidor.getId().getVinCodigo()));		
						
		conselhoProfissionalMedicoVO.setUfConselhoMedico(UF_RS);
		
		return conselhoProfissionalMedicoVO;
	}
	
	/**
	 * Retornar o Número do Conselho Profissional
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	private String getNumeroConselhoProfissional(Integer matricula, Short vinCodigo){

		return this.getCadastrosBasicosFacade()
				.obterRapQualificaoComNroRegConselho(
						matricula,
						vinCodigo, DominioSituacao.A);
		
	}

	/**
	 * Retornar a Sigla do Conselho Profissional
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	private String getSiglaConselhoProfissional(Integer matricula, Short vinCodigo) {
		List<RapConselhosProfissionais> rapConselhoProfissional = this.getCadastrosBasicosFacade()
				.listarConselhoProfissionalComNroRegConselho(matricula,	vinCodigo, DominioSituacao.A);
		
		if (rapConselhoProfissional != null && rapConselhoProfissional.size() > 1) {
			ordenaPorConselhoMedicina(rapConselhoProfissional);
		}
		
		if (rapConselhoProfissional != null && !rapConselhoProfissional.isEmpty() && !StringUtils.isBlank(rapConselhoProfissional.get(0).getSigla())) {
			return rapConselhoProfissional.get(0).getSigla().equals(SIGLA_CREMERS) ? SIGLA_CRM	: rapConselhoProfissional.get(0).getSigla();
		} 
		else {
			return null;
		}		
				
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * Ordena primeiro os conselhos regionais de medicina
	 * 
	 * @param listaConselhoProfissional
	 */
	private void ordenaPorConselhoMedicina(List<RapConselhosProfissionais> listaConselhoProfissional) {

		final List<AipUfs> listaUfs = getCadastrosBasicosPacienteFacade().listarTodosUFs();
		final String CONS_CREME = "CREME";
		final String CONS_CRM = "CRM";
		
		Collections.sort(listaConselhoProfissional, new Comparator<RapConselhosProfissionais>() {    
            public int compare(RapConselhosProfissionais cons1, RapConselhosProfissionais cons2) {    
            	RapConselhosProfissionais c1 = (RapConselhosProfissionais) cons1;    
            	//RapConselhosProfissionais c2 = (RapConselhosProfissionais) cons2;    
                
            	if (!StringUtils.isBlank(c1.getSigla()) ) {
            		
            		for (AipUfs uf : listaUfs) {
            			if ( (c1.getSigla().equals(CONS_CREME + uf.getSigla())) || (c1.getSigla().equals(CONS_CRM + uf.getSigla())) ) {
            				return -1;
            			}
            		}
            		
            	}
            	
            	return 1;
              }  
		});
		
	}
	
	/**
	 * Montar a descrição do conselho profissional
	 * @param matricula
	 * @param vinCodigo
	 * @return
	 */
	private String montarDescricaoConselhoProfissional(RapServidores servidor) {
		String siglaConselho = this.getSiglaConselhoProfissional(servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
		String numeroConselho = this.getNumeroConselhoProfissional(servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
		StringBuilder descricaoConselho = new StringBuilder();

		if (siglaConselho != null) {
			descricaoConselho.append(siglaConselho).append(SPACE);
		}

		descricaoConselho.append(UF_RS).append(SPACE);

		if (numeroConselho != null) {
			descricaoConselho.append(numeroConselho);
		}

		return descricaoConselho.toString();

	}
	
	/**
	 * Montar a descrição do conselho profissional para atendimento externo
	 * @param atendimento
	 * @return
	 */
	private String montarDescricaoConselhoProfissionalExterno(AghAtendimentos atendimento){				
		
		if(atendimento == null){
			throw new IllegalArgumentException(PARAMETRO_NAO_INFORMADO);
		}

		StringBuilder descricaoConselho = new StringBuilder();
		descricaoConselho.append(SIGLA_CRM).append(SPACE)
		.append(UF_RS).append(SPACE);
		
		if (atendimento.getAtendimentoPacienteExterno() != null
				&& atendimento.getAtendimentoPacienteExterno()
						.getMedicoExterno() != null
				&& atendimento.getAtendimentoPacienteExterno()
						.getMedicoExterno().getCrm() != null) {
			descricaoConselho.append((atendimento.getAtendimentoPacienteExterno()
							.getMedicoExterno().getCrm())).append(SPACE);
		}
		
		return descricaoConselho.toString();
		
		
	}
	
	/**
	 * Retornar a descrição do solicitante do exame, com tratamento quando atendimento for externo
	 * @param exameInternetStatus
	 * @return
	 */
	public String obterSolicitanteExame(AelExameInternetStatus exameInternetStatus){
		
		if(exameInternetStatus != null && exameInternetStatus.getSolicitacaoExames() != null && exameInternetStatus.getSolicitacaoExames().getAtendimento() != null && DominioOrigemAtendimento.X.equals(exameInternetStatus.getSolicitacaoExames().getAtendimento().getOrigem())){
			return this.montarDescricaoConselhoProfissionalExterno(exameInternetStatus.getSolicitacaoExames().getAtendimento());
		}else{
			if(exameInternetStatus.getSolicitacaoExames().getServidorResponsabilidade() != null){
				return this.montarDescricaoConselhoProfissional(exameInternetStatus.getSolicitacaoExames().getServidorResponsabilidade());
			}
		}

		return null;
	}
	
	/**
	 * Buscar a data de efetivação do exame
	 * @param solicitacaoExame
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private XMLGregorianCalendar buscaDataExame(
			AelSolicitacaoExames solicitacaoExame) throws ApplicationBusinessException {

		if(solicitacaoExame == null){
			throw new IllegalArgumentException(PARAMETRO_NAO_INFORMADO);
		}

		XMLGregorianCalendar dtRetorno = null;
		Date dtExame = this.getAelExtratoItemSolicitacaoDAO()
				.obterMenorDataAreaExecutoraPorSolicitacaoExame(
						solicitacaoExame.getSeq());
		if (dtExame == null) {
			throw new ApplicationBusinessException(
					ExameInternetStatusONExceptionCode.DATA_HORA_EXAME_NAO_ENCONTRADO);
		} else {
			try {
				dtRetorno = this.obterDataFormatada(dtExame, true);
			} catch (DatatypeConfigurationException e) {
				throw new ApplicationBusinessException(
						ExameInternetStatusONExceptionCode.ERRO_CONFIGURACAO_DATA);
			}

		}
		
		
		return dtRetorno;
	}
	
	/**
	 * Verificar se o éxame possui nota adicional
	 * @param listaItensAgrupados
	 * @return
	 */
	private boolean possuiNotaAdicional(List<AelItemSolicitacaoExames> listaItensAgrupados){

		if(listaItensAgrupados == null){
			throw new IllegalArgumentException(PARAMETRO_NAO_INFORMADO);
		}

		boolean possuiNotaAdicional = false;
		for (AelItemSolicitacaoExames item : listaItensAgrupados) {
			if (item.getNotaAdicional() != null && !item.getNotaAdicional().isEmpty()) {
				possuiNotaAdicional = true;
				break;
			}
		}
		
		return possuiNotaAdicional;
	}
	
	/**
	 * Atualizar os dados do grupo de exame
	 * @param dadosEnvioExameInternetVO
	 * @param seqExameInternetGrupo
	 * @throws ApplicationBusinessException
	 */
	private void atualizaGrupoExame(DadosEnvioExameInternetVO dadosEnvioExameInternetVO, Integer seqExameInternetGrupo) throws ApplicationBusinessException{

		if(dadosEnvioExameInternetVO == null || seqExameInternetGrupo == null){
			throw new IllegalArgumentException("Parâmetros não informados.");
		}

		AelExameInternetGrupo exameInternetGrupo = this
				.getAelExameInternetGrupoDAO().buscarExameInterGrupo(
						seqExameInternetGrupo);

		if (exameInternetGrupo == null) {
			throw new ApplicationBusinessException(
					ExameInternetStatusONExceptionCode.EXAME_INTERNET_GRUPO_NAO_ENCONTRADO);
		}

		try {
			dadosEnvioExameInternetVO.setSeqGrupo(BigInteger
					.valueOf(exameInternetGrupo.getSeq().longValue()));
		} catch (NumberFormatException e) {
			throw new ApplicationBusinessException(
					ExameInternetStatusONExceptionCode.ERRO_CODIGO_GRUPO_EXAME);
		}

		if (!StringUtils.isBlank(exameInternetGrupo.getDescricao())) {
			dadosEnvioExameInternetVO.setDescricaoGrupo(exameInternetGrupo
					.getDescricao().length() <= 30 ? exameInternetGrupo
					.getDescricao() : exameInternetGrupo.getDescricao()
					.substring(0, 30));
		}
		
	}
	
	/**
	 * 
	 * @param solicitacaoExame
	 * @param token
	 * @param jsessionid
	 * @return
	 * @throws IOException
	 * @throws ApplicationBusinessException
	 */
	public ByteArrayOutputStream buildResultadoPDF(
			AelSolicitacaoExames solicitacaoExame, Integer seqGrupoExame,
			String token) throws IOException,
			ApplicationBusinessException {
		
		//TODO: RETIRAR após a solução da arquitetura ser liberada
		//boolean atualizouIdentity = this.iniciarContexto();
		
		DominioSubTipoImpressaoLaudo subTipoImpressaoLaudo = DominioSubTipoImpressaoLaudo.LAUDO_GERAL;
		
		AghParametros urlParametro = getParametroFacade()
				.buscarAghParametro(
						AghuParametrosEnum.P_URL_BASE_RELATORIO);
		
		StringBuilder baseUrl = new StringBuilder(urlParametro.getVlrTexto());
		
		URL url = null;

		baseUrl.append(EXAMES_IMPRIMIR_RESULTADO_EXAME_EXTERNO_SEAM);				
		token = URLEncoder.encode(token, "UTF-8");
		baseUrl.append("?tkn=" ).append( token)
		.append("&paramSoeSeq=" ).append(solicitacaoExame.getSeq().toString())
		.append("&paramGrupoSeq=" ).append( seqGrupoExame.toString())
		.append("&paramSubTipoImpressao=" ).append( subTipoImpressaoLaudo.toString());
		
		url = new URL(baseUrl.toString());

		LOG.info("Tentando gerar pdf pela URL: " + baseUrl.toString());
		
		HttpURLConnection httpConnection = null;
		ByteArrayOutputStream outPDF = null;
		
		try {
			httpConnection = (HttpURLConnection) url.openConnection();
			
//			InputStream inXHTML = httpConnection.getInputStream();

			//TODOMIGRACAO
			// parse xhtml para document
//			Tidy tidy = new Tidy();
//			tidy.setInputEncoding("UTF-8");
//			tidy.setOutputEncoding("UTF-8");
//			tidy.setShowWarnings(false);
//			Document doc = tidy.parseDOM(inXHTML, null);
//			String pdfString = getStringFromDocument(doc);
			
//			if (pdfString.toLowerCase().contains(MSG_ERRO_PDF)) {
//				throw new ApplicationBusinessException(ExameInternetStatusONExceptionCode.ERRO_CRIACAO_PDF, baseUrl.toString());
//			}
//	
//			// transforma xhtml em pdf
//			AGHUITextRenderer renderer = new AGHUITextRenderer();
//			renderer.setDocument(doc, null, new XhtmlNamespaceHandler());
//			outPDF = new ByteArrayOutputStream();
//			renderer.layout();
//			renderer.createPDF(outPDF);
		}
		finally {
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		
		//TODO: RETIRAR após a solução da arquitetura ser liberada
		/*
		if(atualizouIdentity){
			Contexts.getApplicationContext().set(
					"org.jboss.seam.security.identity", null);
		}
		*/
		return outPDF;
	}
	
	/**
	 * Metodo para converter doc em string
	 * 
	 * @param doc
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public String getStringFromDocument(Document doc) throws ApplicationBusinessException {
		try {
			DOMSource domSource = new DOMSource(doc);
			StringWriter writer = new StringWriter();
			StreamResult result = new StreamResult(writer);
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();
			transformer.transform(domSource, result);
			return writer.toString();
		} catch (TransformerException ex) {
			throw new ApplicationBusinessException(ExameInternetStatusONExceptionCode.ERRO_CRIACAO_PDF, "pdf inválido");
		}
	}
	
	/**
	 * Inicia o contexto para chamada do relatório
	 * @throws ApplicationBusinessException
	 */
	/*
	private boolean iniciarContexto() throws ApplicationBusinessException {

		Identity identity = (Identity) Contexts.getApplicationContext().get(
				"identity");
		if (identity == null) {
			AghParametros usuarioParam = getParametroFacade()
					.buscarAghParametro(
							AghuParametrosEnum.P_AGHU_USUARIO_CONSULTA_LAUDO_EXAME);
			String login = usuarioParam.getVlrTexto();
			identity = Identity.instance();
			Principal principal = new SimplePrincipal(login);
			identity.acceptExternallyAuthenticatedPrincipal(principal);
			Contexts.getApplicationContext().set(
					"org.jboss.seam.security.identity", identity);
			return true;
		}else{
			return false;
		}

	}
	*/
	
	protected ISchedulerFacade getSchedulerFacade() {
		return this.schedulerFacade;
	}
	
	protected AelExameInternetStatusDAO getAelExameInternetStatusDAO() {
		return aelExameInternetStatusDAO;
	}

	protected ProcessarFilaExamesLiberados getProcessarFilaExamesLiberados() {
		return processarFilaExamesLiberados;
	}

	protected AelExameInternetGrupoDAO getAelExameInternetGrupoDAO() {
		return aelExameInternetGrupoDAO;
	}

	protected AelExtratoItemSolicitacaoDAO getAelExtratoItemSolicitacaoDAO() {
		return aelExtratoItemSolicitacaoDAO;
	}

	protected ICadastrosBasicosFacade getCadastrosBasicosFacade() {
		return cadastrosBasicosFacade;
	}

	protected ProcessarFilaLaudoExames getProcessarFilaLaudoExames() {
		return processarFilaLaudoExames;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}

	protected IExameIntegracaoInternet getExameIntegracaoInternet() {
		return exameIntegracaoInternet;
	}
	
	// Getters and Setters
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}
	
	protected ICadastrosBasicosPacienteFacade getCadastrosBasicosPacienteFacade() {
		return this.cadastrosBasicosPacienteFacade;
	}

	
}