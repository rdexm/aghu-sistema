package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumStatusItem;
import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.vo.DispensacaoFarmaciaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class RelatorioDispensacaoFarmaciaController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(RelatorioDispensacaoFarmaciaController.class);

	private static final long serialVersionUID = 8715126248062313120L;

	private Boolean imprimirRelatorioDispensacao = true;

	private List<DispensacaoFarmaciaVO> listaDispensacaoFarmaciaVO = new ArrayList<DispensacaoFarmaciaVO>();

	private List<DispensacaoFarmaciaVO> listaMovimentosDispensacaoFarmaciaVO = new ArrayList<DispensacaoFarmaciaVO>();

	private List<DispensacaoFarmaciaVO> listaIncluidoDispensacaoFarmaciaVO = new ArrayList<DispensacaoFarmaciaVO>();

	private List<DispensacaoFarmaciaVO> listaAlteradoDispensacaoFarmaciaVO = new ArrayList<DispensacaoFarmaciaVO>();

	private List<DispensacaoFarmaciaVO> listaExcluidoDispensacaoFarmaciaVO = new ArrayList<DispensacaoFarmaciaVO>();

	private List<MpmPrescricaoMdto> listaPrescricaoMdto = new ArrayList<MpmPrescricaoMdto>();

	private List<MpmPrescricaoMdto> listaMovimentosPrescricaoMdto = new ArrayList<MpmPrescricaoMdto>();

	private List<MpmPrescricaoMdto> listaPrescricaoMdtoIncluidos = new ArrayList<MpmPrescricaoMdto>();

	private List<MpmPrescricaoMdto> listaPrescricaoMdtoAlterados = new ArrayList<MpmPrescricaoMdto>();

	private List<MpmPrescricaoMdto> listaPrescricaoMdtoExcluidos = new ArrayList<MpmPrescricaoMdto>();

	private List<DispensacaoFarmaciaVO> listaMedicamentosMovimentados = new ArrayList<DispensacaoFarmaciaVO>();

	/** Dados que serão impressos em PDF. */
	private List<DispensacaoFarmaciaVO> colecao = new ArrayList<DispensacaoFarmaciaVO>(0);
	
	
	private MpmPrescricaoMedicaId prescricaoMedicaId = new MpmPrescricaoMedicaId();
	
	private String voltarPara;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
    private SistemaImpressao sistemaImpressao;

	
	private Integer seqAtendimento;

	private Integer seqPrescricao;

	private Byte nroViasPme;

	private EnumTipoImpressao tipoImpressao;
	
	private static final String RELATORIO_DISPENSACAO_FARMACIA_PDF = "relatorioDispensacaoFarmaciaPdf";

	private enum DispensacaoFarmaciaExceptionCode implements BusinessExceptionCode {
		ERRO_CLONE_DISPENSACAO_FARMACIA;
	}

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	private RapServidores servidorValida;

	private Date dataMovimento;
	
	public void init() {
		limparColecoes();
		try {
			Short farmPadrao = verificaParametroFarmaciaPadrao();
			
			List<List<DispensacaoFarmaciaVO>> listaImpressaoRemota = new ArrayList<List<DispensacaoFarmaciaVO>>();
			List<DispensacaoFarmaciaVO> listaMedicamentos = new ArrayList<DispensacaoFarmaciaVO>();

			MpmPrescricaoMedica prescricaoMedica = prescricaoMedicaFacade.obterPrescricaoPorId(seqAtendimento, seqPrescricao);
			this.prescricaoMedicaId = prescricaoMedica.getId();
			
			validaTipoImpressao(listaMedicamentos, prescricaoMedica);
			
			List<AghUnidadesFuncionais> listaFarmacias = this.farmaciaDispensacaoFacade.listarFarmacias();
			listaMedicamentos = (List<DispensacaoFarmaciaVO>) this.farmaciaDispensacaoFacade.ordenarDispensacaoFarmaciaVO(listaMedicamentos);

			preFormataListaFarmacias(listaImpressaoRemota, listaMedicamentos, listaFarmacias);

			if (this.getNroViasPme() <= 1 && listaMedicamentos.size() > 0) {
				this.ordenarRelatorioPorFarmacia(listaMedicamentos);
			}

			populaColecaoComListaDeMedicamentos(farmPadrao, listaMedicamentos);
			
			listaMedicamentos.clear();

			listaMedicamentosMovimentados = (List<DispensacaoFarmaciaVO>) farmaciaDispensacaoFacade.ordenarDispensacaoFarmaciaVO(listaMedicamentosMovimentados);

			processaListaFarmacias(listaImpressaoRemota, listaFarmacias);

			if (this.getNroViasPme() <= 1 && listaMedicamentosMovimentados.size() > 0) {
				this.ordenarRelatorioPorFarmacia(listaMedicamentosMovimentados);
			}
			
			populaColecaoComListaDeMedicamentosMovimentados(farmPadrao);
			
			listaMedicamentosMovimentados.clear();

			if (this.colecao.isEmpty()) {
				this.imprimirRelatorioDispensacao = false;
			}

		} catch (ApplicationBusinessException e) {
			this.imprimirRelatorioDispensacao = false;
			LOG.error("Exceção capturada: ", e);
			apresentarExcecaoNegocio(e);
			
		} catch (BaseException e) {
			this.imprimirRelatorioDispensacao = false;
			apresentarExcecaoNegocio(e);
		}
	}


	/**
	 * @param farmPadrao
	 */
	private void populaColecaoComListaDeMedicamentosMovimentados(
			Short farmPadrao) {
		for (DispensacaoFarmaciaVO dispensacaoFarmaciaVO : listaMedicamentosMovimentados) {
			if (farmPadrao.equals(Short.valueOf(dispensacaoFarmaciaVO.getFarmacia()))) {
				this.colecao.add(dispensacaoFarmaciaVO);
			}
		}
	}


	/**
	 * @param farmPadrao
	 * @param listaMedicamentos
	 */
	private void populaColecaoComListaDeMedicamentos(Short farmPadrao,
			List<DispensacaoFarmaciaVO> listaMedicamentos) {
		for (DispensacaoFarmaciaVO dispensacaoFarmaciaVO : listaMedicamentos) {
			if (farmPadrao.equals(Short.valueOf(dispensacaoFarmaciaVO.getFarmacia()))) {
				this.colecao.add(dispensacaoFarmaciaVO);
			}
		}
	}


	/**
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private Short verificaParametroFarmaciaPadrao()
			throws ApplicationBusinessException {
		AghParametros param = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_UNF_FARM_DISP);
		Short farmPadrao = param.getVlrNumerico().shortValue();
		return farmPadrao;
	}


	private void limparColecoes() {
		 listaDispensacaoFarmaciaVO.clear();
		 listaMovimentosDispensacaoFarmaciaVO.clear();
		 listaIncluidoDispensacaoFarmaciaVO.clear();  
		 listaAlteradoDispensacaoFarmaciaVO.clear();  
		 listaExcluidoDispensacaoFarmaciaVO.clear();  
		 listaPrescricaoMdto.clear();
		 listaMovimentosPrescricaoMdto.clear();       
		 listaPrescricaoMdtoIncluidos.clear();        
		 listaPrescricaoMdtoAlterados.clear();        
		 listaPrescricaoMdtoExcluidos.clear();        
		 listaMedicamentosMovimentados.clear();       
		 colecao.clear();
	}


	private void validaTipoImpressao(
			List<DispensacaoFarmaciaVO> listaMedicamentos,
			MpmPrescricaoMedica prescricaoMedica)
			throws ApplicationBusinessException, BaseException {
		Boolean primeiraImpressao = //(servidorValida != null && servidorValida.getId().getMatricula() != null); 
				this.prescricaoMedicaFacade.verificarPrimeiraImpressao(servidorValida);
		
		if(tipoImpressao != null){
			if ((tipoImpressao.equals(EnumTipoImpressao.IMPRESSAO) && primeiraImpressao) || 
					tipoImpressao.equals(EnumTipoImpressao.REIMPRESSAO) || 
					(tipoImpressao.equals(EnumTipoImpressao.SEM_IMPRESSAO) && 
							primeiraImpressao)) {
				
				primeiraImpressaoOuReimpressao(listaMedicamentos, prescricaoMedica);
	
			} else if ((tipoImpressao.equals(EnumTipoImpressao.IMPRESSAO) && !primeiraImpressao) || 
					(tipoImpressao.equals(EnumTipoImpressao.SEM_IMPRESSAO) && !primeiraImpressao)) {
				
				reimpressao(listaMedicamentos, prescricaoMedica);
			}
		}
	}


	private void processaListaFarmacias(
			List<List<DispensacaoFarmaciaVO>> listaImpressaoRemota,
			List<AghUnidadesFuncionais> listaFarmacias) {
		for (AghUnidadesFuncionais farmacia : listaFarmacias) {
			List<DispensacaoFarmaciaVO> listaDispensacaoFarmaciaVO = new ArrayList<DispensacaoFarmaciaVO>();
			for (DispensacaoFarmaciaVO dispensacaoFarmaciaVO : listaMedicamentosMovimentados) {
				if (farmacia.getSeq().toString().equals(dispensacaoFarmaciaVO.getFarmacia())) {
					listaDispensacaoFarmaciaVO.add(dispensacaoFarmaciaVO);
				}
			}
			listaImpressaoRemota.add(listaDispensacaoFarmaciaVO);
		}
	}


	private void primeiraImpressaoOuReimpressao(List<DispensacaoFarmaciaVO> listaMedicamentos, MpmPrescricaoMedica prescricaoMedica) 
				throws ApplicationBusinessException, BaseException {
		try {
			
			this.listaPrescricaoMdto = farmaciaDispensacaoFacade.buscarPrescricaoMedicamentosConfirmados(this.prescricaoMedicaId);
			this.listaDispensacaoFarmaciaVO = farmaciaDispensacaoFacade.popularDispensacaoFarmaciaVO(prescricaoMedicaId, listaPrescricaoMdto, false);
	
			// -- VERIFICA SE DEVE IMPRIMIR NOVAS VIAS --
			AghParametros paramNroCopiasFarm = parametroFacade.buscarAghParametro(
					AghuParametrosEnum.P_NRO_COPIAS_FARM);
			this.setNroViasPme(paramNroCopiasFarm.getVlrNumerico().byteValue());
			String paramValorCopias = paramNroCopiasFarm.getVlrTexto();
			if (this.getNroViasPme() != 0 && "S".equalsIgnoreCase(paramValorCopias)) {
				this.prepararImprimirNovasVias(this.getNroViasPme());
			}
	
			// ----------------------------------------------------------------
			for (DispensacaoFarmaciaVO aux : listaDispensacaoFarmaciaVO) {
				if (aux.getFarmacias() != null && aux.getFarmacias().size() > 0) {
					String farmacia = aux.getFarmacias().get(0);
					DispensacaoFarmaciaVO dispensacaoFarmaciaVO = (DispensacaoFarmaciaVO) BeanUtils.cloneBean(aux);
					dispensacaoFarmaciaVO.setFarmacia(farmacia);
					dispensacaoFarmaciaVO.setImpressora(aux.getImpressoras().isEmpty() ? null : aux.getImpressoras().get(0));
					dispensacaoFarmaciaVO.setClinica(prescricaoMedica.getAtendimento().getEspecialidade() != null ? prescricaoMedica.getAtendimento().getEspecialidade().getClinica().getDescricao() : null);
					listaMedicamentos.add(dispensacaoFarmaciaVO);
				}
			}
			
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			this.imprimirRelatorioDispensacao = false;
			LOG.error("Exceção capturada: ", e);
			throw new ApplicationBusinessException(DispensacaoFarmaciaExceptionCode.ERRO_CLONE_DISPENSACAO_FARMACIA);
		}
	}

	
	
	private void reimpressao(List<DispensacaoFarmaciaVO> listaMedicamentos, MpmPrescricaoMedica prescricaoMedica) 
										throws ApplicationBusinessException, BaseException {
		
		this.listaMovimentosPrescricaoMdto = farmaciaDispensacaoFacade.buscarPrescricaoMedicamentos(this.prescricaoMedicaId);
		
		
		ajustaListaMovimentosPrescricaoMdto();

		if (listaPrescricaoMdto.size() != 0) {
			this.listaDispensacaoFarmaciaVO = farmaciaDispensacaoFacade.popularDispensacaoFarmaciaVO(prescricaoMedicaId, listaPrescricaoMdto, false);
		}

		ajustaListaDispensacaoFarmaciaVO(listaMedicamentos, prescricaoMedica);

		ajustaListaPrescricaoMdtoIncluidos();
		
		ajustaListaPrescricaoMdtoAlterados();
		
		ajustaListaPrescricaoMdtoExcluidos();

		ajustaListaMovimentosDispensacaoFarmaciaVO(prescricaoMedica);

		// -- VERIFICA SE DEVE IMPRIMIR NOVAS --
		AghParametros paramNroCopiasFarm = parametroFacade.buscarAghParametro(
				AghuParametrosEnum.P_NRO_COPIAS_FARM);
		this.setNroViasPme(paramNroCopiasFarm.getVlrNumerico().byteValue());
		String paramValorCopias = paramNroCopiasFarm.getVlrTexto();
		if (this.getNroViasPme() != 0 && "S".equalsIgnoreCase(paramValorCopias)) {
			this.prepararImprimirNovasVias(nroViasPme);
		}
	}

	private void ajustaListaMovimentosDispensacaoFarmaciaVO(MpmPrescricaoMedica prescricaoMedica) throws ApplicationBusinessException  {
		try {
			for (DispensacaoFarmaciaVO aux : listaMovimentosDispensacaoFarmaciaVO) {
				if (aux.getFarmacias() != null && aux.getFarmacias().size() > 0) {
//					String farmacia = aux.getFarmacias().get(0);
					DispensacaoFarmaciaVO dispensacaoFarmaciaVO = (DispensacaoFarmaciaVO) BeanUtils.cloneBean(aux);
//					dispensacaoFarmaciaVO.setFarmacia(farmacia);
//					dispensacaoFarmaciaVO.setImpressora(aux.getImpressoras().isEmpty() ? null : aux.getImpressoras().get(0));
					dispensacaoFarmaciaVO.setClinica(prescricaoMedica.getAtendimento().getEspecialidade() != null ? prescricaoMedica.getAtendimento().getEspecialidade().getClinica().getDescricao() : null);
					listaMedicamentosMovimentados.add(dispensacaoFarmaciaVO);
				}
			}
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			throw new ApplicationBusinessException(DispensacaoFarmaciaExceptionCode.ERRO_CLONE_DISPENSACAO_FARMACIA);
		}
	}

	private void ajustaListaPrescricaoMdtoExcluidos() throws BaseException {
		if (listaPrescricaoMdtoExcluidos.size() != 0) {
			this.listaExcluidoDispensacaoFarmaciaVO = farmaciaDispensacaoFacade.popularDispensacaoFarmaciaVO( prescricaoMedicaId, listaPrescricaoMdtoExcluidos, false);
			for (DispensacaoFarmaciaVO dispensacaoFarmaciaVO : listaExcluidoDispensacaoFarmaciaVO) {
				dispensacaoFarmaciaVO.setStatusItem("Excluir");
			}
			listaMovimentosDispensacaoFarmaciaVO.addAll(getListaExcluidoDispensacaoFarmaciaVO());
		}
	}

	private void ajustaListaPrescricaoMdtoAlterados() throws BaseException {
		if (listaPrescricaoMdtoAlterados.size() != 0) {
			listaAlteradoDispensacaoFarmaciaVO = farmaciaDispensacaoFacade.popularDispensacaoFarmaciaVO( prescricaoMedicaId, 
																										 listaPrescricaoMdtoAlterados, true);
			for (DispensacaoFarmaciaVO dispensacaoFarmaciaVO : listaAlteradoDispensacaoFarmaciaVO) {
				dispensacaoFarmaciaVO.setStatusItem("Alterar");
			}
			listaMovimentosDispensacaoFarmaciaVO.addAll(getListaAlteradoDispensacaoFarmaciaVO());
		}
	}

	private void ajustaListaPrescricaoMdtoIncluidos() throws BaseException {
		if (listaPrescricaoMdtoIncluidos.size() != 0) {
			listaIncluidoDispensacaoFarmaciaVO = farmaciaDispensacaoFacade.popularDispensacaoFarmaciaVO( prescricaoMedicaId, 
																										 listaPrescricaoMdtoIncluidos, false);
			for (DispensacaoFarmaciaVO dispensacaoFarmaciaVO : listaIncluidoDispensacaoFarmaciaVO) {
				dispensacaoFarmaciaVO.setStatusItem("Incluir");
			}
			listaMovimentosDispensacaoFarmaciaVO.addAll(getListaIncluidoDispensacaoFarmaciaVO());
		}
	}

	private void ajustaListaDispensacaoFarmaciaVO(List<DispensacaoFarmaciaVO> listaMedicamentos, MpmPrescricaoMedica prescricaoMedica) throws ApplicationBusinessException {
		try {
			for (DispensacaoFarmaciaVO aux : listaDispensacaoFarmaciaVO) {
				if (aux.getFarmacias() != null && aux.getFarmacias().size() > 0) {
					DispensacaoFarmaciaVO dispensacaoFarmaciaVO = (DispensacaoFarmaciaVO) BeanUtils.cloneBean(aux);
//					dispensacaoFarmaciaVO.setFarmacia(farmacia);
//					dispensacaoFarmaciaVO.setImpressora(aux.getImpressoras().isEmpty() ? null : aux.getImpressoras().get(i));
//					dispensacaoFarmaciaVO.setClinica(prescricaoMedica.getAtendimento().getEspecialidade() != null ? prescricaoMedica.getAtendimento().getEspecialidade().getClinica().getDescricao() : null);
					listaMedicamentos.add(dispensacaoFarmaciaVO);
				}
			}
		} catch (IllegalAccessException | InstantiationException | InvocationTargetException | NoSuchMethodException e) {
			throw new ApplicationBusinessException(DispensacaoFarmaciaExceptionCode.ERRO_CLONE_DISPENSACAO_FARMACIA);
		}
	}

	private void ajustaListaMovimentosPrescricaoMdto() throws ApplicationBusinessException {
		for (MpmPrescricaoMdto prescricaoMdto : listaMovimentosPrescricaoMdto) {
			
			EnumStatusItem statusItem = this.prescricaoMedicaFacade.buscarStatusItem(prescricaoMdto, dataMovimento);
			
			if (statusItem.equals(EnumStatusItem.INCLUIDO)) {
				listaPrescricaoMdtoIncluidos.add(prescricaoMdto);
				
			} else if (statusItem.equals(EnumStatusItem.ALTERADO)) {
				listaPrescricaoMdtoAlterados.add(prescricaoMdto);
				
			} else if (statusItem.equals(EnumStatusItem.EXCLUIDO)) {
				listaPrescricaoMdtoExcluidos.add(prescricaoMdto);
			}
		}
	}



	private void preFormataListaFarmacias( List<List<DispensacaoFarmaciaVO>> listaImpressaoRemota, List<DispensacaoFarmaciaVO> listaMedicamentos, 
										   List<AghUnidadesFuncionais> listaFarmacias) {
		
		for (AghUnidadesFuncionais farmacia : listaFarmacias) {
			List<DispensacaoFarmaciaVO> listaDispensacaoFarmaciaVO = new ArrayList<DispensacaoFarmaciaVO>();
			for (DispensacaoFarmaciaVO dispensacaoFarmaciaVO : listaMedicamentos) {
				if (farmacia.getSeq().toString().equals(dispensacaoFarmaciaVO.getFarmacia())) {
					listaDispensacaoFarmaciaVO.add(dispensacaoFarmaciaVO);
				}
			}
			listaImpressaoRemota.add(listaDispensacaoFarmaciaVO);
		}
	}
	
	
	
	private void ordenarRelatorioPorFarmacia(List<DispensacaoFarmaciaVO> listaMedicamentos) {
		for (DispensacaoFarmaciaVO dispensacao : listaMedicamentos) {
			dispensacao.setOrdemTela(Integer.valueOf(dispensacao.getFarmacia()));
		}
	}

	/**
	 * Prepara novas vias para serem impressas
	 */
	protected void prepararImprimirNovasVias(Byte nroViasPme) {
		List<DispensacaoFarmaciaVO> listaNovasVias = new ArrayList<DispensacaoFarmaciaVO>();
		Integer ordemTela = 2;
		for (int i = 0; i < nroViasPme - 1; i++) {
			for (DispensacaoFarmaciaVO dispensacao : listaDispensacaoFarmaciaVO) {
				dispensacao.setOrdemTela(1);
				DispensacaoFarmaciaVO dispNovaVia = dispensacao.copiar();
				dispNovaVia.setOrdemTela(ordemTela);
				listaNovasVias.add(dispNovaVia);
			}
			for (DispensacaoFarmaciaVO dispensacao : listaMedicamentosMovimentados) {
				dispensacao.setOrdemTela(1);
				DispensacaoFarmaciaVO dispNovaVia = dispensacao.copiar();
				dispNovaVia.setOrdemTela(ordemTela);
				listaNovasVias.add(dispNovaVia);
			}
			ordemTela++;
		}
		if (listaDispensacaoFarmaciaVO.size() > 0) {
			listaDispensacaoFarmaciaVO.addAll(listaNovasVias);

		} else {
			listaMedicamentosMovimentados.addAll(listaNovasVias);
		}
	}

	@Override
	public String recuperarArquivoRelatorio() {
		if (listaIncluidoDispensacaoFarmaciaVO.size() != 0 || 
				listaAlteradoDispensacaoFarmaciaVO.size() != 0 || 
					listaExcluidoDispensacaoFarmaciaVO.size() != 0) {
			
			return "br/gov/mec/aghu/prescricaomedica/report/relatorioMovimentoDispensacaoFarmacia.jasper";
			
		} else {
			return "br/gov/mec/aghu/prescricaomedica/report/relatorioDispensacaoFarmacia.jasper";
		}
	}

	@Override
	public Collection<DispensacaoFarmaciaVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws ApplicationBusinessException, IOException, JRException, DocumentException {
		DocumentoJasper documento = gerarDocumento(Boolean.TRUE);
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		final Map<String, Object> params = new HashMap<String, Object>();
		
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		String caminhoSubRelatorio = "br/gov/mec/aghu/prescricaomedica/report/ItensDispensacaoFarmacia.jasper";
		
		params.put("hospitalLocal", hospital);
		params.put("data", DateUtil.obterDataFormatada(new Date(), "dd/MM/yy HH:mm"));
		params.put("subRelatorioItensDispensacaoFarmacia",Thread.currentThread().getContextClassLoader().getResourceAsStream(caminhoSubRelatorio));

		if (listaIncluidoDispensacaoFarmaciaVO.size() != 0 || 
				listaAlteradoDispensacaoFarmaciaVO.size() != 0 || 
					listaExcluidoDispensacaoFarmaciaVO.size() != 0) { 
			
			params.put("funcionalidade", "Movimentações da Prescrição");
			params.put("nomeRelatorio", "AFAR_IMP_MVTO_PRMD");
			
		} else {
			params.put("funcionalidade", "Prescrição de Medicamentos");
			params.put("nomeRelatorio", "AFAR_IMP_PRCR_MDTO");
		}
		
		return params;
	}
	
	//@Observer("prescricaoConfirmada")
	public void observarEventoImpressaoPrescricaoConfirmada() throws BaseException, JRException, SystemException, IOException, ApplicationBusinessException {
		if(seqAtendimento == null || seqPrescricao == null){
			apresentarMsgNegocio("ERRO_PARAMETROS_INVALIDOS");
			
		} else {
			init();
			if (colecao != null && !colecao.isEmpty() && !EnumTipoImpressao.SEM_IMPRESSAO.equals(tipoImpressao)) {
				Set<ImpImpressora> impressorasCups = colecao.get(0).getImpressorasCups();
				List<DispensacaoFarmaciaVO> colecaoAux = new ArrayList<DispensacaoFarmaciaVO>(this.colecao);
				// Cada item da coleção possui uma impressora definida. Por isso será chamada a impressão individualmente.
				for (ImpImpressora impImpressora : impressorasCups) {
					this.separarDadosPorImpressora(impImpressora);
					DocumentoJasper documento = gerarDocumento();
					sistemaImpressao.imprimir(impImpressora, documento.getJasperPrint());
					//sistemaImpressao.imprimir(documento.getJasperPrint(), getEnderecoIPv4HostRemoto());
					this.colecao = colecaoAux;
				}
			}
		}
	}
	
	private void separarDadosPorImpressora(ImpImpressora impressora) {
		List<DispensacaoFarmaciaVO> retorno = new ArrayList<DispensacaoFarmaciaVO>(0);
		for (DispensacaoFarmaciaVO dispensacaoFarmaciaVO : this.colecao) {
			if (impressora.equals(dispensacaoFarmaciaVO.getImpressoraCups())) {
				retorno.add(dispensacaoFarmaciaVO);
			}
		}
		this.colecao = retorno;		
	}
	
	public String visualizarRelatorio(){
		
		if(seqAtendimento == null || seqPrescricao == null){
			apresentarMsgNegocio("ERRO_PARAMETROS_INVALIDOS");
			return null;
			
		} else {
			init();
			return RELATORIO_DISPENSACAO_FARMACIA_PDF;
		}
	}
	
	public String voltar(){
		return voltarPara;
	}
	
	public List<DispensacaoFarmaciaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<DispensacaoFarmaciaVO> colecao) {
		this.colecao = colecao;
	}

	public Boolean getImprimir() {
		return imprimirRelatorioDispensacao;
	}

	public void setImprimir(Boolean imprimir) {
		this.imprimirRelatorioDispensacao = imprimir;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public List<DispensacaoFarmaciaVO> getListaDispensacaoFarmaciaVO() {
		return listaDispensacaoFarmaciaVO;
	}

	public void setListaDispensacaoFarmaciaVO(List<DispensacaoFarmaciaVO> listaDispensacaoFarmaciaVO) {
		this.listaDispensacaoFarmaciaVO = listaDispensacaoFarmaciaVO;
	}

	public MpmPrescricaoMedicaId getPrescricaoMedicaId() {
		return prescricaoMedicaId;
	}

	public void setPrescricaoMedicaId(MpmPrescricaoMedicaId prescricaoMedicaId) {
		this.prescricaoMedicaId = prescricaoMedicaId;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public Integer getSeqPrescricao() {
		return seqPrescricao;
	}

	public void setSeqPrescricao(Integer seqPrescricao) {
		this.seqPrescricao = seqPrescricao;
	}

	public List<MpmPrescricaoMdto> getListaMovimentosPrescricaoMdto() {
		return listaMovimentosPrescricaoMdto;
	}

	public void setListaMovimentosPrescricaoMdto(
			List<MpmPrescricaoMdto> listaMovimentosPrescricaoMdto) {
		this.listaMovimentosPrescricaoMdto = listaMovimentosPrescricaoMdto;
	}

	public List<DispensacaoFarmaciaVO> getListaMovimentosDispensacaoFarmaciaVO() {
		return listaMovimentosDispensacaoFarmaciaVO;
	}

	public void setListaMovimentosDispensacaoFarmaciaVO(
			List<DispensacaoFarmaciaVO> listaMovimentosDispensacaoFarmaciaVO) {
		this.listaMovimentosDispensacaoFarmaciaVO = listaMovimentosDispensacaoFarmaciaVO;
	}

	public List<DispensacaoFarmaciaVO> getListaIncluidoDispensacaoFarmaciaVO() {
		return listaIncluidoDispensacaoFarmaciaVO;
	}

	public void setListaIncluidoDispensacaoFarmaciaVO(
			List<DispensacaoFarmaciaVO> listaIncluidoDispensacaoFarmaciaVO) {
		this.listaIncluidoDispensacaoFarmaciaVO = listaIncluidoDispensacaoFarmaciaVO;
	}

	public List<DispensacaoFarmaciaVO> getListaAlteradoDispensacaoFarmaciaVO() {
		return listaAlteradoDispensacaoFarmaciaVO;
	}

	public void setListaAlteradoDispensacaoFarmaciaVO(
			List<DispensacaoFarmaciaVO> listaAlteradoDispensacaoFarmaciaVO) {
		this.listaAlteradoDispensacaoFarmaciaVO = listaAlteradoDispensacaoFarmaciaVO;
	}

	public List<DispensacaoFarmaciaVO> getListaExcluidoDispensacaoFarmaciaVO() {
		return listaExcluidoDispensacaoFarmaciaVO;
	}

	public void setListaExcluidoDispensacaoFarmaciaVO(
			List<DispensacaoFarmaciaVO> listaExcluidoDispensacaoFarmaciaVO) {
		this.listaExcluidoDispensacaoFarmaciaVO = listaExcluidoDispensacaoFarmaciaVO;
	}

	public List<MpmPrescricaoMdto> getListaPrescricaoMdto() {
		return listaPrescricaoMdto;
	}

	public void setListaPrescricaoMdto(List<MpmPrescricaoMdto> listaPrescricaoMdto) {
		this.listaPrescricaoMdto = listaPrescricaoMdto;
	}

	public List<MpmPrescricaoMdto> getListaPrescricaoMdtoIncluidos() {
		return listaPrescricaoMdtoIncluidos;
	}

	public void setListaPrescricaoMdtoIncluidos(List<MpmPrescricaoMdto> listaPrescricaoMdtoIncluidos) {
		this.listaPrescricaoMdtoIncluidos = listaPrescricaoMdtoIncluidos;
	}

	public List<MpmPrescricaoMdto> getListaPrescricaoMdtoAlterados() {
		return listaPrescricaoMdtoAlterados;
	}

	public void setListaPrescricaoMdtoAlterados(List<MpmPrescricaoMdto> listaPrescricaoMdtoAlterados) {
		this.listaPrescricaoMdtoAlterados = listaPrescricaoMdtoAlterados;
	}

	public List<MpmPrescricaoMdto> getListaPrescricaoMdtoExcluidos() {
		return listaPrescricaoMdtoExcluidos;
	}

	public void setListaPrescricaoMdtoExcluidos(List<MpmPrescricaoMdto> listaPrescricaoMdtoExcluidos) {
		this.listaPrescricaoMdtoExcluidos = listaPrescricaoMdtoExcluidos;
	}

	public Byte getNroViasPme() {
		return nroViasPme;
	}

	public void setNroViasPme(Byte nroViasPme) {
		this.nroViasPme = nroViasPme;
	}

	public List<DispensacaoFarmaciaVO> getListaMedicamentosMovimentados() {
		return listaMedicamentosMovimentados;
	}

	public void setListaMedicamentosMovimentados(
			List<DispensacaoFarmaciaVO> listaMedicamentosMovimentados) {
		this.listaMedicamentosMovimentados = listaMedicamentosMovimentados;
	}

	public Boolean getImprimirRelatorioDispensacao() {
		return imprimirRelatorioDispensacao;
	}

	public void setImprimirRelatorioDispensacao(Boolean imprimirRelatorioDispensacao) {
		this.imprimirRelatorioDispensacao = imprimirRelatorioDispensacao;
	}

	public EnumTipoImpressao getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(EnumTipoImpressao tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public RapServidores getServidorValida() {
		return servidorValida;
	}

	public void setServidorValida(RapServidores servidorValida) {
		this.servidorValida = servidorValida;
	} 
	
	public Date getDataMovimento() {
		return dataMovimento;
	}
	
	public void setDataMovimento(Date dataMovimento) {
		this.dataMovimento = dataMovimento;
	}
}
