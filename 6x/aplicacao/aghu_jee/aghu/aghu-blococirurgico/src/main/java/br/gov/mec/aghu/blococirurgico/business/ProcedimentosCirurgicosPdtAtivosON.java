package br.gov.mec.aghu.blococirurgico.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcedimentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.vo.ProcedimentosCirurgicosPdtAtivosVO;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ProcedimentosCirurgicosPdtAtivosON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ProcedimentosCirurgicosPdtAtivosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcedimentoCirurgicoDAO mbcProcedimentoCirurgicoDAO;


	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@EJB
	private IParametroFacade iParametroFacade;
	
	private static final long serialVersionUID = 4124033889367412693L;
	private static final String QUEBRA_LINHA = "\n";
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final DominioSituacao SITUACAO = DominioSituacao.A;
	private static final DominioSituacao PHI_IND_SITUACAO = DominioSituacao.A;
	private static final DominioSituacao IPH_IND_SITUACAO = DominioSituacao.A;

	public enum ProcedimentosCirurgicosPdtAtivosONExceptionCode implements BusinessExceptionCode {
		PROCED_CIR_PDT_ATIVOS_MBCR_PROCED_ESPECIAL, PROCED_CIR_PDT_ATIVOS_MBCR_NRO_COPIAS, PROCED_CIR_PDT_ATIVOS_MBCR_NRO_COPIAS_OBRIGATORIO;
	}

	protected IParametroFacade getParametroFacade() {
		return this.iParametroFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.iFaturamentoFacade;
	}

	protected MbcProcedimentoCirurgicoDAO getMbcProcedimentoCirurgicoDAO() {
		return mbcProcedimentoCirurgicoDAO;
	}
	
	private List<DominioTipoProcedimentoCirurgico> obterTiposProcedimentos() {
		List<DominioTipoProcedimentoCirurgico> tipos = new ArrayList<DominioTipoProcedimentoCirurgico>();
		tipos.add(DominioTipoProcedimentoCirurgico.CIRURGIA);
		tipos.add(DominioTipoProcedimentoCirurgico.PROCEDIMENTO_DIAGNOSTICO_TERAPEUTICO);
		return tipos;
	}
	
	private Short obterCodigoTabela() throws ApplicationBusinessException{
		Short tabela = null;
			try {
				AghParametros pTabela = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
				tabela = pTabela.getVlrNumerico().shortValue();
			} catch (ApplicationBusinessException e) {
				throw new ApplicationBusinessException(e.getCode(), ProcedimentosCirurgicosPdtAtivosONExceptionCode.PROCED_CIR_PDT_ATIVOS_MBCR_PROCED_ESPECIAL);
			}
		return tabela;
	}

	private Short obterCodigoConvenioSus() throws ApplicationBusinessException{
		Short convenio = null;
		try {
			AghParametros pConvenio = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
			convenio = pConvenio.getVlrNumerico().shortValue();
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(e.getCode(), ProcedimentosCirurgicosPdtAtivosONExceptionCode.PROCED_CIR_PDT_ATIVOS_MBCR_PROCED_ESPECIAL);
		}
		return convenio;
	}

	private Byte obterPlanoSus(AghuParametrosEnum enumPlanoSus) throws ApplicationBusinessException{
		AghParametros plano = null;
		try {
			plano = getParametroFacade().buscarAghParametro(enumPlanoSus);
		} catch (ApplicationBusinessException e) {
			throw new ApplicationBusinessException(e.getCode(), ProcedimentosCirurgicosPdtAtivosONExceptionCode.PROCED_CIR_PDT_ATIVOS_MBCR_PROCED_ESPECIAL);
		}
		
		return plano.getVlrNumerico().byteValue();
		
	}
	
	private Byte obterPlanoSusAmbulatorio() throws ApplicationBusinessException{
		return obterPlanoSus(AghuParametrosEnum.P_SUS_PLANO_AMBULATORIO);
	}
	
	private Byte obterPlanoSusInternacao() throws ApplicationBusinessException{
		return obterPlanoSus(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
	}
	
	/**
	 * Function
	 * 
	 * @ORADB MBCC_TAB_FAT_SUS_AMB
	 * @param phiSeq Integer
	 * @return String
	 */
	private String obterMbccTabFatSusAmb(final Integer phiSeq, final Short convenio, final Byte planoSusAmbulatorio, final Short tabela){
		
		final Short cpgCphCspCnvCodigo	= convenio;
		final Byte cpgCphCspSeq			= planoSusAmbulatorio;
		final Short iphPhoSeq			= tabela;

		List<Long> listaCodigoTabela = getFaturamentoFacade().obterListaDeCodigoTabela(phiSeq, PHI_IND_SITUACAO, IPH_IND_SITUACAO, cpgCphCspCnvCodigo, cpgCphCspSeq, iphPhoSeq);
		
		return StringUtils.substring(concatenaListaDeInteger(listaCodigoTabela, ","), 0, 39);
	}
	
	/**
	 * Function
	 * 
	 * @ORADB MBCC_TAB_FAT_SUS_INT
	 * @param phiSeq Integer
	 * @return String
	 */
	private String obterMbccTabFatSusInt(final Integer phiSeq, final Short convenio, final Byte planoSusInternacao, final Short tabela){
		
		final Short cpgCphCspCnvCodigo	= convenio;
		final Byte cpgCphCspSeq			= planoSusInternacao;
		final Short iphPhoSeq			= tabela;
		
		List<Long> listaCodigoTabela = getFaturamentoFacade().obterListaDeCodigoTabela(phiSeq, PHI_IND_SITUACAO, IPH_IND_SITUACAO, cpgCphCspCnvCodigo, cpgCphCspSeq, iphPhoSeq);
	
		return StringUtils.substring(concatenaListaDeInteger(listaCodigoTabela, ","), 0, 19);
	}
	
	private String concatenaListaDeInteger(List<Long> lista, String paramSeparador) {
		
		StringBuilder stringBuilder = new StringBuilder();
		
		if (!lista.isEmpty() && lista != null) {
			
			String separador = null;
			
			if (paramSeparador != null) {
				separador = paramSeparador;
			} else {
				separador = ",";
			}

			if (lista.size() > 1) {
				for (Long i : lista) {
					stringBuilder.append(i).append(separador);
				}
			} else {
				stringBuilder.append(lista.get(0));
			}
		}

		return stringBuilder.toString();
	}
	
	private String gerarCabecalhoDoRelatorio() {
		StringBuilder builder = new StringBuilder();
		builder.append(getResourceBundleValue("PROCED_CIR_PDT_ATIVOS_HEADER_CSV_ESPECIALIDADE"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("PROCED_CIR_PDT_ATIVOS_HEADER_CSV_PROCEDIMENTO"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("PROCED_CIR_PDT_ATIVOS_HEADER_CSV_CONTAMINACAO"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("PROCED_CIR_PDT_ATIVOS_HEADER_CSV_PHI"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("PROCED_CIR_PDT_ATIVOS_HEADER_CSV_AMB"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("PROCED_CIR_PDT_ATIVOS_HEADER_CSV_INT"))
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);
	
		return builder.toString();
	}
	
	private String gerarLinhasDoRelatorio(ProcedimentosCirurgicosPdtAtivosVO linha) {
		StringBuilder builder = new StringBuilder();
		
		builder.append(linha.getNomeEspecialidade())
			.append(SEPARADOR)
			.append(linha.getStrProcedimento())
			.append(SEPARADOR)
			.append(linha.getStrContaminacao())
			.append(SEPARADOR)
			.append(linha.getPhiSeq())
			.append(SEPARADOR)
			.append(linha.getValorItemSusAmb())
			.append(SEPARADOR)
			.append(linha.getValorSsmSusInt())
			.append(SEPARADOR)
			.append(QUEBRA_LINHA);
		
		return builder.toString();
	}

	private String obterProcedimentoFormatado(ProcedimentosCirurgicosPdtAtivosVO vo) {
		StringBuilder builder = new StringBuilder();
		builder.append(vo.getPciSeq())
			.append(" - ")
			.append(StringUtils.replace(vo.getDescricao(), ";", " "));
		
		return builder.toString();
	}
	
	private void formatarProcedimentosCirurgicosPdtAtivosVO(List<ProcedimentosCirurgicosPdtAtivosVO> listVO) throws ApplicationBusinessException {
		
		Short convenio = obterCodigoConvenioSus();
		Byte planoSusAmbulatorio = obterPlanoSusAmbulatorio();
		Byte planoSusInternacao = obterPlanoSusInternacao();
		Short tabela = obterCodigoTabela();
		
		for (ProcedimentosCirurgicosPdtAtivosVO vo : listVO) {
			vo.setStrProcedimento(obterProcedimentoFormatado(vo));
			vo.setStrContaminacao(vo.getContaminacao().toString());
			vo.setValorItemSusAmb(obterMbccTabFatSusAmb(vo.getPhiSeq(), convenio, planoSusAmbulatorio, tabela));
			vo.setValorSsmSusInt(obterMbccTabFatSusInt(vo.getPhiSeq(), convenio, planoSusInternacao, tabela));
		}
		
	}
	
	public  List<MbcProcedimentoCirurgicos> listarProcedimentoCirurgicoPorTipoSituacaoEspecialidade(String strPesquisa, Short especialidade){
		return getMbcProcedimentoCirurgicoDAO().listarProcedimentoCirurgicoPorTipoSituacaoEspecialidade(strPesquisa, obterTiposProcedimentos(), SITUACAO, especialidade);	
	}

	public Long listarProcedimentoCirurgicoPorTipoSituacaoEspecialidadeCount(String strPesquisa, Short especialidade){
		return getMbcProcedimentoCirurgicoDAO().listarProcedimentoCirurgicoPorTipoSituacaoEspecialidadeCount(strPesquisa, obterTiposProcedimentos(), SITUACAO, especialidade);
	}
	
	public List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedimentosCirurgicosPdtAtivosVO(final Short especialidade, final Integer procedimento) throws ApplicationBusinessException {
		
		List<ProcedimentosCirurgicosPdtAtivosVO> listVO = getFaturamentoFacade().
			obterProcedCirPorTipoSituacaoEspecialidadeIphPhoSeqProcedimento(obterTiposProcedimentos(), SITUACAO, especialidade, obterCodigoTabela(), procedimento);
		
		formatarProcedimentosCirurgicosPdtAtivosVO(listVO);
		
		return listVO;
	}
	
	public List<ProcedimentosCirurgicosPdtAtivosVO> obterProcedimentosCirurgicosPdtAtivosListaPaginada(
			Integer firstResult, 
			Integer maxResult, 
			String orderProperty,
			boolean asc, 
			final Short especialidade, final Integer procedimento) throws ApplicationBusinessException {

		List<ProcedimentosCirurgicosPdtAtivosVO> listVO = getFaturamentoFacade()
				.obterProcedimentosCirurgicosPdtAtivosListaPaginada(
						firstResult, 
						maxResult, 
						orderProperty, 
						asc,
						obterTiposProcedimentos(), SITUACAO, especialidade, obterCodigoTabela(), procedimento);
		
		formatarProcedimentosCirurgicosPdtAtivosVO(listVO);

		return listVO;
	}
	
	public Long obterProcedimentosCirurgicosPdtAtivosCount(final Short especialidade, final Integer procedimento) throws ApplicationBusinessException {

		return Long.valueOf(obterProcedimentosCirurgicosPdtAtivosVO(especialidade, procedimento).size());
	}
	
	public String gerarCSVProcedimentosCirurgicosPdtAtivos(final Short especialidade, final Integer procedimento) throws IOException, ApplicationBusinessException {

		
		final List<ProcedimentosCirurgicosPdtAtivosVO> listaLinhas = obterProcedimentosCirurgicosPdtAtivosVO(especialidade, procedimento);

		final File file = File.createTempFile(DominioNomeRelatorio.MBCR_PROCED_ESPECIAL.toString(), DominioNomeRelatorio.EXTENSAO_CSV);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		out.write(gerarCabecalhoDoRelatorio());

		if (!listaLinhas.isEmpty()) {
			for(ProcedimentosCirurgicosPdtAtivosVO linha : listaLinhas){
				out.write(gerarLinhasDoRelatorio(linha));
			}
		}
		
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}

	public void validarNroDeCopiasRelProcedCirgPdtAtivos(Integer numeroDeCopias) throws ApplicationBusinessException {
		if(numeroDeCopias != null){
			if(numeroDeCopias < 1){
				throw new ApplicationBusinessException(ProcedimentosCirurgicosPdtAtivosONExceptionCode.PROCED_CIR_PDT_ATIVOS_MBCR_NRO_COPIAS);
			}
		}else{
			
			throw new ApplicationBusinessException(ProcedimentosCirurgicosPdtAtivosONExceptionCode.PROCED_CIR_PDT_ATIVOS_MBCR_NRO_COPIAS_OBRIGATORIO);
		}

	}

}
