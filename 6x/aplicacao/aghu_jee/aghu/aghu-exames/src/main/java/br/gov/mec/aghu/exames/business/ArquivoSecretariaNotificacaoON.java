package br.gov.mec.aghu.exames.business;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoCodificadoDAO;
import br.gov.mec.aghu.exames.dao.AelResultadoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.VAelExameMatAnaliseDAO;
import br.gov.mec.aghu.exames.vo.ArquivoSecretariaVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificadoId;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelResultadoExameId;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ArquivoSecretariaNotificacaoON extends BaseBusiness {
	
	
	@EJB
	private AelConfigMapaRN aelConfigMapaRN;
	
	@EJB
	private ArquivoSecretariaNotificacaoRN arquivoSecretariaNotificacaoRN;
	
	@EJB
	private RelatorioTicketExamesPacienteON relatorioTicketExamesPacienteON;
	
	@EJB
	private AelNotaAdicionalRN aelNotaAdicionalRN;
	
	private static final Log LOG = LogFactory.getLog(ArquivoSecretariaNotificacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AelResultadoExameDAO aelResultadoExameDAO;
	
	@Inject
	private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;
	
	@Inject
	private AelResultadoCodificadoDAO aelResultadoCodificadoDAO;
	
	@Inject
	private VAelExameMatAnaliseDAO vAelExameMatAnaliseDAO;

	private static final long serialVersionUID = 5935040216620202746L;
	
	private static final Integer NUMERO_DIAS_MAXIMO_CONSULTA = 180;

	public enum ArquivoSecretariaNotificacaoONExceptionCode implements BusinessExceptionCode{
		AEL_01467_DATA_INICIAL_MAIOR_DATA_FINAL, MSG_ERRO_NENHUM_RETORNO_PARA_CONSULTA_PERIODO_INFORMADO,
		MSG_ERRO_PERIODO_INFORMADO_MAIOR_180_DIAS;
	}
	
	public enum ArquivoSecretariaNotificacaoONTarget {
		EXAMES_NOTIF_("EXAMES_NOTIF_"),
		EXAMES_NOTIF_CD4_("EXAMES_NOTIF_CD4_"), 
		SEPARADOR(";"),
		ENCODE("ISO-8859-1"),
		CONTENT_TYPE("text/csv"),
		QUEBRA_LINHA("\n"),
		EXTENSAO(".CSV"),
		DD_MM_YYYY("dd/MM/yyyy");
		
		@SuppressWarnings("PMD.AtributoEmSeamContextManager")
		private String str;
		
		private ArquivoSecretariaNotificacaoONTarget(String str) {
			this.str = str;
		}
		
		@Override
		public String toString() {
			return str;
		}
	}
	
	/**
	 * Metodo para gerar arquivo para secretaria com notificações.
	 * #5781 
	 * @param dataInicio
	 * @param datafim
	 * @return
	 * @throws IOException
	 * @throws BaseException 
	 */
	public String gerarArquivoSecretaria(Date dataInicio, Date datafim, Boolean isCN4) throws BaseException {	
		try {
			return gerarArquivo(dataInicio, datafim, isCN4);
		} catch (IOException e) {
			LOG.error("Erro ao tentar gerar arquivo csv.", e);
			throw new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage());
		}
	}
	
	public void efetuarDownloadArquivoSecretaria(String fileName,Date dataInicio, Boolean infantil) throws BaseException{
		try {
			efetuarDownloadCsvArquivoSecretariaNotificacao(fileName, dataInicio, infantil);
		} catch (IOException e) {
			LOG.error("Erro ao tentar gerar arquivo csv.", e);
			throw new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage());
		}
		
	}
	
	/**
	 * Metodo irá criar, alimentar e retornar o arquivo.
	 * No caso de isCD4 ser true, irá criar, alimentar e retornar do arquivo para secretaria com notificações INFANTIS.
	 * Caso de isCD4 ser false, irá criar, alimentar e retornar do arquivo para secretaria com notificações
	 * @param dataInicio
	 * @param datafim
	 * @param isCN4
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws IOException
	 * @throws UnsupportedEncodingException
	 * @throws FileNotFoundException
	 */
	private String gerarArquivo(Date dataInicio, Date datafim, Boolean isCN4) throws ApplicationBusinessException, IOException, UnsupportedEncodingException, FileNotFoundException {
		validarDatasGerarArquivo(dataInicio, datafim);
		
		List<ArquivoSecretariaVO> result = new ArrayList<ArquivoSecretariaVO>();
		if (isCN4) {
			result = filtrarRegistrosArquivoCargaSecretaria(dataInicio, datafim);
		} else {
			result = filtrarRegistrosArquivoSecretaria(dataInicio, datafim);
		}
		
		if (result.isEmpty()) {
			throw new ApplicationBusinessException(ArquivoSecretariaNotificacaoONExceptionCode.MSG_ERRO_NENHUM_RETORNO_PARA_CONSULTA_PERIODO_INFORMADO);
		}
		
		File file = File.createTempFile(obterNomeArquivo(isCN4, dataInicio), ArquivoSecretariaNotificacaoONTarget.EXTENSAO.toString());
		Writer out = new OutputStreamWriter(new FileOutputStream(file), ArquivoSecretariaNotificacaoONTarget.ENCODE.toString());
		incluirCabecalhoArquivo(out, isCN4);
		
		for (ArquivoSecretariaVO vo : result) {
			complementarDadosArquivoSecretariaNotificacao(vo);
			addRegistroArquivo(out, vo, isCN4);
		}
		
		out.flush();
		out.close();
		return file.getAbsolutePath();
	}

	/**
	 * Filtro da clausula where que usa função, na consulta da 5783
	 * @param dataInicio
	 * @param datafim
	 * @return
	 */
	private List<ArquivoSecretariaVO> filtrarRegistrosArquivoCargaSecretaria(Date dataInicio, Date datafim) {
//		List<ArquivoSecretariaVO> todos;
		List<AelResultadoExame> todos;
		todos = getAelItemSolicitacaoExameDAO().pesquisarDadosArquivoSecretariaCarga(dataInicio, datafim);
		
		List<ArquivoSecretariaVO> filtrados = new ArrayList<ArquivoSecretariaVO>();
		
//		for (ArquivoSecretariaVO vo : todos) {
		for (AelResultadoExame res : todos) {
//			AelItemSolicitacaoExamesId iseId = new AelItemSolicitacaoExamesId();
//			iseId.setSeqp(vo.getItem());
//			iseId.setSoeSeq(vo.getSolicitacao());
//			
//			AelItemSolicitacaoExames ise = new AelItemSolicitacaoExames();
//			ise = getAelItemSolicitacaoExameDAO().obterPorChavePrimaria(iseId);
			
			Date dtNascPac = getRelatorioTicketExamesPacienteON().buscarLaudoDataNascimento(res.getItemSolicitacaoExame().getSolicitacaoExame());
			
			if (DateUtil.calcularDiasEntreDatas(dtNascPac, res.getItemSolicitacaoExame().getDthrLiberada()) < 365) {	
				ArquivoSecretariaVO vo = new ArquivoSecretariaVO();
				vo.setDthrLiberada(res.getItemSolicitacaoExame().getDthrLiberada());
				
				vo.setPacCodigo(res.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento().getPaciente().getCodigo());
				if(res.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento().getPaciente().getRecemNascido() != null){
					vo.setGestante(res.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento().getPaciente().getNomeMae());
				}
//				if(res.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimento() != null){
//					
//					
////				}else if (res.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimentoDiverso() != null && res.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente() != null){
//					vo.setPacCodigo(res.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente().getCodigo());
//					if(res.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente().getRecemNascidoPaciente() != null){
//						vo.setGestante(res.getItemSolicitacaoExame().getSolicitacaoExame().getAtendimentoDiverso().getAipPaciente().getRecemNascidoPaciente().getNome());
//					}
//				}
				vo.setSolicitacao(res.getId().getIseSoeSeq());
				vo.setItem(res.getId().getIseSeqp());
				vo.setPclVelEmaExaSigla(res.getId().getPclVelEmaExaSigla());
				vo.setPclVelEmaManSeq(res.getId().getPclVelEmaManSeq());
				vo.setPclVelSeqp(res.getId().getPclVelSeqp());
				vo.setPclCalSeq(res.getId().getPclCalSeq());
				vo.setPclSeqp(res.getId().getPclSeqp());
				vo.setValor(res.getValor());
				if(res.getResultadoCodificado() != null){
					vo.setRcdGtcSeq(res.getResultadoCodificado().getId().getGtcSeq());
					vo.setRcdSeqp(res.getResultadoCodificado().getId().getSeqp());
				}
				vo.setSeqp(res.getId().getSeqp());
				vo.setExame(res.getItemSolicitacaoExame().getExameMatAnalise().getNomeUsualMaterial());
//				vo.setEndereco(getArquivoSecretariaNotificacaoRN().obterEnderecoPaciente(vo.getPacCodigo()));
				filtrados.add(vo);
			}
		}
		
		return filtrados;
	}
	
