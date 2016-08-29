package br.gov.mec.aghu.farmacia.business;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.AghuNumberFormat;
import br.gov.mec.aghu.core.utils.StringUtil;

/**
 * Classe responsável por prover os métodos de negócio para geração e impressão
 * de etiquetas - estória # 5891.
 * 
 */

@Stateless
public class EtiquetasON extends BaseBusiness {

	private static final String UP_XZ = "^XZ";

	private static final String P_SEQ = "p_seq";

	private static final String P_LDC_SEQ = "p_ldc_seq";

	private static final String SIGLA_HOSP_ETIQUETA = "siglaHospEtiqueta";

	private static final String LABORATORIO = "laboratorio";

	private static final String LOT_CODIGO = "lotCodigo";

	private static final String DT_VALIDADE_STRING = "dtValidadeString";

	private static final String UP_FS = "^FS";

	private static final String LAB_ = "LAB:";

	private static final String LOTE_ = "LOTE:";

	private static final String VAL_ = "VAL:";

	private static final String BARRA_ECOMERCIAL = "\\&";

	private static final String UP_ADN = "^ADN";

	private static final String _006 = ",006";

	private static final String UP_FD = "^FD";

	private static final String UP_FO = "^FO";

	private static final String _03D = "%03d";

	private static final String V_CONCENTRACAO = "v_concentracao";

	private static final String V_NOME = "nome";

