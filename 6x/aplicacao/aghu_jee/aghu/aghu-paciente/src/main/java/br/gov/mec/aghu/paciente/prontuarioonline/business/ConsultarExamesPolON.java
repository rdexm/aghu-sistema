package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfBoolean;
import com.itextpdf.text.pdf.PdfEncryptor;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameAmostraColetadaVO;
import br.gov.mec.aghu.exames.solicitacao.vo.ExameOrdemCronologicaVO;
import br.gov.mec.aghu.model.AelDocResultadoExame;
import br.gov.mec.aghu.model.AelDocResultadoExamesHist;
import br.gov.mec.aghu.model.AelGrupoXMaterialAnalise;
import br.gov.mec.aghu.model.AelItemSolicExameHist;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExamesId;
import br.gov.mec.aghu.model.AelNotaAdicional;
import br.gov.mec.aghu.model.AelResultadoCaracteristica;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoExame;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAelExamesLiberados;
import br.gov.mec.aghu.model.VAelPesquisaPolExameUnidade;
import br.gov.mec.aghu.model.VAelPesquisaPolExameUnidadeHist;
import br.gov.mec.aghu.paciente.dao.VAelPesquisaPolExameUnidadeDAO;
import br.gov.mec.aghu.paciente.dao.VAelPesquisaPolExameUnidadeHistDAO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ExamesPOLVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemSolicitacaoExamePolVO;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.ItemSolicitacaoExamePolVOComparator;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class ConsultarExamesPolON extends BaseBusiness {

	private static final long serialVersionUID = -4311047974043695557L;
	private static final Log LOG = LogFactory.getLog(ConsultarExamesPolON.class);

	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private VAelPesquisaPolExameUnidadeHistDAO vAelPesquisaPolExameUnidadeHistDAO;
	
	@Inject
	private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private VAelPesquisaPolExameUnidadeDAO vAelPesquisaPolExameUnidadeDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	
	
	public enum ConsultarExamesPolONExceptionCode implements BusinessExceptionCode {
		ERRO_GERAR_RELATORIO,PARAMETROS_DATA_EXAMES_CRONOLOGICO_NAO_DEFINIDO, AEL_01599, AEL_01600, AEL_03163, AEL_00353;
	}

	/**
	 * Busca dados para montagem da arvore de Laboratórios/Serviços.
	 * @param {Integer} codigoPaciente
	 * @return 
	 */
	public List<VAelPesquisaPolExameUnidade> buscaArvoreLaboratorioServicosDeExames(Integer codigoPaciente) {
		return getVAelPesquisaPolExameUnidadeDAO().buscaArvoreLaboratorioServicosDeExames(codigoPaciente);
	}
	
	public List<VAelPesquisaPolExameUnidadeHist> buscarArvoreLaboratorioServicosDeExamesHist(Integer codigoPaciente) {
		return getVAelPesquisaPolExameUnidadeHistDAO().buscarArvoreLaboratorioServicosDeExamesHist(codigoPaciente);
	}
	
	
	public List<Integer> codigosComDescricaoNulla() throws ApplicationBusinessException {
		List<Integer> listCodigos = new ArrayList<Integer>();
		AghParametros codigosComDescricaoNula = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MATERIAS_ANALISE_EXAMES_POL);
		StringTokenizer cods = new StringTokenizer(codigosComDescricaoNula.getVlrTexto(), ",");
		while(cods.hasMoreElements()){
			listCodigos.add(Integer.valueOf(cods.nextElement().toString()));
		}
		return listCodigos;
	}

	public List<ItemSolicitacaoExamePolVO> buscaExamesAtivosPeloCodigoDoPaciente(Integer codigoPaciente, Short unfSeq, Date data, Integer gmaSeq) throws ApplicationBusinessException{

		List<ItemSolicitacaoExamePolVO> listaVO = new ArrayList<ItemSolicitacaoExamePolVO>();
		List<Integer> listCodigosDescricaoNulla = this.codigosComDescricaoNulla();

		List<VAelExamesLiberados> lis = getExamesFacade().buscaExamesLiberadosPeloPaciente(codigoPaciente, unfSeq);
		for (VAelExamesLiberados exameLiberado : lis) {
			ItemSolicitacaoExamePolVO iseVO = new ItemSolicitacaoExamePolVO();

			iseVO.setDthrLiberada(exameLiberado.getDthrEventoExtratoItem());

			boolean filtroData = true;
			if (data != null) {

				filtroData = false;
				Date dataComparacao1 = DateUtil.truncaData(data);
				Date dataComparacao2 = DateUtil.truncaData(iseVO.getDthrLiberada());

				if (DateUtil.isDatasIguais(dataComparacao1, dataComparacao2)) {
					filtroData = true;
				}

			}

			if (filtroData) {
				iseVO.setSeqp(exameLiberado.getId().getSeqP().shortValue());
				iseVO.setExaDescricaoUsual(exameLiberado.getDescricaoUsual());
				
				if(listCodigosDescricaoNulla.contains(exameLiberado.getSeqMaterialAnalise())){
					iseVO.setManDescricao("");
				}else{
					iseVO.setManDescricao(exameLiberado.getDescricaoMaterialAnalise());
				}
				
				iseVO.setSituacaoCodigoDescricao(exameLiberado.getSituacaoCodigoDescricao());
				iseVO.setSoeSeq(exameLiberado.getId().getSoeSeq());
				iseVO.setUnfDescricao(exameLiberado.getUnfDescricao());
				
				//Preenche coluna de resultado
				preencheResultadoExame(aelItemSolicitacaoExameDAO.obterPorId(new AelItemSolicitacaoExamesId(iseVO.getSoeSeq(), iseVO.getSeqp())),  iseVO);

				iseVO.setTemAnexo(exameLiberado.getCodigoDocumento() != null);
				iseVO.setNotasAdicionais(exameLiberado.getQtdeNotaAdicional() != 0);
				iseVO.setTemImagem(exameLiberado.getCodigoImagem() != null);
				listaVO.add(iseVO);
			}
		}
			
		return listaVO;

	}
	
	private List<Integer> obterSeqMateriasAnalisePorGmaSeq(Integer gmaSeq){
		List<Integer> seqMateriaisAnalises = null; 
		if(gmaSeq!=null){
			//obtém todos os materiais análises que possuem o gmaSeq passado como parametro pela relação many-to-many
			List<AelGrupoXMaterialAnalise> gruposMateriaisAnalises = getExamesFacade().pesquisarGrupoXMateriaisAnalisesPorGrupo(gmaSeq);
			if(gruposMateriaisAnalises!=null){
				seqMateriaisAnalises = new ArrayList<Integer>();
			}
			for(AelGrupoXMaterialAnalise grupoMaterialAnalise : gruposMateriaisAnalises){
				seqMateriaisAnalises.add(grupoMaterialAnalise.getId().getManSeq());
			}
		}
		return seqMateriaisAnalises;
	}
	
	public List<ItemSolicitacaoExamePolVO> buscaExamesPeloCodigoDoPacienteESituacao(
			Integer codigoPaciente,
			Short unfSeq,
			Date data,
			DominioSituacaoItemSolicitacaoExame situacaoItemExame, Integer gmaSeq) throws ApplicationBusinessException {
		List<Integer> listCodigosDescricaoNulla = this.codigosComDescricaoNulla();

		 
		List<Integer> seqMateriaisAnalises = obterSeqMateriasAnalisePorGmaSeq(gmaSeq);
		
		Map<ExamesPOLVO, AelDocResultadoExame> mapISEAnexos = new HashMap<ExamesPOLVO, AelDocResultadoExame>();
		List<ExamesPOLVO> lista = new LinkedList<ExamesPOLVO>();
		List<ItemSolicitacaoExamePolVO> listaVO = new ArrayList<ItemSolicitacaoExamePolVO>();

		if (DominioSituacaoItemSolicitacaoExame.LI.equals(situacaoItemExame)) {
			listaVO = buscaExamesAtivosPeloCodigoDoPaciente(codigoPaciente, unfSeq, data, gmaSeq);
		
		}else {
			List<ExamesPOLVO> listaAtdObj = getExamesFacade().buscaExamesPeloCodigoPacienteSituacaoAtendimento(codigoPaciente, unfSeq, situacaoItemExame, seqMateriaisAnalises);
			adicionaISEHashLista(listaAtdObj, mapISEAnexos);
			lista.addAll(listaAtdObj);
		}
		
		List<ExamesPOLVO> listaAtvObj =  getExamesFacade().buscaExamesPeloCodigoPacienteSituacaoAtendimentoDiverso(codigoPaciente, unfSeq, situacaoItemExame, seqMateriaisAnalises);
		adicionaISEHashLista(listaAtvObj, mapISEAnexos);
		lista.addAll(listaAtvObj);

		for (ExamesPOLVO ise : lista) {
			ItemSolicitacaoExamePolVO iseVO = new ItemSolicitacaoExamePolVO();

			if (DominioSituacaoItemSolicitacaoExame.LI.equals(situacaoItemExame)) {
				iseVO.setDthrLiberada(ise.getDataHoraEventomaxExtratoItem());

			} else {
				iseVO.setDthrLiberada(ise.getAelItemSolicExame().getSolicitacaoExame().getCriadoEm());
			}

			boolean filtroData = true;
			if (data != null) {

				filtroData = false;
				Date dataComparacao1 = DateUtil.truncaData(data);
				Date dataComparacao2 = DateUtil.truncaData(iseVO.getDthrLiberada());

				if (DateUtil.isDatasIguais(dataComparacao1, dataComparacao2)) {
					filtroData = true;
				}

			}

			if (filtroData) {
				iseVO.setSeqp(ise.getAelItemSolicExame().getId().getSeqp());
				iseVO.setExaDescricaoUsual(ise.getAelItemSolicExame().getExame().getDescricaoUsual());
				
				if(listCodigosDescricaoNulla.contains(ise.getSeqMatAnalise())){
					iseVO.setManDescricao("");
				}else{
					iseVO.setManDescricao(ise.getDescricaoMatAnalise());
				}
				
				iseVO.setSituacaoCodigoDescricao(ise.getAelItemSolicExame().getSituacaoItemSolicitacao().getDescricao());
				iseVO.setSoeSeq(ise.getAelItemSolicExame().getSolicitacaoExame().getSeq());
				iseVO.setUnfDescricao(ise.getAelItemSolicExame().getUnidadeFuncional().getDescricao());
				
				//Preenche coluna de resultado
				preencheResultadoExame(ise.getAelItemSolicExame(), iseVO);

				//Busca o resultado dos anexos do map.
				iseVO.setTemAnexo((mapISEAnexos.containsKey(ise) && mapISEAnexos.get(ise) != null) ? Boolean.TRUE : Boolean.FALSE);
				
				iseVO.setNotasAdicionais(ise.getQtdeNotaAdicional() != 0);

				iseVO.setTemImagem(getExamesFacade().possuiExameDeImagem(ise.getAelItemSolicExame().getId()));
				
				listaVO.add(iseVO);
			}			
		}
		Collections.sort(listaVO, new ItemSolicitacaoExamePolVOComparator());
		return listaVO;
	}

	/**
	 * @param ise
	 * @param iseVO
	 * @author bruno.mourao
	 *  
	 * @throws ApplicationBusinessException 
	 * @since 02/04/2012
	 */
	private void preencheResultadoExame(AelItemSolicitacaoExames ise,
			ItemSolicitacaoExamePolVO iseVO) throws ApplicationBusinessException {
		
		//Obtem primeiro resultado
		String resultado = buscaResultado(ise);
		 if (StringUtils.isNotBlank(resultado)) {
			 if (resultado.indexOf(".0") == (resultado.length() - 2)) {
				 resultado = resultado.replace(".0", "");
			 }
			 resultado = resultado.replace('.', ',');
		 }		
		iseVO.setResultado(resultado);
		
	}
	
	/**
	 * @ORADB BUSCA_RESULTADO
	 * 
	 * @param ise
	 * @return
	 * @author bruno.mourao
	 *  
	 * @throws ApplicationBusinessException 
	 * @since 02/04/2012
	 */
	public String buscaResultado(AelItemSolicitacaoExames ise) throws ApplicationBusinessException {
		String result = null;
		Object resultadoExame = obtemPrimeiroResultadoExame(ise);
		if (resultadoExame != null && resultadoExame.toString().length() <= 10) {
			result = resultadoExame.toString();
		}

		return result;
	}

	/**
	 * Esta função retorna para uma solicitação o primeiro resultado do exame.
	 * @param ise
	 * @return
	 * @author bruno.mourao
	 * @since 02/04/2012
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private Object obtemPrimeiroResultadoExame(AelItemSolicitacaoExames ise){
		Object result = null;
		
		if( ise.getResultadoExames() != null && !ise.getResultadoExames().isEmpty()){
			//Conta os resultados indicados sem anulação de laudo
			int laudosNaoAnulados = 0;
			AelResultadoExame primeiroResultado = null;
			for(AelResultadoExame res : ise.getResultadoExames()){
				
				//#53750
				// Pega o primeiro exame de laudo não anulado
				if (res.getAnulacaoLaudo()  == false) {
					if(!res.getAnulacaoLaudo()){	
						primeiroResultado = res;
					}
				}
				
				if(!res.getAnulacaoLaudo()){
					laudosNaoAnulados++;
				}

			}
			
			//Se tiver mais do que um ou não estiver nenhum, termina processamento
			if(laudosNaoAnulados > 1 || laudosNaoAnulados == 0){
				return null;
			}
			
			Double valor = null;
			if (primeiroResultado != null && primeiroResultado.getValor() != null && primeiroResultado.getParametroCampoLaudo() != null
					&& primeiroResultado.getParametroCampoLaudo().getQuantidadeCasasDecimais() != null) {
				valor = primeiroResultado.getValor()
						/ Math.pow(10.0, primeiroResultado.getParametroCampoLaudo().getQuantidadeCasasDecimais().doubleValue());
			}
			
			if(valor != null){
				return valor;
			}
			
			//Obtem resultado codificado
			AelResultadoCodificado resultadoCodificado = primeiroResultado.getResultadoCodificado();
			if(resultadoCodificado != null && resultadoCodificado.getDescricao() != null){
				//Se o tamanho do resultado for menor q 20
				if(resultadoCodificado.getDescricao().length() <=20){
					return resultadoCodificado.getDescricao();
				} else {
					//Maior que 20 = nulo
					return null;
				}
			}
			
			//obtem caracteristica 
			AelResultadoCaracteristica resultadoCaracteristica = primeiroResultado.getResultadoCaracteristica();
			if(resultadoCaracteristica != null && resultadoCaracteristica.getDescricao() != null){
				//se o tamanho da caracteristica for menor q 20
				if(resultadoCaracteristica.getDescricao().length() <= 20){
					return resultadoCaracteristica.getDescricao();
				} else {
					// Maior nulo
					return null;
				}
			}
			
			//obtem descricao resultado
			if(primeiroResultado != null && primeiroResultado.getDescricao() != null){
				//se o tamanho da caracteristica for menor q 20
				if(primeiroResultado.getDescricao().length() <= 20){
					return primeiroResultado.getDescricao();
				} else {//Maior = nulo
					return null;
				}
			}
			
		}
		return result;
	}

	/**
	 * Adiciona em uma lista e em um map, á partir de um array de object, em que a primeira posição vem um objeto 
	 * AelItemSolicitacaoExames e na segunda
	 * vem um object do tipo AelDocResultadoExame ou null.
	 * @param listaObj
	 * @param mapISEAnexos
	 * @param lista
	 */
	private void adicionaISEHashLista(List<ExamesPOLVO> listaObj, Map<ExamesPOLVO, AelDocResultadoExame> mapISEAnexos) {
		if (listaObj != null) {
			for (ExamesPOLVO obj : listaObj) {
				AelDocResultadoExame aelDRE = getExamesFacade().obterDocumentoAnexado(obj.getAelItemSolicExame().getId().getSoeSeq(),
						obj.getAelItemSolicExame().getId().getSeqp());
				obj.setAelDocResultadoExames(aelDRE);
				mapISEAnexos.put(obj, aelDRE);
			}
		}
	}

	protected IExamesFacade getExamesFacade() {
		return this.examesFacade;
	}
	
	/**
	 * Retorna documento do exame.
	 * O documento Eh protegido de impressao ou nao de acordo com as permissoes
	 * do usuario fornecido.
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param usuarioLogado
	 * @return
	 * @throws BaseException
	 */
	public ByteArrayOutputStream buscarArquivoResultadoExame(Integer iseSoeSeq, Short iseSeqp) throws BaseException {
		byte[] byteArray = this.downloadArquivoLaudo(iseSoeSeq, iseSeqp);
		ByteArrayOutputStream os = null;
		try {
			os = getPdf(byteArray, !verificarPermissaoImpressao());
			byteArray = os.toByteArray();
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			throw new BaseException(ConsultarExamesPolONExceptionCode.ERRO_GERAR_RELATORIO);
		}
		return os;
	}
	
	/**
	 * Retorna documento do exame.
	 * O documento Eh protegido de impressao ou nao de acordo com as permissoes
	 * do usuario fornecido.
	 * 
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @param usuarioLogado
	 * @return
	 * @throws BaseException
	 */
	public ByteArrayOutputStream buscarArquivoResultadoExameHist(Integer iseSoeSeq, Short iseSeqp) throws BaseException {
		byte[] byteArray = this.downloadArquivoLaudoHist(iseSoeSeq, iseSeqp);
		ByteArrayOutputStream os = null;
		try {
			os = getPdf(byteArray, !verificarPermissaoImpressao());
			byteArray = os.toByteArray();
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			throw new BaseException(ConsultarExamesPolONExceptionCode.ERRO_GERAR_RELATORIO);
		}
		return os;
	}

	private ByteArrayOutputStream getPdf(byte[] byteArray, boolean protegido) throws DocumentException, IOException {
		PdfReader reader = new PdfReader(byteArray);

		int permissions = PdfWriter.ALLOW_PRINTING;

		if (protegido) {
			// retira todas as permissoes
			permissions = 0;
			// Oculta a MenuBar
			reader.addViewerPreference(PdfName.HIDEMENUBAR,PdfBoolean.PDFTRUE);
			// Oculta a ToolBar
			reader.addViewerPreference(PdfName.HIDETOOLBAR,PdfBoolean.PDFTRUE);
		}
		reader.addViewerPreference(PdfName.HIDE, PdfBoolean.PDFFALSE);
		reader.addViewerPreference(PdfName.HIDEWINDOWUI,PdfBoolean.PDFFALSE);
		
		// encrypt e sai para byte array
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		PdfEncryptor.encrypt(reader, os, null, null, permissions, true);
		reader.close();

		return os;
	}

	private boolean verificarPermissaoImpressao() throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		boolean temPermissao = false;
		if(this.getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "imprimirResultadoExame", "imprimir") &&
		   this.getICascaFacade().usuarioTemPermissao(servidorLogado.getUsuario(), "permiteImprimirResultadosExamesPOL","imprimir")	
		){
			temPermissao = true;
		}
			
		return temPermissao;
	}

	/**
	 * Download do arquivo de laudo
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	private byte[] downloadArquivoLaudo(Integer iseSoeSeq, Short iseSeqp)  {
		if (iseSoeSeq == null || iseSeqp == null) {
			return null;
		}

		// Resgata instancia de AelDocResultadoExame
		AelDocResultadoExame doc = getExamesFacade().obterDocumentoAnexado(iseSoeSeq, iseSeqp);
		if (doc != null) {
			return doc.getDocumento();
		} else {
			return null;
		}
	}
	
	/**
	 * Download do arquivo de laudo histórico
	 * @param iseSoeSeq
	 * @param iseSeqp
	 * @return
	 */
	private byte[] downloadArquivoLaudoHist(Integer iseSoeSeq, Short iseSeqp)  {
		if (iseSoeSeq == null || iseSeqp == null) {
			return null;
		}
		
		// Resgata instancia de AelDocResultadoExameHist
		AelDocResultadoExamesHist doc = getExamesFacade().obterDocumentoAnexadoHist(iseSoeSeq, iseSeqp);
		
		if (doc != null) {
			return doc.getDocumento();
		} else {
			return null;
		}
	}

	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	/**
	 * Realiza a pesquisa de exames e retorna em ordem cronológica para o prontuário online.
	 * @param codPaciente
	 * @return
	 * @throws ApplicationBusinessException
	 * @author bruno.mourao
	 * @since 15/02/2012
	 */
	public List<ExameOrdemCronologicaVO> pesquisarExamesOrdemCronologica(Integer codPaciente) throws ApplicationBusinessException{
		
		AghParametros paramSitCodLiberado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros paramSitCodAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		if(paramSitCodLiberado != null && paramSitCodAreaExec != null){
		
			List<ExameOrdemCronologicaVO> result = getExamesFacade().obterDadosOrdemCronologicaArvorePol(codPaciente, paramSitCodLiberado.getVlrTexto(), paramSitCodAreaExec.getVlrTexto());
			
			// Ordena
			Collections.sort(result, new Comparator<ExameOrdemCronologicaVO>() {
				
				@Override
				public int compare(ExameOrdemCronologicaVO arg0, ExameOrdemCronologicaVO arg1) {
					if (arg0.getDtExame().before(arg1.getDtExame())) {
						return 1; // Data de exame em ordem decrescente
					} else {
						if (arg0.getDtExame().after(arg1.getDtExame())) {
							return -1;// Data de exame em ordem decrescente
						} else {// Datas iguais
							if (arg0.getOrdemNivel1() != null && arg1.getOrdemNivel1() != null) {
								if (arg0.getOrdemNivel1() < arg1.getOrdemNivel1()) {
									return -1;
								} else if (arg0.getOrdemNivel1() > arg1.getOrdemNivel1()) {
									return 1;
								}
							}
							if (arg0.getOrdemNivel2() != null && arg1.getOrdemNivel2() != null) {
								if (arg0.getOrdemNivel2() < arg1.getOrdemNivel2()) {
									return -1;
								} else if (arg0.getOrdemNivel2() > arg1.getOrdemNivel2()) {
									return 1;
								}
							}
							// Ordem nivel 2 iguais
							if (arg0.getDescricaoUsual().compareTo(arg1.getDescricaoUsual()) == 0) {
								// Se forem iguais
								return arg0.getMatDescricao().compareTo(arg1.getMatDescricao());
							} else {
								return arg0.getDescricaoUsual().compareTo(arg1.getDescricaoUsual());
							}
						}
					}
				}
			});
			
			return result;
		} else {
			throw new ApplicationBusinessException(ConsultarExamesPolONExceptionCode.PARAMETROS_DATA_EXAMES_CRONOLOGICO_NAO_DEFINIDO);
		}
	}
	
	/**
	 * Realiza a pesquisa de exames e retorna em ordem cronológica para o prontuário online (Hist).
	 * @param codPaciente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<ExameOrdemCronologicaVO> pesquisarExamesOrdemCronologicaHist(Integer codPaciente) throws ApplicationBusinessException{
		
		AghParametros paramSitCodLiberado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros paramSitCodAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		if(paramSitCodLiberado != null && paramSitCodAreaExec != null){
		
			List<ExameOrdemCronologicaVO> result = getExamesFacade().obterDadosOrdemCronologicaArvorePolHist(codPaciente, paramSitCodLiberado.getVlrTexto(), paramSitCodAreaExec.getVlrTexto());
			
			// Ordena
			Collections.sort(result, new Comparator<ExameOrdemCronologicaVO>() {

				@Override
				public int compare(ExameOrdemCronologicaVO arg0, ExameOrdemCronologicaVO arg1) {
					if (arg0.getDtExame().before(arg1.getDtExame())) {
						return 1; // Data de exame em ordem decrescente
					} else {
						if (arg0.getDtExame().after(arg1.getDtExame())) {
							return -1;// Data de exame em ordem decrescente
						} else {// Datas iguais
							if (arg0.getOrdemNivel1() != null && arg1.getOrdemNivel1() != null) {
								if (arg0.getOrdemNivel1() < arg1.getOrdemNivel1()) {
									return -1;
								} else if (arg0.getOrdemNivel1() > arg1.getOrdemNivel1()) {
									return 1;
								}
							}
							if (arg0.getOrdemNivel2() != null && arg1.getOrdemNivel2() != null) {
								if (arg0.getOrdemNivel2() < arg1.getOrdemNivel2()) {
									return -1;
								} else if (arg0.getOrdemNivel2() > arg1.getOrdemNivel2()) {
									return 1;
								}
							}
							// Ordem nivel 2 iguais
							if (arg0.getDescricaoUsual().compareTo(arg1.getDescricaoUsual()) == 0) {
								// Se forem iguais
								return arg0.getMatDescricao().compareTo(arg1.getMatDescricao());
							} else {
								return arg0.getDescricaoUsual().compareTo(arg1.getDescricaoUsual());
							}
						}
					}
				}
			});

			return result;
		} else {
			throw new ApplicationBusinessException(ConsultarExamesPolONExceptionCode.PARAMETROS_DATA_EXAMES_CRONOLOGICO_NAO_DEFINIDO);
		}
	}

	public List<ExameAmostraColetadaVO> pesquisarExamesAmostrasColetadas(Integer codPaciente) throws ApplicationBusinessException {
		
		AghParametros paramSitCodLiberado = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_LIBERADO);
		AghParametros paramSitCodAreaExec = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		
		if (paramSitCodLiberado != null && paramSitCodAreaExec != null) {

			List<ExameAmostraColetadaVO> result = getExamesFacade().obterDadosPorAmostrasColetadas(codPaciente,
					paramSitCodLiberado.getVlrTexto(), paramSitCodAreaExec.getVlrTexto());

			// Ordena
			Collections.sort(result, new Comparator<ExameAmostraColetadaVO>() {

				@Override
				public int compare(ExameAmostraColetadaVO arg0, ExameAmostraColetadaVO arg1) {

					int resultadoAtributoComparado;

					resultadoAtributoComparado = arg0.getOrdProntOnline().compareTo(arg1.getOrdProntOnline());
					if (resultadoAtributoComparado != 0) {
						return resultadoAtributoComparado;
					}

					resultadoAtributoComparado = arg0.getDescGrpMatAnalise().compareTo(arg1.getDescGrpMatAnalise());
					if (resultadoAtributoComparado != 0) {
						return resultadoAtributoComparado;
					}

					resultadoAtributoComparado = arg1.getDtExame().compareTo(arg0.getDtExame());
					if (resultadoAtributoComparado != 0) {
						return resultadoAtributoComparado;
					}

					if(arg0.getOrdemNivel1() != null && arg1.getOrdemNivel1() != null) {
						resultadoAtributoComparado = arg0.getOrdemNivel1().compareTo(arg1.getOrdemNivel1());
						if (resultadoAtributoComparado != 0) {
							return resultadoAtributoComparado;
						}
					}

					if(arg0.getOrdemNivel2() != null && arg1.getOrdemNivel2() != null) {
						resultadoAtributoComparado = arg0.getOrdemNivel2().compareTo(arg1.getOrdemNivel2());
						if (resultadoAtributoComparado != 0) {
							return resultadoAtributoComparado;
						}
					}

					resultadoAtributoComparado = arg0.getDescricaoUsual().compareTo(arg1.getDescricaoUsual());
					if (resultadoAtributoComparado != 0) {
						return resultadoAtributoComparado;
					}

					resultadoAtributoComparado = arg0.getMatDescricao().compareTo(arg1.getMatDescricao());
					return resultadoAtributoComparado;
				}
			});

			return result;
		} else {
			throw new ApplicationBusinessException(ConsultarExamesPolONExceptionCode.PARAMETROS_DATA_EXAMES_CRONOLOGICO_NAO_DEFINIDO);
		}
	}

	/**
	 * Verifica se a unidade executora do item de solicitacao de exame permite a visualizacao de resultados
	 * 
	 * @HIST 
	 *       ConsultarExamesPolON.verificarPermissaoVisualizarResultadoExamesHist
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public boolean verificarPermissaoVisualizarResultadoExames(Integer soeSeq, Short seqp) {
	
		AelItemSolicitacaoExames itemSolicitacaoExames = this.getExamesFacade().buscaItemSolicitacaoExamePorId(soeSeq, seqp);
	
		String emaExaSigla = itemSolicitacaoExames.getAelUnfExecutaExames().getId().getEmaExaSigla();
		Integer emaManSeq = itemSolicitacaoExames.getAelUnfExecutaExames().getId().getEmaManSeq();
		Short unfSeq = itemSolicitacaoExames.getAelUnfExecutaExames().getId().getUnfSeq().getSeq();
	
		AelUnfExecutaExames unfExecutaExames = this.getExamesFacade().obterAelUnfExecutaExames(emaExaSigla, emaManSeq, unfSeq);
	
		if (unfExecutaExames != null && unfExecutaExames.getIndPermVerLaudoExecutando()) {
			return true;
		}
	
		return false;
	}

	/**
	 * Verifica se a unidade executora do item de solicitacao de exame permite a visualizacao de resultados
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public boolean verificarPermissaoVisualizarResultadoExamesHist(Integer soeSeq, Short seqp) {

		AelItemSolicExameHist itemSolicitacaoExames = this.getExamesFacade().buscaItemSolicitacaoExamePorIdHist(soeSeq, seqp);

		String emaExaSigla = itemSolicitacaoExames.getAelUnfExecutaExames().getId().getEmaExaSigla();
		Integer emaManSeq = itemSolicitacaoExames.getAelUnfExecutaExames().getId().getEmaManSeq();
		Short unfSeq = itemSolicitacaoExames.getAelUnfExecutaExames().getId().getUnfSeq().getSeq();

		AelUnfExecutaExames unfExecutaExames = this.getExamesFacade().obterAelUnfExecutaExames(emaExaSigla, emaManSeq, unfSeq);

		if (unfExecutaExames != null && unfExecutaExames.getIndPermVerLaudoExecutando()) {
			return true;
		}

		return false;
	}
	
	/**
	 * É necessário que o item contenha notas adicionais cadastradas para visualização das mesmas.
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public boolean verificarPermissaoVisualizarNotasAdicionais(Integer soeSeq, Short seqp){
		
		List<AelNotaAdicional> listaNotasAdicionais = this.getExamesFacade().obterListaNotasAdicionaisPeloItemSolicitacaoExame(soeSeq, seqp);
	
		if(!listaNotasAdicionais.isEmpty()){
			return true;
		}
		
		return false;
	}
	
	/**
	 * Verifica se os itens de uma lista de solicitação de exame possuem imagens
	 * @param itensSelecionados
	 * @throws BaseException
	 */
	public void verificarListaExamesPossuemImagem(Map<Integer, Vector<Short>> itensSelecionados) throws BaseException{
		
		Set<Integer> solicitacoes = itensSelecionados.keySet();
		
		int itensValidosComImagens = 0;
		
		for (Integer soeSeq : solicitacoes) {

			Vector<Short> seqps = itensSelecionados.get(soeSeq);

			for (Short seqp : seqps) {
				AelItemSolicitacaoExames itemSolicitacaoExame = getExamesFacade().obteritemSolicitacaoExamesPorChavePrimaria(new AelItemSolicitacaoExamesId(soeSeq, seqp));
				
				if(itemSolicitacaoExame != null && StringUtils.isNotEmpty(itemSolicitacaoExame.getPacOruAccNumber())){
					itensValidosComImagens++;
				}
			}
		}
		
		if(itensValidosComImagens == 0){
			throw new BaseException(ConsultarExamesPolONExceptionCode.AEL_03163);
		}

	}

	public void verificarListaExamesPossuemImagemHist(Map<Integer, Vector<Short>> itensSelecionados) throws BaseException{
		verificarListaExamesPossuemImagemHist(itensSelecionados, false);
	}
	/**
	 * Verifica se os itens de uma lista de solicitação de exame possuem imagens
	 * @param itensSelecionados
	 * @throws BaseException
	 */
	public void verificarListaExamesPossuemImagemHist(Map<Integer, Vector<Short>> itensSelecionados, Boolean origemPol) throws BaseException{
		
		Set<Integer> solicitacoes = itensSelecionados.keySet();
		
		int itensValidosComImagens = 0;
		
		for (Integer soeSeq : solicitacoes) {

			Vector<Short> seqps = itensSelecionados.get(soeSeq);

			for (Short seqp : seqps) {
				AelItemSolicExameHist itemSolicitacaoExame = null;
				if(origemPol){
					itemSolicitacaoExame = getExamesFacade().buscaItemSolicitacaoExamePorIdHistOrigemPol(soeSeq, seqp);
				} else {
					itemSolicitacaoExame = getExamesFacade().buscaItemSolicitacaoExamePorIdHist(soeSeq, seqp);
				}
				
				if(itemSolicitacaoExame != null && StringUtils.isNotEmpty(itemSolicitacaoExame.getPacOruAccNumber())){
					itensValidosComImagens++;
				}
			}
		}
		
		if(itensValidosComImagens == 0){
			throw new BaseException(ConsultarExamesPolONExceptionCode.AEL_03163);
		}

	}


	/**
	 * Verifica se o item de solicitacao de exame possui imagem
	 * 
	 * @param soeSeq
	 * @param seqp
	 * @return
	 */
	public void verificarExamePossuiImagem(Integer soeSeq, Short seqp) throws BaseException {
		AelItemSolicitacaoExames itemSolicitacaoExame = getExamesFacade().obteritemSolicitacaoExamesPorChavePrimaria(
				new AelItemSolicitacaoExamesId(soeSeq, seqp));

		if (itemSolicitacaoExame != null && StringUtils.isEmpty(itemSolicitacaoExame.getPacOruAccNumber())) {
			throw new BaseException(ConsultarExamesPolONExceptionCode.AEL_03163);
		}
	}

	
	
	protected VAelPesquisaPolExameUnidadeDAO getVAelPesquisaPolExameUnidadeDAO() {
		return vAelPesquisaPolExameUnidadeDAO;
	}
	
	protected VAelPesquisaPolExameUnidadeHistDAO getVAelPesquisaPolExameUnidadeHistDAO() {
		return vAelPesquisaPolExameUnidadeHistDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}