//	/**
//	 * Retorna quantos dias tem entre as datas
//	 * @param dataInicio
//	 * @param dataFim
//	 * @return
//	 */
//	private Long calcularDiasEntreDatas(Date dataInicio, Date dataFim) {
//		Calendar dtInicio = Calendar.getInstance();  
//		dtInicio.setTime(dataInicio);  
//		dtInicio.set(dtInicio.get(Calendar.YEAR), dtInicio.get(Calendar.MONTH), dtInicio.get(Calendar.DATE));
//
//		Calendar dtFim = Calendar.getInstance();  
//		dtFim.setTime(dataInicio);  
//		dtFim.set(dtFim.get(Calendar.YEAR), dtFim.get(Calendar.MONTH), dtFim.get(Calendar.DATE));
//		
//		// Calcula a diferença entre as datas
//        long diferenca = dtInicio.getTimeInMillis() - dtFim.getTimeInMillis();
// 
//        // Quantidade de milissegundos em um dia
//        int tempoDia = 1000 * 60 * 60 * 24;
// 
//        long diasDiferenca = diferenca / tempoDia;
//        
//        return diasDiferenca;
//	}	
	
	/**
	 * Filtro da clausula where que  usa função, na consulta da 5781
	 * @param dataInicio
	 * @param datafim
	 * @return
	 * @author bruno.mourao
	 * @since 21/11/2012
	 */
	private List<ArquivoSecretariaVO> filtrarRegistrosArquivoSecretaria(Date dataInicio,
			Date datafim) {
		List<ArquivoSecretariaVO> todos;
		todos = getAelExameMatAnaliseDAO().pesquisarDadosArquivoSecretaria(dataInicio, datafim); // consulta da #5781
		
		List<ArquivoSecretariaVO> filtrados = new ArrayList<ArquivoSecretariaVO>();
		
		Map<AelResultadoExameId, AelResultadoExame> mapRee = new HashMap<AelResultadoExameId, AelResultadoExame>();
		Map<AelResultadoCodificadoId, AelResultadoCodificado> mapRec = new HashMap<AelResultadoCodificadoId, AelResultadoCodificado>();
		Map<String, String> mapResultado = new HashMap<String, String>();
				
		for (ArquivoSecretariaVO vo : todos) {
			if(vo.getPacCodigo() != null) {
				
				AelResultadoExameId reeId = new AelResultadoExameId();
				
				reeId.setIseSeqp(vo.getItem());
				reeId.setIseSoeSeq(vo.getSolicitacao());
				reeId.setPclCalSeq(vo.getPclCalSeq());
				reeId.setPclSeqp(vo.getPclSeqp());
				reeId.setPclVelEmaExaSigla(vo.getPclVelEmaExaSigla());
				reeId.setPclVelEmaManSeq(vo.getPclVelEmaManSeq());
				reeId.setPclVelSeqp(vo.getPclVelSeqp());
				reeId.setSeqp(vo.getSeqp());
								
				AelResultadoExame ree = null;
				if(!mapRee.containsKey(reeId)){
					ree = this.getAelResultadoExameDAO().obterResultadosExamesParaSecretaria(reeId);
					mapRee.put(reeId, ree);
				} else {
					ree = mapRee.get(reeId);
				}
				
				//ree = getAelResultadoExameDAO().obterPorChavePrimaria(reeId);
				
				String resultado = getArquivoSecretariaNotificacaoRN().obterResultado(ree);
				
				AelResultadoCodificadoId arcId = null;
				AelResultadoCodificado aec = null;
				if (vo.getRcdGtcSeq() != null && vo.getRcdSeqp() != null) {
					arcId = new AelResultadoCodificadoId(
							vo.getRcdGtcSeq(),vo.getRcdSeqp());
					
					if(!mapRec.containsKey(arcId)) {
						aec = getAelResultadoCodificadoDAO()
							.obterPorChavePrimaria(arcId);
						mapRec.put(arcId, aec);
					}else {
						aec = mapRec.get(arcId);
					}
				}
				
				String aelcBuscaResNotif = getArquivoSecretariaNotificacaoRN().aelcBuscaResNotif(
						aec, vo.getResultadoNumExp(), vo.getResultadoAlfaNum());
				
				if(resultado.equalsIgnoreCase(aelcBuscaResNotif)){
					String keyMapVo = vo.getPacCodigo().toString() 
					+ vo.getSolicitacao().toString() + vo.getItem()
					.toString()	+ DateUtil.dataToString(vo.getDthrLiberada(), 
							DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO)
					 + resultado;

					if(!mapResultado.containsKey(keyMapVo)){
						mapResultado.put(keyMapVo, resultado);
						vo.setResultado(resultado);
						filtrados.add(vo);
					}
				}
			}
		}
				
		return filtrados;
	}
	
	/**
	 * Complementa os dados do VO.
	 * @param vo
	 */
	private void complementarDadosArquivoSecretariaNotificacao(ArquivoSecretariaVO vo) {
		AelSolicitacaoExames soe = getAelSolicitacaoExameDAO().obterPorChavePrimaria(vo.getSolicitacao());
//		AelResultadoExame ree = getAelResultadoExameDAO().obterPorChavePrimaria(
//				new AelResultadoExameId(
//						vo.getSolicitacao(), vo.getItem(), 
//						vo.getPclVelEmaExaSigla(), vo.getPclVelEmaManSeq(), 
//						vo.getPclVelSeqp(), vo.getPclCalSeq(), 
//						vo.getPclSeqp(), vo.getSeqp()));
		
		vo.setNomePaciente(getAelNotaAdicionalRN().buscarLaudoNomePaciente(soe));
		vo.setIdade(getAelConfigMapaRN().laudoIdadePaciente(vo.getSolicitacao()));
		vo.setTelefone(getInternacaoFacade().obterTelefonePaciente(vo.getPacCodigo()));
		vo.setDataResultado(DateUtil.dataToString(vo.getDthrLiberada(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		vo.setDataNascimento(DateUtil.dataToString(getRelatorioTicketExamesPacienteON().buscarLaudoDataNascimento(soe), ArquivoSecretariaNotificacaoONTarget.DD_MM_YYYY.toString()));
		//vo.setResultado(getArquivoSecretariaNotificacaoRN().obterResultado(ree));
		if(vo.getEndereco() == null){
			vo.setEndereco(getArquivoSecretariaNotificacaoRN().obterEnderecoPaciente(vo.getPacCodigo()));
		}
		vo.setGestante(vo.getGestante()!=null ? vo.getGestante() : " ");
	}

	/**
	 * Gera exception caso a dataFim seja menor que a dataInicio.
	 * @param dataInicio
	 * @param datafim
	 * @throws ApplicationBusinessException
	 */
	private void validarDatasGerarArquivo(Date dataInicio, Date datafim) throws ApplicationBusinessException {
		if(dataInicio.after(datafim)){
			throw new ApplicationBusinessException(ArquivoSecretariaNotificacaoONExceptionCode.AEL_01467_DATA_INICIAL_MAIOR_DATA_FINAL);
		}
		if (DateUtil.diffInDaysInteger(datafim, dataInicio) > NUMERO_DIAS_MAXIMO_CONSULTA) {
			throw new ApplicationBusinessException(ArquivoSecretariaNotificacaoONExceptionCode.MSG_ERRO_PERIODO_INFORMADO_MAIOR_180_DIAS);
		}
	}
	
	/**
	 * Metodo para gerar arquivo para secretaria com notificações INFANTIS.
	 * #5783
	 * @param dataInicio
	 * @param datafim
	 * @return
	 * @throws BaseException 
	 * @throws IOException
	 */
	public String gerarArquivoSecretariaCarga(Date dataInicio, Date datafim, Boolean isCN4) throws BaseException {
		try {
			return gerarArquivo(dataInicio, datafim, isCN4);
		} catch (IOException e) {
			LOG.error("Erro ao tentar gerar arquivo csv.", e);
			throw new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage());
		}
	}
	
	/**
	 * Adiciona o registro no arquivo.
	 * No caso de isCD4 ser true, irá adicionar a informação referente a gestação do arquivo para secretaria com notificações INFANTIS.
	 * Caso de isCD4 ser false, não irá adicionar a informação referente a gestação.
	 * @param out
	 * @param vo
	 * @param isCD4
	 * @throws IOException
	 */
	private void addRegistroArquivo(Writer out, ArquivoSecretariaVO vo, Boolean isCD4) throws IOException {
		StringBuilder sb = new StringBuilder();
		sb.append(vo.getNomePaciente()).append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append(vo.getDataNascimento()).append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append(vo.getIdade()).append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append(vo.getEndereco()).append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append(vo.getTelefone()).append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString())
			.append(vo.getDataResultado()).append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append(vo.getSolicitacao()).append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 			
			.append(vo.getItem()).append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append(vo.getExame()).append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 			
			.append(vo.getResultado() == null ? "" : vo.getResultado()).append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString());
		if (isCD4) {
			sb.append(vo.getGestante()).append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString());			
		}
		sb.append(ArquivoSecretariaNotificacaoONTarget.QUEBRA_LINHA.toString());
		out.write(sb.toString());
	}
	
	/**
	 * Cria o cabeçalho do arquivo.
	 * No caso de isCD4 ser true, irá criar o cabeçalho do arquivo para secretaria com notificações INFANTIS.
	 * Caso de isCD4 ser false, irá criar o cabeçalho do arquivo para secretaria com notificações
	 * @param out
	 * @param isCD4
	 * @throws IOException
	 */
	private void incluirCabecalhoArquivo(Writer out, Boolean isCD4) throws IOException {
		StringBuilder sb = new StringBuilder(80);
		sb.append("Paciente").append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append("Nascimento").append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append("Idade").append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append("Endereço").append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append("Telefone").append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString())
			.append("Dt. Liberada").append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append("Solicitação").append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 			
			.append("Item").append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 
			.append("Exame").append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString()) 			
			.append("Resultado").append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString());
		if (isCD4) {
			sb.append("Gestante").append(ArquivoSecretariaNotificacaoONTarget.SEPARADOR.toString());			
		}
		sb.append(ArquivoSecretariaNotificacaoONTarget.QUEBRA_LINHA.toString());
		out.write(sb.toString());			
	}
	
	/**
	 * Retorna o nome do arquivo. 
	 * No caso de isCD4 ser true, irá retornar o nome do arquivo para secretaria com notificações INFANTIS.
	 * Caso de isCD4 ser false, irá retornar o nome do arquivo para secretaria com notificações
	 * @param isCD4
	 * @param dataIni
	 * @return
	 * @throws IOException
	 */
	public String obterNomeArquivo(Boolean isCD4, Date dataIni) {
		StringBuilder sb = new StringBuilder();
		Calendar cal = Calendar.getInstance();
		cal.setTime(dataIni);

		if (isCD4) {
			sb.append(ArquivoSecretariaNotificacaoONTarget.EXAMES_NOTIF_CD4_.toString());
		} else {
			sb.append(ArquivoSecretariaNotificacaoONTarget.EXAMES_NOTIF_.toString());			
		}

		sb.append(cal.get(Calendar.MONTH)+1)
		.append('_').append(cal.get(Calendar.YEAR))
		.append(ArquivoSecretariaNotificacaoONTarget.EXTENSAO.toString());
		return sb.toString();
	}
	
	public String nameHeaderEfetuarDownloadArquivoSecretaria(Date dataIni, Boolean isCD4) {
		return obterNomeArquivo(isCD4, dataIni);
	}

	private void efetuarDownloadCsvArquivoSecretariaNotificacao(String fileName, Date dataIni, Boolean isCD4) throws UnsupportedEncodingException, IOException {
		final FacesContext fc = FacesContext.getCurrentInstance();
		final HttpServletResponse response = (HttpServletResponse) fc.getExternalContext().getResponse();
		final String nomeDownload = obterNomeArquivo(isCD4, dataIni);
		
		response.setContentType(ArquivoSecretariaNotificacaoONTarget.CONTENT_TYPE.toString());
		response.setHeader("Content-Disposition","attachment;filename=" + nomeDownload);
		response.getCharacterEncoding();
		
		final OutputStream out = response.getOutputStream();
		final Scanner scanner = new Scanner(new FileInputStream(fileName), ArquivoSecretariaNotificacaoONTarget.ENCODE.toString());
		
		while (scanner.hasNextLine()){
			out.write(scanner.nextLine().getBytes(ArquivoSecretariaNotificacaoONTarget.ENCODE.toString()));
			out.write(System.getProperty("line.separator").getBytes(ArquivoSecretariaNotificacaoONTarget.ENCODE.toString()));
		}
		scanner.close();
		out.flush();
		out.close();
		fc.responseComplete();
	}
	
	private ArquivoSecretariaNotificacaoRN getArquivoSecretariaNotificacaoRN() {
		return arquivoSecretariaNotificacaoRN;
	}
	
	private AelNotaAdicionalRN getAelNotaAdicionalRN() {
		return aelNotaAdicionalRN;
	}
	
	private AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
	private AelConfigMapaRN getAelConfigMapaRN() {
		return aelConfigMapaRN;
	}

	private RelatorioTicketExamesPacienteON getRelatorioTicketExamesPacienteON() {
		return relatorioTicketExamesPacienteON;
	}
	
	private IInternacaoFacade getInternacaoFacade () {
		return internacaoFacade;
	}
	
	private AelResultadoExameDAO getAelResultadoExameDAO() {
		return aelResultadoExameDAO;
	}
	
	private VAelExameMatAnaliseDAO getAelExameMatAnaliseDAO() {
		return vAelExameMatAnaliseDAO;
	}
	
	private AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelResultadoCodificadoDAO getAelResultadoCodificadoDAO() {
		return aelResultadoCodificadoDAO;
	}
}