	private static final Log LOG = LogFactory.getLog(EtiquetasON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -66573772831725524L;
	private static final char CHAR10 = 10;

	public enum EtiquetasONExceptionCode implements BusinessExceptionCode {
		COMPUTADOR_SEM_UNIDADE_FUNCIONAL, ERRO_RECUPERAR_REMOTE_ADDRESS, CODIGO_BARRAS_INVALIDO, CODIGO_BARRAS_INVALIDO_LOTE, CODIGO_BARRAS_INVALIDO_ITENS;
	}

	public enum TipoCodigoBarrasMedicamento {
		BAR_CODE_128_SUBSET_C(14, "C"), BAR_CODE_128_SUBSET_A(15, "A");
		
		String parametro;
		Integer tamanhoMax;
		Integer loteMedicamento;
		Integer numItens;
		
		TipoCodigoBarrasMedicamento(Integer tamanhoMax, String parametro){
			this.tamanhoMax=tamanhoMax;
			this.parametro = parametro;
		}

		public boolean ehValidoCodigoBarras(String codigoBarras){
			return 	codigoBarras != null &&	codigoBarras.length() == tamanhoMax ? true : false;
		}
		
		public void parseCodigoBarras(String codigoBarras) throws ApplicationBusinessException{
			if(!ehValidoCodigoBarras(codigoBarras)) {
				throw new ApplicationBusinessException(
						EtiquetasONExceptionCode.CODIGO_BARRAS_INVALIDO, codigoBarras);
			}
			
			loteMedicamento = Integer.valueOf(codigoBarras.substring(0, tamanhoMax - 5));
			numItens = Integer.valueOf(codigoBarras.substring(tamanhoMax - 5, tamanhoMax));
		}
		
		public String geraCodigoBarras(Integer loteMedicamento, Integer numItens) throws ApplicationBusinessException{
			if(loteMedicamento == null || loteMedicamento.toString().length() > (tamanhoMax - 5)) {
				throw new ApplicationBusinessException(
					EtiquetasONExceptionCode.CODIGO_BARRAS_INVALIDO_LOTE, loteMedicamento);
			}
			
			if(numItens == null || numItens.toString().length() > 5) {
				throw new ApplicationBusinessException(
					EtiquetasONExceptionCode.CODIGO_BARRAS_INVALIDO_ITENS,numItens);
			}
			
			this.loteMedicamento = loteMedicamento;
			this.numItens = numItens;
			
			return StringUtil.adicionaZerosAEsquerda(loteMedicamento, tamanhoMax - 5) + StringUtil.adicionaZerosAEsquerda(numItens, 5);
			
		}
		
		public static TipoCodigoBarrasMedicamento obterPorParametro(AghParametros tipoEtiqueta){
			for (TipoCodigoBarrasMedicamento tipoSelecionado : values()) {
				if (tipoSelecionado.parametro.equalsIgnoreCase(tipoEtiqueta.getVlrTexto())) {
					return tipoSelecionado;
				}
			}
			//Retornando valor default
			return BAR_CODE_128_SUBSET_A;
		}

	}

	/**
	 * Método responsável por gerar código ZPL para impressão da etiqueta.
	 * 
	 * @ORADB Function SCEC_GERA_ZPL
	 * 
	 * @param nome
	 * @param v_concentracao
	 * @param lotCodigo
	 * @param dtValidade
	 * @param laboratorio
	 * @param qtde
	 * @param v_seq
	 * @param seq
	 * @param v_qtdInt
	 * @param intervaloEtiquetas 
	 * @param margem 
	 * @return String - Texto ZPL
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings({"PMD.ExcessiveMethodLength"})
	private String gerarZplItem(String nome, String v_concentracao, String lotCodigo, Date dtValidade, String laboratorio, Integer qtde, int v_seq,
			String p_tipo_maquina, int seq, int v_qtdInt, final int margem, final int intervaloEtiquetas) throws ApplicationBusinessException {

		AghParametros nomeHuEti = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_NOME_HU_ETIQUETA);
		String siglaHospEtiqueta = nomeHuEti.getVlrTexto();

		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
		String dtValidadeString = sdf.format(dtValidade);
		String p_seq = StringUtil.adicionaZerosAEsquerda(v_seq, 5);

		String p_seq1;
		String p_seq2;

		//p_seq1 = p_seq + 1, com zeros à esquerda até completar 5 dígitos
		p_seq1 = StringUtil.adicionaZerosAEsquerda(String.valueOf(Integer.parseInt(p_seq)+1),5);

		//p_seq2 = p_seq + 2, com zeros à esquerda até completar 5 dígitos
		p_seq2 = StringUtil.adicionaZerosAEsquerda(String.valueOf(Integer.parseInt(p_seq)+2),5);

		String p_ldc_seq = StringUtil.adicionaZerosAEsquerda(seq, 10);

		final StringBuffer zpl = new StringBuffer(52).append("^XA").append(CHAR10)
		.append("^PRA").append(CHAR10)
		.append("^MD15").append(CHAR10)
		.append("^MNY").append(CHAR10)
		.append("^BY1,,30").append(CHAR10)
		.append("^LH0,0").append(CHAR10)
		.append("^LL960").append(CHAR10);
		Map<String, String> parametros = new HashMap<String, String>(8);
		parametros.put(V_NOME, nome);
		parametros.put(V_CONCENTRACAO, v_concentracao);
		parametros.put(DT_VALIDADE_STRING, dtValidadeString);
		parametros.put(LOT_CODIGO, lotCodigo);
		parametros.put(LABORATORIO, laboratorio);
		parametros.put(SIGLA_HOSP_ETIQUETA, siglaHospEtiqueta);
		parametros.put(P_LDC_SEQ, p_ldc_seq);
		if(v_qtdInt +1 == qtde){
			parametros.put(P_SEQ, p_seq);

			zpl.append(this.criarEtiqueta1(parametros, margem,
					intervaloEtiquetas));
			zpl.append(UP_XZ).append(CHAR10);
		}else if(v_qtdInt +2 == qtde){
			parametros.put(P_SEQ, p_seq);
			zpl.append(this.criarEtiqueta1(parametros, margem, intervaloEtiquetas));
			parametros.put(P_SEQ, p_seq1);
			zpl.append(this.criarEtiqueta2(parametros, margem, intervaloEtiquetas));
			zpl.append(UP_XZ).append(CHAR10);
		}else{
			parametros.put(P_SEQ, p_seq);
			zpl.append(this.criarEtiqueta1(parametros, margem,
					intervaloEtiquetas));
			parametros.put(P_SEQ, p_seq1);
			zpl.append(this.criarEtiqueta2(parametros, margem,
					intervaloEtiquetas));
			parametros.put(P_SEQ, p_seq2);
			zpl.append(this.criarEtiqueta3(parametros, margem, intervaloEtiquetas));
			zpl.append(UP_XZ).append(CHAR10);
		}
		zpl.append("^XA^IDR:*.TXT^XZ");

		return zpl.toString();
	}
	/**
	 * Método responsável por gerar código ZPL para impressão da etiqueta.
	 * 
	 * @ORADB Function SCEC_GERA_ZPL
	 * 
	 * @param nome
	 * @param v_concentracao
	 * @param lotCodigo
	 * @param dtValidade
	 * @param laboratorio
	 * @param qtde
	 * @param v_seq
	 * @param seq
	 * @param v_qtdInt
	 * @param intervaloEtiquetas 
	 * @param margem 
	 * @return String - Texto ZPL
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings({"PMD.ExcessiveParameterList","PMD.ExcessiveMethodLength"})
	private String gerarZplItemB(String nome, String v_concentracao, String lotCodigo, Date dtValidade, String laboratorio, Integer qtde, int v_seq,
			String p_tipo_maquina, int seq, int v_qtdInt, final int margem, final int intervaloEtiquetas) throws ApplicationBusinessException {

		AghParametros nomeHuEti = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_NOME_HU_ETIQUETA);
		String siglaHospEtiqueta = nomeHuEti.getVlrTexto();

		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
		String dtValidadeString = sdf.format(dtValidade);
		String p_seq = StringUtil.adicionaZerosAEsquerda(v_seq, 5);

		String p_seq1;
		String p_seq2;

		//p_seq1 = p_seq + 1, com zeros à esquerda até completar 5 dígitos
		p_seq1 = StringUtil.adicionaZerosAEsquerda(String.valueOf(Integer.parseInt(p_seq)+1),5);

		//p_seq2 = p_seq + 2, com zeros à esquerda até completar 5 dígitos
		p_seq2 = StringUtil.adicionaZerosAEsquerda(String.valueOf(Integer.parseInt(p_seq)+2),5);

		String p_ldc_seq = StringUtil.adicionaZerosAEsquerda(seq, 9);

		final StringBuffer zpl = new StringBuffer(52).append("^XA").append(CHAR10)
		.append("^PRA").append(CHAR10)
		.append("^MD15").append(CHAR10)
		.append("^MNY").append(CHAR10)
		.append("^BY1,,30").append(CHAR10)
		.append("^LH0,0").append(CHAR10)
		.append("^LL960").append(CHAR10);
		Map<String, String> parametros = new HashMap<String, String>(8);
		parametros.put(V_NOME, nome);
		parametros.put(V_CONCENTRACAO, v_concentracao);
		parametros.put(DT_VALIDADE_STRING, dtValidadeString);
		parametros.put(LOT_CODIGO, lotCodigo);
		parametros.put(LABORATORIO, laboratorio);
		parametros.put(SIGLA_HOSP_ETIQUETA, siglaHospEtiqueta);
		parametros.put(P_LDC_SEQ, p_ldc_seq);
		if(v_qtdInt +1 == qtde){
			parametros.put(P_SEQ, p_seq);

			zpl.append(this.criarEtiqueta1(parametros, margem,
					intervaloEtiquetas));
			zpl.append(UP_XZ).append(CHAR10);
		}else if(v_qtdInt +2 == qtde){
			parametros.put(P_SEQ, p_seq);
			zpl.append(this.criarEtiqueta1(parametros, margem, intervaloEtiquetas));
			parametros.put(P_SEQ, p_seq1);
			zpl.append(this.criarEtiqueta2(parametros, margem, intervaloEtiquetas));
			zpl.append(UP_XZ).append(CHAR10);
		}else{
			parametros.put(P_SEQ, p_seq);
			zpl.append(this.criarEtiqueta1(parametros, margem,
					intervaloEtiquetas));
			parametros.put(P_SEQ, p_seq1);
			zpl.append(this.criarEtiqueta2(parametros, margem,
					intervaloEtiquetas));
			parametros.put(P_SEQ, p_seq2);
			zpl.append(this.criarEtiqueta3(parametros, margem, intervaloEtiquetas));
			zpl.append(UP_XZ).append(CHAR10);
		}
		zpl.append("^XA^IDR:*.TXT^XZ");

		return zpl.toString();
	}

	private StringBuffer criarEtiqueta3(Map<String,String> parametros, int margem, int intervaloEtiquetas) {
		final StringBuffer zpl = new StringBuffer(97);
		zpl.append(UP_FO).append(String.format(_03D, margem + (intervaloEtiquetas * 2) + 540)).append(_006)
		.append(CHAR10)
		.append("^FB264,6,0,C,0").append(CHAR10)
		.append(UP_ADN).append(CHAR10).append(UP_FD)
		.append(parametros.get(V_NOME)).append(BARRA_ECOMERCIAL).append(CHAR10);
		if (parametros.get(V_CONCENTRACAO) != null) {
			zpl.append(parametros.get(V_CONCENTRACAO)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(DT_VALIDADE_STRING) != null){
			zpl.append(VAL_).append(parametros.get(DT_VALIDADE_STRING)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(LOT_CODIGO) != null){
			zpl.append(LOTE_).append(parametros.get(LOT_CODIGO)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(LABORATORIO) != null){
			zpl.append(LAB_).append(parametros.get(LABORATORIO)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(SIGLA_HOSP_ETIQUETA) != null){
			zpl.append(parametros.get(SIGLA_HOSP_ETIQUETA)).append(UP_FS).append(CHAR10);
		}
		final String p_ldc_seq = parametros.get(P_LDC_SEQ);
		final String p_seq = parametros.get(P_SEQ);
		zpl.append(UP_FO).append(String.format(_03D, margem + (intervaloEtiquetas * 2) + 565)).append(",113^BCN,33,N,N^FD").append(p_ldc_seq).append(p_seq)
		.append(UP_FS).append(CHAR10)
		.append(UP_FO).append(String.format(_03D, margem + (intervaloEtiquetas * 2) + 605)).append(",150^ABN^FD").append(p_ldc_seq).append(p_seq)
		.append(UP_FS).append(CHAR10);
		return zpl;
	}

	//	private StringBuffer criarEtiqueta2(String nome, String v_concentracao, String dtValidadeString, String lotCodigo, String laboratorio,
	//			String siglaHospEtiqueta, String p_ldc_seq, String p_seq, int margem, int intervaloEtiquetas) {
	private Object criarEtiqueta2(Map<String,String> parametros, int margem, int intervaloEtiquetas) {
		final StringBuffer zpl = new StringBuffer(100);
		zpl.append(UP_FO).append(String.format(_03D, margem + intervaloEtiquetas + 270)).append(_006)
		.append(CHAR10)
		.append("^FB264,6,0,C,0").append(CHAR10)
		.append(UP_ADN).append(CHAR10).append(UP_FD)
		.append(parametros.get(V_NOME)).append(BARRA_ECOMERCIAL).append(CHAR10);
		if (parametros.get(V_CONCENTRACAO) != null) {
			zpl.append(parametros.get(V_CONCENTRACAO)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(DT_VALIDADE_STRING) != null){
			zpl.append(VAL_).append(parametros.get(DT_VALIDADE_STRING)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(LOT_CODIGO) != null){
			zpl.append(LOTE_).append(parametros.get(LOT_CODIGO)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(LABORATORIO) != null){
			zpl.append(LAB_).append(parametros.get(LABORATORIO)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(SIGLA_HOSP_ETIQUETA) != null){
			zpl.append(parametros.get(SIGLA_HOSP_ETIQUETA)).append(UP_FS).append(CHAR10);
		}
		final String p_ldc_seq = parametros.get(P_LDC_SEQ);
		final String p_seq = parametros.get(P_SEQ);
		zpl.append(UP_FO).append(String.format(_03D, margem + intervaloEtiquetas + 305)).append(",113^BCN,33,N,N^FD").append(p_ldc_seq).append(p_seq)
		.append(UP_FS).append(CHAR10)
		.append(UP_FO).append(String.format(_03D, margem + intervaloEtiquetas + 340)).append(",150^ABN^FD").append(p_ldc_seq).append(p_seq).append(UP_FS)
		.append(CHAR10);
		return zpl;
	}

	//	private StringBuffer criarEtiqueta1(String nome, String v_concentracao, String dtValidadeString, String lotCodigo, String laboratorio,
	//			String siglaHospEtiqueta, String p_ldc_seq, String p_seq, int margem, int intervaloEtiquetas) {
	private StringBuffer criarEtiqueta1(Map<String, String> parametros, int margem, int intervaloEtiquetas) {
		final StringBuffer zpl = new StringBuffer(50).append(UP_FO).append(String.format(_03D, margem)).append(_006).append(CHAR10).append("^FB264,6,0,C,0")
		.append(CHAR10).append(UP_ADN).append(CHAR10).append(UP_FD);
		zpl.append(parametros.get(V_NOME)).append(BARRA_ECOMERCIAL).append(CHAR10);
		if (parametros.get(V_CONCENTRACAO) != null) {
			zpl.append(parametros.get(V_CONCENTRACAO)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(DT_VALIDADE_STRING) != null){
			zpl.append(VAL_).append(parametros.get(DT_VALIDADE_STRING)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(LOT_CODIGO) != null){
			zpl.append(LOTE_).append(parametros.get(LOT_CODIGO)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(LABORATORIO) != null){
			zpl.append(LAB_).append(parametros.get(LABORATORIO)).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(parametros.get(SIGLA_HOSP_ETIQUETA) != null){
			zpl.append(parametros.get(SIGLA_HOSP_ETIQUETA)).append(UP_FS).append(CHAR10);
		}
		final String p_ldc_seq = parametros.get(P_LDC_SEQ);
		final String p_seq = parametros.get(P_SEQ);
		zpl.append(UP_FO).append(String.format(_03D, margem + 22)).append(",113^BCN,33,N,N^FD").append(p_ldc_seq).append(p_seq).append(UP_FS).append(CHAR10)
		.append(UP_FO).append(String.format(_03D, margem + 60)).append(",150^ABN^FD").append(p_ldc_seq).append(p_seq).append(UP_FS).append(CHAR10);
		return zpl;
	}

	private IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	/**
	 * Retorna código ZPL das etiquetas para o lote fornecido.
	 * 
	 * @ORADB Procedure SCEP_GERA_ETIQUETAS
	 * 
	 * @param lote
	 * @throws ApplicationBusinessException
	 *             AGH_PARAMETRO_NAO_EXISTENTE
	 */
	public String gerarEtiquetas(SceLoteDocImpressao lote) throws ApplicationBusinessException{
		String nome = StringUtil.subtituiAcentos(recuperarNomeEtiqueta(lote, 18));
		BigDecimal concentracao = null;
		String unidade = null;
		String lotCodigo = null;
		Date dtValidade = null;
		String laboratorio = null;
		final String SIM = "S";
		if(SIM.equalsIgnoreCase(getParametroFacade().obterAghParametro(AghuParametrosEnum.P_ETIQUETA_MEDICAMENTO_COM_CONCENTRACAO).getVlrTexto())) {
			concentracao = lote.getConcentracao(); 
			unidade = lote.getUmmDescricao(); 
		}
		if(SIM.equalsIgnoreCase(getParametroFacade().obterAghParametro(AghuParametrosEnum.P_ETIQUETA_MEDICAMENTO_COM_LOTE).getVlrTexto())) {
			lotCodigo = recuperaLoteCodigo(lote.getLoteCodigo());
		}
		if(SIM.equalsIgnoreCase(getParametroFacade().obterAghParametro(AghuParametrosEnum.P_ETIQUETA_MEDICAMENTO_COM_VALIDADE).getVlrTexto())) {
			dtValidade = lote.getDtValidade();
		}
		if(SIM.equalsIgnoreCase(getParametroFacade().obterAghParametro(AghuParametrosEnum.P_ETIQUETA_MEDICAMENTO_COM_LABORATORIO).getVlrTexto())) {
			laboratorio = getLaboratorio(lote.getMarcaComercial().getDescricao());
		}
		Integer qtde = lote.getQtde();
		Integer nroInicial = lote.getNroInicial(); 
		Integer seq = lote.getSeq().intValue();


		StringBuffer zpl = new StringBuffer();

		int v_qtdInt = 0;
		int v_seq = nroInicial;

		final String v_concentracao;
		if (concentracao == null || unidade == null) {
			v_concentracao = null;
		} else {
			final String concentracaoFormatada = formatConcentracao(concentracao, "");
			v_concentracao = concentracaoFormatada + " " + unidade;
		}

		final int margem = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_MARGEM_ESQUERDA_ETIQ_MDTO).getVlrNumerico().intValue();
		final int intervaloEtiquetas = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_ESPACAMENTO_ETIQ_MDTO).getVlrNumerico().intValue();
		// Criação de etiquetas por subsets, quando existir
		int subset = 'A';
		try {
			final AghParametros parametros = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_TIPO_ETIQUETA_MEDICAMENTO);
			if (StringUtils.isNotEmpty(parametros.getVlrTexto())) {
				subset = parametros.getVlrTexto().charAt(0);
			}
		} catch (final BaseException e) {
			LOG.error(
					new StringBuffer("Erro ao buscar paramentro ").append(AghuParametrosEnum.P_TIPO_ETIQUETA_MEDICAMENTO.toString()).append(": ")
					.append(e.getMessage()), e);

		}

		switchSubsetPrint(nome, lotCodigo, dtValidade, laboratorio, qtde, seq,
				zpl, v_qtdInt, v_seq, v_concentracao, margem,
				intervaloEtiquetas, subset);

		return zpl.toString();

	}
	
	@SuppressWarnings("PMD.MissingBreakInSwitch")
	private void switchSubsetPrint(String nome, String lotCodigo,
			Date dtValidade, String laboratorio, Integer qtde, Integer seq,
			StringBuffer zpl, int v_qtdInt, int v_seq,
			final String v_concentracao, final int margem,
			final int intervaloEtiquetas, int subset)
			throws ApplicationBusinessException {
		switch (subset) {
		case 'C':
		case 'c':
			zpl.append(gerarZplItemSubSetC(nome, v_concentracao, lotCodigo, dtValidade, laboratorio, qtde, v_seq, null, seq, v_qtdInt, margem,
					intervaloEtiquetas));
			break;
		case 'B':
		case 'b':
			while (v_qtdInt < qtde) {
				zpl.append(gerarZplItemB(nome, v_concentracao, lotCodigo, dtValidade, laboratorio, qtde, v_seq, null, seq, v_qtdInt, margem, intervaloEtiquetas));
				v_seq += 3;
				v_qtdInt += 3;
			}
			break;
		case 'A':
		case 'a':
		default:
			while (v_qtdInt < qtde) {
				zpl.append(gerarZplItem(nome, v_concentracao, lotCodigo, dtValidade, laboratorio, qtde, v_seq, null, seq, v_qtdInt, margem, intervaloEtiquetas));
				v_seq += 3;
				v_qtdInt += 3;
			}
			break;
		}
	}
	
	public AghUnidadesFuncionais carregarUnidadeFuncionalImpressao(
			String nomeMicrocomputador) throws ApplicationBusinessException {
		AghMicrocomputador computador = null;
		if(nomeMicrocomputador!=null){
			computador = getAdministracaoFacade().obterAghMicroComputadorPorNomeOuIPException(nomeMicrocomputador);
		}

		if(computador!=null){
			if (computador.getAghUnidadesFuncionais() == null) {
				throw new ApplicationBusinessException(
						EtiquetasONExceptionCode.COMPUTADOR_SEM_UNIDADE_FUNCIONAL);
			}
		}

		return computador.getAghUnidadesFuncionais();
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength"})
	private Object gerarZplItemSubSetC(final String nome, final String v_concentracao, final String lotCodigo, final Date dtValidade, final String laboratorio,
			final Integer qtde, final int v_seq, final Object object, final Integer seq, final int v_qtdInt, final int margem, final int intervaloEtiquetas)
	throws ApplicationBusinessException {
		final char CHAR10 = 10;
		final String siglaHospEtiqueta = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_NOME_HU_ETIQUETA).getVlrTexto();

		final StringBuffer zpl = new StringBuffer(45);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/yyyy");
		String dtValidadeString = sdf.format(dtValidade);
		final String p_ldc_seq = StringUtil.adicionaZerosAEsquerda(seq, 9);

		int l_qtdInt = v_qtdInt;
		int l_seq = v_seq;
		while (l_qtdInt < qtde) {
			final String p_seq = StringUtil.adicionaZerosAEsquerda(l_seq, 5);

			// p_seq1 = p_seq + 1, com zeros à esquerda até completar 5 dígitos
			final String p_seq1 = StringUtil.adicionaZerosAEsquerda(String.valueOf(Integer.parseInt(p_seq) + 1), 5);

			// p_seq2 = p_seq + 2, com zeros à esquerda até completar 5 dígitos
			final String p_seq2 = StringUtil.adicionaZerosAEsquerda(String.valueOf(Integer.parseInt(p_seq) + 2), 5);


			// Gera o ZPL
			zpl.append("^XA").append(CHAR10)
			.append("^PRA").append(CHAR10)
			.append("^MD15").append(CHAR10)
			.append("^MNY").append(CHAR10)
			.append("^LH0,0").append(CHAR10)
			.append("^LL1160").append(CHAR10)
			;
			final Map<String, String> parametros = new HashMap<String, String>(8);
			parametros.put(V_NOME, nome);
			parametros.put(V_CONCENTRACAO, v_concentracao);
			parametros.put(DT_VALIDADE_STRING, dtValidadeString);
			parametros.put(LOT_CODIGO, lotCodigo);
			parametros.put(LABORATORIO, laboratorio);
			parametros.put(SIGLA_HOSP_ETIQUETA, siglaHospEtiqueta);
			parametros.put(P_LDC_SEQ, p_ldc_seq);
			if (l_qtdInt + 1 == qtde) {
				parametros.put(P_SEQ, p_seq);
				zpl.append(this.criarEtiqueta1SubtipoC(parametros, margem, intervaloEtiquetas));
			} else if (l_qtdInt + 2 == qtde) {
				parametros.put(P_SEQ, p_seq);
				zpl.append(this.criarEtiqueta1SubtipoC(parametros, margem, intervaloEtiquetas));
				parametros.put(P_SEQ, p_seq1);
				zpl.append(this.criarEtiqueta2SubtipoC(parametros, margem, intervaloEtiquetas));
			} else {
				parametros.put(P_SEQ, p_seq);
				zpl.append(this.criarEtiqueta1SubtipoC(parametros, margem, intervaloEtiquetas));
				parametros.put(P_SEQ, p_seq1);
				zpl.append(this.criarEtiqueta2SubtipoC(parametros, margem, intervaloEtiquetas));
				parametros.put(P_SEQ, p_seq2);
				zpl.append(this.criarEtiqueta3SubtipoC(parametros, margem, intervaloEtiquetas));
			}
			zpl.append(UP_XZ).append(CHAR10);

			l_seq += 3;
			l_qtdInt += 3;

		}
		zpl.append("^IDR:*.TXT^XZ");
		return zpl.toString();
	}

	//	private Object criarEtiqueta3SubtipoC(String nome, String v_concentracao, String dtValidadeString, String lotCodigo, String laboratorio,
	//			String siglaHospEtiqueta, String p_ldc_seq, String p_seq, int margem, int intervaloEtiquetas) {
	private StringBuffer criarEtiqueta3SubtipoC(Map<String,String> parametros, int margem, int intervaloEtiquetas) {
		final StringBuffer zpl = this.iniciarEtiquetaSubTipoC(margem + (intervaloEtiquetas * 2) + 540);

		this.adicionarCamposEtiquetaSubTipoC(zpl, parametros.get(V_NOME), parametros.get(V_CONCENTRACAO), parametros.get(DT_VALIDADE_STRING),
				parametros.get(LOT_CODIGO), parametros.get(LABORATORIO));

		this.adicionarSiglaHospitalSubTipoC(zpl, parametros.get(SIGLA_HOSP_ETIQUETA), margem + (intervaloEtiquetas * 2) + 545);
		this.adicionarCodigoBarrasSubTipoC(zpl, parametros.get(P_LDC_SEQ), parametros.get(P_SEQ), margem + (intervaloEtiquetas * 2) + 545);
		return zpl;
	}

	//	private Object criarEtiqueta2SubtipoC(String nome, String v_concentracao, String dtValidadeString, String lotCodigo, String laboratorio,
	//			String siglaHospEtiqueta, String p_ldc_seq, String p_seq, int margem, int intervaloEtiquetas) {
	private StringBuffer criarEtiqueta2SubtipoC(Map<String,String> parametros, int margem, int intervaloEtiquetas) {

		final StringBuffer zpl = this.iniciarEtiquetaSubTipoC(margem + intervaloEtiquetas + 270);

		this.adicionarCamposEtiquetaSubTipoC(zpl, parametros.get(V_NOME), parametros.get(V_CONCENTRACAO), parametros.get(DT_VALIDADE_STRING), parametros.get(LOT_CODIGO), parametros.get(LABORATORIO));
		this.adicionarSiglaHospitalSubTipoC(zpl, parametros.get(SIGLA_HOSP_ETIQUETA), margem + intervaloEtiquetas + 275);
		this.adicionarCodigoBarrasSubTipoC(zpl, parametros.get(P_LDC_SEQ), parametros.get(P_SEQ), margem + intervaloEtiquetas + 275);
		return zpl;
	}

	//	private StringBuffer criarEtiqueta1SubtipoC(final String nome, final String v_concentracao, final String dtValidadeString, final String lotCodigo,
	//			final String laboratorio, final String siglaHospEtiqueta, final String p_ldc_seq, final String p_seq, final int margem, final int intervaloEtiquetas) {
	private StringBuffer criarEtiqueta1SubtipoC(Map<String,String> parametros, int margem, int intervaloEtiquetas) {

		final StringBuffer zpl = this.iniciarEtiquetaSubTipoC(margem);

		this.adicionarCamposEtiquetaSubTipoC(zpl, parametros.get(V_NOME), parametros.get(V_CONCENTRACAO), parametros.get(DT_VALIDADE_STRING), parametros.get(LOT_CODIGO), parametros.get(LABORATORIO));
		this.adicionarSiglaHospitalSubTipoC(zpl, parametros.get(SIGLA_HOSP_ETIQUETA), margem + 5);

		this.adicionarCodigoBarrasSubTipoC(zpl, parametros.get(P_LDC_SEQ), parametros.get(P_SEQ), margem + 5);
		return zpl;
	}

	private StringBuffer iniciarEtiquetaSubTipoC(int margem) {
		return new StringBuffer().append(UP_FO).append(String.format(_03D, margem)).append(_006).append(CHAR10).append("^FB260,6,0,C,0").append(CHAR10)
		.append(UP_ADN).append(CHAR10).append(UP_FD).append(CHAR10);
	}

	private StringBuffer adicionarCodigoBarrasSubTipoC(final StringBuffer zpl, final String p_ldc_seq, final String p_seq, final int posicao) {
		zpl.append(UP_FO).append(String.format(_03D, posicao)).append(",100^BY2,,50^BCN,50,N,N,N,N^FD>;>8").append(p_ldc_seq).append(p_seq).append(CHAR10);
		zpl.append(UP_FS).append(CHAR10);
		zpl.append(UP_FO).append(String.format(_03D, posicao + 50)).append(",156^ABN^FD").append(p_ldc_seq).append(p_seq).append(UP_FS).append(CHAR10);
		return zpl;
	}

	private StringBuffer adicionarSiglaHospitalSubTipoC(final StringBuffer zpl, final String siglaHospEtiqueta, final int posicao) {
		if(siglaHospEtiqueta != null) {
			zpl.append(UP_FO).append(String.format(_03D, posicao)).append(",025^ABR,15^FD").append(siglaHospEtiqueta).append(CHAR10);
			zpl.append(UP_FS).append(CHAR10);
		}
		return zpl;
	}

	private StringBuffer adicionarCamposEtiquetaSubTipoC(final StringBuffer zpl, final String nome, final String v_concentracao, final String dtValidadeString,
			final String lotCodigo, String laboratorio) {
		if(v_concentracao == null) {
			zpl.append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(dtValidadeString == null) {
			zpl.append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(lotCodigo == null) {
			zpl.append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if(laboratorio == null) {
			zpl.append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		zpl.append(StringUtils.trim(nome)).append(BARRA_ECOMERCIAL).append(CHAR10);
		if (v_concentracao != null) {
			zpl.append(v_concentracao).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if (dtValidadeString != null) {
			zpl.append(VAL_).append(dtValidadeString).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if (lotCodigo != null) {
			zpl.append(LOTE_).append(lotCodigo).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		if (laboratorio != null) {
			zpl.append(LAB_).append(laboratorio).append(BARRA_ECOMERCIAL).append(CHAR10);
		}
		zpl.append(UP_FS).append(CHAR10);
		return zpl;
	}

	private String getLaboratorio(String laboratorio) {
		return StringUtil.trunc(StringUtil.subtituiAcentos(laboratorio), false, 15l);
	}

	private String recuperaLoteCodigo(String loteCodigo) {
		return StringUtil.trunc(StringUtil.subtituiAcentos(loteCodigo), false, 12l);
	}

	private String formatConcentracao(BigDecimal concentracaoMedicamento, String retornoSeNull) {
		if (concentracaoMedicamento == null) {
			return retornoSeNull;
		} else {
			return AghuNumberFormat.formatarValor(concentracaoMedicamento, AfaMedicamento.class, "concentracao");
		}
	}

	/**
	 * Recupera o nome da etiqueta de afaMedicamento
	 * @param entidade
	 * @param sizeNome, caso null retorna tamanho da coluna por inteiro.
	 * @return
	 */
	private String recuperarNomeEtiqueta(SceLoteDocImpressao entidade, Integer sizeNome) {
		
		String nome = entidade.getMaterial().getAfaMedicamento().getDescricaoEtiqueta();
		
		if (StringUtils.isBlank(nome)){
			nome = entidade.getMaterial().getAfaMedicamento().getDescricao();
		}
		
		/* Alterado pela solicitação #32501 e validado com a equipe de negócio.	
		String nome = entidade.getMaterial().getAfaMedicamento().getDescricaoEtiquetaFrasco();
	
		if(nome == null || "".equals(nome.trim())){
			nome = entidade.getMaterial().getAfaMedicamento().getDescricaoEtiquetaSeringa();
			if(nome == null || "".equals(nome.trim())){
				nome = entidade.getMaterial().getAfaMedicamento().getDescricao();
			}
		}*/

		if(sizeNome != null && nome.length() > sizeNome){
			nome = nome.substring(0, sizeNome);
		}

		return nome;
	}

	protected IAdministracaoFacade getAdministracaoFacade(){
		return administracaoFacade;
	}
	
	protected IEstoqueFacade getEstoqueFacade(){
		return estoqueFacade;
	}	

}
